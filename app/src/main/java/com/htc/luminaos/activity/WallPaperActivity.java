package com.htc.luminaos.activity;

import static com.htc.luminaos.utils.BlurImageView.MAX_BITMAP_SIZE;
import static com.htc.luminaos.utils.BlurImageView.narrowBitmap;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.htc.luminaos.MyApplication;
import com.htc.luminaos.R;
import com.htc.luminaos.adapter.WallPaperAdapter;
import com.htc.luminaos.databinding.ActivityWallpaperCustomBinding;
import com.htc.luminaos.utils.Contants;
import com.htc.luminaos.utils.DialogUtils;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.Utils;
import com.htc.luminaos.widget.FocusKeepRecyclerView;
import com.htc.luminaos.widget.SpacesItemDecoration;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WallPaperActivity extends BaseActivity {

    private ActivityWallpaperCustomBinding wallPaperBinding;
    private ArrayList<File> file_toArray = new ArrayList<>();

    ExecutorService singer = Executors.newSingleThreadExecutor();
    ExecutorService threadExecutor = Executors.newFixedThreadPool(5);

    private Dialog switchDialog = null;

    long curTime = 0;
    private static String TAG = "WallPaperActivity";

    //    private static TimerManager timerManager = null;
    private MyApplication myApplication;
    private Dialog loadingDialog;


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case Contants.PICTURE_NULL:
                    wallPaperBinding.folderResult.setBackgroundResource(R.drawable.folder_x);
                    break;
                case Contants.PICTURE_RESULT:
                    if (msg.obj != null) {
                        File[] files = (File[]) msg.obj;
                        WallPaperAdapter wallPaperAdapter = new WallPaperAdapter(WallPaperActivity.this, files, handler);
                        wallPaperAdapter.setWallPaperOnCallBack(onCallBack);
                        wallPaperBinding.wallpaperRv.setAdapter(wallPaperAdapter);
                    }
                    wallPaperBinding.folderResult.setVisibility(View.GONE);
                    wallPaperBinding.wallpaperRv.setVisibility(View.VISIBLE);
                    break;
                case Contants.PICTURE_FIND:
                    wallPaperBinding.folderResult.setVisibility(View.VISIBLE);
                    wallPaperBinding.folderResult.setBackgroundResource(R.drawable.folder);
                    wallPaperBinding.wallpaperRv.setVisibility(View.GONE);
                    break;
                case Contants.DISSMISS_DIALOG:
                    if (switchDialog != null && switchDialog.isShowing())
                        switchDialog.dismiss();
                    setWallPaper();
                    break;
                case Contants.RESET_CHECK:
                    int receivedPosition = msg.arg1;
                    FocusKeepRecyclerView.ViewHolder viewHolder = wallPaperBinding.wallpaperRv.findViewHolderForAdapterPosition(receivedPosition);
                    if (viewHolder != null) {
                        Log.d(TAG, " 图片背景选择 receivedPosition" + receivedPosition);
                        View itemView = viewHolder.itemView;
                        ImageView check = itemView.findViewById(R.id.check);
                        check.setImageResource(R.drawable.check_no);
                        check.setVisibility(View.GONE);
                    }
                    break;
            }
            return false;
        }
    });

    BroadcastReceiver mediaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d(TAG, "aciton " + intent.getAction());
            if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())
                    || Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())
                    || Intent.ACTION_MEDIA_BAD_REMOVAL.equals(intent.getAction())
                    || Intent.ACTION_MEDIA_REMOVED.equals(intent.getAction())) {
                if (System.currentTimeMillis() - curTime < 300)
                    return;

                curTime = System.currentTimeMillis();
                StorageVolume storage = (StorageVolume) intent.getParcelableExtra(
                        StorageVolume.EXTRA_STORAGE_VOLUME);
                String path = storage.getPath();
//                if (isExternalStoragePath(path)) {
//                    if (wallPaperBinding.usbItem.isSelected())
//                        loadUSB();
//                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            wallPaperBinding = ActivityWallpaperCustomBinding.inflate(LayoutInflater.from(this));
            setContentView(wallPaperBinding.getRoot());
//            observeLiveData();
            initView();
            getPath();
            initData();
            initFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, " 执行onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void observeLiveData() {
        myApplication = (MyApplication) getApplication();
        if (myApplication != null) {
//            MutableLiveData<Boolean> mutableLiveData = myApplication.getIsDataInitialized();
            Boolean isInitialized = myApplication.getIsDataInitialized().getValue();
            if (!isInitialized) {
                Log.d(TAG, " 背景资源还在加载中");
                //显示动画
                showLottieLoading();
                //监听LiveData
                myApplication.getIsDataInitialized().observe(this, isInitializedValue -> {
                    if (isInitializedValue != null && isInitializedValue) {
                        WallPaperAdapter adapter = (WallPaperAdapter) wallPaperBinding.wallpaperRv.getAdapter();
                        adapter.notifyDataSetChanged();
                        loadingDialog.dismiss();
                    }
                });
            } else if (isInitialized) {
                Log.d(TAG, " 背景资源已经加载完成");
            }
        }
    }

    private void initView() {
//        wallPaperBinding.localItem.setOnClickListener(this);
//        wallPaperBinding.usbItem.setOnClickListener(this);
//        GridLayoutManager layoutManager = new GridLayoutManager(this,6);//原生是6列
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        wallPaperBinding.wallpaperRv.setLayoutManager(layoutManager);
        wallPaperBinding.wallpaperRv.addItemDecoration(new SpacesItemDecoration(SpacesItemDecoration.pxAdapter(22.5F), SpacesItemDecoration.pxAdapter(22.5F), SpacesItemDecoration.pxAdapter(10), SpacesItemDecoration.pxAdapter(10)));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addDataScheme("file");
        registerReceiver(mediaReceiver, intentFilter);
    }

    private void initData() {
        loadLocal();
    }

    private void initFocus() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String path = bundle.getString("filePath");
            Log.d(TAG, " 接收到路径 " + path);
            if (path != null && !path.isEmpty()) {
                FocusKeepRecyclerView wallpaperRv = wallPaperBinding.wallpaperRv;
                RecyclerView.Adapter adapter = wallpaperRv.getAdapter();
                if (adapter != null) {
                    int position = adapter.getItemCount() - 2; // 倒数第二个项的位置
                    wallpaperRv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            // 确保 RecyclerView 已完成布局
                            Log.d(TAG, "RecyclerView 已完成布局");
                            // 获取目标项的 ViewHolder
                            RecyclerView.ViewHolder viewHolder = wallpaperRv.findViewHolderForAdapterPosition(position);
                            if (viewHolder != null) {//不需要滚动的
                                Log.d(TAG, "找到目标项，设置焦点");
                                viewHolder.itemView.requestFocus();
                                viewHolder.itemView.performClick();
                            } else {//需要滚动的
                                wallpaperRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                        // 检查滚动是否结束
                                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                            RecyclerView.ViewHolder scrolledViewHolder = wallpaperRv.findViewHolderForAdapterPosition(position);
                                            if (scrolledViewHolder != null) {
                                                Log.e(TAG, "滚动后切背景");
                                                scrolledViewHolder.itemView.requestFocus();
                                                scrolledViewHolder.itemView.performClick();
                                            } else {
                                                Log.e(TAG, "无法找到指定位置的 ViewHolder");
                                            }
                                            // 滚动完成后移除监听器，避免重复调用
                                            wallpaperRv.removeOnScrollListener(this);
                                        }
                                    }
                                });
                                wallpaperRv.smoothScrollToPosition(position);
                                Log.e(TAG, "目标项不可见，无法设置焦点");
                            }
                            // 移除监听器，避免多次调用
                            wallpaperRv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
                }
            }
        }
    }

    private void loadLocal() {
        WallPaperAdapter wallPaperAdapter = new WallPaperAdapter(getApplicationContext(), Utils.drawables, handler, wallPaperBinding.wallpaperRv);
        wallPaperAdapter.setWallPaperOnCallBack(onCallBack);
        wallPaperBinding.wallpaperRv.setAdapter(wallPaperAdapter);
        wallPaperBinding.wallpaperRv.setVisibility(View.VISIBLE);
        wallPaperBinding.folderResult.setVisibility(View.GONE);
    }

    public boolean isExternalStoragePath(String path) {
        if (path.equals("/storage/emulated/0")) {
            return false;
        }
        return true;
    }

    WallPaperAdapter.WallPaperOnCallBack onCallBack = new WallPaperAdapter.WallPaperOnCallBack() {
//        @Override
//        public void WallPaperUsbChange(File file) {
//
//        }

        @Override
        public void WallPaperLocalChange(Object object) {
            switchDialog = DialogUtils.createLoadingDialog(WallPaperActivity.this, getString(R.string.switch_wallpaper_tips));
            switchDialog.show();
            threadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (object instanceof Drawable) {
                        CopyDrawableToSd((Drawable) object);
                    } else if (object instanceof Integer) {
                        CopyResIdToSd((int) object);
                    } else if (object instanceof String) {
                        CopyFileToSd((String) object);
                    }
                }
            });
        }
    };

