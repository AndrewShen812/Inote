package com.gwcd.xunfeirobot;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by sy on 2017/7/19.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/7/19 9:05<br>
 * Revise Record:<br>
 * 2017/7/19: 创建并完成初始实现<br>
 */

public class AudioRecorder implements Runnable{
    private static final String TAG = "AudioRecorder";
    private static final int SAMPLE_RATE = 16000;

    private static AudioRecorder mInstance;
    private AudioRecord mAudioRecord;
    private boolean initSuccess = false;
    private volatile boolean isRecording = false;
    private int mMinBufferSize;
    private File mCacheFile;
    private String mCachePath;
    public static final int MSG_START_RECORDING = 10086;
    public static final int MSG_STOP_RECORDING = 10087;
    public static final int MSG_REPORT_VOLUME = 10088;

    private AudioRecorder() {
        initAudioRecord();
    }

    /** 录音监听接口 */
    public interface OnRecordListener {
        void onStartRecording();
        void onStopRecording();
        void onGetVolume(int volume);
    }

    private OnRecordListener mRecordListener;

    public void setRecordListener(OnRecordListener mRecordListener) {
        this.mRecordListener = mRecordListener;
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_STOP_RECORDING:
                    if (mRecordListener != null) {
                        mRecordListener.onStopRecording();
                    }
                    break;
                case MSG_REPORT_VOLUME:
                    if (mRecordListener != null) {
                        mRecordListener.onGetVolume(msg.arg1);
                    }
                    break;
            }
        }
    }
    private MyHandler mMsgHandler = new MyHandler();

    private void initAudioRecord() {
        if (mAudioRecord == null) {
            try {
                mMinBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                Log.d(TAG, "init mMinBufferSize:" + mMinBufferSize);
                if (AudioRecord.ERROR_BAD_VALUE == mMinBufferSize || AudioRecord.ERROR == mMinBufferSize) {
                    Log.e(TAG, "init exception: get getMinBufferSize.");
                    return;
                }
                /**
                 * 讯飞识别要求：16bit，16000hz，单声道
                 */
                mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, mMinBufferSize);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Log.e(TAG, "init exception:" + e.getMessage());
            }
        }
        initSuccess = null != mAudioRecord;
    }

    public void setCacheFile(String filePath) {
        if (TextUtils.isEmpty(filePath) || !filePath.endsWith(".pcm")) {
            throw new IllegalArgumentException("filePath null or not ends with .pcm");
        }
        mCachePath = filePath;
        initCacheFile();
    }

    private void initCacheFile() {
        if (mCacheFile == null) {
            mCacheFile = new File(mCachePath);
        }
        try {
            File parent = mCacheFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (mCacheFile.exists()) {
                mCacheFile.delete();
            }
            mCacheFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            mCacheFile = null;
            Log.e(TAG, "setCacheFile exception:" + e.getMessage());
        }
    }


    public static AudioRecorder getInstance() {
        if (mInstance == null) {
            synchronized (AudioRecorder.class) {
                if (mInstance == null) {
                    mInstance = new AudioRecorder();
                }
            }
        }

        return mInstance;
    }

    public void startRecording() {
        if (!initSuccess) {
            initAudioRecord();
            if (!initSuccess) {
                return;
            }
        }
        if (isRecording) {
            return;
        }
        initCacheFile();
        isRecording = true;
        new Thread(this).start();
    }

    public void stopRecording() {
        isRecording = false;
    }

    private void releaseRecorder() {
        mAudioRecord.release();
        mAudioRecord = null;
        initSuccess = false;
    }

    /**短时能量低门限*/
    private static final int T_EL = 20;
    /**短时能量高门限*/
    private static final int T_EH = 24;
    /**过零率低门限*/
    private static final float T_ZL = 0.4f;
    /**过零率高门限*/
    private static final float T_ZH = 0.45f;
    /** 静音判断结束阀值，ms */
    private static final int T_SILENCE = 120;

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        try {
            mAudioRecord.startRecording();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.e(TAG, "startRecording exception:" + e.getMessage());
            isRecording = false;
        }
        byte[] buffer = new byte[mMinBufferSize];
        DataOutputStream dos = null;
        if (mCacheFile != null) {
            try {
                dos = new DataOutputStream(new FileOutputStream(mCacheFile, true));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        boolean voiceBegin = false;
        int silenceTime = 0;
        int startTime = 0;
        while (isRecording) {
            //实际读取的数据长度，一般会小于buffersize
            int readSize = mAudioRecord.read(buffer, 0, mMinBufferSize);
            Log.d(TAG, "readSize:" + readSize);
            if (readSize < 0) {
                Log.e(TAG, "read error!");
                break;
            }
//            /**
//             * 经测试，每次正常读取1280字节，16000Hz、16bit、单声道配置下，正好是40ms。
//             * 分帧的帧长取30ms，帧移10ms，每次采集的数据正好可以分成两帧处理
//             *
//             */
            long durationMs = (long) (readSize * 1f / (SAMPLE_RATE * 16 / 8) * 1000);
            double volume = getVolume(buffer, readSize);
            Log.d(TAG, "vad音量:" + volume);
            Message msg = mMsgHandler.obtainMessage(MSG_REPORT_VOLUME);
            msg.arg1 = (int) volume;
            msg.sendToTarget();
            /********************* 端点检测 *********************/
            boolean writeFile = false;
            /** 短时能量、短时过零率、窗函数 */
            // 暂时不处理分帧
//            double[] enfOut = enFrame(buffer, readSize);
            float zcr = zcr(buffer, readSize);
            Log.d(TAG, "vad过零率：" + zcr);
            if (voiceBegin) {
                if (volume > T_EL || zcr > T_ZL) {
                    Log.d(TAG, "还在说话……");
                    silenceTime = 0; // 在说，还没有结束
                    writeFile = true;
                } else {
                    silenceTime += durationMs;
                    if (silenceTime >= T_SILENCE) {
                        Log.d(TAG, "说完了");
                        break;
                    }
                }
            } else {
                // FIXME: || or && ?
                if (volume > T_EH && zcr > T_ZH) {
                    Log.d(TAG, "开始说话了！");
                    voiceBegin = true;
                    writeFile = true;
                } else {
                    Log.d(TAG, "没有说话");
                }
            }
            if (writeFile && dos != null) {
                try {
                    dos.write(buffer, 0, readSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            if (dos != null) {
                dos.flush();
                dos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopRecording();
        mMsgHandler.sendEmptyMessage(MSG_STOP_RECORDING);
    }

    private double getVolume(byte[] buffer, int readSize){
        double sumVolume = 0.0;
        double avgVolume;
        double volume;
        for(int i = 0; i < buffer.length && i < readSize; i+=2){
            sumVolume += Math.abs(byte2Float(buffer[i], buffer[i + 1]));
        }
        avgVolume = sumVolume / readSize / 2;
        volume = Math.log10(1 + avgVolume) * 10;
        return volume;
    }

    private double getEnergy(double[] buffer){
        long sumVolume = 0;
        long avgVolume;
        for(int i = 0; i < buffer.length; i++){
            short data = (short) buffer[i];
            sumVolume += data * data;
        }
        avgVolume = sumVolume / buffer.length;
        return avgVolume;
    }

    private double byte2Float(int low, byte high) {
        int v1 = low & 0xFF;
        int v2 = high & 0xFF;
        int temp = v1 + (v2 << 8);// 小端
        if (temp >= 0x8000) {
            temp = 0xffff - temp;
        }
        return Math.abs(temp);
    }

    /**
     * 分帧加窗
     * @param rawBuffer
     * @param readSize
     * @return
     */
    private double[] enFrame(byte[] rawBuffer, int readSize) {
        double[] out = new double[readSize / 2];
        int oi = 0;
        for (int i = 0; i < readSize; i += 2) {
            out[oi] = byte2Float(rawBuffer[i], rawBuffer[i + 1]);
            oi++;
        }
        Log.d(TAG, "原：" + Arrays.toString(out));
        for (int i = 0; i < out.length; i++) {
            out[i] = out[i] * window(i, out.length);
        }
        Log.d(TAG, "窗：" + Arrays.toString(out));

        return out;
    }

    /**
     * 过零率（zero-crossing rate）计算。数学定义：<br>
     *     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     *     N-1<br>
     *     Zn = 1/2 * ∑ | sgn[Xn(m)] - sgn[Xn(m-1)] |<br>
     *     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     *     m=0<br>
     *     式中，sgn[]是符号函数
     */
    private float zcr(byte[] data, int readSize) {
        int zcr = 0;
        for (int i = 0; i < data.length - 2 && i < readSize; i += 2) {
            byte d1 = data[i];
            byte d2 = data[i + 2];
            zcr += Math.abs(sgn(d2) - sgn(d1));
        }
        return (zcr / 2 * 1f) / (readSize / 2);
    }

    /**
     * 符号函数。定义：如果x>=0，则sgn(x)=1，否则sgn(x)=-1。
     * @param sample
     * @return
     */
    private int sgn(short sample) {
        return sample >= 0 ? 1 : -1;
    }

    /**
     * 窗函数。使用hamming窗。<br>
     *     定义：w(n) = 0.54 - 0.46 * (2 * PI * n / (N - 1)) , 0<=n<=N-1<br>
     *     ？N表示窗长度？
     * @return
     */
    private double window(int n, int N) {
        return 0.54 - 0.46 * Math.cos((2 * Math.PI * n / (N - 1)));
    }
}


