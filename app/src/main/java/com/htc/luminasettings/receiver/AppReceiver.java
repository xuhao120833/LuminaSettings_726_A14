package com.htc.luminasettings.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppReceiver extends BroadcastReceiver{
	
	private AppCallBack callback;
	
	public AppReceiver(AppCallBack mcallback){
		this.callback=mcallback;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            if (callback!=null)
        	callback.appInstall(packageName);
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
			if (callback!=null)
        	callback.appUnInstall(packageName);
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
			if (callback!=null)
        	callback.appChange(packageName);
        }

	}

}
