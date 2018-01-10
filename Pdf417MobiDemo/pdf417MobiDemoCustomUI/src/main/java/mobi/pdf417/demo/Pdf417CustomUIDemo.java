package mobi.pdf417.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.view.View;

import com.microblink.entities.recognizers.RecognizerBundle;
import com.microblink.entities.recognizers.blinkbarcode.barcode.BarcodeRecognizer;
import com.microblink.fragment.RecognizerRunnerFragment;
import com.microblink.fragment.overlay.BarcodeOverlayController;
import com.microblink.fragment.overlay.ScanningOverlay;
import com.microblink.geometry.Rectangle;
import com.microblink.recognition.RecognitionSuccessType;
import com.microblink.uisettings.ActivityRunner;
import com.microblink.uisettings.BarcodeUISettings;
import com.microblink.view.recognition.ScanResultListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

// ScanningOverlayBinder and ScanResultListener interfaces must be implemented for case when RecognizerRunnerFragment is used
public class Pdf417CustomUIDemo extends Activity implements RecognizerRunnerFragment.ScanningOverlayBinder, ScanResultListener {
    private static final int MY_REQUEST_CODE = 1337;

    /**
     * Barcode recognizer that will perform recognition of images
     */
    private BarcodeRecognizer mBarcodeRecognizer;

    /**
     * Recognizer bundle that will wrap the barcode recognizer in order for recognition to be performed
     */
    private RecognizerBundle mRecognizerBundle;

    /**
     * Recognizer runner fragment will be shown on top of layout view with BarcodeOverlayController.
     */
    private RecognizerRunnerFragment mRecognizerRunnerFragment;

    /**
     * BarcodeOverlayController displays same UI as BarcodeScanActivity, but over given RecognizerRunnerFragment.
     * Association is done via {@link #getScanningOverlay()} method in fragment's {@link RecognizerRunnerFragment#onAttach(Activity)}
     * lifecycle event, so you must ensure that mScanOverlay exists at this time.
     */
    private BarcodeOverlayController mScanOverlay = createRecognizerAndOverlay();

    private BarcodeOverlayController createRecognizerAndOverlay() {
        // create recognizers

        // Don't enable recognizers and barcode types which you don't actually use because this will
        // significantly decrease the scanning speed.

        // create new BarcodeRecognizer
        mBarcodeRecognizer = new BarcodeRecognizer();
        // enable scanning of PDF417 2D barcode
        mBarcodeRecognizer.setScanPDF417(true);
        // enable scanning of QR code
        mBarcodeRecognizer.setScanQRCode(true);

        // create bundle BarcodeRecognizer within RecognizerBundle
        mRecognizerBundle = new RecognizerBundle(mBarcodeRecognizer);

        // create BarcodeOverlayController
        return new BarcodeOverlayController(new BarcodeUISettings(mRecognizerBundle), this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mRecognizerRunnerFragment = (RecognizerRunnerFragment) getFragmentManager().findFragmentById(R.id.recognizer_runner_view_container);
        }
    }

