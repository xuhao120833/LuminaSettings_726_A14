package com.htc.luminasettings.utils;



public class scUtils {
	public static PxScale lPxScale = new PxScale();

	public static int[] getpxRatioxy(int[] px4, int[] py4, int oldRatio, int newRatio, float scale, int w, int h){
		if(lPxScale!=null) {
			return lPxScale.getpxRatioxy(px4, py4, oldRatio, newRatio, scale, w,h);
		}else{
			return null;
		}
	}

	public static int checkbddata(String data){
		return lPxScale.checkbddata(data);
	}
}
