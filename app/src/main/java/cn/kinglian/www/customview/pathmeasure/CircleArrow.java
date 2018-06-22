package cn.kinglian.www.customview.pathmeasure;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import cn.kinglian.www.customview.R;

/**
 * 箭头绕圆旋转动画
 * <p>
 * Created by ywy on 2018/6/14.
 */

public class CircleArrow extends View {
    private Paint mBitmapPaint;
    private Paint mCirclePaint;
    private Path mPath;
    private PathMeasure mPathMeasure;
    private int circleColor = Color.parseColor("#eeeeee");
    private int mWidth;
    private int mHeight;
    private float mCurrentPercent = 0.0f;
    private ValueAnimator mValueAnimator;
    private float[] pos = new float[2];
    private float[] tan = new float[2];
    private Bitmap mArrowBitmap;
    private Matrix mMatrix;

    public CircleArrow(Context context) {
        this(context, null);
    }

    public CircleArrow(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleArrow(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setColor(circleColor);
        mPath = new Path();
        mPathMeasure = new PathMeasure();
        if (mValueAnimator == null) {
            mValueAnimator = ValueAnimator.ofFloat(0f, 1f);
            mValueAnimator.setDuration(3000);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.addUpdateListener(animation -> {
                        mCurrentPercent = (float) animation.getAnimatedValue();
                        postInvalidate();
                    }
            );
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        //宽高缩小一倍
        options.inSampleSize = 8;
        mArrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow, options);
        mMatrix = new Matrix();
        mPath.addCircle(0, 0, 200, Path.Direction.CW);
        mPathMeasure.setPath(mPath, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mValueAnimator != null && !mValueAnimator.isRunning()) {
            mValueAnimator.start();
        }
        return super.onTouchEvent(event);
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
        mPathMeasure.getPosTan(mCurrentPercent * mPathMeasure.getLength(), pos, tan);
        mMatrix.reset();
        float degree = (float) (Math.atan2(tan[1], tan[0]) * 180 / Math.PI);
        //绕中心旋转
        mMatrix.postRotate(degree, mArrowBitmap.getWidth() / 2, mArrowBitmap.getHeight() / 2);
        //将中心与圆上当前的点重合
        mMatrix.postTranslate(pos[0] - mArrowBitmap.getWidth() / 2, pos[1] - mArrowBitmap.getHeight() / 2);

        canvas.drawPath(mPath, mCirclePaint);
        canvas.drawBitmap(mArrowBitmap, mMatrix, mBitmapPaint);
        if (mCurrentPercent == 1 && mValueAnimator != null){
            mValueAnimator.setStartDelay(200);
            mValueAnimator.start();
        }
    }
}
