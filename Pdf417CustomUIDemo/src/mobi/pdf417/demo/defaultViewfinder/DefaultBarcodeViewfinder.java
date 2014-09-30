/**
 * Copyright (c)2011 Racuni.hr d.o.o. All rights reserved.
 *
 * ANY UNAUTHORIZED USE OR SALE, DUPLICATION, OR DISTRIBUTION
 * OF THIS PROGRAM OR ANY OF ITS PARTS, IN SOURCE OR BINARY FORMS,
 * WITH OR WITHOUT MODIFICATION, WITH THE PURPOSE OF ACQUIRING
 * UNLAWFUL MATERIAL OR ANY OTHER BENEFIT IS PROHIBITED!
 * THIS PROGRAM IS PROTECTED BY COPYRIGHT LAWS AND YOU MAY NOT
 * REVERSE ENGINEER, DECOMPILE, OR DISASSEMBLE IT.
 */

package mobi.pdf417.demo.defaultViewfinder;

import net.photopay.geometry.PointSet;
import net.photopay.geometry.Quadrilateral;
import net.photopay.geometry.QuadrangleEvaluator;
import net.photopay.geometry.quadDrawers.QuadrilateralDrawer;
import mobi.pdf417.demo.R;
import net.photopay.geometry.Point;
import net.photopay.hardware.camera.CameraType;
import net.photopay.nineoldandroids.animation.ValueAnimator;
import net.photopay.recognition.DetectionStatus;
import net.photopay.view.viewfinder.AbstractViewFinder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

// NOTE: this example uses resources from Pdf417MobiSdk library project
// basically this is the implementation of PDF417.mobi's default skin

/**
 * Class that draws guide lines on top of camera preview and controls the quadrilateral animation.
 */
public class DefaultBarcodeViewfinder extends View implements ValueAnimator.AnimatorUpdateListener {

    private static final long kAnimationDuration = 200;

    // paints required for drawing lines, points and text
    private Paint mPaint;
    private Paint mTextPaint;

    // some drawing parameters
    public static final double margin = 0.11;
    private static int frameBorderWidth = 3;
    private static int pointRadius = 15;
    private static final float textSizePercent = 0.06f;

    // these variables will hold the drawing canvas size and margins
    // for default position of quadrilateral
    private int mWidth = -1;
    private int mHeight = -1;
    private int mTop = -1, mLeft = -1, mRight = -1, mBottom = -1;

    // quadrilaterals - current is the one that is currently being drawn, mTarget is the one to which
    // we perform animation
    private Quadrilateral mCurrent = null;
    private Quadrilateral mTarget = null;
    // pointset of QR code detection
    private PointSet mPointSet = null;
    private Resources mResources = null;
    // quadrilateral animation object
    private ValueAnimator mAnimation = null;

    private QuadrilateralDrawer mQuadDrawer = null;

    // for displaying messages
    private AbstractViewFinder mAbstractViewFinder = null;

    // handler for signaling events to UI thread
    private final Handler mHandler = new Handler();

    private boolean mBiColorPointSet = false;

    // PDF417.mobi logo drawing parameters
    private boolean mDrawOverlay = true;
    private float mOverlayStart = -1.f;
    private float mOverlayEnd = -1.f;

    private float mDensity;

    private float convertPix(float pix) {
        return pix * mDensity;
    }

