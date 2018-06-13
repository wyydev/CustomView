package cn.kinglian.www.customview.bezier;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * 贝塞尔曲线画弹性的圆
 * <p>
 * Created by ywy on 2018/6/7.
 */

public class Bezier5 extends ViewGroup {

    private static final float C = 0.551915024494f;

    private float mc;

    private Path mPath;

    private Paint mPaint;

    private Paint mCirclePaint;

    private int mWidth;

    private int mHeight;

    private int tabNum = 0;

    /**
     * tab之间的间距
     */
    private float space;

    private float mStartX;

    private int mStartY;

    private float mInterpolatedTime = 0;

    private float mRadius;

    /**
     * 当前选中position
     */
    private int mCurrentPosition = 0;

    /**
     * 下一个进入的位置
     */
    private int toPosition = 1;

    /**
     * 移动的距离
     */
    private float distance;

    /**
     * 顺时针记录 ，y相同的点
     */
    private YPoint p1, p3;
    /**
     * 顺时针记录 ，x相同的点
     */
    private XPoint p2, p4;

    public Bezier5(Context context) {
        this(context, null);
    }

    public Bezier5(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Bezier5(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        space = (mWidth - tabNum * 2 * mRadius) / (tabNum + 1);
        //初始圆心
        mStartX = space + mRadius;
        mStartY = mHeight / 2;

        if (mCurrentPosition == 0) {
            mc = C * mRadius;
            p1 = new YPoint(0, -mRadius, mc);
            p3 = new YPoint(0, mRadius, mc);
            p2 = new XPoint(mRadius, 0, mc);
            p4 = new XPoint(-mRadius, 0, mc);
        }

        mc = C * mRadius;
        p1 = new YPoint(mCurrentPosition * (space + 2 * mRadius), -mRadius, mc);
        p3 = new YPoint(mCurrentPosition * (space + 2 * mRadius), mRadius, mc);
        p2 = new XPoint(mRadius + mCurrentPosition * (space + 2 * mRadius), 0, mc);
        p4 = new XPoint(-mRadius + mCurrentPosition * (space + 2 * mRadius), 0, mc);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        tabNum = getChildCount();
        int maxChildWidth = 0;
        int maxChildHeight = 0;
        for (int i = 0; i < tabNum; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            if (child.getMeasuredWidth() > maxChildWidth) {
                maxChildWidth = child.getMeasuredWidth();
            }
            if (child.getMeasuredHeight() > maxChildHeight) {
                maxChildHeight = child.getMeasuredHeight();
            }
        }
//            mRadius = Math.max(maxChildWidth,maxChildHeight)/2;
        mRadius = (float) Math.abs(Math.sqrt(Math.pow(maxChildWidth, 2) + Math.pow(maxChildHeight, 2)) / 2);

        int height = maxChildHeight;

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                height = Math.min(maxChildHeight, heightSize);
                break;
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                height = heightSize;
                break;
            default:
                break;
        }

        setMeasuredDimension(widthSize, height);
    }

