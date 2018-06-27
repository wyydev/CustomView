package cn.kinglian.www.customview.pathmeasure;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 支付结果
 * <p>
 * Created by ywy on 2018/6/21.
 */

public class PayResultView extends View {
    private Paint mSuccessPaint;
    private Paint mFailedPaint;
    private Paint mWarningPaint;
    private Paint mCirclePaint;
    private PathMeasure mPathMeasure;
    private Path successPath;
    private Path failedPathLeft;
    private Path failedPathRight;
    private Path warningPath;
    private Path warningPointPath;
    private Path mCirclePath;
    private Animator.AnimatorListener mListener;
    private ValueAnimator.AnimatorUpdateListener updateListener;
    private ValueAnimator startingAnimator;
    private ValueAnimator endAnimator;
    private int successColor = Color.GREEN;
    private int failedColor = Color.RED;
    private int warningColor = Color.YELLOW;
    private int strokeWidth = 3;
    private int mWidth;
    private int mHeight;
    private int mRadius = 300;
    private RectF supportingRect;
    private int duration = 2000;
    private int endDuration = 1000;
    private float mAnimatorValue = 0;
    private Status mCurrentStatus = Status.NONE;
    private ResultType mResultType = ResultType.SUCCESS;
    private Handler mHandler;
    private boolean isFinish;
    private int mRunningColor = Color.WHITE;

    public PayResultView(Context context) {
        this(context, null);
    }

