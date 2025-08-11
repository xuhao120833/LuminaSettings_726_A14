package com.htc.luminaos.adapter;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htc.luminaos.R;
import com.htc.luminaos.entry.AppInfoBean;
import com.htc.luminaos.utils.AppUtils;
import com.htc.luminaos.utils.ScrollUtils;
import com.htc.luminaos.widget.AppDetailDialog;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:
 * Date:
 * Description:
 */
public class AppsManagerAdapter extends RecyclerView.Adapter<AppsManagerAdapter.MyViewHolder> implements View.OnHoverListener {

    Context mContext;
    RecyclerView recyclerView;
    List<AppInfoBean> infoBeans;
    private PackageManager mPm;

    public AppsManagerAdapter(Context mContext,List<AppInfoBean> infoBeans, RecyclerView recyclerView) {
        this.mContext = mContext;
        this.infoBeans = infoBeans;
        this.recyclerView = recyclerView;
        this.mPm = mContext.getPackageManager();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_settings_item, null));
        myViewHolder.setIsRecyclable(false);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        final AppInfoBean info = infoBeans.get(i);
        myViewHolder.icon.setImageDrawable(info.getAppicon());
        myViewHolder.name.setText(info.getAppname());
//        if(i==0) {
//            myViewHolder.rl_item.requestFocus();
//        }
        myViewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppDetailDialog detailDialog = new AppDetailDialog(mContext,R.style.DialogTheme);
                detailDialog.setData(info.getApplicationInfo());
                detailDialog.setOnClickCallBack(new AppDetailDialog.OnAppDetailCallBack() {
                    @Override
                    public void onClear_cache(String packageName) {
                        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                        activityManager.clearApplicationUserData(packageName,null);
                    }

                    @Override
                    public void onUninstall(String packageName) {

                        boolean[] result = AppUtils.checkIfSystemAppAndCanUninstall(mContext, info.getApplicationInfo().packageName);
                        if (result[0] && !result[1]) {
                            AlertDialog dialog =new AlertDialog.Builder(mContext)
                                    .setTitle(mContext.getString(R.string.hint)) // 对话框标题
                                    .setMessage(mContext.getString(R.string.system_app_cannot_uninstalled)) // 对话框内容
                                    .setPositiveButton(mContext.getString(R.string.enter), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss(); // 点击“确定”按钮时，关闭对话框
                                        }
                                    })
                                    .setCancelable(false) // 使对话框不能通过点击外部区域关闭
                                    .create();

                            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                @Override
                                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                                        dialog.dismiss(); // 按下返回键时关闭对话框
                                        return true; // 表示已经处理了返回键事件
                                    }
                                    return false; // 没有处理返回键事件，继续传递事件
                                }
                            });

                            dialog.show();
                            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            if (positiveButton != null) {
                                positiveButton.setBackgroundColor(Color.parseColor("#4684f3")); // #80 是 50% 透明度，后面是黑色
                                positiveButton.setTextColor(Color.WHITE); // 白色文字，方便看
                                positiveButton.setPadding(40, 20, 40, 20); // 给按钮留点内边距
                            }
                            return;
                        }

                        try {
                            Intent intent=new Intent();
                            Uri uri = Uri.parse("package:"+packageName);
                            //获取删除包名的URI
                            intent.setAction(Intent.ACTION_DELETE);
                            //设置我们要执行的卸载动作
                            intent.setData(uri);
                            //设置获取到的
                            mContext.startActivity(intent);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                });
                detailDialog.show();
            }
        });
        myViewHolder.rl_item.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (recyclerView==null)
                    return;

                if(b){
                    int[] amount = ScrollUtils.getScrollAmount(recyclerView, view);//计算需要滑动的距离
                    recyclerView.smoothScrollBy(amount[0], amount[1]);
                }
            }
        });

        myViewHolder.rl_item.setOnHoverListener(this);
    }

    @Override
    public int getItemCount() {
        return infoBeans.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView icon;
        RelativeLayout rl_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.app_name);
            rl_item = itemView.findViewById(R.id.rl_item);
            icon = itemView.findViewById(R.id.app_icon);
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