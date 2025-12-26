package com.htc.luminasettings.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.htc.luminasettings.R;
import com.htc.luminasettings.databinding.ActivityChangePasswordBinding;
import com.htc.luminasettings.utils.PasswordUtils;

/**
 * Author:
 * Date:
 * Description:
 */
public class ChangePasswordDialog extends BaseDialog implements View.OnClickListener, View.OnFocusChangeListener {
    private Context mContext;
    private static String TAG = "ChangePasswordDialog";
    private ActivityChangePasswordBinding changePasswordBinding;

    public ChangePasswordDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public ChangePasswordDialog(Context context, boolean cancelable,
                                OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public ChangePasswordDialog(Context context, int theme) {
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
        changePasswordBinding = ActivityChangePasswordBinding.inflate(LayoutInflater.from(mContext));
        /*View view = LayoutInflater.from(mContext).inflate(
                R.layout.wifi_settings_layout, null);*/
        if (changePasswordBinding.getRoot() != null) {
            setContentView(changePasswordBinding.getRoot());
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

        changePasswordBinding.rlEye1.setOnClickListener(this);
        changePasswordBinding.rlEye2.setOnClickListener(this);
        changePasswordBinding.rlEye3.setOnClickListener(this);
        changePasswordBinding.enter.setOnClickListener(this);
        changePasswordBinding.cancel.setOnClickListener(this);

        changePasswordBinding.rlEye1.setOnHoverListener(this);
        changePasswordBinding.rlEye2.setOnHoverListener(this);
        changePasswordBinding.rlEye3.setOnHoverListener(this);
        changePasswordBinding.enter.setOnHoverListener(this);
        changePasswordBinding.cancel.setOnHoverListener(this);

        changePasswordBinding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPasswordLength();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        changePasswordBinding.newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPasswordLength();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        changePasswordBinding.newPasswordRepeat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPasswordLength();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        changePasswordBinding.password.setOnFocusChangeListener(this);
        changePasswordBinding.newPassword.setOnFocusChangeListener(this);
        changePasswordBinding.newPasswordRepeat.setOnFocusChangeListener(this);

        changePasswordBinding.rlEye1.requestFocus();
    }

    private void checkPasswordLength() {
        String pwd1 = changePasswordBinding.password.getText().toString();
        String pwd2 = changePasswordBinding.newPassword.getText().toString();
        String pwd3 = changePasswordBinding.newPasswordRepeat.getText().toString();
        boolean bothValid = pwd1.length() >= 4 && pwd2.length() >= 4 && pwd3.length() >= 4;
        changePasswordBinding.enter.setEnabled(bothValid);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_eye1) {
            if (changePasswordBinding.password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // 显示密码
                changePasswordBinding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                changePasswordBinding.eye1.setImageResource(R.drawable.password_eye); // 替换为显示密码的图标
            } else {
                // 隐藏密码
                changePasswordBinding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                changePasswordBinding.eye1.setImageResource(R.drawable.password_eye_off); // 替换为隐藏密码的图标
            }
        } else if (id == R.id.rl_eye2) {
            if (changePasswordBinding.newPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // 显示密码
                changePasswordBinding.newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                changePasswordBinding.eye2.setImageResource(R.drawable.password_eye); // 替换为显示密码的图标
            } else {
                // 隐藏密码
                changePasswordBinding.newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                changePasswordBinding.eye2.setImageResource(R.drawable.password_eye_off); // 替换为隐藏密码的图标
            }
        } else if (id == R.id.rl_eye3) {
            if (changePasswordBinding.newPasswordRepeat.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // 显示密码
                changePasswordBinding.newPasswordRepeat.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                changePasswordBinding.eye3.setImageResource(R.drawable.password_eye); // 替换为显示密码的图标
            } else {
                // 隐藏密码
                changePasswordBinding.newPasswordRepeat.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                changePasswordBinding.eye3.setImageResource(R.drawable.password_eye_off); // 替换为隐藏密码的图标
            }
        } else if (id == R.id.enter) {
            check();
        } else if (id == R.id.cancel) {
            changePasswordBinding.password.setText("");
            changePasswordBinding.newPassword.setText("");
            changePasswordBinding.newPasswordRepeat.setText("");
        }
    }

    private void check() {
        String pwd1 = changePasswordBinding.password.getText().toString();
        String pwd2 = changePasswordBinding.newPassword.getText().toString();
        String pwd3 = changePasswordBinding.newPasswordRepeat.getText().toString();
        if (!PasswordUtils.verifyPassword(mContext,pwd1)) {
            Toast.makeText(mContext,mContext.getText(R.string.password_error3),Toast.LENGTH_SHORT).show();
            changePasswordBinding.cancel.performClick();
            return;
        }
        if(!pwd2.equals(pwd3)) {
            Toast.makeText(mContext,mContext.getText(R.string.password_error4),Toast.LENGTH_SHORT).show();
            return;
        }

        PasswordUtils.setNewPassword(mContext,pwd3);

        Toast.makeText(mContext,mContext.getText(R.string.password_reboot_useful),Toast.LENGTH_SHORT).show();
        // 延时一点关闭当前 Dialog，给用户时间看到 Toast
        changePasswordBinding.getRoot().postDelayed(() -> {
            // 如果你是在 DialogFragment 里：
            dismiss();

        }, 500);
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        if (id == R.id.password) {
            changePasswordBinding.password.setHint("");
        } else if (id == R.id.new_password) {
            changePasswordBinding.newPassword.setHint("");
        } else if (id == R.id.new_password_repeat) {
            changePasswordBinding.newPasswordRepeat.setHint("");
        }
    }
}
