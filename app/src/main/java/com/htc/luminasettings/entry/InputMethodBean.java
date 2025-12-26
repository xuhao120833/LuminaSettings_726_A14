package com.htc.luminasettings.entry;

import java.io.Serializable;

public class InputMethodBean implements Serializable {
    private static final long serialVersionUID = 1;
    private String inputname;
    private String prefkey;

    public String getPrefkey() {
        return this.prefkey;
    }

    public void setPrefkey(String prefkey2) {
        this.prefkey = prefkey2;
    }

    public String getInputname() {
        return this.inputname;
    }

    public void setInputname(String inputname2) {
        this.inputname = inputname2;
    }

    public InputMethodBean() {
    }

    public InputMethodBean(String prefkey2, String inputname2) {
        this.prefkey = prefkey2;
        this.inputname = inputname2;
    }

    public String toString() {
        return "InputMethodBean [prefkey=" + this.prefkey + ", inputname=" + this.inputname + "]";
    }
}
