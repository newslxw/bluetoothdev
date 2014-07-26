/**
 * 
 */
package com.alensic.nursing.mobile.common;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.alensic.nursing.mobile.model.BedTemperature;
import com.alensic.nursing.mobile.model.Users;
import com.alensic.nursing.mobile.util.Constants;
import com.alensic.nursing.mobile.util.PreferenceHelper;

/**保存用户的全局数据
 * @author xwlian
 *
 */
public final class Session {

	private static Users user;
	private static boolean meterOrderByTemperature; //是否按测量排序
	private static boolean autoSave; //是否自动保存
	private static boolean soundOnSaveSucceed; //是否测量成功,声音提示
	private static boolean soundOnSaveFail; //是否测量失败,声音提示
	private static boolean soundOnUploadSucceed; //是否上传成功,声音提醒
	private static boolean soundOnUploadFail; //是否上传失败,声音提醒
	private static boolean uploadByWIFI; //是否通过WIFI上传
	private static boolean uploadByBluetooth; //是否通过蓝牙上传
	private static String WIFIName; //WIFI服务器地址
	private static String bluetoothName; //蓝牙设备名称
	private static String mac; //手机MAC
	private static Map<String,String> lastEarDeviceInfo; 
	private static Map<String,String> lastBloodDeviceInfo; 
	private static String[] lastEarDeviceKey= new String[]{Constants.PreferencesKey.LastEarDeviceMac,Constants.PreferencesKey.LastEarDeviceName};
	private static String[] lastBloodDeviceKey= new String[]{Constants.PreferencesKey.LastBloodDeviceMac,Constants.PreferencesKey.LastBloodDeviceName};

	public static Users getUser() {
		return user;
	}

	public static void setUser(Users user) {
		Session.user = user;
	}

	public static boolean isMeterOrderByTemperature() {
		return meterOrderByTemperature;
	}

	public static void setMeterOrderByTemperature(boolean meterOrderByTemperature) {
		Session.meterOrderByTemperature = meterOrderByTemperature;
	}
	
	public static void exit(Context ctx){
	}
	
	/**
	 * 获取上传选择的耳温计
	 * @param ctx
	 * @return
	 */
	public static Map<String,String> getLastDevice(Context ctx,String dataType){
		if(dataType.equals(BedTemperature.TYPE_BLOOD)){
			if(lastBloodDeviceInfo != null && lastBloodDeviceInfo.get(Constants.PreferencesKey.LastBloodDeviceMac)!=null)return lastBloodDeviceInfo;
			Map<String,String> lastDeviceMap = PreferenceHelper.getPreferenceValues(ctx, new String[]{Constants.PreferencesKey.LastBloodDeviceMac,Constants.PreferencesKey.LastBloodDeviceName});
			if(lastDeviceMap == null || lastDeviceMap.get(Constants.PreferencesKey.LastBloodDeviceMac)==null)return null;
			return lastDeviceMap;
		}else{
			if(lastEarDeviceInfo != null && lastEarDeviceInfo.get(Constants.PreferencesKey.LastEarDeviceMac)!=null)return lastEarDeviceInfo;
			Map<String,String> lastDeviceMap = PreferenceHelper.getPreferenceValues(ctx, new String[]{Constants.PreferencesKey.LastEarDeviceMac,Constants.PreferencesKey.LastEarDeviceName});
			if(lastDeviceMap == null || lastDeviceMap.get(Constants.PreferencesKey.LastEarDeviceMac)==null)return null;
			return lastDeviceMap;
		}
		
		
	}
	
	
	
	/**
	 * 更新耳温计信息
	 * @param ctx
	 * @param deviceMac
	 * @param deviceName
	 */
	public static void updateLastDevice(Context ctx,String deviceMac,String deviceName,String dataType){
		if(dataType.equals(BedTemperature.TYPE_BLOOD)){
			if(lastBloodDeviceInfo == null)lastBloodDeviceInfo=new HashMap<String,String>();
			lastBloodDeviceInfo.put(Constants.PreferencesKey.LastBloodDeviceMac, deviceMac);
			lastBloodDeviceInfo.put(Constants.PreferencesKey.LastBloodDeviceName, deviceName);
			PreferenceHelper.updatePreferenceValues(ctx, lastBloodDeviceKey, new String[]{deviceMac,deviceName});
		}else{
			if(lastEarDeviceInfo == null)lastEarDeviceInfo=new HashMap<String,String>();
			lastEarDeviceInfo.put(Constants.PreferencesKey.LastEarDeviceMac, deviceMac);
			lastEarDeviceInfo.put(Constants.PreferencesKey.LastEarDeviceName, deviceName);
			PreferenceHelper.updatePreferenceValues(ctx, lastEarDeviceKey, new String[]{deviceMac,deviceName});
		}
	}
	
	

	public static boolean isAutoSave() {
		return autoSave;
	}

	public static void setAutoSave(boolean autoSave) {
		Session.autoSave = autoSave;
	}

	public static boolean isSoundOnSaveSucceed() {
		return soundOnSaveSucceed;
	}

	public static void setSoundOnSaveSucceed(boolean soundOnSaveSucceed) {
		Session.soundOnSaveSucceed = soundOnSaveSucceed;
	}

	public static boolean isSoundOnSaveFail() {
		return soundOnSaveFail;
	}

	public static void setSoundOnSaveFail(boolean soundOnSaveFail) {
		Session.soundOnSaveFail = soundOnSaveFail;
	}

	public static boolean isSoundOnUploadSucceed() {
		return soundOnUploadSucceed;
	}

	public static void setSoundOnUploadSucceed(boolean soundOnUploadSucceed) {
		Session.soundOnUploadSucceed = soundOnUploadSucceed;
	}

	public static boolean isSoundOnUploadFail() {
		return soundOnUploadFail;
	}

	public static void setSoundOnUploadFail(boolean soundOnUploadFail) {
		Session.soundOnUploadFail = soundOnUploadFail;
	}

	public static boolean isUploadByWIFI() {
		return uploadByWIFI;
	}

	public static void setUploadByWIFI(boolean uploadByWIFI) {
		Session.uploadByWIFI = uploadByWIFI;
	}

	public static boolean isUploadByBluetooth() {
		return uploadByBluetooth;
	}

	public static void setUploadByBluetooth(boolean uploadByBluetooth) {
		Session.uploadByBluetooth = uploadByBluetooth;
	}

	public static String getWIFIName() {
		return WIFIName;
	}

	public static void setWIFIName(String wIFIName) {
		WIFIName = wIFIName;
	}

	public static String getBluetoothName() {
		return bluetoothName;
	}

	public static void setBluetoothName(String bluetoothName) {
		Session.bluetoothName = bluetoothName;
	}

	public static String getMac() {
		return mac;
	}

	public static void setMac(String mac) {
		Session.mac = mac;
	}
	
	
	
}
