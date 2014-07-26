package com.alensic.nursing.mobile.ui.eardevice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

import android.util.Log;

import com.alensic.nursing.mobile.ui.bluebooth.BlockIOThread;
import com.alensic.nursing.mobile.ui.bluebooth.BluetoothService;
import com.alensic.nursing.mobile.ui.bluebooth.CodeFormat;
import com.alensic.nursing.mobile.ui.bluebooth.ConnectThread;

public class EarBlockIOThread extends BlockIOThread {

	private final static int MAX_INTERV = 100;//两次读取的最大时间差，时间差内视为一组数据
	public EarBlockIOThread(ConnectThread ct, long sleepTime, InputStream in,
			OutputStream out, int bufferSize,CountDownLatch countDownLatch) {
		super(ct, sleepTime, in, out, bufferSize,countDownLatch);
	}
	
	/**
     * 查询血压计最新数据
     * @param buffer 存放血压计数据
     * @return 读取的字节
     * @throws IOException 
     */
	@Override
	protected int receiveDeviceData(byte[] buffer) throws IOException{
		int byteCount = 0;
        byte[] temperature=new byte[2];
        long[] temperatureT=new long[2]; //两次接收的时间
        byte [] tByte = new byte[1];
        while (true) {
            this.getMmInStream().read(tByte);
			temperature[byteCount] = tByte[0];
			temperatureT[byteCount] = System.currentTimeMillis();
			byteCount++;
            if(byteCount==2){//凑够两个字节再发送
            	long interv = temperatureT[1]-temperatureT[0];
        		String v = CodeFormat.bytesToHexStringTwo(temperature, 2);
        		Log.e("EarBlockIOThread","receive data="+v);
            	if(interv>MAX_INTERV){
            		//超时，这个数据不是同一次的数据
            		byteCount=0;
            		Log.e("EarBlockIOThread", "接收超时，后面一次接收的数据和前面一次的不是在同一操作下产生的温度，丢掉");
            		Log.e("EarBlockIOThread",temperatureT[1]+"-"+temperatureT[0]+"="+interv);
            		continue;
            	}
            	buffer[0] = temperature[0];
            	buffer[1] = temperature[1];
            	break;
            }
        }	
    	return 2;
    }
    
    /**
     * 处理血压计返回的数据
     * @param buffer
     * @param bytes
     */
	@Override
	protected void handleData(byte[] buffer,int bytes){
		String v = CodeFormat.bytesToHexStringTwo(buffer, 2);
		int nV = Integer.parseInt(v, 16);
		BigDecimal bd = new BigDecimal(nV);
		bd = bd.divide(new BigDecimal(1000),1,BigDecimal.ROUND_HALF_UP);
		this.getConnectThread().obtainMessage(BluetoothService.MESSAGE_READ, 2, -1, bd.toString());
    }


}
