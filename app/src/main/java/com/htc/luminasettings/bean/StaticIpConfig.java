package com.htc.luminasettings.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author htc
 */
public class StaticIpConfig implements  Parcelable{
    private boolean isDhcp;
    private String ip;
    private String gateWay;
    private String netMask;
    private String dns1;
    private String dns2;

    public StaticIpConfig(){
        super();
    }


    protected StaticIpConfig(Parcel in) {
        isDhcp = in.readByte() != 0;
        ip = in.readString();
        gateWay = in.readString();
        netMask = in.readString();
        dns1 = in.readString();
        dns2 = in.readString();
    }

    public static final Creator<StaticIpConfig> CREATOR = new Creator<StaticIpConfig>() {
        @Override
        public StaticIpConfig createFromParcel(Parcel in) {
            return new StaticIpConfig(in);
        }

        @Override
        public StaticIpConfig[] newArray(int size) {
            return new StaticIpConfig[size];
        }
    };

    public boolean isDhcp() {
        return isDhcp;
    }

    public void setDhcp(boolean dhcp) {
        isDhcp = dhcp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getGateWay() {
        return gateWay;
    }

    public void setGateWay(String gateWay) {
        this.gateWay = gateWay;
    }

    public String getNetMask() {
        return netMask;
    }

    public void setNetMask(String netMask) {
        this.netMask = netMask;
    }

    public String getDns1() {
        return dns1;
    }

    public void setDns1(String dns1) {
        this.dns1 = dns1;
    }

    public String getDns2() {
        return dns2;
    }

    public void setDns2(String dns2) {
        this.dns2 = dns2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isDhcp ? 1 : 0));
        dest.writeString(ip);
        dest.writeString(gateWay);
        dest.writeString(netMask);
        dest.writeString(dns1);
        dest.writeString(dns2);
    }
}
