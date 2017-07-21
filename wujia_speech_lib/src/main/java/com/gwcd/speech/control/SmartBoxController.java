package com.gwcd.speech.control;

import android.os.Bundle;

/**
 * Created by sy on 2017/3/22.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/3/22 15:59<br>
 * Revise Record:<br>
 * 2017/3/22: 创建并完成初始实现<br>
 */

public interface SmartBoxController extends WuSpeechController {

    /**
     * 开关
     * @param data
     */
    void setPower(Bundle data);

    /**
     * 路数控制
     * @param data
     */
    void setLine(Bundle data);
}
