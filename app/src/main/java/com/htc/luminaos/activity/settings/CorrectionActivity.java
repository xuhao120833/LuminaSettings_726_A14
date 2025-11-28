package com.htc.luminaos.activity.settings;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htc.luminaos.R;
import com.htc.luminaos.activity.BaseActivity;
import com.htc.luminaos.databinding.ResetKeystoreLayoutBinding;
import com.htc.luminaos.utils.KeystoneUtils;
import com.htc.luminaos.utils.KeystoneUtils_726;
import com.htc.luminaos.utils.LogUtils;

import androidx.annotation.Nullable;

import java.util.Locale;

public class CorrectionActivity extends BaseActivity {

    private CheckBox check_lt;
    private CheckBox check_lb;
    private CheckBox check_rt;
    private CheckBox check_rb;

    private TextView textv_lt;
    private TextView textv_lb;
    private TextView textv_rt;
    private TextView textv_rb;

    private TextView direction_value_x;
    private TextView direction_value_y;
    private ImageView direction_x;
    private ImageView direction_y;

    private View lt_top;
    private View lt_left;
    private View lt_bottom;
    private View lt_right;

    private View lb_top;
    private View lb_left;
    private View lb_bottom;
    private View lb_right;

    private View rt_top;
    private View rt_left;
    private View rt_bottom;
    private View rt_right;

    private View rb_top;
    private View rb_left;
    private View rb_bottom;
    private View rb_right;

    private TextView select_txt;
    private TextView correct_txt;
    private TextView Menu_reset_txt;
    private TextView exit_txt;

    private RelativeLayout rl_main;

    private KeyEvent mkeyEvent;
    private boolean isACTION_DOWN = false;
    private int key_move_step = 1;
    private int touch_move_step = 1;

    private String TAG = "CorrectionActivity";
    Handler mHandler = new Handler();

