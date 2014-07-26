package com.alensic.nursing.mobile.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alensic.nursing.mobile.common.BaseDao;
import com.alensic.nursing.mobile.model.Bed;
import com.alensic.nursing.mobile.model.BedTemperature;
import com.alensic.nursing.mobile.model.GroupBeds;
import com.alensic.nursing.mobile.model.BedTemperature;
import com.alensic.nursing.mobile.model.GroupBeds;
import com.alensic.nursing.mobile.util.StringUtils;

public class BedDaoImpl extends BaseDao implements BedDao {

	public BedDaoImpl(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Bed getById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Bed> query(Bed bean) {
		SQLiteDatabase db = null;
		List<Bed> list = new ArrayList<Bed>();
		Cursor cursor = null;
		try{
			db = helper.getReadableDatabase();
			cursor = db.query(Bed.TABLE_NAME, null, null, null, null, null,null);
			if(cursor != null){
				while(cursor.moveToNext()){
					Bed b = new Bed();
					Integer bed_id = cursor.getInt(cursor.getColumnIndex(Bed._ID));
					b.setId(bed_id);
					b.setBedNo(cursor.getString(cursor.getColumnIndex(Bed.BED_NO)));
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
	public void insert(Bed bean) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Bed.BED_NO, bean.getBedNo());
		db.insert(Bed.TABLE_NAME, null, values);
		db.close();
	}

	@Override
	public void update(Bed bean) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Bed.BED_NO, bean.getBedNo());
		db.update(Bed.TABLE_NAME, values, Bed._ID + " = ? ", new String[]{String.valueOf(bean.getId())});
		db.close();
	}

	@Override
	public void delete(Integer id) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(Bed.TABLE_NAME, Bed._ID + " = ? ", new String[]{String.valueOf(id)});
		db.close();
	}
	
	@Override
	public Bed addBed(Bed bean){
		if(countByBedNo(bean.getBedNo()) > 0){ //记录已经存在
			return null;
		}
		
		insert(bean);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(Bed.TABLE_NAME, new String[]{Bed._ID}, Bed.BED_NO + " = ?", new String[]{bean.getBedNo()}, null, null, null);
		if(cursor.moveToNext()){
			bean.setId(cursor.getInt(cursor.getColumnIndex(Bed._ID)));
		}
		cursor.close();
		db.close();
		return bean;
	}
	
	@Override
	public Bed modificationBed(Bed bean) {
		if(countByBedNo(bean.getBedNo()) > 0){ //记录已经存在
			return null;
		}
		update(bean);
		return bean;
	}

	@Override
	public int countByBedNo(String bedNo) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(Bed.TABLE_NAME, null, Bed.BED_NO + " = ?", new String[]{bedNo}, null, null, null);
		
		int count = cursor.getCount();
		
		cursor.close();
		db.close();
		
