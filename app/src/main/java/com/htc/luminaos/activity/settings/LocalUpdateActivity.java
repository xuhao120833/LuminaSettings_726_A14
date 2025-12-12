package com.htc.luminaos.activity.settings;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import com.htc.luminaos.R;
import com.htc.luminaos.activity.BaseActivity;
import com.htc.luminaos.databinding.ActivityLocalUpdateBinding;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.widget.UpgradeCheckFailDialog;
import com.htc.luminaos.widget.UpgradeCheckSuccessDialog;
import java.io.File;
import java.io.FileFilter;

public class LocalUpdateActivity extends BaseActivity {
    ActivityLocalUpdateBinding localUpdateBinding;
    private static String TAG = "LocalUpdateActivity";
    private static String OTA_PACKAGE_FILE = "update.zip";
    private static String USB_ROOT = "/mnt/media_rw";
    private static String FLASH_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    private UpgradeCheckFailDialog upgradeCheckFailDialog = null;
    private UpgradeCheckSuccessDialog upgradeCheckSuccessDialog = null;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                if (msg.obj != null) {
                    String path = (String) msg.obj;
//                    startSystemUpdate(path);
                    showUpgradeCheckSuccessDialog(path);
                } else {
                    showUpgradeCheckFailDialog();
                }
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localUpdateBinding = ActivityLocalUpdateBinding.inflate(LayoutInflater.from(this));
        setContentView(localUpdateBinding.getRoot());
        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initView() {
        localUpdateBinding.startLocalUpdate.setOnClickListener(this);
        localUpdateBinding.startLocalUpdate.setOnHoverListener(this);

        localUpdateBinding.tip1.setSelected(true);
        localUpdateBinding.tip2.setSelected(true);
        localUpdateBinding.tip3.setSelected(true);
        localUpdateBinding.tip4.setSelected(true);
        localUpdateBinding.tip5.setSelected(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.start_local_update) {
            goFindUpgradeFile();
        }
    }

    private void goFindUpgradeFile() {
        showCheckingDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = findUpdateFile();
                Message message = handler.obtainMessage();
                message.what = 1;
                message.obj = path;
                handler.sendMessageDelayed(message, 1000);//Android 14 progressDialog一闪而过，加1s延迟
            }
        }).start();
    }

    private void startSystemUpdate(String path) {
        LogUtils.d(TAG, "startSystemUpdate path" + path);
        String newpath = "";
        if (path.contains("/mnt/media_rw/")) {
            newpath = path.replaceFirst("/mnt/media_rw/", "/storage/");
            LogUtils.d(TAG, "startSystemUpdate newpath" + newpath);
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.softwinner.update", "com.softwinner.update.ui.AbUpdate"));
            Bundle bundle = new Bundle();
            bundle.putString("update_path", newpath);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.softwinner.update", "com.softwinner.update.ui.AbUpdate"));
            Bundle bundle = new Bundle();
            bundle.putString("update_path", path);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    /**
     * 拷贝文件
     */
    ProgressDialog progressDialog;

    private void showCheckingDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.checking));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void showUpgradeCheckFailDialog() {
        if (upgradeCheckFailDialog == null) {
            upgradeCheckFailDialog = new UpgradeCheckFailDialog(LocalUpdateActivity.this);
            upgradeCheckFailDialog.setOnClickCallBack(new UpgradeCheckFailDialog.OnClickCallBack() {
                @Override
                public void onRetry() {
                    goFindUpgradeFile();
                }
            });
        }

        if (!upgradeCheckFailDialog.isShowing())
            upgradeCheckFailDialog.show();
    }

    private void showUpgradeCheckSuccessDialog(String path) {
        LogUtils.d(TAG, "showUpgradeCheckSuccessDialog " + path);
        if (upgradeCheckSuccessDialog == null) {
            LogUtils.d(TAG, "new UpgradeCheckSuccessDialog " + path);
            upgradeCheckSuccessDialog = new UpgradeCheckSuccessDialog(LocalUpdateActivity.this);
            upgradeCheckSuccessDialog.setOnClickCallBack(new UpgradeCheckSuccessDialog.OnClickCallBack() {
                @Override
                public void upgrade() {
                    LogUtils.d(TAG, "upgrade path" + path);
                    startSystemUpdate(path);
                }
            });
        }

        if (!upgradeCheckSuccessDialog.isShowing())
            upgradeCheckSuccessDialog.show();
    }

    private String findUpdateFile() {
        String dataPath = FLASH_ROOT + "/" + OTA_PACKAGE_FILE;
        if (new File(dataPath).exists())
            return dataPath;
        //find usb device update package
        if (USB_ROOT == null) {
            return null;
        }
        File usbRoot = new File(USB_ROOT);
        File[] pfiles = usbRoot.listFiles();
        if (pfiles == null) {
            return null;
        }
        for (File tmp : pfiles) {
            if (tmp.isDirectory()) {
                File[] subfiles = tmp.listFiles();
                if (subfiles == null) {
                    File file = new File(tmp.getAbsolutePath().replace(USB_ROOT, "/storage"), OTA_PACKAGE_FILE);
                    if (file.exists())
                        return file.getAbsolutePath();
                    continue;
                }
                for (File subtmp : subfiles) {
                    if (subtmp.isDirectory()) {
                        File[] files = subtmp.listFiles(new FileFilter() {
                            @Override
                            public boolean accept(File arg0) {
                                if (arg0.isDirectory()) {
                                    return false;
                                }
                                if (arg0.getName().equals(OTA_PACKAGE_FILE)) {
                                    return true;
                                }
                                return false;
                            }
                        });
                        if (files != null && files.length > 0) {
                            return files[0].getAbsolutePath();
                        }
                    } else {
                        if (subtmp.getName().equals(OTA_PACKAGE_FILE)) {
                            return subtmp.getAbsolutePath();
                        } else {
                            continue;
                        }
                        //continue;
                    }
                }
            } else if (tmp.isFile()) {
                if (tmp.getName().equals(OTA_PACKAGE_FILE)) {
                    return tmp.getAbsolutePath();
                } else {
                    continue;
                }
            }
        }
        return null;
    }
}