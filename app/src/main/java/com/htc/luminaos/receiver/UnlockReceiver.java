package com.htc.luminaos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
        Log.d(TAG, "收到 ACTION_USER_UNLOCKED");
        if (Intent.ACTION_USER_UNLOCKED.equals(intent.getAction())) {
            unlockCallBack.unLock();
        }
    }
}