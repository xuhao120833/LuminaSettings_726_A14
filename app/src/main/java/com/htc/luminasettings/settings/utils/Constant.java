package com.htc.luminasettings.settings.utils;

import android.os.SystemProperties;

import java.util.regex.Pattern;

/**
 * Author:
 * Date:
 * Description:
 */
public interface Constant {

    String SleepTime = "sleepTime";
    String SleepMode = "SleepMode";

    int AUTO_KEYSTONE_HINT = 10;
    int AUTO_FOCUS_HINT = 11;
    int AUTO_FOUR_CORNER_HINT = 12;
    int intelligent_obstacle_HINT = 13;
    int curtain_identification_HINT = 14;

    // 1:配对成功 2：正在配对 3：删除配对 或者 配对失败
    int BOND_SUCCESSFUL =1;
    int BONDING = 2;
    int BOND_FAIL = 3;
    int REFRESH_FOUND = 8;
    int REFRESH_PAIR = 9;

    int OPEN_INDEX = 0;
    int WPA_INDEX = 1;
    int WPA2_INDEX = 2;

    int AP_24GHZ = 0;
    int AP_5GHZ = 1;

    String BOOT_SOURCE_NAME = "boot_source";
    String BOOT_SOURCE_NAME_KEY = "auto_boot_source";
    String EXIT_TIME_KEY = "exit_time";
    String UPDATE = "hotack_update";
    int download_err=1000;
    int download_progress=1001;

    // 更新进度广播
    String action_receiver_progress = "action.receiver.progress";
    // 下载失败
    String action_download_failed = "action_download_failed";
    // 异常信息
    String key_errmsg = "ErrMsg";
    // 更新状态
    String key_upgradestate = "UpgradeState";
    // 目标软件版本号
    String key_distversion = "DstVersion";
    // 进度
    String MAX = "max";
    String PROGRESS = "progress";

    // 新版本
    // 版本号
    String key_new_version = "newVersion";
    // 大小
    String key_new_size = "newSize";
    // 描述
    String key_new_description = "newDescription";
    // URL
    String key_new_url = "newUrl";
    // 验证值 MD5
    String key_new_validation = "newValidation";
    // 当前版本信息
    String key_current_version = "currentversion";

    // 时间戳
    String key_timestamp = "Timestamp";
    // 签名
    String key_sign = "Sign";
    // 设备唯一标识
    String key_imei = "IMEI";
    // mac地址
    String key_mac = "MAC";
    // 软件版本号
    String key_version = "Version";
    // 产品型号
    String key_model = "Model";
    // 渠道
    String key_channel = "Channel";
    // 返回编码
    String key_code = "Code";
    String key_system_model="persist.sys.Model";
    String key_system_channel="persist.sys.Channel";
    String ota_check = "V1/Ota/Check";
    String ota_Name = "update.zip";
    String key_product_model="ro.product.manufacturer";
    String key_mstat_path="/cache/update_signed.zip";
    String key_check_number = "ExpNum";
    String key_check_data = "Data";
    String key_conn = "Connection";
    String key_appid = "AppID";
    float DEFAULT_SIZE = 150;

    public static final int DEFAULT_START_ANGLE = 270;
    public static final int DEFAULT_SWEEP_ANGLE = 360;

    public static final int DEFAULT_ANIM_TIME = 300;

    public static final int DEFAULT_MAX_VALUE = 100;
    public static final int DEFAULT_VALUE = 50;

    public static final int DEFAULT_HINT_SIZE = 15;
    public static final int DEFAULT_UNIT_SIZE = 30;
    public static final int DEFAULT_VALUE_SIZE = 15;

    public static final int DEFAULT_ARC_WIDTH = 30;
    String ZOOM_169 = "16:9";
    String ZOOM_1610 = "16:10";
    String ZOOM_43 = "4:3";

    int offset_main = 48;//主窗口左边偏移量
    int offset_other = 48;//其他窗口左边偏移量 -=offset_main

    int UPDATE_RGB = 1001;
    int UPDATE_BRIGHT = 1002;
    String SETTING_DOLBY_ONOFF = "setting_dolby_onoff";
    String SETTING_STANDBY_MODE = "setting_standby_mode";

    String MainNetwork = "MainNetwork";
    String MainBluetooth = "MainBluetooth";
    String MainSignal = "MainSignal";
    String MainDisplay = "MainDisplay";
    String MainVideo = "MainVideo";
    String MainAudio = "MainAudio";
    String MainUniversal = "MainUniversal";
    String MainSystem = "MainSystem";
    String MainSpeaker = "MainSpeaker";
    String MainScreenCast = "MainScreenCast";


    String AudioFragment_TAG ="AudioFragment";

    String BluetoothFragment_TAG ="BluetoothFragment";

    String DisplayFragment_TAG ="DisplayFragment";
    String ScreenZoomFragment_TAG ="ScreenZoomFragment";
    String ProjectModeFragment_TAG ="ProjectModeFragment";
    String PictureModeFragment_TAG ="PictureModeFragment";
    String CorrectionSettingsFragment_TAG ="CorrectionSettingsFragment";
    String FocusSensitiveFragment_TAG ="FocusSensitiveFragment";

    String NetworkFragment_TAG ="NetworkFragment";
    String WifiFragment_TAG ="WifiFragment";
    String WifiDetailFragment_TAG ="WifiDetailFragment";
    String WifiActionFragment_TAG ="WifiActionFragment";
    String HotspotFragment_TAG ="HotspotFragment";
    String WiredNetworkFragment_TAG ="WiredNetworkFragment";
    String HotspotSecurityFragment_TAG ="HotspotSecurityFragment";
    String HotspotApFragment_TAG ="HotspotApFragment";

    String SignalFragment_TAG ="SignalFragment";
    String BootSourceFragment_TAG ="BootSourceFragment";

    String SystemFragment_TAG ="SystemFragment";
    String SystemInfoFragment_TAG ="SystemInfoFragment";
    String LanguageFragment_TAG ="LanguageFragment";
    String InputMethodFragment_TAG ="InputMethodFragment";
    String DateAndTimeFragment_TAG ="DateAndTimeFragment";
    String TimezoneFragment_TAG ="TimezoneFragment";
    String DevelopFragment_TAG ="DevelopFragment";
    String SystemUpdateFragment_TAG ="SystemUpdateFragment";
    String OnlineUpdateFragment_TAG ="OnlineUpdateFragment";
    String BusinessModelFragment_TAG ="BusinessModelFragment_TAG";

    String UniversalFragment_TAG ="UniversalFragment";
    String FanSpeedFragment_TAG ="FanSpeedFragment";
    String FanBrightnessFragment_TAG ="FanBrightnessFragment";
    String SleepModeFragment_TAG ="SleepModeFragment";
    String AppsManagerFragment_TAG ="AppsManagerFragment";
    String MenuDismissFragment_TAG ="MenuDismissFragment";
    String ThemeChangeFragment_TAG ="ThemeChangeFragment";

    String VideoFragment_TAG ="VideoFragment";
    String AdvancePictureFragment_TAG ="AdvancePictureFragment";



    boolean sTifCecFlag = Pattern.compile("cec|full")
            .matcher(SystemProperties.get("ro.config.hisi.tif_mode", "null")).find();
}
