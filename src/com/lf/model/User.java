/** 
 * @项目名称：INote   
 * @文件名：User.java    
 * @版本信息：
 * @日期：2015-2-11    
 * @Copyright 2015 www.517na.com Inc. All rights reserved.         
 */
package com.lf.model;

import java.io.Serializable;

import com.na517.finaldb.annotation.sqlite.Id;
import com.na517.finaldb.annotation.sqlite.Property;
import com.na517.finaldb.annotation.sqlite.Table;
import com.na517.finaldb.annotation.sqlite.Transient;

/**    
 *     
 * @项目名称：INote    
 * @类名称：User    
 * @类描述：    
 * @创建人：lianfeng    
 * @创建时间：2015-2-11 下午8:01:13    
 * @修改人：lianfeng    
 * @修改时间：2015-2-11 下午8:01:13    
 * @修改备注：    
 * @version     
 *     
 */
@Table(name = "user")
public class User implements Serializable {

    /**    
     * serialVersionUID:TODO（用一句话描述这个变量表示什么）    
     *    
     * @since Ver 1.1    
     */    
    @Transient
    private static final long serialVersionUID = 1L;
    
    @Id(column = "Id")
    private int _id;    // Id字段为Integer等类型时才能实现自动自增
    
    @Property(column = "UserName")
    private String userName;
    
    @Property(column = "AccessToken")
    private String accessToken;

    public User() {
        
    }
    
    /**
     * 创建一个新的实例 User.    
     *    
     * @param name 用户名
     * @param token 
     */
    public User(String name, String token) {
        this.userName = name;
        this.accessToken = token;
    }
    
    /**    
     * _id    
     *    
     * @return  the _id    
     * @since   CodingExample Ver(编码范例查看) 1.0    
     */
    
    public int get_id() {
        return _id;
    }

    /**    
     * @param _id the _id to set    
     */
    public void set_id(int _id) {
        this._id = _id;
    }

    /**    
     * userName    
     *    
     * @return  the userName    
     * @since   CodingExample Ver(编码范例查看) 1.0    
     */
    
    public String getUserName() {
        return userName;
    }

    /**    
     * @param userName the userName to set    
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**    
     * accessToken    
     *    
     * @return  the accessToken    
     * @since   CodingExample Ver(编码范例查看) 1.0    
     */
    
    public String getAccessToken() {
        return accessToken;
    }

    /**    
     * @param accessToken the accessToken to set    
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
