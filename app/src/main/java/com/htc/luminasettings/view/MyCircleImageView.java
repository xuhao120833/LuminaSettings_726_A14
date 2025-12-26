package com.htc.luminasettings.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.htc.luminasettings.R;
import com.htc.luminasettings.utils.LogUtils;

public class MyCircleImageView extends de.hdodenhof.circleimageview.CircleImageView {
    public boolean hasFocus;
    private Paint borderPaint;

    public RelativeLayout rl_item;

    private static String TAG = "CircleImageView";

    public MyCircleImageView(Context context) {
        super(context);
        init();
    }

    public MyCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyCircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(getResources().getDimension(R.dimen.y_8)); // 设置圆环厚度为8px
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, android.graphics.Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
//        hasFocus = focused;
        LogUtils.d("触发焦点获取", " 开始画白色圆环 focused" + focused);
//        invalidate(); // 重新绘制View
    }

    @Override
    protected void onDraw(Canvas canvas) {

        LogUtils.d("触发焦点获取", " this.isFocused "+this.isFocused());
        if (hasFocus || rl_item.isFocused()) {
            super.onDraw(canvas);
            // 绘制白色圆环
            int width = getWidth();
            int height = getHeight();
            float radius = Math.min(width, height) / 2.0f;
            float cx = width / 2.0f;
            float cy = height / 2.0f;

            LogUtils.d("触发焦点获取", " 开始画白色圆环 " + width + " " + height + " " + radius + " " + cx + " " + cy + " " + this);

            // 圆环应绘制在外侧，因此需要考虑边框的厚度
            canvas.drawCircle(cx, cy, radius - 4, borderPaint); // 半径减去一半的边框厚度
            hasFocus = false;
        } else {
            super.onDraw(canvas);
            LogUtils.d("触发焦点获取", " 去掉白环 " + this);
        }
    }
}
