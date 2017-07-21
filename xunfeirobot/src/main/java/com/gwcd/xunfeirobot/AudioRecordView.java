package com.gwcd.xunfeirobot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;

/**
 * Created by sy on 2017/7/21.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/7/21 16:47<br>
 * Revise Record:<br>
 * 2017/7/21: 创建并完成初始实现<br>
 */

public class AudioRecordView extends View {

    public AudioRecordView(Context context) {
        super(context);
        init(context);
    }

    public AudioRecordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AudioRecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private Paint mPaint;
    private static final int CHART_MARGIN = 24; // 8dp
    int width, height;
    private LinkedList<Point> mData;
    private Path mPath;
    private int step;
    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.DKGRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);
        mData = new LinkedList<>();
        mPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        step = (width - CHART_MARGIN) / MAX_SIZE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(8);
        canvas.drawLine(CHART_MARGIN, 0, CHART_MARGIN, height - CHART_MARGIN, mPaint);
        canvas.drawLine(CHART_MARGIN, height - CHART_MARGIN, width, height - CHART_MARGIN, mPaint);
        mPath.reset();
        for (int i = 0; i < mData.size(); i++) {
            Point p = mData.get(i);
            if (i == 0) {
                mPath.moveTo(p.x, p.y);
            } else {
                mPath.lineTo(p.x, p.y);
            }
        }
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(4);
        canvas.drawPath(mPath, mPaint);
    }

    private static final int MAX_SIZE = 100;
    public void addAudioData(int data) {
        if (mData.size() >= MAX_SIZE) {
            mData.removeFirst();
        }
        for (int i = 0; i < mData.size(); i++) {
            Point p = mData.get(i);
            p.x += step;
        }
        mData.add(new Point(CHART_MARGIN, getShowVolume(data)));
        invalidate();
    }

    private int getShowVolume(int realVolume) {
        float percent = 1 - realVolume / 100f;
        return (int) ((height - CHART_MARGIN) * percent);
    }
}
