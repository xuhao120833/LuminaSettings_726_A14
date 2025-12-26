package com.htc.luminasettings.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.htc.luminasettings.MyApplication;
import com.htc.luminasettings.R;
import com.htc.luminasettings.activity.BaseActivity;
import com.htc.luminasettings.databinding.ActivityMainSettingBinding;
import com.htc.luminasettings.databinding.MainSettingsCustomBinding;
import com.htc.luminasettings.databinding.SettingsCustomBinding;
import com.htc.luminasettings.receiver.DisplaySettingsReceiver;
import com.htc.luminasettings.receiver.InitAngleReceiver;
import com.htc.luminasettings.utils.LogUtils;

public class MainSettingActivity extends BaseActivity {

    MainSettingsCustomBinding mainSettingsCustomBinding;
    private static String TAG = "MainSettingActivity";
    private DisplaySettingsReceiver displaySettingsReceiver = null;
    private InitAngleReceiver initAngleReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //原生逻辑改进
        mainSettingsCustomBinding = MainSettingsCustomBinding.inflate(LayoutInflater.from(this));
        setContentView(mainSettingsCustomBinding.getRoot());
        initView();
        initData();
        initReceiver();
    }

    @Override
    protected void onResume() {
        setLayout();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        getApplicationContext().unregisterReceiver(displaySettingsReceiver);
        displaySettingsReceiver = null;
        unregisterReceiver(initAngleReceiver);
        super.onDestroy();
    }

    private void setLayout() {
        if (MyApplication.config.layout_select == 2 || MyApplication.config.layout_select == 3) {
            Typeface typeface = ResourcesCompat.getFont(this, R.font.arial);

            mainSettingsCustomBinding.settingsTitle.setTextColor(Color.BLACK);
            mainSettingsCustomBinding.settingsTitle.setTypeface(typeface);
            mainSettingsCustomBinding.settingsTitle.setLetterSpacing(0.05f);
            mainSettingsCustomBinding.settingsTitle.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT);

            mainSettingsCustomBinding.wifiTxt.setTextColor(Color.BLACK);
            mainSettingsCustomBinding.wifiTxt.setTypeface(typeface);
            mainSettingsCustomBinding.wifiTxt.setLetterSpacing(0.08f);
            mainSettingsCustomBinding.wifiTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.y_30));
            mainSettingsCustomBinding.wifiTxt.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT);

            mainSettingsCustomBinding.btTxt.setTextColor(Color.BLACK);
            mainSettingsCustomBinding.btTxt.setTypeface(typeface);
            mainSettingsCustomBinding.btTxt.setLetterSpacing(0.08f);
            mainSettingsCustomBinding.btTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.y_30));
            mainSettingsCustomBinding.btTxt.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT);

            mainSettingsCustomBinding.projectTxt.setTextColor(Color.BLACK);
            mainSettingsCustomBinding.projectTxt.setTypeface(typeface);
            mainSettingsCustomBinding.projectTxt.setLetterSpacing(0.08f);
            mainSettingsCustomBinding.projectTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.y_30));
            mainSettingsCustomBinding.projectTxt.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT);

            mainSettingsCustomBinding.languageTxt.setTextColor(Color.BLACK);
            mainSettingsCustomBinding.languageTxt.setTypeface(typeface);
            mainSettingsCustomBinding.languageTxt.setLetterSpacing(0.08f);
            mainSettingsCustomBinding.languageTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.y_30));
            mainSettingsCustomBinding.languageTxt.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT);

            mainSettingsCustomBinding.appsTxt.setTextColor(Color.BLACK);
            mainSettingsCustomBinding.appsTxt.setTypeface(typeface);
            mainSettingsCustomBinding.appsTxt.setLetterSpacing(0.08f);
            mainSettingsCustomBinding.appsTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.y_30));
            mainSettingsCustomBinding.appsTxt.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT);

            mainSettingsCustomBinding.timeTxt.setTextColor(Color.BLACK);
            mainSettingsCustomBinding.timeTxt.setTypeface(typeface);
            mainSettingsCustomBinding.timeTxt.setLetterSpacing(0.08f);
            mainSettingsCustomBinding.timeTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.y_30));
            mainSettingsCustomBinding.timeTxt.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT);

            mainSettingsCustomBinding.otherTxt.setTextColor(Color.BLACK);
            mainSettingsCustomBinding.otherTxt.setTypeface(typeface);
            mainSettingsCustomBinding.otherTxt.setLetterSpacing(0.08f);
            mainSettingsCustomBinding.otherTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.y_30));
            mainSettingsCustomBinding.otherTxt.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT);

            mainSettingsCustomBinding.aboutTxt.setTextColor(Color.BLACK);
            mainSettingsCustomBinding.aboutTxt.setTypeface(typeface);
            mainSettingsCustomBinding.aboutTxt.setLetterSpacing(0.08f);
            mainSettingsCustomBinding.aboutTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.y_30));
            mainSettingsCustomBinding.aboutTxt.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT);
        }
    }


    private void initView() {

        //原生逻辑改进UI
        mainSettingsCustomBinding.rlAbout.setOnClickListener(this);
        mainSettingsCustomBinding.rlAppsManager.setOnClickListener(this);
        mainSettingsCustomBinding.rlBluetooth.setOnClickListener(this);
        mainSettingsCustomBinding.rlDateTime.setOnClickListener(this);
        mainSettingsCustomBinding.rlLanguage.setOnClickListener(this);
        mainSettingsCustomBinding.rlOther.setOnClickListener(this);
        mainSettingsCustomBinding.rlProject.setOnClickListener(this);
        mainSettingsCustomBinding.rlWifi.setOnClickListener(this);

        mainSettingsCustomBinding.rlAbout.setOnHoverListener(this);
        mainSettingsCustomBinding.rlAppsManager.setOnHoverListener(this);
        mainSettingsCustomBinding.rlBluetooth.setOnHoverListener(this);
        mainSettingsCustomBinding.rlDateTime.setOnHoverListener(this);
        mainSettingsCustomBinding.rlLanguage.setOnHoverListener(this);
        mainSettingsCustomBinding.rlOther.setOnHoverListener(this);
        mainSettingsCustomBinding.rlProject.setOnHoverListener(this);
        mainSettingsCustomBinding.rlWifi.setOnHoverListener(this);

        mainSettingsCustomBinding.rlAbout.setOnFocusChangeListener(this);
        mainSettingsCustomBinding.rlAppsManager.setOnFocusChangeListener(this);
        mainSettingsCustomBinding.rlBluetooth.setOnFocusChangeListener(this);
        mainSettingsCustomBinding.rlDateTime.setOnFocusChangeListener(this);
        mainSettingsCustomBinding.rlLanguage.setOnFocusChangeListener(this);
        mainSettingsCustomBinding.rlOther.setOnFocusChangeListener(this);
        mainSettingsCustomBinding.rlProject.setOnFocusChangeListener(this);
        mainSettingsCustomBinding.rlWifi.setOnFocusChangeListener(this);

        mainSettingsCustomBinding.rlWifi.requestFocus();
        mainSettingsCustomBinding.rlWifi.requestFocusFromTouch();
    }

    private void initData() {

    }

    private void initReceiver() {
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
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_wifi) {
            startNewActivityClearTask(NetworkActivity.class);
        } else if (id == R.id.rl_bluetooth) {
            startNewActivityBlue(BluetoothActivity.class);
        } else if (id == R.id.rl_project) {
            startNewActivityClearTask(ProjectActivity.class);
        } else if (id == R.id.rl_apps_manager) {
            startNewActivityClearTask(AppsManagerActivity.class);
        } else if (id == R.id.rl_language) {
            startNewActivityClearTask(LanguageAndKeyboardActivity.class);
        } else if (id == R.id.rl_date_time) {
            startNewActivityClearTask(DateTimeActivity.class);
        } else if (id == R.id.rl_other) {
            startNewActivityClearTask(OtherSettingsActivity.class);
        } else if (id == R.id.rl_about) {
            startNewActivityClearTask(AboutActivity.class);
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        if (hasFocus) {
            if (id == R.id.rl_about) {
                mainSettingsCustomBinding.aboutTxt.setSelected(true);
            } else if (id == R.id.rl_apps_manager) {
                mainSettingsCustomBinding.appsTxt.setSelected(true);
            } else if (id == R.id.rl_bluetooth) {
                mainSettingsCustomBinding.btTxt.setSelected(true);
            } else if (id == R.id.rl_date_time) {
                mainSettingsCustomBinding.timeTxt.setSelected(true);
            } else if (id == R.id.rl_language) {
                mainSettingsCustomBinding.languageTxt.setSelected(true);
            } else if (id == R.id.rl_other) {
                mainSettingsCustomBinding.otherTxt.setSelected(true);
            } else if (id == R.id.rl_project) {
                mainSettingsCustomBinding.projectTxt.setSelected(true);
            } else if (id == R.id.rl_wifi) {
                mainSettingsCustomBinding.wifiTxt.setSelected(true);
            }
        } else {
            if (id == R.id.rl_about) {
                mainSettingsCustomBinding.aboutTxt.setSelected(false);
            } else if (id == R.id.rl_apps_manager) {
                mainSettingsCustomBinding.appsTxt.setSelected(false);
            } else if (id == R.id.rl_bluetooth) {
                mainSettingsCustomBinding.btTxt.setSelected(false);
            } else if (id == R.id.rl_date_time) {
                mainSettingsCustomBinding.timeTxt.setSelected(false);
            } else if (id == R.id.rl_language) {
                mainSettingsCustomBinding.languageTxt.setSelected(false);
            } else if (id == R.id.rl_other) {
                mainSettingsCustomBinding.otherTxt.setSelected(false);
            } else if (id == R.id.rl_project) {
                mainSettingsCustomBinding.projectTxt.setSelected(false);
            } else if (id == R.id.rl_wifi) {
                mainSettingsCustomBinding.wifiTxt.setSelected(false);
            }
        }
    }

}