//    private void CopyResIdToSd(int resId) {
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
//        //判断图片大小，如果超过限制就做缩小处理
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        if (width * height * 6 >= MAX_BITMAP_SIZE) {
//            bitmap = narrowBitmap(bitmap);
//        }
//        MyApplication.mainDrawable = new BitmapDrawable(bitmap);
//        handler.sendEmptyMessage(Contants.DISSMISS_DIALOG);
//        File dir = new File(Contants.WALLPAPER_DIR);
//        if (!dir.exists()) {
//            dir.mkdirs(); // 创建文件夹
//        }
//        File tempFile = new File(Contants.WALLPAPER_MAIN);
//        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//            // 使用 Bitmap.compress 压缩数据，直接将数据写入文件
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            Log.d(TAG, "文件拷贝成功: " + tempFile.getAbsolutePath());
//            fos.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e(TAG, "文件拷贝失败: " + e.getMessage());
//        } finally {
//            // 确保资源释放
//            System.gc(); // 提醒 JVM 执行垃圾回收
//            Log.d(TAG, "内存和 CPU 资源已释放");
//        }
//    }

    private void CopyResIdToSd(int resId) {
        // 获取图片资源的输入流
        InputStream inputStream = getResources().openRawResource(resId);
        // 创建目标文件夹
        File dir = new File(Contants.WALLPAPER_DIR);
        if (!dir.exists()) {
            dir.mkdirs(); // 创建文件夹
        }
        // 创建目标文件
        File tempFile = new File(Contants.WALLPAPER_MAIN);
        try (FileOutputStream fos = new FileOutputStream(tempFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            // 将输入流中的数据直接写入文件
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            bos.flush(); // 刷新缓冲区
            Log.d(TAG, "文件拷贝成功: " + tempFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "文件拷贝失败: " + e.getMessage());
        } finally {
            try {
                inputStream.close(); // 关闭输入流
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 确保资源释放
            System.gc(); // 提醒 JVM 执行垃圾回收
            Log.d(TAG, "内存和 CPU 资源已释放");
        }
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
        //判断图片大小，如果超过限制就做缩小处理
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width * height * 6 >= MAX_BITMAP_SIZE) {
            bitmap = narrowBitmap(bitmap);
        }
        MyApplication.mainDrawable = new BitmapDrawable(bitmap);
        handler.sendEmptyMessage(Contants.DISSMISS_DIALOG);
    }

    /**
     * 将 Bitmap 转换为 FileChannel，供 transferTo 使用
     */
    private FileChannel bitmapToChannel(Bitmap bitmap) throws IOException {
        // 创建临时文件
        File tempFile = File.createTempFile("bitmap_", ".tmp", getApplicationContext().getCacheDir());
        tempFile.deleteOnExit();
        try (FileOutputStream fos = new FileOutputStream(tempFile);
             FileChannel channel = fos.getChannel()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return channel; // 返回文件通道
        }
    }

    private void CopyDrawableToSd(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        //判断图片大小，如果超过限制就做缩小处理
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width * height * 6 >= MAX_BITMAP_SIZE) {
            bitmap = narrowBitmap(bitmap);
        }
        //缩小完毕
        MyApplication.mainDrawable = new BitmapDrawable(bitmap);
        handler.sendEmptyMessage(Contants.DISSMISS_DIALOG);
        File dir = new File(Contants.WALLPAPER_DIR);
        if (!dir.exists()) {
            dir.mkdirs(); // 创建文件夹
        }
        File tempFile = new File(Contants.WALLPAPER_MAIN);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            // 使用 Bitmap.compress 压缩数据，直接将数据写入文件
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Log.d(TAG, "文件拷贝成功: " + tempFile.getAbsolutePath());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "文件拷贝失败: " + e.getMessage());
        } finally {
            // 确保资源释放
            System.gc(); // 提醒 JVM 执行垃圾回收
            Log.d(TAG, "内存和 CPU 资源已释放");
        }
    }

