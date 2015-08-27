package com.jerryyin.quickweather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jerryyin.quickweather.db.QuickWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JerryYin on 8/17/15.
 * 把一些常用的数据库 操作封装起来,以方便我们后面使用
 * 单例类
 * 我们将它的构造方法私有化,并提供了一个 getInstance()方法来获取 QuickWeatherDB 的实例,这样就可以保证全局范围内只会有一个 QucickWeatherDB 的实例。
 */
public class QuickWeatherDB {

    /**
     * 数据库名
     */
    public static final String DB_NAME = "quick_weather";

    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    private static QuickWeatherDB quickWeatherDB;
    private SQLiteDatabase db;


    /**
     * 构造方法私有化
     */
    private QuickWeatherDB(Context context) {
        QuickWeatherOpenHelper dbHelper = new QuickWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取QuickWeatherDB的实例。
     * synchronized 同步化，避免多个线程竞争关系产生的错误
     */
    public synchronized static QuickWeatherDB getInstence(Context context) {
        if (quickWeatherDB == null) {
            quickWeatherDB = new QuickWeatherDB(context);
        }
        return quickWeatherDB;
    }


    /**
     * 将Province实例存储到数据库。
     */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues cv = new ContentValues();
            cv.put("province_name", province.getProvinceName());
            cv.put("province_code", province.getProvinceCode());
            db.insert("Province", null, cv);
        }
    }

    /**
     * 从数据库读取全国所有的省份信息。
     */
    public List<Province> loadProvince() {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将City实例存储到数据库。
     */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues cv = new ContentValues();
            cv.put("city_name", city.getCityName());
            cv.put("city_code", city.getCityCode());
            cv.put("province_id", city.getProvinceId());
            db.insert("City", null, cv);
        }
    }

    /**
     * 从数据库读取某个省份下所有的城市信息。
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将County实例存储到数据库。
     */
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues cv = new ContentValues();
            cv.put("county_name", county.getCountyName());
            cv.put("county_code", county.getCountyCode());
            cv.put("city_id", county.getCityId());
            db.insert("County", null, cv);
        }
    }

    /**
     * 从数据库读取某个城市下所有的县信息。
     */
    public List<County> loadCounties(int cityId) {
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                list.add(county);
            } while (cursor.moveToNext());
        }
        return list;
    }


    /**
     * 将添加的 我的城市(城市名字＋代号) 存储到数据库， 会一次增加id值
     */
    public void saveLocalCities(County city) {
        if (city != null) {
            ContentValues cv = new ContentValues();
            cv.put("county_name", city.getCountyName());
            cv.put("county_code", city.getCountyCode());
            db.insert("LocalCities", null, cv);
        }
    }

    /**
     * 删除 已添加城市
     * 通过 城市名字 查询到并且删除
     */
    public void deleteLocalCities(String countyName) {
        db.delete("LocalCities", "county_name = ?", new String[]{countyName});
    }

    /**
     * 遍历查询当前的 所有 已添加城市
     */
    public List<County> queryLocalCities() {
        List<County> localList = new ArrayList<County>();

        Cursor cursor = db.query("LocalCities", null, null, null, null, null, null);
        //遍历数据库 “LocalCities”表中 所有数据
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                String countyCode = cursor.getString(cursor.getColumnIndex("county_code"));
                String countyName = cursor.getString(cursor.getColumnIndex("county_name"));
                county.setCountyCode(countyCode);
                county.setCountyName(countyName);
                localList.add(county);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return localList;
    }
}
