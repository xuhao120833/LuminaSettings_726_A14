package com.htc.luminaos.activity.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.StaticIpConfiguration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.htc.luminaos.MyApplication;
import com.htc.luminaos.R;
import com.htc.luminaos.activity.BaseActivity;
import com.htc.luminaos.databinding.ActivityWiredBinding;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.NetWorkUtils;
import com.htc.luminaos.widget.StaticConfigDialog;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class WiredActivity extends BaseActivity implements View.OnKeyListener {

    private ActivityWiredBinding wiredNetworkLayoutBinding;
    ConnectivityManager connectivityManager;
    private EthernetManager mEthManager = null;
    private final static String nullIpInfo = "0.0.0.0";
    private IntentFilter mIntentFilter;

    private boolean showPre = true;
    String  mInterfaceName ="";
    IpConfiguration mIpConfiguration;

    private static String TAG = "WiredActivity";

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                LogUtils.d(TAG, "onReceive ===" + info.toString());
                if (null != info && ConnectivityManager.TYPE_ETHERNET == info.getType()) {
                    if (NetworkInfo.State.CONNECTED == info.getState()) {
                        LogUtils.d(TAG, "onReceive === 收到有线网络已连接的广播");
                        wiredNetworkLayoutBinding.wiredTv.setText(getString(R.string.connected));
                        initNetworkInfo();
                    } else if (NetworkInfo.State.DISCONNECTED == info.getState()) {
                        LogUtils.d(TAG, "onReceive === 收到有线网络已断开的广播");
                        wiredNetworkLayoutBinding.wiredTv.setText(getString(R.string.not_connected));
                        resetNetworkInfo();
                    }
                }
            }
        }
    };

