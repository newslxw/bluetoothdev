package com.alensic.nursing.mobile.ui;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.alensic.nursing.mobile.LoginActivity;
import com.alensic.nursing.mobile.R;
import com.alensic.nursing.mobile.R.layout;
import com.alensic.nursing.mobile.R.menu;
import com.alensic.nursing.mobile.common.BaseMainActivity;
import com.alensic.nursing.mobile.common.BaseUploadActivity;
import com.alensic.nursing.mobile.common.ConfirmDialogEvent;
import com.alensic.nursing.mobile.common.Session;
import com.alensic.nursing.mobile.common.SpinnerItem;
import com.alensic.nursing.mobile.dao.BedDao;
import com.alensic.nursing.mobile.dao.BedDaoImpl;
import com.alensic.nursing.mobile.exception.NursingIOException;
import com.alensic.nursing.mobile.model.Bed;
import com.alensic.nursing.mobile.model.BedTemperature;
import com.alensic.nursing.mobile.model.UploadHistory;
import com.alensic.nursing.mobile.util.ActivityUtils;
import com.alensic.nursing.mobile.util.Constants;
import com.alensic.nursing.mobile.util.DateUtil;
import com.alensic.nursing.mobile.util.StringUtils;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class HistoryDetailActivity extends BaseUploadActivity {

	protected LvCTAdapter listItemAdapter;
	protected ListView lvHistory; 
	protected BedDao bedDao = new BedDaoImpl(this);
	protected Integer uploadId=1;
	protected int activityNo = ACTIVITY_HISTORY ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		uploadId = intent.getIntExtra(UploadHistory._ID, -1);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_history_detail);
		lvHistory = (ListView)this.findViewById(R.id.lvHistory);
		//savedInstanceState.get(key)
		
		int ttId = R.string.tt_upload;
		if(activityNo==ACTIVITY_HISTORY ){
			ttId = R.string.tt_upload_detail;
			upLevelActivity=UploadHistoryActivity.class;
		}
		
		initTitleBar(this,activityNo,R.string.ttl_back,R.string.ttr_upload,ttId);
		initNav(this,activityNo);
		if(activityNo==ACTIVITY_HISTORY ){
			this.btnNext.setVisibility(View.INVISIBLE);
		}else{
			this.btnNext.setText("上传");
		}
		
		initBedItem(uploadId);
	}
	
	/**
	 * 初始化病床列表
	 */
	protected void initBedItem(Integer uploadId){
		
		List<Bed> list=null;
		if(this.activityNo == ACTIVITY_UPLOAD){
			list = bedDao.queryNotUploadData();
		}else{
			list = bedDao.queryByUploadId(uploadId);
		}
		listItemAdapter = new LvCTAdapter(this,list,R.layout.lvitem_history,"HistoryDetailActivity");
		lvHistory.setAdapter( listItemAdapter) ; 
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.history_detail, menu);
		return true;
	}

	@Override
	public void onNextBtnClick(View v) {
	}
	

}
