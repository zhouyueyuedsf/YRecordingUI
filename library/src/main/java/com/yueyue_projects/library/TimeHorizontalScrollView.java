package com.yueyue_projects.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
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
    private int mStartPosition;
    /**
     * 可以通过继承获得该值
     */
    List<UnitRuler> mUnitRulers = new LinkedList<>();
    /**
     * HorizontalScrollView要求只有一个子节点，rootLayout
     */
    private LinearLayout rootLayout;
    private boolean initFlag = true;
    private Paint mPaint;
    private int mUnitRulerPx = -1;
    private float mMillisecondEachPx = 0;
    private SparseArray<RenderData> mFrameDatas = new SparseArray<>();
    private boolean mStopFlag = false;
    private ITextureRenderer mITextureRenderer;
    private int bigPrecision;
    private int preBigN = 0;
    private int scrollToPx = 0;
    private ParamsController mTimeScrollViewController;

    private TickStyle tickStyle;
    public TimeHorizontalScrollView(Context context) {
        this(context, null);
    }

    public TimeHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTimeScrollViewController = new TimeScrollViewController(context, attrs, defStyleAttr);
        mStartPosition = ((TimeScrollViewController) mTimeScrollViewController).startPosition;
        if (mTimeScrollViewController.tickStyle == TickStyle.NONE) {
            tickStyle = new TickStyle.None();
        } else if (mTimeScrollViewController.tickStyle == TickStyle.NUMBER) {
            tickStyle = new TickStyle.Number();
        } else if (mTimeScrollViewController.tickStyle == TickStyle.TIME) {
            tickStyle = new TickStyle.Time();
        } else {
            // 自定义实现
            if (tickStyle == null) throw new IllegalArgumentException("need run setTickStyle");
        }
        show();
    }

    /**
     * 自定义实现的时候必须调用该函数
     * @param tickStyle
     */
    public void setTickStyle(TickStyle tickStyle) {
        this.tickStyle = tickStyle;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = 0;
        if ((childCount = rootLayout.getChildCount()) >= 1) {
            int totalWidth = 0;
            for (int i = 0; i < childCount; i++) {
                View v = rootLayout.getChildAt(i);
                if (i == 1) {
                    mUnitRulerPx = v.getMeasuredWidth();
                }
                totalWidth += v.getMeasuredWidth();
            }
            if (totalWidth < ScreenUtil.getScreenWidthPix(this.getContext())) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        addOneUnitRuler();
                    }
                });
            } else {
                //预加载刻度
                if (initFlag) {
                    for (int i = childCount - 1; i < ((TimeScrollViewController)mTimeScrollViewController).initTickNum; i++) {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addOneUnitRuler();
                            }
                        },500);
                    }
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
            mPaint.setColor(((TimeScrollViewController)(mTimeScrollViewController)).defaultRectColor);
            for (int i = 0; i < mFrameDatas.size(); i++) {
                int key = mFrameDatas.keyAt(i);
                if (key >= startPx && key <= pivotPx) {
                    int vol = mFrameDatas.get(key).getVolume();
                    canvas.drawRect(key, this.getMeasuredHeight() / 2 - vol, key + 5, this.getMeasuredHeight() / 2 + vol, mPaint);
                }
            }
        }
        mPaint.setColor(((TimeScrollViewController)(mTimeScrollViewController)).pivotLineColor);
        canvas.drawRect(pivotPx - (((TimeScrollViewController)(mTimeScrollViewController)).pivotLineWidth / 2), 0,
                pivotPx + (((TimeScrollViewController)(mTimeScrollViewController)).pivotLineWidth / 2), mPivotLineHeight, mPaint);
    }


    public void show(){
        ensureRootLayout();
        setStartPosition();
        addOneUnitRuler();
        mPaint = new Paint();
    }



    private void addOneUnitRuler() {
        UnitRuler.StyleBuilder builder = new UnitRuler.StyleBuilder(this.getContext());
        String tickValue = "";
        if (mUnitRulers.size() != 0) {
            tickValue = tickStyle.increment(mUnitRulers.get(mUnitRulers.size() - 1).mParamsController.tickText, mTimeScrollViewController.secondPrecision);
        } else {
            tickValue = tickStyle.getInitTickValue();
        }
        builder.setTickValue(tickValue)
                .setSecondPrecision(mTimeScrollViewController.secondPrecision)
                .setMillisecondPrecision(mTimeScrollViewController.millisecondPrecision)
                .setMilliSecondIntervalSize(mTimeScrollViewController.milliSecondIntervalSize)
                .setTimeRulerPosition(mTimeScrollViewController.rulerPosition)
                .setTickValueColor(mTimeScrollViewController.tickValueColor)
                .setTickImageViewDrawable(mTimeScrollViewController.mainTickDrawableId, mTimeScrollViewController.otherTickDrawableId);
        UnitRuler unitRuler = (UnitRuler) builder.create();
        mUnitRulers.add(unitRuler);
        if (initFlag) {
            bigPrecision = unitRuler.getSecondPrecision();
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
            // 用layout的形式去设置起始位置
            rootLayout.addView(new LinearLayout(this.getContext()), 0,
                    new LinearLayout.LayoutParams(mStartPosition, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            throw new IllegalStateException("rootLayout mush be add before setStartPosition");
        }
    }

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

    public void setFrameData(RenderData renderData){
        mFrameDatas.put(TimeUtil.convertSpecPrecision(mCurTime, mMillisecondEachPx, Calendar.MILLISECOND) + mStartPosition, renderData);
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
    private int mLastPx = Integer.MAX_VALUE;

    public void stop(){
        mStopFlag = true;
        mLastPx = scrollToPx + mStartPosition;
    }

    public void setITextureRenderer(ITextureRenderer textureRenderer) {
        this.mITextureRenderer = textureRenderer;
    }


    private int curX = 0;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mStopFlag) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    curX = (int) ev.getRawX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int delaX = (int) (ev.getRawX() - curX);
                    curX = (int) ev.getRawX();
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
                    smoothScrollTo(scrollToPx, this.getScrollY());
                    return true;
            }
        }
        return super.onTouchEvent(ev);
    }

    public void setPivotLineColor(@ColorInt int color){
        ((TimeScrollViewController)mTimeScrollViewController).pivotLineColor = color;
        invalidate();
    }


    public List<UnitRuler> getUnitRulers() {
        return mUnitRulers;
    }
}
