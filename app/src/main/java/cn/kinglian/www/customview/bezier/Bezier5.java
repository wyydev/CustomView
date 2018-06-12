package cn.kinglian.www.customview.bezier;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

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

    private int mCenterX;

    private int mCenterY;

    private int tabNum = 0;

    /**
     * tab之间的间距
     */
    private float space;

    private float mStartX;

    private int mStartY;

    private float mInterpolatedTime;

    private float mRadius;

    /**
     * 当前选中position
     */
    private int mCurrentPosition;

    /**
     * 顺时针记录 ，x相同的点
     */
    private XPoint p1, p3;
    /**
     * 顺时针记录 ，y相同的点
     */
    private YPoint p2, p4;

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
            p1 = new XPoint(0, mRadius, mc);
            p3 = new XPoint(0, -mRadius, mc);
            p2 = new YPoint(mRadius, 0, mc);
            p4 = new YPoint(-mRadius, 0, mc);
        }

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
        super.dispatchDraw(canvas);
        canvas.save();
        mPath.reset();
        tabNum = getChildCount();
        for (int i = 0; i < tabNum; i++) {
            canvas.drawCircle(space + mRadius + i * (space + 2 * mRadius), mStartY, mRadius, mCirclePaint);
        }


    }

    /**
     * 初始化
     */
    private void init() {
        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);

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
