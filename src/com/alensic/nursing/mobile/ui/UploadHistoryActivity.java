package com.alensic.nursing.mobile.ui;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.alensic.nursing.mobile.LoginActivity;
import com.alensic.nursing.mobile.R;
import com.alensic.nursing.mobile.R.id;
import com.alensic.nursing.mobile.R.layout;
import com.alensic.nursing.mobile.R.menu;
import com.alensic.nursing.mobile.common.BaseMainActivity;
import com.alensic.nursing.mobile.common.BaseUploadActivity;
import com.alensic.nursing.mobile.common.ConfirmDialogEvent;
import com.alensic.nursing.mobile.common.Session;
import com.alensic.nursing.mobile.common.SpinnerItem;
import com.alensic.nursing.mobile.dao.HistoryDao;
import com.alensic.nursing.mobile.dao.HistoryDaoImpl;
import com.alensic.nursing.mobile.dao.TemperatureDao;
import com.alensic.nursing.mobile.dao.TemperatureDaoImpl;
import com.alensic.nursing.mobile.exception.NursingIOException;
import com.alensic.nursing.mobile.model.Bed;
import com.alensic.nursing.mobile.model.BedTemperature;
import com.alensic.nursing.mobile.model.UploadHistory;
import com.alensic.nursing.mobile.ui.eardevice.EarBluetoothService;
import com.alensic.nursing.mobile.ui.filedevice.FileBluetoothService;
import com.alensic.nursing.mobile.ui.titlebar.TitleBarView;
import com.alensic.nursing.mobile.util.ActivityUtils;
import com.alensic.nursing.mobile.util.Constants;
import com.alensic.nursing.mobile.util.DateUtil;
import com.alensic.nursing.mobile.util.StringUtils;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 上传历史
 * @author xwlian
 *
 */
public class UploadHistoryActivity extends BaseUploadActivity {

	
	private LvHistoryAdapter listItemAdapter;
	private ListView lvHistory; 
	private HistoryDao historyDao = new HistoryDaoImpl(this);
	private static UploadHistory seledHistory;
	private TemperatureDao temperatureDao = new TemperatureDaoImpl(this);
	private FileBluetoothService fileService = new FileBluetoothService(this);



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_upload_history);
		lvHistory = (ListView)this.findViewById(R.id.lvHistory);
		
		initTitleBar(this,ACTIVITY_HISTORY,R.string.ttl_back,R.string.ttr_upload,R.string.tt_upload_history);
		initNav(this,ACTIVITY_HISTORY);
		initListItem();
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.upload, menu);
		return true;
	}



	@Override
	public void onNextBtnClick(View v) {
		Builder builder = ActivityUtils.confirmDialog(this, new ConfirmDialogEvent("确认要清空所有已上传的体温数据", null){
			@Override
			public void onOK(Context ctx, DialogInterface dialog,
					int whichButton) {
				historyDao.clear();
				initListItem();
			}
			
		});
		ActivityUtils.show(builder);
		
	}

	
	/**
	 * 初始化病床列表
	 */
	private void initListItem(){
		List<UploadHistory> list=null;
		list = historyDao.queryForList(null);
		boolean bc = true; //判断结果集中有没有选中的病床，没有就清空选中病床
		listItemAdapter = new LvHistoryAdapter(this,list,R.layout.lvitem_history,reuploadHandler);
		lvHistory.setAdapter( listItemAdapter) ; 
		lvHistory.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				openDetail(position);
				return false;
			}
		});
		
		lvHistory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				openDetail(position);
			}
		});
		
		
		
	}
	
	private void openDetail(Integer position){
		seledHistory = (UploadHistory) listItemAdapter.getItem(position);
		Intent intent = new Intent(this,HistoryDetailActivity.class);
		intent.putExtra(UploadHistory._ID, seledHistory.getId());
		startActivity(intent);	
		finish();
	}


	/**
	 * 创建上传的文件并上传服务器
	 */
	public boolean createFileAndUpload(UploadHistory history){

		List<BedTemperature> listTemperature = temperatureDao.queryForUpload(history.getId());
		FileOutputStream outStream = null;
		try {
			String uniqueTag = history.getUniqueTag();
			outStream = this.openFileOutput(Constants.uploadFileName, Context.MODE_WORLD_READABLE);
			StringBuffer sb = new StringBuffer(uniqueTag+"\r\n");
			for(int i=0; i<listTemperature.size();i++){
				BedTemperature bt = listTemperature.get(i);
				sb.append(bt.getBedNo()).append("|").append(bt.getInDate()).append("|").append(bt.getDataType()).append("|").append(bt.getTemperature()).append("\r\n");
			}
			outStream.write(sb.toString().getBytes()); 
			outStream.close();
			outStream=null;
			String uploadTime = DateUtil.getDateTimeStr();
			//history.setUploadName(uploadName);
			history.setGroupName(Session.getUser().getGroupName());
			history.setUploadTime(uploadTime);
			history.setUploadWay(Constants.UploadWay.BLUETOOTH);
			history.setUserName(Session.getUser().getUserName());
			if(fileService.uploadTemperature(history)){
				historyDao.reuploadFinish(history);
				//Toast.makeText(getApplicationContext(),"重传成功!", Toast.LENGTH_SHORT).show();
				return true;
			}
			
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "", e);
			Toast.makeText(getApplicationContext(),"重传失败!无法生成待上传的文件", Toast.LENGTH_SHORT).show();
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
		
		return false;
	}
	
	
	private LvHistoryReupload reuploadHandler = new LvHistoryReupload(){

		@Override
		public void reupload(final UploadHistory history, int position,
				final LvHistoryAdapter adapter) {
			final UploadConfirmDialogEvent evtHandler = new UploadConfirmDialogEvent("请选择上传方式",null,history,position,adapter);
			AlertDialog.Builder builder = ActivityUtils.confirmDialog(UploadHistoryActivity.this,evtHandler);
			builder.setSingleChoiceItems(new String[]{"蓝牙","WIFI"}, 0, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					evtHandler.selOpt = which;
				}
			});
			ActivityUtils.show(builder);
			
		}
	};
	

	
	
	private class UploadConfirmDialogEvent extends ConfirmDialogEvent{

		public int selOpt=1;
		public UploadHistory history;
		public int position;
		public LvHistoryAdapter adapter ;
		public UploadConfirmDialogEvent(String title, String message,final UploadHistory history, int position,
				final LvHistoryAdapter adapter) {
			super(title, message);
			this.history = history;
			this.position = position;
			this.adapter = adapter;
		}
		
		@Override
		public void onOK(Context ctx,DialogInterface dialog, int whichButton) {
			if(selOpt == 0){
				//WIFI
				
			}else{
				//蓝牙
				if(createFileAndUpload(this.history))this.adapter.notifyDataSetChanged();
			}
		}
	}
	
	
}
