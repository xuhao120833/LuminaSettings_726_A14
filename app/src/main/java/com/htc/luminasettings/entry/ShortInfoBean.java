package com.htc.luminasettings.entry;

import android.graphics.drawable.Drawable;

public class ShortInfoBean {
    String packageName;
    private String appname;
    private Drawable appicon;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    public void setAppicon(Drawable appicon) {
        this.appicon = appicon;
    }

    public Drawable getAppicon() {
        return appicon;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getAppname() {
        return appname;
    }
}
