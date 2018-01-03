package mobi.pdf417.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.AnyThread;
import android.view.View;
import android.widget.TextView;

import com.microblink.MicroblinkSDK;
import com.microblink.entities.recognizers.RecognizerBundle;
import com.microblink.entities.recognizers.blinkbarcode.barcode.BarcodeRecognizer;
import com.microblink.uisettings.ActivityRunner;
import com.microblink.uisettings.Pdf417ScanUISettings;
import com.microblink.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class Pdf417MobiDemo extends Activity {

    private static final int MY_REQUEST_CODE = 1337;

    /**
     * Barcode recognizer that will perform recognition of images
     */
    private BarcodeRecognizer mBarcodeRecognizer;

    /**
     * Recognizer bundle that will wrap the barcode recognizer in order for recognition to be performed
     */
    private RecognizerBundle mRecognizerBundle;

    private static final String TAG = "Pdf417MobiDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvVersion = findViewById(R.id.tvVersion);
        tvVersion.setText(buildVersionString());

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
    }

    /**
     * Builds string which contains information about application version and library version.
     * @return String which contains information about application version and library version.
     */
    private String buildVersionString() {
        String nativeVersionString = MicroblinkSDK.getNativeLibraryVersionString();
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String appVersion = pInfo.versionName;
            int appVersionCode = pInfo.versionCode;

            return "Application version: " +
                    appVersion +
                    ", build " +
                    appVersionCode +
                    "\nLibrary version: " +
                    nativeVersionString;
        } catch (NameNotFoundException e) {
            return "";
        }
    }

    public void btnScan_click(View v) {
        Log.i(TAG, "scan will be performed");

        // create settings for Pdf417ScanActivity
        Pdf417ScanUISettings uiSettings = new Pdf417ScanUISettings(mRecognizerBundle);
        // start Pdf417ScanActivity
        ActivityRunner.startActivityForResult(this, MY_REQUEST_CODE, uiSettings);
    }

    /**
     * Checks whether data is URL and in case of URL data creates intent and starts activity.
     * @param data String to check.
     * @return If data is URL returns {@code true}, else returns {@code false}.
     */
    @AnyThread
    private boolean checkIfDataIsUrlAndCreateIntent(String data) {
        // if barcode contains URL, create intent for browser
        // else, contain intent for message
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
            startActivity(Intent.createChooser(intent, getString(R.string.UseWith)));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // First, obtain recognition result
            // method loadFromIntent will update bundled recognizers with results that have arrived
            mRecognizerBundle.loadFromIntent(data);
            showResults();
        }
    }
}
