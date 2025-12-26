package com.htc.luminasettings.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.htc.luminasettings.R;
import com.htc.luminasettings.adapter.LanguageAdapter;
import com.htc.luminasettings.entry.Language;

import java.util.ArrayList;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

public class CustomLanguageDialog extends BaseDialog {
    private String TAG = "CustomLanguageDialog";
    public LanguageAdapter adapter = null;
    private Context mContext;
    private ArrayList<Language> mLocales = null;
    public OnItemClickLanguageCallBack mcallback;

    public interface OnItemClickLanguageCallBack {
        void OnClick(Language language);
    }

    public CustomLanguageDialog(Context context) {
        super(context);
        mContext = context;
    }

    public CustomLanguageDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    public CustomLanguageDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog_language_item, null);
        if (view != null) {
            setContentView(view);
            RecyclerView language_listview = view.findViewById(R.id.language_listview);
            language_listview.addItemDecoration(new SpacesItemDecoration(0,0,SpacesItemDecoration.px2dp(4),0));
            if (mLocales != null) {
                adapter = new LanguageAdapter( mLocales,mContext,language_listview);
                adapter.setCur_language(getCurrentLauguage());
                language_listview.setAdapter(adapter);
            }
            Window dialogWindow = getWindow();
            WindowManager manager = ((Activity) mContext).getWindowManager();
            LayoutParams params = dialogWindow.getAttributes();
            Display d = manager.getDefaultDisplay();
            params.width = d.getWidth();
            params.height =  d.getHeight();
            dialogWindow.setAttributes(params);
        }
    }

    public void setContent(ArrayList<Language> locales) {
        mLocales = locales;
    }

//    private String getCurrentLauguage() {
//        return Locale.getDefault().getLanguage()+ Locale.getDefault().getCountry();
//    }

    private String getCurrentLauguage() {
        return Locale.getDefault().getLanguage();
    }

    public void setOnClickCallBack(OnItemClickLanguageCallBack callback) {
        mcallback = callback;
    }
}
