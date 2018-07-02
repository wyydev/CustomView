package cn.kinglian.www.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 旋转计数View
 * <p>
 * Created by ywy on 2018/6/26.
 */

public class CounterView extends View {
    private int defaultColor = Color.GRAY;
    private Paint mTextPaint;
    private String mTips = "可提现金额";
    private int mTipsColor = defaultColor;
    private float maxValue = 1000f;
    private float mValue = 855f;
    private float mTextOffsetY = 30;
    private float mTipsSize = 20;
    private float mValueSize = 35;

    private static String sString;

    private ValueAnimator.AnimatorUpdateListener updateListener;
    private ValueAnimator animator;
    private int duration = 1500;
    //半径
    private float mRadius = 300;
    //圆弧间的间距
    private float mSpace = 30;
    /**
     * 箭头与进度圆弧的距离
     */
    private float mArrowSpace = 20;
    //开始角度
    private int mStartAngle = 145;
    //划过角度
    private int mSweepAngle = 250;
    //指针每次旋转5度
    private int mScaleAngle = 2;
    private int mProgressColor = Color.RED;
    private float mProgress = 0.5f;
    private Paint mArcPaint;
    private Paint mProgressArcPaint;
    private float mOutStrokeWidth = 8;
    private float mInStrokeWidth = 4;
    private float mCenterX;
    private float mCenterY;
    private boolean isShowTips = true;
    private boolean isShowValue = true;

    public CounterView(Context context) {
        this(context, null);
    }

    public CounterView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CounterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);

        mProgressArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressArcPaint.setStyle(Paint.Style.STROKE);
        mProgressArcPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        updateListener = animation -> {
            mValue = maxValue * (float) animation.getAnimatedValue();
            mProgress = (float) animation.getAnimatedValue();
            invalidate();
        };
        animator = ValueAnimator.ofFloat(0, mValue / maxValue);
        animator.addUpdateListener(updateListener);
        animator.setDuration(duration);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if ( animator != null){
            animator.start();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desireSize = (int) (2 * mRadius + 2 * mOutStrokeWidth + getPaddingLeft() + getPaddingRight());
        int width = desireSize;
        int height = desireSize;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                width = Math.min(desireSize, widthSize);
                break;
            default:
                break;
        }

        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                height = Math.min(desireSize, heightSize);
                break;
            default:
                break;
        }
        setMeasuredDimension(width, height);
    }

    float startX, startY;
    RectF outRectF;
    RectF centerRectF;
    RectF inRectF;


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
        startX = mCenterX - mRadius;
        startY = mCenterY - mRadius;
        outRectF = new RectF(startX, startY, startX + 2 * mRadius, startY + 2 * mRadius);
        centerRectF = new RectF(outRectF.left + mSpace, outRectF.top + mSpace,
                outRectF.right - mSpace, outRectF.bottom - mSpace);
        inRectF = new RectF(centerRectF.left + mSpace, centerRectF.top + mSpace,
                centerRectF.right - mSpace, centerRectF.bottom - mSpace);

        Log.e("CounterView", "width：" + w + "   height:" + h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawArc(canvas);

        drawText(canvas);
    }

    private void drawArc(Canvas canvas) {
        mArcPaint.setStrokeWidth(mOutStrokeWidth);
        //绘制外层弧
        canvas.drawArc(outRectF, mStartAngle, mSweepAngle, false, mArcPaint);

        //绘制外层进度弧
        mProgressArcPaint.setColor(getProgressColor(mProgress));
        mProgressArcPaint.setStrokeWidth(mOutStrokeWidth);
        canvas.drawArc(outRectF, mStartAngle, mSweepAngle * mProgress, false, mProgressArcPaint);

        //绘制指示器层点
        mArcPaint.setColor(defaultColor);
        mArcPaint.setStrokeWidth(mInStrokeWidth);
        //将角度转为弧度 1度 = Math.PI / 180 弧度
        float progressAngle = (float) (Math.PI / 180) * (mStartAngle
                + Math.round(mProgress * mSweepAngle / mScaleAngle) * mScaleAngle);
        float radius = centerRectF.width() / 2;
        int size = mSweepAngle / mScaleAngle;
        for (int i = 0; i <= size; i++) {
            float angle = (float) (Math.PI / 180) * (mStartAngle + i * mScaleAngle);
            Log.e("CounterView", "angle:" + angle);
            float x = mCenterX + (float) (radius * Math.cos(angle));
            float y = mCenterY + (float) (radius * Math.sin(angle));

            if(progressAngle>=angle){
                mArcPaint.setColor(getProgressColor(mProgress));
            }else{
                mArcPaint.setColor(defaultColor);
            }
            canvas.drawPoint(x, y, mArcPaint);
        }

        //绘制指示器层进度点
        mArcPaint.setColor(getProgressColor(mProgress));
        float x = mCenterX + (float) (radius * Math.cos(progressAngle));
        float y = mCenterY + (float) (radius * Math.sin(progressAngle));
        canvas.drawPoint(x, y, mArcPaint);

        //绘制指示器
        mArcPaint.setStrokeWidth(mInStrokeWidth / 3);
        mArcPaint.setColor(mProgressColor);
        //指示器的圆的半径
        float indicatorRadius = mOutStrokeWidth;
        //画进度指示器的圆
        canvas.drawCircle(x, y, indicatorRadius, mArcPaint);

        //箭头点的坐标
        float x1 = mCenterX + (float) ((radius + mArrowSpace) * Math.cos(progressAngle));
        float y1 = mCenterY + (float) ((radius + mArrowSpace) * Math.sin(progressAngle));

        //算出指示器到指示器圆心的直线与指示器切线的夹角
        float a = (float) Math.sin(radius / (radius + mArrowSpace));
        //指示器与指示器圆的切线坐标角度
        float angle2 = progressAngle + a;
        //指示器与指示器圆的切线坐标1
        float x2 = (float) (x + Math.cos(angle2) * indicatorRadius);
        float y2 = (float) (y + Math.sin(angle2) * indicatorRadius);

        //指示器与指示器圆的切线坐标角度
        float angle3 = progressAngle - a;
        //指示器与指示器圆的切线坐标2
        float x3 = (float) (x + Math.cos(angle3) * indicatorRadius);
        float y3 = (float) (y + Math.sin(angle3) * indicatorRadius);

        Path path = new Path();
        path.moveTo(x2, y2);
        path.lineTo(x1, y1);
        path.lineTo(x3, y3);
        //画指示器的箭头
        canvas.drawPath(path, mArcPaint);
        mArcPaint.setColor(defaultColor);

        //画内层弧
        canvas.drawArc(inRectF, mStartAngle, mSweepAngle, false, mArcPaint);

    }


    private void drawText(Canvas canvas) {
        //绘制tips
        if (isShowTips && mTips != null) {
            mTextPaint.setColor(mTipsColor);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setTextSize(mTipsSize);
            float x = mCenterX;
            float y = mCenterY - mTextOffsetY;
            canvas.drawText(mTips, x, y, mTextPaint);
        }

        if (isShowValue) {
            //绘制Value
            mTextPaint.setColor(getProgressColor(mProgress));
            mTextPaint.setTextSize(mValueSize);
            float x1 = mCenterX;
            float y1 = mCenterY + mTextOffsetY;
            canvas.drawText(formatText(mValue, "￥%.2f"), x1, y1, mTextPaint);
        }
    }


    private int getProgressColor(float progress) {
        return mProgressColor;
    }

    public static String formatText(float value, String format) {
        return String.format(format, value);
//        return String.format("%.2f", value);
    }
}
