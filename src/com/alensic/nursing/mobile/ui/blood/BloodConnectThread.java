package com.alensic.nursing.mobile.ui.blood;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.alensic.nursing.mobile.ui.bluebooth.BlockIOThread;
import com.alensic.nursing.mobile.ui.bluebooth.BluetoothService;
import com.alensic.nursing.mobile.ui.bluebooth.ConnectThread;
import com.alensic.nursing.mobile.util.StreamUtils;

public class BloodConnectThread extends ConnectThread {
	private UUID UUID1= UUID.fromString("00001102-0000-1000-8000-00805F9B34FB");
	private UUID UUID2= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private String TAG="BloodConnectThread";

	public BloodConnectThread(BluetoothService toothService, String mac,String deviceName,
			long sleepTime, long timeout) {

		super(toothService, mac, deviceName,sleepTime, timeout);

	    this.setDeviceUUID(UUID2);//蓝牙串口的UUID，是android定义的
	}

	@Override
	public BlockIOThread createBlockIOThread() {
		return new BloodBlockIOThread(this,this.getSleepTime(),this.getMmInStream(),this.getMmOutStream(),this.getBufferSize(),this.getCountDownLatch());
	}
	
    /**
     * 打开血压计连接
     * @return
     */
    protected boolean openConnection(){
        if(this.getmAdapter().isDiscovering())this.getmAdapter().cancelDiscovery();
		StreamUtils.sleepWithNoException(50);//必须加上这个sleep，否则无法发送数据成功，也无法接收数据，比较诡异，是在测试时加断点无意中发现的
        try {
			this.getMmSocket().connect();
			this.setMmInStream(this.getMmSocket().getInputStream());
			this.setMmOutStream(this.getMmSocket().getOutputStream());
		} catch (IOException e) {
            Log.e(TAG, "openConnection() failed", e);
            close(false);
            return false;
		}
        return true;
    }
    
	
	  /**
     * 创建血压计设备连接
     * @return
     */
/*    protected BluetoothSocket initSocket(){
    	try {
    		this.setMmDevice(this.getmAdapter().getRemoteDevice(this.getMmMac()));
    		this.setMmSocket(this.getMmDevice().createRfcommSocketToServiceRecord(this.getDeviceUUID()));
        } catch (IOException e) {
            Log.e(TAG, "create() failed", e);
            close(false);
        }
    	return this.getMmSocket();
    }*/
	

}
