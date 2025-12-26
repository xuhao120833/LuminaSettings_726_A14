package com.htc.luminasettings.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.htc.luminasettings.activity.BaseActivity;
import com.htc.luminasettings.databinding.ActivityBluetoothIncomingFileConfirmBinding;
import com.htc.luminasettings.receiver.BluetoothInformingCallback;
import com.htc.luminasettings.utils.LogUtils;
import com.htc.luminasettings.widget.BlutoothIncomingFileConfirmDialog;

public class BluetoothIncomingFileConfirmActivity extends BaseActivity implements View.OnClickListener, BluetoothInformingCallback {

    private static String TAG = "BluetoothIncomingFileConfirmActivity";
    private BluetoothDevice mDevice;
    private ActivityBluetoothIncomingFileConfirmBinding activityBluetoothIncomingFileConfirmBinding;

    protected ColorDrawable mBgDrawable = new ColorDrawable();
    private BlutoothIncomingFileConfirmDialog  blutoothIncomingFileConfirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (!BluetoothDevice.ACTION_INCOMINGFILE_CONFIRM_REQUEST.equals(intent.getAction())) {
            LogUtils.e(TAG, "Error: this activity may be started only with intent " +
                    BluetoothDevice.ACTION_INCOMINGFILE_CONFIRM_REQUEST);
            finish();
            return;
        }
        mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        blutoothIncomingFileConfirmDialog = new BlutoothIncomingFileConfirmDialog(this,intent,this);
        blutoothIncomingFileConfirmDialog.show();



//        activityBluetoothIncomingFileConfirmBinding = ActivityBluetoothIncomingFileConfirmBinding.inflate(LayoutInflater.from(this));
//        setContentView(activityBluetoothIncomingFileConfirmBinding.getRoot());
//
//        activityBluetoothIncomingFileConfirmBinding.accpet.setOnClickListener(this);
//        activityBluetoothIncomingFileConfirmBinding.cancel.setOnClickListener(this);
//        activityBluetoothIncomingFileConfirmBinding.accpet.setOnHoverListener(this);
//        activityBluetoothIncomingFileConfirmBinding.cancel.setOnHoverListener(this);
    }

    @Override
    public void finishActivity() {
        finish();
    }

//    @Override
//    public void onClick(View v) {
//        LogUtils.d(TAG,"onclick");
//        switch (v.getId()){
//            case R.id.accpet:
//                onTransfer();
//                Toast.makeText(this, getString(R.string.bluetooth_start_receive), Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.cancel:
//                CancelingTransfer();
//                break;
//        }
//    }


}