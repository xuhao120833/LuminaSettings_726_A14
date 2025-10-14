package com.htc.luminaos.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemProperties;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

/**
 * 内存清理
 *
 * @author 邹观荣
 *
 */
public class ClearMemoryUtils {

	private static String TAG = "ClearMemoryUtils";

	/**
	 * 获取android当前可用内存大小
	 *
	 * @return
	 */
	@SuppressWarnings("unused")
	public static long getAvailMemory(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; 当前系统的可用内存
		// Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
		return mi.availMem;
	}

	/**
	 * 获取android当前可用内存大小
	 *
	 * @return
	 */
	@SuppressWarnings("unused")
	public static String getAvailMemoryStr(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; 当前系统的可用内存
		return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
	}

	/**
	 * 获取总内存
	 *
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unused")
	public static long getTotalMemory(Context context) {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;

		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				LogUtils.i(str2, num + "\t");
			}

			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();

		} catch (IOException e) {
		}
		// Formatter.formatFileSize(context, initial_memory);//
		// Byte转换为KB或者MB，内存大小规格化
		return initial_memory;
	}

	public static String getTotalMemoryStr(Context context) {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;

		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				LogUtils.i(str2, num + "\t");
			}

			initial_memory = Integer.valueOf(arrayOfString[1]).intValue();// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();

		} catch (IOException e) {
		}
		return Formatter.formatFileSize(context, initial_memory);//
		// Byte转换为KB或者MB，内存大小规格化
	}

	/**
	 * 获取已使用的内存 %
	 *
	 * @return
	 */
	public static String getUseMemory(long total, long can) {
		String result = "0%";
		long unuse = total - can;
		BigDecimal b1 = new BigDecimal(Double.valueOf(total));
		BigDecimal b2 = new BigDecimal(Double.valueOf(unuse));
		result = b2.divide(b1, 2, BigDecimal.ROUND_HALF_UP) + "%";
		return result;
	}

	/** * 计算已使用内存的百分比 * */
	public static String getUsedPercentValue(Context context) {
		String dir = "/proc/meminfo";
		try {
			FileReader fr = new FileReader(dir);
			BufferedReader br = new BufferedReader(fr, 2048);
			String memoryLine = br.readLine();
			String subMemoryLine = memoryLine.substring(memoryLine
					.indexOf("MemTotal:"));
			br.close();
			long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll(
					"\\D+", ""));
			long availableSize = getAvailMemory(context) / 1024;
			int percent = (int) ((totalMemorySize - availableSize)
					/ (float) totalMemorySize * 100);
			return percent + "%";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "无结果";
	}

	/**
	 * 清理内存
	 *
	 * @param context
	 */
	public static String memoryCleanup(Context context) {
		String result = "";
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infoList = am.getRunningAppProcesses();
		// List<ActivityManager.RunningServiceInfo> serviceInfos = am
		// .getRunningServices(100);
		long beforeMem = getAvailMemoryFormat(context);
		int count = 0;
		if (infoList != null) {
			for (int i = 0; i < infoList.size(); ++i) {
				RunningAppProcessInfo appProcessInfo = infoList.get(i);
				// LogUtils.d(TAG, "process name : " + appProcessInfo.processName);
				// importance 该进程的重要程度 分为几个级别，数值越低就越重要。
				// LogUtils.d(TAG, "importance : " + appProcessInfo.importance);
				// 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
				// 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
				if (appProcessInfo.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
					String[] pkgList = appProcessInfo.pkgList;
					for (int j = 0; j < pkgList.length; ++j) {
						// pkgList 得到该进程下运行的包名
						// LogUtils.i("Clear", "It will be killed, package name : "+
						// pkgList[j]);
						am.killBackgroundProcesses(pkgList[j]);
						count++;
					}
				}

			}
		}
		long afterMem = getAvailMemoryFormat(context);
		// Toast.makeText(context,
		// "clear " + count + " process, " + (afterMem - beforeMem) + "M",
		// Toast.LENGTH_LONG).show();
		result = "clear " + count + " process, " + (afterMem - beforeMem) + "M";
		return result;
	}

