package com.jerryyin.quickweather.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jerryyin.quickweather.R;
import com.jerryyin.quickweather.fragments.WeaInfoFragment;
import com.jerryyin.quickweather.page.transformer.CityPageTransformer;
import com.jerryyin.quickweather.service.AutoUpdateService;
import com.jerryyin.quickweather.util.HttpUtil;
import com.jerryyin.quickweather.util.OnResponseListener;
import com.jerryyin.quickweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

import static com.jerryyin.quickweather.R.id.tv_weekend_future;
import static com.jerryyin.quickweather.R.id.weather_layout;

/**
 * Created by JerryYin on 8/23/15.
 */
public class WeatherInfoActivity2 extends FragmentActivity implements View.OnClickListener {

    /**Constants*/
    private static final String TAG = "WeatherInfoActivity2.class";

    /**
     * Views
     */
    private ImageView mbtnMoreFunction;      //切换城市按钮
    private ImageView mbtnRefreshWer;      //更新天气
    private TextView mtvPublish;        //发布时间

    private LinearLayout mWeatherLayout;
    private ViewPager mCityViewPager;
    private ListView mFuturelist;

    /**
     * Values
     */
    public static WeatherInfoActivity2 instance = null;
    private FragmentManager mfragmentManager;
    public static FragmentPagerAdapter mWeatherPagerAdapter;
    private WeaInfoFragment mWeaInfoFragment;
    private CityPageTransformer mPageTransformer;
    private FutureListAdapter mfutureListAdapter;
    public static List<WeaInfoFragment> mFragmentLists = new ArrayList<WeaInfoFragment>();    //用于存放所有的 自选城市fragment；
    private boolean isFromServer = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout2);

        initViews();
        initViewPager();
        initData();
    }

    public void initViews() {
        instance = this;
        mbtnMoreFunction = (ImageView) findViewById(R.id.btn_more);
        mbtnRefreshWer = (ImageView) findViewById(R.id.btn_refresh);
        mbtnMoreFunction.setOnClickListener(this);
        mbtnRefreshWer.setOnClickListener(this);
        mtvPublish = (TextView) findViewById(R.id.tv_pulish_time);
        mWeatherLayout = (LinearLayout) findViewById(R.id.weather_layout);

        mFuturelist = (ListView) findViewById(R.id.listview_future);
        mfutureListAdapter = new FutureListAdapter(this);
        mFuturelist.setAdapter(mfutureListAdapter);
    }

    public void initData(){
        String countyCode = getIntent().getStringExtra("county_code");  //承接选中的城市 并 查询数据
        if (!(TextUtils.isEmpty(countyCode))) {
            // 有县级代号时就去查询天气
            mtvPublish.setText("同步中...");
            mWeatherLayout.setVisibility(View.INVISIBLE);
            isFromServer = true;
            queryWeatherCode(countyCode);

        } else {
            // 没有县级代号时就直接显示本地天气 ,例如关闭后再打开时，需要显示已有的数据
            isFromServer = false;
            showWeather();
        }
    }

    /**
     * 初始化 ViewPager
     */
    public void initViewPager() {
        mCityViewPager = (ViewPager) findViewById(R.id.weather_view_pager);
        mfragmentManager = getSupportFragmentManager();     //getSupportFragmentManager要用在FragmentActivity及其子类中
        mWeatherPagerAdapter = new WeatherPagerAdapter(mfragmentManager);
        mCityViewPager.setAdapter(mWeatherPagerAdapter);

        mPageTransformer = new CityPageTransformer();
        mCityViewPager.setPageTransformer(true, mPageTransformer);
    }


    // ViewPager适配器
    class WeatherPagerAdapter extends FragmentPagerAdapter {

        public WeatherPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragmentLists.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentLists.get(position);
        }

    }

    /**
     * 初始化 ListView 适配器
     */
    class FutureListAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        private class DataHandler {
            public TextView future_temp1_temp2;
            public TextView future_weatherDesp;
            public ImageView future_weatherImg;
            public TextView future_date;
            public TextView future_weekwnd;
        }

        public FutureListAdapter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DataHandler dataHandler = null;
            if (convertView == null) {
                dataHandler = new DataHandler();
                convertView = mInflater.inflate(R.layout.item_future_list, null);
                dataHandler.future_temp1_temp2 = (TextView) findViewById(R.id.tv_temp1_temp2_future);
                dataHandler.future_weatherDesp = (TextView) findViewById(R.id.tv_weather_desp_future);
                dataHandler.future_weatherImg = (ImageView) findViewById(R.id.img_weather_future);
                dataHandler.future_date = (TextView) findViewById(R.id.tv_date_future);
                dataHandler.future_weekwnd = (TextView) findViewById(tv_weekend_future);
                convertView.setTag(dataHandler);
            } else {
                dataHandler = (DataHandler) convertView.getTag();
            }
            return convertView;
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
                        Utility.paraseWeatherResponse(WeatherInfoActivity2.this, response);  //解析天气数据
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

        loadFragmentInfo(preferences);

        mWeatherLayout.setVisibility(View.VISIBLE);
        mbtnRefreshWer.setImageResource(R.drawable.img_refresh_normal);
        Intent intent = new Intent(this, AutoUpdateService.class);  //只要一旦选中了某个城市并成功更新天气之后, 便会启动服务，
        startService(intent);                                       // AutoUpdateService 就会一直在后台 运行,并保证每 8 小时更新一次天气。
    }

    /**
     *  分情况：
     *  1.第一次进入app，需要选择添加第一个城市，   此时 mWeaInfoFragment ＝ null，mFragmentLists.size() == 0，需要新建fragment并添加到list；
     *  2.已经添加过城市后，退出app后再次打开，                       此时 mWeaInfoFragment ＝ null，mFragmentLists.size() != 0，不需要新建，直接notify显示
     *  3.打开状态下添加城市，从ChooseActivity跳转过来，mWeaInfoFragment ＝ null，mFragmentLists.size() != 0，但需要新建fragment并添加到list；
     *  总结： 1 和 3 情况以及条件一样，归位一类，故最终分为两类；
     */
    public void loadFragmentInfo(SharedPreferences p){
        if (isFromServer || mFragmentLists.size() == 0) {
            mWeaInfoFragment = new WeaInfoFragment(p);
            mFragmentLists.add(mWeaInfoFragment);
        }
        if (ChooseAreaActivity.mQuickWeatherDB.queryLocalCities().size() == 0 ){
            mFragmentLists.clear();
        }
        Log.d(TAG, "mFragmentLists.size() = " + mFragmentLists.size());
        mWeatherPagerAdapter.notifyDataSetChanged();
        mCityViewPager.setCurrentItem(mFragmentLists.size() - 1);   //设置默认位置为新添加的，也就是最后一个
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_refresh:

                break;
            case R.id.btn_more:
                Intent intent = new Intent(this, MoreFunctionActivity.class);
                //传递参数
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public static void deleteFragment(String countyName){
        for (int i = 0; i < mFragmentLists.size(); i ++){
            if (mFragmentLists.get(i).mtvCityName.getText().equals(countyName)){
                mFragmentLists.remove(mFragmentLists.get(i));
            }
        }
        mWeatherPagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
        Log.d(TAG, "WeatherInfoActivity2 is finished !!!");
    }
}
