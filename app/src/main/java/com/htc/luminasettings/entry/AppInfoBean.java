package com.htc.luminasettings.entry;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * @author 作者：zgr
 * @version 创建时间：2016年11月3日 下午5:48:27 类说明 apps
 */
public class AppInfoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * app名字
	 */
	private String appname;
	/**
	 * 包名
	 */
	private String apppackagename;
	/**
	 * MainActivity
	 */
	private String mname;
	/**
	 * icon
	 */
	private Drawable appicon;
	private ApplicationInfo applicationInfo;

	/**
	 * 是否选择
	 */
	private boolean isCheck = false;

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public String getApppackagename() {
		return apppackagename;
	}

	public void setApppackagename(String apppackagename) {
		this.apppackagename = apppackagename;
	}

	public String getMname() {
		return mname;
	}

	public void setMname(String mname) {
		this.mname = mname;
	}

	public Drawable getAppicon() {
		return appicon;
	}

	public void setAppicon(Drawable appicon) {
		this.appicon = appicon;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}


	public ApplicationInfo getApplicationInfo() {
		return applicationInfo;
	}

	public void setApplicationInfo(ApplicationInfo applicationInfo) {
		this.applicationInfo = applicationInfo;
	}

	public AppInfoBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AppInfoBean(String appname, String apppackagename, String mname,
                       Drawable appicon, boolean isCheck) {
		super();
		this.appname = appname;
		this.apppackagename = apppackagename;
		this.mname = mname;
		this.appicon = appicon;
		this.isCheck = isCheck;
	}

	@Override
	public String toString() {
		return "AppInfoBean [appname=" + appname + ", apppackagename="
				+ apppackagename + ", mname=" + mname + ", appicon=" + appicon
				+ ", isCheck=" + isCheck + "]";
	}

}
