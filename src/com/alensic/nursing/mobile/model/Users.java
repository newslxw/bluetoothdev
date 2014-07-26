package com.alensic.nursing.mobile.model;

import com.alensic.nursing.mobile.common.BaseTable;

public class Users extends BaseTable {

	public final static String TABLE_NAME = "t_users";
	public final static String USER_NAME ="user_name";
	public final static String GROUP_NAME="group_name";
	public final static String LOGIN_TIME="login_time";

	private String userName;
	private String groupName;
	private String loginTime;
	
	/*public String getCreateSql() {
		String sql = "create table t_users  "+
				"(                    "+
				"   _id                  integer not null primary key,"+
				"   user_name            varchar(20),"+
				"   group_name           varchar(40),"+
				"   login_time           varchar(14)"+
				");";
		return sql;
	}*/


	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getGroupName() {
		return groupName;
	}


	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	public String getLoginTime() {
		return loginTime;
	}


	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

}
