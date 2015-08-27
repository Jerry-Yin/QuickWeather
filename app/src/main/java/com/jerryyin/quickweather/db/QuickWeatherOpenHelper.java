package com.jerryyin.quickweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JerryYin on 8/17/15.
 */
public class QuickWeatherOpenHelper extends SQLiteOpenHelper {

    /**
     * Province 省份 表构建语句
     */
    public static final String CREATE_PROVINCE = "create table Province(id integer primary key autoincrement, " +
                                                    "province_name text, " +
                                                    "province_code text)";
    /**
     * City 城市 表构建语句
     */
    public static final String CREATE_CITY = "create table City(id integer primary key autoincrement, " +
                                                    "city_name text, " +
                                                    "city_code text, " +
                                                    "province_id integer)";
    /**
     * County 乡镇 表构建语句
     */
    public static final String CREATE_COUNTY = "create table County(id integer primary key autoincrement, " +
                                                    "county_name text, " +
                                                    "county_code text, " +
                                                    "city_id integer)";

    /**
     *  用于存储 添加城市 的 表构建语句
     */
    public static final String CREATE_LOCALCITIES = "create table LocalCities(id integer primary key autoincrement, county_name text, county_code text)";


    public QuickWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
        db.execSQL(CREATE_LOCALCITIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }






}
