package com.alensic.nursing.mobile.dao;

import java.util.List;

import com.alensic.nursing.mobile.common.Dao;
import com.alensic.nursing.mobile.model.UploadHistory;
import com.alensic.nursing.mobile.model.Users;

public interface HistoryDao  extends Dao<UploadHistory>{

	List<UploadHistory> queryForList(UploadHistory bean);

	/**
	 * 清空所有上传历史和已经上传的体温数据
	 */
	void clear();
	
	/**
	 * 数据上传成功，需要update表的状态
	 * 生成上传头信息
	 */
	void uploadFinish(UploadHistory bean);
	
	/**
	 * 数据重传成功，需要update表的状态
	 */
	void reuploadFinish(UploadHistory bean);
}
