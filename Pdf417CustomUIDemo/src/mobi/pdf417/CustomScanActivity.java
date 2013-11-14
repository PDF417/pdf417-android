package mobi.pdf417;

import mobi.pdf417.viewfinder.MyViewfinder;
import net.photopay.base.BaseBarcodeActivity;
import net.photopay.view.viewfinder.AbstractViewFinder;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class CustomScanActivity extends BaseBarcodeActivity {

	private int mScanCount = 0;
	private Handler mHandler = new Handler();
	MyViewfinder mViewfinder;
	
	/**
	 * This method must create and return custom viewfinder object that
	 * contains information about how views are layouted on camera surface
	 * and how they are rotated.
	 */
	protected AbstractViewFinder onCreateViewFinder() {
		LayoutInflater inflater = getLayoutInflater();
		View rotatableCameraOverlay = inflater.inflate(R.layout.camera_overlay_layout, null);
		mViewfinder = new MyViewfinder(this, rotatableCameraOverlay);
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
