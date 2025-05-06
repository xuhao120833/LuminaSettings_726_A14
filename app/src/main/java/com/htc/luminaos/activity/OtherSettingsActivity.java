package com.htc.luminaos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.htc.luminaos.MyApplication;
import com.htc.luminaos.R;
import com.htc.luminaos.databinding.ActivityOtherSettingsBinding;
import com.htc.luminaos.service.TimeOffService;
import com.htc.luminaos.utils.Contants;
import com.htc.luminaos.utils.ShareUtil;
import com.htc.luminaos.utils.Utils;
import com.htc.luminaos.widget.FactoryResetDialog;
import com.htc.luminaos.widget.SetPasswordDialog;
import com.softwinner.tv.AwTvSystemManager;
import com.softwinner.tv.common.AwTvSystemTypes;

public class OtherSettingsActivity extends BaseActivity implements View.OnKeyListener {

    private ActivityOtherSettingsBinding otherSettingsBinding;
    long cur_time = 0;

    private int cur_screen_saver_index = 0;
    String[] screen_saver_title;
    int[] screen_saver_value;

    private int cur_time_off_index = 0;
    String[] time_off_title;
    int[] time_off_value;

    String[] boot_source_name;
    String[] boot_source_value;
    private int boot_source_index = 0;
    private String TAG = "OtherSettingsActivity";
    private AwTvSystemManager mAwTvSystemManager;
    String[] powerModes;
    int curPowerMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        otherSettingsBinding = ActivityOtherSettingsBinding.inflate(LayoutInflater.from(this));
        setContentView(otherSettingsBinding.getRoot());
        initView();
        initData();
    }

    private void initView(){
        otherSettingsBinding.rlButtonSound.setOnClickListener(this);
        otherSettingsBinding.buttonSoundSwitch.setOnClickListener(this);
//        otherSettingsBinding.rlAudioMode.setOnClickListener(this);
        otherSettingsBinding.rlResetFactory.setOnClickListener(this);
        otherSettingsBinding.rlScreenSaver.setOnClickListener(this);
        otherSettingsBinding.rlTimerOff.setOnClickListener(this);
        otherSettingsBinding.rlBootInput.setOnClickListener(this);
        otherSettingsBinding.rlPowerMode.setOnClickListener(this);
        otherSettingsBinding.rlPowerMode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    otherSettingsBinding.powerModeTv.setSelected(true);
                }else {
                    otherSettingsBinding.powerModeTv.setSelected(false);
                }
            }
        });
        otherSettingsBinding.rlSetPassword.setOnClickListener(this);
        otherSettingsBinding.rlAccount.setOnClickListener(this);
        otherSettingsBinding.rlDeveloper.setOnClickListener(this);

        otherSettingsBinding.rlButtonSound.setOnHoverListener(this);
        otherSettingsBinding.buttonSoundSwitch.setOnHoverListener(this);
//        otherSettingsBinding.rlAudioMode.setOnHoverListener(this);
        otherSettingsBinding.rlResetFactory.setOnHoverListener(this);
        otherSettingsBinding.rlScreenSaver.setOnHoverListener(this);
        otherSettingsBinding.rlTimerOff.setOnHoverListener(this);
        otherSettingsBinding.rlBootInput.setOnHoverListener(this);
        otherSettingsBinding.rlPowerMode.setOnHoverListener(this);
        otherSettingsBinding.rlSetPassword.setOnHoverListener(this);
        otherSettingsBinding.rlAccount.setOnHoverListener(this);
        otherSettingsBinding.rlDeveloper.setOnHoverListener(this);

        otherSettingsBinding.rlScreenSaver.setOnKeyListener(this);
        otherSettingsBinding.rlTimerOff.setOnKeyListener(this);
        otherSettingsBinding.rlBootInput.setOnKeyListener(this);
        otherSettingsBinding.rlPowerMode.setOnKeyListener(this);

//        otherSettingsBinding.rlBootInput.requestFocus();
//        otherSettingsBinding.rlBootInput.requestFocusFromTouch();

