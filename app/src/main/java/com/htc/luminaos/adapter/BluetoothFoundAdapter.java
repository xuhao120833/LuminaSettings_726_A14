package com.htc.luminaos.adapter;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htc.luminaos.R;
import com.htc.luminaos.utils.ClsUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:
 * Date:
 * Description:
 */
public class BluetoothFoundAdapter extends RecyclerView.Adapter<BluetoothFoundAdapter.MyViewHolder> implements View.OnHoverListener {

    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private Context mContext;
    private BluetoothDevice CurrentPair;
    private static String TAG = "BluetoothFoundAdapter";
    private BluetoothAdapter bluetoothAdapter;

    public BluetoothFoundAdapter(List<BluetoothDevice> deviceList, Context mContext, BluetoothAdapter bluetoothAdapter) {
        this.deviceList = deviceList;
        this.mContext = mContext;
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public void updateList(List<BluetoothDevice> deviceList) {
        this.deviceList = deviceList;
    }

    public void setCurrentPair(BluetoothDevice CurrentPair) {
        this.CurrentPair = CurrentPair;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ble_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        try {
            final BluetoothDevice device = deviceList.get(i);
            myViewHolder.ble_name.setText(device.getName());
            if (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PHONE) {
                myViewHolder.ble_type.setImageResource(R.drawable.bluetooth);
            } else if (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.COMPUTER) {
                myViewHolder.ble_type.setImageResource(R.drawable.bluetooth);
            } else if (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.AUDIO_VIDEO) {
                myViewHolder.ble_type.setImageResource(R.drawable.bluetooth);
            } else if (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PERIPHERAL) {
                switch (device.getBluetoothClass().getDeviceClass()) {
                    case BluetoothClass.Device.PERIPHERAL_KEYBOARD:
                    case BluetoothClass.Device.PERIPHERAL_KEYBOARD_POINTING:
                        // viewHolder.pair_iv.setImageResource(R.drawable.ic_lockscreen_ime);
                        myViewHolder.ble_type.setImageResource(R.drawable.bluetooth);
                        break;
                    case BluetoothClass.Device.PERIPHERAL_POINTING:
                        myViewHolder.ble_type
                                .setImageResource(R.drawable.bluetooth);
                        break;
                    default:
                        myViewHolder.ble_type.setImageResource(R.drawable.bluetooth);
                        break;
                }
            } else {
                myViewHolder.ble_type.setImageResource(R.drawable.bluetooth);
            }
            myViewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
//                    boolean result = ClsUtils.createBond(device.getClass(),
//                            device);
                        boolean result = startPairing(device);
                        if (result) {
                            Log.i(TAG, "配对成功!");
                        } else {
                            Log.i(TAG, "配对失败!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            if (CurrentPair != null && CurrentPair.getAddress().equals(device.getAddress())) {
                myViewHolder.ble_status.setText(mContext.getString(R.string.pairing));
                myViewHolder.ble_status.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.ble_status.setVisibility(View.GONE);
            }
            myViewHolder.rl_item.setOnHoverListener(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean startPairing(BluetoothDevice mDevice) {
        // Pairing is unreliable while scanning, so cancel discovery
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        if (!mDevice.createBond()) {
            return false;
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ble_type;
        TextView ble_name;
        TextView ble_status;
        RelativeLayout rl_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ble_type = itemView.findViewById(R.id.ble_type);
            ble_name = itemView.findViewById(R.id.ble_name);
            ble_status = itemView.findViewById(R.id.ble_status);
            rl_item = itemView.findViewById(R.id.rl_item);
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

    void stopScanning(BluetoothAdapter mBluetoothAdapter) {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }
}
