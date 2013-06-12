package mobi.pdf417.viewfinder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class RotatedRelativeLayout extends RelativeLayout {

    public static final String TAG = "RotatedRelativeLayout";
    public static final int LEFT_RIGHT = 0;
    public static final int TOP_DOWN = 1;
    public static final int RIGHT_LEFT = 2;
    public static final int BOTTOM_UP = 3;

    protected int direction = LEFT_RIGHT;

    private Matrix rotMatrix_ = new Matrix();

    public RotatedRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected static boolean isVertical(int direction) {
        return (direction == BOTTOM_UP || direction == TOP_DOWN);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isVertical(direction)) {
            super.onMeasure(heightMeasureSpec, widthMeasureSpec);
            Log.i(TAG, "[vertical] Measured dimension: " + getMeasuredHeight() + "x"
                + getMeasuredWidth());
            setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            Log.i(TAG, "[horizontal] Measured dimension: " + getMeasuredWidth() + "x"
                + getMeasuredHeight());
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.i(TAG, "Changed: " + changed + ", Bounds: l: " + l + ", t: " + t + ", r: " + r
            + ", b: " + b);
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
        Log.i(TAG, "Set direction: " + this.direction);
        invalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float[] xy = new float[2];
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            xy[0] = ev.getRawX();
            xy[1] = ev.getRawY();
        } else {
            xy[0] = ev.getX();
            xy[1] = ev.getY();
        }
        rotMatrix_.mapPoints(xy);
        ev.setLocation(xy[0], xy[1]);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();

        int width = getWidth();
        int height = getHeight();

        if (direction == TOP_DOWN) {
            canvas.translate(width, 0);
            canvas.rotate(90);
        } else if (direction == BOTTOM_UP) {
            canvas.translate(0, height);
            canvas.rotate(-90);
        } else if (direction == LEFT_RIGHT) {
            canvas.translate(0, 0);
            canvas.rotate(0);
        } else if (direction == RIGHT_LEFT) {
            canvas.translate(width, height);
            canvas.rotate(180);
        }

        @SuppressWarnings("deprecation")
        Matrix m = canvas.getMatrix();
        m.invert(rotMatrix_);

        super.dispatchDraw(canvas);
        canvas.restore();
    }
}
