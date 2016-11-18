/**
 * @项目名称：INote
 * @文件名：CityDBImpl.java
 * @版本信息：
 * @日期：2015-2-10
 * @Copyright 2015 www.517na.com Inc. All rights reserved.
 */
package com.lf.inote.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lf.inote.constant.Constants;
import com.lf.inote.model.Note;
import com.na517.finaldb.FinalDb;
import com.na517.finaldb.FinalDb.DbUpdateListener;

import java.util.ArrayList;

/**
 * @项目名称：INote
 * @类名称：CityDBImpl
 * @类描述：
 * @创建人：lianfeng
 * @创建时间：2015-2-10 上午9:24:17
 * @修改人：lianfeng
 * @修改时间：2015-2-10 上午9:24:17
 * @修改备注：
 * @version
 */
public class NoteDBImpl {
    
    private FinalDb mDB = null;
    
    private static NoteDBImpl mInstance;
    
    /** 控制整个数据库操作的锁对象 */
    private static byte[] mLock = new byte[0];
    
    /**
     * @description 获得实例，单例模式
     * @date 2015-2-11
     * @param context
     * @return
     */
    public static NoteDBImpl getInstance(Context context) {
        if (null == mInstance) {
            synchronized (mLock) {
                if (mInstance == null) {
                    mInstance = new NoteDBImpl(context);
                }
            }
        }
        
        return mInstance;
    }
    
    private NoteDBImpl(Context context) {
        mDB = FinalDb.create(context, Constants.DB_NAME, Constants.DEBUG, Constants.DB_VERSION, new DbUpdateListener() {
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            }
        });
    }
    
    /** 
     * @description 插入Note
     * @date 2015-2-11
     * @param note
     */
    public void insert(Note note) {
        mDB.save(note);
    }
    
    public void insertNotes(ArrayList<Note> notes) {
        try {
            mDB.getDb().beginTransaction();
            for(int i=0; i<notes.size(); i++) {
                insert(notes.get(i));
            }
            mDB.getDb().setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.i("LF", "Exception:" + e.getMessage());
            e.printStackTrace();
        }
        finally {
            mDB.getDb().endTransaction();
        }
    }
    
    /**
     * @description 删除指定Note
     * @date 2015-2-11
     * @param note
     */
    public void delete(Note note) {
        mDB.delete(note);
    }
    
    /**
     * @description 删除全部
     * @date 2015-2-11
     */
    public void deleteAll() {
        mDB.deleteAll(Note.class);
    }
    
    /**
     * @description 查找指定Note
     * @date 2015-2-11
     * @param note
     * @return
     */
    public Note find(Note note) {
        return mDB.findById(note.get_id(), Note.class);
    }
    
    public ArrayList<Note> findAll() {
        return (ArrayList<Note>) mDB.findAll(Note.class);
    }
    
    /**
     * @description 更新指定Note
     * @date 2015-2-11
     * @param note
     */
    public void update(Note note) {
        mDB.update(note);
    }
    
    public void update(ArrayList<Note> notes) {
        try {
            mDB.getDb().beginTransaction();
            for(int i=0; i<notes.size(); i++) {
                update(notes.get(i));
            }
            mDB.getDb().setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.i("LF", "Exception:" + e.getMessage());
            e.printStackTrace();
        }
        finally {
            mDB.getDb().endTransaction();
        }
    }
}
