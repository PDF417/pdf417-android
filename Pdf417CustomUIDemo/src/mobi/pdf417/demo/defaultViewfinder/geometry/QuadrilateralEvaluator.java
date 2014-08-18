package mobi.pdf417.demo.defaultViewfinder.geometry;

import net.photopay.geometry.Point;
import net.photopay.nineoldandroids.animation.ArgbEvaluator;
import net.photopay.nineoldandroids.animation.TypeEvaluator;

/**
 * Class used for interpolating Quadrilaterals inside animation.
 */
public class QuadrilateralEvaluator implements TypeEvaluator<Quadrilateral> {

    private ArgbEvaluator mColorEval = new ArgbEvaluator();

    @Override
    public Quadrilateral evaluate(float fraction, Quadrilateral startValue,
        Quadrilateral endValue) {

        int color =
            (Integer) mColorEval.evaluate(fraction, startValue.getColor(), endValue.getColor());
        Quadrilateral retQuad;

            Point ulVec =
                endValue.getUpperLeft().operatorMinus(startValue.getUpperLeft()).operatorMultiply(
                    fraction);
            Point urVec =
                endValue.getUpperRight().operatorMinus(startValue.getUpperRight()).operatorMultiply(
                    fraction);
            Point llVec =
                endValue.getLowerLeft().operatorMinus(startValue.getLowerLeft()).operatorMultiply(
                    fraction);
            Point lrVec =
                endValue.getLowerRight().operatorMinus(startValue.getLowerRight()).operatorMultiply(
                    fraction);
            retQuad =
                new Quadrilateral(startValue.getUpperLeft().operatorPlus(ulVec),
                    startValue.getUpperRight().operatorPlus(urVec),
                    startValue.getLowerLeft().operatorPlus(llVec),
                    startValue.getLowerRight().operatorPlus(lrVec));

        retQuad.setColor(color);
        retQuad.setRealUpperLeftIndex(endValue.getRealUpperLeftIndex());
        if (endValue.isDefaultQuad() && (fraction > 0.95 || startValue.isDefaultQuad())) {
            retQuad.setIsDefaultQuad(true);
        }
        return retQuad;
    }
}

