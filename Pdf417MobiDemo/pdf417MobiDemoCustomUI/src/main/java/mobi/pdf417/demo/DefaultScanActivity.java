package mobi.pdf417.demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.microblink.activity.Pdf417ScanActivity;
import com.microblink.geometry.Rectangle;
import com.microblink.hardware.SuccessCallback;
import com.microblink.hardware.orientation.Orientation;
import com.microblink.metadata.Metadata;
import com.microblink.metadata.MetadataListener;
import com.microblink.metadata.MetadataSettings;
import com.microblink.metadata.detection.FailedDetectionMetadata;
import com.microblink.metadata.detection.PointsDetectionMetadata;
import com.microblink.metadata.detection.QuadrilateralDetectionMetadata;
import com.microblink.recognition.InvalidLicenceKeyException;
import com.microblink.recognizers.RecognitionResults;
import com.microblink.recognizers.settings.RecognitionSettings;
import com.microblink.util.CameraPermissionManager;
import com.microblink.view.BaseCameraView;
import com.microblink.view.CameraAspectMode;
import com.microblink.view.CameraEventsListener;
import com.microblink.view.OrientationAllowedListener;
import com.microblink.view.recognition.RecognizerView;
import com.microblink.view.recognition.ScanResultListener;
import com.microblink.view.viewfinder.PointSetView;
import com.microblink.view.viewfinder.quadview.QuadViewManager;
import com.microblink.view.viewfinder.quadview.QuadViewManagerFactory;
import com.microblink.view.viewfinder.quadview.QuadViewPreset;

public class DefaultScanActivity extends Activity implements ScanResultListener, CameraEventsListener, MetadataListener {

    private int mScanCount = 0;
    private Handler mHandler = new Handler();

