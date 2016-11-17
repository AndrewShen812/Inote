/** 
 * @项目名称：INote   
 * @文件名：BillDBImpl.java    
 * @版本信息：
 * @日期：2015-2-25    
 * @Copyright 2015 www.517na.com Inc. All rights reserved.         
 */
package com.lf.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lf.constant.Constants;
import com.lf.model.Bill;
import com.na517.finaldb.FinalDb;
import com.na517.finaldb.FinalDb.DbUpdateListener;

/**    
 *     
 * @项目名称：INote    
 * @类名称：BillDBImpl    
 * @类描述：    
 * @创建人：lianfeng    
 * @创建时间：2015-2-25 下午5:11:28    
 * @修改人：lianfeng    
 * @修改时间：2015-2-25 下午5:11:28    
 * @修改备注：    
 * @version     
 *     
 */
public class BillDBImpl {
    private FinalDb mDB = null;
    
    private static BillDBImpl mInstance;
    
    private BillDBImpl(Context context) {
        mDB = FinalDb.create(context, Constants.DB_NAME, Constants.DEBUG, Constants.DB_VERSION, new DbUpdateListener() {
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            }
        });
    }
    
    public static BillDBImpl getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new BillDBImpl(context);
        }
        
        return mInstance;
    }
    
    public void insert(Bill bill) {
        mDB.save(bill);
    }
    
    public void insert(ArrayList<Bill> bills) {
        try {
            mDB.getDb().beginTransaction();
            for(int i=0; i<bills.size(); i++) {
                insert(bills.get(i));
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
    public void delete(Bill bill) {
        mDB.delete(bill);
    }
    
    /**
     * @description 删除全部
     * @date 2015-2-11
     */
    public void deleteAll() {
        mDB.deleteAll(Bill.class);
    }
    
    /**
     * @description 查找指定Note
     * @date 2015-2-11
     * @param note
     * @return
     */
    public Bill find(Bill bill) {
        return mDB.findById(bill.get_id(), Bill.class);
    }
    
    public ArrayList<Bill> findAll() {
        return (ArrayList<Bill>) mDB.findAll(Bill.class);
    }
    
    /**
     * @description 更新指定Note
     * @date 2015-2-11
     * @param note
     */
    public void update(Bill bill) {
        mDB.update(bill);
    }
    
    public void update(ArrayList<Bill> bills) {
        try {
            mDB.getDb().beginTransaction();
            for(int i=0; i<bills.size(); i++) {
                update(bills.get(i));
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
