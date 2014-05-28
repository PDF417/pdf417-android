package mobi.pdf417;

import java.util.ArrayList;

import mobi.pdf417.viewfinder.MyViewfinder;
import net.photopay.base.BaseBarcodeActivity;
import net.photopay.view.viewfinder.AbstractViewFinder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class CustomScanActivity extends BaseBarcodeActivity {

    private int mScanCount = 0;
    private Handler mHandler = new Handler();
    private MyViewfinder mViewfinder;
    private boolean mRotateRoi = false;
    
    @Override
    protected void onConfigureWindow() {
        super.onConfigureWindow();
        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            mRotateRoi = extras.getBoolean(BaseBarcodeActivity.EXTRAS_ROTATE_ROI, false);
        }
    }

    
    /**
     * This method must create and return custom viewfinder object that contains
     * information about how views are layouted on camera surface and how they
     * are rotated.
     */
    protected AbstractViewFinder onCreateViewFinder() {
        mViewfinder = new MyViewfinder(this, mRotateRoi);
        return mViewfinder;
    }

    @Override
    protected void onSetupViewFinder(AbstractViewFinder viewfinder) {
        super.onSetupViewFinder(viewfinder);
        mViewfinder.setupViewfinder();
    }

    /**
     * this activity will perform 5 scans of barcode and then return the last
     * scanned one
     */
    @Override
    protected void onScanningDone(ArrayList<Pdf417MobiScanData> scanDataList) {
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
            intent.putExtra(BaseBarcodeActivity.EXTRAS_RESULT, scanDataList.get(0));
            intent.putParcelableArrayListExtra(BaseBarcodeActivity.EXTRAS_RESULT_LIST, scanDataList);
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
