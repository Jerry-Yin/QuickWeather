package com.jerryyin.quickweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jerryyin.quickweather.R;
import com.jerryyin.quickweather.service.AutoUpdateService;
import com.jerryyin.quickweather.util.HttpUtil;
import com.jerryyin.quickweather.util.OnResponseListener;
import com.jerryyin.quickweather.util.Utility;

/**
 * Created by JerryYin on 8/18/15.
 */
public class WeatherInfoActivity extends Activity implements View.OnClickListener {

    /**Constants*/

    /**Views*/
    private View mWeatherView;
    private TextView mtvCityName;       //城市名字
    private TextView mtvPublish;        //发布时间
    private TextView mtvWeatherDesp;     //天气信息
    private TextView mtvCurrentDate;      //当前时间
    private TextView mtvTemp1, mtvTemp2;//温度
    private RelativeLayout mWeaLaoyout;
    private ImageView mbtnChangeCity;      //切换城市按钮
    private ImageView mbtnRefreshWer;      //更新天气

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
        mWeatherView = (View) findViewById(R.id.weather_view);
//        mWeatherView.getBackground().setAlpha(15);
        mtvCityName = (TextView) findViewById(R.id.tv_city_name);
        mtvPublish = (TextView) findViewById(R.id.tv_pulish_time);
        mtvWeatherDesp = (TextView) findViewById(R.id.tv_weather_desp);
        mtvCurrentDate = (TextView) findViewById(R.id.tv_current_date);
        mtvTemp1 = (TextView) findViewById(R.id.tv_temp1);
        mtvTemp2 = (TextView) findViewById(R.id.tv_temp2);
        mWeaLaoyout = (RelativeLayout) findViewById(R.id.weather_layout);
        mbtnChangeCity = (ImageView) findViewById(R.id.btn_home_page);
        mbtnRefreshWer = (ImageView) findViewById(R.id.btn_refresh);
        mbtnRefreshWer.setOnClickListener(this);
        mbtnChangeCity.setOnClickListener(this);
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
                    public void run() {
                        mtvPublish.setText("同步失败");
                        mbtnRefreshWer.setImageResource(R.drawable.img_refresh_normal);
                    }
                });

            }
        });
    }

    /**
     * 显示信息，在主线程中完成 ，需要 拿取 已经解析完成并存储到SharedPreference中的数据
     */
    public void showWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mtvPublish.setText("今天" + preferences.getString("publish_time", "") + "发布：");
        mtvCityName.setText(preferences.getString("city_name", ""));
        mtvWeatherDesp.setText(preferences.getString("weather_desp", ""));
        mtvTemp1.setText(preferences.getString("temp1", ""));
        mtvTemp2.setText(preferences.getString("temp2", ""));
        mtvCurrentDate.setText(preferences.getString("current_date", ""));

        mWeaLaoyout.setVisibility(View.VISIBLE);
        mbtnRefreshWer.setImageResource(R.drawable.img_refresh_normal);
        Intent intent = new Intent(this, AutoUpdateService.class);  //只要一旦选中了某个城市并成功更新天气之后, 便会启动服务，
        startService(intent);                                       // AutoUpdateService 就会一直在后台 运行,并保证每 8 小时更新一次天气。
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home_page:
                mbtnChangeCity.setImageResource(R.drawable.img_home_pressed);
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;

            case R.id.btn_refresh:
                mtvPublish.setText("正在刷新...");
                mbtnRefreshWer.setImageResource(R.drawable.img_refresh_pressed);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = preferences.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }
}


