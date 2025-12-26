package com.htc.luminasettings.utils;

import android.content.Context;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;

import java.text.DecimalFormat;

/**
 * 梯形校正工具
 */
public class KeystoneUtils {

	public static final String PROP_KEYSTONE_LB_X = "persist.display.keystone_lbx";
	public static final String PROP_KEYSTONE_LB_Y = "persist.display.keystone_lby";
	public static final String PROP_KEYSTONE_LT_X = "persist.display.keystone_ltx";
	public static final String PROP_KEYSTONE_LT_Y = "persist.display.keystone_lty";
	public static final String PROP_KEYSTONE_RB_X = "persist.display.keystone_rbx";
	public static final String PROP_KEYSTONE_RB_Y = "persist.display.keystone_rby";
	public static final String PROP_KEYSTONE_RT_X = "persist.display.keystone_rtx";
	public static final String PROP_KEYSTONE_RT_Y = "persist.display.keystone_rty";

	public static final String ZOOM_VALUE = "zoom_value";
	public static final String ZOOM_SCALE = "zoom_scale";
	public static final String PROP_ZOOM_SCALE = "persist.sys.zoom_scale";
	public static final String ZOOM_SCALE_OLD = "zoom_scale_old";

	public static final String PROP_HTC_KEYSTONE_LB_X = "persist.htc.keystone.lbx";
	public static final String PROP_HTC_KEYSTONE_LB_Y = "persist.htc.keystone.lby";
	public static final String PROP_HTC_KEYSTONE_LT_X = "persist.htc.keystone.ltx";
	public static final String PROP_HTC_KEYSTONE_LT_Y = "persist.htc.keystone.lty";
	public static final String PROP_HTC_KEYSTONE_RB_X = "persist.htc.keystone.rbx";
	public static final String PROP_HTC_KEYSTONE_RB_Y = "persist.htc.keystone.rby";
	public static final String PROP_HTC_KEYSTONE_RT_X = "persist.htc.keystone.rtx";
	public static final String PROP_HTC_KEYSTONE_RT_Y = "persist.htc.keystone.rty";

	public static final int minX=0;
	public static final int minY=0;
	//public static final int minH_size=480;//960/2=480 480-480/4=360
	//public static final int minV_size=270;//540/2=270 270-270/4=202

//	public static  int minH_size=500;//960/2=480 480-480/4=360
//	public static  int minV_size=500;//540/2=270 270-270/4=202

	public static  int minH_size=1000;//960/2=480 480-480/4=360
	public static  int minV_size=1000;//540/2=270 270-270/4=202
	public static  int lcd_w=1920;
	public static  int lcd_h=1080;


	public static int lb_X = 0;
	public static int lb_Y = 0;
	public static int rb_X = 0;
	public static int rb_Y= 0;
	public static int lt_X = 0;
	public static int lt_Y = 0;
	public static int rt_X = 0;
	public static int rt_Y = 0;

	public static void initKeystoneData(){

		lb_X = CoverX(PROP_KEYSTONE_LB_X);
		lb_Y = CoverY(PROP_KEYSTONE_LB_Y);
		rb_X =CoverX(PROP_KEYSTONE_RB_X);
		rb_Y =CoverY(PROP_KEYSTONE_RB_Y);
		lt_X =CoverX(PROP_KEYSTONE_LT_X);
		lt_Y =CoverY(PROP_KEYSTONE_LT_Y);
		rt_X =CoverX(PROP_KEYSTONE_RT_X);
		rt_Y =CoverY(PROP_KEYSTONE_RT_Y);
	}

