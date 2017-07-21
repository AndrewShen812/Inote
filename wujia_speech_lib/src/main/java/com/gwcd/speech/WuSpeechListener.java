package com.gwcd.speech;

/**
 * Created by sy on 2017/3/10.<br>
 * Function: 语音识别回调接口<br>
 * Creator: sy<br>
 * Create time: 2017/3/10 18:48<br>
 * Revise Record:<br>
 * 2017/3/10: 创建并完成初始实现<br>
 */

public interface WuSpeechListener {
    void onStart();
    void onVolumeChanged(int volume);
    void onEnd();
    void onTextResult(String text);
    void onResult(String text);
    void onError(int errCode, String errMsg);
}
