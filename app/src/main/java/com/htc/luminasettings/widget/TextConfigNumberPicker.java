package com.htc.luminasettings.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.htc.luminasettings.R;

import java.lang.reflect.Field;

public class TextConfigNumberPicker extends NumberPicker {

    private static String TAG = "TextConfigNumberPicker";

    public TextConfigNumberPicker(Context context) {
        super(context);
    }

    public TextConfigNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextConfigNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 检查是否是按键按下事件
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            // 检查是否是方向键
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                // 显式播放按键音
                AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null) {
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                }
                audioManager =null;
            }
        }
        // 调用父类处理按键事件
        return super.dispatchKeyEvent(event);
    }

    private void updateView(View view) {
        if (view instanceof EditText) {
            //设置文字的颜色和大小
            ((EditText) view).setTextColor(getResources().getColor(R.color.black));
            ((EditText) view).setTextSize(23);
        }

        try {
            //设置分割线大小颜色
            Field mSelectionDivider = this.getFile("mSelectionDivider");
            mSelectionDivider.set(this, new ColorDrawable(getResources().getColor(R.color.transpant)));
            mSelectionDivider.set(this, 5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //反射获取控件 mSelectionDivider mInputText当前选择的view
    public Field getFile(String fieldName) {
        try {
            //设置分割线的颜色值
            Field pickerFields = NumberPicker.class.getDeclaredField(fieldName);
            pickerFields.setAccessible(true);
            return pickerFields;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setMInputColor(int color) {
        Field mInputText = this.getFile("mInputText");
        try {
            ((EditText) mInputText.get(this)).setTextColor(color);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}