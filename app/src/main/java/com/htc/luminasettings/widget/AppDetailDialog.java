package com.htc.luminasettings.widget;

import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.format.Formatter;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.htc.luminasettings.R;
import com.htc.luminasettings.databinding.AppDetailLayoutBinding;
import com.htc.luminasettings.utils.LogUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * Author:
 * Date:
 * Description:
 */
public class AppDetailDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private AppDetailLayoutBinding appDetailLayoutBinding;
    private String packapgeName = "unknow";
    private OnAppDetailCallBack mcallback;
    private static final String ATTR_PACKAGE_STATS = "PackageStats";
    private ApplicationInfo info;

    private String TAG = "AppDetailDialog";

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.clear_cache) {
            if (mcallback != null)
                mcallback.onClear_cache(info.packageName);
            dismiss();
        } else if (id == R.id.uninstall) {
            if (mcallback != null)
                mcallback.onUninstall(info.packageName);
            dismiss();
        }
    }

    public interface OnAppDetailCallBack {
        void onClear_cache(String packageName);

        void onUninstall(String packageName);
    }

    public AppDetailDialog(Context context) {
        super(context);

        this.mContext = context;
    }

    public AppDetailDialog(Context context, boolean cancelable,
                           DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public AppDetailDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LogUtils.d(TAG, " 执行AppDetailDialog init");
        appDetailLayoutBinding = AppDetailLayoutBinding.inflate(LayoutInflater.from(mContext));
        /*View view = LayoutInflater.from(mContext).inflate(
                R.layout.wifi_settings_layout, null);*/
        if (appDetailLayoutBinding.getRoot() != null) {
            setContentView(appDetailLayoutBinding.getRoot());
            initView();
            // 设置dialog大小 模块好的控件大小设置
            Window dialogWindow = getWindow();
            if (dialogWindow != null) {
                //去除系统自带的margin
                dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //设置dialog在界面中的属性
                dialogWindow.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                //背景全透明
                dialogWindow.setDimAmount(0f);
            }
            WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
            WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            params.width = (int) (d.getWidth() * 0.4); // 宽度设置为屏幕的0.8，根据实际情况调整
            params.height = (int) (d.getHeight() * 0.4);
            //params.x = parent.getWidth();
            dialogWindow.setGravity(Gravity.CENTER);// 设置对话框位置
            dialogWindow.setAttributes(params);
            setSelect();
        }
    }

    private void setSelect() {
        appDetailLayoutBinding.appName.setSelected(true);
        appDetailLayoutBinding.appsVersion.setSelected(true);
        appDetailLayoutBinding.appVersionTv.setSelected(true);
        appDetailLayoutBinding.appSize.setSelected(true);
        appDetailLayoutBinding.appSizeTv.setSelected(true);
        appDetailLayoutBinding.appCache.setSelected(true);
        appDetailLayoutBinding.appCacheTv.setSelected(true);
        appDetailLayoutBinding.clearCache.setSelected(true);
        appDetailLayoutBinding.uninstall.setSelected(true);
    }

    public void setData(ApplicationInfo info) {
        this.packapgeName = info.packageName;
        this.info = info;
        LogUtils.d("packapgeName " + packapgeName);
        getAppSize(mContext, packapgeName);
    }

    private void initView() {
        appDetailLayoutBinding.clearCache.setOnClickListener(this);
        appDetailLayoutBinding.uninstall.setOnClickListener(this);
        appDetailLayoutBinding.appIcon.setBackground(info.loadIcon(mContext.getPackageManager()));
        appDetailLayoutBinding.appName.setText(info.loadLabel(mContext.getPackageManager()));
        appDetailLayoutBinding.appVersionTv.setText(getVersion(mContext, packapgeName));
    }


    public String getVersion(Context context, String pak) {
        try {
            return context.getPackageManager().getPackageInfo(pak, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public void setOnClickCallBack(OnAppDetailCallBack callback) {
        this.mcallback = callback;
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    appDetailLayoutBinding.appSizeTv.setText(bundle.getString("appSize"));
                    appDetailLayoutBinding.appCacheTv.setText(bundle.getString("cacheSize"));
                    break;
                default:
                    break;
            }
            return false;
        }
    });


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getAppSize(Context context, String packageName) {
        if (!hasUsageStatsPermission(context)) {
            requestAppUsagePermission(context);
        } else {
            new Thread(() -> {
                final StorageStatsManager storageStatsManager = (StorageStatsManager) context.getSystemService(Context.STORAGE_STATS_SERVICE);
                final StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
                final List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
                final UserHandle user = android.os.Process.myUserHandle();
                try {
                    StorageStats storageStats = storageStatsManager.queryStatsForPackage(StorageManager.UUID_DEFAULT, packageName, user);
                    Message message = mHandler.obtainMessage();
                    message.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString("appSize", Formatter.formatShortFileSize(context, storageStats.getAppBytes() + storageStats.getDataBytes()));
                    bundle.putString("cacheSize", Formatter.formatShortFileSize(context, storageStats.getCacheBytes()));
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public static boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public static void requestAppUsagePermission(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            LogUtils.e("AppLog", "Start usage access settings activity fail! " + e);
        }
    }

}