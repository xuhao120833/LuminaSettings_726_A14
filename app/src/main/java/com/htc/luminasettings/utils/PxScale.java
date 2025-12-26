package com.htc.luminasettings.utils;

public class PxScale {
	static {
		System.loadLibrary("PxScale");
	}

	/**
	 * A native method that is implemented by the 'duRYXtp' native library,
	 * which is packaged with this application.
	 *
	 */
	public native int[] getpxRatioxy(int[] px4, int[] py4, int oldRatio, int newRatio, float scale, int w, int h);
	public native int checkbddata(String data);
}