package com.htc.luminaos.utils;

import android.content.Context;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

import java.text.DecimalFormat;

import com.softwinner.tv.AwTvDisplayManager;

/**
 * 梯形校正工具
 */
public class KeystoneUtils_726 {

    //四个角距离四个原点的相对距离
    public static final String PROP_KEYSTONE_LB_X = "persist.display.keystone_lbx";
    public static final String PROP_KEYSTONE_LB_Y = "persist.display.keystone_lby";
    public static final String PROP_KEYSTONE_LT_X = "persist.display.keystone_ltx";
    public static final String PROP_KEYSTONE_LT_Y = "persist.display.keystone_lty";
    public static final String PROP_KEYSTONE_RB_X = "persist.display.keystone_rbx";
    public static final String PROP_KEYSTONE_RB_Y = "persist.display.keystone_rby";
    public static final String PROP_KEYSTONE_RT_X = "persist.display.keystone_rtx";
    public static final String PROP_KEYSTONE_RT_Y = "persist.display.keystone_rty";

    //HTC自己加的
    public static final String PROP_HTC_KEYSTONE_LB_X = "persist.htc.keystone.lbx";
    public static final String PROP_HTC_KEYSTONE_LB_Y = "persist.htc.keystone.lby";
    public static final String PROP_HTC_KEYSTONE_LT_X = "persist.htc.keystone.ltx";
    public static final String PROP_HTC_KEYSTONE_LT_Y = "persist.htc.keystone.lty";
    public static final String PROP_HTC_KEYSTONE_RB_X = "persist.htc.keystone.rbx";
    public static final String PROP_HTC_KEYSTONE_RB_Y = "persist.htc.keystone.rby";
    public static final String PROP_HTC_KEYSTONE_RT_X = "persist.htc.keystone.rtx";
    public static final String PROP_HTC_KEYSTONE_RT_Y = "persist.htc.keystone.rty";

    //数字缩放
    public static final String ZOOM_VALUE = "zoom_value";
    public static final String PROP_ZOOM_VALUE = "persist.sys.zoom_value";
    //画面比例
    public static final String ZOOM_SCALE = "zoom_scale";
    public static final String PROP_ZOOM_SCALE = "persist.sys.zoom_scale";
    public static final String PROP_ZOOM_SCALE_OLD = "persist.sys.zoom_scale_old";
    public static final String ZOOM_SCALE_OLD = "zoom_scale_old";

    public static int lcd_w = 1920;
    public static int lcd_h = 1080;


    //四个角相对于原来四个原点的相对距离
    public static int lb_X = 0;
    public static int lb_Y = 0;
    public static int rb_X = 0;
    public static int rb_Y = 0;
    public static int lt_X = 0;
    public static int lt_Y = 0;
    public static int rt_X = 0;
    public static int rt_Y = 0;

    private static int CoverY(String prop) {
        return SystemProperties.getInt(prop, 0);
    }

    private static String TAG = "KeystoneUtils_726";

    public static void initKeystoneData() {

        lb_X = CoverX(PROP_KEYSTONE_LB_X);
        lb_Y = CoverY(PROP_KEYSTONE_LB_Y);
        rb_X = CoverX(PROP_KEYSTONE_RB_X);
        rb_Y = CoverY(PROP_KEYSTONE_RB_Y);
        lt_X = CoverX(PROP_KEYSTONE_LT_X);
        lt_Y = CoverY(PROP_KEYSTONE_LT_Y);
        rt_X = CoverX(PROP_KEYSTONE_RT_X);
        rt_Y = CoverY(PROP_KEYSTONE_RT_Y);
    }

    /**
     * 获取左上角坐标
     *
     * @return
     */
    public static int[] getKeystoneLeftAndTopXY() {
        int[] xy = new int[]{0, 0};
        xy[0] = CoverX(PROP_KEYSTONE_LT_X);
        xy[1] = CoverY(PROP_KEYSTONE_LT_Y);
        return xy;
    }

