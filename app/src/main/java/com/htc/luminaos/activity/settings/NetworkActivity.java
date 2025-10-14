package com.htc.luminaos.activity.settings;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.htc.luminaos.MyApplication;
import com.htc.luminaos.R;
import com.htc.luminaos.activity.BaseActivity;
import com.htc.luminaos.activity.settings.HotspotActivity;
import com.htc.luminaos.activity.settings.WifiActivity;
import com.htc.luminaos.activity.settings.WiredActivity;
import com.htc.luminaos.databinding.ActivityNetworkBinding;

public class NetworkActivity extends BaseActivity {

    ActivityNetworkBinding networkBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkBinding = ActivityNetworkBinding.inflate(LayoutInflater.from(this));
        setContentView(networkBinding.getRoot());
        initView();
    }
    private void initView(){
        networkBinding.rlWirelessNetwork.setOnClickListener(this);
        networkBinding.rlHotspot.setOnClickListener(this);
        networkBinding.rlWiredNetwork.setOnClickListener(this);

        networkBinding.rlWirelessNetwork.setOnHoverListener(this);
        networkBinding.rlHotspot.setOnHoverListener(this);
        networkBinding.rlWiredNetwork.setOnHoverListener(this);

        networkBinding.rlWirelessNetwork.requestFocus();
        networkBinding.rlWirelessNetwork.requestFocusFromTouch();
        if (isNetworkConnect()){
            networkBinding.rlWiredNetwork.setVisibility(View.VISIBLE);
        }else {
            networkBinding.rlWiredNetwork.setVisibility(View.GONE);
        }
        if (MyApplication.config.settings_hotspot) {
            networkBinding.rlHotspot.setVisibility(View.VISIBLE);
        } else {
            networkBinding.rlHotspot.setVisibility(View.GONE);
        }
    }

    private boolean isNetworkConnect(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        return networkInfo!=null&& networkInfo.isConnected();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_wireless_network){
            startNewActivityWifi(WifiActivity.class);
        }else if (v.getId() == R.id.rl_wired_network){
            startNewActivity(WiredActivity.class);
        }else if (v.getId() == R.id.rl_hotspot){
            startNewActivity(HotspotActivity.class);
        }
    }
}