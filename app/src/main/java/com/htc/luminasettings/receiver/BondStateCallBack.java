package com.htc.luminasettings.receiver;

import android.bluetooth.BluetoothDevice;

/**
 * @author  作者：zgr
 * @version 创建时间：2017年3月27日 下午4:13:14
 * 类说明
 */
public interface BondStateCallBack {

	/**
	 * 配对状态
	 * @param state   1:配对成功    2：正在配对   3：删除配对
	 * @param device
	 */
	public void bondState(int state, BluetoothDevice device);
	
}
