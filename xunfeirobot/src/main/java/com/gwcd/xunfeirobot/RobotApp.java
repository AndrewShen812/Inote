/**
 * Project name：Inote
 * Create time：2017/1/13 18:01
 * Copyright: 2017 GALAXYWIND Network Systems Co.,Ltd.All rights reserved.
 */
package com.gwcd.xunfeirobot;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by sy on 2017/1/13.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/1/13 18:01<br>
 * Revise Record:<br>
 * 2017/1/13: 创建并完成初始实现<br>
 */
public class RobotApp extends Application {

    private static RobotApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=584f7042");
    }

    public static RobotApp getInstance() {
        return mInstance;
    }

    public static RobotApp getAppContext() {
        return mInstance;
    }
}
