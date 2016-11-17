/** 
 * @项目名称：INote   
 * @文件名：NoteApp.java    
 * @版本信息：
 * @日期：2015-2-11    
 * @Copyright 2015 www.517na.com Inc. All rights reserved.         
 */
package com.lf.inote;

import android.app.Application;

import com.lf.model.User;

/**    
 *     
 * @项目名称：INote    
 * @类名称：NoteApp    
 * @类描述：    
 * @创建人：lianfeng    
 * @创建时间：2015-2-11 下午9:31:23    
 * @修改人：lianfeng    
 * @修改时间：2015-2-11 下午9:31:23    
 * @修改备注：    
 * @version     
 *     
 */
public class NoteApp extends Application{
    
    /** 应用实例 */
    private static NoteApp mInstance;
    
    /** 缓存当前登陆的用户 */
    private User user;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        mInstance = this;
        user = new User("", "");
    }
    
    public static NoteApp getInstance() {
        return mInstance;
    }
    
    /**    
     * curUser    
     *    
     * @return  the curUser    
     * @since   CodingExample Ver(编码范例查看) 1.0    
     */
    
    public User getUser() {
        return user;
    }

    /**    
     * @param curUser the curUser to set    
     */
    public void setUser(User curUser) {
        this.user = curUser;
    }
}
