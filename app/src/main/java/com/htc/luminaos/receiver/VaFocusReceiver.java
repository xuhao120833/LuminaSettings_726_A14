package com.htc.luminaos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.htc.luminaos.utils.LogUtils;

public class VaFocusReceiver extends BroadcastReceiver {

    VaFocusCallBack vaFocusCallBack;

    public VaFocusReceiver(VaFocusCallBack vaFocusCallBack) {
        this.vaFocusCallBack = vaFocusCallBack;
    }

    VaFocusReceiver(){};

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("intent.htc.vafocus".equals(intent.getAction())) {
            // 处理广播
            LogUtils.d("VaFocusReceiver", "Received vaFocus broadcast!");

            vaFocusCallBack.vaFocusChange();
        }
    }

}
