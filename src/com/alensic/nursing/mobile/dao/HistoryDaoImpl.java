package com.alensic.nursing.mobile.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateUtils;
import android.util.Log;

import com.alensic.nursing.mobile.R;
import com.alensic.nursing.mobile.common.BaseDao;
import com.alensic.nursing.mobile.common.Session;
import com.alensic.nursing.mobile.exception.DBException;
import com.alensic.nursing.mobile.model.Bed;
import com.alensic.nursing.mobile.model.BedTemperature;
import com.alensic.nursing.mobile.model.Groups;
import com.alensic.nursing.mobile.model.UploadHistory;
import com.alensic.nursing.mobile.model.Users;
import com.alensic.nursing.mobile.util.Constants;
import com.alensic.nursing.mobile.util.DateUtil;
import com.alensic.nursing.mobile.util.PreferenceHelper;

public class HistoryDaoImpl extends BaseDao implements HistoryDao {

	
	public HistoryDaoImpl(Context ctx) {
		super(ctx);
	}

	@Override
	public UploadHistory getById(Integer id) {
		return null;
	}

	@Override
	public List<UploadHistory> query(UploadHistory bean) {
		SQLiteDatabase db = null;
		List<UploadHistory> list = new ArrayList<UploadHistory>();
		Cursor cursor = null;
		try{
			db = helper.getReadableDatabase();
			cursor = db.query(UploadHistory.TABLE_NAME, null, null, null, null, null," _id desc");
			if(cursor != null){
				while(cursor.moveToNext()){
					UploadHistory b = new UploadHistory();
					Integer _id = cursor.getInt(cursor.getColumnIndex(UploadHistory._ID));
					b.setId(_id);
					b.setBluetoothName(cursor.getString(cursor.getColumnIndex(UploadHistory.BLUETOOTH_NAME)));
					b.setGroupName(cursor.getString(cursor.getColumnIndex(UploadHistory.GROUP_NAME)));
					b.setUniqueTag(cursor.getString(cursor.getColumnIndex(UploadHistory.UNIQUE_TAG)));
					b.setUploadName(cursor.getString(cursor.getColumnIndex(UploadHistory.UPLOAD_NAME)));
					b.setUploadTime(cursor.getString(cursor.getColumnIndex(UploadHistory.UPLOAD_TIME)));
					b.setUploadWay(cursor.getString(cursor.getColumnIndex(UploadHistory.UPLOAD_WAY)));
					b.setUserName(cursor.getString(cursor.getColumnIndex(UploadHistory.USER_NAME)));
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
	public void insert(UploadHistory bean) {
		/*ContentValues values = new ContentValues();
		values.put(Users.USER_NAME, bean.getUserName());
		values.put(Users.GROUP_NAME, bean.getGroupName());
		values.put(Users.LOGIN_TIME, DateUtil.getDateTimeStr());
		SQLiteDatabase db = helper.getWritableDatabase();
		db.insert(Users.TABLE_NAME, null, values);*/
	}

	@Override
	public void update(UploadHistory bean) {
		// TODO Auto-generated method stub

	}
	

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<UploadHistory> queryForList(UploadHistory bean) {
		List<UploadHistory> list = new ArrayList<UploadHistory>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(UploadHistory.TABLE_NAME, null, null, null, null, null," _id desc");
		try {
			while (cursor.moveToNext()) {
				UploadHistory h = new UploadHistory();
				h.setBluetoothName(cursor.getString(cursor
						.getColumnIndex(UploadHistory.BLUETOOTH_NAME)));
				h.setId(cursor.getInt(cursor.getColumnIndex(UploadHistory._ID)));
				h.setUploadName(cursor.getString(cursor
						.getColumnIndex(UploadHistory.UPLOAD_NAME)));
				h.setUploadTime(cursor.getString(cursor
						.getColumnIndex(UploadHistory.UPLOAD_TIME)));
				h.setUploadWay(cursor.getString(cursor
						.getColumnIndex(UploadHistory.UPLOAD_WAY)));
				h.setUserName(cursor.getString(cursor
						.getColumnIndex(UploadHistory.USER_NAME)));
				h.setUniqueTag(cursor.getString(cursor.getColumnIndex(UploadHistory.UNIQUE_TAG)));
				list.add(h);
			}
		} finally {
			cursor.close();
			db.close();
		}
		return list;
	}

	@Override
	public void clear() {
		SQLiteDatabase db = helper.getWritableDatabase();
		
		try {
			db.beginTransaction();
			db.delete(UploadHistory.TABLE_NAME, null, null);
			db.delete(BedTemperature.TABLE_NAME, "upload_id is not null", null);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "", e);
			throw new DBException(
					"清空上传历史和已上传的体温数据失败",
					e);
		} finally {
			db.endTransaction();
			db.close();
		}
		
	}

	@Override
	public void uploadFinish(UploadHistory bean) {
		SQLiteDatabase db = helper.getWritableDatabase();
		
		try {
			db.beginTransaction();

			ContentValues values = new ContentValues();
			values.put(UploadHistory.USER_NAME, bean.getUserName());
			values.put(UploadHistory.BLUETOOTH_NAME, bean.getBluetoothName());
			values.put(UploadHistory.UPLOAD_NAME, bean.getUploadName());
			values.put(UploadHistory.UPLOAD_TIME, bean.getUploadTime());
			values.put(UploadHistory.UPLOAD_WAY, bean.getUploadWay());
			values.put(UploadHistory.GROUP_NAME, bean.getGroupName());
			values.put(UploadHistory.UNIQUE_TAG, bean.getUniqueTag());
			db.insert(UploadHistory.TABLE_NAME, null, values);
			Cursor cursor = db.rawQuery("select last_insert_rowid()",null);
			Integer uploadId = null;
			if(cursor.moveToNext()){
				uploadId = cursor.getInt(0);
				bean.setId(uploadId);
			}
			cursor.close();

			values = new ContentValues();
			values.put(BedTemperature.UPLOAD_ID, uploadId);
			db.update(BedTemperature.TABLE_NAME, values," upload_id is null ",null);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "", e);
			throw new DBException(
					"清空上传历史和已上传的体温数据失败",
					e);
		} finally {
			db.endTransaction();
			db.close();
		}
		
	}
	

	@Override
	public void reuploadFinish(UploadHistory bean) {
		SQLiteDatabase db = helper.getWritableDatabase();
		
		try {
			ContentValues values = new ContentValues();
			values.put(UploadHistory.USER_NAME, bean.getUserName());
			values.put(UploadHistory.BLUETOOTH_NAME, bean.getBluetoothName());
			values.put(UploadHistory.UPLOAD_TIME, bean.getUploadTime());
			values.put(UploadHistory.UPLOAD_WAY, bean.getUploadWay());
			values.put(UploadHistory.GROUP_NAME, bean.getGroupName());
			db.update(UploadHistory.TABLE_NAME,values,"_id=?",new String[]{bean.getId().toString()});
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "", e);
			throw new DBException(
					"清空上传历史和已上传的体温数据失败",
					e);
		} finally {
			db.close();
		}
		
	}

	
}
