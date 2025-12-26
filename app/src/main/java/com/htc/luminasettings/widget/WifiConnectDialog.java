package com.htc.luminasettings.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.htc.luminasettings.R;
import com.htc.luminasettings.databinding.WifiConnectDialogBinding;
import com.htc.luminasettings.utils.LogUtils;
import com.htc.luminasettings.utils.Utils;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Author:
 * Date:
 * Description:
 */
public class WifiConnectDialog extends BaseDialog implements View.OnClickListener {
    private Context mContext;
    private static String TAG = "WifiConnectDialog";
    private View parent;
    private WifiConnectDialogBinding wifiConnectDialogBinding;
    private String wifi_name = "unknow";
    private OnWifiConnectCallBack mcallback;

    ConnectivityManager connectivityManager;
    WifiManager wifiManager;

    public int networkId = -1;
    //当前wifi的链接回调
    public boolean hasRemove = false;
    public boolean connectFlag = false;
    Dialog connectingDialog = null;
    Dialog passwordErrorDialog = null;

    Executor threads = Executors.newCachedThreadPool();
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                if (hasRemove && connectFlag) {
                    wifiManager.removeNetwork(networkId);
                    networkId = -1;
                }
            } else if (msg.what == 2) {
                connectFlag = true;
                networkId = mcallback.onClick(wifiConnectDialogBinding.etPassword.getText().toString());
            }
            return false;
        }
    });

    @Override
    public void onClick(View v) {
        LogUtils.d("xuhao", "onclick");
        int id = v.getId();
        if (id == R.id.enter) {
            if (wifiConnectDialogBinding.etPassword.getText().toString().isEmpty() ||
                    wifiConnectDialogBinding.etPassword.getText().toString().length() < 8) {
                wifiConnectDialogBinding.errMsg.setVisibility(View.VISIBLE);
                return;
            }

            //dismiss();
            connectingDialog = ConectingDialog(mContext, mContext.getString(R.string.connecting_ssid, wifi_name));
            connectingDialog.show();
            threads.execute(new Runnable() {
                @Override
                public void run() {
                    disEnableNetwork();
                }
            });
            handler.sendEmptyMessageDelayed(2, 1500); //1.5s之后去连接网络
        } else if (id == R.id.cancel) {
            dismiss();
        } else if (id == R.id.password_visibility) {
            LogUtils.d(TAG, " wifiConnectDialogBinding.etPassword.getInputType() " + wifiConnectDialogBinding.etPassword.getInputType());
            if (wifiConnectDialogBinding.etPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // 显示密码
                wifiConnectDialogBinding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                wifiConnectDialogBinding.passwordVisibility.setImageResource(R.drawable.password_eye); // 替换为显示密码的图标
            } else {
                // 隐藏密码
                wifiConnectDialogBinding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                wifiConnectDialogBinding.passwordVisibility.setImageResource(R.drawable.password_eye_off); // 替换为隐藏密码的图标
            }
        }
    }

    public interface OnWifiConnectCallBack {
        public int onClick(String password);
    }

    public WifiConnectDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public WifiConnectDialog(Context context, boolean cancelable,
                             DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public WifiConnectDialog(Context context, int theme, View parent) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.parent = parent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        init();
    }

    private void init() {
        wifiConnectDialogBinding = WifiConnectDialogBinding.inflate(LayoutInflater.from(mContext));
        /*View view = LayoutInflater.from(mContext).inflate(
                R.layout.wifi_settings_layout, null);*/
        if (wifiConnectDialogBinding.getRoot() != null) {
            setContentView(wifiConnectDialogBinding.getRoot());
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
        initReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        destroyReceiver();
    }


    boolean disEnable = false;

    private void disEnableNetwork() {
        disEnable = true;
        List<WifiConfiguration> configurationList = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configurationList) {
            wifiManager.disableNetwork(config.networkId);
        }

    }

    private void EnableNetwork() {
        if (!disEnable)
            return;

        List<WifiConfiguration> configurationList = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configurationList) {
            wifiManager.enableNetwork(config.networkId, true);
        }
    }


    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mContext.registerReceiver(receiver, intentFilter);
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isInitialStickyBroadcast()) {//把注册完就发送的粘性(初始状态)广播过滤掉。
                return;
            }
            String action = intent.getAction();
            if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                //请求连接的状态发生改变，（已经加入到一个接入点）
                int supl_error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                LogUtils.d(TAG, " 收到SUPPLICANT_STATE_CHANGED_ACTION "+supl_error);
                if (supl_error == WifiManager.ERROR_AUTHENTICATING) {
                    Utils.logIntentExtras(intent, TAG);
                    LogUtils.d(TAG, " 收到SUPPLICANT_STATE_CHANGED_ACTION,执行passwordErrorDialog " + supl_error);
                    if (passwordErrorDialog == null) {
                        passwordErrorDialog = new PasswordErrorDialog(mContext, R.style.DialogTheme);
                    }
                    if (!passwordErrorDialog.isShowing())
                        showErrorDialog();
//                        passwordErrorDialog.show();
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                if (networkInfo != null) {
                    switch (networkInfo.getState()) {
                        case CONNECTING:
                            hasRemove = true;
                            break;
                        case CONNECTED:
                            if (hasRemove && connectFlag) {
                                //不需要清除保存信息，重置false
                                hasRemove = false;
                                connectFlag = false;
                                hideConnectingDialog();
                                dismiss();
                            }
                            break;
                        case DISCONNECTED:
                            if (hasRemove && connectFlag) {
                                hideConnectingDialog();
                                wifiManager.removeNetwork(networkId);
                                networkId = -1;
                                hasRemove = false;
                                connectFlag = false;
                            }
                            break;
                    }

                    LogUtils.d("WIFI", "state " + networkInfo.getState());
                }

            }
        }
    };

    private void destroyReceiver() {
        mContext.unregisterReceiver(receiver);
    }


    public void setConnectName(String name) {
        this.wifi_name = name;
    }

    private void initView() {
        wifiConnectDialogBinding.enter.setOnClickListener(this);
        wifiConnectDialogBinding.cancel.setOnClickListener(this);
        wifiConnectDialogBinding.enter.setOnHoverListener(this);
        wifiConnectDialogBinding.cancel.setOnHoverListener(this);
        wifiConnectDialogBinding.passwordVisibility.setOnClickListener(this);
        wifiConnectDialogBinding.passwordVisibility.setOnHoverListener(this);

        wifiConnectDialogBinding.enter.setEnabled(false);
        wifiConnectDialogBinding.connectWifiName.setText(mContext.getString(R.string.network_connect_tips, wifi_name));
        wifiConnectDialogBinding.checkboxShow
                .setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton arg0,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        if (!isChecked) {
                            // 如果选中，显示密码
                            wifiConnectDialogBinding.etPassword
                                    .setTransformationMethod(HideReturnsTransformationMethod
                                            .getInstance());
                        } else {
                            // 否则隐藏密码
                            wifiConnectDialogBinding.etPassword
                                    .setTransformationMethod(PasswordTransformationMethod
                                            .getInstance());

                        }

                    }
                });
        wifiConnectDialogBinding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wifiConnectDialogBinding.enter.setEnabled(s.length() >= 8);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        wifiConnectDialogBinding.etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                LogUtils.d(TAG, "actionId " + actionId);
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_GO) {
                    //隐藏软键盘
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    v.clearFocus();
                    wifiConnectDialogBinding.enter.requestFocus();
                    wifiConnectDialogBinding.enter.performClick();
//                    Toast.makeText(mContext, " 生效了Baby ", Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });

    }


    public Dialog ConectingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.loadding_layout);// 加载布局

        //改成用ProgressBar来实现旋转动画，不用原来的ImageView旋转方案，原来的会卡。2025/5/8
        // main.xml中的ImageView
