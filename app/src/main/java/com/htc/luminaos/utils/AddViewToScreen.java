package com.htc.luminaos.utils;

import android.util.Log;
import android.view.View;
import android.app.ActionBar;
import android.content.Context;
import android.view.WindowManager;

public class AddViewToScreen {
    public static WindowManager wm;
    private Context mcontext;
    private static String TAG = "AddViewToScreen";

    public void addView(View v, WindowManager.LayoutParams p) {
        Log.d(TAG, "v.isAttachedToWindow() " + String.valueOf(v.isAttachedToWindow()));
        try {
            v.clearFocus();
            wm.addView(v, p);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setContext(Context context) {
        mcontext = context;
        wm = mcontext.getSystemService(WindowManager.class);
    }

    public void clearView(View v) {
        if (v != null) {
            Log.d(TAG, "clearView");
            try {
                wm.removeViewImmediate(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
