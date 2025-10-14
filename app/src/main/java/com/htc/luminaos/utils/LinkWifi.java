package com.htc.luminaos.utils;

/**
 * @author  作者：zgr
 * @version 创建时间：2017年3月28日 上午10:25:41
 * 类说明
 */

import android.app.Service;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class LinkWifi {

    private WifiManager wifiManager;
    private Context context;

    /** 定义几种加密方式，一种是WEP，一种是WPA/WPA2，还有没有密码的情况 */
    public enum WifiCipherType {
        WIFI_CIPHER_WEP, WIFI_CIPHER_WPA_EAP, WIFI_CIPHER_WPA_PSK, WIFI_CIPHER_WPA2_PSK,WIFI_CIPHER_WPA3_PSK, WIFI_CIPHER_NOPASS
    }

    public LinkWifi(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context
                .getSystemService(Service.WIFI_SERVICE);
    }

    /**
     * 检测wifi状态 opened return true;
     */
    public boolean checkWifiState() {
        boolean isOpen = true;
        int wifiState = wifiManager.getWifiState();

        if (wifiState == WifiManager.WIFI_STATE_DISABLED
                || wifiState == WifiManager.WIFI_STATE_DISABLING
                || wifiState == WifiManager.WIFI_STATE_UNKNOWN
                || wifiState == WifiManager.WIFI_STATE_ENABLING) {
            isOpen = false;
        }

        return isOpen;
    }

    public boolean ConnectToNetID(int netID) {
        boolean enable = wifiManager.enableNetwork(netID, true);
        // 保存这个网络配置
        wifiManager.saveConfiguration();
        return enable;
    }


    public int updateNetwofkConfig(WifiConfiguration wifiConfiguration){
        return wifiManager.updateNetwork(wifiConfiguration);
    }

    /** 查看以前是否也配置过这个网络 */
    public WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager
                .getConfiguredNetworks();

        if (existingConfigs != null && existingConfigs.size() > 0) {

            for (WifiConfiguration existingConfig : existingConfigs) {
                //LogUtils.i("zouguanrong","---1-->"+existingConfig.SSID);
                //LogUtils.i("zouguanrong","---2-->"+String.valueOf(existingConfig.networkId));
                if (existingConfig.SSID.toString().equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
        }
        return null;
    }

    public int CreateWifiInfo2(ScanResult wifiinfo, String pwd,String secure) {
        WifiCipherType type;
        if (wifiinfo.capabilities.contains("SAE")) {
            // WPA3-PSK加密
            type = WifiCipherType.WIFI_CIPHER_WPA3_PSK;
        } else if (wifiinfo.capabilities.contains("WPA2-PSK")) {
            // WPA2-PSK加密
            type = WifiCipherType.WIFI_CIPHER_WPA2_PSK;
        } else if (wifiinfo.capabilities.contains("WPA-PSK")) {
            // WPA-PSK加密
            type = WifiCipherType.WIFI_CIPHER_WPA_PSK;
        } else if (wifiinfo.capabilities.contains("WPA-EAP")) {
            // WPA-EAP加密
            type = WifiCipherType.WIFI_CIPHER_WPA_EAP;
        } else if (wifiinfo.capabilities.contains("WEP")) {
            // WEP加密
            type = WifiCipherType.WIFI_CIPHER_WEP;
        } else {
            // 无密码
            type = WifiCipherType.WIFI_CIPHER_NOPASS;
        }

        WifiConfiguration config = CreateWifiInfo(wifiinfo.SSID,
                wifiinfo.BSSID, pwd, type,secure);
        if (config != null) {
            // wifiManager.saveConfiguration();
            return wifiManager.addNetwork(config);
        } else {
            return -1;
        }
    }


    public int CreateWifiInfo3(String security,String ssid, String pwd) {
        WifiCipherType type;
        String secure = "";
        if (security.equals("WPA2-PSK")) {
            // WPA2-PSK加密
            type = WifiCipherType.WIFI_CIPHER_WPA2_PSK;
            secure = "WPA2-PSK";
        } else if (security.equals("WPA-PSK")) {
            // WPA-PSK加密
            type = WifiCipherType.WIFI_CIPHER_WPA_PSK;
            secure = "WPA-PSK";
        }else if (security.equals("WPA3-PSK")) {
            // WPA3-PSK加密
            type = WifiCipherType.WIFI_CIPHER_WPA3_PSK;
            secure = "WPA3-PSK";
        } else if (security.equals("WPA-EAP")) {
            // WPA-EAP加密
            type = WifiCipherType.WIFI_CIPHER_WPA_EAP;
            secure = "WPA-EAP";
        } else if (security.equals("WEP")) {
            // WEP加密
            type = WifiCipherType.WIFI_CIPHER_WEP;
            secure = "WEP";
        } else {
            // 无密码
            type = WifiCipherType.WIFI_CIPHER_NOPASS;
            secure = "NONE";
        }

        WifiConfiguration config = CreateWifiInfo(ssid,null, pwd, type,secure);
        if (config != null) {
            // wifiManager.saveConfiguration();
            return wifiManager.addNetwork(config);
        } else {
            return -1;
        }
    }


    public WifiConfiguration setMaxPriority(WifiConfiguration config) {
        int priority = getMaxPriority() + 1;
        if (priority > 99999) {
            priority = shiftPriorityAndSave();
        }

        config.priority = priority; // 2147483647;
        // System.out.println("priority=" + priority);

        wifiManager.updateNetwork(config);

        // 本机之前配置过此wifi热点，直接返回
        return config;
    }

    /**
     * 创建wifi连接信息
     *
     * @param SSID
     * @param pwd
     * @return
     */
    public int CreateWifiInfo(String SSID, String pwd ,int t) {
        WifiCipherType type= WifiCipherType.WIFI_CIPHER_NOPASS;

		/*switch (t) {
		case 1:
			type = WifiCipherType.WIFI_CIPHER_NOPASS;
			break;

		case 2:
			type = WifiCipherType.WIFI_CIPHER_WPA2_PSK;
			break;

		case 3:
			type = WifiCipherType.WIFI_CIPHER_WPA_PSK;
			break;

		case 4:
			type = WifiCipherType.WIFI_CIPHER_WPA_EAP;
			break;

		case 5:
			type = WifiCipherType.WIFI_CIPHER_WEP;
			break;
		}*/

        WifiConfiguration config = getConfig(t,SSID, pwd);
        if (config != null) {
            // wifiManager.saveConfiguration();
            return wifiManager.addNetwork(config);
        } else {
            return -1;
        }
    }

    /**
     * 这里直接是阅读系统设置里面的wifi配置
     * 其实这里可以阅读以下WifiConfiguration,里面关于wifi的加密参数
     * @param accessPointSecurity
     * @param ssid
     * @param password
     * @return
     */
    public static WifiConfiguration getConfig(int accessPointSecurity, String ssid,
                                              String password) {

        WifiConfiguration config = new WifiConfiguration();
        config.SSID = convertToQuotedString(ssid);
        config.hiddenSSID = true;
        switch (accessPointSecurity) {
            case 1:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;

            case 5:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                int length = password.length();
                // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                if ((length == 10 || length == 26 || length == 58)
                        && password.matches("[0-9A-Fa-f]*")) {
                    config.wepKeys[0] = password;
                } else {
                    config.wepKeys[0] = '"' + password + '"';
                }
                break;

            case 2:
            case 3:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                if (password.length() != 0) {
                    if (password.matches("[0-9A-Fa-f]{64}")) {
                        config.preSharedKey = password;
                    } else {
                        config.preSharedKey = '"' + password + '"';
                    }
                }
                break;

            case 4:
                //EAP暂时还没不完整
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
                break;
            default:
                return null;
        }

        return config;
    }

    public static String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }

    /** 配置一个连接 */
    public WifiConfiguration CreateWifiInfo(String SSID, String BSSID,
                                            String password, WifiCipherType type,String secure) {

        int priority;

        WifiConfiguration config = this.IsExsits(SSID);
        if (config != null) {
            // LogUtils.w("Wmt", "####之前配置过这个网络，删掉它");
            // wifiManager.removeNetwork(config.networkId); // 如果之前配置过这个网络，删掉它

            // 本机之前配置过此wifi热点，调整优先级后，直接返回
            return setMaxPriority(config);
        }

        config = new WifiConfiguration();
        /* 清除之前的连接信息 */
        config.allowedAuthAlgorithms.clear();
        //config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        //config.allowedPairwiseCiphers.clear();
        //config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        // config.preSharedKey = "\""+password+"\"";
        config.status = WifiConfiguration.Status.ENABLED;
        // config.BSSID = BSSID;
        // config.hiddenSSID = true;

        priority = getMaxPriority() + 1;
        if (priority > 99999) {
            priority = shiftPriorityAndSave();
        }

        config.priority = priority; // 2147483647;
        /* 各种加密方式判断 */
        if (type == WifiCipherType.WIFI_CIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
            ShareUtil.put(context,SSID,"NONE");
        } else if (type == WifiCipherType.WIFI_CIPHER_WEP) {
            LogUtils.d("Wmt", "WEP加密，密码" + password);
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
            ShareUtil.put(context,SSID,secure);
        } else if (type == WifiCipherType.WIFI_CIPHER_WPA_EAP) {

            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.status = WifiConfiguration.Status.ENABLED;
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);

            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN
                    | WifiConfiguration.Protocol.WPA);
            ShareUtil.put(context,SSID,secure);
        } else if (type == WifiCipherType.WIFI_CIPHER_WPA_PSK ) {

            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;
            ShareUtil.put(context,SSID,secure);

        } else if (type == WifiCipherType.WIFI_CIPHER_WPA2_PSK ) {

            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

            ShareUtil.put(context,SSID,secure);
        } else if ( type == WifiCipherType.WIFI_CIPHER_WPA3_PSK){
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.setSecurityParams(WifiConfiguration.SECURITY_TYPE_SAE);

            ShareUtil.put(context,SSID,secure);
        }else {
            return null;
        }
        return config;
    }

    private int getMaxPriority() {
        List<WifiConfiguration> localList = this.wifiManager
                .getConfiguredNetworks();
        int i = 0;
        Iterator<WifiConfiguration> localIterator = localList.iterator();
        while (true) {
            if (!localIterator.hasNext())
                return i;
            WifiConfiguration localWifiConfiguration = (WifiConfiguration) localIterator
                    .next();
            if (localWifiConfiguration.priority <= i)
                continue;
            i = localWifiConfiguration.priority;
        }
    }

    private int shiftPriorityAndSave() {
        List<WifiConfiguration> localList = this.wifiManager
                .getConfiguredNetworks();
        sortByPriority(localList);
        int i = localList.size();
        for (int j = 0;; ++j) {
            if (j >= i) {
                this.wifiManager.saveConfiguration();
                return i;
            }
            WifiConfiguration localWifiConfiguration = (WifiConfiguration) localList
                    .get(j);
            localWifiConfiguration.priority = j;
            this.wifiManager.updateNetwork(localWifiConfiguration);
        }
    }

    private void sortByPriority(List<WifiConfiguration> paramList) {
        Collections.sort(paramList, new SjrsWifiManagerCompare());
    }

    class SjrsWifiManagerCompare implements Comparator<WifiConfiguration> {
        public int compare(WifiConfiguration paramWifiConfiguration1,
                           WifiConfiguration paramWifiConfiguration2) {
            return paramWifiConfiguration1.priority
                    - paramWifiConfiguration2.priority;
        }
    }
}
