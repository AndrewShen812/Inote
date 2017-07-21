package com.gwcd.speech.utils;

import android.util.Log;

/**
 * Created by sy on 2017/3/15.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/3/15 18:28<br>
 * Revise Record:<br>
 * 2017/3/15: 创建并完成初始实现<br>
 */

public class SpeechLog {

    private static boolean enable = true;

    private static final String TAG = "SpeechLog";

    public static void setEnable(boolean e) {
        enable = e;
    }

    public static void d(String msg) {
        if (enable) {
            Log.d(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (enable) {
            Log.i(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (enable) {
            Log.e(TAG, msg);
        }
    }
}
