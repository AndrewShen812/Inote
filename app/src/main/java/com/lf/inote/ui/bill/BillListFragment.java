package com.lf.inote.ui.bill;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lf.inote.ui.base.BaseFragment;
import com.lf.inote.R;
import com.lf.inote.constant.ReqCode;
import com.lf.inote.model.Bill;
import com.lf.inote.ui.MainActivity;
import com.lf.inote.utils.adapter.DayExpListAdapter;
import com.lf.inote.utils.adapter.MonthExpListAdapter;
import com.lf.inote.utils.adapter.YearExpListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.lf.inote.ui.MainActivity.BILL;

public class BillListFragment extends BaseFragment implements ExpandableListView.OnChildClickListener,
		ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnGroupExpandListener,
		BillContract.BillView {
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	private static final String DATE = "yyyy-MM-dd";
	public static final int TAG_DAY = 1;
	public static final int TAG_WEEK = 2;
	public static final int TAG_MONTH = 3;
	public static final int TAG_YEAR = 4;

	private TextView mTvTips;
	private LinearLayout mLayoutAdd;

	private ArrayList<Bill> mBillRawData = new ArrayList<Bill>();
	private Map<String, ArrayList<Bill>> mDayBillData = new HashMap<String, ArrayList<Bill>>();
	private Map<String, ArrayList<Bill>> mMonthBillData = new HashMap<String, ArrayList<Bill>>();
	private Map<String, ArrayList<Bill>> mYearBillData = new HashMap<String, ArrayList<Bill>>();
	private DayExpListAdapter mDayBillAdapter;
	private MonthExpListAdapter mMonBillAdapter;
	private YearExpListAdapter mYearBillAdapter;
	private LinearLayout mLayoutBillMode;
	private Button mBtnDay;
	private Button mBtnMonth;
	private Button mBtnYear;
	private int mBillTag = TAG_DAY;
	private LinearLayout mLayoutTotal;
	private TextView mTvTotalIncome;
	private TextView mTvTotalExpense;
	private TextView mTvTotalBalance;
	private ExpandableListView mLvBill;

	private BillContract.Presenter mPresenter;

	public BillListFragment() {
		// Required empty public constructor
	}

	public static BillListFragment newInstance() {
		BillListFragment fragment = new BillListFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, "");
		args.putString(ARG_PARAM2, "");
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
		}
	}

	@Override
	protected int setContentView() {
		return R.layout.fragment_bill_list;
	}

	@Override
	protected void initSubView() {
		mTvTips = findViewById(R.id.tv_main_no_bill_tips);
		mTvTips.setVisibility(View.GONE);
		mLvBill = findViewById(R.id.explv_bill_list);
		mLayoutAdd = findViewById(R.id.ll_add_bill);

		mLayoutBillMode = findViewById(R.id.ll_bill_type);
		mBtnDay = findViewById(R.id.btn_bill_day);
		mBtnMonth = findViewById(R.id.btn_bill_month);
		mBtnYear = findViewById(R.id.btn_bill_year);

		mLayoutTotal = findViewById(R.id.ll_main_total_info);
		mLayoutTotal.setVisibility(View.VISIBLE);
		mTvTotalIncome = findViewById(R.id.tv_main_total_income);
		mTvTotalExpense = findViewById(R.id.tv_main_total_expense);
		mTvTotalBalance = findViewById(R.id.tv_main_total_balance);

		setSubViewOnClickListener(mLayoutAdd);
		setSubViewOnClickListener(mBtnDay);
		setSubViewOnClickListener(mBtnMonth);
		setSubViewOnClickListener(mBtnYear);
		mLvBill.setOnChildClickListener(this);
		mLvBill.setOnGroupCollapseListener(this);
		mLvBill.setOnGroupExpandListener(this);
	}

	@Override
	protected void onSubViewClick(View v) {
		switch (v.getId()) {
			case R.id.btn_bill_day:
				setBillTag(TAG_DAY);
				mBillTag = TAG_DAY;
				break;
			case R.id.btn_bill_month:
				setBillTag(TAG_MONTH);
				mBillTag = TAG_MONTH;
				break;
			case R.id.btn_bill_year:
				setBillTag(TAG_YEAR);
				mBillTag = TAG_YEAR;
				break;
			case R.id.ll_add_bill:
				Intent i = new Intent(getContext(), EditBillActivity.class);
				startActivityForResult(i, ReqCode.ADD_BILL);
				break;
		}
	}

	private void notifyActivityCount(int total) {
		if (mActivity instanceof MainActivity) {
			MainActivity activity = (MainActivity) mActivity;
			activity.getBillCount(total);
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		if(isAttached()) {
			setBillTag(mBillTag);
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if(isAttached()) {
			setBillTag(mBillTag);
		}
	}

	@Override
	protected void initData() {
	}

	/**
	 * @description 设置账单显示模式：1-按天，2-按周；3-按月
	 * @date 2015-3-11
	 * @param tag
	 */
	private void setBillTag(int tag) {
		mBtnDay.setBackgroundResource(R.drawable.button_plain_normal_left);
		mBtnMonth.setBackgroundResource(R.drawable.button_plain_normal);
		mBtnYear.setBackgroundResource(R.drawable.button_plain_normal_right);
		if (TAG_DAY == tag) {
			mBtnDay.setBackgroundResource(R.drawable.button_plain_clicked_left);
			mPresenter.getBillByDay();
		} else if (TAG_MONTH == tag) {
			mBtnMonth.setBackgroundResource(R.drawable.button_plain_clicked);
			mPresenter.getBillByMonth();
		} else if (TAG_YEAR == tag) {
			mBtnYear.setBackgroundResource(R.drawable.button_plain_clicked_right);
			mPresenter.getBillByYear();
		}
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		Intent i = new Intent(getContext(), EditBillActivity.class);
		if (TAG_DAY == mBillTag) {
			i.putExtra(BILL, mDayBillData.get(mDayBillData.keySet().toArray()[groupPosition]).get(childPosition));
		}
		else if (TAG_MONTH == mBillTag) {
			i.putExtra(BILL, mMonthBillData.get(mMonthBillData.keySet().toArray()[groupPosition]).get(childPosition));
		}
		else if (TAG_YEAR == mBillTag) {
			i.putExtra(BILL, mYearBillData.get(mYearBillData.keySet().toArray()[groupPosition]).get(childPosition));
		}

		startActivityForResult(i, ReqCode.VIEW_BILL);
		return true;
	}


	/**
	 * @description 展开分组事件
	 * @date 2015-5-29
	 * @param groupPosition
	 */
	@Override
	public void onGroupExpand(int groupPosition) {
		String date = null;
		ArrayList<Bill> bills = mDayBillData.get(date);

		if (TAG_DAY == mBillTag) {
			date = mDayBillData.keySet().toArray()[groupPosition].toString();
			bills = mDayBillData.get(date);
		}
		else if (TAG_MONTH == mBillTag) {
			date = mMonthBillData.keySet().toArray()[groupPosition].toString();
			bills = mMonthBillData.get(date);
		}
		else if (TAG_YEAR == mBillTag) {
			date = mYearBillData.keySet().toArray()[groupPosition].toString();
			bills = mYearBillData.get(date);
		}
		mPresenter.getIncomeAndExpense(date, bills);
	}

	/**
	 * @description 折叠分组事件
	 * @date 2015-5-29
	 * @param groupPosition
	 */
	@Override
	public void onGroupCollapse(int groupPosition) {
		mPresenter.getTotalBill();
	}

	@Override
	public boolean isViewActive() {
		return isAttached();
	}

	@Override
	public void showNoBill() {
		mLvBill.setVisibility(View.GONE);
		mTvTips.setVisibility(View.VISIBLE);
		notifyActivityCount(0);
	}

	private void setListVisible() {
		mLvBill.setVisibility(View.VISIBLE);
		mTvTips.setVisibility(View.GONE);
	}

	@Override
	public void showBillByDay(Map<String, ArrayList<Bill>> billMap) {
		setListVisible();
		mPresenter.getTotalBill();
		notifyActivityCount(mPresenter.getRawBillSize());
		mDayBillData.clear();
		mDayBillData.putAll(billMap);
		if (mDayBillAdapter == null) {
			mDayBillAdapter = new DayExpListAdapter(getContext(), billMap);
			mLvBill.setAdapter(mDayBillAdapter);
		} else {
			mDayBillAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void showBillByMonth(Map<String, ArrayList<Bill>> billMap) {
		setListVisible();
		mPresenter.getTotalBill();
		notifyActivityCount(mPresenter.getRawBillSize());
		mMonthBillData.clear();
		mMonthBillData.putAll(billMap);
		if (mMonBillAdapter == null) {
			mMonBillAdapter = new MonthExpListAdapter(getContext(), billMap);
			mLvBill.setAdapter(mDayBillAdapter);
		} else {
			mMonBillAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void showBillByYear(Map<String, ArrayList<Bill>> billMap) {
		setListVisible();
		mPresenter.getTotalBill();
		mYearBillData.clear();
		mYearBillData.putAll(billMap);
		notifyActivityCount(mPresenter.getRawBillSize());
		if (mYearBillAdapter == null) {
			mYearBillAdapter = new YearExpListAdapter(getContext(), billMap);
			mLvBill.setAdapter(mDayBillAdapter);
		} else {
			mYearBillAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void showTotalBill(double income, double expense, double balance) {
		mTvTotalIncome.setText("总收入：￥" + income);
		mTvTotalExpense.setText("总支出：￥" + expense);
		mTvTotalBalance.setText("总结余：￥" + balance);
	}

	@Override
	public void showIncomeAndExpense(String date, double income, double expense) {
		mTvTotalIncome.setText(date);
		mTvTotalExpense.setText("支出：￥" + expense);
		mTvTotalBalance.setText("收入：￥" + income);
	}

	@Override
	public void setPresenter(BillContract.Presenter presenter) {
		mPresenter = presenter;
	}
}
