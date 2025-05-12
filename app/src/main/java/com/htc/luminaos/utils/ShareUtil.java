package com.htc.luminaos.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemProperties;
import android.util.Log;

/**
 * Author:
 * Date:
 * Description:
 */
public class ShareUtil {
    private static SharedPreferences preferences;

    private static String TAG = "ShareUtil";

    public static SharedPreferences getInstans(Context context) {
        Context deviceContext = context.isDeviceProtectedStorage()
                ? context
                : context.createDeviceProtectedStorageContext();
        if (preferences == null) {
            synchronized (ShareUtil.class) {
                if (preferences == null) {
                    preferences = deviceContext.getSharedPreferences(Contants.FILE_NAME, Context.MODE_PRIVATE);
                }
            }
        }
        return preferences;
    }

//    public static SharedPreferences getInstans(Context context) {
//        if (preferences == null) {
//            synchronized (ShareUtil.class) {
//                if (preferences == null) {
//                    Context safeContext = context;
//                    if (!context.isDeviceProtectedStorage()) {
//                        safeContext = context.createDeviceProtectedStorageContext();
//                    }
//                    preferences = safeContext.getSharedPreferences(Contants.FILE_NAME, Context.MODE_PRIVATE);
//                }
//            }
//        }
//        return preferences;
//    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    public static void put(Context context, String key, Object object) {

        SharedPreferences sp = getInstans(context);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            if(key.equals(Contants.TimeOffIndex)) {
                int value = (Integer) object;
                SystemProperties.set("persist.sys.timeoffindex", String.valueOf(value));
            }
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        editor.apply();
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sp = getInstans(context);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            if(key.equals(Contants.TimeOffIndex)) {
                return SystemProperties.getInt("persist.sys.timeoffindex",0);
            }
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return defaultObject;
    }


    public static void clear(Context context) {
        Log.d(TAG, " clear清空SharedPreferences");
        SharedPreferences sp = getInstans(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
}
