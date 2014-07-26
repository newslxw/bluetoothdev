package com.alensic.nursing.mobile.util;

public final class Constants {

	
	public final static Integer enable=1;
	public final static Integer disable=2;
	public final static String uploadFileName="temperatureForUpload.txt"; //生成的待上传的文件名称
	
	
	//参数表的参数类型定义
	public static class ParamType{
		public final static String Sys = "sys";//系统
	}
	
	public static class UploadWay{
		public final static String WIFI="wifi";
		public final static String BLUETOOTH="bluetooth";
	}
	
	public static class PreferencesKey{
		public final static String userName="last_user_name";//上传登录的用户名
		public final static String groupName="last_group_name";//上传病区名
		public final static String MeterOrder="order_by_temperature";//是否按测量值排序
		public final static String groupId="last_group_id";//测量体温时最后一次选择的分组
		public static final String lastLoginTime = "last_login_time";//最后一次登录时间，格式yyyyMMddHHmmss
		public static final String LastEarDeviceMac = "last_ear_device_mac";//上传选择的耳温计MAC
		public static final String LastEarDeviceName = "last_ear_device_name";//上传选择的耳温计name

		public static final String LastBloodDeviceMac = "last_blood_device_mac";//上传选择的耳温计MAC
		public static final String LastBloodDeviceName = "last_blood_device_name";//上传选择的耳温计name
		

	}
}
