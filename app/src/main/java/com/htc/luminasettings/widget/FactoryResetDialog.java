package com.htc.luminasettings.widget;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.htc.luminasettings.R;
import com.htc.luminasettings.databinding.ResetLayoutBinding;

/**
 * Author:
 * Date:
 * Description:
 */
public class FactoryResetDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private ResetLayoutBinding resetLayoutBinding;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.enter) {

            Intent resetIntent = new Intent("android.intent.action.FACTORY_RESET");
            resetIntent.setPackage("android");
            resetIntent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            resetIntent.putExtra(Intent.EXTRA_REASON, "ResetConfirmFragment");
            mContext.sendBroadcast(resetIntent);
            dismiss();

            ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(mContext.getString(R.string.reseting_factory));
            progressDialog.setCancelable(false);
            progressDialog.show();

            // 模拟等待 5 秒后系统开始重启，此时 Activity 会被杀掉
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // 如果设备还没重启，可手动隐藏对话框（通常不需要）
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }, 6000);

        } else if (id == R.id.cancel) {
            dismiss();
        }
    }


    public FactoryResetDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public FactoryResetDialog(Context context, boolean cancelable,
                              DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public FactoryResetDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        resetLayoutBinding = ResetLayoutBinding.inflate(LayoutInflater.from(mContext));
        /*View view = LayoutInflater.from(mContext).inflate(
                R.layout.wifi_settings_layout, null);*/
        if (resetLayoutBinding.getRoot() != null) {
            setContentView(resetLayoutBinding.getRoot());
            initView();
            // 设置dialog大小 模块好的控件大小设置
            Window dialogWindow = getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(R.style.right_in_right_out_anim);
                //去除系统自带的margin
                dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //设置dialog在界面中的属性
                dialogWindow.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                //背景全透明
                dialogWindow.setDimAmount(0f);
            }
            WindowManager manager = ((Activity) mContext).getWindowManager();
            Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
            WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            params.width = (int) (d.getWidth() * 0.4); // 宽度设置为屏幕的0.8，根据实际情况调整
            params.height = (int) (d.getHeight() * 0.4);
            dialogWindow.setGravity(Gravity.CENTER);// 设置对话框位置
            dialogWindow.setAttributes(params);
        }
    }

    private void initView(){
        resetLayoutBinding.enter.setOnClickListener(this);
        resetLayoutBinding.cancel.setOnClickListener(this);
    }

}