    ValueAnimator valueAnimator;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(1500);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.addUpdateListener((animation) -> {
                mInterpolatedTime = (float) animation.getAnimatedValue();
                postInvalidate();

            });

            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    distance = (toPosition - mCurrentPosition) * (2 * mRadius + space) + (toPosition > mCurrentPosition ? -mRadius : mRadius);
                    super.onAnimationStart(animation);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    resetP();
                    postInvalidate();
                    super.onAnimationEnd(animation);
                }
            });
        }
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator.start();
        return super.onTouchEvent(event);
    }

    private float scale = 0.8f;
    private double g2 = 1.41421;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        tabNum = getChildCount();
        for (int i = 0; i < tabNum; i++) {
            View child = getChildAt(i);
//            child.layout((int) (space + i * (2 * mRadius + space)), (int) (mStartY - mRadius), (int) (space + i * (2 * mRadius + space) + mRadius * 2), (int) (mStartY + mRadius));
            child.layout((int) (space + (1 - scale * 1 / g2) * mRadius + i * (space + 2 * mRadius)),
                    (int) (mStartY - scale * mRadius / g2),
                    (int) (space + (1 + scale * 1 / g2) * mRadius + i * (space + 2 * mRadius)),
                    (int) (mStartY + scale * mRadius / g2));
        }
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        mPath.reset();
        tabNum = getChildCount();
        for (int i = 0; i < tabNum; i++) {
            canvas.drawCircle(space + mRadius + i * (space + 2 * mRadius), mStartY, mRadius, mCirclePaint);
        }
        canvas.translate(mStartX, mStartY);

        if (mInterpolatedTime > 0 && mInterpolatedTime <= 0.2) {
            //0-0.2时间内，向右p2的x由mRadius变为2*mRadius,向左，p4的x由-mRadius变为-2mRadius
            if (toPosition > mCurrentPosition) {
                p2.setX(mRadius + 5 * mInterpolatedTime * mRadius);
            } else {
                p4.setX(-mRadius - 5 * mInterpolatedTime * mRadius);
            }
        } else if (mInterpolatedTime > 0.2 && mInterpolatedTime <= 0.5) {
            //0.2-0.5时间内p2的x保持不动，p4的x由-mRadius到-2*mRadius利用位移移动至下一个位置,mc增大0.25
            //(mInterpolatedTime-0.2)/0.3 获取0-1的值
            canvas.translate((mInterpolatedTime - 0.2f) * distance / 0.5f, 0);

            if (toPosition > mCurrentPosition) {
                p2.setX(2 * mRadius);
                p4.setX((float) (-mRadius - (mInterpolatedTime - 0.2) / 0.3 * mRadius));
            } else {
                p4.setX(-2 * mRadius);
                p2.setX((float) (mRadius + (mInterpolatedTime - 0.2) / 0.3 * mRadius));
            }

            p2.setMc((float) (mc + 0.25 * (mInterpolatedTime - 0.2) / 0.3));
            p4.setMc((float) (mc + 0.25 * (mInterpolatedTime - 0.2) / 0.3));

        } else if (mInterpolatedTime > 0.5 && mInterpolatedTime <= 0.8) {
            //0.5-0.8时间内p2的x由2*mRadius变为mRadius，p4的x由-2*mRadius变为-mRadius，mc减少0.25恢复原值
            canvas.translate((mInterpolatedTime - 0.2f) * distance / 0.5f, 0);
            if (toPosition > mCurrentPosition) {
                p2.setX((float) (2 * mRadius - (mInterpolatedTime - 0.5) / 0.3 * mRadius));
                p4.setX((float) (-2 * mRadius + (mInterpolatedTime - 0.5) / 0.3 * mRadius));
            } else {
                p4.setX((float) (-2 * mRadius + (mInterpolatedTime - 0.5) / 0.3 * mRadius));
                p2.setX((float) (2 * mRadius - (mInterpolatedTime - 0.5) / 0.3 * mRadius));
            }
            p2.setMc((float) (1.25f * mc - 0.25 * (mInterpolatedTime - 0.2) / 0.3));
            p4.setMc((float) (1.25 * mc - 0.25 * (mInterpolatedTime - 0.2) / 0.3));
        } else if (mInterpolatedTime > 0.8 && mInterpolatedTime <= 0.9) {
            //0.8-0.9时间内p4的x由-mRadius变为-mRadius+0.25*mRadius，回弹超出效果
            canvas.translate((mInterpolatedTime - 0.2f) * distance / 0.5f, 0);
            p2.setMc(mc);
            p4.setMc(mc);
            if (toPosition > mCurrentPosition) {
                p4.setX((float) (-mRadius + 0.25 * (mInterpolatedTime - 0.8) / 0.1 * mRadius));
            } else {
                p2.setX((float) (mRadius - 0.25 * (mInterpolatedTime - 0.8) / 0.1 * mRadius));
            }
        } else if (mInterpolatedTime > 0.9 && mInterpolatedTime <= 1) {
            //回弹超出效果恢复
            p2.setMc(mc);
            p4.setMc(mc);
            if (toPosition > mCurrentPosition) {
                canvas.translate(mRadius + distance, 0);
                p4.setX((float) (-0.75 * mRadius - 0.25 * (mInterpolatedTime - 0.9) / 0.1 * mRadius));
            } else {
                canvas.translate(-mRadius + distance, 0);
                p2.setX((float) (0.75 * mRadius + 0.25 * (mInterpolatedTime - 0.9) / 0.1 * mRadius));
            }
        }

        mPath.moveTo(p1.x, p1.y);
        mPath.cubicTo(p1.rightPoint.x, p1.rightPoint.y, p2.topPoint.x, p2.topPoint.y, p2.x, p2.y);
        mPath.cubicTo(p2.bottomPoint.x, p2.bottomPoint.y, p3.rightPoint.x, p3.rightPoint.y, p3.x, p3.y);
        mPath.cubicTo(p3.leftPoint.x, p3.leftPoint.y, p4.bottomPoint.x, p4.bottomPoint.y, p4.x, p4.y);
        mPath.cubicTo(p4.topPoint.x, p4.topPoint.y, p1.leftPoint.x, p1.leftPoint.y, p1.x, p1.y);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
        super.dispatchDraw(canvas);
    }


    private void resetP() {
        p1.setY(-mRadius);
        p1.setX(0);
        p1.setMc(mc);

        p3.setY(mRadius);
        p3.setX(0);
        p3.setMc(mc);

        p2.setY(0);
        p2.setX(mRadius);
        p2.setMc(mc);

        p4.setY(0);
        p4.setX(-mRadius);
        p4.setMc(mc);
    }


    private void moveTo(int currentPosition, int toPosition) {
        distance = (toPosition - this.mCurrentPosition) * (2 * mRadius + space) + (toPosition > currentPosition ? -mRadius : mRadius);
    }

    /**
     * 初始化
     */
    private void init() {
        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#44ff2222"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.parseColor("#FFFFB9"));
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(5);
    }

    private class XPoint {
        public float x;
        public float y;
        public float mc;
        public PointF topPoint = new PointF();
        public PointF bottomPoint = new PointF();

        public XPoint(float x, float y, float mc) {
            this.x = x;
            this.y = y;
            this.mc = mc;
            topPoint.x = x;
            topPoint.y = y - mc;
            bottomPoint.x = x;
            bottomPoint.y = y + mc;
        }

        public void setX(float x) {
            this.x = x;
            topPoint.x = x;
            bottomPoint.x = x;
        }

        public void setY(float y) {
            this.y = y;
            topPoint.y = y - mc;
            bottomPoint.y = y + mc;
        }

        public void setMc(float mc) {
            this.mc = mc;
            topPoint.y = y - mc;
            bottomPoint.y = y + mc;
        }
    }


    private class YPoint {
        public float x;
        public float y;
        public float mc;
        public PointF leftPoint = new PointF();
        public PointF rightPoint = new PointF();

        public YPoint(float x, float y, float mc) {
            this.x = x;
            this.y = y;
            this.mc = mc;
            leftPoint.y = y;
            leftPoint.x = x - mc;
            rightPoint.y = y;
            rightPoint.x = x + mc;
        }

        public void setX(float x) {
            this.x = x;
            leftPoint.x = x - mc;
            rightPoint.x = x + mc;
        }

        public void setY(float y) {
            this.y = y;
            leftPoint.y = y;
            rightPoint.y = y;
        }

        public void setMc(float mc) {
            this.mc = mc;
            leftPoint.x = x - mc;
            rightPoint.x = x + mc;
        }
    }
}
