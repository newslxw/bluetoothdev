package com.alensic.nursing.mobile.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alensic.nursing.mobile.common.BaseDao;
import com.alensic.nursing.mobile.model.Bed;
import com.alensic.nursing.mobile.model.GroupBeds;
import com.alensic.nursing.mobile.model.Users;

public class GroupBedsDaoImpl extends BaseDao implements GroupBedsDao {

	public GroupBedsDaoImpl(Context ctx) {
		super(ctx);
	}

	@Override
	public GroupBeds getById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GroupBeds> query(GroupBeds bean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(GroupBeds bean) {
		SQLiteDatabase db = helper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put(GroupBeds.BED_ID, bean.getBedId());
		values.put(GroupBeds.GROUPS_ID, bean.getGroupsId());
		db.insert(GroupBeds.TABLE_NAME, null, values);
	}

	@Override
	public void update(GroupBeds bean) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delByBedId(Integer bedId) {
		SQLiteDatabase db = helper.getReadableDatabase();
		db.delete(GroupBeds.TABLE_NAME, GroupBeds.BED_ID + " = ? ", new String[]{String.valueOf(bedId)});
	}

	@Override
	public void delByGroupId(Integer groupId) {
		SQLiteDatabase db = helper.getReadableDatabase();
		db.delete(GroupBeds.TABLE_NAME, GroupBeds.GROUPS_ID + " = ? ", new String[]{String.valueOf(groupId)});
	}

	@Override
	public void delByBedAndGroupId(Integer bedId, Integer groupId) {
		SQLiteDatabase db = helper.getReadableDatabase();
		db.delete(GroupBeds.TABLE_NAME, GroupBeds.BED_ID + " = ? AND " + GroupBeds.GROUPS_ID + " = ? ", new String[]{String.valueOf(bedId), String.valueOf(groupId)});
	}

	@Override
	public List<Bed> queryAllBed4Group(Integer groupId) {
		String sql = "SELECT b.* , gb." + GroupBeds.GROUPS_ID + " AS assigned FROM " + Bed.TABLE_NAME + " AS b LEFT JOIN " + GroupBeds.TABLE_NAME + " AS gb ON b." + Bed._ID + " = gb." + GroupBeds.BED_ID + " AND gb." + GroupBeds.GROUPS_ID + " = ? ORDER BY assigned DESC";
		SQLiteDatabase db = null;
		List<Bed> list = new ArrayList<Bed>();
		Cursor cursor = null;
		try{
			db = helper.getReadableDatabase();
			cursor = db.rawQuery(sql, new String[]{String.valueOf(groupId)});
			if(cursor != null){
				while(cursor.moveToNext()){
					Bed b = new Bed();
					Integer _id = cursor.getInt(cursor.getColumnIndex(Bed._ID));
					b.setId(_id);
					b.setBedNo(cursor.getString(cursor.getColumnIndex(Bed.BED_NO)));
					b.setIsAssign(cursor.getString(cursor.getColumnIndex("assigned")) != null);
					list.add( b);
				}
			}
		}finally{
			if(cursor!=null)cursor.close();
			if(db!=null)db.close();
		}
		return list;
	}

}
