package com.htc.luminasettings.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.htc.luminasettings.R;
import com.htc.luminasettings.activity.AppFavoritesActivity;
import com.htc.luminasettings.entry.ShortInfoBean;
import com.htc.luminasettings.utils.AppUtils;
import com.htc.luminasettings.utils.DBUtils;
import com.htc.luminasettings.utils.LogUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:
 * Date:
 * Description:
 */
public class ShortcutsAdapter extends RecyclerView.Adapter<ShortcutsAdapter.MyViewHolder> {

    Context mContext;
    private ArrayList<ShortInfoBean> short_list;
    ItemCallBack itemCallBack;
    private static String TAG = "ShortcutsAdapter";

    public ShortcutsAdapter(Context mContext,ArrayList<ShortInfoBean> short_list) {
        this.mContext = mContext;
        this.short_list = short_list;
    }

    public void setItemCallBack(ItemCallBack itemCallBack) {
        this.itemCallBack = itemCallBack;
    }

    public ItemCallBack getItemCallBack() {
        return itemCallBack;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shortcuts_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        LogUtils.d(TAG, "Shortcuts short_list.size() "+short_list.size());
        if (i<short_list.size() && short_list.get(i).getAppname()!=null){
            LogUtils.d(TAG, "Shortcuts appName存在 ");
            myViewHolder.icon.setBackground(short_list.get(i).getAppicon());
            myViewHolder.name.setText(short_list.get(i).getAppname());
        } else if (i<short_list.size()) {
            LogUtils.d(TAG, "Shortcuts appName为NULL ");
            String appName = DBUtils.getInstance(mContext).getFavoritesAppName(short_list.get(i).getPackageName());
            Drawable drawable = DBUtils.getInstance(mContext).getFavoritesIcon(short_list.get(i).getPackageName());
            if(appName!=null) {
                myViewHolder.name.setText(appName);
            }else {
                myViewHolder.name.setText(getAppName(short_list.get(i).getPackageName()));
            }
            if(drawable!=null) {
                myViewHolder.icon.setBackground(drawable);
            }else {
                myViewHolder.icon.setBackgroundResource(getAppIcon(short_list.get(i).getPackageName()));
            }
        }

        myViewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCallBack!=null)
                    itemCallBack.onItemClick(i);
            }
        });
        myViewHolder.rl_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AppUtils.startNewActivity(mContext, AppFavoritesActivity.class);
                return true;
            }
        });
    }


    private String getAppName(String pkg){
        switch (pkg){
            case "com.netflix.ninja":
                return "Netflix";
            case "com.disney.disneyplus":
                return "Disney+";
            case "com.google.android.youtube.tv":
                return "Youtube";
            case "com.chrome.beta":
                return "Chrome";
            case "com.amazon.avod.thirdpartyclient":
                return "Prime Video";
            case "net.cj.cjhv.gs.tving":
                return "TVING";
            case "com.wbd.stream":
                return "HBO Max";
            case "com.frograms.wplay":
                return "WATCHA";
            case "in.startv.hotstar.dplus":
                return "Hotstar";
            case "com.jio.media.ondemand":
                return "JioCinema";
            case "com.hulu.plus":
//                jp.happyon.android
                return "Hulu";
            case "tv.abema":
                return "ABEMA";
            default:
                return "APK";
        }
    }

    private int getAppIcon(String pkg){
        switch (pkg){
            case "com.netflix.ninja":
                return R.drawable.netflix;
            case "com.disney.disneyplus":
                return R.drawable.disney;
            case "com.google.android.youtube.tv":
                return R.drawable.youtube;
            case "com.chrome.beta":
                return R.drawable.chrome;
            case "com.amazon.avod.thirdpartyclient":
                return R.drawable.primevideo;
            case "net.cj.cjhv.gs.tving":
                return R.drawable.tving;
            case "com.wbd.stream":
                return R.drawable.max;
            case "com.frograms.wplay":
                return R.drawable.watcha;
            case "in.startv.hotstar.dplus":
                return R.drawable.hotstar;
            case "com.jio.media.ondemand":
                return R.drawable.jio_cinema;
            case "com.hulu.plus":
                return R.drawable.hulu;
            case "tv.abema":
                return R.drawable.abema;
            default:
                return R.mipmap.ic_launcher_round;
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface ItemCallBack{
        void onItemClick(int i);
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView icon;
        RelativeLayout rl_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            rl_item = itemView.findViewById(R.id.rl_item);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
