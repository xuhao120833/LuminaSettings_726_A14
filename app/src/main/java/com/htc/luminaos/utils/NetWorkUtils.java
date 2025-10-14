package com.htc.luminaos.utils;

/**
 * @author 作者：xuhao
 * 类说明：和网络相关的工具类
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.StaticIpConfiguration;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.htc.luminaos.bean.StaticIpConfig;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class NetWorkUtils {

    private String TAG = NetWorkUtils.class.getSimpleName();
    private WifiManager mWifiManager;
    private ConnectivityManager mConnectivityManager;
    //add code start

    private Context mContext;

    public NetWorkUtils(Context context, WifiManager wifiManager) {
        this.mContext = context;
        mWifiManager = wifiManager;
    }


    /**
     * 判断是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断WIFI网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断MOBILE网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 获取当前网络连接的类型信息
     *
     * @param context
     * @return
     */
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

    /**
     * 获取当前的网络状态 ：没有网络0：WIFI网络1：3G网络2：2G网络3
     *
     * @param context
     * @return
     */
    public static int getAPNType(Context context) {
        int netType = 0;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = 1;// wifi
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            TelephonyManager mTelephony = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
                    && !mTelephony.isNetworkRoaming()) {
                netType = 2;// 3G
            } else {
                netType = 3;// 2G
            }
        }
        return netType;
    }

    public String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "." + (0xFF & paramInt >> 24);
    }

