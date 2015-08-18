package com.jerryyin.quickweather.util;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 * Created by JerryYin on 8/17/15.
 * Http网络请求操作，期间会回调接口方法
 */
public class HttpUtil {

    public static void sendHttpRequest(final String adress, final OnResponseListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(adress);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader reader = new BufferedReader(inputStreamReader);

                    StringBuilder response = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }

//                    HttpClient httpClient = new DefaultHttpClient();              //注释部分为 httpclient请求
//                    HttpGet httpGet = new HttpGet(adress);
//                    HttpResponse response = httpClient.execute(httpGet);
//                    String result = null;
//                    if (response.getStatusLine().getStatusCode() == 200) {
//                        //服务器返回的状态码为 200，说明请求成功，进行数据接收操作：
//                        HttpEntity entity = response.getEntity();
////                      String result = EntityUtils.toString(entity);       //如果返回的结果里卖弄含有中文字符的话，直接toString（）会乱码，此时指定编码方式“UTF-8”，如下
//                        result = EntityUtils.toString(entity, "utf-8");
//                    }

                    if (listener != null){
                        // 回调onResponse()方法
                        listener.onResponse(response.toString());
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    if (listener != null){
                        // 回调onError()方法
                        listener.onError(e);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (listener != null){
                        // 回调onError()方法
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
