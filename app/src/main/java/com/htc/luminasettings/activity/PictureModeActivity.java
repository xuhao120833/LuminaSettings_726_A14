package com.htc.luminasettings.activity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.htc.luminasettings.MyApplication;
import com.htc.luminasettings.R;
import com.htc.luminasettings.activity.BaseActivity;
import com.htc.luminasettings.databinding.ActivityBluetoothBinding;
import com.htc.luminasettings.databinding.ActivityPictureModeBinding;
import com.htc.luminasettings.utils.LogUtils;
import com.htc.luminasettings.utils.ReflectUtil;
import com.softwinner.PQControl;
import com.softwinner.tv.AwTvDisplayManager;

public class PictureModeActivity extends BaseActivity implements View.OnKeyListener {

    ActivityPictureModeBinding activityPictureModeBinding;

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

    private String[] colorTemp_name;

    private static String TAG = "PictureModeActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPictureModeBinding = ActivityPictureModeBinding.inflate(LayoutInflater.from(this));
        setContentView(activityPictureModeBinding.getRoot());
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
        colorTemp_name = getResources().getStringArray(R.array.picture_mode_weimi_choices_no_custom);
        //        soundMode_name = getResources().getStringArray(R.array.soundMode_name);
        //        tvAudioControl = new TvAudioControl(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    private void initView() {
        activityPictureModeBinding.rlPictureMode.setOnClickListener(this);
        activityPictureModeBinding.rlColorTemp.setOnClickListener(this);
        activityPictureModeBinding.rlBrightness.setOnClickListener(this);
        activityPictureModeBinding.rlContrast.setOnClickListener(this);
        activityPictureModeBinding.rlHue.setOnClickListener(this);
        activityPictureModeBinding.rlSaturation.setOnClickListener(this);
        activityPictureModeBinding.rlSharpness.setOnClickListener(this);
        activityPictureModeBinding.brightnessLeft.setOnClickListener(this);
        activityPictureModeBinding.brightnessRight.setOnClickListener(this);
        activityPictureModeBinding.contrastLeft.setOnClickListener(this);
        activityPictureModeBinding.contrastRight.setOnClickListener(this);
        activityPictureModeBinding.hueLeft.setOnClickListener(this);
        activityPictureModeBinding.hueRight.setOnClickListener(this);
        activityPictureModeBinding.saturationLeft.setOnClickListener(this);
        activityPictureModeBinding.saturationRight.setOnClickListener(this);
        activityPictureModeBinding.sharpnessLeft.setOnClickListener(this);
        activityPictureModeBinding.sharpnessRight.setOnClickListener(this);


        activityPictureModeBinding.rlPictureMode.setOnKeyListener(this);
        activityPictureModeBinding.rlColorTemp.setOnKeyListener(this);
        activityPictureModeBinding.rlBrightness.setOnKeyListener(this);
        activityPictureModeBinding.rlContrast.setOnKeyListener(this);
        activityPictureModeBinding.rlHue.setOnKeyListener(this);
        activityPictureModeBinding.rlSaturation.setOnKeyListener(this);
        activityPictureModeBinding.rlSharpness.setOnKeyListener(this);

        activityPictureModeBinding.rlPictureMode.setOnHoverListener(this);
        activityPictureModeBinding.rlColorTemp.setOnHoverListener(this);
        activityPictureModeBinding.rlBrightness.setOnHoverListener(this);
        activityPictureModeBinding.rlContrast.setOnHoverListener(this);
        activityPictureModeBinding.rlHue.setOnHoverListener(this);
        activityPictureModeBinding.rlSaturation.setOnHoverListener(this);
        activityPictureModeBinding.rlSharpness.setOnHoverListener(this);

        activityPictureModeBinding.rlPictureMode.setVisibility(MyApplication.config.displayPictureMode ? View.VISIBLE : View.GONE);
//        activityPictureModeBinding.rlColorTemp.setVisibility(MyApplication.config.displayColorTemp ? View.VISIBLE : View.GONE);
        activityPictureModeBinding.rlBrightness.setVisibility(MyApplication.config.brightnessPQ ? View.VISIBLE : View.GONE);
        activityPictureModeBinding.rlContrast.setVisibility(MyApplication.config.contrast ? View.VISIBLE : View.GONE);
        activityPictureModeBinding.rlHue.setVisibility(MyApplication.config.hue ? View.VISIBLE : View.GONE);
        activityPictureModeBinding.rlSaturation.setVisibility(MyApplication.config.saturation ? View.VISIBLE : View.GONE);
        activityPictureModeBinding.rlSharpness.setVisibility(MyApplication.config.sharpness ? View.VISIBLE : View.GONE);
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
        activityPictureModeBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        updateDisplayStatus();
//        sound_mode = tvAudioControl.getAudioMode();
//        activityPictureModeBinding.audioModeTv.setText(soundMode_name[sound_mode]);

        brightness_system = pqControl.getBasicControl(PQControl.PQ_BASIC_BRIGHTNESS);
        mCurContrast = pqControl.getBasicControl(PQControl.PQ_BASIC_CONTRAST);
        mCurSaturation = pqControl.getBasicControl(PQControl.PQ_BASIC_SATURATION);
        mCurHue = pqControl.getBasicControl(PQControl.PQ_BASIC_HUE);
        mSharpness = pqControl.getBasicControl(PQControl.PQ_BASIC_SHARPNESS);