    /**
     * Handles button clicks.
     */
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
        case R.id.btnDefaultActivity: {
            // invoke default scan activity (BarcodeScanActivity)
            BarcodeUISettings uiSettings = new BarcodeUISettings(mRecognizerBundle);
            ActivityRunner.startActivityForResult(this, MY_REQUEST_CODE, uiSettings);
            break;
        }
        case R.id.btnDefaultOverlay: {
            // show container view and add recognizer runner fragment to it
            if (mRecognizerRunnerFragment == null) {
                View scanLayout = findViewById(R.id.recognizer_runner_view_container);
                scanLayout.setVisibility(View.VISIBLE);

                mRecognizerRunnerFragment = new RecognizerRunnerFragment();
                getFragmentManager().beginTransaction()
                                    .add(R.id.recognizer_runner_view_container, mRecognizerRunnerFragment)
                                    .addToBackStack(null)
                                    .commit();
            }
            break;
        }
        case R.id.btnCustomUI: {
            // create intent for custom scan activity
            Intent intent = new Intent(this, DefaultScanActivity.class);

            // add RecognizerBundle to intent
            mRecognizerBundle.saveToIntent(intent);

            startActivityForResult(intent, MY_REQUEST_CODE);
            break;
        }
        case R.id.btnCustomUIROI: {
            // create intent for custom scan activity
            Intent intent = new Intent(this, DefaultScanActivity.class);

            // add RecognizerBundle to intent
            mRecognizerBundle.saveToIntent(intent);

            // define scanning region
            // first parameter of rectangle is x-coordinate represented as percentage
            // of view width*, second parameter is y-coordinate represented as percentage
            // of view height*, third parameter is region width represented as percentage
            // of view width* and fourth parameter is region height represented as percentage
            // of view heigth*
            //
            // * view width and height are defined in current context, i.e. they depend on
            // screen orientation. If you allow your ROI view to be rotated, then in portrait
            // view width will be smaller than height, whilst in landscape orientation width
            // will be larger than height. This complies with view designer preview in eclipse ADT.
            // If you choose not to rotate your ROI view, then your ROI view will be layout either
            // in portrait or landscape, depending on setting for your camera activity in AndroidManifest.xml
            Rectangle roi = new Rectangle(0.2f, 0.1f, 0.5f, 0.4f);
            intent.putExtra(DefaultScanActivity.EXTRAS_ROI, roi);
            // if you intent to rotate your ROI view, you should set the EXTRAS_ROTATE_ROI extra to true
            // so that PDF417.mobi can adjust ROI coordinates for native library when device orientation
            // change event occurs
            intent.putExtra(DefaultScanActivity.EXTRAS_ROTATE_ROI, true);

            startActivityForResult(intent, MY_REQUEST_CODE);
            break;
        }
        }
    }

    /**
     * Checks whether data is URL and in case of URL data creates intent for browser and starts
     * activity.
     * @param data String to check.
     * @return If data is URL returns {@code true}, else returns {@code false}.
     */
    @AnyThread
    private boolean checkIfDataIsUrlAndCreateIntent(String data) {
        // if barcode contains URL, create intent for browser
        boolean barcodeDataIsUrl;

        try {
            @SuppressWarnings("unused")
            URL url = new URL(data);
            barcodeDataIsUrl = true;
        } catch (MalformedURLException exc) {
            barcodeDataIsUrl = false;
        }

        if (barcodeDataIsUrl) {
            // create intent for browser
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(data));
            startActivity(intent);
        }

        return barcodeDataIsUrl;
    }

    @AnyThread
    private void showResults() {
        // after calling mRecognizerBundle.loadFromIntent, results are stored within mBarcodeRecognizer

        BarcodeRecognizer.Result result = mBarcodeRecognizer.getResult();

        if (!checkIfDataIsUrlAndCreateIntent(result.getStringData())) {

            StringBuilder sb = new StringBuilder(result.getBarcodeFormat().name());
            sb.append("\n\n");

            if (result.isUncertain()) {
                sb.append("\nThis scan data is uncertain!\n\nString data:\n");
            }
            sb.append(result.getStringData());

            byte[] rawDataBuffer = result.getRawData();
            sb.append("\n");
            sb.append("Raw data:\n");
            sb.append(Arrays.toString(rawDataBuffer));
            sb.append("\n\n\n");

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            startActivity(Intent.createChooser(intent, getString(R.string.UseWith)));
        }
    }

    /**
     * this method is same as in Pdf417MobiDemo project
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // First, obtain recognition result
            // method loadFromIntent will update bundled recognizers with results that have arrived
            mRecognizerBundle.loadFromIntent(data);
            showResults();
        }
    }

    @NonNull
    @Override
    public ScanningOverlay getScanningOverlay() {
        return mScanOverlay;
    }

    @WorkerThread
    private void removeFragment() {
        getFragmentManager().popBackStack();
        mRecognizerRunnerFragment = null;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View scanLayout = findViewById(R.id.recognizer_runner_view_container);
                scanLayout.setVisibility(View.GONE);
            }
        });
    }

    // called on worker thread when RecognizerRunnerFragment finishes recognition
    @Override
    public void onScanningDone(@NonNull RecognitionSuccessType recognitionSuccessType) {
        // pause scanning to prevent further scanning and mutating of mBarcodeRecognizer's result
        // while fragment is being removed
        mRecognizerRunnerFragment.getRecognizerRunnerView().pauseScanning();
        removeFragment();
        showResults();
    }

    @Override
    public void onBackPressed() {
        if ( getFragmentManager().getBackStackEntryCount() > 0 ) {
            removeFragment();
        } else {
            super.onBackPressed();
        }
    }
}
