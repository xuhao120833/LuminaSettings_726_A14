package com.htc.luminaos.utils;

import android.os.PrjScreen;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Author:
 * Date:
 * Description:
 */
public class ReflectUtil {

    private static String TAG = "ReflectUtil";

    public static int invoke_get_bright(){
        try {
            Method method = PrjScreen.class.getMethod("get_bright");
            int result = (int) method.invoke(PrjScreen.class);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 2;
    }

    public static void invoke_set_bright(int data){
        try {
            Method method = PrjScreen.class.getMethod("set_bright", int.class);
            method.invoke(PrjScreen.class,data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int invokeGet_angle_offset(){
        try {
            LogUtils.d(TAG," 执行invokeGet_angle_offset ");
            Method method = PrjScreen.class.getMethod("get_angle_offset");
            LogUtils.d(TAG," 执行invokeGet_angle_offset 返回值"+(int) method.invoke(PrjScreen.class));
            return (int) method.invoke(PrjScreen.class);

        } catch (Exception e) {
            LogUtils.d(TAG," 执行invokeGet_angle_offset 报错了");
            e.printStackTrace();
        }
        return 0;
    }

    public static void invokeSet_angle_offset(){
        try {
            Method method = PrjScreen.class.getMethod("set_angle_offset");
            method.invoke(PrjScreen.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int invokeGet_brightness_level(){
        try {
            Method method = PrjScreen.class.getMethod("get_brightness_level");
            return (int) method.invoke(PrjScreen.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void invokeSet_brightness_level(int level){
        try {
            Method method = PrjScreen.class.getMethod("set_brightness_level", int.class);
            method.invoke(PrjScreen.class,level);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String invoke_get_bat_status() {
        try {
            Method method = PrjScreen.class.getMethod("get_bat_status");
            String result = (String) method.invoke(PrjScreen.class);
            return result.trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    public static void invokeSet_touch_level(int level){
        try {
            Method method = PrjScreen.class.getMethod("set_touch_level", int.class);
            method.invoke(PrjScreen.class,level);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
