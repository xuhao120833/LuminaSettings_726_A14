package com.htc.luminaos.settings.utils;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.os.Build;
import android.os.Environment;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;

import com.htc.luminaos.utils.LogUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * @author 作者：zgr
 * @version 创建时间：2017年3月21日 下午3:41:46 类说明
 */
public class Constants {

	// 时间变化
	public final static String TIME_TICK = "android.intent.action.TIME_TICK";
	// 日期变化
	public final static String DATE_CHANGED = "android.intent.action.DATE_CHANGED";
	// 时间广播
	public final static String ACTION_USER_SWITCHED = "android.intent.action.USER_SWITCHED";

	public static final String KEY_GMT = "gmt";
	public static final String KEY_OFFSET = "offset";
	public static final int HOURS_1 = 60 * 60000;
	public static final String KEY_ID = "id";
	public static final String KEY_DISPLAYNAME = "name";
	public static final String FILE_NAME = "htcsettings_data";
	public final static String key_Seetings = "htcsettings";
	private static String TAG ="Constants";




	public static String getMac() {
		String macSerial = null;
		String str = "";

		try {
			Process pp = Runtime.getRuntime().exec(
					"cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return macSerial;
	}

	@SuppressLint("NewApi")
	public static String getMacAddr() {
		try {
			List<NetworkInterface> all = Collections.list(NetworkInterface
					.getNetworkInterfaces());
			for (NetworkInterface nif : all) {
				if (!nif.getName().equalsIgnoreCase("wlan0"))
					continue;

				byte[] macBytes = nif.getHardwareAddress();
				if (macBytes == null) {
					return "";
				}

				StringBuilder res1 = new StringBuilder();
				for (byte b : macBytes) {
					res1.append(String.format("%02X:", b));
				}

				if (res1.length() > 0) {
					res1.deleteCharAt(res1.length() - 1);
				}
				return res1.toString();
			}
		} catch (Exception ex) {
		}
		return "02:00:00:00:00:00";
	}

	/**
	 * 获取序列号
	 * 
	 * @return
	 */
	public static String getSerialNumber() {
		String serial = null;
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class);
			serial = (String) get.invoke(c, "ro.serialno");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serial;

	}

	public static String getSystemPropertiesValues(String key){
		return SystemProperties.get(key,"");
	}

	/**
	 * 获取当前版本号 去空格
	 *
	 * @return
	 */
	public static String getCurrentVersionTrim() {
		String result = Build.DISPLAY;

		result = result.replaceAll(" ", "");

		return result;
	}

	/**
	 * 获取otc下载目录
	 *
	 * @return
	 */
	public static String getOtaDir() {
//		return Environment.getExternalStorageDirectory().getAbsolutePath()
//				+ File.separator + Const.ota_dir;
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}


	//0=未标定 1= 已标定  2=已标定，数据异常 3=已标定，数据正常
	public static int CheckCalibrated(String data){
		if (data ==null || data.length()<=152)
			return 0;

		char[] dataChar = data.toCharArray();
		int length = Math.min(152,dataChar.length);
		for(int i=0;i<length;i++){
			if (( dataChar[i]<='9' && dataChar[i]>='0' ) || ( dataChar[i]>='a' && dataChar[i]<='f' ) || ( dataChar[i]>='A' && dataChar[i]<='F' )){
				continue;
			}
			return 0;
		}
		return CheckCRC(dataChar);
	}

	public static int CheckCRC(char[] dataChar){
		if (dataChar.length<154)
			return 1;

		int crc = 0;
		for (int i=0;i<152;i++){
			crc+=dataChar[i];
		}
		crc = (0x100-(crc&0xFF))&0xFF;
		String crc_hex = Integer.toHexString(crc);
		if (crc_hex.length()==1){
			crc_hex = "0"+crc_hex;
		}
		LogUtils.d(TAG,"CRC "+crc_hex);
		boolean status = crc_hex.equals(String.valueOf(dataChar[152])+ dataChar[153]);
		LogUtils.d(TAG,"CRC check="+status);
		return status?3:2;
	}

	public static void sendBackKey(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Instrumentation instrumentation = new Instrumentation();
					instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}
}