    public DefaultBarcodeViewfinder(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every
        // time in onDraw().
        mResources = getResources();
        mDensity = mResources.getDisplayMetrics().density;

        mQuadDrawer = new QuadrilateralDrawer(context);

        // setup paints for drawing
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        frameBorderWidth = (dm.densityDpi + 49) / 50;
        pointRadius = frameBorderWidth * 2;
        mPaint.setStrokeWidth(frameBorderWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.argb(119, 255, 255, 255));

        // go to PDF417.mobi site if user taps in area of PDF417.mobi logo
        this.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mDrawOverlay && mOverlayStart > 0.f) {
                    float yCoord;
                    yCoord = event.getY();
                    if (yCoord >= mOverlayStart && yCoord <= mOverlayEnd) {
                        goToWebsite();
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        });
    }

    private void goToWebsite() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://pdf417.mobi"));
        Activity a = (Activity) getContext();
        a.startActivity(intent);
        a.finish();
    }

    protected final void setDrawOverlay(boolean drawOverlay) {
        mDrawOverlay = drawOverlay;
    }

    public void setViewfinderInterface(AbstractViewFinder viewfinder) {
        this.mAbstractViewFinder = viewfinder;
    }

    public AbstractViewFinder getViewfinderInterface() {
        return this.mAbstractViewFinder;
    }

    public boolean isAnimationInProgress() {
        if (mAnimation != null) {
            return mAnimation.isRunning();
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.photopay.view.IViewFinder#setDefaultTarget()
     */
    public synchronized void setDefaultTarget() {
        int uleftIndex = 0;
        if (mTarget != null) {
            uleftIndex = mTarget.getRealUpperLeftIndex();
        }
        mTarget = new Quadrilateral(mTop, mBottom, mLeft, mRight);
        mTarget.setIsDefaultQuad(true);
        mTarget.setRealUpperLeftIndex(uleftIndex);
        if (mTop != mBottom) {
            startAnimation();
        }
        mPointSet = null;
    }

    private void startAnimation() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mAnimation != null) {
                    mAnimation.cancel();
                }
                mAnimation = ValueAnimator.ofObject(new QuadrangleEvaluator(), mCurrent, mTarget);
                mAnimation.setDuration(kAnimationDuration);
                mAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                mAnimation.addUpdateListener(DefaultBarcodeViewfinder.this);
                mAnimation.start();
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.photopay.view.IViewFinder#setNewTarget(net.photopay.geometry.Point,
     * net.photopay.geometry.Point, net.photopay.geometry.Point,
     * net.photopay.geometry.Point, int)
     */
    public synchronized void setNewTarget(Quadrilateral quad) {
        mTarget = quad;
        mPointSet = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.photopay.view.IViewFinder#setPointSet(float[], boolean)
     */
    public synchronized void setPointSet(PointSet pointSet) {
        mPointSet = pointSet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.photopay.view.IViewFinder#publishDetectionStatus(int, boolean)
     */
    public void publishDetectionStatus(final int detectionStatus) {
        if (detectionStatus == DetectionStatus.DETECTION_STATUS_SUCCESS
                || detectionStatus == DetectionStatus.DETECTION_STATUS_QR_SUCCESS
                || detectionStatus == DetectionStatus.DETECTION_STATUS_PDF417_SUCCESS) {
            mTarget.setColor(mResources.getColor(R.color.recognized_frame));
        } else {
            mTarget.setColor(mResources.getColor(R.color.default_frame));
        }
        if (mTop != mBottom) {
            startAnimation();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mWidth = this.getWidth();
        mHeight = this.getHeight();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        boolean cornersUnknown = (mBottom <= 0);

        if (mWidth == -1) {
            mWidth = canvas.getWidth();
        }
        if (mHeight == -1) {
            mHeight = canvas.getHeight();
        }

        int effectiveWidth = (int) (mWidth * (1. - margin));
        int effectiveHeight = (int) (mHeight * (1. - margin));

        mTop = (mHeight - effectiveHeight) / 2;
        mLeft = (mWidth - effectiveWidth) / 2;
        mRight = mWidth - mLeft;
        mBottom = mHeight - mTop;

        if (cornersUnknown) {
            mCurrent = new Quadrilateral(mTop, mBottom, mLeft, mRight);
            mCurrent.setColor(mResources.getColor(R.color.default_frame));
            mCurrent.setIsDefaultQuad(true);
            mTarget = mCurrent.clone();
        }

        synchronized (this) {
            mQuadDrawer.drawQuad(mCurrent, canvas);
            if (mPointSet != null) {
                mPaint.setColor(mTarget.getColor());
                mPointSet.draw(canvas, mPaint, pointRadius);
            }
        }

        // draw guidelines
        mPaint.setAlpha(32);
        mPaint.setStrokeWidth(frameBorderWidth * 1.5f);

        canvas.drawLine(0, mHeight / 2, mWidth, mHeight / 2, mPaint);
        canvas.drawLine(mWidth / 2, 0, mWidth / 2, mHeight, mPaint);

        mPaint.setAlpha(255);
        mPaint.setStrokeWidth(frameBorderWidth / 2);
        canvas.drawLine(0, mHeight / 2, mWidth, mHeight / 2, mPaint);
        canvas.drawLine(mWidth / 2, 0, mWidth / 2, mHeight, mPaint);
        mPaint.setStrokeWidth(frameBorderWidth);

        if (mDrawOverlay) {
            // draw overlay text
            final String pdf417 = "pdf417";
            final String mobi = ".mobi";
            final String by = "by ";
            final String photo = "Photo";
            final String pay = "Pay";
            final String text3 = "free for non-commercial use";
            mTextPaint.setShadowLayer(convertPix(1.f), convertPix(0.5f), convertPix(0.5f), Color.argb(110, 0, 0, 0));
            mTextPaint.setTextSize((mBottom - mTop) * textSizePercent);
            mTextPaint.setColor(Color.argb(220, 43, 43, 43));
            float textWidth = mTextPaint.measureText(pdf417 + mobi);
            float pdf417Width = mTextPaint.measureText(pdf417);
            float textEnd = mBottom - mTextPaint.getTextSize() * 3.f;
            mOverlayStart = textEnd - mTextPaint.getTextSize();
            canvas.drawText(pdf417, (mLeft + mRight) / 2.f - textWidth / 2.f, textEnd, mTextPaint);
            mTextPaint.setColor(Color.argb(220, 144, 144, 144));
            canvas.drawText(mobi, (mLeft + mRight) / 2.f - textWidth / 2.f + pdf417Width, textEnd, mTextPaint);

            mTextPaint.setColor(Color.argb(220, 0, 0, 0));
            textEnd += mTextPaint.getTextSize() * 1.2;

            mTextPaint.setTextSize(mTextPaint.getTextSize() * 0.9f);
            textWidth = mTextPaint.measureText(by + photo + pay);
            float byWidth = mTextPaint.measureText(by);
            float photoWidth = mTextPaint.measureText(photo);
            canvas.drawText(by, (mLeft + mRight) / 2.f - textWidth / 2.f, textEnd, mTextPaint);
            mTextPaint.setColor(Color.argb(255, 205, 45, 61));
            canvas.drawText(photo, (mLeft + mRight) / 2.f - textWidth / 2.f + byWidth, textEnd, mTextPaint);
            mTextPaint.setColor(Color.argb(255, 52, 47, 49));
            canvas.drawText(pay, (mLeft + mRight) / 2.f - textWidth / 2.f + byWidth + photoWidth, textEnd, mTextPaint);

            mTextPaint.setColor(Color.argb(220, 144, 144, 144));
            textEnd += mTextPaint.getTextSize() * 1.3f;
            mTextPaint.setTextSize(mTextPaint.getTextSize() * 0.7f);
            textWidth = mTextPaint.measureText(text3);
            canvas.drawText(text3, (mLeft + mRight) / 2.f - textWidth / 2.f, textEnd, mTextPaint);
            mOverlayEnd = textEnd;
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (animation == mAnimation) {
            mCurrent = (Quadrilateral) animation.getAnimatedValue();
        }
        invalidate();
    }

    public void displayAutofocusFailed() {
        // this method is called from background thread
        // make sure code is executed on correct thread
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                setDefaultTarget();
                Log.e("displayAutofocusFailed", "Autofocus fail!!!");
                publishDetectionStatus(DetectionStatus.DETECTION_STATUS_BLURRY_FRAME);
            }
        });
    }
}
