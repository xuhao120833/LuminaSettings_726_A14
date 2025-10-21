package com.htc.luminaos.activity;

import static com.htc.luminaos.utils.BlurImageView.MAX_BITMAP_SIZE;
import static com.htc.luminaos.utils.BlurImageView.narrowBitmap;
import static com.htc.luminaos.utils.Utils.FAQ;
import static com.htc.luminaos.utils.Utils.QUICK_GUID;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.htc.luminaos.MyApplication;
import com.htc.luminaos.activity.settings.BluetoothActivity;
import com.htc.luminaos.activity.settings.MainSettingActivity;
import com.htc.luminaos.activity.settings.WifiActivity;
import com.htc.luminaos.contentobserver.NotificationCallBack;
import com.htc.luminaos.contentobserver.NotificationObserver;
import com.htc.luminaos.databinding.DialogSupportBinding;
import com.htc.luminaos.receiver.AppCallBack;
import com.htc.luminaos.receiver.AppReceiver;
import com.htc.luminaos.receiver.BatteryReceiver;
import com.htc.luminaos.receiver.DisplaySettingsReceiver;
import com.htc.luminaos.receiver.InitAngleReceiver;
import com.htc.luminaos.receiver.UnlockCallBack;
import com.htc.luminaos.receiver.UnlockReceiver;
import com.htc.luminaos.receiver.UsbDeviceCallBack;
import com.htc.luminaos.service.KeepAliveService;
import com.htc.luminaos.service.TimeOffService;
import com.htc.luminaos.utils.BatteryCallBack;
import com.htc.luminaos.utils.FaqGuideUtils;
import com.htc.luminaos.utils.FileUtils;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.RoundedCorners;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htc.luminaos.R;
import com.google.gson.Gson;
import com.htc.luminaos.adapter.ShortcutsAdapter;
import com.htc.luminaos.adapter.ShortcutsAdapterCustom;
import com.htc.luminaos.databinding.ActivityMainBinding;
import com.htc.luminaos.databinding.ActivityMainCustomBinding;
import com.htc.luminaos.databinding.ActivityMainCustom2Binding;
import com.htc.luminaos.databinding.ActivityMainCustom3Binding;
import com.htc.luminaos.entry.AppInfoBean;
import com.htc.luminaos.entry.AppSimpleBean;
import com.htc.luminaos.entry.AppsData;
import com.htc.luminaos.entry.ChannelData;
import com.htc.luminaos.entry.ShortInfoBean;
import com.htc.luminaos.manager.RequestManager;
import com.htc.luminaos.receiver.BluetoothCallBcak;
import com.htc.luminaos.receiver.BluetoothReceiver;
import com.htc.luminaos.receiver.MyTimeCallBack;
import com.htc.luminaos.receiver.MyTimeReceiver;
import com.htc.luminaos.receiver.MyWifiCallBack;
import com.htc.luminaos.receiver.MyWifiReceiver;
import com.htc.luminaos.receiver.NetWorkCallBack;
import com.htc.luminaos.receiver.NetworkReceiver;
import com.htc.luminaos.receiver.UsbDeviceReceiver;
import com.htc.luminaos.utils.AppUtils;
import com.htc.luminaos.utils.BluetoothUtils;
import com.htc.luminaos.utils.Constants;
import com.htc.luminaos.utils.Contants;
import com.htc.luminaos.utils.DBUtils;
import com.htc.luminaos.utils.GenerateQrBitmap;
import com.htc.luminaos.utils.ImageBean;
import com.htc.luminaos.utils.ImageUtils;
import com.htc.luminaos.utils.LanguageUtil;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.NetWorkUtils;
import com.htc.luminaos.utils.ShareUtil;
import com.htc.luminaos.utils.StartupTimer;
import com.htc.luminaos.utils.StatusBarItem;
import com.htc.luminaos.utils.SystemPropertiesUtil;
import com.htc.luminaos.utils.TimeUtils;
import com.htc.luminaos.utils.ToastUtil;
import com.htc.luminaos.utils.Uri;
import com.htc.luminaos.utils.Utils;
import com.htc.luminaos.utils.VerifyUtil;
import com.htc.luminaos.utils.MainCustomBindingWrapper;
import com.htc.luminaos.utils.WifiHotUtil;
import com.htc.luminaos.widget.ManualQrDialog;
import com.htc.luminaos.widget.SpacesItemDecoration;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.transformer.AlphaPageTransformer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseMainActivity implements BluetoothCallBcak, MyWifiCallBack, MyTimeCallBack,
        NetWorkCallBack, UsbDeviceCallBack, AppCallBack, BatteryCallBack, View.OnKeyListener, UnlockCallBack, NotificationCallBack {

    private ActivityMainBinding mainBinding;
    private Context mContext;

    public MainCustomBindingWrapper customBinding;
    private ArrayList<ShortInfoBean> short_list = new ArrayList<>();

    boolean get_default_url = false;
    private boolean isFrist = true;
    private ChannelData channelData;
    private List<AppsData> appsDataList;
    /**
     * receiver
     */

    private NetworkReceiver networkReceiver = null;
    // 时间
    private IntentFilter timeFilter = new IntentFilter();
    private MyTimeReceiver timeReceiver = null;
    // wifi
    private IntentFilter wifiFilter = new IntentFilter();
    private MyWifiReceiver wifiReceiver = null;
    // 蓝牙
    private IntentFilter blueFilter = new IntentFilter();
    //usbDevice
    private IntentFilter usbDeviceFilter = new IntentFilter();
    private BluetoothReceiver blueReceiver = null;
    //Usb 设备
    private UsbDeviceReceiver usbDeviceReceiver = null;

    //电池
    private BatteryReceiver batteryReceiver = null;
    //Display Settings 悬浮窗
    public static DisplaySettingsReceiver displaySettingsReceiver = null;
    private InitAngleReceiver initAngleReceiver = null;
    private UnlockReceiver unlockReceiver = null;
    private static String TAG = "MainActivity";
    private String appName = "";
    private boolean requestFlag = false;
    private final int DATA_ERROR = 102;
    private final int DATA_FINISH = 103;

    private Hashtable<String, String> hashtable = new Hashtable<>();

    private AppReceiver appReceiver = null;
    private WifiManager wifiManager = null;

    private StorageManager storageManager = null;

    ExecutorService threadExecutor = Executors.newFixedThreadPool(5);

    private List<StorageVolume> localDevicesList;

    private ConnectivityManager connectivityManager;

    private ConnectivityManager.NetworkCallback networkCallback;

    private boolean isEther = false;
    private WifiHotUtil wifiHotUtil = null;

    private ShortcutsAdapterCustom shortcutsAdapterCustom = null;
    private boolean dataOK = false;
    private NotificationObserver notificationObserver = null;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //读取首页的配置文件，优先读取网络服务器配置，其次读本地配置。只读取一次，清除应用缓存可触发再次读取。
            if (!dataOK) {
                initDataApp();
                short_list = loadHomeAppData();
                LogUtils.d(TAG, " initDataCustom快捷图标 short_list " + short_list.size());
//                LogUtils.d(TAG, " initDataCustom快捷图标 short_list " + short_list.size());
//                LogUtils.d(TAG, " initDataCustom handler" + handler);
                LogUtils.d(TAG, " initDataCustom handler" + handler);
                handler.sendEmptyMessage(204);
                initReceiver();
                dataOK = true;
            }

            handler.postDelayed(this, 200);
        }
    };

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
//                case 202:
//                    ShortcutsAdapter shortcutsAdapter = new ShortcutsAdapter(MainActivity.this, short_list);
//                    shortcutsAdapter.setItemCallBack(itemCallBack);
//                    mainBinding.shortcutsRv.setAdapter(shortcutsAdapter);
//                    break;
                case 204:
