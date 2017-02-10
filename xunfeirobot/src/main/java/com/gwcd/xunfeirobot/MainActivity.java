package com.gwcd.xunfeirobot;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.UnderstanderResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView mTvRobot;
    private TextView mTvUser;
    private ImageView mIVPseech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvRobot = (TextView) findViewById(R.id.tv_app_widget_item_robot);
        mTvUser = (TextView) findViewById(R.id.tv_app_widget_item_user);
        mIVPseech = (ImageView) findViewById(R.id.iv_app_widget_speech);
    }

    private void updateTalkMsg(String robotMsg, String userMsg) {
        if (!TextUtils.isEmpty(robotMsg)) {
            mTvRobot.setText(robotMsg);
        }
        if (!TextUtils.isEmpty(userMsg)) {
            mTvUser.setText(userMsg);
        }
    }

    public void onSpeechInput(View view) {
        understander(MainActivity.this);
    }

    private Toast mToast = null;

    private void showTip(final String str) {
        if (mToast == null) {
            mToast = Toast.makeText(RobotApp.getAppContext(), "", Toast.LENGTH_SHORT);
        }
        mToast.setText(str);
        mToast.show();
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
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        understander.setParameter(SpeechConstant.VAD_BOS, "8000");
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
                String robotText = null;
                if (resultJson.has(KEY_RC) && RC_SUCCESS == resultJson.getInt(KEY_RC)
                        && resultJson.has(KEY_ANSWER)) {
                    JSONObject answer = resultJson.getJSONObject(KEY_ANSWER);
                    robotText = answer.getString(KEY_TEXT);
                }
                if (robotText == null) {
                    String format = RobotApp.getAppContext().getString(R.string.text_widget_not_understand);
                    robotText = String.format(format, text);
                }
                updateTalkMsg(RobotApp.getAppContext().getString(R.string.text_widget_understanding), text);
//                updateTalkMsg(robotText, text);
                speechSynthesize(RobotApp.getAppContext(), robotText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            String errMsg = speechError.getErrorDescription();
            if (!TextUtils.isEmpty(errMsg)) {
                updateTalkMsg(RobotApp.getAppContext().getString(R.string.text_widget_understanding), null);
//                updateTalkMsg(errMsg, null);
                speechSynthesize(RobotApp.getAppContext(), errMsg);
            }
            showTip(speechError.getPlainDescription(true));
            Log.e("error:", speechError.getPlainDescription(true));
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };


    /************ 语音合成 ************/
    private long mSynthesizeStart = 0;
    private long total = 0;
    private int tryTimes;
    private String mLastSource;
    /** 语音合成器 */
    private SpeechSynthesizer mSpeechSynthesizer;

    private void speechSynthesize(Context context, String source) {
        if (TextUtils.isEmpty(source)) {
            return;
        }
        mLastSource = new String(source);
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
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            showTip("缓冲进度：" + percent + "%");
        }

        //开始播放
        public void onSpeakBegin() {
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
}
