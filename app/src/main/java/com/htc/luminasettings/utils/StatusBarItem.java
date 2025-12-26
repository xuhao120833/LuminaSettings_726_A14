package com.htc.luminasettings.utils;

public class StatusBarItem {
    public String tag;
    public String iconPath;
    public String iconPath2;
    public String iconDirectory;

    public StatusBarItem(String tag, String iconPath, String iconPath2, String iconDirectory) {
        this.tag = tag;
        this.iconPath = iconPath;
        this.iconPath2 = iconPath2;
        this.iconDirectory = iconDirectory;
    }
}
