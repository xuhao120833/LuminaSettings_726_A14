package com.htc.luminaos.widget;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.htc.luminaos.R;
import com.htc.luminaos.activity.AccountActivity;
import com.htc.luminaos.databinding.AccountDeleteDialogBinding;
import com.htc.luminaos.utils.LogUtils;

/**
 * Author:
 * Date:
 * Description:
 */
public class AccountDeleteDialog extends BaseDialog implements View.OnClickListener {
    private Context mContext;
    private AccountDeleteDialogBinding deleteDialogBinding;
    private Account account;
    private UserHandle mUserHandle;
    private static String TAG = "AccountDeleteDialog";

    public AccountDeleteDialog(Context context, Account account, UserHandle mUserHandle) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.account = account;
        this.mUserHandle = mUserHandle;
    }

    public AccountDeleteDialog(Context context, boolean cancelable,
                               OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public AccountDeleteDialog(Context context, int theme) {
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
        deleteDialogBinding = AccountDeleteDialogBinding.inflate(LayoutInflater.from(mContext));
        /*View view = LayoutInflater.from(mContext).inflate(
                R.layout.wifi_settings_layout, null);*/
        if (deleteDialogBinding.getRoot() != null) {
            setContentView(deleteDialogBinding.getRoot());
            initView();
            // 设置dialog大小 模块好的控件大小设置
            Window dialogWindow = getWindow();
            WindowManager.LayoutParams wmlp = getWindow().getAttributes();
            wmlp.width = (int) mContext.getResources().getDimension(R.dimen.x_500);
            wmlp.height = (int) mContext.getResources().getDimension(R.dimen.y_300);
            dialogWindow.setGravity(Gravity.CENTER);
            dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogWindow.setDimAmount(0f);
            dialogWindow.setAttributes(wmlp);
            dialogWindow.setWindowAnimations(R.style.right_in_right_out_anim);
        }
    }

    private void initView(){
        deleteDialogBinding.enter.setOnClickListener(this);
        deleteDialogBinding.cancel.setOnClickListener(this);
        deleteDialogBinding.enter.setOnHoverListener(this);
        deleteDialogBinding.cancel.setOnHoverListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.enter) {
            AccountManager.get(mContext).removeAccountAsUser(account, (AccountActivity) mContext, callback, null, mUserHandle);
            dismiss();
        } else if (id == R.id.cancel) {
            dismiss();
        }
    }

    AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {
        @Override
        public void run(AccountManagerFuture<Bundle> future) {
            try {
                Bundle result = future.getResult();
                boolean success = result.getBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
                LogUtils.d(TAG, "AccountManagerCallback  success"+success);
                if (success) {
                    LogUtils.d(TAG, "账号删除成功！");
                } else {
                    LogUtils.d(TAG, "账号删除失败！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
