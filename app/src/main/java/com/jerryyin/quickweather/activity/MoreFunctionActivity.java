package com.jerryyin.quickweather.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.jerryyin.quickweather.R;
import com.jerryyin.quickweather.fragments.WeaInfoFragment;
import com.jerryyin.quickweather.model.County;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JerryYin on 8/24/15.
 */
public class MoreFunctionActivity extends FragmentActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {


    /**
     * Views
     */
    private ImageView mbtnback;
    private ListView mlvSavedCity;
    private ImageView mbtnAddCity;
    private ImageView mbtnSeeting;

    /**
     * Values
     */
    public static ArrayAdapter<String> mlistAdapter;
    public static List<String> mCityNameList = new ArrayList<String>();   //待显示的已添加城市名字
    private List<County> mSavedCityList = new ArrayList<County>();  //已添加城市列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.more_function_layout);

        initViews();
        loadData();
    }

    public void initViews() {
        mbtnback = (ImageView) findViewById(R.id.btn_back);

        mlvSavedCity = (ListView) findViewById(R.id.list_saved_city);
        mlistAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mCityNameList);
        mlvSavedCity.setAdapter(mlistAdapter);
        mlvSavedCity.setOnItemLongClickListener(this);

        mbtnAddCity = (ImageView) findViewById(R.id.btn_add_city);
        mbtnSeeting = (ImageView) findViewById(R.id.btn_seetings);
        mbtnback.setOnClickListener(this);
        mbtnAddCity.setOnClickListener(this);
        mbtnSeeting.setOnClickListener(this);
    }

    /**
     * 从数据库加载数据，需要排除相同的城市多次加载的情况
     */
    public void loadData() {
        mSavedCityList = ChooseAreaActivity.mQuickWeatherDB.queryLocalCities();
        mCityNameList.clear();  //初始化
        for (int i = 0; i < mSavedCityList.size(); i++) {
            String list = mSavedCityList.get(i).getCountyName();
            if (!mCityNameList.contains(list)) {
                mCityNameList.add(list);
            }
        }
        mlistAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
//                this.finish();
//                WeatherInfoActivity2.instance.finish();
//                Intent i = new Intent(this, WeatherInfoActivity2.class);
//                startActivity(i);
                onBackPressed();
                break;

            case R.id.btn_add_city:
//                mbtnChangeCity.setImageResource(R.drawable.img_home_pressed);
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("is_add_city", true);
                startActivity(intent);
                finish();
                break;

            case R.id.btn_seetings:

                break;

            default:
                break;
        }
    }

    /**
     * 长按item可以删除城市
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("删除这个已添加城市？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteLocalCity(mCityNameList.get(position));
                WeatherInfoActivity2.deleteFragment(mCityNameList.get(position));
                loadData();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();

        return false;
    }

    public void deleteLocalCity(String countyName){
        ChooseAreaActivity.mQuickWeatherDB.deleteLocalCities(countyName);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        WeatherInfoActivity2.instance.finish();
        Intent i = new Intent(this, WeatherInfoActivity2.class);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mlistAdapter.notifyDataSetChanged();
    }


}

