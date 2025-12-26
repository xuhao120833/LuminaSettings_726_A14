package com.htc.luminasettings.widget;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.htc.luminasettings.R;
import com.htc.luminasettings.databinding.BluetoothincomingBinding;
import com.htc.luminasettings.receiver.BluetoothInformingCallback;
import com.htc.luminasettings.utils.LogUtils;

/**
 * Author:
 * Date:
 * Description:
 */
public class BlutoothIncomingFileConfirmDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private Intent intent;
    private BluetoothInformingCallback callback;
    private BluetoothincomingBinding binding;
    private String packapgeName = "unknow";
    private ApplicationInfo info;

    private String TAG = "BlutoothIncomingFileConfirmDialog" ;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.accept) {
            Toast.makeText(mContext, mContext.getString(R.string.bluetooth_start_receive), Toast.LENGTH_SHORT).show();
            onTransfer();
        } else if (id == R.id.cancel) {
            CancelingTransfer();
        }
    }


    public BlutoothIncomingFileConfirmDialog(Context context , Intent intent, BluetoothInformingCallback callback) {
        super(context);
        this.mContext = context;
        this.intent = intent;
        this.callback = callback;
    }

    public BlutoothIncomingFileConfirmDialog(Context context, boolean cancelable,
                                             OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public BlutoothIncomingFileConfirmDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LogUtils.d(TAG," 执行AppDetailDialog init");
        binding = BluetoothincomingBinding.inflate(LayoutInflater.from(mContext));
        /*View view = LayoutInflater.from(mContext).inflate(
                R.layout.wifi_settings_layout, null);*/
        if (binding.getRoot() != null) {
            setContentView(binding.getRoot());
            initView();
            // 设置dialog大小 模块好的控件大小设置
            Window dialogWindow = getWindow();
            if (dialogWindow != null) {
                //去除系统自带的margin
                dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //设置dialog在界面中的属性
                dialogWindow.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                //背景全透明
                dialogWindow.setDimAmount(0f);
            }
            WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
            WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            params.width = (int) (d.getWidth() * 0.4); // 宽度设置为屏幕的0.8，根据实际情况调整
            params.height = (int) (d.getHeight() * 0.4);
            //params.x = parent.getWidth();
            dialogWindow.setGravity(Gravity.CENTER);// 设置对话框位置
            dialogWindow.setAttributes(params);
            setSelect();
        }
    }

    private void setSelect() {
//        binding.blueFromTxt1.setSelected(true);
//        binding.blueFromTxt2.setSelected(true);
//        binding.blueFileName1.setSelected(true);
//        binding.blueFileName2.setSelected(true);
//        binding.blueFileSize1.setSelected(true);
//        binding.blueFileSize2.setSelected(true);
        binding.title.setSelected(true);
        binding.accept.setSelected(true);
        binding.cancel.setSelected(true);
    }

    private void initView(){
        binding.accept.setOnClickListener(this);
        binding.cancel.setOnClickListener(this);
//        binding.appIcon.setBackground(info.loadIcon(mContext.getPackageManager()));
//        binding.appName.setText(info.loadLabel(mContext.getPackageManager()));
//        binding.appVersionTv.setText(getVersion(mContext,packapgeName));
    }

    private void onTransfer(){
        Intent intent = new Intent().setAction(BluetoothDevice.ACTION_INCOMINGFILE_CONFIRM_ACCEPT);
        mContext.sendBroadcast(intent);
        dismiss();
        callback.finishActivity();
    }

    private void CancelingTransfer(){
        dismiss();
        callback.finishActivity();
    }

}