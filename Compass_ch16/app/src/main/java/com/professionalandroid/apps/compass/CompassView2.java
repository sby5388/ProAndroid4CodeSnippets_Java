/*
 * Professional Android, 4th Edition
 * Reto Meier and Ian Lake
 * Copyright 2018 John Wiley Wiley & Sons, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.professionalandroid.apps.compass;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

public class CompassView2 extends View {

    private RectF mBoundingBox;
    private Point mCenter;
    private RectF mInnerBoundingBox;

    private RadialGradient mRadialGradient;
    private int mX;
    private int mY;
    private Paint mPaintPgb;
    private Path mOuterRingPath;
    private LinearGradient mSkyShader;
    private Paint mSkyPaint;
    private LinearGradient mGroundShader;
    private Paint mGroundPaint;
    private Path mSkyPath;
    private Path mRollArrow;
    private PointF mRollStringCenter;
    private PointF mHeadStringCenter;
    private RadialGradient mGlassShader;
    private Paint mGlassPaint;

    private enum CompassDirection {
        N, NNE, NE, ENE,
        E, ESE, SE, SSE,
        S, SSW, SW, WSW,
        W, WNW, NW, NNW
    }

    private Paint markerPaint;
    private Paint textPaint;
    private Paint circlePaint;
    private String northString;
    private String eastString;
    private String southString;
    private int textHeight;

    int[] borderGradientColors;
    float[] borderGradientPositions;
    int[] glassGradientColors;
    float[] glassGradientPositions;

    int skyHorizonColorFrom;
    int skyHorizonColorTo;
    int groundHorizonColorFrom;
    int groundHorizonColorTo;

    public CompassView2(Context context) {
        this(context, null);
    }

    public CompassView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompassView2(Context context, AttributeSet attrs,
                        int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setFocusable(true);
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CompassView, defStyleAttr, 0);
        if (a.hasValue(R.styleable.CompassView_bearing)) {
            setBearing(a.getFloat(R.styleable.CompassView_bearing, 0));
        }
        a.recycle();

        Context c = this.getContext();
        Resources r = this.getResources();

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(ContextCompat.getColor(c, R.color.background_color));
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        circlePaint.setStyle(Paint.Style.STROKE);

        northString = r.getString(R.string.cardinal_north);
        eastString = r.getString(R.string.cardinal_east);
        southString = r.getString(R.string.cardinal_south);
        String westString = r.getString(R.string.cardinal_west);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(ContextCompat.getColor(c, R.color.text_color));
        textPaint.setTextSize(40);
        textPaint.setFakeBoldText(true);
        textPaint.setSubpixelText(true);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(30);

        textHeight = (int) textPaint.measureText("yY");

        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setColor(ContextCompat.getColor(c, R.color.marker_color));
        markerPaint.setAlpha(200);
        markerPaint.setStrokeWidth(1);
        markerPaint.setStyle(Paint.Style.STROKE);
        markerPaint.setShadowLayer(2, 1, 1, ContextCompat.getColor(c,
                R.color.shadow_color));

        borderGradientColors = new int[4];
        borderGradientPositions = new float[4];
        borderGradientColors[3] = ContextCompat.getColor(c,
                R.color.outer_border);
        borderGradientColors[2] = ContextCompat.getColor(c,
                R.color.inner_border_one);
        borderGradientColors[1] = ContextCompat.getColor(c,
                R.color.inner_border_two);
        borderGradientColors[0] = ContextCompat.getColor(c,
                R.color.inner_border);
        borderGradientPositions[3] = 0.0f;
        borderGradientPositions[2] = 1 - 0.03f;
        borderGradientPositions[1] = 1 - 0.06f;
        borderGradientPositions[0] = 1.0f;

        glassGradientColors = new int[5];
        glassGradientPositions = new float[5];

        int glassColor = 245;
        glassGradientColors[4] = Color.argb(65, glassColor,
                glassColor, glassColor);
        glassGradientColors[3] = Color.argb(100, glassColor,
                glassColor, glassColor);
        glassGradientColors[2] = Color.argb(50, glassColor,
                glassColor, glassColor);
        glassGradientColors[1] = Color.argb(0, glassColor,
                glassColor, glassColor);
        glassGradientColors[0] = Color.argb(0, glassColor,
                glassColor, glassColor);
        glassGradientPositions[4] = 1 - 0.0f;
        glassGradientPositions[3] = 1 - 0.06f;
        glassGradientPositions[2] = 1 - 0.10f;
        glassGradientPositions[1] = 1 - 0.20f;
        glassGradientPositions[0] = 1 - 1.0f;

        skyHorizonColorFrom = ContextCompat.getColor(c,
                R.color.horizon_sky_from);
        skyHorizonColorTo = ContextCompat.getColor(c,
                R.color.horizon_sky_to);
        groundHorizonColorFrom = ContextCompat.getColor(c,
                R.color.horizon_ground_from);
        groundHorizonColorTo = ContextCompat.getColor(c,
                R.color.horizon_ground_to);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // The compass is a circle that fills as much space as possible.
        // Set the measured dimensions by figuring out the shortest boundary,
        // height or width.
        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);
        int d = Math.min(measuredWidth, measuredHeight);
        setMeasuredDimension(d, d);
    }

    private int measure(int measureSpec) {
        int result = 0;

        // Decode the measurement specifications.
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.UNSPECIFIED) {
            // Return a default size of 200 if no bounds are specified.
            result = 200;
        } else {
            // As you want to fill the available space
            // always return the full available bounds.
            result = specSize;
        }
        return result;
    }

    private void init() {
        mCenter = new Point();
        mBoundingBox = new RectF();
        mInnerBoundingBox = new RectF();
        mPaintPgb = new Paint();
        mOuterRingPath = new Path();
        mSkyPaint = new Paint();
        mGroundPaint = new Paint();
        mSkyPath = new Path();
        mRollArrow = new Path();
        mRollStringCenter = new PointF();
        mHeadStringCenter = new PointF();
        mGlassPaint = new Paint();
    }

    private RadialGradient getRadialGradient(int px, int py) {
        if (mX == px && mY == py) {
            return mRadialGradient;
        }
        final int radius = Math.min(px, py) - 2;
        mRadialGradient = new RadialGradient(px, py, radius,
                borderGradientColors, borderGradientPositions, Shader.TileMode.CLAMP);
        mSkyShader = new LinearGradient(mCenter.x,
                mInnerBoundingBox.top, mCenter.x, mInnerBoundingBox.bottom,
                skyHorizonColorFrom, skyHorizonColorTo, Shader.TileMode.CLAMP);
        mGroundShader = new LinearGradient(mCenter.x,
                mInnerBoundingBox.top, mCenter.x, mInnerBoundingBox.bottom,
                groundHorizonColorFrom, groundHorizonColorTo, Shader.TileMode.CLAMP);

        float ringWidth = textHeight + 4;
        float innerRadius = ((mCenter.y + radius - ringWidth) - (mCenter.y - radius + ringWidth)) / 2;
        mGlassShader = new RadialGradient(px, py, (int) innerRadius,
                glassGradientColors,
                glassGradientPositions,
                Shader.TileMode.CLAMP);
        return mRadialGradient;
    }

    private LinearGradient getSkyShader(int px, int py) {
        return mSkyShader;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float ringWidth = textHeight + 4;

        int height = getMeasuredHeight();
        int width = getMeasuredWidth();

        int px = width / 2;
        int py = height / 2;

        mCenter.set(px, py);
        int radius = Math.min(px, py) - 2;


        mBoundingBox.set(mCenter.x - radius,
                mCenter.y - radius,
                mCenter.x + radius,
                mCenter.y + radius);


        mInnerBoundingBox.set(mCenter.x - radius + ringWidth,
                mCenter.y - radius + ringWidth,
                mCenter.x + radius - ringWidth,
                mCenter.y + radius - ringWidth);
        // TODO: 2021/1/16

        float innerRadius = mInnerBoundingBox.height() / 2;

        mPaintPgb.setShader(getRadialGradient(px, py));
        mOuterRingPath.addOval(mBoundingBox, Path.Direction.CW);
        canvas.drawPath(mOuterRingPath, mPaintPgb);

        mSkyPaint.setShader(getSkyShader(px, py));
        mGroundPaint.setShader(mGroundShader);

        float tiltDegree = mPitch;
        while (tiltDegree > 90 || tiltDegree < -90) {
            if (tiltDegree > 90) {
                tiltDegree = -90 + (tiltDegree - 90);
            }
            if (tiltDegree < -90) {
                tiltDegree = 90 - (tiltDegree + 90);
            }
        }
        float rollDegree = mRoll;
        while (rollDegree > 180 || rollDegree < -180) {
            if (rollDegree > 180) {
                rollDegree = -180 + (rollDegree - 180);
            }
            if (rollDegree < -180) {
                rollDegree = 180 - (rollDegree + 180);
            }
        }


        mSkyPath.addArc(mInnerBoundingBox,
                -tiltDegree,
                (180 + (2 * tiltDegree)));

        canvas.save();
        canvas.rotate(-rollDegree, px, py);
        canvas.drawOval(mInnerBoundingBox, mGroundPaint);
        canvas.drawPath(mSkyPath, mSkyPaint);
        canvas.drawPath(mSkyPath, markerPaint);

        int markWidth = radius / 3;
        int startX = mCenter.x - markWidth;
        int endX = mCenter.x + markWidth;

        double h = innerRadius * Math.cos(Math.toRadians(90 - tiltDegree));
        double justTiltY = mCenter.y - h;

        float pxPerDegree = (mInnerBoundingBox.height() / 2) / 45f;

        for (int i = 90; i >= -90; i -= 10) {
            double ypos = justTiltY + i * pxPerDegree;

            // Only display the scale within the inner face.
            if ((ypos < (mInnerBoundingBox.top + textHeight)) ||
                    (ypos > mInnerBoundingBox.bottom - textHeight))
                continue;

            // Draw a line and the tilt angle for each scale increment.
            canvas.drawLine(startX, (float) ypos,
                    endX, (float) ypos,
                    markerPaint);

            int displayPos = (int) (tiltDegree - i);

            String displayString = String.valueOf(displayPos);

            float stringSizeWidth = textPaint.measureText(displayString);

            canvas.drawText(displayString,
                    (int) (mCenter.x - stringSizeWidth / 2),
                    (int) (ypos) + 1,
                    textPaint);
        }

        markerPaint.setStrokeWidth(2);
        canvas.drawLine(mCenter.x - radius / 2,
                (float) justTiltY,
                mCenter.x + radius / 2,
                (float) justTiltY,
                markerPaint);
        markerPaint.setStrokeWidth(1);

        // Draw the arrow
        mRollArrow.moveTo(mCenter.x - 3, (int) mInnerBoundingBox.top + 14);
        mRollArrow.lineTo(mCenter.x, (int) mInnerBoundingBox.top + 10);
        mRollArrow.moveTo(mCenter.x + 3, mInnerBoundingBox.top + 14);
        mRollArrow.lineTo(mCenter.x, mInnerBoundingBox.top + 10);
        canvas.drawPath(mRollArrow, markerPaint);

        // Draw the string
        String rollText = String.valueOf(rollDegree);
        double rollTextWidth = textPaint.measureText(rollText);
        canvas.drawText(rollText,
                (float) (mCenter.x - rollTextWidth / 2),
                mInnerBoundingBox.top + textHeight + 2,
                textPaint);

        canvas.restore();
        canvas.save();

        canvas.rotate(180, mCenter.x, mCenter.y);
        for (int i = -180; i < 180; i += 10) {
            // Show a numeric value every 30 degrees
            if (i % 30 == 0) {
                String rollString = String.valueOf(i * -1);
                float rollStringWidth = textPaint.measureText(rollString);
                mRollStringCenter.set(mCenter.x - rollStringWidth / 2,
                        mInnerBoundingBox.top + 1 + textHeight);
                canvas.drawText(rollString,
                        mRollStringCenter.x, mRollStringCenter.y,
                        textPaint);
            }
            // Otherwise draw a marker line
            else {
                canvas.drawLine(mCenter.x, (int) mInnerBoundingBox.top,
                        mCenter.x, (int) mInnerBoundingBox.top + 5,
                        markerPaint);
            }
            canvas.rotate(10, mCenter.x, mCenter.y);
        }
        canvas.restore();

        canvas.save();
        canvas.rotate(-1 * (mBearing), px, py);
        double increment = 22.5;

        for (double i = 0; i < 360; i += increment) {
            CompassDirection cd = CompassDirection.values()
                    [(int) (i / 22.5)];

            String headString = cd.toString();
            float headStringWidth = textPaint.measureText(headString);

            mHeadStringCenter.set(mCenter.x - headStringWidth / 2,
                    mBoundingBox.top + 1 + textHeight);

            if (i % increment == 0)
                canvas.drawText(headString,
                        mHeadStringCenter.x, mHeadStringCenter.y,
                        textPaint);
            else
                canvas.drawLine(mCenter.x, (int) mBoundingBox.top,
                        mCenter.x, (int) mBoundingBox.top + 3,
                        markerPaint);
            canvas.rotate((int) increment, mCenter.x, mCenter.y);
        }
        canvas.restore();


        mGlassPaint.setShader(mGlassShader);
        canvas.drawOval(mInnerBoundingBox, mGlassPaint);

        // Draw the outer ring
        canvas.drawOval(mBoundingBox, circlePaint);

        // Draw the inner ring
        circlePaint.setStrokeWidth(2);
        canvas.drawOval(mInnerBoundingBox, circlePaint);
    }

    private float mBearing;

    public void setBearing(float bearing) {
        mBearing = bearing;
        invalidate();
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
    }

    public float getBearing() {
        return mBearing;
    }

    private float mPitch;

    public void setPitch(float pitch) {
        mPitch = pitch;
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
    }

    public float getPitch() {
        return mPitch;
    }

    private float mRoll;

    public void setRoll(float roll) {
        mRoll = roll;
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
    }

    public float getRoll() {
        return mRoll;
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(
            final AccessibilityEvent event) {
        super.dispatchPopulateAccessibilityEvent(event);
        if (isShown()) {
            String bearingStr = String.valueOf(mBearing);
            event.getText().add(bearingStr);
            return true;
        } else {
            return false;
        }
    }
}