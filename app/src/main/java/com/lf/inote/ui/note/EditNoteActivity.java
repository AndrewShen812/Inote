package com.lf.inote.ui.note;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.speech.VoiceRecognitionService;
import com.lf.inote.NoteApp;
import com.lf.inote.R;
import com.lf.inote.db.NoteDBImpl;
import com.lf.inote.model.Note;
import com.lf.inote.ui.Constant;
import com.lf.inote.ui.MainActivity;
import com.lf.inote.utils.StringUtils;
import com.lf.inote.utils.TimeUtil;
import com.lf.inote.view.NoteEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class EditNoteActivity extends Activity implements RecognitionListener {
    
    private static final String DEF_TITLE = "未命名笔记";
    
    private EditText mEtTitle;
    
    private NoteEditText mEtNote;
    
    private Note mNote;
    
    private boolean isNewNote;
    
    private String mCreateTime;
    
    private Context mContext;

    private SpeechRecognizer mSpeechRecognizer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_note);
        
        mContext = this;

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
        mSpeechRecognizer.setRecognitionListener(this);
        
        initView();
        initData();
    }
    
    /**
     * @description 初始化控件
     * @date 2015-2-11
     */
    private void initView() {
        mEtTitle = (EditText) findViewById(R.id.et_note_title);
        mEtNote = (NoteEditText) findViewById(R.id.et_note_content);
        ((ImageView) findViewById(R.id.iv_add_note_speech)).setColorFilter(getResources().getColor(R.color.light_blue));
    }
    
    /**
     * @description 初始化显示数据
     * @date 2015-2-11
     */
    private void initData() {
        mNote = (Note) getIntent().getSerializableExtra(MainActivity.NOTE);
        if (null == mNote) {
            mEtTitle.setText("");
            mEtNote.setText("");
            isNewNote = true;
            mCreateTime = TimeUtil.getCurrentDateTime(null);
        }
        else {
            mEtTitle.setText(mNote.getTitle());
            mEtNote.setText(mNote.getContent());
            isNewNote = false;
        }
    }
    
    /**
     * 返回键处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String title = mEtTitle.getText().toString();
            String content = mEtNote.getText().toString();
            if (!StringUtils.isEmpty(title)) {
                if (isNewNote) {
                    mNote = new Note();
                    mNote.setTitle(title);
                    mNote.setContent(content);
                    mNote.setCreateTime(mCreateTime);
                    mNote.setModifyTime(TimeUtil.getCurrentDateTime(null));
                    mNote.setAuthor(NoteApp.getInstance().getUser().getUserName());
                    NoteDBImpl.getInstance(mContext).insert(mNote);
                }
                else {
                    if (title.equals(mNote.getTitle()) && content.equals(mNote.getContent())) {
                        // 无任何修改
                    }
                    else {
                        mNote.setTitle(title);
                        mNote.setContent(content);
                        mNote.setModifyTime(TimeUtil.getCurrentDateTime(null));
                        mNote.setAuthor(NoteApp.getInstance().getUser().getUserName());
                        NoteDBImpl.getInstance(mContext).update(mNote);
                    }
                }
            }
            else {
                if (isNewNote && !StringUtils.isEmpty(content)) {
                    // 添加默认标题
                    mNote = new Note();
                    mNote.setTitle(DEF_TITLE);
                    mNote.setContent(content);
                    mNote.setCreateTime(mCreateTime);
                    mNote.setModifyTime(TimeUtil.getCurrentDateTime(null));
                    mNote.setAuthor(NoteApp.getInstance().getUser().getUserName());
                    NoteDBImpl.getInstance(mContext).insert(mNote);
                }
                else if(!isNewNote && !StringUtils.isEmpty(content)){
                    mNote.setTitle(DEF_TITLE);
                    mNote.setContent(content);
                    mNote.setModifyTime(TimeUtil.getCurrentDateTime(null));
                    mNote.setAuthor(NoteApp.getInstance().getUser().getUserName());
                    NoteDBImpl.getInstance(mContext).update(mNote);
                }
                else if (isNewNote && StringUtils.isEmpty(content)) {
                    Toast.makeText(mContext, "取消编辑笔记", Toast.LENGTH_SHORT).show();
                }
            }
            setResult(RESULT_OK);
            finish();
        }
        
        return true;
    }

    private static final int REQUEST_UI = 1;

    public void onSpeechInput(View view) {
        Intent intent = new Intent("com.baidu.action.RECOGNIZE_SPEECH");
        bindParams(intent);
        startActivityForResult(intent, REQUEST_UI);
    }

    public void bindParams(Intent intent) {
        intent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
        intent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
        intent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
        intent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
        intent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);

        intent.putExtra(Constant.EXTRA_INFILE, "");
        intent.putExtra(Constant.EXTRA_LANGUAGE, "cmn-Hans-CN");
        intent.putExtra(Constant.EXTRA_NLU, "enable");

        intent.putExtra(Constant.EXTRA_OFFLINE_ASR_BASE_FILE_PATH, "/sdcard/easr/s_1");
        intent.putExtra(Constant.EXTRA_LICENSE_FILE_PATH, "/sdcard/easr/license-tmp-20150530.txt");
        intent.putExtra(Constant.EXTRA_OFFLINE_SLOT_DATA, buildTestSlotData());
    }

    private String buildTestSlotData() {
        JSONObject slotData = new JSONObject();
        JSONArray name = new JSONArray().put("李涌泉").put("郭下纶");
        JSONArray song = new JSONArray().put("七里香").put("发如雪");
        JSONArray artist = new JSONArray().put("周杰伦").put("李世龙");
        JSONArray app = new JSONArray().put("手机百度").put("百度地图");
        JSONArray usercommand = new JSONArray().put("关灯").put("开门");
        try {
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_NAME, name);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_SONG, song);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_ARTIST, artist);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_APP, app);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_USERCOMMAND, usercommand);
        } catch (JSONException e) {

        }
        return slotData.toString();
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        sb.append(":" + error);
        Toast.makeText(mContext, sb.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        mEtNote.setText(nbest.get(0));
        Toast.makeText(mContext, "识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            onResults(data.getExtras());
        }
    }
}
