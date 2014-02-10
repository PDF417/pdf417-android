package mobi.pdf417.viewfinder;

import mobi.pdf417.R;
import net.photopay.view.viewfinder.AbstractViewFinder;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class MyViewfinder extends AbstractViewFinder {

    private View mLayout;
    private Activity mActivity;
    private Button mBackButton;
    private Button mTorchButton;
    private boolean mTorchEnabled = false;

    private View mRoiLayout;
    private boolean mRotateRoi = false;

    private FrameLayout mLayoutHolder = null;

    public MyViewfinder(Activity myActivity, boolean rotateRoi) {
        mActivity = myActivity;
        LayoutInflater inflater = mActivity.getLayoutInflater();
        mLayout = inflater.inflate(R.layout.camera_overlay_layout, null);
        mRoiLayout = inflater.inflate(R.layout.roi_overlay, null);
        mRotateRoi = rotateRoi;

        mBackButton = (Button) mLayout.findViewById(R.id.btnBack);
        mBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });

        mTorchButton = (Button) mLayout.findViewById(R.id.btnTorch);
        mTorchButton.setVisibility(View.GONE);
    }

    public void setupViewfinder() {
        if (isCameraTorchSupported()) {
            mTorchButton.setVisibility(View.VISIBLE);
            mTorchButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    boolean success = setTorchEnabled(!mTorchEnabled);
                    if (success) {
                        mTorchEnabled = !mTorchEnabled;
                    }
                    if (mTorchEnabled) {
                        mTorchButton.setText(R.string.LightOn);
                    } else {
                        mTorchButton.setText(R.string.LightOff);
                    }
                }
            });
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getRotatableView() {
        // if we want to trotate ROI, then we must pack roi layout and button layout into a
        // single view and return it. In this example we will pack them in FrameLayout.
        if (mRotateRoi) {
            if (mLayoutHolder == null) {
                mLayoutHolder = new FrameLayout(mActivity);
                mLayoutHolder.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.FILL_PARENT));
                mLayoutHolder.addView(mRoiLayout);
                mLayoutHolder.addView(mLayout);
            }
            return mLayoutHolder;
        } else {
            return mLayout;
        }
    }

    @Override
    public View getFixedView() {
        // if we do not want to rotate ROI, then return roi layout as fixed view
        // fixed view will not be rotated when device orientation change occurs
        if (!mRotateRoi) {
            return mRoiLayout;
        } else {
            return null;
        }
    }
}
