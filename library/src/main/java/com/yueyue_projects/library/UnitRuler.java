package com.yueyue_projects.library;

import android.content.Context;
import android.content.res.TypedArray;
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
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalWidth = 0;
        int height = 0;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int totalWidthWithoutPadding = 0;

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
        measureChild(tickTextView, widthMeasureSpec, heightMeasureSpec);
        if (tickImageViews.length > 0) {
            height = tickImageViews[0].getMeasuredHeight() + tickTextView.getMeasuredHeight();
        }

        setMeasuredDimension(totalWidth, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int maxHeight = layoutTickImageViews(l, t, r, b);
        tickTextView.layout(0, maxHeight, tickTextView.getMeasuredWidth(), tickTextView.getMeasuredHeight());
    }

    private int layoutTickImageViews(int l, int t, int r, int b) {
        int maxHeight = Integer.MIN_VALUE;
        int lc = 0;
        int tc = 0;
        for (ImageView imageView : tickImageViews) {
            if (maxHeight < imageView.getMeasuredHeight()) {
                maxHeight = imageView.getMeasuredHeight();
            }
            imageView.layout(lc, tc, lc + imageView.getMeasuredWidth(), tc + imageView.getMeasuredHeight());
            lc = lc + imageView.getMeasuredWidth() + intervalSize;
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
