package com.gwcd.xunfeirobot;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import iflytek.speech.util.JsonParser;

public class RecognizeFileActivity extends AppCompatActivity implements View.OnTouchListener {

    private TextView mTvRobot;
    private TextView mTvUser;
    private ImageView mIVPseech;

    private static final String TAG = "RobotApp";
    private AudioRecorder mRecorder;
    private SpeechRecognizer mSpeechRecognizer;
    private static final String CACHE_PATH = Environment.getExternalStorageDirectory().toString()
            + File.separator + "speechVoice/record.pcm";
    private AudioRecordView mAudioView;
    private boolean permissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize_file);
        initViews();

        mRecorder = AudioRecorder.getInstance();
        mRecorder.setRecordListener(new AudioRecorder.OnRecordListener() {
            @Override
            public void onStartRecording() {}

            @Override
            public void onStopRecording() {
                //3.开始听写
                isSessionOver = false;
                mSpeechRecognizer.startListening(mRecoListener);
                writeAudioToRecogniser();
            }

            @Override
            public void onGetVolume(int volume, float zcr) {
                mAudioView.addData(volume, zcr);
            }
        });
        if (checkPermission()) {
            permissionGranted = true;
            mRecorder.setCacheFile(CACHE_PATH);
        } else {
            requestPermission();
        }
        initRecogniser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (permissionGranted) {
            mRecorder.startRecording();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRecorder.stopRecording();
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static final int REQUEST_PERM_CODE = 1000;

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                REQUEST_PERM_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERM_CODE) {
            if (grantResults.length > 0) {
                int grantedCnt = 0;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        grantedCnt ++;
                    }
                }
                if (grantedCnt == grantResults.length) {
                    permissionGranted = true;
                    mRecorder.setCacheFile(CACHE_PATH);
                    mRecorder.startRecording();
                } else {
                    permissionGranted = false;
                    showTip("未授权相关权限");
                }
            } else {
                permissionGranted = false;
                showTip("未授权相关权限");
            }
        }
    }

    private boolean isSessionOver;
    private void writeAudioToRecogniser() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(CACHE_PATH);
                    byte[] buffer = new byte[1024];
                    int readSize;
                    while ((readSize = fis.read(buffer)) > 0 && !isSessionOver) {
                        mSpeechRecognizer.writeAudio(buffer, 0, readSize);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!isSessionOver) {
                        mSpeechRecognizer.stopListening();
                    }
                }
            }
        }).start();
    }

    private void initViews() {
        setTitle("端点检测");
        mTvRobot = (TextView) findViewById(R.id.tv_app_widget_item_robot);
        mTvUser = (TextView) findViewById(R.id.tv_app_widget_item_user);
        mIVPseech = (ImageView) findViewById(R.id.iv_app_widget_speech);
        mAudioView = (AudioRecordView) findViewById(R.id.audio_chart);
        mIVPseech.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() != R.id.iv_app_widget_speech) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                mRecorder.startRecording();
                return true;
            case MotionEvent.ACTION_UP:
//                mRecorder.stopRecording();
                return true;
        }
        return false;
    }

    private void updateTalkMsg(String robotMsg, String userMsg) {
        if (!TextUtils.isEmpty(robotMsg)) {
            mTvRobot.setText(robotMsg);
        }
        if (!TextUtils.isEmpty(userMsg)) {
            mTvUser.setText(userMsg);
        }
    }

    private Toast mToast = null;
    private void showTip(final String str) {
        boolean show = true;
        if (!show) {
            return;
        }
        if (mToast == null) {
            mToast = Toast.makeText(RobotApp.getAppContext(), "", Toast.LENGTH_SHORT);
        }
        mToast.setText(str);
        mToast.show();
    }

    private void initRecogniser() {
        mSpeechRecognizer = SpeechRecognizer.createRecognizer(this, null);
        //2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        setParam(mSpeechRecognizer);
    }

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
        mIat.setParameter(SpeechConstant.VAD_BOS, "1000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
        /**
         * -1, 写音频流；-2：写文件
         */
        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
//        mIat.setParameter(SpeechConstant.ASR_SOURCE_PATH, CACHE_PATH);
    }

    //听写监听器
    private RecognizerListener mRecoListener = new RecognizerListener() {
        //听写结果回调接口(返回Json格式结果，用户可参见附录12.1)；
        //一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
        //关于解析Json的代码可参见MscDemo中JsonParser类；
        //isLast等于true时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d("Result", results.getResultString());
            printResult(results, isLast);
        }

        //会话发生错误回调接口
        public void onError(SpeechError error) {
            String errMsg = error.getErrorDescription();
            if (!TextUtils.isEmpty(errMsg)) {
                updateTalkMsg(errMsg, null);
            }
            showTip(error.getPlainDescription(true));
            isSessionOver = true;
            mRecorder.startRecording();
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
            isSessionOver = true; // 已经通过VAD检测到了写入数据的静音末端
        }

        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

    private StringBuffer mBuffer = new StringBuffer();
    private void printResult(RecognizerResult results, boolean isLast) {
        String text = JsonParser.parseIatResult(results.getResultString());
        if (!TextUtils.isEmpty(text)) {
            mBuffer.append(text);
        }

        if (!isLast) {
            return;
        }
        isSessionOver = true;
        mRecorder.startRecording();
        String finalText = mBuffer.toString();
        mBuffer.setLength(0);
        if (!TextUtils.isEmpty(finalText)) {
            String showText = "我听到了，你说的是“" + finalText + "”吗？";
            updateTalkMsg(showText, finalText);
        }
    }
}
