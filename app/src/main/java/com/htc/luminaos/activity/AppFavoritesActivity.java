package com.htc.luminaos.activity;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.TextView;

import com.htc.luminaos.MyApplication;
import com.htc.luminaos.R;
import com.htc.luminaos.adapter.AppFavoritesAdapter;
import com.htc.luminaos.entry.AppInfoBean;
import com.htc.luminaos.entry.AppSimpleBean;
import com.htc.luminaos.receiver.AppCallBack;
import com.htc.luminaos.receiver.AppReceiver;
import com.htc.luminaos.utils.AppUtils;
import com.htc.luminaos.utils.DBUtils;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.ShareUtil;
import com.htc.luminaos.utils.ToastUtil;
import com.htc.luminaos.utils.Utils;
import com.htc.luminaos.widget.GridViewItemOrderUtil;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;


/**
 * @author Xuhao
 * @version 创建时间 2020/9/8 下午3:50:51
 */
public class AppFavoritesActivity extends BaseActivity implements AppCallBack {

    private String currentPackageName = null;

    private String tag = "AppFavoritesActivity";

    private int selected;

    private ArrayList<AppInfoBean> list = new ArrayList<>();

    private SharedPreferences sp;
    private Editor ed;

    private GridView appfavorites_gridview;
    private AppFavoritesAdapter adapter;
    private TextView select_number_tv;
    private TextView title;

    private IntentFilter appFilter = new IntentFilter();
    private AppReceiver appReceiver = null;
    private final int Handler_update = 10000;
    String resident = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_appfovorites_activity);
        sp = ShareUtil.getInstans(this);
        ed = sp.edit();
        resident = sp.getString("resident", "");
        initView();
        initData();
    }

    public void initView() {
        appfavorites_gridview = findViewById(R.id.appfavorites_gridview);
        select_number_tv = findViewById(R.id.select_number_tv);
        title = findViewById(R.id.title);
    }

    public void initData() {
        initReceiver();
        loadDataApp();
    }

    public void onclick(View view) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        setLayout();
    }

    private void setLayout() {
        if (MyApplication.config.layout_select == 2 || MyApplication.config.layout_select == 3) {
            Typeface typeface = ResourcesCompat.getFont(this, R.font.arial);
            title.setTextColor(Color.BLACK);
            title.setTypeface(typeface, Typeface.BOLD);
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.y_40));
            title.setLetterSpacing(0.05f);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destoryReceiver();
    }

    /**
     * 注销广播
     */
    private void destoryReceiver() {
        if (appReceiver != null) {
            unregisterReceiver(appReceiver);
        }
    }

    private void initReceiver() {
        // app
        appFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        appFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        appFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        appFilter.addDataScheme("package");
        appReceiver = new AppReceiver(this);
        registerReceiver(appReceiver, appFilter);
    }


    private void loadAllApp() {
        adapter = new AppFavoritesAdapter(AppFavoritesActivity.this, list);
        appfavorites_gridview.setAdapter(adapter);
        if (list.size() > 0) {
            select_number_tv.setText((selected + 1) + "/" + list.size());
        } else {
            select_number_tv.setText("0/0");
        }
        appfavorites_gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //特定IP Special
                String specialPackage = "";
                if (Utils.specialApps != null) {
                    specialPackage = Utils.specialApps.getPackageName();
                }
                LogUtils.d(tag," setOnItemClickListener specialPackage " + specialPackage);
//                LogUtils.d(tag, " setOnItemClickListener specialPackage " + specialPackage);
                if (resident.contains(list.get(position).getApppackagename())
                        || list.get(position).getApppackagename().equals(specialPackage)) {
                    ToastUtil.showShortToast(AppFavoritesActivity.this, getString(R.string.resident_app));
                    return;
                }
                list.get(position).setCheck(!list.get(position).isCheck());
                select_number_tv.setText((position + 1) + "/" + list.size());
                int count = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isCheck()) {
                        LogUtils.d(tag," 快捷栏数量 list i " + i);
//                        LogUtils.d(tag, " 快捷栏数量 list i " + i);
                        count += 1;
                    }
                }
                LogUtils.d(tag," 快捷栏数量 count " + count);
