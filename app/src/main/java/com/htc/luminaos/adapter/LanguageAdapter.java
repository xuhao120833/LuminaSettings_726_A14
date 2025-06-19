package com.htc.luminaos.adapter;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.internal.app.LocalePicker;
import com.htc.luminaos.R;
import com.htc.luminaos.activity.MainActivity;
import com.htc.luminaos.entry.Language;
import com.htc.luminaos.receiver.DisplaySettingsReceiver;
import com.htc.luminaos.utils.ScrollUtils;
import com.htc.luminaos.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:
 * Date:
 * Description:
 */
public class LanguageAdapter  extends RecyclerView.Adapter<LanguageAdapter.MyViewHolder> implements View.OnHoverListener{

    List<Language> languageList = new ArrayList<>();
    Context mContext;
    String cur_language="";
    private RecyclerView recyclerView;
    private static String TAG = "LanguageAdapter";

    public LanguageAdapter(List<Language> languageList, Context mContext,RecyclerView recyclerView){
        this.mContext = mContext;
        this.languageList = languageList;
        this.recyclerView = recyclerView;
    }

    public void updateList(List<Language> languageList){
        this.languageList = languageList;
    }

    public void setCur_language(String cur_language) {
        this.cur_language = cur_language;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.language_item,null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final Language language = languageList.get(i);
        String locale = null;
        if(Locale.getDefault().getLanguage().equals("zh") || Locale.getDefault().getLanguage().equals("en") ){ //英文、中文单独拉出来处理，因为中英文附带了国家码。
            cur_language = Locale.getDefault().getLanguage()+ Locale.getDefault().getCountry();
            locale = language.getLocale().getLanguage()+ language.getLocale().getCountry();
        } else {
            cur_language = Locale.getDefault().getLanguage();
            locale = language.getLocale().getLanguage();
        }
        Log.d(TAG," 语言环境 "+cur_language+" "+locale);
        if (cur_language.equals(locale)){
            myViewHolder.status.setVisibility(View.VISIBLE);
        }else {
            myViewHolder.status.setVisibility(View.GONE);
        }
        myViewHolder.language_name.setText(language.getLabel());
        myViewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(Locale.getDefault().getLanguage().equals("zh") || Locale.getDefault().getLanguage().equals("en") ){ //英文、中文单独拉出来处理，因为中英文附带了国家码。
//                    cur_language = Locale.getDefault().getLanguage()+ Locale.getDefault().getCountry();
//                } else {
//                    cur_language = Locale.getDefault().getLanguage();
//                }
                Log.d(TAG,"cur_language "+cur_language+" language.get "+language.getLocale().getLanguage()+language.getLocale().getCountry());
                if (!cur_language.equals(language.getLocale().getLanguage()+language.getLocale().getCountry())) {
                    LocalePicker.updateLocale(language.getLocale());
                    setCur_language(language.getLocale().getLanguage()+language.getLocale().getCountry());
                    notifyDataSetChanged();

                    Utils.support_image_path = "";
                }
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
        return languageList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView language_name;
        ImageView status;
        RelativeLayout rl_item;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            language_name = itemView.findViewById(R.id.name);
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
