package com.htc.luminaos.widget;

import static com.htc.luminaos.utils.Utils.list;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.htc.luminaos.MyApplication;
import com.htc.luminaos.R;
import com.htc.luminaos.activity.ProjectActivity;
import com.htc.luminaos.adapter.TimezoneAdapter;
import com.htc.luminaos.databinding.InitAngleLayoutBinding;
import com.htc.luminaos.databinding.TimeZoneLayoutBinding;
import com.htc.luminaos.utils.Constants;
import com.htc.luminaos.utils.Contants;
import com.htc.luminaos.utils.KeystoneUtils;
import com.htc.luminaos.utils.KeystoneUtils_726;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.ReflectUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Author:
 * Date:
 * Description:
 */
public class InitAngleDialog extends BaseDialog implements View.OnClickListener {
    private Context mContext;
    InitAngleLayoutBinding initAngleLayoutBinding;
    private int mDefault;
    //    private ArrayList<HashMap> list = null;
    private static String TAG = "InitAngleDialog";

    Handler handler = new Handler();

    private ProjectActivity projectActivity;

    //是不是主动关闭了自动梯形矫正
    private boolean activeClose = false;


    @Override
    public void setWallPaper() {
        if (MyApplication.mainDrawable != null) {
            LinearLayout linearLayout = Objects.requireNonNull(getWindow()).getDecorView().findViewById(R.id.rl_main);
            if (linearLayout != null)
                linearLayout.setBackground(MyApplication.mainDrawable);
        }
    }

    public InitAngleDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
        projectActivity = (ProjectActivity) context;
        initData();
    }

    private void initData() {   //再做初始角度矫正之前，先关闭“自动梯形矫正”开关，重置矫正画面。
        if (projectActivity.getAuto()) { //打开了先关闭
            activeClose = true;
            projectActivity.setAuto();
        }
        KeystoneUtils_726.resetKeystone();
        KeystoneUtils_726.writeGlobalSettings(getContext(), KeystoneUtils_726.ZOOM_VALUE, 0);
        KeystoneUtils_726.writeSystemProperties(KeystoneUtils_726.PROP_ZOOM_VALUE,0);
        projectActivity.All = 0;
        projectActivity.updateZoomView();
        SystemProperties.set("persist.sys.keystone_offset", "0");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (activeClose) { //主动关闭了，需要恢复打开状态
            activeClose = false;
            projectActivity.setAuto();
            projectActivity = null;
        }
    }

    private void init() {
        initAngleLayoutBinding = InitAngleLayoutBinding.inflate(LayoutInflater.from(mContext));
        if (initAngleLayoutBinding.getRoot() != null) {
            setContentView(initAngleLayoutBinding.getRoot());
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
            params.width = d.getWidth();
            params.height = d.getHeight();
            dialogWindow.setAttributes(params);
        }
    }

    private void initView() {
        initAngleLayoutBinding.startInitAngle.setOnClickListener(this);
        initAngleLayoutBinding.followMe.setSelected(true);
        initAngleLayoutBinding.startInitAngle.setSelected(true);
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
        dialog = new ProgressDialog(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage(getContext().getString(R.string.defaultcorrectionin));
        dialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                Toast.makeText(mContext, getContext().getText(R.string.init_angle_tip4), Toast.LENGTH_SHORT).show();
                LogUtils.d("get_angle_offset " + ReflectUtil.invokeGet_angle_offset());
                dismiss();
            }
        }, 3000);
    }
}
