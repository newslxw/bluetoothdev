package com.alensic.nursing.mobile.ui.filedevice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.alensic.nursing.mobile.common.ConfirmDialogEvent;
import com.alensic.nursing.mobile.common.Session;
import com.alensic.nursing.mobile.common.SpinnerItem;
import com.alensic.nursing.mobile.dao.HistoryDao;
import com.alensic.nursing.mobile.dao.HistoryDaoImpl;
import com.alensic.nursing.mobile.dao.TemperatureDao;
import com.alensic.nursing.mobile.dao.TemperatureDaoImpl;
import com.alensic.nursing.mobile.exception.NursingIOException;
import com.alensic.nursing.mobile.model.BedTemperature;
import com.alensic.nursing.mobile.model.UploadHistory;
import com.alensic.nursing.mobile.ui.TemperatureActivity;
import com.alensic.nursing.mobile.util.ActivityUtils;
import com.alensic.nursing.mobile.util.Constants;
import com.alensic.nursing.mobile.util.DateUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;


public class FileBluetoothService {


	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mDevice;
	// UUID可以看做一个端口号00001101-0000-1000-8000-00805F9B34FB是传输数据，00001106-0000-1000-8000-00805F9B34FB是传输文件
	public static final UUID FILE_UUID = UUID.fromString("00001106-0000-1000-8000-00805F9B34FB");
    private Activity ctx ;
    

    

	public FileBluetoothService(Activity ctx) {
		this.ctx = ctx;
	}

	
		
	


	/**
	 * 上传数据
	 * @return
	 */
	public boolean uploadTemperature(UploadHistory history){
		BluetoothSocket socket = null;
		boolean bOK = true;
		try {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if(mBluetoothAdapter.isDiscovering()){
				mBluetoothAdapter.cancelDiscovery();
			}
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setType("*/*");
			intent.setClassName("com.android.bluetooth"  
		               , "com.android.bluetooth.opp.BluetoothOppLauncherActivity");
			File f = ctx.getFileStreamPath(Constants.uploadFileName);
			intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f) );;
			ctx.startActivity(intent);
		}finally{
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e) {
					Log.e(this.getClass().getName(), "", e);
				}
			}
		}
		
		
		
		return bOK;
	}
	




	public BluetoothAdapter getmBluetoothAdapter() {
		return mBluetoothAdapter;
	}



	public void setmBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
		this.mBluetoothAdapter = mBluetoothAdapter;
	}
	
	/**
	 * 取得BluetoothSocket
	 */
	public BluetoothSocket initSocket(BluetoothDevice device) {
		BluetoothSocket temp = null;
		try {
			temp = device.createRfcommSocketToServiceRecord(FILE_UUID);

			//以上取得socket方法不能正常使用， 用以下方法代替
			/*Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
	        temp = (BluetoothSocket) m.invoke(device, 1);*/
	        //怪异错误： 直接赋值给socket,对socket操作可能出现异常，  要通过中间变量temp赋值给socket
		} catch (SecurityException e) {
			Log.e("initsocket", e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("initsocket", e.getMessage());
			//e.printStackTrace();
		}
		return temp;
	}

}
