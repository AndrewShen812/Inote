package com.gwcd.xunfeirobot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.EventManager;
import com.gwcd.speech.WuSpeechFactory;
import com.gwcd.speech.WuSpeechListener;
import com.gwcd.speech.WuSpeechRecognizer;
import com.gwcd.speech.control.AirConController;
import com.gwcd.speech.control.WuSpeechControllerManager;
import com.gwcd.speech.semantic.StringItem;
import com.gwcd.speech.semantic.types.ItemType;
import com.gwcd.speech.utils.SpeechLog;
import com.gwcd.speech.wakeup.WuWakeUpUtility;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import java.util.ArrayList;
import java.util.List;

public class SematicActivity extends AppCompatActivity {
    private TextView mTvRobot;
    private TextView mTvUser;
    private ImageView mIVPseech;

    private EventManager mWpEventManager;

    private static final String TAG = "RobotApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromWakeUp = getIntent().getBooleanExtra("fromWakeUp", false);

        mTvRobot = (TextView) findViewById(R.id.tv_app_widget_item_robot);
        mTvUser = (TextView) findViewById(R.id.tv_app_widget_item_user);
        mIVPseech = (ImageView) findViewById(R.id.iv_app_widget_speech);

        setTitle("唤醒和语义测试");

        WuSpeechControllerManager ctrlManager = WuSpeechControllerManager.getInstance();
        ctrlManager.registerController(mAirConController);
    }

    private boolean fromWakeUp;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("--debug onNewIntent:");
        fromWakeUp = intent.getBooleanExtra("fromWakeUp", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        WuWakeUpUtility.getInstance().stopWakeUpListening();
        System.out.println("--debug onResume:");
        System.out.println("--debug TaskId:" + this.getTaskId());
        System.out.println("--debug fromWakeUp:" + fromWakeUp);
        if (fromWakeUp) {
//            onSpeechInput(null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        WuWakeUpUtility.getInstance().startWakeUpListening();
    }

    private void updateTalkMsg(String robotMsg, String userMsg) {
        if (!TextUtils.isEmpty(robotMsg)) {
            mTvRobot.setText(robotMsg);
        }
        if (!TextUtils.isEmpty(userMsg)) {
            mTvUser.setText(userMsg);
        }
    }

    private StringItem[] getCommunityInfoFromRes() {
        String[] resArr = getResources().getStringArray(R.array.community_info_array);
        List<StringItem> items = new ArrayList<>();
        ItemType itemType = ItemType.ITEM_NONE;
        for (String line : resArr) {
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("[设备信息]")) {
                System.out.println("skip line:" + line);
                itemType = ItemType.ITEM_DEV_NICK_NAME;
                continue;
            } else if (line.startsWith("[标签信息]")) {
                System.out.println("skip line:" + line);
                itemType = ItemType.ITEM_TAG_NAME;
                continue;
            } else if (line.startsWith("[情景信息]")) {
                System.out.println("skip line:" + line);
                itemType = ItemType.ITEM_SCENE;
                continue;
            }
            System.out.println("parse line:" + line + ", itemType:" + itemType.toString());
            items.add(new StringItem(line, itemType, 0L, 0, 0));
        }
        StringItem[] itemArr = new StringItem[items.size()];
        items.toArray(itemArr);

        return itemArr;
    }

    public void onSpeechInput(View view) {
        WuSpeechRecognizer wuSpeechRecognizer = WuSpeechFactory.createSpeechRecognizer(
                SematicActivity.this, getCommunityInfoFromRes());
        wuSpeechRecognizer.setWuSpeechListener(new WuSpeechListener() {
            @Override
            public void onStart() {
                showTip("开始说话吧");
            }

            @Override
            public void onVolumeChanged(int volume) {
                showTip("正在说话，当前音量：" + volume);
            }

            @Override
            public void onEnd() {
                showTip("结束说话");
            }

            @Override
            public void onTextResult(String text) {
                updateTalkMsg(RobotApp.getAppContext().getString(R.string.text_widget_understanding), text);
            }

            @Override
            public void onResult(String text) {
//                StringBuffer sb = new StringBuffer("");
//                if (array != null && !array.isEmpty()) {
//                    speechSynthesize(RobotApp.getAppContext(), text + ", 识别结果如下");
//                    for (StringMatchResult result : array) {
//                        result.dumpInfo();
//                        sb.append(result.dumpInfo());
//                        sb.append("\n");
//                    }
//                    updateTalkMsg(sb.toString(), null);
//                } else {
//                    speechSynthesize(RobotApp.getAppContext(), text + ", 无法识别的命令");
//                    updateTalkMsg(text + ", 无法识别的命令", null);
//                }

            }

            @Override
            public void onError(int errCode, String errMsg) {
                if (!TextUtils.isEmpty(errMsg)) {
                    speechSynthesize(RobotApp.getAppContext(), errMsg);
                    updateTalkMsg(errMsg, null);
                }
            }
        });
        wuSpeechRecognizer.startListening();
    }

    private AirConController mAirConController = new AirConController() {
        @Override
        public void setPower(Bundle data) {
            SpeechLog.d("setPower called.");
            updateTalkMsg("已执行设置空调开关", null);
        }

        @Override
        public void setMode(Bundle data) {
            SpeechLog.d("setMode called.");
            updateTalkMsg("已执行设置空调模式", null);
        }

        @Override
        public void setTemp(Bundle data) {
            SpeechLog.d("setTemp called.");
            updateTalkMsg("已执行设置空调温度", null);
        }

        @Override
        public void setWind(Bundle data) {
            SpeechLog.d("setWind called.");
            updateTalkMsg("已执行设置空调风速", null);
        }
    };

    private Toast mToast = null;

    private void showTip(final String str) {
        if (mToast == null) {
            mToast = Toast.makeText(RobotApp.getAppContext(), "", Toast.LENGTH_SHORT);
        }
        mToast.setText(str);
        mToast.show();
    }

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