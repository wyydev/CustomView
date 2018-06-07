package cn.kinglian.www.customview.bezier;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 贝塞尔曲线
 * <p>
 * Created by wen on 2018/6/5.
 */

public class Bezier2 extends View {
    private Paint mPaint;
    private PointF statrPoint, endPoint, controlPoint1, controlPoint2;
    private int centerX, centerY;

    public final static int POINT_ONE = 0x1;
    public final static int POINT_TWO = 0x2;

    private int currentPoint = POINT_ONE;

    public Bezier2(Context context) {
        this(context, null);
    }

    public Bezier2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Bezier2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        statrPoint = new PointF(0, 0);
        endPoint = new PointF(0, 0);
        controlPoint1 = new PointF(0, 0);
        controlPoint2 = new PointF(0, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;

        controlPoint1.x = centerX - 200;
        controlPoint1.y = centerY + 200;
        controlPoint2.x = centerX + 200;
        controlPoint2.y = centerY + 200;

        statrPoint.x = centerX - 200;
        if (statrPoint.x < 0) {
            statrPoint.x = 0;
        }

        statrPoint.y = centerY;

        endPoint.x = centerX + 200;
        if (endPoint.x > w) {
            endPoint.x = w;
        }
        endPoint.y = centerY;
    }

    public void setControlPoint(int point) {
        if (point == POINT_TWO) {
            currentPoint = point;
        } else {
            currentPoint = POINT_ONE;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(20);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawPoint(statrPoint.x, statrPoint.y, mPaint);
        canvas.drawPoint(endPoint.x, endPoint.y, mPaint);
        canvas.drawPoint(controlPoint1.x, controlPoint1.y, mPaint);
        canvas.drawPoint(controlPoint2.x, controlPoint2.y, mPaint);

        mPaint.setStrokeWidth(4);
        canvas.drawLine(statrPoint.x, statrPoint.y, controlPoint1.x, controlPoint1.y, mPaint);
        canvas.drawLine(endPoint.x, endPoint.y, controlPoint2.x, controlPoint2.y, mPaint);
        canvas.drawLine(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, mPaint);

        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(8);
        Path path = new Path();
        path.moveTo(statrPoint.x, statrPoint.y);
        path.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, endPoint.x, endPoint.y);
        canvas.drawPath(path, mPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (currentPoint == POINT_ONE) {
            controlPoint1.y = event.getY();
            controlPoint1.x = event.getX();
        } else if (currentPoint == POINT_TWO) {
            controlPoint2.y = event.getY();
            controlPoint2.x = event.getX();
        }
        invalidate();
        return true;
    }
}