    public PayResultView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PayResultView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initPaint();
        initPath();
        initAnimator();
        initHandler();

    }

    private void initPaint() {
        mSuccessPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFailedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWarningPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSuccessPaint.setStyle(Paint.Style.STROKE);
        mFailedPaint.setStyle(Paint.Style.STROKE);
        mWarningPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mSuccessPaint.setColor(successColor);
        mFailedPaint.setColor(failedColor);
        mWarningPaint.setColor(warningColor);
        mSuccessPaint.setStrokeWidth(strokeWidth);
        mFailedPaint.setStrokeWidth(strokeWidth);
        mWarningPaint.setStrokeWidth(strokeWidth);
        mCirclePaint.setStrokeWidth(strokeWidth);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }


    Path supportPath = new Path();

    private void initPath() {
        successPath = new Path();
        failedPathLeft = new Path();
        failedPathRight = new Path();
        warningPath = new Path();
        warningPointPath = new Path();
        mCirclePath = new Path();
        mPathMeasure = new PathMeasure();
        RectF circleRect = new RectF(-mRadius, -mRadius, mRadius, mRadius);
        mCirclePath.addArc(circleRect, 45, -359.9f);

        supportingRect = new RectF(-3 * mRadius / 5, -3 * mRadius / 5, 3 * mRadius / 5, 3 * mRadius / 5);
        supportPath.addRect(supportingRect, Path.Direction.CW);
        //计算√的坐标
        successPath.moveTo(supportingRect.left, 0);
        successPath.lineTo(supportingRect.left / 4, supportingRect.bottom / 2);
        successPath.lineTo(supportingRect.right, supportingRect.top);
        //计算×的坐标
        failedPathLeft.moveTo(supportingRect.left, supportingRect.top);
        failedPathLeft.lineTo(supportingRect.right, supportingRect.bottom);
        failedPathRight.moveTo(supportingRect.right, supportingRect.top);
        failedPathRight.lineTo(supportingRect.left, supportingRect.bottom);

        //计算!的坐标
        warningPath.moveTo(0, supportingRect.top);
        warningPath.lineTo(0, 4 * supportingRect.bottom / 5);
        warningPointPath.addCircle(0, supportingRect.bottom, strokeWidth, Path.Direction.CW);
    }

    private void initAnimator() {

        updateListener = (animation) -> {
            mAnimatorValue = (float) animation.getAnimatedValue();
            invalidate();
        };

        mListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mHandler.sendEmptyMessage(0x1);
            }
        };

        startingAnimator = ValueAnimator.ofFloat(0, 1).setDuration(duration);
        endAnimator = ValueAnimator.ofFloat(0, 1).setDuration(endDuration);
        startingAnimator.addUpdateListener(updateListener);
        startingAnimator.addListener(mListener);
        endAnimator.addUpdateListener(updateListener);
        endAnimator.addListener(mListener);
    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (mCurrentStatus) {
                    case PAYING:
                        if (!isFinish) {
                            isFinish = false;
                            if (startingAnimator.getListeners() == null || startingAnimator.getListeners().size() == 0) {
                                startingAnimator.addListener(mListener);
                            }
                            startingAnimator.start();
                        } else {
                            isFinish = true;
                            mCurrentStatus = Status.FINISHED;
                            startingAnimator.removeAllListeners();
                            endAnimator.start();
                        }

                        break;
                    case FINISHED:
                        mCurrentStatus = Status.NONE;
                        break;
                    default:
                        break;
                }
            }
        };
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //开始动画
        mCurrentStatus = Status.PAYING;
        startingAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = 0;
        int height = 0;
        int desireSize = 2 * mRadius + 2 * strokeWidth;
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                width = Math.min(desireSize, widthSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                width = widthSize;
                break;
            default:
                width = desireSize;
                break;
        }

        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                height = Math.min(desireSize, heightSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                height = heightSize;
                break;
            default:
                height = desireSize;
                break;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(mWidth / 2, mHeight / 2);

        switch (mCurrentStatus) {
            case PAYING:
                //画圈圈
                drawRunningCircle(canvas);
                break;
            case FINISHED:
                //画圆、结果
                drawResult(canvas);
                break;
            case NONE:
                //最终结果
                drawFinish(canvas);
                break;
            default:
                break;
        }


//        canvas.drawPath(supportPath, mFailedPaint);
    }

    Path pt2 = new Path();

    private void drawResult(Canvas canvas) {
        switch (mResultType) {
            case SUCCESS:
                mCirclePaint.setColor(successColor);
                canvas.drawPath(mCirclePath, mCirclePaint);
                pt2.reset();
                mPathMeasure.setPath(successPath, false);
                mPathMeasure.getSegment(0, mAnimatorValue * mPathMeasure.getLength(), pt2, true);
                canvas.drawPath(pt2, mSuccessPaint);
                break;
            case FAILED:
                mCirclePaint.setColor(failedColor);
                canvas.drawPath(mCirclePath, mCirclePaint);
                pt2.reset();
                if (mAnimatorValue > 0 && mAnimatorValue < 0.5) {
                    //画左边
                    mPathMeasure.setPath(failedPathLeft, false);
                    mPathMeasure.getSegment(0, range0Until1(0f, 0.5f) * mPathMeasure.getLength(), pt2, true);
                    canvas.drawPath(pt2, mFailedPaint);
                } else if (mAnimatorValue > 0.5 && mAnimatorValue <= 1) {
                    //画右边
                    canvas.drawPath(failedPathLeft, mFailedPaint);
                    mPathMeasure.setPath(failedPathRight, false);
                    mPathMeasure.getSegment(0, range0Until1(0.5f, 01f) * mPathMeasure.getLength(), pt2, true);
                    canvas.drawPath(pt2, mFailedPaint);
                }

                break;
            case WARNING:
                mCirclePaint.setColor(warningColor);
                canvas.drawPath(mCirclePath, mCirclePaint);
                pt2.reset();
                if (mAnimatorValue > 0 && mAnimatorValue < 0.9) {
                    //画竖线
                    mPathMeasure.setPath(warningPath, false);
                    mPathMeasure.getSegment(0, range0Until1(0f, 0.9f) * mPathMeasure.getLength(), pt2, true);
                    canvas.drawPath(pt2, mWarningPaint);
                } else if (mAnimatorValue > 0.9 && mAnimatorValue <= 1) {
                    //画点
                    canvas.drawPath(warningPath, mWarningPaint);
                    mPathMeasure.setPath(warningPointPath, false);
                    mPathMeasure.getSegment(0, range0Until1(0.9f, 01f) * mPathMeasure.getLength(), pt2, true);
                    canvas.drawPath(pt2, mWarningPaint);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 将值域转化为[0,1]
     *
     * @param minValue 大于等于
     * @param maxValue 小于等于
     * @return 根据当前 animatedValue,返回 [0,1] 对应的数值
     */
    private float range0Until1(float minValue, float maxValue) {
        return (mAnimatorValue - minValue) / (maxValue - minValue);
    }


    /**
     * 绘制最终结果
     *
     * @param canvas
     */
    private void drawFinish(Canvas canvas) {
        switch (mResultType) {
            case SUCCESS:
                drawSuccess(canvas);
                break;
            case FAILED:
                drawFailed(canvas);
                break;
            case WARNING:
                drawWarning(canvas);
                break;
            default:
                break;
        }
    }

// 颜色渐变
// ValueAnimator colorAnimator = ValueAnimator.ofArgb()

    Path pt1 = new Path();

    private void drawRunningCircle(Canvas canvas) {
        mCirclePaint.setColor(mRunningColor);
        mPathMeasure.setPath(mCirclePath, false);
        pt1.reset();
        float stop = mPathMeasure.getLength() * mAnimatorValue;
        //(0.5 - Math.abs(mAnimatorValue - 0.5)  0-0.5-0
        float start = (float) (stop - ((0.5 - Math.abs(mAnimatorValue - 0.5)) * (mPathMeasure.getLength() / 2)));
        mPathMeasure.getSegment(start, stop, pt1, true);
        canvas.drawPath(pt1, mCirclePaint);
    }

    private void drawWarning(Canvas canvas) {
        mCirclePaint.setColor(warningColor);
        canvas.drawPath(mCirclePath, mCirclePaint);
        canvas.drawPath(warningPath, mWarningPaint);
        canvas.drawPath(warningPointPath, mWarningPaint);
    }

    private void drawFailed(Canvas canvas) {
        mCirclePaint.setColor(failedColor);
        canvas.drawPath(mCirclePath, mCirclePaint);
        canvas.drawPath(failedPathLeft, mFailedPaint);
        canvas.drawPath(failedPathRight, mFailedPaint);
    }

    private void drawSuccess(Canvas canvas) {
        mCirclePaint.setColor(successColor);
        canvas.drawPath(mCirclePath, mCirclePaint);
        canvas.drawPath(successPath, mSuccessPaint);
    }

    /**
     * 动画状态
     */
    enum Status {
        PAYING,
        FINISHED,
        NONE
    }

    /**
     * 支付结果
     */
    public enum ResultType {
        SUCCESS,
        FAILED,
        WARNING
    }


    public void finish(ResultType type) {
        isFinish = true;
        mResultType = type;
    }

    public void start() {
        isFinish = false;
        //开始动画
        mCurrentStatus = Status.PAYING;
        if (startingAnimator.isRunning()) {
            startingAnimator.cancel();
        }
        if (startingAnimator.getListeners() == null || startingAnimator.getListeners().size() == 0) {
            startingAnimator.addListener(mListener);
        }
        startingAnimator.start();
    }

}
