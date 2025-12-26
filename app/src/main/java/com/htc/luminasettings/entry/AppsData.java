package com.htc.luminasettings.entry;

import java.io.Serializable;

public class AppsData implements Serializable {

    String name;
    String pkg;
    String desc;
    String icon;
    String category;
    String zone;
    String verName;
    int verCode;
    String path;
    int size;
    String md5;
    String developer;
    String upDate;

    boolean isForce;
    String verDesc;
    int reverseLen;
    int installType=0;
    String appType = "apk";

    public String getVerDesc() {
        return verDesc;
    }

    public void setVerDesc(String verDesc) {
        this.verDesc = verDesc;
    }

    public int getReverseLen() {
        return reverseLen;
    }

    public void setReverseLen(int reverseLen) {
        this.reverseLen = reverseLen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getVerName() {
        return verName;
    }

    public void setVerName(String verName) {
        this.verName = verName;
    }

    public int getVerCode() {
        return verCode;
    }

    public void setVerCode(int verCode) {
        this.verCode = verCode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getUpDate() {
        return upDate;
    }

    public void setUpDate(String upDate) {
        this.upDate = upDate;
    }

    public boolean isForce() {
        return isForce;
    }

    public void setForce(boolean force) {
        isForce = force;
    }

    public int getInstallType() {
        return installType;
    }

    public void setInstallType(int installType) {
        this.installType = installType;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }


/*public AppsData(Parcel in) {
        name = in.readString();
        pkg = in.readString();
        desc = in.readString();
        icon = in.readString();
        path = in.readString();
        category = in.readString();
        zone = in.readString();
        verName = in.readString();
        verDesc = in.readString();
        verCode = in.readInt();
        md5 = in.readString();
        upDate = in.readString();
        developer = in.readString();
        reverseLen = in.readInt();
        size = in.readInt();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isForce = in.readBoolean();
        }
    }

    public static final Parcelable.Creator<AppsData> CREATOR = new Parcelable.Creator<AppsData>() {
        @Override
        public AppsData createFromParcel(Parcel in) {
            return new AppsData(in);
        }

        @Override
        public AppsData[] newArray(int size) {
            return new AppsData[size];
        }
    };*/

    /*@Override
    public int describeContents() {
        return 0;
    }*/

    /*@Override
    public void writeToParcel(Parcel dest, int flags) {
         dest.writeString(name);
         dest.writeString(pkg);
         dest.writeString(desc);
         dest.writeString(icon);
         dest.writeString(path);
         dest.writeString(category);
         dest.writeString(zone);
         dest.writeString(verName);
         dest.writeString(verDesc);
         dest.writeInt(verCode);
         dest.writeString(md5);
         dest.writeString(upDate);
         dest.writeString(developer);
         dest.writeInt(reverseLen);
         dest.writeInt(size);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(isForce);
        }
    }*/

}