//    private void CopyBitmapToSd(Bitmap bitmap) {
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        //判断图片大小，如果超过限制就做缩小处理
//        if (width * height * 4 >= MAX_BITMAP_SIZE) {
//            bitmap = narrowBitmap(bitmap);
//        }
//        //缩小完毕
//        File file1 = new File(Contants.WALLPAPER_DIR);
//        if (!file1.exists())
//            file1.mkdir();
//
//        File file = new File(Contants.WALLPAPER_MAIN);//将要保存图片的路径
//        if (file.exists())
//            file.delete();
//        try {
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
//            bos.flush();
//            bos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    private void CopyFileToSd(String path) {
//        Bitmap bitmap = BitmapFactory.decodeFile(path);
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        //判断图片大小，如果超过限制就做缩小处理
//        if (width * height * 6 >= MAX_BITMAP_SIZE) {
//            bitmap = narrowBitmap(bitmap);
//        }
//        MyApplication.mainDrawable = new BitmapDrawable(bitmap);
//        handler.sendEmptyMessage(Contants.DISSMISS_DIALOG);
//        File dir = new File(Contants.WALLPAPER_DIR);
//        if (!dir.exists()) {
//            dir.mkdirs(); // 创建文件夹
//        }
//        File tempFile = new File(Contants.WALLPAPER_MAIN);
//        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//            // 使用 Bitmap.compress 压缩数据，直接将数据写入文件
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            Log.d(TAG, "文件拷贝成功: " + tempFile.getAbsolutePath());
//            fos.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e(TAG, "文件拷贝失败: " + e.getMessage());
//        } finally {
//            // 确保资源释放
//            System.gc(); // 提醒 JVM 执行垃圾回收
//            Log.d(TAG, "内存和 CPU 资源已释放");
//        }
//    }

    private void CopyFileToSd(String path) {
        // 创建目标文件夹
        File dir = new File(Contants.WALLPAPER_DIR);
        if (!dir.exists()) {
            dir.mkdirs(); // 创建文件夹
        }
        // 创建目标文件
        File tempFile = new File(Contants.WALLPAPER_MAIN);
        try (FileInputStream fis = new FileInputStream(path);
             FileOutputStream fos = new FileOutputStream(tempFile);
             BufferedInputStream bis = new BufferedInputStream(fis);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            // 读取源文件并写入目标文件
            byte[] buffer = new byte[1024];
            int length;
            while ((length = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            bos.flush();  // 刷新缓冲区
            Log.d(TAG, "文件拷贝成功: " + tempFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "文件拷贝失败: " + e.getMessage());
        } finally {
            // 确保资源释放
            System.gc(); // 提醒 JVM 执行垃圾回收
            Log.d(TAG, "内存和 CPU 资源已释放");
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //判断图片大小，如果超过限制就做缩小处理
        if (width * height * 6 >= MAX_BITMAP_SIZE) {
            bitmap = narrowBitmap(bitmap);
        }
        MyApplication.mainDrawable = new BitmapDrawable(bitmap);
        handler.sendEmptyMessage(Contants.DISSMISS_DIALOG);
    }

    public FileFilter pictureFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            // TODO Auto-generated method stub
            // keep all needed files
            try {
                if (pathname.isDirectory()) {
                    /*filesNum++;
                    return true;*/
                    File[] files = pathname.listFiles(pictureFilter);
                    if (files != null && files.length > 0)
                        file_toArray.addAll(Arrays.asList(files));
                }
            } catch (SecurityException e) {
                e.printStackTrace();
                return false;
            }
            String name = pathname.getAbsolutePath();
            if (isPictureFile(name)) {
                return true;
            }
            return false;
        }
    };


    public boolean isPictureFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpeg")
                    || ext.equalsIgnoreCase("jpg")
                    || ext.equalsIgnoreCase("bmp") || ext.equalsIgnoreCase("jfif")
                    || ext.equalsIgnoreCase("tiff") || ext.equalsIgnoreCase("webp")) {
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mediaReceiver);
        if (!singer.isShutdown()) {
            singer.shutdown();
            singer.shutdownNow();
        }
        if (!threadExecutor.isShutdown()) {
            threadExecutor.shutdown();
            threadExecutor.shutdownNow();
        }
        super.onDestroy();
    }

    private void getPath() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String path = bundle.getString("filePath");
            Log.d(TAG, " 接收到路径 " + path);
            String copypath = copyFileToWallpaperFolder(path);
