package com.htc.luminaos.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.htc.luminaos.R;
import com.htc.luminaos.utils.Contants;
import com.htc.luminaos.utils.ShareUtil;
import com.htc.luminaos.widget.ShutDownDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimeOffService extends Service {
    String TAG = "TimeOffService";
    int offTime = -1;//OFF Time S
    Timer timer;
    SharedPreferences sharedPreferences;
    Handler handler = new Handler();
    TimerTask timerTask ;
    ActivityManager am;

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

        startForeground(1, createMinimalNotification());

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
//        return START_STICKY;//如果系统因为资源不足（如内存）杀死了该服务，之后资源允许时，系统会自动重启服务。
        return START_REDELIVER_INTENT;//重启服务，并且使用原始intent中的数据
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
                String topActivity = getTopActivity();
                if(!topActivity.isEmpty() && !topActivity.contains("com.htc.hyk_test")) {
                    Log.d(TAG, "createNewTimerTask topActivity " + topActivity);
                    if (0 < offTime && offTime <= 10) {
                        Log.d(TAG, " createNewTimerTask offTime " + offTime);
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
                        offTime -= 10;
                        return;
                    }
                    offTime -= 10;
                } else {
                    //do nothing
                    Log.d(TAG,"createNewTimerTask com.htc.hyk_test do nothing");
                }
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

    private String getTopActivity() {
        if(am == null)
            am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList == null || taskList.isEmpty()) {
            Log.e(TAG, "No running tasks found");
            return "";
        }
        ComponentName cn = taskList.get(0).topActivity;
        if(cn == null)
            return "";
        Log.d(TAG, "getTopActivity = " + cn.getClassName());
        return cn.getClassName();
    }

    private Notification createMinimalNotification() {
        String channelId = "timeoff_channel_id";
        String channelName = "Time Off Service";

        NotificationChannel channel = new NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_MIN // 尽可能低的优先级
        );
        channel.setSound(null, null); // 禁用声音
        channel.enableLights(false);
        channel.enableVibration(false);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // 必须设置
                .setContentTitle("") // 标题空
                .setContentText("")  // 内容空
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOngoing(true) // 不能被滑掉
                .setVisibility(NotificationCompat.VISIBILITY_SECRET); // 锁屏也不显示

        return builder.build();
    }

}
