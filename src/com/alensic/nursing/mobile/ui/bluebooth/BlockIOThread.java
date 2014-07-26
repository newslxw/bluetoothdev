package com.alensic.nursing.mobile.ui.bluebooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

import android.util.Log;

import com.alensic.nursing.mobile.model.Blood;
import com.alensic.nursing.mobile.util.StreamUtils;

/**
 * 蓝牙socket的IO操作基类，由ConnectThread调用
 * @author xwlian
 *
 */
public class BlockIOThread extends Thread{
	
	private ConnectThread connectThread;
	private long sleepTime;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private String TAG = "BlockIOThread";
    private int bufferSize = 1024;
	private CountDownLatch countDownLatch;
	
	
	/**
	 * 
	 * @param ct   连接管理线程
	 * @param sleepTime	多久读一次数据
	 * @param in	inputstream
	 * @param out	outstream
	 * @param bufferSize	一次读多少个字节
	 * @param countDownLatch	线程计数器
	 */
	public BlockIOThread(ConnectThread ct,long sleepTime,InputStream in,OutputStream out,int bufferSize,CountDownLatch countDownLatch){
		this.connectThread = ct;
		this.sleepTime = sleepTime;
		this.mmInStream = in;
		this.mmOutStream = out;
		this.bufferSize = bufferSize;
		this.countDownLatch = countDownLatch;
		
	}
	

	@Override
	public void run() {
		byte[] buffer = new byte[bufferSize];
    	int bytes = 0;
    	try{
	        while(true){
	        	connectThread.setLastReadTime(); //设置最后一次操作时间
				bytes = this.receiveDeviceData(buffer);
	        	handleData(buffer,bytes);
	        	StreamUtils.sleepWithNoException(sleepTime);
	        }
    	}catch (Exception e) {
    		if(!connectThread.isbTimeout()){
    			Log.e(TAG, "bluetoothsocket io exception",e);
    		}
		}finally{
			if(!connectThread.isbTimeout()){//不是被timerThread线程关闭了io导致退出，需要调用关闭连接函数
				connectThread.close(false);
				connectThread.connectionLost(true);
			}
			countDownLatch.countDown();
		}
	}

    /**
     * 查询血压计最新数据
     * @param buffer 存放血压计数据
     * @return 读取的字节
     * @throws IOException 
     */
    protected int receiveDeviceData(byte[] buffer) throws IOException{
    	int bytes;
        bytes = mmInStream.read(buffer);
    	return bytes;
    }
    
    /**
     * 处理血压计返回的数据
     * @param buffer
     * @param bytes
     */
    protected void handleData(byte[] buffer,int bytes){
		String v = CodeFormat.bytesToHexStringTwo(buffer, bytes);
		connectThread.obtainMessage(BluetoothService.MESSAGE_READ, 2, -1, v);
    }


	public ConnectThread getConnectThread() {
		return connectThread;
	}


	public void setConnectThread(ConnectThread connectThread) {
		this.connectThread = connectThread;
	}


	public long getSleepTime() {
		return sleepTime;
	}


	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
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


	public int getBufferSize() {
		return bufferSize;
	}


	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}



}
