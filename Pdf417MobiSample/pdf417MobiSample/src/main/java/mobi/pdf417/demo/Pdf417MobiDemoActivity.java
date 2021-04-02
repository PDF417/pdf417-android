package mobi.pdf417.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.microblink.blinkbarcode.MicroblinkSDK;
import com.microblink.blinkbarcode.entities.recognizers.RecognizerBundle;
import com.microblink.blinkbarcode.entities.recognizers.blinkbarcode.barcode.BarcodeRecognizer;
import com.microblink.blinkbarcode.uisettings.ActivityRunner;
import com.microblink.blinkbarcode.uisettings.BarcodeUISettings;

import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;

public class Pdf417MobiDemoActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 1337;

    /**
     * Barcode recognizer that will perform recognition of images
     */
    private BarcodeRecognizer mBarcodeRecognizer;

    /**
     * Recognizer bundle that will wrap the barcode recognizer in order for recognition to be performed
     */
    private RecognizerBundle mRecognizerBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupVersionTextView();

        // You have to enable recognizers and barcode types you want to support
        // Don't enable what you don't need, it will significantly decrease scanning performance
        mBarcodeRecognizer = new BarcodeRecognizer();
        mBarcodeRecognizer.setScanPdf417(true);
        mBarcodeRecognizer.setScanQrCode(true);

        mRecognizerBundle = new RecognizerBundle(mBarcodeRecognizer);
    }

    public void onScanButtonClick(View v) {
        // start default barcode scanning activity
        BarcodeUISettings uiSettings = new BarcodeUISettings(mRecognizerBundle);
        uiSettings.setBeepSoundResourceID(R.raw.beep);
        ActivityRunner.startActivityForResult(this, MY_REQUEST_CODE, uiSettings);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            handleScanResultIntent(data);
        }
    }

    private void handleScanResultIntent(Intent data) {
        // updates bundled recognizers with results that have arrived
        mRecognizerBundle.loadFromIntent(data);
        // after calling mRecognizerBundle.loadFromIntent, results are stored in mBarcodeRecognizer
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
        StringBuilder sb = new StringBuilder(result.getBarcodeType().name());
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

    /**
     * Builds string which contains information about application version and library version.
     */
    private void setupVersionTextView() {
        String versionString;

        versionString = "Library version: " +  MicroblinkSDK.getNativeLibraryVersionString();

        TextView tvVersion = findViewById(R.id.tvVersion);
        tvVersion.setText(versionString);
    }

}
