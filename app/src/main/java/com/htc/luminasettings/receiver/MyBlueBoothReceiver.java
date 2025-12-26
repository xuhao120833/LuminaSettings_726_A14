package com.htc.luminasettings.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author 作者：zgr
 * @version 创建时间：2016年11月24日 下午4:54:04 类说明
 */
public class MyBlueBoothReceiver extends BroadcastReceiver {

    private MyBlueBoothCallBack callback;

    public MyBlueBoothReceiver(MyBlueBoothCallBack mcallback) {
        this.callback = mcallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    // 手机蓝牙关闭
                    callback.getBlueBoothState(0);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    // 手机蓝牙正在关闭
                    //callback.getBlueBoothState(3);
                    break;
                case BluetoothAdapter.STATE_ON:
                    // 手机蓝牙开启
                    callback.getBlueBoothState(1);
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    // 手机蓝牙正在开启
                    //callback.getBlueBoothState(3);
                    break;
            }
        }
    }

}
