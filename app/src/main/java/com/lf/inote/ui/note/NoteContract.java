/**
 * Project name：Inote
 * Create time：2016/11/17 18:33
 * Copyright: 2016 GALAXYWIND Network Systems Co.,Ltd.All rights reserved.
 */
package com.lf.inote.ui.note;

import com.lf.inote.ui.base.MVPView;
import com.lf.inote.model.Note;

import java.util.ArrayList;

/**
 * Created by sy on 2016/11/17.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2016/11/17 18:33<br>
 * Revise Record:<br>
 * 2016/11/17: 创建并完成初始实现<br>
 */
public interface NoteContract {

	interface NoteView extends MVPView<Presenter> {
		void showNoNote();
		void showAllNotes(ArrayList<Note> mNoteListData);
	}

	interface Presenter {
		void getAllNotes();
		void deleteNote(Note note);
		int getAllNoteSize();
	}
}
