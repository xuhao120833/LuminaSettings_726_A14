package com.htc.luminasettings.adapter;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.htc.luminasettings.R;
import com.htc.luminasettings.utils.Contants;
import com.htc.luminasettings.utils.ImageUtils;
import com.htc.luminasettings.utils.LogUtils;
import com.htc.luminasettings.utils.ShareUtil;
import com.htc.luminasettings.widget.FocusKeepRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Author:
 * Date:
 * Description:
 */
public class WallPaperAdapter extends RecyclerView.Adapter<WallPaperAdapter.MyViewHolder> implements View.OnFocusChangeListener, View.OnHoverListener {

    Context mContext;
    ArrayList<Object> drawables;
    WallPaperOnCallBack wallPaperOnCallBack;
    File[] files;
    boolean isLocal = true;
    private ExecutorService executorService = Executors.newFixedThreadPool(8);
    Handler handler;

    FocusKeepRecyclerView focusKeepRecyclerView;

    public static int selectpostion = -1;
    private static String TAG = "WallPaperAdapter";

    private LruCache<String, Bitmap> imageCache;

    private static LruCache<Integer, BitmapDrawable> drawableCache;

    private Map<Integer, Future<?>> taskMap = new ConcurrentHashMap<>();
    Bitmap whiteBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

    RecyclerView.LayoutManager layoutManager = null;
    GridLayoutManager gridLayoutManager = null;

    public WallPaperAdapter(Context mContext, ArrayList<Object> drawables, Handler handler, FocusKeepRecyclerView focusKeepRecyclerView) {
        this.mContext = mContext;
        this.drawables = drawables;
        this.handler = handler;
        this.focusKeepRecyclerView = focusKeepRecyclerView;
        selectpostion = readShared();
        initCache();
        Canvas canvas = new Canvas(whiteBitmap);
        canvas.drawColor(Color.WHITE);
        layoutManager = focusKeepRecyclerView.getLayoutManager();
        gridLayoutManager = (GridLayoutManager) layoutManager;
    }

    public WallPaperAdapter(Context mContext, File[] files, Handler handler) {
        this.mContext = mContext;
        this.files = files;
        isLocal = false;
//        this.threadExecutor = threadExecutor;
        this.handler = handler;
        selectpostion = readShared();
    }

