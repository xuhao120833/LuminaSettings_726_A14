package com.htc.luminaos.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.htc.luminaos.R;
import com.htc.luminaos.databinding.HotspotNameDialogBinding;
import com.htc.luminaos.utils.InputMethodUtil;
import com.htc.luminaos.utils.WifiHotUtil;

/**
 * Author:
 * Date:
 * Description:
 */
public class HotspotNameDialog extends BaseDialog implements View.OnClickListener {
    private Context mContext;
    private HotspotNameDialogBinding hotspotNameDialogBinding;
    private HotspotNameCallBack mcallback;
    private WifiHotUtil wifiHotUtil;
    private SoftApConfiguration wifiConfiguration;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.enter) {
            if (hotspotNameDialogBinding.etHotspotName.getText().toString().equals("")) {
                hotspotNameDialogBinding.errMsg.setVisibility(View.VISIBLE);
                hotspotNameDialogBinding.errMsg.setText(mContext.getString(R.string.empty_string));
                return;
            }
            if (mcallback != null)
                mcallback.onClick(hotspotNameDialogBinding.etHotspotName.getText().toString());
            dismiss();
        } else if (id == R.id.cancel) {
            dismiss();
        }
    }

    public interface HotspotNameCallBack {
        public void onClick(String password);
    }

    public HotspotNameDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public HotspotNameDialog(Context context, boolean cancelable,
                                 OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public HotspotNameDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public void HotspotConfig(WifiHotUtil wifiHotUtil){
        this.wifiHotUtil = wifiHotUtil;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onStart() {
        wifiConfiguration = wifiHotUtil.getWifiConfig();
        hotspotNameDialogBinding.hotspotName.setText(wifiConfiguration.getSsid());
        super.onStart();
    }

    private void init() {
        hotspotNameDialogBinding = HotspotNameDialogBinding.inflate(LayoutInflater.from(mContext));
        /*View view = LayoutInflater.from(mContext).inflate(
                R.layout.wifi_settings_layout, null);*/
        if (hotspotNameDialogBinding.getRoot() != null) {
            setContentView(hotspotNameDialogBinding.getRoot());
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
        hotspotNameDialogBinding.enter.setOnClickListener(this);
        hotspotNameDialogBinding.cancel.setOnClickListener(this);
        InputMethodUtil.openInputMethod(mContext,hotspotNameDialogBinding.etHotspotName,new Handler());
    }

    public void setOnClickCallBack(HotspotNameCallBack callback) {
        this.mcallback = callback;
    }
}
