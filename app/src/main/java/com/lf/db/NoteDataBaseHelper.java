/** 
 * @项目名称：INote   
 * @文件名：NoteDataBase.java    
 * @版本信息：
 * @日期：2015-2-12    
 * @Copyright 2015 www.517na.com Inc. All rights reserved.         
 */
package com.lf.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**    
 *     
 * @项目名称：INote    
 * @类名称：NoteDataBase    
 * @类描述：    
 * @创建人：lianfeng    
 * @创建时间：2015-2-12 下午2:50:10    
 * @修改人：lianfeng    
 * @修改时间：2015-2-12 下午2:50:10    
 * @修改备注：    
 * @version     
 *     
 */
public class NoteDataBaseHelper  extends SQLiteOpenHelper {
    // 用户数据库文件的版本
    private final static int DB_VERSION = 1;
    
    private final static String DB_NAME = "note_db.db";
    
    public NoteDataBaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    
    public NoteDataBaseHelper(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    
    /** 该函数是在第一次创建的时候执行， 实际上是第一次得到SQLiteDatabase对象的时候才会调用这个方法 */
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }
    
    private void createTable(SQLiteDatabase db) {
        
        String noteTable = "CREATE TABLE IF NOT EXISTS note (_id INTEGER PRIMARY KEY AUTOINCREMENT,author TEXT,title TEXT,content TEXT,createTime TEXT,modifyTime TEXT)";
        db.execSQL(noteTable);
        
        String userTable = "CREATE TABLE IF NOT EXISTS user (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,accessToken TEXT)";
        db.execSQL(userTable);
    }
}
