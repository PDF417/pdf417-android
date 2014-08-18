package mobi.pdf417.demo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import mobi.pdf417.Pdf417MobiScanData;
import mobi.pdf417.Pdf417MobiSettings;
import mobi.pdf417.activity.Pdf417ScanActivity;
import net.photopay.barcode.BarcodeDetailedData;
import net.photopay.barcode.BarcodeElement;
import net.photopay.barcode.ElementType;
import net.photopay.base.BaseBarcodeActivity;
import net.photopay.geometry.Rectangle;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Pdf417CustomUIDemo extends Activity {

    public static final String TAG = "MainActivity";

    public static final String LICENSE = "A5EV-UIU5-7EHU-DP5L-Y2AK-YWIA-NYIE-Z2QE";

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
            intent.putExtra(BaseBarcodeActivity.EXTRAS_LICENSE_KEY, LICENSE);
            // create settings object
            Pdf417MobiSettings sett = new Pdf417MobiSettings();
            // set this to true to enable PDF417 scanning
            sett.setPdf417Enabled(true);
            // set this to true to enable QR code scanning
            sett.setQrCodeEnabled(true);
            // set this to true to prevent showing dialog after successful scan
            sett.setDontShowDialog(true);
            // put settings as intent extra
            intent.putExtra(BaseBarcodeActivity.EXTRAS_SETTINGS, sett);
            startActivityForResult(intent, MY_REQUEST_CODE);
            break;
        }
        case R.id.btnDefaultUINoLogo: {
            // create intent for scan activity
            Intent intent = new Intent(this, Pdf417ScanActivity.class);
            // add license that allows removing of logo in default UI
            intent.putExtra(BaseBarcodeActivity.EXTRAS_LICENSE_KEY, LICENSE);
            // create settings object
            Pdf417MobiSettings sett = new Pdf417MobiSettings();
            // set this to true to enable PDF417 scanning
            sett.setPdf417Enabled(true);
            // set this to true to enable QR code scanning
            sett.setQrCodeEnabled(true);
            // if license permits this, remove Pdf417.mobi logo overlay on scan activity
            // if license forbids this, this option has no effect
            sett.setRemoveOverlayEnabled(true);
            // put settings as intent extra
            intent.putExtra(BaseBarcodeActivity.EXTRAS_SETTINGS, sett);
            startActivityForResult(intent, MY_REQUEST_CODE);
            break;
        }
        case R.id.btnCustomUI: {
            // create intent for custom scan activity
            Intent intent = new Intent(this, DefaultScanActivity.class);
            // add license that allows creating custom camera overlay
            intent.putExtra(BaseBarcodeActivity.EXTRAS_LICENSE_KEY, LICENSE);
            // create settings object
            Pdf417MobiSettings sett = new Pdf417MobiSettings();
            // set this to true to enable PDF417 scanning
            sett.setPdf417Enabled(true);
            // set this to true to enable QR code scanning
            sett.setQrCodeEnabled(true);
            // put settings as intent extra
            intent.putExtra(BaseBarcodeActivity.EXTRAS_SETTINGS, sett);
            startActivityForResult(intent, MY_REQUEST_CODE);
            break;
        }
        case R.id.btnCustomUIROI: {
            // create intent for custom scan activity
            Intent intent = new Intent(this, CustomScanActivity.class);
            // add license that allows creating custom camera overlay
            intent.putExtra(BaseBarcodeActivity.EXTRAS_LICENSE_KEY, LICENSE);
            // create settings object
            Pdf417MobiSettings sett = new Pdf417MobiSettings();
            // set this to true to enable PDF417 scanning
            sett.setPdf417Enabled(true);
            // set this to true to enable QR code scanning
            sett.setQrCodeEnabled(true);
            // put settings as intent extra
            intent.putExtra(BaseBarcodeActivity.EXTRAS_SETTINGS, sett);
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
            intent.putExtra(BaseBarcodeActivity.EXTRAS_ROI, roi);
            // if you intent to rotate your ROI view, you should set the EXTRAS_ROTATE_ROI extra to true
            // so that PDF417.mobi can adjust ROI coordinates for native library when device orientation
            // change event occurs
            intent.putExtra(BaseBarcodeActivity.EXTRAS_ROTATE_ROI, true);

            startActivityForResult(intent, MY_REQUEST_CODE);
            break;
        }
        }
    }

    /**
     * this method is same as in Pdf417MobiDemo project
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE && resultCode == BaseBarcodeActivity.RESULT_OK) {
            // read scan result
            Pdf417MobiScanData scanData = data.getParcelableExtra(BaseBarcodeActivity.EXTRAS_RESULT);

            // read scanned barcode type (PDF417 or QR code)
            String barcodeType = scanData.getBarcodeType();
            // read the data contained in barcode
            String barcodeData = scanData.getBarcodeData();
            // read raw barcode data
            BarcodeDetailedData rawData = scanData.getBarcodeRawData();

            if (rawData != null) {
                // the following is the explanation on how to use raw data
                /**
                 * BarcodeDetailedData is a structure that contains vector of
                 * barcode elements. Each barcode element contains byte array
                 * with raw data for this element and the type of that byte
                 * array. The type can be either ElementType.TEXT_DATA or
                 * ElementType.BYTE_DATA. TEXT_DATA means that bytes in that
                 * element represent text and can be converted to string.
                 * BYTE_DATA meand that bytes in that element don't represent
                 * anything in particular and you should decide of to use them.
                 * 
                 * Of course, both BYTE_DATA and TEXT_DATA fields can be
                 * converted to string (this is actually done in library for
                 * string result obtained in barcodeData variable).
                 */
                // get list of barcode elements
                List<BarcodeElement> elems = rawData.getElements();
                // log the amount of elements
                Log.i(TAG, "Number of barcode elements is " + elems.size());
                // now iterate over elements
                for (int i = 0; i < elems.size(); ++i) {
                    BarcodeElement elem = elems.get(i);
                    // get the barcode element type
                    ElementType elemType = elem.getElementType();
                    // get raw bytes of the element
                    byte[] rawBytes = elem.getElementBytes();

                    // do with that data whatever you want
                    // for example print it
                    Log.i(TAG, "Element #" + i + " is of type: " + elemType.name());
                    StringBuilder sb = new StringBuilder("{");
                    for (int j = 0; j < rawBytes.length; ++j) {
                        sb.append((int) rawBytes[j] & 0x0FF);
                        if (j != rawBytes.length - 1) {
                            sb.append(", ");
                        }
                    }
                    sb.append("}");
                    Log.i(TAG, sb.toString());
                }
            }

            // if barcode contains URL, create intent for browser
            // else, contain intent for message
            boolean barcodeDataIsUrl = false;

            try {
                @SuppressWarnings("unused")
                URL url = new URL(barcodeData);
                barcodeDataIsUrl = true;
            } catch (MalformedURLException exc) {
                barcodeDataIsUrl = false;
            }

            if (barcodeDataIsUrl) {
                // create intent for browser
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(barcodeData));
                startActivity(intent);
            } else {
                // ask user what to do with data
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, barcodeType + ": " + barcodeData);
                startActivity(Intent.createChooser(intent, getString(R.string.UseWith)));
            }
        }
    }
}
