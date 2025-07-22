package com.htc.luminaos.utils;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.htc.luminaos.R;
import com.htc.luminaos.entry.SpecialApps;
import com.htc.luminaos.settings.utils.T;

import java.util.ArrayList;
import java.util.HashMap;

public class Utils {
    public static boolean hasfocus = false;

    public static boolean hasUsbDevice = false;

    public static boolean customBackground = false;

    //首页默认背景resId,无配置默认-1
    public static int mainBgResId = -1;

    public static int usbDevicesNumber = 0;

    //默认背景使用的ArrayList
    public static ArrayList<Object> drawables = new ArrayList<>();
    public static String support_image_path = "";
    //判断display悬浮窗有没有添加到桌上
    public static boolean attachedToWindow= false;
    public static final int REQUEST_CODE_PICK_IMAGE = 1;

    //一个全局的特定IP APP信息
    public static SpecialApps specialApps = null;

    public static String specialAppsList ="";

    public static int[] drawablesId = {
            R.drawable.background_main,
            R.drawable.background_custom,
            R.drawable.background1,
            R.drawable.background5,
            R.drawable.background10,
            R.drawable.background11,
            R.drawable.background12,
            R.drawable.background0,
            R.drawable.background13,
    };

    //实际启动信源用到的名称 HDMI1,HDMI2,CVBS1
    public static String[] sourceList = null;

    //用来显示的名称 HDMI,HDMI2,AV
    public static String[] sourceListTitle = null;

    //全局时区列表
    public static ArrayList<HashMap> list = null;

    /**
     * 打印 Intent 的 Extras 信息
     *
     * @param intent 需要打印的 Intent
     * @param tag    用于日志的 TAG
     */
    public static void logIntentExtras(Intent intent, String tag) {
        if (intent == null) {
            Log.d(tag, "logIntentExtras Intent is null");
            return;
        }

        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.d(tag, "logIntentExtras Intent extras:");
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.d(tag, "[" + key + "] = " + value);
            }
        } else {
            Log.d(tag, "logIntentExtras No extras in the Intent");
        }
    }

}
