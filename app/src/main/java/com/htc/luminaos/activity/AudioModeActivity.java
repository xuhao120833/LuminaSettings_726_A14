package com.htc.luminaos.activity;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManagerEx;
import android.media.AudioSettingParams;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.htc.luminaos.MyApplication;
import com.htc.luminaos.R;
import com.htc.luminaos.databinding.ActivityAudioModeBinding;
import com.htc.luminaos.databinding.ActivityPictureModeBinding;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.ReflectUtil;
import com.htc.luminaos.utils.Utils;
import com.softwinner.PQControl;
import com.softwinner.TvAudioControl;
import com.softwinner.tv.AwTvAudioManager;
import com.softwinner.tv.AwTvDisplayManager;
import com.softwinner.tv.common.AwTvAudioTypes;

public class AudioModeActivity extends BaseActivity implements View.OnKeyListener, View.OnClickListener {

    ActivityAudioModeBinding activityAudioModeBinding;

    private long cur_time = 0;

    private int sound_mode = 0;//当前声音模式下标
    private String[] soundMode_name;
    TvAudioControl tvAudioControl;
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
    private int min = -100;
    private int max = 100;
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
    private AudioManagerEx mAudioManagerEx = null;
    private Handler handler = new Handler();
    private static String AUDIO_SFX_SYNC_FILE = "audio_sound_effects_sync_file";
    private static String TAG = "AudioModeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAudioModeBinding = ActivityAudioModeBinding.inflate(LayoutInflater.from(this));
        setContentView(activityAudioModeBinding.getRoot());
        initView();
        soundMode_name = getResources().getStringArray(R.array.soundMode_name);
        mAudioManagerEx = new AudioManagerEx(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (sound_mode != 4) {
            mAudioManagerEx.setAudioParameters(AUDIO_SFX_SYNC_FILE, "");
//        }
    }

    private void initView() {
        activityAudioModeBinding.rlAudioMode.setOnClickListener(this);
        activityAudioModeBinding.rlAudioMode.setOnKeyListener(this);
        activityAudioModeBinding.rlAudioMode.setOnHoverListener(this);
        activityAudioModeBinding.audioModeRight.setOnClickListener(this);
        activityAudioModeBinding.audioModeLeft.setOnClickListener(this);

        activityAudioModeBinding.rl100hz.setOnClickListener(this);
        activityAudioModeBinding.rl100hz.setOnKeyListener(this);
        activityAudioModeBinding.rl100hz.setOnHoverListener(this);
        activityAudioModeBinding.right100.setOnClickListener(this);
        activityAudioModeBinding.left100.setOnClickListener(this);

        activityAudioModeBinding.rl500hz.setOnClickListener(this);
        activityAudioModeBinding.rl500hz.setOnKeyListener(this);
        activityAudioModeBinding.rl500hz.setOnHoverListener(this);
        activityAudioModeBinding.right500.setOnClickListener(this);
        activityAudioModeBinding.left500.setOnClickListener(this);

        activityAudioModeBinding.rl2khz.setOnClickListener(this);
        activityAudioModeBinding.rl2khz.setOnKeyListener(this);
        activityAudioModeBinding.rl2khz.setOnHoverListener(this);
        activityAudioModeBinding.right2k.setOnClickListener(this);
        activityAudioModeBinding.left2k.setOnClickListener(this);

        activityAudioModeBinding.rl4khz.setOnClickListener(this);
        activityAudioModeBinding.rl4khz.setOnKeyListener(this);
        activityAudioModeBinding.rl4khz.setOnHoverListener(this);
        activityAudioModeBinding.right4k.setOnClickListener(this);
        activityAudioModeBinding.left4k.setOnClickListener(this);

        activityAudioModeBinding.rl6khz.setOnClickListener(this);
        activityAudioModeBinding.rl6khz.setOnKeyListener(this);
        activityAudioModeBinding.rl6khz.setOnHoverListener(this);
        activityAudioModeBinding.right6k.setOnClickListener(this);
        activityAudioModeBinding.left6k.setOnClickListener(this);

        activityAudioModeBinding.rl8khz.setOnClickListener(this);
        activityAudioModeBinding.rl8khz.setOnKeyListener(this);
        activityAudioModeBinding.rl8khz.setOnHoverListener(this);
        activityAudioModeBinding.right8k.setOnClickListener(this);
        activityAudioModeBinding.left8k.setOnClickListener(this);

        activityAudioModeBinding.rl10khz.setOnClickListener(this);
        activityAudioModeBinding.rl10khz.setOnKeyListener(this);
        activityAudioModeBinding.rl10khz.setOnHoverListener(this);
        activityAudioModeBinding.right10k.setOnClickListener(this);
        activityAudioModeBinding.left10k.setOnClickListener(this);

        activityAudioModeBinding.rl12khz.setOnClickListener(this);
        activityAudioModeBinding.rl12khz.setOnKeyListener(this);
        activityAudioModeBinding.rl12khz.setOnHoverListener(this);
        activityAudioModeBinding.right12k.setOnClickListener(this);
        activityAudioModeBinding.left12k.setOnClickListener(this);

        activityAudioModeBinding.rl14khz.setOnClickListener(this);
        activityAudioModeBinding.rl14khz.setOnKeyListener(this);
        activityAudioModeBinding.rl14khz.setOnHoverListener(this);
        activityAudioModeBinding.right14k.setOnClickListener(this);
        activityAudioModeBinding.left14k.setOnClickListener(this);

        activityAudioModeBinding.rl18khz.setOnClickListener(this);
        activityAudioModeBinding.rl18khz.setOnKeyListener(this);
        activityAudioModeBinding.rl18khz.setOnHoverListener(this);
        activityAudioModeBinding.right18k.setOnClickListener(this);
        activityAudioModeBinding.left18k.setOnClickListener(this);

        activityAudioModeBinding.rl100hz.setVisibility(MyApplication.config.Menu100HZ ? View.VISIBLE : View.GONE);
        activityAudioModeBinding.rl500hz.setVisibility(MyApplication.config.Menu500HZ ? View.VISIBLE : View.GONE);
        activityAudioModeBinding.rl2khz.setVisibility(MyApplication.config.Menu2KHZ ? View.VISIBLE : View.GONE);
        activityAudioModeBinding.rl4khz.setVisibility(MyApplication.config.Menu4KHZ ? View.VISIBLE : View.GONE);
        activityAudioModeBinding.rl6khz.setVisibility(MyApplication.config.Menu6KHZ ? View.VISIBLE : View.GONE);
        activityAudioModeBinding.rl8khz.setVisibility(MyApplication.config.Menu8KHZ ? View.VISIBLE : View.GONE);
        activityAudioModeBinding.rl10khz.setVisibility(MyApplication.config.Menu10KHZ ? View.VISIBLE : View.GONE);
        activityAudioModeBinding.rl12khz.setVisibility(MyApplication.config.Menu12KHZ ? View.VISIBLE : View.GONE);
        activityAudioModeBinding.rl14khz.setVisibility(MyApplication.config.Menu14KHZ ? View.VISIBLE : View.GONE);
        activityAudioModeBinding.rl18khz.setVisibility(MyApplication.config.Menu18KHZ ? View.VISIBLE : View.GONE);

    }

    private void initData() {
        sound_mode = getSettingModeValue();
        LogUtils.d(TAG, "initData sound_mode " + sound_mode);
        activityAudioModeBinding.audioModeTv.setText(soundMode_name[sound_mode]);
        updateAllEQValue();
        updateAudioStatus();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_audio_mode) {
            if (sound_mode == 4 && Utils.audio_change) {
                mAudioManagerEx.setAudioParameters(AUDIO_SFX_SYNC_FILE, "");
                Utils.audio_change = false;
            }
            sound_mode += 1;
            if (sound_mode == soundMode_name.length) {
                sound_mode = 0;
            }
            updateSettingModeValue(sound_mode);
            activityAudioModeBinding.audioModeTv.setText(soundMode_name[sound_mode]);
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
            activityAudioModeBinding.tv100hz.setText(String.valueOf(value_100hz));
            updateSettingIntValue(KEY_BQ_1, value_100hz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_500hz) {
            if (value_500hz == 100)
                return;
            value_500hz += 1;
            activityAudioModeBinding.tv500hz.setText(String.valueOf(value_500hz));
            updateSettingIntValue(KEY_BQ_2, value_500hz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_2khz) {
            if (value_2khz == 100)
                return;
            value_2khz += 1;
            activityAudioModeBinding.tv2khz.setText(String.valueOf(value_2khz));
            updateSettingIntValue(KEY_BQ_3, value_2khz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_4khz) {
            if (value_4khz == 100)
                return;
            value_4khz += 1;
            activityAudioModeBinding.tv4khz.setText(String.valueOf(value_4khz));
            updateSettingIntValue(KEY_BQ_4, value_4khz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_6khz) {
            if (value_6khz == 100)
                return;
            value_6khz += 1;
            activityAudioModeBinding.tv6khz.setText(String.valueOf(value_6khz));
            updateSettingIntValue(KEY_BQ_5, value_6khz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_8khz) {
            if (value_8khz == 100)
                return;
            value_8khz += 1;
            activityAudioModeBinding.tv8khz.setText(String.valueOf(value_8khz));
            updateSettingIntValue(KEY_BQ_6, value_8khz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_10khz) {
            if (value_10khz == 100)
                return;
            value_10khz += 1;
            activityAudioModeBinding.tv10khz.setText(String.valueOf(value_10khz));
            updateSettingIntValue(KEY_BQ_7, value_10khz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_12khz) {
            if (value_12khz == 100)
                return;
            value_12khz += 1;
            activityAudioModeBinding.tv12khz.setText(String.valueOf(value_12khz));
            updateSettingIntValue(KEY_BQ_8, value_12khz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_14khz) {
            if (value_14khz == 100)
                return;
            value_14khz += 1;
            activityAudioModeBinding.tv14khz.setText(String.valueOf(value_14khz));
            updateSettingIntValue(KEY_BQ_9, value_14khz);
            Utils.audio_change = true;
        } else if (id == R.id.rl_18khz) {
            if (value_18khz == 100)
                return;
            value_18khz += 1;
            activityAudioModeBinding.tv18khz.setText(String.valueOf(value_18khz));
            updateSettingIntValue(KEY_BQ_10, value_18khz);
            Utils.audio_change = true;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((System.currentTimeMillis() - cur_time) < 100 && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
            return true;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN)
            cur_time = System.currentTimeMillis();

        if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
            return true;
        }
        AudioManager audioManager = (AudioManager) v.getContext().getSystemService(Context.AUDIO_SERVICE);
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
            int id = v.getId();
            if (id == R.id.rl_audio_mode) {
                if (sound_mode == 4 && Utils.audio_change) {
                    mAudioManagerEx.setAudioParameters(AUDIO_SFX_SYNC_FILE, "");
                    Utils.audio_change = false;
                }
                sound_mode -= 1;
                if (sound_mode == -1) {
                    sound_mode = soundMode_name.length - 1;
                }
                updateSettingModeValue(sound_mode);
                activityAudioModeBinding.audioModeTv.setText(soundMode_name[sound_mode]);
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
                activityAudioModeBinding.tv100hz.setText(String.valueOf(value_100hz));
                updateSettingIntValue(KEY_BQ_1, value_100hz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_500hz) {
                if (value_500hz == -100)
                    return false;
                value_500hz -= 1;
                activityAudioModeBinding.tv500hz.setText(String.valueOf(value_500hz));
                updateSettingIntValue(KEY_BQ_2, value_500hz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_2khz) {
                if (value_2khz == -100)
                    return false;
                value_2khz -= 1;
                activityAudioModeBinding.tv2khz.setText(String.valueOf(value_2khz));
                updateSettingIntValue(KEY_BQ_3, value_2khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_4khz) {
                if (value_4khz == -100)
                    return false;
                value_4khz -= 1;
                activityAudioModeBinding.tv4khz.setText(String.valueOf(value_4khz));
                updateSettingIntValue(KEY_BQ_4, value_4khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_6khz) {
                if (value_6khz == -100)
                    return false;
                value_6khz -= 1;
                activityAudioModeBinding.tv6khz.setText(String.valueOf(value_6khz));
                updateSettingIntValue(KEY_BQ_5, value_6khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_8khz) {
                if (value_8khz == -100)
                    return false;
                value_8khz -= 1;
                activityAudioModeBinding.tv8khz.setText(String.valueOf(value_8khz));
                updateSettingIntValue(KEY_BQ_6, value_8khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_10khz) {
                if (value_10khz == -100)
                    return false;
                value_10khz -= 1;
                activityAudioModeBinding.tv10khz.setText(String.valueOf(value_10khz));
                updateSettingIntValue(KEY_BQ_7, value_10khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_12khz) {
                if (value_12khz == -100)
                    return false;
                value_12khz -= 1;
                activityAudioModeBinding.tv12khz.setText(String.valueOf(value_12khz));
                updateSettingIntValue(KEY_BQ_8, value_12khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_14khz) {
                if (value_14khz == -100)
                    return false;
                value_14khz -= 1;
                activityAudioModeBinding.tv14khz.setText(String.valueOf(value_14khz));
                updateSettingIntValue(KEY_BQ_9, value_14khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_18khz) {
                if (value_18khz == -100)
                    return false;
                value_18khz -= 1;
                activityAudioModeBinding.tv18khz.setText(String.valueOf(value_18khz));
                updateSettingIntValue(KEY_BQ_10, value_18khz);
                Utils.audio_change = true;
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN) {
            int id = v.getId();
            if (id == R.id.rl_audio_mode) {
                if (sound_mode == 4 && Utils.audio_change) {
                    mAudioManagerEx.setAudioParameters(AUDIO_SFX_SYNC_FILE, "");
                    Utils.audio_change = false;
                }
                sound_mode += 1;
                if (sound_mode == soundMode_name.length) {
                    sound_mode = 0;
                }
                updateSettingModeValue(sound_mode);
                activityAudioModeBinding.audioModeTv.setText(soundMode_name[sound_mode]);
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
                activityAudioModeBinding.tv100hz.setText(String.valueOf(value_100hz));
                updateSettingIntValue(KEY_BQ_1, value_100hz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_500hz) {
                if (value_500hz == 100)
                    return false;
                value_500hz += 1;
                activityAudioModeBinding.tv500hz.setText(String.valueOf(value_500hz));
                updateSettingIntValue(KEY_BQ_2, value_500hz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_2khz) {
                if (value_2khz == 100)
                    return false;
                value_2khz += 1;
                activityAudioModeBinding.tv2khz.setText(String.valueOf(value_2khz));
                updateSettingIntValue(KEY_BQ_3, value_2khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_4khz) {
                if (value_4khz == 100)
                    return false;
                value_4khz += 1;
                activityAudioModeBinding.tv4khz.setText(String.valueOf(value_4khz));
                updateSettingIntValue(KEY_BQ_4, value_4khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_6khz) {
                if (value_6khz == 100)
                    return false;
                value_6khz += 1;
                activityAudioModeBinding.tv6khz.setText(String.valueOf(value_6khz));
                updateSettingIntValue(KEY_BQ_5, value_6khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_8khz) {
                if (value_8khz == 100)
                    return false;
                value_8khz += 1;
                activityAudioModeBinding.tv8khz.setText(String.valueOf(value_8khz));
                updateSettingIntValue(KEY_BQ_6, value_8khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_10khz) {
                if (value_10khz == 100)
                    return false;
                value_10khz += 1;
                activityAudioModeBinding.tv10khz.setText(String.valueOf(value_10khz));
                updateSettingIntValue(KEY_BQ_7, value_10khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_12khz) {
                if (value_12khz == 100)
                    return false;
                value_12khz += 1;
                activityAudioModeBinding.tv12khz.setText(String.valueOf(value_12khz));
                updateSettingIntValue(KEY_BQ_8, value_12khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_14khz) {
                if (value_14khz == 100)
                    return false;
                value_14khz += 1;
                activityAudioModeBinding.tv14khz.setText(String.valueOf(value_14khz));
                updateSettingIntValue(KEY_BQ_9, value_14khz);
                Utils.audio_change = true;
                return true;
            } else if (id == R.id.rl_18khz) {
                if (value_18khz == 100)
                    return false;
                value_18khz += 1;
                activityAudioModeBinding.tv18khz.setText(String.valueOf(value_18khz));
                updateSettingIntValue(KEY_BQ_10, value_18khz);
                Utils.audio_change = true;
                return true;
            }
        }
        return false;
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
        activityAudioModeBinding.tv100hz.setText("" + value_100hz);
        activityAudioModeBinding.tv500hz.setText("" + value_500hz);
        activityAudioModeBinding.tv2khz.setText("" + value_2khz);
        activityAudioModeBinding.tv4khz.setText("" + value_4khz);
        activityAudioModeBinding.tv6khz.setText("" + value_6khz);
        activityAudioModeBinding.tv8khz.setText("" + value_8khz);
        activityAudioModeBinding.tv10khz.setText("" + value_10khz);
        activityAudioModeBinding.tv12khz.setText("" + value_12khz);
        activityAudioModeBinding.tv14khz.setText("" + value_14khz);
        activityAudioModeBinding.tv18khz.setText("" + value_18khz);
    }

    private void updateAudioStatus() {
        if (sound_mode == 4) {
            activityAudioModeBinding.scrollImage.setFocusable(true);
            activityAudioModeBinding.rl100hz.setEnabled(true);
            activityAudioModeBinding.right100.setEnabled(true);
            activityAudioModeBinding.left100.setEnabled(true);
            activityAudioModeBinding.rl500hz.setEnabled(true);
            activityAudioModeBinding.right500.setEnabled(true);
            activityAudioModeBinding.left500.setEnabled(true);
            activityAudioModeBinding.rl2khz.setEnabled(true);
            activityAudioModeBinding.right2k.setEnabled(true);
            activityAudioModeBinding.left2k.setEnabled(true);
            activityAudioModeBinding.rl4khz.setEnabled(true);
            activityAudioModeBinding.right4k.setEnabled(true);
            activityAudioModeBinding.left4k.setEnabled(true);
            activityAudioModeBinding.rl6khz.setEnabled(true);
            activityAudioModeBinding.right6k.setEnabled(true);
            activityAudioModeBinding.left6k.setEnabled(true);
            activityAudioModeBinding.rl8khz.setEnabled(true);
            activityAudioModeBinding.right8k.setEnabled(true);
            activityAudioModeBinding.left8k.setEnabled(true);
            activityAudioModeBinding.rl10khz.setEnabled(true);
            activityAudioModeBinding.right10k.setEnabled(true);
            activityAudioModeBinding.left10k.setEnabled(true);
            activityAudioModeBinding.rl12khz.setEnabled(true);
            activityAudioModeBinding.right12k.setEnabled(true);
            activityAudioModeBinding.left12k.setEnabled(true);
            activityAudioModeBinding.rl14khz.setEnabled(true);
            activityAudioModeBinding.right14k.setEnabled(true);
            activityAudioModeBinding.left14k.setEnabled(true);
            activityAudioModeBinding.rl18khz.setEnabled(true);
            activityAudioModeBinding.right18k.setEnabled(true);
            activityAudioModeBinding.left18k.setEnabled(true);

            activityAudioModeBinding.rl100hz.setAlpha(1.0f);
            activityAudioModeBinding.rl500hz.setAlpha(1.0f);
            activityAudioModeBinding.rl2khz.setAlpha(1.0f);
            activityAudioModeBinding.rl4khz.setAlpha(1.0f);
            activityAudioModeBinding.rl6khz.setAlpha(1.0f);
            activityAudioModeBinding.rl8khz.setAlpha(1.0f);
            activityAudioModeBinding.rl10khz.setAlpha(1.0f);
            activityAudioModeBinding.rl12khz.setAlpha(1.0f);
            activityAudioModeBinding.rl14khz.setAlpha(1.0f);
            activityAudioModeBinding.rl18khz.setAlpha(1.0f);
        } else {
            activityAudioModeBinding.scrollImage.setFocusable(false);
            activityAudioModeBinding.rl100hz.setEnabled(false);
            activityAudioModeBinding.right100.setEnabled(false);
            activityAudioModeBinding.left100.setEnabled(false);
            activityAudioModeBinding.rl500hz.setEnabled(false);
            activityAudioModeBinding.right500.setEnabled(false);
            activityAudioModeBinding.left500.setEnabled(false);
            activityAudioModeBinding.rl2khz.setEnabled(false);
            activityAudioModeBinding.right2k.setEnabled(false);
            activityAudioModeBinding.left2k.setEnabled(false);
            activityAudioModeBinding.rl4khz.setEnabled(false);
            activityAudioModeBinding.right4k.setEnabled(false);
            activityAudioModeBinding.left4k.setEnabled(false);
            activityAudioModeBinding.rl6khz.setEnabled(false);
            activityAudioModeBinding.right6k.setEnabled(false);
            activityAudioModeBinding.left6k.setEnabled(false);
            activityAudioModeBinding.rl8khz.setEnabled(false);
            activityAudioModeBinding.right8k.setEnabled(false);
            activityAudioModeBinding.left8k.setEnabled(false);
            activityAudioModeBinding.rl10khz.setEnabled(false);
            activityAudioModeBinding.right10k.setEnabled(false);
            activityAudioModeBinding.left10k.setEnabled(false);
            activityAudioModeBinding.rl12khz.setEnabled(false);
            activityAudioModeBinding.right12k.setEnabled(false);
            activityAudioModeBinding.left12k.setEnabled(false);
            activityAudioModeBinding.rl14khz.setEnabled(false);
            activityAudioModeBinding.right14k.setEnabled(false);
            activityAudioModeBinding.left14k.setEnabled(false);
            activityAudioModeBinding.rl18khz.setEnabled(false);
            activityAudioModeBinding.right18k.setEnabled(false);
            activityAudioModeBinding.left18k.setEnabled(false);

            activityAudioModeBinding.rl100hz.setAlpha(0.7f);
            activityAudioModeBinding.rl500hz.setAlpha(0.7f);
            activityAudioModeBinding.rl2khz.setAlpha(0.7f);
            activityAudioModeBinding.rl4khz.setAlpha(0.7f);
            activityAudioModeBinding.rl6khz.setAlpha(0.7f);
            activityAudioModeBinding.rl8khz.setAlpha(0.7f);
            activityAudioModeBinding.rl10khz.setAlpha(0.7f);
            activityAudioModeBinding.rl12khz.setAlpha(0.7f);
            activityAudioModeBinding.rl14khz.setAlpha(0.7f);
            activityAudioModeBinding.rl18khz.setAlpha(0.7f);
        }
    }

    private int getSettingModeValue() {
        int value = 0;
        String tempval = null;
        if (mAudioManagerEx == null) {
            mAudioManagerEx = new AudioManagerEx(this);
        }
        LogUtils.d(TAG, "getSettingIntValue ");
        tempval = mAudioManagerEx.getAudioParameters(AudioSettingParams.SW_PEQ_PRE_MODE);
        LogUtils.d(TAG, "getSettingIntValue get value: " + tempval);
        if (tempval != null && tempval.length() > 0) {
            value = Integer.parseInt(tempval);
        }
        return value;
    }

    public int updateSettingModeValue(int value) {
        if (mAudioManagerEx == null) {
            mAudioManagerEx = new AudioManagerEx(this);
        }
        LogUtils.d(TAG, "updateSettingModeValue value " + value);
        mAudioManagerEx.setAudioParameters(AudioSettingParams.SW_PEQ_PRE_MODE, Integer.toString(value));
        return 0;
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

    private int setAudioPEQValue(int band, int value) {
        if (mAudioManagerEx == null) {
            mAudioManagerEx = new AudioManagerEx(this);
        }
        String bandval = Integer.toString(band) + ":" + value;
        mAudioManagerEx.setAudioParameters(AudioSettingParams.SW_PEQ_GAIN, bandval);
        LogUtils.d(TAG, "setAudioPEQValue item: band:value -> " + bandval);
        return 0;
    }

    private int getAudioPEQValue(int band) {
        try {
            String tempval = null;
            int value = 0;
            if (mAudioManagerEx == null) {
                mAudioManagerEx = new AudioManagerEx(this);
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

}
