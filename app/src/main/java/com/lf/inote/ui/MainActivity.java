package com.lf.inote.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.oauth.BaiduOAuth;
import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;
import com.lf.inote.NoteApp;
import com.lf.inote.R;
import com.lf.inote.constant.Constants;
import com.lf.inote.constant.ReqCode;
import com.lf.inote.db.UserDBImpl;
import com.lf.inote.model.User;
import com.lf.inote.ui.bill.BillListFragment;
import com.lf.inote.ui.bill.BillPresenter;
import com.lf.inote.ui.note.NoteListFragment;
import com.lf.inote.ui.note.NotePresenter;

public class MainActivity extends FragmentActivity implements OnClickListener {

	/**
	 * 传递的Note标识
	 */
	public static final String NOTE = "Note";

	/**
	 * 传递的Bill标识
	 */
	public static final String BILL = "Bill";

	public static final int MODE_NOTE = 1;

	public static final int MODE_BILL = 2;

	private Activity mContext;

	private long mFirstClickTime;

	private TextView mTvPage;

	private TextView mTvUserLogin;

	private TextView mTvCount;

	private Button mBtnNote;

	private Button mBtnBill;

	private NoteListFragment mNoteFragment;

	private BillListFragment mBillFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

		mContext = this;

		initView();
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
		mBtnNote = (Button) findViewById(R.id.btn_type_note);
		mBtnBill = (Button) findViewById(R.id.btn_type_tallybook);

		mNoteFragment = NoteListFragment.newInstance();
		mBillFragment = BillListFragment.newInstance();
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, mBillFragment, "bill")
				.add(R.id.fragment_container, mNoteFragment, "note").commit();
		new NotePresenter(mNoteFragment);
		new BillPresenter(mBillFragment);

		mTvUserLogin.setOnClickListener(this);
		mBtnNote.setOnClickListener(this);
		mBtnBill.setOnClickListener(this);

		setMode(MODE_NOTE);
	}

	public void getNoteCount(int total) {
		String format = getResources().getString(R.string.text_note_count);
		mTvCount.setText(String.format(format, total));
	}

	public void getBillCount(int total) {
		String format = getResources().getString(R.string.text_bill_count);
		mTvCount.setText(String.format(format, total));
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
			getSupportFragmentManager().beginTransaction()
					.hide(mBillFragment)
					.show(mNoteFragment).commit();
		} else if (MODE_BILL == mode) {
			mBtnNote.setBackgroundResource(R.drawable.button_plain_normal_left);
			mBtnBill.setBackgroundResource(R.drawable.button_plain_clicked_right);
			getSupportFragmentManager().beginTransaction()
					.hide(mNoteFragment)
					.show(mBillFragment).commit();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_type_note:
			setMode(MODE_NOTE);
			break;
		case R.id.btn_type_tallybook:
			setMode(MODE_BILL);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
			case ReqCode.ADD_NOTE:
			case ReqCode.VIEW_NOTE:
				setMode(MODE_NOTE);
				break;
			case ReqCode.ADD_BILL:
			case ReqCode.VIEW_BILL:
				setMode(MODE_BILL);
				break;
			default:
				break;
			}
		}
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
}