		return count;
	}

	@Override
	public List<Bed> queryGroupBed(String groupID){
		String sql = "SELECT b.* FROM " + Bed.TABLE_NAME + " AS b, " + GroupBeds.TABLE_NAME + " AS gb WHERE b." + Bed._ID + " = gb." + GroupBeds.BED_ID + " AND gb." + GroupBeds.GROUPS_ID + " = ?" ;
		SQLiteDatabase db = null;
		List<Bed> list = new ArrayList<Bed>();
		Cursor cursor = null;
		try{
			db = helper.getReadableDatabase();
			cursor = db.rawQuery(sql, new String[]{groupID});
			if(cursor != null){
				while(cursor.moveToNext()){
					Bed bean = new Bed();
					Integer bed_id = cursor.getInt(cursor.getColumnIndex(Bed._ID));
					bean.setId(bed_id);
					bean.setBedNo(cursor.getString(cursor.getColumnIndex(Bed.BED_NO)));
					list.add(bean);
				}
			}
		}finally{
			if(cursor!=null)cursor.close();
			if(db!=null)db.close();
		}
		return list;
	}

	@Override
	public List<Bed> queryByGroupNo(Integer groupId,boolean orderByTemperature,String dataType) {
		String []selectionArgs= null;
		List<Bed> list = new ArrayList<Bed>();
		Map<Integer,Integer> posMap = new HashMap<Integer,Integer>(); //记下病床在list中的索引
		Cursor cursor = null;
		String sql = "";
		String orderBy = "order by b._id asc,temperature_id desc ";
		if(orderByTemperature){
			orderBy = " order by temperature_id desc, b._id asc ";
		}
		if(groupId!=null){
			sql="select b.*,bt.temperature,bt.indate,bt._id temperature_id,bt.data_type from " + Bed.TABLE_NAME + " b join "+GroupBeds.TABLE_NAME + " gb on b._id=gb.bed_id " +
				"left join "+BedTemperature.TABLE_NAME + " bt on b._id=bt.bed_id and bt.upload_id  is null and bt.data_type=? where gb.groups_id=? "+orderBy;
			selectionArgs = new String[]{dataType,groupId.toString()}; 
		}else{
			sql="select b.*,bt.temperature,bt.indate,bt._id temperature_id,bt.data_type from " + Bed.TABLE_NAME + " b " +
					"left join "+BedTemperature.TABLE_NAME + " bt on b._id=bt.bed_id and bt.upload_id is null  and bt.data_type=?  "+orderBy;
			selectionArgs = new String[]{dataType}; 
		}
		SQLiteDatabase db = helper.getReadableDatabase();
		try{
			cursor = db.rawQuery(sql, selectionArgs);
			if(cursor != null){
				while(cursor.moveToNext()){
					Bed bean = null;
					BedTemperature bt = new BedTemperature();
					Integer bed_id = cursor.getInt(cursor.getColumnIndex(Bed._ID));
					
					bt.setId(cursor.getInt(cursor.getColumnIndex("temperature_id")));
					bt.setInDate(cursor.getString(cursor.getColumnIndex(BedTemperature.INDATE)));
					bt.setTemperature(cursor.getString(cursor.getColumnIndex(BedTemperature.TEMPERATURE)));
					bt.setDataType(cursor.getString(cursor.getColumnIndex(BedTemperature.DATA_TYPE)));
					bt.setDataType(dataType);
					
					if(posMap.containsKey(bed_id)){//如果病床已经存在，说明这个病床包含多个没有上传的体温数据，只取最新体温
						bean = list.get(posMap.get(bed_id));
						if(bean.getTemperature().getId()>bt.getId()) continue; //id大的是新的测量值，list中的体温值较新，所以保留
					}else{
						bean = new Bed();
						bean.setBedNo(cursor.getString(cursor.getColumnIndex(Bed.BED_NO)));
						bean.setId(bed_id);
						posMap.put(bed_id, list.size());
						list.add(bean);
					}
					bean.setTemperature(bt);
				}
			}
		}finally{
			if(cursor !=null)cursor.close();
			db.close();
		}
		return list;
	}

	@Override
	public List<Bed> queryByUploadId(Integer uploadId) {
		List<Bed> list = new ArrayList<Bed>();
		String sql="select b.*,bt.temperature,bt.indate,bt._id temperature_id,bt.data_type from " + Bed.TABLE_NAME + " b " +
				" join "+BedTemperature.TABLE_NAME + " bt on b._id=bt.bed_id where bt.upload_id=?  order by b._id asc";

		Cursor cursor = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		try {
			cursor = db.rawQuery(sql, new String[] { uploadId.toString() });
			if (cursor != null) {
				while (cursor.moveToNext()) {
					Bed bean = null;
					BedTemperature bt = new BedTemperature();
					Integer bed_id = cursor.getInt(cursor.getColumnIndex(Bed._ID));

					bt.setId(cursor.getInt(cursor.getColumnIndex("temperature_id")));
					bt.setInDate(cursor.getString(cursor.getColumnIndex(BedTemperature.INDATE)));
					bt.setTemperature(cursor.getString(cursor.getColumnIndex(BedTemperature.TEMPERATURE)));
					bt.setDataType(cursor.getString(cursor.getColumnIndex(BedTemperature.DATA_TYPE)));
					bean = new Bed();
					bean.setBedNo(cursor.getString(cursor.getColumnIndex(Bed.BED_NO)));
					bean.setId(bed_id);
					bean.setTemperature(bt);
					list.add(bean);
				}
			}
		} finally {
			if (cursor != null)
				cursor.close();
			if(db!=null)db.close();
		}
		
		return list;
	}

	@Override
	public List<Bed> queryNotUploadData() {
		List<Bed> list = new ArrayList<Bed>();
		String sql="select b.*,bt.temperature,bt.indate,bt._id temperature_id,bt.data_type from " + Bed.TABLE_NAME + " b " +
				" join "+BedTemperature.TABLE_NAME + " bt on b._id=bt.bed_id and bt.upload_id is null   order by b._id asc";

		Cursor cursor = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		try {
			cursor = db.rawQuery(sql,null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					Bed bean = null;
					BedTemperature bt = new BedTemperature();
					Integer bed_id = cursor.getInt(cursor.getColumnIndex(Bed._ID));

					bt.setId(cursor.getInt(cursor.getColumnIndex("temperature_id")));
					bt.setInDate(cursor.getString(cursor.getColumnIndex(BedTemperature.INDATE)));
					bt.setTemperature(cursor.getString(cursor.getColumnIndex(BedTemperature.TEMPERATURE)));
					bt.setDataType(cursor.getString(cursor.getColumnIndex(BedTemperature.DATA_TYPE)));
					bean = new Bed();
					bean.setBedNo(cursor.getString(cursor.getColumnIndex(Bed.BED_NO)));
					bean.setId(bed_id);
					bean.setTemperature(bt);
					list.add(bean);
				}
			}
		} finally {
			if (cursor != null)
				cursor.close();
			if(db!=null)db.close();
		}
		
		return list;
	}

}
