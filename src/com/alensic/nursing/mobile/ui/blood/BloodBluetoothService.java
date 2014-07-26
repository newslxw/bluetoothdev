/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alensic.nursing.mobile.ui.blood;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.UUID;

import com.alensic.nursing.mobile.model.BedTemperature;
import com.alensic.nursing.mobile.model.Blood;
import com.alensic.nursing.mobile.ui.bluebooth.BluetoothService;
import com.alensic.nursing.mobile.ui.bluebooth.ConnectThread;
import com.alensic.nursing.mobile.util.StreamUtils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 * 血压计接收服务
 * @author xwlian
 *
 */
public class BloodBluetoothService extends BluetoothService {
    
    
    public BloodBluetoothService(Context context, Handler handler) {
    	super(context, handler);
    	this.setSleepTime(5000);
    	this.setnTimeout(10000);
	}

	@Override
	public ConnectThread createConnectThread() {
		return new BloodConnectThread(this,this.getMmMac(),this.getmDeviceName(),this.getSleepTime(),this.getnTimeout());
	}
    
    
    public void connectionFailed(boolean bToast) {
        String str = "无法连接血压计";
        setState(STATE_LISTEN,str);
        if(bToast)obtainMessage(BluetoothService.MESSAGE_TOAST, 2, -1, str);
    }

    public void connectionLost(boolean bToast) {
        String str = "血压计连接中断";
        setState(STATE_LISTEN,str);
        if(bToast)obtainMessage(BluetoothService.MESSAGE_TOAST, 2, -1, str);
    }
    
	
}
