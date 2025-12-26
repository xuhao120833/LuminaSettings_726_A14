package com.htc.luminasettings.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.htc.luminasettings.R;

/**
 * @author 作者：zgr
 * @version 创建时间：2017年3月6日 上午11:11:59 类说明 已连接
 */
public class CustomConfigSuccessDialog extends Dialog {

	private Context mContext;
	private String mssid;
	private String mstateinfo;
	private String msignalstrength;
	private String msecurity;
	private String mwifiipaddress;

	private OnClickConfigSuccessCallBack mcallback;

	public interface OnClickConfigSuccessCallBack {
		public void OnDisConnectClick();

		public void OnForgetClick();
	}

	public CustomConfigSuccessDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	public CustomConfigSuccessDialog(Context context, boolean cancelable,
                                     OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	public CustomConfigSuccessDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.layout_configsuccess_item, null);
		if (view != null) {
			setContentView(view);

			TextView configsuccess_ssid = (TextView) view
					.findViewById(R.id.configsuccess_ssid);
			TextView configsuccess_state_tv = (TextView) view
					.findViewById(R.id.configsuccess_state_tv);
			TextView signalstrength_tv = (TextView) view
					.findViewById(R.id.signalstrength_tv);
			TextView security_tv = (TextView) view
					.findViewById(R.id.security_tv);
			TextView wifiipaddress_tv = (TextView) findViewById(R.id.wifiipaddress_tv);
			TextView configsuccess_disconnect = view.findViewById(R.id.configsuccess_disconnect);
			TextView configsuccess_forget = view.findViewById(R.id.configsuccess_forget);
			TextView configsuccess_cancel = view.findViewById(R.id.configsuccess_cancel);

			configsuccess_ssid.setText(mssid);
			configsuccess_state_tv.setText(mstateinfo);
			signalstrength_tv.setText(msignalstrength);
			security_tv.setText(msecurity);
			wifiipaddress_tv.setText(mwifiipaddress);

			// 设置dialog大小 模块好的控件大小设置
			Window dialogWindow = getWindow();
			WindowManager manager = ((Activity) mContext).getWindowManager();
			WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
			dialogWindow.setGravity(Gravity.CENTER);// 设置对话框位置
			Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
			params.width = (int) (d.getWidth() * 0.47); // 宽度设置为屏幕的0.8，根据实际情况调整
			params.height = (int) (d.getHeight() * 0.5);
			dialogWindow.setAttributes(params);

			if (mcallback != null) {
				configsuccess_disconnect
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								mcallback.OnDisConnectClick();
								dismiss();
							}
						});

				configsuccess_forget
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								mcallback.OnForgetClick();
								dismiss();
							}
						});

				configsuccess_cancel
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								dismiss();
							}
						});
			}

		}
	}

	public void setContent(String ssid, String stateinfo,
                           String signalstrength, String security, String wifiipaddress) {
		this.mssid = ssid;
		this.mstateinfo = stateinfo;
		this.msignalstrength = signalstrength;
		this.msecurity = security;
		this.mwifiipaddress = wifiipaddress;
	}

	public void setOnClickCallBack(OnClickConfigSuccessCallBack callback) {
		this.mcallback = callback;
	}

}
