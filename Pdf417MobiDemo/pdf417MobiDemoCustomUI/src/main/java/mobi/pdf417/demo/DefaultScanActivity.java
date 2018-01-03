package mobi.pdf417.demo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.microblink.activity.Pdf417ScanActivity;
import com.microblink.entities.recognizers.RecognizerBundle;
import com.microblink.geometry.Rectangle;
import com.microblink.hardware.SuccessCallback;
import com.microblink.hardware.orientation.Orientation;
import com.microblink.metadata.MetadataCallbacks;
import com.microblink.metadata.detection.FailedDetectionCallback;
import com.microblink.metadata.detection.points.DisplayablePointsDetection;
import com.microblink.metadata.detection.points.PointsDetectionCallback;
import com.microblink.metadata.detection.quad.DisplayableQuadDetection;
import com.microblink.metadata.detection.quad.QuadDetectionCallback;
import com.microblink.recognition.RecognitionSuccessType;
import com.microblink.util.CameraPermissionManager;
import com.microblink.view.CameraAspectMode;
import com.microblink.view.CameraEventsListener;
import com.microblink.view.OrientationAllowedListener;
import com.microblink.view.recognition.RecognizerRunnerView;
import com.microblink.view.recognition.ScanResultListener;
import com.microblink.view.viewfinder.PointSetView;
import com.microblink.view.viewfinder.quadview.QuadViewManager;
import com.microblink.view.viewfinder.quadview.QuadViewManagerFactory;
import com.microblink.view.viewfinder.quadview.QuadViewPreset;

public class DefaultScanActivity extends Activity implements ScanResultListener, CameraEventsListener, FailedDetectionCallback, QuadDetectionCallback, PointsDetectionCallback {

    public static final String EXTRAS_ROTATE_ROI = "EXTRAS_ROTATE_ROI";
    public static final String EXTRAS_ROI = "EXTRAS_ROI";

    private int mScanCount = 0;
    private Handler mHandler = new Handler();

