package mobi.pdf417.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.view.View;
import android.webkit.URLUtil;

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

import java.util.Arrays;

// ScanningOverlayBinder must be implemented for case when RecognizerRunnerFragment is used
public class MainActivity extends Activity implements RecognizerRunnerFragment.ScanningOverlayBinder {

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
        // You have to enable recognizers and barcode types you want to support
        // Don't enable what you don't need, it will significantly decrease scanning performance
        mBarcodeRecognizer = new BarcodeRecognizer();
        mBarcodeRecognizer.setScanPDF417(true);
        mBarcodeRecognizer.setScanQRCode(true);

        mRecognizerBundle = new RecognizerBundle(mBarcodeRecognizer);

        return new BarcodeOverlayController(new BarcodeUISettings(mRecognizerBundle), new ScanResultListener() {
            // called when RecognizerRunnerFragment finishes recognition
            @Override
            @WorkerThread
            public void onScanningDone(@NonNull RecognitionSuccessType recognitionSuccessType) {
                // pause scanning to prevent further scanning and mutating of mBarcodeRecognizer's result
                // while fragment is being removed
                mRecognizerRunnerFragment.getRecognizerRunnerView().pauseScanning();
                removeFragment();
                handleScanResult();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mRecognizerRunnerFragment = (RecognizerRunnerFragment) getFragmentManager().findFragmentById(R.id.recognizer_runner_view_container);
        }
    }

    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btnDefaultActivity: {
                startDefaultScanActivity();
                break;
            }
            case R.id.btnDefaultOverlay: {
                showScanFragment();
                break;
            }
            case R.id.btnCustomUI: {
                startCustomUiActivity();
                break;
            }
            case R.id.btnCustomUIROI: {
                startCustomUiActivityWithCustomScanRegion();
                break;
            }
        }
    }

    private void startDefaultScanActivity() {
        BarcodeUISettings uiSettings = new BarcodeUISettings(mRecognizerBundle);
        ActivityRunner.startActivityForResult(this, MY_REQUEST_CODE, uiSettings);
    }

    private void showScanFragment() {
        if (mRecognizerRunnerFragment == null) {
            View scanLayout = findViewById(R.id.recognizer_runner_view_container);
            scanLayout.setVisibility(View.VISIBLE);

            mRecognizerRunnerFragment = new RecognizerRunnerFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.recognizer_runner_view_container, mRecognizerRunnerFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void startCustomUiActivity() {
        Intent intent = new Intent(this, CustomUIScanActivity.class);
        // add RecognizerBundle to intent
        mRecognizerBundle.saveToIntent(intent);
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    private void startCustomUiActivityWithCustomScanRegion() {
        Intent intent = new Intent(this, CustomUIScanActivity.class);

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
        // screen orientation. If you allow your scan region view to be rotated, then in portrait
        // view width will be smaller than height, whilst in landscape orientation width
        // will be larger than height. This complies with view designer preview in eclipse ADT.
        // If you choose not to rotate your ROI view, then your ROI view will be layout either
        // in portrait or landscape, depending on setting for your camera activity in AndroidManifest.xml
        Rectangle scanRegion = new Rectangle(0.2f, 0.1f, 0.5f, 0.4f);
        intent.putExtra(CustomUIScanActivity.EXTRAS_SCAN_REGION, scanRegion);
        // if you intent to rotate your ROI view, you should set the EXTRAS_ROTATE_SCAN_REGION extra to true
        // so that PDF417.mobi can adjust ROI coordinates for native library when device orientation
        // change event occurs
        intent.putExtra(CustomUIScanActivity.EXTRAS_ROTATE_SCAN_REGION, true);

        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // updates bundled recognizers with results that have arrived
            mRecognizerBundle.loadFromIntent(data);
            handleScanResult();
        }
    }

    private void handleScanResult() {
        BarcodeRecognizer.Result result = mBarcodeRecognizer.getResult();

        //do what you want with the result
        if (URLUtil.isValidUrl(result.getStringData())) {
            openScanResultInBrowser(result);
        } else {
            shareScanResult(result);
        }
    }

    private void openScanResultInBrowser(BarcodeRecognizer.Result result) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(result.getStringData()));
        startActivity(Intent.createChooser(intent, getString(R.string.UseWith)));
    }

    private void shareScanResult(BarcodeRecognizer.Result result) {
        StringBuilder sb = new StringBuilder(result.getBarcodeFormat().name());
        sb.append("\n\n");

        if (result.isUncertain()) {
            sb.append("\nThis scan data is uncertain!\n\nString data:\n");
        }
        sb.append(result.getStringData());

        sb.append("\nRaw data:\n");
        sb.append(Arrays.toString(result.getRawData()));
        sb.append("\n\n\n");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(intent, getString(R.string.UseWith)));
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

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            removeFragment();
        } else {
            super.onBackPressed();
        }
    }
}
