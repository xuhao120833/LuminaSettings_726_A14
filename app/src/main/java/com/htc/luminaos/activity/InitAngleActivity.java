package com.htc.luminaos.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.htc.luminaos.R;
import com.htc.luminaos.databinding.InitAngleLayoutBinding;
import com.htc.luminaos.utils.KeystoneUtils;
import com.htc.luminaos.utils.KeystoneUtils_726;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.ReflectUtil;

public class InitAngleActivity extends BaseActivity {
    InitAngleLayoutBinding initAngleLayoutBinding;
    private static String TAG = "InitAngleDialog";
    Handler handler = new Handler();
    //是不是主动关闭了自动梯形矫正
    private boolean activeClose = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAngleLayoutBinding = InitAngleLayoutBinding.inflate(LayoutInflater.from(this));
        setContentView(initAngleLayoutBinding.getRoot());
        initView();
        initData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (activeClose) { //主动关闭了，需要恢复打开状态
            activeClose = false;
            setAuto();
        }
    }

    private void initData() {   //再做初始角度矫正之前，先关闭“自动梯形矫正”开关，重置矫正画面。
        if (getAuto()) { //打开了先关闭
            activeClose = true;
            setAuto();
        }
        KeystoneUtils_726.resetKeystone();
        KeystoneUtils_726.writeGlobalSettings(getApplicationContext(), KeystoneUtils_726.ZOOM_VALUE, 0);
        KeystoneUtils_726.writeSystemProperties(KeystoneUtils_726.PROP_ZOOM_VALUE,0);
        SystemProperties.set("persist.sys.keystone_offset", "0");

        KeystoneUtils_726.writeSystemProperties(KeystoneUtils_726.PROP_ZOOM_SCALE,0);
    }

    private void initView() {
        initAngleLayoutBinding.startInitAngle.setOnClickListener(this);
        initAngleLayoutBinding.followMe.setSelected(true);
        initAngleLayoutBinding.startInitAngle.setSelected(true);
        initAngleLayoutBinding.startInitAngle.requestFocus();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_init_angle) {
            initCorrectAngle();
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
                Toast.makeText(getApplicationContext(), getText(R.string.init_angle_tip4), Toast.LENGTH_SHORT).show();
                LogUtils.d("get_angle_offset " + ReflectUtil.invokeGet_angle_offset());
                finish();
            }
        }, 3000);
    }

    public boolean getAuto() {
        return SystemProperties.getBoolean("persist.sys.tpryauto", false);
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
}