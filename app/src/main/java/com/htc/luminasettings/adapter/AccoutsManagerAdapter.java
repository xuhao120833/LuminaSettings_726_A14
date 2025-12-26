package com.htc.luminasettings.adapter;

import android.accounts.Account;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.htc.luminasettings.R;
import com.htc.luminasettings.utils.ScrollUtils;
import com.htc.luminasettings.widget.AccountDeleteDialog;

import java.util.List;

/**
 * Author:
 * Date:
 * Description:
 */
public class AccoutsManagerAdapter extends RecyclerView.Adapter<AccoutsManagerAdapter.MyViewHolder> implements View.OnHoverListener ,View.OnClickListener  {

    Context mContext;
    RecyclerView recyclerView;
    List<Account> accounts;
    private PackageManager mPm;
    UserHandle mUserHandle;
    AccountDeleteDialog accountDeleteDialog;
    private static String TAG = "AccoutsManagerAdapter";

    public AccoutsManagerAdapter(Context mContext, List<Account> accounts, RecyclerView recyclerView, UserHandle mUserHandle) {
        this.mContext = mContext;
        this.accounts = accounts;
        this.recyclerView = recyclerView;
        this.mPm = mContext.getPackageManager();
        this.mUserHandle = mUserHandle;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.account_settings_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.icon.setImageResource(R.drawable.account5);
        myViewHolder.name.setText(accounts.get(i).name);
        myViewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        myViewHolder.rl_item.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (recyclerView==null)
                    return;
                ImageView appIcon = myViewHolder.rl_item.findViewById(R.id.app_icon);
                if(b){
                    int[] amount = ScrollUtils.getScrollAmount(recyclerView, view);//计算需要滑动的距离
                    recyclerView.smoothScrollBy(amount[0], amount[1]);
                    appIcon.setImageResource(R.drawable.account6);
                } else {
                    appIcon.setImageResource(R.drawable.account5);
                }
            }
        });

        myViewHolder.rl_item.setOnHoverListener(this);

        myViewHolder.rl_item.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return accounts.size();
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

    @Override
    public void onClick(View v) {
        if (recyclerView != null) {
            int position = recyclerView.getChildAdapterPosition(v);
            if (position != RecyclerView.NO_POSITION) {
                // position 有效，执行操作
                accountDeleteDialog = new AccountDeleteDialog(mContext,accounts.get(position),mUserHandle);
                accountDeleteDialog.show();
            }
        }
    }
}