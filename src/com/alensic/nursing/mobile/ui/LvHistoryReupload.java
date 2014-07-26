package com.alensic.nursing.mobile.ui;

import com.alensic.nursing.mobile.model.UploadHistory;

import android.content.Context;

public abstract class LvHistoryReupload {

	
	
	/**
	 * 重传按钮事件处理
	 * @param history
	 * @param position
	 * @param adapter
	 * @return
	 */
	public abstract void reupload(final UploadHistory history, int position,final LvHistoryAdapter adapter);

	
}
