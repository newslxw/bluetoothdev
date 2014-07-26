package com.alensic.nursing.mobile.model;

import com.alensic.nursing.mobile.common.BaseTable;

public class Groups extends BaseTable {
	
	public final static String TABLE_NAME = "t_groups";
	public final static String GROUP_NAME ="group_name";
	public final static String REMOVABLE ="removable"; 

	private String groupName;
	private Integer removable;//定义在Constants.enable/disable
	private String groupAT;
	
	/*public String getCreateSql() {
		String sql = "create table t_groups"+
				"("+
				"   _id                  integer not null primary key,  "+
				"   group_name           varchar(40),"+
				"   removable            integer                     "+
				");";
		return sql;
	}*/

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getRemovable() {
		return removable;
	}

	public void setRemovable(Integer removable) {
		this.removable = removable;
	}

	public String getGroupAT() {
		return groupAT;
	}

	public void setGroupAT(String groupAT) {
		this.groupAT = groupAT;
	}
}
