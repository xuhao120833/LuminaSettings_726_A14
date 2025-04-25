package com.htc.luminaos.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.htc.luminaos.MyApplication;
import com.htc.luminaos.R;
import com.htc.luminaos.databinding.ActivityProjectBinding;
import com.htc.luminaos.databinding.ResetKeystoreLayoutBinding;
import com.htc.luminaos.receiver.VaFocusCallBack;
import com.htc.luminaos.receiver.VaFocusReceiver;
import com.htc.luminaos.settings.utils.Constants;
import com.htc.luminaos.utils.AppUtils;
import com.htc.luminaos.utils.Contants;
import com.htc.luminaos.utils.KeystoneUtils;
import com.htc.luminaos.utils.KeystoneUtils_726;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.ReflectUtil;
import com.htc.luminaos.utils.ShareUtil;
import com.htc.luminaos.utils.ToastUtil;
import com.htc.luminaos.utils.scUtils;
import com.htc.luminaos.widget.InitAngleDialog;
import com.htc.luminaos.widget.TimezoneDialog;
import com.softwinner.tv.AwTvDisplayManager;
import com.softwinner.tv.AwTvSystemManager;
import com.softwinner.tv.common.AwTvDisplayTypes;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProjectActivity extends BaseActivity implements View.OnKeyListener, VaFocusCallBack {

    private ActivityProjectBinding projectBinding;
    private int cur_project_mode = 0;
    private int old_project_mode = -1;
    List<String> project_name = new ArrayList<>();
    private AwTvDisplayManager tvDisplayManager;

    private String TAG = "ProjectActivity";

    private ExecutorService singer;
    private int left = 100;
    private int top = 100;
    private int right = 100;
    private int bottom = 100;
    private int max_value = 100;
    public int All;
    private int zoom_scale = 0;
    private int ZOOM_MAX = 20;

    private int zoom_value = 0;
    DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.CHINESE));//格式化小数
    private double scale = 1D;//缩放比例，根据选中的屏幕缩放模式
    private int step_x = 16;//X轴步进
    private int step_y = 9;//Y轴步进
    double zoom_step_x = (double) KeystoneUtils_726.lcd_w / 100;
    double zoom_step_y = (double) KeystoneUtils_726.lcd_h / 100;
    private SharedPreferences sharedPreferences;

    private int cur_device_Mode = 0;
    long cur_time = 0;

    Handler handler = new Handler();

    IntentFilter filter;
    VaFocusReceiver vaFocusReceiver;

    public int[] lt_xy = new int[2];
    public int[] rt_xy = new int[2];
    public int[] lb_xy = new int[2];
    public int[] rb_xy = new int[2];

    String[] screen_zoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "执行onCreate ProjectActivity");
        super.onCreate(savedInstanceState);
        projectBinding = ActivityProjectBinding.inflate(LayoutInflater.from(this));
        setContentView(projectBinding.getRoot());
        initView();
        initData();
        filter = new IntentFilter("intent.htc.vafocus");
        vaFocusReceiver = new VaFocusReceiver(this);
        registerReceiver(vaFocusReceiver, filter);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "执行onResume ProjectActivity");
        super.onResume();
        All = KeystoneUtils_726.readGlobalSettings(this, KeystoneUtils_726.ZOOM_VALUE, 0);
        updateZoomView();
        //16:9 16:10 4:3 画面缩放
        updateSzoomTv();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(vaFocusReceiver);
    }

    private void initView() {
        projectBinding.rlDisplaySettings.setOnClickListener(this);
        projectBinding.rlDisplaySettings.setOnHoverListener(this);
        projectBinding.rlColorMode.setOnClickListener(this);
        projectBinding.rlColorMode.setOnHoverListener(this);
        projectBinding.rlAudioMode.setOnClickListener(this);
        projectBinding.rlAudioMode.setOnHoverListener(this);
        projectBinding.rlProjectMode.setOnClickListener(this);
        projectBinding.rlProjectMode.setOnHoverListener(this);
        projectBinding.rlAutoKeystone.setOnClickListener(this);
        projectBinding.rlAutoKeystone.setOnHoverListener(this);
        projectBinding.rlInitAngle.setOnClickListener(this);
        projectBinding.rlInitAngle.setOnHoverListener(this);
        projectBinding.autoKeystoneSwitch.setOnClickListener(this);

        projectBinding.rlProjectMode.setOnKeyListener(this);
        projectBinding.rlDeviceMode2.setOnKeyListener(this);
        projectBinding.rlDeviceMode2.setOnClickListener(this);
        projectBinding.rlDeviceMode2.setOnHoverListener(this);
        updateText(ReflectUtil.invokeGet_brightness_level()); //初始化设备模式的Text显示
        projectBinding.rlDigitalZoom.setOnKeyListener(this);
        projectBinding.rlDigitalZoom.setOnHoverListener(this);
        projectBinding.rlDigitalZoom.setOnClickListener(this);
        projectBinding.rlScreenZoom.setOnKeyListener(this);
        projectBinding.rlScreenZoom.setOnHoverListener(this);
        projectBinding.rlScreenZoom.setOnClickListener(this);
        projectBinding.rlHorizontalCorrect.setOnKeyListener(this);
        projectBinding.rlHorizontalCorrect.setOnHoverListener(this);
        projectBinding.rlVerticalCorrect.setOnKeyListener(this);
        projectBinding.rlVerticalCorrect.setOnHoverListener(this);

        projectBinding.rlManualKeystone.setOnClickListener(this);
        projectBinding.rlManualKeystone.setOnHoverListener(this);
        projectBinding.rlResetKeystone.setOnClickListener(this);
        projectBinding.rlResetKeystone.setOnHoverListener(this);

        projectBinding.rlAutoFocus.setOnClickListener(this);
        projectBinding.rlAutoFocus.setOnHoverListener(this);
        projectBinding.autoFocusSwitch.setOnClickListener(this);
        projectBinding.rlAutoFourCorner.setOnClickListener(this);
        projectBinding.rlAutoFourCorner.setOnHoverListener(this);
        projectBinding.rlAutoFourCorner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    try {
                        // 当获取焦点时，延迟2秒弹出Toast
                        int calibratedTips = R.string.no_caalibrated;
                        int i = 0;
                        i = Constants.CheckCalibrated(AwTvSystemManager.getInstance(getApplicationContext()).getSecureStorageKey("vafocusCam").trim());
                        if (i != 1 && i != 3) {
                            i = checkNewBDDATA();
                        }
                        calibratedTips = getStringId(i);
                        int finalCalibratedTips = calibratedTips;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(v.getContext(), getString(R.string.auto_four_corner_hint) + ";" + getString(finalCalibratedTips), Toast.LENGTH_LONG).show();
                            }
                        }, 2000); // 2000毫秒 = 2秒
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // 失去焦点时，取消延迟任务
                    handler.removeCallbacksAndMessages(null);
                }
            }
        });
        projectBinding.autoFourCornerSwitch.setOnClickListener(this);
        projectBinding.rlScreenRecognition.setOnClickListener(this);
        projectBinding.rlScreenRecognition.setOnHoverListener(this);
        projectBinding.screenRecognitionSwitch.setOnClickListener(this);
        projectBinding.rlIntelligentObstacle.setOnClickListener(this);
        projectBinding.rlIntelligentObstacle.setOnHoverListener(this);
        projectBinding.intelligentObstacleSwitch.setOnClickListener(this);
        projectBinding.rlCalibration.setOnClickListener(this);
        projectBinding.rlCalibration.setOnHoverListener(this);

        projectBinding.digitalZoomLeft.setOnClickListener(this);
        projectBinding.digitalZoomRight.setOnClickListener(this);
        projectBinding.screenZoomLeft.setOnClickListener(this);
        projectBinding.screenZoomRight.setOnClickListener(this);

        projectBinding.rlDisplaySettings.setVisibility(MyApplication.config.displaySetting ? View.VISIBLE : View.GONE);
        projectBinding.rlColorMode.setVisibility(MyApplication.config.brightAndColor ? View.VISIBLE : View.GONE);
        projectBinding.rlAudioMode.setVisibility(MyApplication.config.AudioMode ? View.VISIBLE : View.GONE);
        projectBinding.rlProjectMode.setVisibility(MyApplication.config.projectMode ? View.VISIBLE : View.GONE);
        projectBinding.rlDeviceMode2.setVisibility(MyApplication.config.deviceMode ? View.VISIBLE : View.GONE);
        projectBinding.rlDigitalZoom.setVisibility(MyApplication.config.wholeZoom ? View.VISIBLE : View.GONE);
        projectBinding.rlScreenZoom.setVisibility(MyApplication.config.screenZoom ? View.VISIBLE : View.GONE);
        projectBinding.rlAutoKeystone.setVisibility(MyApplication.config.autoKeystone ? View.VISIBLE : View.GONE);
        projectBinding.rlInitAngle.setVisibility(MyApplication.config.initAngleCorrect ? View.VISIBLE : View.GONE);
        projectBinding.rlManualKeystone.setVisibility(MyApplication.config.manualKeystone ? View.VISIBLE : View.GONE);
        projectBinding.rlResetKeystone.setVisibility(MyApplication.config.resetKeystone ? View.VISIBLE : View.GONE);
        projectBinding.rlAutoFocus.setVisibility(MyApplication.config.autoFocus ? View.VISIBLE : View.GONE);
        projectBinding.rlAutoFourCorner.setVisibility(MyApplication.config.autoFourCorner ? View.VISIBLE : View.GONE);
        projectBinding.rlScreenRecognition.setVisibility(MyApplication.config.screenRecognition ? View.VISIBLE : View.GONE);
        projectBinding.rlIntelligentObstacle.setVisibility(MyApplication.config.intelligentObstacle ? View.VISIBLE : View.GONE);

        if (SystemProperties.get("persist.sys.camok", "0").equals("1")) {
            projectBinding.rlAutoFocus.setVisibility(View.VISIBLE);
            projectBinding.rlIntelligentObstacle.setVisibility(View.VISIBLE);
            projectBinding.rlScreenRecognition.setVisibility(View.VISIBLE);
            projectBinding.rlAutoFourCorner.setVisibility(View.VISIBLE);
            projectBinding.rlInitAngle.setVisibility(View.GONE);
            projectBinding.rlAutoKeystone.setVisibility(View.GONE);

            if (SystemProperties.get("persist.sys.focusupdn", "0").equals("1")) {
                //自动梯形
                projectBinding.rlAutoKeystone.setVisibility(View.VISIBLE);
                projectBinding.rlInitAngle.setVisibility(MyApplication.config.initAngleCorrect ? View.VISIBLE : View.GONE);
                projectBinding.rlAutoFourCorner.setVisibility(View.GONE);
                projectBinding.rlIntelligentObstacle.setVisibility(View.GONE);
                projectBinding.rlScreenRecognition.setVisibility(View.GONE);
            }

        } else {
            projectBinding.rlAutoKeystone.setVisibility(View.VISIBLE);
            projectBinding.rlInitAngle.setVisibility(MyApplication.config.initAngleCorrect ? View.VISIBLE : View.GONE);
            projectBinding.rlAutoFourCorner.setVisibility(View.GONE);
            projectBinding.rlIntelligentObstacle.setVisibility(View.GONE);
            projectBinding.rlScreenRecognition.setVisibility(View.GONE);
            projectBinding.rlAutoFocus.setVisibility(View.GONE);

        }

        if ((boolean) ShareUtil.get(this, Contants.KEY_DEVELOPER_MODE, false)
                && projectBinding.rlAutoFourCorner.getVisibility() == View.VISIBLE) {
            projectBinding.rlCalibration.setVisibility(View.VISIBLE);
        }

        requestFirstItemFocus();
    }

    private void requestFirstItemFocus() {
        if (MyApplication.config.brightAndColor) {
            projectBinding.rlColorMode.requestFocus();
            projectBinding.rlColorMode.requestFocusFromTouch();
        } else if (MyApplication.config.AudioMode) {
            projectBinding.rlAudioMode.requestFocus();
            projectBinding.rlAudioMode.requestFocusFromTouch();
        } else {
            projectBinding.rlProjectMode.requestFocus();
            projectBinding.rlProjectMode.requestFocusFromTouch();
        }
    }

    private void initData() {
        screen_zoom = getResources().getStringArray(R.array.screen_zoom);
        tvDisplayManager = AwTvDisplayManager.getInstance();
        project_name.add(getString(R.string.project_mode_1));
        project_name.add(getString(R.string.project_mode_2));
        project_name.add(getString(R.string.project_mode_3));
        project_name.add(getString(R.string.project_mode_4));
        cur_project_mode = tvDisplayManager.factoryGetPanelValue(AwTvDisplayTypes.EnumPanelConfigType.E_AW_PANEL_CONFIG_MIRROR);
        if (cur_project_mode < 0 || cur_project_mode > 3) {
            cur_project_mode = 0;
        }
        projectBinding.projectModeTv.setText(project_name.get(cur_project_mode));
        singer = Executors.newSingleThreadExecutor();
        sharedPreferences = ShareUtil.getInstans(this);
        String zoom_mode = sharedPreferences.getString("zoom_mode", "16:9");
        assert zoom_mode != null;
        switch (zoom_mode) {
            case "16:9":

                scale = 1D;
                step_x = 16;
                step_y = 9;
                break;
            case "4:3":

                scale = 0.875D;
                step_x = 12;
                step_y = 9;
                break;
            case "16:10":

                scale = 0.95D;
                step_x = 16;
                step_y = 10;
                break;
        }

        All = KeystoneUtils_726.readGlobalSettings(this, KeystoneUtils_726.ZOOM_VALUE, 0);
        updateZoomView();
        initAuto();
        initBstacle();
        initMbRecognize();
        initAutoFourCorner();
        projectBinding.autoFocusSwitch.setChecked(get_auto_focus());

        //16:9 16:10 4:3 画面缩放
//        updateSzoomTv();
    }

    private void updateSzoomTv() {
        Log.d(TAG,"updateSzoomTv zoom_scale"+zoom_scale);
        zoom_scale = KeystoneUtils_726.readSystemProperties(KeystoneUtils_726.PROP_ZOOM_SCALE,zoom_scale);
        Log.d(TAG,"updateSzoomTv zoom_scale"+zoom_scale);
        switch (zoom_scale) {
            case 0:
                projectBinding.screenZoomeTv.setText(screen_zoom[0]);
                break;
            case 1:
                projectBinding.screenZoomeTv.setText(screen_zoom[1]);
                break;
            case 2:
                projectBinding.screenZoomeTv.setText(screen_zoom[2]);
                break;
        }
    }

    private void initAuto() {
        boolean auto = getAuto();
        if (!auto) {
            projectBinding.autoKeystoneSwitch.setChecked(false);
        } else {
            projectBinding.autoKeystoneSwitch.setChecked(true);
        }
    }

    public void setAuto() {
        //int auto = PrjScreen.get_prj_auto_keystone_enable();
        //PrjScreen.set_prj_auto_keystone_enable(auto == 0 ? 1 : 0);
        boolean auto = getAuto();
        SystemProperties.set("persist.sys.tpryauto", String.valueOf(auto ? 0 : 1));
        //自动梯形打开的时候发送一次更新
        if (!auto) {
//            sendKeystoneBroadcast();
            sendKeystoneBroadcastByAuto();
        } else {
            updateZoomValue();
        }
        initAuto();
    }

    private void sendKeystoneBroadcastByAuto() {
        Intent intent = new Intent("android.intent.hotack_keystone");
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.putExtra("ratio", 1);
        intent.putExtra("keystone",1);
        sendBroadcast(intent);
    }

    private void updateZoomValue() {
        String value = SystemProperties.get("persist.sys.zoom.value", "0,0,0,0,0,0,0,0");
        String[] va = value.split(",");
        if (va.length == 8) {
            KeystoneUtils_726.lb_X = Integer.parseInt(va[0]);
            KeystoneUtils_726.lb_Y = Integer.parseInt(va[1]);
            KeystoneUtils_726.lt_X = Integer.parseInt(va[2]);
            KeystoneUtils_726.lt_Y = Integer.parseInt(va[3]);
            KeystoneUtils_726.rt_X = Integer.parseInt(va[4]);
            KeystoneUtils_726.rt_Y = Integer.parseInt(va[5]);
            KeystoneUtils_726.rb_X = Integer.parseInt(va[6]);
            KeystoneUtils_726.rb_Y = Integer.parseInt(va[7]);
            KeystoneUtils_726.UpdateKeystoneZOOM(true);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_color_mode) {
            startNewActivity(PictureModeActivity.class);
//            startNewActivity(DisplaySettingsActivity.class);
        } else if (id == R.id.rl_audio_mode) {
            startNewActivity(AudioModeActivity.class);
        } else if (id == R.id.rl_power_mode) {
            old_project_mode = cur_project_mode;
            if (cur_project_mode == project_name.size() - 1)
                cur_project_mode = 0;
            else
                cur_project_mode++;

            updateProjectMode();
        } else if (id == R.id.rl_manual_keystone) {
            if (getAuto() && projectBinding.rlAutoKeystone.getVisibility() == View.VISIBLE) {
                ToastUtil.showShortToast(this, getString(R.string.auto_keystone_on));
                return;
            } else if (get_AutoFourCorner() && projectBinding.rlAutoFourCorner.getVisibility() == View.VISIBLE) {
                ToastUtil.showShortToast(this, getString(R.string.auto_four_corner_on));
                return;
            }
            startNewActivity(CorrectionActivity.class);
        } else if (id == R.id.rl_reset_keystone) {
            if (getAuto() && projectBinding.rlAutoKeystone.getVisibility() == View.VISIBLE) {
                ToastUtil.showShortToast(this, getString(R.string.auto_keystone_on));
                return;
            } else if (get_AutoFourCorner() && projectBinding.rlAutoFourCorner.getVisibility() == View.VISIBLE) {
                ToastUtil.showShortToast(this, getString(R.string.auto_four_corner_on));
                return;
            }
            ShowResetKeystoreDialog();
        } else if (id == R.id.rl_auto_keystone || id == R.id.auto_keystone_switch) {
            setAuto();
        } else if (id == R.id.rl_auto_focus || id == R.id.auto_focus_switch) {
            set_auto_focus(!get_auto_focus());
            projectBinding.autoFocusSwitch.setChecked(get_auto_focus());
        } else if (id == R.id.rl_auto_four_corner || id == R.id.auto_four_corner_switch) {
            setAutoFourCorner();
        } else if (id == R.id.rl_screen_recognition || id == R.id.screen_recognition_switch) {
            setMbRecognize();
        } else if (id == R.id.rl_intelligent_obstacle || id == R.id.intelligent_obstacle_switch) {
            setBstacle();
        } else if (id == R.id.rl_calibration) {
            AppUtils.startNewApp(this, "com.hysd.vafocus", "com.hysd.vafocus.VajzActivity");
        } else if (id == R.id.rl_init_angle) {//                AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
//                builder.setTitle(getString(R.string.hint));
//                builder.setMessage(getString(R.string.defaultcorrectionhint));
//                builder.setPositiveButton(getString(R.string.enter), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        initCorrectAngle();
//                    }
//                });
//                builder.setNegativeButton(getString(R.string.cancel), null);
//                builder.show();

//            InitAngleDialog initAngleDialog = new InitAngleDialog(this, R.style.DialogTheme);
//            initAngleDialog.show();
            startNewActivity(InitAngleActivity.class);
        } else if (id == R.id.rl_project_mode) {
            old_project_mode = cur_project_mode;
            Log.d(TAG, "onClick向右切换安装模式");
            if (cur_project_mode == project_name.size() - 1)
                cur_project_mode = 0;
            else
                cur_project_mode++;
            updateProjectMode();
        } else if (id == R.id.rl_device_mode2) {
            Log.d(TAG, "onClick向右切换设备模式");
            cur_device_Mode++;
            if (cur_device_Mode > 2) {
                cur_device_Mode = 0;
            }
            updateText(cur_device_Mode);
            ReflectUtil.invokeSet_brightness_level(cur_device_Mode);
        } else if (id == R.id.rl_digital_zoom) {
            if (All >= ZOOM_MAX)
                return;
            All++;
            set_screen_zoom(All, All, All, All);
            updateZoomView();
        } else if (id == R.id.digital_zoom_left) {
            if (All <= 0)
                return;
            All--;
            set_screen_zoom(All, All, All, All);
            updateZoomView();
        } else if (id == R.id.digital_zoom_right) {
            if (All >= ZOOM_MAX)
                return;
            All++;
            set_screen_zoom(All, All, All, All);
            updateZoomView();
        } else if (id == R.id.rl_screen_zoom) {
            zoom_scale++;
            if (zoom_scale > 2)
                zoom_scale = 0;
            if (!SystemProperties.get("persist.sys.camok", "0").equals("1") || SystemProperties.getBoolean("persist.sys.tpryauto", false))
                set_screen_zoom(All, All, All, All, zoom_scale);
            else
                updateScaleZoom(zoom_scale);
            Log.d(TAG, " writeSystemProperties KeystoneUtils_726.PROP_ZOOM_SCALE ");
            KeystoneUtils_726.writeSystemProperties(KeystoneUtils_726.PROP_ZOOM_SCALE,zoom_scale);
            updateSzoomTv();
        } else if (id == R.id.screen_zoom_left) {
            zoom_scale--;
            if (zoom_scale < 0)
                zoom_scale = 2;
            if (!SystemProperties.get("persist.sys.camok", "0").equals("1") || SystemProperties.getBoolean("persist.sys.tpryauto", false))
                set_screen_zoom(All, All, All, All, zoom_scale);
            else
                updateScaleZoom(zoom_scale);
            KeystoneUtils_726.writeGlobalSettings(this, KeystoneUtils_726.ZOOM_SCALE, zoom_scale);
            updateSzoomTv();
        } else if (id == R.id.screen_zoom_right) {
            zoom_scale++;
            if (zoom_scale > 2)
                zoom_scale = 0;
            if (!SystemProperties.get("persist.sys.camok", "0").equals("1") || SystemProperties.getBoolean("persist.sys.tpryauto", false))
                set_screen_zoom(All, All, All, All, zoom_scale);
            else
                updateScaleZoom(zoom_scale);
            KeystoneUtils_726.writeGlobalSettings(this, KeystoneUtils_726.ZOOM_SCALE, zoom_scale);
            updateSzoomTv();
        }
    }

    private ProgressDialog dialog = null;

    private void initCorrectAngle() {
        ReflectUtil.invokeSet_angle_offset();
        dialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage(getString(R.string.defaultcorrectionin));
        dialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                LogUtils.d("get_angle_offset " + ReflectUtil.invokeGet_angle_offset());
            }
        }, 3000);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if ((event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT)
                && (System.currentTimeMillis() - cur_time < 150)) {
            return true;
        }

        if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            int id = v.getId();
            if (id == R.id.rl_project_mode) {
                Log.d(TAG, "向左切换安装模式");
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                old_project_mode = cur_project_mode;
                if (cur_project_mode == 0)
                    cur_project_mode = project_name.size() - 1;
                else
                    cur_project_mode--;

                updateProjectMode();
//                    break;
                return true;
            } else if (id == R.id.rl_digital_zoom) {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;

                if (All <= 0)
                    return false;

                All--;
                set_screen_zoom(All, All, All, All);
                updateZoomView();
//                    break;
                return true;
            } else if (id == R.id.rl_screen_zoom) {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                zoom_scale--;
                if (zoom_scale < 0)
                    zoom_scale = 2;
                if (!SystemProperties.get("persist.sys.camok", "0").equals("1") || SystemProperties.getBoolean("persist.sys.tpryauto", false))
                    set_screen_zoom(All, All, All, All, zoom_scale);
                else
                    updateScaleZoom(zoom_scale);
                Log.d(TAG, " KEYCODE_DPAD_LEFT writeSystemProperties KeystoneUtils_726.PROP_ZOOM_SCALE ");
                KeystoneUtils_726.writeSystemProperties(KeystoneUtils_726.PROP_ZOOM_SCALE,zoom_scale);
                updateSzoomTv();
//                    break;
                return true;
            } else if (id == R.id.rl_horizontal_correct) {
            } else if (id == R.id.rl_vertical_correct) {
            } else if (id == R.id.rl_device_mode2) {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                Log.d(TAG, "向左切换设备模式");
                cur_device_Mode--;
                if (cur_device_Mode < 0) {
                    cur_device_Mode = 2;
                }
                updateText(cur_device_Mode);
                ReflectUtil.invokeSet_brightness_level(cur_device_Mode);
//                    break;
                return true;
            }

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            int id = v.getId();
            if (id == R.id.rl_project_mode) {
                Log.d(TAG, "向右切换安装模式");
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                old_project_mode = cur_project_mode;
                if (cur_project_mode == project_name.size() - 1)
                    cur_project_mode = 0;
                else
                    cur_project_mode++;

                updateProjectMode();
//                    break;
                return true;
            } else if (id == R.id.rl_digital_zoom) {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;

                if (All >= ZOOM_MAX)
                    return false;

                All++;
                set_screen_zoom(All, All, All, All);
                updateZoomView();
//                    break;
                return true;
            } else if (id == R.id.rl_screen_zoom) {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                zoom_scale++;
                if (zoom_scale > 2)
                    zoom_scale = 0;
                if (!SystemProperties.get("persist.sys.camok", "0").equals("1") || SystemProperties.getBoolean("persist.sys.tpryauto", false))
                    set_screen_zoom(All, All, All, All, zoom_scale);
                else
                    updateScaleZoom(zoom_scale);
                Log.d(TAG, " KEYCODE_DPAD_RIGHT writeSystemProperties KeystoneUtils_726.PROP_ZOOM_SCALE ");
                KeystoneUtils_726.writeSystemProperties(KeystoneUtils_726.PROP_ZOOM_SCALE,zoom_scale);
                updateSzoomTv();
//                    break;
                return true;
            } else if (id == R.id.rl_horizontal_correct) {
            } else if (id == R.id.rl_vertical_correct) {
            } else if (id == R.id.rl_device_mode2) {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                Log.d(TAG, "向右切换设备模式");
                cur_device_Mode++;
                if (cur_device_Mode > 2) {
                    cur_device_Mode = 0;
                }
                updateText(cur_device_Mode);
                ReflectUtil.invokeSet_brightness_level(cur_device_Mode);
//                    break;
                return true;
            }
        }

        return false;
    }

    private void updateText(int mode) {
        switch (mode) {
            case 0:
                projectBinding.deviceModeTv.setText(getString(R.string.device_mode0));
                break;
            case 1:
                projectBinding.deviceModeTv.setText(getString(R.string.device_mode1));
                break;
            case 2:
                projectBinding.deviceModeTv.setText(getString(R.string.device_mode2));
                break;
        }

    }

    private void updateProjectMode() {
//        tvDisplayManager.factorySetPanelValue(AwTvDisplayTypes.EnumPanelConfigType.E_AW_PANEL_CONFIG_MIRROR, cur_project_mode);
        projectBinding.projectModeTv.setText(project_name.get(cur_project_mode));
//        SystemProperties.set("persist.sys.panelvalue", String.valueOf(cur_project_mode));
//        if (getAuto())
//            sendProjectBroadCast();
        SystemProperties.set("persist.sys.panelvalue", String.valueOf(cur_project_mode));
//        if (SystemProperties.get("persist.sys.camok", "0").equals("1")
//                && SystemProperties.get("persist.sys.focusupdn", "0").equals("0"))
        KeystoneUtils_726.setKeystoneNormalXY(old_project_mode, cur_project_mode);
        if (SystemProperties.getBoolean("persist.sys.tpryauto", false))
            sendProjectBroadCast();
        tvDisplayManager.factorySetPanelValue(AwTvDisplayTypes.EnumPanelConfigType.E_AW_PANEL_CONFIG_MIRROR, cur_project_mode);

//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            Log.d(TAG," 延时5s执行tvDisplayManager.factorySetPanelValue");
//            tvDisplayManager.factorySetPanelValue(AwTvDisplayTypes.EnumPanelConfigType.E_AW_PANEL_CONFIG_MIRROR, cur_project_mode);
//        }, 5000); // 延迟 5 秒（5000 毫秒）
    }

    private void sendProjectBroadCast() {
        Intent intent = new Intent("android.intent.projective_mode");
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        sendBroadcast(intent);
    }

    public void get_screen_zoom() {
        String zoomV = SystemProperties.get("persist.vendor.overscan.main", "overscan 100,100,100,100");
        LogUtils.i("PrjScreen", "get_screen_zoom zoomV=" + zoomV);
        if (!zoomV.equals("")) {
            String[] arraysZoom = zoomV.substring(9).split(",");
            if (arraysZoom.length == 4) {
                left = (int) Double.parseDouble(arraysZoom[0]);
                top = (int) Double.parseDouble(arraysZoom[1]);
                right = (int) Double.parseDouble(arraysZoom[2]);
                bottom = (int) Double.parseDouble(arraysZoom[3]);
            }
        }
    }

    public void updateZoomView() {
        projectBinding.digitalZoomTv.setText(String.valueOf(All));
        if (All <= 0) {
            projectBinding.digitalZoomLeft.setVisibility(View.GONE);
            projectBinding.digitalZoomRight.setVisibility(View.VISIBLE);
        } else if (All >= ZOOM_MAX) {
            projectBinding.digitalZoomLeft.setVisibility(View.VISIBLE);
            projectBinding.digitalZoomRight.setVisibility(View.GONE);
        } else {
            projectBinding.digitalZoomRight.setVisibility(View.VISIBLE);
            projectBinding.digitalZoomLeft.setVisibility(View.VISIBLE);
        }

    }


    public void set_screen_zoom(int l, int t, int r, int b) {
        KeystoneUtils_726.writeGlobalSettings(this, KeystoneUtils_726.ZOOM_VALUE, l);

//        if (zoom_scale == 0) {
//            scale = 1D;
//            step_x = 16;
//            step_y = 9;
//        } else if (zoom_scale == 2) {
//            scale = 0.875D;
//            step_x = 12;
//            step_y = 9;
//        } else if (zoom_scale == 1) {
//            scale = 0.95D;
//            step_x = 16;
//            step_y = 10;
//        }

        l = max_value - l;
        t = max_value - t;
        r = max_value - r;
        b = max_value - b;
//        changeform(l, t, r, b);

        if (!SystemProperties.get("persist.sys.camok", "0").equals("1") || getAuto()) {
            changeform(l, t, r, b);
        } else updateZoom(max_value - l);
    }

    public void changeform(int l, int t, int right, int bottom) {
        Log.d(TAG," changeform ");
        KeystoneUtils_726.lt_X = Integer.parseInt(df.format(((100 - 100 * scale) * zoom_step_x + (100 - l) * step_x) * 1000 / KeystoneUtils_726.lcd_w));
        KeystoneUtils_726.lt_Y = 1000 - Integer.parseInt(df.format((KeystoneUtils_726.lcd_h - (100 - t) * step_y) * 1000 / KeystoneUtils_726.lcd_h));

        KeystoneUtils_726.lb_X = Integer.parseInt(df.format(((100 - 100 * scale) * zoom_step_x + (100 - l) * step_x) * 1000 / KeystoneUtils_726.lcd_w));
        KeystoneUtils_726.lb_Y = Integer.parseInt(df.format(((100 - bottom) * step_y) * 1000 / KeystoneUtils_726.lcd_h));

        KeystoneUtils_726.rt_X = 1000 - Integer.parseInt(df.format((KeystoneUtils_726.lcd_w * scale - (100 - right) * step_x) * 1000 / KeystoneUtils_726.lcd_w));
        KeystoneUtils_726.rt_Y = 1000 - Integer.parseInt(df.format((KeystoneUtils_726.lcd_h - (100 - t) * step_y) * 1000 / KeystoneUtils_726.lcd_h));

        KeystoneUtils_726.rb_X = 1000 - Integer.parseInt(df.format((KeystoneUtils_726.lcd_w * scale - (100 - right) * step_x) * 1000 / KeystoneUtils_726.lcd_w));
        KeystoneUtils_726.rb_Y = Integer.parseInt(df.format(((100 - bottom) * step_y) * 1000 / KeystoneUtils_726.lcd_h));

        if (getAuto()) {
            Log.d(TAG," UpdateKeystoneZOOM(false) ");
            KeystoneUtils_726.UpdateKeystoneZOOM(false);
            sendKeystoneBroadcast();
        } else {
            Log.d(TAG," UpdateKeystoneZOOM(true) ");
            KeystoneUtils_726.UpdateKeystoneZOOM(true);
        }
    }

    public void updateZoom(int zoom) {
        lt_xy = KeystoneUtils_726.getKeystoneHtcLeftAndTopXY();
        rt_xy = KeystoneUtils_726.getKeystoneHtcRightAndTopXY();
        lb_xy = KeystoneUtils_726.getKeystoneHtcLeftAndBottomXY();
        rb_xy = KeystoneUtils_726.getKeystoneHtcRightAndBottomXY();
        int[] px4 = new int[4];
        int[] py4 = new int[4];
        px4[0] = Integer.parseInt(df.format((lt_xy[0] * KeystoneUtils_726.lcd_w) / 1000));
        py4[0] = Integer.parseInt(df.format(((1000 - lt_xy[1]) * KeystoneUtils_726.lcd_h) / 1000));
        px4[1] = Integer.parseInt(df.format(((1000 - rt_xy[0]) * KeystoneUtils_726.lcd_w) / 1000));
        py4[1] = Integer.parseInt(df.format(((1000 - rt_xy[1]) * KeystoneUtils_726.lcd_h) / 1000));
        px4[2] = Integer.parseInt(df.format((lb_xy[0] * KeystoneUtils_726.lcd_w) / 1000));
        py4[2] = Integer.parseInt(df.format((lb_xy[1] * KeystoneUtils_726.lcd_h) / 1000));
        px4[3] = Integer.parseInt(df.format(((1000 - rb_xy[0]) * KeystoneUtils_726.lcd_w) / 1000));
        py4[3] = Integer.parseInt(df.format((rb_xy[1] * KeystoneUtils_726.lcd_h) / 1000));
        DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.CHINA));
        float a = Float.parseFloat(df.format((max_value - zoom * 2) * 0.01).replace(",", "."));
        Log.d(TAG, "float  a =" + a);
        int old_ratio = KeystoneUtils_726.readGlobalSettings(this, "zoom_scale_old", 0);
        int ratio = KeystoneUtils_726.readGlobalSettings(this, "zoom_scale", 0);
        int[] tpData = scUtils.getpxRatioxy(px4, py4, old_ratio, ratio, a, KeystoneUtils_726.lcd_w, KeystoneUtils_726.lcd_h);
        if (tpData != null && tpData[8] == 1) {
            KeystoneUtils_726.optKeystoneFun(tpData);
        }
    }

    private void sendKeystoneBroadcast() {
        Log.d(TAG," 发送自动梯形校正的广播 ");
        Intent intent = new Intent("android.intent.hotack_keystone");
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.putExtra("ratio", 1);
        sendBroadcast(intent);
    }

    public boolean getAuto() {
        return SystemProperties.getBoolean("persist.sys.tpryauto", false);
    }

    public boolean get_auto_focus() {
        return SystemProperties.getBoolean("persist.sys.vafocus", false);
    }

    public void set_auto_focus(boolean b) {
        if (b) {
            SystemProperties.set("persist.sys.vafocus", "1");
//            SystemProperties.set("hotack.sensor.anti_shake", "1");
        } else {
            SystemProperties.set("persist.sys.vafocus", "0");
        }
    }

    private void initAutoFourCorner() {
        boolean auto = get_AutoFourCorner();
        if (!auto) {
            // 手动
            projectBinding.autoFourCornerSwitch.setChecked(false);
        } else {
            // 自动
            projectBinding.autoFourCornerSwitch.setChecked(true);
        }
    }


    private void setAutoFourCorner() {
        boolean auto = get_AutoFourCorner();
        set_AutoFourCorner(!auto);
        initAutoFourCorner();
    }

    public void set_AutoFourCorner(boolean b) {
        if (b) {
            SystemProperties.set("persist.sys.tpryxcrt", "1");
//            SystemProperties.set("hotack.sensor.anti_shake", "1");
        } else {
            SystemProperties.set("persist.sys.tpryxcrt", "0");
        }
    }

    public boolean get_AutoFourCorner() {
        return SystemProperties.getBoolean("persist.sys.tpryxcrt", false);
    }

    private void initMbRecognize() {
        boolean auto = get_MbRecognize();
        projectBinding.screenRecognitionSwitch.setChecked(auto);
    }

    //智能避障状态更新
    private void initBstacle() {
        boolean auto = get_Bstacle();
        projectBinding.intelligentObstacleSwitch.setChecked(auto);
    }

    private void setMbRecognize() {
        boolean auto = get_MbRecognize();
        set_MbRecognize(!auto);
        initMbRecognize();
    }

    private void setBstacle() {
        boolean auto = get_Bstacle();
        set_Bstacle(!auto);
        initBstacle();
    }

    public void set_MbRecognize(boolean b) {
        if (b) {
            SystemProperties.set("persist.sys.mbrecognize", "1");
        } else {

            SystemProperties.set("persist.sys.mbrecognize", "0");
        }
    }

    public void set_Bstacle(boolean b) {
        if (b) {
            SystemProperties.set("persist.sys.obstacle", "1");
        } else {

            SystemProperties.set("persist.sys.obstacle", "0");
        }
    }

    public boolean get_MbRecognize() {
        return SystemProperties.getBoolean("persist.sys.mbrecognize", false);
    }

    public boolean get_Bstacle() {
        return SystemProperties.getBoolean("persist.sys.obstacle", false);
    }

    private void ShowResetKeystoreDialog() {
        ResetKeystoreLayoutBinding resetKeystoreLayoutBinding = ResetKeystoreLayoutBinding.inflate(LayoutInflater.from(this));
        Dialog dialoge = new Dialog(this, R.style.DialogTheme);
        dialoge.setContentView(resetKeystoreLayoutBinding.getRoot());
        /*builder.setMessage(getString(R.string.reset_keystore));
        builder.setPositiveButton(R.string.enter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeystoneUtils.resetKeystone();
                All =0;
                updateZoomView();
                dialog.dismiss();
            }
        });*/
        Window window = dialoge.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.right_in_right_out_anim);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置dialog在界面中的属性
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            //背景全透明
            window.setDimAmount(0f);
        }
        Display d = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams params = window.getAttributes(); // 获取对话框当前的参数值
        params.width = (int) (d.getWidth() * 0.4); // 宽度设置为屏幕的0.8，根据实际情况调整
        params.height = (int) (d.getHeight() * 0.4);
        //params.x = parent.getWidth();
        window.setGravity(Gravity.CENTER);// 设置对话框位置
        window.setAttributes(params);
        window.setAttributes(params);
        resetKeystoreLayoutBinding.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeystoneUtils_726.resetKeystone();

                //数字缩放复位
                KeystoneUtils_726.writeGlobalSettings(getApplicationContext(), KeystoneUtils_726.ZOOM_VALUE, 0);
                All = 0;
                updateZoomView();

                //画面比例复位
                KeystoneUtils_726.writeSystemProperties(KeystoneUtils_726.PROP_ZOOM_SCALE,0);
                updateSzoomTv();
                dialoge.dismiss();
            }
        });
        resetKeystoreLayoutBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoge.dismiss();
            }
        });
        dialoge.show();
    }

    //新的标定数据校验
    private int checkNewBDDATA() {
        int ret1, ret2, ret3, ret4;
        ret1 = scUtils.checkbddata(AwTvSystemManager.getInstance(getApplicationContext()).getSecureStorageKey("PoCamX"));
        ret2 = scUtils.checkbddata(AwTvSystemManager.getInstance(getApplicationContext()).getSecureStorageKey("PoCamY"));
        ret3 = scUtils.checkbddata(AwTvSystemManager.getInstance(getApplicationContext()).getSecureStorageKey("JbCamX"));
        ret4 = scUtils.checkbddata(AwTvSystemManager.getInstance(getApplicationContext()).getSecureStorageKey("JbCamY"));
        if (ret1 == 1 && ret2 == 1 && ret3 == 1 && ret4 == 1)
            return 1;
        else if (ret1 == 0 && ret2 == 0 && ret3 == 0 && ret4 == 0)
            return 0;

        return -1;
    }

    private int getStringId(int i) {
        switch (i) {
            case 1:
                return R.string.calibrated;
            case 2:
            case -1:
                return R.string.calibrated_data_fail;
            case 3:
                return R.string.calibrated_data_normal;
            case 0:
            default:
                return R.string.no_caalibrated;
        }
    }

    @Override
    public void vaFocusChange() {
        Log.d(TAG, " vaFocusChange " + All);
        All = 0;
        projectBinding.digitalZoomTv.setText("0");
        projectBinding.digitalZoomRight.setVisibility(View.VISIBLE);
        projectBinding.digitalZoomLeft.setVisibility(View.GONE);
    }

    //全局缩放和比例兼容
    public void set_screen_zoom(int l, int t, int r, int b, int scaleMode) {
        if (scaleMode == 0) {
            scale = 1D;
            step_x = 16;
            step_y = 9;
        } else if (scaleMode == 2) {
            scale = 0.875D;
            step_x = 12;
            step_y = 9;
        } else if (scaleMode == 1) {
            scale = 0.95D;
            step_x = 16;
            step_y = 10;
        }

        l = 100 - l;
        t = 100 - t;
        r = 100 - r;
        b = 100 - b;
        changeform(l, t, r, b);
        String modify_value = "overscan " + df.format((double) l * scale) + "," + t + "," + df.format((double) r * scale) + "," + b;
        SystemProperties.set("persist.vendor.overscan.main", modify_value);
    }

    public void updateScaleZoom(int scale) {
        lt_xy = KeystoneUtils_726.getKeystoneHtcLeftAndTopXY();
        rt_xy = KeystoneUtils_726.getKeystoneHtcRightAndTopXY();
        lb_xy = KeystoneUtils_726.getKeystoneHtcLeftAndBottomXY();
        rb_xy = KeystoneUtils_726.getKeystoneHtcRightAndBottomXY();
        int[] px4 = new int[4];
        int[] py4 = new int[4];
        px4[0] = Integer.parseInt(df.format((lt_xy[0] * KeystoneUtils_726.lcd_w) / 1000));
        py4[0] = Integer.parseInt(df.format(((1000 - lt_xy[1]) * KeystoneUtils_726.lcd_h) / 1000));
        px4[1] = Integer.parseInt(df.format(((1000 - rt_xy[0]) * KeystoneUtils_726.lcd_w) / 1000));
        py4[1] = Integer.parseInt(df.format(((1000 - rt_xy[1]) * KeystoneUtils_726.lcd_h) / 1000));
        px4[2] = Integer.parseInt(df.format((lb_xy[0] * KeystoneUtils_726.lcd_w) / 1000));
        py4[2] = Integer.parseInt(df.format((lb_xy[1] * KeystoneUtils_726.lcd_h) / 1000));
        px4[3] = Integer.parseInt(df.format(((1000 - rb_xy[0]) * KeystoneUtils_726.lcd_w) / 1000));
        py4[3] = Integer.parseInt(df.format((rb_xy[1] * KeystoneUtils_726.lcd_h) / 1000));
        LogUtils.d("px4 = " + Arrays.toString(px4) + "  py4 = " + Arrays.toString(py4));
        DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.CHINA));
        float a = Float.parseFloat(df.format((100 - KeystoneUtils_726.readGlobalSettings(this, "zoom_value", 0) * 2) * 0.01).replace(",", "."));
        int oldScale = KeystoneUtils_726.readGlobalSettings(this, "zoom_scale_old", 0);
        Log.d(TAG, "a=" + a + " oldScale=" + oldScale + " scale=" + scale);
        int[] tpData = scUtils.getpxRatioxy(px4, py4, oldScale,
                scale, a, KeystoneUtils_726.lcd_w, KeystoneUtils_726.lcd_h);
        if (tpData[8] == 1) {
            KeystoneUtils_726.optKeystoneFun(tpData);
        }
    }
}