package com.yueyue_projects.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.Calendar;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class TimeHorizontalScrollView extends HorizontalScrollView {
    private final int DEFAULT_START_POSITION = ScreenUtil.getScreenWidthPix(this.getContext().getApplicationContext()) / 2;
    private int mStartPosition = DEFAULT_START_POSITION;

    private List<UnitRuler> mUnitRulers = new LinkedList<>();

    private final int PRE_LOAD_NUM = 3;
    private LinearLayout rootLayout;
    private boolean initFlag = true;

    private Paint mPaint;
    private int mUnitRulerPx = -1;
    private Bitmap mBufferBitmap;
    private Canvas mBufferCanvas;
    public TimeHorizontalScrollView(Context context) {
        this(context, null);
    }

    public TimeHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
        mPaint = new Paint();
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
                for (int i = 0; i < PRE_LOAD_NUM; i++) {
                    addOneUnitRuler();
                }
                initFlag = false;
            }

        }
    }



    @Override
    protected void onDraw(Canvas canvas) {
        if (mBufferBitmap == null) {
            return;
        }
        canvas.drawBitmap(mBufferBitmap, mStartPosition,this.getMeasuredHeight() / 2 - 10, null);
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
        unitRuler.setRulerPosition(UnitRuler.RULER_BOTTOM);
        if (mUnitRulers.size() != 0) {
            unitRuler.setTickValue(TimeUtil.incrementBySecond(mUnitRulers.get(mUnitRulers.size() - 1).getTickValue()));
        } else {
            unitRuler.setTickValue("00:00");
        }
        mUnitRulers.add(unitRuler);
        if (initFlag) {
            bigPrecision = unitRuler.getSecondPrecision();
            smallPrecision = unitRuler.getMillisecondPrecision();
        }
        rootLayout.addView(unitRuler, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300));
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


    private int scrollToPx ;
    /**
     *
     * @param time 格式为mm:ss:SSS
     */
    public void moveTo(String time){
        if (mUnitRulerPx != -1) {
            float millisecondEachPx = ((float) (bigPrecision * 1000)) / mUnitRulerPx;
            scrollToPx = TimeUtil.convertSpecPrecision(time, millisecondEachPx, Calendar.MILLISECOND);
            int bigN = TimeUtil.convertSpecPrecision(time, bigPrecision, Calendar.SECOND);
            if (bigN - preBigN > 1) {
                for (int i = 0; i < bigN - preBigN; i++) {
                    addOneUnitRuler();
                }
                preBigN = bigN;
            }
            Log.d(TAG, "moveTo: " + scrollToPx);

            //双缓冲
            if (mBufferBitmap == null) {
                mBufferBitmap = Bitmap.createBitmap(1080 + PRE_LOAD_NUM * mUnitRulerPx, 20, Bitmap.Config.ARGB_4444);
                mBufferCanvas = new Canvas(mBufferBitmap);
            }
            mBufferCanvas.drawRect(scrollToPx, 0, 10 + scrollToPx, 20, mPaint);
            post(new Runnable() {
                @Override
                public void run() {
                    smoothScrollTo(scrollToPx, TimeHorizontalScrollView.this.getScrollY());
                    invalidate();
                }
            });

        }
    }
}
