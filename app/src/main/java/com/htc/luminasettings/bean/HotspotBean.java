package com.htc.luminasettings.bean;

/**
 * Author:
 * Date:
 * Description:
 */
public class HotspotBean {
    String mask="";
    String ip="";
    String mac="";

    public HotspotBean(String mask,String ip,String mac){
        this.mask = mask;
        this.ip=ip;
        this.mac=mac;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getMask() {
        return mask;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }
}
