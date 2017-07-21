package com.gwcd.speech.wakeup;

/**
 * Created by sy on 2017/3/13.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/3/13 14:20<br>
 * Revise Record:<br>
 * 2017/3/13: 创建并完成初始实现<br>
 */

public interface WuWakeUpListener {

    /**
     * 进入语音唤醒状态
     */
    void onStartListening();

    /**
     * 语音唤醒事件
     * @param word 唤醒词
     */
    void onWakeUp(String word);

    /**
     * 退出语音唤醒
     */
    void onStopListening();
}
