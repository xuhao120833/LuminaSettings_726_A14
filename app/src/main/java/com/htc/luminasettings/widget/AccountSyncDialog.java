package com.htc.luminasettings.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.htc.luminasettings.R;
import com.htc.luminasettings.databinding.AccountSyncDialogBinding;

/**
 * Author:
 * Date:
 * Description:
 */
public class AccountSyncDialog extends BaseDialog implements View.OnClickListener {
    private Context mContext;
    private AccountSyncDialogBinding syncDialogBinding;
    private boolean sync;
    private UserHandle myUserHandle;
    private AccountCallBack accountCallBack;
    StringBuilder builder = new StringBuilder();

    public AccountSyncDialog(Context context,boolean sync,UserHandle myUserHandle,AccountCallBack accountCallBack) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.sync = sync;
        this.myUserHandle = myUserHandle;
        this.accountCallBack = accountCallBack;
    }

    public AccountSyncDialog(Context context, boolean cancelable,
                             OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public AccountSyncDialog(Context context, int theme) {
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
        super.onStart();
    }

    private void init() {
        syncDialogBinding = AccountSyncDialogBinding.inflate(LayoutInflater.from(mContext));
        /*View view = LayoutInflater.from(mContext).inflate(
                R.layout.wifi_settings_layout, null);*/
        if (syncDialogBinding.getRoot() != null) {
            setContentView(syncDialogBinding.getRoot());
            initView();
            // 设置dialog大小 模块好的控件大小设置
            Window dialogWindow = getWindow();
            WindowManager.LayoutParams wmlp = getWindow().getAttributes();
            wmlp.width = (int) mContext.getResources().getDimension(R.dimen.x_550);
//            wmlp.height = (int) mContext.getResources().getDimension(R.dimen.y_300);
            wmlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialogWindow.setGravity(Gravity.CENTER);
            dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogWindow.setDimAmount(0f);
            dialogWindow.setAttributes(wmlp);
            dialogWindow.setWindowAnimations(R.style.right_in_right_out_anim);


//            Window dialogWindow = getWindow();
//            if (dialogWindow != null) {
//                dialogWindow.setWindowAnimations(R.style.right_in_right_out_anim);
//                //去除系统自带的margin
//                dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                //设置dialog在界面中的属性
//                dialogWindow.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
//                //背景全透明
//                dialogWindow.setDimAmount(0f);
//            }
//            dialogWindow.setGravity(Gravity.CENTER);// 设置对话框位置
        }
    }

    private void initView(){
        syncDialogBinding.enter.setOnClickListener(this);
        syncDialogBinding.cancel.setOnClickListener(this);
        syncDialogBinding.enter.setOnHoverListener(this);
        syncDialogBinding.cancel.setOnHoverListener(this);

        if(sync) {
            syncDialogBinding.syncTitle.setText(mContext.getResources().getString(R.string.account_sync_title1));
            builder.append(mContext.getResources().getString(R.string.account_message1));
            builder.append(mContext.getResources().getString(R.string.account_message2));
            builder.append(mContext.getResources().getString(R.string.account_message3));
            syncDialogBinding.syncMessage.setText(builder.toString());
            builder.setLength(0);
        } else {
            syncDialogBinding.syncTitle.setText(mContext.getResources().getString(R.string.account_sync_title2));
            builder.append(mContext.getResources().getString(R.string.account_message4));
            builder.append(mContext.getResources().getString(R.string.account_message5));
            builder.append(mContext.getResources().getString(R.string.account_message6));
            syncDialogBinding.syncMessage.setText(builder.toString());
            builder.setLength(0);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.enter) {
            ContentResolver.setMasterSyncAutomaticallyAsUser(sync, myUserHandle.getIdentifier());
            dismiss();
            accountCallBack.setSwitch(sync);
        } else if (id == R.id.cancel) {
            ContentResolver.setMasterSyncAutomaticallyAsUser(sync, myUserHandle.getIdentifier());
            dismiss();
        }
    }

    public interface AccountCallBack {
        public void setSwitch(boolean sync);
    }

}
