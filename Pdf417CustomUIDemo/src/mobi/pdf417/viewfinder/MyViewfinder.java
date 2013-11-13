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
    }
    
    @Override
    public View getRotatableView() {
        return mLayout;
    }
}
