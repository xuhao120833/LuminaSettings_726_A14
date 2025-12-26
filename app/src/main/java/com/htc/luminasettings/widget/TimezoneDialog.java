package com.htc.luminasettings.widget;

import static com.htc.luminasettings.utils.Utils.list;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.htc.luminasettings.R;
import com.htc.luminasettings.adapter.TimezoneAdapter;
import com.htc.luminasettings.databinding.TimeZoneLayoutBinding;
import com.htc.luminasettings.utils.Contants;
import com.htc.luminasettings.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Author:
 * Date:
 * Description:
 */
public class TimezoneDialog extends BaseDialog implements View.OnClickListener {
    private Context mContext;
    TimeZoneLayoutBinding timeZoneLayoutBinding;
    private int mDefault;
//    private ArrayList<HashMap> list = null;
    private static String TAG = "TimezoneDialog";

    @Override
    public void onClick(View v) {

    }

    public TimezoneDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public TimezoneDialog(Context context, boolean cancelable,
                          DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

        this.mContext = context;
    }

    public TimezoneDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onStart() {
        initData();
        super.onStart();
    }

    private void init() {
        timeZoneLayoutBinding = TimeZoneLayoutBinding.inflate(LayoutInflater.from(mContext));
        if (timeZoneLayoutBinding.getRoot() != null) {
            setContentView(timeZoneLayoutBinding.getRoot());
            initView();
            // 设置dialog大小 模块好的控件大小设置
            Window dialogWindow = getWindow();
            if (dialogWindow != null) {
                //去除系统自带的margin
                dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //设置dialog在界面中的属性
                dialogWindow.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                //背景全透明
                dialogWindow.setDimAmount(0f);
            }
            WindowManager manager = ((Activity) mContext).getWindowManager();
            Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
            WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            params.width = d.getWidth();
            params.height = d.getHeight();
            dialogWindow.setAttributes(params);
        }
    }

    private void initView(){
        timeZoneLayoutBinding.timeZoneRv.addItemDecoration(new SpacesItemDecoration(0,0,SpacesItemDecoration.px2dp(4),0));
        timeZoneLayoutBinding.timeZoneRv.setItemAnimator(null);
    }

