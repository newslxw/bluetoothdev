package com.alensic.nursing.mobile.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

import com.alensic.nursing.mobile.R;
import com.alensic.nursing.mobile.common.BaseMainActivity;
import com.alensic.nursing.mobile.dao.BedDao;
import com.alensic.nursing.mobile.dao.BedDaoImpl;
import com.alensic.nursing.mobile.dao.GroupBedsDao;
import com.alensic.nursing.mobile.dao.GroupBedsDaoImpl;
import com.alensic.nursing.mobile.dao.GroupsDao;
import com.alensic.nursing.mobile.dao.GroupsDaoImpl;
import com.alensic.nursing.mobile.dao.UsersDao;
import com.alensic.nursing.mobile.dao.UsersDaoImpl;
import com.alensic.nursing.mobile.model.Bed;
import com.alensic.nursing.mobile.model.GroupBeds;

public class AssignBedActivity extends BaseMainActivity {
	
	private static final String ASSIGN_BED_KEY = "_AssignBed";

	private BedDao bedDao = new BedDaoImpl(this);
	private GroupsDao groupsDao = new GroupsDaoImpl(this);
	private GroupBedsDao groupBedsDao = new GroupBedsDaoImpl(this);
	private UsersDao userDao = new UsersDaoImpl(this);
	
	private Bundle bundle;
	
	private ListView mBedLv;
	
	private List<Map<String,Bed>> bedList = new ArrayList<Map<String,Bed>>();
	private SimpleAdapter bedAdapter = null;
	
	private int index = -1; //注入ListView子控件事件时,作为遍历下标用
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		upLevelActivity=GroupMngActivity.class;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_assign_bed);
		initTitleBar(this,ACTIVITY_BED,R.string.ttl_back,R.string.ttr_bed_assign,R.string.tt_bed);
		initNav(this,ACTIVITY_BED);
		
		bundle = getIntent().getExtras();
		updateTitle(bundle.getString("GroupName"));
		
		mBedLv = (ListView)findViewById(R.id.bedListView);
		
		initListView();
		initSetListener();
		setListView();
	}
	
	
	/**
	 * 初始化ListView
	 */
	private void initListView(){
		bedAdapter = new SimpleAdapter(getApplicationContext(), bedList, R.layout.list_item_assign_bed, 
				new String[] { ASSIGN_BED_KEY, ASSIGN_BED_KEY, ASSIGN_BED_KEY },
				new int[] { R.id.assignBox, R.id.bedNo, R.id.bedID });
		
		bedAdapter.setViewBinder(new ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, final Object data,
					String textRepresentation) {
				
				switch(++index % 3){
				case 0:
					final CheckBox assignBox = (CheckBox)view;
					assignBox.setChecked(((Bed)data).getIsAssign());
					break;
				case 1:
					TextView bedNoText = (TextView)view;
					bedNoText.setText(((Bed)data).getBedNo());
					break;
				case 2:
					TextView bedIDText = (TextView)view;
					bedIDText.setText(((Bed)data).getId().toString());
					break;
				}
				return true;
			}
		});
		
		mBedLv.setAdapter(bedAdapter);
		
	}
	
	/**
	 * 初始化事件
	 */
	private void initSetListener(){
		
		mBedLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				CheckBox assignBox = (CheckBox)arg1.findViewById(R.id.assignBox);
				assignBox.setChecked(!assignBox.isChecked());
				
			}
		});
		
	}

	/**
	 * set ListView的值
	 */
	private void setListView(){
		Cursor cursor;
		bedList.clear();
		List<Bed> list ;
		list = groupBedsDao.queryAllBed4Group(bundle.getInt("GroupId"));//查询全部,已分配组的会打钩
		
		for(int i=0; i<list.size();i++)
		{
			Bed tempAssignBed = list.get(i);
			Map<String,Bed> tempMap = new HashMap<String,Bed>();
			tempMap.put(ASSIGN_BED_KEY,tempAssignBed);
			bedList.add(tempMap);
		}
		bedAdapter.notifyDataSetChanged();
		
	}
	
	// 显示Toast函数
	private void displayToast(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 更新标题栏的数目
	 * @param groupName 组名
	 */
	public void updateTitle(String groupName){
		tvTitle.setText(groupName+"-选择病床");
	}
	
	@Override
	public void onNextBtnClick(View v) {
		List<Integer> bedIds = new ArrayList<Integer>();
		
		for(int i = 0, len = mBedLv.getChildCount(); i < len; i++){
			View view = mBedLv.getChildAt(i);
			CheckBox cb = (CheckBox)view.findViewById(R.id.assignBox);
			if(cb.isChecked()){
				TextView bedIdText = (TextView)view.findViewById(R.id.bedID);
				bedIds.add(Integer.valueOf( bedIdText.getText().toString() ));
			}
		}
		
		groupBedsDao.delByGroupId(bundle.getInt("GroupId"));//删除该组所有分配
		
		GroupBeds tempGB = new GroupBeds();
		tempGB.setGroupsId(bundle.getInt("GroupId"));
		for(int j = 0, jLen = bedIds.size(); j < jLen; j++){
			tempGB.setBedId(bedIds.get(j));
			groupBedsDao.insert(tempGB);
		}
		
		displayToast("分配完成");
		Intent intent = new Intent(this,GroupMngActivity.class);
		startActivity(intent);	
		finish();
	}
}

