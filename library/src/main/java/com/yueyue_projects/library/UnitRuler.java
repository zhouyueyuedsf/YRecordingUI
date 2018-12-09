package com.yueyue_projects.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class UnitRuler extends ViewGroup{
    private final int DEFAULT_LEVEL_PRECISION = 1;
    private final int DEFAULT_MILLSECOND_PRECISION = 250;

    public static final int RULER_TOP = 0;
    public static final int RULER_BOTTOM = 1;
    private int rulerPosition = RULER_TOP;
    /**
     * 大格精度
     */
    private int secondPrecision = DEFAULT_LEVEL_PRECISION;

    /**
     * 小格精度
     */
    private int millisecondPrecision = DEFAULT_MILLSECOND_PRECISION;


    private int intervalSize;

    private ImageView[] tickImageViews;

    private TextView tickTextView;
    public UnitRuler(Context context) {
        this(context, null);
    }

    public UnitRuler(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UnitRuler(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tickImageViews = new ImageView[(secondPrecision * 1000) / millisecondPrecision];
        for (int i = 0; i < tickImageViews.length; i++) {
            tickImageViews[i] = new ImageView(context);
            addSystemView(tickImageViews[i]);
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UnitRuler, defStyleAttr, 0);
        int mainTickDrawableId = ta.getResourceId(R.styleable.UnitRuler_mainTickDrawableId, -1);
        int otherTickDrawableId = ta.getResourceId(R.styleable.UnitRuler_otherTickDrawableId, -1);
        if (mainTickDrawableId != -1) {
            tickImageViews[0].setImageDrawable(context.getResources().getDrawable(mainTickDrawableId));
        } else {
            tickImageViews[0].setImageDrawable(context.getResources().getDrawable(R.drawable.im_main_tick));
        }

        if (otherTickDrawableId != -1) {
            for (int i = 1; i < tickImageViews.length; i++) {
                tickImageViews[i].setImageDrawable(context.getResources().getDrawable(otherTickDrawableId));
            }
        } else {
            for (int i = 1; i < tickImageViews.length; i++) {
                tickImageViews[i].setImageDrawable(context.getResources().getDrawable(R.drawable.im_tick));
            }
        }
        intervalSize = ta.getInteger(R.styleable.UnitRuler_tickPaddingRight, 50);
        String tickText = ta.getString(R.styleable.UnitRuler_tickValue);
        tickTextView = new TextView(context);
        addSystemView(tickTextView);
        tickTextView.setText(tickText);

        rulerPosition = ta.getInteger(R.styleable.UnitRuler_rulerPosition, RULER_TOP);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalWidth = 0;
        int totalHeight = 0;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int totalWidthWithoutPadding = 0;
        measureChild(tickTextView, widthMeasureSpec, heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            for (ImageView imageView : tickImageViews) {
                measureChild(imageView, widthMeasureSpec, heightMeasureSpec);
                totalWidthWithoutPadding += imageView.getMeasuredWidth();
            }
            intervalSize = (MeasureSpec.getSize(widthMeasureSpec) - totalWidthWithoutPadding) / tickImageViews.length;
            totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            for (ImageView imageView : tickImageViews) {
                measureChild(imageView, widthMeasureSpec, heightMeasureSpec);
                totalWidth += imageView.getMeasuredWidth() + intervalSize;
            }
        }
        // 处理高度
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            totalHeight = MeasureSpec.getSize(heightMeasureSpec);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            if (tickImageViews.length > 0) {
                totalHeight = tickImageViews[0].getMeasuredHeight() + tickTextView.getMeasuredHeight();
            }
        }
        setMeasuredDimension(totalWidth, totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (rulerPosition == RULER_TOP) {
            layoutTop(l, t, r, b);
        } else if (rulerPosition == RULER_BOTTOM) {
            layoutBottom(l, t, r, b);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    private void layoutBottom(int l, int t, int r, int b) {
        tickTextView.layout(0,
                b - t - tickTextView.getMeasuredHeight(), tickTextView.getMeasuredWidth(), b - t);
//        layoutTickImageViews(0, )
        int maxHeight = Integer.MIN_VALUE;
        for (ImageView imageView : tickImageViews) {
            if (maxHeight < imageView.getMeasuredHeight()) {
                maxHeight = imageView.getMeasuredHeight();
            }
        }
        layoutTickImageViews(0, b - maxHeight - tickTextView.getMeasuredHeight());
    }

    private void layoutTop(int l, int t, int r, int b) {
        int maxHeight = layoutTickImageViews(0, 0);
        tickTextView.layout(0, maxHeight, tickTextView.getMeasuredWidth(), tickTextView.getMeasuredHeight());
    }


    private int layoutTickImageViews(int startLeft, int startTop) {
        int maxHeight = Integer.MIN_VALUE;
        for (ImageView imageView : tickImageViews) {
            if (maxHeight < imageView.getMeasuredHeight()) {
                maxHeight = imageView.getMeasuredHeight();
            }
            imageView.layout(startLeft, startTop, startLeft + imageView.getMeasuredWidth(), startTop + imageView.getMeasuredHeight());
            startLeft = startLeft + imageView.getMeasuredWidth() + intervalSize;
        }
        return maxHeight;
    }


    public void setMainTickImageViewDrawable(Drawable drawable){
        if (tickImageViews.length > 0) {
            tickImageViews[0].setImageDrawable(drawable);
        }
    }

    public void setOtherTickImageViewDrawable(Drawable drawable){
        for (int i = 1; i < tickImageViews.length; i++) {
            tickImageViews[i].setImageDrawable(drawable);
        }
    }

    public void setRulerPosition(int position){
        rulerPosition = position;
    }

    public void setTickValue(String value){
        tickTextView.setText(value);
    }

    public String getTickValue(){
        return tickTextView.getText().toString();
    }
    private void addSystemView(View v) {
        addView(v, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    public int getSecondPrecision() {
        return secondPrecision;
    }

    public int getMillisecondPrecision() {
        return millisecondPrecision;
    }
}
