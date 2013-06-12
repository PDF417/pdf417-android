package mobi.pdf417;

import java.net.MalformedURLException;
import java.net.URL;

import mobi.pdf417.activity.ScanActivity;
import net.photopay.base.BaseBarcodeActivity;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    
    public static final String TAG = "MainActivity";
    
    private static final int MY_REQUEST_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.btnDefaultUINoDialog: {
                // create intent for scan activity
                Intent intent = new Intent(this, ScanActivity.class);
                // add license that allows removing of dialog in default UI
                intent.putExtra(BaseBarcodeActivity.EXTRAS_LICENSE_KEY, "1c61089106f282473fbe6a5238ec585f8ca0c29512b2dea3b7c17b8030c9813dc965ca8e70c8557347177515349e6e");
                // create settings object
                Pdf417MobiSettings sett = new Pdf417MobiSettings();
                // set this to true to enable PDF417 scanning
                sett.setPdf417Enabled(true);
                // set this to true to enable QR code scanning
                sett.setQrCodeEnabled(true); 
                // if license permits this, set this to true to prevent showing dialog after
                // successful scan
                // if license forbids this, this option has no effect
                sett.setDontShowDialog(true);
                // put settings as intent extra
                intent.putExtra(BaseBarcodeActivity.EXTRAS_SETTINGS, sett);
                startActivityForResult(intent, MY_REQUEST_CODE);
                break;
            }
            case R.id.btnDefaultUINoLogo: {
                // create intent for scan activity
                Intent intent = new Intent(this, ScanActivity.class);
                // add license that allows removing of dialog in default UI
                intent.putExtra(BaseBarcodeActivity.EXTRAS_LICENSE_KEY, "1c61089106f282473fbe6a5238ec585f8ca0c29512b2dea3b7c17b8030c9813dc965ca8e70c8557347177515349e6e");
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
                Intent intent = new Intent(this, CustomScanActivity.class);
                // add license that allows removing of dialog in default UI
                intent.putExtra(BaseBarcodeActivity.EXTRAS_LICENSE_KEY, "1c61089106f282473fbe6a5238ec585f8ca0c29512b2dea3b7c17b8030c9813dc965ca8e70c8557347177515349e6e");
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
        }
    }
    
    /**
     * this method is same as in Pdf417MobiDemo project
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==MY_REQUEST_CODE && resultCode==BaseBarcodeActivity.RESULT_OK) {

            // read scanned barcode type (PDF417 or QR code)
            String barcodeType = data.getStringExtra(BaseBarcodeActivity.EXTRAS_BARCODE_TYPE);
            // read the data contained in barcode
            String barcodeData = data.getStringExtra(BaseBarcodeActivity.EXTRAS_RESULT);

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
