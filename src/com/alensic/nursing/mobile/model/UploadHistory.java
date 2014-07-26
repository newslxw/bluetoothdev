package com.alensic.nursing.mobile.model;

import com.alensic.nursing.mobile.common.BaseTable;

public final class UploadHistory extends BaseTable {


	public final static String TABLE_NAME = "t_upload_history";
	public final static String UPLOAD_NAME   ="upload_name";   
	public final static String UPLOAD_TIME   ="upload_time";   
	public final static String USER_NAME     ="user_name";     
	public final static String GROUP_NAME    ="group_name";    
	public final static String UPLOAD_WAY    ="upload_way";    
	public final static String BLUETOOTH_NAME="bluetooth_name";
	public final static String UNIQUE_TAG="unique_tag";

	private String uploadName;
	private String uploadTime;
	private String userName;
	private String uploadWay;//定义在Constants.UploadWay
	private String bluetoothName;
	private String groupName;
	private String uniqueTag;
	
	
	/*public String getCreateSql() {
		String sql = "create table t_upload_history                    "+
				"(                                                  "+
				"   _id                  integer not null primary key,"+
				"   upload_name          varchar(40),"+
				"   upload_time          varchar(14),"+
				"   user_name            varchar(20),"+
				"   group_name           varchar(40),              "+
				"   upload_way           varchar(10) ,"+
				"   bluetooth_name       varchar(20)         "+
				");";
		return sql;
	}*/

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	public String getUploadName() {
		return uploadName;
	}

	public void setUploadName(String uploadName) {
		this.uploadName = uploadName;
	}

	public String getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUploadWay() {
		return uploadWay;
	}

	public void setUploadWay(String uploadWay) {
		this.uploadWay = uploadWay;
	}

	public String getBluetoothName() {
		return bluetoothName;
	}

	public void setBluetoothName(String bluetoothName) {
		this.bluetoothName = bluetoothName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getUniqueTag() {
		return uniqueTag;
	}

	public void setUniqueTag(String uniqueTag) {
		this.uniqueTag = uniqueTag;
	}
}
