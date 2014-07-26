package com.alensic.nursing.mobile.ui;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alensic.nursing.mobile.common.ConfirmDialogEvent;
import com.alensic.nursing.mobile.common.Session;
import com.alensic.nursing.mobile.dao.BedDao;
import com.alensic.nursing.mobile.dao.BedDaoImpl;
import com.alensic.nursing.mobile.dao.GroupsDao;
import com.alensic.nursing.mobile.dao.GroupsDaoImpl;
import com.alensic.nursing.mobile.dao.HistoryDao;
import com.alensic.nursing.mobile.dao.HistoryDaoImpl;
import com.alensic.nursing.mobile.dao.TemperatureDao;
import com.alensic.nursing.mobile.dao.TemperatureDaoImpl;
import com.alensic.nursing.mobile.exception.NursingIOException;
import com.alensic.nursing.mobile.model.BedTemperature;
import com.alensic.nursing.mobile.model.UploadHistory;
import com.alensic.nursing.mobile.ui.eardevice.EarBluetoothService;
import com.alensic.nursing.mobile.ui.filedevice.FileBluetoothService;
import com.alensic.nursing.mobile.util.ActivityUtils;
import com.alensic.nursing.mobile.util.Constants;
import com.alensic.nursing.mobile.util.DateUtil;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class UploadActivity extends HistoryDetailActivity  {

	protected TemperatureDao temperatureDao = new TemperatureDaoImpl(this);
	protected HistoryDao historyDao = new HistoryDaoImpl(this);
	
	protected FileBluetoothService fileService = new FileBluetoothService(this);
	protected static String fileDeviceMac ;
	protected String fileDeviceName;
	protected static final int REQUEST_FILE_DEVICE = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		activityNo = ACTIVITY_UPLOAD;
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onNextBtnClick(View v) {
		List<BedTemperature> listTemperature = temperatureDao.queryForUpload();
		if(listTemperature == null || listTemperature.isEmpty()){
			Toast toast = Toast.makeText(getApplicationContext(),
				     "没有数据需要上传!", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		
		// 上传数据
		final UploadConfirmDialogEvent evtHandler = new UploadConfirmDialogEvent("请选择上传方式",null);
		AlertDialog.Builder builder = ActivityUtils.confirmDialog(this,evtHandler);
		builder.setSingleChoiceItems(new String[]{"蓝牙","WIFI"}, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				evtHandler.selOpt = which;
			}
		});
		ActivityUtils.show(builder);
		
	}
	
	protected class UploadConfirmDialogEvent extends ConfirmDialogEvent{

		public int selOpt=1;
		public UploadConfirmDialogEvent(String title, String message) {
			super(title, message);
		}
		
		@Override
		public void onOK(Context ctx,DialogInterface dialog, int whichButton) {
			if(selOpt == 0){
				//WIFI
				
			}else{
				//蓝牙
				createFileAndUpload();
			}
		}
	}
	
	/**
	 * 创建上传的文件并上传服务器
	 */
	public void createFileAndUpload(){

		List<BedTemperature> listTemperature = temperatureDao.queryForUpload();
		if(listTemperature == null || listTemperature.isEmpty()){
			Toast toast = Toast.makeText(getApplicationContext(),
				     "没有数据需要上传!", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		FileOutputStream outStream = null;
		try {
			String uploadTime = DateUtil.getDateTimeStr();
			String uploadName = DateUtil.formatDBdate(uploadTime)+" 上传的数据";
			String uniqueTag = Session.getMac()+"_"+uploadTime;
			outStream = this.openFileOutput(Constants.uploadFileName, Context.MODE_WORLD_READABLE);
			StringBuffer sb = new StringBuffer(uniqueTag+"\r\n");//文件的唯一标识
			for(int i=0; i<listTemperature.size();i++){
				BedTemperature bt = listTemperature.get(i);
				sb.append(bt.getBedNo()).append("|").append(bt.getInDate()).append("|").append(bt.getDataType()).append("|").append(bt.getTemperature()).append("\r\n");
			}
			outStream.write(sb.toString().getBytes()); 
			outStream.close();
			outStream=null;
			UploadHistory history = new UploadHistory();
			history.setBluetoothName(this.fileDeviceName);
			history.setUploadName(uploadName);
			history.setGroupName(Session.getUser().getGroupName());
			history.setUploadTime(uploadTime);
			history.setUploadWay(Constants.UploadWay.BLUETOOTH);
			history.setUserName(Session.getUser().getUserName());
			history.setUniqueTag(uniqueTag);
			if(fileService.uploadTemperature(history)){
				historyDao.uploadFinish(history);
				initBedItem(uploadId);
			}
			
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "", e);
			throw new NursingIOException("系统错误，无法生成待上传的数据",e);
		} finally{  
			if(outStream!=null)
				try {
					outStream.close();
				} catch (IOException e) {
					Log.e(this.getClass().getName(), "", e);
					throw new NursingIOException("系统错误，无法生成待上传的数据",e);
				}     
    	} 
		
	
	}



	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_FILE_DEVICE:
			if (resultCode == Activity.RESULT_OK) {
				fileDeviceMac = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				fileDeviceName = data.getExtras().getString(EarBluetoothService.DEVICE_NAME);
				createFileAndUpload();
			}else{
				Toast toast = Toast.makeText(getApplicationContext(),
					     "上传取消:没有选择接收机器", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			break;
		}
	}

}
