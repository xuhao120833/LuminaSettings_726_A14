package com.htc.luminaos.activity;

import static android.net.ConnectivityManager.ACTION_TETHER_STATE_CHANGED;
import static android.net.wifi.WifiManager.WIFI_AP_STATE_CHANGED_ACTION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import com.android.internal.annotations.VisibleForTesting;
import com.htc.luminaos.R;
import com.htc.luminaos.databinding.ActivityHotspotBinding;
import com.htc.luminaos.receiver.HotspotReceiver;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.ToastUtil;
import com.htc.luminaos.utils.WifiHotUtil;
import com.htc.luminaos.widget.HotspotNameDialog;
import com.htc.luminaos.widget.HotspotPasswordDialog;

public class HotspotActivity extends BaseActivity implements View.OnKeyListener {

    private String TAG = "HotspotActivity";
    ActivityHotspotBinding hotspotBinding;
    private WifiHotUtil wifiHotUtil = null;

    private String ssid = "AndroidAP";
    private String password = "";
    private int mSecurityType = 0;
    private IntentFilter hotspotFilter = new IntentFilter(
            "android.net.wifi.WIFI_AP_STATE_CHANGED");
    private HotspotReceiver hotspotReceiver = null;

    public static final int OPEN_INDEX = 0;
    public static final int WPA_INDEX = 1;
    public static final int WPA2_INDEX = 2;
    public String[] securityArray;
    private int apBand = 0;
    public String[] apBandArray;
    private ConnectivityManager mConnectivityManager;
    private OnStartTetheringCallback mStartTetheringCallback;
    private WifiManager mWifiManager;
    @VisibleForTesting
    TetherChangeReceiver mTetherChangeReceiver;
    private static final IntentFilter TETHER_STATE_CHANGE_FILTER;
    private boolean mRestartWifiApAfterConfigChange;

