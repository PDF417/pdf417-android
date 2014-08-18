package mobi.pdf417.demo.defaultViewfinder.geometry;

import net.photopay.geometry.Point;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Class that represents a drawable quadrilateral that will be animated and drawn towards detected barcode.
 */
public class Quadrilateral {

    public static final String TAG = "Quadrilateral";

    private static final float lineLengthPerc = 0.3f;

    public static int defaultQuadColor = -1;
    public static int canvasWidth = 0;
    public static int canvasHeight = 0;

    Point mUpperLeft;
    Point mUpperRight;
    Point mLowerLeft;
    Point mLowerRight;
    int mColor = defaultQuadColor;
    int mRealUpperLeftIndex = 1;
    boolean mDefaultQuad = false;

    public Quadrilateral(int top, int bottom, int left, int right) {
        mUpperLeft = new Point(left, top);
        mUpperRight = new Point(right, top);
        mLowerLeft = new Point(left, bottom);
        mLowerRight = new Point(right, bottom);
    }

    public Quadrilateral(Point uleft, Point uright, Point lleft, Point lright) {
        mUpperLeft = uleft;
        mUpperRight = uright;
        mLowerLeft = lleft;
        mLowerRight = lright;
    }

    public static Quadrilateral fromPointsAndCanvasSize(final Point uleft, final Point uright, final Point lleft,
            final Point lright, final int canvasWidth, final int canvasHeight) {
        return new Quadrilateral(new Point(uleft.getY() * canvasWidth, uleft.getX() * canvasHeight), new Point(
                uright.getY() * canvasWidth, uright.getX() * canvasHeight), new Point(lleft.getY() * canvasWidth,
                lleft.getX() * canvasHeight), new Point(lright.getY() * canvasWidth, lright.getX() * canvasHeight));
    }

    public Point getUpperLeft() {
        return mUpperLeft;
    }

    public Point getUpperRight() {
        return mUpperRight;
    }

    public Point getLowerLeft() {
        return mLowerLeft;
    }

    public Point getLowerRight() {
        return mLowerRight;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public void setRealUpperLeftIndex(int realULeftIndex) {
        mRealUpperLeftIndex = realULeftIndex;
    }

    public int getRealUpperLeftIndex() {
        return mRealUpperLeftIndex;
    }

    public boolean isDefaultQuad() {
        return mDefaultQuad;
    }

    public void setIsDefaultQuad(boolean defaultQuad) {
        mDefaultQuad = defaultQuad;
    }

    public Quadrilateral clone() {
        Quadrilateral q;
        q = new Quadrilateral(mUpperLeft, mUpperRight, mLowerLeft, mLowerRight);
        q.setIsDefaultQuad(mDefaultQuad);
        q.setColor(mColor);
        q.setRealUpperLeftIndex(mRealUpperLeftIndex);
        return q;
    }

    /**
     * Mirrors the quadrangle so that it can be properly drawn on mirrored
     * camera preview
     * 
     * @param canvasWidth
     *            width of canvas
     * @param canvasHeight
     *            height of canvas
     */
    public void mirror(final int canvasWidth, final int canvasHeight) {
        Point nLL = null, nLR = null, nUL = null, nUR = null;
        // mirror y coordinate
        nLL = mUpperLeft.mirrorY(canvasHeight);
        nLR = mUpperRight.mirrorY(canvasHeight);
        nUL = mLowerLeft.mirrorY(canvasHeight);
        nUR = mLowerRight.mirrorY(canvasHeight);
        mUpperLeft = nUL;
        mUpperRight = nUR;
        mLowerLeft = nLL;
        mLowerRight = nLR;
    }

    public void draw(Canvas canvas, Paint paint) {

        if (canvasHeight <= 0 || canvasWidth <= 0) {
            canvasHeight = canvas.getHeight();
            canvasWidth = canvas.getWidth();
        }

        float normLength = Math.max(canvasWidth / 8, canvasHeight / 8);

        // directions
        Point uleftToRightDirection = mUpperRight.operatorMinus(mUpperLeft).operatorMultiply(lineLengthPerc);
        Point uleftToDownDirection = mLowerLeft.operatorMinus(mUpperLeft).operatorMultiply(lineLengthPerc);
        Point lleftToRightDirection = mLowerRight.operatorMinus(mLowerLeft).operatorMultiply(lineLengthPerc);
        Point urightToDownDirection = mLowerRight.operatorMinus(mUpperRight).operatorMultiply(lineLengthPerc);

        normLength = Math.min(normLength, uleftToRightDirection.norm());
        normLength = Math.min(normLength, uleftToDownDirection.norm());
        normLength = Math.min(normLength, lleftToRightDirection.norm());
        normLength = Math.min(normLength, urightToDownDirection.norm());

        uleftToRightDirection = uleftToRightDirection.clamp(normLength);
        uleftToDownDirection = uleftToDownDirection.clamp(normLength);
        urightToDownDirection = urightToDownDirection.clamp(normLength);
        lleftToRightDirection = lleftToRightDirection.clamp(normLength);

        paint.setColor(mColor);

        // upper left
        Point uleftToRight = mUpperLeft.operatorPlus(uleftToRightDirection);
        Point uleftToDown = mUpperLeft.operatorPlus(uleftToDownDirection);
        canvas.drawLine(mUpperLeft.getX(), mUpperLeft.getY(), uleftToRight.getX(), uleftToRight.getY(), paint);
        canvas.drawLine(mUpperLeft.getX(), mUpperLeft.getY(), uleftToDown.getX(), uleftToDown.getY(), paint);

        // upper right
        Point urightToLeft = mUpperRight.operatorMinus(uleftToRightDirection);
        Point urightToDown = mUpperRight.operatorPlus(urightToDownDirection);
        canvas.drawLine(mUpperRight.getX(), mUpperRight.getY(), urightToLeft.getX(), urightToLeft.getY(), paint);
        canvas.drawLine(mUpperRight.getX(), mUpperRight.getY(), urightToDown.getX(), urightToDown.getY(), paint);

        // lower left
        Point lleftToUp = mLowerLeft.operatorMinus(uleftToDownDirection);
        Point lleftToRight = mLowerLeft.operatorPlus(lleftToRightDirection);
        canvas.drawLine(mLowerLeft.getX(), mLowerLeft.getY(), lleftToUp.getX(), lleftToUp.getY(), paint);
        canvas.drawLine(mLowerLeft.getX(), mLowerLeft.getY(), lleftToRight.getX(), lleftToRight.getY(), paint);

        // lower right
        Point lrightToUp = mLowerRight.operatorMinus(urightToDownDirection);
        Point lrightToLeft = mLowerRight.operatorMinus(lleftToRightDirection);
        canvas.drawLine(mLowerRight.getX(), mLowerRight.getY(), lrightToUp.getX(), lrightToUp.getY(), paint);
        canvas.drawLine(mLowerRight.getX(), mLowerRight.getY(), lrightToLeft.getX(), lrightToLeft.getY(), paint);

    }
}
