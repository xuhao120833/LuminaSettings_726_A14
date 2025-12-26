package com.htc.luminasettings.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

/**
 * @author  作者：xuhao
 * @version 创建时间：2017年3月28日 上午11:09:52
 * 类说明  wifi开关广播
 */
public class WifiEnabledReceiver extends BroadcastReceiver {

	private WifiEnabledCallBack mcallback;
	
	public WifiEnabledReceiver(WifiEnabledCallBack callback){
		this.mcallback=callback;
	}
	
	public interface WifiEnabledCallBack{
		public void openWifi();
		public void closeWifi();
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action=intent.getAction();
		if(action!=null&&action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
			int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
			switch (wifistate) {
			case WifiManager.WIFI_STATE_DISABLED:
				//系统关闭了wifi
				mcallback.closeWifi();
				break;
			case WifiManager.WIFI_STATE_ENABLED:
				//系统打开了wifi
				mcallback.openWifi();
				break;
			}
		}
		
	}

}
