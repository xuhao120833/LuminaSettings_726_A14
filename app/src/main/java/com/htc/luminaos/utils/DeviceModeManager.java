package com.htc.luminaos.utils;

import android.util.Log;

import com.htc.luminaos.MyApplication;
import com.htc.luminaos.R;
import com.htc.luminaos.databinding.ActivityProjectBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DeviceModeManager {
    private static final String TAG = "DeviceModeManager";

    private final List<Integer> modes;  // 支持的模式列表
    private int currentMode = 0;

    public DeviceModeManager(String supportedModesStr) {
        modes = new ArrayList<>();
        if (supportedModesStr != null && !supportedModesStr.isEmpty()) {
            String[] parts = supportedModesStr.split(",");
            for (String s : parts) {
                try {
                    modes.add(Integer.parseInt(s.trim()));
                } catch (NumberFormatException ignored) {}
            }
        }
        Collections.sort(modes); // 保证顺序
        // 初始化 currentMode 为反射获取的亮度模式
        int initMode = ReflectUtil.invokeGet_brightness_level();
        if (modes.contains(initMode)) {
            currentMode = initMode;
        } else if (!modes.isEmpty()) {
            currentMode = modes.get(0); // 回退到第一个支持模式
        }
        LogUtils.d(TAG, "初始化设备模式: " + currentMode);
    }


    /** 获取支持的模式列表 */
    public List<Integer> getModes() {
        return modes;
    }

    /** 获取当前模式 */
    public int getCurrentMode() {
        return currentMode;
    }

    /** 设置当前模式（如果合法） */
    public void setCurrentMode(int mode) {
        if (modes.contains(mode)) {
            currentMode = mode;
        }
    }

    /** 切换到下一个模式（循环） */
    public int nextMode() {
        if (modes.isEmpty()) return currentMode;
        int index = modes.indexOf(currentMode);
        if (index == -1 || index == modes.size() - 1) {
            currentMode = modes.get(0); // 回到第一个
        } else {
            currentMode = modes.get(index + 1);
        }
        LogUtils.d(TAG, "切换到设备模式: " + currentMode);
        return currentMode;
    }

    /** 切换到上一个模式（循环） */
    public int prevMode() {
        if (modes.isEmpty()) return currentMode;
        int index = modes.indexOf(currentMode);
        if (index == -1 || index == 0) {
            currentMode = modes.get(modes.size() - 1); // 循环到最后一个
        } else {
            currentMode = modes.get(index - 1);
        }
        LogUtils.d(TAG, "切换到设备模式: " + currentMode);
        return currentMode;
    }


    /** 更新 UI 文本显示 */
    public void updateText(ActivityProjectBinding projectBinding) {
        switch (currentMode) {
            case 0:
                projectBinding.deviceModeTv.setText(
                        projectBinding.getRoot().getContext().getString(R.string.device_mode0));
                break;
            case 1:
                if (MyApplication.config.low_noise_mode) {
                    projectBinding.deviceModeTv.setText(
                            projectBinding.getRoot().getContext().getString(R.string.device_mode3));
                } else {
                    projectBinding.deviceModeTv.setText(
                            projectBinding.getRoot().getContext().getString(R.string.device_mode1));
                }
                break;
            case 2:
                projectBinding.deviceModeTv.setText(
                        projectBinding.getRoot().getContext().getString(R.string.device_mode2));
                break;
            default:
                // 默认显示第一个模式
                if (!modes.isEmpty()) {
                    projectBinding.deviceModeTv.setText(
                            projectBinding.getRoot().getContext().getString(R.string.device_mode0));
                }
                break;
        }
    }
}
