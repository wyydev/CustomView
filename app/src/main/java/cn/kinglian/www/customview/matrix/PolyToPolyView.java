package cn.kinglian.www.customview.matrix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import cn.kinglian.www.customview.R;

/**
 * 折叠view
 * <p>
 * Created by ywy on 2018/6/25.
 */

public class PolyToPolyView extends View {
    private Bitmap mBitmap;
    private Matrix mMatrix;

    private Paint mShadowPaint;
    private Matrix mShadowGradientMatrix;
    private LinearGradient mShadowGradient;

    public PolyToPolyView(Context context) {
        this(context, null);
    }

    public PolyToPolyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyToPolyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
        mMatrix = new Matrix();
        //左上，右上，右下，左下
        float[] src = new float[]{0, 0,
                mBitmap.getWidth(), 0,
                mBitmap.getWidth(), mBitmap.getHeight(),
                0, mBitmap.getHeight()
        };
        float[] dst = new float[]{0, 0,
                mBitmap.getWidth(), 100,
                mBitmap.getWidth(), mBitmap.getHeight() - 100,
                0, mBitmap.getHeight()
        };

        mMatrix.setPolyToPoly(src, 0, dst, 0, 4);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowGradientMatrix = new Matrix();
        mShadowGradientMatrix.setScale(mBitmap.getWidth(), 1);
        mShadowGradient = new LinearGradient(0, 0, 0.5f , 0, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        mShadowGradient.setLocalMatrix(mShadowGradientMatrix);
        mShadowPaint.setShader(mShadowGradient);
        mShadowPaint.setAlpha((int) (0.9 * 255));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        //对bitmap和阴影都应用mMatrix
        canvas.concat(mMatrix);
        //不能使用该方法，否则应用两次
//        canvas.drawBitmap(mBitmap, mMatrix, null);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        canvas.drawRect(0, 0, mBitmap.getWidth(), mBitmap.getHeight(), mShadowPaint);
        canvas.restore();
    }
}
