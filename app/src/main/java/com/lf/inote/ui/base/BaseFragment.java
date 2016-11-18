/**
 * Project name：Inote
 * Create time：2016/11/17 18:42
 * Copyright: 2016 GALAXYWIND Network Systems Co.,Ltd.All rights reserved.
 */
package com.lf.inote.ui.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lf.inote.NoteApp;

/**
 * Created by sy on 2016/11/17.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2016/11/17 18:42<br>
 * Revise Record:<br>
 * 2016/11/17: 创建并完成初始实现<br>
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

	protected Activity mActivity;
	/** 是否已附加到页面 */
	private boolean isAttached = false;
	private int mLayoutResId;
	protected View mRootView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		isAttached = true;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mActivity = null;
		isAttached = false;
	}

	protected boolean isAttached() {
		return isAttached && isPageActive();
	}
	protected abstract int setContentView();

	protected abstract void initSubView();

	protected abstract void initData();

	/**
	 * 页面数据是否可用 true不可用
	 */
	private boolean mPageInvalid = true;

	protected boolean isPageInitSuccess() {
		return !mPageInvalid;
	}

	final protected boolean isPageActive() {
		return getActivity() != null && isPageInitSuccess()
				&& (!isHidden()) && isResumed() && getView() != null;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mLayoutResId = setContentView();
		mRootView = inflater.inflate(mLayoutResId, container, false);
		initSubView();
		initData();
		if (mRootView != null) {
			mPageInvalid = false;
		}
		return mRootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mPageInvalid = true;
	}

	protected <T extends View> T findViewById(int id) {
		if (mRootView != null) {
			return (T) mRootView.findViewById(id);
		}

		return null;
	}

	protected void setSubViewOnClickListener(View v) {
		v.setOnClickListener(this);
	}

	protected void onSubViewClick(View v) {}

	@Override
	public void onClick(View v) {
		onSubViewClick(v);
	}

	/**
	 * 更高版本的兼容库中已存在getContext方法，访问级别为public，这里覆盖
	 * @return ApplicationContext
	 */
	public Context getContext() {
		return NoteApp.getAppContext();
	}
}