    static {
        TETHER_STATE_CHANGE_FILTER = new IntentFilter(
                ACTION_TETHER_STATE_CHANGED);
        TETHER_STATE_CHANGE_FILTER.addAction(WIFI_AP_STATE_CHANGED_ACTION);
    }


    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message arg0) {
            switch (arg0.what) {
                case 2001:
                    mConnectivityManager.startTethering(
                            ConnectivityManager.TETHERING_WIFI, true,
                            mStartTetheringCallback,
                            new Handler(Looper.getMainLooper()));
                    break;
                case 1001:
                    updateHotspotSwitchStatus(true);
                    break;
                default:
                    break;
            }
            // TODO Auto-generated method stub
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hotspotBinding = ActivityHotspotBinding.inflate(LayoutInflater.from(this));
        setContentView(hotspotBinding.getRoot());
        securityArray = new String[]{getString(R.string.none), getString(R.string.wpa_psk), getString(R.string.wpa2_psk)};
        apBandArray = new String[]{getString(R.string.one_band), getString(R.string.two_band)};
        wifiHotUtil = new WifiHotUtil(HotspotActivity.this);
        initView();
        initData();
    }

    private void initView() {
        hotspotBinding.rlCancel.setOnClickListener(this);
        hotspotBinding.rlEnter.setOnClickListener(this);
        hotspotBinding.rlHotspotName.setOnClickListener(this);
        hotspotBinding.rlHotspotPassword.setOnClickListener(this);
        hotspotBinding.rlHotspotSwitch.setOnClickListener(this);
        hotspotBinding.hotspotSwitch.setOnClickListener(this);
        hotspotBinding.rlShowPassword.setOnClickListener(this);
        hotspotBinding.showPasswordSwitch.setOnClickListener(this);

        hotspotBinding.rlHotspotSecurity.setOnClickListener(this);
        hotspotBinding.rlFrequency.setOnClickListener(this);

        hotspotBinding.rlHotspotSecurity.setOnKeyListener(this);
        hotspotBinding.rlFrequency.setOnKeyListener(this);

        hotspotBinding.rlCancel.setOnHoverListener(this);
        hotspotBinding.rlEnter.setOnHoverListener(this);
        hotspotBinding.rlHotspotName.setOnHoverListener(this);
        hotspotBinding.rlHotspotPassword.setOnHoverListener(this);
        hotspotBinding.rlHotspotSwitch.setOnHoverListener(this);
        hotspotBinding.rlShowPassword.setOnHoverListener(this);
        hotspotBinding.rlHotspotSecurity.setOnHoverListener(this);
        hotspotBinding.rlFrequency.setOnHoverListener(this);

    }

    private void initData() {
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mStartTetheringCallback = new OnStartTetheringCallback();

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mTetherChangeReceiver = new TetherChangeReceiver();
        registerReceiver(mTetherChangeReceiver, TETHER_STATE_CHANGE_FILTER);
        initHotspotData();
        initHotspotState();
        initReceiver();
        hotspotBinding.showPasswordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 如果选中，显示密码
                    hotspotBinding.passwordTv
                            .setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());
                } else {
                    // 否则隐藏密码
                    hotspotBinding.passwordTv
                            .setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());
                }
            }
        });
        hotspotBinding.hotspotSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                updateHotspotSwitchStatus(false);
                mHandler.sendEmptyMessageDelayed(1001, 3000);
                if (isChecked) {
                    if (wifiHotUtil != null) {
                        WifiConfiguration configuration = wifiHotUtil
                                .getWifiConfig();
                        if (configuration != null) {
                            if (!wifiHotUtil.isWifiApEnabled()) {

                                startTether();
                            }
                        } else {

                        }

                    }
                } else {
                    if (wifiHotUtil != null) {
                        if (wifiHotUtil.isWifiApEnabled()) {

                            mConnectivityManager
                                    .stopTethering(ConnectivityManager.TETHERING_WIFI);
                        }
                    }
                }
            }
        });
    }

    private void updateHotspotSwitchStatus(boolean status) {
        if (status) {
            hotspotBinding.hotspotSwitch.setEnabled(true);
            hotspotBinding.rlHotspotSwitch.setEnabled(true);
            hotspotBinding.rlHotspotSwitch.setAlpha(1.0f);
        } else {
            hotspotBinding.hotspotSwitch.setEnabled(false);
            hotspotBinding.rlHotspotSwitch.setEnabled(false);
            hotspotBinding.rlHotspotSwitch.setAlpha(0.5f);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_hotspot_switch || id == R.id.hotspot_switch) {
            hotspotBinding.hotspotSwitch.setChecked(!hotspotBinding.hotspotSwitch.isChecked());
        } else if (id == R.id.rl_show_password || id == R.id.show_password_switch) {
            hotspotBinding.showPasswordSwitch.setChecked(!hotspotBinding.showPasswordSwitch.isChecked());
        } else if (id == R.id.rl_hotspot_password && mSecurityType != 0) {
            HotspotPasswordDialog passwordDialog = new HotspotPasswordDialog(this, R.style.DialogTheme);
            passwordDialog.HotspotConfig(wifiHotUtil);
            passwordDialog.setOnClickCallBack(new HotspotPasswordDialog.HotspotPasswordCallBack() {
                @Override
                public void onClick(String password) {
                    hotspotBinding.passwordTv.setText(password);
                    SystemProperties.set("persist.htc.hotpassword", password);
                    writeConfig();
                }
            });
            passwordDialog.show();
        } else if (id == R.id.rl_hotspot_name) {
            HotspotNameDialog hotspotNameDialog = new HotspotNameDialog(this, R.style.DialogTheme);
            hotspotNameDialog.HotspotConfig(wifiHotUtil);
            hotspotNameDialog.setOnClickCallBack(new HotspotNameDialog.HotspotNameCallBack() {
                @Override
                public void onClick(String password) {
                    hotspotBinding.hotspotNameTv.setText(password);
                    writeConfig();
                }
            });
            hotspotNameDialog.show();
        } else if (id == R.id.rl_hotspot_security) {
            if (mSecurityType == 2) {
                mSecurityType = 0;
            } else {
                mSecurityType++;
            }
            updateSecurity();
        } else if (id == R.id.rl_frequency) {
            apBand = apBand == 1 ? 0 : 1;
            hotspotBinding.frequencyTv.setText(apBandArray[apBand]);
            writeConfig();
            LogUtils.d(TAG, " 确认键调整");
        } else if (id == R.id.rl_enter) {
            if (!hotspotBinding.rlHotspotSwitch.isEnabled())
                return;
            updateHotspotSwitchStatus(false);
            mHandler.sendEmptyMessageDelayed(1001, 3000);
            String msiid = hotspotBinding.hotspotNameTv.getText().toString();
            if (msiid.isEmpty()) {
                ToastUtil.showShortToast(HotspotActivity.this,
                        getString(R.string.ssidmsg));
                return;
            }
            String mpassword = "";
            if (mSecurityType != OPEN_INDEX) {
                mpassword = hotspotBinding.passwordTv.getText().toString();
                if (mpassword.isEmpty()) {
                    ToastUtil.showShortToast(HotspotActivity.this,
                            getString(R.string.passwordmsg));
                    return;
                }

                if (mpassword.length() < 8) {
                    ToastUtil.showShortToast(HotspotActivity.this,
                            getString(R.string.passwordmsglength));
                    return;
                }

            }

            WifiConfiguration mWifiConfig = getConfig(msiid, mSecurityType,
                    mpassword, apBand);

            if (mWifiConfig != null && wifiHotUtil != null) {

                if (mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLED) {
                    LogUtils.d("TetheringSettings",
                            "Wifi AP config changed while enabled, stop and restart");
                    mRestartWifiApAfterConfigChange = true;
                    mConnectivityManager
                            .stopTethering(ConnectivityManager.TETHERING_WIFI);
                }
                if (mSecurityType == OPEN_INDEX) {
                    wifiHotUtil.turnOnWifiAps(msiid, mpassword,
                            WifiHotUtil.WifiSecurityType.WIFICIPHER_NOPASS, apBand);
                } else {
                    wifiHotUtil.turnOnWifiAps(msiid, mpassword,
                            WifiHotUtil.WifiSecurityType.WIFICIPHER_WPA2, apBand);
                }

            }
        } else if (id == R.id.rl_cancel) {
            finish();
        }
    }

    private void writeConfig() {
        if (!hotspotBinding.rlHotspotSwitch.isEnabled())
            return;
//        updateHotspotSwitchStatus(false);
//        mHandler.sendEmptyMessageDelayed(1001,3000);
        String msiid = hotspotBinding.hotspotNameTv.getText().toString();
        if (msiid.isEmpty()) {
            ToastUtil.showShortToast(HotspotActivity.this,
                    getString(R.string.ssidmsg));
            return;
        }
        String mpassword = "";
        if (mSecurityType != OPEN_INDEX) {
            mpassword = hotspotBinding.passwordTv.getText().toString();
            if (mpassword.isEmpty()) {
                ToastUtil.showShortToast(HotspotActivity.this,
                        getString(R.string.passwordmsg));
                return;
            }

            if (mpassword.length() < 8) {
                ToastUtil.showShortToast(HotspotActivity.this,
                        getString(R.string.passwordmsglength));
                return;
            }

        }

        WifiConfiguration mWifiConfig = getConfig(msiid, mSecurityType,
                mpassword, apBand);

        if (mWifiConfig != null && wifiHotUtil != null) {

            if (mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLED) {
                LogUtils.d("TetheringSettings",
                        "Wifi AP config changed while enabled, stop and restart");
                mRestartWifiApAfterConfigChange = true;
                mConnectivityManager
                        .stopTethering(ConnectivityManager.TETHERING_WIFI);
            }
            if (mSecurityType == OPEN_INDEX) {
                wifiHotUtil.turnOnWifiAps(msiid, mpassword,
                        WifiHotUtil.WifiSecurityType.WIFICIPHER_NOPASS, apBand);
            } else {
                wifiHotUtil.turnOnWifiAps(msiid, mpassword,
                        WifiHotUtil.WifiSecurityType.WIFICIPHER_WPA2, apBand);
            }

        }
    }

    private void updateSecurity() {
        if (mSecurityType == 0) {
//            password = "";
            hotspotBinding.passwordTv.setText("");
            hotspotBinding.securityTv.setText(securityArray[mSecurityType]);
        } else {
            password = SystemProperties.get("persist.htc.hotpassword", "12345678");
            hotspotBinding.passwordTv.setText(password);
        }
        hotspotBinding.securityTv.setText(securityArray[mSecurityType]);
        writeConfig();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
            int id = v.getId();
            if (id == R.id.rl_hotspot_security) {
                if (mSecurityType == 0) {
                    mSecurityType = 2;
                } else {
                    mSecurityType--;
                }
                updateSecurity();
                return true;
//                    break;
            } else if (id == R.id.rl_frequency) {
                apBand = apBand == 1 ? 0 : 1;
                hotspotBinding.frequencyTv.setText(apBandArray[apBand]);
                LogUtils.d(TAG, " 向左调整");
                writeConfig();
                return true;
//                    break;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN) {
            int id = v.getId();
            if (id == R.id.rl_hotspot_security) {
                if (mSecurityType == 2) {
                    mSecurityType = 0;
                } else {
                    mSecurityType++;
                }
                updateSecurity();
                return true;
//                    break;
            } else if (id == R.id.rl_frequency) {
                apBand = apBand == 1 ? 0 : 1;
                hotspotBinding.frequencyTv.setText(apBandArray[apBand]);
                writeConfig();
                LogUtils.d(TAG, " 向右调整");
                return true;
//                    break;
            }
        }
        return false;
    }

    /**
     * 初事化热点开关状态
     */
    public void initHotspotState() {
        if (wifiHotUtil != null) {
            hotspotBinding.hotspotSwitch.setChecked(wifiHotUtil.isWifiApEnabled());
        } else {
            wifiHotUtil = new WifiHotUtil(HotspotActivity.this);
        }
    }

    public void initHotspotData() {
        if (wifiHotUtil != null) {
            WifiConfiguration configuration = wifiHotUtil.getWifiConfig();
            if (configuration != null) {

                ssid = configuration.SSID;
                hotspotBinding.hotspotNameTv.setText(ssid);

                mSecurityType = wifiHotUtil.getSecurityTypeIndex(configuration);

                if (mSecurityType == WPA_INDEX || mSecurityType == WPA2_INDEX) {
                    password = configuration.preSharedKey;
                    SystemProperties.set("persist.htc.hotpassword", configuration.preSharedKey);
                } else {
                    password = "";
                }
                if (password != null) {
                    hotspotBinding.passwordTv.setText(password);
                }

                String mSecurity = getSecurityType(mSecurityType);
                hotspotBinding.securityTv.setText(mSecurity);

                apBand = configuration.apBand;
                switch (apBand) {
                    case 0:
                        hotspotBinding.frequencyTv.setText(getString(R.string.one_band));
                        break;

                    case 1:
                    default:
                        apBand = 1;
                        hotspotBinding.frequencyTv.setText(getString(R.string.two_band));
                        break;
                }
            }
        }
    }

    public void destoryReceiver() {
        if (hotspotReceiver != null) {
            unregisterReceiver(hotspotReceiver);
            unregisterReceiver(mTetherChangeReceiver);
        }
    }

    public void initReceiver() {
        hotspotReceiver = new HotspotReceiver(new HotspotReceiver.HotspotCallBack() {

            @Override
            public void aPState(int state) {
                // 便携式热点的状态为：10---正在关闭；11---已关闭；12---正在开启；13---已开启
                LogUtils.i(TAG, "state==" + state);
                switch (state) {
                    case 13:
                        initHotspotState();
                        break;

                    case 11:
                        initHotspotState();
                        break;
                }
            }
        });
        registerReceiver(hotspotReceiver, hotspotFilter);
    }

    private String getSecurityType(int type) {
        String SecurityType = getString(R.string.none);
        switch (type) {
            case OPEN_INDEX:
                SecurityType = getString(R.string.none);
                break;

            case WPA_INDEX:
                SecurityType = getString(R.string.wpa_psk);
                break;

            case WPA2_INDEX:
                SecurityType = getString(R.string.wpa2_psk);
                break;
        }
        return SecurityType;

    }

    /**
     * 获取需要设置的config
     *
     * @param mssid
     * @return
     */
    public WifiConfiguration getConfig(String mssid, int mSecurityType,
                                       String mpassword, int mapBand) {

        WifiConfiguration config = new WifiConfiguration();

        config.apBand = mapBand;

        config.SSID = mssid;

        switch (mSecurityType) {
            case OPEN_INDEX:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                return config;
            case WPA_INDEX:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.preSharedKey = mpassword;
                return config;
            case WPA2_INDEX:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA2_PSK);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.preSharedKey = mpassword;
                return config;
        }
        return null;
    }

    private static final class OnStartTetheringCallback extends
            ConnectivityManager.OnStartTetheringCallback {


        @Override
        public void onTetheringStarted() {
            LogUtils.i("hxdmsg", " execute onTetheringStarted");
        }

        @Override
        public void onTetheringFailed() {
            LogUtils.i("hxdmsg", " execute onThtheringFailed");
        }


        private void update() {

        }
    }

    private void startTether() {
        mRestartWifiApAfterConfigChange = false;
        // wifiHotUtil.openWifiAp(configuration);
        mConnectivityManager.startTethering(ConnectivityManager.TETHERING_WIFI,
                true, mStartTetheringCallback,
                new Handler(Looper.getMainLooper()));
    }


    @Override
    protected void onDestroy() {

        destoryReceiver();
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @VisibleForTesting
    class TetherChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context content, Intent intent) {
            String action = intent.getAction();
            LogUtils.d(TAG,
                    "updating display config due to receiving broadcast action "
                            + action);
            // updateDisplayWithNewConfig();
            if (action.equals(ACTION_TETHER_STATE_CHANGED)) {
                if (mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_DISABLED
                        && mRestartWifiApAfterConfigChange) {
                    startTether();
                }
            } else if (action.equals(WIFI_AP_STATE_CHANGED_ACTION)) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_AP_STATE,
                        0);
                if (state == WifiManager.WIFI_AP_STATE_DISABLED
                        && mRestartWifiApAfterConfigChange) {
                    startTether();
                }
            }
        }
    }
}