/**
 * Project name：Inote
 * Create time：2017/1/13 18:01
 * Copyright: 2017 GALAXYWIND Network Systems Co.,Ltd.All rights reserved.
 */
package com.gwcd.xunfeirobot;

import android.app.Application;
import android.content.Intent;

import com.gwcd.speech.wakeup.WuWakeUpListener;
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
        String wujiaId = "58e74825";
        String testId = "584f7042";
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + wujiaId);

//        WuWakeUpUtility.getInstance().initWakeUp(this, mWakeUpListener);
//        WuWakeUpUtility.getInstance().startWakeUpListening();
    }

    private WuWakeUpListener mWakeUpListener = new WuWakeUpListener() {
        @Override
        public void onStartListening() {
            System.out.println("--debug onStartListening");
        }

        @Override
        public void onWakeUp(String word) {
            System.out.println("--debug onWakeUp");
            Intent intent = new Intent(mInstance, SematicActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("fromWakeUp", true);
            startActivity(intent);
        }

        @Override
        public void onStopListening() {
            System.out.println("--debug onStopListening");
        }
    };

    public static RobotApp getInstance() {
        return mInstance;
    }

    public static RobotApp getAppContext() {
        return mInstance;
    }
}