	/**
	 * 获取左上角坐标
	 * @return
	 */
	public static int[] getKeystoneLeftAndTopXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_KEYSTONE_LT_X) ;
		xy[1] =CoverY(PROP_KEYSTONE_LT_Y);
		return xy;
	}

	/**
	 * 获取左下角坐标
	 * @return
	 */
	public static int[] getKeystoneLeftAndBottomXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_KEYSTONE_LB_X) ;
		xy[1] =CoverY(PROP_KEYSTONE_LB_Y);
		return xy;
	}

	/**
	 * 获取右上角坐标
	 * @return
	 */
	public static int[] getKeystoneRightAndTopXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_KEYSTONE_RT_X);
		xy[1] =CoverY(PROP_KEYSTONE_RT_Y);
		return xy;
	}

	/**
	 * 获取右下角坐标
	 * @return
	 */
	public static int[] getKeystoneRightAndBottomXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_KEYSTONE_RB_X);
		xy[1] =CoverY(PROP_KEYSTONE_RB_Y);
		return xy;
	}

	/**
	 * 获取四角梯形矫正记录的左上角坐标
	 * @return
	 */
	public static int[] getKeystoneHtcLeftAndTopXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_HTC_KEYSTONE_LT_X) ;
		xy[1] =CoverY(PROP_HTC_KEYSTONE_LT_Y);
		return xy;
	}

	/**
	 * 获取四角梯形矫正记录的左下角坐标
	 * @return
	 */
	public static int[] getKeystoneHtcLeftAndBottomXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_HTC_KEYSTONE_LB_X) ;
		xy[1] =CoverY(PROP_HTC_KEYSTONE_LB_Y);
		return xy;
	}

	/**
	 * 获取四角梯形矫正记录的右上角坐标
	 * @return
	 */
	public static int[] getKeystoneHtcRightAndTopXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_HTC_KEYSTONE_RT_X);
		xy[1] =CoverY(PROP_HTC_KEYSTONE_RT_Y);
		return xy;
	}

	/**
	 * 获取四角梯形矫正记录的右下角坐标
	 * @return
	 */
	public static int[] getKeystoneHtcRightAndBottomXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_HTC_KEYSTONE_RB_X);
		xy[1] =CoverY(PROP_HTC_KEYSTONE_RB_Y);
		return xy;
	}

	/************OppositeTo
	 ************/
	public static int[] getKeystoneOppositeToLeftAndTopXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_KEYSTONE_RT_X);
		xy[1] =CoverY(PROP_KEYSTONE_LB_Y);
		return xy;
	}

	public static int[] getKeystoneOppositeToLeftAndBottomXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_KEYSTONE_RB_X);
		xy[1] =CoverY(PROP_KEYSTONE_LT_Y);
		return xy;
	}

	public static int[] getKeystoneOppositeToRightAndTopXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_KEYSTONE_LT_X);
		xy[1] =CoverY(PROP_KEYSTONE_RB_Y);
		return xy;
	}

	public static int[] getKeystoneOppositeToRightAndBottomXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_KEYSTONE_LB_X);
		xy[1] =CoverY(PROP_KEYSTONE_RT_Y);
		return xy;
	}
	/**
	 * 设置四角值
	 * @param type 左上 1  左下2  右上 3 右下  4
	 * @param xy 坐标
	 */
	public static void setkeystoneValue(int type , int[] xy){
		int x=xy[0];
		int y=xy[1];
		int[] xy_OppositeTo = new int[] { 0, 0 };
		switch (type) {
			case 1:

				xy_OppositeTo = getKeystoneOppositeToLeftAndTopXY();
				if(x>=minX && (x+xy_OppositeTo[0])<=minH_size){
					;
				}else if(x<minX){
					x = 0;
				}else if((x+xy_OppositeTo[0])>minH_size){
					x = minH_size - xy_OppositeTo[0];
				}

				if(y>=minY && (y+xy_OppositeTo[1])<=minV_size){
					;
				}else if(y<minY){
					y=0;
				}else if((y+xy_OppositeTo[1])>minV_size){
					y=minV_size - xy_OppositeTo[1];
				}
				LogUtils.d("test3","x "+x+"y"+y);
				//y = lcd_h - y;
				lt_X =x;
				lt_Y =y;
				UpdateKeystone();
				SystemProperties.set(PROP_HTC_KEYSTONE_LT_X, String.valueOf(x));
				SystemProperties.set(PROP_HTC_KEYSTONE_LT_Y, String.valueOf(y));
				break;
			case 2:
				xy_OppositeTo = getKeystoneOppositeToLeftAndBottomXY();
				if(x>=minX && (x+xy_OppositeTo[0])<=minH_size){
					;
				}else if(x<minX){
					x=0;
				}else if((x+xy_OppositeTo[0])>minH_size){
					x = minH_size - xy_OppositeTo[0];
				}

				if(y>=minY && (y+xy_OppositeTo[1])<=minV_size){
					;
				}else if(y<minY){
					y=0;
				}else if((y+xy_OppositeTo[1])>minV_size){
					y=minV_size - xy_OppositeTo[1];
				}
				lb_X=x;
				lb_Y=y;
				UpdateKeystone();
				SystemProperties.set(PROP_HTC_KEYSTONE_LB_X, String.valueOf(x));
				SystemProperties.set(PROP_HTC_KEYSTONE_LB_Y, String.valueOf(y));
				break;
			case 3:
				xy_OppositeTo = getKeystoneOppositeToRightAndTopXY();
				if(x>=minX && (x+xy_OppositeTo[0])<=minH_size){
					;
				}else if(x<minX){
					x=0;
				}else if((x+xy_OppositeTo[0])>minH_size){
					x = minH_size - xy_OppositeTo[0];
				}
				//x = lcd_w - x;
				if(y>=minY && (y+xy_OppositeTo[1])<=minV_size){
					;
				}else if(y<minY){
					y=0;
				}else if((y+xy_OppositeTo[1])>minV_size){
					y=minV_size - xy_OppositeTo[1];
				}
				//y = lcd_h - y;
				rt_X =x;
				rt_Y =y;
				UpdateKeystone();
				SystemProperties.set(PROP_HTC_KEYSTONE_RT_X, String.valueOf(x));
				SystemProperties.set(PROP_HTC_KEYSTONE_RT_Y, String.valueOf(y));
				break;
			case 4:
				xy_OppositeTo = getKeystoneOppositeToRightAndBottomXY();
				if(x>=minX && (x+xy_OppositeTo[0])<=minH_size){
					;
				}else if(x<minX){
					x=0;
				}else if((x+xy_OppositeTo[0])>minH_size){
					x = minH_size - xy_OppositeTo[0];
				}
				//x = lcd_w - x;
				if(y>=minY && (y+xy_OppositeTo[1])<=minV_size){
					;
				}else if(y<minY){
					y=0;
				}else if((y+xy_OppositeTo[1])>minV_size){
					y=minV_size - xy_OppositeTo[1];
				}
				rb_X =x;
				rb_Y =y;
				UpdateKeystone();
				SystemProperties.set(PROP_HTC_KEYSTONE_RB_X, String.valueOf(x));
				SystemProperties.set(PROP_HTC_KEYSTONE_RB_Y, String.valueOf(y));
				break;
		}
	}

	public static void resetKeystone(){
		lt_X = 0;
		lt_Y = 0;
		rt_X = 0;
		rt_Y = 0;
		rb_X = 0;
		rb_Y = 0;
		lb_X = 0;
		lb_Y = 0;
		UpdateKeystone();
		SystemProperties.set(PROP_HTC_KEYSTONE_LT_X, "0");
		SystemProperties.set(PROP_HTC_KEYSTONE_LT_Y, "0");
		SystemProperties.set(PROP_HTC_KEYSTONE_RT_X, "0");
		SystemProperties.set(PROP_HTC_KEYSTONE_RT_Y, "0");
		SystemProperties.set(PROP_HTC_KEYSTONE_LB_X, "0");
		SystemProperties.set(PROP_HTC_KEYSTONE_LB_Y, "0");
		SystemProperties.set(PROP_HTC_KEYSTONE_RB_X, "0");
		SystemProperties.set(PROP_HTC_KEYSTONE_RB_Y, "0");
		SystemProperties.set("persist.sys.zoom.value","0,0,0,0,0,0,0,0");

	}

	private static int CoverX(String prop){

		return SystemProperties.getInt(prop,0);
	}

	private static int CoverY(String prop){
		return SystemProperties.getInt(prop,0);
	}

	private static IBinder flinger;

	public static void UpdateKeystone(){
		LogUtils.d("UpdateKeystone","rb_X "+ rb_X+"rb_Y "+rb_Y);
		try {
			if (flinger==null)
			 flinger = ServiceManager.getService("SurfaceFlinger");

			if (flinger != null) {
				Parcel data = Parcel.obtain();
				data.writeInterfaceToken("android.ui.ISurfaceComposer");

				data.writeFloat((float)((double) lb_X *0.001));
				data.writeFloat((float)((double) lb_Y *0.001));
				data.writeFloat((float)((double) lt_X *0.001));
				data.writeFloat((float)((double) lt_Y *0.001));
				data.writeFloat((float)((double) rt_X *0.001));
				data.writeFloat((float)((double) rt_Y *0.001));
				data.writeFloat((float)((double) rb_X *0.001));
				data.writeFloat((float)((double) rb_Y *0.001));
				flinger.transact(1050, data, null, 0);
				data.recycle();
			} else {
				LogUtils.i("tag","error get surfaceflinger service");
			}
		} catch (RemoteException ex) {
			LogUtils.i("tag","error talk with surfaceflinger service");
		}
	}

	public static void UpdateKeystoneZOOM(boolean write){
		LogUtils.d("UpdateKeystone","rb_X "+ rb_X+"rb_Y "+rb_Y);
		if (!write){
			SystemProperties.set("persist.sys.zoom.value",lb_X+","+lb_Y+","+lt_X+","+lt_Y+","+rt_X+","+rt_Y+","+rb_X+","+rb_Y);
			return;
		}
		try {
			if (flinger==null)
			 flinger = ServiceManager.getService("SurfaceFlinger");

			if (flinger != null) {
				Parcel data = Parcel.obtain();
				data.writeInterfaceToken("android.ui.ISurfaceComposer");

				data.writeFloat((float)((double) lb_X *0.001));
				data.writeFloat((float)((double) lb_Y *0.001));
				data.writeFloat((float)((double) lt_X *0.001));
				data.writeFloat((float)((double) lt_Y*0.001));
				data.writeFloat((float)((double) rt_X*0.001));
				data.writeFloat((float)((double) rt_Y*0.001));
				data.writeFloat((float)((double) rb_X*0.001));
				data.writeFloat((float)((double) rb_Y*0.001));
				flinger.transact(1050, data, null, 0);
				data.recycle();
				SystemProperties.set("persist.sys.zoom.value",lb_X+","+lb_Y+","+lt_X+","+lt_Y+","+rt_X+","+rt_Y+","+rb_X+","+rb_Y);
				data.recycle();

			} else {
				LogUtils.i("tag","error get surfaceflinger service");
			}
		} catch (RemoteException ex) {
			LogUtils.i("tag","error talk with surfaceflinger service");
		}
	}

	public static void setKeystoneNormalXY(int cur_mode, int new_mode) {
		int lt_Nx,lt_Ny,rt_Nx,rt_Ny;
		int lb_Nx,lb_Ny,rb_Nx,rb_Ny;
		int lt_x,lt_y,rt_x,rt_y;
		int lb_x,lb_y,rb_x,rb_y;
		int[] lt_xy = getKeystoneLeftAndTopXY();
		int[] rt_xy = getKeystoneRightAndTopXY();
		int[] lb_xy = getKeystoneLeftAndBottomXY();
		int[] rb_xy = getKeystoneRightAndBottomXY();
		if(cur_mode==1){//LR
			rt_Nx = lt_xy[0];
			rt_Ny = lt_xy[1];
			lt_Nx = rt_xy[0];
			lt_Ny = rt_xy[1];
			rb_Nx = lb_xy[0];
			rb_Ny = lb_xy[1];
			lb_Nx = rb_xy[0];
			lb_Ny = rb_xy[1];
		}else if(cur_mode==2){//LRUD
			rb_Nx = lt_xy[0];
			rb_Ny = lt_xy[1];
			lb_Nx = rt_xy[0];
			lb_Ny = rt_xy[1];
			rt_Nx = lb_xy[0];
			rt_Ny = lb_xy[1];
			lt_Nx = rb_xy[0];
			lt_Ny = rb_xy[1];
		}else if(cur_mode==3){//UD
			lb_Nx = lt_xy[0];
			lb_Ny = lt_xy[1];
			rb_Nx = rt_xy[0];
			rb_Ny = rt_xy[1];
			lt_Nx = lb_xy[0];
			lt_Ny = lb_xy[1];
			rt_Nx = rb_xy[0];
			rt_Ny = rb_xy[1];
		}else{//nor
			lt_Nx = lt_xy[0];
			lt_Ny = lt_xy[1];
			rt_Nx = rt_xy[0];
			rt_Ny = rt_xy[1];
			lb_Nx = lb_xy[0];
			lb_Ny = lb_xy[1];
			rb_Nx = rb_xy[0];
			rb_Ny = rb_xy[1];
		}

		if(new_mode==1){//LR
			lt_x=rt_Nx;
			lt_y=rt_Ny;
			rt_x=lt_Nx;
			rt_y=lt_Ny;
			lb_x=rb_Nx;
			lb_y=rb_Ny;
			rb_x=lb_Nx;
			rb_y=lb_Ny;
		}else if(new_mode==2){//LRUD
			lt_x=rb_Nx;
			lt_y=rb_Ny;
			rt_x=lb_Nx;
			rt_y=lb_Ny;
			lb_x=rt_Nx;
			lb_y=rt_Ny;
			rb_x=lt_Nx;
			rb_y=lt_Ny;
		}else if(new_mode==3){//UD
			lt_x=lb_Nx;
			lt_y=lb_Ny;
			rt_x=rb_Nx;
			rt_y=rb_Ny;
			lb_x=lt_Nx;
			lb_y=lt_Ny;
			rb_x=rt_Nx;
			rb_y=rt_Ny;
		}else{ //
			lt_x=lt_Nx;
			lt_y=lt_Ny;
			rt_x=rt_Nx;
			rt_y=rt_Ny;
			lb_x=lb_Nx;
			lb_y=lb_Ny;
			rb_x=rb_Nx;
			rb_y=rb_Ny;
		}
		lt_X = lt_x;
		lt_Y = lt_y;
		rt_X = rt_x;
		rt_Y = rt_y;
		rb_X = rb_x;
		rb_Y = rb_y;
		lb_X = lb_x;
		lb_Y = lb_y;
		UpdateKeystoneZOOM(true);
		lt_xy = getKeystoneHtcLeftAndTopXY();
		rt_xy = getKeystoneHtcRightAndTopXY();
		lb_xy = getKeystoneHtcLeftAndBottomXY();
		rb_xy = getKeystoneHtcRightAndBottomXY();
		if(cur_mode==1){//LR
			rt_Nx = lt_xy[0];
			rt_Ny = lt_xy[1];
			lt_Nx = rt_xy[0];
			lt_Ny = rt_xy[1];
			rb_Nx = lb_xy[0];
			rb_Ny = lb_xy[1];
			lb_Nx = rb_xy[0];
			lb_Ny = rb_xy[1];
		}else if(cur_mode==2){//LRUD
			rb_Nx = lt_xy[0];
			rb_Ny = lt_xy[1];
			lb_Nx = rt_xy[0];
			lb_Ny = rt_xy[1];
			rt_Nx = lb_xy[0];
			rt_Ny = lb_xy[1];
			lt_Nx = rb_xy[0];
			lt_Ny = rb_xy[1];
		}else if(cur_mode==3){//UD
			lb_Nx = lt_xy[0];
			lb_Ny = lt_xy[1];
			rb_Nx = rt_xy[0];
			rb_Ny = rt_xy[1];
			lt_Nx = lb_xy[0];
			lt_Ny = lb_xy[1];
			rt_Nx = rb_xy[0];
			rt_Ny = rb_xy[1];
		}else{//nor
			lt_Nx = lt_xy[0];
			lt_Ny = lt_xy[1];
			rt_Nx = rt_xy[0];
			rt_Ny = rt_xy[1];
			lb_Nx = lb_xy[0];
			lb_Ny = lb_xy[1];
			rb_Nx = rb_xy[0];
			rb_Ny = rb_xy[1];
		}

		if(new_mode==1){//LR
			lt_x=rt_Nx;
			lt_y=rt_Ny;
			rt_x=lt_Nx;
			rt_y=lt_Ny;
			lb_x=rb_Nx;
			lb_y=rb_Ny;
			rb_x=lb_Nx;
			rb_y=lb_Ny;
		}else if(new_mode==2){//LRUD
			lt_x=rb_Nx;
			lt_y=rb_Ny;
			rt_x=lb_Nx;
			rt_y=lb_Ny;
			lb_x=rt_Nx;
			lb_y=rt_Ny;
			rb_x=lt_Nx;
			rb_y=lt_Ny;
		}else if(new_mode==3){//UD
			lt_x=lb_Nx;
			lt_y=lb_Ny;
			rt_x=rb_Nx;
			rt_y=rb_Ny;
			lb_x=lt_Nx;
			lb_y=lt_Ny;
			rb_x=rt_Nx;
			rb_y=rt_Ny;
		}else{ //
			lt_x=lt_Nx;
			lt_y=lt_Ny;
			rt_x=rt_Nx;
			rt_y=rt_Ny;
			lb_x=lb_Nx;
			lb_y=lb_Ny;
			rb_x=rb_Nx;
			rb_y=rb_Ny;
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

	public static void optKeystoneFun(int[] tpData) {
		DecimalFormat df = new DecimalFormat("0");//格式化小数
		lt_X=Integer.parseInt(df.format((tpData[0] * 1000)/lcd_w));
		lt_Y=Integer.parseInt(df.format((tpData[1] * 1000)/lcd_h));
		rt_X=Integer.parseInt(df.format((tpData[2] * 1000)/lcd_w));
		rt_Y=Integer.parseInt(df.format((tpData[3] * 1000)/lcd_h));
		lb_X=Integer.parseInt(df.format((tpData[4] * 1000)/lcd_w));
		lb_Y=Integer.parseInt(df.format((tpData[5] * 1000)/lcd_h));
		rb_X=Integer.parseInt(df.format((tpData[6] * 1000)/lcd_w));
		rb_Y=Integer.parseInt(df.format((tpData[7] * 1000)/lcd_h));
		UpdateKeystoneZOOM(true);
	}
	/*
	key:zoom_value : 全局缩放的值 (进行四角矫正的时候需要重置为0)
		zoom_scale ：比例模式  0=16：9  2=4：3  1=16：10
	 */

//	public static void resetKeystone(){
//		lt_X = 0;
//		lt_Y = 0;
//		rt_X = 0;
//		rt_Y = 0;
//		rb_X = 0;
//		rb_Y = 0;
//		lb_X = 0;
//		lb_Y = 0;
//		UpdateKeystone();
//	}

	public static void writeGlobalSettings(Context context, String key, int value){
		Settings.Global.putInt(context.getContentResolver(),key,value);
	}

	public static int readGlobalSettings(Context context,String key,int def){
		return   Settings.Global.getInt(context.getContentResolver(),key,def);
	}

	public static void writeSystemProperties(String key, int value) {
//        Settings.Global.putInt(context.getContentResolver(), key, value);
		SystemProperties.set(key,String.valueOf(value));
	}

	public static int readSystemProperties(String key, int def) {
		return SystemProperties.getInt(key,def);
	}

}
