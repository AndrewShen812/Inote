package com.gwcd.xunfeirobot;

import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gwcd.speech.WuSpeechFactory;
import com.gwcd.speech.semantic.types.ItemType;
import com.gwcd.speech.semantic.SemanticUtils;
import com.gwcd.speech.semantic.StringItem;
import com.gwcd.speech.semantic.StringMatchResult;
import com.gwcd.speech.semantic.WuSemanticEngine;
import com.gwcd.speech.utils.SpeechLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TextSemanticActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtInfoFile;
    private EditText mEtInFile;
    private Button mBtnStart;
    private TextView mTvLog;

    private String mStorage;
    private String mDefName;
    private String mInfoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_semantic);

        setTitle("文本语义解析测试");
        mEtInfoFile = (EditText) findViewById(R.id.et_info_file);
        mEtInFile = (EditText) findViewById(R.id.et_input_file);
        mBtnStart = (Button) findViewById(R.id.btn_start);
        mTvLog = (TextView) findViewById(R.id.tv_log);
        mBtnStart.setOnClickListener(this);

        mStorage = Environment.getExternalStorageDirectory().toString() + File.separator + "com.gwcd.semantic";
        mDefName = "语音测试样本lxq.txt";
        mInfoName = "语音测试家庭信息.txt";

        File dir = new File(mStorage);
        if (!dir.exists()) {
            dir.mkdir();
        }
        mEtInFile.setText(mStorage + File.separator + mDefName);
        mEtInfoFile.setText(mStorage + File.separator + mInfoName);
        prepareTestFile();
    }

    private void prepareTestFile() {
        try {
            File infoFile = new File(mStorage + File.separator + mInfoName);
            if (!infoFile.exists()) {
                infoFile.createNewFile();
                String[] infos = getResources().getStringArray(R.array.community_info_array);
                stringArray2File(infos, infoFile);
            }
            File inFile = new File(mStorage + File.separator + mDefName);
            if (!inFile.exists()) {
                inFile.createNewFile();
                String[] infos = getResources().getStringArray(R.array.test_input_array);
                stringArray2File(infos, inFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stringArray2File(final String[] items, final File outFile) {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (items == null || !outFile.exists()) {
                        return;
                    }
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
                    for (String item : items) {
                        writer.write(item);
                        writer.newLine();
                    }
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        mTvLog.setFocusable(true);
        mTvLog.setFocusableInTouchMode(true);
        mTvLog.requestFocus();
        mTvLog.setText("");
        onClickStart();
    }

    private Toast mToast;

    private void showTips(String tips) {
        if (mToast == null) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(tips);
        mToast.show();
    }



    private void onClickStart() {
        String infoPath = mEtInfoFile.getText().toString().trim();
        if (TextUtils.isEmpty(infoPath)) {
            showTips("请输入家庭信息文件路径");
            return;
        }
        String inputPath = mEtInFile.getText().toString().trim();
        if (TextUtils.isEmpty(inputPath)) {
            showTips("请输入样本文件路径");
            return;
        }
        File infoFile = new File(infoPath);
        if (!infoFile.exists() || !infoFile.isFile()) {
            showTips("家庭信息文件不存在");
            return;
        }
        File file = new File(inputPath);
        if (!file.exists() && !file.isFile()) {
            showTips("样本文件不存在");
            return;
        }
        startParse(infoFile, file);
    }

    public static final int MSG_RESULT = 0;
    public static final int MSG_ERR = 1;
    private android.os.Handler updateHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ERR:
                    showTips((String) msg.obj);
                    break;
                case MSG_RESULT:
                    String r = (String) msg.obj;
                    if (!TextUtils.isEmpty(r)) {
                        mTvLog.append(r);
                    }
                    break;
            }
        }
    };

    private void startParse(final File infoFile, final File file){
        new Thread () {
            @Override
            public void run() {
                try {
                    String inName = file.getName();
                    int index = inName.lastIndexOf('.');
                    File outFile = new File(mStorage + File.separator + inName.substring(0, index) + "_测试结果" + inName.substring(index));
                    if (!outFile.exists()) {
                        outFile.createNewFile();
                    }

                    Message msg1 = updateHandler.obtainMessage(MSG_RESULT, "输出文件：" + outFile.getPath() + "\n\ndebug输出:\n");
                    updateHandler.sendMessage(msg1);
                    WuSemanticEngine semanticEngine = WuSpeechFactory.createSemanticEngine(TextSemanticActivity.this, getComInfosFromFile(infoFile));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"));
                    writer.write("数据源:\n" + infoFile.getPath());
                    writer.newLine();
                    writer.write(file.getPath());
                    writer.newLine();
                    writer.write("解析时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    writer.newLine();
                    StringBuffer fileBuffer = new StringBuffer("");
                    int total = 0, ok = 0, fail = 0;
                    String line;
                    List<String> lines = new ArrayList<>();
                    int maxLen = 0;
                    while ((line = reader.readLine()) != null) {
                        if (!line.isEmpty()) {
                            lines.add(line);
                            int strLen = StringUtils.getLength(line);
                            if (strLen > maxLen) {
                                maxLen = strLen;
                            }
                        }
                    }
                    for (String l : lines) {
                        total++;
                        List<StringMatchResult> items = semanticEngine.getResult(l);
                        StringBuffer uiBuffer = new StringBuffer("");
                        StringBuffer testBuffer = new StringBuffer("");
                        uiBuffer.append(l  + "  解析结果：--------------------\n");
                        int curLen = StringUtils.getLength(l);
                        String space = getSpace(curLen, maxLen);
                        if (items != null && !items.isEmpty() && SemanticUtils.isMatchResultValid(items)) {
                            for (StringMatchResult item : items) {
                                uiBuffer.append(item.dumpInfo());
                                uiBuffer.append("\n");
                            }
                            fileBuffer.append(l + space + "[ok  ]\t\t" + SemanticUtils.orderedDumpInfo(items));
                            testBuffer.append(l + space + "[ok  ]\t\t" + SemanticUtils.orderedDumpInfo(items));
                            ok++;
                        } else {
                            uiBuffer.append("无法解析的操作\n");
                            fileBuffer.append(l + space + "[fail]\t\t" + SemanticUtils.orderedDumpInfo(items));
                            testBuffer.append(l + space + "[fail]\t\t" + SemanticUtils.orderedDumpInfo(items));
                            fail++;
                        }
                        fileBuffer.append("\n");
                        testBuffer.append("\n");
                        uiBuffer.append("\n");
                        if (uiBuffer.length() > 0) {
                            Message msg = updateHandler.obtainMessage(MSG_RESULT, uiBuffer.toString());
                            updateHandler.sendMessage(msg);
                            SpeechLog.d("format:" + testBuffer.toString());
                        }
                    }
                    reader.close();
                    writer.write("统计结果:\t\t" + "总数:" + total + "\t\t成功:" + ok + "\t\t失败:" + fail);
                    writer.newLine();
                    writer.newLine();
                    writer.write("详细信息:");
                    writer.newLine();
                    writer.append(fileBuffer.toString());
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    final String strmsg = e.getMessage();
                    Message msg = updateHandler.obtainMessage(MSG_ERR, "抱歉，操作失败。" + strmsg);
                    updateHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    @NonNull
    private String getSpace(int curLen, int maxLen) {
        StringBuffer sb = new StringBuffer("  ");
        if (curLen == maxLen) {
            return sb.toString();
        }
        int remain = maxLen - curLen;
        for(int i =0 ; i< remain ;i++){
            sb.append(" ");
        }

        return sb.toString();
    }

    private StringItem[] getComInfosFromFile(File infoFile) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(infoFile)));
        String line;
        List<StringItem> items = new ArrayList<>();
        ItemType itemType = ItemType.ITEM_NONE;
        while ((line = reader.readLine()) != null) {
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
}