    public void setWallPaperOnCallBack(WallPaperOnCallBack wallPaperOnCallBack) {
        this.wallPaperOnCallBack = wallPaperOnCallBack;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LogUtils.d(TAG, " 执行onCreateViewHolder " + i);
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wallpaper_custom_item, null));
        myViewHolder.setIsRecyclable(false);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, @SuppressLint("RecyclerView") final int i) {
        selectpostion = readShared();
        LogUtils.d(TAG, "onBindViewHolder selectpostion " + selectpostion);
        if (i == selectpostion) {
            myViewHolder.check.setVisibility(View.VISIBLE);
            myViewHolder.check.setImageResource(R.drawable.check_correct);
        } else if (selectpostion == -1) {//使用默认配置背景
            SharedPreferences sharedPreferences = ShareUtil.getInstans(mContext);
            String defaultbg = sharedPreferences.getString(Contants.DefaultBg, "1");
            int number = Integer.parseInt(defaultbg);
            if (number - 1 == i) {
                myViewHolder.check.setVisibility(View.VISIBLE);
                myViewHolder.check.setImageResource(R.drawable.check_correct);
            }
            writeShared(number - 1);
        } else {
            myViewHolder.check.setVisibility(View.GONE);
        }
        myViewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //xuhao add
                    int position = myViewHolder.getAdapterPosition();
                    if (position < drawables.size() - 1) {
                        LogUtils.d(TAG, " 图片背景选择 position < drawables.size()-1" + position + " drawables.size " + drawables.size());
                        if (selectpostion == position) { //当前点击的和上次点击的位置一样，不做处理
//                            myViewHolder.check.setVisibility(View.GONE);
                        } else {
                            //写入数据库
                            writeShared(position);
                            notifyItemChanged(selectpostion);
                            selectpostion = readShared();
                            LogUtils.d(TAG, " 图片背景选择 selectpostion" + selectpostion);
                            //xuhao
                            myViewHolder.check.setImageResource(R.drawable.check_correct);
                            myViewHolder.check.setVisibility(View.VISIBLE);
                            if (wallPaperOnCallBack != null) {
                                wallPaperOnCallBack.WallPaperLocalChange(drawables.get(position));
                            }
                        }
                    } else {
                        LogUtils.d(TAG, " 图片背景选择 position" + position + " drawables.size " + drawables.size());
                        // 打开文件管理器选择图片
                        startExplorer();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        myViewHolder.rl_item.setOnHoverListener(this);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {//View可见时调用
        super.onViewAttachedToWindow(holder);
        // 可以在这里做一些只需要在可见时才执行的操作
        int position = holder.getAdapterPosition();
        LogUtils.d(TAG, " onViewAttachedToWindow " + position);
        if (position < drawables.size() - 1) {
            holder.icon_card.setCardBackgroundColor(Color.parseColor("#00000000"));
            LogUtils.d(TAG, " 添加View i " + position);
            loadAndSetBackground(position, holder);
        } else {
            LogUtils.d(TAG, " 添加最后一个View " + position);
//            myViewHolder.icon.setImageBitmap(whiteBitmap);
            holder.icon_card.setCardBackgroundColor(Color.parseColor("#00000000"));
            Object object = drawables.get(position);
            Drawable drawable = (Drawable) object;
            holder.icon.setImageDrawable(drawable);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) { //View不可见时调用
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) { //ViewHolder被复用时调用
        super.onViewRecycled(holder);
    }

    private void loadAndSetBackground(int i, MyViewHolder myViewHolder) {
        LogUtils.d(TAG, "loadAndSetBackground");
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Object object = drawables.get(i);
                if (object instanceof Drawable) {
                    Drawable drawable = (Drawable) object;
                    Bitmap bitmap = compressBitmapByDrawable(drawable);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 如果 i 在可见范围内
                            myViewHolder.icon.setImageBitmap(bitmap);
                            LogUtils.d(TAG, "loadAndSetBackground object instanceof Drawable " + i);
                        }
                    });
                } else if (object instanceof Integer) {
                    // 处理 Integer 类型的情况
                    Integer intValue = (Integer) object;
//                    // 使用 intValue
                    Bitmap bitmap = compressBitmapById(intValue);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            myViewHolder.icon.setImageBitmap(bitmap);
                            LogUtils.d(TAG, "loadAndSetBackground object instanceof Integer " + i);
                        }
                    });

                } else if (object instanceof String) {
                    LogUtils.d(TAG, "loadAndSetBackground object instanceof String");
                    // 处理 String 类型的情况
                    String filePath = (String) object;
                    Bitmap bitmap = compressBitmapByPath(filePath);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            myViewHolder.icon.setImageBitmap(bitmap);
                            LogUtils.d(TAG, "loadAndSetBackground object instanceof String " + i);
                        }
                    });
                } else {
                    // 处理其他类型的情况
                }
            }
        });
    }

    private Bitmap compressBitmapByPath(String srcPath) {
        LogUtils.d(TAG, " 图片缩略图 BitmapHunter run()");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(srcPath, options);
        LogUtils.d(TAG, " 图片缩略图 图片信息 " + options.outWidth + " " + options.outHeight);
        options.inSampleSize = ImageUtils.calculateInSampleSize(options);
        LogUtils.d(TAG, " 图片缩略图 inSampleSize " + options.inSampleSize);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(srcPath, options);
    }

    private Bitmap compressBitmapById(int resId) {
        LogUtils.d(TAG, " 图片缩略图 BitmapHunter run()");
        // 获取图片的宽高
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 仅获取图片的边界信息（宽度和高度），而不实际加载图片的像素数据。
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 通过资源ID获取图片的宽高
        BitmapFactory.decodeResource(mContext.getResources(), resId, options);
        LogUtils.d(TAG, " 图片缩略图 图片信息 " + options.outWidth + " " + options.outHeight);
        // 计算缩放比例
        options.inSampleSize = ImageUtils.calculateInSampleSize(options);
        LogUtils.d(TAG, " 图片缩略图 inSampleSize " + options.inSampleSize);
        // 解码图片并返回压缩后的Bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(mContext.getResources(), resId, options);
    }

    private Bitmap compressBitmapByDrawable(Drawable drawable) {
        LogUtils.d(TAG, " 图片缩略图 BitmapHunter run()");
        // 将Drawable转换为Bitmap
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            // 如果是其他类型的Drawable，手动将其转换为Bitmap
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        // 获取图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        LogUtils.d(TAG, " 图片缩略图 图片信息 " + width + " " + height);
        // 设置压缩参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 计算缩放比例
        int sampleSize = ImageUtils.calculateInSampleSize(options);
        LogUtils.d(TAG, " 图片缩略图 inSampleSize " + sampleSize);
        // 根据缩放比例压缩Bitmap
        options.inSampleSize = sampleSize;
        return Bitmap.createScaledBitmap(bitmap, width / sampleSize, height / sampleSize, true);
    }


    @Override
    public int getItemCount() {
        LogUtils.d(TAG, "getItemCount ");
        return drawables.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView icon_card;
        ImageView icon;
        TextView status;
        FrameLayout rl_item;

        ImageView check;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            icon_card = itemView.findViewById(R.id.icon_card);
            icon = itemView.findViewById(R.id.icon);
            check = itemView.findViewById(R.id.check);
            status = itemView.findViewById(R.id.status);
            rl_item = itemView.findViewById(R.id.rl_item);
        }
    }


    public interface WallPaperOnCallBack {
        void WallPaperLocalChange(Object drawable);

//        void WallPaperUsbChange(File file);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
    }


    public void writeShared(int postion) {
        SharedPreferences sharedPreferences = ShareUtil.getInstans(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Contants.SelectWallpaperLocal, postion);
        editor.apply();
    }

    private int readShared() {
        SharedPreferences sharedPreferences = ShareUtil.getInstans(mContext);
        return sharedPreferences.getInt(Contants.SelectWallpaperLocal, -1); // -1 是默认值，当没有找到该键时返回
    }

    // 初始化缓存
    public void initCache() {
        // 初始化缓存，设置最大缓存大小为当前可用内存的1/8
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        if (drawableCache == null) {
            drawableCache = new LruCache<Integer, BitmapDrawable>(cacheSize) {
                @Override
                protected int sizeOf(Integer key, BitmapDrawable value) {
                    // 缓存大小以KB为单位
                    return value.getBitmap().getByteCount() / 1024;
                }
            };
        }
    }

    private void startExplorer() {
        LogUtils.d(TAG, " startExplorer打开文件管理器");
        // 定义目标应用的包名
        String packageName = "com.hisilicon.explorer";
        // 检查系统中是否安装了这个应用
        PackageManager packageManager = mContext.getPackageManager();
        try {
            // 尝试获取该包名的信息，如果找不到则会抛出异常
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            // 如果应用已安装，创建 Intent
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setComponent(new ComponentName(packageName, "com.hisilicon.explorer.activity.MainExplorerActivity"));
            intent.setAction(Intent.ACTION_MAIN);
            // 创建一个 Bundle 并添加数据
            Bundle bundle = new Bundle();
            bundle.putBoolean("wallpaper", true);  // 传递布尔值
            // 将 Bundle 添加到 Intent 中
            intent.putExtras(bundle);
            // 启动应用
            mContext.startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
            // 如果没有安装这个应用，处理异常
            LogUtils.d(TAG, "应用未安装");
            Toast.makeText(mContext, "未找到com.hisilicon.explorer应用", Toast.LENGTH_SHORT).show();
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
