package com.htc.luminasettings.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HotspotReceiver extends BroadcastReceiver {
    private HotspotCallBack mcallback;

    public interface HotspotCallBack {
        void aPState(int i);
    }

    public HotspotReceiver(HotspotCallBack callback) {
        this.mcallback = callback;
    }

    public void onReceive(Context context, Intent intent) {
        if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(intent.getAction())) {
            this.mcallback.aPState(intent.getIntExtra("wifi_state", 0));
        }
    }
}
