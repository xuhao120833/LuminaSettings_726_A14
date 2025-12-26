package com.htc.luminasettings.utils;

import android.content.Context;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;


/**
 * @ClassName: WifiHotUtil
 * @Description: 打印日志信息WiFi热点工具
 * @author: jajuan.wang
 * @date: 2015-05-28 15:12 version:1.0.0
 */
public class WifiHotUtil {
	public static final String TAG = "WifiApAdmin";

	public int OPEN_INDEX = 0;
	public int WPA2_INDEX = 1;
	private WifiManager mWifiManager = null;
	private Context mContext = null;

	public WifiHotUtil(Context context) {
		mContext = context;
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	}

	// 获取 SoftApConfiguration
	public SoftApConfiguration getWifiConfig() {
		if (mWifiManager != null) {
			return mWifiManager.getSoftApConfiguration();
		}
		return null;
	}

	// 根据 SoftApConfiguration 判断安全类型
	public int getSecurityTypeIndex(SoftApConfiguration config) {
		switch (config.getSecurityType()) {
			case SoftApConfiguration.SECURITY_TYPE_WPA2_PSK:
			case SoftApConfiguration.SECURITY_TYPE_WPA3_SAE: // 如果需要支持 WPA3
				return WPA2_INDEX;
			case SoftApConfiguration.SECURITY_TYPE_OPEN:
			default:
				return OPEN_INDEX;
		}
	}
	/**
	 * 热点开关是否打开
	 *
	 * @return
	 */
	public boolean isWifiApEnabled() {
		try {
			Method method = mWifiManager.getClass()
					.getMethod("isWifiApEnabled");
			method.setAccessible(true);
			return (Boolean) method.invoke(mWifiManager);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}