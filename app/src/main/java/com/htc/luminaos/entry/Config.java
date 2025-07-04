package com.htc.luminaos.entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:
 * Date:
 * Description:
 */
public class Config {

    public String filterApps = "";

    public List<Apps> apps;
    //IP设置
    public boolean ipSetting = true;

    //亮度和色彩模式
    public boolean brightAndColor = false;
    //图像模式
    public boolean displayPictureMode = true;
    //色温
    public boolean displayColorTemp = false;
    public boolean displayPictureModeShowCustom = true;
    //用户自定义背景目录
    public String custombackground = "";
    //状态栏support图标开关
    public boolean support = false;
    //support多语言目录
    public String support_directory = "";
    //配置首页状态栏蓝牙、Wifi的跳转
    public String statusbar_bt = "";
    public String statusbar_wifi = "";
    public boolean bluetooth = true;
    public int layout_select = 1;

    public boolean displayPictureModeWeiMiTitle = false;

    public boolean brightness = true;
    public int brightnessLevel = 1;
    public boolean brightnessPQ = true;
    public boolean contrast = true;
    public boolean hue = true;
    public boolean saturation = true;
    public boolean sharpness = true;

    //电源模式 上电开机 上电待机，默认关闭
    public boolean powerMode = false;
    //其它设置 谷歌账号切换，默认打开
    public boolean account = true;

    public boolean email = false;

    public String email_number = "";

    public boolean set_password = false;

    public int brightnessDefault = 50;
    public int contrastDefault = 50;
    public int hueDefault = 50;
    public int saturationDefault = 50;
    public int sharpnessDefault = 50;

    public boolean red = false;
    public boolean green = false;
    public boolean blue = false;
    public boolean displayVoiceMode = false;

    //上电信源
    public boolean bootSource = true;
    //屏幕保护
    public boolean screenSaver = true;
    //无障碍服务
    public boolean accessibility = false;

    public  String sourceList ="HDMI1";
    public  String sourceListTitle ="HDMI";

    //wifi DHCP、静态IP切换
    public boolean wifiIpSettings = true;

    //自动梯形矫正
    public boolean autoKeystone = true;

    //初始角度矫正
    public boolean initAngleCorrect = true;

    //自动四角矫正
    public boolean autoFourCorner = true;

    public boolean manualKeystone = true;
    public boolean resetKeystone = true;
    public int manualKeystoneWidth = 1000;
    public int manualKeystoneHeight = 1000;
    public boolean autoFocus = true;
    public boolean screenRecognition = true;
    public boolean intelligentObstacle = true;
    public boolean calibration = true;
    public boolean projectMode = true;
    public boolean displaySetting = false;
    public boolean wholeZoom = true;

    public boolean screenZoom = true;

    //设备模式
    public boolean deviceMode = true;

    //设备型号
    public boolean deviceModel = true;
    public boolean uiVersion = true;
    public boolean androidVersion = true;
    public boolean resolution = true;
    public boolean memory = true;
    public int memoryScale = 1;
    public boolean storage = true;
    public int storageScale = 1;
    public boolean wlanMacAddress = true;
    public boolean updateFirmware = true;
    public boolean onlineUpdate = true;
    public boolean serialNumber = true;

    public boolean AudioMode = false; //声音模式
    public boolean Menu120HZ = true;
    public boolean Menu200HZ = true;
    public boolean Menu500HZ = true;
    public boolean Menu1D2KHZ = true;
    public boolean Menu3KHZ = true;
    public boolean Menu7D5KHZ = true;
    public boolean Menu12KHZ = true;

}