//    View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
//        if (!hasFocus && v instanceof EditText) {
//            EditText editText = (EditText) v;
//            String text = editText.getText().toString();
//            // 你可以根据 editText 的 ID 或其他特性进行不同处理
//            switch (v.getId()) {
//                case R.id.ip_address_tv:
////                    break;
//                case R.id.gateway_tv:
////                    break;
//                case R.id.subnet_mask_tv:
////                    break;
//                case R.id.dns_tv:
////                    break;
//                case R.id.dns2_tv:
//                    LogUtils.d(TAG," 执行到editTextFocusChange");
//                    editTextFocusChange();
//                    break;
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wiredNetworkLayoutBinding = ActivityWiredBinding.inflate(LayoutInflater.from(this));
        setContentView(wiredNetworkLayoutBinding.getRoot());
        mEthManager = (EthernetManager) getSystemService(Context.ETHERNET_SERVICE);
        connectivityManager =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        initView();
    }

    private void initView(){
        wiredNetworkLayoutBinding.rlIpSetting.setOnClickListener(this);
        wiredNetworkLayoutBinding.rlDns.setOnClickListener(this);
        wiredNetworkLayoutBinding.rlDns2.setOnClickListener(this);
        wiredNetworkLayoutBinding.rlGateway.setOnClickListener(this);
        wiredNetworkLayoutBinding.rlIpAddress.setOnClickListener(this);
        wiredNetworkLayoutBinding.rlSubnetMask.setOnClickListener(this);

        wiredNetworkLayoutBinding.ipAddressTv.setOnKeyListener(this);
        wiredNetworkLayoutBinding.gatewayTv.setOnKeyListener(this);
        wiredNetworkLayoutBinding.rlSubnetMask.setOnKeyListener(this);
        wiredNetworkLayoutBinding.dnsTv.setOnKeyListener(this);
        wiredNetworkLayoutBinding.dns2Tv.setOnKeyListener(this);

        wiredNetworkLayoutBinding.ipAddressTv.setOnKeyListener(this);
        wiredNetworkLayoutBinding.gatewayTv.setOnKeyListener(this);
        wiredNetworkLayoutBinding.rlSubnetMask.setOnKeyListener(this);
        wiredNetworkLayoutBinding.dnsTv.setOnKeyListener(this);
        wiredNetworkLayoutBinding.dns2Tv.setOnKeyListener(this);

//        wiredNetworkLayoutBinding.ipAddressTv.setOnFocusChangeListener(focusChangeListener);
//        wiredNetworkLayoutBinding.gatewayTv.setOnFocusChangeListener(focusChangeListener);
//        wiredNetworkLayoutBinding.rlSubnetMask.setOnFocusChangeListener(focusChangeListener);
//        wiredNetworkLayoutBinding.dnsTv.setOnFocusChangeListener(focusChangeListener);
//        wiredNetworkLayoutBinding.dns2Tv.setOnFocusChangeListener(focusChangeListener);

        wiredNetworkLayoutBinding.rlIpSetting.setOnHoverListener(this);
        wiredNetworkLayoutBinding.rlDns.setOnHoverListener(this);
        wiredNetworkLayoutBinding.rlDns2.setOnHoverListener(this);
        wiredNetworkLayoutBinding.rlGateway.setOnHoverListener(this);
        wiredNetworkLayoutBinding.rlIpAddress.setOnHoverListener(this);
        wiredNetworkLayoutBinding.rlSubnetMask.setOnHoverListener(this);
        wiredNetworkLayoutBinding.rlWired.setOnHoverListener(this);
        wiredNetworkLayoutBinding.rlMac.setOnHoverListener(this);
        wiredNetworkLayoutBinding.rlIpSetting.setVisibility(MyApplication.config.ipSetting?View.VISIBLE:View.GONE);
    }

    @Override
    protected void onResume() {
        loadIpConfiguration();
//        updateEthernetStatus();
        if (isNetworkConnect()){
            LogUtils.d(TAG,"onResume isNetworkConnect");
            wiredNetworkLayoutBinding.wiredTv.setText(getString(R.string.connected));
            initNetworkInfo();
        }else {
            LogUtils.d(TAG,"onResume 网络未连接");
            wiredNetworkLayoutBinding.wiredTv.setText(getString(R.string.not_connected));
            resetNetworkInfo();
        }
        updateEthernetStatus();
        mIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mIntentFilter);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        int id = v.getId();
        if (id == R.id.rl_ip_setting) {
            if (mIpConfiguration.getIpAssignment() == IpConfiguration.IpAssignment.STATIC) {
                mIpConfiguration.setIpAssignment(IpConfiguration.IpAssignment.DHCP);
                mIpConfiguration.setStaticIpConfiguration(null);
                mEthManager.setConfiguration(mInterfaceName, mIpConfiguration);
            } else {
                mIpConfiguration.setIpAssignment(IpConfiguration.IpAssignment.STATIC);
                mEthManager.setConfiguration(mInterfaceName, mIpConfiguration);
            }
            updateEthernetStatus();
        } else if (id == R.id.rl_ip_address) {
            wiredNetworkLayoutBinding.ipAddressTv.requestFocus();
            wiredNetworkLayoutBinding.ipAddressTv.setSelection(wiredNetworkLayoutBinding.ipAddressTv.getText().length());
            // 显示软键盘
            if (imm != null) {
                imm.showSoftInput(wiredNetworkLayoutBinding.ipAddressTv, InputMethodManager.SHOW_IMPLICIT);
            }
        } else if (id == R.id.rl_gateway) {
            wiredNetworkLayoutBinding.gatewayTv.requestFocus();
            wiredNetworkLayoutBinding.gatewayTv.setSelection(wiredNetworkLayoutBinding.gatewayTv.getText().length());
            // 显示软键盘
            if (imm != null) {
                imm.showSoftInput(wiredNetworkLayoutBinding.gatewayTv, InputMethodManager.SHOW_IMPLICIT);
            }
        } else if (id == R.id.rl_subnet_mask) {
            wiredNetworkLayoutBinding.subnetMaskTv.requestFocus();
            wiredNetworkLayoutBinding.subnetMaskTv.setSelection(wiredNetworkLayoutBinding.subnetMaskTv.getText().length());
            // 显示软键盘
            if (imm != null) {
                imm.showSoftInput(wiredNetworkLayoutBinding.subnetMaskTv, InputMethodManager.SHOW_IMPLICIT);
            }
//                StaticConfigDialog staticConfigDialog = new StaticConfigDialog(this,R.style.DialogTheme);
//                staticConfigDialog.setIpConfiguration(mIpConfiguration);
//                staticConfigDialog.show();
        } else if (id == R.id.rl_dns) {
            wiredNetworkLayoutBinding.dnsTv.requestFocus();
            wiredNetworkLayoutBinding.dnsTv.setSelection(wiredNetworkLayoutBinding.dnsTv.getText().length());
            // 显示软键盘
            if (imm != null) {
                imm.showSoftInput(wiredNetworkLayoutBinding.dnsTv, InputMethodManager.SHOW_IMPLICIT);
            }
        } else if (id == R.id.rl_dns2) {
            wiredNetworkLayoutBinding.dns2Tv.requestFocus();
            wiredNetworkLayoutBinding.dns2Tv.setSelection(wiredNetworkLayoutBinding.dns2Tv.getText().length());
            // 显示软键盘
            if (imm != null) {
                imm.showSoftInput(wiredNetworkLayoutBinding.dns2Tv, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            int id = v.getId();
            if (id == R.id.ip_address_tv) {
                if (wiredNetworkLayoutBinding.ipAddressTv.hasFocus() && event.getAction() == KeyEvent.ACTION_DOWN)
                    wiredNetworkLayoutBinding.rlIpAddress.requestFocus();
                return true;
            } else if (id == R.id.gateway_tv) {
                if (wiredNetworkLayoutBinding.gatewayTv.hasFocus() && event.getAction() == KeyEvent.ACTION_DOWN)
                    wiredNetworkLayoutBinding.rlGateway.requestFocus();
                return true;
            } else if (id == R.id.subnet_mask_tv) {
                if (wiredNetworkLayoutBinding.subnetMaskTv.hasFocus() && event.getAction() == KeyEvent.ACTION_DOWN)
                    wiredNetworkLayoutBinding.rlSubnetMask.requestFocus();
                return true;
            } else if (id == R.id.dns_tv) {
                if (wiredNetworkLayoutBinding.dnsTv.hasFocus() && event.getAction() == KeyEvent.ACTION_DOWN)
                    wiredNetworkLayoutBinding.rlDns.requestFocus();
                return true;
            } else if (id == R.id.dns2_tv) {
                if (wiredNetworkLayoutBinding.dns2Tv.hasFocus() && event.getAction() == KeyEvent.ACTION_DOWN)
                    wiredNetworkLayoutBinding.rlDns2.requestFocus();
                return true;
            }
        }
        return false;
    }

    public void loadIpConfiguration() {
        String[] ifaces = mEthManager.getAvailableInterfaces();
        if (ifaces.length > 0) {
            mInterfaceName = ifaces[0];
            LogUtils.d(TAG," loadIpConfiguration ifaces.length > 0 mInterfaceName "+mInterfaceName);
            mIpConfiguration = mEthManager.getConfiguration(mInterfaceName);
        }
    }

    private boolean isNetworkConnect(){
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        return networkInfo!=null&& networkInfo.isConnected();
    }

    private void updateEthernetStatus(){
        LogUtils.d(TAG,"updateEthernetStatus getIpAssignment "+mIpConfiguration.getIpAssignment());
        IpConfiguration.IpAssignment ipAssignment = mIpConfiguration.getIpAssignment();

        if (ipAssignment == IpConfiguration.IpAssignment.STATIC) {
            LogUtils.d(TAG,"  updateEthernetStatus STATIC");
            wiredNetworkLayoutBinding.ipSettingTv.setText(getString(R.string.static_ip));
            wiredNetworkLayoutBinding.rlIpAddress.setEnabled(true);
            wiredNetworkLayoutBinding.rlGateway.setEnabled(true);
            wiredNetworkLayoutBinding.rlSubnetMask.setEnabled(true);
            wiredNetworkLayoutBinding.rlDns.setEnabled(true);
            wiredNetworkLayoutBinding.rlDns2.setEnabled(true);
            wiredNetworkLayoutBinding.rlMac.setEnabled(true);
            wiredNetworkLayoutBinding.ipAddressTv.setEnabled(true);
            wiredNetworkLayoutBinding.gatewayTv.setEnabled(true);
            wiredNetworkLayoutBinding.subnetMaskTv.setEnabled(true);
            wiredNetworkLayoutBinding.dnsTv.setEnabled(true);
            wiredNetworkLayoutBinding.dns2Tv.setEnabled(true);
            wiredNetworkLayoutBinding.macTv.setEnabled(true);

            wiredNetworkLayoutBinding.rlIpAddress.setAlpha(1.0f);
            wiredNetworkLayoutBinding.rlGateway.setAlpha(1.0f);
            wiredNetworkLayoutBinding.rlSubnetMask.setAlpha(1.0f);
            wiredNetworkLayoutBinding.rlDns.setAlpha(1.0f);
            wiredNetworkLayoutBinding.rlDns2.setAlpha(1.0f);
            wiredNetworkLayoutBinding.rlMac.setAlpha(1.0f);

        }else if ((ipAssignment == IpConfiguration.IpAssignment.DHCP) || (ipAssignment == IpConfiguration.IpAssignment.UNASSIGNED)){
            LogUtils.d(TAG,"  updateEthernetStatus DHCP");
            wiredNetworkLayoutBinding.ipSettingTv.setText(getString(R.string.dhcp));
            wiredNetworkLayoutBinding.rlIpAddress.setEnabled(false);
            wiredNetworkLayoutBinding.rlGateway.setEnabled(false);
            wiredNetworkLayoutBinding.rlSubnetMask.setEnabled(false);
            wiredNetworkLayoutBinding.rlDns.setEnabled(false);
            wiredNetworkLayoutBinding.rlDns2.setEnabled(false);
            wiredNetworkLayoutBinding.rlMac.setEnabled(false);
            wiredNetworkLayoutBinding.ipAddressTv.setEnabled(false);
            wiredNetworkLayoutBinding.gatewayTv.setEnabled(false);
            wiredNetworkLayoutBinding.subnetMaskTv.setEnabled(false);
            wiredNetworkLayoutBinding.dnsTv.setEnabled(false);
            wiredNetworkLayoutBinding.dns2Tv.setEnabled(false);
            wiredNetworkLayoutBinding.macTv.setEnabled(false);

            wiredNetworkLayoutBinding.rlIpAddress.setAlpha(0.7f);
            wiredNetworkLayoutBinding.rlGateway.setAlpha(0.7f);
            wiredNetworkLayoutBinding.rlSubnetMask.setAlpha(0.7f);
            wiredNetworkLayoutBinding.rlDns.setAlpha(0.7f);
            wiredNetworkLayoutBinding.rlDns2.setAlpha(0.7f);
            wiredNetworkLayoutBinding.rlMac.setAlpha(0.7f);
        }
    }

    private void resetNetworkInfo(){
        wiredNetworkLayoutBinding.macTv.setText("0.0.0.0");
        wiredNetworkLayoutBinding.subnetMaskTv.setText("0.0.0.0");
        wiredNetworkLayoutBinding.dnsTv.setText("0.0.0.0");
        wiredNetworkLayoutBinding.dns2Tv.setText("0.0.0.0");
        wiredNetworkLayoutBinding.gatewayTv.setText("0.0.0.0");
        wiredNetworkLayoutBinding.ipAddressTv.setText("0.0.0.0");
    }


    private void initNetworkInfo(){
        try {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            String mac = networkInfo.getExtraInfo();
            wiredNetworkLayoutBinding.macTv.setText(mac);
                List<LinkAddress> linkAddresses = connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getLinkAddresses();
                //获取当前连接的网络ip地址信息
                if (linkAddresses != null && !linkAddresses.isEmpty()) {
                    //注意：同时可以查看到两个网口的信息，但是ip地址不是固定的位置（即下标）
                    //所以遍历的时候需要判断一下当前获取的ip地址是否符合ip地址的正则表达式
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < linkAddresses.size(); i++) {
                        InetAddress address = linkAddresses.get(i).getAddress();
                        LogUtils.d("ip地址" + address.getHostAddress());
//                        builder.append(address.getHostAddress());
//                        if (i != linkAddresses.size() - 1) {
//                            builder.append("\n");
//                        }
                        // 只选择IPv4地址
                        if (address instanceof Inet4Address) {
                            builder.append(address.getHostAddress());
                            if (i != linkAddresses.size() - 1) {
                                builder.append("\n");
                            }
                        }
                    }
                    wiredNetworkLayoutBinding.ipAddressTv.setText(builder.toString());
                }
                List<RouteInfo> routes = connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getRoutes();
                if (routes != null && !routes.isEmpty()) {
                    for (int i = 0; i < routes.size(); i++) {
                        //和ip地址一样，需要判断获取的网址符不符合正则表达式
                        String hostAddress = routes.get(i).getGateway().getHostAddress();
                        if (isCorrectIp(hostAddress)) {
                            LogUtils.d("网关信息：" + hostAddress);
                            wiredNetworkLayoutBinding.gatewayTv.setText(hostAddress.replace("/", ""));
                        }
                    }
                }
                List<InetAddress> dnsServers = connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getDnsServers();
                if (dnsServers != null && dnsServers.size() >= 2) {
                    LogUtils.d("dns1 " + dnsServers.get(0).toString());
                    LogUtils.d("dns2 " + dnsServers.get(1).toString());
                    wiredNetworkLayoutBinding.dnsTv.setText(dnsServers.get(0).getHostAddress());
                    wiredNetworkLayoutBinding.dns2Tv.setText(dnsServers.get(1).getHostAddress());
                }
                String mask = getIpAddressMaskForInterfaces(connectivityManager.getLinkProperties(
                        connectivityManager.getActiveNetwork()).getInterfaceName());
                wiredNetworkLayoutBinding.subnetMaskTv.setText(mask);
        }catch (Exception e) {
            e.printStackTrace();
        }
        //第二种方法
//        NetWorkUtils.MyNetworkInfo networkInfo = NetWorkUtils.getWiredNetworkInfo(getApplicationContext());
//        wiredNetworkLayoutBinding.ipAddressTv.setText(networkInfo.ipv4Address);
//        wiredNetworkLayoutBinding.gatewayTv.setText(networkInfo.gateway);
//        wiredNetworkLayoutBinding.subnetMaskTv.setText(networkInfo.subnetMask);
//        wiredNetworkLayoutBinding.dnsTv.setText(networkInfo.dnsServers.get(0));
//        wiredNetworkLayoutBinding.dns2Tv.setText(networkInfo.dnsServers.get(1));
//        wiredNetworkLayoutBinding.macTv.setText(networkInfo.mac);
    }


    /**
     * 获取子网掩码
     * @param interfaceName
     * @return
     */
    public static String getIpAddressMaskForInterfaces(String interfaceName) {
        //"eth0"
        try {
            //获取本机所有的网络接口
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            //判断 Enumeration 对象中是否还有数据
            while (networkInterfaceEnumeration.hasMoreElements()) {
                //获取 Enumeration 对象中的下一个数据
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                if (!networkInterface.isUp() && !interfaceName.equals(networkInterface.getDisplayName())) {
                    //判断网口是否在使用，判断是否时我们获取的网口
                    continue;
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    if (interfaceAddress.getAddress() instanceof Inet4Address) {
                        //仅仅处理ipv6
                        //获取掩码位数，通过 calcMaskByPrefixLength 转换为字符串
                        return String.valueOf(calcMaskByPrefixLength(interfaceAddress.getNetworkPrefixLength()));
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();

        }

        return "0.0.0.0";
    }

    //通过子网掩码的位数计算子网掩码
    public static String calcMaskByPrefixLength(int length) {

        int mask = 0xffffffff << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int maskParts[] = new int[partsNum];
        int selector = 0x000000ff;

        for (int i = 0; i < maskParts.length; i++) {
            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }

        String result = "";
        result = result + maskParts[0];
        for (int i = 1; i < maskParts.length; i++) {
            result = result + "." + maskParts[i];
        }
        return result;
    }


    public static boolean isCorrectIp(String ip) {
        if (ip == null || "".equals(ip))
            return false;
        String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        return ip.matches(regex);
    }

    private Network getFirstEthernet() {
        final Network[] networks = connectivityManager.getAllNetworks();
        for (final Network network : networks) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                return network;
            }
        }
        return null;
    }

    private String formatIpAddresses(Network network) {
        final LinkProperties linkProperties = connectivityManager.getLinkProperties(network);
        if (linkProperties == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        boolean gotAddress = false;
        for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
            if (gotAddress) {
                sb.append("\n");
            }
            sb.append(linkAddress.getAddress().getHostAddress());
            gotAddress = true;
        }
        if (gotAddress) {
            return sb.toString();
        } else {
            return null;
        }
    }

    @Override
    protected void onStop() {
        if (mReceiver!=null){
            unregisterReceiver(mReceiver);
        }
        setIP();
        super.onStop();
    }

    public void editTextFocusChange() {
        LogUtils.d(TAG, " 执行到editTextFocusChange ");
//        setIP();
    }

    private void setIP() {
        if(mIpConfiguration.getIpAssignment() == IpConfiguration.IpAssignment.STATIC) {
            LogUtils.d(TAG,"执行setIP");
            String IP = wiredNetworkLayoutBinding.ipAddressTv.getText().toString();
            String GATEWAY = wiredNetworkLayoutBinding.gatewayTv.getText().toString();
            String NETMASK = "24";
            String DNS1 = wiredNetworkLayoutBinding.dnsTv.getText().toString();
            String DNS2 = wiredNetworkLayoutBinding.dns2Tv.getText().toString();
            StaticIpConfiguration staticConfig = new StaticIpConfiguration();
            if (TextUtils.isEmpty(IP)) {
//            return "无效IP";
                LogUtils.d(TAG, "无效IP");
            }
            Inet4Address ipaddr = null;
            try {
                ipaddr = (Inet4Address) NetworkUtils.numericToInetAddress(IP);
            } catch (IllegalArgumentException | ClassCastException e) {
//            return "无效IP";
                LogUtils.d(TAG, "无效IP");
            }
            try {
                if (TextUtils.isEmpty(NETMASK) || (0 > Integer.parseInt(NETMASK) || Integer.parseInt(NETMASK) > 32)) {
//                return "无效网络前缀长度";
                    LogUtils.d(TAG, "无效网络前缀长度");
                }
            } catch (IllegalArgumentException | ClassCastException e) {
//            return "无效网络前缀长度";
                LogUtils.d(TAG, "无效网络前缀长度");
            }
            try {
                staticConfig.ipAddress = new LinkAddress(ipaddr, Integer.parseInt(NETMASK));
            } catch (IllegalArgumentException | ClassCastException e) {
//            return "无效IP";
                LogUtils.d(TAG, "无效IP");
            }
            if (!TextUtils.isEmpty(GATEWAY)) {
                try {
                    InetAddress getwayaddr = NetworkUtils.numericToInetAddress(GATEWAY);
                    staticConfig.gateway = getwayaddr;
                } catch (IllegalArgumentException | ClassCastException e) {
//                return "无效网关";
                    LogUtils.d(TAG, "无效网关");
                }
            }
            if (!TextUtils.isEmpty(DNS1)) {
                try {
                    InetAddress idns1 = NetworkUtils.numericToInetAddress(DNS1);
                    staticConfig.dnsServers.add(idns1);
                } catch (IllegalArgumentException | ClassCastException e) {
//                return "无效DNS1";
                    LogUtils.d(TAG, "无效DNS1");
                }
            }
            if (!TextUtils.isEmpty(DNS2)) {
                try {
                    InetAddress idns2 = NetworkUtils.numericToInetAddress(DNS2);
                    staticConfig.dnsServers.add(idns2);
                } catch (IllegalArgumentException | ClassCastException e) {
//                return "无效DNS2";
                    LogUtils.d(TAG, "无效DNS2");
                }
            }
            mIpConfiguration.setIpAssignment(IpConfiguration.IpAssignment.STATIC);
            mIpConfiguration.setStaticIpConfiguration(staticConfig);
            mEthManager.setConfiguration(mEthManager.getAvailableInterfaces()[0], mIpConfiguration);
//        return "";
        }
    }
}