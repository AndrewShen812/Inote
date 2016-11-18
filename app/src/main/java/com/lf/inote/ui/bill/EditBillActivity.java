package com.lf.inote.ui.bill;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lf.inote.R;
import com.lf.inote.ui.MainActivity;
import com.lf.inote.db.BillDBImpl;
import com.lf.inote.model.Bill;
import com.lf.inote.utils.TimeUtil;
import com.lf.inote.view.NoteEditText;

import java.util.Calendar;

public class EditBillActivity extends Activity implements OnClickListener {

	/** 账单类型-收入 */
	public static final int TYPE_IN = 1;
	
	/** 账单类型-支出 */
	public static final int TYPE_OUT = 0;
	
	private TextView mTvDate;

	private Button mBtnTypeIn;

	private Button mBtnTypeOut;

	private EditText mEtAmount;

	private NoteEditText mEtRemark;

	private LinearLayout mLayoutSave;

	/** 账单类型：1-收入，0-支出 */
	private int mBillType = TYPE_OUT;
	
	private Context mContext;
	
	private Bill mBill;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit_bill);

		mContext = this;
		
		initView();
		initData();
	}

	private void initView() {
		mTvDate = (TextView) findViewById(R.id.tv_add_bill_date);
		mBtnTypeIn = (Button) findViewById(R.id.btn_bill_type_in);
		mBtnTypeOut = (Button) findViewById(R.id.btn_bill_type_out);

		mEtAmount = (EditText) findViewById(R.id.et_add_bill_amount);
		mEtRemark = (NoteEditText) findViewById(R.id.et_bill_remark);

		mLayoutSave = (LinearLayout) findViewById(R.id.ll_add_bill_save);

		mTvDate.setOnClickListener(this);
		mBtnTypeIn.setOnClickListener(this);
		mBtnTypeOut.setOnClickListener(this);
		mLayoutSave.setOnClickListener(this);
		
		setBillType(TYPE_OUT);
	}

	private void initData() {
		mBill = (Bill) getIntent().getSerializableExtra(MainActivity.BILL);
		if (null != mBill) {
			mTvDate.setText(mBill.getDate());
			setBillType(mBill.getType());
			mEtAmount.setText(mBill.getMoney() + "");
			mEtRemark.setText(mBill.getRemark());
		}
		else {
			mTvDate.setText(TimeUtil.getCurrentDateTime("yyyy-MM-dd"));
		}
	}

	private void setBillType(int type) {
		mBillType = type; 
		if(TYPE_IN == type) {
			mBtnTypeIn.setBackgroundResource(R.drawable.button_plain_clicked_left);
			mBtnTypeOut.setBackgroundResource(R.drawable.button_plain_normal_right);
        }
        else if(TYPE_OUT == type) {
        	mBtnTypeIn.setBackgroundResource(R.drawable.button_plain_normal_left);
        	mBtnTypeOut.setBackgroundResource(R.drawable.button_plain_clicked_right);
        }
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_add_bill_date:
			selectDate();
			break;
		case R.id.btn_bill_type_in:
			setBillType(TYPE_IN);
			break;
		case R.id.btn_bill_type_out:
			setBillType(TYPE_OUT);
			break;
		case R.id.ll_add_bill_save:
			saveBill();
			break;
		default:
			break;
		}
	}
	
	private void selectDate() {
		Calendar calendar = Calendar.getInstance();
		DatePickerDialog date = new DatePickerDialog(mContext, new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				mTvDate.setText(String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));
			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		date.show();
	}
	
	private void saveBill() {
		boolean isNewBill = false;
		String date = mTvDate.getText().toString();
		if (null == mBill) {
			mBill = new Bill();
			isNewBill = true;
		}
		mBill.setDate(date);
		mBill.setType(mBillType);
		try {
			double money = Double.parseDouble(mEtAmount.getText().toString());
			mBill.setMoney(money);
		} catch (NumberFormatException e) {
			Toast.makeText(mContext, "金额格式错误", Toast.LENGTH_SHORT).show();
			return;
		}
		if (mEtRemark.getText().toString().trim().equals("")) {
			Toast.makeText(mContext, "请添加备注", Toast.LENGTH_SHORT).show();
			return;
		}
		mBill.setRemark(mEtRemark.getText().toString().trim());
		if (isNewBill) {
			BillDBImpl.getInstance(mContext).insert(mBill);
		}
		else {
			BillDBImpl.getInstance(mContext).update(mBill);
		}
		setResult(RESULT_OK);
        finish();
	}
	
    /**
     * 返回键处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	Toast.makeText(mContext, "取消编辑账单", Toast.LENGTH_SHORT).show();
        	setResult(RESULT_CANCELED);
            finish();
        }
        
        return true;
    }
}
