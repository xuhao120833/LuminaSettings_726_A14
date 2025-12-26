package com.htc.luminasettings.receiver;

/**
 * wifi监听
 * @author zgr 
 *
 */
public interface MyWifiCallBack {

	/**
	 * 更新WIFI开关状态
	 * @param state 0 打开 1 关闭
	 */
	public void getWifiState(int state);
	
	/**
	 * 更新wifi信号
	 * @param count
	 */
	public void getWifiNumber(int count);
	
}
