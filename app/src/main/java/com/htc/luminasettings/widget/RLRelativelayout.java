package com.htc.luminasettings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.htc.luminasettings.utils.LogUtils;

/**
 * Author: Xuhao
 * Date:
 * Description:用作给布局最后一个子控件TextView 跟随布局焦点选中，从而让文本进行滚动显示
 */
public class RLRelativelayout extends RelativeLayout {
    public RLRelativelayout(Context context) {
        super(context);
    }

    public RLRelativelayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RLRelativelayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RLRelativelayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean isFocused() {
        LogUtils.d("getChildCount",getChildCount()+"");
        if (getChildCount()==0)
            return super.isFocused();

        if (super.isFocused()){
            getChildAt(getChildCount()-1).setSelected(true);
            return true;
        }
        getChildAt(getChildCount()-1).setSelected(false);
        return false;
    }
}
