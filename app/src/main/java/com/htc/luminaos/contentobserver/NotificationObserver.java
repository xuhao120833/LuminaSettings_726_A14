package com.htc.luminaos.contentobserver;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

public class NotificationObserver extends ContentObserver {

    private Context mContext = null;
    private NotificationCallBack callBack;

    public NotificationObserver(Context mContext, NotificationCallBack callBack) {
        super(new Handler());
        this.mContext = mContext;
        this.callBack = callBack;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        try {
            int value = Settings.Global.getInt(mContext.getContentResolver(), "notification");
            Log.d("Observer", "notification changed: " + value);
            callBack.changeNoticeIcon(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
