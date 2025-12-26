package com.htc.luminasettings.bean;

import java.io.Serializable;

/**
 * @author 作者：zgr
 * @version 创建时间：2017年8月7日 下午3:24:54 类说明
 */
public class VersionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int Code;

	private String Message;

	private VersionDataBean Data;

	public int getCode() {
		return Code;
	}

	public void setCode(int code) {
		Code = code;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public VersionDataBean getData() {
		return Data;
	}

	public void setData(VersionDataBean data) {
		Data = data;
	}

	public VersionBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VersionBean(int code, String message, VersionDataBean data) {
		super();
		Code = code;
		Message = message;
		Data = data;
	}

	@Override
	public String toString() {
		return "VersionBean [Code=" + Code + ", Message=" + Message + ", Data="
				+ Data + "]";
	}

}
