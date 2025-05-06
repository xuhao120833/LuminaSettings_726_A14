package com.htc.luminaos.service;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.htc.luminaos.R;
import com.htc.luminaos.utils.Contants;
import com.htc.luminaos.utils.ShareUtil;
import com.htc.luminaos.widget.ShutDownDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class TimeOffService extends Service {
    String TAG = "TimeOffService";
    int offTime = -1;//OFF Time S
    Timer timer;
    SharedPreferences sharedPreferences;
    Handler handler = new Handler();

    TimerTask timerTask ;

    public TimeOffService() {
    }

    @Override
    public void onCreate() {
        sharedPreferences = ShareUtil.getInstans(this);
        Log.d(TAG, "onCreate()");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initData(intent);
        Log.d(TAG, "onStartCommand()");
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        if (offTime > 0) {
            timer = new Timer();
            timerTask = null;
            timerTask = createNewTimerTask();
            timer.schedule(timerTask, 10000, 10000);//每10秒检查一次
        } else {
            stopSelf();
        }
        return START_STICKY;//如果系统因为资源不足（如内存）杀死了该服务，之后资源允许时，系统会自动重启服务。
    }

    private void initData(Intent intent) {
        if (intent == null) {
            if (sharedPreferences.getBoolean(Contants.TimeOffStatus, false))
                offTime = sharedPreferences.getInt(Contants.TimeOffTime, 0);
            return;
        }
        if (intent.hasExtra(Contants.TimeOffStatus)) {
            if (!intent.getBooleanExtra(Contants.TimeOffStatus, true))
                stopSelf();
        }
        offTime = sharedPreferences.getInt(Contants.TimeOffTime, 0);
        Log.d(TAG, " initData offTime " + offTime);
    }

    private void restartTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        offTime = sharedPreferences.getInt(Contants.TimeOffTime, 0);
        if (offTime > 0) {
            timer = new Timer();
            timerTask = null;
            timerTask = createNewTimerTask(); // 创建新的 TimerTask
            timer.schedule(timerTask, 10000, 10000);
        } else {
            stopSelf();
        }
    }

    private TimerTask createNewTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if (offTime <= 10) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ShutDownDialog shutDownDialog = new ShutDownDialog(getBaseContext(), R.style.DialogTheme);
                            shutDownDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    if (shutDownDialog.isConfirmedShutdown()) {
                                        ShareUtil.put(getApplicationContext(), Contants.TimeOffTime, 0);
                                        ShareUtil.put(getApplicationContext(), Contants.TimeOffStatus, false);
//                                        ShareUtil.put(getApplicationContext(), Contants.TimeOffIndex, 0);
                                        if (timer != null) {
                                            timer.cancel();
                                            timer = null;
                                        }
                                        stopSelf(); // 用户点击了“确认”，才结束服务
                                    } else {
                                        // 重新启动定时器，继续倒计时
                                        restartTimer();
                                    }
                                }
                            });
                            shutDownDialog.show();
                        }
                    });
                    return;
                }
                offTime -= 10;
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean stopService(Intent name) {
        Log.d(TAG, "stopService()");
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }
}
