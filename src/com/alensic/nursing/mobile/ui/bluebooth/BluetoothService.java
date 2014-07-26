package com.alensic.nursing.mobile.ui.bluebooth;

import java.util.UUID;

import com.alensic.nursing.mobile.model.BedTemperature;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 蓝牙操作基类
 * @author xwlian
 *
 */
public class BluetoothService {

    public static final int STATE_NONE = 0;       
    public static final int STATE_LISTEN = 1;    
    public static final int STATE_CONNECTING = 2; 
    public static final int STATE_CONNECTED = 3;  
    public static final int STATE_CANNT_CONNECTED = 4;  
	public static final String DEVICE_NAME = "device_name";
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final String TOAST = "toast";
	
	private int mState;
	private Handler mHandler;
    private ConnectThread mConnectThread;

    private long sleepTime = 5000;
    private long nTimeout = 2*sleepTime; //阻塞超时时间
    private String mmMac;
    private String mDeviceName;//设备名
    private Context ctx;
    private String TAG = "BluetoothService";
    
    
    
    public BluetoothService(Context context, Handler handler) {
        mState = STATE_NONE;
        this.ctx = context;
        mHandler = handler;
    }
    
    
    public synchronized void connect(final String mac,final String deviceName) {
    	new Thread(){
			@Override
			public void run() {
		    	mmMac = mac;
		    	mDeviceName = deviceName;
		    	if(mConnectThread!=null){
		    		mConnectThread.close(true);
		    		Log.e(TAG, "等待就连接结束");
		    		synchronized(mConnectThread){
		    			try {
							mConnectThread.wait();
						} catch (InterruptedException e) {
						}
		        	}
		    		mConnectThread = null;
		    	}
	    		Log.e(TAG, "开始新连接 ");
		        mConnectThread = createConnectThread();
		        setState(STATE_CONNECTING);
		        mConnectThread.start();
			}
    	}.start();
    }
    

    public synchronized void stop() {
        if (mConnectThread != null) {
        	mConnectThread.close(true);
        	mConnectThread = null;
        }
        setState(STATE_NONE);
    }
    
    public ConnectThread createConnectThread(){
    	return new ConnectThread(this,this.getMmMac(),this.getmDeviceName(),this.getSleepTime(),this.getnTimeout());
    }


    public void connectionFailed(boolean bToast) {
        String str = "无法连接";
        setState(STATE_LISTEN,str);
        if(bToast) obtainMessage(BluetoothService.MESSAGE_TOAST, 2, -1, str);
    }

    public void connectionLost(boolean bToast) {
        String str = "连接中断";
        setState(STATE_LISTEN,str);
        if(bToast) obtainMessage(BluetoothService.MESSAGE_TOAST, 2, -1, str);
    }
    
	
	public synchronized void setState(int state) {
        mState = state;
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }
	
	public synchronized void setState(int state,String msg) {
        mState = state;
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1, msg).sendToTarget();
    }
	
    public synchronized int getState() {
        return mState;
    }
    
    public void obtainMessage(int what, int arg1, int arg2, Object obj){
		mHandler.obtainMessage(what, 2, -1, obj).sendToTarget();
	}


	public long getSleepTime() {
		return sleepTime;
	}


	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}


	public long getnTimeout() {
		return nTimeout;
	}


	public void setnTimeout(long nTimeout) {
		this.nTimeout = nTimeout;
	}


	public String getMmMac() {
		return mmMac;
	}


	public void setMmMac(String mmMac) {
		this.mmMac = mmMac;
	}


	public String getmDeviceName() {
		return mDeviceName;
	}


	public void setmDeviceName(String mDeviceName) {
		this.mDeviceName = mDeviceName;
	}


	public Context getCtx() {
		return ctx;
	}


	public void setCtx(Context ctx) {
		this.ctx = ctx;
	}

}
