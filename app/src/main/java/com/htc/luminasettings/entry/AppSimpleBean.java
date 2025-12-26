package com.htc.luminasettings.entry;

import java.io.Serializable;

/**
 * @author 作�?�：hxd
 * @version 创建时间 2020/9/8 下午3:50:51 类说�?
 */
public class AppSimpleBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ID
	private int id;
	// 包名
	private String packagename;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPackagename() {
		return packagename;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

	public AppSimpleBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AppSimpleBean(int id, String packagename) {
		super();
		this.id = id;
		this.packagename = packagename;
	}

	@Override
	public String toString() {
		return "AppSimpleBean [id=" + id + ", packagename=" + packagename + "]";
	}

}
