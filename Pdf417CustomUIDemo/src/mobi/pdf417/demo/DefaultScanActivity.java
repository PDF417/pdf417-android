package mobi.pdf417.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.photopay.geometry.Point;
import net.photopay.geometry.PointSet;
import net.photopay.geometry.Quadrilateral;
import net.photopay.geometry.Rectangle;
import net.photopay.geometry.quadDrawers.QuadrilateralDrawer;
import net.photopay.view.CameraEventsListener;
import net.photopay.view.NotSupportedReason;
import net.photopay.view.recognition.DetectionStatus;
import net.photopay.view.recognition.Pdf417MobiScanResultListener;
import net.photopay.view.recognition.Pdf417MobiView;
import net.photopay.view.recognition.RecognizerViewEventListener;
import net.photopay.view.viewfinder.PointSetView;
import net.photopay.view.viewfinder.QuadView;

import java.util.ArrayList;
import java.util.List;

import mobi.pdf417.Pdf417MobiScanData;
import mobi.pdf417.Pdf417MobiSettings;
import mobi.pdf417.activity.Pdf417ScanActivity;

public class DefaultScanActivity extends Activity implements Pdf417MobiScanResultListener, CameraEventsListener, RecognizerViewEventListener {

    private int mScanCount = 0;
    private Handler mHandler = new Handler();

    /** This is PDF417.mobi view that controls camera and scanning */
    private Pdf417MobiView mPdf417MobiView;
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
        mPdf417MobiView = new Pdf417MobiView(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            // setup scanner parameters
            mPdf417MobiView.setLicenseKey(extras.getString(Pdf417ScanActivity.EXTRAS_LICENSE_KEY));
            mPdf417MobiView.setScanSettings((Pdf417MobiSettings)extras.getParcelable(Pdf417ScanActivity.EXTRAS_SETTINGS));
        }

        // add listeners
        mPdf417MobiView.setPdf417MobiScanResultListener(this);
        mPdf417MobiView.setCameraEventsListener(this);
        mPdf417MobiView.setRecognizerViewEventListener(this);

        // animate rotatable views on top of scanner view
        mPdf417MobiView.setAnimateRotation(true);

        // create scanner (make sure scan settings and listeners were set prior calling create)
        mPdf417MobiView.create();

        // after scanner is created, you can add your views to it and you can add it to your view hierarchy

        // set scanner view as the only view in activity
        setContentView(mPdf417MobiView);

        // create QuadView
        mQuadView = new QuadView(this, null, new QuadrilateralDrawer(this), 0.11, 0.11, mPdf417MobiView.getHostScreenOrientation());
        // create PointSetView
        mPointSetView = new PointSetView(this, null);

        // add quad view and point set view to scanner view as fixed (non-rotatable) views
        mPdf417MobiView.addChildView(mQuadView, false);
        mPdf417MobiView.addChildView(mPointSetView, false);

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
        mPdf417MobiView.addChildView(mLayout, true);

        // if ROI is set, then create and add ROI layout
        if(extras != null) {
            boolean rotateRoi = extras.getBoolean(Pdf417ScanActivity.EXTRAS_ROTATE_ROI);
            Rectangle roi = extras.getParcelable(Pdf417ScanActivity.EXTRAS_ROI);
            if(roi != null) {
                // tell scanner to use ROI
                mPdf417MobiView.setScanningRegion(roi, rotateRoi);

                // add ROI layout
                View roiView = getLayoutInflater().inflate(R.layout.roi_overlay, null);
                mPdf417MobiView.addChildView(roiView, rotateRoi);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // resume scanner
        mPdf417MobiView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // pause scanner
        mPdf417MobiView.pause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // start scanner
        mPdf417MobiView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // stop scanner
        mPdf417MobiView.stop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // change configuration of scanner's internal views
        mPdf417MobiView.changeConfiguration(newConfig);
    }

    /**
     * Callback which is called when user clicks the back button.
     *
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
    public void onScanningDone(ArrayList<Pdf417MobiScanData> scanDataList) {
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
            intent.putExtra(Pdf417ScanActivity.EXTRAS_RESULT, scanDataList.get(0));
            intent.putParcelableArrayListExtra(Pdf417ScanActivity.EXTRAS_RESULT_LIST, scanDataList);
            setResult(Pdf417ScanActivity.RESULT_OK, intent);
            finish();
        } else {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mPdf417MobiView.resumeScanning();
                }
            }, 2000);
        }
    }

    @Override
    public void onCameraPreviewStarted() {
        // if device supports torch, make torch button visible and setup it
        // isCameraTorchSupported returns true if device supports controlling the torch
        if(mPdf417MobiView.isCameraTorchSupported()) {
            mTorchButton.setVisibility(View.VISIBLE);
            mTorchButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // setTorchEnabled returns true if torch turning off/on has succeeded
                    boolean success = mPdf417MobiView.setTorchState(!mTorchEnabled);
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
