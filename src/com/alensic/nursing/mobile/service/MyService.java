package com.alensic.nursing.mobile.service;

import java.util.List;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

public class MyService extends Service {
	static int sdkVersion = android.os.Build.VERSION.SDK_INT;
	public MyService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.e("info", "Service bind");
		
		
		return null;
	}

	@Override
	public void onCreate() {
		Log.e("info", "Service create");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.e("info", "Service destroy");
		useNewClipBoard("Service destroy");
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("info", "Service start");
		useNewClipBoard("MyService start");
		useEditTextService("MyService start");
		return super.onStartCommand(intent, flags, startId);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void useNewClipBoard(String str ){
		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clipData = ClipData.newPlainText("MyService", str);  //只有3.2以上版本有这个
		clipData.addItem(new Item(str));
		clipboard.setPrimaryClip(clipData);
		
	}
	
	public void useEditTextService(String str){
		ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE); 
		List<RunningTaskInfo> list = mActivityManager.getRunningTasks(500);
		for(int i=0; i<list.size(); i++){
			RunningTaskInfo ti = list.get(i);
			int id = ti.id;
			ComponentName cn = ti.topActivity;
			
		}
	}
	

	@Override
	public void onRebind(Intent intent) {
		Log.e("info", "Service rebind");
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.e("info", "Service unbind");
		return super.onUnbind(intent);
	}




	
}
