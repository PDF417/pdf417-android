package mobi.pdf417;

import mobi.pdf417.defaultViewfinder.DefaultBarcodeSkin;
import net.photopay.base.BaseBarcodeActivity;
import net.photopay.hardware.orientation.Orientation;
import net.photopay.view.viewfinder.AbstractViewFinder;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

public class DefaultScanActivity extends BaseBarcodeActivity {

    private int mScanCount = 0;
    private Handler mHandler = new Handler();
    private DefaultBarcodeSkin mSkin;
    
    /**
     * Callback which is called when user clicks the back button.
     * 
     * @param view
     *            Reference to view that was clicked.
     */
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, null);
        finish();
    }

    @Override
    protected boolean isOrientationAllowed(Orientation orientation) {
        return true;
    }

    @Override
    protected AbstractViewFinder onCreateViewFinder() {
        LayoutInflater inflater = getLayoutInflater();

        ViewGroup defaultBarcodeLayout =
            (ViewGroup) inflater.inflate(R.layout.default_barcode_camera_overlay, null);
        mSkin = new DefaultBarcodeSkin(this, defaultBarcodeLayout, true);
        return mSkin;
    }
    
    @Override
    protected void onSetupViewFinder(AbstractViewFinder viewfinder) {
        super.onSetupViewFinder(viewfinder);
        mSkin.setupSkin();
    }
    
    /**
     * this activity will perform 5 scans of barcode and then return the last
     * scanned one
     */
    @Override
    protected void onScanningDone(Pdf417MobiScanData scanData) {
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
            intent.putExtra(BaseBarcodeActivity.EXTRAS_RESULT, scanData);
            setResult(BaseBarcodeActivity.RESULT_OK, intent);
            finish();
        } else {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    resumeScanning();
                }
            }, 2000);
        }
    }

}
