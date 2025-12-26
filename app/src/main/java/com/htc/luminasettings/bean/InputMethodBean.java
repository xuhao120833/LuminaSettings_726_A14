package com.htc.luminasettings.bean;

import java.io.Serializable;

/**
 * @author 作者：zgr
 * @version 创建时间：2017年3月21日 上午11:01:03 类说明
 */
public class InputMethodBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String prefkey;
	private String inputname;

	public String getPrefkey() {
		return prefkey;
	}

	public void setPrefkey(String prefkey) {
		this.prefkey = prefkey;
	}

	public String getInputname() {
		return inputname;
	}

	public void setInputname(String inputname) {
		this.inputname = inputname;
	}

	public InputMethodBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InputMethodBean(String prefkey, String inputname) {
		super();
		this.prefkey = prefkey;
		this.inputname = inputname;
	}

	@Override
	public String toString() {
		return "InputMethodBean [prefkey=" + prefkey + ", inputname="
				+ inputname + "]";
	}

}
