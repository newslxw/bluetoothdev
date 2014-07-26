package com.alensic.nursing.mobile.ui;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alensic.nursing.mobile.R;
import com.alensic.nursing.mobile.common.BaseMainActivity;
import com.alensic.nursing.mobile.common.ConfirmDialogEvent;
import com.alensic.nursing.mobile.dao.BedDao;
import com.alensic.nursing.mobile.dao.BedDaoImpl;
import com.alensic.nursing.mobile.dao.GroupBedsDao;
import com.alensic.nursing.mobile.dao.GroupBedsDaoImpl;
import com.alensic.nursing.mobile.dao.GroupsDao;
import com.alensic.nursing.mobile.dao.GroupsDaoImpl;
import com.alensic.nursing.mobile.dao.UsersDao;
import com.alensic.nursing.mobile.dao.UsersDaoImpl;
import com.alensic.nursing.mobile.model.Groups;
import com.alensic.nursing.mobile.util.ActivityUtils;

public class GroupMngActivity extends BaseMainActivity {
	
	private static final String GROUP_KEY = "_group";
	
	private static final int DOUBLE_CLICK_TIME = 1000; //双击间隔时间
	
	private static final String GROUP_NAME_NULL_ALERT = "分组名不能为空!";
	
	private static final String GROUP_NAME_EXIST_ALERT = "分组名:{0}已存在,请重新输入";

	private BedDao bedDao = new BedDaoImpl(this);
	private GroupsDao groupsDao = new GroupsDaoImpl(this);
	private GroupBedsDao groupBedsDao = new GroupBedsDaoImpl(this);
	private UsersDao userDao = new UsersDaoImpl(this);
	
	private ListView mGroupLv;
	
	private List<Map<String,Groups>> groupList = new ArrayList<Map<String,Groups>>();
	private SimpleAdapter groupAdapter = null;
	
