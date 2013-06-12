package mobi.pdf417.viewfinder;

import mobi.pdf417.R;
import net.photopay.geometry.Point;
import net.photopay.hardware.orientation.Orientation;
import net.photopay.view.viewfinder.AbstractBarcodeViewFinder;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MyViewfinder extends AbstractBarcodeViewFinder {
    
    private RotatedRelativeLayout myLayout_;
    private Activity myActivity_;
    private Button backButton_;
    
    public MyViewfinder(Activity myActivity, RotatedRelativeLayout layout) {
        myLayout_ = layout;
        myActivity_ = myActivity;
        backButton_ = (Button)myLayout_.findViewById(R.id.btnBack);
        backButton_.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                myActivity_.finish();
            }
        });
    }

    @Override
    public View getView() {
        return myLayout_;
    }

    @Override
    public void publishDetectionStatus(int detectionStatus, boolean showProgress) {
        // this will be explained later        
    }

    @Override
    public void setDefaultTarget() {
        // this will be explained later
        
    }

    @Override
    public void setNewTarget(Point uleft, Point uright, Point lleft, Point lright, int uleftIndex) {
        // this will be explained later
        
    }

    @Override
    public void setOrientation(Orientation orientation) {
        Log.i("MVF", "Set orientation to: " + orientation);
        myLayout_.setDirection(orientation.intValue());
    }

    @Override
    public void setPointSet(float[] points, boolean biColorPointSet) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void displayAutofocusFailed() {
        // TODO Auto-generated method stub
        
    }


}
