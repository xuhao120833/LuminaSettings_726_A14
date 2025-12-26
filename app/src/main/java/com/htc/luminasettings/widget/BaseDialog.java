package com.htc.luminasettings.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.htc.luminasettings.MyApplication;
import com.htc.luminasettings.R;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Author:
 * Date:
 * Description:
 */
public class BaseDialog extends Dialog implements View.OnHoverListener{
    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onStart() {
        setWallPaper();
        super.onStart();
    }

    public void setWallPaper(){
        if (MyApplication.mainDrawable!=null){
            RelativeLayout relativeLayout = Objects.requireNonNull(getWindow()).getDecorView().findViewById(R.id.rl_main);
            if (relativeLayout!=null)
                relativeLayout.setBackground(MyApplication.mainDrawable);
        }
    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        int what = event.getAction();
        switch (what) {
            case MotionEvent.ACTION_HOVER_ENTER: // 鼠标进入view
                v.requestFocus();
                break;
            case MotionEvent.ACTION_HOVER_MOVE: // 鼠标在view上
                break;
            case MotionEvent.ACTION_HOVER_EXIT: // 鼠标离开view
                break;
        }
        return false;
    }

}
