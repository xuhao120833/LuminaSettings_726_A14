package com.htc.luminaos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.htc.luminaos.R;
import com.htc.luminaos.databinding.ActivityPasswordVerifyBinding;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.PasswordUtils;


public class PasswordVerifyActivity extends AppCompatActivity implements View.OnKeyListener, View.OnHoverListener, View.OnClickListener, View.OnFocusChangeListener {

    private ActivityPasswordVerifyBinding passwordVerifyBinding;
    private static String TAG = "PasswordVerifyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passwordVerifyBinding = ActivityPasswordVerifyBinding.inflate(LayoutInflater.from(this));
        setContentView(passwordVerifyBinding.getRoot());
        initView();
    }

    private void initView() {
        passwordVerifyBinding.rlEye1.setOnClickListener(this);
        passwordVerifyBinding.rlEye2.setOnClickListener(this);
        passwordVerifyBinding.enter.setOnClickListener(this);
        passwordVerifyBinding.cancel.setOnClickListener(this);

        passwordVerifyBinding.rlEye1.setOnHoverListener(this);
        passwordVerifyBinding.rlEye2.setOnHoverListener(this);
        passwordVerifyBinding.enter.setOnHoverListener(this);
        passwordVerifyBinding.cancel.setOnHoverListener(this);

        passwordVerifyBinding.Password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPasswordLength();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        passwordVerifyBinding.Password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPasswordLength();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        passwordVerifyBinding.Password1.setOnFocusChangeListener(this);
        passwordVerifyBinding.Password2.setOnFocusChangeListener(this);

        passwordVerifyBinding.rlEye1.requestFocus();
        passwordVerifyBinding.title3.setSelected(true);

    }

    private void checkPasswordLength() {
        String pwd1 = passwordVerifyBinding.Password1.getText().toString();
        String pwd2 = passwordVerifyBinding.Password2.getText().toString();
        boolean bothValid = pwd1.length() >= 4 && pwd2.length() >= 4;
        passwordVerifyBinding.enter.setEnabled(bothValid);
    }

    private void initData() {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // 拦截 Back 键
            return true;
        }
        return super.dispatchKeyEvent(event); // 其他按键继续正常处理
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cancel) {
            passwordVerifyBinding.Password1.setText("");
            passwordVerifyBinding.Password2.setText("");
        } else if (id == R.id.enter) {
            checkPassword();
        } else if (id == R.id.rl_eye1) {
            LogUtils.d(TAG, " wifiConnectDialogBinding.etPassword.getInputType() " + passwordVerifyBinding.Password1.getInputType());
            if (passwordVerifyBinding.Password1.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // 显示密码
                passwordVerifyBinding.Password1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordVerifyBinding.eye1.setImageResource(R.drawable.password_eye); // 替换为显示密码的图标
            } else {
                // 隐藏密码
                passwordVerifyBinding.Password1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordVerifyBinding.eye1.setImageResource(R.drawable.password_eye_off); // 替换为隐藏密码的图标
            }
        } else if (id == R.id.rl_eye2) {
            LogUtils.d(TAG, " wifiConnectDialogBinding.etPassword.getInputType() " + passwordVerifyBinding.Password2.getInputType());
            if (passwordVerifyBinding.Password2.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // 显示密码
                passwordVerifyBinding.Password2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordVerifyBinding.eye2.setImageResource(R.drawable.password_eye); // 替换为显示密码的图标
            } else {
                // 隐藏密码
                passwordVerifyBinding.Password2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordVerifyBinding.eye2.setImageResource(R.drawable.password_eye_off); // 替换为隐藏密码的图标
            }
        }
    }

    private void checkPassword() {
        String password1 = passwordVerifyBinding.Password1.getText().toString();
        String password2 = passwordVerifyBinding.Password2.getText().toString();
        if(!password1.equals(password2)) {
            Toast.makeText(getApplicationContext(),getText(R.string.password_error),Toast.LENGTH_SHORT).show();
            passwordVerifyBinding.cancel.performClick();
            return;
        }

        PasswordUtils.setNewPassword(getApplicationContext(),password1);
        LogUtils.d(TAG," startNewActivity(MainActivity.class) ");
        PasswordUtils.sessionVerified = true;
        startNewActivity(MainActivity.class);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        if (id == R.id.Password1) {
            passwordVerifyBinding.Password1.setHint("");
        } else if (id == R.id.Password2) {
            passwordVerifyBinding.Password2.setHint("");
        }

    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        int what = event.getAction();
        switch (what) {
            case MotionEvent.ACTION_HOVER_ENTER: // 鼠标进入view
                v.requestFocus();
                break;
            case MotionEvent.ACTION_HOVER_MOVE: // 鼠标在view上
                break;
            case MotionEvent.ACTION_HOVER_EXIT: // 鼠标离开view
                break;
        }
        return false;
    }

    public void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}