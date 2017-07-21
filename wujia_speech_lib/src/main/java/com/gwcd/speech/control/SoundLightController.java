package com.gwcd.speech.control;

import android.os.Bundle;

/**
 * Created by sy on 2017/3/22.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/3/22 16:03<br>
 * Revise Record:<br>
 * 2017/3/22: 创建并完成初始实现<br>
 */

public interface SoundLightController extends WuSpeechController {

    /**
     * 声光报警器，照明模式开关灯
     * @param data
     */
    void lightOnOff(Bundle data);
}
