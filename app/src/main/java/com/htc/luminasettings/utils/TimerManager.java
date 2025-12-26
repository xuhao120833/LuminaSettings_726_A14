package com.htc.luminasettings.utils;

import android.os.Handler;

public class TimerManager {
    private static final String TAG = "TimerManager";

    private Handler handler = new Handler();
    private long startTime; // 记录开始时间
    private long elapsedTime = 0; // 记录已运行时间（毫秒）
    private boolean isRunning = false; // 判断计时器是否在运行

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                elapsedTime = System.currentTimeMillis() - startTime;
                // 继续循环
                handler.postDelayed(this, 100); // 每 100 毫秒更新一次
            }
        }
    };

    // 启动计时器
    public void startTimer() {
        if (!isRunning) {
            startTime = System.currentTimeMillis();
            isRunning = true;
            handler.post(timerRunnable); // 开始循环
            LogUtils.d(TAG, "Timer started.");
        }
    }

    // 停止计时器
    public void stopTimer() {
        if (isRunning) {
            isRunning = false;
            handler.removeCallbacks(timerRunnable); // 移除回调
            elapsedTime = System.currentTimeMillis() - startTime; // 计算最终时间
            LogUtils.d(TAG, "Timer stopped.");
        }
    }

    // 获取已用时间（毫秒）
//    public long getElapsedTime() {
//        long seconds = elapsedTime / 1000;
//        long milliseconds = elapsedTime % 1000;
//        LogUtils.d(TAG, "TimerManager已经用了多少时间：" + seconds + " seconds " + milliseconds + " ms");
//        return elapsedTime;
//    }

    public void getElapsedTime() {
        long seconds = elapsedTime / 1000;
        long milliseconds = elapsedTime % 1000;
        LogUtils.d(TAG, "TimerManager已经用了多少时间：" + seconds + " seconds " + milliseconds + " ms");
    }

    // 打印已用时间
    public void printElapsedTime() {
        long seconds = elapsedTime / 1000;
        long milliseconds = elapsedTime % 1000;
        LogUtils.d(TAG, "TimerManager最终用了多少时间：" + seconds + " seconds " + milliseconds + " ms");
    }
}
