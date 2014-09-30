package mobi.pdf417.demo.defaultViewfinder.geometry;

import java.util.ArrayList;

import net.photopay.geometry.Point;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Class that represents a drawable set of points.
 */
public class PointSet {
    private ArrayList<Point> mPoints = null;

    public PointSet(float[] points, int width, int height) {
        assert (points.length % 2 == 0);
        mPoints = new ArrayList<Point>(points.length / 2);
        for (int i = 0; i < points.length - 1; i += 2) {
            mPoints.add(new Point(points[i + 1] * width, points[i] * height));
        }
    }

    public void draw(Canvas canvas, Paint paint, int pointRadius) {
        if (mPoints != null) {
            int i = 0;
            for (Point p : mPoints) {
                p.draw(canvas, paint, pointRadius);
                ++i;
            }
        }
    }

    public void mirror(final int canvasWidth, final int canvasHeight) {
        ArrayList<Point> newPoints = new ArrayList<Point>(mPoints.size());
        for (Point p : mPoints) {
            newPoints.add(p.mirrorY(canvasHeight));
        }
        mPoints = newPoints;
    }
}
