/** 
 * @项目名称：INote   
 * @文件名：Bill.java    
 * @版本信息：
 * @日期：2015-2-25    
 * @Copyright 2015 www.517na.com Inc. All rights reserved.         
 */
package com.lf.inote.model;

import com.na517.finaldb.annotation.sqlite.Id;
import com.na517.finaldb.annotation.sqlite.Property;
import com.na517.finaldb.annotation.sqlite.Table;
import com.na517.finaldb.annotation.sqlite.Transient;

import java.io.Serializable;

/**    
 *     
 * @项目名称：INote    
 * @类名称：Bill    
 * @类描述：    
 * @创建人：lianfeng    
 * @创建时间：2015-2-25 下午5:00:24    
 * @修改人：lianfeng    
 * @修改时间：2015-2-25 下午5:00:24    
 * @修改备注：    
 * @version     
 *     
 */
@Table(name = "bill")
public class Bill implements Serializable {
    
    /**    
     * serialVersionUID
     *    
     * @since Ver 1.1    
     */    
    @Transient
    private static final long serialVersionUID = 1L;

    /** 唯一标识 */
    @Id(column = "_id")
    private int _id;
    
    /** 日期 */
    @Property(column = "date")
    private String date;
    
    /** 1-支出；2-收入 */
    @Property(column = "type")
    private int type;
    
    /** 金额 */
    @Property(column = "money")
    private double money;
    
    /** 备注 */
    @Property(column = "remark")
    private String remark;
    
    public Bill() {
        
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
     * date    
     *    
     * @return  the date    
     * @since   CodingExample Ver(编码范例查看) 1.0    
     */
    
    public String getDate() {
        return date;
    }

    /**    
     * @param date the date to set    
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**    
     * type    
     *    
     * @return  the type    
     * @since   CodingExample Ver(编码范例查看) 1.0    
     */
    
    public int getType() {
        return type;
    }

    /**    
     * @param type the type to set    
     */
    public void setType(int type) {
        this.type = type;
    }

    /**    
     * money    
     *    
     * @return  the money    
     * @since   CodingExample Ver(编码范例查看) 1.0    
     */
    
    public double getMoney() {
        return money;
    }

    /**    
     * @param money the money to set    
     */
    public void setMoney(double money) {
        this.money = money;
    }

    /**    
     * remark    
     *    
     * @return  the remark    
     * @since   CodingExample Ver(编码范例查看) 1.0    
     */
    
    public String getRemark() {
        return remark;
    }

    /**    
     * @param remark the remark to set    
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
}
