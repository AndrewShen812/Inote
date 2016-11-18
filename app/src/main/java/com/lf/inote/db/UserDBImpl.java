/**
 * @项目名称：INote
 * @文件名：UserDBImpl.java
 * @版本信息：
 * @日期：2015-2-11
 * @Copyright 2015 www.517na.com Inc. All rights reserved.
 */
package com.lf.inote.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.lf.inote.constant.Constants;
import com.lf.inote.model.User;
import com.na517.finaldb.FinalDb;
import com.na517.finaldb.FinalDb.DbUpdateListener;

/**
 * @项目名称：INote
 * @类名称：UserDBImpl
 * @类描述：
 * @创建人：lianfeng
 * @创建时间：2015-2-11 下午9:22:57
 * @修改人：lianfeng
 * @修改时间：2015-2-11 下午9:22:57
 * @修改备注：
 * @version
 */
public class UserDBImpl {
    private FinalDb mDB = null;
    
    private static UserDBImpl mInstance;
    
    /** 控制整个日志数据库操作的锁对象 */
    private static byte[] mLock = new byte[0];
    
    /**
     * @description 获得实例，单例模式
     * @date 2015-2-11
     * @param context
     * @return
     */
    public static UserDBImpl getInstance(Context context) {
        if (null == mInstance) {
            synchronized (mLock) {
                if (mInstance == null) {
                    mInstance = new UserDBImpl(context);
                }
            }
        }
        
        return mInstance;
    }
    
    private UserDBImpl(Context context) {
        mDB = FinalDb.create(context, Constants.DB_NAME, Constants.DEBUG, Constants.DB_VERSION, new DbUpdateListener() {
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            }
        });
    }
    
    /**
     * @description 插入用户
     * @date 2015-2-11
     * @param User
     */
    public void insert(User user) {
        mDB.save(user);
    }
    
    /**
     * @description 删除指定用户
     * @date 2015-2-11
     * @param User
     */
    public void delete(User user) {
        mDB.delete(user);
    }
    
    /**
     * @description 删除全部
     * @date 2015-2-11
     */
    public void deleteAll() {
        mDB.deleteAll(User.class);
    }
    
    /**
     * @description 查找指定用户
     * @date 2015-2-11
     * @param User
     * @return
     */
    public User find(User user) {
        return mDB.findById(user.get_id(), User.class);
    }
    
    public ArrayList<User> findAll() {
        return (ArrayList<User>) mDB.findAll(User.class);
    }
    
    /**
     * @description 更新指定用户
     * @date 2015-2-11
     * @param User
     */
    public void update(User user) {
        mDB.update(user);
    }
}
