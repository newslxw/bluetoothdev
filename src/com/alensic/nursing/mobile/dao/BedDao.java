package com.alensic.nursing.mobile.dao;

import java.util.List;

import android.database.Cursor;

import com.alensic.nursing.mobile.common.Dao;
import com.alensic.nursing.mobile.model.Bed;

public interface BedDao  extends Dao<Bed> {

	/**
	 * 新增病床号
	 * @param bean
	 * @return
	 */
	Bed addBed(Bed bean);
	
	/**
	 * 修改病床号
	 * @param bean
	 * @return
	 */
	Bed modificationBed(Bed bean);
	
	/**
	 * 查询病床及其没上传的温度值列表，如果groupId为null则返回所有病床，
	 * 需要排除同一个病床有多条没上传的体温导致病床重复的情况
	 * @param groupNo
	 * @param orderByTemperture 是否已测量的排前面
	 * @param dataType 数据类型
	 * @return
	 */
	List<Bed> queryByGroupNo(Integer groupId,boolean orderByTemperature,String dataType);

	List<Bed> queryGroupBed(String groupID);

	/**
	 * 查询上传历史明细
	 * @param uploadId
	 * @return
	 */
	List<Bed> queryByUploadId(Integer uploadId);
	
	/**
	 * 通过病床号统计数量,通常用于检测病床号是否存在
	 * @param bedNo
	 * @return
	 */
	int countByBedNo(String bedNo);
	
	/**
	 * 查询所有待上传数据
	 * @return
	 */
	List<Bed> queryNotUploadData();

}
