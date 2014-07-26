package com.alensic.nursing.mobile.ui.bluebooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alensic.nursing.mobile.model.BedTemperature;
import com.alensic.nursing.mobile.model.Blood;
import com.alensic.nursing.mobile.util.StreamUtils;

/**
 * 蓝牙操作基类
 * @author xwlian
 *
 */
public class ConnectThread extends Thread {
	
	private String TAG="ConnectThread";
	private BluetoothService toothService;
    private BluetoothAdapter mAdapter;
    private boolean bTimeout=false;
    private TimerThread timerThread;
    private BlockIOThread blockIOThread;
    private int bufferSize=1024;
    private boolean bCloseByService=false; //是否由service发出的关闭命令
    
    private String mmMac;
    private long sleepTime = 5000;
    private long nTimeout = 2*sleepTime; //阻塞超时时间
    private UUID deviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//蓝牙串口的UUID，是android定义的
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private long lastReadTime = 0;  //最后一次读数据时
    private int nTryCount=3; //尝试连接次数
    private String mDeviceName;//设备名称
    private CountDownLatch countDownLatch;
	
	
    

    /**
     * 
     * @param toothService 蓝牙服务对象
     * @param mac          目标蓝牙的mac
     * @param deviceName	目标蓝牙名称
     * @param sleepTime    一次IO后睡眠时间（ms)
     * @param timeout	   读取数据超时时间(ms)，0-表示不用判断超时，一直阻塞到有数据为止; >0 时必须大于sleepTime,超出这个时间就强制关闭socket，并重新连接
     */
    public ConnectThread(BluetoothService toothService,String mac,String deviceName,long sleepTime,long timeout) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mmMac = mac;
        this.TAG = toothService.getClass().getSimpleName()+":"+this;
        this.toothService = toothService;
        this.nTimeout = timeout;
        this.sleepTime = sleepTime;
        this.mDeviceName = deviceName;
        setName("BloodConnectThread");
        
    }
    
    /**
     * 创建socket的读写对象
     * @return
     */
    public BlockIOThread createBlockIOThread(){
    	return new BlockIOThread(this,this.sleepTime,this.mmInStream,this.mmOutStream,bufferSize,countDownLatch);
    }
    
    /**
     * 创建socket的超时监听对象
     * @return
     */
    public TimerThread createTimerThread(){
    	return new TimerThread(this,this.sleepTime,this.nTimeout,countDownLatch);
    }
    
    private void ensureDiscoverable() {  
    	// 如果我们已经发现，阻止它
		if (!mAdapter.isDiscovering())
		{
			mAdapter.startDiscovery();
		}
		// 要求从bluetoothadapter发现
       /* if (mAdapter.getScanMode() !=  
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {  
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);  
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);  
            toothService.getCtx().startActivity(discoverableIntent);  
        } */
    } 
    
    public void run() {
    	Log.i(TAG, "BEGIN mConnectThread");
		this.setbCloseByService(false);//清空标志
    	boolean bClose = false;
    	BluetoothSocket tsocket = null;
    	boolean tbopen = false;
    	int i=1;
    	while(true){
    		//循环直到打开为止
    		tsocket = null;
    		tbopen = false;
    		Log.e(TAG, this+"正在第"+(i++)+"次连接"+mDeviceName);
    		toothService.setState(BluetoothService.STATE_CONNECTING,"正在连接"+mDeviceName);
    		ensureDiscoverable();
	    	if(!this.isbCloseByService())tsocket = this.initSocket();
	    	if(!this.isbCloseByService()&&tsocket!=null)tbopen = this.openConnection();
	    	
	    	if(!this.isbCloseByService()&&!tbopen){
	    		StreamUtils.sleepWithNoException(sleepTime);
	    		continue;
	    	}
	    	//循环直到打开为止
    		if(this.isbCloseByService()){//保证在建立连接时，service调用close能顺利退出循环
    			Log.e(TAG,this+"连接被终止"+mDeviceName);
    			this.setbCloseByService(false);//清空标志
                close(false);
                break;
    		}
    		if(tbopen){
        		toothService.setState(BluetoothService.STATE_CONNECTED);
    	    	if(this.nTimeout>0){
    	            this.countDownLatch = new CountDownLatch(2);
    		    	blockIOThread = createBlockIOThread();
    	    		timerThread = createTimerThread();
    		    	blockIOThread.start();
    	    		timerThread.start();
    	    	}else{
    	            this.countDownLatch = new CountDownLatch(1);
    		    	blockIOThread = createBlockIOThread();
    		    	blockIOThread.start();
    	    	}
    	    	try {
					this.countDownLatch.await();
				} catch (InterruptedException e) {
				}
        	}
    	}
    	synchronized(this){
    		this.notifyAll();
    	}
		/*if(!tbopen&&!this.isbCloseByService()){
    		String msg = nTryCount+"次尝试后依然无法建立蓝牙连接"+mDeviceName;
    		Log.e(TAG,msg);
    		//toothService.obtainMessage(BluetoothService.MESSAGE_TOAST, 2, -1, msg);
    		toothService.connectionFailed(true);
		}*/
        	
    }
    

    
    /**
     * 创建血压计设备连接
     * @return
     */
    protected BluetoothSocket initSocket(){
    	try {
    		mmDevice = mAdapter.getRemoteDevice(mmMac);
    		mmSocket = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
    		
        } catch (IOException e) {
            Log.e(TAG, "create() failed", e);
            close(false);
        }
    	return mmSocket;
    }
    
    /**
     * 打开血压计连接
     * @return
     */
    protected boolean openConnection(){
        if(mAdapter.isDiscovering())mAdapter.cancelDiscovery();
        try {
			mmSocket.connect();
			mmInStream = mmSocket.getInputStream();
			mmOutStream = mmSocket.getOutputStream();
		} catch (IOException e) {
            Log.e(TAG, "openConnection() failed", e);
            close(false);
            return false;
		}
        return true;
    }
    

    /**
     * 
     * @param bCloseByService true是service发出关闭命令，这时连接可能还没建立，需要设置bCloseByService标志;fase-非service发出的关闭命令，这时连接已经建立
     */
    public void close(boolean bCloseByService) {
    	clearTimeoutFlag();
    	if(bCloseByService)this.setbCloseByService(bCloseByService);
    	mmOutStream = StreamUtils.closeStream(TAG,mmOutStream);
    	mmInStream = StreamUtils.closeStream(TAG,mmInStream);
    	mmSocket = StreamUtils.closeSocket(TAG,mmSocket);
        mmDevice = null;
    }

    public void connectionFailed(boolean bToast) {
    	toothService.connectionFailed(bToast);
    }

    public void connectionLost(boolean bToast) {
    	toothService.connectionLost(bToast);
    }
    
    /**
     * 
     * @return 最后一次socket的IO操作时间戳
     */
	public synchronized long getLastReadTime() {
		return lastReadTime;
	}

	/**
     * 设置最后一次socket的IO操作时间戳
     */
	public synchronized void setLastReadTime() {
		this.lastReadTime = System.currentTimeMillis();
	}
	
	/**
	 * 阻塞超时,强制关闭IO，然后重新连接
	 */
	public void  timeout(){
		this.setbTimeout(true);
		Log.e(TAG,"io timeout , close and reconnect socket");
		close(false);
		/*StreamUtils.sleepWithNoException(sleepTime);
		try {
			countDownLatch.await(2*sleepTime,TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
		}*/
	}
	
	/**
	 * 返回消息，采用 Handler.obtainMessage一样的参数格式
	 * @param what
	 * @param arg1
	 * @param arg2
	 * @param obj
	 */
	public void obtainMessage(int what, int arg1, int arg2, Object obj){
		toothService.obtainMessage(what, 2, -1, obj);
	}

	/**
	 * 判断是不是被timerthread强制关闭了socket的io
	 * @return
	 */
	public synchronized boolean isbTimeout() {
		return bTimeout;
	}

	/**
	 * 设置强制关闭标志
	 * @param bTimeout
	 */
	public synchronized void setbTimeout(boolean bTimeout) {
		this.bTimeout = bTimeout;
	}
	
	/**
	 * 清空强制关闭标志
	 */
	public synchronized void clearTimeoutFlag(){
		this.bTimeout = false;
	}

	public synchronized boolean isbCloseByService() {
		return bCloseByService;
	}

	public synchronized void setbCloseByService(boolean bCloseByService) {
		this.bCloseByService = bCloseByService;
	}

	public UUID getDeviceUUID() {
		return deviceUUID;
	}

	public void setDeviceUUID(UUID deviceUUID) {
		this.deviceUUID = deviceUUID;
	}

	public String getMmMac() {
		return mmMac;
	}

	public void setMmMac(String mmMac) {
		this.mmMac = mmMac;
	}

	public BluetoothAdapter getmAdapter() {
		return mAdapter;
	}

	public void setmAdapter(BluetoothAdapter mAdapter) {
		this.mAdapter = mAdapter;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
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

	public BluetoothSocket getMmSocket() {
		return mmSocket;
	}

	public void setMmSocket(BluetoothSocket mmSocket) {
		this.mmSocket = mmSocket;
	}

	public BluetoothDevice getMmDevice() {
		return mmDevice;
	}

	public void setMmDevice(BluetoothDevice mmDevice) {
		this.mmDevice = mmDevice;
	}

	public InputStream getMmInStream() {
		return mmInStream;
	}

	public void setMmInStream(InputStream mmInStream) {
		this.mmInStream = mmInStream;
	}

	public OutputStream getMmOutStream() {
		return mmOutStream;
	}

	public void setMmOutStream(OutputStream mmOutStream) {
		this.mmOutStream = mmOutStream;
	}

	public BluetoothService getToothService() {
		return toothService;
	}

	public TimerThread getTimerThread() {
		return timerThread;
	}

	public BlockIOThread getBlockIOThread() {
		return blockIOThread;
	}

	public void setLastReadTime(long lastReadTime) {
		this.lastReadTime = lastReadTime;
	}

	public int getnTryCount() {
		return nTryCount;
	}

	public void setnTryCount(int nTryCount) {
		this.nTryCount = nTryCount;
	}

	public String getmDeviceName() {
		return mDeviceName;
	}

	public void setmDeviceName(String mDeviceName) {
		this.mDeviceName = mDeviceName;
	}

	public CountDownLatch getCountDownLatch() {
		return countDownLatch;
	}

	public void setCountDownLatch(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}
}