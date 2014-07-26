package com.alensic.nursing.mobile.dao;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alensic.nursing.mobile.common.BaseDao;
import com.alensic.nursing.mobile.model.SysParam;

public class SysParamDaoImpl extends BaseDao implements SysParamDao {

	public SysParamDaoImpl(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	@Override
	public SysParam getById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SysParam> query(SysParam bean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(SysParam bean) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(SysParam bean) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub

	}

	@Override
	public String queryValue4Code(String code) {
		String value;
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try{
			db = helper.getReadableDatabase();
			cursor = db.query(SysParam.TABLE_NAME, new String[]{SysParam.PARAM_VALUE}, SysParam.PARAM_CODE + " = ? ", new String[]{code}, null, null, null);
			if(cursor.moveToNext()){
				value = cursor.getString(0);
			}else{
				value = null;
			}
		}finally{
			if(cursor!=null)cursor.close();
			if(db!=null)db.close();
		}
		return value;
	}

	@Override
	public void updateValue4Code(String code, String value) {
		SQLiteDatabase db = null;
		try{
			db=helper.getReadableDatabase();
			ContentValues values = new ContentValues();
			values.put(SysParam.PARAM_VALUE, value);
			db.update(SysParam.TABLE_NAME, values, SysParam.PARAM_CODE + " = ? ", new String[]{code});
		}finally{
			if(db !=null)db.close();
		}
	}

	
}