	// 获取可用内存大小
	@SuppressWarnings("unused")
	public static long getAvailMemoryFormat(Context context) {
		// 获取android当前可用内存大小
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; 当前系统的可用内存
		// return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
		// LogUtils.d(TAG, "可用内存---->>>" + mi.availMem / (1024 * 1024));
		return mi.availMem / (1024 * 1024);
	}



	/**
	 * 获得SD卡总大小
	 *
	 * @return
	 */
	public static String getSDTotalSize(Context context) {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return Formatter.formatFileSize(context, blockSize
				* totalBlocks);
	}

	/**
	 * 获得sd卡剩余容量，即可用大小
	 *
	 * @return
	 */
	public static String getSDAvailableSize(Context context) {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(context, blockSize
				* availableBlocks);
	}

	/**
	 * 获得机身内存总大小
	 *
	 * @return
	 */
	public static String getRomTotalSize(Context context) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return Formatter.formatFileSize(context, blockSize
				* totalBlocks);
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
	 * 获得机身可用内存
	 *
	 * @return
	 */
	public static String getRomAvailableSize(Context context,int scale) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();

		LogUtils.d(TAG," getRomAvailableSize " + blockSize * availableBlocks * scale);

//		return Formatter.formatFileSize(context, blockSize //使用的是10进制，和文件管理器有误差，弃用。
//				* availableBlocks * scale);git

		int size = SystemProperties.getInt("persist.sys.mem_reserve", 0);
		final long RESERVED_BYTES = (long) size * 1024 * 1024;

		long Available = blockSize * availableBlocks * scale - RESERVED_BYTES * scale;
		if(Available <= 0) {
			return "0B";
		} else {
			return formatSizeBinary(Available);
		}
//		return formatSizeBinary(blockSize
//				* availableBlocks * scale - RESERVED_BYTES * scale);
	}

	/**
	 * 将字节数格式化为带单位的字符串（使用二进制单位：1024）
	 *
	 * @param bytes 要格式化的字节数
	 * @return 格式化字符串（如 "204.6 MB", "1.2 GB"）
	 */
	public static String formatSizeBinary(long bytes) {
		final long KB = 1024;
		final long MB = KB * 1024;
		final long GB = MB * 1024;

		LogUtils.d(TAG,"formatSizeBinary "+bytes);

		if (bytes >= GB) {
			return String.format(Locale.US, "%.1f GB", bytes / (double) GB);
		} else if (bytes >= MB) {
			return String.format(Locale.US, "%.1f MB", bytes / (double) MB);
		} else if (bytes >= KB) {
			return String.format(Locale.US, "%.1f KB", bytes / (double) KB);
		} else {
			return bytes + " B";
		}
	}

	/**
	 * 获取系统总内存
	 *
	 * @param context 可传入应用程序上下文。
	 * @return 总内存大单位为B。
	 */
	public static long getTotalMemorySize(Context context) {
		String dir = "/proc/meminfo";
		try {
			FileReader fr = new FileReader(dir);
			BufferedReader br = new BufferedReader(fr, 2048);
			String memoryLine = br.readLine();
			String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
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
	 * @param context 可传入应用程序上下文。
	 * @return 当前可用内存单位为B。
	 */
	public static long getAvailableMemory(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		am.getMemoryInfo(memoryInfo);
		return memoryInfo.availMem;
	}

	private static DecimalFormat fileIntegerFormat = new DecimalFormat("#0");
	private static DecimalFormat fileDecimalFormat = new DecimalFormat("#0.#");

	/**
	 * 单位换算
	 *
	 * @param size 单位为B
	 * @param isInteger 是否返回取整的单位
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
			fileSizeString = df.format((double) size / (1024 * 1024 * 1024)) + "GB";
		}
		return fileSizeString;
	}

}
