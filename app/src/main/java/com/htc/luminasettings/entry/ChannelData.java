package com.htc.luminasettings.entry;

import java.util.ArrayList;

public class ChannelData {

    int code;
    String message;
    ArrayList<AppsData> data ;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<AppsData> getData() {
        return data;
    }

    public void setData(ArrayList<AppsData> data) {
        this.data = data;
    }

}
