package com.htc.luminaos.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.htc.luminaos.R;


public class KeepAliveService extends Service {
    private static String TAG = "KeepAliveService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, " 启动KeepAliveService ");

        NotificationChannel channel = new NotificationChannel(
                "keep_alive_channel",                  // Channel ID
                "Keep Alive Service Channel",          // Channel name（可见）
                NotificationManager.IMPORTANCE_LOW     // Importance level
        );
        channel.setDescription("用于启动桌面保活服务的通知通道");
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, "keep_alive_channel")
                .setContentTitle("Launcher正在运行")
                .setContentText("保持桌面常驻内存")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();

        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