//        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.loadding_iv);
        TextView tipTextView = (TextView) v.findViewById(R.id.loadding_tv);// 提示文字
        // 加载动画
//        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
//                context, R.anim.loading_animation);
//        // 使用ImageView显示动画
//        hyperspaceJumpAnimation.setInterpolator(new LinearInterpolator());
//        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
//        startanim(true,spaceshipImage);

        tipTextView.setText(msg);// 设置加载信息
        Dialog connectingDialog = new Dialog(context, R.style.DialogTheme);// 创建自定义样式dialog
        connectingDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.sendEmptyMessage(1);
            }
        });
        connectingDialog.setContentView(layout, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT));// 设置布局
        return connectingDialog;
    }

    public void hideConnectingDialog() {
        if (connectingDialog != null && connectingDialog.isShowing()) {
            connectingDialog.dismiss();
        }
    }

    public void hidePasswordErrorDialog() {
        if (passwordErrorDialog != null && passwordErrorDialog.isShowing()) {
            passwordErrorDialog.dismiss();
        }
    }


    public void setOnClickCallBack(OnWifiConnectCallBack callback) {
        this.mcallback = callback;
    }

    @Override
    public void dismiss() {
        EnableNetwork();
        handler.removeCallbacksAndMessages(null);
        super.dismiss();
    }

    private void showErrorDialog() {
        LogUtils.d(TAG, " showErrorDialog");
        GradientDrawable dialogBackground = new GradientDrawable();
        dialogBackground.setColor(Color.WHITE); // 对话框背景色
        dialogBackground.setCornerRadius(mContext.getResources().getDimension(R.dimen.x_15));
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.hint)) // 对话框标题
                .setMessage(mContext.getString(R.string.Authentication_error)) // 对话框内容
                .setPositiveButton(mContext.getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtils.d(TAG, " showErrorDialog onClick dialog.dismiss()");
                        dialog.dismiss(); // 点击“确定”按钮时，关闭对话框
                    }
                })
                .setCancelable(false) // 使对话框不能通过点击外部区域关闭
                .create();

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    LogUtils.d(TAG, " showErrorDialog KEYCODE_BACK dialog.dismiss()");
                    dialog.dismiss(); // 按下返回键时关闭对话框
                    return true; // 表示已经处理了返回键事件
                }
                return false; // 没有处理返回键事件，继续传递事件
            }
        });

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(dialogBackground);
        }

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (positiveButton != null) {
            GradientDrawable buttonBackground = new GradientDrawable();
            buttonBackground.setColor(Color.parseColor("#4684f3")); // 蓝色背景
            buttonBackground.setCornerRadius(mContext.getResources().getDimension(R.dimen.x_15)); // 同样的圆角
            positiveButton.setBackground(buttonBackground);

            positiveButton.setTextColor(Color.WHITE);
            positiveButton.setPadding(40, 20, 40, 20);
        }
    }

    private void startanim(boolean startornot,ImageView spaceshipImage ) {
        if (!startornot) {
            // 使用属性动画实现平滑过渡
            animateViewVisibility(spaceshipImage, View.GONE);
            return;
        }

        // 改用属性动画
        animateViewVisibility(spaceshipImage, View.VISIBLE);
        startPropertyAnimation(spaceshipImage);
    }

    private void animateViewVisibility(View view, int visibility) {
        if (view.getVisibility() == visibility) return;

        float startAlpha = visibility == View.VISIBLE ? 0f : 1f;
        float endAlpha = visibility == View.VISIBLE ? 1f : 0f;

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", startAlpha, endAlpha);
        animator.setDuration(200); // 适当缩短过渡时间
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(visibility);
            }
        });
        animator.start();
    }

    private void startPropertyAnimation(ImageView spaceshipImage) {
        // 使用性能更好的属性动画
        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(
                spaceshipImage,
                "rotation",
                0f,
                360f
        );

        rotationAnim.setDuration(1000); // 适当调整持续时间
        rotationAnim.setInterpolator(new LinearInterpolator());
        rotationAnim.setRepeatCount(ValueAnimator.INFINITE);

        // 启用硬件加速层
        spaceshipImage.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        rotationAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                spaceshipImage.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        });
        rotationAnim.start();
    }

}
