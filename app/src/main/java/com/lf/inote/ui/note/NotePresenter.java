/**
 * Project name：Inote
 * Create time：2016/11/17 18:52
 * Copyright: 2016 GALAXYWIND Network Systems Co.,Ltd.All rights reserved.
 */
package com.lf.inote.ui.note;

import com.lf.inote.NoteApp;
import com.lf.inote.db.NoteDBImpl;
import com.lf.inote.model.Note;

import java.util.ArrayList;

/**
 * Created by sy on 2016/11/17.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2016/11/17 18:52<br>
 * Revise Record:<br>
 * 2016/11/17: 创建并完成初始实现<br>
 */
public class NotePresenter implements NoteContract.Presenter {

	private NoteContract.NoteView mNoteView;

	private ArrayList<Note> mNoteListData;

	public NotePresenter(NoteContract.NoteView mNoteView) {
		this.mNoteView = mNoteView;
		mNoteView.setPresenter(this);
	}

	private boolean checkNoNote() {
		if (mNoteListData == null || mNoteListData.isEmpty()) {
			return true;
		}

		return false;
	}

	@Override
	public void getAllNotes() {
		mNoteListData = NoteDBImpl.getInstance(NoteApp.getAppContext()).findAll();
		if (!checkNoNote()) {
			mNoteView.showAllNotes(mNoteListData);
		}
	}

	@Override
	public void deleteNote(Note note) {
		NoteDBImpl.getInstance(NoteApp.getAppContext()).delete(note);
	}

	@Override
	public int getAllNoteSize() {
		if (mNoteListData != null) {
			return mNoteListData.size();
		}

		return 0;
	}
}
