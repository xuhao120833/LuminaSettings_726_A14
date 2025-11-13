package com.htc.luminaos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.AudioManagerEx;
import android.media.AudioSettingParams;
import android.os.Build;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.htc.luminaos.MyApplication;
import com.htc.luminaos.R;
import com.htc.luminaos.databinding.ActivityDisplaySettingsBinding;
import com.htc.luminaos.utils.AddViewToScreen;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.ReflectUtil;
import com.htc.luminaos.utils.Utils;
import com.softwinner.PQControl;
import com.softwinner.tv.AwTvDisplayManager;
import com.softwinner.tv.common.AwTvAudioTypes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DisplaySettingsReceiver extends BroadcastReceiver implements View.OnClickListener, View.OnKeyListener, View.OnHoverListener, View.OnFocusChangeListener {
    private Context mContext;
    ActivityDisplaySettingsBinding displaySettingsBinding;
    private static String TAG = "DisplaySettingsReceiver";
    public static final String DisplayAction = "com.htc.DISPLAY_SETTINGS";
    private AddViewToScreen mavts = new AddViewToScreen();
    public WindowManager.LayoutParams lp;
    private String[] picture_mode_choices;
    private String[] picture_mode_values;
    private PQControl pqControl;
    AwTvDisplayManager awTvDisplayManager;
    private int brightness_system = 100;
    private int brightness = 0;
    private int mCurContrast = 50;
    private int mCurSaturation = 50;
    private int mCurHue = 50;
    private int mSharpness = 50;
    private int mColorTemp = 0;

    private int mR = 50;
    private int mG = 50;
    private int mB = 50;
    private long cur_time = 0;

    private int curPosition = 0;//当前图像模式

    //以下为EQ声音调节

    private int sound_mode = 0;//当前声音模式下标
    //返回键退出时，不能只在sound_mode==4的时候同步,只要切了不同模式就得Sync，防止重启读出来的数值不对
    //old_mode记录初始值
    private int old_mode = 0;//初始声音模式下标
    private int new_mode = 0;//修改后的声音模式下标
    private String[] soundMode_name;
    private int value_100hz = 0;
    private int value_500hz = 0;
    private int value_2khz = 0;
    private int value_4khz = 0;
    private int value_6khz = 0;
    private int value_8khz = 0;
    private int value_10khz = 0;
    private int value_12khz = 0;
    private int value_14khz = 0;
    private int value_18khz = 0;
    private static final String KEY_BQ_1 = "bq_1";
    private static final String KEY_BQ_2 = "bq_2";
    private static final String KEY_BQ_3 = "bq_3";
    private static final String KEY_BQ_4 = "bq_4";
    private static final String KEY_BQ_5 = "bq_5";
    private static final String KEY_BQ_6 = "bq_6";
    private static final String KEY_BQ_7 = "bq_7";
    private static final String KEY_BQ_8 = "bq_8";
    private static final String KEY_BQ_9 = "bq_9";
    private static final String KEY_BQ_10 = "bq_10";
    private static final String PQ_AND_EQ = "pq_eq";
    private static final String PQ = "pq";
    private static final String EQ = "eq";

    private AudioManagerEx mAudioManagerEx = null;
    private Handler handler = new Handler();
    private static String AUDIO_SFX_SYNC_FILE = "audio_sound_effects_sync_file";


    public DisplaySettingsReceiver(Context context) {
        mContext = context;
        mavts.setContext(mContext);
        initLayoutParams();
        initView();
        if (MyApplication.config.displayPictureModeShowCustom) {
            picture_mode_values = mContext.getResources().getStringArray(R.array.picture_mode_values);
            picture_mode_choices = mContext.getResources().getStringArray(MyApplication.config.displayPictureModeWeiMiTitle ?
                    R.array.picture_mode_weimi_choices : R.array.picture_mode_choices);
        } else {
            picture_mode_values = mContext.getResources().getStringArray(R.array.picture_mode_values_no_custom);
            picture_mode_choices = mContext.getResources().getStringArray(MyApplication.config.displayPictureModeWeiMiTitle ?
                    R.array.picture_mode_weimi_choices_no_custom : R.array.picture_mode_choices_no_custom);
        }
        awTvDisplayManager = AwTvDisplayManager.getInstance();
        pqControl = new PQControl();

        //EQ
        soundMode_name = mContext.getResources().getStringArray(R.array.soundMode_name);
        mAudioManagerEx = new AudioManagerEx(mContext);
    }

    public DisplaySettingsReceiver() {
        // 必须存在这个无参构造函数
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d(TAG, " 收到DisplaySettings的广播 ");
        String action = intent.getAction();
        if (action.equals(DisplayAction)) {
            try {
                boolean show = intent.getBooleanExtra("show", false);
                String type = intent.getStringExtra("type");
                type = (type != null) ? type : "";
                boolean attachedToWindow = SystemProperties.getBoolean("display.attach", false);
                if (show && !attachedToWindow) {
                    initData();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String currentTime = sdf.format(new Date());
                    LogUtils.d(TAG, "mavts.addView " + currentTime);
                    initViewByType(type);
                    mavts.addView(displaySettingsBinding.getRoot(), lp);
                    SystemProperties.set("display.attach", String.valueOf(true));
                } else if (!show && attachedToWindow) {
                    if (mAudioManagerEx != null && ((old_mode != new_mode) || sound_mode == 4)) {
                        mAudioManagerEx.setAudioParameters(AUDIO_SFX_SYNC_FILE, "");
                        old_mode = new_mode;
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String currentTime = sdf.format(new Date());
                    LogUtils.d(TAG, "mavts.clearView " + currentTime);
                    mavts.clearView(displaySettingsBinding.getRoot());
                    SystemProperties.set("display.attach", String.valueOf(false));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initLayoutParams() {
        lp = new WindowManager.LayoutParams();
        lp.format = PixelFormat.RGBA_8888;

        lp.flags = WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        lp.width = (int) mContext.getResources().getDimension(R.dimen.x_400);
        lp.height = (int) mContext.getResources().getDimension(R.dimen.y_640);
        lp.gravity = Gravity.START;
        lp.x = (int) mContext.getResources().getDimension(R.dimen.x_27);
    }

    private void initView() {
        displaySettingsBinding = ActivityDisplaySettingsBinding.inflate(LayoutInflater.from(mContext));

        displaySettingsBinding.changeMode.setOnClickListener(this);
        displaySettingsBinding.rlPictureMode.setOnClickListener(this);
//        displaySettingsBinding.rlBrightness.setOnClickListener(this);
//        displaySettingsBinding.rlContrast.setOnClickListener(this);
//        displaySettingsBinding.rlHue.setOnClickListener(this);
//        displaySettingsBinding.rlSaturation.setOnClickListener(this);
//        displaySettingsBinding.rlSharpness.setOnClickListener(this);
        displaySettingsBinding.brightnessLeft.setOnClickListener(this);
        displaySettingsBinding.brightnessRight.setOnClickListener(this);
        displaySettingsBinding.contrastLeft.setOnClickListener(this);
        displaySettingsBinding.contrastRight.setOnClickListener(this);
        displaySettingsBinding.hueLeft.setOnClickListener(this);
        displaySettingsBinding.hueRight.setOnClickListener(this);
        displaySettingsBinding.saturationLeft.setOnClickListener(this);
        displaySettingsBinding.saturationRight.setOnClickListener(this);
        displaySettingsBinding.sharpnessLeft.setOnClickListener(this);
        displaySettingsBinding.sharpnessRight.setOnClickListener(this);

        displaySettingsBinding.changeMode.setOnFocusChangeListener(this);
        displaySettingsBinding.rlPictureMode.setOnFocusChangeListener(this);
        displaySettingsBinding.rlBrightness.setOnFocusChangeListener(this);
        displaySettingsBinding.rlContrast.setOnFocusChangeListener(this);
        displaySettingsBinding.rlHue.setOnFocusChangeListener(this);
        displaySettingsBinding.rlSaturation.setOnFocusChangeListener(this);
        displaySettingsBinding.rlSharpness.setOnFocusChangeListener(this);

        displaySettingsBinding.changeMode.setOnKeyListener(this);
        displaySettingsBinding.rlPictureMode.setOnKeyListener(this);
        displaySettingsBinding.rlBrightness.setOnKeyListener(this);
        displaySettingsBinding.rlContrast.setOnKeyListener(this);
        displaySettingsBinding.rlHue.setOnKeyListener(this);
        displaySettingsBinding.rlSaturation.setOnKeyListener(this);
        displaySettingsBinding.rlSharpness.setOnKeyListener(this);

        displaySettingsBinding.changeMode.setOnHoverListener(this);
        displaySettingsBinding.rlPictureMode.setOnHoverListener(this);
        displaySettingsBinding.rlBrightness.setOnHoverListener(this);
        displaySettingsBinding.rlContrast.setOnHoverListener(this);
        displaySettingsBinding.rlHue.setOnHoverListener(this);
        displaySettingsBinding.rlSaturation.setOnHoverListener(this);
        displaySettingsBinding.rlSharpness.setOnHoverListener(this);

        displaySettingsBinding.rlPictureMode.setVisibility(MyApplication.config.displayPictureMode ? View.VISIBLE : View.GONE);
        displaySettingsBinding.rlBrightness.setVisibility(MyApplication.config.brightnessPQ ? View.VISIBLE : View.GONE);
        displaySettingsBinding.rlContrast.setVisibility(MyApplication.config.contrast ? View.VISIBLE : View.GONE);
        displaySettingsBinding.rlHue.setVisibility(MyApplication.config.hue ? View.VISIBLE : View.GONE);
        displaySettingsBinding.rlSaturation.setVisibility(MyApplication.config.saturation ? View.VISIBLE : View.GONE);
        displaySettingsBinding.rlSharpness.setVisibility(MyApplication.config.sharpness ? View.VISIBLE : View.GONE);

        displaySettingsBinding.pictureMode.setSelected(true);
        displaySettingsBinding.pictureModeTv.setSelected(true);
        displaySettingsBinding.txtBrightness.setSelected(true);
        displaySettingsBinding.txtContrast.setSelected(true);
        displaySettingsBinding.txtHue.setSelected(true);
        displaySettingsBinding.txtSaturation.setSelected(true);
        displaySettingsBinding.txtSharpness.setSelected(true);

        //以下为EQ
        displaySettingsBinding.rlAudioMode.setOnClickListener(this);
        displaySettingsBinding.rlAudioMode.setOnKeyListener(this);
        displaySettingsBinding.rlAudioMode.setOnHoverListener(this);
        displaySettingsBinding.rlAudioMode.setOnFocusChangeListener(this);
        displaySettingsBinding.audioModeRight.setOnClickListener(this);
        displaySettingsBinding.audioModeLeft.setOnClickListener(this);

        displaySettingsBinding.rl100hz.setOnClickListener(this);
        displaySettingsBinding.rl100hz.setOnKeyListener(this);
        displaySettingsBinding.rl100hz.setOnHoverListener(this);
        displaySettingsBinding.right100.setOnClickListener(this);
        displaySettingsBinding.left100.setOnClickListener(this);

        displaySettingsBinding.rl500hz.setOnClickListener(this);
        displaySettingsBinding.rl500hz.setOnKeyListener(this);
        displaySettingsBinding.rl500hz.setOnHoverListener(this);
        displaySettingsBinding.right500.setOnClickListener(this);
        displaySettingsBinding.left500.setOnClickListener(this);

        displaySettingsBinding.rl2khz.setOnClickListener(this);
        displaySettingsBinding.rl2khz.setOnKeyListener(this);
        displaySettingsBinding.rl2khz.setOnHoverListener(this);
        displaySettingsBinding.right2k.setOnClickListener(this);
        displaySettingsBinding.left2k.setOnClickListener(this);

        displaySettingsBinding.rl4khz.setOnClickListener(this);
        displaySettingsBinding.rl4khz.setOnKeyListener(this);
        displaySettingsBinding.rl4khz.setOnHoverListener(this);
        displaySettingsBinding.right4k.setOnClickListener(this);
        displaySettingsBinding.left4k.setOnClickListener(this);

        displaySettingsBinding.rl6khz.setOnClickListener(this);
        displaySettingsBinding.rl6khz.setOnKeyListener(this);
        displaySettingsBinding.rl6khz.setOnHoverListener(this);
        displaySettingsBinding.right6k.setOnClickListener(this);
        displaySettingsBinding.left6k.setOnClickListener(this);

        displaySettingsBinding.rl8khz.setOnClickListener(this);
        displaySettingsBinding.rl8khz.setOnKeyListener(this);
        displaySettingsBinding.rl8khz.setOnHoverListener(this);
        displaySettingsBinding.right8k.setOnClickListener(this);
        displaySettingsBinding.left8k.setOnClickListener(this);

        displaySettingsBinding.rl10khz.setOnClickListener(this);
        displaySettingsBinding.rl10khz.setOnKeyListener(this);
        displaySettingsBinding.rl10khz.setOnHoverListener(this);
        displaySettingsBinding.right10k.setOnClickListener(this);
        displaySettingsBinding.left10k.setOnClickListener(this);

        displaySettingsBinding.rl12khz.setOnClickListener(this);
        displaySettingsBinding.rl12khz.setOnKeyListener(this);
        displaySettingsBinding.rl12khz.setOnHoverListener(this);
        displaySettingsBinding.right12k.setOnClickListener(this);
        displaySettingsBinding.left12k.setOnClickListener(this);

        displaySettingsBinding.rl14khz.setOnClickListener(this);
        displaySettingsBinding.rl14khz.setOnKeyListener(this);
        displaySettingsBinding.rl14khz.setOnHoverListener(this);
        displaySettingsBinding.right14k.setOnClickListener(this);
        displaySettingsBinding.left14k.setOnClickListener(this);

        displaySettingsBinding.rl18khz.setOnClickListener(this);
        displaySettingsBinding.rl18khz.setOnKeyListener(this);
        displaySettingsBinding.rl18khz.setOnHoverListener(this);
        displaySettingsBinding.right18k.setOnClickListener(this);
        displaySettingsBinding.left18k.setOnClickListener(this);

        displaySettingsBinding.rl100hz.setVisibility(MyApplication.config.Menu100HZ ? View.VISIBLE : View.GONE);
        displaySettingsBinding.rl500hz.setVisibility(MyApplication.config.Menu500HZ ? View.VISIBLE : View.GONE);
        displaySettingsBinding.rl2khz.setVisibility(MyApplication.config.Menu2KHZ ? View.VISIBLE : View.GONE);
        displaySettingsBinding.rl4khz.setVisibility(MyApplication.config.Menu4KHZ ? View.VISIBLE : View.GONE);
        displaySettingsBinding.rl6khz.setVisibility(MyApplication.config.Menu6KHZ ? View.VISIBLE : View.GONE);
        displaySettingsBinding.rl8khz.setVisibility(MyApplication.config.Menu8KHZ ? View.VISIBLE : View.GONE);
        displaySettingsBinding.rl10khz.setVisibility(MyApplication.config.Menu10KHZ ? View.VISIBLE : View.GONE);
        displaySettingsBinding.rl12khz.setVisibility(MyApplication.config.Menu12KHZ ? View.VISIBLE : View.GONE);
        displaySettingsBinding.rl14khz.setVisibility(MyApplication.config.Menu14KHZ ? View.VISIBLE : View.GONE);
        displaySettingsBinding.rl18khz.setVisibility(MyApplication.config.Menu18KHZ ? View.VISIBLE : View.GONE);
    }

    private void initViewByType(String type) {
        switch (type) {
            case PQ_AND_EQ -> {
                displaySettingsBinding.changeModeLeft.setVisibility(View.VISIBLE);
                displaySettingsBinding.changeModeRight.setVisibility(View.VISIBLE);
                displaySettingsBinding.pictureModeLinear.setVisibility(View.VISIBLE);
                displaySettingsBinding.audioModeScrollView.setVisibility(View.GONE);
                displaySettingsBinding.title.setText(mContext.getString(R.string.display_settings));
                displaySettingsBinding.changeMode.setOnClickListener(this);
                displaySettingsBinding.changeMode.setOnKeyListener(this);
                displaySettingsBinding.changeMode.setOnHoverListener(this);
                displaySettingsBinding.changeMode.setOnFocusChangeListener(this);
                displaySettingsBinding.changeMode.setFocusable(true);
                displaySettingsBinding.changeMode.setFocusableInTouchMode(true);
                displaySettingsBinding.changeMode.requestFocus();
            }
            case PQ -> {
                displaySettingsBinding.changeModeLeft.setVisibility(View.GONE);
                displaySettingsBinding.changeModeRight.setVisibility(View.GONE);
                displaySettingsBinding.pictureModeLinear.setVisibility(View.VISIBLE);
                displaySettingsBinding.audioModeScrollView.setVisibility(View.GONE);
                displaySettingsBinding.title.setText(mContext.getString(R.string.display_settings));
                displaySettingsBinding.changeMode.setOnClickListener(null);
                displaySettingsBinding.changeMode.setOnKeyListener(null);
                displaySettingsBinding.changeMode.setOnHoverListener(null);
                displaySettingsBinding.changeMode.setOnFocusChangeListener(null);
                displaySettingsBinding.changeMode.setFocusable(false);
                displaySettingsBinding.changeMode.setFocusableInTouchMode(false);
                displaySettingsBinding.rlPictureMode.requestFocus();

            }
            case EQ -> {
                displaySettingsBinding.changeModeLeft.setVisibility(View.GONE);
                displaySettingsBinding.changeModeRight.setVisibility(View.GONE);
                displaySettingsBinding.pictureModeLinear.setVisibility(View.GONE);
                displaySettingsBinding.audioModeScrollView.setVisibility(View.VISIBLE);
                displaySettingsBinding.title.setText(mContext.getString(R.string.Audio_Settings));
                displaySettingsBinding.changeMode.setOnClickListener(null);
                displaySettingsBinding.changeMode.setOnKeyListener(null);
                displaySettingsBinding.changeMode.setOnHoverListener(null);
                displaySettingsBinding.changeMode.setOnFocusChangeListener(null);
                displaySettingsBinding.changeMode.setFocusable(false);
                displaySettingsBinding.changeMode.setFocusableInTouchMode(false);
                displaySettingsBinding.rlAudioMode.requestFocus();
            }
            default -> {
                displaySettingsBinding.changeModeLeft.setVisibility(View.VISIBLE);
                displaySettingsBinding.changeModeRight.setVisibility(View.VISIBLE);
                displaySettingsBinding.pictureModeLinear.setVisibility(View.VISIBLE);
                displaySettingsBinding.audioModeScrollView.setVisibility(View.GONE);
                displaySettingsBinding.title.setText(mContext.getString(R.string.display_settings));
                displaySettingsBinding.changeMode.setOnClickListener(this);
                displaySettingsBinding.changeMode.setOnKeyListener(this);
                displaySettingsBinding.changeMode.setOnHoverListener(this);
                displaySettingsBinding.changeMode.setOnFocusChangeListener(this);
                displaySettingsBinding.changeMode.setFocusable(true);
                displaySettingsBinding.changeMode.setFocusableInTouchMode(true);
                if (!MyApplication.config.AudioMode) {
                    displaySettingsBinding.changeModeLeft.setVisibility(View.GONE);
                    displaySettingsBinding.changeModeRight.setVisibility(View.GONE);
                    displaySettingsBinding.changeMode.setOnClickListener(null);
                    displaySettingsBinding.changeMode.setOnKeyListener(null);
                    displaySettingsBinding.changeMode.setOnHoverListener(null);
                    displaySettingsBinding.changeMode.setOnFocusChangeListener(null);
                    displaySettingsBinding.changeMode.setFocusable(false);
                    displaySettingsBinding.changeMode.setFocusableInTouchMode(false);
                    displaySettingsBinding.rlPictureMode.requestFocus();
                } else {
                    displaySettingsBinding.changeMode.requestFocus();
                }
            }
        }
    }

    private void initData() {
        String pictureName = pqControl.getPictureModeName();
        LogUtils.d(TAG, "pictureName " + pictureName);
        for (int i = 0; i < picture_mode_values.length; i++) {
            if (picture_mode_values[i].equals(pictureName)) {
                curPosition = i;
                break;
            }
        }
        displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        updateDisplayStatus();

        brightness_system = pqControl.getBasicControl(PQControl.PQ_BASIC_BRIGHTNESS);
        mCurContrast = pqControl.getBasicControl(PQControl.PQ_BASIC_CONTRAST);
        mCurSaturation = pqControl.getBasicControl(PQControl.PQ_BASIC_SATURATION);
        mCurHue = pqControl.getBasicControl(PQControl.PQ_BASIC_HUE);
        mSharpness = pqControl.getBasicControl(PQControl.PQ_BASIC_SHARPNESS);

        int[] mRGBInfo = pqControl.factoryGetWBInfo(mColorTemp);
        mR = mRGBInfo[PQControl.GAIN_R];
        mG = mRGBInfo[PQControl.GAIN_G];
        mB = mRGBInfo[PQControl.GAIN_B];

        updateBrightnessSystem(false);
        getBrightness();
        updateContrast(false);
        updateHue(false);
        updateSaturation(false);
        updateSharpness(false);

        //EQ
        sound_mode = getSettingModeValue();
        old_mode = sound_mode;
        LogUtils.d(TAG, "initData sound_mode " + sound_mode);
        displaySettingsBinding.audioModeTv.setText(soundMode_name[sound_mode]);
        updateAllEQValue();
        updateAudioStatus();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            LogUtils.d(TAG, "onKey KEYCODE_BACK");
            if (mAudioManagerEx != null && ((old_mode != new_mode) || sound_mode == 4)) {
                mAudioManagerEx.setAudioParameters(AUDIO_SFX_SYNC_FILE, "");
                old_mode = new_mode;
            }
            LogUtils.d(TAG, "onKey mAudioManagerEx.setAudioParameters");
            SystemProperties.set("display.attach", String.valueOf(false));
            LogUtils.d(TAG, "onKey display.attach " + SystemProperties.getBoolean("display.attach", false));
            mavts.clearView(displaySettingsBinding.getRoot());
            return true;
        }
        if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
            return true;
        }
        AudioManager audioManager = (AudioManager) v.getContext().getSystemService(Context.AUDIO_SERVICE);
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
            int id = v.getId();
            if (id == R.id.change_mode) {
                if (displaySettingsBinding.audioModeScrollView.getVisibility() == View.VISIBLE) {
                    displaySettingsBinding.audioModeScrollView.setVisibility(View.GONE);
                    displaySettingsBinding.pictureModeLinear.setVisibility(View.VISIBLE);
                    displaySettingsBinding.title.setText(mContext.getString(R.string.display_settings));
                } else {
                    displaySettingsBinding.audioModeScrollView.setVisibility(View.VISIBLE);
                    displaySettingsBinding.title.setText(mContext.getString(R.string.Audio_Settings));
                    displaySettingsBinding.pictureModeLinear.setVisibility(View.GONE);
                }
                if (audioManager != null) {
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                }
            } else if (id == R.id.rl_picture_mode) {
                if (curPosition == 0) {
                    curPosition = picture_mode_values.length - 1;
                } else {
                    curPosition -= 1;
                }
                pqControl.setPictureMode(picture_mode_values[curPosition]);
                //awTvDisplayManager.setPictureModeByName(enumPictureModes[curPosition]);
                updatePictureMode();
                displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
                if (audioManager != null) {
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                }
                audioManager = null;
//                    return true;
            } else if (id == R.id.rl_color_temp) {
//                if (mColorTemp == 0) {
//                    mColorTemp = colorTemp_name.length - 1;
//                } else {
//                    mColorTemp -= 1;
//                }
//                updateColorTemp(mColorTemp);
//                if (audioManager != null) {
//                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
//                }
//                audioManager = null;
//                    return true;
            } else if (id == R.id.rl_brightness) {
                if (brightness_system == 1)
                    return false;

                brightness_system -= 1;
                if (brightness_system <= 1) {
                    brightness_system = 1;
                }
                updateBrightnessSystem(true);
                return true;
//                    break;
            } else if (id == R.id.rl_contrast) {
                if (mCurContrast == 1)
                    return false;

                mCurContrast -= 1;
                if (mCurContrast < 1)
                    mCurContrast = 1;
                updateContrast(true);
//                    break;
                return true;
            } else if (id == R.id.rl_hue) {
                if (mCurHue == 1)
                    return false;

                mCurHue -= 1;
                if (mCurHue < 1)
                    mCurHue = 1;

                updateHue(true);
                return true;
//                    break;
            } else if (id == R.id.rl_saturation) {
                LogUtils.d(TAG, "饱和度 向左");
                if (mCurSaturation == 1) {
                    LogUtils.d(TAG, "饱和度 向左不执行");
                    return false;
                }

                mCurSaturation -= 1;
                if (mCurSaturation < 1)
                    mCurSaturation = 1;

                updateSaturation(true);
                return true;
//                    break;
            } else if (id == R.id.rl_sharpness) {
                if (mSharpness == 1)
                    return false;

                mSharpness -= 1;
                if (mSharpness < 1)
                    mSharpness = 1;

                updateSharpness(true);
                return true;
//                    break;
            } else if (id == R.id.rl_audio_mode) {
                if (sound_mode == 4 && Utils.audio_change) {
                    mAudioManagerEx.setAudioParameters(AUDIO_SFX_SYNC_FILE, "");
                    Utils.audio_change = false;
                }
                sound_mode -= 1;
                if (sound_mode == -1) {
                    sound_mode = soundMode_name.length - 1;
                }
                new_mode = sound_mode;
                updateSettingModeValue(sound_mode);
                displaySettingsBinding.audioModeTv.setText(soundMode_name[sound_mode]);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateAllEQValue();
                    }
                }, 200);
                updateAudioStatus();
                if (audioManager != null) {
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                }
                audioManager = null;
            } else if (id == R.id.rl_100hz) {
                if (value_100hz == -100)
                    return false;
                value_100hz -= 1;
                displaySettingsBinding.tv100hz.setText(String.valueOf(value_100hz));
                updateSettingIntValue(KEY_BQ_1, value_100hz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_500hz) {
                if (value_500hz == -100)
                    return false;
                value_500hz -= 1;
                displaySettingsBinding.tv500hz.setText(String.valueOf(value_500hz));
                updateSettingIntValue(KEY_BQ_2, value_500hz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_2khz) {
                if (value_2khz == -100)
                    return false;
                value_2khz -= 1;
                displaySettingsBinding.tv2khz.setText(String.valueOf(value_2khz));
                updateSettingIntValue(KEY_BQ_3, value_2khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_4khz) {
                if (value_4khz == -100)
                    return false;
                value_4khz -= 1;
                displaySettingsBinding.tv4khz.setText(String.valueOf(value_4khz));
                updateSettingIntValue(KEY_BQ_4, value_4khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_6khz) {
                if (value_6khz == -100)
                    return false;
                value_6khz -= 1;
                displaySettingsBinding.tv6khz.setText(String.valueOf(value_6khz));
                updateSettingIntValue(KEY_BQ_5, value_6khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_8khz) {
                if (value_8khz == -100)
                    return false;
                value_8khz -= 1;
                displaySettingsBinding.tv8khz.setText(String.valueOf(value_8khz));
                updateSettingIntValue(KEY_BQ_6, value_8khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_10khz) {
                if (value_10khz == -100)
                    return false;
                value_10khz -= 1;
                displaySettingsBinding.tv10khz.setText(String.valueOf(value_10khz));
                updateSettingIntValue(KEY_BQ_7, value_10khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_12khz) {
                if (value_12khz == -100)
                    return false;
                value_12khz -= 1;
                displaySettingsBinding.tv12khz.setText(String.valueOf(value_12khz));
                updateSettingIntValue(KEY_BQ_8, value_12khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_14khz) {
                if (value_14khz == -100)
                    return false;
                value_14khz -= 1;
                displaySettingsBinding.tv14khz.setText(String.valueOf(value_14khz));
                updateSettingIntValue(KEY_BQ_9, value_14khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_18khz) {
                if (value_18khz == -100)
                    return false;
                value_18khz -= 1;
                displaySettingsBinding.tv18khz.setText(String.valueOf(value_18khz));
                updateSettingIntValue(KEY_BQ_10, value_18khz);
                Utils.audio_change = true;
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN) {
            int id = v.getId();
            if (id == R.id.change_mode) {
                if (displaySettingsBinding.audioModeScrollView.getVisibility() == View.VISIBLE) {
                    displaySettingsBinding.audioModeScrollView.setVisibility(View.GONE);
                    displaySettingsBinding.pictureModeLinear.setVisibility(View.VISIBLE);
                    displaySettingsBinding.title.setText(mContext.getString(R.string.display_settings));
                } else {
                    displaySettingsBinding.audioModeScrollView.setVisibility(View.VISIBLE);
                    displaySettingsBinding.title.setText(mContext.getString(R.string.Audio_Settings));
                    displaySettingsBinding.pictureModeLinear.setVisibility(View.GONE);
                }
                if (audioManager != null) {
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                }
            } else if (id == R.id.rl_color_temp) {
                //色温
//                if (mColorTemp == colorTemp_name.length - 1) {
//                    mColorTemp = 0;
//                } else {
//                    mColorTemp += 1;
//                }
//                updateColorTemp(mColorTemp);
//                if (audioManager != null) {
//                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
//                }
//                audioManager = null;
//                    return true;
            } else if (id == R.id.rl_brightness) {
                if (brightness_system == 100)
                    return false;

                brightness_system += 1;
                if (brightness_system > 100)
                    brightness_system = 100;

                updateBrightnessSystem(true);
                return true;
//                    break;
            } else if (id == R.id.rl_contrast) {
                if (mCurContrast == 100)
                    return false;

                mCurContrast += 1;
                if (mCurContrast > 100)
                    mCurContrast = 100;

                updateContrast(true);
                return true;
//                    break;
            } else if (id == R.id.rl_hue) {
                if (mCurHue == 100)
                    return false;

                mCurHue += 1;
                if (mCurHue > 100)
                    mCurHue = 100;

                updateHue(true);
                return true;
//                    break;
            } else if (id == R.id.rl_saturation) {
                if (mCurSaturation == 100)
                    return false;

                mCurSaturation += 1;
                if (mCurSaturation > 100)
                    mCurSaturation = 100;

                updateSaturation(true);
                return true;
//                    break;
            } else if (id == R.id.rl_sharpness) {
                LogUtils.d(TAG, "锐度 向右");
                if (mSharpness == 100) {
                    LogUtils.d(TAG, "锐度 向右不执行");
                    return false;
                }

                mSharpness += 1;
                if (mSharpness > 100)
                    mSharpness = 100;

                updateSharpness(true);
                return true;
//                    break;
            } else if (id == R.id.rl_picture_mode) {
                if (curPosition == picture_mode_values.length - 1) {
                    curPosition = 0;
                } else {
                    curPosition += 1;
                }
                pqControl.setPictureMode(picture_mode_values[curPosition]);
                updatePictureMode();
                displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
                if (audioManager != null) {
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                }
                audioManager = null;
//                    return true;
            } else if (id == R.id.rl_audio_mode) {
                if (sound_mode == 4 && Utils.audio_change) {
                    mAudioManagerEx.setAudioParameters(AUDIO_SFX_SYNC_FILE, "");
                    Utils.audio_change = false;
                }
                sound_mode += 1;
                if (sound_mode == soundMode_name.length) {
                    sound_mode = 0;
                }
                new_mode = sound_mode;
                updateSettingModeValue(sound_mode);
                displaySettingsBinding.audioModeTv.setText(soundMode_name[sound_mode]);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateAllEQValue();
                    }
                }, 200);
                updateAudioStatus();
                if (audioManager != null) {
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                }
                audioManager = null;
            } else if (id == R.id.rl_100hz) {
                if (value_100hz == 100)
                    return false;
                value_100hz += 1;
                displaySettingsBinding.tv100hz.setText(String.valueOf(value_100hz));
                updateSettingIntValue(KEY_BQ_1, value_100hz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_500hz) {
                if (value_500hz == 100)
                    return false;
                value_500hz += 1;
                displaySettingsBinding.tv500hz.setText(String.valueOf(value_500hz));
                updateSettingIntValue(KEY_BQ_2, value_500hz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_2khz) {
                if (value_2khz == 100)
                    return false;
                value_2khz += 1;
                displaySettingsBinding.tv2khz.setText(String.valueOf(value_2khz));
                updateSettingIntValue(KEY_BQ_3, value_2khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_4khz) {
                if (value_4khz == 100)
                    return false;
                value_4khz += 1;
                displaySettingsBinding.tv4khz.setText(String.valueOf(value_4khz));
                updateSettingIntValue(KEY_BQ_4, value_4khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_6khz) {
                if (value_6khz == 100)
                    return false;
                value_6khz += 1;
                displaySettingsBinding.tv6khz.setText(String.valueOf(value_6khz));
                updateSettingIntValue(KEY_BQ_5, value_6khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_8khz) {
                if (value_8khz == 100)
                    return false;
                value_8khz += 1;
                displaySettingsBinding.tv8khz.setText(String.valueOf(value_8khz));
                updateSettingIntValue(KEY_BQ_6, value_8khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_10khz) {
                if (value_10khz == 100)
                    return false;
                value_10khz += 1;
                displaySettingsBinding.tv10khz.setText(String.valueOf(value_10khz));
                updateSettingIntValue(KEY_BQ_7, value_10khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_12khz) {
                if (value_12khz == 100)
                    return false;
                value_12khz += 1;
                displaySettingsBinding.tv12khz.setText(String.valueOf(value_12khz));
                updateSettingIntValue(KEY_BQ_8, value_12khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_14khz) {
                if (value_14khz == 100)
                    return false;
                value_14khz += 1;
                displaySettingsBinding.tv14khz.setText(String.valueOf(value_14khz));
                updateSettingIntValue(KEY_BQ_9, value_14khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_18khz) {
                if (value_18khz == 100)
                    return false;
                value_18khz += 1;
                displaySettingsBinding.tv18khz.setText(String.valueOf(value_18khz));
                updateSettingIntValue(KEY_BQ_10, value_18khz);
                Utils.audio_change = true;
                return true;
            }
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.change_mode) {
            if (displaySettingsBinding.audioModeScrollView.getVisibility() == View.VISIBLE) {
                displaySettingsBinding.audioModeScrollView.setVisibility(View.GONE);
                displaySettingsBinding.pictureModeLinear.setVisibility(View.VISIBLE);
                displaySettingsBinding.title.setText(mContext.getString(R.string.display_settings));
            } else {
                displaySettingsBinding.audioModeScrollView.setVisibility(View.VISIBLE);
                displaySettingsBinding.title.setText(mContext.getString(R.string.Audio_Settings));
                displaySettingsBinding.pictureModeLinear.setVisibility(View.GONE);
            }
        } else if (id == R.id.rl_picture_mode) {
            if (curPosition == picture_mode_values.length - 1) {
                curPosition = 0;
            } else {
                curPosition += 1;
            }
            pqControl.setPictureMode(picture_mode_values[curPosition]);
            updatePictureMode();
            displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        } else if (id == R.id.rl_brightness) {
            if (brightness_system == 100)
                return;

            brightness_system += 1;
            if (brightness_system > 100)
                brightness_system = 100;

            updateBrightnessSystem(true);
        } else if (id == R.id.brightness_left) {
            if (brightness_system == 1)
                return;

            brightness_system -= 1;
            if (brightness_system <= 1) {
                brightness_system = 1;
            }
            updateBrightnessSystem(true);
        } else if (id == R.id.brightness_right) {
            if (brightness_system == 100)
                return;

            brightness_system += 1;
            if (brightness_system > 100)
                brightness_system = 100;

            updateBrightnessSystem(true);
        } else if (id == R.id.rl_contrast) {
            if (mCurContrast == 100)
                return;

            mCurContrast += 1;
            if (mCurContrast > 100)
                mCurContrast = 100;

            updateContrast(true);
        } else if (id == R.id.contrast_left) {
            if (mCurContrast == 1)
                return;

            mCurContrast -= 1;
            if (mCurContrast < 1)
                mCurContrast = 1;
            updateContrast(true);
        } else if (id == R.id.contrast_right) {
            if (mCurContrast == 100)
                return;

            mCurContrast += 1;
            if (mCurContrast > 100)
                mCurContrast = 100;

            updateContrast(true);
        } else if (id == R.id.rl_hue) {
            if (mCurHue == 100)
                return;

            mCurHue += 1;
            if (mCurHue > 100)
                mCurHue = 100;

            updateHue(true);
        } else if (id == R.id.hue_left) {
            if (mCurHue == 1)
                return;

            mCurHue -= 1;
            if (mCurHue < 1)
                mCurHue = 1;

            updateHue(true);
        } else if (id == R.id.hue_right) {
            if (mCurHue == 100)
                return;

            mCurHue += 1;
            if (mCurHue > 100)
                mCurHue = 100;

            updateHue(true);
        } else if (id == R.id.rl_saturation) {
            if (mCurSaturation == 100)
                return;

            mCurSaturation += 1;
            if (mCurSaturation > 100)
                mCurSaturation = 100;

            updateSaturation(true);
        } else if (id == R.id.saturation_left) {
            LogUtils.d(TAG, "饱和度 向左");
            if (mCurSaturation == 1) {
                LogUtils.d(TAG, "饱和度 向左不执行");
                return;
            }

            mCurSaturation -= 1;
            if (mCurSaturation < 1)
                mCurSaturation = 1;

            updateSaturation(true);
        } else if (id == R.id.saturation_right) {
            if (mCurSaturation == 100)
                return;

            mCurSaturation += 1;
            if (mCurSaturation > 100)
                mCurSaturation = 100;

            updateSaturation(true);
        } else if (id == R.id.rl_sharpness) {
            LogUtils.d(TAG, "锐度 向右");
            if (mSharpness == 100) {
                LogUtils.d(TAG, "锐度 向右不执行");
                return;
            }

            mSharpness += 1;
            if (mSharpness > 100)
                mSharpness = 100;

            updateSharpness(true);
        } else if (id == R.id.sharpness_left) {
            if (mSharpness == 1)
                return;

            mSharpness -= 1;
            if (mSharpness < 1)
                mSharpness = 1;

            updateSharpness(true);
        } else if (id == R.id.sharpness_right) {
            LogUtils.d(TAG, "锐度 向右");
            if (mSharpness == 100) {
                LogUtils.d(TAG, "锐度 向右不执行");
                return;
            }

            mSharpness += 1;
            if (mSharpness > 100)
                mSharpness = 100;

            updateSharpness(true);
        } else if (id == R.id.rl_audio_mode) {
            if (sound_mode == 4 && Utils.audio_change) {
                mAudioManagerEx.setAudioParameters(AUDIO_SFX_SYNC_FILE, "");
                Utils.audio_change = false;
            }
            sound_mode += 1;
            if (sound_mode == soundMode_name.length) {
                sound_mode = 0;
            }
            new_mode = sound_mode;
            updateSettingModeValue(sound_mode);
            displaySettingsBinding.audioModeTv.setText(soundMode_name[sound_mode]);
            handler.postDelayed(new Runnable() { //延迟200ms再去读取，防止设置的模式还未生效
                @Override
                public void run() {
                    updateAllEQValue();
                }
            }, 200);
//            updateAllEQValue();
            updateAudioStatus();
        } else if (id == R.id.rl_100hz) {
            if (value_100hz == 100)
                return;
            value_100hz += 1;
            displaySettingsBinding.tv100hz.setText(String.valueOf(value_100hz));
            updateSettingIntValue(KEY_BQ_1, value_100hz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_500hz) {
            if (value_500hz == 100)
                return;
            value_500hz += 1;
            displaySettingsBinding.tv500hz.setText(String.valueOf(value_500hz));
            updateSettingIntValue(KEY_BQ_2, value_500hz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_2khz) {
            if (value_2khz == 100)
                return;
            value_2khz += 1;
            displaySettingsBinding.tv2khz.setText(String.valueOf(value_2khz));
            updateSettingIntValue(KEY_BQ_3, value_2khz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_4khz) {
            if (value_4khz == 100)
                return;
            value_4khz += 1;
            displaySettingsBinding.tv4khz.setText(String.valueOf(value_4khz));
            updateSettingIntValue(KEY_BQ_4, value_4khz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_6khz) {
            if (value_6khz == 100)
                return;
            value_6khz += 1;
            displaySettingsBinding.tv6khz.setText(String.valueOf(value_6khz));
            updateSettingIntValue(KEY_BQ_5, value_6khz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_8khz) {
            if (value_8khz == 100)
                return;
            value_8khz += 1;
            displaySettingsBinding.tv8khz.setText(String.valueOf(value_8khz));
            updateSettingIntValue(KEY_BQ_6, value_8khz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_10khz) {
            if (value_10khz == 100)
                return;
            value_10khz += 1;
            displaySettingsBinding.tv10khz.setText(String.valueOf(value_10khz));
            updateSettingIntValue(KEY_BQ_7, value_10khz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_12khz) {
            if (value_12khz == 100)
                return;
            value_12khz += 1;
            displaySettingsBinding.tv12khz.setText(String.valueOf(value_12khz));
            updateSettingIntValue(KEY_BQ_8, value_12khz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_14khz) {
            if (value_14khz == 100)
                return;
            value_14khz += 1;
            displaySettingsBinding.tv14khz.setText(String.valueOf(value_14khz));
            updateSettingIntValue(KEY_BQ_9, value_14khz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_18khz) {
            if (value_18khz == 100)
                return;
            value_18khz += 1;
            displaySettingsBinding.tv18khz.setText(String.valueOf(value_18khz));
            updateSettingIntValue(KEY_BQ_10, value_18khz);
            Utils.audio_change = true;
        }

    }

    private void updateDisplayStatus() {

        if (curPosition == picture_mode_choices.length - 1) {

            displaySettingsBinding.rlBrightness.setEnabled(true);
            displaySettingsBinding.rlContrast.setEnabled(true);
            displaySettingsBinding.rlHue.setEnabled(true);
            displaySettingsBinding.rlSaturation.setEnabled(true);
            displaySettingsBinding.rlSharpness.setEnabled(true);

            displaySettingsBinding.brightnessLeft.setEnabled(true);
            displaySettingsBinding.brightnessRight.setEnabled(true);
            displaySettingsBinding.contrastLeft.setEnabled(true);
            displaySettingsBinding.contrastRight.setEnabled(true);
            displaySettingsBinding.hueLeft.setEnabled(true);
            displaySettingsBinding.hueRight.setEnabled(true);
            displaySettingsBinding.saturationLeft.setEnabled(true);
            displaySettingsBinding.saturationRight.setEnabled(true);
            displaySettingsBinding.sharpnessLeft.setEnabled(true);
            displaySettingsBinding.sharpnessRight.setEnabled(true);

            displaySettingsBinding.rlBrightness.setAlpha(1.0f);
            displaySettingsBinding.rlContrast.setAlpha(1.0f);
            displaySettingsBinding.rlHue.setAlpha(1.0f);
            displaySettingsBinding.rlSaturation.setAlpha(1.0f);
            displaySettingsBinding.rlSharpness.setAlpha(1.0f);
        } else {
            displaySettingsBinding.rlBrightness.setEnabled(false);
            displaySettingsBinding.rlContrast.setEnabled(false);
            displaySettingsBinding.rlHue.setEnabled(false);
            displaySettingsBinding.rlSaturation.setEnabled(false);
            displaySettingsBinding.rlSharpness.setEnabled(false);

            displaySettingsBinding.brightnessLeft.setEnabled(false);
            displaySettingsBinding.brightnessRight.setEnabled(false);
            displaySettingsBinding.contrastLeft.setEnabled(false);
            displaySettingsBinding.contrastRight.setEnabled(false);
            displaySettingsBinding.hueLeft.setEnabled(false);
            displaySettingsBinding.hueRight.setEnabled(false);
            displaySettingsBinding.saturationLeft.setEnabled(false);
            displaySettingsBinding.saturationRight.setEnabled(false);
            displaySettingsBinding.sharpnessLeft.setEnabled(false);
            displaySettingsBinding.sharpnessRight.setEnabled(false);

            displaySettingsBinding.rlBrightness.setAlpha(0.7f);
            displaySettingsBinding.rlContrast.setAlpha(0.7f);
            displaySettingsBinding.rlHue.setAlpha(0.7f);
            displaySettingsBinding.rlSaturation.setAlpha(0.7f);
            displaySettingsBinding.rlSharpness.setAlpha(0.7f);
        }
    }

    private void updateBrightnessSystem(boolean set) {
        if (set) {
            pqControl.factorySetBasicControl(0xFF, picture_mode_values[curPosition], PQControl.PQ_BASIC_BRIGHTNESS, brightness_system);
            curPosition = picture_mode_choices.length - 1;
            displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
        displaySettingsBinding.txtBrightnessParcent.setText(String.valueOf(brightness_system));
    }

    private void getBrightness() {
        if (MyApplication.config.brightnessLevel == 1 || MyApplication.config.brightnessLevel == 2)
            brightness = ReflectUtil.invoke_get_bright() - (3 - MyApplication.config.brightnessLevel);
        else {
            brightness = ReflectUtil.invoke_get_bright();
        }
        displaySettingsBinding.txtSharpnessPercent.setText(String.valueOf(brightness + 1));
    }

    private void updateContrast(boolean set) {
        if (set) {
            pqControl.factorySetBasicControl(0xFF, picture_mode_values[curPosition], PQControl.PQ_BASIC_CONTRAST, mCurContrast);
            curPosition = picture_mode_choices.length - 1;
            displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
        displaySettingsBinding.txtContrastPercent.setText("" + mCurContrast);
    }

    private void updateSaturation(boolean set) {
        if (set) {
            pqControl.factorySetBasicControl(0xFF, picture_mode_values[curPosition], PQControl.PQ_BASIC_SATURATION, mCurSaturation);
            curPosition = picture_mode_choices.length - 1;
            displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
        displaySettingsBinding.txtSaturationPercent.setText("" + mCurSaturation);

    }

    private void updateHue(boolean set) {
        if (set) {
            pqControl.factorySetBasicControl(0xFF, picture_mode_values[curPosition], PQControl.PQ_BASIC_HUE, mCurHue);
            curPosition = picture_mode_choices.length - 1;
            displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
        displaySettingsBinding.txtHuePercent.setText("" + mCurHue);
    }

    private void updateSharpness(boolean set) {
        if (set) {
            pqControl.factorySetBasicControl(0xFF, picture_mode_values[curPosition], PQControl.PQ_BASIC_SHARPNESS, mSharpness);
            curPosition = picture_mode_choices.length - 1;
            displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
        displaySettingsBinding.txtSharpnessPercent.setText("" + mSharpness);
    }

    private void updatePictureMode() {
        updateDisplayStatus();
        brightness_system = pqControl.getBasicControl(PQControl.PQ_BASIC_BRIGHTNESS);
        mCurSaturation = pqControl.getBasicControl(PQControl.PQ_BASIC_SATURATION);
        mCurContrast = pqControl.getBasicControl(PQControl.PQ_BASIC_CONTRAST);
        mCurHue = pqControl.getBasicControl(PQControl.PQ_BASIC_HUE);
        mSharpness = pqControl.getBasicControl(PQControl.PQ_BASIC_SHARPNESS);

        mColorTemp = pqControl.getColorTemperature();
        int[] mRGBInfo = pqControl.factoryGetWBInfo(mColorTemp);
        mR = mRGBInfo[PQControl.GAIN_R];
        mG = mRGBInfo[PQControl.GAIN_G];
        mB = mRGBInfo[PQControl.GAIN_B];

        getBrightness();
        updateBrightnessSystem(false);
        updateContrast(false);
        updateHue(false);
        updateSaturation(false);
        updateSharpness(false);
    }

    private int getSettingModeValue() {
        int value = 0;
        String tempval = null;
        if (mAudioManagerEx == null) {
            mAudioManagerEx = new AudioManagerEx(mContext);
        }
        LogUtils.d(TAG, "getSettingIntValue ");
        tempval = mAudioManagerEx.getAudioParameters(AudioSettingParams.SW_PEQ_PRE_MODE);
        LogUtils.d(TAG, "getSettingIntValue get value: " + tempval);
        if (tempval != null && tempval.length() > 0) {
            value = Integer.parseInt(tempval);
        }
        return value;
    }

    private void updateAllEQValue() {
        value_100hz = getPEQIntValue(KEY_BQ_1);
        value_500hz = getPEQIntValue(KEY_BQ_2);
        value_2khz = getPEQIntValue(KEY_BQ_3);
        value_4khz = getPEQIntValue(KEY_BQ_4);
        value_6khz = getPEQIntValue(KEY_BQ_5);
        value_8khz = getPEQIntValue(KEY_BQ_6);
        value_10khz = getPEQIntValue(KEY_BQ_7);
        value_12khz = getPEQIntValue(KEY_BQ_8);
        value_14khz = getPEQIntValue(KEY_BQ_9);
        value_18khz = getPEQIntValue(KEY_BQ_10);
        displaySettingsBinding.tv100hz.setText("" + value_100hz);
        displaySettingsBinding.tv500hz.setText("" + value_500hz);
        displaySettingsBinding.tv2khz.setText("" + value_2khz);
        displaySettingsBinding.tv4khz.setText("" + value_4khz);
        displaySettingsBinding.tv6khz.setText("" + value_6khz);
        displaySettingsBinding.tv8khz.setText("" + value_8khz);
        displaySettingsBinding.tv10khz.setText("" + value_10khz);
        displaySettingsBinding.tv12khz.setText("" + value_12khz);
        displaySettingsBinding.tv14khz.setText("" + value_14khz);
        displaySettingsBinding.tv18khz.setText("" + value_18khz);
    }

    private int getPEQIntValue(String key) {
        int value = 0;
        int band = 0;
        LogUtils.d(TAG, "getSettingIntValue " + key);
        switch (key) {
            case KEY_BQ_1:
                band = 1;
                break;
            case KEY_BQ_2:
                band = 2;
                break;
            case KEY_BQ_3:
                band = 3;
                break;
            case KEY_BQ_4:
                band = 4;
                break;
            case KEY_BQ_5:
                band = 5;
                break;
            case KEY_BQ_6:
                band = 6;
                break;
            case KEY_BQ_7:
                band = 7;
                break;
            case KEY_BQ_8:
                band = 8;
                break;
            case KEY_BQ_9:
                band = 9;
                break;
            case KEY_BQ_10:
                band = 10;
                break;
            default:
                return -1;
        }
        value = getAudioPEQValue(band);
        return value;
    }

    private int getAudioPEQValue(int band) {
        try {
            String tempval = null;
            int value = 0;
            if (mAudioManagerEx == null) {
                mAudioManagerEx = new AudioManagerEx(mContext);
            }
            tempval = mAudioManagerEx.getAudioParameters(AudioSettingParams.SW_PEQ_GAIN);
            LogUtils.d(TAG, "getAudioPEQValue: " + tempval);
            if (tempval != null && tempval.length() > 0) {
                String[] tempArray = tempval.split(",");
                value = Integer.parseInt(tempArray[band - 1]);
            }
            LogUtils.d(TAG, "getAudioPEQValue band=" + band + ", value=" + value);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void updateAudioStatus() {
        if (sound_mode == 4) {
            displaySettingsBinding.audioModeScrollView.setFocusable(true);
            displaySettingsBinding.rl100hz.setEnabled(true);
            displaySettingsBinding.right100.setEnabled(true);
            displaySettingsBinding.left100.setEnabled(true);
            displaySettingsBinding.rl500hz.setEnabled(true);
            displaySettingsBinding.right500.setEnabled(true);
            displaySettingsBinding.left500.setEnabled(true);
            displaySettingsBinding.rl2khz.setEnabled(true);
            displaySettingsBinding.right2k.setEnabled(true);
            displaySettingsBinding.left2k.setEnabled(true);
            displaySettingsBinding.rl4khz.setEnabled(true);
            displaySettingsBinding.right4k.setEnabled(true);
            displaySettingsBinding.left4k.setEnabled(true);
            displaySettingsBinding.rl6khz.setEnabled(true);
            displaySettingsBinding.right6k.setEnabled(true);
            displaySettingsBinding.left6k.setEnabled(true);
            displaySettingsBinding.rl8khz.setEnabled(true);
            displaySettingsBinding.right8k.setEnabled(true);
            displaySettingsBinding.left8k.setEnabled(true);
            displaySettingsBinding.rl10khz.setEnabled(true);
            displaySettingsBinding.right10k.setEnabled(true);
            displaySettingsBinding.left10k.setEnabled(true);
            displaySettingsBinding.rl12khz.setEnabled(true);
            displaySettingsBinding.right12k.setEnabled(true);
            displaySettingsBinding.left12k.setEnabled(true);
            displaySettingsBinding.rl14khz.setEnabled(true);
            displaySettingsBinding.right14k.setEnabled(true);
            displaySettingsBinding.left14k.setEnabled(true);
            displaySettingsBinding.rl18khz.setEnabled(true);
            displaySettingsBinding.right18k.setEnabled(true);
            displaySettingsBinding.left18k.setEnabled(true);

            displaySettingsBinding.rl100hz.setAlpha(1.0f);
            displaySettingsBinding.rl500hz.setAlpha(1.0f);
            displaySettingsBinding.rl2khz.setAlpha(1.0f);
            displaySettingsBinding.rl4khz.setAlpha(1.0f);
            displaySettingsBinding.rl6khz.setAlpha(1.0f);
            displaySettingsBinding.rl8khz.setAlpha(1.0f);
            displaySettingsBinding.rl10khz.setAlpha(1.0f);
            displaySettingsBinding.rl12khz.setAlpha(1.0f);
            displaySettingsBinding.rl14khz.setAlpha(1.0f);
            displaySettingsBinding.rl18khz.setAlpha(1.0f);
        } else {
            displaySettingsBinding.audioModeScrollView.setFocusable(false);
            displaySettingsBinding.rl100hz.setEnabled(false);
            displaySettingsBinding.right100.setEnabled(false);
            displaySettingsBinding.left100.setEnabled(false);
            displaySettingsBinding.rl500hz.setEnabled(false);
            displaySettingsBinding.right500.setEnabled(false);
            displaySettingsBinding.left500.setEnabled(false);
            displaySettingsBinding.rl2khz.setEnabled(false);
            displaySettingsBinding.right2k.setEnabled(false);
            displaySettingsBinding.left2k.setEnabled(false);
            displaySettingsBinding.rl4khz.setEnabled(false);
            displaySettingsBinding.right4k.setEnabled(false);
            displaySettingsBinding.left4k.setEnabled(false);
            displaySettingsBinding.rl6khz.setEnabled(false);
            displaySettingsBinding.right6k.setEnabled(false);
            displaySettingsBinding.left6k.setEnabled(false);
            displaySettingsBinding.rl8khz.setEnabled(false);
            displaySettingsBinding.right8k.setEnabled(false);
            displaySettingsBinding.left8k.setEnabled(false);
            displaySettingsBinding.rl10khz.setEnabled(false);
            displaySettingsBinding.right10k.setEnabled(false);
            displaySettingsBinding.left10k.setEnabled(false);
            displaySettingsBinding.rl12khz.setEnabled(false);
            displaySettingsBinding.right12k.setEnabled(false);
            displaySettingsBinding.left12k.setEnabled(false);
            displaySettingsBinding.rl14khz.setEnabled(false);
            displaySettingsBinding.right14k.setEnabled(false);
            displaySettingsBinding.left14k.setEnabled(false);
            displaySettingsBinding.rl18khz.setEnabled(false);
            displaySettingsBinding.right18k.setEnabled(false);
            displaySettingsBinding.left18k.setEnabled(false);

            displaySettingsBinding.rl100hz.setAlpha(0.7f);
            displaySettingsBinding.rl500hz.setAlpha(0.7f);
            displaySettingsBinding.rl2khz.setAlpha(0.7f);
            displaySettingsBinding.rl4khz.setAlpha(0.7f);
            displaySettingsBinding.rl6khz.setAlpha(0.7f);
            displaySettingsBinding.rl8khz.setAlpha(0.7f);
            displaySettingsBinding.rl10khz.setAlpha(0.7f);
            displaySettingsBinding.rl12khz.setAlpha(0.7f);
            displaySettingsBinding.rl14khz.setAlpha(0.7f);
            displaySettingsBinding.rl18khz.setAlpha(0.7f);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        if (hasFocus) {
            if (id == R.id.change_mode) {
                displaySettingsBinding.title.setSelected(true);
            } else if (id == R.id.rl_picture_mode) {
                displaySettingsBinding.pictureMode.setSelected(true);
                displaySettingsBinding.pictureModeTv.setSelected(true);
            } else if (id == R.id.rl_brightness) {
                displaySettingsBinding.txtBrightness.setSelected(true);
            } else if (id == R.id.rl_contrast) {
                displaySettingsBinding.txtContrast.setSelected(true);
            } else if (id == R.id.rl_hue) {
                displaySettingsBinding.txtHue.setSelected(true);
            } else if (id == R.id.rl_saturation) {
                displaySettingsBinding.txtSaturation.setSelected(true);
            } else if (id == R.id.rl_sharpness) {
                displaySettingsBinding.txtSharpness.setSelected(true);
            } else if (id == R.id.rl_audio_mode) {
                displaySettingsBinding.audioMode.setSelected(true);
                displaySettingsBinding.audioModeTv.setSelected(true);
            }
        } else {
            if (id == R.id.change_mode) {
                displaySettingsBinding.title.setSelected(false);
            } else if (id == R.id.rl_picture_mode) {
                displaySettingsBinding.pictureMode.setSelected(false);
                displaySettingsBinding.pictureModeTv.setSelected(false);
            } else if (id == R.id.rl_brightness) {
                displaySettingsBinding.txtBrightness.setSelected(false);
            } else if (id == R.id.rl_contrast) {
                displaySettingsBinding.txtContrast.setSelected(false);
            } else if (id == R.id.rl_hue) {
                displaySettingsBinding.txtHue.setSelected(false);
            } else if (id == R.id.rl_saturation) {
                displaySettingsBinding.txtSaturation.setSelected(false);
            } else if (id == R.id.rl_sharpness) {
                displaySettingsBinding.txtSharpness.setSelected(false);
            } else if (id == R.id.rl_audio_mode) {
                displaySettingsBinding.audioMode.setSelected(false);
                displaySettingsBinding.audioModeTv.setSelected(false);
            }
        }
    }

    public int updateSettingIntValue(String key, int value) {
        int band = 0;
        LogUtils.d(TAG, "updateSettingValue key=" + key + " value=" + value);
        switch (key) {
            case KEY_BQ_1:
                band = 1;
                break;
            case KEY_BQ_2:
                band = 2;
                break;
            case KEY_BQ_3:
                band = 3;
                break;
            case KEY_BQ_4:
                band = 4;
                break;
            case KEY_BQ_5:
                band = 5;
                break;
            case KEY_BQ_6:
                band = 6;
                break;
            case KEY_BQ_7:
                band = 7;
                break;
            case KEY_BQ_8:
                band = 8;
                break;
            case KEY_BQ_9:
                band = 9;
                break;
            case KEY_BQ_10:
                band = 10;
                break;
            default:
                return -1;
        }
        setAudioPEQValue(band, value);
        return 0;
    }

    private int setAudioPEQValue(int band, int value) {
        if (mAudioManagerEx == null) {
            mAudioManagerEx = new AudioManagerEx(mContext);
        }
        String bandval = Integer.toString(band) + ":" + value;
        mAudioManagerEx.setAudioParameters(AudioSettingParams.SW_PEQ_GAIN, bandval);
        LogUtils.d(TAG, "setAudioPEQValue item: band:value -> " + bandval);
        return 0;
    }

    public int updateSettingModeValue(int value) {
        if (mAudioManagerEx == null) {
            mAudioManagerEx = new AudioManagerEx(mContext);
        }
        LogUtils.d(TAG, "updateSettingModeValue value " + value);
        mAudioManagerEx.setAudioParameters(AudioSettingParams.SW_PEQ_PRE_MODE, Integer.toString(value));
        return 0;
    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        int what = event.getAction();
        switch (what) {
            case MotionEvent.ACTION_HOVER_ENTER: // 鼠标进入view
                v.requestFocus();
                break;
            case MotionEvent.ACTION_HOVER_MOVE: // 鼠标在view上
                break;
            case MotionEvent.ACTION_HOVER_EXIT: // 鼠标离开view
                break;
        }
        return false;
    }
}