	//处理双击时的变量
    private long clickTime = 0; //当前点击时间
    private long lastClickTime = 0; //上一次点击时间
    private int lastClickId = 0; //上一次点击控件的ID
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		upLevelActivity=BedMngActivity.class;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_group_mng);
		initTitleBar(this,ACTIVITY_BED,R.string.ttl_back,R.string.ttr_group,R.string.tt_group);
		initNav(this,ACTIVITY_BED);
		
		mGroupLv = (ListView)findViewById(R.id.groupListView);
		
		initListView();
		initSetListener();
		setListView();
	}
	
	
	/**
	 * 初始化ListView
	 */
	private void initListView(){
		groupAdapter = new SimpleAdapter(getApplicationContext(), groupList, R.layout.list_item_group_mng, 
				new String[] {GROUP_KEY },
				new int[] {R.id.groupItem });
		
		groupAdapter.setViewBinder(new ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, final Object data,
					String textRepresentation) {
				
				LinearLayout groupItem = (LinearLayout)view;
				TextView groupNameText = (TextView)groupItem.findViewById(R.id.groupName);
				Button assignBedBtn = (Button)groupItem.findViewById(R.id.assignBed);
				Button delGroupImg = (Button)groupItem.findViewById(R.id.delGroup);
				final TableRow normal = (TableRow)groupItem.findViewById(R.id.normal);
				final TableRow modification = (TableRow)groupItem.findViewById(R.id.modification);
				final EditText newGroupName = (EditText)groupItem.findViewById(R.id.newGroupName);
				ImageView updateGroup = (ImageView)groupItem.findViewById(R.id.updateGroup);
				
				normal.setVisibility(View.VISIBLE);
				modification.setVisibility(View.GONE);
				
				//设置组名
				groupNameText.setText(((Groups)data).getGroupAT());


				//设置分配按钮
				assignBedBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Bundle bundle = new Bundle();
						bundle.putString("GroupName", ((Groups)data).getGroupName());
						bundle.putInt("GroupId", ((Groups)data).getId());
						
						Intent intent = new Intent(GroupMngActivity.this, AssignBedActivity.class);
						intent.putExtras(bundle);
						
						startActivity(intent);
						finish();
					}
				});

				//设置删除分组
				if(((Groups)data).getRemovable() == 2){
					delGroupImg.setVisibility(View.INVISIBLE);
				}
				delGroupImg.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Builder builder = ActivityUtils.confirmDialog(GroupMngActivity.this, new ConfirmDialogEvent("确定要删除吗?",null){
							@Override
							public void onOK(Context ctx,DialogInterface dialog, int whichButton) {
								groupBedsDao.delByGroupId(((Groups)data).getId());
								groupsDao.delete(((Groups)data).getId());
								setListView(); //刷新列表
							}
				    		
				    	});
						ActivityUtils.show(builder);
					}
				});
				
				//设置修改组名
				if(((Groups)data).getRemovable() != 2){
					newGroupName.setText(((Groups)data).getGroupName());
					updateGroup.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							if(((Groups)data).getGroupName().equals(newGroupName.getText().toString().trim())){ //修改后的分组名与原来一样
								normal.setVisibility(View.VISIBLE);
								modification.setVisibility(View.GONE);
								return;
							}
							if("".equals(newGroupName.getText().toString().trim())){ //分组名为空
								displayToast(GROUP_NAME_NULL_ALERT);
								return;
							}
							Groups groups = new Groups();
							groups.setId(((Groups)data).getId());
							groups.setGroupName(newGroupName.getText().toString().trim());
							groups = groupsDao.modificationGroup(groups);
							if(groups == null){ //分组号已存在,修改失败
								displayToast(MessageFormat.format(GROUP_NAME_EXIST_ALERT, newGroupName.getText().toString().trim()));
								return;
							}
							setListView(); //刷新列表
							
						}
					});
				}
				
				return true;
			}
		});
		
		mGroupLv.setAdapter(groupAdapter);
	}
	
	/**
	 * 初始化事件
	 */
	private void initSetListener(){
		mGroupLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View v, int arg2,
					long arg3) {//处理双击事件
				clickTime = System.currentTimeMillis();
				if(lastClickTime == 0 || lastClickId == 0 || lastClickId != v.getId() || clickTime - lastClickTime > DOUBLE_CLICK_TIME){ //视为第一次点击
					lastClickId = v.getId();
					lastClickTime = clickTime;
					return;
				}
				cleanDBClick();
				
				showModificationItem(listView, v, arg2, arg3);
			}
		});
		
		mGroupLv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> listView, View v,
					int arg2, long arg3) {

				showModificationItem(listView, v, arg2, arg3);
				return false;
			}
		});
	}
	
	/**
	 * 清除双击信息
	 */
	private void cleanDBClick(){
		clickTime = 0; 
	    lastClickTime = 0; 
	    lastClickId = 0;
	}
	
	/**
	 * 显示修改的Itme
	 * @param listView
	 * @param v
	 * @param arg2
	 * @param arg3
	 */
	private void showModificationItem(AdapterView<?> listView, View v, int arg2,
			long arg3){
		//检测是否系统默认的分组
		EditText newGroupName = (EditText)v.findViewById(R.id.newGroupName);
		if("".equals(newGroupName.getText().toString())){ //是系统默认的分组,不能修改
			return;
		}
		
		//还原ListView
		for(int i = 0, len = listView.getChildCount(); i < len; i++){
			View itemView = listView.getChildAt(i);
			TableRow normal = (TableRow)itemView.findViewById(R.id.normal);
			TableRow modification = (TableRow)itemView.findViewById(R.id.modification);
			normal.setVisibility(View.VISIBLE);
			modification.setVisibility(View.GONE);
		}
		
		//显示需要修改的行
		TableRow normal = (TableRow)v.findViewById(R.id.normal);
		TableRow modification = (TableRow)v.findViewById(R.id.modification);
		normal.setVisibility(View.GONE);
		modification.setVisibility(View.VISIBLE);
		newGroupName.requestFocus();
		newGroupName.selectAll();
	}

	/**
	 * set ListView的值
	 */
	private void setListView(){
		groupList.clear();
		List<Groups> list = groupsDao.queryGroupAndAssignedTotal();
		
		int total = 0;
		if(list!=null){
			total = list.size();
			for(int i=0; i<list.size(); i++)
			{
				Groups tempDerivedGroups = list.get(i);
				Map<String,Groups> tempMap = new HashMap<String,Groups>();
				tempMap.put(GROUP_KEY,tempDerivedGroups);
				groupList.add(tempMap);
			}
		}
		
		updateTitle(total); //刷新标题
		groupAdapter.notifyDataSetChanged(); //刷新ListView
		
	}
	
	// 显示Toast函数
	private void displayToast(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 更新标题栏的数目
	 * @param total
	 */
	public void updateTitle(int total){
		tvTitle.setText("分组信息("+total+")");
	}

	@Override
	public void onNextBtnClick(View v) {
		LayoutInflater factory = LayoutInflater.from(GroupMngActivity.this);
		final View addGroupView = factory.inflate(R.layout.dialog_add_group, null);
		
		AlertDialog.Builder builder = ActivityUtils.confirmDialog(GroupMngActivity.this, new ConfirmDialogEvent(null,null){
			@Override
			public void onOK(Context ctx,DialogInterface dialog, int whichButton) {
				EditText addGroupName = (EditText)addGroupView.findViewById(R.id.addGroupName);
				if("".equals(addGroupName.getText().toString().trim())){
					displayToast(GROUP_NAME_NULL_ALERT);
					ActivityUtils.setDialogShowing(dialog,false);
					return;
				}
				Groups groups = new Groups();
				groups.setGroupName(addGroupName.getText().toString().trim());
				groups = groupsDao.addGroup(groups);
				if(groups == null){
					displayToast(MessageFormat.format(GROUP_NAME_EXIST_ALERT, addGroupName.getText().toString().trim()));
					ActivityUtils.setDialogShowing(dialog,false);
					return;
				}
				setListView();
			}
    		
    	});
		
		
		builder.setView(addGroupView);
		ActivityUtils.show(builder);
	}
	
}

