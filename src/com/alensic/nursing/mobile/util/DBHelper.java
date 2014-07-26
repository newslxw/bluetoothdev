/**
 * 
 */
package com.alensic.nursing.mobile.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.alensic.nursing.mobile.R;
import com.alensic.nursing.mobile.exception.DBException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQL工具类,这个类不能用单例模式，SQLiteOpenHelper会保证数据库唯一性，无需担心
 * 如果用了单例模式，可能导致activity及context的引用一直存在而无法回收，导致内存泄露
 * 
 * @author xwlian
 * 
 */
public class DBHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "NightingaleMobile.db";
	private static final String VERSION = "1.00";
	private static final int DB_VERSION = (int) (Float.parseFloat(VERSION)*100);

	private Context context;
	private static DBHelper DBHelper;


	private DBHelper(Context ctx) {
		super(ctx, DB_NAME, null, DB_VERSION);
		this.context = ctx;
	}

	public static DBHelper getInstance(Context ctx) {
		/*if (DBHelper == null) {
			DBHelper = new DBHelper(ctx);
		}*/

		return new DBHelper(ctx);
	}
	
	/**
	 * 运行测试数据建立测试环境
	 * @param db
	 */
	public void runTestData() {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();
			applySQLs(db, R.raw.db_test);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "", e);
			throw new DBException(
					"Database runTestData error! Please contact the support or developer.",
					e);
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.beginTransaction();
			// create db 
			applySQLs(db, R.raw.db_create);
			// Initialise
			applySQLs(db, R.raw.db_init);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "", e);
			throw new DBException(
					"Database create error! Please contact the support or developer.",
					e);
		} finally {
			db.endTransaction();
		}
	}

	private void applySQLs(SQLiteDatabase db, int sqlResourceId)
			throws IOException {
		InputStream tmpIS = context.getResources().openRawResource(sqlResourceId);
		InputStreamReader tmpReader = new InputStreamReader(tmpIS);
		BufferedReader tmpBuf = new BufferedReader(tmpReader);

		StringBuffer sql = new StringBuffer();
		String tmpStr = null;
		while ((tmpStr = tmpBuf.readLine()) != null) {
			sql.append(tmpStr);
			sql.append('\n');
			if (tmpStr.trim().endsWith(";")) {
				db.execSQL(sql.toString());
				sql = new StringBuffer();
			}
		}

		tmpBuf.close();
		tmpReader.close();
		tmpIS.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			/***
			 * NO upgrade before 1.0 *** // upgrade db NightingleMobile
			 * applySQLs(db, R.raw.db_upgrade);
			 */

			db.beginTransaction();
			applySQLs(db, R.raw.db_clean);
			onCreate(db);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "", e);
			throw new DBException(
						"Database upgrade error! Please contact the support or developer.",
						e);
		} finally {
			db.endTransaction();
		}
	}

}
