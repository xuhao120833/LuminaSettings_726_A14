package com.htc.luminaos.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.htc.luminaos.R;
import com.htc.luminaos.databinding.WifiConnectNoDialogBinding;
import com.htc.luminaos.utils.LogUtils;


/**
 * Author:
 * Date:
 * Description:
 */
public class WifiConnectNoPasswordDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private View parent;
    private WifiConnectNoDialogBinding wifiConnectNoDialogBinding;
    private String wifi_name = "unknow";
    private OnWifiConnectNoPasswordCallBack mcallback;
    private static String TAG = "WifiConnectNoPasswordDialog";

    @Override
    public void onClick(View v) {
        LogUtils.d(TAG,"onclick");
        int id = v.getId();
        if (id == R.id.enter) {
            if (mcallback != null)
                mcallback.onClick();
            dismiss();
        } else if (id == R.id.cancel) {
            dismiss();
        }
    }

    public interface OnWifiConnectNoPasswordCallBack {
        public void onClick();
    }

    public WifiConnectNoPasswordDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public WifiConnectNoPasswordDialog(Context context, boolean cancelable,
                                       DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public WifiConnectNoPasswordDialog(Context context, int theme, View parent) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.parent = parent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        wifiConnectNoDialogBinding = WifiConnectNoDialogBinding.inflate(LayoutInflater.from(mContext));
        /*View view = LayoutInflater.from(mContext).inflate(
                R.layout.wifi_settings_layout, null);*/
        if (wifiConnectNoDialogBinding.getRoot() != null) {
            setContentView(wifiConnectNoDialogBinding.getRoot());
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
            WindowManager manager = ((Activity) mContext).getWindowManager();
            Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
            WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            params.width = (int) (d.getWidth() * 0.4); // 宽度设置为屏幕的0.8，根据实际情况调整
            params.height = (int) (d.getHeight() * 0.4);
            //params.x = parent.getWidth();
            dialogWindow.setGravity(Gravity.CENTER);// 设置对话框位置
            dialogWindow.setAttributes(params);
        }
    }

    public void  setConnectName(String name){
        this.wifi_name = name;
    }

    private void initView(){
        wifiConnectNoDialogBinding.enter.setOnClickListener(this);
        wifiConnectNoDialogBinding.cancel.setOnClickListener(this);
    }

    public void setOnClickCallBack(OnWifiConnectNoPasswordCallBack callback) {
        this.mcallback = callback;
    }
}
