package cn.kinglian.www.customview.bezier;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

/**
 * 贝塞尔曲线画爱心
 * <p>
 * Created by wen on 2018/6/5.
 */

public class Bezier4 extends View {
    // 一个常量，用来计算绘制圆形贝塞尔曲线控制点的位置
    private static final float C = 0.551915024494f;

    private Paint mPaint;

    private int centerX, centerY;

    private float mCircleRadius = 50;

    private float mDifference = mCircleRadius * C;

    //顺时针记录绘制圆形的数据点
    private float[] mData = new float[8];

    //顺时针记录绘制圆形的8个控制点
    private float[] mCtrl = new float[16];

    //变化总时长
    private float mDuration = 2000;

    //当前时长
    private float mCurrent = 0;

    //将时长划分为多少份
    private float mCount = 100;

    //每一份的时长
    private float mPiece = mDuration / mCount;

    private Interpolator interpolator2;
    private ValueAnimator valueAnimator2;
    private ValueAnimator valueAnimator6;
    private Interpolator interpolator6;
    private AnimatorSet animatorSet;
    private ValueAnimator valueAnimator0;

    public Bezier4(Context context) {
        this(context, null);
    }

    public Bezier4(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Bezier4(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(Color.BLACK);
            mPaint.setStrokeWidth(8);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setTextSize(60);
        }

        // 初始化数据点
        mData[0] = mCircleRadius;
        mData[1] = mCircleRadius;

        mData[2] = mCircleRadius * 2;
        mData[3] = centerY;

        mData[4] = mData[0];
        mData[5] = -mData[1];

        mData[6] = 0;
        mData[7] = mData[3];


    }

    private void initCtrlPoint() {
        mCtrl[0] = mData[0] + mDifference;
        mCtrl[1] = mData[1];

        mCtrl[2] = mData[2];
        mCtrl[3] = mData[3] + mDifference;

        mCtrl[4] = mData[2];
        mCtrl[5] = mData[3] - mDifference;

        mCtrl[6] = mData[4] + mDifference;
        mCtrl[7] = mData[5];

        mCtrl[8] = mData[4] - mDifference;
        mCtrl[9] = mData[5];

        mCtrl[10] = mData[6];
        mCtrl[11] = mData[7] - mDifference;

        mCtrl[12] = mData[6];
        mCtrl[13] = mData[7] + mDifference;

        mCtrl[14] = mData[0] - mDifference;
        mCtrl[15] = mData[1];
    }

    public void move() {
        animatorSet.start();
    }

    public void reset(){
        mData[2] = mCircleRadius * 2;
        mData[6] = 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;



        valueAnimator0 = ValueAnimator.ofFloat(mCircleRadius, w-mCircleRadius);
        valueAnimator0.setDuration((long) mDuration+300);
        valueAnimator0.setStartDelay(300);

        valueAnimator2 = ValueAnimator.ofFloat(mCircleRadius * 2, centerX * 2);
        valueAnimator2.setDuration((long) mDuration);



        valueAnimator6 = ValueAnimator.ofFloat(0, centerX * 2 - mCircleRadius * 2);
        valueAnimator6.setDuration((long) mDuration);
        valueAnimator6.setStartDelay(350);


        //初始化插值器
        interpolator2 = new LinearInterpolator();
        interpolator6 = new LinearInterpolator();

        valueAnimator0.setInterpolator(interpolator2);
        valueAnimator2.setInterpolator(interpolator2);
        valueAnimator6.setInterpolator(interpolator6);


        valueAnimator0.addUpdateListener((animation)->{
            mData[0] = (float) animation.getAnimatedValue();
            mData[4] = mData[0];
            postInvalidate();
        });


        valueAnimator2.addUpdateListener((animation)->{
            mData[2] = (float) animation.getAnimatedValue();
            postInvalidate();
        });



        valueAnimator6.addUpdateListener((animation)->{
            mData[6] = (float) animation.getAnimatedValue();
            postInvalidate();
        });

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimator0,valueAnimator2, valueAnimator6);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        drawCoordinateSystem(canvas);

        canvas.translate(0, centerY);
        //反转坐标系
        canvas.scale(1, -1);

//        drawAuxiliaryLine(canvas);

        // 初始化控制点
        initCtrlPoint();


        // 绘制贝塞尔曲线
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(8);

        Path path = new Path();
        path.moveTo(mData[0], mData[1]);

        path.cubicTo(mCtrl[0], mCtrl[1], mCtrl[2], mCtrl[3], mData[2], mData[3]);
        path.cubicTo(mCtrl[4], mCtrl[5], mCtrl[6], mCtrl[7], mData[4], mData[5]);
        path.cubicTo(mCtrl[8], mCtrl[9], mCtrl[10], mCtrl[11], mData[6], mData[7]);
        path.cubicTo(mCtrl[12], mCtrl[13], mCtrl[14], mCtrl[15], mData[0], mData[1]);

        canvas.drawPath(path, mPaint);

//        mCurrent += mPiece;
//        if (mCurrent < mDuration) {
//
//            mData[1] -= 120 / mCount;
//            mCtrl[7] += 80 / mCount;
//            mCtrl[9] += 80 / mCount;
//
//            mCtrl[4] -= 20 / mCount;
//            mCtrl[10] += 20 / mCount;
//
//            mData[5] -= 60 / mCount;
//
//            postInvalidateDelayed((long) mPiece);
//        } else {
//            init();
//            mCurrent = 0;
//            postInvalidateDelayed((long) 500);
//        }
    }


    /**
     * 绘制坐标系
     */
    private void drawCoordinateSystem(Canvas canvas) {
        canvas.drawLine(0, centerY, centerX * 2, centerY, mPaint);
        canvas.drawLine(centerX, 0, centerX, centerY * 2, mPaint);
    }


    // 绘制辅助线
    private void drawAuxiliaryLine(Canvas canvas) {
        // 绘制数据点和控制点
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(20);

        for (int i = 0; i < 8; i += 2) {
            canvas.drawPoint(mData[i], mData[i + 1], mPaint);
        }

        for (int i = 0; i < 16; i += 2) {
            canvas.drawPoint(mCtrl[i], mCtrl[i + 1], mPaint);
        }


        // 绘制辅助线
        mPaint.setStrokeWidth(4);

        for (int i = 2, j = 2; i < 8; i += 2, j += 4) {
            canvas.drawLine(mData[i], mData[i + 1], mCtrl[j], mCtrl[j + 1], mPaint);
            canvas.drawLine(mData[i], mData[i + 1], mCtrl[j + 2], mCtrl[j + 3], mPaint);
        }
        canvas.drawLine(mData[0], mData[1], mCtrl[0], mCtrl[1], mPaint);
        canvas.drawLine(mData[0], mData[1], mCtrl[14], mCtrl[15], mPaint);
    }
}
