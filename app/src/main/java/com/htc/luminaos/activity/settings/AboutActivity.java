package com.htc.luminaos.activity.settings;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.htc.luminaos.MyApplication;
import com.htc.luminaos.R;
import com.htc.luminaos.activity.BaseActivity;
import com.htc.luminaos.databinding.ActivityAboutBinding;
import com.htc.luminaos.databinding.DialogSupportBinding;
import com.htc.luminaos.utils.AppUtils;
import com.htc.luminaos.utils.ClearMemoryUtils;
import com.htc.luminaos.utils.Contants;
import com.htc.luminaos.utils.DeviceUtils;
import com.htc.luminaos.utils.FaqGuideUtils;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.ShareUtil;
import com.htc.luminaos.utils.ToastUtil;
import com.htc.luminaos.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends BaseActivity {

    private ActivityAboutBinding aboutBinding;
    private static String TAG = "AboutActivity";

    private final long GBYTE = 1024 * 1024 * 1024;
    private final long MBYTE = 1024 * 1024;
    List<Integer> ENTER_FACTORY_REBOOT = new ArrayList<>();
    List<Integer> ENTER_FACTORY = new ArrayList<>();
    List<Integer> ENTER_MAC = new ArrayList<>();
    List<Integer> record_list = new ArrayList<>();
    boolean isRecord = false;
    boolean isDebug = false;
    int mPosition = 5;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aboutBinding = ActivityAboutBinding.inflate(LayoutInflater.from(this));
        setContentView(aboutBinding.getRoot());
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        if (isNetworkConnect()) {
            aboutBinding.rlWiredMac.setVisibility(View.VISIBLE);
        } else {
            aboutBinding.rlWiredMac.setVisibility(View.GONE);
        }
        super.onResume();
    }

    private boolean isNetworkConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        return networkInfo != null && networkInfo.isConnected();
    }

    private void initView() {
        aboutBinding.rlDeviceModel.setOnClickListener(this);
        aboutBinding.rlUpdateFirmware.setOnClickListener(this);
        aboutBinding.rlOnlineUpdate.setOnClickListener(this);
        aboutBinding.rlEmailImage.setOnClickListener(this);
        aboutBinding.rlTermsUse.setOnClickListener(this);
        aboutBinding.rlPrivacyPolicy.setOnClickListener(this);
        aboutBinding.rlDeviceModel.requestFocus();
        aboutBinding.rlDeviceModel.requestFocusFromTouch();

        aboutBinding.rlDeviceModel.setVisibility(MyApplication.config.deviceModel ? View.VISIBLE : View.GONE);
        aboutBinding.rlUiVersion.setVisibility(MyApplication.config.uiVersion ? View.VISIBLE : View.GONE);
        aboutBinding.rlAndroidVersion.setVisibility(MyApplication.config.androidVersion ? View.VISIBLE : View.GONE);
        aboutBinding.rlResolution.setVisibility(MyApplication.config.resolution ? View.VISIBLE : View.GONE);
        aboutBinding.rlMemory.setVisibility(MyApplication.config.memory ? View.VISIBLE : View.GONE);
        aboutBinding.rlStorage.setVisibility(MyApplication.config.storage ? View.VISIBLE : View.GONE);
        aboutBinding.rlWirelessMac.setVisibility(MyApplication.config.wlanMacAddress ? View.VISIBLE : View.GONE);
        aboutBinding.rlSerialNumber.setVisibility(MyApplication.config.serialNumber ? View.VISIBLE : View.GONE);
        aboutBinding.rlUpdateFirmware.setVisibility(MyApplication.config.updateFirmware ? View.VISIBLE : View.GONE);
        aboutBinding.rlOnlineUpdate.setVisibility(MyApplication.config.onlineUpdate ? View.VISIBLE : View.GONE);
        aboutBinding.rlEmail.setVisibility(MyApplication.config.email ? View.VISIBLE : View.GONE);
        aboutBinding.rlEmailImage.setVisibility(MyApplication.config.about_support ? View.VISIBLE : View.GONE);
        aboutBinding.rlTermsUse.setVisibility(MyApplication.config.termsUse ? View.VISIBLE : View.GONE);
        aboutBinding.rlPrivacyPolicy.setVisibility(MyApplication.config.privacyPolicy ? View.VISIBLE : View.GONE);

        aboutBinding.rlDeviceModel.setOnHoverListener(this);
        aboutBinding.rlUpdateFirmware.setOnHoverListener(this);
        aboutBinding.rlOnlineUpdate.setOnHoverListener(this);
        aboutBinding.rlEmailImage.setOnHoverListener(this);
        aboutBinding.rlTermsUse.setOnHoverListener(this);
        aboutBinding.rlPrivacyPolicy.setOnHoverListener(this);
    }

    private void initData() {
        sp = ShareUtil.getInstans(this);
        isDebug = sp.getBoolean(Contants.KEY_DEVELOPER_MODE, false);
        aboutBinding.deviceModelTv.setText(SystemProperties.get("persist.sys.modelName", "Projecter"));//产品型号
//        aboutBinding.uiVersionTv.setText(SystemProperties.get("ro.build.version.incremental"));//产品ui版本
        updateTextViewWithModifiedVersion(aboutBinding.uiVersionTv);//产品ui版本读取属性更换前缀
        if (MyApplication.config.androidVersionNumber != 11) {
            aboutBinding.androidVersionTv.setText(String.valueOf(MyApplication.config.androidVersionNumber));
        } else {
            aboutBinding.androidVersionTv.setText(Build.VERSION.RELEASE);//android version
        }
        aboutBinding.serialNumberTv.setText(SystemProperties.get("ro.serialno", "unknow"));
//        aboutBinding.serialNumberTv.setText(getProperty("ro.serialno","unknow"));
        getMemorySize();
        getStorageSize();
        if (MyApplication.config.resolution_string != null && !MyApplication.config.resolution_string.isEmpty()) {
            aboutBinding.resolutionTv.setText(MyApplication.config.resolution_string);
        } else {
            aboutBinding.resolutionTv.setText(getResolution());
        }
        //王冲日本 D048BQ D063 MAXZEN 要求显示分辨率854 X 480
//        aboutBinding.resolutionTv.setText("854 X 480");
        aboutBinding.wirelessMacTv.setText(getWlanMacAddress());
        aboutBinding.wiredMacTv.setText(DeviceUtils.getEthMac());
        aboutBinding.emailNumber.setText(MyApplication.config.email_number);
        initQuickKey();
    }

    private void updateTextViewWithModifiedVersion(TextView textView) {
        String version = SystemProperties.get("ro.build.version.incremental", "");
        String prefix = SystemProperties.get("persist.display.prefix", "");

        if (!version.isEmpty() && !prefix.isEmpty()) {
            // 找第一个点，替换前缀
            int dotIndex = version.indexOf('.');
            if (dotIndex != -1) {
                String suffix = version.substring(dotIndex); // 包括第一个点
                String newVersion = prefix + suffix;
                textView.setText(newVersion);
            } else {
                // 没有点，直接用 prefix
                textView.setText(prefix);
            }
        } else if (!version.isEmpty()) {
            textView.setText(version); // 如果拿不到prefix，就显示原来的
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_device_model) {
            if (!isDebug) {
                if (mPosition == 0) {
                    sp.edit().putBoolean(Contants.KEY_DEVELOPER_MODE, true).apply();
                    isDebug = true;
                    mPosition = 5;
                    ToastUtil.showShortToast(this,
                            getString(R.string.developer_mode_on));
                }
                mPosition--;
            } else {
                if (mPosition == 0) {
                    sp.edit().putBoolean(Contants.KEY_DEVELOPER_MODE, false).apply();
                    isDebug = false;
                    mPosition = 5;
                    ToastUtil.showShortToast(this,
                            getString(R.string.developer_mode_off));
                }
                mPosition--;
            }
        } else if (id == R.id.rl_update_firmware) {
            try {
                startNewActivity(LocalUpdateActivity.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.rl_online_update) {
            AppUtils.startNewApp(this, "com.htc.htcotaupdate");
        } else if (id == R.id.rl_email_image) {
            if (!MyApplication.config.support_faq.isEmpty() || !MyApplication.config.support_quick_guide.isEmpty()) {
                FaqGuideUtils.checkAndOpenUrls(MyApplication.config.support_faq, MyApplication.config.support_quick_guide, this);
            } else {
                showSupportDialog();
            }
        } else if (id == R.id.rl_terms_use) {
            Intent intent = new Intent(this, TermsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("type", "terms");
            startActivity(intent);
        } else if (id == R.id.rl_privacy_policy) {
            Intent intent = new Intent(this, TermsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("type", "privacy");
            startActivity(intent);
        }
        super.onClick(v);
    }

    private void initQuickKey() {
        //进厂测先重启测试组合键
        ENTER_FACTORY_REBOOT.add(KeyEvent.KEYCODE_VOLUME_DOWN);
        ENTER_FACTORY_REBOOT.add(KeyEvent.KEYCODE_DPAD_UP);
        ENTER_FACTORY_REBOOT.add(KeyEvent.KEYCODE_DPAD_RIGHT);
        ENTER_FACTORY_REBOOT.add(KeyEvent.KEYCODE_DPAD_DOWN);
        ENTER_FACTORY_REBOOT.add(KeyEvent.KEYCODE_DPAD_LEFT);
        ENTER_FACTORY_REBOOT.add(KeyEvent.KEYCODE_VOLUME_DOWN);

        //进厂测组合键
        ENTER_FACTORY.add(KeyEvent.KEYCODE_VOLUME_UP);
        ENTER_FACTORY.add(KeyEvent.KEYCODE_DPAD_UP);
        ENTER_FACTORY.add(KeyEvent.KEYCODE_DPAD_RIGHT);
        ENTER_FACTORY.add(KeyEvent.KEYCODE_DPAD_DOWN);
        ENTER_FACTORY.add(KeyEvent.KEYCODE_DPAD_LEFT);
        ENTER_FACTORY.add(KeyEvent.KEYCODE_VOLUME_UP);

        //显示wifi，以太网，SN的二维码组合键
        ENTER_MAC.add(KeyEvent.KEYCODE_VOLUME_MUTE);
        ENTER_MAC.add(KeyEvent.KEYCODE_DPAD_UP);
        ENTER_MAC.add(KeyEvent.KEYCODE_DPAD_RIGHT);
        ENTER_MAC.add(KeyEvent.KEYCODE_DPAD_DOWN);
        ENTER_MAC.add(KeyEvent.KEYCODE_DPAD_LEFT);
        ENTER_MAC.add(KeyEvent.KEYCODE_VOLUME_MUTE);
    }


    private void getMemorySize() {
        String total_memory = "1GB";
        long memorySize = ClearMemoryUtils.getTotalMemorySize(this);
        if (VerifyDDRStatus(memorySize)) {
            memorySize = memorySize * MyApplication.config.memoryScale;
            if (memorySize > 8 * GBYTE)
                total_memory = "10GB";
            else if (memorySize > 6 * GBYTE)
                total_memory = "8GB";
            else if (memorySize > 4 * GBYTE)
                total_memory = "6GB";
            else if (memorySize > 2 * GBYTE)
                total_memory = "4GB";
            else if (memorySize > GBYTE)
                total_memory = "2GB";
            else
                total_memory = "1GB";
        } else {
            memorySize = memorySize * MyApplication.config.memoryScale;
            // 转成 MB 字符串
            long memoryMB = memorySize / (1024L * 1024L);
            total_memory = memoryMB + "MB";
        }

        aboutBinding.memoryTv.setText(total_memory + "/"
                + ClearMemoryUtils.formatFileSize(ClearMemoryUtils
                .getAvailableMemory(this) * MyApplication.config.memoryScale, false));
    }

    private boolean VerifyDDRStatus(long memorySize) {
        if (memorySize < 800 * MBYTE) {
            return false;
        } else if (1024 * MBYTE < memorySize && memorySize < 1600 * MBYTE) {
            return false;
        }
        return true;
    }

    private void getStorageSize() {
        long totalSize = ClearMemoryUtils
                .getRomTotalSizeLong(this);
        LogUtils.d(TAG, "getStorageSize totalSize " + totalSize);
        String total = "8 GB";
        int number_total = 8;
        try {
            if (totalSize > 64 * GBYTE)
                number_total = 128;
            else if (totalSize > 32 * GBYTE)
                number_total = 64;
            else if (totalSize > 16 * GBYTE)
                number_total = 32;
            else if (totalSize > 8 * GBYTE)
                number_total = 16;
            else if (totalSize > 2 * GBYTE)
                number_total = 8;
            else
                number_total = 4;
        } catch (Exception e) {
            // TODO: handle exception
            total = ClearMemoryUtils.getRomTotalSize(this);
        }
        total = String.valueOf(number_total * MyApplication.config.storageScale) + " GB";
        LogUtils.d(TAG, "getStorageSize total " + total);
        aboutBinding.storageTv.setText(total + "/"
                + ClearMemoryUtils.getRomAvailableSize(this, MyApplication.config.storageScale));
    }

    private String getResolution() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return displayMetrics.widthPixels + " X " + displayMetrics.heightPixels;
    }

    private String getWlanMacAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiManager.isWifiEnabled() && wifiInfo != null) {
            return wifiInfo.getMacAddress();
        }
        return "00:00:00:00:00:00";
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
            isRecord = true;
        }
//        LogUtils.d(TAG, "onKeyDown " + keyCode);
        if (isRecord && event.getAction() == KeyEvent.ACTION_UP) {
            record_list.add(keyCode);
            if (record_list.size() >= 6) {
                LogUtils.d("xuhao", "record_list " + record_list.toString());
                if (isEqual(record_list, ENTER_FACTORY_REBOOT)) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.hotack.hotackfeaturestest", "com.hotack.activity.TestActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("reboot_test", true);
                    startActivity(intent);
                } else if (isEqual(record_list, ENTER_FACTORY)) {
                    AppUtils.startNewApp(this, "com.hotack.hotackfeaturestest", "com.hotack.activity.TestActivity");
                } else if (isEqual(record_list, ENTER_MAC)) {
                    AppUtils.startNewApp(this, "com.hotack.writesn", "com.hotack.writesn.MainActivity");
                }
                isRecord = false;
                record_list.clear();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private boolean isEqual(List<Integer> list1, List<Integer> list2) {
        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i)))
                return false;
        }
        return true;
    }

    private void showSupportDialog() {
        Dialog dialog = new Dialog(this, R.style.DialogTheme);
        DialogSupportBinding supportBinding = DialogSupportBinding.inflate(LayoutInflater.from(this));
        dialog.setContentView(supportBinding.getRoot());
        File file = new File(Utils.support_image_path);
        if (file.exists()) {
            Glide.with(this)
                    .load(file)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            supportBinding.rlMain.setBackground(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // 可选：清除背景或设置占位图
                        }
                    });
        } else {
            LogUtils.e("ImageLoad", "File not found: " + Utils.support_image_path);
        }
        Window window = dialog.getWindow();
        if (window != null) {
            //去除系统自带的margin
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置dialog在界面中的属性
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            //背景全透明
            window.setDimAmount(0f);
        }
        WindowManager manager = getWindowManager();
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams params = window.getAttributes(); // 获取对话框当前的参数值
        params.width = d.getWidth();
        params.height = d.getHeight();
        window.setAttributes(params);
        dialog.show();
    }
}