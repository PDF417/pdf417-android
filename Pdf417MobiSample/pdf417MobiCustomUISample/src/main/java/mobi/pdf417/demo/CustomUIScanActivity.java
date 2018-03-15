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

import com.microblink.activity.BarcodeScanActivity;
import com.microblink.entities.recognizers.RecognizerBundle;
import com.microblink.geometry.Rectangle;
import com.microblink.hardware.SuccessCallback;
import com.microblink.hardware.orientation.Orientation;
import com.microblink.intent.IntentDataTransferMode;
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

@SuppressLint("InflateParams")
public class CustomUIScanActivity extends Activity implements View.OnClickListener {

    public static final String EXTRAS_ROTATE_SCAN_REGION = "EXTRAS_ROTATE_SCAN_REGION";
    public static final String EXTRAS_SCAN_REGION = "EXTRAS_SCAN_REGION";

    /** RecognizerRunnerView is the builtin view that controls camera and recognition */
    private RecognizerRunnerView mRecognizerRunnerView;

    /** RecognizerBundle will hold the recognizer objects that arrived via Intent */
    private RecognizerBundle mRecognizerBundle;

    /** CameraPermissionManager is provided helper class that can be used to obtain the permission to use camera.
     * It is used on Android 6.0 (API level 23) or newer.
     */
    private CameraPermissionManager mCameraPermissionManager;

    /** This is built-in helper for built-in view that draws detection location */
    private QuadViewManager mQvManager = null;

    /** This is a builtin point set view that can visualize points of interest, such as those of QR code */
    private PointSetView mPointSetView;

