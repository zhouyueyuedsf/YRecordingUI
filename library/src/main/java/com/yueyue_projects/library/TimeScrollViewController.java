package com.yueyue_projects.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;

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

    int initTickNum;

    int startPosition;

    public TimeScrollViewController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TimeHorizontalScrollView, defStyleAttr, 0);
        pivotLineWidth = ta.getDimensionPixelSize(R.styleable.TimeHorizontalScrollView_pivotLineWidth, 4);
        pivotLineColor = ta.getColor(R.styleable.TimeHorizontalScrollView_pivotLineColor, Color.BLUE);
        defaultRectWidth = ta.getDimensionPixelSize(R.styleable.TimeHorizontalScrollView_defaultRectWidth, 5);
        defaultRectColor = ta.getColor(R.styleable.TimeHorizontalScrollView_pivotLineColor, Color.BLACK);
        initTickNum = ta.getInteger(R.styleable.TimeHorizontalScrollView_initTickNum, 3);

        TypedValue outValue = new TypedValue();
        ta.getValue(R.styleable.TimeHorizontalScrollView_startPosition, outValue);
        if (outValue.type == TypedValue.TYPE_DIMENSION) {
            startPosition = outValue.data;
        } else if (outValue.type == TypedValue.TYPE_INT_DEC) {
            switch (outValue.data) {
                case StartPosition.LEFT:
                    startPosition = 0;
                    break;
                case StartPosition.CENTER:
                    startPosition = ScreenUtil.getScreenWidthPix(context) / 2;
                    break;
                default:
                    startPosition = ScreenUtil.getScreenWidthPix(context) / 2;
            }
        }
        ta.recycle();
    }

    final class StartPosition {
        static final int LEFT = 0;
        static final int CENTER = 2;
    }
}
