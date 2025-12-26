package com.htc.luminasettings.view;

import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ScrollView;

import com.htc.luminasettings.utils.LogUtils;

public class CustomScrollView  extends ScrollView {
    AudioManager audioManager ;
    private Context mContext;

    private static String TAG = "CustomScrollView";

    public CustomScrollView(Context context) {
        super(context);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mContext = context;
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mContext = context;
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mContext = context;
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mContext = context;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {

        Boolean result = super.dispatchKeyEvent(keyEvent);
        LogUtils.d(TAG," dispatchKeyEvent结果 "+result+" "+getButtonSound());
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && result && getButtonSound()&& keyEvent.getKeyCode()!=KeyEvent.KEYCODE_DPAD_CENTER)
        {
            audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
        }
        return result;
    }


    private boolean getButtonSound(){
        return Settings.System.getInt(mContext.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0)==1;
    }

}
