package com.htc.luminasettings.utils;

import android.os.Build;
import android.os.SystemProperties;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Author:
 * Date:
 * Description:
 */
public class Constants {
    public static int PAGE_APPS_COUNT = 12;

    public static int subTextSize = 18;
    public static int subTextBgRadius = 4;
    public static String MODIFY = "modify";
    // 时间广播
    public static String ACTION_USER_SWITCHED = "android.intent.action.USER_SWITCHED";
    public static String FILE_NAME = "data";


    public static String getChannel() {
        String ch = SystemProperties.get("persist.sys.storechannel", "");
        if (ch.equals("")) {
            ch = SystemProperties.get("persist.sys.Channel", "project");
        }
        return ch;
    }

    /**
     * 获取以太网MAC
     * @return
     */
    public static String getWan0Mac() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(
                    "/sys/class/addr_mgt/addr_wifi"));
            return reader.readLine();
        } catch (Exception e) {
            return "";
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public  static boolean isOne(int num , int n){
        return (num >> (n - 1) & 1) == 1;
    }

    public static String getHtcDisplay() {
        String result = Build.DISPLAY;
        result = result.trim();
        String preDisplay = SystemProperties.get("persist.display.prefix","");
        if (!"".equals(preDisplay)){
            int beginIndex = result.indexOf(".");
            result =beginIndex==-1?preDisplay+"."+result:preDisplay+result.substring(beginIndex);
        }

        return result;
    }
}
