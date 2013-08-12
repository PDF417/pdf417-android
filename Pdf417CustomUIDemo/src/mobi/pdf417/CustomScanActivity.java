package mobi.pdf417;

import mobi.pdf417.R;
import mobi.pdf417.viewfinder.MyViewfinder;
import mobi.pdf417.viewfinder.RotatedRelativeLayout;
import net.photopay.base.BaseBarcodeActivity;
import net.photopay.hardware.orientation.Orientation;
import net.photopay.view.viewfinder.AbstractBarcodeViewFinder;
import net.photopay.view.viewfinder.IViewFinder;
import android.view.LayoutInflater;

public class CustomScanActivity extends BaseBarcodeActivity {

    @Override
    protected boolean isOrientationAllowed(Orientation orientation) {
        // in this example, all orientations will be allowed
        return true;
    }

    @Override
    protected AbstractBarcodeViewFinder onCreateBarcodeViewFinder() {
        AbstractBarcodeViewFinder viewFinder = null;
        LayoutInflater inflater = getLayoutInflater();

        RotatedRelativeLayout cameraOverlay =
            (RotatedRelativeLayout) inflater.inflate(R.layout.camera_overlay_layout, null);
        viewFinder = new MyViewfinder(this, cameraOverlay);
        return viewFinder;
    }

    @Override
    protected void onSetupViewFinder(IViewFinder viewfinder) {
        // just set the viewfinder into its default position
        viewfinder.setDefaultTarget();
        viewfinder.setOrientation(Orientation.ORIENTATION_PORTRAIT);
    }

}
