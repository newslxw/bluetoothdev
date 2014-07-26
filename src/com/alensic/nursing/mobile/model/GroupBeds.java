package com.alensic.nursing.mobile.model;

import com.alensic.nursing.mobile.common.BaseTable;

public final class GroupBeds extends BaseTable {

	public final static String TABLE_NAME = "t_group_beds";
	public final static String BED_ID   ="bed_id";
	public final static String GROUPS_ID="groups_id";
	
	private Integer bedId;
	private Integer groupsId;
	

	/*public String getCreateSql() {
		String sql =         
				"create table t_group_beds                            " +
						"(                                                    " +
						"   _id                  integer not null primary key," +
						"   bed_id               integer,"+
						"   groups_id            integer "+
						");             ";
						      
						      
		return sql;
	}
*/
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	public Integer getBedId() {
		return bedId;
	}

	public void setBedId(Integer bedId) {
		this.bedId = bedId;
	}

	public Integer getGroupsId() {
		return groupsId;
	}

	public void setGroupsId(Integer groupsId) {
		this.groupsId = groupsId;
	}
}
