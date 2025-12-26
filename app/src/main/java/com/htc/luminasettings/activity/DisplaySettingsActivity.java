package com.htc.luminasettings.activity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.htc.luminasettings.MyApplication;
import com.htc.luminasettings.R;
import com.htc.luminasettings.activity.BaseActivity;
import com.htc.luminasettings.databinding.ActivityDisplaySettingsBinding;
import com.htc.luminasettings.utils.AddViewToScreen;
import com.htc.luminasettings.utils.LogUtils;
import com.htc.luminasettings.utils.ReflectUtil;
import com.softwinner.PQControl;
import com.softwinner.tv.AwTvDisplayManager;

public class DisplaySettingsActivity extends BaseActivity implements View.OnKeyListener {

    ActivityDisplaySettingsBinding displaySettingsBinding;
    private Context mContext;
    private static String TAG = "DisplaySettingsActivity";
    private AddViewToScreen mavts = new AddViewToScreen();
    public WindowManager.LayoutParams lp;
//    private Dialog dialog;
//    private Window dialogWindow;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mavts.setContext(getApplicationContext());
        initLayoutParams();
        initView();
        if (MyApplication.config.displayPictureModeShowCustom) {
            picture_mode_values = getResources().getStringArray(R.array.picture_mode_values);
            picture_mode_choices = getResources().getStringArray(MyApplication.config.displayPictureModeWeiMiTitle ?
                    R.array.picture_mode_weimi_choices : R.array.picture_mode_choices);
        } else {
            picture_mode_values = getResources().getStringArray(R.array.picture_mode_values_no_custom);
            picture_mode_choices = getResources().getStringArray(MyApplication.config.displayPictureModeWeiMiTitle ?
                    R.array.picture_mode_weimi_choices_no_custom : R.array.picture_mode_choices_no_custom);
        }
        awTvDisplayManager = AwTvDisplayManager.getInstance();
        pqControl = new PQControl();
//        initWindow();
//        initLp();
//        dialog.show();
        mavts.addView(displaySettingsBinding.getRoot(),lp);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
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
        lp.width = (int)getResources().getDimension(R.dimen.x_400);
        lp.height = (int)getResources().getDimension(R.dimen.y_640);
        lp.gravity = Gravity.START;
        lp.x = (int)getResources().getDimension(R.dimen.x_27);
    }

    private void initView() {
        displaySettingsBinding = ActivityDisplaySettingsBinding.inflate(LayoutInflater.from(this));
//        dialog = new Dialog(this,R.style.BlurDialogTheme);
//        dialog.setContentView(displaySettingsBinding.getRoot());
//        dialogWindow = dialog.getWindow();

        displaySettingsBinding.rlPictureMode.setOnClickListener(this);
//        displaySettingsBinding.rlColorTemp.setOnClickListener(this);
        displaySettingsBinding.rlBrightness.setOnClickListener(this);
        displaySettingsBinding.rlContrast.setOnClickListener(this);
        displaySettingsBinding.rlHue.setOnClickListener(this);
        displaySettingsBinding.rlSaturation.setOnClickListener(this);
        displaySettingsBinding.rlSharpness.setOnClickListener(this);
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


        displaySettingsBinding.rlPictureMode.setOnKeyListener(this);
//        displaySettingsBinding.rlColorTemp.setOnKeyListener(this);
        displaySettingsBinding.rlBrightness.setOnKeyListener(this);
        displaySettingsBinding.rlContrast.setOnKeyListener(this);
        displaySettingsBinding.rlHue.setOnKeyListener(this);
        displaySettingsBinding.rlSaturation.setOnKeyListener(this);
        displaySettingsBinding.rlSharpness.setOnKeyListener(this);

        displaySettingsBinding.rlPictureMode.setOnHoverListener(this);
//        displaySettingsBinding.rlColorTemp.setOnHoverListener(this);
        displaySettingsBinding.rlBrightness.setOnHoverListener(this);
        displaySettingsBinding.rlContrast.setOnHoverListener(this);
        displaySettingsBinding.rlHue.setOnHoverListener(this);
        displaySettingsBinding.rlSaturation.setOnHoverListener(this);
        displaySettingsBinding.rlSharpness.setOnHoverListener(this);

        displaySettingsBinding.rlPictureMode.setVisibility(MyApplication.config.displayPictureMode ? View.VISIBLE : View.GONE);
//        displaySettingsBinding.rlColorTemp.setVisibility(MyApplication.config.displayColorTemp ? View.VISIBLE : View.GONE);
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
    }

//    private void initWindow() {
//        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
//        dialogWindow.setDimAmount(0f);
//        dialogWindow.setWindowAnimations(R.style.left_in_left_out_anim);
//    }
//
//    private void initLp() {
//        WindowManager.LayoutParams wmlp =dialogWindow.getAttributes();
//        wmlp.width = (int) mContext.getResources().getDimension(R.dimen.x_400);
//        wmlp.height = (int) mContext.getResources().getDimension(R.dimen.x_640);
//        wmlp.x = (int) getResources().getDimension(R.dimen.x_27);
//        wmlp.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
//        dialogWindow.setAttributes(wmlp);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            dialogWindow.setBackgroundBlurRadius(50);//adb命令 wm disable-blur 查看系统是否支持
//        }
//    }

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
//        sound_mode = tvAudioControl.getAudioMode();
//        activityPictureModeBinding.audioModeTv.setText(soundMode_name[sound_mode]);

        brightness_system = pqControl.getBasicControl(PQControl.PQ_BASIC_BRIGHTNESS);
        mCurContrast = pqControl.getBasicControl(PQControl.PQ_BASIC_CONTRAST);
        mCurSaturation = pqControl.getBasicControl(PQControl.PQ_BASIC_SATURATION);
        mCurHue = pqControl.getBasicControl(PQControl.PQ_BASIC_HUE);
        mSharpness = pqControl.getBasicControl(PQControl.PQ_BASIC_SHARPNESS);

