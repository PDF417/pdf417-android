package mobi.pdf417.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.microblink.activity.Pdf417ScanActivity;
import com.microblink.geometry.Rectangle;
import com.microblink.recognizers.barcode.BarcodeType;
import com.microblink.recognizers.barcode.pdf417.Pdf417RecognizerSettings;
import com.microblink.recognizers.barcode.pdf417.Pdf417ScanResult;
import com.microblink.recognizers.barcode.zxing.ZXingRecognizerSettings;
import com.microblink.recognizers.barcode.zxing.ZXingScanResult;
import com.microblink.recognizers.settings.RecognizerSettings;
import com.microblink.results.barcode.BarcodeDetailedData;

import java.net.MalformedURLException;
import java.net.URL;

public class Pdf417CustomUIDemo extends Activity {

    public static final String TAG = "MainActivity";

    public static final String LICENSE = "UDPICR2T-RA2LGTSD-YTEONPSJ-LE4WWOWC-5ICAIBAE-AQCAIBAE-AQCAIBAE-AQCFKMFM";

    private static final int MY_REQUEST_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.btnDefaultUINoDialog: {
            // create intent for scan activity
            Intent intent = new Intent(this, Pdf417ScanActivity.class);
            // add license that allows removing of dialog in default UI
            intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSE_KEY, LICENSE);
            // disable showing of dialog after scan
            intent.putExtra(Pdf417ScanActivity.EXTRAS_SHOW_DIALOG_AFTER_SCAN, false);

            // enable PDF417 recognizer and QR code from ZXing recognizer
            ZXingRecognizerSettings zxingSettings = new ZXingRecognizerSettings();
            zxingSettings.setScanQRCode(true);

            intent.putExtra(Pdf417ScanActivity.EXTRAS_RECOGNIZER_SETTINGS_ARRAY, new RecognizerSettings[] {new Pdf417RecognizerSettings(), zxingSettings});

