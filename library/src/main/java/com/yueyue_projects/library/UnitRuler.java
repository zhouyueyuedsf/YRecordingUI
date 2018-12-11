package com.yueyue_projects.library;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class UnitRuler extends ViewGroup implements IBuilderParam{
    /**
     * 默认的秒级精度
     */
    static final int DEFAULT_SECOND_PRECISION = 1;
    /**
     * 默认的毫秒级精度
     */
    static final int DEFAULT_MILLISECOND_PRECISION = 250;

    public static final int RULER_TOP = 0;
    public static final int RULER_BOTTOM = 1;
    static final int MillI_SECOND_INTERVAL_SIZE = 50;

//    private StyleBuilder mStyleBuilder;

    /**
     * 大格精度
     */
    private int secondPrecision = DEFAULT_SECOND_PRECISION;

    /**
     * 小格精度
     */
    private int millisecondPrecision = DEFAULT_MILLISECOND_PRECISION;

    private int milliSecondIntervalSize = MillI_SECOND_INTERVAL_SIZE;

    private ImageView[] tickImageViews;

    private TextView tickTextView;

    UnitRulerParamsController mParamsController;
    public UnitRuler(Context context) {
        this(context, null);
    }

    public UnitRuler(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UnitRuler(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mParamsController = new UnitRulerParamsController(context, attrs, defStyleAttr);
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
            milliSecondIntervalSize = (MeasureSpec.getSize(widthMeasureSpec) - totalWidthWithoutPadding) / tickImageViews.length;
            mParamsController.milliSecondIntervalSize = milliSecondIntervalSize;
            totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            milliSecondIntervalSize = mParamsController.milliSecondIntervalSize;
            for (ImageView imageView : tickImageViews) {
                measureChild(imageView, widthMeasureSpec, heightMeasureSpec);
                totalWidth += imageView.getMeasuredWidth() + milliSecondIntervalSize;
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
        if (mParamsController.rulerPosition == RULER_TOP) {
            layoutTop(l, t, r, b);
        } else if (mParamsController.rulerPosition == RULER_BOTTOM) {
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
            startLeft = startLeft + imageView.getMeasuredWidth() + milliSecondIntervalSize;
        }
        return maxHeight;
    }


    static class StyleBuilder extends Builder<IBuilderParam> {

        public StyleBuilder(Context context) {
            super(context);
            P = new UnitRulerParamsController.UnitRulerParams(context);
        }

        public Builder setTickValue(String value){
            ((UnitRulerParamsController.UnitRulerParams)P).tickText = value;
            return this;
        }

        @Override
        public IBuilderParam create() {
            UnitRuler unitRuler = new UnitRuler(context);
            P.apply(unitRuler.mParamsController);
            unitRuler.show();
            return unitRuler;
        }
    }

    private void show() {
        tickImageViews = new ImageView[(mParamsController.secondPrecision * 1000) / mParamsController.millisecondPrecision];
        for (int i = 0; i < tickImageViews.length; i++) {
            tickImageViews[i] = new ImageView(this.getContext());
            addSystemView(tickImageViews[i]);
        }
        if (mParamsController.mainTickDrawableId != -1) {
            tickImageViews[0].setImageDrawable(this.getContext().getResources().getDrawable(mParamsController.mainTickDrawableId));
        } else {
            tickImageViews[0].setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.im_main_tick));
        }

        if (mParamsController.otherTickDrawableId != -1) {
            for (int i = 1; i < tickImageViews.length; i++) {
                tickImageViews[i].setImageDrawable(this.getContext().getResources().getDrawable(mParamsController.otherTickDrawableId));
            }
        } else {
            for (int i = 1; i < tickImageViews.length; i++) {
                tickImageViews[i].setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.im_tick));
            }
        }
        tickTextView = new TextView(this.getContext());
        addSystemView(tickTextView);
        tickTextView.setText(mParamsController.tickText);
        tickTextView.setTextColor(mParamsController.tickValueColor);
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

    int getRenderComponentsTotalHeight(){
        if (tickImageViews.length > 0) {
            return tickTextView.getMeasuredHeight() + tickImageViews[0].getMeasuredHeight();
        }
        return tickTextView.getHeight();
    }
}
