package cn.kinglian.www.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    private Paint mTipsPaint;
    private String mTips;
    private int mTipsColor = defaultColor;
    private Paint mValuePaint;
    private float maxValue = 100f;
    private float mValue = 0;

    //半径
    private float mRadius = 300;
    //圆弧间的间距
    private float mSpace = 30;
    //开始角度
    private int mStartAngle = 145;
    //划过角度
    private int mSweepAngle = 250;
    //指针每次旋转5度
    private int mScaleAngle = 5;
    private int mProgressColor = Color.YELLOW;
    private float mProgress = 0.5f;
    private Paint mArcPaint;
    private Paint mProgressArcPaint;
    private float mOutStrokeWidth = 8;
    private float mInStrokeWidth = 4;
    private float mCenterX;
    private float mCenterY;

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
        float progressAngle = (float) (2 * Math.PI / 360) * (mStartAngle + Math.round(mProgress * mSweepAngle / mScaleAngle) * mScaleAngle);
        float radius = centerRectF.width() / 2;
        int size = mSweepAngle / mScaleAngle;
        for (int i = 0; i < size; i++) {
            float angle = (float) (2 * Math.PI / 360) * (mStartAngle + i * mScaleAngle);
            Log.e("CounterView","angle:"+angle);
            float angle2 = (float)(mStartAngle + i * mScaleAngle);
            Log.e("CounterView","angle2:"+angle2);

            float x = mCenterX + (float) (radius * Math.cos(angle));
            float y = mCenterY + (float) (radius * Math.sin(angle));
            canvas.drawPoint(x, y, mArcPaint);
        }

        //绘制指示器层进度点
        mArcPaint.setColor(getProgressColor(mProgress));
        float x = mCenterX + (float) (radius * Math.cos(progressAngle));
        float y = mCenterY + (float) (radius * Math.sin(progressAngle));
        canvas.drawPoint(x, y, mArcPaint);


        //绘制指示器


        mArcPaint.setColor(defaultColor);
        mArcPaint.setPathEffect(null);
        //画内层弧
        canvas.drawArc(inRectF, mStartAngle, mSweepAngle, false, mArcPaint);

    }


    private int getProgressColor(float progress) {
        return mProgressColor;
    }

    public static String formatText(float value, String format) {
        return String.format(format, value);
//        return String.format("%.2f", value);
    }
}
