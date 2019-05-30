package com.yueyue_projects.yrecordingui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.util.SparseArray;
import android.view.View;

import com.yueyue_projects.library.ITextureRenderer;
import com.yueyue_projects.library.RenderData;
import com.yueyue_projects.library.TimeHorizontalScrollView;
import com.yueyue_projects.library.TimeUtil;
import com.yueyue_projects.library.UnitRuler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Paint paint = new Paint();

        final TimeHorizontalScrollView scrollView = (TimeHorizontalScrollView)findViewById(R.id.time_scroll_view);
        final AppCompatImageView imageView = findViewById(R.id.iv_change_input_mode);
        final AppCompatEditText editText = findViewById(R.id.et_choose_tomato_num);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = editText.getVisibility();
                editText.setVisibility(scrollView.getVisibility());
                scrollView.setVisibility(a);

            }
        });
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                final List<UnitRuler> unitRulerList = scrollView.getUnitRulers();
                for (int i = 0; i < unitRulerList.size(); i++) {
                    final UnitRuler unitRuler = unitRulerList.get(i);
                    final int index = i;
                    unitRuler.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            unitRuler.setMainTickDrawable(R.drawable.ic_tomato_checked_timer_32dp);
                            for (int j = 0; j < unitRulerList.size(); j++) {
                                if (j <= index) {
                                    unitRulerList.get(j).setMainTickDrawable(R.drawable.ic_tomato_checked_timer_32dp);
                                } else {
                                    unitRulerList.get(j).setMainTickDrawable(R.drawable.ic_tomato_unchecked_timer_32dp);
                                }
                            }
                        }
                    });
                }
            }
        }, 1500);

//        scrollView.show();
//        scrollView.setPivotLineColor(Color.GREEN);
//
//        scrollView.setITextureRenderer(new ITextureRenderer() {
//            @Override
//            public void draw(Canvas canvas, int renderStartPx, int renderPivotPx, int renderEndPx, int renderHeight, SparseArray<RenderData> renderDatas) {
//                for (int i = 0; i < renderDatas.size(); i++) {
//                    int key = renderDatas.keyAt(i);
//                    if (key >= renderStartPx && key <= renderPivotPx) {
//                        int vol = renderDatas.get(key).getVolume();
//                        paint.setColor(Color.WHITE);
//                        canvas.drawRect(key, renderHeight/ 2 - vol, key + 2, renderHeight / 2 + vol, paint);
//                    } else if (key > renderPivotPx && key <= renderEndPx) {
//                        int vol = renderDatas.get(key).getVolume();
//                        paint.setColor(Color.RED);
//                        canvas.drawRect(key, renderHeight / 2 - vol, key + 2, renderHeight / 2 + vol, paint);
//                    };
//                }
//            }
//        });
//
//
//        scrollView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                scrollView.start();
//                long t = 0;
//                for (int i = 0; i < 500; i++) {
//                    scrollView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            scrollView.setFrameData(new MyRenderData((int) (Math.random() * 100)));
//                        }
//                    }, t);
//                    t += 20;
//                }
//                scrollView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        scrollView.stop();
//                    }
//                }, 10 * 1000);
//            }
//        }, 1000);

    }

}