    /**
     * 获取左下角坐标
     *
     * @return
     */
    public static int[] getKeystoneLeftAndBottomXY() {
        int[] xy = new int[]{0, 0};
        xy[0] = CoverX(PROP_KEYSTONE_LB_X);
        xy[1] = CoverY(PROP_KEYSTONE_LB_Y);
        return xy;
    }

    /**
     * 获取右上角坐标
     *
     * @return
     */
    public static int[] getKeystoneRightAndTopXY() {
        int[] xy = new int[]{0, 0};
        xy[0] = CoverX(PROP_KEYSTONE_RT_X);
        xy[1] = CoverY(PROP_KEYSTONE_RT_Y);
        return xy;
    }

    /**
     * 获取右下角坐标
     *
     * @return
     */
    public static int[] getKeystoneRightAndBottomXY() {
        int[] xy = new int[]{0, 0};
        xy[0] = CoverX(PROP_KEYSTONE_RB_X);
        xy[1] = CoverY(PROP_KEYSTONE_RB_Y);
        return xy;
    }

    /************OppositeTo
     ************/
    public static int[] getKeystoneOppositeToLeftAndTopXY() {
        int[] xy = new int[]{0, 0};
        xy[0] = CoverX(PROP_KEYSTONE_RT_X);
        xy[1] = CoverY(PROP_KEYSTONE_LB_Y);
        return xy;
    }

    public static int[] getKeystoneOppositeToLeftAndBottomXY() {
        int[] xy = new int[]{0, 0};
        xy[0] = CoverX(PROP_KEYSTONE_RB_X);
        xy[1] = CoverY(PROP_KEYSTONE_LT_Y);
        return xy;
    }

    public static int[] getKeystoneOppositeToRightAndTopXY() {
        int[] xy = new int[]{0, 0};
        xy[0] = CoverX(PROP_KEYSTONE_LT_X);
        xy[1] = CoverY(PROP_KEYSTONE_RB_Y);
        return xy;
    }

    public static int[] getKeystoneOppositeToRightAndBottomXY() {
        int[] xy = new int[]{0, 0};
        xy[0] = CoverX(PROP_KEYSTONE_LB_X);
        xy[1] = CoverY(PROP_KEYSTONE_RT_Y);
        return xy;
    }

    /**
     * 设置四角值
     *
     * @param type 左上 1  左下2  右上 3 右下  4
     * @param xy   坐标
     */
    public static void setkeystoneValue(int type, int[] xy, boolean rtl) {
        int x = xy[0];
        int y = xy[1];
        if (x < 0 || y < 0)
            return;
        int type_1 = 0;
        if (rtl) {
            switch (type) {
                case 1:
                    type_1 = 3;
                    break;

                case 2:
                    type_1 = 4;
                    break;

                case 3:
                    type_1 = 1;
                    break;
                case 4:
                    type_1 = 2;
                    break;
            }
        } else {
            type_1 = type;
        }
        switch (type_1) {
            case 1:
                lt_X = x;
                lt_Y = y;
                UpdateKeystone();
                break;
            case 2:
                lb_X = x;
                lb_Y = y;
                UpdateKeystone();
                break;
            case 3:
                rt_X = x;
                rt_Y = y;
                UpdateKeystone();
                break;
            case 4:
                rb_X = x;
                rb_Y = y;
                UpdateKeystone();
                break;
        }
    }

    private static int CoverX(String prop) {

        return SystemProperties.getInt(prop, 0);
    }

    private static void writeParcelToFlinger(int ltx, int lty, int rtx, int rty, int lbx, int lby, int rbx, int rby) {
        try {
//            AwTvDisplayManager.getInstance().setKeystoreValue(100.0f,100.0f,ltx, lty, rtx, rty, lbx, lby, rbx, rby);
            LogUtils.d(TAG, " writeParcelToFlinger ");
            AwTvDisplayManager.getInstance().setKeystoreValue(100.0f, 100.0f, lbx, lby, rbx, rby, ltx, lty, rtx, rty);
        } catch (Exception ex) {
            LogUtils.i(TAG, "error talk with surfaceflinger service");
        }
    }


