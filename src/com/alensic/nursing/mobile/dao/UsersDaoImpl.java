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

import com.alensic.nursing.mobile.common.BaseDao;
import com.alensic.nursing.mobile.common.Session;
import com.alensic.nursing.mobile.model.Bed;
import com.alensic.nursing.mobile.model.UploadHistory;
import com.alensic.nursing.mobile.model.Users;
import com.alensic.nursing.mobile.util.Constants;
import com.alensic.nursing.mobile.util.DateUtil;
import com.alensic.nursing.mobile.util.PreferenceHelper;

public class UsersDaoImpl extends BaseDao implements UsersDao {

	
	public UsersDaoImpl(Context ctx) {
		super(ctx);
	}

	@Override
	public Users getById(Integer id) {
		return null;
	}

	@Override
	public List<Users> query(Users bean) {
		
		SQLiteDatabase db = null;
		List<Users> list = new ArrayList<Users>();
		Cursor cursor = null;
		try{
			db = helper.getReadableDatabase();
			cursor = db.query(Users.TABLE_NAME, null, null, null, null, null,null);
			if(cursor != null){
				while(cursor.moveToNext()){
					Users b = new Users();
					Integer _id = cursor.getInt(cursor.getColumnIndex(Bed._ID));
					b.setId(_id);
					b.setGroupName(cursor.getString(cursor.getColumnIndex(Users.USER_NAME)));
					b.setLoginTime(cursor.getString(cursor.getColumnIndex(Users.LOGIN_TIME)));
					b.setUserName(cursor.getString(cursor.getColumnIndex(Users.USER_NAME)));
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
	public void insert(Users bean) {
		ContentValues values = new ContentValues();
		values.put(Users.USER_NAME, bean.getUserName());
		values.put(Users.GROUP_NAME, bean.getGroupName());
		values.put(Users.LOGIN_TIME, DateUtil.getDateTimeStr());
		SQLiteDatabase db = helper.getWritableDatabase();
		db.insert(Users.TABLE_NAME, null, values);
	}

	@Override
	public void update(Users bean) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 判断用户是否已经存在
	 * @param userName
	 * @param groupName
	 * @return
	 */
	private boolean isNotExistedUser(String userName,String groupName){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(Users.TABLE_NAME, new String[]{"_id"}, "user_name=? and group_name=?", new String[]{userName,groupName}, null, null,null);
		boolean ret = (cursor.getCount()==0);
		cursor.close();
		return ret;
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void login(Users user) {
		Users oUser = getLastUser();
		boolean bOU = false;
		Session.setUser(user);
		if(oUser!=null && user.getUserName().equalsIgnoreCase(oUser.getUserName()) 
				&& user.getGroupName().equalsIgnoreCase(oUser.getGroupName())){
			bOU = true;
		}
		if(!bOU){
			//不是上次登录的用户和病区，保存信息
			PreferenceHelper.updateLastUser(ctx, user);
			if(this.isNotExistedUser(user.getUserName(), user.getGroupName())){
				this.insert(user);
			}
		}
		
		
	}

	@Override
	public Users getLastUser() {
		if(Session.getUser()!=null&&Session.getUser().getUserName()!=null) return Session.getUser();
		Users user = PreferenceHelper.getPreferenceUser(ctx);
		return user;
	}

}
