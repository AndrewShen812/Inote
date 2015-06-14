/** 
 * @项目名称：INote   
 * @文件名：NoteListAdapter.java    
 * @版本信息：
 * @日期：2015-2-11    
 * @Copyright 2015 www.517na.com Inc. All rights reserved.         
 */
package com.lf.utils.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lf.inote.R;
import com.lf.model.Note;

/**    
 *     
 * @项目名称：INote    
 * @类名称：NoteListAdapter    
 * @类描述：    
 * @创建人：lianfeng    
 * @创建时间：2015-2-11 上午11:06:22    
 * @修改人：lianfeng    
 * @修改时间：2015-2-11 上午11:06:22    
 * @修改备注：    
 * @version     
 *     
 */
@SuppressLint("InflateParams")
public class NoteListAdapter extends ArrayListAdapter<Note>{

    private Context mContext;
    
    private class ItemView {
        /**标题*/
        TextView title;
        /**修改时间*/
        TextView time;
        /**内容简要*/
        TextView brief;
    }
       
    /**    
     * 创建一个新的实例 NoteListAdapter.    
     *    
     * @param context    
     */
    public NoteListAdapter(Activity context) {
        super(context);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemView item = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.note_list_item, null);
            item = new ItemView();
            item.title = (TextView) convertView.findViewById(R.id.note_item_title);
            item.time = (TextView) convertView.findViewById(R.id.note_item_modify_time);
            item.brief = (TextView) convertView.findViewById(R.id.note_item_brief);
            convertView.setTag(item);
        }
        else {
            item = (ItemView) convertView.getTag();
        }
        // init data
        Note note = mList.get(position);
        item.title.setText(note.getTitle());
        item.time.setText(note.getModifyTime());
//        if (BRIEF_LEN > note.getContent().length()) {
//            item.brief.setText(note.getContent());
//        }
//        else {
//            item.brief.setText(note.getContent().substring(0, BRIEF_LEN) + "...");
//        }
        item.brief.setText(note.getContent());
        return convertView;
    }
    
}
