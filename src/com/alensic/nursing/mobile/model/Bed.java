/**
 * 
 */
package com.alensic.nursing.mobile.model;

import com.alensic.nursing.mobile.common.BaseTable;

/**
 * @author xwlian
 *
 */
public class Bed extends BaseTable {

	public static final String TABLE_NAME = "t_bed";
	public static final String BED_NO = "bed_no";
	private String bedNo;
	private BedTemperature temperature; //体温
	private BedTemperature temperatureBak; //数据库中体温，在接收测量之后，又想恢复时，可以用在个先备份原来温度
	private Boolean isAssign;
	
	
	
	

	/* (non-Javadoc)
	 * @see com.alensic.nursing.mobile.common.BaseTable#getCreateSql()
	 */
	/*public String getCreateSql() {
		String sql = "";
		sql+="create table t_bed"+
		"("+
		 "  integer              int not null primary key,"+
		 "  bed_no               varchar(20)"+
		 ");";
		return sql;
	}*/

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	public String getBedNo() {
		return bedNo;
	}

	public void setBedNo(String bedNo) {
		this.bedNo = bedNo;
	}

	public BedTemperature getTemperature() {
		return temperature;
	}

	public void setTemperature(BedTemperature temperature) {
		this.temperature = temperature;
	}

	public BedTemperature getTemperatureBak() {
		return temperatureBak;
	}

	public void setTemperatureBak(BedTemperature temperatureBak) {
		this.temperatureBak = temperatureBak;
	}

	public Boolean getIsAssign() {
		return isAssign;
	}

	public void setIsAssign(Boolean isAssign) {
		this.isAssign = isAssign;
	}

	
	
}
