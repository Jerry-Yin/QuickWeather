package com.jerryyin.quickweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jerryyin.quickweather.service.AutoUpdateService;

/**
 * Created by JerryYin on 8/19/15.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }

}
