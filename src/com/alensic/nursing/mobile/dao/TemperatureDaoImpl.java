package com.alensic.nursing.mobile.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alensic.nursing.mobile.common.BaseDao;
import com.alensic.nursing.mobile.model.Bed;
import com.alensic.nursing.mobile.model.BedTemperature;
import com.alensic.nursing.mobile.model.Users;
import com.alensic.nursing.mobile.util.DateUtil;

public class TemperatureDaoImpl extends BaseDao implements TemperatureDao {

	public TemperatureDaoImpl(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	@Override
	public BedTemperature getById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BedTemperature> query(BedTemperature bean) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	@Override
	public List<BedTemperature> queryForUpload(){
		List<BedTemperature> list = new ArrayList<BedTemperature>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = null;
		try{
			String sql = "select bt.*,b.bed_no from "+BedTemperature.TABLE_NAME+" bt join "+Bed.TABLE_NAME+" b on bt.bed_id=b._id where upload_id is null order by bt._id asc ";
			cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()){
				BedTemperature bt = new BedTemperature();
				bt.setId(cursor.getInt(cursor.getColumnIndex(BedTemperature._ID)));
				bt.setBedId(cursor.getInt(cursor.getColumnIndex(BedTemperature.BED_ID)));
				bt.setInDate(cursor.getString(cursor.getColumnIndex(BedTemperature.INDATE)));
				bt.setTemperature(cursor.getString(cursor.getColumnIndex(BedTemperature.TEMPERATURE)));
				bt.setBedNo(cursor.getString(cursor.getColumnIndex(Bed.BED_NO)));
				bt.setDataType(cursor.getString(cursor.getColumnIndex(BedTemperature.DATA_TYPE)));
				list.add(bt);
			}
		}finally{
			if(cursor !=null) cursor.close();
			if(db!=null)db.close();
		}
		return list;
		
	}
	

	@Override
	public List<BedTemperature> queryForUpload(Integer uploadId){
		List<BedTemperature> list = new ArrayList<BedTemperature>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = null;
		try{
			String sql = "select bt.*,b.bed_no from "+BedTemperature.TABLE_NAME+" bt join "+Bed.TABLE_NAME+" b on bt.bed_id=b._id where upload_id=? order by bt._id asc ";
			cursor = db.rawQuery(sql, new String[]{uploadId.toString()});
			while(cursor.moveToNext()){
				BedTemperature bt = new BedTemperature();
				bt.setId(cursor.getInt(cursor.getColumnIndex(BedTemperature._ID)));
				bt.setBedId(cursor.getInt(cursor.getColumnIndex(BedTemperature.BED_ID)));
				bt.setInDate(cursor.getString(cursor.getColumnIndex(BedTemperature.INDATE)));
				bt.setTemperature(cursor.getString(cursor.getColumnIndex(BedTemperature.TEMPERATURE)));
				bt.setBedNo(cursor.getString(cursor.getColumnIndex(Bed.BED_NO)));
				bt.setDataType(cursor.getString(cursor.getColumnIndex(BedTemperature.DATA_TYPE)));
				list.add(bt);
			}
		}finally{
			if(cursor !=null) cursor.close();
			db.close();
		}
		return list;
		
	}
	

	@Override
	public void insert(BedTemperature bean) {
		//String sql = "select * from "+BedTemperature.TABLE_NAME+" where bed_id=? order by _id desc";
		//SQLiteDatabase db = helper.getReadableDatabase();
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BedTemperature.BED_ID, bean.getBedId());
		values.put(BedTemperature.TEMPERATURE, bean.getTemperature());
		values.put(BedTemperature.INDATE, bean.getInDate());
		values.put(BedTemperature.DATA_TYPE, bean.getDataType());
		db.insert(BedTemperature.TABLE_NAME, null, values);
		Cursor cursor = db.rawQuery("select last_insert_rowid()", null);
		if(cursor.moveToNext()){
			bean.setId(cursor.getInt(0));
		}
		cursor.close();
		db.close();

	}

	@Override
	public void update(BedTemperature bean) {

		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BedTemperature.BED_ID, bean.getBedId());
		values.put(BedTemperature.TEMPERATURE, bean.getTemperature());
		values.put(BedTemperature.INDATE, bean.getInDate());
		values.put(BedTemperature.DATA_TYPE, bean.getDataType());
		db.update(BedTemperature.TABLE_NAME, values," _id=? ",new String[]{bean.getId().toString()});
		db.close();

	}

	@Override
	public void delete(Integer id) {

		ContentValues values = new ContentValues();
		values.put(BedTemperature._ID, id);
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(BedTemperature.TABLE_NAME, "_id=?", new String[]{Integer.toString(id)});

		db.close();

	}

}
