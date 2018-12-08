package com.yueyue_projects.yrecordingui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                String time = "00:00:000";
                long t = 0;
                for (int i = 0; i < 2000; i++) {
                    time = increment(time);
                    t += 5;
                    final String finalTime = time;
                    scrollView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.moveTo(finalTime);
                        }
                    }, t);

                }
            }
        }, 1000);

    }
    Calendar calendar = Calendar.getInstance();
    public String increment(String time) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:SSS", Locale.CHINA);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTime(TimeUtil.String2Date(time, formatter));
        calendar.add(Calendar.MILLISECOND, 5);
        return TimeUtil.Date2String(calendar.getTime(), formatter);
    }
}
