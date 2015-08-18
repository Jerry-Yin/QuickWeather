package com.jerryyin.quickweather.util;

import android.text.TextUtils;

import com.jerryyin.quickweather.model.City;
import com.jerryyin.quickweather.model.County;
import com.jerryyin.quickweather.model.Province;
import com.jerryyin.quickweather.model.QuickWeatherDB;

/**
 * Created by JerryYin on 8/17/15.
 * 工具类来解析和处理服务器返回的数据（格式：“代号|城市,代号|城市”）
 */
public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(QuickWeatherDB quickWeatherDB, String response){
        if (!(TextUtils.isEmpty(response))){
            String[] allProvince = response.split(",");     //将数据从“,”处分割开，返回的是数组
            if (allProvince != null && allProvince.length > 0){
                for (String P : allProvince){
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
    public synchronized static boolean handleCitiesResponse(QuickWeatherDB quickWeatherDB, String response, int provinceId){
        if (!(TextUtils.isEmpty(response))){
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0){
                for (String c : allCities){
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
    public synchronized static boolean handleCountiesResponse(QuickWeatherDB quickWeatherDB, String response, int cityId){
        if (!(TextUtils.isEmpty(response))){
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0){
                for (String c : allCounties){
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

}