//                    LogUtils.d(TAG, " handler 204");
                    LogUtils.d(TAG, " handler 204");
                    if (shortcutsAdapterCustom != null) {
                        shortcutsAdapterCustom.setShort_list(short_list);
                    } else {
                        shortcutsAdapterCustom = new ShortcutsAdapterCustom(MainActivity.this, short_list);
                        shortcutsAdapterCustom.setItemCallBack(itemCallBackCustom);

                        customBinding.shortcutsRv.setAdapter(shortcutsAdapterCustom);
                    }
                    handler.removeCallbacks(runnable);
                    break;
                case DATA_ERROR:
                    requestFlag = false;
                    ToastUtil.showShortToast(MainActivity.this, getString(R.string.data_err));
                    break;
                case DATA_FINISH:
                    requestFlag = false;
                    if (channelData != null && channelData.getData().size() > 0) {
                        startAppFormChannel();
                    }
                    break;
                case 0:
                    if (customBinding.rlEthernet.getVisibility() == View.VISIBLE) {
                        customBinding.rlEthernet.setVisibility(View.GONE);
                    }
                    break;
                case 1:
                    if (customBinding.rlEthernet.getVisibility() == View.GONE) {
                        customBinding.rlEthernet.setVisibility(View.VISIBLE);
                    }
                    break;
            }
            return false;
        }
    });

    BroadcastReceiver refreshAppsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.htc.refreshApps".equals(intent.getAction())) {
                File file = new File("/system/others.config");
                if (file.exists()) {
                    threadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            refreshApps(file);
                        }
                    });
                } else {
//                    LogUtils.d(TAG, " 收到refreshApps的广播，且没有/system/others.config");
                    LogUtils.d(TAG, " 收到refreshApps的广播，且没有/system/others.config");
                    short_list = loadHomeAppData();
                    handler.sendEmptyMessage(204);
                }
            }
        }
    };

    private void refreshApps(File file) {
        try {
            LogUtils.d(TAG, " 收到refreshApps的广播，有/system/others.config，重新去读specialApps配置");
            FileInputStream is = new FileInputStream(file);
            byte[] b = new byte[is.available()];
            is.read(b);
            String result = new String(b);
            List<String> residentList = new ArrayList<>();
            JSONObject obj = new JSONObject(result);
            readSpecialApps(obj, residentList);
            short_list = loadHomeAppData();
            handler.sendEmptyMessage(204);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StartupTimer.mark("MainActivity.onCreate() start");
        super.onCreate(savedInstanceState);
        //定制逻辑 xuhao add 20240717
        try {
            chooseLayout();
            mContext = getApplicationContext();
//            customBinding = ActivityMainCustomBinding.inflate(LayoutInflater.from(this));
            StartupTimer.mark("ActivityMainCustomBinding.inflate完成");
            setContentView(customBinding.root);
            StartupTimer.mark("setContentView(customBinding.getRoot())完成");
            setDefaultBackgroundById();
            //加载support图片
            loadSupport();
            StartupTimer.mark("setDefaultBackgroundById完成");
            initViewCustom();
            initDataCustom();
            initContentObserver();
            StartupTimer.mark("initDataCustom完成");
//            initReceiver();
//            StartupTimer.mark("initReceiver完成");
            wifiManager = (WifiManager) getSystemService(Service.WIFI_SERVICE);
            storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
            StartupTimer.mark("storageManager完成");
            localDevicesList = new ArrayList<StorageVolume>();
            StartupTimer.mark("localDevicesList完成");
            devicesPathAdd();
            StartupTimer.mark("devicesPathAdd完成");
//            countUsbDevices(getApplicationContext());
            LogUtils.d(TAG, " onCreate快捷图标 short_list " + short_list.size());
            //以太网检测
            isEthernetConnect(getApplicationContext());
            StartupTimer.mark("onCreate完成");
            startRebootService();

            startForegroundService(new Intent(this, KeepAliveService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        StartupTimer.mark("onResume开始");
        super.onResume();
        try {
            updateTime();
            updateBle();
            if ((boolean) ShareUtil.get(this, Contants.MODIFY, false)) {
                short_list = loadHomeAppData();
//            handler.sendEmptyMessage(202);
                LogUtils.d(TAG, " onResume handler" + handler);
                handler.sendEmptyMessage(204);
                ShareUtil.put(this, Contants.MODIFY, false);
            }
            LogUtils.d(TAG, " onResume快捷图标 short_list " + short_list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        StartupTimer.mark("onResume完成");
        StartupTimer.print(" MainActivity StartupTime");
    }

    private void startRebootService() {
        int[] time_off_value = getResources().getIntArray(R.array.time_off_value);
        int cur_time_off_index = (int) ShareUtil.get(this, Contants.TimeOffIndex, 0);
        Intent intent = new Intent(this, TimeOffService.class);
        if (cur_time_off_index == 0) {
            ShareUtil.put(this, Contants.TimeOffStatus, false);
            ShareUtil.put(this, Contants.TimeOffTime, time_off_value[cur_time_off_index]);
            intent.putExtra(Contants.TimeOffStatus, false);
        } else {
            ShareUtil.put(this, Contants.TimeOffStatus, true);
            ShareUtil.put(this, Contants.TimeOffTime, time_off_value[cur_time_off_index]);
            intent.putExtra(Contants.TimeOffStatus, true);
        }
        startForegroundService(intent);
    }

    private void chooseLayout() {
        LogUtils.d(TAG, " chooseLayout MyApplication.config.layout_select " + MyApplication.config.layout_select);
        if (MyApplication.config.layout_select == 2) {
            ActivityMainCustom2Binding binding2 = ActivityMainCustom2Binding.inflate(LayoutInflater.from(this));
            customBinding = new MainCustomBindingWrapper(binding2);
        } else if (MyApplication.config.layout_select == 3) {
            ActivityMainCustom3Binding binding3 = ActivityMainCustom3Binding.inflate(LayoutInflater.from(this));
            customBinding = new MainCustomBindingWrapper(binding3);
        } else {
            ActivityMainCustomBinding binding1 = ActivityMainCustomBinding.inflate(LayoutInflater.from(this));
            customBinding = new MainCustomBindingWrapper(binding1);
        }
    }

    private void initView() {
        mainBinding.rlApps.setOnClickListener(this);
        mainBinding.rlGoogle.setOnClickListener(this);
        mainBinding.rlSettings.setOnClickListener(this);
        mainBinding.rlUsb.setOnClickListener(this);
        mainBinding.rlAv.setOnClickListener(this);
        mainBinding.rlHdmi1.setOnClickListener(this);
        mainBinding.rlHdmi2.setOnClickListener(this);
        mainBinding.rlVga.setOnClickListener(this);
        mainBinding.rlManual.setOnClickListener(this);
        mainBinding.rlWifi.setOnClickListener(this);
        mainBinding.rlBluetooth.setOnClickListener(this);
        mainBinding.rlWallpapers.setOnClickListener(this);
        mainBinding.rlApps.setOnHoverListener(this);
        mainBinding.rlGoogle.setOnHoverListener(this);
        mainBinding.rlSettings.setOnHoverListener(this);
        mainBinding.rlUsb.setOnHoverListener(this);
        mainBinding.rlAv.setOnHoverListener(this);
        mainBinding.rlHdmi1.setOnHoverListener(this);
        mainBinding.rlHdmi2.setOnHoverListener(this);
        mainBinding.rlVga.setOnHoverListener(this);
        mainBinding.rlManual.setOnHoverListener(this);
        mainBinding.rlWifi.setOnHoverListener(this);
        mainBinding.rlBluetooth.setOnHoverListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mainBinding.shortcutsRv.addItemDecoration(new SpacesItemDecoration(0,
                (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.03), 0, 0));
        mainBinding.shortcutsRv.setLayoutManager(layoutManager);
    }

    private void initViewCustom() {
        //我的应用 新的UI放到recyclerView里面去了

        //应用商店
        customBinding.rlGoogle.setOnClickListener(this);
        customBinding.rlGoogle.setOnHoverListener(this);
        customBinding.rlGoogle.setOnFocusChangeListener(this);
        //设置
        customBinding.rlSettings.setOnClickListener(this);
        customBinding.rlSettings.setOnHoverListener(this);
        customBinding.rlSettings.setOnFocusChangeListener(this);
        customBinding.rlSettings.setOnKeyListener(this);
        //文件管理
        customBinding.rlUsb.setOnClickListener(this);
        customBinding.rlUsb.setOnHoverListener(this);
        customBinding.rlUsb.setOnFocusChangeListener(this);
        //HDMI 1
        customBinding.rlHdmi1.setOnClickListener(this);
        customBinding.rlHdmi1.setOnHoverListener(this);
        customBinding.rlHdmi1.setOnFocusChangeListener(this);
        customBinding.rlHdmi1.setOnKeyListener(this);
        //rl_av
        //rl_hdmi2
        //rl_vga
        //rl_manual 说明书，新的UI没这个功能
        //wifi
        customBinding.rlWifi.setOnClickListener(this);
        customBinding.rlWifi.setOnHoverListener(this);
        customBinding.rlWifi.setOnFocusChangeListener(this);
        //蓝牙
        customBinding.rlBluetooth.setOnClickListener(this);
        customBinding.rlBluetooth.setOnHoverListener(this);
        customBinding.rlBluetooth.setOnFocusChangeListener(this);
        customBinding.rlBluetooth.setVisibility(MyApplication.config.bluetooth ? View.VISIBLE : View.GONE);
        //清除缓存
        customBinding.rlClearMemory.setOnClickListener(this);
        customBinding.rlClearMemory.setOnHoverListener(this);
        customBinding.rlClearMemory.setOnFocusChangeListener(this);
        //切换背景
        customBinding.rlWallpapers.setOnClickListener(this);
        customBinding.rlWallpapers.setOnHoverListener(this);
        customBinding.rlWallpapers.setOnFocusChangeListener(this);
        //Eshare
        customBinding.homeEshare.setOnClickListener(this);
        customBinding.homeEshare.setOnHoverListener(this);
        customBinding.homeEshare.setOnFocusChangeListener(this);
        //Netflix
        customBinding.homeNetflix.setOnClickListener(this);
        customBinding.homeNetflix.setOnHoverListener(this);
        customBinding.homeNetflix.setOnFocusChangeListener(this);
        //Youtube
        customBinding.homeYoutube.setOnClickListener(this);
        customBinding.homeYoutube.setOnHoverListener(this);
        customBinding.homeYoutube.setOnFocusChangeListener(this);
        //迪士尼
        customBinding.homeDisney.setOnClickListener(this);
        customBinding.homeDisney.setOnHoverListener(this);
        customBinding.homeDisney.setOnFocusChangeListener(this);
        //首页Usb插入、拔出图标
//        customBinding.usbConnect
        //support
        customBinding.rlSupport.setOnClickListener(this);
        customBinding.rlSupport.setOnHoverListener(this);
        customBinding.rlSupport.setOnFocusChangeListener(this);
        customBinding.rlSupport.setVisibility((MyApplication.config.support && !Utils.support_image_path.isEmpty()) ? View.VISIBLE : View.GONE);
        //通知
        customBinding.rlNotice.setOnClickListener(this);
        customBinding.rlNotice.setOnHoverListener(this);
        customBinding.rlNotice.setOnFocusChangeListener(this);
        customBinding.rlNotice.setVisibility(MyApplication.config.notice ? View.VISIBLE : View.GONE);
        //电池状态
        initBattery();
        //U盘插入
        customBinding.rlUsbConnect.setOnClickListener(this);
        customBinding.rlUsbConnect.setOnHoverListener(this);
        customBinding.rlUsbConnect.setOnFocusChangeListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this) {
            @Override
            public boolean canScrollHorizontally() {
                // 禁用水平滚动
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        customBinding.shortcutsRv.addItemDecoration(new SpacesItemDecoration(0,
//                (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.03), 0, 0));
        //定义Item之间的间距
        customBinding.shortcutsRv.addItemDecoration(new SpacesItemDecoration(0,
                (int) getResources().getDimension(R.dimen.x_43), 0, 0));
        customBinding.shortcutsRv.setLayoutManager(layoutManager);

//        if (MyApplication.config.layout_select == 2 || MyApplication.config.layout_select == 3) {
//            customBinding.icon1border.setBackground(null);
//            customBinding.icon2border.setBackground(null);
//            customBinding.icon3border.setBackground(null);
//            customBinding.icon4border.setBackground(null);
//        }
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initDataApp();
                short_list = loadHomeAppData();
                handler.sendEmptyMessage(202);
            }
        }).start();
    }

    public void initBattery() {
        LogUtils.d(TAG, "电池状态 初始化");

        if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryEnable).equals("1")) {//是否有电池
            LogUtils.d(TAG, "电池状态 初始化 有电池");
            customBinding.rlBattery.setVisibility(View.VISIBLE);
            if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("1")) {
                LogUtils.d(TAG, "电池状态 初始化 正在充电");
                switch (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryLevel)) {
                    case "0":
                        customBinding.battery.setImageResource(R.drawable.battery_charging_1);
                        break;
                    case "1":
                        customBinding.battery.setImageResource(R.drawable.battery_charging_2);
                        break;
                    case "2":
                        customBinding.battery.setImageResource(R.drawable.battery_charging_3);
                        break;
                    case "3":
                        customBinding.battery.setImageResource(R.drawable.battery_charging_4);
                        break;
                    case "4":
                        customBinding.battery.setImageResource(R.drawable.battery_charging_5);
                        break;
                }
            } else if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("0")) {
                LogUtils.d(TAG, "电池状态 初始化 没充电");
                switch (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryLevel)) {
                    case "0":
                        customBinding.battery.setImageResource(R.drawable.battery_1);
                        break;
                    case "1":
                        customBinding.battery.setImageResource(R.drawable.battery_2);
                        break;
                    case "2":
                        customBinding.battery.setImageResource(R.drawable.battery_3);
                        break;
                    case "3":
                        customBinding.battery.setImageResource(R.drawable.battery_4);
                        break;
                    case "4":
                        customBinding.battery.setImageResource(R.drawable.battery_5);
                        break;
                }
            }
        } else {
            LogUtils.d(TAG, "电池状态 初始化 没有电池");
        }

    }

    @Override
    public void setBatteryLevel(String level) {
        LogUtils.d(TAG, "电池状态 setBatteryLevel");
        if (level == null) {
            LogUtils.d(TAG, "setBatteryLevel level为空返回");
            return;
        }
        switch (level) {
            case "0":
                if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("1")) {
                    customBinding.battery.setImageResource(R.drawable.battery_charging_1);
                } else if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("0")) {
                    customBinding.battery.setImageResource(R.drawable.battery_1);
                }
                break;
            case "1":
                if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("1")) {
                    customBinding.battery.setImageResource(R.drawable.battery_charging_2);
                } else if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("0")) {
                    customBinding.battery.setImageResource(R.drawable.battery_2);
                }
                break;
            case "2":
                if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("1")) {
                    customBinding.battery.setImageResource(R.drawable.battery_charging_3);
                } else if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("0")) {
                    customBinding.battery.setImageResource(R.drawable.battery_3);
                }
                break;
            case "3":
                if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("1")) {
                    customBinding.battery.setImageResource(R.drawable.battery_charging_4);
                } else if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("0")) {
                    customBinding.battery.setImageResource(R.drawable.battery_4);
                }
                break;
            case "4":
                if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("1")) {
                    customBinding.battery.setImageResource(R.drawable.battery_charging_5);
                } else if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("0")) {
                    customBinding.battery.setImageResource(R.drawable.battery_5);
                }
                break;
        }

    }

    @Override
    public void Plug_in_charger() {
        LogUtils.d(TAG, "电池状态 Plug_in_charger");
        switch (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryLevel)) {
            case "0":
                customBinding.battery.setImageResource(R.drawable.battery_charging_1);
                break;
            case "1":
                customBinding.battery.setImageResource(R.drawable.battery_charging_2);
                break;
            case "2":
                customBinding.battery.setImageResource(R.drawable.battery_charging_3);
                break;
            case "3":
                customBinding.battery.setImageResource(R.drawable.battery_charging_4);
                break;
            case "4":
                customBinding.battery.setImageResource(R.drawable.battery_charging_5);
                break;
        }
    }

    @Override
    public void Unplug_the_charger() {
        LogUtils.d(TAG, "电池状态 Unplug_the_charger");
        switch (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryLevel)) {
            case "0":
                customBinding.battery.setImageResource(R.drawable.battery_1);
                break;
            case "1":
                customBinding.battery.setImageResource(R.drawable.battery_2);
                break;
            case "2":
                customBinding.battery.setImageResource(R.drawable.battery_3);
                break;
            case "3":
                customBinding.battery.setImageResource(R.drawable.battery_4);
                break;
            case "4":
                customBinding.battery.setImageResource(R.drawable.battery_5);
                break;
        }
    }

    private void initDataCustom() {
        handler.post(runnable);
    }


    private void initReceiver() {
        IntentFilter networkFilter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        networkReceiver = new NetworkReceiver();
        networkReceiver.setNetWorkCallBack(this);
        registerReceiver(networkReceiver, networkFilter);

        // 时间变化 分为单位
        timeReceiver = new MyTimeReceiver(this);
        timeFilter.addAction(Intent.ACTION_TIME_CHANGED);
        timeFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        timeFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
        timeFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        timeFilter.addAction(Intent.ACTION_USER_SWITCHED);
        timeFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver, timeFilter);

        // wifi
        wifiFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

        wifiReceiver = new MyWifiReceiver(this);
        registerReceiver(wifiReceiver, wifiFilter);

        // 蓝牙
        // blueFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        // blueFilter
        // .addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        // blueFilter.addAction("android.bluetooth.device.action.FOUND");
        // blueFilter
        // .addAction("android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED");
        blueFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        blueFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        blueReceiver = new BluetoothReceiver(this);
        registerReceiver(blueReceiver, blueFilter);

        //Usb设备插入、拔出
        usbDeviceReceiver = new UsbDeviceReceiver(this);
