package com.jerryyin.quickweather.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by JerryYin on 8/25/15.
 */
public class GetCurrentDate {

    public static String mYear;
    public static String mMonth;
    public static String mDay;
    public static String mWay;

    public GetCurrentDate(){
        getDate();
    }

    public void getDate(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWay)){
            mWay ="星期天";
        }else if("2".equals(mWay)){
            mWay ="星期一";
        }else if("3".equals(mWay)){
            mWay ="星期二";
        }else if("4".equals(mWay)){
            mWay ="星期三";
        }else if("5".equals(mWay)){
            mWay ="星期四";
        }else if("6".equals(mWay)){
            mWay ="星期五";
        }else if("7".equals(mWay)){
            mWay ="星期六";
        }
    }


    // 获取系统当前的时间
//    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月d日  HH:mm:ss", Locale.CHINA);
//    Date date = new Date(System.currentTimeMillis());
//    String currentDate = dateFormat.format(date);
}
