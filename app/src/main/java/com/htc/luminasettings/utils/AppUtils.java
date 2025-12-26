package com.htc.luminasettings.utils;

/**
 * @author 作者：zgr
 * @version 创建时间：2016年11月3日 下午5:50:52
 * 类说明
 */

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.htc.luminasettings.R;
import com.htc.luminasettings.entry.AppInfoBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AppUtils {

    private static String TAG = "AppUtils";

    /**
     * 获取全部应用程序的信息
     *
     * @param context
     * @return
     */
    public static ArrayList<AppInfoBean> getApplicationMsg(Context context) {
        ArrayList<AppInfoBean> list = new ArrayList<AppInfoBean>();
        PackageManager pm = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,
                PackageManager.GET_UNINSTALLED_PACKAGES);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<ResolveInfo> resolveInfos_lb = leanbackActivitiesIn(pm);
            for (ResolveInfo resolveInfo_lb : resolveInfos_lb) {
                boolean has_flag = false;
                for (ResolveInfo resolveInfo : resolveInfos) {
                    if (resolveInfo_lb.activityInfo.packageName.equals(resolveInfo.activityInfo.packageName)) {
                        has_flag = true;
                        break;
                    }
                }
                if (has_flag)
                    continue;
                resolveInfos.add(resolveInfo_lb);
            }
        }
        //排除"filterApps" 屏蔽掉的APP
//		String[] filterApps = MyApplication.config.filterApps.split(";");
        String[] filterApps = DBUtils.getInstance(context).getFilterApps();
        List<String> stringList = new ArrayList<>();
        if (filterApps != null) {
            LogUtils.d(TAG, " 禁用名单 " + filterApps[0]);
            stringList = Arrays.asList(filterApps);
        }
        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(resolveInfos,
                new ResolveInfo.DisplayNameComparator(pm));
        for (ResolveInfo reInfo : resolveInfos) {
            String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
            String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
            String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
            Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
            // 创建一个AppInfo对象，并赋值
            AppInfoBean appInfo = new AppInfoBean();
            appInfo.setAppname(appLabel);
            appInfo.setApppackagename(pkgName);
            appInfo.setApplicationInfo(reInfo.activityInfo.applicationInfo);
            appInfo.setAppicon(icon);
            appInfo.setMname(activityName);
            if (!stringList.contains(pkgName) && !Utils.specialAppsList.contains(pkgName)) {
                list.add(appInfo); // 添加至列表中
            }
        }
        return list;
    }

    public static ArrayList<AppInfoBean> getApplicationMsg(Context context, boolean all) {
        ArrayList<AppInfoBean> list = new ArrayList<AppInfoBean>();
        PackageManager pm = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,
                PackageManager.GET_UNINSTALLED_PACKAGES);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<ResolveInfo> resolveInfos_lb = leanbackActivitiesIn(pm);
            for (ResolveInfo resolveInfo_lb : resolveInfos_lb) {
                boolean has_flag = false;
                for (ResolveInfo resolveInfo : resolveInfos) {
                    if (resolveInfo_lb.activityInfo.packageName.equals(resolveInfo.activityInfo.packageName)) {
                        has_flag = true;
                        break;
                    }
                }

                if (has_flag)
                    continue;

                resolveInfos.add(resolveInfo_lb);

            }
        }

        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(resolveInfos,
                new ResolveInfo.DisplayNameComparator(pm));
        for (ResolveInfo reInfo : resolveInfos) {
            String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
            String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
            String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
            Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
            // 创建一个AppInfo对象，并赋值
            AppInfoBean appInfo = new AppInfoBean();
            appInfo.setAppname(appLabel);
            appInfo.setApppackagename(pkgName);
            appInfo.setAppicon(icon);
            appInfo.setApplicationInfo(reInfo.activityInfo.applicationInfo);
            appInfo.setMname(activityName);
            list.add(appInfo); // 添加至列表中

        }
        return list;
    }

    private static List<ResolveInfo> getResolveInfos(PackageManager packageManager, Intent intent) {
        return packageManager.queryIntentActivities(intent, PackageManager.GET_UNINSTALLED_PACKAGES);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static List<ResolveInfo> leanbackActivitiesIn(PackageManager packageManager) {
        Intent intent = new Intent()
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        return getResolveInfos(packageManager, intent);
    }

    /**
     * 根据应用包名获取应用信息
     *
     * @param context
     * @param packageName
     * @return
     */
    public static AppInfoBean getApplicationMsg_Package(Context context,
                                                        String packageName) {
        AppInfoBean appInfo = null;
        PackageManager pm = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,
                PackageManager.GET_UNINSTALLED_PACKAGES);
        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(resolveInfos,
                new ResolveInfo.DisplayNameComparator(pm));
        for (ResolveInfo reInfo : resolveInfos) {
            String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
            String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
            String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
            Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
            // 为应用程序的启动Activity 准备Intent
            // 创建一个AppInfo对象，并赋值
            if (pkgName.equals(packageName)) {
                appInfo = new AppInfoBean();
                appInfo.setAppname(appLabel);
                appInfo.setApppackagename(pkgName);
                appInfo.setAppicon(icon);
                appInfo.setMname(activityName);
            }
        }
        return appInfo;
    }

    /**
     * 根据包和类启动
     *
     * @param context
     * @param packageName
     * @param className
     */
    public static void startNewApp(Context context, String packageName,
                                   String className) {
        if (checkPackage(context, packageName)) {
            try {
                Intent intent = new Intent();
                ComponentName component = new ComponentName(packageName, className);
                intent.setComponent(component);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 判断包是否存在
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkPackage(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 根据包启动APK
     *
     * @param context
     * @param packageName
     */
    public static boolean startNewApp(Context context, String packageName) {

        LogUtils.d(TAG, " startNewApp " + packageName);
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                return true;
            }
            intent = packageManager.getLeanbackLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //ToastUtil.showShortToast(context, context.getString(R.string.data_none));
        return false;
    }

    public static void startNewActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 判断应用是否是系统应用以及是否可以被卸载
     *
     * @param context 上下文
     * @param packageName 应用包名
     * @return 返回一个数组，第一个值为是否是系统应用，第二个值为是否可以被卸载
     */
    public static boolean[] checkIfSystemAppAndCanUninstall(Context context, String packageName) {
        boolean[] result = new boolean[2]; // result[0] 是系统应用标志，result[1] 是是否可卸载标志
        PackageManager pm = context.getPackageManager();

        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);

            // 判断是否是系统应用
            boolean isSystemApp = (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

            // 判断是否可卸载
            boolean canUninstall = (appInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0;

            result[0] = isSystemApp;
            result[1] = !isSystemApp; // 系统应用通常不能被卸载，因此可卸载标志与是否是系统应用取反

        } catch (PackageManager.NameNotFoundException e) {
            // 应用未找到
            result[0] = false;
            result[1] = false;
        }

        return result;
    }

    public static String getAppNameByPackageName(Context context, String packageName) {
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

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;  // 找到 → 已安装
        } catch (PackageManager.NameNotFoundException e) {
            return false; // 没找到 → 未安装
        }
    }

}