    public static void UpdateKeystone() {
        LogUtils.d("UpdateKeystone", "rb_X " + rb_X + "rb_Y " + rb_Y);
        writeParcelToFlinger(lt_X, lt_Y, rt_X, rt_Y, lb_X, lb_Y, rb_X, rb_Y);
    }

    public static void UpdateKeystoneZOOMNC() {
        SystemProperties.set("persist.sys.zoom.value", lb_X + "," + lb_Y + "," + lt_X + "," + lt_Y + "," + rt_X + "," + rt_Y + "," + rb_X + "," + rb_Y);
        LogUtils.d("UpdateKeystoneZOOMNC ", lb_X + "," + lb_Y + "," + lt_X + "," + lt_Y + "," + rt_X + "," + rt_Y + "," + rb_X + "," + rb_Y);
//        writeParcelToFlinger(lt_X, lt_Y, rt_X, rt_Y, lb_X, lb_Y, rb_X, rb_Y);
    }

    public static void UpdateKeystoneZOOM(boolean write) { //有摄像头
        LogUtils.d("UpdateKeystoneZOOM before ", lb_X + "," + lb_Y + "," + lt_X + "," + lt_Y + "," + rt_X + "," + rt_Y + "," + rb_X + "," + rb_Y);
        if (!write) {
            SystemProperties.set("persist.sys.zoom.value", lb_X + "," + lb_Y + "," + lt_X + "," + lt_Y + "," + rt_X + "," + rt_Y + "," + rb_X + "," + rb_Y);
            return;
        }
//        writeParcelToFlinger(lt_X, lt_Y, rt_X, rt_Y, lb_X, lb_Y, rb_X, rb_Y);
        float ofltx = lt_X / 1000.000f;
        float oflty = lt_Y / 1000.000f;
        float ofrtx = rt_X / 1000.000f;
        float ofrty = rt_Y / 1000.000f;
        float oflbx = lb_X / 1000.000f;
        float oflby = lb_Y / 1000.000f;
        float ofrbx = rb_X / 1000.000f;
        float ofrby = rb_Y / 1000.000f;
        LogUtils.d("UpdateKeystoneZOOM", " lcd_w " + lcd_w + " lcd_h " + lcd_h);

        writeParcelToFlinger((int) (ofltx * lcd_w), (int) (oflty * lcd_h), (int) (ofrtx * lcd_w), (int) (ofrty * lcd_h), (int) (oflbx * lcd_w), (int) (oflby * lcd_h), (int) (ofrbx * lcd_w), (int) (ofrby * lcd_h));
        SystemProperties.set("persist.sys.zoom.value", lb_X + "," + lb_Y + "," + lt_X + "," + lt_Y + "," + rt_X + "," + rt_Y + "," + rb_X + "," + rb_Y);
        LogUtils.d("UpdateKeystoneZOOM after ", lb_X + "," + lb_Y + "," + lt_X + "," + lt_Y + "," + rt_X + "," + rt_Y + "," + rb_X + "," + rb_Y);
    }

