package com.htc.luminaos.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
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
	public int WPA_INDEX = 1;
	public int WPA2_INDEX = 2;

	private static final String DEFAULT_AP_PASSWORD = "12345678";
	public static int WIFI_AP_STATE_DISABLING = 10;
	public static int WIFI_AP_STATE_DISABLED = 11;
	public static int WIFI_AP_STATE_ENABLING = 12;
	public static int WIFI_AP_STATE_ENABLED = 13;
	public static int WIFI_AP_STATE_FAILED = 14;

	public enum WifiSecurityType {
		WIFICIPHER_NOPASS, WIFICIPHER_WPA, WIFICIPHER_WEP, WIFICIPHER_INVALID, WIFICIPHER_WPA2
	}

	private WifiManager mWifiManager = null;

	private Context mContext = null;

	private OnStartTetheringCallback mStartTetheringCallback;

	public WifiHotUtil(Context context) {
		mContext = context;
		mWifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		mStartTetheringCallback = new OnStartTetheringCallback();
		mConnectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	public void startWifiAp(String ssid, String passwd) {
		// wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}

		if (!isWifiApEnabled()) {
			stratWifiAp(ssid, passwd);
		}
	}

	/**
	 * 获取热点信息
	 * 
	 * @return
	 */
	public WifiConfiguration getWifiConfig() {
		WifiConfiguration configuration = null;
		configuration = mWifiManager.getWifiApConfiguration();
		return configuration;
	}

	public int getSecurityTypeIndex(WifiConfiguration wifiConfig) {
		if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
			return WPA_INDEX;
		} else if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA2_PSK)) {
			return WPA2_INDEX;
		}
		return OPEN_INDEX;
	}

	/**
	 * 设置热点名称及密码，并创建热点
	 * 
	 * @param mSSID
	 * @param mPasswd
	 */
	private void stratWifiAp(String mSSID, String mPasswd) {
		Method method1 = null;
		try {
			// 通过反射机制打开热点
			method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
					WifiConfiguration.class, boolean.class);
			WifiConfiguration netConfig = new WifiConfiguration();

			netConfig.SSID = mSSID;
			netConfig.preSharedKey = mPasswd;

			netConfig.allowedAuthAlgorithms
					.set(AuthAlgorithm.OPEN);
			netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			netConfig.allowedKeyManagement
					.set(KeyMgmt.WPA_PSK);
			netConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			netConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			netConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.CCMP);
			netConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.TKIP);
			method1.invoke(mWifiManager, netConfig, true);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
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

	/**
	 * 关闭WiFi热点
	 */
	public void closeWifiAp() {
		WifiManager wifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		if (isWifiApEnabled()) {
			try {
				Method method = wifiManager.getClass().getMethod(
						"getWifiApConfiguration");
				method.setAccessible(true);
				WifiConfiguration config = (WifiConfiguration) method
						.invoke(wifiManager);
				Method method2 = wifiManager.getClass().getMethod(
						"setWifiApEnabled", WifiConfiguration.class,
						boolean.class);
				method2.invoke(wifiManager, config, false);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	// public void setWifiApConfiguration(WifiConfiguration mWifiConfig) {
	// if (mWifiConfig != null) {
	// /**
	// * if soft AP is running, bring up with new config else update the
	// * configuration alone
	// */
	// if (mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLED) {
	// mWifiManager.setWifiApEnabled(null, false);
	// mWifiManager.setWifiApEnabled(mWifiConfig, true);
	// } else {
	// mWifiManager.setWifiApConfiguration(mWifiConfig);
	// }
	// }
	// }

	/**
	 * 设置热点名称及密码，并创建热点
	 *
	 * @param mSSID
	 * @param mPasswd
	 */
	public void openWifiAp(WifiConfiguration netConfig) {

		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}

		if (!isWifiApEnabled()) {
			Method method1 = null;
			try {
				// 通过反射机制打开热点
				method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
						WifiConfiguration.class, boolean.class);
				method1.invoke(mWifiManager, netConfig, true);

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean turnOnWifiAps(String SSID, String Password,
			WifiSecurityType Type, int APband) {
//		setWifiApEnabled();
		WifiConfiguration config = new WifiConfiguration();
		config.SSID = SSID;

		config.apBand = APband;
		LogUtils.i("hxdmsg", " apBand: " + APband);
		if (Type == WifiSecurityType.WIFICIPHER_NOPASS) {
			config.allowedKeyManagement.set(KeyMgmt.NONE);
		} else if (Type == WifiSecurityType.WIFICIPHER_WPA) {
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.preSharedKey = Password;
		} else if (Type == WifiSecurityType.WIFICIPHER_WPA2) {
			config.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.preSharedKey = Password;
		}
		boolean b = mWifiManager.setWifiApConfiguration(config);
		LogUtils.i("hxdmsg", " mWifiManger.setWifiApConfiguration: " + b);
		return b;
	}

	public boolean turnOnWifiAp(String str, String password,
			WifiSecurityType Type, int apband) {
		String ssid = str;
		// 配置热点信息。
		WifiConfiguration wcfg = new WifiConfiguration();
		wcfg.apBand = apband;
		wcfg.SSID = new String(ssid);
		wcfg.networkId = 1;
		wcfg.allowedAuthAlgorithms.clear();
		wcfg.allowedGroupCiphers.clear();
		wcfg.allowedKeyManagement.clear();
		wcfg.allowedPairwiseCiphers.clear();
		wcfg.allowedProtocols.clear();

		if (Type == WifiSecurityType.WIFICIPHER_NOPASS) {
			// if(DEBUG)LogUtils.d(TAG, "wifi ap----no password");
			wcfg.allowedAuthAlgorithms.set(
					AuthAlgorithm.OPEN, true);
			wcfg.wepKeys[0] = "";
			wcfg.allowedKeyManagement.set(KeyMgmt.NONE);
			wcfg.wepTxKeyIndex = 0;
		} else if (Type == WifiSecurityType.WIFICIPHER_WPA) {
			// if(DEBUG)LogUtils.d(TAG, "wifi ap----wpa");
			// 密码至少8位，否则使用默认密码
			if (null != password && password.length() >= 8) {
				wcfg.preSharedKey = password;
			} else {
				wcfg.preSharedKey = DEFAULT_AP_PASSWORD;
			}
			wcfg.hiddenSSID = false;
			wcfg.allowedAuthAlgorithms
					.set(AuthAlgorithm.OPEN);
			wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			wcfg.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			// wcfg.allowedKeyManagement.set(4);
			wcfg.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			wcfg.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			wcfg.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
		} else if (Type == WifiSecurityType.WIFICIPHER_WPA2) {
			// if(DEBUG)LogUtils.d(TAG, "wifi ap---- wpa2");
			// 密码至少8位，否则使用默认密码
			if (null != password && password.length() >= 8) {
				wcfg.preSharedKey = password;
			} else {
				wcfg.preSharedKey = DEFAULT_AP_PASSWORD;
			}
			wcfg.hiddenSSID = true;
			wcfg.allowedAuthAlgorithms
					.set(AuthAlgorithm.OPEN);
			wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			wcfg.allowedKeyManagement.set(4);
			// wcfg.allowedKeyManagement.set(4);
			wcfg.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			wcfg.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			wcfg.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
		}
		try {
			Method method = mWifiManager.getClass().getMethod(
					"setWifiApConfiguration", wcfg.getClass());
			Boolean rt = (Boolean) method.invoke(mWifiManager, wcfg);
			// if(DEBUG) LogUtils.d(TAG, " rt = " + rt);

			LogUtils.d(TAG, " rt = " + rt);

		} catch (NoSuchMethodException e) {
			// LogUtils.e(TAG, e.getMessage());
		} catch (IllegalArgumentException e) {
			// LogUtils.e(TAG, e.getMessage());
		} catch (IllegalAccessException e) {
			// LogUtils.e(TAG, e.getMessage());
		} catch (InvocationTargetException e) {
			// LogUtils.e(TAG, e.getMessage());
		}
		return setWifiApEnabled();
	}

	private ConnectivityManager mConnectivityManager;

	// 在重新设置了热点之后，需要关闭重新打开热点
	private boolean setWifiApEnabled() {
		// // 开启wifi热点需要关闭wifi
		// while (mWifiManager.getWifiState() !=
		// WifiManager.WIFI_STATE_DISABLED) {
		// mWifiManager.setWifiEnabled(false);
		// try {
		// Thread.sleep(200);
		// } catch (Exception e) {
		// // LogUtils.e(TAG, e.getMessage());
		// return false;
		// }
		// }
		// 确保wifi 热点关闭。
		// while (getWifiAPState() != WIFI_AP_STATE_DISABLED) {
		// try {
		// Method method1 = mWifiManager.getClass().getMethod(
		// "setWifiApEnabled", WifiConfiguration.class,
		// boolean.class);
		// method1.invoke(mWifiManager, null, false);
		//
		// Thread.sleep(200);
		// } catch (Exception e) {
		// // LogUtils.e(TAG, e.getMessage());
		// return false;
		// }
		// }

		mConnectivityManager.stopTethering(ConnectivityManager.TETHERING_WIFI);

		//

		// try {
		//
		// mConnectivityManager.startTethering(
		// ConnectivityManager.TETHERING_WIFI,
		// true, mStartTetheringCallback, null);
		// Thread.sleep(200);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// 开启wifi热点
		// try {
		// Method method1 = mWifiManager.getClass().getMethod(
		// "setWifiApEnabled", WifiConfiguration.class, boolean.class);
		// method1.invoke(mWifiManager, null, true);
		// Thread.sleep(200);
		// } catch (Exception e) {
		// // LogUtils.e(TAG, e.getMessage());
		// return false;
		// }
		return true;
	}

	public int getWifiAPState() {
		int state = -1;
		try {
			Method method2 = mWifiManager.getClass()
					.getMethod("getWifiApState");
			state = (Integer) method2.invoke(mWifiManager);
		} catch (Exception e) {
			// LogUtils.e(TAG, e.getMessage());
		}
		// if(DEBUG)LogUtils.i("WifiAP", "getWifiAPState.state " + state);
		return state;
	}

	private static final class OnStartTetheringCallback extends
			ConnectivityManager.OnStartTetheringCallback {
		// final WeakReference<HotPotFragment> mTetherSettings;
		//
		// OnStartTetheringCallback(HotPotFragment settings) {
		// mTetherSettings = new WeakReference<HotPotFragment>(settings);
		// }

		@Override
		public void onTetheringStarted() {
			// update();
			LogUtils.i("hxdmsg", " execute onTetheringStarted------");
		}

		@Override
		public void onTetheringFailed() {
			// update();
			LogUtils.i("hxdmsg", " execute onThtheringFailed--------");
		}

		private void update() {
			// HotPotFragment settings = mTetherSettings.get();
			// if (settings != null) {
			// settings.updateState();
			// }
		}
	}

}