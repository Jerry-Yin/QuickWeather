package com.jerryyin.quickweather.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.jerryyin.quickweather.model.City;
import com.jerryyin.quickweather.model.County;
import com.jerryyin.quickweather.model.Province;
import com.jerryyin.quickweather.model.QuickWeatherDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by JerryYin on 8/17/15.
 * 工具类来解析和处理服务器返回的数据（格式：“代号|城市,代号|城市”）
 */
public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(QuickWeatherDB quickWeatherDB, String response) {
        if (!(TextUtils.isEmpty(response))) {
            String[] allProvince = response.split(",");     //将数据从“,”处分割开，返回的是数组
            if (allProvince != null && allProvince.length > 0) {
                for (String P : allProvince) {
                    String[] array = P.split("\\|");        //将数据从“｜”处分割，返回的是数组
                    Province province = new Province();
                    province.setProvinceName(array[1]);
                    province.setProvinceCode(array[0]);
                    // 将解析出来的数据存储到Province表
                    quickWeatherDB.saveProvince(province);
                }
                return true;
            }
        }

        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public synchronized static boolean handleCitiesResponse(QuickWeatherDB quickWeatherDB, String response, int provinceId) {
        if (!(TextUtils.isEmpty(response))) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityName(array[1]);
                    city.setCityCode(array[0]);
                    city.setProvinceId(provinceId);
                    // 将解析出来的数据存储到City表
                    quickWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }


    /**
     * 解析和处理服务器返回的县级数据
     */
    public synchronized static boolean handleCountiesResponse(QuickWeatherDB quickWeatherDB, String response, int cityId) {
        if (!(TextUtils.isEmpty(response))) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyName(array[1]);
                    county.setCountyCode(array[0]);
                    county.setCityId(cityId);
                    // 将解析出来的数据存储到County表
                    quickWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }


    /**
     * 服务器返回的 天气数据 格式：
     * {"weatherinfo": {"city":"昆山", "cityid":"101190404", "temp1":"21°C", "temp2":"9°C",  "weather":"多云转小雨", "img1":"d1.gif", "img2":"n7.gif", "ptime":"11:00"}
     * }
     * 解析服务器返回的JSON数据,并将数据存储到本地
     */
    public static void paraseWeatherResponse(Context context, String response) {
        try {
            JSONObject object = new JSONObject(response);
            JSONObject weatherInfo = object.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
//            Bitmap image1 = (Bitmap) weatherInfo.get("img1");
//            Bitmap image2 = (Bitmap) weatherInfo.get("img2");
            Bitmap image1 = null;
            Bitmap image2 = null;

            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime, image1, image2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     *  将服务器返回的所有天气信息存储到SharedPreferences文件中。
     */
    public static void saveWeatherInfo(Context context, String cityName, String weatherCode,
                                       String temp1, String temp2, String weatherDesp,
                                       String publishTime, Bitmap image1, Bitmap iamge2){

//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月d日  HH:mm:ss", Locale.CHINA);
//        Date date = new Date(System.currentTimeMillis());
//        String currentDate = dateFormat.format(date);       //获取系统当前时间

        GetCurrentDate date = new GetCurrentDate();
//        String currentDate = date.mYear + "年" + date.mMonth + "月" + date.mDay + "日";
        String currentDate = date.mMonth + "／" + date.mDay;
        String currentWeekend = date.mWay;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("city_selected", true);   //标志位,以此来辨别当前是否已经选中了一个城市
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1.substring(0, temp1.length()-1));    // 此处温度是带 ℃ 单位的；占一个字符长度
        editor.putString("temp2", temp2.substring(0, temp2.length()-1));    //temp1.substring(0, temp1.length()-1)，去掉最后一个字符再显示
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", currentDate);
        editor.putString("current_weekend", currentWeekend);
//        editor.put
        editor.commit();
    }

//    /**
//     *  添加选中的城市到本地（我的城市）
//     */
//    public static void saveCityToLocal(Context context, String countyName){
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = preferences.edit();
////        ContentValues cv = new ContentValues();
////        cv.put();
//        editor.putString("saved_city", countyName);
//        editor.commit();
//    }
}
