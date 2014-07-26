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
import com.alensic.nursing.mobile.model.Groups;
import com.alensic.nursing.mobile.model.Users;

public class GroupsDaoImpl extends BaseDao implements GroupsDao {

	public GroupsDaoImpl(Context ctx) {
		super(ctx);
	}

	@Override
	public Groups getById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Groups> query(Groups bean) {
		SQLiteDatabase db = null;
		List<Groups> list = new ArrayList<Groups>();
		Cursor cursor = null;
		try{
			db = helper.getReadableDatabase();
			cursor = db.query(Groups.TABLE_NAME, null, null, null, null, null,null);
			if(cursor != null){
				while(cursor.moveToNext()){
					Groups b = new Groups();
					Integer _id = cursor.getInt(cursor.getColumnIndex(Groups._ID));
					b.setId(_id);
					b.setGroupName(cursor.getString(cursor.getColumnIndex(Groups.GROUP_NAME)));
					b.setRemovable(cursor.getInt(cursor.getColumnIndex(Groups.REMOVABLE)));
					list.add( b);
				}
			}
		}finally{
			if(cursor!=null)cursor.close();
			if(db!=null)db.close();
		}
		return list;
	}

	@Override
	public void insert(Groups bean) {
		SQLiteDatabase db = helper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put(Groups.GROUP_NAME, bean.getGroupName());
		values.put(Groups.REMOVABLE, bean.getRemovable() == null ? 1 : bean.getRemovable());
		db.insert(Groups.TABLE_NAME, null, values);
		db.close();
	}

	@Override
	public void update(Groups bean) {
		SQLiteDatabase db = helper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put(Groups.GROUP_NAME, bean.getGroupName());
		if(bean.getRemovable() != null){
			values.put(Groups.REMOVABLE, bean.getRemovable());
		}
		db.update(Groups.TABLE_NAME, values, Groups._ID + " = ? ", new String[]{String.valueOf(bean.getId())});
		db.close();

	}

	@Override
	public void delete(Integer id) {
		SQLiteDatabase db = helper.getReadableDatabase();
		db.delete(Groups.TABLE_NAME, Groups._ID + " = ? ", new String[]{String.valueOf(id)});
	}

	@Override
	public List<Groups> queryGroupAndAssignedTotal() {
		Groups groups = null;
		List<Groups> list = new ArrayList<Groups>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = null;
		String sql = "SELECT g."+Groups._ID+", g."+Groups.GROUP_NAME+", g."+Groups.REMOVABLE+", g."+Groups.GROUP_NAME+" || '(' || ifnull(c.count,'0') || ')' AS "+GROUP_NAME_AND_ASSIGNED_TOTAL+" FROM "+Groups.TABLE_NAME+" g LEFT JOIN (select gb."+GroupBeds.GROUPS_ID+" AS groups_id,count(gb."+GroupBeds.BED_ID+") AS count FROM "+GroupBeds.TABLE_NAME+" AS gb GROUP BY gb."+GroupBeds.GROUPS_ID+") AS c ON g."+Groups._ID+" = c.groups_id ";
		try{
			cursor= db.rawQuery(sql, null);
			while(cursor!=null&&cursor.moveToNext()){
				groups = new Groups();
				Integer _id = cursor.getInt(cursor.getColumnIndex(Groups._ID));
				groups.setId(_id);
				groups.setGroupName(cursor.getString(cursor.getColumnIndex(Groups.GROUP_NAME)));
				groups.setRemovable(cursor.getInt(cursor.getColumnIndex(Groups.REMOVABLE)));
				groups.setGroupAT(cursor.getString(cursor.getColumnIndex(GROUP_NAME_AND_ASSIGNED_TOTAL)));
				list.add(groups);
			}
		}finally{
			if(cursor!=null)cursor.close();
			if(db!=null)db.close();
		}
		return list;
	}

	@Override
	public Groups addGroup(Groups bean) {
		int count = countByGroupName(bean.getGroupName());
		if(count > 0){
			return null;
		}
		
		insert(bean);
		return bean;
	}

	@Override
	public Groups modificationGroup(Groups bean) {
		int count = countByGroupName(bean.getGroupName());
		if(count > 0){
			return null;
		}
		update(bean);
		return bean;
	}

	@Override
	public int countByGroupName(String groupName) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(Groups.TABLE_NAME, null, Groups.GROUP_NAME + " = ?", new String[]{groupName}, null, null, null);
		int count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}

	
}
