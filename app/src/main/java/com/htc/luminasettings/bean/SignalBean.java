package com.htc.luminasettings.bean;

/**
 * Author:
 * Date:
 * Description:
 */
public class SignalBean {
    public String name ="no signal";
    public boolean status =false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }
}
