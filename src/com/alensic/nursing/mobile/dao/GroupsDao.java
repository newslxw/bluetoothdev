package com.alensic.nursing.mobile.dao;

import java.util.List;

import android.database.Cursor;

import com.alensic.nursing.mobile.common.Dao;
import com.alensic.nursing.mobile.model.Groups;

public interface GroupsDao extends Dao<Groups> {
	
	public static final String GROUP_NAME_AND_ASSIGNED_TOTAL = "GroupAT";
	
	/**
	 * 查询组名和已分配病床数,
	 * 前3字段名和内容与Groups相同
	 * 最后字段名为 静态变量GROUP_NAME_AND_ASSIGNED_TOTAL ,内容格式如  默认分组(8)
	 * @return
	 */
	public List<Groups> queryGroupAndAssignedTotal() ;
	
	/**
	 * 新增分组
	 * @param bean
	 * @return
	 */
	public Groups addGroup(Groups bean);
	
	/**
	 * 修改分组
	 * @param bean
	 * @return
	 */
	public Groups modificationGroup(Groups bean);

	/**
	 * 通过分组名统计数量,通常用于检测分组名是否存在
	 * @param groupName
	 * @return
	 */
	int countByGroupName(String groupName);
}
