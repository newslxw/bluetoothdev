/**
 * 
 */
package com.alensic.nursing.mobile.ui.bluebooth;

import java.util.concurrent.CountDownLatch;

import com.alensic.nursing.mobile.util.StreamUtils;

/**
 * 判断蓝牙socket的io是否超时的类，由ConnectThread调用
 * @author xwlian
 *
 */
public class TimerThread extends Thread{

	private ConnectThread ioThread;
	private long sleepTime;
	private long timeout;
	private CountDownLatch countDownLatch;
	
	/**
	 * 
	 * @param ct    连接管理线程
	 * @param sleepTime	多久运行一次
	 * @param timeout	blockio超时时间
	 * @param countDownLatch	线程计数器
	 */
	public TimerThread(ConnectThread ct,long sleepTime,long timeout,CountDownLatch countDownLatch){
		this.ioThread = ct;
		this.sleepTime = sleepTime;
		this.timeout = timeout;
		this.countDownLatch = countDownLatch;
	}
	
	@Override
	public void run() {
		try{
			do{
				StreamUtils.sleepWithNoException(sleepTime);
				long cur = System.currentTimeMillis();
				long lastTime = ioThread.getLastReadTime();
				if((cur - lastTime)>timeout) {
					ioThread.timeout();
					break;
				}
			}while(true);
		}finally{
			countDownLatch.countDown();
		}
	}
	
	

	
}
