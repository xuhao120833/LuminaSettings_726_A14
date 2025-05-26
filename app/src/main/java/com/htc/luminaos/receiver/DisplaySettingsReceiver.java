package com.htc.luminaos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
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
import com.htc.luminaos.utils.ReflectUtil;
import com.softwinner.PQControl;
import com.softwinner.tv.AwTvDisplayManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DisplaySettingsReceiver extends BroadcastReceiver implements View.OnClickListener, View.OnKeyListener, View.OnHoverListener {
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
    }

    DisplaySettingsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG," 收到DisplaySettings的广播 ");
        String action = intent.getAction();
        if (action.equals(DisplayAction)) {
            try {
                initData();
                boolean show = intent.getBooleanExtra("show", false);
                if (show && !displaySettingsBinding.getRoot().isAttachedToWindow()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String currentTime = sdf.format(new Date());
                    Log.d(TAG, "mavts.addView " + currentTime);
                    mavts.addView(displaySettingsBinding.getRoot(), lp);
                } else if (!show && displaySettingsBinding.getRoot().isAttachedToWindow()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String currentTime = sdf.format(new Date());
                    Log.d(TAG, "mavts.clearView " + currentTime);
                    mavts.clearView(displaySettingsBinding.getRoot());
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


        displaySettingsBinding.rlPictureMode.setOnKeyListener(this);
        displaySettingsBinding.rlBrightness.setOnKeyListener(this);
        displaySettingsBinding.rlContrast.setOnKeyListener(this);
        displaySettingsBinding.rlHue.setOnKeyListener(this);
        displaySettingsBinding.rlSaturation.setOnKeyListener(this);
        displaySettingsBinding.rlSharpness.setOnKeyListener(this);

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
    }

    private void initData() {
        String pictureName = pqControl.getPictureModeName();
        Log.d(TAG, "pictureName " + pictureName);
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
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            mavts.clearView(displaySettingsBinding.main);
            return true;
        }
        if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
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
                Log.d(TAG, "饱和度 向左");
                if (mCurSaturation == 1) {
                    Log.d(TAG, "饱和度 向左不执行");
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
                Log.d(TAG, "锐度 向右");
                if (mSharpness == 100) {
                    Log.d(TAG, "锐度 向右不执行");
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
            Log.d(TAG, "饱和度 向左");
            if (mCurSaturation == 1) {
                Log.d(TAG, "饱和度 向左不执行");
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
            Log.d(TAG, "锐度 向右");
            if (mSharpness == 100) {
                Log.d(TAG, "锐度 向右不执行");
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
            Log.d(TAG, "锐度 向右");
            if (mSharpness == 100) {
                Log.d(TAG, "锐度 向右不执行");
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

    @Override
    public boolean onHover(View v, MotionEvent event) {
        return false;
    }
}