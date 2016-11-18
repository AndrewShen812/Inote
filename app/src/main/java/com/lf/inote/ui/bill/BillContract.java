/**
 * Project name：Inote
 * Create time：2016/11/17 18:33
 * Copyright: 2016 GALAXYWIND Network Systems Co.,Ltd.All rights reserved.
 */
package com.lf.inote.ui.bill;

import com.lf.inote.ui.base.MVPView;
import com.lf.inote.model.Bill;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by sy on 2016/11/17.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2016/11/17 18:33<br>
 * Revise Record:<br>
 * 2016/11/17: 创建并完成初始实现<br>
 */
public interface BillContract {

	interface BillView extends MVPView<Presenter> {
		boolean isViewActive();
		void showNoBill();
		void showBillByDay(Map<String, ArrayList<Bill>> billMap);
		void showBillByMonth(Map<String, ArrayList<Bill>> billMap);
		void showBillByYear(Map<String, ArrayList<Bill>> billMap);
		void showTotalBill(double income, double expense, double balance);
		void showIncomeAndExpense(String date, double income, double expense);
	}

	interface Presenter {
		void getBillByDay();
		void getBillByMonth();
		void getBillByYear();
		void getTotalBill();
		int getRawBillSize();
		void getIncomeAndExpense(String date, ArrayList<Bill> Bills);
		
		void addBill();
		void editBill();
	}
}
