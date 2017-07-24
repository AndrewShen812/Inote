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

import static android.os.Build.VERSION_CODES.N;

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
    private FFT mFFT;

    private AudioRecorder() {
        mFFT = new FFT();
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
        while (isRecording) {
            //实际读取的数据长度，一般会小于buffersize
            int readSize = mAudioRecord.read(buffer, 0, mMinBufferSize);
            Log.d(TAG, "readSize:" + readSize);
            if (readSize < 0) {
                Log.e(TAG, "read error!");
                break;
            }
            long durationMs = (long) (readSize * 1f / (SAMPLE_RATE * 16 / 8) * 1000);
            double volume = getVolume(buffer, readSize);
            Message msg = mMsgHandler.obtainMessage(MSG_REPORT_VOLUME);
            msg.arg1 = (int) volume;
            msg.sendToTarget();
            Log.d(TAG, "分贝值:" + volume + ", 时长：" + durationMs);
            // TODO: 2017/7/19 端点检测
            /** 短时能量、短时过零率、窗函数 */
            float[] fftIO = new float[FFT.FFT_N];
            for (int i = 0; i < readSize; i++) {
                fftIO[i] = buffer[i];
            }
            mFFT.calculate(fftIO);
            boolean writeFile = false;
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
//        releaseRecorder();
        mMsgHandler.sendEmptyMessage(MSG_STOP_RECORDING);
    }

    private double getVolume(byte[] buffer, int readSize){
        double sumVolume = 0.0;
        double avgVolume;
        double volume;
        for(int i = 0; i < buffer.length && i < readSize; i+=2){
            int v1 = buffer[i] & 0xFF;
            int v2 = buffer[i + 1] & 0xFF;
            int temp = v1 + (v2 << 8);// 小端
            if (temp >= 0x8000) {
                temp = 0xffff - temp;
            }
            sumVolume += Math.abs(temp);
        }
        avgVolume = sumVolume / readSize / 2;
        volume = Math.log10(1 + avgVolume) * 10;
        return volume;
    }

    private static final double PI = 3.14159265358979323846;
    /**
     * 窗函数。使用hamming窗。<br>
     *     定义：w(n) = 0.54 - 0.46 * (2 * PI * n / (N - 1)) , 0<=n<=N-1<br>
     *     ？N表示窗长度？
     * @return
     */
    private double window(int n) {
        return 0.54 - 0.46 * Math.cos((2 * Math.PI * n / (N - 1)));
    }
}


