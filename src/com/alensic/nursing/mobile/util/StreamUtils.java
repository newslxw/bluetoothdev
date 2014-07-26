/**
 * 
 */
package com.alensic.nursing.mobile.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * 蓝牙socket.close不是synchronized，在多个线程同时调用close时会抛出下面类似异常：
 * @@@ ABORTING: INVALID HEAP ADDRESS IN dlfree
 * Fatal signal 11 (SIGSEGV) at 0xdeadbaad (code=1)
 * 所以必须加synchronized标识
 * @author xwlian
 * 
 */
public class StreamUtils {

    public synchronized static BluetoothSocket closeSocket(String TAG,BluetoothSocket socket){
			if (socket != null){
				try {
					if(socket.getInputStream()!=null||socket.getOutputStream()!=null){
						socket.close();
						socket = null;
					}
				} catch (IOException e1) {
					Log.e(TAG, "socket close() failed", e1);
				}
			}
			return null;
     }
     
     public synchronized static InputStream closeStream(String TAG,InputStream in){
     	if(in != null){
     		try {
					in.close();
					in = null;
				} catch (IOException e) {
					Log.e(TAG, "inputstream close() failed", e);
				}
     	}
     	return null;
     }
     
     public synchronized static OutputStream closeStream(String TAG,OutputStream out){
     	if(out != null){
     		try {
     			out.close();
     			out = null;
				} catch (IOException e) {
					Log.e(TAG, "outputstream close() failed", e);
				}
     	}
     	return null;
     }
     
     public static void sleepWithNoException(long time){
     	try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				
			}
     }
}