//        mColorTemp = pqControl.getColorTemperature();
//        activityPictureModeBinding.colorTempTv.setText(colorTemp_name[mColorTemp]);
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

//        updateR(false);
//        updateG(false);
//        updateB(false);
//        getPictureModeImage();

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
//        if ((System.currentTimeMillis() - cur_time) < 100 && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
//            return true;
//        }
//        if (event.getAction() == KeyEvent.ACTION_DOWN)
//            cur_time = System.currentTimeMillis();

        if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT )) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            dialog.dismiss();
            mavts.clearView(displaySettingsBinding.main);
            finish();
        }
        AudioManager audioManager = (AudioManager) v.getContext().getSystemService(Context.AUDIO_SERVICE);
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
            int id = v.getId();
            if (id == R.id.rl_picture_mode) {
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
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN) {
            int id = v.getId();
            if (id == R.id.rl_color_temp) {
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
            }
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_picture_mode) {
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
        }
    }

    private void updateDisplayStatus() {

        if (curPosition == picture_mode_choices.length - 1) {
//            displaySettingsBinding.scrollImage.setFocusable(true);

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
//            displaySettingsBinding.scrollImage.setFocusable(false);

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
//        activityPictureModeBinding.sbBrightness.setProgress(brightness_system);
        displaySettingsBinding.txtBrightnessParcent.setText(String.valueOf(brightness_system));
    }

    private void getBrightness() {

//        activityPictureModeBinding.sbBrightness.setMax(MyApplication.config.brightnessLevel);
        if (MyApplication.config.brightnessLevel == 1 || MyApplication.config.brightnessLevel == 2)
            brightness = ReflectUtil.invoke_get_bright() - (3 - MyApplication.config.brightnessLevel);
        else {
            brightness = ReflectUtil.invoke_get_bright();
        }
//        activityPictureModeBinding.sbBrightness.setProgress(brightness);
        displaySettingsBinding.txtSharpnessPercent.setText(String.valueOf(brightness + 1));
    }

    private void updateContrast(boolean set) {

        if (set) {
            pqControl.factorySetBasicControl(0xFF, picture_mode_values[curPosition], PQControl.PQ_BASIC_CONTRAST, mCurContrast);
            curPosition = picture_mode_choices.length - 1;
            displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
//        activityPictureModeBinding.sbContrast.setProgress(mCurContrast);
        displaySettingsBinding.txtContrastPercent.setText("" + mCurContrast);
    }

    private void updateSaturation(boolean set) {
        if (set) {
            pqControl.factorySetBasicControl(0xFF, picture_mode_values[curPosition], PQControl.PQ_BASIC_SATURATION, mCurSaturation);
            curPosition = picture_mode_choices.length - 1;
            displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
//        activityPictureModeBinding.sbSaturation.setProgress(mCurSaturation);
        displaySettingsBinding.txtSaturationPercent.setText("" + mCurSaturation);

    }

    private void updateHue(boolean set) {
        if (set) {
            pqControl.factorySetBasicControl(0xFF, picture_mode_values[curPosition], PQControl.PQ_BASIC_HUE, mCurHue);
            curPosition = picture_mode_choices.length - 1;
            displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
//        activityPictureModeBinding.sbHue.setProgress(mCurHue);
        displaySettingsBinding.txtHuePercent.setText("" + mCurHue);
    }

    private void updateSharpness(boolean set) {
        if (set) {
            pqControl.factorySetBasicControl(0xFF, picture_mode_values[curPosition], PQControl.PQ_BASIC_SHARPNESS, mSharpness);
            curPosition = picture_mode_choices.length - 1;
            displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
//        activityPictureModeBinding.sbSharpness.setProgress(mSharpness);
        displaySettingsBinding.txtSharpnessPercent.setText("" + mSharpness);
    }

    private void updatePictureMode() {
        // brightness = pqControl.getBasicControl(PQControl.PQ_BASIC_BRIGHTNESS);
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

//        updateR(false);
//        updateG(false);
//        updateB(false);
    }

    //色温
//    private void updateColorTemp(int colorTemp) {
//        //pqControl.setColorTemperature(colorTemp);
//        pqControl.factorySetColorTemperature(0xFF, "0xFF", colorTemp);
//        displaySettingsBinding.colorTempTv.setText(colorTemp_name[colorTemp]);
//        int[] mRGBInfo = pqControl.factoryGetWBInfo(mColorTemp);
//        mR = mRGBInfo[PQControl.GAIN_R];
//        mG = mRGBInfo[PQControl.GAIN_G];
//        mB = mRGBInfo[PQControl.GAIN_B];
////        updateR(false);
////        updateG(false);
////        updateB(false);
//    }

}