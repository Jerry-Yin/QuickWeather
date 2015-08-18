package com.jerryyin.quickweather.util;

/**
 * Created by JerryYin on 8/17/15.
 * 回调的接口
 */
public interface OnResponseListener {

    /** ServerTalker will callback this method after connect with server.
     * @param response the data send by server.
     */

    void onResponse(String response);

    void onError(Exception e);
}
