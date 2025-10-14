package com.htc.luminaos.activity.settings;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.htc.luminaos.R;
import com.htc.luminaos.activity.settings.BluetoothSpeakerActivity;
import com.htc.luminaos.databinding.ActivityPowerBinding;
import com.htc.luminaos.service.TimeOffService;
import com.htc.luminaos.utils.Contants;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.ShareUtil;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class PowerActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private ActivityPowerBinding powerBinding;
    public String Tag = "MainActivity";
    int Count = 15;
    PowerManager pm;
    ExecutorService factor = Executors.newFixedThreadPool(2);

    private int cur_time_off_index = 0;
    String[] time_off_title;
    int[] time_off_value;
    long cur_time = 0;


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == 101){
                Count--;
                powerBinding.count.setText(getString(R.string.second,String.valueOf(Count)));
                if (Count==0){
                    pm.shutdown(false,null,false);
                }else {
                    handler.sendEmptyMessageDelayed(101,1000);
                }
            }else if (message.what == 201){
                pm.reboot(null);
                LogUtils.d(Tag,"reboot");
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        powerBinding = ActivityPowerBinding.inflate(LayoutInflater.from(this));
        setContentView(powerBinding.getRoot());

        Window window =  getWindow();
        WindowManager manager = getWindowManager();
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams params = window.getAttributes(); // 获取对话框当前的参数值
        params.width = d.getWidth(); // 宽度设置为屏幕，根据实际情况调整
        params.height = d.getHeight();
        window.setAttributes(params);
        initView();
        initData();
    }

    private void initView(){

        powerBinding.rlSleep.setOnClickListener(this);
        powerBinding.rlShutdown.setOnClickListener(this);
        powerBinding.rlReboot.setOnClickListener(this);
        powerBinding.rlBluetoothSpeaker.setOnClickListener(this);
        powerBinding.rlTimerOff.setOnClickListener(this);

        powerBinding.rlSleep.setOnFocusChangeListener(this);
        powerBinding.rlShutdown.setOnFocusChangeListener(this);
        powerBinding.rlReboot.setOnFocusChangeListener(this);
        powerBinding.rlBluetoothSpeaker.setOnFocusChangeListener(this);
        powerBinding.rlTimerOff.setOnFocusChangeListener(this);

        powerBinding.rlTimerOff.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT ||event.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT)
                        && (System.currentTimeMillis()-cur_time<150)){
                    return true;
                }

                if (keyCode==KeyEvent.KEYCODE_DPAD_UP &&event.getAction()==KeyEvent.ACTION_UP){
                    if (v.getId() == R.id.rl_timer_off) {
                        if (cur_time_off_index == time_off_title.length - 1)
                            cur_time_off_index = 0;
                        else
                            cur_time_off_index++;

                        setTimeOff(cur_time_off_index);
                    }
                }else if (keyCode==KeyEvent.KEYCODE_DPAD_DOWN &&event.getAction()==KeyEvent.ACTION_UP){
                    if (v.getId() == R.id.rl_timer_off) {
                        if (cur_time_off_index == 0)
                            cur_time_off_index = time_off_title.length - 1;
                        else
                            cur_time_off_index--;

                        setTimeOff(cur_time_off_index);
                    }
                }
                return false;
            }
        });

    }

    private void initData(){
         pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        powerBinding.rlShutdown.requestFocus();
        powerBinding.rlShutdown.requestFocusFromTouch();
        powerBinding.count.setText(getString(R.string.second,String.valueOf(Count)));
        time_off_title =  getResources().getStringArray(R.array.time_off_title);
        time_off_value = getResources().getIntArray(R.array.time_off_value);
        cur_time_off_index =(int) ShareUtil.get(this, Contants.TimeOffIndex,0);
        powerBinding.timerOffTv.setText(time_off_title[cur_time_off_index]);
    }

    private void setTimeOff(int index){
        powerBinding.timerOffTv.setText(time_off_title[index]);
        ShareUtil.put(this,Contants.TimeOffIndex,index);
        Intent intent = new Intent(this, TimeOffService.class);
        if (index==0){
            ShareUtil.put(this,Contants.TimeOffStatus,false);
            intent.putExtra(Contants.TimeOffStatus,false);
            intent.putExtra(Contants.TimeOffTime,-1);
        }else {
            ShareUtil.put(this,Contants.TimeOffStatus,true);
            ShareUtil.put(this,Contants.TimeOffTime,time_off_value[index]);
            intent.putExtra(Contants.TimeOffStatus,true);
            intent.putExtra(Contants.TimeOffTime,time_off_value[index]);
        }
        startForegroundService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rl_sleep) {
            goToSleep();
        } else if (id == R.id.rl_shutdown) {
            pm.shutdown(false, null, false);
        } else if (id == R.id.rl_reboot) {
            handler.sendEmptyMessageDelayed(201, 100);
        } else if (id == R.id.rl_bluetooth_speaker) {
            Intent intent = new Intent(this, BluetoothSpeakerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (id == R.id.rl_timer_off) {
            if (cur_time_off_index == time_off_title.length - 1)
                cur_time_off_index = 0;
            else
                cur_time_off_index++;

            setTimeOff(cur_time_off_index);
        }
    }


    public boolean goToSleep() {
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        try {
            Method method = pm.getClass().getMethod("goToSleep", Long.TYPE);
            method.invoke(pm, SystemClock.uptimeMillis());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void onFocusChange(View view, boolean b) {

        if (view.getId()==R.id.rl_shutdown){
            if (b){
                handler.sendEmptyMessageDelayed(101,1000);
            }else {
                if (handler.hasMessages(101)){
                    handler.removeMessages(101);
                }
            }
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        finish();
    }

    @Override
    protected void onDestroy() {
        factor.shutdown();
        factor.shutdownNow();
        super.onDestroy();
    }

}