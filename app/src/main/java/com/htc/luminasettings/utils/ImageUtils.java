package com.htc.luminasettings.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;

public class ImageUtils {

    private static String TAG = "ImageUtils";

    /**
     * 计算inSampleSize值，对图片进行缩放处理
     *
     * @param options   用于获取原图的长宽
     * @return 返回计算后的inSampleSize值
     */
    public static int calculateInSampleSize(BitmapFactory.Options options) {
        // 原图片的宽高
        final int height = options.outHeight;
        final int width = options.outWidth;
        // 获取设备的屏幕宽度
        float widthPixels = (float) Resources.getSystem().getDisplayMetrics().widthPixels;
        // 计算比例，避免除法结果过小
        float scaleFactor = widthPixels / 1920f;
        float x =135f*scaleFactor;
        float y = 207f*scaleFactor;
        float inSampleSize = 1.0f;
        if (height > x || width > y) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // 计算inSampleSize值
            while ((halfHeight / inSampleSize) >= x && (halfWidth / inSampleSize) >= y) {
                inSampleSize *= 2;
            }
        }
        LogUtils.d(TAG, " 图片缩略图 calculateInSampleSize options.inSampleSize " + options.inSampleSize + " inSampleSize " +inSampleSize);
        return (int)inSampleSize;
    }

    /**
     * 从指定路径加载图片并转换为 Drawable 对象
     *
     * @param filePath 图片文件的路径
     * @return Drawable 对象，如果加载失败则返回 null
     */
    public static Drawable loadImageFromPath(String filePath, Context context) {
        if (filePath == null || filePath.isEmpty()) {
            LogUtils.e(TAG, "文件路径为空");
            return null;
        }
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            LogUtils.e(TAG, "文件不存在或不是有效的文件：" + filePath);
            return null;
        }
        try {
            // 使用 BitmapFactory 解码文件为 Bitmap
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            if (bitmap != null) {
                // 转换为 Drawable 并返回
                return new BitmapDrawable(context.getResources(), bitmap);
            } else {
                LogUtils.e(TAG, "无法解码图片文件：" + filePath);
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "加载图片时发生错误：" + e.getMessage());
        }
        return null;
    }
}
