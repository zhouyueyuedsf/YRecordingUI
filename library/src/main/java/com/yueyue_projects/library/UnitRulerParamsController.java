package com.yueyue_projects.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

public class UnitRulerParamsController extends ParamsController {
    String tickText;

    public UnitRulerParamsController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UnitRuler, defStyleAttr, 0);
        tickText = ta.getString(R.styleable.UnitRuler_tickValue);
        ta.recycle();
    }

    public static class UnitRulerParams extends Params{
        public String tickText;

        public UnitRulerParams(Context context) {
            super(context);
        }

        @Override
        public void apply(ParamsController mParamsController) {
            super.apply(mParamsController);
            if (this.tickText != null) {
                ((UnitRulerParamsController)(mParamsController)).setTickText(this.tickText);
            }
        }
    }
    public void setTickText(String tickText) {
        this.tickText = tickText;
    }
}
