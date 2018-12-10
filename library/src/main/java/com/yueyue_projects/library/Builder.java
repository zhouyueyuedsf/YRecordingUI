package com.yueyue_projects.library;

import android.content.Context;

public abstract class Builder<T> {
    ParamsController.Params P;
    Context context;
    public Builder(Context context) {
        this.context = context;
    }

    public Builder setTimeRulerPosition(int rulerPosition) {
        P.rulerPosition = rulerPosition;
        return this;
    }

    public Builder setSecondPrecision(int secondPrecision) {
        P.secondPrecision = secondPrecision;
        return this;
    }

    public Builder setMilliSecondIntervalSize(int milliSecondIntervalSize) {
        P.milliSecondIntervalSize = milliSecondIntervalSize;
        return this;
    }

    public Builder setTickImageViewDrawable(int mainTickImageViewDrawableId, int otherTickImageViewDrawableId) {
        P.mainTickImageViewDrawableId = mainTickImageViewDrawableId;
        P.otherTickImageViewDrawableId = otherTickImageViewDrawableId;
        return this;
    }

    public void setMillisecondPrecision(int millisecondPrecision) {
        P.millisecondPrecision = millisecondPrecision;
    }
    public T create() {
        return null;
    }


}
