package com.lf.inote;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.oauth.BaiduOAuth;
import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;
import com.lf.constant.Constants;
import com.lf.constant.ReqCode;
import com.lf.db.BillDBImpl;
import com.lf.db.NoteDBImpl;
import com.lf.db.UserDBImpl;
import com.lf.model.Bill;
import com.lf.model.Note;
import com.lf.model.User;
import com.lf.utils.SortUtils;
import com.lf.utils.TimeUtil;
import com.lf.utils.adapter.DayExpListAdapter;
import com.lf.utils.adapter.MonthExpListAdapter;
import com.lf.utils.adapter.NoteListAdapter;
import com.lf.utils.adapter.YearExpListAdapter;

public class MainActivity extends Activity implements OnClickListener, OnItemClickListener, OnItemLongClickListener, OnChildClickListener,
		OnGroupCollapseListener, OnGroupExpandListener {

	/**
	 * 传递的Note标识
	 */
	public static final String NOTE = "Note";

	/**
	 * 传递的Bill标识
	 */
	public static final String BILL = "Bill";

	private static final String DATE = "yyyy-MM-dd";

	public static final int MODE_NOTE = 1;

	public static final int MODE_BILL = 2;

	public static final int TAG_DAY = 1;

	public static final int TAG_WEEK = 2;

	public static final int TAG_MONTH = 3;

	public static final int TAG_YEAR = 4;

	private TextView mTvTips;

	private ListView mLvNote;

	private LinearLayout mLayoutAdd;

	private Activity mContext;

	private long mFirstClickTime;

	private ArrayList<Note> mNoteListData = new ArrayList<Note>();

	private ArrayList<Bill> mBillRawData = new ArrayList<Bill>();

	private Map<String, ArrayList<Bill>> mDayBillData = new HashMap<String, ArrayList<Bill>>();

	private Map<String, ArrayList<Bill>> mMonthBillData = new HashMap<String, ArrayList<Bill>>();

	private Map<String, ArrayList<Bill>> mYearBillData = new HashMap<String, ArrayList<Bill>>();

	private NoteListAdapter mNoteAdapter;

	private DayExpListAdapter mDayBillAdapter;
	
	private MonthExpListAdapter mMonBillAdapter;
	
	private YearExpListAdapter mYearBillAdapter;

	private TextView mTvPage;

	private TextView mTvUserLogin;

	private TextView mTvCount;

	private Button mBtnNote;

	private Button mBtnBill;

	private int mEditMode = MODE_NOTE;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

		mContext = this;

		initView();
		initNoteData();
	}

	/**
	 * @description 初始化UI组件
	 * @date 2015-3-11
	 */
	private void initView() {
		// 初始化标题栏
		mTvPage = (TextView) findViewById(R.id.tv_title_page);
		mTvPage.setText(R.string.app_name);
		mTvUserLogin = (TextView) findViewById(R.id.tv_title_user_login);
		mTvCount = (TextView) findViewById(R.id.tv_title_count);

		// 初始化页面
		mTvTips = (TextView) findViewById(R.id.tv_main_no_note_tips);
		mTvTips.setVisibility(View.GONE);
		mLvNote = (ListView) findViewById(R.id.lv_note_list);

		mLayoutAdd = (LinearLayout) findViewById(R.id.ll_add_note);
		mBtnNote = (Button) findViewById(R.id.btn_type_note);
		mBtnBill = (Button) findViewById(R.id.btn_type_tallybook);

		mLayoutBillMode = (LinearLayout) findViewById(R.id.ll_bill_type);
		mBtnDay = (Button) findViewById(R.id.btn_bill_day);
		mBtnMonth = (Button) findViewById(R.id.btn_bill_month);
		mBtnYear = (Button) findViewById(R.id.btn_bill_year);

		mLayoutTotal = (LinearLayout) findViewById(R.id.ll_main_total_info);
		mLayoutTotal.setVisibility(View.VISIBLE);
		mTvTotalIncome = (TextView) findViewById(R.id.tv_main_total_income);
		mTvTotalExpense = (TextView) findViewById(R.id.tv_main_total_expense);
		mTvTotalBalance = (TextView) findViewById(R.id.tv_main_total_balance);

		mLvBill = (ExpandableListView) findViewById(R.id.explv_bill_list);

		mLayoutAdd.setOnClickListener(this);
		mLvNote.setOnItemClickListener(this);
		mTvUserLogin.setOnClickListener(this);
		mBtnNote.setOnClickListener(this);
		mBtnBill.setOnClickListener(this);
		mBtnDay.setOnClickListener(this);
		mBtnMonth.setOnClickListener(this);
		mBtnYear.setOnClickListener(this);

		mLvNote.setOnItemLongClickListener(this);
		mLvBill.setOnChildClickListener(this);
		mLvBill.setOnGroupCollapseListener(this);
		mLvBill.setOnGroupExpandListener(this);

		setMode(MODE_NOTE);
	}

	/**
	 * @description 初始化笔记列表数据
	 * @date 2015-2-13
	 */
	private void initNoteData() {
		mNoteAdapter = new NoteListAdapter(mContext);
		mLvNote.setAdapter(mNoteAdapter);
		mNoteListData = NoteDBImpl.getInstance(mContext).findAll();
		String format = getResources().getString(R.string.text_note_count);
		if (null == mNoteListData || mNoteListData.size() == 0) {
			mLvNote.setVisibility(View.GONE);
			mTvTips.setVisibility(View.VISIBLE);
			mTvCount.setText(String.format(format, 0));
		} else {
			mLvNote.setVisibility(View.VISIBLE);
			mTvTips.setVisibility(View.GONE);
			mNoteAdapter.setList(mNoteListData);
			mNoteAdapter.notifyDataSetChanged();
			mTvCount.setText(String.format(format, mNoteListData.size()));
		}
	}

	/**
	 * @description 初始化账单数据
	 * @date 2015-3-11
	 */
	@SuppressWarnings("deprecation")
	private void initBillData(int billTag) {
		mBillRawData = BillDBImpl.getInstance(mContext).findAll();
		String format = getResources().getString(R.string.text_bill_count);
		if (null == mBillRawData || mBillRawData.size() == 0) {
			mLvBill.setVisibility(View.GONE);
			mTvTips.setVisibility(View.VISIBLE);
			mTvCount.setText(String.format(format, 0));
		} else {
			// 按日期升序排序
			Collections.sort(mBillRawData, SortUtils.getComparatorTimeAsc());
			String date = "";
			if (TAG_DAY == billTag) {
				// 按天分组
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
				
				mDayBillAdapter = new DayExpListAdapter(mContext, mDayBillData);
				mLvBill.setAdapter(mDayBillAdapter);
				mDayBillAdapter.notifyDataSetChanged();
			} else if (TAG_MONTH == billTag) {
				// 按月分组
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
				
				mMonBillAdapter = new MonthExpListAdapter(mContext, mMonthBillData);
				mLvBill.setAdapter(mMonBillAdapter);
				mMonBillAdapter.notifyDataSetChanged();
			} else if (TAG_YEAR == billTag) {
				// 按年分组
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
				
				mYearBillAdapter = new YearExpListAdapter(mContext, mYearBillData);
				mLvBill.setAdapter(mYearBillAdapter);
				mYearBillAdapter.notifyDataSetChanged();
			}

			mLvBill.setVisibility(View.VISIBLE);
			mTvTips.setVisibility(View.GONE);
			mTvCount.setText(String.format(format, mBillRawData.size()));
			showTotalBill();
		}
	}

	private void showTotalBill() {
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

		mTvTotalIncome.setText("总收入：￥" + bigInc.doubleValue());
		mTvTotalExpense.setText("总支出：￥" + bigExp.doubleValue());
		mTvTotalBalance.setText("总结余：￥" + bigBal.doubleValue());
	}

	/**
	 * @description 设置工作模式：1-笔记，2-账单
	 * @date 2015-3-11
	 * @param mode
	 */
	private void setMode(int mode) {
		if (MODE_NOTE == mode) {
			mBtnNote.setBackgroundResource(R.drawable.button_plain_clicked_left);
			mBtnBill.setBackgroundResource(R.drawable.button_plain_normal_right);
			mLayoutBillMode.setVisibility(View.GONE);
			mLayoutTotal.setVisibility(View.GONE);
			mLvNote.setVisibility(View.VISIBLE);
			mLvBill.setVisibility(View.GONE);
			initNoteData();
		} else if (MODE_BILL == mode) {
			mBtnNote.setBackgroundResource(R.drawable.button_plain_normal_left);
			mBtnBill.setBackgroundResource(R.drawable.button_plain_clicked_right);
			mLayoutBillMode.setVisibility(View.VISIBLE);
			mLayoutTotal.setVisibility(View.VISIBLE);
			mLvNote.setVisibility(View.GONE);
			mLvBill.setVisibility(View.VISIBLE);
			setBillTag(mBillTag);
			initBillData(mBillTag);
		}
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
		} else if (TAG_MONTH == tag) {
			mBtnMonth.setBackgroundResource(R.drawable.button_plain_clicked);
		} else if (TAG_YEAR == tag) {
			mBtnYear.setBackgroundResource(R.drawable.button_plain_clicked_right);
		}
		initBillData(tag);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_type_note:
			setMode(MODE_NOTE);
			mEditMode = MODE_NOTE;
			break;
		case R.id.btn_type_tallybook:
			setMode(MODE_BILL);
			mEditMode = MODE_BILL;
			break;
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
		case R.id.ll_add_note:
			Intent i = null;
			if (MODE_NOTE == mEditMode) {
				i = new Intent(mContext, EditNoteActivity.class);
				startActivityForResult(i, ReqCode.ADD_NOTE);
			} else if (MODE_BILL == mEditMode) {
				i = new Intent(mContext, EditBillActivity.class);
				startActivityForResult(i, ReqCode.ADD_BILL);
			}

			// Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性
			// intent.setType("text/plain"); // 分享发送的数据类型
			// intent.putExtra(Intent.EXTRA_SUBJECT, "subject"); // 分享的主题
			// intent.putExtra(Intent.EXTRA_TEXT, "extratext"); // 分享的内容
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 这个也许是分享列表的背景吧
			// startActivity(Intent.createChooser(intent, "分享"));// 目标应用选择对话框的标题
			break;
		case R.id.tv_title_user_login:
			BaiduOAuth oauthClient = new BaiduOAuth();
			oauthClient.startOAuth(MainActivity.this, Constants.mbApiKey, new String[] { "basic" }, new BaiduOAuth.OAuthListener() {
				@Override
				public void onException(String msg) {
					Toast.makeText(getApplicationContext(), "Login failed " + msg, Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onComplete(BaiduOAuthResponse response) {
					if (null != response) {
						String accessToken = response.getAccessToken();
						String userName = response.getUserName();
						// Toast.makeText(getApplicationContext(), "Token: "
						// + accessToken + ";    User name:" + userName,
						// Toast.LENGTH_SHORT).show();
						Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
						User user = new User();
						user.setAccessToken(accessToken);
						user.setUserName(userName);
						UserDBImpl.getInstance(mContext).insert(user);
						NoteApp.getInstance().setUser(user);
						mTvUserLogin.setText(userName);
					}
				}

				@Override
				public void onCancel() {
					Toast.makeText(getApplicationContext(), "Login cancelled", Toast.LENGTH_SHORT).show();
				}
			});
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent i = null;
		if (MODE_NOTE == mEditMode) {
			i = new Intent(mContext, EditNoteActivity.class);
			i.putExtra(NOTE, mNoteListData.get(position));
			startActivityForResult(i, ReqCode.VIEW_NOTE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
			case ReqCode.ADD_NOTE:
			case ReqCode.VIEW_NOTE:
				initNoteData();
				break;
			case ReqCode.ADD_BILL:
			case ReqCode.VIEW_BILL:
				initBillData(mBillTag);
				break;
			default:
				break;
			}
		}
		// super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondClickTime = System.currentTimeMillis();
			if (secondClickTime - mFirstClickTime > 2000) {
				Toast.makeText(mContext, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mFirstClickTime = secondClickTime;
			} else {
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}
		}
		return false;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		showLongClickDialog(position);
		return true;
	}

	private void showLongClickDialog(final int position) {
		String[] items = { "删除" };
		AlertDialog.Builder dlg = new AlertDialog.Builder(mContext);
		dlg.setTitle("选择操作");
		dlg.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					AlertDialog.Builder dlg = new AlertDialog.Builder(mContext);
					dlg.setTitle("确定要删除？");
					dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							deleteListItem(position, 0);
						}
					});
					dlg.setNegativeButton("取消", null);
					dlg.create().show();
					break;
				default:
					break;
				}
			}
		});
		dlg.create().show();
	}

	private void deleteListItem(int gPosition, int cPosition) {
		if (MODE_NOTE == mEditMode) {
			Note note = mNoteListData.get(gPosition);
			NoteDBImpl.getInstance(mContext).delete(note);
			mNoteListData.remove(gPosition);
			mNoteAdapter.notifyDataSetChanged();
		}
		// else if (MODE_BILL == mEditMode) {
		// Bill bill = mDayBillGroup.get(position);
		// BillDBImpl.getInstance(mContext).delete(bill);
		// mDayBillGroup.remove(position);
		// mDayBillAdapter.notifyDataSetChanged();
		// initTotalBill();
		// }
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		if (MODE_BILL == mEditMode) {
			Intent i = new Intent(mContext, EditBillActivity.class);
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
		}
		return true;
	}

	/**
	 * @description 显示某天/月/年的收支概况
	 * @date 2015-5-29
	 * @param date
	 * @param dayBills
	 */
	private void showDateBill(String date, ArrayList<Bill> Bills) {
		mTvTotalIncome.setText(date);
		double income = 0;
		double expense = 0;
		for (Bill bill : Bills) {
			if (EditBillActivity.TYPE_IN == bill.getType()) {
				income += bill.getMoney();
			} else if (EditBillActivity.TYPE_OUT == bill.getType()) {
				expense += bill.getMoney();
			}
		}
		mTvTotalExpense.setText("支出：￥" + expense);
		mTvTotalBalance.setText("收入：￥" + income);
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
		
		showDateBill(date, bills);
	}

	/**
	 * @description 折叠分组事件
	 * @date 2015-5-29
	 * @param groupPosition
	 */
	@Override
	public void onGroupCollapse(int groupPosition) {
		showTotalBill();
	}
}
