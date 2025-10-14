package com.htc.luminaos.activity;

import static android.provider.Settings.ACTION_ADD_ACCOUNT;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.htc.luminaos.R;
import com.htc.luminaos.adapter.AccoutsManagerAdapter;
import com.htc.luminaos.databinding.ActivityAccountBinding;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.widget.AccountSyncDialog;
import com.htc.luminaos.widget.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class AccountActivity extends BaseActivity implements AccountSyncDialog.AccountCallBack {

    ActivityAccountBinding accountBinding;
    private static String TAG = "AccountActivity";
    List<Account> accounts;
    AccoutsManagerAdapter adapter;
    private boolean sync = ContentResolver.getMasterSyncAutomaticallyAsUser(UserHandle.myUserId());
    UserHandle myUserHandle = android.os.Process.myUserHandle();
    private final BroadcastReceiver accountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isInitialStickyBroadcast()) {//把注册完就发送的粘性(初始状态)广播过滤掉。
                return;
            }
            String action = intent.getAction();
            if (AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION.equals(action) && adapter != null) {
                // 账号列表发生变化（添加、移除、更新等）
                LogUtils.d(TAG, "Google 账号发生变更 action" + action);
                List<Account> newAccounts = getDynamicRawDataToIndex(getApplicationContext());
                accounts.clear();
                accounts.addAll(newAccounts);
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountBinding = ActivityAccountBinding.inflate(LayoutInflater.from(this));
        setContentView(accountBinding.getRoot());
        initView();
        initData();
        initReceiver();
    }

    @Override
    protected void onResume() {
        sync = ContentResolver.getMasterSyncAutomaticallyAsUser(UserHandle.myUserId());
        checkSyncSwitch();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (accountReceiver != null) {
            unregisterReceiver(accountReceiver);
        }
        super.onDestroy();
    }

    private void initView() {
        accountBinding.rlSyncSwitch.setOnClickListener(this);
        accountBinding.syncSwitch.setOnClickListener(this);
        accountBinding.rlAccountAdd.setOnClickListener(this);

        accountBinding.rlSyncSwitch.setOnHoverListener(this);
        accountBinding.rlAccountAdd.setOnHoverListener(this);

        checkSyncSwitch();
    }

    private void checkSyncSwitch() {
        if (sync) {
            accountBinding.syncSwitch.setChecked(true);
        } else {
            accountBinding.syncSwitch.setChecked(false);
        }
    }

    private void initData() {
        accounts = getDynamicRawDataToIndex(getApplicationContext());
        adapter = new AccoutsManagerAdapter(this, accounts, accountBinding.accountRv, myUserHandle);
        accountBinding.accountRv.addItemDecoration(new SpacesItemDecoration(0,
                0, (int) getResources().getDimension(R.dimen.y_20), 0));
        accountBinding.accountRv.setAdapter(adapter);
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION);
        intentFilter.addAction(Intent.ACTION_DEVICE_STORAGE_OK);
        registerReceiverAsUser(accountReceiver, myUserHandle, intentFilter, null, null);
//        registerReceiver(accountReceiver, intentFilter);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.rl_sync_switch) {
            showDialog();
        } else if (viewId == R.id.sync_switch) {
            showDialog();
        } else if (viewId == R.id.rl_account_add) {
            LogUtils.d(TAG, " 添加谷歌账号");
//                String[] mAuthorities = new String[]{"com.google"};
            Intent intent = new Intent(ACTION_ADD_ACCOUNT);
//                intent.putExtra(EXTRA_USER, myUserHandle);
//                intent.putExtra(EXTRA_AUTHORITIES, mAuthorities);
            startActivity(intent);
        }
    }

    public List<Account> getDynamicRawDataToIndex(Context context) {
        final List<Account> indexRaws = new ArrayList<>();
        final UserManager userManager = (UserManager) context.getSystemService(
                Context.USER_SERVICE);
        final List<UserInfo> profiles = userManager.getProfiles(UserHandle.myUserId());
        for (final UserInfo userInfo : profiles) {
            if (userInfo.isManagedProfile()) {
                return indexRaws;
            }
        }

        final AccountManager accountManager = AccountManager.get(context);
        final Account[] accounts = accountManager.getAccounts();
        LogUtils.d(TAG, "getDynamicRawDataToIndex accounts length " + accounts.length);
        if (accounts.length == 0)
            return indexRaws;
        for (Account account : accounts) {
            indexRaws.add(account);
            LogUtils.d(TAG, "getDynamicRawDataToIndex account name " + account.name + " type " + account.type);
        }
        return indexRaws;
    }

    private void showDialog() {
        LogUtils.d(TAG, "showDialog sync" + sync);
        AccountSyncDialog accountSyncDialog = new AccountSyncDialog(this, !sync, myUserHandle, this);
        accountSyncDialog.show();
    }

    @Override
    public void setSwitch(boolean sync) {
        accountBinding.syncSwitch.setChecked(sync);
        this.sync = ContentResolver.getMasterSyncAutomaticallyAsUser(UserHandle.myUserId());
    }

}