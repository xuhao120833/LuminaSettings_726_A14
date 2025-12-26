package com.htc.luminasettings.entry;

public class SpecialApps {
    private String appName;
    private String packageName;
    private byte[] iconData; // BLOB 数据
    private String continent;
    private String countryCode;


    public String getPackageName() {
        if(packageName == null || packageName.isEmpty()){

        }
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public byte[] getIconData() {
        return iconData;
    }

    public void setIconData(byte[] iconData) {
        this.iconData = iconData;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }


    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