        mColorTemp = pqControl.getColorTemperature();
        activityPictureModeBinding.colorTempTv.setText(colorTemp_name[mColorTemp]);
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
            activityPictureModeBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        } else if (id == R.id.rl_color_temp) {
            if (mColorTemp==2){
                mColorTemp = 0;
            }else {
                mColorTemp +=1;
            }
            updateColorTemp(mColorTemp);
        }else if (id == R.id.rl_brightness) {
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

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((System.currentTimeMillis() - cur_time) < 100 && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
            return true;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN)
            cur_time = System.currentTimeMillis();

        if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT )) {
            return true;
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
                activityPictureModeBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
                if (audioManager != null) {
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                }
                audioManager = null;
//                    return true;
            } else if (id == R.id.rl_color_temp) {
                if (mColorTemp == 0) {
                    mColorTemp = colorTemp_name.length - 1;
                } else {
                    mColorTemp -= 1;
                }
                updateColorTemp(mColorTemp);
                if (audioManager != null) {
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                }
                audioManager = null;
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
                if (mColorTemp == colorTemp_name.length - 1) {
                    mColorTemp = 0;
                } else {
                    mColorTemp += 1;
                }
                updateColorTemp(mColorTemp);
                if (audioManager != null) {
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                }
                audioManager = null;
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
                activityPictureModeBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
                if (audioManager != null) {
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                }
                audioManager = null;
//                    return true;
            }
        }

        return false;
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

    private void updateDisplayStatus() {

        if (curPosition == picture_mode_choices.length - 1) {
            activityPictureModeBinding.scrollImage.setFocusable(true);

            activityPictureModeBinding.rlBrightness.setEnabled(true);
            activityPictureModeBinding.rlContrast.setEnabled(true);
            activityPictureModeBinding.rlHue.setEnabled(true);
            activityPictureModeBinding.rlSaturation.setEnabled(true);
            activityPictureModeBinding.rlSharpness.setEnabled(true);

            activityPictureModeBinding.brightnessLeft.setEnabled(true);
            activityPictureModeBinding.brightnessRight.setEnabled(true);
            activityPictureModeBinding.contrastLeft.setEnabled(true);
            activityPictureModeBinding.contrastRight.setEnabled(true);
            activityPictureModeBinding.hueLeft.setEnabled(true);
            activityPictureModeBinding.hueRight.setEnabled(true);
            activityPictureModeBinding.saturationLeft.setEnabled(true);
            activityPictureModeBinding.saturationRight.setEnabled(true);
            activityPictureModeBinding.sharpnessLeft.setEnabled(true);
            activityPictureModeBinding.sharpnessRight.setEnabled(true);

            activityPictureModeBinding.rlBrightness.setAlpha(1.0f);
            activityPictureModeBinding.rlContrast.setAlpha(1.0f);
            activityPictureModeBinding.rlHue.setAlpha(1.0f);
            activityPictureModeBinding.rlSaturation.setAlpha(1.0f);
            activityPictureModeBinding.rlSharpness.setAlpha(1.0f);
        } else {
            activityPictureModeBinding.rlBrightness.setEnabled(false);
            activityPictureModeBinding.rlContrast.setEnabled(false);
            activityPictureModeBinding.rlHue.setEnabled(false);
            activityPictureModeBinding.rlSaturation.setEnabled(false);
            activityPictureModeBinding.rlSharpness.setEnabled(false);
            activityPictureModeBinding.scrollImage.setFocusable(false);

            activityPictureModeBinding.brightnessLeft.setEnabled(false);
            activityPictureModeBinding.brightnessRight.setEnabled(false);
            activityPictureModeBinding.contrastLeft.setEnabled(false);
            activityPictureModeBinding.contrastRight.setEnabled(false);
            activityPictureModeBinding.hueLeft.setEnabled(false);
            activityPictureModeBinding.hueRight.setEnabled(false);
            activityPictureModeBinding.saturationLeft.setEnabled(false);
            activityPictureModeBinding.saturationRight.setEnabled(false);
            activityPictureModeBinding.sharpnessLeft.setEnabled(false);
            activityPictureModeBinding.sharpnessRight.setEnabled(false);

            activityPictureModeBinding.rlBrightness.setAlpha(0.7f);
            activityPictureModeBinding.rlContrast.setAlpha(0.7f);
            activityPictureModeBinding.rlHue.setAlpha(0.7f);
            activityPictureModeBinding.rlSaturation.setAlpha(0.7f);
            activityPictureModeBinding.rlSharpness.setAlpha(0.7f);
        }
    }

    private void getBrightness() {

//        activityPictureModeBinding.sbBrightness.setMax(MyApplication.config.brightnessLevel);
        if (MyApplication.config.brightnessLevel == 1 || MyApplication.config.brightnessLevel == 2)
            brightness = ReflectUtil.invoke_get_bright() - (3 - MyApplication.config.brightnessLevel);
        else {
            brightness = ReflectUtil.invoke_get_bright();
        }
//        activityPictureModeBinding.sbBrightness.setProgress(brightness);
        activityPictureModeBinding.txtSharpnessPercent.setText(String.valueOf(brightness + 1));
    }

    private void updateBrightness(boolean set) {
        if (set) {
            if (MyApplication.config.brightnessLevel == 1 || MyApplication.config.brightnessLevel == 2)
                ReflectUtil.invoke_set_bright(brightness + (3 - MyApplication.config.brightnessLevel));
            else
                ReflectUtil.invoke_set_bright(brightness);
        }
//        activityPictureModeBinding.sbBrightness.setProgress(brightness);
        activityPictureModeBinding.txtBrightnessParcent.setText(String.valueOf(brightness + 1));
    }

    private void updateBrightnessSystem(boolean set) {
        if (set) {
            pqControl.factorySetBasicControl(0xFF, picture_mode_values[curPosition], PQControl.PQ_BASIC_BRIGHTNESS, brightness_system);
            curPosition = picture_mode_choices.length - 1;
            activityPictureModeBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
//        activityPictureModeBinding.sbBrightness.setProgress(brightness_system);
        activityPictureModeBinding.txtBrightnessParcent.setText(String.valueOf(brightness_system));
    }

    private void updateContrast(boolean set) {

        if (set) {
            pqControl.factorySetBasicControl(0xFF, picture_mode_values[curPosition], PQControl.PQ_BASIC_CONTRAST, mCurContrast);
            curPosition = picture_mode_choices.length - 1;
            activityPictureModeBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
//        activityPictureModeBinding.sbContrast.setProgress(mCurContrast);
        activityPictureModeBinding.txtContrastPercent.setText("" + mCurContrast);
    }

    private void updateSaturation(boolean set) {
        if (set) {
            pqControl.factorySetBasicControl(0xFF, picture_mode_values[curPosition], PQControl.PQ_BASIC_SATURATION, mCurSaturation);
            curPosition = picture_mode_choices.length - 1;
            activityPictureModeBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
//        activityPictureModeBinding.sbSaturation.setProgress(mCurSaturation);
        activityPictureModeBinding.txtSaturationPercent.setText("" + mCurSaturation);

    }

    private void updateHue(boolean set) {
        if (set) {
            pqControl.factorySetBasicControl(0xFF, picture_mode_values[curPosition], PQControl.PQ_BASIC_HUE, mCurHue);
            curPosition = picture_mode_choices.length - 1;
            activityPictureModeBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
//        activityPictureModeBinding.sbHue.setProgress(mCurHue);
        activityPictureModeBinding.txtHuePercent.setText("" + mCurHue);
    }

    private void updateSharpness(boolean set) {
        if (set) {
            pqControl.factorySetBasicControl(0xFF, picture_mode_values[curPosition], PQControl.PQ_BASIC_SHARPNESS, mSharpness);
            curPosition = picture_mode_choices.length - 1;
            activityPictureModeBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
//        activityPictureModeBinding.sbSharpness.setProgress(mSharpness);
        activityPictureModeBinding.txtSharpnessPercent.setText("" + mSharpness);
    }

//    private void updateR(boolean set) {
//        if (set) {
//            pqControl.factorySetWBInfo(mColorTemp, PQControl.GAIN_R , mR);
//        }
//        activityPictureModeBinding.sbRed.setProgress(mR);
//        activityPictureModeBinding.redTv.setText("" + mR);
//    }

//    private void updateG(boolean set) {
//        if (set) {
//            pqControl.factorySetWBInfo(mColorTemp, PQControl.GAIN_G, mG);
//        }
//        activityPictureModeBinding.sbGreen.setProgress(mG);
//        activityPictureModeBinding.greenTv.setText("" + mG);
//    }

//    private void updateB(boolean set) {
//        if (set) {
//            pqControl.factorySetWBInfo(mColorTemp,PQControl.GAIN_B,mB);
//        }
//        activityPictureModeBinding.sbBlue.setProgress(mB);
//        activityPictureModeBinding.blueTv.setText("" + mB);
//    }

    private void updateColorTemp(int colorTemp) {
        //pqControl.setColorTemperature(colorTemp);
        pqControl.factorySetColorTemperature(0xFF, "0xFF", colorTemp);
        activityPictureModeBinding.colorTempTv.setText(colorTemp_name[colorTemp]);
        int[] mRGBInfo = pqControl.factoryGetWBInfo(mColorTemp);
        mR = mRGBInfo[PQControl.GAIN_R];
        mG = mRGBInfo[PQControl.GAIN_G];
        mB = mRGBInfo[PQControl.GAIN_B];
//        updateR(false);
//        updateG(false);
//        updateB(false);
    }

//    private void getPictureModeImage(){
//        File file = new File("/oem/picture_mode_image.png");
//        if (MyApplication.config.picture_mode_image && file.exists()){
//            Drawable drawable = BitmapDrawable.createFromPath(file.getAbsolutePath());
//            pictureModeLayoutBinding.pictureModeImage.setBackground(drawable);
//        }
//    }


}
