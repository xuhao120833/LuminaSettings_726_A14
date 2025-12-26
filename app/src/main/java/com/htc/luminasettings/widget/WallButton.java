package com.htc.luminasettings.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Author:
 * Date:
 * Description:
 */
public class WallButton extends TextView {
    private int height = 6;
    private static final String TAG = "MyButton";

    public WallButton(Context context) {
        super(context);
    }

    public WallButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WallButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.isSelected()) {
            Paint mPaint = new Paint();
            mPaint.setColor(0xFFFFFFFF);
            Rect rect = new Rect(0, this.getHeight() - height, this.getWidth(), this.getHeight());
            canvas.drawRect(rect, mPaint);
        }
    }
}