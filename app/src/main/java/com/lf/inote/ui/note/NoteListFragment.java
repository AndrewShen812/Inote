package com.lf.inote.ui.note;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lf.inote.ui.base.BaseFragment;
import com.lf.inote.R;
import com.lf.inote.constant.ReqCode;
import com.lf.inote.model.Note;
import com.lf.inote.ui.MainActivity;
import com.lf.inote.utils.adapter.NoteListAdapter;

import java.util.ArrayList;

import static com.lf.inote.ui.MainActivity.NOTE;

public class NoteListFragment extends BaseFragment implements AdapterView.OnItemClickListener,
		AdapterView.OnItemLongClickListener, NoteContract.NoteView {
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	private TextView mTvTips;
	private ListView mLvNote;
	private LinearLayout mLayoutAdd;

	private ArrayList<Note> mNoteListData = new ArrayList<Note>();
	private NoteListAdapter mNoteAdapter;

	private NoteContract.Presenter mPresenter;

	public NoteListFragment() {
		// Required empty public constructor
	}

	public static NoteListFragment newInstance() {
		NoteListFragment fragment = new NoteListFragment();
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
		return R.layout.fragment_note_list;
	}

	@Override
	protected void initSubView() {
		mTvTips = findViewById(R.id.tv_main_no_note_tips);
		mTvTips.setVisibility(View.GONE);
		mLvNote = findViewById(R.id.lv_note_list);
		mLayoutAdd = findViewById(R.id.ll_add_note);

		setSubViewOnClickListener(mLayoutAdd);
		mLvNote.setOnItemClickListener(this);
		mLvNote.setOnItemLongClickListener(this);
	}

	@Override
	protected void onSubViewClick(View v) {
		if (v.getId() == R.id.ll_add_note) {
			Intent i = new Intent(getContext(), EditNoteActivity.class);
			startActivityForResult(i, ReqCode.ADD_BILL);
		}
	}

	private void notifyActivityCount(int total) {
		if (mActivity instanceof MainActivity) {
			MainActivity activity = (MainActivity) mActivity;
			activity.getNoteCount(total);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(isAttached()) {
			mPresenter.getAllNotes();
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if(isAttached()) {
			mPresenter.getAllNotes();
		}
	}

	@Override
	protected void initData() { }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent i = new Intent(getContext(), EditNoteActivity.class);
		i.putExtra(NOTE, mNoteListData.get(position));
		startActivityForResult(i, ReqCode.VIEW_NOTE);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		showLongClickDialog(position);
		return true;
	}

	private void showLongClickDialog(final int position) {
		String[] items = { "删除" };
		AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
		dlg.setTitle("选择操作");
		dlg.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
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
		Note note = mNoteListData.get(gPosition);
		mPresenter.deleteNote(note);
		mNoteListData.remove(gPosition);
		mNoteAdapter.notifyDataSetChanged();
	}

	@Override
	public void showNoNote() {
		mLvNote.setVisibility(View.GONE);
		mTvTips.setVisibility(View.VISIBLE);
		notifyActivityCount(0);
	}

	@Override
	public void showAllNotes(ArrayList<Note> allNotes) {
		mLvNote.setVisibility(View.VISIBLE);
		mTvTips.setVisibility(View.GONE);
		notifyActivityCount(mPresenter.getAllNoteSize());
		mNoteListData.clear();
		mNoteListData.addAll(allNotes);
		if (mNoteAdapter == null) {
			mNoteAdapter = new NoteListAdapter(getContext());
			mNoteAdapter.setList(mNoteListData);
			mLvNote.setAdapter(mNoteAdapter);
		} else {
			mNoteAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void setPresenter(NoteContract.Presenter presenter) {
		mPresenter = presenter;
	}
}
