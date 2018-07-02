package cn.kinglian.www.customview.loading;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * loading view
 * <p>
 * Created by ywy on 2018/7/2.
 */

public class LoadingView extends View {

    private static final int STATE_IDLE = 0x1;

    private static final int STATE_LOADING = 0x2;

    private final int MAX_LINE_LENGTH = dp2px(getContext(), 120);

    private final int MIN_LINE_LENGTH = dp2px(getContext(), 40);

    private final int MAX_DURATION = 3000;

    private final int MIN_DURATION = 500;

    private Paint mPaint;

    private int mEntireLineLength = MAX_LINE_LENGTH;

    private int mLineLength = mEntireLineLength;

    private int mRadius;

    private int[] mColors = new int[]{0xB07ECBDA, 0xB0E6A92C, 0xB0D6014D, 0xB05ABA94};

    private int mWidth;

    private int mHeight;

    private int mCurrentState = STATE_LOADING;

    private int startAngle = 60;
    private float mCircleY;

    private AnimatorSet mAnimatorSet;
    private int step = 0;
    private List<Animator> mAnimatorList;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColors[0]);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mRadius = MIN_LINE_LENGTH / 2;
        mPaint.setStrokeWidth(mRadius);
        mAnimatorList = new ArrayList<>();
        initAnimation();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAnimatorSet.start();
    }

    private void initAnimation() {
        mAnimatorList.clear();
        ValueAnimator canvasRotateAnimation = ValueAnimator.ofInt(startAngle, startAngle + 360);

        canvasRotateAnimation.addUpdateListener((animation ->
                startAngle = (int) animation.getAnimatedValue()));

        mAnimatorList.add(canvasRotateAnimation);

        ValueAnimator lineToCircleAnimation = ValueAnimator.ofInt(mEntireLineLength, mRadius / 12);
        lineToCircleAnimation.addUpdateListener((animation -> {
            mLineLength = (int) animation.getAnimatedValue();
            invalidate();
        }));

        mAnimatorList.add(lineToCircleAnimation);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(mAnimatorList);
        mAnimatorSet.setDuration(MAX_DURATION);
        mAnimatorSet.setInterpolator(new LinearInterpolator());
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mCurrentState == STATE_LOADING) {
                    step++;
                    startRotateCircleAnimation();
                }
            }
        });
        mAnimatorList.add(mAnimatorSet);
    }

    private void startRotateCircleAnimation() {
        ValueAnimator rotateCircleAnimation = ValueAnimator.ofInt(startAngle, startAngle + 180);

        rotateCircleAnimation.addUpdateListener((animation -> {
            startAngle = (int) animation.getAnimatedValue();
            invalidate();
        }));
        rotateCircleAnimation.setDuration(MAX_DURATION / 2);
        rotateCircleAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mCurrentState == STATE_LOADING) {
                    step++;
                    startMoveCircleAnimation();
                }
            }
        });
        rotateCircleAnimation.start();
        mAnimatorList.add(rotateCircleAnimation);
    }

    private void startMoveCircleAnimation() {

        ValueAnimator canvasRotateAnimation = ValueAnimator.ofInt(startAngle, startAngle + 90, startAngle + 180);

        canvasRotateAnimation.addUpdateListener((animation ->
                startAngle = (int) animation.getAnimatedValue()));

        mAnimatorList.add(canvasRotateAnimation);

        ValueAnimator moveCircleAnimation = ValueAnimator.ofFloat(mEntireLineLength, mEntireLineLength / 4, mEntireLineLength);
        moveCircleAnimation.addUpdateListener((animation -> {
            mCircleY = (float) animation.getAnimatedValue();
            invalidate();
        }));

        mAnimatorList.add(moveCircleAnimation);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(canvasRotateAnimation, moveCircleAnimation);
        animatorSet.setDuration(MAX_DURATION / 2);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });
        animatorSet.start();
        mAnimatorList.add(animatorSet);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int desireSize = 1000;
        int width = desireSize;
        int height = desireSize;
        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
                width = widthSize;
                break;
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
                height = heightSize;
                break;
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (step % 4) {
            case 0:
                for (int i = 0; i < mColors.length; i++) {
                    canvas.rotate(startAngle + i * 90, mWidth / 2, mHeight / 2);
                    mPaint.setColor(mColors[i]);
                    canvas.drawLine(mWidth / 2 - mEntireLineLength / 2.2f, mHeight / 2 - mLineLength / 2, mWidth / 2 - mEntireLineLength / 2.2f, mHeight / 2 + mLineLength / 2, mPaint);
                    canvas.rotate(-(startAngle + i * 90), mWidth / 2, mHeight / 2);
                }
                break;
            case 1:
                for (int i = 0; i < mColors.length; i++) {
                    canvas.rotate(startAngle + i * 90, mWidth / 2, mHeight / 2);
                    mPaint.setColor(mColors[i]);
                    canvas.drawCircle(mWidth / 2 - mEntireLineLength / 2.2f, mHeight / 2 - mLineLength / 2, mRadius / 2, mPaint);
                    canvas.rotate(-(startAngle + i * 90), mWidth / 2, mHeight / 2);
                }
                break;

            case 2:
                for (int i = 0; i < mColors.length; i++) {
                    canvas.rotate(startAngle + i * 90, mWidth / 2, mHeight / 2);
                    mPaint.setColor(mColors[i]);
                    canvas.drawCircle(mWidth / 2 - mEntireLineLength / 2.2f, mHeight / 2 + mCircleY, mRadius / 2, mPaint);
                    canvas.rotate(-(startAngle + i * 90), mWidth / 2, mHeight / 2);
                }
                break;
            case 3:
                break;
            default:
                break;
        }

    }

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