//        usbDeviceFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//        usbDeviceFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        usbDeviceFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        usbDeviceFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        usbDeviceFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        usbDeviceFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        usbDeviceFilter.addDataScheme("file");
        registerReceiver(usbDeviceReceiver, usbDeviceFilter);

        //APP安装、改变、卸载
        appReceiver = new AppReceiver(this);
        IntentFilter appFilter = new IntentFilter();
        appFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        appFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        appFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        appFilter.addDataScheme("package");
        registerReceiver(appReceiver, appFilter);

        //电量变化
        batteryReceiver = new BatteryReceiver(this, this);
        IntentFilter batteryFilter = new IntentFilter();
        batteryFilter.addAction("action.projector.dcin");
        batteryFilter.addAction("action.projector.batterylevel");
        registerReceiver(batteryReceiver, batteryFilter);

        //监听APPStore发出 特定IP广播，意味着它已经写了Settings ip_country_code 的值
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.htc.refreshApps");
        registerReceiver(refreshAppsReceiver, intentFilter);
//        short_list = loadHomeAppData();
//        handler.sendEmptyMessage(204);

        //Display Settings悬浮窗
        if (displaySettingsReceiver == null) {
            LogUtils.d(TAG, "registerReceiver displaySettingsReceiver");
            displaySettingsReceiver = new DisplaySettingsReceiver(getApplicationContext());
            IntentFilter displayFilter = new IntentFilter();
            displayFilter.addAction(DisplaySettingsReceiver.DisplayAction);
            getApplicationContext().registerReceiver(displaySettingsReceiver, displayFilter);
        }

        //初始角度矫正
        initAngleReceiver = new InitAngleReceiver(getApplicationContext());
        IntentFilter initAngleFilter = new IntentFilter();
        initAngleFilter.addAction("com.htc.INITANGLE");
        registerReceiver(initAngleReceiver, initAngleFilter);

        //监听解锁
//        unlockReceiver = new UnlockReceiver(this);
//        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_UNLOCKED);
//        registerReceiver(unlockReceiver, filter);
    }

    ShortcutsAdapter.ItemCallBack itemCallBack = new ShortcutsAdapter.ItemCallBack() {
        @Override
        public void onItemClick(int i) {
//            if (i < short_list.size()) {
//                if (short_list.get(i).getAppname() != null) {
//                    AppUtils.startNewApp(MainActivity.this, short_list.get(i).getPackageName());
//                } else if (appsDataList != null) {
//                    AppsData appsData = findAppsData(short_list.get(i).getPackageName());
//                    if (appsData != null) {
//                        Intent intent = new Intent();
//                        intent.setComponent(new ComponentName("com.htc.storeos", "com.htc.storeos.AppDetailActivity"));
//                        intent.putExtra("appData", new Gson().toJson(appsData));
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    } else {
//                        ToastUtil.showShortToast(getBaseContext(), getString(R.string.data_none));
//                    }
//                } else {
//                    ToastUtil.showShortToast(getBaseContext(), getString(R.string.data_none));
//                }
//            } else {
//                AppUtils.startNewActivity(MainActivity.this, AppFavoritesActivity.class);
//            }
        }
    };

    ShortcutsAdapterCustom.ItemCallBack itemCallBackCustom = new ShortcutsAdapterCustom.ItemCallBack() {
        @Override
        public void onItemClick(int i, String name) {
            if (i < short_list.size()) {

                LogUtils.d(TAG, " xuhao执行点击前 " + i);
                if (i == 0) {
                    LogUtils.d(TAG, " 打开APP详情页");
                    startNewActivity(AppsActivity.class);
                    return;
                }
                LogUtils.d(TAG, " short_list.get(i).getPackageName() " + short_list.get(i).getPackageName());
                if (!AppUtils.startNewApp(MainActivity.this, short_list.get(i).getPackageName())) {
                    appName = name;
                    requestChannelData();
                }

            } else {
                AppUtils.startNewActivity(MainActivity.this, AppFavoritesActivity.class);
            }
        }
    };


