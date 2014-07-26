package com.alensic.nursing.mobile.model;

import com.alensic.nursing.mobile.common.BaseTable;

public final class SysParam extends BaseTable {

	public final static String TABLE_NAME = "t_sys_param";
	public final static String PARAM_CODE ="param_code" ; 
	public final static String PARAM_DESC ="param_desc" ; 
	public final static String PARAM_VALUE="param_value"; 
	public final static String CONFIGABLE ="configable" ; 
	public final static String PARAM_TYPE ="param_type" ; 

	private String paramCode;
	private String paramDesc;
	private String paramValue;
	private String paramType;//定义在Constants.ParamType
	private Integer configable;//定义在Constants.enable/disable
	
	
	
	/*public String getCreateSql() {
		String sql = "create table t_sys_param    "+
				"("+
				"   _id                  integer not null primary key,    "+
				"   param_code           varchar(40),                  "+
				"   param_desc           varchar(100),"+
				"   param_value          varchar(100),               "+
				"   configable           integer,"+
				"   param_type           varchar(10) "+
				");";
		return sql;
	}*/

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	public String getParamCode() {
		return paramCode;
	}

	public void setParamCode(String paramCode) {
		this.paramCode = paramCode;
	}

	public String getParamDesc() {
		return paramDesc;
	}

	public void setParamDesc(String paramDesc) {
		this.paramDesc = paramDesc;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public Integer getConfigable() {
		return configable;
	}

	public void setConfigable(Integer configable) {
		this.configable = configable;
	}
}
