package mobi.pdf417.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.microblink.activity.Pdf417ScanActivity;
import com.microblink.geometry.Point;
import com.microblink.geometry.PointSet;
import com.microblink.geometry.Quadrilateral;
import com.microblink.geometry.Rectangle;
import com.microblink.geometry.quadDrawers.QuadrilateralDrawer;
import com.microblink.hardware.SuccessCallback;
import com.microblink.recognition.InvalidLicenceKeyException;
import com.microblink.recognizers.BaseRecognitionResult;
import com.microblink.recognizers.settings.RecognizerSettings;
import com.microblink.view.CameraAspectMode;
import com.microblink.view.CameraEventsListener;
import com.microblink.view.NotSupportedReason;
import com.microblink.view.recognition.DetectionStatus;
import com.microblink.view.recognition.RecognitionType;
import com.microblink.view.recognition.RecognizerView;
import com.microblink.view.recognition.RecognizerViewEventListener;
import com.microblink.view.recognition.ScanResultListener;
import com.microblink.view.viewfinder.PointSetView;
import com.microblink.view.viewfinder.QuadView;

import java.util.List;

public class DefaultScanActivity extends Activity implements ScanResultListener, CameraEventsListener, RecognizerViewEventListener {

    private int mScanCount = 0;
    private Handler mHandler = new Handler();

    /** This is recognizer view that controls camera and scanning */
    private RecognizerView mRecognizerView;
    /** This is a builtin quadrilateral view that can show quadrilateral around detected object */
    private QuadView mQuadView;
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

        // create a scanner view
        mRecognizerView = new RecognizerView(this);

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
            Parcelable[] settingsArrayRaw = extras.getParcelableArray(Pdf417ScanActivity.EXTRAS_RECOGNIZER_SETTINGS_ARRAY);
            // unfortunately, direct cast is not allowed, instead we need to cast each and every parcelable element from parcelable array
            // RecognizerSettings is abstract recognizer settings class. Every settings class described in pdf417MobiDemo inherits that class.
            RecognizerSettings[] settingsArray = new RecognizerSettings[settingsArrayRaw.length];
            for(int i = 0; i < settingsArrayRaw.length; ++i) {
                settingsArray[i] = (RecognizerSettings)settingsArrayRaw[i];
            }

            mRecognizerView.setRecognitionSettings(settingsArray);
        }

        // add listeners
        mRecognizerView.setScanResultListener(this);
        mRecognizerView.setCameraEventsListener(this);
        mRecognizerView.setRecognizerViewEventListener(this);

        // animate rotatable views on top of scanner view
        mRecognizerView.setAnimateRotation(true);

        // zoom and crop camera instead of fitting it into view
        mRecognizerView.setAspectMode(CameraAspectMode.ASPECT_FILL);

        // create scanner (make sure scan settings and listeners were set prior calling create)
        mRecognizerView.create();

        // after scanner is created, you can add your views to it and you can add it to your view hierarchy

        // set scanner view as the only view in activity
        setContentView(mRecognizerView);

        // create QuadView
        mQuadView = new QuadView(this, null, new QuadrilateralDrawer(this), 0.11, 0.11, mRecognizerView.getHostScreenOrientation());
        // create PointSetView
        mPointSetView = new PointSetView(this, null);

        // add quad view and point set view to scanner view as fixed (non-rotatable) views
        mRecognizerView.addChildView(mQuadView, false);
        mRecognizerView.addChildView(mPointSetView, false);

        // inflate buttons layout from XML
        mLayout = getLayoutInflater().inflate(mobi.pdf417.demo.R.layout.default_barcode_camera_overlay, null);

        // setup back button
        mBackButton = (Button) mLayout.findViewById(R.id.backButton);
        mBackButton.setText(getString(R.string.photopayHome));

        mBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // obtain a reference to torch button, but make it invisible
        // we will make it appear only if device supports torch control.
        // That information will be known only after camera has become active.
        mTorchButton = (Button) mLayout.findViewById(R.id.torchButton);
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
        // start scanner (make sure create was called prior calling start)
        mRecognizerView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // resume scanner (make sure start was called prior calling resume)
        mRecognizerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // pause scanner (make sure resume was called prior calling pause)
        mRecognizerView.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // stop scanner (make sure pause was called prior calling stop)
        mRecognizerView.stop();
    }

    @Override
    protected void onDestroy() {
        // destroy scanner (make sure stop was called prior calling destroy)
        mRecognizerView.destroy();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // change configuration of scanner's internal views
        mRecognizerView.changeConfiguration(newConfig);
    }

    /**
     * Callback which is called when user clicks the back button.
     */
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, null);
        finish();
    }

    /**
     * this activity will perform 5 scans of barcode and then return the last
     * scanned one
     */
    @Override
    public void onScanningDone(BaseRecognitionResult[] baseRecognitionResults, RecognitionType recognitionType) {
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
        if (mScanCount >= 5) {
            Intent intent = new Intent();
            intent.putExtra(Pdf417ScanActivity.EXTRAS_RECOGNITION_RESULT_LIST, baseRecognitionResults);
            setResult(Pdf417ScanActivity.RESULT_OK, intent);
            finish();
        } else {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mRecognizerView.resumeScanning(true);
                }
            }, 2000);
        }
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
                                    if(success) {
                                        mTorchEnabled = !mTorchEnabled;
                                        if(mTorchEnabled) {
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

    }

    @Override
    public void onStartupError(Throwable throwable) {
        // this method is called when error in initialization of either
        // camera or native library occurs
        errorDialog();
    }

    @Override
    public void onNotSupported(NotSupportedReason notSupportedReason) {
        // this method is called when scanner is used on unsupported device
        Log.e("ERROR", "Not supported reason: " + notSupportedReason);
        errorDialog();
    }

    private void errorDialog() {
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
    public void onNothingDetected() {
        // this method is called when nothing has been detected
        mQuadView.setDefaultTarget();
        mQuadView.publishDetectionStatus(DetectionStatus.FAIL);
        mPointSetView.setPointSet(null);
    }

    @Override
    public void onDisplayPointsOfInterest(List<Point> points, DetectionStatus detectionStatus) {
        mQuadView.publishDetectionStatus(detectionStatus);
        mPointSetView.setPointSet(new PointSet(points));
    }

    @Override
    public void onDisplayQuadrilateralObject(Quadrilateral quadrilateral, DetectionStatus detectionStatus) {
        mPointSetView.setPointSet(null);
        mQuadView.setNewTarget(quadrilateral);
        mQuadView.publishDetectionStatus(detectionStatus);
    }
}
