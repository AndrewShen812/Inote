/**
 * Project name：Inote
 * Create time：2016/12/23 10:04
 * Copyright: 2016 GALAXYWIND Network Systems Co.,Ltd.All rights reserved.
 */
package com.lf.inote.ui.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.speech.util.JsonParser;
import com.lf.inote.NoteApp;
import com.lf.inote.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created by sy on 2016/12/23.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2016/12/23 10:04<br>
 * Revise Record:<br>
 * 2016/12/23: 创建并完成初始实现<br>
 */
public class SpeechWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "SpeechWidgetProvider";

    public static final String ACTION_SPEECH = "com.lf.inote.APPWIDGET_SPEECH";

    public static final int MSG_RESET_TIPS = 1;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private RemoteViews mWidgetView;
    private AppWidgetManager mWidgetManager;
    private HashSet<Integer> mWidgetIdSet = new HashSet<>();

    /** 语音合成器 */
    private SpeechSynthesizer mSpeechSynthesizer;
    /** 语音识别器 */
    private SpeechRecognizer mSpeechRecognizer;

    private Handler mResetHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mResetHandler.removeMessages(MSG_RESET_TIPS);
            mWidgetManager = AppWidgetManager.getInstance(NoteApp.getAppContext());
//            updateRobotText(mWidgetManager, NoteApp.getAppContext().getString(R.string.text_widget_robot_tip));
//            updateUserText(mWidgetManager, NoteApp.getAppContext().getString(R.string.text_widget_user_tip));
            updateTalkMsg(NoteApp.getAppContext().getString(R.string.text_widget_robot_tip),
                    NoteApp.getAppContext().getString(R.string.text_widget_user_tip));
        }
    };

    private void updateTalkMsg(String robotMsg, String userMsg) {
        Intent intent = new Intent(SpeechWidgetService.ACTION_MSG_UPDATE);
        if (!TextUtils.isEmpty(robotMsg)) {
            intent.putExtra("robotMsg", robotMsg);
        }
        if (!TextUtils.isEmpty(userMsg)) {
            intent.putExtra("userMsg", userMsg);
        }

        NoteApp.getAppContext().sendBroadcast(intent);
        mWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetId, R.id.lv_app_widget_msg_list);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        // 如果在xml中配置用同一个AppWidgetProvider管理多个Widget, appWidgetIds则表示这多个widget的id
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            mWidgetIdSet.add(appWidgetId);
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.d(TAG, "updateAppWidget appWidgetId=" + appWidgetId);
        mAppWidgetId = appWidgetId;
        mWidgetManager = appWidgetManager;
        if (mWidgetView == null) {
            mWidgetView = new RemoteViews(context.getPackageName(), R.layout.layout_app_widget);
        }

        Intent speechIntent = new Intent(context, SpeechWidgetProvider.class);
        speechIntent.setAction(ACTION_SPEECH);
        speechIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent btPendingIntent = PendingIntent.getBroadcast(context, 0, speechIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mWidgetView.setOnClickPendingIntent(R.id.iv_app_widget_speech, btPendingIntent);

        Intent serviceIntent = new Intent(context, SpeechWidgetService.class);
        mWidgetView.setRemoteAdapter(R.id.lv_app_widget_msg_list, serviceIntent);

        updateTalkMsg(NoteApp.getAppContext().getString(R.string.text_widget_robot_tip),
                NoteApp.getAppContext().getString(R.string.text_widget_user_tip));

        appWidgetManager.updateAppWidget(mAppWidgetId, mWidgetView);
    }

    private void updateRobotText(AppWidgetManager appWidgetManager, String text) {
        Log.d(TAG, "into updateRobotText");
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID || mWidgetManager == null) {
            return;
        }
        if (mWidgetView == null) {
            mWidgetView = new RemoteViews(NoteApp.getAppContext().getPackageName(), R.layout.layout_app_widget);
        }
        Log.d(TAG, "into updateRobotText, mAppWidgetId:" + mAppWidgetId);
        mWidgetView.setTextViewText(R.id.tv_app_widget_robot, text);
        appWidgetManager.updateAppWidget(mAppWidgetId, mWidgetView);
    }

    private void updateUserText(AppWidgetManager appWidgetManager, String text) {
        Log.d(TAG, "into updateUserText");
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID || mWidgetManager == null) {
            return;
        }
        if (mWidgetView == null) {
            mWidgetView = new RemoteViews(NoteApp.getAppContext().getPackageName(), R.layout.layout_app_widget);
        }
        mWidgetView.setTextViewText(R.id.tv_app_widget_user, text);
        appWidgetManager.updateAppWidget(mAppWidgetId, mWidgetView);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();

        mWidgetManager = AppWidgetManager.getInstance(NoteApp.getAppContext());
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        Log.d(TAG, "SpeechWidgetProvider onReceive : "+intent.getAction());
        Log.d(TAG, "SpeechWidgetProvider mAppWidgetId : "+mAppWidgetId);
        if (action.equals(ACTION_SPEECH)) {
            mResetHandler.removeMessages(MSG_RESET_TIPS);
            speechInput(NoteApp.getAppContext());
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(TAG, "onDeleted");
        ArrayList idList = new ArrayList<>();
        Collections.addAll(idList, appWidgetIds);
        mWidgetIdSet.removeAll(idList);
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled");
        mWidgetIdSet.clear();
        context.startService(new Intent(context, SpeechWidgetService.class));
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled");
        mWidgetIdSet.clear();
        context.stopService(new Intent(context, SpeechWidgetService.class));
    }

    /********************************* 语音识别相关 *********************************/
    private Toast mToast = null;
    private List<String> mResultList = new ArrayList<>();

    private void showTip(final String str) {
        if (mToast == null) {
            mToast = Toast.makeText(NoteApp.getAppContext(), "", Toast.LENGTH_SHORT);
        }
        mToast.setText(str);
        mToast.show();
    }

    /************ 语音合成 ************/
    private long mSynthesizeStart = 0;
    private long total = 0;
    private int tryTimes;
    private String mLastSource;

    private void speechSynthesize(Context context, String source) {
        if (TextUtils.isEmpty(source)) {
            return;
        }
        mLastSource = source;
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(context, null);
        // 清空参数
        mSpeechSynthesizer.setParameter(SpeechConstant.PARAMS, null);
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        /**
         * 设置播放器音频流类型
         * 0-通话
         * 1-系统
         * 2-铃声
         * 3-音乐
         * 4-闹铃
         * 5-通知
         */
        mSpeechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mSpeechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//		mSpeechSynthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
//		mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/iNote/tts.pcm");
        //3.开始合成
        mSpeechSynthesizer.startSpeaking(source, mSynListener);
        mSynthesizeStart = System.currentTimeMillis();
    }

    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
            String err = error == null ? "" : "，error:" + error.getPlainDescription(true);
            showTip("会话结束" + err);
            mResetHandler.removeMessages(MSG_RESET_TIPS);
            mResetHandler.sendEmptyMessageDelayed(MSG_RESET_TIPS, 10000);
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            showTip("缓冲进度：" + percent + "%");
        }

        //开始播放
        public void onSpeakBegin() {
//            updateRobotText(mWidgetManager, mLastSource);
            updateTalkMsg(mLastSource, null);
            long cost = System.currentTimeMillis() - mSynthesizeStart;
            Log.d("--debug", "Synthesize cost:" + cost);
            tryTimes++;
            total += cost;
            Log.d("--debug", "合成" + tryTimes + "次平均耗时：" + (total * 1f / tryTimes));
            showTip("开始播放");
        }

        //暂停播放
        public void onSpeakPaused() {
            showTip("暂停播放");
        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            showTip("播放进度:" + percent + "%");
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
            showTip("恢复播放");
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };

    /************ 语音识别 ************/
    private void speechInput(Context context) {
        mResultList.clear();
//        noDialog(context);
		understander(context);
    }

    private void noDialog(Context context) {
        //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        mSpeechRecognizer = SpeechRecognizer.createRecognizer(context, null);
        //2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        setParam(mSpeechRecognizer);
        //3.开始听写
        mSpeechRecognizer.startListening(mRecoListener);
    }

    private void understander(Context context) {
        //1.创建文本语义理解对象
        SpeechUnderstander understander = SpeechUnderstander.createUnderstander(context, null);
        //2.设置参数，语义场景配置请登录http://osp.voicecloud.cn/
        understander.setParameter(SpeechConstant.DOMAIN, "iat");
        understander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        understander.setParameter(SpeechConstant.ACCENT, "mandarin");
        understander.setParameter(SpeechConstant.NLP_VERSION, "2.0");
        understander.setParameter(SpeechConstant.RESULT_TYPE, "json");
        //3.开始语义理解
        understander.startUnderstanding(mUnderstanderListener);
    }

    private static final String KEY_RC = "rc";
    private static final String KEY_TEXT = "text";
    private static final String KEY_ANSWER = "answer";
    private static final int RC_SUCCESS = 0;

    private SpeechUnderstanderListener mUnderstanderListener = new SpeechUnderstanderListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            showTip("正在说话，当前音量：" + i);
        }

        @Override
        public void onBeginOfSpeech() {
            showTip("开始说话");
        }

        @Override
        public void onEndOfSpeech() {
            showTip("结束说话");
        }

        @Override
        public void onResult(UnderstanderResult understanderResult) {
            Log.d("Result", understanderResult.getResultString());
            JSONObject resultJson = null;
            try {
                resultJson = new JSONObject(understanderResult.getResultString());
                String text = resultJson.getString(KEY_TEXT);
                if (!TextUtils.isEmpty(text)) {
                    updateUserText(mWidgetManager, text);
                }
                String robotText = null;
                if (resultJson.has(KEY_RC) && RC_SUCCESS == resultJson.getInt(KEY_RC)
                        && resultJson.has(KEY_ANSWER)) {
                    JSONObject answer = resultJson.getJSONObject(KEY_ANSWER);
                    robotText = answer.getString(KEY_TEXT);
                }
                if (robotText == null) {
                    String format = NoteApp.getAppContext().getString(R.string.text_widget_not_understand);
                    robotText = String.format(format, text);
                }
                updateTalkMsg(NoteApp.getAppContext().getString(R.string.text_widget_understanding), text);
                speechSynthesize(NoteApp.getAppContext(), robotText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            String errMsg = speechError.getErrorDescription();
            if (!TextUtils.isEmpty(errMsg)) {
                updateTalkMsg(errMsg, null);
                speechSynthesize(NoteApp.getAppContext(), errMsg);
            }
            showTip(speechError.getPlainDescription(true));
            Log.e("error:", speechError.getPlainDescription(true));
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    public void setParam(SpeechRecognizer mIat) {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        /**
         * mandarin: 普通话
         * cantonese: 粤语
         * henanese: 河南话
         * en_us: 英语
         */
        String lag = "mandarin";
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    //听写监听器
    private RecognizerListener mRecoListener = new RecognizerListener() {
        //听写结果回调接口(返回Json格式结果，用户可参见附录12.1)；
        //一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
        //关于解析Json的代码可参见MscDemo中JsonParser类；
        //isLast等于true时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d("Result", results.getResultString());
            printResult(results);
        }

        //会话发生错误回调接口
        public void onError(SpeechError error) {
            String errMsg = error.getErrorDescription();
            if (!TextUtils.isEmpty(errMsg)) {
                updateRobotText(mWidgetManager, errMsg);
                speechSynthesize(NoteApp.getAppContext(), errMsg);
            }
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
        }

        //开始录音
        public void onBeginOfSpeech() {
            showTip("开始说话");
        }

        //结束录音
        public void onEndOfSpeech() {
            showTip("结束说话");
        }

        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        mResultList.add(text);

        StringBuffer sb = new StringBuffer();
        for (String r : mResultList) {
            if (!TextUtils.isEmpty(r)) {
                sb.append(r);
            }
        }
        String finalText = sb.toString();
        if (!TextUtils.isEmpty(finalText)) {
            String showText = "我听到了，你说的是“" + finalText + "”吗？";
            updateRobotText(mWidgetManager, showText);
            updateUserText(mWidgetManager, finalText);
            speechSynthesize(NoteApp.getAppContext(), showText);
        }
    }
}
