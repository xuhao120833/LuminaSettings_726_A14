package com.htc.luminasettings.entry;

import java.text.Collator;
import java.util.Locale;

public class Language implements Comparable<Language> {
    static Collator sCollator = Collator.getInstance();
    private int iconRes;
    private String label;
    private Locale locale;

    public Language(String label2, Locale locale2) {
        this.label = label2;
        this.locale = locale2;
    }

    public String toString() {
        return this.label;
    }

    public int compareTo(Language lang) {
        return sCollator.compare(this.label, lang.label);
//        return this.label.compareToIgnoreCase(lang.label);
    }

    public int getIconRes() {
        return this.iconRes;
    }

    public void setIconRes(int iconRes2) {
        this.iconRes = iconRes2;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label2) {
        this.label = label2;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale2) {
        this.locale = locale2;
    }
}