    private boolean rtl = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_correction_activity);
        rtl = isRTL();
        initFindViewById();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (select_txt != null) { //字体过长则滚动显示
            select_txt.setSelected(true);
            correct_txt.setSelected(true);
            Menu_reset_txt.setSelected(true);
            exit_txt.setSelected(true);
        }
    }

    public void initFindViewById() {

        rl_main = (RelativeLayout) findViewById(R.id.rl_main);

        check_lt = (CheckBox) findViewById(R.id.check_lt);
        check_lb = (CheckBox) findViewById(R.id.check_lb);
        check_rt = (CheckBox) findViewById(R.id.check_rt);
        check_rb = (CheckBox) findViewById(R.id.check_rb);
        textv_lt = (TextView) findViewById(R.id.textv_lt);
        textv_lb = (TextView) findViewById(R.id.textv_lb);
        textv_rt = (TextView) findViewById(R.id.textv_rt);
        textv_rb = (TextView) findViewById(R.id.textv_rb);

        direction_x = findViewById(R.id.direction_x);
        direction_y = findViewById(R.id.direction_y);
        direction_value_x = findViewById(R.id.direction_value_x);
        direction_value_y = findViewById(R.id.direction_value_y);

        lt_top = findViewById(R.id.lt_top);
        lt_left = findViewById(R.id.lt_left);
        lt_right = findViewById(R.id.lt_right);
        lt_bottom = findViewById(R.id.lt_bottom);

        lb_top = findViewById(R.id.lb_top);
        lb_left = findViewById(R.id.lb_left);
        lb_right = findViewById(R.id.lb_right);
        lb_bottom = findViewById(R.id.lb_bottom);

        rt_top = findViewById(R.id.rt_top);
        rt_left = findViewById(R.id.rt_left);
        rt_right = findViewById(R.id.rt_right);
        rt_bottom = findViewById(R.id.rt_bottom);

        rb_top = findViewById(R.id.rb_top);
        rb_left = findViewById(R.id.rb_left);
        rb_right = findViewById(R.id.rb_right);
        rb_bottom = findViewById(R.id.rb_bottom);

        select_txt = findViewById(R.id.select_point_txt);
        correct_txt = findViewById(R.id.correct_txt);
        Menu_reset_txt = findViewById(R.id.Menu_reset_txt);
        exit_txt = findViewById(R.id.exit_txt);

        select_txt.setSelected(true);
        correct_txt.setSelected(true);
        Menu_reset_txt.setSelected(true);
        exit_txt.setSelected(true);

        check_lt.setChecked(true);
        check_lb.setChecked(false);
        check_rt.setChecked(false);
        check_rb.setChecked(false);

        check_lt.setOnClickListener(Listener);
        check_lb.setOnClickListener(Listener);
        check_rt.setOnClickListener(Listener);
        check_rb.setOnClickListener(Listener);

        lt_top.setOnClickListener(valueListener);
        lt_left.setOnClickListener(valueListener);
        lt_right.setOnClickListener(valueListener);
        lt_bottom.setOnClickListener(valueListener);

        lb_top.setOnClickListener(valueListener);
        lb_left.setOnClickListener(valueListener);
        lb_right.setOnClickListener(valueListener);
        lb_bottom.setOnClickListener(valueListener);

        rt_top.setOnClickListener(valueListener);
        rt_left.setOnClickListener(valueListener);
        rt_right.setOnClickListener(valueListener);
        rt_bottom.setOnClickListener(valueListener);

        rb_top.setOnClickListener(valueListener);
        rb_left.setOnClickListener(valueListener);
        rb_right.setOnClickListener(valueListener);
        rb_bottom.setOnClickListener(valueListener);

        check_lt.setOnHoverListener(this);
        check_lb.setOnHoverListener(this);
        check_rt.setOnHoverListener(this);
        check_rb.setOnHoverListener(this);

        KeystoneUtils_726.initKeystoneData();
        int[] xy = new int[]{0, 0};
        xy = KeystoneUtils_726.getKeystoneLeftAndTopXY();
        textv_lt.setText(xy[0] + "," + xy[1]);

    }


    public void initData() {
        refreshStateValueUI();
    }

    private OnClickListener Listener = new OnClickListener() {

        @SuppressWarnings("static-access")
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.check_lt) {
                switchDirection(1);
            } else if (id == R.id.check_lb) {
                switchDirection(2);
            } else if (id == R.id.check_rt) {
                switchDirection(3);
            } else if (id == R.id.check_rb) {
                switchDirection(4);
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        LogUtils.d(TAG, " 手动矫正onKeyDown " + keyCode);
        boolean ret;
        int repeatCount = keyEvent.getRepeatCount();
        if (repeatCount == 0) {
            key_move_step = 1;
        }
        LogUtils.d(TAG, " 手动矫正onKeyDown keyCode " + keyCode + " keyEvent " + keyEvent + " repeatCount " + repeatCount + " key_move_step " + key_move_step);
        ret = calculationValue(keyCode, keyEvent, key_move_step);
        if (key_move_step < 8) {
            key_move_step++;
        }
        return ret;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {

        int action = keyEvent.getAction();
        int keyCode = keyEvent.getKeyCode();

        if ((keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_SETTINGS) && action == KeyEvent.ACTION_DOWN) {
            LogUtils.d(TAG, "dispatchKeyEvent MENU键或者Settings键唤出矫正复位 " + keyCode);
            menu_press(keyEvent);
            return true;
        }

        if (action == KeyEvent.ACTION_DOWN) {
            L("--dispatchKeyEvent keyCode-->" + keyCode);
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                    || keyCode == KeyEvent.KEYCODE_ENTER) {
                // 切换方向
                if (!isACTION_DOWN) {
                    switchDirection(-1);
                    isACTION_DOWN = true;
                }

                return true;
            }
        } else if (action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                    || keyCode == KeyEvent.KEYCODE_ENTER) {
                if (isACTION_DOWN) {
                    isACTION_DOWN = false;
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(keyEvent);
    }


    private void L(String l) {
        LogUtils.i("CorrectionActivity", l);
    }

    /**
     * 切换方向
     */
    private void switchDirection(int value) {
        if (check_lt != null && check_lb != null && check_rt != null
                && check_rb != null) {
            if (value > 0) {
                switch (value) {
                    case 1:
                        check_lt.setChecked(true);
                        check_lb.setChecked(false);
                        check_rt.setChecked(false);
                        check_rb.setChecked(false);
                        break;

                    case 2:
                        check_lt.setChecked(false);
                        check_lb.setChecked(true);
                        check_rt.setChecked(false);
                        check_rb.setChecked(false);
                        break;

                    case 3:
                        check_lt.setChecked(false);
                        check_lb.setChecked(false);
                        check_rt.setChecked(true);
                        check_rb.setChecked(false);
                        break;

                    case 4:
                        check_lt.setChecked(false);
                        check_lb.setChecked(false);
                        check_rt.setChecked(false);
                        check_rb.setChecked(true);
                        break;
                }
            } else {
                if (check_lt.isChecked()) {
                    check_lt.setChecked(false);
                    check_lb.setChecked(false);
                    check_rt.setChecked(true);
                    check_rb.setChecked(false);
                } else if (check_lb.isChecked()) {
                    check_lt.setChecked(true);
                    check_lb.setChecked(false);
                    check_rt.setChecked(false);
                    check_rb.setChecked(false);
                } else if (check_rt.isChecked()) {
                    check_lt.setChecked(false);
                    check_lb.setChecked(false);
                    check_rt.setChecked(false);
                    check_rb.setChecked(true);
                } else if (check_rb.isChecked()) {
                    check_lt.setChecked(false);
                    check_lb.setChecked(true);
                    check_rt.setChecked(false);
                    check_rb.setChecked(false);
                }
            }

            refreshStateValueUI();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private boolean calculationValue(int keyCode, KeyEvent keyEvent, int step) {
        int[] xy = new int[]{0, 0};
        int type = 1;
        if (rtl) {
            if (check_lt != null && check_lb != null && check_rt != null
                    && check_rb != null) {
                if (check_lt.isChecked()) {
                    type = 1;
                    xy = KeystoneUtils_726.getKeystoneRightAndTopXY();
                    Log.d("test3", "xy[0] " + xy[0] + "xy[1]" + xy[1]);
                } else if (check_lb.isChecked()) {
                    type = 2;
                    xy = KeystoneUtils_726.getKeystoneRightAndBottomXY();
                } else if (check_rt.isChecked()) {
                    type = 3;
                    xy = KeystoneUtils_726.getKeystoneLeftAndTopXY();
                } else if (check_rb.isChecked()) {
                    type = 4;
                    xy = KeystoneUtils_726.getKeystoneLeftAndBottomXY();
                }
            }
            if (type == 3) {
                Log.d("keyCode", "" + keyCode);
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    int x = xy[0] - step;
                    xy[0] = x;
                    Log.d("test3", "xy[0] " + xy[0] + "xy[1]" + xy[1]);
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    int x = xy[0] + step;
                    xy[0] = x;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    int y = xy[1] - step;
                    xy[1] = y;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    int y = xy[1] + step;
                    xy[1] = y;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                }
            } else if (type == 4) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    int x = xy[0] - step;
                    xy[0] = x;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    int x = xy[0] + step;
                    xy[0] = x;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    int y = xy[1] + step;
                    xy[1] = y;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    int y = xy[1] - step;
                    xy[1] = y;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                }
            } else if (type == 1) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    int x = xy[0] + step;
                    xy[0] = x;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    int x = xy[0] - step;
                    xy[0] = x;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    int y = xy[1] - step;
                    xy[1] = y;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    int y = xy[1] + step;
                    xy[1] = y;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                }
            } else if (type == 2) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    int x = xy[0] + step;
                    xy[0] = x;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    int x = xy[0] - step;
                    xy[0] = x;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    int y = xy[1] + step;
                    xy[1] = y;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    int y = xy[1] - step;
                    xy[1] = y;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                }
            }

        } else {
            if (check_lt != null && check_lb != null && check_rt != null
                    && check_rb != null) {
                if (check_lt.isChecked()) {
                    type = 1;
                    xy = KeystoneUtils_726.getKeystoneLeftAndTopXY();
                    Log.d("test3", "xy[0] " + xy[0] + "xy[1]" + xy[1]);
                } else if (check_lb.isChecked()) {
                    type = 2;
                    xy = KeystoneUtils_726.getKeystoneLeftAndBottomXY();
                } else if (check_rt.isChecked()) {
                    type = 3;
                    xy = KeystoneUtils_726.getKeystoneRightAndTopXY();
                } else if (check_rb.isChecked()) {
                    type = 4;
                    xy = KeystoneUtils_726.getKeystoneRightAndBottomXY();
                }
            }
            if (type == 1) {
                Log.d("keyCode", "" + keyCode);
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    int x = xy[0] - step;
                    xy[0] = x;
                    Log.d("test3", "xy[0] " + xy[0] + "xy[1]" + xy[1]);
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    int x = xy[0] + step;
                    xy[0] = x;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    int y = xy[1] - step;
                    xy[1] = y;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    int y = xy[1] + step;
                    xy[1] = y;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                }
            } else if (type == 2) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    int x = xy[0] - step;
                    xy[0] = x;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    int x = xy[0] + step;
                    xy[0] = x;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    int y = xy[1] + step;
                    xy[1] = y;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    int y = xy[1] - step;
                    xy[1] = y;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                }
            } else if (type == 3) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    int x = xy[0] + step;
                    xy[0] = x;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    int x = xy[0] - step;
                    xy[0] = x;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    int y = xy[1] - step;
                    xy[1] = y;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    int y = xy[1] + step;
                    xy[1] = y;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                }
            } else if (type == 4) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    int x = xy[0] + step;
                    xy[0] = x;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    int x = xy[0] - step;
                    xy[0] = x;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    int y = xy[1] + step;
                    xy[1] = y;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    int y = xy[1] - step;
                    xy[1] = y;
                    KeystoneUtils_726.setkeystoneValue(type, xy, rtl);
                    refreshState();
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, keyEvent);
    }

    private void refreshState() {
        refreshStateValueUI();
    }

    /**
     * 刷新系统状态以及同步UI状态
     */
    private void refreshStateValueUI() {
        if (check_lt != null && check_lb != null && check_rt != null
                && check_rb != null) {
            int type = -1;
            if (check_lt.isChecked()) {
                type = 1;
                if(rtl)
                    type = 3;
            } else if (check_lb.isChecked()) {
                type = 2;
                if(rtl)
                    type = 4;
            } else if (check_rt.isChecked()) {
                type = 3;
                if(rtl)
                    type = 1;
            } else if (check_rb.isChecked()) {
                type = 4;
                if(rtl)
                    type = 2;
            }
            // 更新
            setLRTB(type);
        }
    }

    private void setLRTB(int type) {
        switch (type) {
            //LT
            case 1:
                direction_x.setBackgroundResource(R.drawable.correction_circle_right);
                direction_y.setBackgroundResource(R.drawable.correction_circle_down);
                direction_value_x.setText(KeystoneUtils_726.lt_X + "");
                direction_value_y.setText(KeystoneUtils_726.lt_Y + "");


                if(rtl) {
                    lt_right.setVisibility(View.GONE);
                    lt_bottom.setVisibility(View.GONE);
                    lt_left.setVisibility(View.GONE);
                    lt_top.setVisibility(View.GONE);
                }else {
                    lt_right.setVisibility(View.VISIBLE);
                    lt_bottom.setVisibility(View.VISIBLE);
                    lt_left.setVisibility(View.GONE);
                    lt_top.setVisibility(View.GONE);
                }

                lb_top.setVisibility(View.GONE);
                lb_left.setVisibility(View.GONE);
                lb_right.setVisibility(View.GONE);
                lb_bottom.setVisibility(View.GONE);


                if(rtl) {
                    rt_top.setVisibility(View.GONE);
                    rt_left.setVisibility(View.VISIBLE);
                    rt_left.setBackgroundResource(R.drawable.correction_arrow_right);
                    rt_right.setVisibility(View.GONE);
                    rt_bottom.setVisibility(View.VISIBLE);
                }else {
                    rt_top.setVisibility(View.GONE);
                    rt_left.setVisibility(View.GONE);
                    rt_right.setVisibility(View.GONE);
                    rt_bottom.setVisibility(View.GONE);
                }

                rb_top.setVisibility(View.GONE);
                rb_left.setVisibility(View.GONE);
                rb_right.setVisibility(View.GONE);
                rb_bottom.setVisibility(View.GONE);

                break;
            //LB
            case 2:
                direction_x.setBackgroundResource(R.drawable.correction_circle_right);
                direction_y.setBackgroundResource(R.drawable.correction_circle_up);
                direction_value_x.setText(KeystoneUtils_726.lb_X + "");
                direction_value_y.setText(KeystoneUtils_726.lb_Y + "");

                lt_top.setVisibility(View.GONE);
                lt_left.setVisibility(View.GONE);
                lt_right.setVisibility(View.GONE);
                lt_bottom.setVisibility(View.GONE);


                if(rtl) {
                    lb_right.setVisibility(View.GONE);
                    lb_top.setVisibility(View.GONE);
                    lb_bottom.setVisibility(View.GONE);
                    lb_left.setVisibility(View.GONE);
                }else {
                    lb_right.setVisibility(View.VISIBLE);
                    lb_top.setVisibility(View.VISIBLE);
                    lb_bottom.setVisibility(View.GONE);
                    lb_left.setVisibility(View.GONE);
                }

                rt_top.setVisibility(View.GONE);
                rt_left.setVisibility(View.GONE);
                rt_right.setVisibility(View.GONE);
                rt_bottom.setVisibility(View.GONE);


                if(rtl) {
                    rb_top.setVisibility(View.VISIBLE);
                    rb_left.setVisibility(View.VISIBLE);
                    rb_left.setBackgroundResource(R.drawable.correction_arrow_right);
                    rb_right.setVisibility(View.GONE);
                    rb_bottom.setVisibility(View.GONE);
                }else {
                    rb_top.setVisibility(View.GONE);
                    rb_left.setVisibility(View.GONE);
                    rb_right.setVisibility(View.GONE);
                    rb_bottom.setVisibility(View.GONE);
                }
                break;
            //RT
            case 3:
                direction_x.setBackgroundResource(R.drawable.correction_circle_left);
                direction_y.setBackgroundResource(R.drawable.correction_circle_down);
                direction_value_x.setText(KeystoneUtils_726.rt_X + "");
                direction_value_y.setText(KeystoneUtils_726.rt_Y + "");

                if(rtl) {
                    lt_top.setVisibility(View.GONE);
                    lt_left.setVisibility(View.GONE);
                    lt_right.setVisibility(View.VISIBLE);
                    lt_right.setBackgroundResource(R.drawable.correction_arrow_left);
                    lt_bottom.setVisibility(View.VISIBLE);
                } else {
                    lt_top.setVisibility(View.GONE);
                    lt_left.setVisibility(View.GONE);
                    lt_right.setVisibility(View.GONE);
                    lt_bottom.setVisibility(View.GONE);
                }

                lb_top.setVisibility(View.GONE);
                lb_left.setVisibility(View.GONE);
                lb_right.setVisibility(View.GONE);
                lb_bottom.setVisibility(View.GONE);

                if(rtl) {
                    rt_left.setVisibility(View.GONE);
                    rt_bottom.setVisibility(View.GONE);
                    rt_right.setVisibility(View.GONE);
                    rb_top.setVisibility(View.GONE);
                } else {
                    rt_left.setVisibility(View.VISIBLE);
                    rt_bottom.setVisibility(View.VISIBLE);
                    rt_right.setVisibility(View.GONE);
                    rb_top.setVisibility(View.GONE);
                }

                rb_top.setVisibility(View.GONE);
                rb_left.setVisibility(View.GONE);
                rb_right.setVisibility(View.GONE);
                rb_bottom.setVisibility(View.GONE);
                break;
            //RB
            case 4:
                direction_x.setBackgroundResource(R.drawable.correction_circle_left);
                direction_y.setBackgroundResource(R.drawable.correction_circle_up);
                direction_value_x.setText(KeystoneUtils_726.rb_X + "");
                direction_value_y.setText(KeystoneUtils_726.rb_Y + "");

                lt_top.setVisibility(View.GONE);
                lt_left.setVisibility(View.GONE);
                lt_right.setVisibility(View.GONE);
                lt_bottom.setVisibility(View.GONE);

                if(rtl) {
                    lb_top.setVisibility(View.VISIBLE);
                    lb_left.setVisibility(View.GONE);
                    lb_right.setVisibility(View.VISIBLE);
                    lb_right.setBackgroundResource(R.drawable.correction_arrow_left);
                    lb_bottom.setVisibility(View.GONE);
                } else {
                    lb_top.setVisibility(View.GONE);
                    lb_left.setVisibility(View.GONE);
                    lb_right.setVisibility(View.GONE);
                    lb_bottom.setVisibility(View.GONE);
                }

                rt_top.setVisibility(View.GONE);
                rt_left.setVisibility(View.GONE);
                rt_right.setVisibility(View.GONE);
                rt_bottom.setVisibility(View.GONE);

                if(rtl) {
                    rb_left.setVisibility(View.GONE);
                    rb_top.setVisibility(View.GONE);
                    rb_bottom.setVisibility(View.GONE);
                    rb_right.setVisibility(View.GONE);
                } else {
                    rb_left.setVisibility(View.VISIBLE);
                    rb_top.setVisibility(View.VISIBLE);
                    rb_bottom.setVisibility(View.GONE);
                    rb_right.setVisibility(View.GONE);
                }

                break;
        }

    }

    Runnable reset_step_runnable = new Runnable() {
        @Override
        public void run() {
            touch_move_step = 1;
        }
    };

    private OnClickListener valueListener = new OnClickListener() {

        @SuppressWarnings("static-access")
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.lt_top || id == R.id.lb_top || id == R.id.rt_top || id == R.id.rb_top) {
                mHandler.removeCallbacks(reset_step_runnable);
                calculationValue(mkeyEvent.KEYCODE_DPAD_UP, mkeyEvent, touch_move_step);
                if (touch_move_step < 8) touch_move_step++;
                mHandler.postDelayed(reset_step_runnable, 600);
            } else if (id == R.id.lt_left || id == R.id.lb_left || id == R.id.rt_left || id == R.id.rb_left) {
                mHandler.removeCallbacks(reset_step_runnable);
                calculationValue(mkeyEvent.KEYCODE_DPAD_LEFT, mkeyEvent, touch_move_step);
                if (touch_move_step < 8) touch_move_step++;
                mHandler.postDelayed(reset_step_runnable, 600);
            } else if (id == R.id.lt_right || id == R.id.lb_right || id == R.id.rt_right || id == R.id.rb_right) {
                mHandler.removeCallbacks(reset_step_runnable);
                calculationValue(mkeyEvent.KEYCODE_DPAD_RIGHT, mkeyEvent, touch_move_step);
                if (touch_move_step < 8) touch_move_step++;
                mHandler.postDelayed(reset_step_runnable, 600);
            } else if (id == R.id.lt_bottom || id == R.id.lb_bottom || id == R.id.rt_bottom || id == R.id.rb_bottom) {
                mHandler.removeCallbacks(reset_step_runnable);
                calculationValue(mkeyEvent.KEYCODE_DPAD_DOWN, mkeyEvent, touch_move_step);
                if (touch_move_step < 8) touch_move_step++;
                mHandler.postDelayed(reset_step_runnable, 600);
            }
        }
    };

    private void ShowResetKeystoreDialog() {
        ResetKeystoreLayoutBinding resetKeystoreLayoutBinding = ResetKeystoreLayoutBinding.inflate(LayoutInflater.from(this));
        Dialog dialoge = new Dialog(this, R.style.DialogTheme);
        dialoge.setContentView(resetKeystoreLayoutBinding.getRoot());
        Window window = dialoge.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.right_in_right_out_anim);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置dialog在界面中的属性
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            //背景全透明
            window.setDimAmount(0f);
        }
        Display d = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams params = window.getAttributes(); // 获取对话框当前的参数值
        params.width = (int) (d.getWidth() * 0.4); // 宽度设置为屏幕的0.8，根据实际情况调整
        params.height = (int) (d.getHeight() * 0.4);
        //params.x = parent.getWidth();
        window.setGravity(Gravity.CENTER);// 设置对话框位置
        window.setAttributes(params);
        window.setAttributes(params);
        resetKeystoreLayoutBinding.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeystoneUtils_726.resetKeystone();
                KeystoneUtils_726.writeGlobalSettings(getApplicationContext(), KeystoneUtils_726.ZOOM_VALUE, 0);
                KeystoneUtils_726.writeSystemProperties(KeystoneUtils_726.PROP_ZOOM_VALUE, 0);

                KeystoneUtils_726.writeSystemProperties(KeystoneUtils_726.PROP_ZOOM_SCALE, 0);

                SystemProperties.set("persist.sys.keystone_offset", "0");
                SystemProperties.set("persist.sys.keystonefinalAngle", "0");

                dialoge.dismiss();
                direction_value_x.setText("0");
                direction_value_y.setText("0");
            }
        });
        resetKeystoreLayoutBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoge.dismiss();
            }
        });
        dialoge.show();
    }

    private void menu_press(KeyEvent keyEvent) {
        LogUtils.d(TAG, " 手动矫正 menu_long_press");
        try {
            ShowResetKeystoreDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isRTL() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL;
    }


}