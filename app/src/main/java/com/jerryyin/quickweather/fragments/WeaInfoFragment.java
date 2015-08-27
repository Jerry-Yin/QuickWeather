package com.jerryyin.quickweather.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jerryyin.quickweather.R;

import java.util.logging.Handler;

/**
 * Created by JerryYin on 8/22/15.
 */
public class WeaInfoFragment extends Fragment {

    /**Constant*/
    public final int UPDATE_UI = 0x01;

    private View mContentView;
    private Activity mSelf;

    /**Views*/
    private TextView mtvTemp;
    private TextView mtvWeatherDesp;
    public static TextView mtvCityName;
    private ImageView mIvWeather;
    private TextView mtvCurrentDate;
    private TextView mtvCurrentWeekend;

    /**Values*/
    private UIHandler mUiHandler;
    private SharedPreferences p;


    // for child fragment to get the activity
    public Activity getmSelf() {
        return mSelf;
    }

    public WeaInfoFragment(SharedPreferences p){
        this.p = p;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != mContentView){
            ViewGroup vg = (ViewGroup) mContentView.getParent();
            if (null != vg){
                vg.removeView(mContentView);
            }
        }else {
            mSelf = getActivity();
            mContentView = inflater.inflate(R.layout.weather_info_fragment, null);
            setupViews();
        }
        return mContentView;
    }

    public void setupViews(){
        //数据初始化
        initData();
        mUiHandler = new UIHandler();

        mtvTemp = (TextView) mContentView.findViewById(R.id.tv_temp1_temp2);
        mtvWeatherDesp = (TextView) mContentView.findViewById(R.id.tv_weather_desp);
        mtvCityName = (TextView) mContentView.findViewById(R.id.tv_local);
        mIvWeather = (ImageView) mContentView.findViewById(R.id.img_weather);
        mtvCurrentDate = (TextView) mContentView.findViewById(R.id.tv_current_date);
        mtvCurrentWeekend = (TextView) mContentView.findViewById(R.id.tv_current_weekend);

        mUiHandler.sendEmptyMessage(UPDATE_UI);
    }

    public void initData(){
//        String countyCode = getIntent().getStringExtra("county_code");
//        if (!(TextUtils.isEmpty(countyCode))) {
//            // 有县级代号时就去查询天气
////            mtvPublish.setText("同步中...");
//            mWeaLaoyout.setVisibility(View.INVISIBLE);
//            queryWeatherCode(countyCode);
//        } else {
//            // 没有县级代号时就直接显示本地天气
//            showWeather();
//        }
    }

    public class UIHandler extends android.os.Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_UI:
                    mtvCityName.setText(p.getString("city_name", ""));
                    mtvWeatherDesp.setText(p.getString("weather_desp", ""));
                    String temp1 = p.getString("temp1", "");
                    String temp2 = p.getString("temp2", "");
                    mtvTemp.setText(temp1 + "/" + temp2);
                    mtvCurrentDate.setText(p.getString("current_date", ""));
                    mtvCurrentWeekend.setText(p.getString("current_weekend", ""));
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }


}
