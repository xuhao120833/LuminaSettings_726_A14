package com.htc.luminasettings.utils;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Author:
 * Date:
 * Description:
 */
public class InputMethodUtil {

    private InputMethodUtil inputMethodUtil = null;

    public InputMethodUtil getInstance(){
        if (inputMethodUtil==null)
            inputMethodUtil=new InputMethodUtil();

        return inputMethodUtil;
    }

    public static  void openInputMethod(Context context, View view, Handler handler){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(view,InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        },200);
    }

}