    private boolean mTorchEnabled = false;
    private Button mTorchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_ui_scan);

        mRecognizerRunnerView = findViewById(R.id.recognizer_runner_view);

        mRecognizerBundle = new RecognizerBundle();
        mRecognizerBundle.loadFromIntent(getIntent());

        mRecognizerRunnerView.setRecognizerBundle(mRecognizerBundle);

        mRecognizerRunnerView.setScanResultListener(mScanResultListener);
        mRecognizerRunnerView.setCameraEventsListener(mCameraEventsListener);

        // orientation allowed listener is asked if orientation is allowed when device orientation
        // changes - if orientation is allowed, rotatable views will be rotated to that orientation
        mRecognizerRunnerView.setOrientationAllowedListener(new OrientationAllowedListener() {
            @Override
            public boolean isOrientationAllowed(Orientation orientation) {
                // allow all orientations
                return true;
            }
        });

        MetadataCallbacks metadataCallbacks = new MetadataCallbacks();
        metadataCallbacks.setQuadDetectionCallback(mQuadDetectionCallback);
        metadataCallbacks.setPointsDetectionCallback(mPointsDetectionCallback);
        metadataCallbacks.setFailedDetectionCallback(mFailedDetectionCallback);
        mRecognizerRunnerView.setMetadataCallbacks(metadataCallbacks);

        // animate rotatable views on top of recognizer view
        mRecognizerRunnerView.setAnimateRotation(true);
        // zoom and crop camera instead of fitting it into view
        mRecognizerRunnerView.setAspectMode(CameraAspectMode.ASPECT_FILL);

        mCameraPermissionManager = new CameraPermissionManager(this);
        setupCameraPermissionOverlay();

        setupQuadViewManager();
        setupPointSetView();
        setupScanRegionView();
        setupCameraOverlayButtons();

        // make sure RecognizerBundle, listeners and callbacks were set prior calling create
        mRecognizerRunnerView.create();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
         /*
         * If using IntentDataTransferMode.PERSISTED_OPTIMISED transfer mode for RecognitionBundle,
         * then it is backed by temporary file which gets deleted each time loadFromBundle is called.
         * This can cause crash if your activity gets restarted by the Android. To prevent that crash
         * you should save RecognizerBundle's state in your onSaveInstanceState method. This will
         * ensure that bundle is written back to temporary file that will be available for loadFromBundle
         * method if activity gets restarted. However, if no restart occur, you must ensure this
         * temporary file gets deleted. Therefore, you must call clearSavedState in your onResume callback.
         */
        mRecognizerBundle.saveState();
    }

    private void setupQuadViewManager() {
        // Use provided factory method from QuadViewManagerFactory that can instantiate the
        // QuadViewManager based on several presets defined in QuadViewPreset enum. Details about
        // each of them can be found in javadoc. This method automatically adds the QuadView as a
        // child of RecognizerRunnerView.
        // Here we use preset which sets up quad view in the same style as used in built-in BarcodeScanActivity.
        mQvManager = QuadViewManagerFactory.createQuadViewFromPreset(mRecognizerRunnerView, QuadViewPreset.DEFAULT_CORNERS_FROM_BARCODE_SCAN_ACTIVITY);
    }

    private void setupPointSetView() {
        mPointSetView = new PointSetView(this, null, mRecognizerRunnerView.getHostScreenOrientation());
        // add point set view to scanner view as fixed (non-rotatable) view
        mRecognizerRunnerView.addChildView(mPointSetView, false);
    }

    private void setupScanRegionView() {
        // if scan region is set, then create and add scan region layout
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Rectangle scanRegion = extras.getParcelable(EXTRAS_SCAN_REGION);
            if(scanRegion != null) {
                boolean rotateScanRegion = extras.getBoolean(EXTRAS_ROTATE_SCAN_REGION);
                mRecognizerRunnerView.setScanningRegion(scanRegion, rotateScanRegion);
                View roiView = getLayoutInflater().inflate(R.layout.scan_region_overlay, null);
                mRecognizerRunnerView.addChildView(roiView, rotateScanRegion);
            }
        }
    }

    private void setupCameraPermissionOverlay() {
        // get the built in layout that should be displayed when camera permission is not given
        View cameraPermissionOverlayView = mCameraPermissionManager.getAskPermissionOverlay();
        if (cameraPermissionOverlayView != null) {
            // add it to the current layout that contains the recognizer view
            ViewGroup rootView = findViewById(R.id.root);
            rootView.addView(cameraPermissionOverlayView);
        }
    }

    private void setupCameraOverlayButtons() {
        View layout = getLayoutInflater().inflate(R.layout.camera_overlay, null);

        Button backButton = layout.findViewById(R.id.btnBack);
        backButton.setOnClickListener(this);

        // Make it invisible until we know if device supports torch control
        mTorchButton = layout.findViewById(R.id.btnTorch);
        mTorchButton.setVisibility(View.GONE);
        mTorchButton.setOnClickListener(this);

        mRecognizerRunnerView.addChildView(layout, true);
    }

    /**
     * all activity lifecycle events must be passed on to RecognizerRunnerView
     */

    @Override
    protected void onStart() {
        super.onStart();
        if(mRecognizerRunnerView != null) {
            mRecognizerRunnerView.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecognizerRunnerView.resume();
        /*
         * Clear temporary file created in onSaveInstanceState in case no activity restart happened
         * after call to onSaveInstanceState. If restart happened and temporary file was consumed
         * by loadFromBundle method in onCreate, then this method will do nothing.
         */
        mRecognizerBundle.clearSavedState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRecognizerRunnerView.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRecognizerRunnerView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecognizerRunnerView.destroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // change configuration of recognizer runner's internal views
        mRecognizerRunnerView.changeConfiguration(newConfig);
        mQvManager.configurationChanged(mRecognizerRunnerView, newConfig);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, null);
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnBack) {
            onBackPressed();
        } else if (id == R.id.btnTorch) {
            toggleTorchState();
        }
    }

    private void toggleTorchState() {
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

    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // on API level 23, we need to pass request permission result to camera permission manager
        mCameraPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //this activity performs 5 scans of barcode and returns the last one
    private final ScanResultListener mScanResultListener = new ScanResultListener() {

        private int mScanCount = 0;

        @Override
        public void onScanningDone(@NonNull RecognitionSuccessType recognitionSuccessType) {
            mScanCount++;

            String scanCountText = "Scanned: " + mScanCount + " / 5";
            Toast.makeText(CustomUIScanActivity.this, scanCountText, Toast.LENGTH_SHORT).show();

            // pause scanning to prevent scan results to come while
            // activity is being finished or while we wait for delayed task that will resume scanning
            mRecognizerRunnerView.pauseScanning();
            if (mScanCount >= 5) {
                // if we have 5 scans, return most recent result via Intent
                Intent intent = new Intent();
                mRecognizerBundle.saveToIntent(intent);
                setResult(BarcodeScanActivity.RESULT_OK, intent);
                finish();
            } else {
                // if we still do not have 5 scans, wait 2 seconds and then resume scanning and reset recognition state
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecognizerRunnerView.resumeScanning(true);
                    }
                }, 2000);
            }
        }
    };

    private final CameraEventsListener mCameraEventsListener = new CameraEventsListener() {

        @Override
        public void onCameraPermissionDenied() {
            mCameraPermissionManager.askForCameraPermission();
        }

        @Override
        public void onCameraPreviewStarted() {
            if(mRecognizerRunnerView.isCameraTorchSupported()) {
                mTorchButton.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onCameraPreviewStopped() {
            // this method is called just after camera preview has stopped
        }

        @Override
        public void onError(Throwable ex) {
            // This method will be called when opening of camera resulted in exception or
            // recognition process encountered an error.
            // The error details will be given in ex parameter.
            com.microblink.util.Log.e(this, ex, "Error");
            AlertDialog.Builder ab = new AlertDialog.Builder(CustomUIScanActivity.this);
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
        public void onAutofocusFailed() {
            Toast.makeText(CustomUIScanActivity.this, "Autofocus failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAutofocusStarted(Rect[] rects) {
            // here you should draw focusing animation
            // rects array define array of rectangles in view's coordinate system where
            // focus metering is taking place
        }

        @Override
        public void onAutofocusStopped(Rect[] rects) {
            // remove focusing animation
            // rects array defines array of rectangles in view's coordinate system where
            // focus metering is taking place
        }
    };

    private final QuadDetectionCallback mQuadDetectionCallback = new QuadDetectionCallback() {
        @Override
        public void onQuadDetection(@NonNull DisplayableQuadDetection displayableQuadDetection) {
            // begin quadrilateral animation to detected quadrilateral
            mQvManager.animateQuadToDetectionPosition(displayableQuadDetection);
            // clear points
            mPointSetView.setDisplayablePointsDetection(null);
        }
    };

    private final PointsDetectionCallback mPointsDetectionCallback = new PointsDetectionCallback() {
        @Override
        public void onPointsDetection(@NonNull DisplayablePointsDetection displayablePointsDetection) {
            mPointSetView.setDisplayablePointsDetection(displayablePointsDetection);
        }
    };

    private final FailedDetectionCallback mFailedDetectionCallback = new FailedDetectionCallback() {
        @Override
        public void onDetectionFailed() {
            mPointSetView.setDisplayablePointsDetection(null);
            // begin quadrilateral animation to default position
            mQvManager.animateQuadToDefaultPosition();
        }
    };

}
