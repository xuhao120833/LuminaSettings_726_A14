package com.htc.luminaos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.htc.luminaos.utils.LogUtils;

import java.util.List;

/**
 * wifi信号监听
 * 
 * @author xuhao
 * 
 */
public class MyWifiReceiver extends BroadcastReceiver {

	private MyWifiCallBack mCallBack;;
	private String TAG = "MyWifiReceiver";

	public MyWifiReceiver(MyWifiCallBack callback) {
		this.mCallBack = callback;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent != null) {
			String action = intent.getAction();
			// 监听WIFI状态变化
			if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				// WIFI开关
				int wifistate = intent.getIntExtra(
						WifiManager.EXTRA_WIFI_STATE,
						WifiManager.WIFI_STATE_DISABLED);
				if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
					// 如果关闭
					mCallBack.getWifiState(1);
				} else {
					// 打开
					mCallBack.getWifiState(0);
				}
			} else if (action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
				// 当前的信号
				int number = getStrength(context);
				mCallBack.getWifiNumber(number);
			} else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				// 网络状态改变
				NetworkInfo info = intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
					// 断开连接
					mCallBack.getWifiNumber(1);
				}else if (info.getState().equals(NetworkInfo.State.CONNECTED)){
					mCallBack.getWifiNumber(3);
				}
			}
		}
	}

	/**
	 * 获取信号
	 * 
	 * @param context
	 * @return
	 */
	public int getStrength(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();
		if (info.getBSSID() != null) {
			int strength = WifiManager.calculateSignalLevel(info.getRssi(), 4);

			int level = info.getRssi();
			// 链接速度
			// int speed = info.getLinkSpeed();
			// 链接速度单位
			// String units = WifiInfo.LINK_SPEED_UNITS;
			// Wifi源名称
			// String ssid = info.getSSID();
			LogUtils.d(TAG,"getStrength信号强度更新 "+strength + " "+level);
			return level;
		}
		return 0;
	}
}
