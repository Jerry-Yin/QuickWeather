package com.jerryyin.quickweather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jerryyin.quickweather.R;
import com.jerryyin.quickweather.util.HttpUtil;
import com.jerryyin.quickweather.util.OnResponseListener;
import com.jerryyin.quickweather.util.Utility;

/**
 * Created by JerryYin on 8/18/15.
 */
public class WeatherInfoActivity extends Activity {

    /**Constants*/

    /**
     * Views
     */
    private TextView mtvCityName;       //城市名字
    private TextView mtvPublish;        //发布时间
    private TextView mtvWeatherDesp;     //天气信息
    private TextView mtvCurrentDate;      //当前时间
    private TextView mtvTemp1, mtvTemp2;//温度
    private RelativeLayout mWeaLaoyout;
    private Button mbtnChangeCity;      //切换城市按钮
    private Button mbtnRefreshWer;      //更新天气

    /**
     * Values
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        initViews();
        initData();
    }

    public void initViews() {
        mtvCityName = (TextView) findViewById(R.id.tv_city_name);
        mtvPublish = (TextView) findViewById(R.id.tv_pulish_time);
        mtvWeatherDesp = (TextView) findViewById(R.id.tv_weather_desp);
        mtvCurrentDate = (TextView) findViewById(R.id.tv_current_date);
        mtvTemp1 = (TextView) findViewById(R.id.tv_temp1);
        mtvTemp2 = (TextView) findViewById(R.id.tv_temp2);
        mWeaLaoyout = (RelativeLayout) findViewById(R.id.weather_layout);
    }

    public void initData() {
        String countyCode = getIntent().getStringExtra("county_code");
        if (!(TextUtils.isEmpty(countyCode))) {
            // 有县级代号时就去查询天气
            mtvPublish.setText("同步中...");
            mWeaLaoyout.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            // 没有县级代号时就直接显示本地天气
            showWeather();
        }
    }

    /**
     * 查询县级代号所对应的天气代号
     * 示例：北京－－010101|101010100
     */
    private void queryWeatherCode(String countyCode) {
        String adress = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(adress, "countyCode");
    }

    /**
     * 查询天气代号所对应的天气。
     * 示例：
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

    /**
     * 根据传入的地址和类型去向服务器查询 天气代号 或者 天气信息。
     */
    private void queryFromServer(final String adress, final String codeType) {
        HttpUtil.sendHttpRequest(adress, new OnResponseListener() {
            //服务器请求成功后会回调此方法
            @Override
            public void onResponse(String response) {
                if ("countyCode".equals(codeType)) {
                    // 从服务器返回的数据中解析出天气代号
                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }

                    }
                } else if ("weatherCode".equals(codeType)) {
                    //  处理服务器返回的天气信息
                    if (!TextUtils.isEmpty(response)) {
                        Utility.paraseWeatherResponse(WeatherInfoActivity.this, response);  //解析天气数据
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showWeather();
                            }
                        });
                    }
                }
            }

            //服务器请求失败
            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() { mtvPublish.setText("同步失败");
                    } });

            }
        });
    }

    /**
     *  显示信息，在主线程中完成 ，需要 拿取 已经解析完成并存储到SharedPreference中的数据
     */
    public void showWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mtvPublish.setText("今天" + preferences.getString("publish_time","") + "发布：");
        mtvCityName.setText(preferences.getString("city_name", ""));
        mtvWeatherDesp.setText(preferences.getString("weather_desp", ""));
        mtvTemp1.setText(preferences.getString("temp1", ""));
        mtvTemp2.setText(preferences.getString("temp2", ""));
        mtvCurrentDate.setText(preferences.getString("current_date", ""));


        mWeaLaoyout.setVisibility(View.VISIBLE);
    }

}

