package com.gwcd.xunfeirobot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sy on 2017/7/21.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/7/21 16:47<br>
 * Revise Record:<br>
 * 2017/7/21: 创建并完成初始实现<br>
 */

public class VoiceRecordView extends View {

    public VoiceRecordView(Context context) {
        super(context);
        init(context);
    }

    public VoiceRecordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VoiceRecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private Paint mPaint;

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
