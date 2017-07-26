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
import java.util.LinkedList;

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
    private Thread mRecordThread;
    public static final int MSG_START_RECORDING = 10086;
    public static final int MSG_STOP_RECORDING = 10087;
    public static final int MSG_REPORT_VOLUME = 10088;
    public static final int CACHE_SIZE = 3;
    private LinkedList<byte[]> mPreCache;
    private LinkedList<byte[]> mSufCache;

    private AudioRecorder() {
        mPreCache = new LinkedList<>();
        mSufCache = new LinkedList<>();
        initAudioRecord();
    }

    /** 录音监听接口 */
    public interface OnRecordListener {
        void onStartRecording();
        void onStopRecording();
        void onGetVolume(int volume, float zcr);
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
                        mRecordListener.onGetVolume(msg.arg1, Float.valueOf((String) msg.obj));
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
        mRecordThread = new Thread(this);
        mRecordThread.start();
    }

    public void stopRecording() {
        isRecording = false;
//        if (mRecordThread != null) {
//            mRecordThread.interrupt();
//        }
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
    private static final float T_ZL = 0.2f;
    /**过零率高门限*/
    private static final float T_ZH = 0.3f;
    /** 静音判断结束阀值，ms
     * 参考：http://blog.csdn.net/ffmpeg4976/article/details/52349007
     * 高灵敏度：200~400ms左右，短词识别；低灵敏度：1500~3000ms，长句、有停顿的识别
     * */
    private static final int T_SILENCE = 800;

    /** 采集3~4秒的静音短能量平均值作为阀值 */
    private static final int SILENCE_SAMPLE_SIZE = 100;
    private int mSilenceSampleCnt = 0;
    private int mSilenceVolTotal = 0;
    private int mSilenceAvgVol = T_EL;

    private float mSilenceZcrTotal = 0;
    private float mSilenceAvgZcr = T_ZL;

    private class FrameCache {
        byte[] data;
    }

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
        boolean writePreCache = true;
        boolean isWaitingSufCache = false;
        while (isRecording) {
            //实际读取的数据长度，一般会小于buffersize
            int readSize = mAudioRecord.read(buffer, 0, mMinBufferSize);
            if (readSize < 0) {
                break;
            }
            long durationMs = (long) (readSize * 1f / (SAMPLE_RATE * 16 / 8) * 1000);
            double volume = getVolume(buffer, readSize);
            float zcr = zcr(buffer, readSize);
//            float zcr = zcr2(buffer, readSize);
            if (volume == 0 && zcr == 0) {
                continue;
            }
            if (isWaitingSufCache) {
                if (mSufCache.size() >= CACHE_SIZE) {
                    // 已经获取到结尾额外的3帧数据
                    break;
                }
                setCacheData(mSufCache, buffer, readSize);
                continue;
            }
            Log.d(TAG, "vad音量:" + volume);
            Message msg = mMsgHandler.obtainMessage(MSG_REPORT_VOLUME);
            msg.arg1 = (int) volume;
            msg.obj = String.valueOf(zcr);
            msg.sendToTarget();
            /********************* 端点检测 *********************/
            boolean writeFile = false;
            // 暂时不处理分帧
            Log.d(TAG, "vad能量：" + getEnergy(buffer, readSize));
            Log.d(TAG, "vad过零率：" + zcr);
            if (voiceBegin) {
                if (volume > getTEL() || zcr > getTZL()) {
                    Log.d(TAG, "vv还在说话……");
                    silenceTime = 0; // 在说，还没有结束
                    writeFile = true;
                } else {
                    silenceTime += durationMs;
                    if (silenceTime >= T_SILENCE) {
                        Log.d(TAG, "vv说完了");
                        // 结束之后多检测3帧数据
                        isWaitingSufCache = true;
                        continue;
                    }
                }
            } else {
                if (volume > getTEH() || zcr > getTZH()) {
                    Log.d(TAG, "vv开始说话了！");
                    voiceBegin = true;
                    writeFile = true;
                } else {
                    Log.d(TAG, "vv没有说话");
                    doSilenceSample(volume, zcr);
                    setCacheData(mPreCache, buffer, readSize);
                }
            }
            if (writeFile && dos != null) {
                try {
                    if (writePreCache) { // 是否需要写前置的额外3帧数据
                        writePreCache = false;
                        writeCacheData(mPreCache, dos);
                    }
                    dos.write(buffer, 0, readSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            if (dos != null) {
                writeCacheData(mSufCache, dos);
                dos.flush();
                dos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopRecording();
        releaseRecorder();
        mMsgHandler.sendEmptyMessage(MSG_STOP_RECORDING);
    }

    private void setCacheData(LinkedList<byte[]> cacheList, byte[] orgData, int orgSize) {
        byte[] cacheBuf = new byte[orgSize];
        System.arraycopy(orgData, 0, cacheBuf, 0, orgSize);
        if (cacheList.size() == CACHE_SIZE) {
            cacheList.removeFirst();
        }
        cacheList.add(cacheBuf);
    }

    private void writeCacheData(LinkedList<byte[]> cacheList, DataOutputStream dos) {
        try {
            for (int i = 0; i < cacheList.size(); i++) {
                byte[] cache = cacheList.get(i);
                if (cache != null) {
                    dos.write(cache, 0, cache.length);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 获取短时能量动态低阀值 */
    private int getTEL() {
        return mSilenceAvgVol == T_EL ? T_EL : (mSilenceAvgVol + 5);
    }

    private int getTEH() {
        return getTEL() + 5;
    }

    /** 获取过零率动态低阀值 */
    private float getTZL() {
        return mSilenceSampleCnt == T_ZL ? T_ZL : (mSilenceAvgZcr + 0.08f);
    }

    private float getTZH() {
        return getTZL() + 0.15f;
    }

    /**
     * 静音段采集工作
     * @param volume
     * @param zcr
     */
    private void doSilenceSample(double volume, float zcr) {
        if (mSilenceSampleCnt < SILENCE_SAMPLE_SIZE) {
            mSilenceSampleCnt++;
            mSilenceVolTotal += volume;
            mSilenceAvgVol = mSilenceVolTotal / mSilenceSampleCnt;
            mSilenceZcrTotal += zcr;
            mSilenceAvgZcr = mSilenceZcrTotal / mSilenceSampleCnt;
        }
        if (mSilenceSampleCnt >= SILENCE_SAMPLE_SIZE) {
            mSilenceSampleCnt = 0;
            mSilenceVolTotal = 0;
            mSilenceAvgVol = T_EL;
            mSilenceZcrTotal = 0;
            mSilenceAvgZcr = T_ZL;
        }
        Log.d(TAG, "avg mSilenceSampleCnt:" + mSilenceSampleCnt);
        Log.d(TAG, "avg mSilenceAvgVol:" + mSilenceAvgVol);
        Log.d(TAG, "avg mSilenceAvgZcr:" + mSilenceAvgZcr);
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

    private double getEnergy(byte[] buffer, int readSize){
        long sumEnergy = 0;
        for(int i = 0; i < buffer.length && i < readSize; i+=2){
            sumEnergy += buffer[i] * buffer[i];
        }
        return sumEnergy;
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

    private short byte2Short(byte low, byte high) {
        int v1 = low & 0xFF;
        int v2 = high & 0xFF;
        short temp = (short) ((v2 << 8) | v1);// 小端
        return temp;
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
        for (int i = 1; i < data.length - 2 && i < readSize; i += 2) {
            byte d1 = data[i];
            byte d2 = data[i + 2];
            zcr += Math.abs(sgn(d2) - sgn(d1));
        }
        return (zcr / 2 * 1f) / (readSize / 2);
    }

    private float zcr2(byte[] data, int readSize) {
        int zcr = 0;
        for (int i = 0; i < data.length - 2 && i < readSize; i += 2) {
            byte d1L = data[i];
            byte d1H = data[i + 1];
            byte d2L = data[i + 2];
            byte d2H = data[i + 3];
            short d1 = byte2Short(d1L, d1H);
            short d2 = byte2Short(d2L, d2H);
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


