package com.gwcd.speech.control;

import android.os.Bundle;

/**
 * Created by sy on 2017/3/22.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/3/22 15:55<br>
 * Revise Record:<br>
 * 2017/3/22: 创建并完成初始实现<br>
 */

public interface LightController extends WuSpeechController {

    /**
     * 设置颜色，RGB值
     * @param data
     */
    void setColor(Bundle data);

    /**
     * 设置亮度
     * @param data
     */
    void setLight(Bundle data);

    /**
     * 设置色温
     * @param data
     */
    void setColorTemp(Bundle data);

    /**
     * 设置模式
     * @param data
     */
    void setMode(Bundle data);
}
