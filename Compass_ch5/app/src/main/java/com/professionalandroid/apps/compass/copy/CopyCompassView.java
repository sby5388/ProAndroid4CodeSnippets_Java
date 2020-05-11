package com.professionalandroid.apps.compass.copy;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.professionalandroid.apps.compass.BuildConfig;
import com.professionalandroid.apps.compass.R;

/**
 * @author Administrator  on 2020/5/8.
 */
public class CopyCompassView extends View {
    private static final String TAG = "CopyCompassView";
    private static final boolean debug = BuildConfig.DEBUG;

    /**
     * 刻度尺画笔
     */
    private Paint markerPaint;
    /**
     * 文本画笔
     */
    private Paint textPaint;
    /**
     * 背景（圆圈）画笔
     */
    private Paint circlePaint;
    /**
     * 四个方向
     */
    private String northString;
    private String eastString;
    private String southString;
    private String westString;

    /**
     * 文本大小
     */
    private int textHeight;
    /**
     * TODO ？
     */
    private float mBearing;


    public CopyCompassView(Context context) {
        super(context);
    }

    public CopyCompassView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CopyCompassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setFocusable(true);
        // TODO: 2020/5/8
        final TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.CopyCompassView, defStyleAttr, 0);
        //todo 是否携带了某个参数
        if (typedArray.hasValue(R.styleable.CopyCompassView_myBearing)) {
            setMyBearing(typedArray.getFloat(R.styleable.CopyCompassView_myBearing, 0));
        }
        typedArray.recycle();

        final Resources r = context.getResources();

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(ContextCompat.getColor(context, R.color.background_color));
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        northString = r.getString(R.string.cardinal_north);
        eastString = r.getString(R.string.cardinal_east);
        southString = r.getString(R.string.cardinal_south);
        westString = r.getString(R.string.cardinal_west);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(ContextCompat.getColor(context, R.color.text_color));
        textPaint.setTextSize(40f);

        // TODO: 2020/5/8 测绘文本的高度
        textHeight = (int) textPaint.measureText("yY");

        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setColor(ContextCompat.getColor(context, R.color.marker_color));

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //TODO 指南针需要尽可能地填充最大的圆
        //  设置边距来确定最大的高度或者宽度
        final int measuredWidth = measure(widthMeasureSpec);
        final int measuredHeight = measure(heightMeasureSpec);

        final int min = Math.min(measuredWidth, measuredHeight);
        if (debug) {
            Log.d(TAG, "onMeasure: widthMeasureSpec = " + widthMeasureSpec);
            Log.d(TAG, "onMeasure: heightMeasureSpec = " + heightMeasureSpec);
            Log.d(TAG, "onMeasure:measuredWidth = " + measuredWidth);
            Log.d(TAG, "onMeasure: measuredHeight = " + measuredHeight);
            Log.d(TAG, "onMeasure: min  = " + min);
        }
        // TODO: 2020/5/8 这个是啥
        setMeasuredDimension(min, min);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // TODO: 2020/5/8 >??
        final int measuredWidth = getMeasuredWidth();
        final int measuredHeight = getMeasuredHeight();

        final int px = measuredWidth / 2;
        final int py = measuredHeight / 2;

        //半径
        final int radius = Math.min(px, py);

        // FIXME: 2020/5/8 出现了闪退
        canvas.drawCircle(px, py, radius, circlePaint);

        // Rotate our perspective so that the 'top' is
        // facing the current bearing.
        //旋转初始化的角度(围绕圆心)
        canvas.save();
        canvas.rotate(-mBearing, px, py);

        final int textWidth = (int) textPaint.measureText("W");
        //todo cardinal:主要的
        final int cardinalX = px - textWidth / 2;
        final int cardinalY = px - textWidth / 2 + textHeight;

        //每15度、每45度绘制不同的内容，同时旋转
        // Draw the marker every 15 degrees and text every 45.
        for (int i = 0; i < 24; i++) {
            //draw a marker
            canvas.drawLine(px, py - radius, px, py - radius + 10, markerPaint);
        }
        canvas.restore();


    }

    private int measure(int measureSpec) {

        int result;
        //TODO  解码测量规范
        final int mode = MeasureSpec.getMode(measureSpec);
        final int size = MeasureSpec.getSize(measureSpec);

        if (debug) {
            Log.d(TAG, "measure: measureSpec = " + measureSpec);
            Log.d(TAG, "measure: mode = " + mode);
            Log.d(TAG, "measure: size = " + size);
        }
        //TODO UNSPECIFIED:不限制大小，尽可能地大
        if (mode == MeasureSpec.UNSPECIFIED) {
            // Return a default size of 200 if no bounds are specified.
            result = 200;
        } else {
            // As you want to fill the available space
            // always return the full available bounds.
            result = size;
        }

        return result;
    }


    private void setMyBearing(float bearing) {
        this.mBearing = bearing;
    }


}
