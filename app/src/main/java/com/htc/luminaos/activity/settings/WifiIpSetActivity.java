package com.htc.luminaos.activity.settings;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.net.IpConfiguration;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.htc.luminaos.R;
import com.htc.luminaos.activity.BaseActivity;
import com.htc.luminaos.bean.StaticIpConfig;
import com.htc.luminaos.databinding.ActivityWifiBinding;
import com.htc.luminaos.databinding.ActivityWifiIpSet2Binding;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.NetWorkUtils;

import java.util.ArrayList;

public class WifiIpSetActivity extends BaseActivity implements View.OnKeyListener {

    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private NetWorkUtils netWorkUtils;
    private WifiConfiguration wifiConfig;
    private StaticIpConfig staticIpConfig;
    private ActivityWifiIpSet2Binding wifiIpSet2Binding;
    private ArrayList<String> ipList;
    private static String TAG = "WifiIpSetActivity";

    // 创建通用的 OnFocusChangeListener
    View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
        if (!hasFocus && v instanceof EditText) {
            EditText editText = (EditText) v;
            String text = editText.getText().toString();
            // 你可以根据 editText 的 ID 或其他特性进行不同处理
            int id = v.getId();
            if (id == R.id.ip || id == R.id.gateway || id == R.id.netmask || id == R.id.dns1 || id == R.id.dns2) {//                    break;

//                    break;

//                    break;

//                    break;

                editTextFocusChange();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiIpSet2Binding = ActivityWifiIpSet2Binding.inflate(LayoutInflater.from(this));
        setContentView(wifiIpSet2Binding.getRoot());
        wifiManager = (WifiManager) getSystemService(Service.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        netWorkUtils = new NetWorkUtils(getApplicationContext(), wifiManager);
        wifiConfig = netWorkUtils.getWifiConfiguration(getApplicationContext(), wifiInfo.getSSID());
        staticIpConfig = new StaticIpConfig();
        ipList = new ArrayList();
        ipList.add(String.valueOf(IpConfiguration.IpAssignment.DHCP));
        ipList.add(String.valueOf(IpConfiguration.IpAssignment.STATIC));
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause(); // 确保调用父类的 onPause 方法
        LogUtils.d(TAG,"执行onPause 方法");
//        if(wifiIpSet2Binding.ip.hasFocus() || wifiIpSet2Binding.gateway.hasFocus() || wifiIpSet2Binding.netmask.hasFocus()
//            || wifiIpSet2Binding.dns1.hasFocus() || wifiIpSet2Binding.dns2.hasFocus()) {
//            LogUtils.d(TAG,"onPause中执行editTextFocusChange方法");
//            editTextFocusChange();
//        }
    }

    private void initView() {
        String mode = String.valueOf(wifiConfig.getIpConfiguration().getIpAssignment());
        if (mode.equals(ipList.get(0))) {
            setEnable(false);
        } else {
            setEnable(true);
        }
        wifiIpSet2Binding.ipModeTv.setText(mode);

        wifiIpSet2Binding.rlIpMode.setOnClickListener(this);
        wifiIpSet2Binding.rlIp.setOnClickListener(this);
        wifiIpSet2Binding.rlGateway.setOnClickListener(this);
        wifiIpSet2Binding.rlNetmask.setOnClickListener(this);
        wifiIpSet2Binding.rlDns1.setOnClickListener(this);
        wifiIpSet2Binding.rlDns2.setOnClickListener(this);
        wifiIpSet2Binding.ipModeLeft.setOnClickListener(this);
        wifiIpSet2Binding.ipModeRight.setOnClickListener(this);

        wifiIpSet2Binding.rlIpMode.setOnHoverListener(this);
        wifiIpSet2Binding.rlIp.setOnHoverListener(this);
        wifiIpSet2Binding.rlGateway.setOnHoverListener(this);
        wifiIpSet2Binding.rlNetmask.setOnHoverListener(this);
        wifiIpSet2Binding.rlDns1.setOnHoverListener(this);
        wifiIpSet2Binding.rlDns2.setOnHoverListener(this);

        wifiIpSet2Binding.rlIpMode.setOnKeyListener(this);
        wifiIpSet2Binding.ip.setOnKeyListener(this);
        wifiIpSet2Binding.gateway.setOnKeyListener(this);
        wifiIpSet2Binding.netmask.setOnKeyListener(this);
        wifiIpSet2Binding.dns1.setOnKeyListener(this);
        wifiIpSet2Binding.dns2.setOnKeyListener(this);

        wifiIpSet2Binding.ip.setOnFocusChangeListener(focusChangeListener);
        wifiIpSet2Binding.gateway.setOnFocusChangeListener(focusChangeListener);
        wifiIpSet2Binding.netmask.setOnFocusChangeListener(focusChangeListener);
        wifiIpSet2Binding.dns1.setOnFocusChangeListener(focusChangeListener);
        wifiIpSet2Binding.dns2.setOnFocusChangeListener(focusChangeListener);
        wifiIpSet2Binding.ip.setText(netWorkUtils.intToIp(wifiManager.getDhcpInfo().ipAddress));
        wifiIpSet2Binding.gateway.setText(netWorkUtils.intToIp(wifiManager.getDhcpInfo().gateway));
        wifiIpSet2Binding.netmask.setText(netWorkUtils.intToIp(wifiManager.getDhcpInfo().netmask));
        wifiIpSet2Binding.dns1.setText(netWorkUtils.intToIp(wifiManager.getDhcpInfo().dns1));
        wifiIpSet2Binding.dns2.setText(netWorkUtils.intToIp(wifiManager.getDhcpInfo().dns2));
    }

    public void editTextFocusChange() {
        LogUtils.d(TAG, " 执行到editTextFocusChange ");
        staticIpConfig.setIp(wifiIpSet2Binding.ip.getText().toString().trim());
        staticIpConfig.setGateWay(wifiIpSet2Binding.gateway.getText().toString().trim());
        staticIpConfig.setNetMask(wifiIpSet2Binding.netmask.getText().toString().trim());
        staticIpConfig.setDns1(wifiIpSet2Binding.dns1.getText().toString().trim());
        staticIpConfig.setDns2(wifiIpSet2Binding.dns2.getText().toString().trim());
        setIpMode(staticIpConfig);
    }

    @Override
    public void onClick(View v) {
        String mode = "";
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        int id = v.getId();
        if (id == R.id.rl_ip_mode) {
            mode = wifiIpSet2Binding.ipModeTv.getText().toString();
            if (mode.equals(ipList.get(0))) {
                wifiIpSet2Binding.ipModeTv.setText(ipList.get(1));
                staticIpConfig.setDhcp(wifiIpSet2Binding.ipModeTv.getText().toString().equals(ipList.get(0)));
//                    setIpMode(staticIpConfig);
                setEnable(true);

            } else if (mode.equals(ipList.get(1))) {
                wifiIpSet2Binding.ipModeTv.setText(ipList.get(0));
                staticIpConfig.setDhcp(wifiIpSet2Binding.ipModeTv.getText().toString().equals(ipList.get(0)));
                setIpMode(staticIpConfig);
                setEnable(false);
            }
//                staticIpConfig.setDhcp(wifiIpSet2Binding.ipModeTv.getText().toString().equals(ipList.get(0)));
        } else if (id == R.id.rl_ip) {
            wifiIpSet2Binding.ip.requestFocus();
            wifiIpSet2Binding.ip.setSelection(wifiIpSet2Binding.ip.getText().length());
            // 显示软键盘
            if (imm != null) {
                imm.showSoftInput(wifiIpSet2Binding.ip, InputMethodManager.SHOW_IMPLICIT);
            }
//                staticIpConfig.setIp(wifiIpSet2Binding.ip.getText().toString().trim());
        } else if (id == R.id.rl_gateway) {
            wifiIpSet2Binding.gateway.requestFocus();
            wifiIpSet2Binding.gateway.setSelection(wifiIpSet2Binding.gateway.getText().length());
            // 显示软键盘
            if (imm != null) {
                imm.showSoftInput(wifiIpSet2Binding.gateway, InputMethodManager.SHOW_IMPLICIT);
            }
//                staticIpConfig.setGateWay(wifiIpSet2Binding.gateway.getText().toString().trim());
        } else if (id == R.id.rl_netmask) {
            wifiIpSet2Binding.netmask.requestFocus();
            wifiIpSet2Binding.netmask.setSelection(wifiIpSet2Binding.netmask.getText().length());
            if (imm != null) {
                imm.showSoftInput(wifiIpSet2Binding.netmask, InputMethodManager.SHOW_IMPLICIT);
            }
//                staticIpConfig.setNetMask(wifiIpSet2Binding.netmask.getText().toString().trim());
        } else if (id == R.id.rl_dns1) {
            wifiIpSet2Binding.dns1.requestFocus();
            wifiIpSet2Binding.dns1.setSelection(wifiIpSet2Binding.dns1.getText().length());
            if (imm != null) {
                imm.showSoftInput(wifiIpSet2Binding.dns1, InputMethodManager.SHOW_IMPLICIT);
            }
//                staticIpConfig.setDns1(wifiIpSet2Binding.dns1.getText().toString().trim());
        } else if (id == R.id.rl_dns2) {
            wifiIpSet2Binding.dns2.requestFocus();
            wifiIpSet2Binding.dns2.setSelection(wifiIpSet2Binding.dns2.getText().length());
            if (imm != null) {
                imm.showSoftInput(wifiIpSet2Binding.dns2, InputMethodManager.SHOW_IMPLICIT);
            }
//                staticIpConfig.setDns2(wifiIpSet2Binding.dns2.getText().toString().trim());
        } else if (id == R.id.ip_mode_left) {
            mode = wifiIpSet2Binding.ipModeTv.getText().toString();
            if (mode.equals(ipList.get(0))) {
                wifiIpSet2Binding.ipModeTv.setText(ipList.get(1));
                staticIpConfig.setDhcp(wifiIpSet2Binding.ipModeTv.getText().toString().equals(ipList.get(0)));
//                    setIpMode(staticIpConfig);
                setEnable(true);

            } else if (mode.equals(ipList.get(1))) {
                wifiIpSet2Binding.ipModeTv.setText(ipList.get(0));
                staticIpConfig.setDhcp(wifiIpSet2Binding.ipModeTv.getText().toString().equals(ipList.get(0)));
                setIpMode(staticIpConfig);
                setEnable(false);
            }
//                staticIpConfig.setDhcp(wifiIpSet2Binding.ipModeTv.getText().toString().equals(ipList.get(0)));
        } else if (id == R.id.ip_mode_right) {
            mode = wifiIpSet2Binding.ipModeTv.getText().toString();
            if (mode.equals(ipList.get(0))) {
                wifiIpSet2Binding.ipModeTv.setText(ipList.get(1));
                staticIpConfig.setDhcp(wifiIpSet2Binding.ipModeTv.getText().toString().equals(ipList.get(0)));
//                    setIpMode(staticIpConfig);
                setEnable(true);

            } else if (mode.equals(ipList.get(1))) {
                wifiIpSet2Binding.ipModeTv.setText(ipList.get(0));
                staticIpConfig.setDhcp(wifiIpSet2Binding.ipModeTv.getText().toString().equals(ipList.get(0)));
                setIpMode(staticIpConfig);
                setEnable(false);
            }
//                staticIpConfig.setDhcp(wifiIpSet2Binding.ipModeTv.getText().toString().equals(ipList.get(0)));
        }
        imm = null;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (v.getId() == R.id.rl_ip_mode) {
                String mode = wifiIpSet2Binding.ipModeTv.getText().toString();
                if (mode.equals(ipList.get(0))) {
                    wifiIpSet2Binding.ipModeTv.setText(ipList.get(1));
                    staticIpConfig.setDhcp(wifiIpSet2Binding.ipModeTv.getText().toString().equals(ipList.get(0)));
//                        setIpMode(staticIpConfig);
                    setEnable(true);

                } else if (mode.equals(ipList.get(1))) {
                    wifiIpSet2Binding.ipModeTv.setText(ipList.get(0));
                    staticIpConfig.setDhcp(wifiIpSet2Binding.ipModeTv.getText().toString().equals(ipList.get(0)));
                    setIpMode(staticIpConfig);
                    setEnable(false);
                }
                AudioManager audioManager = (AudioManager) v.getContext().getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null) {
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                }
                audioManager = null;
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (v.getId() == R.id.rl_ip_mode) {
                String mode = wifiIpSet2Binding.ipModeTv.getText().toString();
                if (mode.equals(ipList.get(0))) {
                    wifiIpSet2Binding.ipModeTv.setText(ipList.get(1));
                    staticIpConfig.setDhcp(wifiIpSet2Binding.ipModeTv.getText().toString().equals(ipList.get(0)));
//                        setIpMode(staticIpConfig);
                    setEnable(true);

                } else if (mode.equals(ipList.get(1))) {
                    wifiIpSet2Binding.ipModeTv.setText(ipList.get(0));
                    staticIpConfig.setDhcp(wifiIpSet2Binding.ipModeTv.getText().toString().equals(ipList.get(0)));
                    setIpMode(staticIpConfig);
                    setEnable(false);
                }
                AudioManager audioManager = (AudioManager) v.getContext().getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null) {
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                }
                audioManager = null;
                return true;
            }
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            int id = v.getId();
            if (id == R.id.ip) {
                if (wifiIpSet2Binding.ip.hasFocus() && event.getAction() == KeyEvent.ACTION_DOWN)
                    wifiIpSet2Binding.rlIp.requestFocus();
                return true;
            } else if (id == R.id.gateway) {
                if (wifiIpSet2Binding.gateway.hasFocus() && event.getAction() == KeyEvent.ACTION_DOWN)
                    wifiIpSet2Binding.rlGateway.requestFocus();
                return true;
            } else if (id == R.id.netmask) {
                if (wifiIpSet2Binding.netmask.hasFocus() && event.getAction() == KeyEvent.ACTION_DOWN)
                    wifiIpSet2Binding.rlNetmask.requestFocus();
                return true;
            } else if (id == R.id.dns1) {
                if (wifiIpSet2Binding.dns1.hasFocus() && event.getAction() == KeyEvent.ACTION_DOWN)
                    wifiIpSet2Binding.rlDns1.requestFocus();
                return true;
            } else if (id == R.id.dns2) {
                if (wifiIpSet2Binding.dns2.hasFocus() && event.getAction() == KeyEvent.ACTION_DOWN)
                    wifiIpSet2Binding.rlDns2.requestFocus();
                return true;
            }
        }

        return false;
    }

