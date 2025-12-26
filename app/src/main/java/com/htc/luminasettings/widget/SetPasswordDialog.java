package com.htc.luminasettings.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import com.htc.luminasettings.databinding.ActivitySetPasswordBinding;
import com.htc.luminasettings.utils.PasswordUtils;
/**
 * Author:
 * Date:
 * Description:
 */
public class SetPasswordDialog extends BaseDialog implements View.OnClickListener {
    private Context mContext;
    private static String TAG = "SetPasswordDialog";

    private ActivitySetPasswordBinding setPasswordBinding;

    public SetPasswordDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public SetPasswordDialog(Context context, boolean cancelable,
                             OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public SetPasswordDialog(Context context, int theme) {
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
        setPasswordBinding = ActivitySetPasswordBinding.inflate(LayoutInflater.from(mContext));
        /*View view = LayoutInflater.from(mContext).inflate(
                R.layout.wifi_settings_layout, null);*/
        if (setPasswordBinding.getRoot() != null) {
            setContentView(setPasswordBinding.getRoot());
            initView();
            // 设置dialog大小 模块好的控件大小设置
            Window dialogWindow = getWindow();
            if (dialogWindow != null) {
                //去除系统自带的margin
                dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //设置dialog在界面中的属性
                dialogWindow.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            }
            WindowManager manager = ((Activity) mContext).getWindowManager();
            Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
            WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            params.width = d.getWidth(); // 宽度设置为屏幕的0.8，根据实际情况调整
            params.height = d.getHeight();
            //params.x = parent.getWidth();
            dialogWindow.setGravity(Gravity.CENTER);// 设置对话框位置
            dialogWindow.setAttributes(params);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initView() {

        setPasswordBinding.rlPasswordSwitch.setOnClickListener(this);
        setPasswordBinding.passwordSwitch.setOnClickListener(this);
        setPasswordBinding.rlChangePassword.setOnClickListener(this);
        setPasswordBinding.changePasswordRight.setOnClickListener(this);

        setPasswordBinding.rlPasswordSwitch.setOnHoverListener(this);
        setPasswordBinding.rlChangePassword.setOnHoverListener(this);

        setPasswordBinding.passwordSwitch.setChecked(PasswordUtils.isPasswordSwitchEnabled());

    }

    @SuppressLint("ShowToast")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_password_switch) {
            PasswordUtils.setPasswordSwitchEnabled(!setPasswordBinding.passwordSwitch.isChecked());
            setPasswordBinding.passwordSwitch.setChecked(!setPasswordBinding.passwordSwitch.isChecked());
            Toast.makeText(getContext(), mContext.getText(R.string.reboot_useful), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.password_switch) {
            PasswordUtils.setPasswordSwitchEnabled(!setPasswordBinding.passwordSwitch.isChecked());
            setPasswordBinding.passwordSwitch.setChecked(!setPasswordBinding.passwordSwitch.isChecked());
            Toast.makeText(getContext(), mContext.getText(R.string.reboot_useful), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.rl_change_password) {
            startChangePasswordDialog();
        } else if (id == R.id.change_password_right) {
            startChangePasswordDialog();
        }
    }

    private void startChangePasswordDialog() {
        ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(mContext);
        changePasswordDialog.show();
    }


}
