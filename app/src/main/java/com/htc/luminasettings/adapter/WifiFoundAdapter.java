package com.htc.luminasettings.adapter;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htc.luminasettings.R;
import com.htc.luminasettings.activity.WifiActivity;
import com.htc.luminasettings.utils.LinkWifi;
import com.htc.luminasettings.utils.LogUtils;
import com.htc.luminasettings.widget.CustomConfigDisConnectDialog;
import com.htc.luminasettings.widget.CustomConfigSuccessDialog;
import com.htc.luminasettings.widget.WifiConnectDialog;
import com.htc.luminasettings.widget.WifiConnectNoPasswordDialog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:
 * Date:
 * Description:
 */
public class WifiFoundAdapter extends RecyclerView.Adapter<WifiFoundAdapter.MyViewHolder> implements View.OnHoverListener {

    private List<ScanResult> scanResultList = new ArrayList<>();
    private Context mContext;
    private LinkWifi linkWifi;
    private ScanResult mCurrentScanResult;
    private WifiManager wifiManager = null;

    private ConnectivityManager mConnectivityManager;

    private String TAG = "WifiFoundAdapter";

    public WifiFoundAdapter(List<ScanResult> scanResultList, Context mContext){
        this.scanResultList = scanResultList;
        this.mContext = mContext;
        linkWifi = new LinkWifi(mContext);
        this.wifiManager = (WifiManager) mContext
                .getSystemService(Service.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
    }

    public void updateList(List<ScanResult> scanResultList){
        this.scanResultList = scanResultList;
    }
    public void setCurrentScanResult(ScanResult currentScanResult) {
        this.mCurrentScanResult = currentScanResult;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wifi_found_item,null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
       final ScanResult scanResult = scanResultList.get(i);

        WifiConfiguration wifiConfiguration = linkWifi.IsExsits(scanResult.SSID);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String state_str="";
        if (wifiConfiguration != null) {
            state_str = "(" + mContext.getString(R.string.wifi_remembered)
                    + ")";
        }
        myViewHolder.wifi_status_icon.setVisibility(View.GONE);
        if (mCurrentScanResult != null) {
            if (mCurrentScanResult.SSID.equals(scanResult.SSID)) {
                state_str = mContext.getResources().getString(
                        R.string.connecting);
            }
        } else {
            if (wifiConfiguration != null) {
                if (wifiInfo != null
                        && wifiInfo.getNetworkId() == wifiConfiguration.networkId) {
                    // LogUtils.i("zouguanrong", "--wifiinfo not null");
                    if (wifiInfo.getIpAddress() != 0) {
                        state_str = mContext.getResources().getString(
                                R.string.isconnected);
                        myViewHolder.wifi_status_icon.setVisibility(View.VISIBLE);
//							WIFIActivity.isShow = false;
                    } else {
                        String summary = getSettingsSummary(wifiConfiguration);
                        if (!TextUtils.isEmpty(summary)) {
                            state_str = summary;
                        } else {
                            state_str = mContext.getResources().getString(
                                    R.string.connecting);
                        }
                    }
                }
            }
        }

        int level = scanResult.level;
        LogUtils.d(TAG," 信号强度更新 "+level);
        if (level < -85){
            myViewHolder.wifi_level.setBackgroundResource(R.drawable.wifi_custom_1);
        }else if (level < -70){
            myViewHolder.wifi_level.setBackgroundResource(R.drawable.wifi_custom_2);
        }else if (level < -60){
            myViewHolder.wifi_level.setBackgroundResource(R.drawable.wifi_custom_3);
        }else if (level < -50){
            myViewHolder.wifi_level.setBackgroundResource(R.drawable.wifi_custom_4);
        }else {
            myViewHolder.wifi_level.setBackgroundResource(R.drawable.wifi_custom_4);
        }

        myViewHolder.wifi_name.setText(scanResult.SSID);
        myViewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configWifiRelay(scanResult);
            }
        });
        myViewHolder.wifi_status.setText(state_str);
        /*myViewHolder.wifi_status.setVisibility(View.GONE);
        if (mCurrentScanResult != null) {
            if (mCurrentScanResult.SSID.equals(scanResult.SSID)) {
                myViewHolder.wifi_status.setVisibility(View.VISIBLE);
            }
        }*/
        if (scanResult.capabilities.contains("WPA2-PSK") ||
                scanResult.capabilities.contains("WPA-PSK") ||
                scanResult.capabilities.contains("WPA3-PSK") ||
                scanResult.capabilities.contains("SAE") ||
                scanResult.capabilities.contains("WEP") ||
                scanResult.capabilities.contains("WPA-EAP") ||
                scanResult.capabilities.contains("EAP")){
            myViewHolder.lock_iv.setVisibility(View.VISIBLE);
        }else {
            myViewHolder.lock_iv.setVisibility(View.GONE);
        }
        myViewHolder.rl_item.setOnHoverListener(this);
    }

    @Override
    public int getItemCount() {
        return scanResultList.size();
    }


    /**
     * 链接网络
     *
     * @param wifiinfo
     *            wifi信息
     */
    private void configWifiRelay(final ScanResult wifiinfo) {

        String wifilevel = "";
        if (wifiinfo.level < -90) {
            wifilevel = mContext.getResources().getString(R.string.weak);
        } else if (wifiinfo.level < -85) {
            wifilevel = mContext.getResources().getString(R.string.general);
        } else if (wifiinfo.level < -70) {
            wifilevel = mContext.getResources().getString(R.string.general);
        } else if (wifiinfo.level < -60) {
            wifilevel = mContext.getResources().getString(R.string.strong);
        } else if (wifiinfo.level < -50) {
            wifilevel = mContext.getResources().getString(R.string.strong);
        } else {
            wifilevel = mContext.getResources().getString(R.string.strong);
        }

        String capabilities = "";
        if (wifiinfo.capabilities.contains("WPA2-PSK")) {
            // WPA-PSK加密
            capabilities ="WPA2-PSK";
        }
        if (wifiinfo.capabilities.contains("WPA-PSK")) {
            // WPA-PSK加密
            capabilities =capabilities.equals("")?"WPA-PSK":capabilities+"/WPA-PSK";
        }
        if (wifiinfo.capabilities.contains("SAE") || wifiinfo.capabilities.contains("WPA3-PSK")) {
            // WPA3-PSK加密
            capabilities =capabilities.equals("")?"WPA3-PSK":capabilities+"/WPA3-PSK";
        }
        if (wifiinfo.capabilities.contains("WPA-EAP")) {
            // WPA-EAP加密
            capabilities =capabilities.equals("")?"WPA-EAP":capabilities+"/WPA-EAP";
        }
        if (wifiinfo.capabilities.contains("WEP")) {
            // WEP加密
            capabilities =capabilities.equals("")?"WEP":capabilities+"/WEP";
        }
        String finalCapabilities = capabilities;
        // 如果本机已经配置过的话
        if (linkWifi.IsExsits(wifiinfo.SSID) != null) {
            final int netID = linkWifi.IsExsits(wifiinfo.SSID).networkId;
            // 如果目前连接了此网络
            if (wifiManager.getConnectionInfo().getNetworkId() == netID) {
                if (WifiActivity.inferConnectInfoLogin(mContext))
                    return;

                CustomConfigSuccessDialog configSuccessDialog = new CustomConfigSuccessDialog(
                        mContext, R.style.DialogTheme);

                String ssid = wifiinfo.SSID;
                String stateinfo = mContext.getString(R.string.wifistate);
                String signalstrength = wifilevel;
                String security = "";
                if (capabilities.equals("")) {
                    capabilities = mContext.getResources().getString(R.string.none);
                }
                security = capabilities;

                String wifiipaddress = Formatter.formatIpAddress(wifiManager
                        .getConnectionInfo().getIpAddress());
                configSuccessDialog.setContent(ssid, stateinfo, signalstrength,
                        security, wifiipaddress);

                configSuccessDialog
                        .setOnClickCallBack(new CustomConfigSuccessDialog.OnClickConfigSuccessCallBack() {

                            @Override
                            public void OnForgetClick() {
                                
                                wifiManager.removeNetwork(netID);
                                // 取消这个网络的保存
                                wifiManager.saveConfiguration();
                            }

                            @Override
                            public void OnDisConnectClick() {
                                
                                wifiManager.disconnect();
                            }
                        });

                configSuccessDialog.cancel();
                configSuccessDialog.show();
                return;
            } else {

                CustomConfigDisConnectDialog configDisConnectDialog = new CustomConfigDisConnectDialog(
                        mContext, R.style.DialogTheme);

                String ssid = wifiinfo.SSID;
                String signalstrength = wifilevel;
                String security = "";
                if (capabilities.equals("")) {
                    capabilities = mContext.getResources().getString(R.string.none);
                }
                security = capabilities;

                configDisConnectDialog.setContent(ssid, signalstrength,
                        security);

                configDisConnectDialog
                        .setOnClickCallBack(new CustomConfigDisConnectDialog.OnClickConfigCallBack() {

                            @Override
                            public void OnForgetClick() {
                                
                                wifiManager.removeNetwork(netID);
                                // 取消这个网络的保存
                                wifiManager.saveConfiguration();
                            }

                            @Override
                            public void OnConnectClick() {
                                
                                WifiConfiguration config = linkWifi
                                        .IsExsits(wifiinfo.SSID);

                                if (config != null) {

                                    setCurrentScanResult(wifiinfo);
                                    notifyDataSetChanged();
                                    linkWifi.setMaxPriority(config);
                                    linkWifi.ConnectToNetID(config.networkId);
                                }
                            }
                        });

                configDisConnectDialog.cancel();
                configDisConnectDialog.show();

                return;
            }
        }

        // 判断安全性
        if (!capabilities.equals("")) {
            // 有密码
            WifiConnectDialog wifiConnectDialog = new WifiConnectDialog(
                    mContext, R.style.DialogTheme,null);
            String ssid = wifiinfo.SSID;
            //String signalstrength = wifilevel;
            String security = "";
            if (capabilities.equals("")) {
                capabilities = mContext.getResources().getString(R.string.none);
            }
            security = capabilities;

            wifiConnectDialog.setConnectName(ssid);

            wifiConnectDialog.setOnClickCallBack(new WifiConnectDialog.OnWifiConnectCallBack() {

                        @Override
                        public int onClick(String password) {
                            
                            if (password.length() < 8) {

                                /*Toast.makeText(
                                        mContext,
                                        mContext.getResources().getString(
                                                R.string.passwordmessage),
                                        Toast.LENGTH_LONG).show();*/
                            } else {

                                // wifiManager.disconnect();


                                setCurrentScanResult(wifiinfo);
                                notifyDataSetChanged();

                                // 此处加入连接wifi代码
                                int netID = linkWifi.CreateWifiInfo2(wifiinfo,
                                        password, finalCapabilities);
                                // System.out.println("wifipwd is:"+password);
                                linkWifi.ConnectToNetID(netID);

                                return netID;
                            }
                            return -1;
                        }
                    });

            wifiConnectDialog.cancel();
            wifiConnectDialog.show();
        } else {
            WifiConnectNoPasswordDialog wifiConnectNoPasswordDialog = new WifiConnectNoPasswordDialog(
                    mContext, R.style.DialogTheme,null);
            wifiConnectNoPasswordDialog.setConnectName(wifiinfo.SSID);
            wifiConnectNoPasswordDialog.setOnClickCallBack(new WifiConnectNoPasswordDialog.OnWifiConnectNoPasswordCallBack() {

                @Override
                public void onClick() {
                    

                   setCurrentScanResult(wifiinfo);
                    notifyDataSetChanged();


                    int netID = linkWifi.CreateWifiInfo2(wifiinfo, "",finalCapabilities);

                    linkWifi.ConnectToNetID(netID);
                }

            });
            wifiConnectNoPasswordDialog.cancel();
            wifiConnectNoPasswordDialog.show();
        }

    }

    private String getSettingsSummary(WifiConfiguration config) {
        StringBuffer summary = new StringBuffer("");
        if (config != null) {

            android.net.wifi.WifiConfiguration.NetworkSelectionStatus networkSelectionStatus = config
                    .getNetworkSelectionStatus();
            // LogUtils.i("zouguanrong",
            // "getNetworkSelectionDisableReason-->"+networkSelectionStatus.getNetworkSelectionDisableReason());
            switch (networkSelectionStatus.getNetworkSelectionDisableReason()) {

                case WifiConfiguration.NetworkSelectionStatus.DISABLED_AUTHENTICATION_FAILURE:
                    summary.append(mContext
                            .getString(R.string.wifi_disabled_password_failure));
                    break;
                case WifiConfiguration.NetworkSelectionStatus.DISABLED_BY_WIFI_MANAGER:
                case WifiConfiguration.NetworkSelectionStatus.DISABLED_AUTHENTICATION_NO_CREDENTIALS:
                    summary.append(mContext
                            .getString(R.string.wifi_disabled_generic));
                    break;
                // case
                // WifiConfiguration.NetworkSelectionStatus.DISABLED_NO_INTERNET:
                // summary.append(mContext.getString(R.string.wifi_no_internet));
                // break;
                case WifiConfiguration.NetworkSelectionStatus.DISABLED_DHCP_FAILURE:
                    summary.append(mContext
                            .getString(R.string.wifi_disabled_network_failure));
                    break;
                case WifiConfiguration.NetworkSelectionStatus.DISABLED_ASSOCIATION_REJECTION:
                    summary.append(mContext
                            .getString(R.string.wifi_disabled_generic));
                    break;

                case WifiConfiguration.NetworkSelectionStatus.NETWORK_SELECTION_ENABLED:

                    switch (config.status) {
                        case WifiConfiguration.Status.CURRENT:
                            // LogUtils.i("zouguanrong", "--SSID-->" + config.SSID
                            // + "-->CURRENT");
                            break;

                        case WifiConfiguration.Status.ENABLED:
                            // LogUtils.i("zouguanrong", "--SSID-->" + config.SSID
                            // + "-->ENABLED");
//					if (WIFIActivity.isShow) {
//						WIFIActivity.isShow = false;
//						summary.append(mContext
//								.getString(R.string.wifi_disabled_password_ver));
//					}
                            break;

                        case WifiConfiguration.Status.DISABLED:
                            // LogUtils.i("zouguanrong", "--SSID-->" + config.SSID
                            // + "-->DISABLED");
                            break;
                    }
                    break;
            }

        }
        return summary.toString();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView wifi_level;
        TextView wifi_name;
        TextView wifi_status;
        ImageView lock_iv;
        ImageView wifi_status_icon;
        RelativeLayout rl_item;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            wifi_level = itemView.findViewById(R.id.wifi_level);
            wifi_name = itemView.findViewById(R.id.wifi_name);
            lock_iv = itemView.findViewById(R.id.lock_iv);
            wifi_status_icon = itemView.findViewById(R.id.wifi_status_icon);
            wifi_status = itemView.findViewById(R.id.wifi_status);
            rl_item = itemView.findViewById(R.id.rl_item);
        }
    }

    /**
     * 获取信号
     *
     * @param context
     * @return
     */
    public int getStrength(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info.getBSSID() != null) {
            int strength = WifiManager.calculateSignalLevel(info.getRssi(), 4);
            // 链接速度
            // int speed = info.getLinkSpeed();
            // 链接速度单位
            // String units = WifiInfo.LINK_SPEED_UNITS;
            // Wifi源名称
            // String ssid = info.getSSID();
            LogUtils.d(TAG,"getStrength信号强度更新 "+strength);
            return strength;
        }
        return 0;
    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        int what = event.getAction();
        switch (what) {
            case MotionEvent.ACTION_HOVER_ENTER: // 鼠标进入view
                v.requestFocus();
                break;
            case MotionEvent.ACTION_HOVER_MOVE: // 鼠标在view上
                break;
            case MotionEvent.ACTION_HOVER_EXIT: // 鼠标离开view
                break;
        }
        return false;
    }

}
