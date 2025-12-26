package com.htc.luminasettings.receiver;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author 作者：zgr
 * @version 创建时间：2016年11月4日 下午6:17:48 类说明
 */
public class BluetoothReceiver extends BroadcastReceiver {

    private String TAG = "BlueboothReceiver";

    private BluetoothCallBcak mcallback;

    public BluetoothReceiver(BluetoothCallBcak callback) {
        this.mcallback = callback;
    }

    @SuppressLint("InlinedApi")
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
                mcallback.bluetoothChange();
            } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        // 手机蓝牙关闭
                        mcallback.bluetoothChange();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        // 手机蓝牙正在关闭
                        break;
                    case BluetoothAdapter.STATE_ON:
                        // 手机蓝牙开启
                        mcallback.bluetoothChange();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        // 手机蓝牙正在开启
                        break;
                }
            }

        }

    }
}