//                LogUtils.d(tag, " 快捷栏数量 count " + count);
                LogUtils.d(tag," 快捷栏数量 getFavoritesCount() " + DBUtils.getInstance(getApplicationContext()).getFavoritesCount());
//                LogUtils.d(tag, " 快捷栏数量 getFavoritesCount() " + DBUtils.getInstance(getApplicationContext()).getFavoritesCount());
                //这里得分两种情况，有配置文件和没有配置文件
                int favorites = 0;
                int max = 0;
                File file = new File("/oem/shortcuts.config");
                if (!file.exists()) {
                    file = new File("/system/shortcuts.config");
                }
                if (!file.exists()) {
                    file = new File("/system/others.config");
                }
                if (!file.exists()) {
                    favorites = count;
                    max = 7;
                    LogUtils.d(tag, " 快捷栏数量 !file.exists() ");
                    if (favorites > max) {// count>favorites是新增
                        list.get(position).setCheck(!list.get(position).isCheck());
                        ToastUtil.showShortToast(AppFavoritesActivity.this, getString(R.string.short_max_tips));
                    } else {
                        boolean isCheck = list.get(position).isCheck();
                        if (isCheck) {
                            if (!DBUtils.getInstance(AppFavoritesActivity.this).isExistData(list.get(position).getApppackagename())) {
                                DBUtils.getInstance(AppFavoritesActivity.this)
                                        .addFavorites(list.get(position).getAppname(),
                                                list.get(position).getApppackagename(),
                                                list.get(position).getAppicon());
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            DBUtils.getInstance(AppFavoritesActivity.this)
                                    .deleteFavorites(list.get(position).getApppackagename());
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    favorites = DBUtils.getInstance(getApplicationContext()).getFavoritesCount();
                    if (Utils.specialApps != null) {
                        favorites++;
                    }
                    max = 6;
                    LogUtils.d(tag, " 快捷栏数量 file.exists() " + favorites);
                    if (favorites > max && list.get(position).isCheck()) {
                        list.get(position).setCheck(!list.get(position).isCheck());
                        ToastUtil.showShortToast(AppFavoritesActivity.this, getString(R.string.short_max_tips));
                    } else {
                        boolean isCheck = list.get(position).isCheck();
                        if (isCheck) {
                            if (!DBUtils.getInstance(AppFavoritesActivity.this).isExistData(list.get(position).getApppackagename())) {
                                DBUtils.getInstance(AppFavoritesActivity.this)
                                        .addFavorites(list.get(position).getAppname(),
                                                list.get(position).getApppackagename(),
                                                list.get(position).getAppicon());
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            DBUtils.getInstance(AppFavoritesActivity.this)
                                    .deleteFavorites(
                                            list.get(position).getApppackagename());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

        appfavorites_gridview.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {

                selected = position;
                select_number_tv.setText((selected + 1) + "/"
                        + list.size());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        appfavorites_gridview.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                GridViewItemOrderUtil.lastToNextFirst(appfavorites_gridview,
                        list.size(), 6, selected, keyCode, event);
                return false;
            }
        });

        appfavorites_gridview
                .setOnGenericMotionListener(new OnGenericMotionListener() {

                    @Override
                    public boolean onGenericMotion(View v, MotionEvent event) {

                        LogUtils.i(tag, "onGenericMotion");
                        if (0 != (event.getSource() & InputDevice.SOURCE_CLASS_POINTER)) {
                            switch (event.getAction()) {
                                // process the scroll wheel movement…处理滚轮事�??
                                case MotionEvent.ACTION_SCROLL:
                                    // 获得垂直坐标上的滚动方向,也就是滚轮向下滚
                                    if (event
                                            .getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f) {
                                        appfavorites_gridview
                                                .setSelection(appfavorites_gridview
                                                        .getFirstVisiblePosition());
                                    }
                                    // 获得垂直坐标上的滚动方向,也就是滚轮向上滚
                                    else {
                                        appfavorites_gridview
                                                .setSelection(appfavorites_gridview
                                                        .getFirstVisiblePosition());
                                    }
                                    return false;
                            }
                        }

                        return false;
                    }
                });

        appfavorites_gridview.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == SCROLL_STATE_IDLE) {
                    LogUtils.i(tag, "scrollState=" + scrollState);
                    appfavorites_gridview.setSelection(appfavorites_gridview
                            .getFirstVisiblePosition());
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {


            }
        });

    }

    private void loadDataApp() {
        // AppTask task = new AppTask();
        // task.execute();
        ArrayList<AppInfoBean> mList = AppUtils.getApplicationMsg(AppFavoritesActivity.this);
        ArrayList<AppSimpleBean> simpleList = DBUtils.getInstance(AppFavoritesActivity.this).getFavorites();
        //特定IP Special APP
        if (Utils.specialApps != null) {
            AppSimpleBean appSimpleBean = new AppSimpleBean();
            appSimpleBean.setId(simpleList.size());
            appSimpleBean.setPackagename(Utils.specialApps.getPackageName());
            simpleList.add(appSimpleBean);
        }
        for (int i = 0; i < simpleList.size(); i++) {
            for (int j = 0; j < mList.size(); j++) {
                if (simpleList.get(i).getPackagename().equals(mList.get(j).getApppackagename())) {
                    mList.get(j).setCheck(true);
                }
            }
        }
        if (mList != null) {
            list = mList;
            loadAllApp();
        }
    }


    private class AppTask extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground(Void... arg0) {
            ArrayList<AppInfoBean> list = AppUtils
                    .getApplicationMsg(AppFavoritesActivity.this);
            ArrayList<AppSimpleBean> simpleList = DBUtils.getInstance(
                    AppFavoritesActivity.this).getFavorites();
            for (int i = 0; i < simpleList.size(); i++) {
                for (int j = 0; j < list.size(); j++) {
                    if (simpleList.get(i).getPackagename()
                            .equals(list.get(j).getApppackagename())) {
                        list.get(j).setCheck(true);
                    }
                }
            }
            return list;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (result != null) {
                list = (ArrayList<AppInfoBean>) result;
                mHandler.sendEmptyMessage(Handler_update);
            }
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Handler_update:
                    loadAllApp();
                    break;

                default:
                    break;
            }
            return false;
        }
    });


    @Override
    public void appChange(String packageName) {
        if (!DBUtils.getInstance(AppFavoritesActivity.this).isExistData(
                packageName)
                && currentPackageName != null
                && currentPackageName.equals(packageName)) {
            DBUtils.getInstance(AppFavoritesActivity.this).addFavorites(getAppNameByPackageName(getApplicationContext(), packageName),
                    packageName, getAppIconByPackageName(getApplicationContext(), packageName));
            currentPackageName = null;
        }
        loadDataApp();
    }

    @Override
    public void appUnInstall(String packageName) {

        LogUtils.d(tag, " 收到卸载广播" + packageName);

        int code = DBUtils.getInstance(AppFavoritesActivity.this)
                .deleteFavorites(packageName);

        if (code > 0) {
            LogUtils.d(tag, " 收到卸载广播,删除成功" + code);
            currentPackageName = packageName;
        } else {
            LogUtils.d(tag, " 收到卸载广播,删除失败" + code);
            currentPackageName = null;
        }

        loadDataApp();
    }

    @Override
    public void appInstall(String packageName) {

        loadDataApp();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    // 传入应用的 packageName，返回对应的应用图标 Drawable
    public Drawable getAppIconByPackageName(Context context, String packageName) {
        try {
            // 获取 PackageManager 实例
            PackageManager packageManager = context.getPackageManager();

            // 通过 packageName 获取应用信息 (ApplicationInfo)
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);

            // 从 ApplicationInfo 中获取图标
            return appInfo.loadIcon(packageManager);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // 如果未找到对应的应用，返回 null
            return null;
        }
    }

    public String getAppNameByPackageName(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        String appName = null;

        try {
            // 获取应用程序的信息（ApplicationInfo）
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
            // 获取应用名称
            appName = packageManager.getApplicationLabel(appInfo).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return appName; // 如果未找到应用，则返回 null
    }


}
