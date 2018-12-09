package com.yueyue_projects.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    private float mMillisecondEachPx = 0;
    private SparseArray<Integer> mFrameDatas = new SparseArray<>();
    private boolean mStopFlag = false;

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
             mMillisecondEachPx = ((float) (bigPrecision * 1000)) / mUnitRulerPx;
        }
    }



    @Override
    protected void onDraw(Canvas canvas) {
        int startPx = 0;
        int endPx = 0;
        if (scrollToPx > ScreenUtil.getScreenWidthPix(this.getContext()) - mStartPosition) {
            startPx = scrollToPx;
            endPx = startPx + mStartPosition;
        } else {
            startPx = mStartPosition;
            endPx = scrollToPx + startPx;
        }

        for (int i = 0; i < mFrameDatas.size(); i++) {
            int key = mFrameDatas.keyAt(i);
            if (key >= startPx && key <= endPx) {
                int vol = mFrameDatas.get(key);
                canvas.drawRect(key, this.getMeasuredHeight() / 2 - vol, key + 5, this.getMeasuredHeight() / 2 + vol, mPaint);
            }
        }
//        for (int i = startPx; i < endPx; i++) {
//            Integer vol = 0;
//            if ((vol = mFrameDatas.get(i)) != null) {
//                canvas.drawRect(i, this.getMeasuredHeight() / 2 - vol, i + 5, this.getMeasuredHeight() / 2 + vol, mPaint);
//            }
//        }
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


    private int scrollToPx = 0;

    private void moveTo(String time){
        if (mUnitRulerPx != -1) {
            scrollToPx = TimeUtil.convertSpecPrecision(time, mMillisecondEachPx, Calendar.MILLISECOND);
            int bigN = TimeUtil.convertSpecPrecision(time, bigPrecision, Calendar.SECOND);
            if (bigN - preBigN > 1) {
                for (int i = 0; i < bigN - preBigN; i++) {
                    addOneUnitRuler();
                }
                preBigN = bigN;
            }
            Log.d(TAG, "moveTo: " + mCurTime);
            smoothScrollTo(scrollToPx, TimeHorizontalScrollView.this.getScrollY());
        }
    }

    /**
     * 该帧所在的时间点和音量
     * @param time 格式为mm:ss:SSS
     * @param volumeValue (0 - 100)
     */
    public void setFrameData(String time, int volumeValue){
        mFrameDatas.put(TimeUtil.convertSpecPrecision(time, mMillisecondEachPx, Calendar.MILLISECOND) + mStartPosition, volumeValue);
    }

    public void setFrameData(int volumeValue){
        int px = TimeUtil.convertSpecPrecision(mCurTime, mMillisecondEachPx, Calendar.MILLISECOND);
        mFrameDatas.put(px + mStartPosition, volumeValue);
        invalidate();
    }


    String mCurTime = "00:00:000";
    long oldTime = 0;
    /**
     * 开启记录
     */
    public void start(){
        final Timer timer = new Timer();
        oldTime = System.currentTimeMillis();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long curTime = System.currentTimeMillis();
                mCurTime = TimeUtil.incrementByMill(mCurTime, (int) (curTime - oldTime));
                oldTime = curTime;
                post(new Runnable() {
                    @Override
                    public void run() {
                        moveTo(mCurTime);
                    }
                });
                if (mStopFlag) {
                    timer.cancel();
                }
            }
        }, 0, 20);
    }

    public void stop(){
        mStopFlag = true;
    }

    /**
     * 设置渲染间距
     * @param interval
     */
    public void setInterval(int interval){

    }

//    public void convertTimeByPx(int leftPx){
//        leftPx -= mStartPosition;
//        // 秒
//        int curUnit = leftPx / mUnitRulerPx;
//        int[] coefficients = {60, 60, 24};
//        int i = 0;
//        while(curUnit > coefficients[i]) {
//            i++;
//            curUnit = curUnit / coefficients[i];
//        };
//
//    }
}
