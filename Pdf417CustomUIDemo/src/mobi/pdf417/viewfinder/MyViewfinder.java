package mobi.pdf417.viewfinder;

import mobi.pdf417.R;
import net.photopay.view.viewfinder.AbstractViewFinder;
import android.app.Activity;
import android.view.View;
import android.widget.Button;


public class MyViewfinder extends AbstractViewFinder {
    
    private View mLayout;
    private Activity mActivity;
    private Button mBackButton;
    private Button mTorchButton;
    private boolean mTorchEnabled = false;
    
    public MyViewfinder(Activity myActivity, View layout) {
        mLayout = layout;
        mActivity = myActivity;
        mBackButton = (Button)mLayout.findViewById(R.id.btnBack);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
        
        mTorchButton = (Button)mLayout.findViewById(R.id.btnTorch);
        mTorchButton.setVisibility(View.GONE);
    }
    
    public void setupViewfinder() {
        if(isCameraTorchSupported()) {
        	mTorchButton.setVisibility(View.VISIBLE);
        	mTorchButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					boolean success = setTorchEnabled(!mTorchEnabled);
					if(success) {
						mTorchEnabled = !mTorchEnabled;
					}
					if(mTorchEnabled) {
						mTorchButton.setText(R.string.LightOn);
					} else {
						mTorchButton.setText(R.string.LightOff);
					}
				}
			});
        }
    }
    
    @Override
    public View getRotatableView() {
        return mLayout;
    }
}