//            Utils.drawables.remove(Utils.drawables.size() - 1);
            //插入倒数第二个
            Utils.drawables.add(Utils.drawables.size() - 1, copypath);
//            // 添加新的路径
//            Utils.drawables.add(copypath);
        }
    }

//    public String copyFileToWallpaperFolder(String sourcePath) {
//        // 目标文件夹路径
//        String targetDirPath = Environment.getExternalStorageDirectory() + "/.mywallpaper/";
//        File targetDir = new File(targetDirPath);
//        // 检查并创建目标文件夹
//        if (!targetDir.exists() && !targetDir.mkdirs()) {
//            Log.e(TAG, "无法创建目标文件夹: " + targetDirPath);
//            return "-1";
//        }
//        Log.d(TAG, "目标文件夹已存在或创建成功: " + targetDirPath);
//        // 创建目标文件对象（保持与源文件相同的文件名）
//        File sourceFile = new File(sourcePath);
//        File targetFile = new File(targetDir, sourceFile.getName());
//        // 拷贝文件
//        try (FileInputStream fis = new FileInputStream(sourceFile);
//             FileOutputStream fos = new FileOutputStream(targetFile)) {
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = fis.read(buffer)) > 0) {
//                fos.write(buffer, 0, length);
//            }
//            fis.close();
//            fos.close();
//            Log.d(TAG, "文件拷贝成功: " + targetFile.getAbsolutePath());
//            return targetFile.getAbsolutePath();
//        } catch (IOException e) {
//            Log.e(TAG, "文件拷贝失败: " + e.getMessage());
//            return "-1";
//        }
//    }

    public String copyFileToWallpaperFolder(String sourcePath) {
        // 目标文件夹路径
        String targetDirPath = "/data/user_de/0/com.htc.luminaos/files/.mywallpaper";
        File targetDir = new File(targetDirPath);
        // 检查并创建目标文件夹
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            Log.e(TAG, "无法创建目标文件夹: " + targetDirPath);
            return "-1";
        }
        Log.d(TAG, "目标文件夹已存在或创建成功: " + targetDirPath);
        // 创建目标文件对象（保持与源文件相同的文件名）
        File sourceFile = new File(sourcePath);
        File targetFile = new File(targetDir, sourceFile.getName());
        // 使用 FileChannel 进行高效的文件拷贝
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(targetFile);
             FileChannel srcChannel = fis.getChannel();
             FileChannel destChannel = fos.getChannel()) {
            // 使用 transferTo 方法进行高效拷贝
            long size = srcChannel.size();
            long transferred = 0;
            while (transferred < size) {
                transferred += srcChannel.transferTo(transferred, size - transferred, destChannel);
            }
            Log.d(TAG, "文件拷贝成功: " + targetFile.getAbsolutePath());
            return targetFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "文件拷贝失败: " + e.getMessage());
            return "-1";
        } finally {
            // 确保所有资源被释放
            System.gc(); // 提醒 JVM 执行垃圾回收
            Log.d(TAG, "内存和 CPU 资源已释放");
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void showLottieLoading() {
        // 创建 Dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.wapper_load);
        loadingDialog.setCancelable(false); // 禁用点击外部关闭
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); // 透明背景
//        LottieAnimationView lottieLoadingView = loadingDialog.findViewById(R.id.loadingAnimation);
//        if (lottieLoadingView != null) {
//            lottieLoadingView.setSpeed(2.0f); // 设置为 2 倍速播放
//        }
        // 显示 Dialog
        loadingDialog.show();
    }

}