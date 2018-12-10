package com.yueyue_projects.library;

import android.content.Context;
import android.content.res.TypedArray;

public class ParamsController {
     int mainTickDrawableId;
     int otherTickDrawableId;
     int milliSecondIntervalSize;
     String tickText;
     int rulerPosition;
    static final int MillI_SECOND_INTERVAL_SIZE = 50;
    /**
     * 大格精度
     */
    public int secondPrecision = -1;

    /**
     * 小格精度
     */
    public int millisecondPrecision = -1;
    public ParamsController(Context context){
        TypedArray ta = context.obtainStyledAttributes(null, R.styleable.TimeHorizontalScrollView, 0, 0);
        mainTickDrawableId = ta.getResourceId(R.styleable.TimeHorizontalScrollView_mainTickDrawableId, -1);
        otherTickDrawableId = ta.getResourceId(R.styleable.TimeHorizontalScrollView_otherTickDrawableId, -1);
        milliSecondIntervalSize = ta.getInteger(R.styleable.TimeHorizontalScrollView_tickPaddingRight, MillI_SECOND_INTERVAL_SIZE);
        rulerPosition = ta.getInteger(R.styleable.TimeHorizontalScrollView_rulerPosition, UnitRuler.RULER_BOTTOM);
        secondPrecision = ta.getInteger(R.styleable.TimeHorizontalScrollView_SecondPrecision, UnitRuler.DEFAULT_SECOND_PRECISION);
        millisecondPrecision = ta.getInteger(R.styleable.TimeHorizontalScrollView_MillisecondPrecision, UnitRuler.DEFAULT_MILLISECOND_PRECISION);
        ta.recycle();
    }
    public abstract static class Params {
        private Context mContext;
        public Params(Context context){
            mContext = context;
        }
        /**
         * 默认的秒级精度
         */
        static final int DEFAULT_SECOND_PRECISION = 1;
        /**
         * 默认的毫秒级精度
         */
        static final int DEFAULT_MILLISECOND_PRECISION = 250;
        /**
         * 大格精度
         */
        public int secondPrecision = DEFAULT_SECOND_PRECISION;

        /**
         * 小格精度
         */
        public int millisecondPrecision = DEFAULT_MILLISECOND_PRECISION;

        public int rulerPosition = UnitRuler.RULER_BOTTOM;


        public int milliSecondIntervalSize = -1;

        public int mainTickImageViewDrawableId = -1, otherTickImageViewDrawableId = -1;

        public void apply(ParamsController mParamsController) {
            mParamsController.setSecondPrecision(this.secondPrecision);
            mParamsController.setMillisecondPrecision(this.millisecondPrecision);
            if (this.mainTickImageViewDrawableId != -1) {
                mParamsController.setMainTickDrawableId(this.mainTickImageViewDrawableId);
            }
            if (this.otherTickImageViewDrawableId != -1) {
                mParamsController.setOtherTickDrawableId(this.otherTickImageViewDrawableId);
            }
            if (this.milliSecondIntervalSize != -1) {
                mParamsController.setMilliSecondIntervalSize(this.milliSecondIntervalSize);
            }
            mParamsController.setRulerPosition(this.rulerPosition);
        }
    }

    public void setMainTickDrawableId(int mainTickDrawableId) {
        this.mainTickDrawableId = mainTickDrawableId;
    }

    public void setOtherTickDrawableId(int otherTickDrawableId) {
        this.otherTickDrawableId = otherTickDrawableId;
    }

    public void setMilliSecondIntervalSize(int milliSecondIntervalSize) {
        this.milliSecondIntervalSize = milliSecondIntervalSize;
    }

    public void setRulerPosition(int rulerPosition) {
        this.rulerPosition = rulerPosition;
    }

    public void setSecondPrecision(int secondPrecision) {
        this.secondPrecision = secondPrecision;
    }

    public void setMillisecondPrecision(int millisecondPrecision) {
        this.millisecondPrecision = millisecondPrecision;
    }
}