    public static void setKeystoneNormalXY(int cur_mode, int new_mode) {
        int lt_Nx, lt_Ny, rt_Nx, rt_Ny;
        int lb_Nx, lb_Ny, rb_Nx, rb_Ny;
        int lt_x, lt_y, rt_x, rt_y;
        int lb_x, lb_y, rb_x, rb_y;
        int[] lt_xy = getKeystoneLeftAndTopXY();
        int[] rt_xy = getKeystoneRightAndTopXY();
        int[] lb_xy = getKeystoneLeftAndBottomXY();
        int[] rb_xy = getKeystoneRightAndBottomXY();
        if (cur_mode == 1) {//LR
            rt_Nx = lt_xy[0];
            rt_Ny = lt_xy[1];
            lt_Nx = rt_xy[0];
            lt_Ny = rt_xy[1];
            rb_Nx = lb_xy[0];
            rb_Ny = lb_xy[1];
            lb_Nx = rb_xy[0];
            lb_Ny = rb_xy[1];
        } else if (cur_mode == 2) {//LRUD
            rb_Nx = lt_xy[0];
            rb_Ny = lt_xy[1];
            lb_Nx = rt_xy[0];
            lb_Ny = rt_xy[1];
            rt_Nx = lb_xy[0];
            rt_Ny = lb_xy[1];
            lt_Nx = rb_xy[0];
            lt_Ny = rb_xy[1];
        } else if (cur_mode == 3) {//UD
            lb_Nx = lt_xy[0];
            lb_Ny = lt_xy[1];
            rb_Nx = rt_xy[0];
            rb_Ny = rt_xy[1];
            lt_Nx = lb_xy[0];
            lt_Ny = lb_xy[1];
            rt_Nx = rb_xy[0];
            rt_Ny = rb_xy[1];
        } else {//nor
            lt_Nx = lt_xy[0];
            lt_Ny = lt_xy[1];
            rt_Nx = rt_xy[0];
            rt_Ny = rt_xy[1];
            lb_Nx = lb_xy[0];
            lb_Ny = lb_xy[1];
            rb_Nx = rb_xy[0];
            rb_Ny = rb_xy[1];
        }

        if (new_mode == 1) {//LR
            lt_x = rt_Nx;
            lt_y = rt_Ny;
            rt_x = lt_Nx;
            rt_y = lt_Ny;
            lb_x = rb_Nx;
            lb_y = rb_Ny;
            rb_x = lb_Nx;
            rb_y = lb_Ny;
        } else if (new_mode == 2) {//LRUD
            lt_x = rb_Nx;
            lt_y = rb_Ny;
            rt_x = lb_Nx;
            rt_y = lb_Ny;
            lb_x = rt_Nx;
            lb_y = rt_Ny;
            rb_x = lt_Nx;
            rb_y = lt_Ny;
        } else if (new_mode == 3) {//UD
            lt_x = lb_Nx;
            lt_y = lb_Ny;
            rt_x = rb_Nx;
            rt_y = rb_Ny;
            lb_x = lt_Nx;
            lb_y = lt_Ny;
            rb_x = rt_Nx;
            rb_y = rt_Ny;
        } else { //
            lt_x = lt_Nx;
            lt_y = lt_Ny;
            rt_x = rt_Nx;
            rt_y = rt_Ny;
            lb_x = lb_Nx;
            lb_y = lb_Ny;
            rb_x = rb_Nx;
            rb_y = rb_Ny;
        }
        lt_X = lt_x;
        lt_Y = lt_y;
        rt_X = rt_x;
        rt_Y = rt_y;
        rb_X = rb_x;
        rb_Y = rb_y;
        lb_X = lb_x;
        lb_Y = lb_y;
//        UpdateKeystoneZOOM(true);
        writeParcelToFlinger(lt_x, lt_y, rt_x, rt_y, lb_x, lb_y, rb_x, rb_y);
        lt_xy = getKeystoneHtcLeftAndTopXY();
        rt_xy = getKeystoneHtcRightAndTopXY();
        lb_xy = getKeystoneHtcLeftAndBottomXY();
        rb_xy = getKeystoneHtcRightAndBottomXY();
        if (cur_mode == 1) {//LR
            rt_Nx = lt_xy[0];
            rt_Ny = lt_xy[1];
            lt_Nx = rt_xy[0];
            lt_Ny = rt_xy[1];
            rb_Nx = lb_xy[0];
            rb_Ny = lb_xy[1];
            lb_Nx = rb_xy[0];
            lb_Ny = rb_xy[1];
        } else if (cur_mode == 2) {//LRUD
            rb_Nx = lt_xy[0];
            rb_Ny = lt_xy[1];
            lb_Nx = rt_xy[0];
            lb_Ny = rt_xy[1];
            rt_Nx = lb_xy[0];
            rt_Ny = lb_xy[1];
            lt_Nx = rb_xy[0];
            lt_Ny = rb_xy[1];
        } else if (cur_mode == 3) {//UD
            lb_Nx = lt_xy[0];
            lb_Ny = lt_xy[1];
            rb_Nx = rt_xy[0];
            rb_Ny = rt_xy[1];
            lt_Nx = lb_xy[0];
            lt_Ny = lb_xy[1];
            rt_Nx = rb_xy[0];
            rt_Ny = rb_xy[1];
        } else {//nor
            lt_Nx = lt_xy[0];
            lt_Ny = lt_xy[1];
            rt_Nx = rt_xy[0];
            rt_Ny = rt_xy[1];
            lb_Nx = lb_xy[0];
            lb_Ny = lb_xy[1];
            rb_Nx = rb_xy[0];
            rb_Ny = rb_xy[1];
        }

        if (new_mode == 1) {//LR
            lt_x = rt_Nx;
            lt_y = rt_Ny;
            rt_x = lt_Nx;
            rt_y = lt_Ny;
            lb_x = rb_Nx;
            lb_y = rb_Ny;
            rb_x = lb_Nx;
            rb_y = lb_Ny;
        } else if (new_mode == 2) {//LRUD
            lt_x = rb_Nx;
            lt_y = rb_Ny;
            rt_x = lb_Nx;
            rt_y = lb_Ny;
            lb_x = rt_Nx;
            lb_y = rt_Ny;
            rb_x = lt_Nx;
            rb_y = lt_Ny;
        } else if (new_mode == 3) {//UD
            lt_x = lb_Nx;
            lt_y = lb_Ny;
            rt_x = rb_Nx;
            rt_y = rb_Ny;
            lb_x = lt_Nx;
            lb_y = lt_Ny;
            rb_x = rt_Nx;
            rb_y = rt_Ny;
        } else { //
            lt_x = lt_Nx;
            lt_y = lt_Ny;
            rt_x = rt_Nx;
            rt_y = rt_Ny;
            lb_x = lb_Nx;
            lb_y = lb_Ny;
            rb_x = rb_Nx;
            rb_y = rb_Ny;
        }
        SystemProperties.set(PROP_HTC_KEYSTONE_LT_X, String.valueOf(lt_x));
        SystemProperties.set(PROP_HTC_KEYSTONE_LT_Y, String.valueOf(lt_y));
        SystemProperties.set(PROP_HTC_KEYSTONE_LB_X, String.valueOf(lb_x));
        SystemProperties.set(PROP_HTC_KEYSTONE_LB_Y, String.valueOf(lb_y));
        SystemProperties.set(PROP_HTC_KEYSTONE_RT_X, String.valueOf(rt_x));
        SystemProperties.set(PROP_HTC_KEYSTONE_RT_Y, String.valueOf(rt_y));
        SystemProperties.set(PROP_HTC_KEYSTONE_RB_X, String.valueOf(rb_x));
        SystemProperties.set(PROP_HTC_KEYSTONE_RB_Y, String.valueOf(rb_y));
    }

