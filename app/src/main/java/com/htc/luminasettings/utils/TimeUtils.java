package com.htc.luminasettings.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.text.format.DateFormat;

import com.htc.luminasettings.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author  作者：zgr
 * @version 创建时间：2016年11月4日 下午12:06:19
 * 类说明
 */
public class TimeUtils {


	/**
	 * 获取当前时间 格式HH:mm
	 *
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentTime(Context context) {
		String time = "";
		if(DateFormat.is24HourFormat(context)){
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			time = sdf.format(new Date());
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
			time = sdf.format(new Date())+"|"+getNowAmPm(context);

		}
		return time;
	}

	//获取上下午
	public static String getNowAmPm(Context context) {
		Calendar cal = Calendar.getInstance();
		Date now = Calendar.getInstance().getTime();
		cal.setTime(now);
		int i = cal.get(Calendar.AM_PM);
		switch (i) {
			case 0:
				return context.getString(R.string.day_am);
			case 1:
				return context.getString(R.string.day_pm);
			default:
				return "";
		}
	}


	/**
	 * 获取当前时间 格式HH:mm:ss
	 *
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentTime1() {
		String time = "";
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		time = sdf.format(new Date());
		return time;
	}

	/**
	 * 获取当前日期 yyyy-MM-dd
	 *
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentDate() {
		String time = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		time = sdf.format(new Date());
		return time;
	}

	/**
	 * 获取当前日期MM/dd
	 *
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentDate1() {
		String time = "";
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
		time = sdf.format(new Date());
		return time;
	}

	public static String getCurrentYear() {
		String time = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		time = sdf.format(new Date());
		return time;
	}

	public static String getCurrentDay() {
		String time = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		time = sdf.format(new Date());
		return time;
	}

	/**
	 * 获取当前星期 E
	 *
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentWeek() {
		String time = "";
		SimpleDateFormat sdf = new SimpleDateFormat("E");
		time = sdf.format(new Date());
		return time;
	}

	public static String getCurrentMonth() {
		String time = "";
		SimpleDateFormat sdf = new SimpleDateFormat("E. MMM", Locale.UK);
		sdf.setTimeZone(new java.util.SimpleTimeZone(0, "GMT"));
		time = sdf.format(new Date());
		return time;
	}

	public static String getSystemTimeFotmat(Context context) {
		String str = "";
		return DateFormat.getTimeFormat(context.getApplicationContext()).format(new Date());
	}

	public static String getSystemDateFotmat(Context context){

		String result="";

		try {
			SimpleDateFormat dateFormat =new SimpleDateFormat(getDateFormat(context));
			result=dateFormat.format(new Date());
		} catch (Exception e) {
			try {
				java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context.getApplicationContext());
				result=dateFormat.format(new Date());
			} catch (Exception e2) {
				// TODO: handle exception
				result=getCurrentDate1();
			}
		}

		return result;

	}

	public static String getDateFormat(Context context) {
		return Settings.System.getString(
				context.getContentResolver(),
				Settings.System.DATE_FORMAT);
	}
	
}
