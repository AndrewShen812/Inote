/**
 * Project name：Inote
 * Create time：2016/11/17 18:52
 * Copyright: 2016 GALAXYWIND Network Systems Co.,Ltd.All rights reserved.
 */
package com.lf.inote.ui.bill;

import com.lf.inote.NoteApp;
import com.lf.inote.db.BillDBImpl;
import com.lf.inote.model.Bill;
import com.lf.inote.utils.SortUtils;
import com.lf.inote.utils.TimeUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sy on 2016/11/17.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2016/11/17 18:52<br>
 * Revise Record:<br>
 * 2016/11/17: 创建并完成初始实现<br>
 */
public class BillPresenter implements BillContract.Presenter {

	private static final String DATE = "yyyy-MM-dd";

	private BillContract.BillView mBillView;
	private ArrayList<Bill> mBillRawData = new ArrayList<Bill>();

	public BillPresenter(BillContract.BillView billView) {
		this.mBillView = billView;
		mBillView.setPresenter(this);
	}

	private boolean checkNoBill() {
		if (mBillRawData == null || mBillRawData.isEmpty()) {
			mBillView.showNoBill();
			return true;
		}

		return false;
	}

	@Override
	public void getBillByDay() {
		mBillRawData = BillDBImpl.getInstance(NoteApp.getAppContext()).findAll();
		if (!checkNoBill()) {
			Collections.sort(mBillRawData, SortUtils.getComparatorTimeAsc());
			// 按天分组
			String date = "";
			Map<String, ArrayList<Bill>> mDayBillData = new HashMap<String, ArrayList<Bill>>();
			mDayBillData.clear();
			ArrayList<Bill> dayBillList = null;
			for (int i = 0; i < mBillRawData.size(); i++) {
				Bill bill = mBillRawData.get(i);
				if (!date.equals(bill.getDate())) {
					date = bill.getDate();
					dayBillList = new ArrayList<Bill>();
					for (int j = 0; j < mBillRawData.size(); j++) {
						if (mBillRawData.get(j).getDate().equals(date)) {
							dayBillList.add(mBillRawData.get(j));
						}
					}
					mDayBillData.put(date, dayBillList);
				}
			}
			mBillView.showBillByDay(mDayBillData);
		}
	}

	@Override
	public void getBillByMonth() {
		mBillRawData = BillDBImpl.getInstance(NoteApp.getAppContext()).findAll();
		if (!checkNoBill()) {
			Collections.sort(mBillRawData, SortUtils.getComparatorTimeAsc());
			// 按月分组
			Map<String, ArrayList<Bill>> mMonthBillData = new HashMap<String, ArrayList<Bill>>();
			mMonthBillData.clear();
			int mon = -1;
			ArrayList<Bill> monBillList = null;
			for (int i = 0; i < mBillRawData.size(); i++) {
				Bill bill = mBillRawData.get(i);
				Date d = TimeUtil.parseStringtoDate(bill.getDate(), DATE);
				if (mon != d.getMonth()) {
					mon = d.getMonth();
					monBillList = new ArrayList<Bill>();
					for (int j = 0; j < mBillRawData.size(); j++) {
						if (TimeUtil.parseStringtoDate(mBillRawData.get(j).getDate(), DATE).getMonth() == mon) {
							monBillList.add(mBillRawData.get(j));
						}
					}
					mMonthBillData.put(String.format("%d-%02d", d.getYear() + 1900, mon + 1), monBillList);
				}
			}
			mBillView.showBillByMonth(mMonthBillData);
		}
	}

	@Override
	public void getBillByYear() {
		mBillRawData = BillDBImpl.getInstance(NoteApp.getAppContext()).findAll();
		if (!checkNoBill()) {
			Collections.sort(mBillRawData, SortUtils.getComparatorTimeAsc());
			Map<String, ArrayList<Bill>> mYearBillData = new HashMap<String, ArrayList<Bill>>();
			mYearBillData.clear();
			int year = -1;
			ArrayList<Bill> yearBillList = null;
			for (int i = 0; i < mBillRawData.size(); i++) {
				Bill bill = mBillRawData.get(i);
				Date d = TimeUtil.parseStringtoDate(bill.getDate(), DATE);
				if (year != d.getYear()) {
					year = d.getYear();
					yearBillList = new ArrayList<Bill>();
					for (int j = 0; j < mBillRawData.size(); j++) {
						if (TimeUtil.parseStringtoDate(mBillRawData.get(j).getDate(), DATE).getYear() == year) {
							yearBillList.add(mBillRawData.get(j));
						}
					}
					mYearBillData.put(String.format("%d", d.getYear() + 1900), yearBillList);
				}
			}
			mBillView.showBillByYear(mYearBillData);
		}
	}

	@Override
	public void getTotalBill() {
		BigDecimal bigInc = new BigDecimal("0");
		BigDecimal bigExp = new BigDecimal("0");
		BigDecimal bigTmp = null;

		for (Bill bill : mBillRawData) {
			bigTmp = new BigDecimal("" + bill.getMoney());
			if (bill.getType() == EditBillActivity.TYPE_IN) {
				bigInc = bigInc.add(bigTmp);
			} else if (bill.getType() == EditBillActivity.TYPE_OUT) {
				bigExp = bigExp.add(bigTmp);
			}
		}
		BigDecimal bigBal = bigInc.subtract(bigExp);
		mBillView.showTotalBill(bigInc.doubleValue(), bigExp.doubleValue(), bigBal.doubleValue());
	}

	@Override
	public int getRawBillSize() {
		if (mBillRawData != null) {
			return mBillRawData.size();
		}
		return 0;
	}

	@Override
	public void getIncomeAndExpense(String date, ArrayList<Bill> Bills) {
		double income = 0;
		double expense = 0;
		for (Bill bill : Bills) {
			if (EditBillActivity.TYPE_IN == bill.getType()) {
				income += bill.getMoney();
			} else if (EditBillActivity.TYPE_OUT == bill.getType()) {
				expense += bill.getMoney();
			}
		}
		mBillView.showIncomeAndExpense(date, income, expense);
	}

	@Override
	public void addBill() {

	}

	@Override
	public void editBill() {

	}
}