    private void initData(){
        try {
            MyComparator comparator = new MyComparator(Contants.KEY_OFFSET);
//            list = getZones();
//            getSupportedTimeZones();
            if (list != null && list.size() > 0) {
                Collections.sort(list, comparator);
                searchindex(list);
                TimezoneAdapter timezoneAdapter = new TimezoneAdapter(mContext, list, timeZoneLayoutBinding.timeZoneRv);
                timezoneAdapter.setCurrentPosition(mDefault);
                timezoneAdapter.setHasStableIds(true);
                timeZoneLayoutBinding.timeZoneRv.setAdapter(timezoneAdapter);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void searchindex(ArrayList<HashMap> list){
//        for (int i = 0; i < list.size(); i++) {
//            HashMap map = list.get(i);
////            LogUtils.d(TAG," map.get(Contants.KEY_ID)"+map.get(Contants.KEY_ID)+" "+i+" "+TimeZone.getDefault().getID());
//            if (map.get(Contants.KEY_ID).equals(TimeZone.getDefault().getID())) {
////                LogUtils.d(TAG," map.get(Contants.KEY_ID)"+map.get(Contants.KEY_ID)+" "+i);
//                mDefault = i;
//                return;
//            }
//        }


    private void searchindex(ArrayList<HashMap> list) {
        LogUtils.d(TAG," TimeZone.getDefault().getID() "+TimeZone.getDefault().getID());
        for (int i = 0; i < list.size(); i++) {
            HashMap map = list.get(i);
            LogUtils.d(TAG," map.get(Contants.KEY_ID) "+map.get(Contants.KEY_ID));
            if (map.get(Contants.KEY_ID).equals(TimeZone.getDefault().getID())) {
                mDefault = i;
                LogUtils.d(TAG," map.get(Contants.KEY_ID)"+map.get(Contants.KEY_ID)+" "+i);
                // 将选中的元素移动到列表的第一个位置
                list.remove(i);         // 从原位置移除
                list.add(0, map);       // 添加到列表第一个位置
                mDefault = 0;
                return;
            }
        }
    }


    // parse timezones.xml to get timezone info
//    private ArrayList<HashMap> getZones() {
//        ArrayList<HashMap> myData = new ArrayList<HashMap>();
//        long date = Calendar.getInstance().getTimeInMillis();
//        try {
//            XmlResourceParser xrp = mContext.getResources().getXml(R.xml.timezones);
//            while (xrp.next() != XmlResourceParser.START_TAG)
//                continue;
//            xrp.next();
//            while (xrp.getEventType() != XmlResourceParser.END_TAG) {
//                while (xrp.getEventType() != XmlResourceParser.START_TAG) {
//                    if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
//                        return myData;
//                    }
//                    xrp.next();
//                }
//                if (xrp.getName().equals("timezone")) {
//                    String id = xrp.getAttributeValue(0);
//                    String displayName = xrp.nextText();
//                    addItem(myData, id, displayName, date);
////                    LogUtils.d(TAG," getZones "+id+" "+displayName);
//                }
//                while (xrp.getEventType() != XmlResourceParser.END_TAG) {
//                    xrp.next();
//                }
//                xrp.next();
//            }
//            xrp.close();
//        } catch (XmlPullParserException xppe) {
//            // LOGD("Ill-formatted timezones.xml file");
//        } catch (java.io.IOException ioe) {
//            // LOGD("Unable to read timezones.xml file");
//        }
//
//        return myData;
//    }

//    protected void addItem(List<HashMap> myData, String id, String displayName,
//                           long date) {
//        HashMap map = new HashMap();
//        map.put(Contants.KEY_ID, id);
//        map.put(Contants.KEY_DISPLAYNAME, displayName);
//        TimeZone tz = TimeZone.getTimeZone(id);
//        int offset = tz.getOffset(date);
//        int p = Math.abs(offset);
//        StringBuilder name = new StringBuilder();
//        name.append("GMT");
//
//        if (offset < 0) {
//            name.append('-');
//        } else {
//            name.append('+');
//        }
//
//        name.append(p / (Contants.HOURS_1));
//        name.append(':');
//
//        int min = p / 60000;
//        min %= 60;
//
//        if (min < 10) {
//            name.append('0');
//        }
//        name.append(min);
//
//        map.put(Contants.KEY_GMT, name.toString());
//        map.put(Contants.KEY_OFFSET, offset);
//
////        if (id.equals(TimeZone.getDefault().getID())) {
////            LogUtils.d(TAG," addItem id "+id);
////            mDefault = myData.size()-1;
////        }
//
//        myData.add(map);
//    }

    private static class MyComparator implements Comparator<HashMap> {
        private String mSortingKey;

        public MyComparator(String sortingKey) {
            mSortingKey = sortingKey;
        }

        public void setSortingKey(String sortingKey) {
            mSortingKey = sortingKey;
        }

        public int compare(HashMap map1, HashMap map2) {
            Object value1 = map1.get(mSortingKey);
            Object value2 = map2.get(mSortingKey);

            /*
             * This should never happen, but just in-case, put non-comparable
             * items at the end.
             */
            if (!isComparable(value1)) {
                return isComparable(value2) ? 1 : 0;
            } else if (!isComparable(value2)) {
                return -1;
            }

            return ((Comparable) value1).compareTo(value2);
        }

        private boolean isComparable(Object value) {
            return (value != null) && (value instanceof Comparable);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    /**
     * 获取 Android 系统支持的时区列表
     *
     * @return ArrayList<HashMap<String, String>> 时区列表，每个 HashMap 包含时区 ID 和显示名称
     */
    public static ArrayList<HashMap<String, String>> getSupportedTimeZones() {
        ArrayList<HashMap<String, String>> timeZoneList = new ArrayList<>();

        // 获取所有支持的时区 ID
        String[] timeZoneIds = TimeZone.getAvailableIDs();

        for (String id : timeZoneIds) {
            if (!TextUtils.isEmpty(id)) {
                TimeZone timeZone = TimeZone.getTimeZone(id);

                HashMap<String, String> timeZoneInfo = new HashMap<>();
                timeZoneInfo.put("id", id); // 时区 ID
                timeZoneInfo.put("displayName", timeZone.getDisplayName(false, TimeZone.SHORT, Locale.getDefault())); // 时区显示名称
                timeZoneList.add(timeZoneInfo);
                LogUtils.d(TAG," timeZoneInfo信息 "+id+"  "+timeZoneInfo.get(id));
            }
        }

        return timeZoneList;
    }
}
