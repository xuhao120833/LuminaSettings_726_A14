package com.htc.luminasettings.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.htc.luminasettings.utils.NetWorkUtils;

/**
 * @author 作者：zgr
 * @version 创建时间：2017年7月4日 上午11:34:17 类说明 网络监听
 */
public class NetworkReceiver extends BroadcastReceiver {

	private NetWorkCallBack netWorkCallBack;
	
	
	public NetWorkCallBack getNetWorkCallBack() {
		return netWorkCallBack;
	}

	public void setNetWorkCallBack(NetWorkCallBack netWorkCallBack) {
		this.netWorkCallBack = netWorkCallBack;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			if(NetWorkUtils.isNetworkConnected(context.getApplicationContext())){
				netWorkCallBack.connect();
			}else{
				netWorkCallBack.disConnect();
			}
		}
	}
}
