package com.alensic.nursing.mobile.dao;

import java.util.List;

import android.database.Cursor;

import com.alensic.nursing.mobile.common.Dao;
import com.alensic.nursing.mobile.model.Bed;
import com.alensic.nursing.mobile.model.GroupBeds;

public interface GroupBedsDao extends Dao<GroupBeds> {
	
	void delByBedId(Integer bedId);
	
	void delByGroupId(Integer groupId);
	
	void delByBedAndGroupId(Integer bedId, Integer groupId);
	
	List<Bed> queryAllBed4Group(Integer groupId);

}
