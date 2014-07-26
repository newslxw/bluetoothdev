/**
 * 
 */
package com.alensic.nursing.mobile.model;

import com.alensic.nursing.mobile.common.BaseTable;
import com.alensic.nursing.mobile.util.StringUtils;

/**
 * @author xwlian
 *
 */
public class BedTemperature extends BaseTable {

	public final static String TABLE_NAME = "t_bed_temperature";
	public final static String BED_ID       ="bed_id";       
	public final static String TEMPERATURE  ="temperature";  
	public final static String INDATE       ="indate";       
	public final static String UPLOAD_ID    ="upload_id";  
	public final static String DATA_TYPE    ="data_type";  
	public final static String TYPE_EW = "EW";  //测量类型-耳温
	public final static String TYPE_BLOOD = "BLOOD";  //测量类型-血压
	
	private Integer bedId ;
	private String temperature;
	private String inDate;
	private Integer uploadId;
	private String bedNo;
	private String dataType;
	
	/**
	 * 按照显示要求格式化测量的数据，一个测量数据如果包含多个值，用#连接。
	 * @return
	 */
	public String showTemperature(){
		String ret = temperature;
		if(StringUtils.isEmpty(ret)) return ret;
		if(TYPE_BLOOD.equals(dataType)){
			String[] arr = ret.split("#");
			ret = arr[0]+" mmHg " + arr[1]+" MAP "+ arr[2]+" 脉率";
		}
		return ret;
	}
	
	/*public String getCreateSql() {
		String sql = "create table t_bed_temperature					"+
				"(                                                      "+
				"   _id                  integer not null primary key,  "+
				"   bed_id               integer,                       "+
				"   temperature          varchar(6),                    "+
				"   indate               varchar(14),                   "+
				"   upload_id            integer                        "+
				");                                                     ";

		return sql;
	}*/

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

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getInDate() {
		return inDate;
	}

	public void setInDate(String inDate) {
		this.inDate = inDate;
	}

	public Integer getUploadId() {
		return uploadId;
	}

	public void setUploadId(Integer uploadId) {
		this.uploadId = uploadId;
	}

	public String getBedNo() {
		return bedNo;
	}

	public void setBedNo(String bedNo) {
		this.bedNo = bedNo;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
