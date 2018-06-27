package cn.kinglian.www.customview.matrix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import cn.kinglian.www.customview.R;

/**
 * 折叠
 * <p>
 * Created by ywy on 2018/6/25.
 */

public class FoldView extends View {
    private Bitmap mBitmap;
    /**
     * 折叠个数
     */
    private int foldNum = 8;
    private Matrix[] matrices = new Matrix[foldNum];
    /**
     * 折叠后总宽和原图总宽比例
     */
    private float mFactor = 0.8f;
    /**
     * 原图每块宽度
     */
    private int mPerWidth;
    /**
     * 折叠后的总宽
     */
    private int widthAfterFold;
    /**
     * 折叠后每块的宽度
     */
    private int perWidthAfterFold;

    private Paint mShadowPaint;
    private Paint mShadowGradientPaint;
    private LinearGradient mLinearGradient;
    private Matrix mShadowMatrix;

    public FoldView(Context context) {
        this(context, null);
    }

    public FoldView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FoldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.penguins);
        mPerWidth = mBitmap.getWidth() / foldNum;
        widthAfterFold = (int) (mBitmap.getWidth() * mFactor);
        perWidthAfterFold = widthAfterFold / foldNum;
        //初始化matrix
        for (int i = 0; i < foldNum; i++) {
            matrices[i] = new Matrix();
        }
        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int alpha = (int) (255 * mFactor * 0.8);
        mShadowPaint.setColor(Color.argb((int) (alpha * 0.8f), 0, 0, 0));
        mShadowGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinearGradient = new LinearGradient(0, 0, 0.5f, 0,
                Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        mShadowMatrix = new Matrix();
        mShadowMatrix.setScale(mPerWidth, 1);
        mShadowGradientPaint.setAlpha(alpha);
        mShadowGradientPaint.setShader(mLinearGradient);
        //纵轴减小的那个高度，勾股定理
        int depth = (int) Math.abs(Math.sqrt(mPerWidth * mPerWidth
                - perWidthAfterFold * perWidthAfterFold));
        depth = depth/2;
//        depth = 200;
        //转换点
        float[] src = new float[8];
        float[] dst = new float[8];

        for (int i = 0; i < foldNum; i++) {
            //左上
            src[0] = i * mPerWidth;
            src[1] = 0;
            //右上
            src[2] = mPerWidth + src[0];
            src[3] = 0;
            //右下
            src[4] = src[2];
            src[5] = mBitmap.getHeight();
            //左下
            src[6] = src[0];
            src[7] = src[5];

            boolean isEven = (i % 2) == 0;

            //转换后左上
            dst[0] = i * perWidthAfterFold;
            dst[1] = isEven ? 0 : depth;
            //右上
            dst[2] = perWidthAfterFold + dst[0];
            dst[3] = isEven ? depth : 0;
            //右下
            dst[4] = dst[2];
            dst[5] = isEven ? mBitmap.getHeight() - depth : mBitmap.getHeight();
            //左下
            dst[6] = dst[0];
            dst[7] = isEven ? mBitmap.getHeight() : mBitmap.getHeight() - depth;

            matrices[i].setPolyToPoly(src, 0, dst, 0, 4);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < foldNum; i++) {
            canvas.save();
            canvas.concat(matrices[i]);
            //控制显示的大小
            canvas.clipRect(i * mPerWidth, 0, mPerWidth + i * mPerWidth, mBitmap.getHeight());
            canvas.drawBitmap(mBitmap, 0, 0, null);
            //移动绘制阴影
            canvas.translate(i * mPerWidth, 0);
            canvas.drawRect(0, 0, mPerWidth, mBitmap.getHeight(),
                    (i % 2) == 0 ? mShadowPaint : mShadowGradientPaint);
            canvas.restore();
        }
    }
}
