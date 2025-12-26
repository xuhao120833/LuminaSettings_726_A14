package com.htc.luminasettings.adapter;

import android.content.Context;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htc.luminasettings.R;
import com.htc.luminasettings.entry.InputMethodBean;
import com.htc.luminasettings.utils.LogUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:
 * Date:
 * Description:
 */
public class InputMethodAdapter extends RecyclerView.Adapter<InputMethodAdapter.MyViewHolder> implements View.OnHoverListener{

    private Context mContext;
    private ArrayList<InputMethodBean> marray=null;
    private LayoutInflater mInflater;
    private int currentPosition=-1;
    private static String TAG = "InputMethodAdapter";

    public void setCurrentPosition(int position){
        if(position>=0&&position<marray.size()){
            this.currentPosition=position;
        }
    }

    public InputMethodAdapter(Context context, ArrayList<InputMethodBean> array){
        this.mContext=context;
        this.marray=array;
        this.mInflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.language_item,null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        final InputMethodBean bean = marray.get(i);
        myViewHolder.inputMethodName.setText(bean.getInputname());
        if (currentPosition == i){
            myViewHolder.status.setVisibility(View.VISIBLE);
            myViewHolder.rl_item.requestFocus();
        }else{
            myViewHolder.status.setVisibility(View.GONE);
        }

        myViewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.Secure.putString(mContext.getContentResolver(),
                        Settings.Secure.DEFAULT_INPUT_METHOD,
                        bean.getPrefkey());
                LogUtils.d(TAG," bean.getPrefkey() "+bean.getPrefkey());
                setCurrentPosition(i);
                notifyDataSetChanged();
            }
        });
        myViewHolder.rl_item.setOnHoverListener(this);
    }

    @Override
    public int getItemCount() {
        return marray.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView inputMethodName;
        ImageView status;
        RelativeLayout rl_item;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            inputMethodName = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);
            rl_item = itemView.findViewById(R.id.rl_item);
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