    /**
     * 获取四角梯形矫正记录的左上角坐标
     *
     * @return
     */
    public static int[] getKeystoneHtcLeftAndTopXY() {
        int[] xy = new int[]{0, 0};
        xy[0] = CoverX(PROP_HTC_KEYSTONE_LT_X);
        xy[1] = CoverY(PROP_HTC_KEYSTONE_LT_Y);
        return xy;
    }

    /**
     * 获取四角梯形矫正记录的左下角坐标
     *
     * @return
     */
    public static int[] getKeystoneHtcLeftAndBottomXY() {
        int[] xy = new int[]{0, 0};
        xy[0] = CoverX(PROP_HTC_KEYSTONE_LB_X);
        xy[1] = CoverY(PROP_HTC_KEYSTONE_LB_Y);
        return xy;
    }

    /**
     * 获取四角梯形矫正记录的右上角坐标
     *
     * @return
     */
    public static int[] getKeystoneHtcRightAndTopXY() {
        int[] xy = new int[]{0, 0};
        xy[0] = CoverX(PROP_HTC_KEYSTONE_RT_X);
        xy[1] = CoverY(PROP_HTC_KEYSTONE_RT_Y);
        return xy;
    }

    /**
     * 获取四角梯形矫正记录的右下角坐标
     *
     * @return
     */
    public static int[] getKeystoneHtcRightAndBottomXY() {
        int[] xy = new int[]{0, 0};
        xy[0] = CoverX(PROP_HTC_KEYSTONE_RB_X);
        xy[1] = CoverY(PROP_HTC_KEYSTONE_RB_Y);
        return xy;
    }

