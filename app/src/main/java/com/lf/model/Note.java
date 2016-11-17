/** 
 * @项目名称：INote   
 * @文件名：Note.java    
 * @版本信息：
 * @日期：2015-2-10    
 * @Copyright 2015 www.517na.com Inc. All rights reserved.         
 */
package com.lf.model;

import com.na517.finaldb.annotation.sqlite.Id;
import com.na517.finaldb.annotation.sqlite.Property;
import com.na517.finaldb.annotation.sqlite.Table;
import com.na517.finaldb.annotation.sqlite.Transient;

import java.io.Serializable;

/**    
 *     
 * @项目名称：INote    
 * @类名称：Note    
 * @类描述：笔记    
 * @创建人：lianfeng    
 * @创建时间：2015-2-10 下午5:35:20    
 * @修改人：lianfeng    
 * @修改时间：2015-2-10 下午5:35:20    
 * @修改备注：    
 * @version     
 *     
 */
@Table(name = "note")
public class Note implements Serializable {

    /**    
     * serialVersionUID
     *    
     * @since Ver 1.1    
     */    
    @Transient
    private static final long serialVersionUID = 1L;
    
    @Id(column = "_id")
    private int _id;    // Id字段为Integer等类型时才能实现自动自增
    
    @Property(column = "author")
    private String author;
    
    @Property(column = "title")
    private String title;
    
    @Property(column = "content")
    private String content;
    
    @Property(column = "createTime")
    private String createTime;
    
    @Property(column = "modifyTime")
    private String modifyTime;
    
    @Transient
    private boolean isSelected;
    
    /**    
     * isSelected    
     *    
     * @return  the isSelected    
     * @since   CodingExample Ver(编码范例查看) 1.0    
     */
    
    public boolean isSelected() {
        return isSelected;
    }

    /**    
     * @param isSelected the isSelected to set    
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Note() {
        
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
     * author    
     *    
     * @return  the author    
     * @since   CodingExample Ver(编码范例查看) 1.0    
     */
    
    public String getAuthor() {
        return author;
    }

    /**    
     * @param author the author to set    
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**    
     * title    
     *    
     * @return  the title    
     * @since   CodingExample Ver(编码范例查看) 1.0    
     */
    
    public String getTitle() {
        return title;
    }

    /**    
     * @param title the title to set    
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**    
     * content    
     *    
     * @return  the content    
     * @since   CodingExample Ver(编码范例查看) 1.0    
     */
    
    public String getContent() {
        return content;
    }

    /**    
     * @param content the content to set    
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**    
     * createTime    
     *    
     * @return  the createTime    
     * @since   CodingExample Ver(编码范例查看) 1.0    
     */
    
    public String getCreateTime() {
        return createTime;
    }

    /**    
     * @param createTime the createTime to set    
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**    
     * modifyTime    
     *    
     * @return  the modifyTime    
     * @since   CodingExample Ver(编码范例查看) 1.0    
     */
    
    public String getModifyTime() {
        return modifyTime;
    }

    /**    
     * @param modifyTime the modifyTime to set    
     */
    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }
}
