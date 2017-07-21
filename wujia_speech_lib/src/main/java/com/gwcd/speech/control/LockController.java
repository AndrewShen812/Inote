package com.gwcd.speech.control;

import android.os.Bundle;

/**
 * Created by sy on 2017/3/22.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/3/22 15:56<br>
 * Revise Record:<br>
 * 2017/3/22: 创建并完成初始实现<br>
 */

public interface LockController extends WuSpeechController {

    /**
     * 关锁
     * @param data
     */
    void lock(Bundle data);

    /**
     * 开锁
     * @param data
     */
    void unlock(Bundle data);

    /**
     * 布防
     * @param data
     */
    void arming(Bundle data);

    /**
     * 撤防
     * @param data
     */
    void disarming(Bundle data);

}
