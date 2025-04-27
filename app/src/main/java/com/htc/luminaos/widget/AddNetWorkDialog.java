package com.htc.luminaos.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.htc.luminaos.R;
import com.htc.luminaos.utils.InputMethodUtil;
import com.htc.luminaos.utils.LinkWifi;

import java.util.ArrayList;
import java.util.List;

public class AddNetWorkDialog extends BaseDialog {

	private LinkWifi linkWifi;
	
	private Context mContext;
	private WifiManager mWifiManager;
	private List<WifiConfiguration> wifiConfigurationList = new ArrayList<>();
	private static String TAG = "AddNetWorkDialog";

	public AddNetWorkDialog(Context context) {
		super(context);
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		this.mContext = context;
	}

	public AddNetWorkDialog(Context context, boolean cancelable,
                            OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.mContext = context;
	}

	public AddNetWorkDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		linkWifi=new LinkWifi(mContext);
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.layout_addnetwork, null);
		if (view != null) {
			setContentView(view);

			// 设置dialog大小 模块好的控件大小设置
			Window dialogWindow = getWindow();
			assert dialogWindow != null;
			dialogWindow.setWindowAnimations(R.style.right_in_right_out_anim);
			WindowManager manager = ((Activity) mContext).getWindowManager();
			WindowManager.LayoutParams params = dialogWindow.getAttributes();
			dialogWindow.setGravity(Gravity.CENTER);
			Display d = manager.getDefaultDisplay();
			params.width = (int) (d.getWidth()*0.4);
			params.height = (int) (d.getHeight()*0.6);
			dialogWindow.setAttributes(params);

			final EditText networkName_et = (EditText) view
					.findViewById(R.id.networkName_et);
			final EditText password_et = (EditText) view
					.findViewById(R.id.password_et);
			final TextView wifi_security = (TextView) view
					.findViewById(R.id.wifi_security);
			CheckBox wifi_password_checkbox = (CheckBox) view
					.findViewById(R.id.wifi_password_checkbox);
			TextView ok =  view.findViewById(R.id.ok);
			TextView cancel =  view.findViewById(R.id.cancel);
			final LinearLayout password_layout = (LinearLayout) view
					.findViewById(R.id.password_layout);
			wifi_security.setText(mContext.getString(R.string.none));
			password_layout.setVisibility(View.GONE);
			networkName_et.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode==KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() ==KeyEvent.ACTION_DOWN){
						wifi_security.requestFocus();
						wifi_security.requestFocusFromTouch();
						return true;
					}
					return false;
				}
			});

			InputMethodUtil.openInputMethod(mContext,networkName_et,new Handler());
			networkName_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					Log.d("editor","actionId "+actionId);
					if (actionId==EditorInfo.IME_ACTION_NEXT || actionId==EditorInfo.IME_ACTION_DONE
							||actionId==EditorInfo.IME_ACTION_SEND||actionId== EditorInfo.IME_ACTION_GO){
						InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
						inputMethodManager.hideSoftInputFromWindow(networkName_et.getWindowToken(), 0);
						networkName_et.clearFocus();
						wifi_security.requestFocus();
					}
					return false;
				}
			});

			password_et.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode==KeyEvent.KEYCODE_DPAD_UP && event.getAction() ==KeyEvent.ACTION_DOWN){
						wifi_security.requestFocus();
						wifi_security.requestFocusFromTouch();
						return true;
					}else if (keyCode==KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() ==KeyEvent.ACTION_DOWN){
						wifi_password_checkbox.requestFocus();
						wifi_password_checkbox.requestFocusFromTouch();
						return true;
					}
					return false;
				}
			});
			password_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					Log.d("editor","actionId "+actionId);
					if (actionId==EditorInfo.IME_ACTION_NEXT || actionId==EditorInfo.IME_ACTION_DONE
							||actionId==EditorInfo.IME_ACTION_SEND||actionId== EditorInfo.IME_ACTION_GO){

						String security = wifi_security.getText().toString();
						String msiid = networkName_et.getText().toString();
						String password=password_et.getText().toString();

						if (TextUtils.isEmpty(msiid)) {
							Toast.makeText(mContext,
									mContext.getString(R.string.ssidmsg),
									Toast.LENGTH_SHORT).show();
							return true;
						}
						if (!security.equals(mContext.getString(R.string.none))) {
							// 有密码
							if (password.length() < 8) {
								Toast.makeText(
										mContext,
										mContext.getResources().getString(
												R.string.passwordmessage),
										Toast.LENGTH_LONG).show();
								return true;
							}
						}

						WifiConfiguration isExitConf =  linkWifi.IsExsits(msiid);
						if (isExitConf!=null)
							mWifiManager.removeNetwork(isExitConf.networkId);

						int netID=linkWifi.CreateWifiInfo3(security, msiid, password);
						//L.d("netID ===> "+netID);
						wifiConfigurationList = mWifiManager.getConfiguredNetworks();
						for (WifiConfiguration c : wifiConfigurationList){
							mWifiManager.disableNetwork(c.networkId);

						}
						linkWifi.ConnectToNetID(netID);
						dismiss();
						return true;
					}
					return false;
				}
			});
			wifi_password_checkbox.setChecked(true);
			password_et.setTransformationMethod(HideReturnsTransformationMethod
					.getInstance());
			wifi_password_checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton arg0,
								boolean isChecked) {
							if (isChecked) {
								password_et
										.setTransformationMethod(HideReturnsTransformationMethod
												.getInstance());
							} else {
								password_et
										.setTransformationMethod(PasswordTransformationMethod
												.getInstance());
							}
						}
					});

			wifi_security.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					initPopupWindow(wifi_security, wifi_security,
							password_layout);
				}
			});

			cancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dismiss();
				}
			});

			ok.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String security = wifi_security.getText().toString();
					String msiid = networkName_et.getText().toString();
					String password=password_et.getText().toString();

					if (TextUtils.isEmpty(msiid)) {
						Toast.makeText(mContext,
								mContext.getString(R.string.ssidmsg),
								Toast.LENGTH_SHORT).show();
						return;
					}
					if (!security.equals(mContext.getString(R.string.none))) {
						// 有密码
						if (password.length() < 8) {
							Toast.makeText(
									mContext,
									mContext.getResources().getString(
											R.string.passwordmessage),
											Toast.LENGTH_LONG).show();
							return;
						}
					}

					WifiConfiguration isExitConf =  linkWifi.IsExsits(msiid);
					if (isExitConf!=null)
						mWifiManager.removeNetwork(isExitConf.networkId);

					int netID=linkWifi.CreateWifiInfo3(security, msiid, password);
					//L.d("netID ===> "+netID);
					wifiConfigurationList = mWifiManager.getConfiguredNetworks();
					for (WifiConfiguration c : wifiConfigurationList){
						mWifiManager.disableNetwork(c.networkId);

					}
					linkWifi.ConnectToNetID(netID);
					dismiss();
				}
			});

		}

	}



	public class Wpaadapter extends BaseAdapter {

		private ArrayList<String> wpalist = new ArrayList<String>();
		private LayoutInflater mInflater;

		public Wpaadapter(Context context) {
			mInflater = LayoutInflater.from(context);
			wpalist.add(context.getString(R.string.none));
			wpalist.add("WPA2-PSK");
			wpalist.add("WPA3-PSK");
			wpalist.add("WPA-PSK");
		}

		@Override
		public int getCount() {
			return wpalist.size();
		}

		@Override
		public String getItem(int arg0) {
			return wpalist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View convertview, ViewGroup arg2) {
			convertview = mInflater.inflate(
					R.layout.layout_wpa_popupwindow_item, null);
			TextView wpa_tv = (TextView) convertview.findViewById(R.id.wpa_tv);
			wpa_tv.setText(getItem(arg0));
			return convertview;
		}

	}

	private PopupWindow popupwindow = null;

	public void initPopupWindow(View showview, final TextView tv,
			final LinearLayout lay) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.layout_wpa_popupwindow, null);
		if (view != null) {
			ListView wpalistview = (ListView) view
					.findViewById(R.id.apwpa_listview);

			final Wpaadapter adapter = new Wpaadapter(mContext);
			wpalistview.setAdapter(adapter);

			popupwindow = new PopupWindow(view, showview.getWidth(),
					WindowManager.LayoutParams.WRAP_CONTENT);
			// 聚焦，设置点击可取消
			popupwindow.setFocusable(true);
			popupwindow.setOutsideTouchable(true);
			popupwindow.setBackgroundDrawable(new BitmapDrawable());
			popupwindow.setContentView(view);
			// 设置显示的位置
			popupwindow.showAsDropDown(showview);

			wpalistview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					String security = adapter.getItem(arg2);

					tv.setText(security);

					if (security.equals(mContext.getString(R.string.none))) {
						lay.setVisibility(View.GONE);
					} else {
						lay.setVisibility(View.VISIBLE);
					}

					popupwindow.dismiss();
				}
			});
		}

	}

}
