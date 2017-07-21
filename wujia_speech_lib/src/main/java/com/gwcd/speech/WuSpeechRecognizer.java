package com.gwcd.speech;

import android.content.Context;
import android.os.Bundle;

import com.gwcd.speech.control.WuSpeechControlDispatcher;
import com.gwcd.speech.semantic.StringItem;
import com.gwcd.speech.semantic.StringMatchResult;
import com.gwcd.speech.semantic.WuSemanticEngine;
import com.gwcd.speech.utils.SpeechLog;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by sy on 2017/3/10.<br>
 * Function: 悟家文本语义理解对象<br>
 * Creator: sy<br>
 * Create time: 2017/3/10 16:34<br>
 * Revise Record:<br>
 * 2017/3/10: 创建并完成初始实现<br>
 */

public class WuSpeechRecognizer extends WuSpeechComponent {

    private static final String KEY_RC = "rc";
    private static final String KEY_TEXT = "text";
    private static final String KEY_ANSWER = "answer";
    private static final String KEY_SERVICE = "service";
    private static final String KEY_OPERATION = "operation";
    private static final String KEY_SEMANTIC = "semantic";
    private static final int RC_SUCCESS = 0;
    public static final int ERR_FAIL_TO_CONTROL = 65001;

    /** 讯飞语音理解对象 */
    private SpeechUnderstander mXfUnderstander;

    private WuSpeechListener mWuSpeechListener;

    private WuSemanticEngine mWuSemanticEngine;

    private WuSpeechControlDispatcher mControlDispatcher;

    protected WuSpeechRecognizer(Context context, StringItem[] communityInfo) {
        if (communityInfo == null) {
            return;
        }
        mWuSemanticEngine = WuSpeechFactory.createSemanticEngine(context, communityInfo);
        mControlDispatcher = new WuSpeechControlDispatcher();
        initXfUnderstander(context);
    }

    private void initXfUnderstander(Context context) {
        //1.创建文本语义理解对象
        mXfUnderstander = SpeechUnderstander.createUnderstander(context, null);
        //2.设置参数，语义场景配置请登录http://osp.voicecloud.cn/
        mXfUnderstander.setParameter(SpeechConstant.DOMAIN, "iat");
        mXfUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mXfUnderstander.setParameter(SpeechConstant.ACCENT, "mandarin");
        mXfUnderstander.setParameter(SpeechConstant.NLP_VERSION, "2.0");
        mXfUnderstander.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置标点符号
        mXfUnderstander.setParameter(SpeechConstant.ASR_PTT, "0");
        // 设置语音前端点:静音超时时间(毫秒)，即用户多长时间不说话则当做超时处理
        mXfUnderstander.setParameter(SpeechConstant.VAD_BOS, "4000");
    }

    public void setWuSpeechListener(WuSpeechListener mWuSpeechListener) {
        this.mWuSpeechListener = mWuSpeechListener;
    }

    public void startListening() {
        //3.开始语义理解
        mXfUnderstander.startUnderstanding(mUnderstanderListener);
    }

    private SpeechUnderstanderListener mUnderstanderListener = new SpeechUnderstanderListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            if (mWuSpeechListener != null) {
                mWuSpeechListener.onVolumeChanged(i);
            }
        }

        @Override
        public void onBeginOfSpeech() {
            if (mWuSpeechListener != null) {
                mWuSpeechListener.onStart();
            }
        }

        @Override
        public void onEndOfSpeech() {
            if (mWuSpeechListener != null) {
                mWuSpeechListener.onEnd();
            }
        }

        @Override
        public void onResult(UnderstanderResult understanderResult) {
            SpeechLog.d(understanderResult.getResultString());
            handleResult(understanderResult);
        }

        @Override
        public void onError(SpeechError speechError) {
            if (mWuSpeechListener != null) {
                mWuSpeechListener.onError(speechError.getErrorCode(), speechError.getErrorDescription());
            }
            SpeechLog.e(speechError.getPlainDescription(true));
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    private void handleResult(UnderstanderResult understanderResult) {
        JSONObject resultJson;
        try {
            resultJson = new JSONObject(understanderResult.getResultString());
            String text = resultJson.getString(KEY_TEXT);
            SpeechLog.d("json text: " + resultJson);
            if (isXfSemantic(resultJson)) {
                // 讯飞语音处理，目前不使用
            } else {
                if (mWuSpeechListener != null) {
                    mWuSpeechListener.onTextResult(text);
                    mWuSpeechListener.onResult(text);
                }
                List<StringMatchResult> results = mWuSemanticEngine.getResult(text);
                boolean success = mControlDispatcher.dispatchToController(results);
                if (!success && mWuSpeechListener != null) {
                    mWuSpeechListener.onError(ERR_FAIL_TO_CONTROL, "unable to control any devices");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isXfSemantic(JSONObject resultJson) throws JSONException {
        /** 是否使用讯飞语义，目前不使用 */
        boolean useXfSemantic = false;
        if (useXfSemantic
                && resultJson.has(KEY_RC) && RC_SUCCESS == resultJson.getInt(KEY_RC)
                && resultJson.has(KEY_TEXT)
                && resultJson.has(KEY_SERVICE)
                && resultJson.has(KEY_OPERATION)
                && resultJson.has(KEY_SEMANTIC)) {
            return true;
        }

        return false;
    }
}
