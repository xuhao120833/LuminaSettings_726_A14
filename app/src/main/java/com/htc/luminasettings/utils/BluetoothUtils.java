package com.htc.luminasettings.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothUtils {

	public static BluetoothUtils mInstance = null;
	private BluetoothAdapter mBluetoothAdapter;
	private Context mContext;

	@SuppressLint("NewApi")
	private BluetoothUtils(Context context) {
		this.mContext = context;
		BluetoothManager bluetoothManager = (BluetoothManager) mContext
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
	}

	public static BluetoothUtils getInstance(Context context) {
		BluetoothUtils inst = mInstance;
		if (inst == null) {
			synchronized (BluetoothUtils.class) {
				inst = mInstance;
				if (inst == null) {
					inst = new BluetoothUtils(context);
					mInstance = inst;
				}
			}
		}
		return inst;
	}

	/**
	 * 获取当前连接的蓝牙设备
	 * 
	 * @return
	 */
//	public List<BluetoothDevice> getCurrentConnectDevice() {
//		if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
//			Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;
//			try {// 得到蓝牙状态的方法
//				Method method = bluetoothAdapterClass.getDeclaredMethod(
//						"getConnectionState", (Class[]) null);
//				// 打开权限
//				method.setAccessible(true);
//				int state = (Integer) method.invoke(mBluetoothAdapter, (Object[]) null);
//				if (state == BluetoothAdapter.STATE_CONNECTED) {
//					List<BluetoothDevice> deviceconnectList = new ArrayList<BluetoothDevice>();
//					Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
//					for (BluetoothDevice device : devices) {
//						Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected",
//										(Class[]) null);
//						method.setAccessible(true);
//						boolean isConnected = (Boolean) isConnectedMethod
//								.invoke(device, (Object[]) null);
//						if (isConnected) {
//							// L.i("Connected : " + device.getAddress());
//							deviceconnectList.add(device);
//						}
//					}
//					return deviceconnectList;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				return null;
//			}
//		}
//		return null;
//	}

	private List<BluetoothDevice> getCurrentConnectDevice() {
		Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;// 得到BluetoothAdapter的Class对象
		try {// 得到蓝牙状态的方法
            /*Method method = bluetoothAdapterClass.getDeclaredMethod(
                    "getConnectionState", (Class[]) null);
            // 打开权限
            method.setAccessible(true);
            int state = (Integer) method.invoke(bluetoothAdapter,
                    (Object[]) null);*/
			List<BluetoothDevice> deviceconnectList = new ArrayList<BluetoothDevice>();
			Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
			for (BluetoothDevice device : devices) {
				Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
				boolean isConnected = (Boolean) isConnectedMethod.invoke(device, (Object[]) null);
				if (isConnected) {
					if (device != null) {
						deviceconnectList.add(device);
					}
				}
			}
			return deviceconnectList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean isBluetoothConnected() {
		List<BluetoothDevice> mDevice = getCurrentConnectDevice();
		if (mDevice != null && mDevice.size() > 0) {
			return true;
		}
		return false;
	}

}
