package com.htc.luminaos.activity;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidHost;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;

import com.htc.luminaos.R;
import com.htc.luminaos.adapter.BluetoothBondAdapter;
import com.htc.luminaos.adapter.BluetoothFoundAdapter;
import com.htc.luminaos.databinding.ActivityBluetoothBinding;
import com.htc.luminaos.receiver.BluetoothCallBcak;
import com.htc.luminaos.receiver.BluetoothReceiver;
import com.htc.luminaos.receiver.BondStateCallBack;
import com.htc.luminaos.receiver.BondStateReceiver;
import com.htc.luminaos.receiver.MyBlueBoothCallBack;
import com.htc.luminaos.receiver.MyBlueBoothReceiver;
import com.htc.luminaos.utils.Contants;
import com.htc.luminaos.widget.SpacesItemDecoration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothActivity extends BaseActivity implements BluetoothCallBcak, BondStateCallBack, MyBlueBoothCallBack {

    private ActivityBluetoothBinding bluetoothBinding;

    private BluetoothFoundAdapter foundAdapter;
    private BluetoothBondAdapter bondAdapter;

    private BluetoothAdapter bluetoothAdapter;

    private IntentFilter mCanFilter = new IntentFilter();

    public static BluetoothA2dp a2dp;
    private static String TAG = "BluetoothActivity";

    /**
     * 配对广播
     */
    private IntentFilter bondFilter = new IntentFilter(
            BluetoothDevice.ACTION_BOND_STATE_CHANGED);
    private BondStateReceiver bondReceiver = null;

    /**
     * 蓝牙连接状态
     */
    private IntentFilter blueFilter = new IntentFilter();
    private BluetoothReceiver blueReceiver = null;

    // bluebooth开关
    private IntentFilter blueBoothFilter = new IntentFilter(
            BluetoothAdapter.ACTION_STATE_CHANGED);
    private MyBlueBoothReceiver blueBoothReceiver = null;
    private List<BluetoothDevice> scanList = new ArrayList<>();
    private List<BluetoothDevice> bondList = new ArrayList<>();
    private BluetoothDevice currentDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothBinding = ActivityBluetoothBinding.inflate(LayoutInflater.from(this));
        setContentView(bluetoothBinding.getRoot());
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.getProfileProxy(this, mProfileServiceListener,  //蓝牙音响
                BluetoothProfile.A2DP);
        bluetoothAdapter.getProfileProxy(this,  //蓝牙耳机
                mListener, 4);
        initReceiver();
        initView();
        initData();
    }

    private void initView() {
        bluetoothBinding.rlBluetoothSwitch.setOnClickListener(this);
        bluetoothBinding.rlBluetoothSwitch.setOnHoverListener(this);
        bluetoothBinding.bluetoothSwitch.setOnClickListener(this);
        bluetoothBinding.rlSearchBle.setOnClickListener(this);
        bluetoothBinding.rlSearchBle.setOnHoverListener(this);
        bluetoothBinding.pairRv.setItemAnimator(null);
        bluetoothBinding.pairRv.addItemDecoration(new SpacesItemDecoration(0, 0, SpacesItemDecoration.px2dp(4), 0));
        bluetoothBinding.availableRv.setItemAnimator(null);
        bluetoothBinding.availableRv.addItemDecoration(new SpacesItemDecoration(0, 0, SpacesItemDecoration.px2dp(4), 0));
        bluetoothBinding.bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!bluetoothAdapter.isEnabled())
                        bluetoothAdapter.enable();
                } else {
                    if (bluetoothAdapter.isEnabled())
                        bluetoothAdapter.disable();
                }
            }
        });
    }

    private void initData() {
        if (bluetoothAdapter.isEnabled()) {
            bluetoothBinding.bluetoothSwitch.setChecked(true);
            scanList.clear();
            if (foundAdapter != null)
                foundAdapter.notifyDataSetChanged();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14 对应的版本号是 34
//                // Android 14 及以上，使用新的 setScanMode 方法
            bluetoothAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
//            } else {
//                // Android 14 以下，使用旧的 setScanMode 方法
//                bluetoothAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 0);
//            }
            handler.removeCallbacks(discoveryRunnable);
            handler.postDelayed(discoveryRunnable, 3000);//蓝牙延迟搜索，不然蓝牙音箱的回链不上
            updatePairList();
            updateView(true);
        } else {
            handler.removeCallbacks(discoveryRunnable);
            bluetoothBinding.bluetoothSwitch.setChecked(false);
            updateView(false);
        }
    }


    /**
     * 获取可用蓝牙列表
     */
    private BroadcastReceiver mCanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            // 获得已经搜索到的蓝牙设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 搜索到的不是已经绑定的蓝牙设备
                if (device.getBondState() != BluetoothDevice.BOND_BONDED && device.getName() != null) {
                    Log.i(TAG, "device " + device.getName());
                    if (!scanList.contains(device) && !device.getName().isEmpty()) {
                        scanList.add(device);
                        Message message = handler.obtainMessage();
                        message.what = Contants.REFRESH_FOUND;
                        handler.sendMessage(message);
                    }
                }
                // 搜索完成
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                searchAnim(false);
            }
        }
    };


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            BluetoothDevice device = (BluetoothDevice) msg.obj;
            switch (msg.what) {
                case Contants.BOND_SUCCESSFUL:
                    if (scanList.contains(device)) {
                        View view = getCurrentFocus();
                        if (view != null)
                            view.clearFocus();

                        scanList.remove(device);
                        foundAdapter.notifyDataSetChanged();
                    }
                    if (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.AUDIO_VIDEO) {
                        currentDevice = device;
                    }
                    updatePairList();
                    // 音频设备，配对成功后需要进行连接
//                    if (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.AUDIO_VIDEO) {
//                        connectDeviceFromA2DP(device);
//                    } else if (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PERIPHERAL) {
//                        if (isKeyboardDevice(device.getUuids())) {
//                            Log.i("zouguanrong", "-----connectKeyboard----");
//                            connectKeyboard(device);
//                        } else {
//                            connectDeviceFromA2DP(device);
//                        }
//                    }
                    connectDevice(device);
                    break;
                case Contants.BONDING:
                    if (scanList.contains(device)) {
                        foundAdapter.setCurrentPair(device);
                        foundAdapter.notifyDataSetChanged();
                    }
                    break;
                case Contants.BOND_FAIL:
                    if (foundAdapter != null) {
                        foundAdapter.setCurrentPair(null);
                        foundAdapter.notifyDataSetChanged();
                    }
                    updatePairList();
                    break;
                case Contants.REFRESH_FOUND:
                    if (foundAdapter == null) {
                        foundAdapter = new BluetoothFoundAdapter(scanList, BluetoothActivity.this,bluetoothAdapter);
                        foundAdapter.setHasStableIds(true);
                        bluetoothBinding.availableRv.setAdapter(foundAdapter);
                    } else {
                        foundAdapter.updateList(scanList);
                        foundAdapter.notifyDataSetChanged();
                    }
                    break;
                case Contants.REFRESH_PAIR:
                    updatePairList();
                    handler.sendEmptyMessageDelayed(Contants.REFRESH_PAIR, 5000);
                    break;

            }

            return false;
        }
    });

    private void connectDevice(BluetoothDevice mDevice) {
        if (!ensurePaired(mDevice)) {
            return;
        }
        synchronized (this) {
            Log.d(TAG, "connect " + this);
            mDevice.connect();
        }
    }

    private boolean ensurePaired(BluetoothDevice mDevice) {
        if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
            startPairing(mDevice);
            return false;
        } else {
            return true;
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

    Runnable discoveryRunnable = new Runnable() {
        @Override
        public void run() {
            bluetoothAdapter.startDiscovery();
            searchAnim(true);
        }
    };

    private void updatePairList() {
        bondList = getPairList();
        if (bondAdapter == null) {
            bondAdapter = new BluetoothBondAdapter(bondList, BluetoothActivity.this,bluetoothAdapter);
            setConnectedState();
            bondAdapter.setHasStableIds(true);
            bluetoothBinding.pairRv.setAdapter(bondAdapter);
        } else {
            bondAdapter.updateList(bondList);
            bondAdapter.currentConnectDevice(currentDevice);
            setConnectedState();
            bondAdapter.notifyDataSetChanged();
        }

        if (!handler.hasMessages(Contants.REFRESH_PAIR)) {
            handler.sendEmptyMessageDelayed(Contants.REFRESH_PAIR, 5000);
        }
    }

    private void updateView(boolean isvisible) {
        if (isvisible) {
            bluetoothBinding.pairRv.setVisibility(View.VISIBLE);
            bluetoothBinding.availableRv.setVisibility(View.VISIBLE);

            bluetoothBinding.rlSearchBle.setVisibility(View.VISIBLE);
            bluetoothBinding.pairTitle.setVisibility(View.VISIBLE);
            bluetoothBinding.availableTitle.setVisibility(View.VISIBLE);
        } else {
            bluetoothBinding.pairRv.setVisibility(View.GONE);
            bluetoothBinding.availableRv.setVisibility(View.GONE);

            bluetoothBinding.rlSearchBle.setVisibility(View.GONE);
            bluetoothBinding.pairTitle.setVisibility(View.GONE);
            bluetoothBinding.availableTitle.setVisibility(View.GONE);
        }
    }

    private void initReceiver() {

        /**
         * 获取搜索列表 使用广播
         */
        mCanFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mCanFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mCanReceiver, mCanFilter);

        /**
         * 配对广播监听
         */
        bondReceiver = new BondStateReceiver(this);
        registerReceiver(bondReceiver, bondFilter);

        /**
         * 蓝牙连接状态监听
         */
        blueFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        blueFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        blueFilter.addAction(BluetoothDevice.ACTION_FOUND);
        blueFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        blueReceiver = new BluetoothReceiver(this);
        registerReceiver(blueReceiver, blueFilter);

        /**
         * 开关
         */
        blueBoothReceiver = new MyBlueBoothReceiver(this);
        registerReceiver(blueBoothReceiver, blueBoothFilter);
    }

    private void DestroyReceiver() {
        if (mCanReceiver != null) {
            unregisterReceiver(mCanReceiver);
        }
        if (blueReceiver != null) {
            unregisterReceiver(blueReceiver);
        }
        if (blueBoothReceiver != null) {
            unregisterReceiver(blueBoothReceiver);
        }
        if (bondReceiver != null) {
            unregisterReceiver(bondReceiver);
        }
    }

    private void searchAnim(boolean isAnim) {
        if (!isAnim) {
            bluetoothBinding.refreshIv.setVisibility(View.GONE);
            bluetoothBinding.refreshIv.clearAnimation();
            return;
        }
        bluetoothBinding.refreshIv.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(
                BluetoothActivity.this, R.anim.search_anim);
        LinearInterpolator interpolator = new LinearInterpolator();
        anim.setInterpolator(interpolator);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                // TODO Auto-generated method stub
            }
        });
        bluetoothBinding.refreshIv.startAnimation(anim);
    }

    /**
     * 获取已配对列表
     *
     * @return
     */
    private List<BluetoothDevice> getPairList() {
        List<BluetoothDevice> list = new ArrayList<BluetoothDevice>();
        try {
            // 得到蓝牙状态的方法
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : devices) {
                //Log.d(TAG,"pair device "+device.getName());
                if (device != null) {
                    list.add(device);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 获取连接状态
     *
     * @param
     * @return
     */
    private void setConnectedState() {

        //重置状态
        bondAdapter.clearConnectMap();
        if (a2dp == null)
            return;
        List<BluetoothDevice> deviceList = getCurrentConnectDevice();
        BluetoothDevice activeDevice = null;
        activeDevice = a2dp.getActiveDevice();
        if (deviceList != null && deviceList.size() > 0) {
            for (int i = 0; i < deviceList.size(); i++) {
                if (deviceList.get(i).getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.AUDIO_VIDEO
                        && activeDevice != null) {
                    if (!deviceList.get(i).getAddress().equals(activeDevice.getAddress()))
                        continue;
                }
                bondAdapter
                        .updateConnectMap(deviceList.get(i).getAddress(), 1);
            }
        }
    }

    /**
     * 获取当前连接的蓝牙设备
     *
     * @return
     */
    private List<BluetoothDevice> getCurrentConnectDevice() {
        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;// 得到BluetoothAdapter的Class对象
        try {// 得到蓝牙状态的方法
            Method method = bluetoothAdapterClass.getDeclaredMethod(
                    "getConnectionState", (Class[]) null);
            // 打开权限
            method.setAccessible(true);
            int state = (Integer) method.invoke(bluetoothAdapter,
                    (Object[]) null);
            if (state == BluetoothAdapter.STATE_CONNECTED) {
                List<BluetoothDevice> deviceconnectList = new ArrayList<BluetoothDevice>();
                Set<BluetoothDevice> devices = bluetoothAdapter
                        .getBondedDevices();
                for (BluetoothDevice device : devices) {
                    Method isConnectedMethod = BluetoothDevice.class
                            .getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected = (Boolean) isConnectedMethod.invoke(
                            device, (Object[]) null);
                    if (isConnected) {
                        if (device != null) {
                            deviceconnectList.add(device);
                        }
                    }
                }
                return deviceconnectList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 连接蓝牙设备（通过监听蓝牙协议的服务，在连接服务的时候使用BluetoothA2dp协议）
     */

    private BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceDisconnected(int profile) {
            a2dp = null;
        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            try {
                if (profile == BluetoothProfile.HEADSET) {

                } else if (profile == BluetoothProfile.A2DP) {
                    /** 使用A2DP的协议连接蓝牙设备（使用了反射技术调用连接的方法） */
                    a2dp = (BluetoothA2dp) proxy;
                    updatePairList();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public static BluetoothHidHost mBluetoothProfile = null;

    private BluetoothProfile.ServiceListener mListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            try {
                if (profile == 4) {
                    mBluetoothProfile = (BluetoothHidHost) proxy;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {

        }
    };

    public static void connectDeviceFromA2DP(BluetoothDevice device) {
        try {
            if (a2dp != null
                    && a2dp.getConnectionState(device) != BluetoothProfile.STATE_CONNECTED) {
                a2dp.getClass()
                        .getMethod("connect",
                                BluetoothDevice.class)
                        .invoke(a2dp, device);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    /**
     * 连接设备 键盘
     *
     * @param
     */
    public static void connectKeyboard(BluetoothDevice device) {

        if (mBluetoothProfile == null) {
            return;
        }
        try {
            Method method = mBluetoothProfile.getClass().getMethod("connect",
                    new Class[]{BluetoothDevice.class});
            method.invoke(mBluetoothProfile, device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String[] bTtypes = new String[]{
            "00001108-0000-1000-8000-00805f9b34fb",
            "0000111e-0000-1000-8000-00805f9b34fb",
            "0000110b-0000-1000-8000-00805f9b34fb",
            "0000110e-0000-1000-8000-00805f9b34fb",
            "00001116-0000-1000-8000-00805f9b34fb",
            "00001115-0000-1000-8000-00805f9b34fb"
    };

    public static boolean isKeyboardDevice(ParcelUuid[] uuids) {
        if (uuids != null && uuids.length > 0) {
            for (int i = 0; i < uuids.length; i++) {
                for (int j = 0; j < bTtypes.length; j++) {
                    if (uuids[i].toString().equals(bTtypes[j])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_bluetooth_switch || id == R.id.bluetooth_switch) {
            bluetoothBinding.bluetoothSwitch.setChecked(!bluetoothBinding.bluetoothSwitch.isChecked());
        } else if (id == R.id.rl_search_ble) {
            startScanning(bluetoothAdapter);
        }
    }


    @Override
    public void bluetoothChange() {

    }

    @Override
    public void bondState(int state, BluetoothDevice device) {
        Message message = handler.obtainMessage();
        message.what = state;
        message.obj = device;
        handler.sendMessage(message);
    }

    @Override
    public void getBlueBoothState(int state) {
        initData();
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopScanning(bluetoothAdapter);
    }

    @Override
    protected void onDestroy() {
        DestroyReceiver();
        bluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, a2dp);
        bluetoothAdapter.closeProfileProxy(4, mBluetoothProfile);
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    //开始扫描
    void startScanning(BluetoothAdapter mBluetoothAdapter) {
        if (!mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.startDiscovery();
            updatePairList();
            searchAnim(true);
        }
    }

    //停止扫描
    void stopScanning(BluetoothAdapter mBluetoothAdapter) {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }
}