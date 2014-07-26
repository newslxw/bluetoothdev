package com.alensic.nursing.mobile.dao;

import com.alensic.nursing.mobile.common.Dao;
import com.alensic.nursing.mobile.model.SysParam;

public interface SysParamDao  extends Dao<SysParam> {

	String queryValue4Code(String code);
	
	void updateValue4Code(String code, String value);
}