//    public AppsData findAppsData(String pkg) {
//        for (AppsData appsData : appsDataList) {
//            if (appsData.getApp_id().equals(pkg))
//                return appsData;
//        }
//        return null;
//    }


    private void requestAppData() {
        String channel = SystemProperties.get("persist.sys.Channel", "project");
        String url = Uri.BASE_URL + channel + "/channel_apps_" + Locale.getDefault().getLanguage() + ".xml";
        RequestManager.getInstance().getData(url, channelCallback);
    }

    Callback channelCallback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            e.printStackTrace();
            LogUtils.d("onFailure()");
            handler.sendEmptyMessage(DATA_ERROR);
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            try {
                String content = response.body().string();
                LogUtils.d("content " + content);
                if (RequestManager.isOne(Uri.complexType, 3)) {
                    byte[] bytes = Base64.decode(content, Base64.NO_WRAP);
                    content = new String(VerifyUtil.gzipDecompress(bytes), StandardCharsets.UTF_8);
                    LogUtils.d("content " + content);
                }
                channelData = new Gson().fromJson(content, ChannelData.class);
                if (channelData.getCode() != 0) {
                    handler.sendEmptyMessage(DATA_ERROR);
                } else {
                    handler.sendEmptyMessage(DATA_FINISH);
                }

            } catch (Exception e) {
                handler.sendEmptyMessage(DATA_ERROR);
            }
        }
    };

    private void responseErrorRedirect() {
        if (!get_default_url) {
            get_default_url = true;
            String channel = SystemProperties.get("persist.sys.Channel", "project");
            String url = Uri.BASE_URL + channel + "/channel_apps_global.xml";
            RequestManager.getInstance().getData(url, channelCallback);
        }
    }

    @Override
    public void onClick(View v) {
        String appname = null;
        String action = null;
        int id = v.getId();
        if (id == R.id.rl_notice) {
            goAction("com.htc.notification/com.htc.notification.MainActivity");
        } else if (id == R.id.rl_support) {
            if (!MyApplication.config.support_faq.isEmpty() || !MyApplication.config.support_quick_guide.isEmpty()) {
                FaqGuideUtils.checkAndOpenUrls(MyApplication.config.support_faq, MyApplication.config.support_quick_guide,this);
            } else {
                showSupportDialog();
            }
        } else if (id == R.id.rl_clear_memory) {
            goAction("com.htc.clearmemory/com.htc.clearmemory.MainActivity");
        } else if (id == R.id.rl_wallpapers) {
            startNewActivity(WallPaperActivity.class);
        } else if (id == R.id.rl_Google) {
            appname = DBUtils.getInstance(this).getAppNameByTag("icon4");
            action = DBUtils.getInstance(this).getActionByTag("icon4");
            LogUtils.d(TAG, " appnameaction" + appname + " " + action);
            if (appname != null && action != null && !appname.equals("") && !action.equals("")) {
                if (!AppUtils.startNewApp(MainActivity.this, action)) {
                    appName = appname;
                    requestChannelData();
                }
            } else {
                if (Utils.gtvBanner) {
                    if (!AppUtils.startNewApp(MainActivity.this, "com.mm.droid.livetv.gtv")) {
                        appName = "GTV";
                        requestChannelData();
                    }
//                    AppUtils.startNewApp(MainActivity.this, "com.mm.droid.livetv.gtv");
                } else {
                    AppUtils.startNewApp(MainActivity.this, "com.htc.storeos");
                }
            }
//                AppUtils.startNewApp(MainActivity.this, "com.htc.storeos");
        } else if (id == R.id.rl_apps) {
            startNewActivity(AppsActivity.class);
        } else if (id == R.id.rl_settings) {//                startNewActivity(MainSettingActivity.class);
            try {
                String listaction = DBUtils.getInstance(this).getActionFromListModules("list4");
                if (listaction != null && !listaction.equals("")) { //读取配置
                    goAction(listaction);
                } else {// 默认跳转
                    startNewActivity(MainSettingActivity.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.rl_usb) {//                AppUtils.startNewApp(MainActivity.this, "com.softwinner.TvdFileManager");
            AppUtils.startNewApp(MainActivity.this, "com.hisilicon.explorer");
        } else if (id == R.id.rl_usb_connect) {
            AppUtils.startNewApp(MainActivity.this, "com.hisilicon.explorer");
        } else if (id == R.id.rl_av) {
            startSource("CVBS1");
        } else if (id == R.id.rl_hdmi1) {//                startSource("HDMI1");
            try {
                String listaction = DBUtils.getInstance(this).getActionFromListModules("list3");
                if (listaction != null && !listaction.equals("")) { //读取配置
                    goAction(listaction);
                } else {
                    if (Utils.sourceList.length > 1) { //支持多信源
                        showSourceDialog();
                    } else {
                        // 默认跳转
                        startSource("HDMI1");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.rl_hdmi2) {
            startSource("HDMI2");
        } else if (id == R.id.rl_vga) {
            startSource("VGA");
        } else if (id == R.id.rl_manual) {
            ManualQrDialog manualQrDialog = new ManualQrDialog(this, R.style.DialogTheme);
            manualQrDialog.show();
        } else if (id == R.id.rl_wifi) {
            if (!MyApplication.config.statusbar_wifi.isEmpty()) {
                goAction(MyApplication.config.statusbar_wifi.trim());
            } else {
                startNewActivity(WifiActivity.class);
            }
        } else if (id == R.id.rl_bluetooth) {
            if (!MyApplication.config.statusbar_bt.isEmpty()) {
                goAction(MyApplication.config.statusbar_bt.trim());
            } else {
                startNewActivity(BluetoothActivity.class);
            }
        } else if (id == R.id.home_eshare) {
            try {
                String listaction = DBUtils.getInstance(this).getActionFromListModules("list1");
                if (listaction != null && !listaction.equals("")) { //读取配置
                    goAction(listaction);
                } else {// 默认跳转
                    AppUtils.startNewApp(MainActivity.this, "com.ecloud.eshare.server");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.home_disney) {
            LogUtils.d("xuhao", "打开迪士尼");
            appname = DBUtils.getInstance(this).getAppNameByTag("icon3");
            action = DBUtils.getInstance(this).getActionByTag("icon3");
            if (appname != null && action != null && !appname.equals("") && !action.equals("")) {
                if (!AppUtils.startNewApp(MainActivity.this, action)) {
                    appName = appname;
                    requestChannelData();
                }
            } else if (!AppUtils.startNewApp(MainActivity.this, "com.disney.disneyplus")) {
                appName = "Disney+";
                requestChannelData();
            }
//                AppUtils.startNewApp(MainActivity.this, "com.disney.disneyplus");
        } else if (id == R.id.home_netflix) {
            LogUtils.d("xuhao", "打开奈飞");
            appname = DBUtils.getInstance(this).getAppNameByTag("icon1");
            action = DBUtils.getInstance(this).getActionByTag("icon1");
            if (appname != null && action != null && !appname.equals("") && !action.equals("")) {
                if (!AppUtils.startNewApp(MainActivity.this, action)) {
                    LogUtils.d("xuhao", "打开奈飞 第一个坑位不为空 " + appname + "2" + action + "3");
                    appName = appname;
                    requestChannelData();
                }
            } else if (!AppUtils.startNewApp(MainActivity.this, "com.netflix.mediaclient")) {
                if (!AppUtils.startNewApp(MainActivity.this, "com.netflix.ninja")) {
                    LogUtils.d("xuhao", "打开奈飞 第一个坑位为空");
                    appName = "Netflix";
                    requestChannelData();
                }
            }
//                if (!AppUtils.startNewApp(MainActivity.this, "com.netflix.mediaclient")) {
//                    appName = "Netflix";
//                    requestChannelData();
//                }
//                AppUtils.startNewApp(MainActivity.this, "com.netflix.mediaclient");
//                com.netflix.mediaclient 手机版
//                com.netflix.ninja 电视版
        } else if (id == R.id.home_youtube) {
            LogUtils.d("xuhao", "打开YOUtube");
            appname = DBUtils.getInstance(this).getAppNameByTag("icon2");
            action = DBUtils.getInstance(this).getActionByTag("icon2");
            if (appname != null && action != null && !appname.equals("") && !action.equals("")) {
                if (!AppUtils.startNewApp(MainActivity.this, action)) {
                    appName = appname;
                    requestChannelData();
                }
            } else if (!AppUtils.startNewApp(MainActivity.this, "com.google.android.youtube.tv")) {
                appName = "Youtube";
                requestChannelData();
            }
//                if (!AppUtils.startNewApp(MainActivity.this, "com.google.android.youtube.tv")) {
//                    appName = "Youtube";
//                    requestChannelData();
//                }
//                AppUtils.startNewApp(MainActivity.this, "com.google.android.youtube.tv");
        }

    }

    private void goAction(String listaction) {
        try {
            LogUtils.d(TAG, " goAction list配置跳转 " + listaction);
            if (listaction.contains("/")) {
                String[] parts = listaction.split("/", 2);
                String packageName = parts[0];
                String activityName = parts[1];
                LogUtils.d(TAG, " goAction 包名活动名 " + packageName + " " + activityName);
                startNewActivity(packageName, activityName);
            } else if (listaction.equals("HDMI1") || listaction.equals("HDMI2") || listaction.equals("VGA") || listaction.equals("CVBS1")) {
                startSource(listaction);
            } else {
                AppUtils.startNewApp(MainActivity.this, listaction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSource(String sourceName) {
        LogUtils.d(TAG, " startSource启动信源 " + sourceName);
        Intent intent_hdmi = new Intent();
        intent_hdmi.setComponent(new ComponentName("com.softwinner.awlivetv", "com.softwinner.awlivetv.MainActivity"));
        intent_hdmi.putExtra("input_source", sourceName);
        intent_hdmi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent_hdmi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent_hdmi);
    }

    /**
     * 第一次初始化默认快捷栏app数据
     */
    private boolean initDataApp() {
        boolean isLoad = true;
        SharedPreferences sharedPreferences = ShareUtil.getInstans(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int code = sharedPreferences.getInt("code", 0);
        LogUtils.d(TAG, " initDataApp读code值 " + code);
        int reload = SystemProperties.getInt("persist.htc.reload", 0);
        if (reload == 1) {
            DBUtils.getInstance(this).deleteTable();
        }
        if (code == 0 || reload == 1) {  //保证配置文件只在最初读一次
            //1、优先连接服务器读取配置

            //2、服务器没有，就读本地
            LogUtils.d(TAG, " MainActivity开始读取配置文件 ");

            // 读取文件,优先读取oem分区
            File file = new File("/oem/shortcuts.config");

            if (!file.exists()) {
                file = new File("/system/shortcuts.config");
            }

            if (!file.exists()) {
                LogUtils.d(TAG, " 配置文件不存在 ");
                DBUtils.getInstance(this).deleteTable();

                editor.putInt("code", 1);
                editor.apply();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 设置首页的配置图标
                        try {
                            setDefaultMainIcon();
                            setDefaultStatusBarIcon();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                return false;
            }

            try {
                FileInputStream is = new FileInputStream(file);
                byte[] b = new byte[is.available()];
                is.read(b);
                String result = new String(b);

                LogUtils.d(TAG, " MainActivity读取到的配置文件 " + result); //这里把配置文件原封不动的读取出来，不做一整行处理

                List<String> residentList = new ArrayList<>();
                JSONObject obj = new JSONObject(result);

                //读取默认背景配置 这块提前放到MyApplication中
//                readDefaultBackground(obj);

                //读取快捷栏图标
                readStatusBar(obj);

                //读取首页四大APP图标
                readMain(obj);

                //读取specialApps
//                readSpecialApps(obj, residentList);

                //读取APP快捷图标
                readShortcuts(obj, residentList, sharedPreferences);

                //读取filterApps屏蔽显示的APP
                readFilterApps(obj);

                //读取右边list第一个、第三个、第四个的配置
                readListModules(obj);
                LogUtils.d(TAG, " 当前的语言环境是： " + LanguageUtil.getCurrentLanguage());

                //读取品牌图标
                readBrand(obj);

                //是否显示时间
                //readTime();

                editor.putString("resident", residentList.toString());

                editor.putInt("code", 1);
                editor.apply();
                SystemProperties.set("persist.htc.reload", String.valueOf(0));
                is.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                isLoad = false;
            }
        }

        //设置首页的配置图标
        // 在主线程中更新 UI
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 设置首页的配置图标
                try {
                    setIconOrText();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return isLoad;
    }

    private void readStatusBar(JSONObject obj) {
        try {
            if (obj.has("statusBar")) {
                JSONArray jsonarrray = obj.getJSONArray("statusBar");
                for (int i = 0; i < jsonarrray.length(); i++) {
                    JSONObject jsonobject = jsonarrray.getJSONObject(i);
                    String tag = jsonobject.getString("tag");
                    String iconPath = jsonobject.getString("iconPath");
                    String iconPath2 = jsonobject.getString("iconPath2");
                    String iconDirectory = jsonobject.getString("iconDirectory");

                    Drawable drawable = FileUtils.loadImageAsDrawable(this, iconPath);
                    setStatusBarIcon(tag, drawable);
                    if (tag.equals("home_bluetooth")) {
                        Utils.bt_disconnect = drawable;
                        Utils.bt_connected = FileUtils.loadImageAsDrawable(this, iconPath2);
                    }
                    DBUtils.getInstance(this).insertStatusBarData(tag, iconPath, iconPath2, iconDirectory);

                    LogUtils.d(TAG, " 读取到的statusBar " + tag + iconPath + iconPath2 + iconDirectory);
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 设置首页的配置图标
                        try {
                            setDefaultStatusBarIcon();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStatusBarIcon(String tag, Drawable drawable) {
        LogUtils.d(TAG, " setStatusBarIcon " + tag);
        switch (tag) {
            case "support":
                setIcon(customBinding.support, drawable, R.drawable.bar_help);
                break;
//            case "battery":
//                setIcon(customBinding.battery, drawable, R.drawable.battery_4);
//                break;
            case "usb_connect":
                setIcon(customBinding.usbConnect, drawable, R.drawable.usb_green);
                break;
            case "clear_memory":
                setIcon(customBinding.clearMemory, drawable, R.drawable.bar_clean);
                break;
            case "wallpaper":
                setIcon(customBinding.wallpaper, drawable, R.drawable.wapper_custom);
                break;
            case "home_bluetooth":
                setIcon(customBinding.homeBluetooth, drawable, R.drawable.bt_custom2);
                break;
            case "home_ethernet":
                setIcon(customBinding.homeEthernet, drawable, R.drawable.bar_ethernet_green);
                break;
//            case "home_wifi":
//                setIcon(customBinding.homeWifi, drawable, R.drawable.wifi_custom_4);
//                break;
        }
    }

    private void setDefaultStatusBarIcon() {
        LogUtils.d(TAG, " setDefaultStatusBarIcon ");
        customBinding.support.setImageResource(R.drawable.bar_help);
        customBinding.battery.setImageResource(R.drawable.battery_4);
        customBinding.usbConnect.setImageResource(R.drawable.usb_green);
        customBinding.clearMemory.setImageResource(R.drawable.bar_clean);
        customBinding.wallpaper.setImageResource(R.drawable.wapper_custom);
        customBinding.homeBluetooth.setImageResource(R.drawable.bt_custom2);
        customBinding.homeEthernet.setImageResource(R.drawable.bar_ethernet_green);
        customBinding.homeWifi.setImageResource(R.drawable.wifi_custom_4);
    }

    private void readDefaultBackground(JSONObject obj) {
        try {
            if (obj.has("defaultbackground")) {
                String DefaultBackground = obj.getString("defaultbackground").trim();
                LogUtils.d(TAG, " readDefaultBackground " + DefaultBackground);
                // 将字符串存入数据库；
                SharedPreferences sharedPreferences = ShareUtil.getInstans(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Contants.DefaultBg, DefaultBackground);
                editor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readMain(JSONObject obj) {
        try {
            if (obj.has("mainApp")) {
                JSONArray jsonarrray = obj.getJSONArray("mainApp");

                for (int i = 0; i < jsonarrray.length(); i++) {
                    JSONObject jsonobject = jsonarrray.getJSONObject(i);
                    String tag = jsonobject.getString("tag");
                    String appName = jsonobject.getString("appName");
                    String iconPath = jsonobject.getString("iconPath");
                    String action = jsonobject.getString("action");

                    LogUtils.d(TAG, " 读取到的mainApp " + tag + appName + iconPath + action);

                    //从iconPath中把png读出来赋值给drawable
                    Drawable drawable = FileUtils.loadImageAsDrawable(this, iconPath);

                    setMainIcon(tag, drawable);

                    //把读到的数据放入db数据库
                    DBUtils.getInstance(this).insertMainAppData(tag, appName, drawable, action);

                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 设置首页的配置图标
                        try {
                            setDefaultMainIcon();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void readSpecialApps(JSONObject obj, List<String> residentList) {
        try {
            if (obj.has("specialApps")) {
                DBUtils.getInstance(this).clearSpecialAppsTableAndResetId(); //重写之前清空数据表
                JSONArray jsonarrray = obj.getJSONArray("specialApps");
                for (int i = 0; i < jsonarrray.length(); i++) {
                    JSONObject jsonobject = jsonarrray.getJSONObject(i);
                    String appName = jsonobject.getString("appName");
                    String packageName = jsonobject.getString("packageName");

                    Utils.specialAppsList += packageName;

                    String iconPath = jsonobject.getString("iconPath");
                    String continent = jsonobject.getString("continent");
                    String countryCode = jsonobject.getString("countryCode");
                    Drawable drawable = FileUtils.loadImageAsDrawable(this, iconPath);
//                    if (!DBUtils.getInstance(this).isExistSpecial(packageName)) {
                    long addCode = DBUtils.getInstance(this).addSpeciales(appName, packageName, drawable, continent, countryCode);
                    LogUtils.d(TAG, " specialApps 添加快捷数据库成功 " + appName + " " + packageName);
//                    }
                    LogUtils.d(TAG, " specialApps读到的数据 " + appName + " " + packageName + " " + iconPath + " " + continent + " " + " " + countryCode);

//                    LogUtils.d(TAG," Utils.specialAppsList "+Utils.specialAppsList);
                }
                LogUtils.d(TAG, " Utils.specialAppsList " + Utils.specialAppsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readShortcuts(JSONObject obj, List<String> residentList, SharedPreferences sharedPreferences) {
        try {
            if (obj.has("apps")) {
                JSONArray jsonarrray = obj.getJSONArray("apps");
                //xuhao
                //用户每次更新配置，必须把原来数据库中保存的上一次失效的数据清除掉
                ArrayList<AppSimpleBean> mylist = DBUtils.getInstance(this).getFavorites();
                for (int i = 0; i < jsonarrray.length(); i++) {
                    JSONObject jsonobject = jsonarrray.getJSONObject(i);
                    String packageName = jsonobject.getString("packageName");

                    for (int d = 0; d < mylist.size(); d++) {
                        LogUtils.d(TAG, " 对比 " + mylist.get(d).getPackagename() + " " + packageName);
                        if (mylist.get(d).getPackagename().equals(packageName)) { //去除掉两个队列中相同的部分
                            LogUtils.d(TAG, " 移除两个队列中的相同部分 " + packageName + mylist.size());
                            mylist.remove(d);
                            LogUtils.d(TAG, " mylist.size " + mylist.size());
                            break;
                        }
                    }
                }
                for (int d = 0; d < mylist.size(); d++) { //剩余的不同的就是无效的，把无效的delet，保证每次修改配置之后都正确生效
                    if (sharedPreferences.getString("resident", "").contains(mylist.get(d).getPackagename())) {
                        LogUtils.d(TAG, " 移除APP快捷图标栏废弃的配置 ");
                        DBUtils.getInstance(this).deleteFavorites(mylist.get(d).getPackagename());
                    }
                }
                //xuhao
                for (int i = 0; i < jsonarrray.length(); i++) {
                    JSONObject jsonobject = jsonarrray.getJSONObject(i);
                    String appName = jsonobject.getString("appName");
                    String packageName = jsonobject.getString("packageName");
                    String iconPath = jsonobject.getString("iconPath");
                    boolean resident = jsonobject.getBoolean("resident"); //用于标志移除上一轮配置文件和这一轮配置文件不需要的App
                    if (resident) {
                        residentList.add(packageName);
                    }
                    Drawable drawable = FileUtils.loadImageAsDrawable(this, iconPath);
                    if (!DBUtils.getInstance(this).isExistData(packageName)) {
                        long addCode = DBUtils.getInstance(this).addFavorites(appName, packageName, drawable);
                        LogUtils.d(TAG, " Shortcuts 添加快捷数据库成功 " + appName + " " + packageName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFilterApps(JSONObject obj) {
        try {
            if (obj.has("filterApps")) {
                String filterApps = obj.getString("filterApps");
                LogUtils.d(TAG, " readFilterApps " + filterApps);
                // 将字符串按分号拆分成数组
                String[] packageNames = filterApps.split(";");
                DBUtils.getInstance(this).insertFilterApps(packageNames);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void readListModules(JSONObject obj) {
        try {
            if (obj.has("listModules")) {
                JSONArray jsonarrray = obj.getJSONArray("listModules");
                for (int i = 0; i < jsonarrray.length(); i++) {
                    JSONObject jsonobject = jsonarrray.getJSONObject(i);
                    String tag = jsonobject.getString("tag");
                    String iconPath = jsonobject.getString("iconPath");
                    String action = jsonobject.getString("action");
                    JSONObject textObject = jsonobject.getJSONObject("text");
                    JSONArray keys = textObject.names();
                    LogUtils.d(TAG, " 读取到的listModules keys " + keys);
                    if (keys != null) {
                        for (int b = 0; b < keys.length(); b++) {
                            String key = keys.getString(b);
                            String value = textObject.getString(key);
                            LogUtils.d(TAG, " 读取到的listModules " + tag + iconPath + key + value);
                            hashtable.put(key, value);
                        }
                    }
                    //从iconPath中把png读出来赋值给drawable
                    Drawable drawable = FileUtils.loadImageAsDrawable(this, iconPath);
                    //将读取到的数据写入数据库
                    DBUtils.getInstance(this).insertListModulesData(tag, drawable, hashtable, action);
                    hashtable.clear();
//                DBUtils.getInstance(this).getHashtableFromDatabase("list1");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readBrand(JSONObject obj) {
        try {
            if (obj.has("brandLogo")) {
                JSONObject jsonobject = obj.getJSONObject("brandLogo");
                String iconPath = jsonobject.getString("iconPath");
                Drawable drawable = FileUtils.loadImageAsDrawable(this, iconPath);
                DBUtils.getInstance(this).insertBrandLogoData(drawable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ArrayList<ShortInfoBean> loadHomeAppData() {
        ArrayList<AppSimpleBean> appSimpleBeans = DBUtils.getInstance(this).getFavorites(); //获取配置文件中设置的首页显示App
        ArrayList<ShortInfoBean> shortInfoBeans = new ArrayList<>();
//        ArrayList<AppInfoBean> appList = AppUtils.getApplicationMsg(this);//获取所有的应用(排除了配置文件中拉黑的App)
        //xuhao add 默认添加我的应用按钮
        ShortInfoBean mshortInfoBean = new ShortInfoBean();
        mshortInfoBean.setAppicon(ContextCompat.getDrawable(this, R.drawable.home_app_manager));
        shortInfoBeans.add(mshortInfoBean);
        //xuhao
        //特定IP配置
        setIpShortInfo(shortInfoBeans);
        ArrayList<AppInfoBean> appList = AppUtils.getApplicationMsg(this);//获取所有的应用(排除了配置文件中拉黑的App)
        LogUtils.d(TAG, " loadHomeAppData快捷图标 appList " + appList.size());
        LogUtils.d(TAG, " loadHomeAppData快捷图标 appSimpleBeans " + appSimpleBeans.size());
        for (int i = 0; i < appSimpleBeans.size(); i++) {
            ShortInfoBean shortInfoBean = new ShortInfoBean();
            shortInfoBean.setPackageName(appSimpleBeans.get(i).getPackagename());

            LogUtils.d(TAG, " loadHomeAppData快捷图标 appSimpleBeans.get(i) " + appSimpleBeans.get(i).getPackagename());
            for (int j = 0; j < appList.size(); j++) {
                if (appSimpleBeans.get(i).getPackagename()
                        .equals(appList.get(j).getApppackagename())) {
                    shortInfoBean.setAppicon(appList.get(j).getAppicon());
                    shortInfoBean.setAppname(appList.get(j).getAppname());
                    LogUtils.d(TAG, " loadHomeAppData快捷图标 setAppname " + appList.get(j).getAppname());
                    break;
                }
            }
            shortInfoBeans.add(shortInfoBean);
        }

        return shortInfoBeans;
    }

    private void setIpShortInfo(ArrayList<ShortInfoBean> shortInfoBeans) {
        try {
            String country_code = Settings.System.getString(getContentResolver(), "ip_country_code");
            LogUtils.d(TAG, " ip_country_code " + country_code);
            if (country_code != null) {
                String[] continent_countryCode = country_code.split(",");
                String continent = null;
                String code = null;
                //分情况提取 continent 和 code
                if (continent_countryCode.length > 1) {
                    continent = continent_countryCode[0];
                    code = continent_countryCode[1];
                } else if (continent_countryCode.length == 1 && !continent_countryCode[0].isEmpty()) {
                    if (continent_countryCode[0].contains("洲")) {
                        continent = continent_countryCode[0];
                        code = null;
                    } else {
                        continent = null;
                        code = continent_countryCode[0];
                    }
                } else {
                    LogUtils.d(TAG, "setIpShortInfo 获取到的ip_country_code 格式不对");
                    return;
                }
                Utils.specialApps = DBUtils.getInstance(this).querySpecialApps(continent, code);
                if (Utils.specialApps != null) {
                    ShortInfoBean shortInfoBean = new ShortInfoBean();
                    shortInfoBean.setAppname(Utils.specialApps.getAppName());
                    shortInfoBean.setPackageName(Utils.specialApps.getPackageName());
                    shortInfoBean.setAppicon(DBUtils.getInstance(this).byteArrayToDrawable(Utils.specialApps.getIconData()));
                    shortInfoBeans.add(shortInfoBean);
                    Utils.specialAppsList = "";

                    //SpecialApp为GTV时，应用商店图标改成轮播巴西图片
                    if (Utils.specialApps.getAppName().equals("GTV") && code.equals("BR")) {
//                        handler.post(this::showGtvBanner);
//                        showGtvBanner();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showGtvBanner();
                                Utils.gtvBanner = true;
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideGtvBanner();
                                Utils.gtvBanner = false;
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideGtvBanner();
                            Utils.gtvBanner = false;
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showGtvBanner() {
        LogUtils.d(TAG, "useBanner: ");
        List<ImageBean> images = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            ImageBean dataBean = new ImageBean();
            dataBean.setImageUrl("file:///android_asset/gtv_br/brazil_football_" + i + ".png");
            images.add(dataBean);
        }
        customBinding.icon4.setVisibility(View.GONE);
        customBinding.gtvBanner.setVisibility(View.VISIBLE);
        customBinding.gtvBanner.start();
        customBinding.gtvBanner.isAutoLoop(true);
        customBinding.gtvBanner.setUserInputEnabled(false);
        customBinding.gtvBanner.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        customBinding.gtvBanner.setFocusable(false);

        customBinding.gtvBanner.setAdapter(new BannerImageAdapter<ImageBean>(images) {
            @Override
            public void onBindView(BannerImageHolder holder, ImageBean data, int position, int size) {
                // Log.i(TAG, "onBindView: " + data.imageUrl);

                Glide.with(mContext)
                        .load(data.getImageUrl())
                        // 缓存原始+解码数据
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        // 禁用内存缓存（调试用）true打开
                        .skipMemoryCache(false)
                        .into(holder.imageView);
            }
        }, true).setIndicator(new CircleIndicator(mContext), false);
    }

    private void hideGtvBanner() {
        LogUtils.d(TAG, "hideGtvBanner");
        customBinding.icon4.setVisibility(View.VISIBLE);
        customBinding.gtvBanner.setVisibility(View.GONE);
        if (MyApplication.config.layout_select == 2 || MyApplication.config.layout_select == 3) {
            customBinding.icon4.setImageResource(R.drawable.appstore2);
        } else {
            customBinding.icon4.setImageResource(R.drawable.appstore);
        }
        customBinding.gtvBanner.stop();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
            return true;
        return super.dispatchKeyEvent(event);
    }

    private void updateBle() {
        LogUtils.d(TAG, " updateBle ");
        boolean isConnected = BluetoothUtils.getInstance(this).isBluetoothConnected();
        if (isConnected) {
//            mainBinding.homeBluetooth.setBackgroundResource(R.drawable.bluetooth_con);
            if (Utils.bt_connected == null) {
                customBinding.homeBluetooth.setImageResource(R.drawable.bt_custom_green);
            } else {
                customBinding.homeBluetooth.setImageDrawable(Utils.bt_connected);
            }
        } else {
//            mainBinding.homeBluetooth.setBackgroundResource(R.drawable.bluetooth_not);
            if (Utils.bt_disconnect == null) {
                customBinding.homeBluetooth.setImageResource(R.drawable.bt_custom2);
            } else {
                customBinding.homeBluetooth.setImageDrawable(Utils.bt_disconnect);
            }
        }
    }

    private void updateTime() {
//        String builder = TimeUtils.getCurrentDate() +
//                " | " +
//                TimeUtils
//                        .getCurrentTime(this);
//        mainBinding.timeTv.setText(builder);
        customBinding.timeTv.setText(TimeUtils.getCurrentTime(this));
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(networkReceiver);
        unregisterReceiver(timeReceiver);
        unregisterReceiver(blueReceiver);
        unregisterReceiver(wifiReceiver);
        unregisterReceiver(usbDeviceReceiver);
        unregisterReceiver(appReceiver);
        unregisterReceiver(batteryReceiver);
        getApplicationContext().unregisterReceiver(displaySettingsReceiver);
        displaySettingsReceiver = null;
        unregisterReceiver(initAngleReceiver);
//        unregisterReceiver(unlockReceiver);

        getContentResolver().unregisterContentObserver(notificationObserver);
        if (Utils.gtvBanner) {
            customBinding.gtvBanner.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void bluetoothChange() {
        updateBle();
    }

    @Override
    public void UsbDeviceChange() {

        LogUtils.d("UsbDeviceChange ", String.valueOf(Utils.hasUsbDevice));

        if (Utils.hasUsbDevice) {
            LogUtils.d("UsbDeviceChange ", "usbConnect设为VISIBLE");
            customBinding.rlUsbConnect.setVisibility(View.VISIBLE);
        } else {
            customBinding.rlUsbConnect.clearFocus();
            customBinding.rlUsbConnect.clearAnimation();
            customBinding.rlUsbConnect.setVisibility(View.GONE);
            LogUtils.d("UsbDeviceChange ", "usbConnect设为GONE");
        }
    }

    @Override
    public void changeTime() {
        updateTime();
    }

    @Override
    public void getWifiState(int state) {
        if (state == 1) {
//            mainBinding.homeWifi.setBackgroundResource(R.drawable.wifi_not);
            customBinding.homeWifi.setImageResource(R.drawable.wifi_custom_4);
        }
    }

    @Override
    public void getWifiNumber(int count) {

        List<ScanResult> wifiList = wifiManager.getScanResults();
        LogUtils.d(TAG, "getWifiNumber " + count);

        if (count == 1) {
            customBinding.homeWifi.setImageResource(R.drawable.wifi_custom_4);
            return;
        } else if (count == 3) {
            customBinding.homeWifi.setImageResource(R.drawable.wifi_custom_green_4);
            return;
        }

        LogUtils.d(TAG, " level数据" + count);
        if (count < -85) {
            customBinding.homeWifi.setImageResource(R.drawable.wifi_custom_green_1);
        } else if (count < -70) {
            customBinding.homeWifi.setImageResource(R.drawable.wifi_custom_green_2);
        } else if (count < -60) {
            customBinding.homeWifi.setImageResource(R.drawable.wifi_custom_green_3);
        } else if (count < -50) {
            customBinding.homeWifi.setImageResource(R.drawable.wifi_custom_green_4);
        } else {
            customBinding.homeWifi.setImageResource(R.drawable.wifi_custom_green_4);
        }
    }

    @Override
    public void connect() {
//        if (isFrist) {
//            isFrist = false;
//            requestAppData();
//        }
    }

    @Override
    public void disConnect() {

    }

    private void requestChannelData() {
        if (requestFlag)
            return;

        if (!NetWorkUtils.isNetworkConnected(this)) {
            ToastUtil.showShortToast(this, getString(R.string.network_disconnect_tip));
            return;
        }
        requestFlag = true;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        OkHttpClient okHttpClient = builder.build();
        String time = String.valueOf(System.currentTimeMillis());
        String chan = Constants.getChannel();
        LogUtils.d("chanId " + chan);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.addHeader("chanId", chan);
        requestBuilder.addHeader("timestamp", time);
        HashMap<String, Object> requestData = new HashMap<>();
        requestData.put("chanId", chan);
        String deviceId = Constants.getWan0Mac();
        if (Constants.isOne(Uri.complexType, 1)) {
            String aesKey = VerifyUtil.initKey();
            LogUtils.d("aesKey " + aesKey);
            deviceId = VerifyUtil.encrypt(deviceId, aesKey, aesKey, VerifyUtil.AES_CBC);
            LogUtils.d("deviceId " + deviceId);
        }
        requestData.put("deviceId", deviceId);
        requestData.put("model", SystemProperties.get("persist.sys.modelName", "project"));
        requestData.put("sysVersion", Constants.getHtcDisplay());
        try {
            requestData.put("verCode", getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            requestData.put("verCode", 10);
            throw new RuntimeException(e);
        }

        requestData.put("complexType", Uri.complexType);//
        Gson gson = new Gson();
        String json = gson.toJson(requestData);
        requestBuilder.url(Uri.SIGN_APP_LIST_URL)
                .post(RequestBody.create(json, MediaType.parse("application/json;charset=UTF-8")));
        String sign = RequestManager.getInstance().getSign(json, chan, time);
        LogUtils.d("sign " + sign);
        requestBuilder.addHeader("sign", sign);
        Request request = requestBuilder.build();
        okHttpClient.newCall(request).enqueue(channelCallback);
    }

    private void startAppFormChannel() {
        for (AppsData appsData : channelData.getData()) {
            if (appName.equals(appsData.getName())) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.htc.storeos", "com.htc.storeos.AppDetailActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("appData", new Gson().toJson(appsData));
                startActivity(intent);
                return;
            }
        }
        ToastUtil.showShortToast(this, getString(R.string.data_none));
    }

    private void setIconOrText() {

        //StatusBar
        setStatusBar();

        //MainApp
        setMainApp();

        //ListModules
        setListModules();

        //brandLogo
        setbrandLogo();

        //DefaultBackground   改成提前用setDefaultBackgroundById去设置背景
//        setDefaultBackground();

    }

    //wifi和电源图标太多 暂时不可配置
    private void setStatusBar() {
        LogUtils.d(TAG, "setStatusBar");
        StatusBarItem item = DBUtils.getInstance(this).queryStatusBarData("support");
        String iconPath = item != null ? item.iconPath : "";
        String iconPath2 = item != null ? item.iconPath2 : "";
        Drawable drawable = FileUtils.loadImageAsDrawable(this, iconPath);
        if (drawable != null) {
            customBinding.support.setImageDrawable(drawable);
        } else {
            customBinding.support.setImageResource(R.drawable.bar_help);
        }

//        item = DBUtils.getInstance(this).queryStatusBarData("battery");
//        iconPath = item != null ? item.iconPath : "";
//        drawable = FileUtils.loadImageAsDrawable(this, iconPath);
//        if (drawable != null) {
//            customBinding.battery.setImageDrawable(drawable);
//        } else {
//            customBinding.battery.setImageResource(R.drawable.battery_4);
//        }
        LogUtils.d(TAG, "setStatusBar battery.getDrawable() " + customBinding.battery.getDrawable());
        if (customBinding.battery.getDrawable() == null) {
            customBinding.battery.setImageResource(R.drawable.battery_4);
        }

        item = DBUtils.getInstance(this).queryStatusBarData("usb_connect");
        iconPath = item != null ? item.iconPath : "";
        drawable = FileUtils.loadImageAsDrawable(this, iconPath);
        if (drawable != null) {
            customBinding.usbConnect.setImageDrawable(drawable);
        } else {
            customBinding.usbConnect.setImageResource(R.drawable.usb_green);
        }

        item = DBUtils.getInstance(this).queryStatusBarData("clear_memory");
        iconPath = item != null ? item.iconPath : "";
        drawable = FileUtils.loadImageAsDrawable(this, iconPath);
        if (drawable != null) {
            customBinding.clearMemory.setImageDrawable(drawable);
        } else {
            customBinding.clearMemory.setImageResource(R.drawable.bar_clean);
        }

        item = DBUtils.getInstance(this).queryStatusBarData("wallpaper");
        iconPath = item != null ? item.iconPath : "";
        drawable = FileUtils.loadImageAsDrawable(this, iconPath);
        if (drawable != null) {
            customBinding.wallpaper.setImageDrawable(drawable);
        } else {
            customBinding.wallpaper.setImageResource(R.drawable.wapper_custom);
        }

        item = DBUtils.getInstance(this).queryStatusBarData("home_bluetooth");
        iconPath = item != null ? item.iconPath : "";
        iconPath2 = item != null ? item.iconPath2 : "";
        drawable = FileUtils.loadImageAsDrawable(this, iconPath);
        Utils.bt_disconnect = drawable;
        Utils.bt_connected = FileUtils.loadImageAsDrawable(this, iconPath2);
        LogUtils.d(TAG, " home_bluetooth ");
        updateBle();
//        if (drawable != null) {
//            customBinding.homeBluetooth.setImageDrawable(drawable);
//        } else {
//            customBinding.homeBluetooth.setImageResource(R.drawable.bt_custom2);
//        }

        item = DBUtils.getInstance(this).queryStatusBarData("home_ethernet");
        iconPath = item != null ? item.iconPath : "";
        drawable = FileUtils.loadImageAsDrawable(this, iconPath);
        if (drawable != null) {
            customBinding.homeEthernet.setImageDrawable(drawable);
        } else {
            customBinding.homeEthernet.setImageResource(R.drawable.bar_ethernet_green);
        }

//        item = DBUtils.getInstance(this).queryStatusBarData("home_wifi");
//        iconPath = item != null ? item.iconPath : "";
//        drawable = FileUtils.loadImageAsDrawable(this, iconPath);
//        if (drawable != null) {
//            customBinding.homeWifi.setImageDrawable(drawable);
//        } else {
//            c
//        }
        LogUtils.d(TAG, "setStatusBar homeWifi.getDrawable() " + customBinding.homeWifi.getDrawable());
//        if(customBinding.homeWifi.getDrawable() == null) {
//            customBinding.homeWifi.setImageResource(R.drawable.wifi_custom_4);
        updateWifiIcon(customBinding.homeWifi);
//        }
    }

    private void updateWifiIcon(ImageView wifiIconView) {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null || !wifiManager.isWifiEnabled()) {
            LogUtils.d(TAG, "Wi-Fi is disabled");
            wifiIconView.setImageResource(R.drawable.wifi_custom_4);
            return;
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null || wifiInfo.getNetworkId() == -1) {
            LogUtils.d(TAG, "Not connected to any Wi-Fi network");
            wifiIconView.setImageResource(R.drawable.wifi_custom_4);
            return;
        }

        getWifiNumber(getStrength(getApplicationContext()));
    }

    private void setMainApp() {
        Drawable drawable = DBUtils.getInstance(this).getIconDataByTag("icon1");
        if (drawable != null) {
            customBinding.icon1.setImageDrawable(drawable);
        } else {
            customBinding.icon1.setImageResource(R.drawable.home_app_netflix);
        }

        drawable = DBUtils.getInstance(this).getIconDataByTag("icon2");
        if (drawable != null) {
            customBinding.icon2.setImageDrawable(drawable);
        } else {
            customBinding.icon2.setImageResource(R.drawable.home_app_youtube);
        }
        drawable = DBUtils.getInstance(this).getIconDataByTag("icon3");
        if (drawable != null) {
            customBinding.icon3.setImageDrawable(drawable);
        } else {
            customBinding.icon3.setImageResource(R.drawable.home_app_disney);
        }

        drawable = DBUtils.getInstance(this).getIconDataByTag("icon4");
        if (drawable != null) {
            customBinding.icon4.setImageDrawable(drawable);
        } else {
            if (MyApplication.config.layout_select == 2 || MyApplication.config.layout_select == 3) {
                customBinding.icon4.setImageResource(R.drawable.appstore2);
            } else {
                customBinding.icon4.setImageResource(R.drawable.appstore);
            }
        }
    }

    private void setMainIcon(String tag, Drawable drawable) {
        LogUtils.d(TAG, " setMainIcon " + tag);
        switch (tag) {
            case "icon1":
                setIcon(customBinding.icon1, drawable, R.drawable.home_app_netflix);
                break;
            case "icon2":
                setIcon(customBinding.icon2, drawable, R.drawable.home_app_youtube);
                break;
            case "icon3":
                setIcon(customBinding.icon3, drawable, R.drawable.home_app_disney);
                break;
            case "icon4":
                int id = (MyApplication.config.layout_select == 2 || MyApplication.config.layout_select == 3) ? R.drawable.appstore2 : R.drawable.appstore;
                setIcon(customBinding.icon4, drawable, id);
                break;
        }
    }

    private void setDefaultMainIcon() {
        customBinding.icon1.setImageResource(R.drawable.home_app_netflix);
        customBinding.icon2.setImageResource(R.drawable.home_app_youtube);
        customBinding.icon3.setImageResource(R.drawable.home_app_disney);
        if (MyApplication.config.layout_select == 2 || MyApplication.config.layout_select == 3) {
            customBinding.icon4.setImageResource(R.drawable.appstore2);
        } else {
            customBinding.icon4.setImageResource(R.drawable.appstore);
        }
    }

    private void setIcon(ImageView view, Drawable drawable, int defaultId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 设置首页的配置图标
                try {
                    if (drawable != null) {
                        view.setImageDrawable(drawable);
                    } else {
                        view.setImageResource(defaultId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setListModules() {
        Drawable drawable = DBUtils.getInstance(this).getDrawableFromListModules("list1");
        if (drawable != null) {
            customBinding.eshareIcon.setImageDrawable(drawable);
            drawable = null;
        }
        drawable = DBUtils.getInstance(this).getDrawableFromListModules("list3");
        if (drawable != null) {
            customBinding.hdmiIcon.setImageDrawable(drawable);
            drawable = null;
        }
        drawable = DBUtils.getInstance(this).getDrawableFromListModules("list4");
        if (drawable != null) {
            customBinding.settingsIcon.setImageDrawable(drawable);
            drawable = null;
        }
        Hashtable<String, String> mHashtable1 = DBUtils.getInstance(this).getHashtableFromListModules("list1");
        Hashtable<String, String> mHashtable3 = DBUtils.getInstance(this).getHashtableFromListModules("list3");
        Hashtable<String, String> mHashtable4 = DBUtils.getInstance(this).getHashtableFromListModules("list4");
        LogUtils.d(TAG, "xu当前语言" + LanguageUtil.getCurrentLanguage());
        if (mHashtable1 != null) {
            String text = mHashtable1.get(LanguageUtil.getCurrentLanguage());
            LogUtils.d(TAG, "xu当前语言 text eshareText" + text);
            if (text != null && !text.isEmpty()) {
                customBinding.eshareText.setText(text);
            }
        }
        if (mHashtable3 != null) {
            String text = mHashtable3.get(LanguageUtil.getCurrentLanguage());
            LogUtils.d(TAG, "xu当前语言 text hdmiText" + text);
            if (text != null && !text.isEmpty()) {
                customBinding.hdmiText.setText(text);
            }
        }
        if (mHashtable4 != null) {
            String text = mHashtable4.get(LanguageUtil.getCurrentLanguage());
            LogUtils.d(TAG, "xu当前语言 text settingsText" + text);
            if (text != null && !text.isEmpty()) {
                customBinding.settingsText.setText(text);
            }
        }
    }

    private void setbrandLogo() {
        Drawable drawable = DBUtils.getInstance(this).getDrawableFromBrandLogo(1);
        if (drawable != null) {
            customBinding.brand.setImageDrawable(drawable);
        } else {
            customBinding.brand.setImageDrawable(null);
        }
    }

//    @SuppressLint("UseCompatLoadingForDrawables")
//    private void setDefaultBackgroundById() {
//        //如果用户自主修改了背景，那么重启之后不再设置默认背景start
//        SharedPreferences sharedPreferences = ShareUtil.getInstans(getApplicationContext());
//        int selectBg = sharedPreferences.getInt(Contants.SelectWallpaperLocal, -1);
//        if (selectBg != -1) {
//            LogUtils.d(TAG, " setDefaultBackground 用户已经自主修改了背景");
//            return;
//        }
//        //背景控制end
//        String defaultbg = sharedPreferences.getString(Contants.DefaultBg, "1");
//        LogUtils.d(TAG, " setDefaultBackground defaultbg " + defaultbg);
//        int number = Integer.parseInt(defaultbg);
//        LogUtils.d(TAG, " setDefaultBackground number " + number);
//        if (number > Utils.drawablesId.length) {
//            LogUtils.d(TAG, " setDefaultBackground 用户设置的默认背景，超出了范围");
//            return;
//        }
//        setWallPaper(Utils.drawablesId[number - 1]);
//        Drawable drawable = getResources().getDrawable(Utils.drawablesId[number - 1]);
//        MyApplication.mainDrawable = (BitmapDrawable) drawable;
//        setDefaultBg(drawable);
//    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setDefaultBackgroundById() {
        //如果用户自主修改了背景，那么重启之后不再设置默认背景start
        StartupTimer.mark("setDefaultBackgroundById开始");
        SharedPreferences sharedPreferences = ShareUtil.getInstans(getApplicationContext());
        StartupTimer.mark("ShareUtil.getInstans完成");
        int selectBg = sharedPreferences.getInt(Contants.SelectWallpaperLocal, -1);
        StartupTimer.mark("getInt(Contants.SelectWallpaperLocal完成");
        if (selectBg != -1) {
            LogUtils.d(TAG, " setDefaultBackground 用户已经自主修改了背景");
            return;
        }
        //背景控制end
        String defaultbg = sharedPreferences.getString(Contants.DefaultBg, "1");
        StartupTimer.mark("getString(Contants.DefaultBg完成");
//        String defaultbg = MyApplication.config.defaultbackground;
        if (defaultbg.isEmpty()) {
            defaultbg = "1";
        }
        int number = Integer.parseInt(defaultbg);
        LogUtils.d(TAG, " setDefaultBackground number " + number);
        LogUtils.d(TAG, " setDefaultBackground defaultbg " + defaultbg);
        StartupTimer.mark("Integer.parseInt(defaultbg)完成");
        if (Utils.customBackground) {
            String path = (String) Utils.drawables.get(number - 1);
            LogUtils.d(TAG, " loadImageFromPath path " + path);
            Drawable drawable = ImageUtils.loadImageFromPath(path, getApplicationContext());
            MyApplication.mainDrawable = (BitmapDrawable) drawable;
            setDefaultBg(drawable);
        } else {
            if (number > Utils.drawablesId.length) {
                LogUtils.d(TAG, " setDefaultBackground 用户设置的默认背景，超出了范围");
                return;
            }
            if (number == 1) {
                Drawable drawable = (Drawable) Utils.drawables.get(0);
                MyApplication.mainDrawable = (BitmapDrawable) drawable;
                setDefaultBg(drawable);
            } else if (number > 1) {
                setWallPaper(Utils.drawablesId[number - 1]);
                Drawable drawable = getResources().getDrawable(Utils.drawablesId[number - 1]);
                MyApplication.mainDrawable = (BitmapDrawable) drawable;
                setDefaultBg(drawable);
            }
        }
        StartupTimer.mark("setDefaultBackgroundById完成");
    }

//    private void setDefaultBackground() {
//        //如果用户自主修改了背景，那么重启之后不再设置默认背景start
//        SharedPreferences sharedPreferences = ShareUtil.getInstans(getApplicationContext());
//        int selectBg = sharedPreferences.getInt(Contants.SelectWallpaperLocal, -1);
//        if (selectBg != -1) {
//            LogUtils.d(TAG, " setDefaultBackground 用户已经自主修改了背景");
//            return;
//        }
//        //背景控制end
//        String defaultbg = sharedPreferences.getString(Contants.DefaultBg, "1");
//        LogUtils.d(TAG, " setDefaultBackground defaultbg " + defaultbg);
//        int number = Integer.parseInt(defaultbg);
//        LogUtils.d(TAG, " setDefaultBackground number " + number);
//        if (number > Utils.drawables.size()) {
//            LogUtils.d(TAG, " setDefaultBackground 用户设置的默认背景，超出了范围");
//            return;
//        }
//        MyApplication.mainDrawable = (BitmapDrawable) Utils.drawables.get(number - 1);
//        setWallPaper(Utils.drawables.get(number - 1));
//        setDefaultBg(Utils.drawables.get(number - 1));
//    }

//    private void setDefaultBg(int resId) {
//        threadExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                CopyResIdToSd(resId);
//                CopyResIdToSd(BlurImageView.BoxBlurFilter(MainActivity.this, resId));
//                if (new File(Contants.WALLPAPER_MAIN).exists()) {
//                    MyApplication.mainDrawable = new BitmapDrawable(BitmapFactory.decodeFile(Contants.WALLPAPER_MAIN));
//                }
//                if (new File(Contants.WALLPAPER_OTHER).exists())
//                    MyApplication.otherDrawable = new BitmapDrawable(BitmapFactory.decodeFile(Contants.WALLPAPER_OTHER));
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // 设置首页的配置图标
//                        try {
//                            setWallPaper();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//            }
//        });
//    }

    private void setDefaultBg(Drawable drawable) {
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                CopyDrawableToSd(drawable);
            }
        });
    }


    @Override
    public void appChange(String packageName) {
        LogUtils.d(TAG, "MainActivity 收到Change广播");
    }

    @Override
    public void appUnInstall(String packageName) {
        LogUtils.d(TAG, "MainActivity 收到卸载广播 " + packageName);
        SharedPreferences sp = ShareUtil.getInstans(this);
        SharedPreferences.Editor ed = sp.edit();
        String resident = sp.getString("resident", "");
        if (resident.contains(packageName)) {
            LogUtils.d(TAG, " 配置文件中apps：\"resident\":true 常驻首页前台，应用删除了，也不能从首页APP快捷栏移除");
            return;
        }
        DBUtils.getInstance(this).deleteFavorites(packageName);
//        short_list = loadHomeAppData();
//        handler.sendEmptyMessage(205);
    }

    @Override
    public void appInstall(String packageName) {
        LogUtils.d(TAG, "MainActivity 收到安装广播");
    }

    private void CopyResIdToSd(int resId) {
        File file = new File(Contants.WALLPAPER_DIR);
        if (!file.exists())
            file.mkdir();

        InputStream inputStream = getResources().openRawResource(resId);
        try {
            File file1 = new File(Contants.WALLPAPER_MAIN);
            if (file1.exists())
                file1.delete();

            FileOutputStream fileOutputStream = new FileOutputStream(file1);

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, bytesRead);
            }
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    private void CopyResIdToSd(Bitmap bitmap) {
//        File file1 = new File(Contants.WALLPAPER_DIR);
//        if (!file1.exists())
//            file1.mkdir();
//
//        File file = new File(Contants.WALLPAPER_OTHER);//将要保存图片的路径
//        if (file.exists())
//            file.delete();
//        try {
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
//            bos.flush();
//            bos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public int countUsbDevices(Context context) {
//        File storageDir = new File("/storage/");
//        int usbCount = 0;
//
//        if (storageDir.exists() && storageDir.isDirectory()) {
//            File[] directories = storageDir.listFiles();
//            LogUtils.d(TAG, "检测到  directories" + directories);
//            if (directories != null) {
//                for (File dir : directories) {
//                    LogUtils.d(TAG, "检测到  directories");
//                    // 检查子目录是否是一个挂载点，并且是否是外部可移动存储
//                    if (dir.isDirectory() && dir.canRead() && isUsbDevice(dir)) {
//                        usbCount++;
//                    }
//                }
//            }
//        }
//
//        LogUtils.d(TAG, "检测到 " + usbCount + " 个U盘");
//        return usbCount;
//    }

    // 辅助函数，用于判断给定目录是否为 USB 设备
    private boolean isUsbDevice(File dir) {
        try {
            // 获取目录的挂载信息
            String mountInfo = getMountInfo(dir);
            // 检查是否为支持的 USB 设备文件系统格式
            return mountInfo.contains("vfat") ||
                    mountInfo.contains("exfat") ||
                    mountInfo.contains("ntfs") ||
                    mountInfo.contains("fat32") ||
                    mountInfo.contains("fuse");
        } catch (Exception e) {
            LogUtils.e(TAG, "检查目录是否为 USB 设备时出错 " + e);
            return false;
        }
    }


    // 获取目录的挂载信息
    private String getMountInfo(File dir) {
        try {
            // 使用 "mount" 命令获取挂载信息
            Process process = Runtime.getRuntime().exec("mount");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.contains(dir.getAbsolutePath())) {
                    output.append(line);
                    break;
                }
            }

            reader.close();
            LogUtils.e(TAG, "检测到 output.toString() " + output.toString());
            return output.toString();
        } catch (IOException e) {
            LogUtils.e(TAG, "获取挂载信息时出错 " + e);
            return "";
        }
    }

    private void CopyDrawableToSd(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        //判断图片大小，如果超过限制就做缩小处理
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width * height * 4 >= MAX_BITMAP_SIZE) {
            bitmap = narrowBitmap(bitmap);
        }
        //缩小完毕
        MyApplication.mainDrawable = new BitmapDrawable(bitmap);
        File dir = new File(Contants.WALLPAPER_DIR);
        if (!dir.exists()) dir.mkdirs();
        File file1 = new File(Contants.WALLPAPER_MAIN);
//        if (file1.exists()) file1.delete();
        try (FileOutputStream fileOutputStream = new FileOutputStream(file1)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream); // 可根据需要更改格式
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void devicesPathAdd() {
        if (storageManager == null) {
            LogUtils.e(TAG, "devicesPathAdd manager is null return error!");
            return;
        }
        localDevicesList = storageManager.getStorageVolumes();
        LogUtils.d(TAG, " 检测到devicesPathAdd " + localDevicesList.size());
        StorageVolume storageVolume;
        for (int i = 0; i < localDevicesList.size(); i++) {
            storageVolume = localDevicesList.get(i);
//            LogUtils.d(TAG," 检测到storageVolume.getPath() "+storageVolume.getPath()+" "+Environment.getExternalStorageDirectory().getPath());
            if (!storageVolume.getPath().equals(Environment.getExternalStorageDirectory().getPath())) {
                if (storageVolume.getId().startsWith("public:179")) {
                    /* 获取SD卡设备路径列表 */
                    LogUtils.d(TAG, " 检测到SD卡 " + storageVolume.getPath());
                } else if (storageVolume.getId().startsWith("public:8")) {
                    /* 获取USB设备路径列表 */
                    Utils.hasUsbDevice = true;
                    Utils.usbDevicesNumber += 2;
                    if (customBinding.rlUsbConnect.getVisibility() == View.GONE) {
                        customBinding.rlUsbConnect.setVisibility(View.VISIBLE);
                    }
                    LogUtils.d(TAG, " 检测到USB设备 " + storageVolume.getPath() + " Utils.hasUsbDevice " + Utils.hasUsbDevice
                            + " Utils.usbDevicesNumber " + Utils.usbDevicesNumber);
                } else if (storageVolume.getPath().contains("sata")) {
                    /* 获取sata设备路径列表 */
                    LogUtils.d(TAG, " 检测到sata设备 " + storageVolume.getPath());
                }
            }
        }
    }

    /**
     * android 11 检测以太网连接
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public boolean isEthernetConnect(Context context) {
        try {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                // 以太网已连接
                // 以太网已断开
                networkCallback = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        super.onAvailable(network);
                        // 以太网已连接
                        isEther = true;
                        handler.sendEmptyMessage(1);
//                    Message msg = new Message();
//                    msg.what = ETHERNET_HANDLE;
//                    msg.arg1 = 1;
//                    mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onLost(Network network) {
                        super.onLost(network);
                        // 以太网已断开
                        isEther = false;
                        handler.sendEmptyMessage(0);
//                    Message msg2 = new Message();
//                    msg2.what = ETHERNET_HANDLE;
//                    msg2.arg1 = 0;
//                    mHandler.sendMessage(msg2);
                    }
                };
                NetworkRequest request = new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                        .build();
                connectivityManager.registerNetworkCallback(request, networkCallback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isEther;
    }

    // 背景是动画
//    public void showSourceDialog() {
//        // 创建一个 Dialog 对象
//        Dialog dialog = new Dialog(this);
//        dialog.setContentView(R.layout.dialog_source); // 使用自定义布局
//        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT); // 宽度为屏幕宽度，高度自适应内容
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // 设置黑色背景
//        dialog.getWindow().setGravity(Gravity.CENTER); // 设置在屏幕中央显示
//        // 获取 LinearLayout 来动态添加选项
//        LinearLayout layout = dialog.findViewById(R.id.source_layout);
//        // 设置 Lottie 动画视图
//        LottieAnimationView lottieBackground = dialog.findViewById(R.id.lottie_background);
//        for (int i = 0; i < Utils.sourceListTitle.length; i++) {
//            String title = Utils.sourceListTitle[i];
//            // 获取 LayoutInflater 对象
//            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
//            // 将 XML 布局文件转换为 View 对象
//            LinearLayout source_item = (LinearLayout) inflater.inflate(R.layout.source_item, null);
//            TextView source_title = (TextView) source_item.findViewById(R.id.source_title);
//            source_title.setText(title);
//            // 设置上下外边距
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    (int) getResources().getDimension(R.dimen.x_400),
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//            params.setMargins(0, (int) getResources().getDimension(R.dimen.x_20),
//                    0, (int) getResources().getDimension(R.dimen.x_20));
//            source_item.setLayoutParams(params);
//            source_item.setBackgroundResource(R.drawable.source_bg_custom);
//            int finalI = i;
//            source_item.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startSource(Utils.sourceList[finalI]);
//                }
//            });
//            source_item.setOnHoverListener(this);
//            // 将每一行的 LinearLayout 加入到主布局
//            layout.addView(source_item);
//        }
//        layout.setVisibility(View.INVISIBLE);
//        lottieBackground.playAnimation();
//        lottieBackground.addAnimatorListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                layout.setVisibility(View.VISIBLE);
//            }
//        });
//        // 显示 Dialog
//        dialog.show();
//    }

    //正常背景
    public void showSourceDialog() {
        // 创建一个 Dialog 对象
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_source2); // 使用自定义布局
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT); // 宽度为屏幕宽度，高度自适应内容
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // 设置黑色背景
        dialog.getWindow().setGravity(Gravity.CENTER); // 设置在屏幕中央显示
        // 获取 LinearLayout 来动态添加选项
        LinearLayout layout = dialog.findViewById(R.id.source_layout);
//        // 设置 Lottie 动画视图
//        LottieAnimationView lottieBackground = dialog.findViewById(R.id.lottie_background);
        for (int i = 0; i < Utils.sourceListTitle.length + 1; i++) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            // 将 XML 布局文件转换为 View 对象
            LinearLayout source_item = (LinearLayout) inflater.inflate(R.layout.source_item, null);
            TextView source_title = (TextView) source_item.findViewById(R.id.source_title);
            if (i == 0) {
                source_title.setText(getResources().getString(R.string.choose_source));
                source_title.setTextColor(getResources().getColor(R.color.black));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        (int) getResources().getDimension(R.dimen.x_400),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, (int) getResources().getDimension(R.dimen.x_30),
                        0, (int) getResources().getDimension(R.dimen.x_30));
                source_item.setLayoutParams(params);
                source_item.setFocusable(false);
                source_item.setFocusableInTouchMode(false);
                layout.addView(source_item);
                source_title.setSelected(true);
            } else {
                String title = Utils.sourceListTitle[i - 1];
                // 获取 LayoutInflater 对象
                source_title.setText(title);
                // 设置上下外边距
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        (int) getResources().getDimension(R.dimen.x_400),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0,
                        0, (int) getResources().getDimension(R.dimen.x_30));
                source_item.setLayoutParams(params);
                source_item.setBackgroundResource(R.drawable.source_bg_custom);
                int finalI = i;
                source_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startSource(Utils.sourceList[finalI - 1]);
                    }
                });
                source_item.setOnHoverListener(this);
                // 将每一行的 LinearLayout 加入到主布局
                layout.addView(source_item);
            }
        }
        // 显示 Dialog
        dialog.show();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        if (hasFocus) {
            if (id == R.id.home_eshare) {
                customBinding.eshareText.setSelected(true);
            } else if (id == R.id.rl_usb) {
                customBinding.fileText.setSelected(true);
            } else if (id == R.id.rl_hdmi1) {
                customBinding.hdmiText.setSelected(true);
            } else if (id == R.id.rl_settings) {
                customBinding.settingsText.setSelected(true);
            }
        } else {
            if (id == R.id.home_eshare) {
                customBinding.eshareText.setSelected(false);
            } else if (id == R.id.rl_usb) {
                customBinding.fileText.setSelected(false);
            } else if (id == R.id.rl_hdmi1) {
                customBinding.hdmiText.setSelected(false);
            } else if (id == R.id.rl_settings) {
                customBinding.settingsText.setSelected(false);
            }
        }

        if (hasFocus && MyApplication.config.layout_select == 2 && MyApplication.config.select_border) {
            if (id == R.id.home_netflix) {
                customBinding.icon1border.setVisibility(View.VISIBLE);
            } else if (id == R.id.home_youtube) {
                customBinding.icon2border.setVisibility(View.VISIBLE);
            } else if (id == R.id.home_disney) {
                customBinding.icon3border.setVisibility(View.VISIBLE);
            } else if (id == R.id.rl_Google) {
                customBinding.icon4border.setVisibility(View.VISIBLE);
            }

        } else if (!hasFocus && MyApplication.config.layout_select == 2 && MyApplication.config.select_border) {
            if (id == R.id.home_netflix) {
                customBinding.icon1border.setVisibility(View.GONE);
            } else if (id == R.id.home_youtube) {
                customBinding.icon2border.setVisibility(View.GONE);
            } else if (id == R.id.home_disney) {
                customBinding.icon3border.setVisibility(View.GONE);
            } else if (id == R.id.rl_Google) {
                customBinding.icon4border.setVisibility(View.GONE);
            }
        }

        if (hasFocus && MyApplication.config.layout_select == 3 && MyApplication.config.select_border) {
            if (id == R.id.home_netflix) {
                customBinding.icon1border.setVisibility(View.VISIBLE);
            } else if (id == R.id.home_youtube) {
                customBinding.icon2border.setVisibility(View.VISIBLE);
            } else if (id == R.id.home_disney) {
                customBinding.icon3border.setVisibility(View.VISIBLE);
            } else if (id == R.id.rl_Google) {
                customBinding.icon4border.setVisibility(View.VISIBLE);
            } else if (id == R.id.home_eshare) {
                customBinding.icon5border.setVisibility(View.VISIBLE);
            } else if (id == R.id.rl_usb) {
                customBinding.icon6border.setVisibility(View.VISIBLE);
            } else if (id == R.id.rl_hdmi1) {
                customBinding.icon7border.setVisibility(View.VISIBLE);
            } else if (id == R.id.rl_settings) {
                customBinding.icon8border.setVisibility(View.VISIBLE);
            }

        } else if (!hasFocus && MyApplication.config.layout_select == 3 && MyApplication.config.select_border) {
            if (id == R.id.home_netflix) {
                customBinding.icon1border.setVisibility(View.GONE);
            } else if (id == R.id.home_youtube) {
                customBinding.icon2border.setVisibility(View.GONE);
            } else if (id == R.id.home_disney) {
                customBinding.icon3border.setVisibility(View.GONE);
            } else if (id == R.id.rl_Google) {
                customBinding.icon4border.setVisibility(View.GONE);
            } else if (id == R.id.home_eshare) {
                customBinding.icon5border.setVisibility(View.GONE);
            } else if (id == R.id.rl_usb) {
                customBinding.icon6border.setVisibility(View.GONE);
            } else if (id == R.id.rl_hdmi1) {
                customBinding.icon7border.setVisibility(View.GONE);
            } else if (id == R.id.rl_settings) {
                customBinding.icon8border.setVisibility(View.GONE);
            }
        }

        super.onFocusChange(v, hasFocus);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        int id = v.getId();
        //解决按下键焦点跑到文件管理器的问题
        if ((id == R.id.rl_settings || id == R.id.rl_hdmi1) && keyCode == KeyEvent.KEYCODE_DPAD_DOWN && MyApplication.config.layout_select == 3) {
            LogUtils.d(TAG, " keCode " + keyCode + " " + event.getEventTime());
            if ((customBinding.rlSettings.hasFocus() || customBinding.rlHdmi1.hasFocus()) && event.getAction() == KeyEvent.ACTION_DOWN) {
                int itemCount = customBinding.shortcutsRv.getAdapter().getItemCount();
                if (itemCount > 4) {
                    return false;
                } else {
                    customBinding.shortcutsRv.post(() -> {
                        int lastPosition = itemCount - 1;
                        RecyclerView.ViewHolder viewHolder = customBinding.shortcutsRv.findViewHolderForAdapterPosition(lastPosition);
                        if (viewHolder != null && viewHolder.itemView != null) {
                            viewHolder.itemView.requestFocus();
                        }
                    });
                    return true;
                }
            }
        }
        //解决按右键焦点跑到AppStore的问题
//        if(id == R.id.rl_settings && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && MyApplication.config.layout_select == 3) {
//            if(customBinding.rlSettings.hasFocus() && event.getAction() == KeyEvent.ACTION_DOWN){
//                return true;
//            }
//        }

        // 解决按右键焦点跑到AppStore的问题，同时兼容RTL语言（如阿拉伯语）
        if (id == R.id.rl_settings && MyApplication.config.layout_select == 3) {
            boolean isRtl = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL;
            int blockedKey = isRtl ? KeyEvent.KEYCODE_DPAD_LEFT : KeyEvent.KEYCODE_DPAD_RIGHT;

            if (customBinding.rlSettings.hasFocus() && keyCode == blockedKey && event.getAction() == KeyEvent.ACTION_DOWN) {
                return true; // 屏蔽方向键，防止焦点穿透
            }
        }

        return false;
    }

    private void loadSupport() {
        LogUtils.d(TAG, "loadSupport");
        if ((MyApplication.config.support_directory.isEmpty() || !MyApplication.config.support) && !MyApplication.config.about_support) {
            LogUtils.d(TAG, "loadSupport 配置不对不加载");
            return;
        }
//        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".bmp", ".webp"};
        File directory = new File(MyApplication.config.support_directory);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        if (judgeLanguage(file)) {
                            LogUtils.d(TAG, "找到当前语言的support图片路径 " + file.getAbsolutePath());
                            break; // 找到一个匹配后就跳出循环
                        }
                    }
                }
                // 如果当前语言没找到，尝试找英文
                LogUtils.d(TAG, "loadSupport Utils.support_image_path " + Utils.support_image_path);
                if (Utils.support_image_path.isEmpty()) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().contains("_en")) {
                            Utils.support_image_path = file.getAbsolutePath();
                            LogUtils.d(TAG, "找不到当前语言，使用英文support图片路径: " + file.getAbsolutePath());
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean judgeLanguage(File file) {
        String name = file.getName();
        Locale currentLocale = Resources.getSystem().getConfiguration().getLocales().get(0);
        String languageCode;
        if ("zh".equals(currentLocale.getLanguage())) {
            // 区分 zh-CN、zh-TW、zh-HK
            languageCode = "_zh_" + currentLocale.getCountry();
        } else {
            // 其他语言只用语言码
            languageCode = "_" + currentLocale.getLanguage();
        }
        LogUtils.d("JudgeLanguage", "当前语言码: " + languageCode);
        if (name.contains(languageCode)) {
            Utils.support_image_path = file.getAbsolutePath();
            return true;
        }
        return false;
    }

    private void showSupportDialog() {
        Dialog dialog = new Dialog(this, R.style.DialogTheme);
        DialogSupportBinding supportBinding = DialogSupportBinding.inflate(LayoutInflater.from(this));
        dialog.setContentView(supportBinding.getRoot());
        File file = new File(Utils.support_image_path);
        if (file.exists()) {
            Glide.with(this)
                    .load(file)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            supportBinding.rlMain.setBackground(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // 可选：清除背景或设置占位图
                        }
                    });
        } else {
            LogUtils.e("ImageLoad", "File not found: " + Utils.support_image_path);
        }
        Window window = dialog.getWindow();
        if (window != null) {
            //去除系统自带的margin
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置dialog在界面中的属性
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            //背景全透明
            window.setDimAmount(0f);
        }
        WindowManager manager = getWindowManager();
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams params = window.getAttributes(); // 获取对话框当前的参数值
        params.width = d.getWidth();
        params.height = d.getHeight();
        window.setAttributes(params);
        dialog.show();
    }

    @Override
    public void unLock() {
        short_list = loadHomeAppData();
        handler.sendEmptyMessage(204);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LogUtils.d(TAG, " onConfigurationChanged ");
        super.onConfigurationChanged(newConfig);

        if (!Utils.cur_language.isEmpty()) {
            String[] parts = Utils.cur_language.split("-");
            Locale locale;
            String db_cur_language;
            if (parts.length == 2) {
                locale = new Locale(parts[0], parts[1]);
                db_cur_language = Utils.cur_language;
            } else {
                locale = new Locale(Utils.cur_language);
                db_cur_language = Utils.cur_language + "-";
            }
            LogUtils.d(TAG, " onConfigurationChanged Utils.cur_language " + Utils.cur_language);
            // 1. 设置新的语言
//            Locale.setDefault(locale);
            Configuration config = getResources().getConfiguration();
            config.setLocale(locale);
            // 2. 创建新的 Resources 实例
            Context context = createConfigurationContext(config);
            Resources newResources = context.getResources();
            // 3. 用新的资源手动设置 UI 文本
            updateUITexts(newResources);

            setListModulesText(db_cur_language);
        }
    }

    private void updateUITexts(Resources res) {
        customBinding.eshareText.setText(res.getString(R.string.Eshare));
        customBinding.fileText.setText(res.getString(R.string.Filemanager));
        customBinding.settingsText.setText(res.getString(R.string.settings));
    }

    private void setListModulesText(String db_cur_language) {

        Hashtable<String, String> mHashtable1 = DBUtils.getInstance(this).getHashtableFromListModules("list1");
        Hashtable<String, String> mHashtable3 = DBUtils.getInstance(this).getHashtableFromListModules("list3");
        Hashtable<String, String> mHashtable4 = DBUtils.getInstance(this).getHashtableFromListModules("list4");
        LogUtils.d(TAG, "setListModulesText" + db_cur_language);
        if (mHashtable1 != null) {
            String text = mHashtable1.get(db_cur_language);
            LogUtils.d(TAG, "setListModulesText text eshareText" + text);
            if (text != null && !text.isEmpty()) {
                customBinding.eshareText.setText(text);
            }
        }
        if (mHashtable3 != null) {
            String text = mHashtable3.get(db_cur_language);
            LogUtils.d(TAG, "setListModulesText text hdmiText" + text);
            if (text != null && !text.isEmpty()) {
                customBinding.hdmiText.setText(text);
            }
        }
        if (mHashtable4 != null) {
            String text = mHashtable4.get(db_cur_language);
            LogUtils.d(TAG, "setListModulesText text settingsText" + text);
            if (text != null && !text.isEmpty()) {
                customBinding.settingsText.setText(text);
            }
        }
    }

    @Override
    public void changeNoticeIcon(int number) {
        if (customBinding.notice != null) {
            if (number == 1) {
                customBinding.notice.setImageResource(R.drawable.bar_notice_on);
            } else if (number == 0) {
                customBinding.notice.setImageResource(R.drawable.bar_notice_off);
            }
        }
    }

    private void initContentObserver() {
        int value = Settings.Global.getInt(getContentResolver(), "notification", 1);
        if (value == 1) {
            customBinding.notice.setImageResource(R.drawable.bar_notice_on);
        }
        notificationObserver = new NotificationObserver(getApplicationContext(), this);
        getContentResolver().registerContentObserver(Settings.Global.getUriFor("notification"), false, notificationObserver);
    }

    /**
     * 获取信号
     *
     * @param context
     * @return
     */
    public int getStrength(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info.getBSSID() != null) {
            int strength = WifiManager.calculateSignalLevel(info.getRssi(), 4);

            int level = info.getRssi();
            // 链接速度
            // int speed = info.getLinkSpeed();
            // 链接速度单位
            // String units = WifiInfo.LINK_SPEED_UNITS;
            // Wifi源名称
            // String ssid = info.getSSID();
            LogUtils.d(TAG, "getStrength信号强度更新 " + strength + " " + level);
            return level;
        }
        return 0;
    }
}