package cn.kinglian.www.customview.pathmeasure;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 搜索动画view
 * <p>
 * Created by ywy on 2018/6/14.
 */

public class SearchView extends View {
    private int mWidth;
    private int mHeight;
    private Paint mCirclePaint;
    private Paint mSearchPaint;
    @ColorInt
    private int mCircleColor = Color.WHITE;
    @ColorInt
    private int mSearchColor = Color.WHITE;
    private int mPathWidth = 4;
    private PathMeasure mPathMeasure;
    private Path mCirclePath;
    private Path mSearchPath;
    private int mCircleRadius = 100;
    private int mSearchRadius = 50;
    private ValueAnimator.AnimatorUpdateListener updateListener;
    private Animator.AnimatorListener listener;
    private ValueAnimator startAnimator;
    private ValueAnimator searchAnimator;
    private ValueAnimator endAnimator;
    private int mDuration = 2000;
    private float mAnimatorValue = 0.0f;
    private Handler mHandler;
    private Status mCurrentStatus = Status.NONE;
    private boolean isOver;
    private int count = 1;
    private int rotateCount = 2;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initPaint();
        initPath();
        initAnimator();
        initHandler();

        //开始圆圈动画
        mCurrentStatus = Status.STARTING;
        startAnimator.start();
    }

    private void initPaint() {
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mPathWidth);
        mSearchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSearchPaint.setStyle(Paint.Style.STROKE);
        mSearchPaint.setStrokeWidth(mPathWidth);
        mSearchPaint.setColor(mSearchColor);
        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void initPath() {
        mCirclePath = new Path();
        mSearchPath = new Path();
        mPathMeasure = new PathMeasure();

        RectF ovalCircle = new RectF(-mCircleRadius, -mCircleRadius, mCircleRadius, mCircleRadius);
        mCirclePath.addArc(ovalCircle, 45f, -359.9f);
        RectF ovalSearch = new RectF(-mSearchRadius, -mSearchRadius, mSearchRadius, mSearchRadius);
        mSearchPath.addArc(ovalSearch, 45f, 359.9f);
        //获取外圆起点
        float[] pos = new float[2];
        mPathMeasure.setPath(mCirclePath, false);
        mPathMeasure.getPosTan(0, pos, null);
        mSearchPath.lineTo(pos[0], pos[1]);
    }


    private void initAnimator() {
        startAnimator = ValueAnimator.ofFloat(0, 1).setDuration(mDuration);
        searchAnimator = ValueAnimator.ofFloat(0, 1).setDuration(mDuration);
        endAnimator = ValueAnimator.ofFloat(1, 0).setDuration(mDuration);

        updateListener = animation -> {
            mAnimatorValue = (float) animation.getAnimatedValue();
            invalidate();
        };

        listener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mHandler.sendEmptyMessage(0x1);
            }
        };

        startAnimator.addUpdateListener(updateListener);
        startAnimator.addListener(listener);

        searchAnimator.addUpdateListener(updateListener);
        searchAnimator.addListener(listener);

        endAnimator.addUpdateListener(updateListener);
        endAnimator.addListener(listener);
    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (mCurrentStatus) {
                    case STARTING:
                        isOver = false;
                        mCurrentStatus = Status.SEARCHING;
                        startAnimator.removeAllListeners();
                        searchAnimator.start();
                        break;
                    case SEARCHING:
                        //搜索中（转圈动画）
                        if (!isOver) {
                            searchAnimator.start();
                            count++;
                            if (count > rotateCount) {
                                isOver = true;
                            }
                        } else {
                            mCurrentStatus = Status.ENDING;
                            endAnimator.start();
                        }
                        break;
                    case ENDING:
                        mCurrentStatus = Status.NONE;
                        count = 1;
                        //重复动画
                        isOver = false;
                        mCurrentStatus = Status.STARTING;
                        startAnimator.addListener(listener);
                        startAnimator.start();
                        break;
                    case NONE:
                        break;
                    default:
                        break;
                }
            }
        };
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(mWidth / 2, mHeight / 2);

        drawSearch(canvas);
    }

    Path dst = new Path();
    Path dst2 = new Path();
    Path dst3 = new Path();

    private void drawSearch(Canvas canvas) {
        switch (mCurrentStatus) {
            case NONE:
                canvas.drawPath(mSearchPath, mSearchPaint);
                break;
            case STARTING:
                mPathMeasure.setPath(mSearchPath, false);
                dst.reset();
                mPathMeasure.getSegment(mPathMeasure.getLength() * mAnimatorValue, mPathMeasure.getLength(), dst, true);
                canvas.drawPath(dst, mSearchPaint);
                break;
            case SEARCHING:
                mPathMeasure.setPath(mCirclePath, false);
                dst2.reset();
                float stop = mPathMeasure.getLength() * mAnimatorValue;
                //(0.5 - Math.abs(mAnimatorValue - 0.5)  0-0.5-0
                float start = (float) (stop - ((0.5 - Math.abs(mAnimatorValue - 0.5)) * (mPathMeasure.getLength())));
//                Log.e(SearchView.class.getSimpleName(), "stop：" + stop + "   start:" + start+"    (0.5 - Math.abs(mAnimatorValue - 0.5))="+(0.5 - Math.abs(mAnimatorValue - 0.5)));
                mPathMeasure.getSegment(start, stop, dst2, true);
                canvas.drawPath(dst2, mCirclePaint);
                break;
            case ENDING:
                mPathMeasure.setPath(mSearchPath, false);
                dst3.reset();
                mPathMeasure.getSegment(mPathMeasure.getLength() * mAnimatorValue, mPathMeasure.getLength(), dst3, true);
                canvas.drawPath(dst3, mSearchPaint);
                break;
            default:
                break;
        }
    }


    public enum Status {
        NONE,
        STARTING,
        SEARCHING,
        ENDING
    }

    interface SearchListener {
        void onStart();

        void onSearching();

        void onEnd();
    }

}
