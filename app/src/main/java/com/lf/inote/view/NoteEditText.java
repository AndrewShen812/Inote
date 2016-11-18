/**
 * @项目名称：INote
 * @文件名：NoteEditText.java
 * @版本信息：
 * @日期：2015-2-10
 * @Copyright 2015 www.517na.com Inc. All rights reserved.
 */
package com.lf.inote.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * @项目名称：INote
 * @类名称：NoteEditText
 * @类描述：
 * @创建人：lianfeng
 * @创建时间：2015-2-10 下午7:10:23
 * @修改人：lianfeng
 * @修改时间：2015-2-10 下午7:10:23
 * @修改备注：
 * @version
 */
@SuppressLint("DrawAllocation")
public class NoteEditText extends EditText {
    
    private static final int MARGIN = 10;
    
    private Paint mPaint;
    
    public NoteEditText(Context context) {
        super(context, null);
        setCursorVisible(true);
    }
    
    public NoteEditText(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        setCursorVisible(true);
    }
    
    public NoteEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCursorVisible(true);
    }
    
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.GRAY);
        
        // 得到每行的高度
        int lineHeight = getLineHeight();
        // 获得控件高度
        int height = this.getHeight();
        // 根据行数循环画线
        int i = 0;
        while (height / lineHeight != 0) {
            height -= lineHeight;
            int lineY = (i + 1) * lineHeight;
            canvas.drawLine(MARGIN, lineY, this.getWidth() - MARGIN, lineY, mPaint);
            i++;
        }
    }
    
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    
    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
//    @Override
//    public void onTextChanged(CharSequence s, int start, int count, int after) {
//    }
//
//    @Override
//    public void beforeTextChanged(CharSequence s, int start, int count,
//            int after) {
//
//    }
//
//    @Override
//    public void afterTextChanged(Editable s) {
//
//    }
//
//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//    }
}
