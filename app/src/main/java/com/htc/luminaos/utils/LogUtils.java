package com.htc.luminaos.utils;

import android.util.Log;

public class LogUtils {
    private static String TAG = "LogUtils";
    public static boolean debug = true;

    public static void d(String msg){
        if (!debug)
            return;
        Log.d(TAG,msg);
    }

    public static void e(String msg){
        if (!debug)
            return;
        Log.e(TAG,msg);
    }

    public static void i(String msg){
        if (!debug)
            return;
        Log.i(TAG,msg);
    }

    public static void d(String TAG,String msg){
        if (!debug)
            return;
        Log.d(TAG,msg);
    }

    public static void e(String TAG,String msg){
        if (!debug)
            return;
        Log.e(TAG,msg);
    }

    public static void i(String TAG,String msg){
        if (!debug)
            return;
        Log.i(TAG,msg);
    }

}
