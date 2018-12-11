package com.yueyue_projects.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

public class TimeScrollViewController extends ParamsController {
    /**
     * 中轴线的宽度
     */
    int pivotLineWidth;
    /**
     * 中轴线的颜色
     */
    int pivotLineColor;

    int defaultRectWidth;

    int defaultRectColor;

    public TimeScrollViewController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TimeHorizontalScrollView, defStyleAttr, 0);
        pivotLineWidth = ta.getDimensionPixelSize(R.styleable.TimeHorizontalScrollView_pivotLineWidth, 4);
        pivotLineColor = ta.getColor(R.styleable.TimeHorizontalScrollView_pivotLineColor, Color.BLUE);
        defaultRectWidth = ta.getDimensionPixelSize(R.styleable.TimeHorizontalScrollView_defaultRectWidth, 5);
        defaultRectColor = ta.getColor(R.styleable.TimeHorizontalScrollView_pivotLineColor, Color.BLACK);

        ta.recycle();
    }
}
