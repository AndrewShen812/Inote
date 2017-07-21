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

public interface Ch2oController extends WuSpeechController {

    /**
     * 刷新当前值
     * @param data
     */
    void refresh(Bundle data);
}
