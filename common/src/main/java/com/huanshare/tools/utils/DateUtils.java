package com.huanshare.tools.utils;

import org.apache.commons.lang3.time.FastDateFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

    public static String FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String FORMAT_NOT_TIME = "yyyy-MM-dd";
    public static String FORMAT_LONG = "yyyyMMddHHmmssms";
    public static String FORMAT_SHORT = "yyMMdd";
    public static String YEAR_MONTH = "yyyy-MM";

    public static String getNow(Date date) {
        return FastDateFormat.getInstance(FORMAT).format(date);
    }

    public static String getLongFormat(Date date) {
        return FastDateFormat.getInstance(FORMAT_LONG).format(date);
    }

    public static String getShortFormat(Date date) {
        return FastDateFormat.getInstance(FORMAT_LONG).format(date);
    }

    public static Date stringToDate(String str) {
        return stringToDate(str, FORMAT_NOT_TIME);
    }

    public static Date stringToDate(String str, String dateFormat) {
        DateFormat format = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateToString(Date dateVal) {
        return dateToString(dateVal, FORMAT);
    }

    public static String dateToString(Date dateVal, String format) {
        if (dateVal != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(dateVal);
        } else {
            return "";
        }
    }

    public static String getDateByCalendar(Calendar calendar, String format) {
        return FastDateFormat.getInstance(format).format(calendar);
    }

    public static String getPrevMonths(String format, Integer prevMons) {
        Calendar calendar = Calendar.getInstance();
        StringBuffer result = new StringBuffer(getDateByCalendar(calendar, format));
        // 获取当前月的前6个月，已默认添加了一个月
        for (int i = 0; i < prevMons; i++) {
            calendar.add(Calendar.MONTH, -1);
            result.append("," + getDateByCalendar(calendar, format));
        }
        return result.substring(0, result.length());
    }

    //增加，type传Calendar.DAY_OF_MONTH之类的枚举
    public static Date add(Date date, Integer type, Integer day) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(type, day);
        return calendar.getTime();
    }


}
