package com.gwcd.speech.wakeup;

import android.content.Context;
import android.util.AndroidRuntimeException;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by sy on 2017/3/13.<br>
 * Function: 语音唤醒工具类<br>
 * Creator: sy<br>
 * Create time: 2017/3/13 14:24<br>
 * Revise Record:<br>
 * 2017/3/13: 创建并完成初始实现<br>
 */

public class WuWakeUpUtility {

    private static final String TAG = "WuWakeUpUtility";

    private static WuWakeUpUtility mInstance;
    private Context mAppContext;
    private WuWakeUpListener mWakeUpListener;
    private EventManager mWpEventManager;
    private HashMap mWakeUpParams;

    private WuWakeUpUtility() {

    }

    /**
     * 获取语音唤醒工具类对象，单例模式
     * @return
     */
    public static WuWakeUpUtility getInstance() {
        if (mInstance == null) {
            synchronized (WuWakeUpUtility.class) {
                if (mInstance == null) {
                    mInstance = new WuWakeUpUtility();
                }
            }
        }

        return mInstance;
    }

    /**
     * 初始化唤醒工具
     * @param context 上下文
     * @param wakeUpListener 唤醒监听回调接口
     */
    public void initWakeUp(Context context, WuWakeUpListener wakeUpListener) {
        mAppContext = context.getApplicationContext();
        mWakeUpListener = wakeUpListener;
    }

    /**
     * 设置唤醒监听回调接口
     * @param mWakeUpListener
     */
    public void setWakeUpListener(WuWakeUpListener mWakeUpListener) {
        this.mWakeUpListener = mWakeUpListener;
    }

    /**
     * 启动唤醒监听
     */
    public void startWakeUpListening() {
        initWeakUp();
    }

    /**
     * 停止唤醒监听
     */
    public void stopWakeUpListening() {
        stopWeakUp();
    }

    private static final String EVENT_ENTER = "wp.enter";
    private static final String EVENT_START = "wp.start";
    private static final String EVENT_DATA = "wp.data";
    private static final String EVENT_STOP = "wp.stop";
    private static final String EVENT_EXIT = "wp.exit";
    private static final String NAME_WP = "wp";
    private static final String KEY_WORD = "word";
    private static final String KEY_WAKE_UP_BIN_FILE = "kws-file";
    private static final String VALUE_WAKE_UP_BIN_FILE = "assets:///WakeUp.bin";

    private EventListener mEventListener = new EventListener() {
        @Override
        public void onEvent(String name, String params, byte[] data, int offset, int length) {
            Log.d(TAG, String.format("event: name=%s, params=%s", name, params));
            try {
                JSONObject json = new JSONObject(params);
                if (EVENT_ENTER.equals(name)) {
                    if (mWakeUpListener != null) {
                        mWakeUpListener.onStartListening();
                    }
                } else if (EVENT_DATA.equals(name)) { // 每次唤醒成功, 将会回调name=wp.data的时间, 被激活的唤醒词在params的word字段
                    String word = json.getString(KEY_WORD);
                    if (mWakeUpListener != null) {
                        mWakeUpListener.onWakeUp(word);
                    }
                } else if (EVENT_EXIT.equals(name)) {
                    mWpEventManager.unregisterListener(mEventListener);
                    if (mWakeUpListener != null) {
                        mWakeUpListener.onStopListening();
                    }
                }
            } catch (JSONException e) {
                throw new AndroidRuntimeException(e);
            }
        }
    };

    private void initWeakUp() {
        // 唤醒功能打开步骤
        // 1) 创建唤醒事件管理器
        if (mWpEventManager == null) {
            mWpEventManager = EventManagerFactory.create(mAppContext, NAME_WP);
        }

        // 2) 注册唤醒事件监听器
        mWpEventManager.registerListener(mEventListener);

        // 3) 通知唤醒管理器, 启动唤醒功能
        if (mWakeUpParams == null) {
            mWakeUpParams = new HashMap();
            mWakeUpParams.put(KEY_WAKE_UP_BIN_FILE, VALUE_WAKE_UP_BIN_FILE); // 设置唤醒资源, 唤醒资源请到 http://yuyin.baidu.com/wake#m4 来评估和导出
        }
        mWpEventManager.send(EVENT_START, new JSONObject(mWakeUpParams).toString(), null, 0, 0);
    }

    private void stopWeakUp() {
        // 停止唤醒监听
        if (mWpEventManager != null) {
            mWpEventManager.send(EVENT_STOP, null, null, 0, 0);
        }
    }
}
