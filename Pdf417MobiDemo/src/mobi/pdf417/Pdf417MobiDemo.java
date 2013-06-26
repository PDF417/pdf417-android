package mobi.pdf417;

import java.net.MalformedURLException;
import java.net.URL;

import mobi.pdf417.activity.Pdf417ScanActivity;
import net.photopay.base.BaseBarcodeActivity;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Pdf417MobiDemo extends Activity {
    
    public static final String TAG = "MainActivity";
    
    private static final int MY_REQUEST_CODE = 1337;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** request full screen window without title bar (looks better :-P )*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
    }
    
    public void btnScan_click(View v) {
        Log.i(TAG, "scan will be performed");
        // create intent for scan activity
        Intent intent = new Intent(this, Pdf417ScanActivity.class);
        // add a resource id for sound that will be played on successful scan (optional)
        intent.putExtra(Pdf417ScanActivity.EXTRAS_BEEP_RESOURCE, R.raw.beep);
        // add license key (required)
//        intent.putExtra(BaseCameraActivity.EXTRAS_LICENSE_KEY, "4b2b088801ead5183cef6d5038b45003494ce5ca36a1e19fd145557926109e0865a1e794438f6c12a0");
        /**
         * You can use Pdf417MobiSettings object to tweak additional scanning parameters.
         * This is entirely optional. If you don't send this object via intent, default
         * scanning parameters will be used - this means both QR and PDF417 codes will
         * be scanned and default camera overlay will be shown.
         */
        Pdf417MobiSettings sett = new Pdf417MobiSettings();
        // set this to true to enable PDF417 scanning
        sett.setPdf417Enabled(true);
        // set this to true to enable QR code scanning
        sett.setQrCodeEnabled(true); 
        // if license permits this, set this to true to prevent showing dialog after
        // successful scan
        // if license forbids this, this option has no effect
        sett.setDontShowDialog(true);
        // if license permits this, remove Pdf417.mobi logo overlay on scan activity
        // if license forbids this, this option has no effect
        sett.setRemoveOverlayEnabled(true);
        // put settings as intent extra
        intent.putExtra(BaseBarcodeActivity.EXTRAS_SETTINGS, sett);
        startActivityForResult(intent, MY_REQUEST_CODE);
    }
    
    public void btnInfo_click(View v) {
        int vid = v.getId();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        switch(vid) {
            case R.id.btnGitHub: {
                intent.setData(Uri.parse("https://github.com/PDF417/Android"));
                break;
            }
            case R.id.btnFacebook: {
                intent.setData(Uri.parse("https://www.facebook.com/pdf417mobi"));
                break;
            }
            case R.id.btnInfo: {
                intent.setData(Uri.parse("http://pdf417.mobi"));
                break;
            }
        }
        startActivity(intent);
    }
    
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
