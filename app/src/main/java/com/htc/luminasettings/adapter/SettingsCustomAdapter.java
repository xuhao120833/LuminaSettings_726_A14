package com.htc.luminasettings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.htc.luminasettings.R;

/**
 * Author:
 * Date:
 * Description:
 */
public class SettingsCustomAdapter extends RecyclerView.Adapter<SettingsCustomAdapter.MyViewHolder>{

    Context mContext;

    public SettingsCustomAdapter(Context mContext) {
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.settings_custom, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

    }


    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener {

        FrameLayout settings_wifi;

        ScrollView scrollView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            settings_wifi = itemView.findViewById(R.id.settings_wifi);
            scrollView = itemView.findViewById(R.id.scroll);

            settings_wifi.setOnFocusChangeListener(this);

        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(v.getId()==R.id.settings_wifi && hasFocus){
                //滚动到RecyclerView顶部
                scrollView.scrollTo(0,0);
            }
        }

    }

}
