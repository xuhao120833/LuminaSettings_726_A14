package com.htc.luminasettings.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取设备信息
 * 
 * @author 作者：zgr
 * @version 创建时间：2017-9-29 下午4:36:44
 */
public class DeviceUtils {

	/**
	 * 获取型号
	 * 
	 * @return
	 */
	public static String getModel() {
		return Build.MODEL;
	}

	public static void ShutDown(Context context){
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		pm.shutdown(false,null,false);
	}
	
	/**
	 * 获取Android版本
	 * @return
	 */
	public static String getAndroidVersion(){
		return Build.VERSION.RELEASE;
	}

	/**
	 * 获取版本号
	 * @return
	 */
	public static String getBuildNumber(){
		return Build.DISPLAY;
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
	 * 获取以太网MAC
	 * @return
	 */
	public static String getEthMac() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(
					"sys/class/net/eth0/address"));
			return reader.readLine();
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
			}
		}
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

	/**
	 * 获取内核版本
	 * 
	 * @return
	 */
	public static String getFormattedKernelVersion() {
		String procVersionStr;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"/proc/version"), 256);
			try {
				procVersionStr = reader.readLine();
			} finally {
				reader.close();
			}

			final String PROC_VERSION_REGEX = "Linux version (\\S+) " + /*
																		 * group
																		 * 1:
																		 * "3.0.31-g6fb96c9"
																		 */
			"\\((\\S+?)\\) " + /* group 2: "x@y.com" (kernel builder) */
			"(?:\\(gcc.+? \\)) " + /* ignore: GCC version information */
			"(#\\d+) " + /* group 3: "#1" */
			"(?:.*?)?" + /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
			"((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /*
												 * group 4:
												 * "Thu Jun 28 11:02:39 PDT 2012"
												 */

			Pattern p = Pattern.compile(PROC_VERSION_REGEX);
			Matcher m = p.matcher(procVersionStr);

			if (!m.matches()) {
				return "Unavailable";
			} else if (m.groupCount() < 4) {
				return "Unavailable";
			} else {
				return (new StringBuilder(m.group(1)).append("\n").append(m
						.group(4))).toString();
			}
		} catch (IOException e) {
			return "Unavailable";
		}
	}

	/**
	 * 获得机身内存总大小 内部存储
	 * 
	 * @return
	 */
	public static String getRomTotalSize(Context context) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return Formatter.formatFileSize(context, blockSize * totalBlocks);
	}

	/**
	 * 获得机身内存总大小
	 * 
	 * @return
	 */
	public static long getRomTotalSizeLong(Context context) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return blockSize* totalBlocks;
	}
	
	/**
	 * 获得机身可用内存 内部存储
	 * 
	 * @return
	 */
	public static String getRomAvailableSize(Context context) {
		File path = Environment.getDataDirectory();
		//L.i("path:"+path.getAbsolutePath());
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(context, blockSize * availableBlocks);
	}

	/**
	 * 获取系统总内存
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 * @return 总内存大单位为B。
	 */
	public static long getTotalMemorySize(Context context) {
		String dir = "/proc/meminfo";
		try {
			FileReader fr = new FileReader(dir);
			BufferedReader br = new BufferedReader(fr, 2048);
			String memoryLine = br.readLine();
			String subMemoryLine = memoryLine.substring(memoryLine
					.indexOf("MemTotal:"));
			br.close();
			return Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) * 1024l;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取当前可用内存，返回数据以字节为单位。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 * @return 当前可用内存单位为B。
	 */
	public static long getAvailableMemory(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(memoryInfo);
		return memoryInfo.availMem;
	}

	private static DecimalFormat fileIntegerFormat = new DecimalFormat("#0");
	private static DecimalFormat fileDecimalFormat = new DecimalFormat("#0.#");

	/**
	 * 单位换算
	 * 
	 * @param size
	 *            单位为B
	 * @param isInteger
	 *            是否返回取整的单位
	 * @return 转换后的单位
	 */
	public static String formatFileSize(long size, boolean isInteger) {
		DecimalFormat df = isInteger ? fileIntegerFormat : fileDecimalFormat;
		String fileSizeString = "0M";
		if (size < 1024 && size > 0) {
			fileSizeString = df.format((double) size) + "B";
		} else if (size < 1024 * 1024) {
			fileSizeString = df.format((double) size / 1024) + "K";
		} else if (size < 1024 * 1024 * 1024) {
			fileSizeString = df.format((double) size / (1024 * 1024)) + "M";
		} else {
			fileSizeString = df.format((double) size / (1024 * 1024 * 1024))
					+ "G";
		}
		return fileSizeString;
	}
}
