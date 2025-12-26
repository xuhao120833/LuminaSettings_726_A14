package com.htc.luminasettings.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author 作者：zgr
 * @version 创建时间：2017年8月7日 下午3:26:18 类说明
 */
public class VersionDataBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String NewestVersion;

	private boolean IsForceUpdate;

	private String Size;

	private List<VersionPackagesBean> Packages;

	public String getNewestVersion() {
		return NewestVersion;
	}

	public void setNewestVersion(String newestVersion) {
		NewestVersion = newestVersion;
	}

	public boolean getIsForceUpdate() {
		return IsForceUpdate;
	}

	public void setIsForceUpdate(boolean isForceUpdate) {
		IsForceUpdate = isForceUpdate;
	}

	public String getSize() {
		return Size;
	}

	public void setSize(String size) {
		Size = size;
	}

	public List<VersionPackagesBean> getPackages() {
		return Packages;
	}

	public void setPackages(List<VersionPackagesBean> packages) {
		Packages = packages;
	}

	public VersionDataBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VersionDataBean(String newestVersion, boolean isForceUpdate,
			String size, List<VersionPackagesBean> packages) {
		super();
		NewestVersion = newestVersion;
		IsForceUpdate = isForceUpdate;
		Size = size;
		Packages = packages;
	}

	@Override
	public String toString() {
		return "VersionDataBean [NewestVersion=" + NewestVersion
				+ ", IsForceUpdate=" + IsForceUpdate + ", Size=" + Size
				+ ", Packages=" + Packages + "]";
	}

}
