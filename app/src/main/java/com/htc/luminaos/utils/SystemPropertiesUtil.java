package com.htc.luminaos.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemPropertiesUtil {

    private static String TAG = "SystemPropertiesUtil";

    public static String batteryLevel = "persist.sys.battery_level";// 0 to 4 对应 0%, 25%, 50%, 75%, 100%
    public static String batteryEnable = "persist.sys.battery_enable";// 1 有电池，0 没电池
    public static String batteryDc = "persist.sys.battery_dc";// 1 充电, 0 没有充电


    public static String getSystemProperty(String propertyName) {
        String propertyValue = "";
        try {
            Process process = Runtime.getRuntime().exec("getprop " + propertyName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            propertyValue = reader.readLine();
            reader.close();
            process.waitFor();
            LogUtils.d(TAG,"电池状态 获取系统属性 "+propertyName+" 获取的值为"+propertyValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        return propertyValue;
        return propertyValue != null ? propertyValue.trim() : "";
    }
}
