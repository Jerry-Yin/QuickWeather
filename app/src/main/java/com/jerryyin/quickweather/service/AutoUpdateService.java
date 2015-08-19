package com.jerryyin.quickweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.jerryyin.quickweather.receiver.AutoUpdateReceiver;
import com.jerryyin.quickweather.util.HttpUtil;
import com.jerryyin.quickweather.util.OnResponseListener;
import com.jerryyin.quickweather.util.Utility;

/**
 * Created by JerryYin on 8/18/15.
 * 后台定时更新数据的服务类
 * 外部定时触发，通过线程获取数据，解析并存储到本地；
 */
public class AutoUpdateService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                upDateWeather();
            }
        }).start();

        //定时启动
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8*60*60*1000;  //8小时
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;    //触发时间

        Intent i = new Intent(AutoUpdateService.this, AutoUpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AutoUpdateService.this, 0, i, 0);

        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);

        return super.onStartCommand(intent, flags, startId);
    }


    /**
     *  更新天气信息
     */
    public void upDateWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weathweCode = preferences.getString("weather_code", "");
        String adress = "http://www.weather.com.cn/data/cityinfo/" + weathweCode + ".xml";
        HttpUtil.sendHttpRequest(adress, new OnResponseListener() {
            @Override
            public void onResponse(String response) {
                Utility.paraseWeatherResponse(AutoUpdateService.this, response);    // 数据解析完成后会存储到SharedPreference中
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

}
