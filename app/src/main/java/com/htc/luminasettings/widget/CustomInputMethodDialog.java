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
import com.htc.luminasettings.adapter.InputMethodAdapter;
import com.htc.luminasettings.entry.InputMethodBean;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class CustomInputMethodDialog extends BaseDialog {
    private String TAG = "CustomLanguageDialog";
    public InputMethodAdapter adapter = null;
    private Context mContext;
    private int mSelection = -1;
    public OnItemClickInputMethodCallBack mcallback;
    private ArrayList<InputMethodBean> mlist = null;

    public interface OnItemClickInputMethodCallBack {
        void OnClick(InputMethodBean inputMethodBean);
    }

    public CustomInputMethodDialog(Context context) {
        super(context);
        mContext = context;
    }

    public CustomInputMethodDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    public CustomInputMethodDialog(Context context, int theme, int selection) {
        super(context, theme);
        mContext = context;
        mSelection = selection;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog_inputmethod_item, null);
        if (view != null) {
            setContentView(view);
           RecyclerView inputmethod_listview =  view.findViewById(R.id.inputmethod_listview);
            inputmethod_listview.addItemDecoration(new SpacesItemDecoration(0,0,SpacesItemDecoration.px2dp(4),0));
            if (mlist != null) {
                adapter = new InputMethodAdapter(mContext, mlist);
                adapter.setCurrentPosition(mSelection);
                inputmethod_listview.setAdapter(adapter);
            }
            Window dialogWindow = getWindow();
            WindowManager manager = ((Activity) mContext).getWindowManager();
            LayoutParams params = dialogWindow.getAttributes();
            dialogWindow.setGravity(17);
            Display d = manager.getDefaultDisplay();
            params.width =  d.getWidth();
            params.height =  d.getHeight();
            dialogWindow.setAttributes(params);
        }
    }

    public void setContent(ArrayList<InputMethodBean> list) {
        mlist = list;
    }

    public void setOnClickCallBack(OnItemClickInputMethodCallBack callback) {
        mcallback = callback;
    }
}
