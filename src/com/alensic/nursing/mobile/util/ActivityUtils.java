/**
 * 
 */
package com.alensic.nursing.mobile.util;

import java.lang.reflect.Field;

import com.alensic.nursing.mobile.R;
import com.alensic.nursing.mobile.common.ConfirmDialogEvent;
import com.alensic.nursing.mobile.exception.BaseException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

/**
 * @author xwlian
 *
 */
public final class ActivityUtils {

	/**
	 * 错误处理
	 * @param ctx
	 * @param e
	 */
	public static void gotoErrorPage(Context ctx, BaseException e){
		if(ctx != null){
			/*Intent intent = new Intent(ctx, ErrorActivity.class);
			intent.putExtra("error", e);
			ctx.startActivity(intent);
			ctx.finish();*/
		}
	}
	
	public static AlertDialog.Builder confirmDialog(final Context ctx, final ConfirmDialogEvent evtHandler){
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ctx,R.style.AlertDialogCustom));
		if(evtHandler != null){
			builder.setTitle(evtHandler.title);
			builder.setMessage(evtHandler.message);
			builder.setNegativeButton(evtHandler.labelOK, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					setDialogShowing(dialog,true);
					evtHandler.onOK(ctx,dialog,whichButton);
				}
			});
			builder.setPositiveButton(evtHandler.labelCancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					setDialogShowing(dialog,true);
					evtHandler.onCancel(ctx,dialog,whichButton);
				}
			});
		}
		
		//builder.setCancelable(false);
		return builder;
	}
	
	/**
	 * 设置对话框按钮字体大小
	 * @param builder
	 * @return
	 */
	public static AlertDialog show( AlertDialog.Builder builder){
		AlertDialog dialog = builder.show();
		Context ctx = dialog.getContext();
		Resources r = ctx.getResources(); 
		// 通过getDimension方法获得尺寸值 
		float btn_s = r.getDimension(R.dimen.text_size_dialog_btn);
		
		Button btn = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		if(btn != null)	btn.setTextSize(btn_s);
		btn = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
		if(btn != null)	btn.setTextSize(btn_s);
		btn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		if(btn != null)	btn.setTextSize(btn_s);
		return dialog;
	}
	
	/**
	 * 修改confirmDialog的mShowing属性，改成false，按按钮时就不会关闭对话框
	 * @param dialog
	 * @param mShowing
	 */
	public static void setDialogShowing(DialogInterface dialog,boolean mShowing){

	    Field field;
		try {
			field = dialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
		    field.setAccessible( true );
		     //   将mShowing变量设为false，表示对话框已关闭 
		    field.set(dialog, mShowing );
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * 获取本机的MAC
	 * @param ctx
	 * @return
	 */
	public static String getLocalMacAddress(Context ctx) {
		WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}
	
}
