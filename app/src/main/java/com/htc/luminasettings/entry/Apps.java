package com.htc.luminasettings.entry;

public class Apps {
    public int id;
    public String packageName;
    public boolean resident;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isResident() {
        return resident;
    }

    public void setResident(boolean resident) {
        this.resident = resident;
    }

    @Override
    public String toString() {
        return "App{" +
                "id=" + id +
                ", packageName='" + packageName + '\'' +
                ", resident=" + resident +
                '}';
    }
}
