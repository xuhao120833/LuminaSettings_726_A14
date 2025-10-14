package com.htc.luminaos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.View;

import com.htc.luminaos.activity.MainActivity;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsbDeviceReceiver extends BroadcastReceiver {

    HashMap<String, UsbDevice> deviceHashMap;

    UsbDeviceCallBack callBack;

    MainActivity activity;

    private static String TAG = "UsbDeviceReceiver";

    private static String USB_ROOT = "/mnt/media_rw";

    public UsbDeviceReceiver(UsbDeviceCallBack callBack) {
        this.callBack = callBack;
        activity = (MainActivity) callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            LogUtils.d(TAG, "UsbDeviceReceiver UsbDevice收到广播 intent.getAction() " + intent.getAction());
            if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) { // U盘插入且挂载完毕
                StorageVolume storageVolume = (StorageVolume) intent.getParcelableExtra(StorageVolume.EXTRA_STORAGE_VOLUME);
                String path = storageVolume.getPath();
                LogUtils.d(TAG, "UsbDeviceReceiver storageVolume.getPath()" + path);
                if(isExternalStoragePath(path)) {
                    Utils.hasUsbDevice = true;
                    Utils.usbDevicesNumber++;
                    if(activity.customBinding.rlUsbConnect.getVisibility() == View.GONE) {
                        callBack.UsbDeviceChange();
                    }
                    LogUtils.d(TAG, "UsbDeviceReceiver 有USB设备插入挂载成功 显示U盘图标" + Utils.usbDevicesNumber);
                }
            } else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())
                    || Intent.ACTION_MEDIA_BAD_REMOVAL.equals(intent.getAction())) { //U盘拔出卸载完毕
                if (Utils.usbDevicesNumber > 1) {
                    Utils.usbDevicesNumber--;
                    Utils.hasUsbDevice = true;
                } else if (Utils.usbDevicesNumber == 1) {
                    Utils.usbDevicesNumber = 0;
                    Utils.hasUsbDevice = false;
                }
                int usbCount = countUsbDevices(context);
                LogUtils.d(TAG, "UsbDeviceReceiver countUsbDevices "+usbCount);
                if (usbCount==0){
                    Utils.hasUsbDevice = false;
                }
                if (!Utils.hasUsbDevice) {
                    LogUtils.d(TAG, "UsbDeviceReceiver USB设备已经全部拔出 隐藏U盘图标");
                    callBack.UsbDeviceChange();
                }
                LogUtils.d(TAG, "UsbDeviceReceiver 有USB设备拔出卸载 " + Utils.usbDevicesNumber);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int countUsbDevices(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> volumes = storageManager.getStorageVolumes();
        int usbCount = 0;

        for (StorageVolume volume : volumes) {
            if (volume.isRemovable()) {
                usbCount++;
            }
        }
        LogUtils.d(TAG, "checkUsb  开机检测到 "+usbCount+" 个U盘");
        return usbCount;
    }

    public boolean isExternalStoragePath(String path) {
        if(path.equals("/storage/emulated/0")){
            return false;
        }
        return true;
    }

}
