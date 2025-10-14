package com.htc.luminaos.activity;

import android.app.Activity;
import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PrjScreen;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.htc.luminaos.R;
import com.htc.luminaos.databinding.ActivityBluetoothSpeakerBinding;
import com.htc.luminaos.utils.LogUtils;

import java.io.FileInputStream;

public class BluetoothSpeakerActivity extends Activity {
    private final String TAG = "BluetoothSpeakerActivity";
    private BluetoothAdapter bluetoothAdapter;
    private WifiManager wifiManager;
    ActivityBluetoothSpeakerBinding bluetoothSpeakerBinding;
    private boolean direct_exit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothSpeakerBinding = ActivityBluetoothSpeakerBinding.inflate(LayoutInflater.from(this));
        setContentView(bluetoothSpeakerBinding.getRoot());
        bluetoothSpeakerBinding.deviceNameTv.setText(SystemProperties.get("persist.sys.deviceName","ADT-3"));
        bluetoothSpeakerBinding.offScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                direct_exit =false;
                PrjScreen.set_led_power(3,0);
            }
        });
        bluetoothAdapter  = BluetoothAdapter.getDefaultAdapter();
        SwitchBluetoothSpeaker(1);
        initReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        direct_exit =true;
        bluetoothSpeakerBinding.offScreen.requestFocus();
        bluetoothSpeakerBinding.offScreen.requestFocusFromTouch();
        super.onResume();
    }

    private void initReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothA2dpSink.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode()==KeyEvent.KEYCODE_BACK && event.getAction() ==KeyEvent.ACTION_UP) {
            if (!direct_exit) {
                direct_exit =true;
                PrjScreen.set_led_power(3, 1);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void SwitchBluetoothSpeaker(int mode) {
        if (!bluetoothAdapter.isEnabled() && mode == 1)
            bluetoothAdapter.enable();

        Intent intent = new Intent("android.intent.switch_bt_speaker");
        intent.putExtra("changetosink",mode==1);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        sendBroadcast(intent);
        //PrjScreen.set_led_power(3,mode==1?0:1);
        /*updateBrightness(mode==1?10:brightness);
        PrjScreen.set_fan_level(mode==1?1:fan_level);*/
        if (wifiManager==null)
            wifiManager =(WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled() && mode==1)
            wifiManager.setWifiEnabled(false);
        else if (!wifiManager.isWifiEnabled() && mode==0)
            wifiManager.setWifiEnabled(true);
    }




    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothA2dpSink.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
                if (state == BluetoothProfile.STATE_CONNECTED) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    bluetoothSpeakerBinding.tv.setText(getString(R.string.connected) +"-"+ device.getName());
                } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                    bluetoothSpeakerBinding.tv.setText(getString(R.string.not_connected));
                }
            }
        }
    };

    private  String get_devval(String fname)
    {
        try
        {
            FileInputStream fin = new FileInputStream(fname);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            fin.close();
            String res = new String(buffer);
            return res.trim();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPause() {
        LogUtils.d(TAG,"onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        LogUtils.d(TAG,"onStop()");
        PrjScreen.set_led_power(3,1);
        SwitchBluetoothSpeaker(0);
        unregisterReceiver(broadcastReceiver);
        finish();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtils.d(TAG,"onDestroy()");
        super.onDestroy();
    }
}