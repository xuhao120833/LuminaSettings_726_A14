package com.htc.luminasettings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Author:
 * Date:
 * Description:用作给布局第一个子控件TextView 跟随布局焦点选中，从而让文本进行滚动显示
 */
public class RL1Relativelayout extends RelativeLayout {
    public RL1Relativelayout(Context context) {
        super(context);
    }

    public RL1Relativelayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RL1Relativelayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RL1Relativelayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean isFocused() {
        if (getChildCount()==0)
            return super.isFocused();

        if (super.isFocused()){
            getChildAt(0).setSelected(true);
            return true;
        }
        getChildAt(0).setSelected(false);
        return false;
    }
}
