package com.htc.luminasettings.settings.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Author:
 * Date:
 * Description:
 */
public class ToastUtil {
    private static Toast mToast;

    public static void showShortToast(Context context,String mString){
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        mToast.setText(mString);
        mToast.show();
    }

    public static void showLongToast(Context context,String mString){
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        mToast.setText(mString);
        mToast.show();
    }


}
