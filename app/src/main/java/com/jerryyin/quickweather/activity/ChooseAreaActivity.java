package com.jerryyin.quickweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jerryyin.quickweather.R;
import com.jerryyin.quickweather.model.City;
import com.jerryyin.quickweather.model.County;
import com.jerryyin.quickweather.model.Province;
import com.jerryyin.quickweather.model.QuickWeatherDB;
import com.jerryyin.quickweather.util.HttpUtil;
import com.jerryyin.quickweather.util.OnResponseListener;
import com.jerryyin.quickweather.util.Utility;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by JerryYin on 8/17/15.
 */
public class ChooseAreaActivity extends Activity implements AdapterView.OnItemClickListener {

    /**
     * constants
     */
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    /**
     * Views
     */
    private ProgressDialog mProgressDialog;
    private TextView mtvTitle;
    private ListView mlvChooseLists;

    /**
     * Values
     */
    private ArrayAdapter<String> mListsAdapter;
    private QuickWeatherDB mQuickWeatherDB;
    private List<String> mDataList = new ArrayList<String>();

    /**
     * 省市县数据列表
     */
    private List<Province> mProvinceList;
    private List<City> mCityList;
    private List<County> mCountyList;

    /**
     * 选中的省，市
     */
    private Province mSelProvince;
    private City mSelCity;

    /**
     * 当前选中的级别
     */
    private int mCurrentLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_layout);

        initViews();
        initAdapter();
        initData();
    }

    public void initViews() {
        mtvTitle = (TextView) findViewById(R.id.tv_title);
        mlvChooseLists = (ListView) findViewById(R.id.lv_choose_list);
    }

    public void initAdapter() {
        mListsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDataList);
        mlvChooseLists.setAdapter(mListsAdapter);
        mlvChooseLists.setOnItemClickListener(this);
    }

    public void initData() {
        mQuickWeatherDB = QuickWeatherDB.getInstence(this);
        queryProvinces();  // 默认 初次打开加载省级数据
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mCurrentLevel == LEVEL_PROVINCE) {
            mSelProvince = mProvinceList.get(position);
            queryCities();
        } else if (mCurrentLevel == LEVEL_CITY) {
            mSelCity = mCityList.get(position);
            queryCounties();
        }
    }

    /**
     * 查询全国所有的省,优先从数据库查询,如果没有查询到再去服务器上查询。
     */
    private void queryProvinces() {
        mProvinceList = mQuickWeatherDB.loadProvince();
        if (mProvinceList.size() > 0) {
            mDataList.clear();
            for (Province province : mProvinceList) {
                mDataList.add(province.getProvinceName());
            }
            mListsAdapter.notifyDataSetChanged();
            mtvTitle.setText("中国");
            mlvChooseLists.setSelection(0);
            mCurrentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    /**
     * 查询选中省内所有城市,优先从数据库查询,如果没有查询到再去服务器上查询。
     */
    private void queryCities() {
        mCityList = mQuickWeatherDB.loadCities(mSelProvince.getId());
        if (mCityList.size() > 0) {
            mDataList.clear();
            for (City city : mCityList) {
                mDataList.add(city.getCityName());
            }
            mListsAdapter.notifyDataSetChanged();
            mtvTitle.setText(mSelProvince.getProvinceName());
            mlvChooseLists.setSelection(0);
            mCurrentLevel = LEVEL_CITY;
        } else {
            queryFromServer(mSelProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 查询选中城市所有县,优先从数据库查询,如果没有查询到再去服务器上查询。
     */
    private void queryCounties() {
        mCountyList = mQuickWeatherDB.loadCounties(mSelCity.getId());
        if (mCountyList.size() > 0) {
            mDataList.clear();
            for (County county : mCountyList) {
                mDataList.add(county.getCountyName());
            }
            mListsAdapter.notifyDataSetChanged();
            mtvTitle.setText(mSelCity.getCityName());
            mlvChooseLists.setSelection(0);
            mCurrentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(mSelCity.getCityCode(), "county");
        }
    }

    /**
     * 根据传入的代号和类型从服务器上查询省市县数据。
     */
    private void queryFromServer(final String code, final String type) {
        String adress = null;
        if (!(TextUtils.isEmpty(code))) {
            adress = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            adress = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(adress, new OnResponseListener() {
            @Override
            public void onResponse(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(mQuickWeatherDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCitiesResponse(mQuickWeatherDB, response, mSelProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountiesResponse(mQuickWeatherDB, response, mSelCity.getId());
                }
                if (result) {
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    // 实现原理其实也是基于异 步消息处理机制的
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                            Toast.makeText(ChooseAreaActivity.this, "加载成功！！！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("加载中，，，");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 捕获Back按键,根据当前的级别来判断,此时应该返回市列表、省列表、还是直接退出。
     */
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (mCurrentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (mCurrentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            finish();
        }
    }
}

