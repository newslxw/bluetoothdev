package com.alensic.nursing.mobile.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class MyServiceConnection implements ServiceConnection {

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		Log.e("info", "Service Connection Success");
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		Log.e("info", "Service Disconnection Success");
	}

}
