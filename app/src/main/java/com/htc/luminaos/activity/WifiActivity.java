package com.htc.luminaos.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.htc.luminaos.MyApplication;
import com.htc.luminaos.R;
import com.htc.luminaos.adapter.WifiFoundAdapter;
import com.htc.luminaos.databinding.ActivityWifiBinding;
import com.htc.luminaos.receiver.AddWifiCallBack;
import com.htc.luminaos.receiver.WifiChanagerReceiver;
import com.htc.luminaos.receiver.WifiEnabledReceiver;
import com.htc.luminaos.utils.ShareUtil;
import com.htc.luminaos.utils.Utils;
import com.htc.luminaos.widget.AddNetWorkDialog;
import com.htc.luminaos.widget.PasswordErrorDialog;
import com.htc.luminaos.widget.SpacesItemDecoration;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WifiActivity extends BaseActivity implements WifiEnabledReceiver.WifiEnabledCallBack, WifiChanagerReceiver.WifiChanagerCallBack, AddWifiCallBack {

    private ActivityWifiBinding wifiBinding;

    // wifi开关广播
    private IntentFilter wifiEnabledFilter = new IntentFilter(
            WifiManager.WIFI_STATE_CHANGED_ACTION);
    private WifiEnabledReceiver enabledReceiver;
    private boolean isStartAnim = false;
    // wifi变化
    private IntentFilter wifichanager = new IntentFilter();
    private WifiChanagerReceiver chanagerReceiver = null;
    private WifiFoundAdapter wifiFoundAdapter;
    private WifiManager mWifiManager;
    private ExecutorService singer = Executors.newFixedThreadPool(3);
    private static String TAG = "WifiActivity";
    private String action = "";
    private int addWifiId = -1;
    Dialog connectingDialog = null;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                List<ScanResult> newWifiList = (List<ScanResult>) msg.obj;

                if (wifiFoundAdapter == null) {
                    wifiFoundAdapter = new WifiFoundAdapter(newWifiList, WifiActivity.this);
                    wifiFoundAdapter.setHasStableIds(true);
                    wifiBinding.wifiRv.addItemDecoration(new SpacesItemDecoration(0,
                            0, SpacesItemDecoration.px2dp(5), 0));
                    wifiBinding.wifiRv.setAdapter(wifiFoundAdapter);
                } else {
                    wifiFoundAdapter.updateList(newWifiList);
                    wifiFoundAdapter.setCurrentScanResult(null);
                    wifiFoundAdapter.notifyDataSetChanged();
                }

                //wifi已经刷新完毕
                if (!action.isEmpty() && action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) && !newWifiList.isEmpty()) {
                    Log.d(TAG,"startanim(false) "+action);
                    startanim(false);
                    action = "";
                }
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiBinding = ActivityWifiBinding.inflate(LayoutInflater.from(this));
        setContentView(wifiBinding.getRoot());
        initReceiver();
        initView();
        initData();
        if (mWifiManager.isWifiEnabled()) {
            startanim(true);
        }
    }

    private void initView() {
        wifiBinding.rlWifiSwitch.setOnClickListener(this);
        wifiBinding.rlAddNetwork.setOnClickListener(this);
        wifiBinding.rlIpSettings.setOnClickListener(this);
        wifiBinding.rlWifiSwitch.setOnHoverListener(this);
        wifiBinding.rlAddNetwork.setOnHoverListener(this);
        wifiBinding.rlIpSettings.setOnHoverListener(this);
        wifiBinding.wifiSwitch.setOnClickListener(this);
        wifiBinding.rlRefreshNetwork.setOnClickListener(this);
        wifiBinding.rlRefreshNetwork.setOnHoverListener(this);
        wifiBinding.wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                singer.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (isChecked) {
                            if (!mWifiManager.isWifiEnabled()) {
                                mWifiManager.setWifiEnabled(true);
                                mWifiManager.startScan();
                            }
                        } else {
                            if (mWifiManager.isWifiEnabled())
                                mWifiManager.setWifiEnabled(false);
                        }
                    }
                });
            }
        });

        wifiBinding.rlIpSettings.setVisibility(MyApplication.config.wifiIpSettings ? View.VISIBLE : View.GONE);
    }

    private void initData() {
        try {
            if (mWifiManager == null)
                mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            wifiBinding.wifiSwitch.setChecked(mWifiManager.isWifiEnabled());
            if (mWifiManager.isWifiEnabled()) {
                mWifiManager.startScan();
                singer.execute(RefreshRunnable);
            }
//            mWifiManager.reconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_wifi_switch || id == R.id.wifi_switch) {
            boolean check = wifiBinding.wifiSwitch.isChecked();
            wifiBinding.wifiSwitch.setChecked(!check);
            if (!check) {
                startanim(true);
            } else {
                startanim(false);
                action = "";
            }
        } else if (id == R.id.rl_add_network) {
            AddNetWorkDialog addNetWorkDialog = new AddNetWorkDialog(this, R.style.DialogTheme, this);
            addNetWorkDialog.show();
        } else if (id == R.id.rl_refresh_network) {
            if(mWifiManager.isWifiEnabled()) {
                mWifiManager.startScan();
                startanim(true);
            }
        } else if (id == R.id.rl_ip_settings) {
            if (isWifiConnected()) {
                startNewActivity(WifiIpSetActivity.class);
            } else {
                Toast.makeText(this, getResources().getString(R.string.network_disconnect_tip), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null
                    && networkInfo.isConnected()
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }


    Runnable RefreshRunnable = new Runnable() {
        @Override
        public void run() {
            refreshWifiList();
        }
    };

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isInitialStickyBroadcast()) {//把注册完就发送的粘性(初始状态)广播过滤掉。
                return;
            }
            String action = intent.getAction();
            if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, " 收到SUPPLICANT_STATE_CHANGED_ACTION");
                //请求连接的状态发生改变，（已经加入到一个接入点）
                int supl_error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                Log.d(TAG," supl_error "+supl_error);
                if (supl_error == WifiManager.ERROR_AUTHENTICATING) {
                    Utils.logIntentExtras(intent, TAG);
                    Log.d(TAG, " 收到SUPPLICANT_STATE_CHANGED_ACTION,执行passwordErrorDialog " + supl_error);
                    if(connectingDialog!=null && connectingDialog.isShowing()) {
                        connectingDialog.dismiss();
                    }
                    showErrorDialog();
                    mWifiManager.removeNetwork(addWifiId);
                    addWifiId = -1;
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, " 收到NETWORK_STATE_CHANGED_ACTION");
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null) {
                    switch (networkInfo.getState()) {
                        case CONNECTING:
                            break;
                        case CONNECTED:
                            if(connectingDialog!=null && connectingDialog.isShowing()) {
                                connectingDialog.dismiss();
                            }
                            break;
                        case DISCONNECTED:
                            break;
                    }
                    Log.d("WIFI", "state " + networkInfo.getState());
                }

            }
        }
    };

    private void showErrorDialog() {
        Log.d(TAG, " showErrorDialog");
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.hint)) // 对话框标题
                .setMessage(getString(R.string.Authentication_error)) // 对话框内容
                .setPositiveButton(getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, " showErrorDialog onClick dialog.dismiss()");
                        dialog.dismiss(); // 点击“确定”按钮时，关闭对话框
                    }
                })
                .setCancelable(false) // 使对话框不能通过点击外部区域关闭
                .create();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Log.d(TAG, " showErrorDialog KEYCODE_BACK dialog.dismiss()");
                    dialog.dismiss(); // 按下返回键时关闭对话框
                    return true; // 表示已经处理了返回键事件
                }
                return false; // 没有处理返回键事件，继续传递事件
            }
        });
        dialog.show();
    }

    private void initReceiver() {
        enabledReceiver = new WifiEnabledReceiver(this);
        registerReceiver(enabledReceiver, wifiEnabledFilter);

        wifichanager.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        wifichanager.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifichanager.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        wifichanager.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        wifichanager.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
        wifichanager.addAction(WifiManager.RSSI_CHANGED_ACTION);
        wifichanager.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        wifichanager.setPriority(1000); // 设置优先级，最高为1000
        chanagerReceiver = new WifiChanagerReceiver(this);
        registerReceiver(chanagerReceiver, wifichanager);

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
//        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        registerReceiver(receiver, intentFilter);
    }

    private void destroyReceiver() {
        if (enabledReceiver != null) {
            unregisterReceiver(enabledReceiver);
        }
        if (chanagerReceiver != null) {
            unregisterReceiver(chanagerReceiver);
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    private void updateViewShow(boolean show) {
        if (show) {
            wifiBinding.wifiRv.setVisibility(View.VISIBLE);
        } else {
            wifiBinding.wifiRv.setVisibility(View.GONE);
            if (wifiFoundAdapter != null) {
                List<ScanResult> newWifiList = new ArrayList<>();
                wifiFoundAdapter.updateList(newWifiList);
                wifiFoundAdapter.notifyDataSetChanged();
            }
        }
    }

    private synchronized void refreshWifiList() {
        if (mWifiManager == null)
            mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> newWifiList = new ArrayList<>();
        List<ScanResult> wifiList = mWifiManager.getScanResults();
        if (wifiList == null)
            return;
        boolean isAdd;
        Log.d("xuhao", "wifiList.size() " + wifiList.size());
        if (wifiList.size() > 0) {
            for (int i = 0; i < wifiList.size(); i++) {
                isAdd = true;
                for (int j = 0; j < newWifiList.size(); j++) {
                    if (newWifiList.get(j).SSID.equals(wifiList.get(i).SSID)) {
                        isAdd = false;
                        if (newWifiList.get(j).level < wifiList.get(i).level) {
                            // ssid相同且新的信号更强
                            newWifiList.remove(j);
                            newWifiList.add(wifiList.get(i));
                            break;
                        }
                    }
                    if (newWifiList.get(j).SSID.equals("")
                            || newWifiList.get(j).SSID == null) {
                        newWifiList.remove(j);
                    }
                }
                if (isAdd) {
                    newWifiList.add(wifiList.get(i));
                }
            }
        }
        for (int i = 0; i < newWifiList.size(); i++) {
            for (int j = 0; j < newWifiList.size() - i - 1; j++) {
                ScanResult result;
                if (newWifiList.get(j).level < newWifiList.get(j + 1).level) {
                    result = newWifiList.get(j);
                    newWifiList.set(j, newWifiList.get(j + 1));
                    newWifiList.set(j + 1, result);
                }
            }
        }
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        if (configs != null && configs.size() > 0) {
            for (int i = 0; i < configs.size(); i++) {
                WifiConfiguration wifiConfiguration = configs.get(i);
                String configSSID = wifiConfiguration.SSID.replace("\"", "");
                if (!TextUtils.isEmpty(configSSID)) {
                    boolean exitflag = false;
                    for (int j = 0; j < newWifiList.size(); j++) {
                        ScanResult result;
                        if (configSSID.equals(newWifiList.get(j).SSID)) {
                            result = newWifiList.get(j);
                            newWifiList.remove(j);
                            newWifiList.add(0, result);
                            exitflag = true;
                            break;
                        }
                    }
                    if (!exitflag) {
                        try {
                            ScanResult str = null;
                            Constructor<ScanResult> ctor = ScanResult.class.getDeclaredConstructor(ScanResult.class);
                            ctor.setAccessible(true);
                            ScanResult sr = ctor.newInstance(str);
                            sr.SSID = configSSID;
                            String secure = (String) ShareUtil.get(WifiActivity.this, configSSID, "");
                            if (!"".equals(secure)) {
                                sr.capabilities = secure;
                            } else if (wifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE)) {
                                sr.capabilities = "NONE";
                            } else if (wifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
                                sr.capabilities = "WPA-PSK";
                            } else if (wifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA2_PSK)) {
                                sr.capabilities = "WPA2-PSK";
                            } else if (wifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP)) {
                                sr.capabilities = "WPA-EAP";
                            } else {
                                sr.capabilities = "NONE";
                            }
                            sr.level = -65;
                            newWifiList.add(0, sr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
        WifiInfo info = mWifiManager.getConnectionInfo();
        if (info != null) {
            String infoSSID = info.getSSID().replace("\"", "");
            if (!TextUtils.isEmpty(infoSSID)) {
                for (int i = 0; i < newWifiList.size(); i++) {
                    if (infoSSID.equals(newWifiList.get(i).SSID)) {

                        ScanResult result = newWifiList.get(i);
                        newWifiList.remove(i);
                        newWifiList.add(0, result);
                        break;
                    }
                }
            }
        }
        Message message = handler.obtainMessage();
        message.what = 1;
        message.obj = newWifiList;
        handler.sendMessage(message);
    }

    @Override
    public void refreshWifi(String action) {
        this.action = action;
        singer.execute(RefreshRunnable);
    }

    boolean connectingFlag = false;

    @Override
    public void wifiStatueChange(int state) {
        Log.d("state", String.valueOf(state));
        if (state == 2 && connectingFlag) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    inferConnectInfoLogin(WifiActivity.this);
                }
            }, 1000);

        }
        connectingFlag = state != 0 && state != 2;
    }


    @Override
    public void WifiConnectOrLose() {

    }

    @Override
    public void openWifi() {
        Log.d(TAG, "openWifi()");
        updateViewShow(true);
    }

    @Override
    public void closeWifi() {
        updateViewShow(false);
    }

    @Override
    protected void onDestroy() {
        destroyReceiver();
        super.onDestroy();
    }

    private void startanim(boolean startornot) {
        if (!startornot) {
            wifiBinding.refreshNet.setVisibility(View.GONE);
            wifiBinding.refreshNet.clearAnimation();
            return;
        }
        Animation anim = AnimationUtils.loadAnimation(
                WifiActivity.this, R.anim.search_anim);
        LinearInterpolator interpolator = new LinearInterpolator();
        anim.setInterpolator(interpolator);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                // TODO Auto-generated method stub
            }
        });

        wifiBinding.refreshNet.setVisibility(View.VISIBLE);
        wifiBinding.refreshNet.startAnimation(anim);
    }

    public static boolean inferConnectInfoLogin(Context context) {
        NetworkCapabilities wifiNetworkCapabilities = getActiveWifiNetworkCapabilities(context);
        if (wifiNetworkCapabilities != null) {
            if (wifiNetworkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)) {
                openCaptivePortalPage(context);
                return true;
            }
        }
        return false;
    }

    public static void openCaptivePortalPage(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        Network[] networks = connectivityManager.getAllNetworks();
        for (Network network : networks) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo.isConnected()
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                connectivityManager.startCaptivePortalApp(network);
                return;
            }
        }
    }

    @Nullable
    public static NetworkCapabilities getActiveWifiNetworkCapabilities(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = mConnectivityManager.getAllNetworks();
        for (Network network : networks) {
            NetworkInfo networkInfo = mConnectivityManager.getNetworkInfo(network);
            if (networkInfo.isConnected()
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return mConnectivityManager.getNetworkCapabilities(network);
            }
        }
        return null;
    }

    @Override
    public void addWifi(int netId, String ssid) {
//        addWifiId = netId;
//        connectingDialog = ConectingDialog(this,getString(R.string.connecting_ssid, ssid));
//        connectingDialog.show();
    }

    public Dialog ConectingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.loadding_layout);// 加载布局
        //改成用ProgressBar来实现旋转动画，不用原来的ImageView旋转方案，原来的会卡。2025/5/8
        TextView tipTextView = (TextView) v.findViewById(R.id.loadding_tv);// 提示文字
        tipTextView.setText(msg);// 设置加载信息
        Dialog connectingDialog = new Dialog(context, R.style.DialogTheme);// 创建自定义样式dialog
        connectingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.sendEmptyMessage(1);
            }
        });
        connectingDialog.setContentView(layout, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT));// 设置布局
        return connectingDialog;
    }
}