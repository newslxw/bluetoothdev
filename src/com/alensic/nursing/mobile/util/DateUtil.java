package com.alensic.nursing.mobile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtil {

	public static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";
	/**
	 * 默认的时间格式
	 */
	public static final String DEFAULT_DATE_TIME_FORMAT = "yyyyMMddHHmmss";
	
	/**
	 * 获取当前时间的14位字符串
	 * @return
	 */
	public static String getDateTimeStr(){
		SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);    
        return format.format(new Date()); 
	}
	
	public static String getDateTimeStr(Date date){
		SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);    
        return format.format(date); 
	}
	
	/**
	 * 把14位字符串解析成Date类型
	 * @param strDate
	 * @return
	 * @throws ParseException
	 */
	public static Date toDateTime(String strDate) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
		Date d = null;
		d =format.parse(strDate);
		return d;
	}
    
	/**
	 * 把数据库存储的14位字符串格式的时间转换成以下格式的字符串 yyyy-MM-dd HH:mm:ss
	 * @param d
	 * @return
	 */
	public static String formatDBdate(String d){
		if(d==null) return d;
		if(d.length() != 8 && d.length() !=14) return d;
		String ret = d.substring(0,4)+"-" + d.substring(4,6)+"-"+d.substring(6,8);
		if(d.length()==14) ret=ret+" "+d.substring(8,10)+":"+d.substring(10,12)+":"+d.substring(12,14);
		return ret;
	}
	
	/**
	 * 判断inDate和当前测量的温度间隔是否在60分钟以内
	 * @param tDate  当前温度的采集时间
	 * @param inDate 病床数据库中的最新采集时间
	 * @return 
	 * @throws ParseException
	 */
	public static boolean isRepeatTemperature(String tDate,String inDate) throws ParseException{
		Date d = toDateTime(inDate);
		Date cur =toDateTime(tDate);
		long ld = d.getTime();
		long lcur = cur.getTime();
		return ((lcur - ld) <= 1000*60*60);
	}
}
