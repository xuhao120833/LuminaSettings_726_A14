package com.htc.luminasettings.receiver;
/**
 * @author  作者：zgr
 * @version 创建时间：2016年11月8日 上午9:15:45
 * 类说明
 */
public interface AppCallBack {

	public void appChange(String packageName);
	public void appUnInstall(String packageName);
	public void appInstall(String packageName);
	
}