//	/**
//	 * @param staticIpConfig
//	 * @return
//	 */
//	public boolean setWiFiWithStaticIP(StaticIpConfig staticIpConfig) {
//		synchronized (this) {
//			final long ident = Binder.clearCallingIdentity();
//			boolean success = false;
//
//			IpConfiguration ipConfig;
//			WifiConfiguration wifiConfig = getWifiConfiguration(mContext, mWifiManager.getConnectionInfo().getSSID());
//
//			if (wifiConfig != null) {
//				ipConfig = wifiConfig.getIpConfiguration();
//
//			} else {
//				ipConfig = new IpConfiguration();
//			}
//			try {
//				StaticIpConfiguration staticConfig = wifiConfig.getIpConfiguration().getStaticIpConfiguration();
//				if (staticConfig == null) {
//					staticConfig = new StaticIpConfiguration();
//				} else {
//					staticConfig.clear();
//				}
//				if (staticIpConfig.isDhcp()) {
//					wifiConfig.getIpConfiguration().setIpAssignment(IpConfiguration.IpAssignment.DHCP);
//					wifiConfig.setIpConfiguration(new IpConfiguration(IpConfiguration.IpAssignment.DHCP, IpConfiguration.ProxySettings.NONE, staticConfig, null));
//				} else {
//					wifiConfig.getIpConfiguration().setIpAssignment(IpConfiguration.IpAssignment.STATIC);
//					InetAddress inetAddress = NetworkUtils.numericToInetAddress(staticIpConfig.getIp());
//					staticConfig.ipAddress = new LinkAddress(inetAddress, 24);
//					staticConfig.gateway = (Inet4Address) NetworkUtils.numericToInetAddress(staticIpConfig.getGateWay());
//					if (!TextUtils.isEmpty(staticIpConfig.getDns1())) {
//						staticConfig.dnsServers.add(NetworkUtils.numericToInetAddress(staticIpConfig.getDns1()));
//					}
//					if (!TextUtils.isEmpty(staticIpConfig.getDns2())) {
//						staticConfig.dnsServers.add(NetworkUtils.numericToInetAddress(staticIpConfig.getDns2()));
//					}
//					wifiConfig.setIpConfiguration(new IpConfiguration(IpConfiguration.IpAssignment.STATIC, IpConfiguration.ProxySettings.NONE, staticConfig, null));
//					ipConfig.setStaticIpConfiguration(staticConfig);
//				}
//				saveConfiguration(wifiConfig);
//				updateConfiguration(wifiConfig);
//				disconnectWiFi();
//				reconnectWiFi();
//				success = true;
//
//			} catch (Exception e) {
//				LogUtils.e(TAG, e.getMessage());
//			} finally {
//				Binder.restoreCallingIdentity(ident);
//			}
//			return success;
//		}
//	}

    public void saveConfiguration(WifiConfiguration config) {
        mWifiManager.save(config, null);
    }

    public void updateConfiguration(WifiConfiguration config) {
        mWifiManager.updateNetwork(config);
    }

    public boolean disconnectWiFi() {
        return mWifiManager.disconnect();
    }

    public boolean reconnectWiFi() {
        return mWifiManager.reconnect();
    }

    public WifiConfiguration getWifiConfiguration(Context context, String ssid) {
        final long ident = Binder.clearCallingIdentity();
        try {
            List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
            for (WifiConfiguration wifiConfig : list) {
                if (wifiConfig.SSID.equals(ssid)) {
                    return wifiConfig;
                }
            }
        } catch (Exception e) {
            LogUtils.e(TAG, e.getMessage());
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
        return null;
    }

    public boolean isNetworkAvailable(ConnectivityManager connManager, Context context) {
        if (connManager == null) {
        } else {
            NetworkInfo[] info = connManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param staticIpConfig
     * @return
     */
    public boolean setWiFiWithStaticIP(StaticIpConfig staticIpConfig) {
        synchronized (this) {
//			final long ident = Binder.clearCallingIdentity();
            WifiConfiguration wifiConfig = getWifiConfiguration(mContext, mWifiManager.getConnectionInfo().getSSID());
            try {
                if (staticIpConfig.isDhcp()) {
                    switchToDHCP(mContext, wifiConfig);
                } else {
                    LogUtils.d(TAG, "staticIpConfig.getIp() " + staticIpConfig.getIp());
                    if (staticIpConfig.getIp() == null || staticIpConfig.getIp().isEmpty()) {
                        return false;
                    }
                    setStaticIpConfig(mContext, wifiConfig, staticIpConfig.getIp(), staticIpConfig.getGateWay()
                            , staticIpConfig.getDns1(), staticIpConfig.getDns2(), 24);
                }
                disconnectWiFi();
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                return false;
            }
            return true;
        }
    }


    /**
     * 设置当前 WiFi 的静态 IP 配置
     *
     * @param context         上下文对象
     * @param wifiConfig      当前 WiFi 的 WifiConfiguration 对象
     * @param ipAddress       静态 IP 地址（如 "192.168.1.100"）
     * @param gateway         网关地址（如 "192.168.1.1"）
     * @param dns1            首选 DNS 地址（如 "8.8.8.8"）
     * @param dns2            备用 DNS 地址（如 "8.8.4.4"），可以为 null
     * @param prefixLength    子网前缀长度（通常为 24）
     * @return 是否设置成功
     */
    public boolean setStaticIpConfig(
            Context context,
            WifiConfiguration wifiConfig,
            String ipAddress,
            String gateway,
            String dns1,
            String dns2,
            int prefixLength
    ) {
        try {
            // 创建 StaticIpConfiguration
            StaticIpConfiguration staticIpConfig = new StaticIpConfiguration();
            staticIpConfig.ipAddress = new LinkAddress(InetAddress.getByName(ipAddress), prefixLength);
            staticIpConfig.gateway = InetAddress.getByName(gateway);
            staticIpConfig.dnsServers.add(InetAddress.getByName(dns1));
            if (dns2 != null && !dns2.isEmpty()) {
                staticIpConfig.dnsServers.add(InetAddress.getByName(dns2));
            }

            // 直接设置静态 IP 配置

//			wifiConfig.setStaticIpConfiguration(staticIpConfig);
            wifiConfig.setIpAssignment(IpConfiguration.IpAssignment.STATIC);
            wifiConfig.setStaticIpConfiguration(staticIpConfig);

            // 更新网络配置
            int networkId = mWifiManager.updateNetwork(wifiConfig);
            if (networkId == -1) {
                return false;
            }

            // 连接到配置的网络
            return mWifiManager.enableNetwork(networkId, true);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将 WiFi 配置切换为 DHCP 模式
     *
     * @param context    上下文对象
     * @param wifiConfig 当前 WiFi 的 WifiConfiguration 对象
     * @return 是否设置成功
     */
    public static boolean switchToDHCP(Context context, WifiConfiguration wifiConfig) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            // 设置为 DHCP 模式
            wifiConfig.setIpAssignment(IpConfiguration.IpAssignment.DHCP);
            wifiConfig.setStaticIpConfiguration(null); // 删除静态 IP 配置

            // 更新 WiFi 配置
            int networkId = wifiManager.updateNetwork(wifiConfig);
            if (networkId == -1) {
                return false;
            }
            // 连接到配置的网络
            return wifiManager.enableNetwork(networkId, true);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static MyNetworkInfo getWiredNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return null;
        }

        Network[] networks = connectivityManager.getAllNetworks();
        for (Network network : networks) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                LinkProperties linkProperties = connectivityManager.getLinkProperties(network);
                if (linkProperties != null) {
                    String ipv4Address = null;
                    String gateway = null;
                    String subnetMask = null;
                    List<String> dnsServers = new ArrayList<>();
                    String macAddress = null; // MAC 地址

                    // Get IPv4 Address
                    for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
                        InetAddress address = linkAddress.getAddress();
                        if (address instanceof Inet4Address) {
                            ipv4Address = address.getHostAddress();
                            break;
                        }
                    }

                    // Get Gateway
                    for (RouteInfo route : linkProperties.getRoutes()) {
                        if (route.getGateway() instanceof Inet4Address) {
                            gateway = route.getGateway().getHostAddress();
                            break;
                        }
                    }

                    // Get DNS Servers
                    for (InetAddress dns : linkProperties.getDnsServers()) {
                        if (dns instanceof Inet4Address) {
                            dnsServers.add(dns.getHostAddress());
                        }
                    }

                    // Calculate Subnet Mask (simple method)
                    if (!linkProperties.getLinkAddresses().isEmpty()) {
                        int prefixLength = linkProperties.getLinkAddresses().get(0).getPrefixLength();
                        subnetMask = prefixLengthToSubnetMask(prefixLength);
                    }

                    // 获取 MAC 地址（仅适用于有线网络）
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    String mac = networkInfo.getExtraInfo();

                    return new MyNetworkInfo(ipv4Address, gateway, subnetMask, dnsServers,mac);
                }
            }
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    private static String prefixLengthToSubnetMask(int prefixLength) {
        int mask = 0xffffffff << (32 - prefixLength);
        return String.format("%d.%d.%d.%d",
                (mask >> 24) & 0xff,
                (mask >> 16) & 0xff,
                (mask >> 8) & 0xff,
                mask & 0xff);
    }

    public static class MyNetworkInfo {
        public String ipv4Address;
        public String gateway;
        public String subnetMask;
        public List<String> dnsServers;
		public String mac;

        public MyNetworkInfo(String ipv4Address, String gateway, String subnetMask, List<String> dnsServers,String mac) {
            this.ipv4Address = ipv4Address;
            this.gateway = gateway;
            this.subnetMask = subnetMask;
            this.dnsServers = dnsServers;
			this.mac = mac;
        }

        @Override
        public String toString() {
            return "IPv4 Address: " + ipv4Address + "\n" +
                    "Gateway: " + gateway + "\n" +
                    "Subnet Mask: " + subnetMask + "\n" +
                    "DNS Servers: " + dnsServers;
        }
    }

}