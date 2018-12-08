package com.yueyue_projects.library;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.Calendar;
import java.util.Deque;
import java.util.LinkedList;

import static android.content.ContentValues.TAG;

public class TimeHorizontalScrollView extends HorizontalScrollView {
    private final int DEFAULT_START_POSITION = ScreenUtil.getScreenWidthPix(this.getContext().getApplicationContext()) / 2;
    private int mStartPosition = DEFAULT_START_POSITION;
    private Deque<UnitRuler> mUnitRulers = new LinkedList<>();

    private final int PRE_LOAD_COUNT = 3;
    private LinearLayout rootLayout;
    private boolean initFlag = true;

    private int mUnitRulerPx = -1;
    public TimeHorizontalScrollView(Context context) {
        this(context, null);
    }

    public TimeHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.getChildCount() == 1 && rootLayout.getChildCount() >= 1 && initFlag) {
            int totalWidth = 0;
            for (int i = 0; i < rootLayout.getChildCount(); i++) {
                View v = rootLayout.getChildAt(i);
                if (i == 1) {
                    mUnitRulerPx = v.getMeasuredWidth();
                }
                totalWidth += v.getMeasuredWidth();
            }
            if (totalWidth < ScreenUtil.getScreenWidthPix(this.getContext())) {
                addOneUnitRuler();
            } else {
                //预加载三个刻度
                for (int i = 0; i < 3; i++) {
                    addOneUnitRuler();
                }
                initFlag = false;
            }

        }
    }


    private void initUI(){
        ensureRootLayout();
        setStartPosition();
        addOneUnitRuler();
    }

    private int bigPrecision;
    private int smallPrecision;
    private void addOneUnitRuler() {
        UnitRuler unitRuler = new UnitRuler(this.getContext());
        if (mUnitRulers.size() != 0) {
            unitRuler.setTickValue(TimeUtil.incrementBySecond(mUnitRulers.getLast().getTickValue()));
        } else {
            unitRuler.setTickValue("00:00");
        }
        mUnitRulers.add(unitRuler);
        if (initFlag) {
            bigPrecision = unitRuler.getSecondPrecision();
            smallPrecision = unitRuler.getMillisecondPrecision();
        }
        rootLayout.addView(unitRuler, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void addRootLayout(){
        if (rootLayout == null) {
            rootLayout = new LinearLayout(this.getContext());
            rootLayout.setOrientation(LinearLayout.HORIZONTAL);
            this.addView(rootLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private void ensureRootLayout(){
        if (this.getChildCount() == 0) {
            addRootLayout();
        }
    }

    private void setStartPosition() {
        if (rootLayout != null) {
            rootLayout.addView(new LinearLayout(this.getContext()), 0,
                    new LinearLayout.LayoutParams(ScreenUtil.getScreenWidthPix(this.getContext()) - mStartPosition, ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            throw new IllegalStateException("rootLayout mush be add before setStartPosition");
        }
    }

    private int preBigN = 0;

    /**
     *
     * @param time 格式为mm:ss:SSS
     */
    public void moveTo(String time){
        if (mUnitRulerPx != -1) {
            float millisecondEachPx = ((float) (bigPrecision * 1000)) / mUnitRulerPx;

            final int scrollToPx = TimeUtil.convertSpecPrecision(time, millisecondEachPx, Calendar.MILLISECOND);

            int bigN = TimeUtil.convertSpecPrecision(time, bigPrecision, Calendar.SECOND);
            if (bigN - preBigN > 1) {
                for (int i = 0; i < bigN - preBigN; i++) {
                    addOneUnitRuler();
                }
                preBigN = bigN;
            }
            Log.d(TAG, "moveTo: " + scrollToPx);
            post(new Runnable() {
                @Override
                public void run() {
                    smoothScrollTo(scrollToPx, TimeHorizontalScrollView.this.getScrollY());
                }
            });

        }
    }
}
