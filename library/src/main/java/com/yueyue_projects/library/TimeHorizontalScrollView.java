package com.yueyue_projects.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
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

public class TimeHorizontalScrollView extends HorizontalScrollView implements IBuilderParam {
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

    private ITextureRenderer mITextureRenderer;

    public TimeHorizontalScrollView(Context context) {
        this(context, null);
    }

    public TimeHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        timeScrollViewController = new TimeScrollViewController(context, attrs, defStyleAttr);
        show();
    }

    @SuppressLint("DrawAllocation")
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

            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            if (heightMode == MeasureSpec.EXACTLY) {
                int height = MeasureSpec.getSize(heightMeasureSpec);
                mPivotLineHeight = height - mUnitRulers.get(0).getRenderComponentsTotalHeight();
            }
        }


    }
    private int mPivotLineHeight = 0;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int startPx = 0;
        int pivotPx = 0;
        int endPx = 0;
        if (scrollToPx > ScreenUtil.getScreenWidthPix(this.getContext()) - mStartPosition) {
            startPx = scrollToPx;
            pivotPx = startPx + mStartPosition;
        } else {
            startPx = mStartPosition;
            pivotPx = scrollToPx + startPx;
        }
        endPx = pivotPx + ScreenUtil.getScreenWidthPix(this.getContext()) - mStartPosition;

        if (mITextureRenderer != null) {
            mITextureRenderer.draw(canvas, startPx, pivotPx, endPx, mPivotLineHeight, mFrameDatas);
        } else {
            mPaint.setColor(((TimeScrollViewController)(timeScrollViewController)).defaultRectColor);
            for (int i = 0; i < mFrameDatas.size(); i++) {
                int key = mFrameDatas.keyAt(i);
                if (key >= startPx && key <= pivotPx) {
                    int vol = mFrameDatas.get(key);
                    canvas.drawRect(key, this.getMeasuredHeight() / 2 - vol, key + 5, this.getMeasuredHeight() / 2 + vol, mPaint);
                }
            }
        }
        mPaint.setColor(((TimeScrollViewController)(timeScrollViewController)).pivotLineColor);
        canvas.drawRect(pivotPx - (((TimeScrollViewController)(timeScrollViewController)).pivotLineWidth / 2), 0,
                pivotPx + (((TimeScrollViewController)(timeScrollViewController)).pivotLineWidth / 2), mPivotLineHeight, mPaint);
    }


    public void show(){
        ensureRootLayout();
        setStartPosition();
        addOneUnitRuler();
        mPaint = new Paint();
    }


    private int bigPrecision;
    private int smallPrecision;
    private void addOneUnitRuler() {
        UnitRuler.StyleBuilder builder = new UnitRuler.StyleBuilder(this.getContext());
        String tickValue = "00:00";
        if (mUnitRulers.size() != 0) {
            tickValue = TimeUtil.incrementBySecond(mUnitRulers.get(mUnitRulers.size() - 1).mParamsController.tickText, timeScrollViewController.secondPrecision);
        }
        builder.setTickValue(tickValue)
                .setSecondPrecision(timeScrollViewController.secondPrecision)
                .setMillisecondPrecision(timeScrollViewController.millisecondPrecision)
                .setMilliSecondIntervalSize(timeScrollViewController.milliSecondIntervalSize)
                .setTimeRulerPosition(timeScrollViewController.rulerPosition)
                .setTickValueColor(timeScrollViewController.tickValueColor)
                .setTickImageViewDrawable(timeScrollViewController.mainTickDrawableId, timeScrollViewController.otherTickDrawableId);
        UnitRuler unitRuler = (UnitRuler) builder.create();
        mUnitRulers.add(unitRuler);
        if (initFlag) {
            bigPrecision = unitRuler.getSecondPrecision();
            smallPrecision = unitRuler.getMillisecondPrecision();
        }
        rootLayout.addView(unitRuler, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void addRootLayout(){
        if (rootLayout == null) {
            rootLayout = new LinearLayout(this.getContext());
            rootLayout.setOrientation(LinearLayout.HORIZONTAL);
            this.addView(rootLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
                    new LinearLayout.LayoutParams(ScreenUtil.getScreenWidthPix(this.getContext()) - mStartPosition, ViewGroup.LayoutParams.MATCH_PARENT));
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


    private String mCurTime = "00:00:000";
    long oldTime = 0;
    /**
     * 开启记录
     */
    public void start(){
        //设置不可滚动
        final Timer timer = new Timer();
        oldTime = System.currentTimeMillis();
        mStopFlag = false;
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

    /**
     * 录音停止时，所在的位置
     */
    private int mLastPx = 0;

    public void stop(){
        mStopFlag = true;
        mLastPx = scrollToPx + mStartPosition;
    }

    /**
     * 设置渲染间距
     * @param interval
     */
    public void setInterval(int interval){

    }

    public void setITextureRenderer(ITextureRenderer textureRenderer) {
        this.mITextureRenderer = textureRenderer;
    }


    private int curX = 0;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                curX = (int) ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                int delaX = (int) (ev.getRawX() - curX);
                curX = (int) ev.getRawX();
                Log.d(TAG, "onTouchEvent: delatX: " + delaX);
                scrollToPx -= delaX;
                if (scrollToPx + mStartPosition >= mLastPx) {
                    if (delaX <= 0) {
                        scrollToPx = mLastPx - mStartPosition;
                        smoothScrollTo(scrollToPx, this.getScrollY());
                        return false;
                    }
                }
                if (scrollToPx <= 0) {
                    if (delaX >= 0) {
                        scrollToPx = 0;
                        smoothScrollTo(scrollToPx, this.getScrollY());
                        return false;
                    }
                }
                Log.d(TAG, "onTouchEvent: scrollPx: " + scrollToPx);
                smoothScrollTo(scrollToPx, this.getScrollY());
                return true;
        }
        return true;
    }

//    @Override
//    public void fling(int velocityX) {
//        //重写fling方法，将速度除以三，减缓其滑动速度
//        super.fling(velocityX / 100);
//    }

    private ParamsController timeScrollViewController;
//    public static class StyleBuilder extends UnitRuler.StyleBuilder {
//
//        public StyleBuilder(Context context) {
//            super(context);
//            P = new TimeScrollViewController.TimeScrollParams(context);
//        }
//
//        @Override
//        public IBuilderParam create() {
//            TimeHorizontalScrollView scrollView = new TimeHorizontalScrollView(context);
//            //自己设置的值
//            P.apply(scrollView.timeScrollViewController);
//            scrollView.show();
//            return scrollView;
//        }
//    }

}
