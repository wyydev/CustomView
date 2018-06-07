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
 * Created by wen on 2018/6/4.
 */

public class Bezier extends View {
    private Paint mPaint;
    private PointF statrPoint, endPoint, controlPoint;
    private int centerX, centerY;

    public Bezier(Context context) {
        this(context, null);
    }

    public Bezier(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Bezier(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        statrPoint = new PointF(0, 0);
        endPoint = new PointF(0, 0);
        controlPoint = new PointF(0, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;

        controlPoint.x = centerX;
        controlPoint.y = centerY;
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(20);
//        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawPoint(statrPoint.x, statrPoint.y, mPaint);
        canvas.drawPoint(endPoint.x, endPoint.y, mPaint);
        canvas.drawPoint(controlPoint.x, controlPoint.y, mPaint);

        mPaint.setStrokeWidth(4);
        canvas.drawLine(statrPoint.x, statrPoint.y, controlPoint.x, controlPoint.y, mPaint);
        canvas.drawLine(endPoint.x, endPoint.y, controlPoint.x, controlPoint.y, mPaint);

        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(8);
        Path path = new Path();
        path.moveTo(statrPoint.x, statrPoint.y);
        path.quadTo(controlPoint.x, controlPoint.y, endPoint.x, endPoint.y);
        canvas.drawPath(path, mPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controlPoint.x = event.getX();
        controlPoint.y = event.getY();
        invalidate();
        return true;
    }
}
