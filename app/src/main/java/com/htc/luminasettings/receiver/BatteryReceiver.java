package com.htc.luminasettings.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.htc.luminasettings.utils.BatteryCallBack;


public class BatteryReceiver extends BroadcastReceiver {

    private Context mContext;

    private BatteryCallBack batteryCallBack;

    BatteryReceiver() {}

    public BatteryReceiver(Context context, BatteryCallBack callBack) {
        mContext = context;
        batteryCallBack = callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if ("action.projector.dcin".equals(action)) {
            String dcInStatus = intent.getStringExtra("dc_in");
            if ("bat_dcin".equals(dcInStatus)) {
                // 插入充电器
                // 处理插入充电器的逻辑
                batteryCallBack.Plug_in_charger();

            } else if ("unplug_dcin".equals(dcInStatus)) {
                // 拔掉充电器
                // 处理拔掉充电器的逻辑
                batteryCallBack.Unplug_the_charger();
            }
        } else if ("action.projector.batterylevel".equals(action)) {
            String batteryLevel = intent.getStringExtra("battery_level");
            // 处理电池等级的逻辑
            // bat_level是电池等级(0-4)字符串，分别对应 0， 25%，50%，75%，100%

            batteryCallBack.setBatteryLevel(batteryLevel);
        }
    }
}