//        otherSettingsBinding.rlAudioMode.setVisibility(MyApplication.config.AudioMode?View.VISIBLE:View.GONE);
        otherSettingsBinding.rlPowerMode.setVisibility(MyApplication.config.powerMode?View.VISIBLE:View.GONE);
        otherSettingsBinding.rlAccount.setVisibility(MyApplication.config.account?View.VISIBLE:View.GONE);
        otherSettingsBinding.rlBootInput.setVisibility(MyApplication.config.bootSource?View.VISIBLE:View.GONE);
        otherSettingsBinding.rlSetPassword.setVisibility(MyApplication.config.set_password?View.VISIBLE:View.GONE);

        if ((boolean)ShareUtil.get(this,Contants.KEY_DEVELOPER_MODE,false)){
            otherSettingsBinding.rlDeveloper.setVisibility(View.VISIBLE);
        }

        requestFirstItemFocus();
    }

    private void requestFirstItemFocus() {
        if (MyApplication.config.bootSource) {
            otherSettingsBinding.rlBootInput.requestFocus();
            otherSettingsBinding.rlBootInput.requestFocusFromTouch();
        } else if (MyApplication.config.powerMode) {
            otherSettingsBinding.rlPowerMode.requestFocus();
            otherSettingsBinding.rlPowerMode.requestFocusFromTouch();
        } else {
            otherSettingsBinding.rlButtonSound.requestFocus();
            otherSettingsBinding.rlButtonSound.requestFocusFromTouch();
        }
    }

    private void initData(){
        otherSettingsBinding.buttonSoundSwitch.setChecked(getButtonSound());

        screen_saver_title =  getResources().getStringArray(R.array.screen_saver_title);
        screen_saver_value = getResources().getIntArray(R.array.screen_saver_value);
        cur_screen_saver_index = getCurScreenSaverIndex();
        otherSettingsBinding.screenSaverTv.setText(screen_saver_title[cur_screen_saver_index]);

        time_off_title =  getResources().getStringArray(R.array.time_off_title);
        time_off_value = getResources().getIntArray(R.array.time_off_value);
        cur_time_off_index =(int) ShareUtil.get(this, Contants.TimeOffIndex,0);
        Log.d(TAG," initData cur_time_off_index "+cur_time_off_index);
        otherSettingsBinding.timerOffTv.setText(time_off_title[cur_time_off_index]);
        /*if ((boolean) ShareUtil.get(this, Contants.TimeOffStatus,false)){
            int  timeOffTime =(int) ShareUtil.get(this, Contants.TimeOffTime,0);
            otherSettingsBinding.timerOffTv.setText(timeOffTime/60+"Min");
        }else {
            otherSettingsBinding.timerOffTv.setText(time_off_title[cur_time_off_index]);
        }*/
        mAwTvSystemManager = AwTvSystemManager.getInstance(this);
        powerModes = getResources().getStringArray(R.array.power_mode_name);
        curPowerMode = mAwTvSystemManager.getPowerOnMode()== AwTvSystemTypes.EnumPowerMode.E_AW_POWER_MODE_DIRECT?1:0;
        otherSettingsBinding.powerModeTv.setText(powerModes[curPowerMode]);

        if (Utils.sourceList!= null && Utils.sourceList.length > 0 && !Utils.sourceList[0].isEmpty()) { //兼容多信源的情况
            boot_source_name = new String[Utils.sourceListTitle.length + 1];
            boot_source_name[0] = getResources().getString(R.string.boot_source_1);
            System.arraycopy(Utils.sourceListTitle, 0, boot_source_name, 1, Utils.sourceListTitle.length);

            boot_source_value = new String[Utils.sourceList.length + 1];
            boot_source_value[0] = "LOCAL";
            System.arraycopy(Utils.sourceList, 0, boot_source_value, 1, Utils.sourceList.length);
//            boot_source_name = Utils.sourceListTitle;
//            boot_source_value = Utils.sourceList;
        } else {
            boot_source_name = getResources().getStringArray(R.array.boot_source_name);
            boot_source_value = getResources().getStringArray(R.array.boot_source_value);
        }
        String source_value = get_power_signal();
        for (int i=0;i<boot_source_value.length;i++){
            if (source_value.equals(boot_source_value[i])) {
                boot_source_index = i;
                break;
            }
        }
        otherSettingsBinding.bootInputTv.setText(boot_source_name[boot_source_index]);
    }

    private String get_power_signal(){

        return SystemProperties.get("persist.sys.default_source","LOCAL");
    }

    //获取设备默认使用的HDMI接口，如果没设置就是HDMI1，设置了就是HDMI2
    private String get_hdmisource(){

        return SystemProperties.get("persist.sys.hdmisource","HDMI1");
    }

    private void set_power_signal(String source){
        SystemProperties.set("persist.sys.default_source",source);
    }

    private int getCurScreenSaverIndex(){
        int screen_off_timeout = Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,300000);
        Log.d(TAG,"getCurScreenSaverIndex screen_off_timeout "+screen_off_timeout);
        for (int i=0;i<screen_saver_value.length;i++){
            if (screen_off_timeout==screen_saver_value[i])
                return i;
        }
        return 0;
    }

    private void updateScreenSaver(int index){
        Log.d(TAG,"updateScreenSaver screen_saver_value[index] "+screen_saver_value[index]);
        Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,screen_saver_value[index]);
        otherSettingsBinding.screenSaverTv.setText(screen_saver_title[index]);
    }

    private void setTimeOff(int index){
        Log.d(TAG," 定时关机时间为 "+time_off_title.length+" "+index);
        otherSettingsBinding.timerOffTv.setText(time_off_title[index]);
        ShareUtil.put(this,Contants.TimeOffIndex,index);
        Intent intent = new Intent(this, TimeOffService.class);
        if (index==0){
            ShareUtil.put(this,Contants.TimeOffStatus,false);
            ShareUtil.put(this,Contants.TimeOffTime,time_off_value[index]);
            intent.putExtra(Contants.TimeOffStatus,false);
        }else {
            ShareUtil.put(this,Contants.TimeOffStatus,true);
            ShareUtil.put(this,Contants.TimeOffTime,time_off_value[index]);
            intent.putExtra(Contants.TimeOffStatus,true);
        }
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_button_sound || id == R.id.button_sound_switch) {
            otherSettingsBinding.buttonSoundSwitch.setChecked(!otherSettingsBinding.buttonSoundSwitch.isChecked());
            setButtonSound(otherSettingsBinding.buttonSoundSwitch.isChecked());
            //            case R.id.rl_audio_mode:
//                startNewActivity(AudioModeActivity.class);
//                break;
        } else if (id == R.id.rl_reset_factory) {
            FactoryResetDialog factoryResetDialog = new FactoryResetDialog(this, R.style.DialogTheme);
            factoryResetDialog.show();
        } else if (id == R.id.rl_screen_saver) {
            if (cur_screen_saver_index == screen_saver_title.length - 1)
                cur_screen_saver_index = 0;
            else
                cur_screen_saver_index++;
            updateScreenSaver(cur_screen_saver_index);
        } else if (id == R.id.rl_timer_off) {
            if (cur_time_off_index == time_off_title.length - 1)
                cur_time_off_index = 0;
            else
                cur_time_off_index++;

            setTimeOff(cur_time_off_index);
        } else if (id == R.id.rl_boot_input) {
            if (boot_source_index == boot_source_name.length - 1)
                boot_source_index = 0;
            else
                boot_source_index++;

            otherSettingsBinding.bootInputTv.setText(boot_source_name[boot_source_index]);
            set_power_signal(boot_source_value[boot_source_index]);
        } else if (id == R.id.rl_power_mode) {
            curPowerMode = curPowerMode == 1 ? 0 : 1;
            otherSettingsBinding.powerModeTv.setText(powerModes[curPowerMode]);
            mAwTvSystemManager.setPowerOnMode(curPowerMode == 1 ?
                    AwTvSystemTypes.EnumPowerMode.E_AW_POWER_MODE_DIRECT : AwTvSystemTypes.EnumPowerMode.E_AW_POWER_MODE_STANDBY);
        } else if (id == R.id.rl_account) {
            Log.d(TAG, "打开Google账号切换界面");
//            Intent intent = new Intent(Settings.ACTION_SYNC_SETTINGS);
//            intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[]{"com.google"});
//            startActivity(intent);
            startNewActivity(AccountActivity.class);
        } else if (id == R.id.rl_developer) {
            startNewActivity(DeveloperModeActivity.class);
        } else if(id == R.id.rl_set_password) {
            SetPasswordDialog passwordDialog = new SetPasswordDialog(this);
            passwordDialog.show();
        }
    }

   private boolean getButtonSound(){
        return Settings.System.getInt(getContentResolver(),
                Settings.System.SOUND_EFFECTS_ENABLED, 0)==1;
   }

    private void setButtonSound(boolean ret){
        Settings.System.putInt(getContentResolver(),Settings.System.SOUND_EFFECTS_ENABLED,ret?1:0);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if ((event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT ||event.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT)
                && (System.currentTimeMillis()-cur_time<150)){
            return true;
        }

        if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT )) {
            return true;
        }

        if (keyCode==KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() ==KeyEvent.ACTION_DOWN){
            int id = v.getId();
            if (id == R.id.rl_screen_saver) {
                if (cur_screen_saver_index == 0)
                    cur_screen_saver_index = screen_saver_title.length - 1;
                else
                    cur_screen_saver_index--;
                updateScreenSaver(cur_screen_saver_index);
//                    break;
                return true;
            } else if (id == R.id.rl_timer_off) {
                if (cur_time_off_index == 0)
                    cur_time_off_index = time_off_title.length - 1;
                else
                    cur_time_off_index--;

                setTimeOff(cur_time_off_index);
//                    break;
                return true;
            } else if (id == R.id.rl_boot_input) {
                if (boot_source_index == 0)
                    boot_source_index = boot_source_name.length - 1;
                else
                    boot_source_index--;

                otherSettingsBinding.bootInputTv.setText(boot_source_name[boot_source_index]);
                set_power_signal(boot_source_value[boot_source_index]);
//                    break;
                return true;
            } else if (id == R.id.rl_power_mode) {
                curPowerMode = curPowerMode == 1 ? 0 : 1;
                otherSettingsBinding.powerModeTv.setText(powerModes[curPowerMode]);
                mAwTvSystemManager.setPowerOnMode(curPowerMode == 1 ?
                        AwTvSystemTypes.EnumPowerMode.E_AW_POWER_MODE_DIRECT : AwTvSystemTypes.EnumPowerMode.E_AW_POWER_MODE_STANDBY);
                return true;
            }
        }else if (keyCode==KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() ==KeyEvent.ACTION_DOWN){
            int id = v.getId();
            if (id == R.id.rl_screen_saver) {
                if (cur_screen_saver_index == screen_saver_title.length - 1)
                    cur_screen_saver_index = 0;
                else
                    cur_screen_saver_index++;
                updateScreenSaver(cur_screen_saver_index);
                return true;
//                    break;
            } else if (id == R.id.rl_timer_off) {
                if (cur_time_off_index == time_off_title.length - 1)
                    cur_time_off_index = 0;
                else
                    cur_time_off_index++;

                setTimeOff(cur_time_off_index);
                return true;
//                    break;
            } else if (id == R.id.rl_boot_input) {
                if (boot_source_index == boot_source_name.length - 1)
                    boot_source_index = 0;
                else
                    boot_source_index++;

                otherSettingsBinding.bootInputTv.setText(boot_source_name[boot_source_index]);
                set_power_signal(boot_source_value[boot_source_index]);
//                    break;
                return true;
            } else if (id == R.id.rl_power_mode) {
                curPowerMode = curPowerMode == 1 ? 0 : 1;
                otherSettingsBinding.powerModeTv.setText(powerModes[curPowerMode]);
                mAwTvSystemManager.setPowerOnMode(curPowerMode == 1 ?
                        AwTvSystemTypes.EnumPowerMode.E_AW_POWER_MODE_DIRECT : AwTvSystemTypes.EnumPowerMode.E_AW_POWER_MODE_STANDBY);
                return true;
            }
        }

        return false;
    }
}