    /** RecognizerView is the builtin view that controls camera and recognition */
    private RecognizerView mRecognizerView;
    /** CameraPermissionManager is provided helper class that can be used to obtain the permission to use camera.
     * It is used on Android 6.0 (API level 23) or newer.
     */
    private CameraPermissionManager mCameraPermissionManager;
    /** This is built-in helper for built-in view that draws detection location */
    QuadViewManager mQvManager = null;
    /** This is a builtin point set view that can visualize points of interest, such as those of QR code */
    private PointSetView mPointSetView;
    /** This is a holder for buttons layout inflated from XML */
    private View mLayout;
    /** This is a back button */
    private Button mBackButton = null;
    /** This is a torch button */
    private Button mTorchButton = null;
    /** This variable holds the torch state */
    private boolean mTorchEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_scan);

        // create a scanner view
        mRecognizerView = (RecognizerView) findViewById(R.id.recognizerView);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            // setup scanner parameters
            try {
                mRecognizerView.setLicenseKey(extras.getString(Pdf417ScanActivity.EXTRAS_LICENSE_KEY));
            } catch (InvalidLicenceKeyException e) {
                e.printStackTrace();
                Toast.makeText(this, "Invalid licence key", Toast.LENGTH_SHORT).show();
                finish();
            }
            RecognitionSettings recognitionSettings = extras.getParcelable(Pdf417ScanActivity.EXTRAS_RECOGNITION_SETTINGS);
            mRecognizerView.setRecognitionSettings(recognitionSettings);
        }

        // add listeners
        mRecognizerView.setScanResultListener(this);
        mRecognizerView.setCameraEventsListener(this);

        // orientation allowed listener is asked if orientation is allowed when device orientation
        // changes - if orientation is allowed, rotatable views will be rotated to that orientation
        mRecognizerView.setOrientationAllowedListener(new OrientationAllowedListener() {
            @Override
            public boolean isOrientationAllowed(Orientation orientation) {
                // allow all orientations
                return true;
            }
        });

        // define which metadata will be available in MetadataListener (onMetadataAvailable method)
        MetadataSettings metadataSettings = new MetadataSettings();
        // detection metadata should be available in MetadataListener
        // detection metadata are all metadata objects from com.microblink.metadata.detection package
        metadataSettings.setDetectionMetadataAllowed(true);
        // set metadata listener and defined metadata settings
        // metadata listener will obtain selected metadata
        mRecognizerView.setMetadataListener(this, metadataSettings);

        // animate rotatable views on top of scanner view
        mRecognizerView.setAnimateRotation(true);

        // zoom and crop camera instead of fitting it into view
        mRecognizerView.setAspectMode(CameraAspectMode.ASPECT_FILL);

        // instantiate the camera permission manager
        mCameraPermissionManager = new CameraPermissionManager(this);
        // get the built in layout that should be displayed when camera permission is not given
        View v = mCameraPermissionManager.getAskPermissionOverlay();
        if (v != null) {
            // add it to the current layout that contains the recognizer view
            ViewGroup vg = (ViewGroup) findViewById(R.id.my_default_scan_root);
            vg.addView(v);
        }

        // create scanner (make sure scan settings and listeners were set prior calling create)
        mRecognizerView.create();

        // after scanner is created, you can add your views to it

        // initialize QuadViewManager
        // Use provided factory method from QuadViewManagerFactory that can instantiate the
        // QuadViewManager based on several presets defined in QuadViewPreset enum. Details about
        // each of them can be found in javadoc. This method automatically adds the QuadView as a
        // child of RecognizerView.
        // Here we use preset which sets up quad view in the same style as used in built-in PDF417 ScanActivity.
        mQvManager= QuadViewManagerFactory.createQuadViewFromPreset(mRecognizerView, QuadViewPreset.DEFAULT_CORNERS_FROM_PDF417_SCAN_ACTIVITY);

        // create PointSetView
        mPointSetView = new PointSetView(this, null);

        // add point set view to scanner view as fixed (non-rotatable) view
        mRecognizerView.addChildView(mPointSetView, false);

        // inflate buttons layout from XML
        mLayout = getLayoutInflater().inflate(mobi.pdf417.demo.R.layout.default_barcode_camera_overlay, null);

        // setup back button
        mBackButton = (Button) mLayout.findViewById(R.id.defaultBackButton);
        mBackButton.setText(getString(R.string.mbHome));

        mBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // obtain a reference to torch button, but make it invisible
        // we will make it appear only if device supports torch control.
        // That information will be known only after camera has become active.
        mTorchButton = (Button) mLayout.findViewById(R.id.defaultTorchButton);
        mTorchButton.setVisibility(View.GONE);

        // add buttons layout as rotatable view on top of scanner view
        mRecognizerView.addChildView(mLayout, true);

        // if ROI is set, then create and add ROI layout
        if(extras != null) {
            boolean rotateRoi = extras.getBoolean(Pdf417ScanActivity.EXTRAS_ROTATE_ROI);
            Rectangle roi = extras.getParcelable(Pdf417ScanActivity.EXTRAS_ROI);
            if(roi != null) {
                // tell scanner to use ROI
                mRecognizerView.setScanningRegion(roi, rotateRoi);

                // add ROI layout
                View roiView = getLayoutInflater().inflate(R.layout.roi_overlay, null);
                mRecognizerView.addChildView(roiView, rotateRoi);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // all activity lifecycle events must be passed on to RecognizerView
        if(mRecognizerView != null) {
            mRecognizerView.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // all activity lifecycle events must be passed on to RecognizerView
        if(mRecognizerView != null) {
            mRecognizerView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // all activity lifecycle events must be passed on to RecognizerView
        // if permission was not given, RecognizerView was not resumed so we
        // cannot pause it
        if(mRecognizerView != null) {
            mRecognizerView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // all activity lifecycle events must be passed on to RecognizerView
        if(mRecognizerView != null) {
            mRecognizerView.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // all activity lifecycle events must be passed on to RecognizerView
        if(mRecognizerView != null) {
            mRecognizerView.destroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // change configuration of scanner's internal views
        if (mRecognizerView != null) {
            mRecognizerView.changeConfiguration(newConfig);
        }
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
        if(mRecognizerView.isCameraTorchSupported()) {
            mTorchButton.setVisibility(View.VISIBLE);
            mTorchButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // setTorchEnabled returns true if torch turning off/on has succeeded
                    mRecognizerView.setTorchState(!mTorchEnabled, new SuccessCallback() {
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
    public void onMetadataAvailable(Metadata metadata) {
        // This method will be called when metadata becomes available during recognition process.
        // Here, for every metadata type that is allowed through metadata settings,
        // desired actions can be performed.
        if (metadata instanceof FailedDetectionMetadata) {
            // this metadata object indicates that during recognition process nothing was detected.
            if (mPointSetView != null) {
                // clear points
                mPointSetView .setPointSet(null);
            }
            if (mQvManager != null) {
                // begin quadrilateral animation to its default position
                // (internally displays FAIL status)
                mQvManager.animateQuadToDefaultPosition();
            }
        } else if (mPointSetView != null && metadata instanceof PointsDetectionMetadata) {
            // this metadata object is passed when recognizer detects an object that is represented by
            // points of interest (e.g. QR code)
            // show the points of interest inside points view
            mPointSetView.setPointSet(((PointsDetectionMetadata) metadata).getPoints());
        } else if (mQvManager != null && metadata instanceof QuadrilateralDetectionMetadata) {
            // this metadata object is passed when recognizer detects an object that is represented by quadrilateral
            // update detection position
            QuadrilateralDetectionMetadata quadMetadata = (QuadrilateralDetectionMetadata) metadata;
            // begin quadrilateral animation to detected quadrilateral
            mQvManager.animateQuadToDetectionPosition(quadMetadata.getQuadrilateral(), quadMetadata.getDetectionStatus());
            if (mPointSetView != null) {
                mPointSetView.setPointSet(null);
            }
        }
    }

    /**
     * this activity will perform 5 scans of barcode and then return the last
     * scanned one
     */
    @Override
    public void onScanningDone(RecognitionResults results) {
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
        mRecognizerView.pauseScanning();
        if (mScanCount >= 5) {
            // if we have 5 scans, return most recent result via Intent
            Intent intent = new Intent();
            intent.putExtra(Pdf417ScanActivity.EXTRAS_RECOGNITION_RESULTS, results);
            setResult(Pdf417ScanActivity.RESULT_OK, intent);
            finish();
        } else {
            // if we still do not have 5 scans, wait 2 seconds and then resume
            // scanning and reset recognition state
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mRecognizerView.resumeScanning(true);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // on API level 23, we need to pass request permission result to camera permission manager
        mCameraPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
