/** 
 * @项目名称：INote   
 * @文件名：SortUtils.java    
 * @版本信息：
 * @日期：2015-5-29    
 * @Copyright 2015 www.517na.com Inc. All rights reserved.         
 */
package com.lf.inote.utils;

import com.lf.inote.model.Bill;

import java.util.Comparator;

/**    
 *     
 * @项目名称：INote    
 * @类名称：SortUtils    
 * @类描述：    
 * @创建人：lianfeng    
 * @创建时间：2015-5-29 上午10:08:24    
 * @修改人：lianfeng    
 * @修改时间：2015-5-29 上午10:08:24    
 * @修改备注：    
 * @version     
 *     
 */
public class SortUtils {

	/**
	 * 根据时间进入排序
	 * @return
	 */
	public static Comparator<Bill> getComparatorTimeDesc() {
		return new Comparator<Bill>() {
			@Override
			public int compare(Bill lhs, Bill rhs) {
				String lhsTime = lhs.getDate();
				long lhsValue = TimeUtil.parseStringtoDate(lhsTime, "yyyy-MM-dd").getTime();
				String rhsTime = rhs.getDate();
				long rhsValue = TimeUtil.parseStringtoDate(rhsTime, "yyyy-MM-dd").getTime();
				if(lhsValue < rhsValue) {
					return 1;
				}else if(lhsValue == rhsValue){
					return 0;
				}
				return -1;
			}
		};
	}
	
	/**
	 * 根据时间进入排序
	 * @return
	 */
	public static Comparator<Bill> getComparatorTimeAsc() {
		return new Comparator<Bill>() {
			@Override
			public int compare(Bill lhs, Bill rhs) {
				String lhsTime = lhs.getDate();
				long lhsValue = TimeUtil.parseStringtoDate(lhsTime, "yyyy-MM-dd").getTime();
				String rhsTime = rhs.getDate();
				long rhsValue = TimeUtil.parseStringtoDate(rhsTime, "yyyy-MM-dd").getTime();
				if(lhsValue > rhsValue) {
					return 1;
				}else if(lhsValue == rhsValue){
					return 0;
				}
				return -1;
			}
		};
	}
}
