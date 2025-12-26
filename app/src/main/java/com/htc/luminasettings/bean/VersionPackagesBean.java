package com.htc.luminasettings.bean;

import java.io.Serializable;

/**
 * @author 作者：zgr
 * @version 创建时间：2017年8月7日 下午3:27:22 类说明
 */
public class VersionPackagesBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String FingerPrint;

	private String Description;

	private String Type;

	private String SrcVersion;

	private String Carrier;

	private String Channel;

	private String Model;

	private String Version;

	private String URL;

	private String Validation;

	private String Size;

	public String getFingerPrint() {
		return FingerPrint;
	}

	public void setFingerPrint(String fingerPrint) {
		FingerPrint = fingerPrint;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getSrcVersion() {
		return SrcVersion;
	}

	public void setSrcVersion(String srcVersion) {
		SrcVersion = srcVersion;
	}

	public String getCarrier() {
		return Carrier;
	}

	public void setCarrier(String carrier) {
		Carrier = carrier;
	}

	public String getChannel() {
		return Channel;
	}

	public void setChannel(String channel) {
		Channel = channel;
	}

	public String getModel() {
		return Model;
	}

	public void setModel(String model) {
		Model = model;
	}

	public String getVersion() {
		return Version;
	}

	public void setVersion(String version) {
		Version = version;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getValidation() {
		return Validation;
	}

	public void setValidation(String validation) {
		Validation = validation;
	}

	public String getSize() {
		return Size;
	}

	public void setSize(String size) {
		Size = size;
	}

	public VersionPackagesBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VersionPackagesBean(String fingerPrint, String description,
			String type, String srcVersion, String carrier, String channel,
			String model, String version, String uRL, String validation,
			String size) {
		super();
		FingerPrint = fingerPrint;
		Description = description;
		Type = type;
		SrcVersion = srcVersion;
		Carrier = carrier;
		Channel = channel;
		Model = model;
		Version = version;
		URL = uRL;
		Validation = validation;
		Size = size;
	}

	@Override
	public String toString() {
		return "VersionPackagesBean [FingerPrint=" + FingerPrint
				+ ", Description=" + Description + ", Type=" + Type
				+ ", SrcVersion=" + SrcVersion + ", Carrier=" + Carrier
				+ ", Channel=" + Channel + ", Model=" + Model + ", Version="
				+ Version + ", URL=" + URL + ", Validation=" + Validation
				+ ", Size=" + Size + "]";
	}

}
