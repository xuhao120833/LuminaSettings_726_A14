package com.htc.luminaos.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

import com.htc.luminaos.R;
import com.htc.luminaos.activity.settings.PrivacyTermsActivity;
import com.htc.luminaos.databinding.UcOffBinding;
import com.htc.luminaos.databinding.UpgradeCheckFailBinding;
import com.htc.luminaos.receiver.UcOffCallBack;
import com.htc.luminaos.utils.LogUtils;


/**
 * Author:
 * Date:
 * Description:
 */
public class UcOffDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private UcOffCallBack ucOffCallback;
    private UcOffBinding ucOffBinding;
    private static String TAG = "UcOffDialog";

    @Override
    public void onClick(View v) {
        LogUtils.d(TAG,"onclick");
        int id = v.getId();
        if (id == R.id.enter) {
            SystemProperties.set("sys.implicit.policy","0");
            ucOffCallback.setVisibility();
            dismiss();
        } else if (id == R.id.cancel) {
            dismiss();
        }
    }

    public UcOffDialog(Context context) {
        super(context);
        this.mContext = context;
        ucOffCallback = (UcOffCallBack) context;
    }

    public UcOffDialog(Context context, boolean cancelable,
                       OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public UcOffDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        ucOffBinding = UcOffBinding.inflate(LayoutInflater.from(mContext));
        if (ucOffBinding.getRoot() != null) {
            setContentView(ucOffBinding.getRoot());
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
            params.width = (int)mContext.getResources().getDimension(R.dimen.x_600); // 宽度设置为屏幕的0.8，根据实际情况调整
            params.height = (int)mContext.getResources().getDimension(R.dimen.x_360);
            //params.x = parent.getWidth();
            dialogWindow.setGravity(Gravity.CENTER);// 设置对话框位置
            dialogWindow.setAttributes(params);
        }
    }

    private void initView(){
        ucOffBinding.enter.setOnClickListener(this);
        ucOffBinding.cancel.setOnClickListener(this);
    }
}
