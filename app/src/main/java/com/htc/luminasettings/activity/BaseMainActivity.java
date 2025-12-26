package com.htc.luminasettings.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.htc.luminasettings.MyApplication;
import com.htc.luminasettings.R;
import com.htc.luminasettings.utils.PasswordUtils;
import com.htc.luminasettings.utils.Utils;

import androidx.annotation.Nullable;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Author:
 * Date:
 * Description:
 */
public class BaseMainActivity extends Activity implements View.OnClickListener, View.OnHoverListener, View.OnFocusChangeListener {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PasswordUtils.isPasswordSwitchEnabled() && !PasswordUtils.isSessionVerified()) {
            if(!PasswordUtils.hasPasswordBeenSet(getApplicationContext())) {
                startNewActivity(PasswordVerifyActivity.class);
            } else {
                startNewActivity(PasswordVerifyActivity2.class);
            }
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        try {
            setWallPaper();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    public void setWallPaper() {
        if (MyApplication.mainDrawable != null) {
            ViewGroup relativeLayout = findViewById(R.id.rl_main);
            if (relativeLayout != null)
                relativeLayout.setBackground(MyApplication.mainDrawable);
        } else {
            ViewGroup relativeLayout = findViewById(R.id.rl_main);
            if (relativeLayout != null) {
                if (Utils.drawables.get(0) instanceof Drawable) {
                    relativeLayout.setBackground((Drawable) Utils.drawables.get(0));
                } else if (Utils.drawables.get(0) instanceof Integer) {
                    relativeLayout.setBackgroundResource((int) Utils.drawables.get(0));
                } else if (Utils.drawables.get(0) instanceof String) {
                    try {
                        // 从文件加载 Bitmap
                        FileInputStream inputStream = new FileInputStream((String) Utils.drawables.get(0));
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        // 创建 Drawable 对象
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        inputStream.close();
                        relativeLayout.setBackground(drawable);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void setWallPaper(int resId) {
        if (MyApplication.mainDrawable != null) {
            RelativeLayout relativeLayout = findViewById(R.id.rl_main);
            if (relativeLayout != null)
                relativeLayout.setBackground(MyApplication.mainDrawable);
        } else if (resId != -1) {
            RelativeLayout relativeLayout = findViewById(R.id.rl_main);
            if (relativeLayout != null)
                relativeLayout.setBackgroundResource(resId);
        }
    }

    public void setWallPaper(Drawable drawable) {
        RelativeLayout relativeLayout = findViewById(R.id.rl_main);
        if (relativeLayout != null)
            relativeLayout.setBackground(drawable);
    }


    @Override
    public void onClick(View v) {

    }

    public void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void startNewActivity(String packageName, String activity) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activity));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        AnimationSet animationSet = new AnimationSet(true);
        v.bringToFront();
        if (hasFocus) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.10f,
                    1.0f, 1.10f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(150);
            animationSet.addAnimation(scaleAnimation);
            animationSet.setFillAfter(true);
            v.startAnimation(animationSet);
        } else {
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.10f, 1.0f,
                    1.10f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            animationSet.addAnimation(scaleAnimation);
            scaleAnimation.setDuration(150);
            animationSet.setFillAfter(true);
            v.startAnimation(animationSet);
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