    public static void optKeystoneFun(int[] tpData) {
        lt_X = tpData[0];
        lt_Y = tpData[1];
        rt_X = tpData[2];
        rt_Y = tpData[3];
        lb_X = tpData[4];
        lb_Y = tpData[5];
        rb_X = tpData[6];
        rb_Y = tpData[7];
        SystemProperties.set("persist.sys.zoom.value", lb_X + "," + lb_Y + "," + lt_X + "," + lt_Y + "," + rt_X + "," + rt_Y + "," + rb_X + "," + rb_Y);
        LogUtils.d("UpdateKeystoneZOOMNC ", lb_X + "," + lb_Y + "," + lt_X + "," + lt_Y + "," + rt_X + "," + rt_Y + "," + rb_X + "," + rb_Y);
        writeParcelToFlinger(lt_X, lt_Y, rt_X, rt_Y, lb_X, lb_Y, rb_X, rb_Y);

//        DecimalFormat df = new DecimalFormat("0");//格式化小数
//        lt_X = Integer.parseInt(df.format((tpData[0] * 1000) / lcd_w));
//        lt_Y = Integer.parseInt(df.format((tpData[1] * 1000) / lcd_h));
//        rt_X = Integer.parseInt(df.format((tpData[2] * 1000) / lcd_w));
//        rt_Y = Integer.parseInt(df.format((tpData[3] * 1000) / lcd_h));
//        lb_X = Integer.parseInt(df.format((tpData[4] * 1000) / lcd_w));
//        lb_Y = Integer.parseInt(df.format((tpData[5] * 1000) / lcd_h));
//        rb_X = Integer.parseInt(df.format((tpData[6] * 1000) / lcd_w));
//        rb_Y = Integer.parseInt(df.format((tpData[7] * 1000) / lcd_h));
//        UpdateKeystoneZOOM(true);
    }

    public static void resetKeystone() {
        lt_X = 0;
        lt_Y = 0;
        rt_X = 0;
        rt_Y = 0;
        rb_X = 0;
        rb_Y = 0;
        lb_X = 0;
        lb_Y = 0;
        UpdateKeystone();
        SystemProperties.set("persist.sys.zoom.value", lb_X + "," + lb_Y + "," + lt_X + "," + lt_Y + "," + rt_X + "," + rt_Y + "," + rb_X + "," + rb_Y);

        SystemProperties.set(PROP_HTC_KEYSTONE_LT_X, String.valueOf(lt_X));
        SystemProperties.set(PROP_HTC_KEYSTONE_LT_Y, String.valueOf(lt_Y));
        SystemProperties.set(PROP_HTC_KEYSTONE_LB_X, String.valueOf(lb_X));
        SystemProperties.set(PROP_HTC_KEYSTONE_LB_Y, String.valueOf(lb_Y));
        SystemProperties.set(PROP_HTC_KEYSTONE_RT_X, String.valueOf(rt_X));
        SystemProperties.set(PROP_HTC_KEYSTONE_RT_Y, String.valueOf(rt_Y));
        SystemProperties.set(PROP_HTC_KEYSTONE_RB_X, String.valueOf(rb_X));
        SystemProperties.set(PROP_HTC_KEYSTONE_RB_Y, String.valueOf(rb_Y));
    }

    public static void writeGlobalSettings(Context context, String key, int value) {
        Settings.Global.putInt(context.getContentResolver(), key, value);
    }

    public static int readGlobalSettings(Context context, String key, int def) {
        return Settings.Global.getInt(context.getContentResolver(), key, def);
    }

    public static void writeSystemProperties(String key, int value) {
//        Settings.Global.putInt(context.getContentResolver(), key, value);
        SystemProperties.set(key, String.valueOf(value));
    }

    public static int readSystemProperties(String key, int def) {
        return SystemProperties.getInt(key, def);
    }


}
