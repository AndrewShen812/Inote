package com.lf.inote;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.lf.db.NoteDBImpl;
import com.lf.model.Note;
import com.lf.utils.StringUtils;
import com.lf.utils.TimeUtil;
import com.lf.view.NoteEditText;

public class EditNoteActivity extends Activity {
    
    private static final String DEF_TITLE = "未命名笔记";
    
    private EditText mEtTitle;
    
    private NoteEditText mEtNote;
    
    private Note mNote;
    
    private boolean isNewNote;
    
    private String mCreateTime;
    
    private Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_note);
        
        mContext = this;
        
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
}
