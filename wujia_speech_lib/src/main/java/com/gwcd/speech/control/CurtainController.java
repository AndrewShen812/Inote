package com.gwcd.speech.control;

import android.os.Bundle;

/**
 * Created by sy on 2017/3/22.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/3/22 16:02<br>
 * Revise Record:<br>
 * 2017/3/22: 创建并完成初始实现<br>
 */

public interface CurtainController extends WuSpeechController {
    /**
     * 开窗帘
     * @param data
     */
    void open(Bundle data);

    /**
     * 关窗帘
     * @param data
     */
    void colse(Bundle data);

    /**
     * 设置窗帘类型，开合帘or卷帘
     * @param data
     */
    void setType(Bundle data);

    /**
     * 反向设置
     * @param data
     */
    void reverse(Bundle data);
}
