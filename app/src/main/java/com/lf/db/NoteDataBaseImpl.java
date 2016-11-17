/**
 * @项目名称：INote
 * @文件名：NoteDataBaseImpl.java
 * @版本信息：
 * @日期：2015-2-12
 * @Copyright 2015 www.517na.com Inc. All rights reserved.
 */
package com.lf.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lf.model.Note;

/**
 * @项目名称：INote
 * @类名称：NoteDataBaseImpl
 * @类描述：
 * @创建人：lianfeng
 * @创建时间：2015-2-12 下午2:50:38
 * @修改人：lianfeng
 * @修改时间：2015-2-12 下午2:50:38
 * @修改备注：
 * @version
 */
public class NoteDataBaseImpl {
    
    private static NoteDataBaseImpl mInstance;
    
    private Context mContext;
    
    private NoteDataBaseHelper mDbHelper;
    
    /**
     * 创建一个新的实例 NoteDataBaseImpl.    
     *    
     * @param context
     */
    private NoteDataBaseImpl(Context context) {
        mContext = context;
        mDbHelper = new NoteDataBaseHelper(context);
    }
    
    /**
     * @description 获得实例，单例模式
     * @date 2015-2-13
     * @param context
     * @return
     */
    public static NoteDataBaseImpl getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new NoteDataBaseImpl(context);
        }
        
        return mInstance;
    }
    
    public void insertNote(Note note) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = constructNoteValue(note);
            db.insert("note", null, values);
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
            db = null;
        }
    }
    
    public void insertNote(ArrayList<Note> notes) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < notes.size(); i++) {
                ContentValues values = constructNoteValue(notes.get(i));
                db.insert("note", null, values);
                values = null;
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
            db = null;
        }
    }
    
    public void deleteNote(Note note) {
        
    }
    
    public void deleteAll() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("note", null, null);
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
            db = null;
        }
    }
    
    public void updateNote(Note note) {
        
    }
    
    public void updateNote(ArrayList<Note> notes) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for(int i=0; i<notes.size(); i++) {
                ContentValues values = constructNoteValue(notes.get(i));
                db.update("note", values, "_id=?", new String[] {notes.get(i).get_id() + ""});
                values = null;
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.i("LF", "Exception:" + e.getMessage());
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
            db = null;
        }
    }
    
    public ArrayList<Note> getAllNote() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        ArrayList<Note> notes = new ArrayList<Note>();
        try {
            Cursor c = db.rawQuery("select * from note", null);
            if (c != null) {
                try {
                    while (c.moveToNext()) {
                        notes.add(destructNoteValue(c));
                    }
                }
                catch (Exception e) {
                    Log.e("LF", "getAllNote Exception: " + e.getMessage());
                    e.printStackTrace();
                }
                finally {
                    c.close();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            db.close();
        }
        return notes;
    }
    
    /**
     * @description 构造含有Note信息的ContentValues对象
     * @date 2015-2-13
     * @param note
     * @return
     */
    private ContentValues constructNoteValue(Note note) {
        ContentValues values = new ContentValues();
        values.put("author", note.getAuthor());
        values.put("title", note.getTitle());
        values.put("content", note.getContent());
        values.put("createTime", note.getCreateTime());
        values.put("modifyTime", note.getModifyTime());
        
        return values;
    }
    
    /**
     * @description 从数据库查询游标中解析出Note信息
     * @date 2015-2-13
     * @param c
     * @return
     */
    private Note destructNoteValue(Cursor c) {
        Note note = new Note();
        note.setAuthor(c.getString(c.getColumnIndex("author")));
        note.setContent(c.getString(c.getColumnIndex("content")));
        note.setTitle(c.getString(c.getColumnIndex("title")));
        note.setCreateTime(c.getString(c.getColumnIndex("createTime")));
        note.setModifyTime(c.getString(c.getColumnIndex("modifyTime")));
        
        return note;
    }
}
