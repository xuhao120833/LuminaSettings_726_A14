package com.htc.luminasettings.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.htc.luminasettings.R;
import com.htc.luminasettings.databinding.DisDeviceDialogBinding;
import com.htc.luminasettings.utils.LogUtils;

/**
        * Author:
        * Date:
        * Description:
        */
public class DisDeviceDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private View parent;
    private DisDeviceDialogBinding disDeviceDialogBinding;

    private OnDisDeviceCallBack mcallback;
    private String device_title_name=null;
    private static String TAG = "DisDeviceDialog";

    @Override
    public void onClick(View v) {
        LogUtils.d(TAG,"onclick");
        int id = v.getId();
        if (id == R.id.enter) {
            mcallback.onEnterClick();
            dismiss();
        } else if (id == R.id.cancel) {
            dismiss();
        } else if (id == R.id.clear) {
            mcallback.onUnPairClick();
            dismiss();
        }
    }

    public interface OnDisDeviceCallBack {
        public void onEnterClick();
        public void onUnPairClick();

    }

    public DisDeviceDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public DisDeviceDialog(Context context, boolean cancelable,
                           DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public DisDeviceDialog(Context context, int theme, View parent) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.parent = parent;
    }

    public void setDevice_title_name(String device_title_name) {
        if (device_title_name==null || "".equals(device_title_name))
            this.device_title_name = SystemProperties.get("persist.sys.connectBleName","");
        else
            this.device_title_name = device_title_name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        disDeviceDialogBinding = DisDeviceDialogBinding.inflate(LayoutInflater.from(mContext));
        /*View view = LayoutInflater.from(mContext).inflate(
                R.layout.wifi_settings_layout, null);*/
        if (disDeviceDialogBinding.getRoot() != null) {
            setContentView(disDeviceDialogBinding.getRoot());
            initView();
            // 设置dialog大小 模块好的控件大小设置
            Window dialogWindow = getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(R.style.right_in_right_out_anim);
                //去除系统自带的margin
                dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //设置dialog在界面中的属性
                dialogWindow.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            }
            WindowManager manager = ((Activity) mContext).getWindowManager();
            Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
            WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            params.width = (int) (d.getWidth() * 0.47); // 宽度设置为屏幕的0.8，根据实际情况调整
            params.height = (int) (d.getHeight() * 0.5);
            //params.x = parent.getWidth();
            dialogWindow.setGravity(Gravity.CENTER);// 设置对话框位置
            dialogWindow.setAttributes(params);
        }
    }

    private void initView(){
        disDeviceDialogBinding.deleteDeviceName.setText(device_title_name);
        disDeviceDialogBinding.enter.setOnClickListener(this);
        disDeviceDialogBinding.cancel.setOnClickListener(this);
        disDeviceDialogBinding.clear.setOnClickListener(this);

        disDeviceDialogBinding.enter.setSelected(true);
        disDeviceDialogBinding.cancel.setSelected(true);
        disDeviceDialogBinding.clear.setSelected(true);

    }

    public void setOnClickCallBack(OnDisDeviceCallBack callback) {
        this.mcallback = callback;
    }
}