    private void setEnable(boolean b) {
        // 创建一个控件数组
        View[] views = {
                wifiIpSet2Binding.ip,
                wifiIpSet2Binding.txtIp,
                wifiIpSet2Binding.gateway,
                wifiIpSet2Binding.txtGateway,
                wifiIpSet2Binding.netmask,
                wifiIpSet2Binding.txtNetmask,
                wifiIpSet2Binding.dns1,
                wifiIpSet2Binding.txtDns1,
                wifiIpSet2Binding.dns2,
                wifiIpSet2Binding.txtDns2
        };
        if (b == true) {
            wifiIpSet2Binding.scrollImage.setFocusable(b);
            wifiIpSet2Binding.rlIp.setFocusable(b);
            wifiIpSet2Binding.rlGateway.setFocusable(b);
            wifiIpSet2Binding.rlNetmask.setFocusable(b);
            wifiIpSet2Binding.rlDns1.setFocusable(b);
            wifiIpSet2Binding.rlDns2.setFocusable(b);
            // 遍历控件数组，设置启用状态
            for (View view : views) {
//                view.setFocusable(b);
                view.setEnabled(b);
                view.setAlpha(1f);
            }
        } else {
            wifiIpSet2Binding.scrollImage.setFocusable(b);
            wifiIpSet2Binding.rlIp.setFocusable(b);
            wifiIpSet2Binding.rlGateway.setFocusable(b);
            wifiIpSet2Binding.rlNetmask.setFocusable(b);
            wifiIpSet2Binding.rlDns1.setFocusable(b);
            wifiIpSet2Binding.rlDns2.setFocusable(b);
            for (View view : views) {
//                view.setFocusable(b);
                view.setEnabled(b);
                view.setAlpha(0.7f);
            }
        }
    }

    private void setIpMode(StaticIpConfig staticIpConfig) {
        //设置ip
        LogUtils.d(TAG, "调用到setIpMode ");
        boolean success = netWorkUtils.setWiFiWithStaticIP(staticIpConfig);
        LogUtils.d(TAG, "IP模式切换成功 " + success + " " + staticIpConfig.isDhcp());
    }

}