package com.htc.luminaos.activity.settings;

import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import com.htc.luminaos.R;
import com.htc.luminaos.activity.BaseActivity;
import com.htc.luminaos.databinding.ActivityDeveloperModeBinding;

public class DeveloperModeActivity extends BaseActivity {

    private ActivityDeveloperModeBinding developerModeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        developerModeBinding =ActivityDeveloperModeBinding.inflate(LayoutInflater.from(this));
        setContentView(developerModeBinding.getRoot());
        initView();
        initData();
    }

    private void initView(){
        developerModeBinding.rlUsbDebug.setOnClickListener(this);
        developerModeBinding.rlUsbDebug.setOnHoverListener(this);
        developerModeBinding.usbDebugSwitch.setOnClickListener(this);

        developerModeBinding.rlAdbDebug.setOnClickListener(this);
        developerModeBinding.rlAdbDebug.setOnHoverListener(this);
        developerModeBinding.adbDebugSwitch.setOnClickListener(this);

        developerModeBinding.adbDebugSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Settings.Global.putInt(getContentResolver(),
                            Settings.Global.ADB_ENABLED, 1);
                }else {
                    Settings.Global.putInt(getContentResolver(),
                            Settings.Global.ADB_ENABLED, 0);

                }
            }
        });
        developerModeBinding.usbDebugSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SystemProperties.set("persist.sys.usb0device","1");
                }else {
                    SystemProperties.set("persist.sys.usb0device","0");
                }
            }
        });
    }

    private void initData(){
        int ADB_ENABLED = Settings.Global.getInt(getContentResolver(),
                Settings.Global.ADB_ENABLED, 0);

        boolean usbModeSe = SystemProperties.getBoolean("persist.sys.usb0device",false);

        developerModeBinding.adbDebugSwitch.setChecked(ADB_ENABLED==1);
        developerModeBinding.usbDebugSwitch.setChecked(usbModeSe);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_usb_debug || id == R.id.usb_debug_switch) {
            developerModeBinding.usbDebugSwitch.setChecked(!developerModeBinding.usbDebugSwitch.isChecked());
        } else if (id == R.id.rl_adb_debug || id == R.id.adb_debug_switch) {
            developerModeBinding.adbDebugSwitch.setChecked(!developerModeBinding.adbDebugSwitch.isChecked());
        }
    }
}