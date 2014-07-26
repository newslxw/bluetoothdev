package com.alensic.nursing.mobile.ui.eardevice;

import com.alensic.nursing.mobile.ui.blood.BloodBlockIOThread;
import com.alensic.nursing.mobile.ui.bluebooth.BlockIOThread;
import com.alensic.nursing.mobile.ui.bluebooth.BluetoothService;
import com.alensic.nursing.mobile.ui.bluebooth.ConnectThread;

public class EarConnectThread extends ConnectThread {

	public EarConnectThread(BluetoothService toothService, String mac,String deviceName,
			long sleepTime, long timeout) {
		super(toothService, mac, deviceName, sleepTime, timeout);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public BlockIOThread createBlockIOThread() {
		return new EarBlockIOThread(this,this.getSleepTime(),this.getMmInStream(),this.getMmOutStream(),this.getBufferSize(),this.getCountDownLatch());
	}

}
