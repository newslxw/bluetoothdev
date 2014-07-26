/**
 * 
 */
package com.alensic.nursing.mobile.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.alensic.nursing.mobile.model.Users;

/**操作preferences的工具类
 * @author xwlian
 *
 */
public final class PreferenceHelper {


	public final static String PREFS_NAME ="NightingaleMobile";  //Preferences名称
	
	/**
	 * 获取Preference
	 * @return
	 */
	public static SharedPreferences getSharedPreferences(Context ctx){
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); 
		return settings;
	}
	
	/**
	 * 记录登录的用户，病区，时间到preference中
	 * @param user
	 */
	public static void updateLastUser(Context ctx,Users user){
		updatePreferenceValues(ctx,
				new String[]{Constants.PreferencesKey.userName,
				Constants.PreferencesKey.groupName,
				Constants.PreferencesKey.lastLoginTime},
				new String[]{
				user.getUserName(),user.getGroupName(),user.getLoginTime()
		});
	}
	
	/**
	 * 获取preference中的当前用户信息
	 * @return
	 */
	public static Users getPreferenceUser(Context ctx){
		Map<String,String> map = getPreferenceValues(ctx,
				new String[]{Constants.PreferencesKey.userName,
				Constants.PreferencesKey.groupName,
				Constants.PreferencesKey.lastLoginTime});
		Users user = null;
		if(map != null && !map.isEmpty()){
			user = new Users();
			user.setGroupName(map.get(Constants.PreferencesKey.groupName));
			user.setUserName(map.get(Constants.PreferencesKey.userName));
			user.setLoginTime(map.get(Constants.PreferencesKey.lastLoginTime));
		}
		return user;
	}
	
	/**
	 * 获取preference中的值
	 * @param keys
	 * @return map
	 */
	public static Map<String,String> getPreferenceValues(Context ctx,String[] keys){
		SharedPreferences settings = getSharedPreferences(ctx);
		Map<String,String> map = new HashMap<String,String>();
		if(settings!=null){
			for(String key:keys){
				map.put(key, settings.getString(key,null));
			}
		}
		return map;
	}
	
	/**
	 * 更新perference中的值
	 * @param keys   需要更新的key数组
	 * @param values 对于的value数组
	 */
	public static void updatePreferenceValues(Context ctx,String[] keys,String[] values){
		SharedPreferences settings = getSharedPreferences(ctx);
		Editor editor = settings.edit();
		for(int i=0; i<keys.length; i++){
			editor.putString(keys[i], values[i]);
		}
		editor.commit();
	}
	
}
