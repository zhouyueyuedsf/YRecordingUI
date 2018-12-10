package com.yueyue_projects.yrecordingui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;

import com.yueyue_projects.library.ITextureRenderer;
import com.yueyue_projects.library.TimeHorizontalScrollView;
import com.yueyue_projects.library.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TimeHorizontalScrollView scrollView = (TimeHorizontalScrollView)findViewById(R.id.time_scroll_view);
        scrollView.setITextureRenderer(new ITextureRenderer() {
            @Override
            public void draw(Canvas canvas, int renderStartPx, int renderEndPx, SparseArray<Integer> renderDatas) {
                int h = canvas.getHeight();
                for (int i = 0; i < renderDatas.size(); i++) {
                    int key = renderDatas.keyAt(i);
                    if (key >= renderStartPx && key <= renderEndPx) {
                        int vol = renderDatas.get(key);
                        canvas.drawRect(key, h / 2 - vol, key + 5, h / 2 + vol, new Paint());
                    }
                }
            }
        });
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.start();
                long t = 0;
                for (int i = 0; i < 500; i++) {
                    scrollView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.setFrameData((int) (Math.random() * 11) + 10);
                        }
                    }, t);
                    t += 20;
                }
                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.stop();
                    }
                }, 10 * 1000);
            }
        }, 1000);
    }

//    Calendar calendar = Calendar.getInstance();
//    public String increment(String time) {
//        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:SSS", Locale.CHINA);
//        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
//        calendar.setTime(TimeUtil.String2Date(time, formatter));
//        calendar.add(Calendar.MILLISECOND, 100);
//        return TimeUtil.Date2String(calendar.getTime(), formatter);
//    }
}
