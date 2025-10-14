package com.htc.luminaos.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.htc.luminaos.entry.AppSimpleBean;
import com.htc.luminaos.entry.SpecialApps;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;


/**
 * @author 徐豪
 * @version 创建时间 2020/9/8 下午3:50:51
 */
public class DBUtils extends SQLiteOpenHelper {

    private static String TAG = "DBUtils";
    private static DBUtils mInstance = null;
    private final static String DATABASE_NAME = "htc_launcher.db";
    private final static int VERSION = 1;
    private final String TABLENAME_SPECIALAPPS = "table_specialApps";
    private final String TABLENAME_FAVORITES = "table_favorites";

    private final String TABLENAME_FILTERAPPS = "filterApps";
    private final String TABLENAME_STATUSBAR = "statusBar";

    private final String TABLENAME_MAINAPP = "mainApp";

    private final String TABLENAME_LISTMODULES = "listModules";

    private final String TABLENAME_BRANDLOGO = "brandLogo";
    private SharedPreferences sharedPreferences;

    public static DBUtils getInstance(Context context) {
        if (mInstance == null) {
            Context deContext = context.createDeviceProtectedStorageContext();
            mInstance = new DBUtils(deContext);
        }
        return mInstance;
    }

    private DBUtils(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        sharedPreferences = ShareUtil.getInstans(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // TODO Auto-generated method stub
            //创建SpecialApps
            LogUtils.d(TAG, " 创建 specialApps 表 ");
            String specialApps_sql = "CREATE TABLE " + TABLENAME_SPECIALAPPS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "appName TEXT , " +
                    "packageName TEXT , " +
                    "iconData BLOB , " +
                    "continent TEXT , " + // 用于存储序列化的 Hashtable
                    "countryCode TEXT );";
            db.execSQL(specialApps_sql);


            // 创建我的收藏表
            LogUtils.d(TAG, " 创建apps数据表 ");
            String favorites_sql = "CREATE TABLE " + TABLENAME_FAVORITES
                    + " ( id integer primary key, appName TEXT, packagename text, iconData BLOB );";
            db.execSQL(favorites_sql);


            LogUtils.d(TAG, " 创建filterApps数据表 ");
            String filterApps_sql = "CREATE TABLE " + TABLENAME_FILTERAPPS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "packageName TEXT);";
            db.execSQL(filterApps_sql);

            // 创建statusBar表
            LogUtils.d(TAG, " 创建statusBar数据表 ");
            String statusBar_sql = "CREATE TABLE " + TABLENAME_STATUSBAR + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tag TEXT , " +
                    "iconPath TEXT , " +
                    "iconPath2 TEXT , " +
                    "iconDirectory TEXT );";
            db.execSQL(statusBar_sql);

            // 创建mainApp表
            LogUtils.d(TAG, " 创建mainApp数据表 ");
            String mainApp_sql = "CREATE TABLE " + TABLENAME_MAINAPP + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tag TEXT , " +
                    "appName TEXT , " +
                    "iconData BLOB , " +
                    "action TEXT );";
            db.execSQL(mainApp_sql);


            // 创建listModules表
            LogUtils.d(TAG, " 创建 listModules 表 ");
            String listModules_sql = "CREATE TABLE " + TABLENAME_LISTMODULES + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tag TEXT , " +
                    "iconData BLOB , " +
                    "hashtable_data TEXT , " + // 用于存储序列化的 Hashtable
                    "action TEXT );";
            db.execSQL(listModules_sql);


            // 创建brand品牌表
            LogUtils.d(TAG, " 创建brand品牌表");
            String brandLogo_sql = "CREATE TABLE " + TABLENAME_BRANDLOGO + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "iconData BLOB );";
            db.execSQL(brandLogo_sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        LogUtils.d(TAG, " 执行onUpgrade ");
        // TODO Auto-generated method stub
        String favorites_sql = "DROP TABLE IF EXISTS " + TABLENAME_SPECIALAPPS;
        db.execSQL(favorites_sql);

        favorites_sql = "DROP TABLE IF EXISTS " + TABLENAME_FAVORITES;
        db.execSQL(favorites_sql);

        favorites_sql = "DROP TABLE IF EXISTS " + TABLENAME_FILTERAPPS;
        db.execSQL(favorites_sql);

        favorites_sql = "DROP TABLE IF EXISTS " + TABLENAME_STATUSBAR;
        db.execSQL(favorites_sql);

        favorites_sql = "DROP TABLE IF EXISTS " + TABLENAME_MAINAPP;
        db.execSQL(favorites_sql);

        favorites_sql = "DROP TABLE IF EXISTS " + TABLENAME_LISTMODULES;
        db.execSQL(favorites_sql);

        favorites_sql = "DROP TABLE IF EXISTS " + TABLENAME_BRANDLOGO;
        db.execSQL(favorites_sql);

        onCreate(db);
    }


    /**
     * 添加收藏
     *
     * @param packagename
     * @return
     */
    public long addFavorites(String appName, String packagename, Drawable drawable) {
        synchronized (this) {
            SQLiteDatabase db = null;
            try {
                long code = -1;
                db = getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("appName", appName);
                cv.put("packagename", packagename);
                cv.put("iconData", drawableToByteArray(drawable));
                code = db.insert(TABLENAME_FAVORITES, null, cv);
                sharedPreferences.edit().putBoolean(Contants.MODIFY, true).apply();
                db.close();
                return code;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }


    /**
     * 获取收藏
     *
     * @return
     */
    public ArrayList<AppSimpleBean> getFavorites() {
        synchronized (this) {
            try {
                ArrayList<AppSimpleBean> list = new ArrayList<AppSimpleBean>();
                SQLiteDatabase db = getReadableDatabase();
                String sql = "select id , packagename  from " + TABLENAME_FAVORITES;
                Cursor cs = db.rawQuery(sql, null);
                while (cs.moveToNext()) {
                    AppSimpleBean bean = new AppSimpleBean();
                    bean.setId(cs.getInt(0));
                    bean.setPackagename(cs.getString(1));
                    list.add(bean);
                }
                db.close();
                return list;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    /**
     * 删除收藏
     *
     * @param packagename
     * @return
     */
    public int deleteFavorites(String packagename) {
        synchronized (this) {
            try {
                int code = -1;
                SQLiteDatabase db = getWritableDatabase();
                code = db.delete(TABLENAME_FAVORITES, "packagename=?",
                        new String[]{packagename});
                db.close();
                sharedPreferences.edit().putBoolean(Contants.MODIFY, true).apply();
                return code;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
    }


    /**
     * 清空收藏
     */
    public void clearFavorites() {
        synchronized (this) {
            SQLiteDatabase db = getWritableDatabase();
            String sql = "delete from " + TABLENAME_FAVORITES;
            db.execSQL(sql);
            db.close();
            sharedPreferences.edit().putBoolean(Contants.MODIFY, true).apply();
        }
    }


    public Drawable getFavoritesIcon(String packageName) {
        synchronized (this) {
            SQLiteDatabase db = getReadableDatabase();
            Drawable iconDrawable = null;
            // 查询语句
            String query = "SELECT iconData FROM " + TABLENAME_FAVORITES + " WHERE packagename = ?";
            Cursor cursor = db.rawQuery(query, new String[]{packageName});
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    byte[] iconBlob = cursor.getBlob(cursor.getColumnIndex("iconData"));
                    iconDrawable = byteArrayToDrawable(iconBlob);  // 将字节数组转换为 Drawable
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 关闭 Cursor 和数据库连接
                if (cursor != null) {
                    cursor.close();
                }
                db.close();
            }
            LogUtils.d(TAG, "Shortcuts 读取iconDrawable " + iconDrawable);
            return iconDrawable;
        }
    }


    public String getFavoritesAppName(String packageName) {
        synchronized (this) {
            SQLiteDatabase db = getReadableDatabase();
            String appName = null;
            Cursor cursor = null;
            try {
                // SQL 查询语句，通过 packagename 查找 appName
                String query = "SELECT appName FROM " + TABLENAME_FAVORITES + " WHERE packagename = ?";
                // 执行查询
                cursor = db.rawQuery(query, new String[]{packageName});
                // 检查结果是否存在
                if (cursor != null && cursor.moveToFirst()) {
                    // 获取 appName 列的数据
                    appName = cursor.getString(cursor.getColumnIndex("appName"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close(); // 关闭 cursor 以释放资源
                }
                db.close();
            }
            LogUtils.d(TAG, "Shortcuts 读取appName " + appName);
            return appName;
        }
    }


    /**
     * 查询数据库中条数
     *
     * @return
     */
//    public long getFavoritesCount() {
//        long count = 0;
//        SQLiteDatabase db = getWritableDatabase();
//        String sql = "select count(*) from " + TABLENAME_FAVORITES;
//        Cursor cursor = db.rawQuery(sql, null);
//        cursor.moveToFirst();
//        count = cursor.getLong(0);
//        cursor.close();
//        return count;
//    }
    public int getFavoritesCount() {
        synchronized (this) {
            SQLiteDatabase db = getWritableDatabase();
            String countQuery = "SELECT COUNT(*) FROM " + TABLENAME_FAVORITES;
            Cursor cursor = db.rawQuery(countQuery, null);
            int count = 0;

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
            return count;
        }
    }


    public boolean isExistData(String packagename) {
        synchronized (this) {
            boolean isExist = false;
            SQLiteDatabase db = getWritableDatabase();
            Cursor cs = db.rawQuery("select id , packagename  from "
                            + TABLENAME_FAVORITES + " where packagename = ?",
                    new String[]{packagename});
            while (cs.moveToNext()) {
                isExist = true;
            }
            db.close();
            return isExist;
        }
    }

    /**
     * 添加收藏
     *
     * @param packagename
     * @return
     */
    public long addSpeciales(String appName, String packagename, Drawable drawable, String continent, String countryCode) {
        synchronized (this) {
            SQLiteDatabase db = null;
            try {
                long code = -1;
                db = getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("appName", appName);
                cv.put("packagename", packagename);
                cv.put("iconData", drawableToByteArray(drawable));
                cv.put("continent", continent);
                cv.put("countryCode", countryCode);
                code = db.insert(TABLENAME_SPECIALAPPS, null, cv);
                sharedPreferences.edit().putBoolean(Contants.MODIFY, true).apply();
                db.close();
                return code;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    public SpecialApps querySpecialApps(String continent, String countryCode) {
        synchronized (this) {
            SQLiteDatabase db = getWritableDatabase();
            SpecialApps specialApp = null; // 用于存储查询到的结果
            Cursor cursor = null;
            LogUtils.d(TAG, " querySpecialApps continent " + continent + " args " + countryCode);
            try {
                // 动态拼接 WHERE 条件  假定APPStre传来的数据是 亚洲|ZH
                StringBuilder query = new StringBuilder("SELECT * FROM table_specialApps WHERE ");
                List<String> args = new ArrayList<>();
                if (continent != null && countryCode != null) { //第一轮精确查找，洲和国家同时满足
                    LogUtils.d(TAG, "querySpecialApps 首先第一种情况 精确匹配洲和国家码 ");
                    query.append("continent = ? ");
                    args.add(continent); // 不再使用模糊匹配
                    query.append("AND ");
                    query.append("countryCode = ? ");
                    args.add(countryCode); // 精确匹配
                    cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));
                    if (cursor == null || !cursor.moveToFirst()) { //第二轮只查找国家码，洲为空
                        LogUtils.d(TAG, "querySpecialApps 第二种情况 只匹配国家码");
                        query.setLength(0); // 清空之前的查询
                        args.clear(); // 清空参数列表
                        query.append("SELECT * FROM table_specialApps WHERE ");
                        query.append("(continent IS NULL OR continent = '') ");
                        query.append("AND ");
                        query.append("countryCode = ? ");
                        args.add(countryCode);
                        cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));
                    }
                    if (cursor == null || !cursor.moveToFirst()) { //第三轮查找是否有同一个洲多个国家指定包含，如 亚洲，ZH|JA|TR 这种，指定了亚洲三个国家，对ZH也生效
                        LogUtils.d(TAG, "querySpecialApps 第三种情况 指定洲 ，如 亚洲,ZH|JA|KR，洲相同包含了ZH就行");
                        query.setLength(0); // 清空之前的查询
                        args.clear(); // 清空参数列表
                        // 使用 INSTR 实现包含逻辑
                        query.append("SELECT * FROM table_specialApps WHERE ");
                        query.append("continent = ? ");
                        args.add(continent);
                        query.append("AND ");
                        query.append("INSTR(countryCode, ?) > 0 ");
                        args.add(countryCode);
                        cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));
                    }
                    if (cursor == null || !cursor.moveToFirst()) { //第四轮查找，查找洲相同，但是有指定非ZH图标的配置，例如亚洲,!JA 就对ZH生效
                        LogUtils.d(TAG, "querySpecialApps 第四种情况 亚洲,ZH 查找有没有让它生效的类似 亚洲,!JA 这种配置");
                        // 第一次查询无结果，准备第二次查询
                        query.setLength(0); // 清空之前的查询
                        args.clear(); // 清空参数列表
                        query.append("SELECT * FROM table_specialApps WHERE ");
                        query.append("continent = ? ");
                        args.add(continent);
                        query.append("AND ");
                        query.append("INSTR(countryCode, '!') > 0 ");
                        query.append("AND ");
                        query.append("countryCode != ? ");
                        args.add("!" + countryCode);
                        cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));
                    }
                    if (cursor == null || !cursor.moveToFirst()) { //第五轮查找，不指定洲，不同洲的国家放在一起，如ZH|RU|EN|
                        LogUtils.d(TAG, "querySpecialApps 第五种情况 不指定洲，不同洲放在一起 ZH|EN|RU");
                        query.setLength(0); // 清空之前的查询
                        args.clear(); // 清空参数列表
                        // 使用 INSTR 实现包含逻辑
                        query.append("SELECT * FROM table_specialApps WHERE ");
                        query.append("(continent IS NULL OR continent = '') ");
                        query.append("AND countryCode IS NOT NULL ");
                        query.append("AND INSTR(countryCode, ?) > 0 ");
                        args.add(countryCode);
                        cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));
                    }
                    if (cursor == null || !cursor.moveToFirst()) { //第六轮查找，指定一个洲所有国家都生效的情况，即countryCode=""
                        LogUtils.d(TAG, "querySpecialApps 第六种情况 只指定一个洲，对洲内所有国家生效 ");
                        query.setLength(0); // 清空之前的查询
                        args.clear(); // 清空参数列表
                        query.append("SELECT * FROM table_specialApps WHERE ");
                        query.append("continent = ? ");
                        args.add(continent);
                        query.append(" AND (countryCode IS NULL OR countryCode = '') ");
                        cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));
                    }
                } else if (continent != null && countryCode == null) { //应对 APPStore写值，其中有一个为空时的情况
                    LogUtils.d(TAG, "querySpecialApps AppStroe传来的countryCode 国家码为空");
                    query.append("continent = ? ");
                    args.add(continent);
                    query.append(" AND (countryCode IS NULL OR countryCode = '') ");
                    cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));
                } else {
                    LogUtils.d(TAG, "querySpecialApps AppStroe传来的continent 洲为空");
//                query.append("(continent IS NULL OR continent = '') ");
//                query.append("AND ");
                    query.append("countryCode = ? ");
                    args.add(countryCode);
                    cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));
                }
                // 解析结果
                if (cursor != null && cursor.moveToFirst()) {
                    // 如果查询有结果，构造 SpecialApps 对象
                    specialApp = new SpecialApps();
                    specialApp.setAppName(cursor.getString(cursor.getColumnIndexOrThrow("appName")));
                    specialApp.setPackageName(cursor.getString(cursor.getColumnIndexOrThrow("packageName")));
                    specialApp.setIconData(cursor.getBlob(cursor.getColumnIndexOrThrow("iconData")));
                    specialApp.setContinent(cursor.getString(cursor.getColumnIndexOrThrow("continent")));
                    specialApp.setCountryCode(cursor.getString(cursor.getColumnIndexOrThrow("countryCode")));
                    LogUtils.d(TAG, " querySpecialApps 查询到了结果 ");
                } else {
                    LogUtils.d(TAG, " querySpecialApps 未查询到结果 ");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return specialApp; // 返回查询到的 SpecialApps 对象，或 null（如果没有匹配结果）
        }
    }


    public boolean isExistSpecial(String packagename) {
        synchronized (this) {
            boolean isExist = false;
            SQLiteDatabase db = getWritableDatabase();
            Cursor cs = db.rawQuery("select id , packagename  from "
                            + TABLENAME_SPECIALAPPS + " where packagename = ?",
                    new String[]{packagename});
            while (cs.moveToNext()) {
                isExist = true;
            }
            db.close();
            return isExist;
        }
    }

    public void clearSpecialAppsTableAndResetId() {
        synchronized (this) {
            SQLiteDatabase db = getWritableDatabase();
            try {
                LogUtils.d(TAG," clearSpecialAppsTableAndResetId 清空数据");
                db.execSQL("DELETE FROM " + TABLENAME_SPECIALAPPS);
                db.execSQL("DELETE FROM sqlite_sequence WHERE name='" + TABLENAME_SPECIALAPPS + "';");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.close();
            }
        }
    }

    /***
     * Time:2025/6/25
     * Author:xuhao
     * Usage:将从配置文件config中读出来的信息保存进本地的db数据库。
     * @param tag
     * @param iconPath
     * @param iconPath2
     * @param iconDirectory
     */
    public void insertStatusBarData(String tag, String iconPath, String iconPath2, String iconDirectory) {
        synchronized (this) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("tag", tag);
            values.put("iconPath", iconPath);
            values.put("iconPath2", iconPath2);  // 插入 BLOB 数据
            values.put("iconDirectory", iconDirectory);

            // 检查是否存在相同的 tag
            String selection = "tag = ?";
            String[] selectionArgs = {tag};
            int rowsAffected = db.update(TABLENAME_STATUSBAR, values, selection, selectionArgs);

            if (rowsAffected == 0) {
                // 如果没有记录被更新，说明不存在这个 tag，执行插入操作
                long code = db.insert(TABLENAME_STATUSBAR, null, values);
                if (code == -1) {
                    LogUtils.d(TAG, "MainApp插入数据失败");
                } else {
                    LogUtils.d(TAG, "MainApp插入数据成功，行ID：" + code);
                }
            } else {
                LogUtils.d(TAG, "MainApp数据更新成功，更新行数：" + rowsAffected);
            }
            db.close();
        }
    }

    public StatusBarItem queryStatusBarData(String tag) {
        synchronized (this) {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = null;
            String[] projection = {"iconPath", "iconPath2", "iconDirectory"};
            String selection = "tag = ?";
            String[] selectionArgs = {tag};
            StatusBarItem item = null;

            try {
                cursor = db.query(
                        TABLENAME_STATUSBAR,  // 表名
                        projection,           // 要查询的列
                        selection,            // WHERE 子句
                        selectionArgs,        // WHERE 参数
                        null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    String iconPath = cursor.getString(cursor.getColumnIndexOrThrow("iconPath"));
                    String iconPath2 = cursor.getString(cursor.getColumnIndexOrThrow("iconPath2"));
                    String iconDirectory = cursor.getString(cursor.getColumnIndexOrThrow("iconDirectory"));
                    LogUtils.d(TAG,"queryStatusBarData "+tag+" "+iconPath+" "+iconPath2+" "+iconDirectory);
                    item = new StatusBarItem(tag, iconPath, iconPath2, iconDirectory);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 关闭 Cursor 和数据库连接
                if (cursor != null) {
                    cursor.close();
                }
                db.close();
            }
            return item;
        }
    }


    /***
     * Time:2024/8/10
     * Author:xuhao
     * Usage:将从配置文件config中读出来的信息保存进本地的db数据库。
     * @param tag
     * @param appName
     * @param drawable
     * @param action
     */
    public void insertMainAppData(String tag, String appName, Drawable drawable, String action) {
        synchronized (this) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("tag", tag);
            values.put("appName", appName);
            values.put("iconData", drawableToByteArray(drawable));  // 插入 BLOB 数据
            values.put("action", action);

            // 检查是否存在相同的 tag
            String selection = "tag = ?";
            String[] selectionArgs = {tag};
            int rowsAffected = db.update(TABLENAME_MAINAPP, values, selection, selectionArgs);

            if (rowsAffected == 0) {
                // 如果没有记录被更新，说明不存在这个 tag，执行插入操作
                long code = db.insert(TABLENAME_MAINAPP, null, values);
                if (code == -1) {
                    LogUtils.d(TAG, "MainApp插入数据失败");
                } else {
                    LogUtils.d(TAG, "MainApp插入数据成功，行ID：" + code);
                }
            } else {
                LogUtils.d(TAG, "MainApp数据更新成功，更新行数：" + rowsAffected);
            }
            db.close();
        }
    }

    public void insertFilterApps(String[] packageNames) {
        synchronized (this) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();

            // 遍历数组并插入到数据库中
            for (String packageName : packageNames) {
                values.clear(); // 清空之前的值
                values.put("packageName", packageName);
                long code = db.insert(TABLENAME_FILTERAPPS, null, values);
                if (code == -1) {
                    LogUtils.d(TAG, "FilterApps插入数据失败");
                } else {
                    LogUtils.d(TAG, "FilterApps插入数据成功，行ID：" + code);
                }
            }
            db.close();
        }
    }

    public String[] getFilterApps() {
        synchronized (this) {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = null;
            String[] packageNames = null;

            try {
                // 查询所有数据
                cursor = db.query(TABLENAME_FILTERAPPS, new String[]{"packageName"}, null, null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    // 获取数据行数
                    int count = cursor.getCount();
                    // 初始化数组
                    packageNames = new String[count];

                    // 遍历Cursor，填充数组
                    int index = 0;
                    do {
                        String packageName = cursor.getString(cursor.getColumnIndex("packageName"));
                        packageName = packageName.trim();
                        packageNames[index++] = packageName;
                        LogUtils.d(TAG, " getFilterApps " + packageName);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                // 捕获异常并记录日志
                LogUtils.e(TAG, "读取数据失败 "+e);
            } finally {
                // 确保Cursor和数据库连接在完成后关闭
                if (cursor != null) {
                    cursor.close();
                }
                db.close();
            }
            return packageNames;
        }
    }


    public void insertListModulesData(String tag, Drawable drawable, Hashtable hashtable, String action) {
        synchronized (this) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("tag", tag);
            values.put("iconData", drawableToByteArray(drawable));  // 插入 BLOB 数据
            // hashtable序列化存入数据库
            saveHashtableToDatabase(hashtable, values);
            values.put("action", action);

            // 检查是否存在相同的 tag
            String selection = "tag = ?";
            String[] selectionArgs = {tag};
            int rowsAffected = db.update(TABLENAME_LISTMODULES, values, selection, selectionArgs);

            if (rowsAffected == 0) {
                // 如果没有记录被更新，说明不存在这个 tag，执行插入操作
                long code = db.insert(TABLENAME_LISTMODULES, null, values);
                if (code == -1) {
                    LogUtils.d(TAG, "ListModules插入数据失败");
                } else {
                    LogUtils.d(TAG, "ListModules插入数据成功，行ID：" + code);
                }
            } else {
                LogUtils.d(TAG, "ListModules数据更新成功，更新行数：" + rowsAffected);
            }
            db.close();
        }
    }

    public void insertBrandLogoData(Drawable drawable) {
        synchronized (this) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("iconData", drawableToByteArray(drawable));

            // 检查是否存在数据（假设表中只有一个logo）
            int rowsAffected = db.update(TABLENAME_BRANDLOGO, values, null, null);

            if (rowsAffected == 0) {
                // 如果没有记录被更新，说明表中还没有数据，执行插入操作
                long code = db.insert(TABLENAME_BRANDLOGO, null, values);
                if (code == -1) {
                    LogUtils.d(TAG, "BrandLogo插入数据失败");
                } else {
                    LogUtils.d(TAG, "BrandLogo插入数据成功，行ID：" + code);
                }
            } else {
                LogUtils.d(TAG, "BrandLogo数据更新成功，更新行数：" + rowsAffected);
            }
            db.close();
        }
    }


    public Hashtable<String, String> getHashtableFromListModules(String tag) {
        synchronized (this) {
            SQLiteDatabase db = getWritableDatabase();
            Hashtable<String, String> hashtable = null;

            // 查询数据库获取保存的JSON字符串，通过tag筛选
            Cursor cursor = db.query(
                    "listModules",            // 表名
                    new String[]{"hashtable_data"}, // 要查询的列
                    "tag = ?",                     // 查询条件
                    new String[]{tag},             // 查询条件的参数
                    null,                          // 不进行分组
                    null,                          // 不进行分组后的筛选
                    null                           // 不进行排序
            );

            if (cursor != null && cursor.moveToFirst()) {
                String jsonString = cursor.getString(cursor.getColumnIndex("hashtable_data"));

                LogUtils.d(TAG, " 读取到的jsonString的值为 " + jsonString);

                // 使用Gson将JSON字符串反序列化为Hashtable
                Gson gson = new Gson();
                Type type = new TypeToken<Hashtable<String, String>>() {
                }.getType();
                hashtable = gson.fromJson(jsonString, type);

                LogUtils.d(TAG, " 获取到的阿拉伯语言 " + hashtable.get("ar"));

                cursor.close();  // 关闭Cursor
            }
            db.close();
            return hashtable;  // 返回反序列化后的Hashtable
        }
    }

    public String getActionFromListModules(String tag) {
        synchronized (this) {
            SQLiteDatabase db = getWritableDatabase();
            String action = null;
            Cursor cursor = null;
            try {
                cursor = db.query(
                        "listModules",            // 表名
                        new String[]{"action"},        // 要查询的列
                        "tag = ?",                     // 查询条件
                        new String[]{tag},             // 查询条件的参数
                        null,                          // 不进行分组
                        null,                          // 不进行分组后的筛选
                        null                           // 不进行排序
                );

                // 检查是否有结果，并提取 action
                if (cursor != null && cursor.moveToFirst()) {
                    action = cursor.getString(cursor.getColumnIndex("action"));
                }
                LogUtils.d(TAG, "getActionFromListModules action " + action);
            } catch (Exception e) {
                LogUtils.d(TAG, "getActionFromListModules查询失败 "+e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            db.close();
            return action;
        }
    }

    public Drawable getDrawableFromListModules(String tag) {
        synchronized (this) {
            SQLiteDatabase db = getWritableDatabase();
            Drawable drawable = null;
            // 查询数据库获取保存的JSON字符串，通过tag筛选
            Cursor cursor = db.query(
                    "listModules",            // 表名
                    new String[]{"iconData"}, // 要查询的列
                    "tag = ?",                     // 查询条件
                    new String[]{tag},             // 查询条件的参数
                    null,                          // 不进行分组
                    null,                          // 不进行分组后的筛选
                    null                           // 不进行排序
            );
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    byte[] iconData = cursor.getBlob(cursor.getColumnIndex("iconData"));
                    drawable = byteArrayToDrawable(iconData);  // 将字节数组转换为 Drawable
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 关闭 Cursor 和数据库连接
                if (cursor != null) {
                    cursor.close();
                }
                db.close();
            }
            return drawable;
        }
    }

    public Drawable getDrawableFromBrandLogo(int id) {
        synchronized (this) {
            SQLiteDatabase db = getReadableDatabase();
            Drawable drawable = null;
            Cursor cursor = db.query(
                    TABLENAME_BRANDLOGO,             // 表名
                    new String[]{"iconData"},        // 要查询的列
                    "id = ?",                        // 查询条件
                    new String[]{String.valueOf(id)}, // 查询条件的参数
                    null,                            // 不进行分组
                    null,                            // 不进行分组后的筛选
                    null                             // 不进行排序
            );
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    byte[] iconData = cursor.getBlob(cursor.getColumnIndex("iconData"));
                    drawable = byteArrayToDrawable(iconData);  // 将字节数组转换为 Drawable
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 关闭 Cursor 和数据库连接
                if (cursor != null) {
                    cursor.close();
                }
                db.close();
            }
            return drawable;  // 返回 Drawable
        }
    }


    /***
     * Time:2024/8/10
     * Author:xuhao
     * Usage:将drawable转化成ByteArray，
     * 方便保存到SQLite数据库中。
     * @param drawable
     * @return
     */
    public byte[] drawableToByteArray(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = getBitmapFromDrawable(drawable);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /***
     * Time:2024/8/12
     * Author:xuhao
     * Usage:通过name去查询数据库的数据，返回drawable
     * @param tag
     * @return
     */
    public Drawable getIconDataByTag(String tag) {
        synchronized (this) {
            SQLiteDatabase db = getReadableDatabase();
            Drawable drawable = null;
            Cursor cursor = null;

            try {
                // 查询数据库中的数据
                cursor = db.query(
                        TABLENAME_MAINAPP,                   // 表名
                        new String[]{"iconData"},            // 需要查询的列名
                        "tag = ?",                          // 查询条件
                        new String[]{tag},                  // 查询条件的参数
                        null,                                // Group By
                        null,                                // Having
                        null                                 // Order By
                );

                // 检查是否查找到结果
                if (cursor != null && cursor.moveToFirst()) {
                    byte[] iconData = cursor.getBlob(cursor.getColumnIndex("iconData"));
                    drawable = byteArrayToDrawable(iconData);  // 将字节数组转换为 Drawable
                }
                LogUtils.d(TAG, "查询数据成功");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 关闭 Cursor 和数据库连接
                if (cursor != null) {
                    cursor.close();
                }
                db.close();
            }
            return drawable;
        }
    }

    /***
     * Time:2024/8/12
     * Author:xuhao
     * Usage:将ByteArray转化成drawable。
     * @param byteArray
     * @return
     */
    public Drawable byteArrayToDrawable(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        return new BitmapDrawable(Resources.getSystem(), BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
    }

    public String getAppNameByTag(String tag) {
        synchronized (this) {
            SQLiteDatabase db = getReadableDatabase();
            String appName = null;

            // 查询条件
            String selection = "tag = ?";
            String[] selectionArgs = {tag};

            // 查询数据库
            Cursor cursor = db.query(
                    TABLENAME_MAINAPP,   // 表名
                    new String[]{"appName"}, // 查询的列
                    selection,          // 查询条件
                    selectionArgs,      // 查询条件参数
                    null,               // 分组
                    null,               // 分组条件
                    null                // 排序
            );
            // 处理查询结果
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    appName = cursor.getString(cursor.getColumnIndex("appName"));
                }
                cursor.close();
            }
            db.close();
            return appName;
        }
    }

    public String getActionByTag(String tag) {
        synchronized (this) {
            SQLiteDatabase db = getReadableDatabase();
            String action = null;
            // 查询条件
            String selection = "tag = ?";
            String[] selectionArgs = {tag};
            // 查询数据库
            Cursor cursor = db.query(
                    TABLENAME_MAINAPP,   // 表名
                    new String[]{"action"}, // 查询的列
                    selection,          // 查询条件
                    selectionArgs,      // 查询条件参数
                    null,               // 分组
                    null,               // 分组条件
                    null                // 排序
            );
            // 处理查询结果
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    action = cursor.getString(cursor.getColumnIndex("action"));
                }
                cursor.close();
            }
            db.close();
            return action;
        }
    }

    public void saveHashtableToDatabase(Hashtable<String, String> hashtable, ContentValues values) {
        // 使用Gson将Hashtable序列化为JSON字符串
        Gson gson = new Gson();
        String jsonString = gson.toJson(hashtable);

        LogUtils.d(TAG, "序列化之后的hashtable_data值为 " + jsonString);

        // 创建ContentValues用于插入数据
        values.put("hashtable_data", jsonString);  // 将序列化后的字符串存入ContentValues
    }


    public void deleteTable() {
        synchronized (this) {
            SQLiteDatabase db = getWritableDatabase();
            try {
                db.delete(TABLENAME_FAVORITES, null, null);
                db.delete(TABLENAME_FILTERAPPS,null,null);
                db.delete(TABLENAME_STATUSBAR, null, null);
                db.delete(TABLENAME_MAINAPP, null, null);
                db.delete(TABLENAME_LISTMODULES, null, null);
                db.delete(TABLENAME_BRANDLOGO, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    public Bitmap getBitmapFromDrawable(Drawable drawable) {
//
//        return ((BitmapDrawable) drawable).getBitmap();
//    }

    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof AdaptiveIconDrawable) {
            Drawable newDrawable = drawable.getConstantState().newDrawable().mutate();
            // 对 AdaptiveIconDrawable 进行处理
            int width = newDrawable.getIntrinsicWidth();
            int height = newDrawable.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            newDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            newDrawable.draw(canvas);
            return bitmap;
        } else {
            Drawable newDrawable = drawable.getConstantState().newDrawable().mutate();
            // 兜底方案：对其他类型 Drawable 的处理
            int width = newDrawable.getIntrinsicWidth() > 0 ? newDrawable.getIntrinsicWidth() : 1;
            int height = newDrawable.getIntrinsicHeight() > 0 ? newDrawable.getIntrinsicHeight() : 1;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            newDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            newDrawable.draw(canvas);
            return bitmap;
        }
    }


}