    /** RecognizerView is the builtin view that controls camera and recognition */
    private RecognizerRunnerView mRecognizerRunnerView;
    /** CameraPermissionManager is provided helper class that can be used to obtain the permission to use camera.
     * It is used on Android 6.0 (API level 23) or newer.
     */
    private CameraPermissionManager mCameraPermissionManager;
    /** This is built-in helper for built-in view that draws detection location */
    QuadViewManager mQvManager = null;
    /** This is a builtin point set view that can visualize points of interest, such as those of QR code */
    private PointSetView mPointSetView;
    /** This is a torch button */
    private Button mTorchButton = null;
    /** This variable holds the torch state */
    private boolean mTorchEnabled = false;
    /** RecognizerBundle will hold the recognizer objects that arrived via Intent */
    private RecognizerBundle mRecognizerBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_scan);

        // create a scanner view
        mRecognizerRunnerView = findViewById(R.id.recognizerRunnerView);

        mRecognizerBundle = new RecognizerBundle();
        mRecognizerBundle.loadFromIntent(getIntent());

        // set RecognizerBundle to RecognizerRunnerView
        mRecognizerRunnerView.setRecognizerBundle(mRecognizerBundle);

        // add listeners
        mRecognizerRunnerView.setScanResultListener(this);
        mRecognizerRunnerView.setCameraEventsListener(this);

        // orientation allowed listener is asked if orientation is allowed when device orientation
        // changes - if orientation is allowed, rotatable views will be rotated to that orientation
        mRecognizerRunnerView.setOrientationAllowedListener(new OrientationAllowedListener() {
            @Override
            public boolean isOrientationAllowed(Orientation orientation) {
                // allow all orientations
                return true;
            }
        });

        // define which metadata will be available, like detection metadata
        MetadataCallbacks metadataCallbacks = new MetadataCallbacks();
        // set callback for quad detection
        metadataCallbacks.setQuadDetectionCallback(this);
        // set callback for points detection
        metadataCallbacks.setPointsDetectionCallback(this);
        // set callback when no detection
        metadataCallbacks.setFailedDetectionCallback(this);

        // register metadata callbacks to recognizer runner view
        mRecognizerRunnerView.setMetadataCallbacks(metadataCallbacks);

        // animate rotatable views on top of scanner view
        mRecognizerRunnerView.setAnimateRotation(true);

        // zoom and crop camera instead of fitting it into view
        mRecognizerRunnerView.setAspectMode(CameraAspectMode.ASPECT_FILL);

        // instantiate the camera permission manager
        mCameraPermissionManager = new CameraPermissionManager(this);
        // get the built in layout that should be displayed when camera permission is not given
        View v = mCameraPermissionManager.getAskPermissionOverlay();
        if (v != null) {
            // add it to the current layout that contains the recognizer view
            ViewGroup vg = findViewById(R.id.my_default_scan_root);
            vg.addView(v);
        }

        // after scanner is created, you can add your views to it

        // initialize QuadViewManager
        // Use provided factory method from QuadViewManagerFactory that can instantiate the
        // QuadViewManager based on several presets defined in QuadViewPreset enum. Details about
        // each of them can be found in javadoc. This method automatically adds the QuadView as a
        // child of RecognizerView.
        // Here we use preset which sets up quad view in the same style as used in built-in PDF417 ScanActivity.
        mQvManager= QuadViewManagerFactory.createQuadViewFromPreset(mRecognizerRunnerView, QuadViewPreset.DEFAULT_CORNERS_FROM_PDF417_SCAN_ACTIVITY);

        // create PointSetView
        mPointSetView = new PointSetView(this, null, mRecognizerRunnerView.getHostScreenOrientation());

        // add point set view to scanner view as fixed (non-rotatable) view
        mRecognizerRunnerView.addChildView(mPointSetView, false);

        // inflate buttons layout from XML
        View layout = getLayoutInflater().inflate(R.layout.default_barcode_camera_overlay, null);

        // setup back button
        /* This is a back button */
        Button backButton = layout.findViewById(R.id.defaultBackButton);
        backButton.setText(getString(R.string.mbHome));

        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // obtain a reference to torch button, but make it invisible
        // we will make it appear only if device supports torch control.
        // That information will be known only after camera has become active.
        mTorchButton = layout.findViewById(R.id.defaultTorchButton);
        mTorchButton.setVisibility(View.GONE);

        // add buttons layout as rotatable view on top of scanner view
        mRecognizerRunnerView.addChildView(layout, true);

        // if ROI is set, then create and add ROI layout
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            boolean rotateRoi = extras.getBoolean(EXTRAS_ROTATE_ROI);
            Rectangle roi = extras.getParcelable(EXTRAS_ROI);
            if(roi != null) {
                // tell scanner to use ROI
                mRecognizerRunnerView.setScanningRegion(roi, rotateRoi);

                // add ROI layout
                @SuppressLint("InflateParams")
                View roiView = getLayoutInflater().inflate(R.layout.roi_overlay, null);
                mRecognizerRunnerView.addChildView(roiView, rotateRoi);
            }
        }

        // create scanner (make sure RecognizerBundle, listeners and callbacks were set prior calling create)
        mRecognizerRunnerView.create();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // all activity lifecycle events must be passed on to RecognizerView
        if(mRecognizerRunnerView != null) {
            mRecognizerRunnerView.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // all activity lifecycle events must be passed on to RecognizerView
        mRecognizerRunnerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // all activity lifecycle events must be passed on to RecognizerView
        mRecognizerRunnerView.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // all activity lifecycle events must be passed on to RecognizerView
        mRecognizerRunnerView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // all activity lifecycle events must be passed on to RecognizerView
        mRecognizerRunnerView.destroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // change configuration of scanner's internal views
        mRecognizerRunnerView.changeConfiguration(newConfig);
        mQvManager.configurationChanged(mRecognizerRunnerView, newConfig);
    }

    /**
     * Callback which is called when user clicks the back button.
     */
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, null);
        finish();
    }

    @Override
    public void onCameraPreviewStarted() {
        // if device supports torch, make torch button visible and setup it
        // isCameraTorchSupported returns true if device supports controlling the torch and
        // camera preview is active
        if(mRecognizerRunnerView.isCameraTorchSupported()) {
            mTorchButton.setVisibility(View.VISIBLE);
            mTorchButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // setTorchEnabled returns true if torch turning off/on has succeeded
                    mRecognizerRunnerView.setTorchState(!mTorchEnabled, new SuccessCallback() {
                        @Override
                        public void onOperationDone(final boolean success) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (success) {
                                        mTorchEnabled = !mTorchEnabled;
                                        if (mTorchEnabled) {
                                            mTorchButton.setText(R.string.LightOn);
                                        } else {
                                            mTorchButton.setText(R.string.LightOff);
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onCameraPreviewStopped() {
        // this method is called just after camera preview has stopped
    }

    @Override
    public void onAutofocusFailed() {
        // this method is called when camera autofocus fails
        Toast.makeText(this, "Autofocus failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAutofocusStarted(Rect[] rects) {
        // draw here focusing animation
        // rects array define array of rectangles in view's coordinate system where
        // focus metering is taking place
    }

    @Override
    public void onAutofocusStopped(Rect[] rects) {
        // remove focusing animation
        // rects array define array of rectangles in view's coordinate system where
        // focus metering is taking place
    }

    @Override
    public void onDetectionFailed() {
        // clear points
        mPointSetView.setDisplayablePointsDetection(null);
        // begin quadrilateral animation to default position
        mQvManager.animateQuadToDefaultPosition();
    }

    @Override
    public void onPointsDetection(@NonNull DisplayablePointsDetection displayablePointsDetection) {
        mPointSetView.setDisplayablePointsDetection(displayablePointsDetection);
    }

    @Override
    public void onQuadDetection(@NonNull DisplayableQuadDetection displayableQuadDetection) {
        // begin quadrilateral animation to detected quadrilateral
        mQvManager.animateQuadToDetectionPosition(displayableQuadDetection);
        // clear points
        mPointSetView.setDisplayablePointsDetection(null);
    }

    /**
     * this activity will perform 5 scans of barcode and then return the last
     * scanned one
     */
    @Override
    public void onScanningDone(@NonNull RecognitionSuccessType recognitionSuccessType) {
        mScanCount++;
        StringBuilder sb = new StringBuilder();
        sb.append("Scanned ");
        sb.append(mScanCount);
        switch (mScanCount) {
            case 1:
                sb.append("st");
                break;
            case 2:
                sb.append("nd");
                break;
            case 3:
                sb.append("rd");
                break;
            default:
                sb.append("th");
                break;
        }
        sb.append(" barcode!");
        Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
        // pause scanning to prevent scan results to come while
        // activity is being finished or while we wait for delayed task
        // that will resume scanning
        mRecognizerRunnerView.pauseScanning();
        if (mScanCount >= 5) {
            // if we have 5 scans, return most recent result via Intent
            Intent intent = new Intent();
            mRecognizerBundle.saveToIntent(intent);
            setResult(Pdf417ScanActivity.RESULT_OK, intent);
            finish();
        } else {
            // if we still do not have 5 scans, wait 2 seconds and then resume
            // scanning and reset recognition state
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mRecognizerRunnerView.resumeScanning(true);
                }
            }, 2000);
        }
    }


    @Override
    public void onError(Throwable ex) {
        // This method will be called when opening of camera resulted in exception or
        // recognition process encountered an error.
        // The error details will be given in ex parameter.
        com.microblink.util.Log.e(this, ex, "Error");
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("There has been an error!")
                .setTitle("Error")
                .setCancelable(false)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dialog != null) {
                            dialog.dismiss();
                        }
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }).create().show();
    }

    @Override
    @TargetApi(23)
    public void onCameraPermissionDenied() {
        // this method is called on Android 6.0 and newer if camera permission was not given
        // by user

        // ask user to give a camera permission. Provided manager asks for
        // permission only if it has not been already granted.
        // on API level < 23, this method does nothing
        mCameraPermissionManager.askForCameraPermission();
    }

    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // on API level 23, we need to pass request permission result to camera permission manager
        mCameraPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
