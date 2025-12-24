package com.htc.luminaos.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.LayoutInflater;
import android.view.View;

import com.htc.luminaos.R;
import com.htc.luminaos.activity.BaseActivity;
import com.htc.luminaos.databinding.ActivityPrivacyTermsBinding;
import com.htc.luminaos.receiver.UcOffCallBack;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.widget.ExportDataDialog;
import com.htc.luminaos.widget.UcOffDialog;

public class PrivacyTermsActivity extends BaseActivity implements UcOffCallBack {
    private static String TAG = "PrivacyTermsActivity";
    private ActivityPrivacyTermsBinding termsBinding;
    private ExportDataDialog exportDataDialog;
    private UcOffDialog ucOffDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        termsBinding = ActivityPrivacyTermsBinding.inflate(LayoutInflater.from(getApplicationContext()));
        setContentView(termsBinding.getRoot());
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setVisibility();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initView() {
        termsBinding.rlUserConsent.setOnClickListener(this);
        termsBinding.rlExportData.setOnClickListener(this);
        termsBinding.rlUcOff.setOnClickListener(this);

        termsBinding.rlUserConsent.setOnHoverListener(this);
        termsBinding.rlExportData.setOnHoverListener(this);
        termsBinding.rlUcOff.setOnHoverListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_user_consent) {
            Intent intent = new Intent("com.htc.privacyPolicy");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (id == R.id.rl_export_data) {
            showExportDataDialog();
        } else if (id == R.id.rl_uc_off) {
            showUcOffDialog();
        }
    }

    private void showExportDataDialog() {
        if (exportDataDialog == null) {
            exportDataDialog = new ExportDataDialog(PrivacyTermsActivity.this);
        }
        if (!exportDataDialog.isShowing())
            exportDataDialog.show();
    }

    private void showUcOffDialog() {
        if (ucOffDialog == null) {
            ucOffDialog = new UcOffDialog(PrivacyTermsActivity.this);
        }
        if (!ucOffDialog.isShowing())
            ucOffDialog.show();
    }

    @Override
    public void setVisibility() {
        if (SystemProperties.getInt("sys.implicit.policy", 0) == 1) {
            termsBinding.rlUserConsent.setVisibility(View.GONE);
            termsBinding.rlExportData.setVisibility(View.VISIBLE);
            termsBinding.rlUcOff.setVisibility(View.VISIBLE);
        } else {
            termsBinding.rlUserConsent.setVisibility(View.VISIBLE);
            termsBinding.rlExportData.setVisibility(View.GONE);
            termsBinding.rlUcOff.setVisibility(View.GONE);
        }
    }
}