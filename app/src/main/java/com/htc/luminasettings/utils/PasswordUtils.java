package com.htc.luminasettings.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemProperties;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {

    private static final String DEFAULT_PASSWORD_HASH = sha256("11111111");
    private static final String PREFS_NAME = "launcher_prefs";
    private static final String KEY_PASSWORD = "user_password_hash";
    private static final String PROP_PASSWORD_SWITCH = "persist.sys.password_switch"; // 系统属性key

    //临时变量，进程级状态，不会持久化
    public static boolean sessionVerified = false;

    // 校验输入密码
    public static boolean verifyPassword(Context context, String inputPassword) {
        String hashedInput = sha256(inputPassword);
        String savedHash = getSavedPasswordHash(context);
        boolean match = hashedInput.equals(savedHash);
        if (match) {
            sessionVerified = true; // 校验成功后标记
        }
        return match;
    }

    // 查询当前是否已经验证过
    public static boolean isSessionVerified() {
        return sessionVerified;
    }

    // 修改密码
    public static void setNewPassword(Context context, String newPassword) {
        String hashed = sha256(newPassword);
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_PASSWORD, hashed).apply();
        sessionVerified = false; // 换密码就重置
    }

    // 获取保存的密码哈希
    public static String getSavedPasswordHash(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_PASSWORD, DEFAULT_PASSWORD_HASH);
    }

    // 判断是否曾设置过密码（即是否写入过 SharedPreferences）
    public static boolean hasPasswordBeenSet(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.contains(KEY_PASSWORD);
    }

    // 使用 SHA-256 哈希加密字符串
    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            // 转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 加密失败", e);
        }
    }

    // 设置开关状态
    public static void setPasswordSwitchEnabled(boolean enabled) {
        SystemProperties.set(PROP_PASSWORD_SWITCH, enabled ? "1" : "0");
    }

    // 读取开关状态（默认true）
    public static boolean isPasswordSwitchEnabled() {
        return "1".equals(SystemProperties.get(PROP_PASSWORD_SWITCH, "0"));
    }

}