            startActivityForResult(intent, MY_REQUEST_CODE);
            break;
        }
        case R.id.btnDefaultUINoLogo: {
            // create intent for scan activity
            Intent intent = new Intent(this, Pdf417ScanActivity.class);
            // add license that allows removing of dialog in default UI
            intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSE_KEY, LICENSE);
            // enable showing of dialog after scan
            intent.putExtra(Pdf417ScanActivity.EXTRAS_SHOW_DIALOG_AFTER_SCAN, true);

            // enable PDF417 recognizer and QR code from ZXing recognizer
            ZXingRecognizerSettings zxingSettings = new ZXingRecognizerSettings();
            zxingSettings.setScanQRCode(true);

            intent.putExtra(Pdf417ScanActivity.EXTRAS_RECOGNIZER_SETTINGS_ARRAY, new RecognizerSettings[]{new Pdf417RecognizerSettings(), zxingSettings});

            startActivityForResult(intent, MY_REQUEST_CODE);
            break;
        }
        case R.id.btnCustomUI: {
            // create intent for custom scan activity
            Intent intent = new Intent(this, DefaultScanActivity.class);
            // add license that allows creating custom camera overlay
            intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSE_KEY, LICENSE);

            // enable PDF417 recognizer and QR code from ZXing recognizer
            ZXingRecognizerSettings zxingSettings = new ZXingRecognizerSettings();
            zxingSettings.setScanQRCode(true);

            intent.putExtra(Pdf417ScanActivity.EXTRAS_RECOGNIZER_SETTINGS_ARRAY, new RecognizerSettings[] {new Pdf417RecognizerSettings(), zxingSettings});

            startActivityForResult(intent, MY_REQUEST_CODE);
            break;
        }
        case R.id.btnCustomUIROI: {
            // create intent for custom scan activity
            Intent intent = new Intent(this, DefaultScanActivity.class);
            // add license that allows creating custom camera overlay
            intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSE_KEY, LICENSE);

            // enable PDF417 recognizer and QR code from ZXing recognizer
            ZXingRecognizerSettings zxingSettings = new ZXingRecognizerSettings();
            zxingSettings.setScanQRCode(true);

            intent.putExtra(Pdf417ScanActivity.EXTRAS_RECOGNIZER_SETTINGS_ARRAY, new RecognizerSettings[] {new Pdf417RecognizerSettings(), zxingSettings});

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
            intent.putExtra(Pdf417ScanActivity.EXTRAS_ROI, roi);
            // if you intent to rotate your ROI view, you should set the EXTRAS_ROTATE_ROI extra to true
            // so that PDF417.mobi can adjust ROI coordinates for native library when device orientation
            // change event occurs
            intent.putExtra(Pdf417ScanActivity.EXTRAS_ROTATE_ROI, true);

            startActivityForResult(intent, MY_REQUEST_CODE);
            break;
        }
        }
    }

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
            startActivity(intent);
        }

        return barcodeDataIsUrl;
    }

    /**
     * this method is same as in Pdf417MobiDemo project
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE && resultCode == Pdf417ScanActivity.RESULT_OK) {
            // First, obtain scan results array. If scan was successful, array will contain at least one element.
            // Multiple element may be in array if multiple scan results from single image were allowed in settings.

            Parcelable[] resultArray = data.getParcelableArrayExtra(Pdf417ScanActivity.EXTRAS_RECOGNITION_RESULT_LIST);

            StringBuilder sb = new StringBuilder();

            for(Parcelable p : resultArray) {
                if(p instanceof Pdf417ScanResult) { // check if scan result is result of Pdf417 recognizer
                    Pdf417ScanResult result = (Pdf417ScanResult) p;
                    // getStringData getter will return the string version of barcode contents
                    String barcodeData = result.getStringData();
                    // isUncertain getter will tell you if scanned barcode contains some uncertainties
                    boolean uncertainData = result.isUncertain();
                    // getRawData getter will return the raw data information object of barcode contents
                    BarcodeDetailedData rawData = result.getRawData();
                    // BarcodeDetailedData contains information about barcode's binary layout, if you
                    // are only interested in raw bytes, you can obtain them with getAllData getter
                    byte[] rawDataBuffer = rawData.getAllData();

                    // if data is URL, open the browser and stop processing result
                    if(checkIfDataIsUrlAndCreateIntent(barcodeData)) {
                        return;
                    } else {
                        // add data to string builder
                        sb.append("PDF417 scan data");
                        if (uncertainData) {
                            sb.append("This scan data is uncertain!\n\n");
                        }
                        sb.append(" string data:\n");
                        sb.append(barcodeData);
                        if (rawData != null) {
                            sb.append("\n\n");
                            sb.append("PDF417 raw data:\n");
                            sb.append(rawData.toString());
                            sb.append("\n");
                            sb.append("PDF417 raw data merged:\n");
                            sb.append("{");
                            for (int i = 0; i < rawDataBuffer.length; ++i) {
                                sb.append((int) rawDataBuffer[i] & 0x0FF);
                                if (i != rawDataBuffer.length - 1) {
                                    sb.append(", ");
                                }
                            }
                            sb.append("}\n\n\n");
                        }
                    }
                } else if(p instanceof ZXingScanResult) { // check if scan result is result of ZXing recognizer
                    ZXingScanResult result= (ZXingScanResult) p;
                    // with getBarcodeType you can obtain barcode type enum that tells you the type of decoded barcode
                    BarcodeType type = result.getBarcodeType();
                    // as with PDF417, getStringData will return the string contents of barcode
                    String barcodeData = result.getStringData();
                    if(checkIfDataIsUrlAndCreateIntent(barcodeData)) {
                        return;
                    } else {
                        sb.append(type.name());
                        sb.append(" string data:\n");
                        sb.append(barcodeData);
                        sb.append("\n\n\n");
                    }
                }
            }

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            startActivity(Intent.createChooser(intent, getString(R.string.UseWith)));
        }
    }
}
