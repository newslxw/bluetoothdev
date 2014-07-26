package com.alensic.nursing.mobile.dao;

import java.util.List;

import com.alensic.nursing.mobile.common.Dao;
import com.alensic.nursing.mobile.model.BedTemperature;

public interface TemperatureDao extends Dao<BedTemperature> {

	/**
	 * 查询待上传数据
	 * @return
	 */
	List<BedTemperature> queryForUpload();
	
	/**
	 * 查询待重传数据
	 * @return
	 */
	List<BedTemperature> queryForUpload(Integer uploadId);
	

}
