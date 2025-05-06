package com.htc.luminaos.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.htc.luminaos.R;
import com.htc.luminaos.databinding.ResetLayoutBinding;
import com.htc.luminaos.databinding.ShutDownLayoutBinding;
import com.htc.luminaos.utils.DeviceUtils;

/**
 * Author:
 * Date:
 * Description:
 */
public class ShutDownDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private ShutDownLayoutBinding shutDownLayoutBinding;
    int count = 15;
    Handler handler = new Handler();

    private boolean confirmedShutdown = false;

    public boolean isConfirmedShutdown() {
        return confirmedShutdown;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.enter) {
            confirmedShutdown = true; // 标记为确认关机
            dismiss();
            DeviceUtils.ShutDown(mContext);
        } else if (id == R.id.cancel) {
            dismiss();
        }
    }


    public ShutDownDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public ShutDownDialog(Context context, boolean cancelable,
                              DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public ShutDownDialog(Context context, int theme) {
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

    @Override
    protected void onStart() {
        handler.postDelayed(runnable,1000);
        super.onStart();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            count--;
            shutDownLayoutBinding.shutdownTips.setText(Html.fromHtml(String.format(mContext.getString(R.string.shutdown_tips),count)));
            if (count<=0){
                dismiss();
                DeviceUtils.ShutDown(mContext);
                return;
            }
            handler.postDelayed(this,1000);
        }
    };

    private void init() {
        shutDownLayoutBinding = ShutDownLayoutBinding.inflate(LayoutInflater.from(mContext));
        /*View view = LayoutInflater.from(mContext).inflate(
                R.layout.wifi_settings_layout, null);*/
        if (shutDownLayoutBinding.getRoot() != null) {
            setContentView(shutDownLayoutBinding.getRoot());
            initView();
            // 设置dialog大小 模块好的控件大小设置
            Window dialogWindow = getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(R.style.right_in_right_out_anim);
                //去除系统自带的margin
                dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogWindow.setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
                //设置dialog在界面中的属性
                dialogWindow.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                //背景全透明
                dialogWindow.setDimAmount(0f);
            }
            WindowManager manager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
            Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
            WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            params.width = (int) (d.getWidth() * 0.4); // 宽度设置为屏幕的0.8，根据实际情况调整
            params.height = (int) (d.getHeight() * 0.4);
            dialogWindow.setGravity(Gravity.CENTER);// 设置对话框位置
            dialogWindow.setAttributes(params);
        }
    }

    private void initView(){
        shutDownLayoutBinding.enter.setOnClickListener(this);
        shutDownLayoutBinding.cancel.setOnClickListener(this);
        shutDownLayoutBinding.shutdownTips.setText(Html.fromHtml(mContext.getString(R.string.shutdown_tips,count)));
    }

    @Override
    public void dismiss() {
        handler.removeCallbacks(runnable);
        super.dismiss();
    }
}
