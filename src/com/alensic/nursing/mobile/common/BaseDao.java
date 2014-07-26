package com.alensic.nursing.mobile.common;

import java.util.HashMap;
import java.util.Map;

import com.alensic.nursing.mobile.model.Users;
import com.alensic.nursing.mobile.util.Constants;
import com.alensic.nursing.mobile.util.DBHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 所有dao都必须继承
 * @author xwlian
 *
 */
public abstract class BaseDao {

	
	protected DBHelper helper;
	protected Context ctx;
	public BaseDao(Context ctx){
		this.ctx = ctx;
		helper = DBHelper.getInstance(ctx);
	}
	
}
