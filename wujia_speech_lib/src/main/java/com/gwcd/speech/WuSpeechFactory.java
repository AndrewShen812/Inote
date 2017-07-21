package com.gwcd.speech;

import android.content.Context;

import com.gwcd.speech.semantic.StringItem;
import com.gwcd.speech.semantic.WuSemanticEngine;

/**
 * Created by sy on 2017/3/10.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/3/10 16:44<br>
 * Revise Record:<br>
 * 2017/3/10: 创建并完成初始实现<br>
 */

public class WuSpeechFactory {

    /**
     * 创建一个悟家语音识别对象
     * @param context
     * @param communityInfo
     * @return
     */
    public static WuSpeechRecognizer createSpeechRecognizer(Context context, StringItem[] communityInfo) {
        return new WuSpeechRecognizer(context, communityInfo);
    }

    /**
     * 创建一个语义识别引擎
     * @param context
     * @param communityInfo
     * @return
     */
    public static WuSemanticEngine createSemanticEngine(Context context, StringItem[] communityInfo) {
        return new WuSemanticEngine(context, communityInfo);
    }
}
