package cn.kinglian.www.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义雷达view
 * <p>
 * Created by wen on 2018/5/23.
 */

public class RadarView extends View {

    private static int DEFAULT_TEXT_SIZE = 25;
    private static int DEFAULT_COLOR = Color.BLACK;
    private static int DEFAULT_POINT_RADIUS = 10;
    private static int DEFAULT_MAX_VALUE = 100;

    //view中点
    private int mCenterX;
    private int mCenterY;

    //文字大小和颜色
    private int textSize = DEFAULT_TEXT_SIZE;
    private int textColor = DEFAULT_COLOR;
    //线颜色
    private int lineColor = DEFAULT_COLOR;
    //小圆点颜色和半径
    private int pointColor = DEFAULT_COLOR;
    private int pointRadius = DEFAULT_POINT_RADIUS;
    //覆盖区域颜色
    private int overlayColor = DEFAULT_COLOR;
    private Paint mTextPaint;
    private Paint mPointPaint;
    private Paint mLinePaint;
    private Paint mOverlayPaint;

    //数据
    private List<RadarData> mData;
    //属性分数最大值
    private int maxValue = DEFAULT_MAX_VALUE;
    //角度
    private float angle = 0;
    //雷达最大半径
    private float radius;


    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    //初始化
    private void init(Context context, AttributeSet attrs) {
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mOverlayPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        //文字
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        mPointPaint.setColor(pointColor);
        mLinePaint.setColor(lineColor);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(2);
        mOverlayPaint.setColor(overlayColor);
        mOverlayPaint.setStyle(Paint.Style.FILL);

        mData = new ArrayList<>(6);
        mData.add(new RadarData("伤害", 80));
        mData.add(new RadarData("助攻", 30));
        mData.add(new RadarData("击杀", 50));
        mData.add(new RadarData("拿塔", 60));
        mData.add(new RadarData("拿龙", 100));
        mData.add(new RadarData("拿兵", 200));
//        mData.add(new RadarData("g", 100));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCenterX = w / 2;
        mCenterY = h / 2;
        radius = Math.min(w, h) / 2 * 0.9f;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mData == null) {
            return;
        }
        angle = (float) (Math.PI * 2 / mData.size());

        drawPolygon(canvas);

        drawLines(canvas);

        drawText(canvas);

        drawRegion(canvas);
    }


    /**
     * 绘制正多边形
     */
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        //网格之间的间隙
        float r = radius / (mData.size() - 1);
        //绘制网格
        for (int i = 1; i < mData.size(); i++) { //绘制几个网格,中心点不用绘制
            //当前半径
            float curR = r * i;
            path.reset();
            for (int j = 0; j < mData.size(); j++) {
                if (j == 0) {
                    //移动到起点
                    path.moveTo(mCenterX + curR, mCenterY);
                } else {
                    //计算每个点的坐标
                    float x = (float) (mCenterX + curR * Math.cos(angle * j));
                    float y = (float) (mCenterY + curR * Math.sin(angle * j));
                    path.lineTo(x, y);
                }
            }
            path.close();
            canvas.drawPath(path, mLinePaint);
        }
    }

    /**
     * 绘制直线
     */
    private void drawLines(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < mData.size(); i++) {
            path.reset();
            path.moveTo(mCenterX, mCenterY);
            float x = (float) (mCenterX + radius * Math.cos(angle * i));
            float y = (float) (mCenterY + radius * Math.sin(angle * i));
            path.lineTo(x, y);
            canvas.drawPath(path, mLinePaint);
        }
    }

    private void drawText(Canvas canvas) {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float textHeight = fontMetrics.descent - fontMetrics.ascent;
        //取出最外层点的坐标
        for (int i = 0; i < mData.size(); i++) {
            float x = (float) (mCenterX + (radius + textHeight / 2) * Math.cos(angle * i));
            float y = (float) (mCenterY + (radius + textHeight / 2) * Math.sin(angle * i));
            if (angle * i >= 0 && angle * i <= Math.PI / 2) {//第4象限
                canvas.drawText(mData.get(i).getName(), x, y, mTextPaint);
            } else if (angle * i >= 3 * Math.PI / 2 && angle * i <= Math.PI * 2) {//第3象限
                canvas.drawText(mData.get(i).getName(), x, y, mTextPaint);
            } else if (angle * i > Math.PI / 2 && angle * i <= Math.PI) {//第2象限
                float dis = mTextPaint.measureText(mData.get(i).getName());//文本长度
                canvas.drawText(mData.get(i).getName(), x - dis, y, mTextPaint);
            } else if (angle * i >= Math.PI && angle * i < 3 * Math.PI / 2) {//第1象限
                float dis = mTextPaint.measureText(mData.get(i).getName());//文本长度
                canvas.drawText(mData.get(i).getName(), x - dis, y, mTextPaint);
            }
        }
    }

    /**
     * 绘制区域
     */
    private void drawRegion(Canvas canvas){
        Path path = new Path();
        mPointPaint.setAlpha(255);
        for(int i=0;i<mData.size();i++){
            double percent = mData.get(i).getValue()/maxValue;
            float x = (float) (mCenterX+radius*Math.cos(angle*i)*percent);
            float y = (float) (mCenterY+radius*Math.sin(angle*i)*percent);
            if(i==0){
                path.moveTo(x, mCenterY);
            }else{
                path.lineTo(x,y);
            }
            //绘制小圆点
            canvas.drawCircle(x,y,pointRadius,mPointPaint);
        }
        mOverlayPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, mOverlayPaint);
        mOverlayPaint.setAlpha(127);
        //绘制填充区域
        mOverlayPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, mOverlayPaint);
    }


    public void setData(List<RadarData> radarDataList) {
        if (radarDataList == null || radarDataList.size() < 3) {
            throw new IllegalArgumentException("radarDataList 的大小不能小于3");
        }
        this.mData = radarDataList;
    }
}
