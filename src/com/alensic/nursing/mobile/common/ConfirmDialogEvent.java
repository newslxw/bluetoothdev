package com.alensic.nursing.mobile.common;

import android.content.Context;
import android.content.DialogInterface;

/**
 * 确认对话框事件处理
 * @author xwlian
 *
 */
public class ConfirmDialogEvent {
	public String title = "";
	public String message = "确定吗?";
	public String labelOK = "确定";
	public String labelCancel = "取消";
	
	public ConfirmDialogEvent(String title,String message){
		this.title = title;
		this.message = message;
	}
	
	/**
	 * 
	 * @param ctx 创建对话框时的Context
	 * @param whichButton 
	 * @param dialog 
	 */
	public void onOK(final Context ctx, DialogInterface dialog, int whichButton){
		
	}
	

	/**
	 * 
	 * @param ctx 创建对话框时的Context
	 * @param whichButton 
	 * @param dialog 
	 */
	public void onCancel(final Context ctx, DialogInterface dialog, int whichButton){
		
	}
	
}
