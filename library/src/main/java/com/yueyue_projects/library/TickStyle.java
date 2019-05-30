package com.yueyue_projects.library;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static com.yueyue_projects.library.TimeUtil.String2Date;

public interface TickStyle {
    int NONE = 0;
    int NUMBER = 1;
    int TIME = 2;
    int OTHER = 3;

    /**
     * 刻度增加一个单位的接口
     * @param value 基本值
     * @param amount 一个单位增加多少
     * @return
     */
    String increment(String value, int amount);

    String getInitTickValue();

    final class None implements TickStyle {

        @Override
        public String increment(String value, int amount) {
            return null;
        }

        @Override
        public String getInitTickValue() {
            return null;
        }
    }

    final class Number implements TickStyle {

        @Override
        public String increment(String value, int amount) {
            if (value == null) throw new IllegalStateException("value can not null for number");
            return String.valueOf(Integer.valueOf(value) + amount);
        }

        @Override
        public String getInitTickValue() {
            return "0";
        }
    }

    final class Time implements TickStyle {
        private static Calendar calendar = Calendar.getInstance();

        @Override
        public String increment(String time, int amount) {
            SimpleDateFormat formatter = new SimpleDateFormat("mm:ss", Locale.CHINA);
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            calendar.setTime(String2Date(time, formatter));
            calendar.add(Calendar.SECOND, amount);
            return formatter.format(calendar.getTime());
        }

        @Override
        public String getInitTickValue() {
            return "00:00";
        }
    }

    abstract class Other implements TickStyle {

        @Override
        public abstract String increment(String value, int amount);

        @Override
        public abstract String getInitTickValue();
    }
}
