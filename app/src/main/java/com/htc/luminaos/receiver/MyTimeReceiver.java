package com.htc.luminaos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 时间变化广播
 * @author zgr
 *
 */
public class MyTimeReceiver extends BroadcastReceiver {

	private MyTimeCallBack mCallBack;
	
	public MyTimeReceiver(MyTimeCallBack callback){
		this.mCallBack=callback;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action=intent.getAction();
//		if(action!=null&&action.equals(Constants.TIME_TICK)){
//			//时间变化
//			mCallBack.changeTime();
//		}
	
		if(action!=null){
		     if (Intent.ACTION_TIME_CHANGED.equals(action)
	                    || Intent.ACTION_TIMEZONE_CHANGED.equals(action)
	                    || Intent.ACTION_LOCALE_CHANGED.equals(action)
	                    || Intent.ACTION_CONFIGURATION_CHANGED.equals(action)
	                    || Intent.ACTION_USER_SWITCHED.equals(action)
	                    ||action.equals(Intent.ACTION_TIME_TICK)) {
		    	 //LogUtils.i(TAG, action);
		    	 mCallBack.changeTime();
		     }
		}
		
	}

}
