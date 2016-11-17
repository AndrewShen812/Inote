package com.lf.utils;



/**
 * @项目名称：AdSDKLib
 * @类名称：LogUtils
 * @类描述：用于Log输出
 * @创建人：huaiying
 * @创建时间：2015年1月26日 上午8:32:44
 * @修改人：huaiying
 * @修改时间：2015年1月26日 上午8:32:44
 * @修改备注：
 * @version
 */
public final class LogUtils {
    
    private static final String TAG = LogUtils.class.getSimpleName();
    
    private static final boolean DEBUG = true;
    
    /**
     * @description 默认输出information信息
     * @date 2015年1月26日
     * @param msg
     */
    public static void i(String msg) {
        i(TAG, ""+msg);
    }
    
    /**
     * @description 默认输出Debug信息
     * @date 2015年1月26日
     * @param msg
     */
    public static void d(String msg) {
        d(TAG, ""+msg);
    }
    
    /**
     * @description 默认输出Error信息
     * @date 2015年1月26日
     * @param msg
     */
    public static void e(String msg) {
        e(TAG, ""+msg);
    }
    
    /**
     * @description 默认输出Verbose信息
     * @date 2015年1月26日
     * @param msg
     */
    public static void v(String msg) {
        v(TAG, ""+msg);
    }
    
    /**
     * @description 输出调试模式下的Information信息
     * @date 2015年1月26日
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
        if (DEBUG) {
            android.util.Log.i(tag, ""+msg);
        }
    }
    
    /**
     * @description 输出调试模式下的Error信息
     * @date 2015年1月26日
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (DEBUG) {
            android.util.Log.e(tag, ""+msg);
        }
    }
    
    /**
     * @description 输出调试模式下的Debug信息
     * @date 2015年1月26日
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        if (DEBUG) {
            android.util.Log.d(tag, ""+msg);
        }
    }
    
    /**
     * @description 输出调试模式下的Verbose信息
     * @date 2015年1月26日
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg) {
        if (DEBUG) {
            android.util.Log.v(tag, ""+msg);
        }
    }
    
    /**
     * @description 输出调试模式下的Warning信息
     * @date 2015年1月26日
     * @param tag
     * @param msg
     */
    public static void w(String tag, String msg) {
        if (DEBUG) {
            android.util.Log.w(tag, ""+msg);
        }
    }
    
}
