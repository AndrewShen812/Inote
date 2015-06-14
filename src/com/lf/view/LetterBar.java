/**
 * @项目名称：TempLook
 * @文件名：LetterBar.java
 * @版本信息：
 * @日期：2015-2-6
 * @Copyright 2015 www.517na.com Inc. All rights reserved.
 */
package com.lf.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * @项目名称：TempLook
 * @类名称：LetterBar
 * @类描述：
 * @创建人：lianfeng
 * @创建时间：2015-2-6 下午3:11:13
 * @修改人：lianfeng
 * @修改时间：2015-2-6 下午3:11:13
 * @修改备注：
 * @version
 */
public class LetterBar extends View {
    
    /** 无效位置 */
    private static final int INVALID_POS = -1;
    
    private static final String[] mLetters = { "A",
                                              "B",
                                              "C",
                                              "D",
                                              "E",
                                              "F",
                                              "G",
                                              "H",
                                              "I",
                                              "J",
                                              "K",
                                              "L",
                                              "M",
                                              "N",
                                              "O",
                                              "P",
                                              "Q",
                                              "R",
                                              "S",
                                              "T",
                                              "U",
                                              "V",
                                              "W",
                                              "X",
                                              "Y",
                                              "Z" };
    
    private Paint mPaint = new Paint();
    
    private PopupWindow mPopupWindow;
    
    private TextView mPopupText;
    
    private Handler mHandler = new Handler();
    
    private OnLetterClickListener mLetterClickListener;
    
    private int mChoosed = INVALID_POS;
    
    public LetterBar(Context context) {
        this(context, null);
    }
    
    public LetterBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public LetterBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    /**
     * 绘制控件
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int h = getHeight();
        int w = getWidth();
        int singleH = h / mLetters.length;
        for (int i = 0; i < mLetters.length; i++) {
            mPaint.setColor(Color.parseColor("#999999"));
            mPaint.setTextSize(singleH / 1.5f);
            if (i == mChoosed) {// 颜色区别显示当前选中的字母
                mPaint.setColor(Color.parseColor("#3399ff"));
            }
            float xPos = w / 2 - mPaint.measureText(mLetters[i]) / 2;
            float yPos = singleH * i + singleH;
            canvas.drawText(mLetters[i], xPos, yPos, mPaint);
            mPaint.reset();
        }
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float y = event.getY();
        int curPos = (int) (y / getHeight() * mLetters.length);
        switch (event.getAction()) {
        /**
         * 按下和滑动时
         */
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (curPos >= 0 && curPos <= mLetters.length) {
                    if (null != mLetterClickListener) {
                        mLetterClickListener.onLetterClick(mLetters[curPos]);
                        mChoosed = curPos;
                        showPopup(curPos);
                    }
                    invalidate();
                }
                break;
            /**
             * 抬起时
             */
            case MotionEvent.ACTION_UP:
                mChoosed = INVALID_POS;
                dismissPopup();
                invalidate();
                break;
        }
        return true;
    }
    
    /**
     * @项目名称：TempLook
     * @类名称：OnLetterClickListener
     * @类描述： 字母选中监听处理
     * @创建人：lianfeng
     * @创建时间：2015-2-6 下午4:45:18
     * @修改人：lianfeng
     * @修改时间：2015-2-6 下午4:45:18
     * @修改备注：
     * @version v1.0
     */
    public interface OnLetterClickListener {
        void onLetterClick(String letter);
    }
    
    public void setOnLetterClickListener(OnLetterClickListener listener) {
        mLetterClickListener = listener;
    }
    
    private void dismissPopup() {
        mHandler.postDelayed(dismissRunnable, 500);
    }
    
    Runnable dismissRunnable = new Runnable() {
        
        @Override
        public void run() {
            if (mPopupWindow != null) {
                mPopupWindow.dismiss();
            }
        }
    };
    
    private void showPopup(int item) {
        if (mPopupWindow == null) {
            mHandler.removeCallbacks(dismissRunnable);
            mPopupText = new TextView(getContext());
            mPopupText.setBackgroundColor(Color.GRAY);
            mPopupText.setTextColor(Color.CYAN);
            mPopupText.setTextSize(20);
            mPopupText.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            mPopupWindow = new PopupWindow(mPopupText, 100, 100);
        }
        String text = mLetters[item];
        mPopupText.setText(text);
        if (mPopupWindow.isShowing()) {
            mPopupWindow.update();
        }
        else {
            mPopupWindow.showAtLocation(getRootView(), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        }
    }
}
