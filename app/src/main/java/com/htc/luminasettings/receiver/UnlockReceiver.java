package com.htc.luminasettings.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.htc.luminasettings.utils.LogUtils;

public class UnlockReceiver extends BroadcastReceiver {

    private static String TAG = "UnlockReceiver";
    private UnlockCallBack unlockCallBack;
    public UnlockReceiver(UnlockCallBack unlockCallBack) {
        this.unlockCallBack = unlockCallBack;
    }

    public UnlockReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d(TAG, "收到 ACTION_USER_UNLOCKED");
        if (Intent.ACTION_USER_UNLOCKED.equals(intent.getAction())) {
            unlockCallBack.unLock();
        }
    }
}