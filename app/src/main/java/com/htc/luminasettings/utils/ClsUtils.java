package com.htc.luminasettings.utils;

import android.bluetooth.BluetoothDevice;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClsUtils {
    public static boolean createBond(Class btClass, BluetoothDevice btDevice)
            throws Exception {
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }
    public static  boolean removeBond(Class btClass, BluetoothDevice btDevice)
            throws Exception {
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }


    public static void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printAllInform(Class clsShow) {
        try {
            Method[] hideMethod = clsShow.getMethods();
            for (Method name : hideMethod) {
                LogUtils.e("method name", name.getName());
            }
            Field[] allFields = clsShow.getFields();
            for (Field name2 : allFields) {
                LogUtils.e("Field name", name2.getName());
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

}
