package com.htc.luminasettings.utils;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:
 * Date:
 * Description:
 */
public class ScrollUtils {
    /**
     * 计算需要滑动的距离,使焦点在滑动中始终居中
     * @param recyclerView
     * @param view
     */
    public static int[] getScrollAmount(RecyclerView recyclerView, View view) {
        int[] out = new int[2];
        final int parentLeft = recyclerView.getPaddingLeft();
        final int parentTop = recyclerView.getPaddingTop();
        final int parentRight = recyclerView.getWidth() - recyclerView.getPaddingRight();
        final int parentBottom = recyclerView.getHeight() - recyclerView.getPaddingBottom();
        final int childLeft = view.getLeft() - view.getScrollX();
        final int childTop = view.getTop() - view.getScrollY();

        final int dx =childLeft - parentLeft - ((parentRight - view.getWidth()) / 2);

        final int dy = childTop - parentTop - ((parentBottom - view.getHeight()) / 2);
        out[0] = dx;
        out[1] = dy;
        return out;

    }
}
