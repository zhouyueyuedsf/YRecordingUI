package com.yueyue_projects.library;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtil {


    private static Calendar calendar = Calendar.getInstance();

    private static final String PATTERN = "mm:ss:SSS";

    public static Date String2Date(String str){
        SimpleDateFormat formatter = new SimpleDateFormat(PATTERN, Locale.CHINA);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return formatter.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date String2Date(String str, SimpleDateFormat formatter){
        try {
            return formatter.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String Date2String(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat(PATTERN, Locale.CHINA);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(date);
    }

    public static String Date2String(Date date, SimpleDateFormat formatter){
        return formatter.format(date);
    }


    public static String incrementBySecond(String time, int amount) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss", Locale.CHINA);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTime(String2Date(time, formatter));
        calendar.add(Calendar.SECOND, amount);
        return Date2String(calendar.getTime(), formatter);
    }

    public static String incrementByMill(String time, int amount) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:SSS", Locale.CHINA);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTime(TimeUtil.String2Date(time, formatter));
        calendar.add(Calendar.MILLISECOND, amount);
        return TimeUtil.Date2String(calendar.getTime(), formatter);
    }

    /**
     * 计算n = time / precisionValue ;
     * @param time
     * @param precisionValue 精度值，以ms为单位
     */
    public static int convertSpecPrecision(String time, float precisionValue, int precision) {
        if (precision == Calendar.SECOND) {
            precisionValue *= 1000;
        }
        Date date = String2Date(time);
        if (date != null) {
            return (int) (date.getTime() / precisionValue);
        }
        return -1;
    }

}
