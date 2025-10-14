package com.htc.luminaos.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.TimeZoneNames;
import android.icu.util.ULocale;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.htc.luminaos.R;
import com.htc.luminaos.databinding.ActivityDateTimeBinding;
import com.htc.luminaos.receiver.MyTimeCallBack;
import com.htc.luminaos.receiver.MyTimeReceiver;
import com.htc.luminaos.utils.Contants;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.TimeUtils;
import com.htc.luminaos.utils.ToastUtil;
import com.htc.luminaos.utils.Utils;
import com.htc.luminaos.widget.TimezoneDialog;

import org.xmlpull.v1.XmlPullParserException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeActivity extends BaseActivity implements View.OnKeyListener {

    private ActivityDateTimeBinding dateTimeBinding;
    private NumberPicker np_year, np_month, np_day, np_hour, np_minute;
    private int maxDay;

    long cur_time = 0;
    private IntentFilter timeFilter = null;
    private MyTimeReceiver timeReceiver = null;
    boolean is24HourFormat = true;
    private static String TAG = "DateTimeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateTimeBinding = ActivityDateTimeBinding.inflate(LayoutInflater.from(this));
        setContentView(dateTimeBinding.getRoot());
        initView();
        initData();
        initReceiver();

    }

    private void initView() {
        dateTimeBinding.rlAuto.setOnClickListener(this);
        dateTimeBinding.autoSwitch.setOnClickListener(this);
        dateTimeBinding.rlDate.setOnClickListener(this);
        dateTimeBinding.rlTime.setOnClickListener(this);
        dateTimeBinding.rlTimezone.setOnClickListener(this);
        dateTimeBinding.rlTimeFormat.setOnClickListener(this);

        dateTimeBinding.rlAuto.setOnHoverListener(this);
        dateTimeBinding.autoSwitch.setOnHoverListener(this);
        dateTimeBinding.rlDate.setOnHoverListener(this);
        dateTimeBinding.rlTime.setOnHoverListener(this);
        dateTimeBinding.rlTimezone.setOnHoverListener(this);
        dateTimeBinding.rlTimeFormat.setOnHoverListener(this);

        dateTimeBinding.rlTimeFormat.setOnKeyListener(this);
        dateTimeBinding.timeZoneTv.setSelected(true);
    }

    private void initData() {
        dateTimeBinding.autoSwitch.setChecked(getAutoTime());
        dateTimeBinding.timeZoneTv.setText(getTimeZoneText());
        dateTimeBinding.dateTv.setText(TimeUtils.getCurrentDate());
        dateTimeBinding.timeTv.setText(TimeUtils.getCurrentTime(this));
        updateFormat();
    }

    private void initReceiver() {
        timeFilter = new IntentFilter();
        timeReceiver = new MyTimeReceiver(new MyTimeCallBack() {

            @Override
            public void changeTime() {
                initData();
            }
        });
        timeFilter.addAction(Intent.ACTION_TIME_CHANGED);
        timeFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        timeFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
        timeFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        timeFilter.addAction(Intent.ACTION_USER_SWITCHED);
        timeFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver, timeFilter);
    }

    /**
     * 获取当前时区
     *
     * @return
     */
    private String getTimeZoneText() {
        Utils.list = getZones();
        TimeZone tz = Calendar.getInstance().getTimeZone();
        String timeZoneId = tz.getID();
        LogUtils.d(TAG, " getTimeZoneText timeZoneId" + timeZoneId);
        boolean daylight = tz.inDaylightTime(new Date());
        Locale locale = Locale.getDefault();
        // 根据语言环境获取显示名称
//        String displayName = tz.getDisplayName(false, TimeZone.LONG, locale);
//        String displayName = getICUTimeZoneDisplayName(tz.getID(), locale);
        String displayName = searchDisplayName(Utils.list,timeZoneId);
                StringBuilder builder = new StringBuilder();

        builder.append(formatOffset(tz.getRawOffset() + (daylight ? tz.getDSTSavings() : 0)))
                .append(", ")
                .append(displayName);
        return builder.toString();
    }

    private String searchDisplayName(ArrayList<HashMap> list,String timeZoneId) {
        LogUtils.d(TAG," TimeZone.getDefault().getID() "+TimeZone.getDefault().getID());
        for (int i = 0; i < list.size(); i++) {
            HashMap map = list.get(i);
            LogUtils.d(TAG," map.get(Contants.KEY_ID) "+map.get(Contants.KEY_ID));
            if (map.get(Contants.KEY_ID).equals(timeZoneId)) {
                return map.get(Contants.KEY_DISPLAYNAME).toString();
            }
        }
        return null;
    }

    public String getICUTimeZoneDisplayName(String timeZoneId, Locale locale) {
        TimeZoneNames names = TimeZoneNames.getInstance(ULocale.forLocale(locale));
        String displayName = names.getDisplayName(
                timeZoneId,
                TimeZoneNames.NameType.LONG_STANDARD,
                System.currentTimeMillis()
        );
        return displayName != null ? displayName : timeZoneId;
    }

    private char[] formatOffset(int off) {
        off = off / 1000 / 60;

        char[] buf = new char[9];
        buf[0] = 'G';
        buf[1] = 'M';
        buf[2] = 'T';

        if (off < 0) {
            buf[3] = '-';
            off = -off;
        } else {
            buf[3] = '+';
        }
        int hours = off / 60;
        int minutes = off % 60;

        buf[4] = (char) ('0' + hours / 10);
        buf[5] = (char) ('0' + hours % 10);

        buf[6] = ':';

        buf[7] = (char) ('0' + minutes / 10);
        buf[8] = (char) ('0' + minutes % 10);

        return buf;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_auto || id == R.id.auto_switch) {
            dateTimeBinding.autoSwitch.setChecked(!dateTimeBinding.autoSwitch.isChecked());
            setAutoTime(dateTimeBinding.autoSwitch.isChecked());
        } else if (id == R.id.rl_date) {
            if (dateTimeBinding.autoSwitch.isChecked()) {
                ToastUtil.showShortToast(this, getString(R.string.auto_time_hint));
                return;
            }

            showDateDialog();
        } else if (id == R.id.rl_time) {
            if (dateTimeBinding.autoSwitch.isChecked()) {
                ToastUtil.showShortToast(this, getString(R.string.auto_time_hint));
                return;
            }

            showTimeDialog();
        } else if (id == R.id.rl_timezone) {
            TimezoneDialog timezoneDialog = new TimezoneDialog(DateTimeActivity.this, R.style.DialogTheme);
            timezoneDialog.show();
        } else if (id == R.id.rl_time_format) {
            UpdateTimeDispaly(is24HourFormat);
            initData();
        }
    }

    private void updateFormat() {
        if (DateFormat.is24HourFormat(this)) {
            dateTimeBinding.timeFormatTv.setText(getString(R.string.hour24));
            dateTimeBinding.timeFormat.setText(getString(R.string.time_format_24));
            is24HourFormat = true;
        } else {
            dateTimeBinding.timeFormatTv.setText(getString(R.string.hour12));
            dateTimeBinding.timeFormat.setText(getString(R.string.time_format_12));
            is24HourFormat = false;
        }
    }

    private void UpdateTimeDispaly(boolean is24Hour) {
        if (!is24Hour) {
            Settings.System.putString(getContentResolver(), Settings.System.TIME_12_24, "24");
        } else {
            Settings.System.putString(getContentResolver(), Settings.System.TIME_12_24, "12");
        }
    }


    private boolean getAutoTime() {
        return Settings.Global.getInt(getContentResolver(), "auto_time", 0) == 1;
    }

    private void setAutoTime(boolean auto) {
        Settings.Global.putInt(getContentResolver(), "auto_time", auto ? 1 : 0);
    }

    Dialog dialog_date;

    public void showDateDialog() {
        dialog_date = new Dialog(this, R.style.DialogTheme);
        View mView = View.inflate(this, R.layout.date_dialog, null);
        np_year = mView.findViewById(R.id.np_year);
        np_month = mView.findViewById(R.id.np_month);
        np_day = mView.findViewById(R.id.np_day);
        TextView enter = mView.findViewById(R.id.enter);
        TextView cancel = mView.findViewById(R.id.cancel);
        //获取当前日期
        Calendar c = Calendar.getInstance();
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH) + 1;//月份是从0开始算的
        final int day = c.get(Calendar.DAY_OF_MONTH);

        np_year.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np_month.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np_day.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        //设置年份
        np_year.setMaxValue(2999);
        np_year.setValue(year); //中间参数 设置默认值
        np_year.setMinValue(1900);

        //设置月份
        np_month.setMaxValue(12);
        np_month.setValue(month);
        np_month.setMinValue(1);

        //设置天数
        np_day.setMaxValue(31);
        np_day.setValue(day);
        np_day.setMinValue(1);

        //年份滑动监听
        np_year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                LogUtils.i("NumberPicker", "oldVal-----" + oldVal + "-----newVal-----" + newVal);
                //平年闰年判断
                if (newVal % 4 == 0) {
                    maxDay = 29;
                } else {
                    maxDay = 28;
                }
                //设置天数的最大值
                np_day.setMaxValue(maxDay);
            }
        });

        //月份滑动监听
        np_month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                LogUtils.i("NumberPicker", "oldVal-----" + oldVal + "-----newVal-----" + newVal);
                //月份判断
                switch (newVal) {
                    case 2:
                        if (np_year.getValue() % 4 == 0) {
                            maxDay = 29;
                        } else {
                            maxDay = 28;
                        }
                        break;
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                    case 12:
                        maxDay = 31;
                        break;
                    default:
                        maxDay = 30;
                        break;
                }
                //设置天数的最大值
                np_day.setMaxValue(maxDay);
            }
        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int years = np_year.getValue();
                int months = np_month.getValue();
                int days = np_day.getValue();
                Calendar a = Calendar.getInstance();
                a.set(Calendar.YEAR, years);
                a.set(Calendar.MONTH, months - 1);
                a.set(Calendar.DATE, days);
                SystemClock.setCurrentTimeMillis(a.getTimeInMillis());
                dialog_date.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_date.dismiss();
            }
        });
        dialog_date.setContentView(mView);

        Window window = dialog_date.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.right_in_right_out_anim);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置dialog在界面中的属性
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            //背景全透明
            window.setDimAmount(0f);
        }
        WindowManager manager = ((Activity) this).getWindowManager();
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams params = window.getAttributes(); // 获取对话框当前的参数值
        params.width = (int) (d.getWidth() * 0.4); // 宽度设置为屏幕的0.8，根据实际情况调整
        params.height = (int) (d.getHeight() * 0.5);
        //params.x = parent.getWidth();
        window.setGravity(Gravity.CENTER);// 设置对话框位置

        dialog_date.show();
    }

    Dialog dialog_time;

    public void showTimeDialog() {
        dialog_time = new Dialog(this, R.style.DialogTheme);
        View mView = View.inflate(this, R.layout.time_dialog, null);
        np_hour = mView.findViewById(R.id.np_hour);
        np_minute = mView.findViewById(R.id.np_minute);
        TextView enter = mView.findViewById(R.id.enter);
        TextView cancel = mView.findViewById(R.id.cancel);

        //获取当前时间
        Calendar c = Calendar.getInstance();
        final int hour;
        if (DateFormat.is24HourFormat(this)) {
            hour = c.get(Calendar.HOUR_OF_DAY);
        } else {
            hour = c.get(Calendar.HOUR);
        }
        final int minute = c.get(Calendar.MINUTE);

        np_hour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np_minute.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);


        //设置小时
        np_hour.setMaxValue(23);
        np_hour.setValue(hour); //中间参数 设置默认值
        np_hour.setMinValue(0);

        //设置分
        np_minute.setMaxValue(59);
        np_minute.setValue(minute);
        np_minute.setMinValue(0);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = np_hour.getValue();
                int minute = np_minute.getValue();
                Calendar a = Calendar.getInstance();
                if (DateFormat.is24HourFormat(DateTimeActivity.this)) {
                    a.set(Calendar.HOUR_OF_DAY, hour);
                } else {
                    a.set(Calendar.HOUR, hour);
                }
                a.set(Calendar.MINUTE, minute);
                SystemClock.setCurrentTimeMillis(a.getTimeInMillis());
                dialog_time.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_time.dismiss();
            }
        });
        dialog_time.setContentView(mView);
        Window window = dialog_time.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.right_in_right_out_anim);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置dialog在界面中的属性
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            //背景全透明
            window.setDimAmount(0f);
        }

        WindowManager manager = ((Activity) this).getWindowManager();
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams params = window.getAttributes(); // 获取对话框当前的参数值
        params.width = (int) (d.getWidth() * 0.4); // 宽度设置为屏幕的0.8，根据实际情况调整
        params.height = (int) (d.getHeight() * 0.5);
        //params.x = parent.getWidth();
        window.setGravity(Gravity.CENTER);// 设置对话框位置

        dialog_time.show();

    }

    @Override
    protected void onDestroy() {
        if (timeReceiver != null) {
            unregisterReceiver(timeReceiver);
        }
        super.onDestroy();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if ((event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT)
                && (System.currentTimeMillis() - cur_time < 150)) {
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_UP) {
            if (v.getId() == R.id.rl_time_format) {
                UpdateTimeDispaly(is24HourFormat);
                initData();
                AudioManager audioManager = (AudioManager) v.getContext().getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null) {
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                }
                audioManager = null;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_UP) {
            if (v.getId() == R.id.rl_time_format) {
                UpdateTimeDispaly(is24HourFormat);
                initData();
                AudioManager audioManager = (AudioManager) v.getContext().getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null) {
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                }
                audioManager = null;
            }
        }
        return false;
    }

    // parse timezones.xml to get timezone info
    private ArrayList<HashMap> getZones() {
        ArrayList<HashMap> myData = new ArrayList<HashMap>();
        long date = Calendar.getInstance().getTimeInMillis();
        try {
            XmlResourceParser xrp = getResources().getXml(R.xml.timezones);
            while (xrp.next() != XmlResourceParser.START_TAG)
                continue;
            xrp.next();
            while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                while (xrp.getEventType() != XmlResourceParser.START_TAG) {
                    if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
                        return myData;
                    }
                    xrp.next();
                }
                if (xrp.getName().equals("timezone")) {
                    String id = xrp.getAttributeValue(0);
                    String displayName = xrp.nextText();
                    addItem(myData, id, displayName, date);
//                    LogUtils.d(TAG," getZones "+id+" "+displayName);
                }
                while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                    xrp.next();
                }
                xrp.next();
            }
            xrp.close();
        } catch (XmlPullParserException xppe) {
            // LOGD("Ill-formatted timezones.xml file");
        } catch (java.io.IOException ioe) {
            // LOGD("Unable to read timezones.xml file");
        }
        return myData;
    }

    protected void addItem(List<HashMap> myData, String id, String displayName,
                           long date) {
        HashMap map = new HashMap();
        map.put(Contants.KEY_ID, id);
        map.put(Contants.KEY_DISPLAYNAME, displayName);
        TimeZone tz = TimeZone.getTimeZone(id);
        int offset = tz.getOffset(date);
        int p = Math.abs(offset);
        StringBuilder name = new StringBuilder();
        name.append("GMT");
        if (offset < 0) {
            name.append('-');
        } else {
            name.append('+');
        }
        name.append(p / (Contants.HOURS_1));
        name.append(':');
        int min = p / 60000;
        min %= 60;
        if (min < 10) {
            name.append('0');
        }
        name.append(min);
        map.put(Contants.KEY_GMT, name.toString());
        map.put(Contants.KEY_OFFSET, offset);
//        if (id.equals(TimeZone.getDefault().getID())) {
//            LogUtils.d(TAG," addItem id "+id);
//            mDefault = myData.size()-1;
//        }
        myData.add(map);
    }


}