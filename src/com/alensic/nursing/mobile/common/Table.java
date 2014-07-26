/**
 * 
 */
package com.alensic.nursing.mobile.common;

import android.provider.BaseColumns;

/**表接口
 * @author xwlian
 *
 */
public interface Table extends BaseColumns {


	
	//public String getCreateSql();
	
	public String getTableName();
	public Integer getId();
	public void setId(Integer id);
}
