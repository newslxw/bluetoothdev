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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alensic.nursing.mobile.R;
import com.alensic.nursing.mobile.common.BaseMainActivity;
import com.alensic.nursing.mobile.common.ConfirmDialogEvent;
import com.alensic.nursing.mobile.common.SpinnerItem;
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
import com.alensic.nursing.mobile.model.Groups;
import com.alensic.nursing.mobile.util.ActivityUtils;

public class BedMngActivity extends BaseMainActivity {
	
	private static final String BED_KEY = "_bed";
	
	private static final int DOUBLE_CLICK_TIME = 1000; //双击间隔时间
	
	private static final String BED_NO_NULL_ALERT = "病床号不能为空!";
	
	private static final String BED_NO_EXIST_ALERT = "病床号:{0}已存在,请重新输入";

	private BedDao bedDao = new BedDaoImpl(this);
	private GroupsDao groupsDao = new GroupsDaoImpl(this);
	private GroupBedsDao groupBedsDao = new GroupBedsDaoImpl(this);
	private UsersDao userDao = new UsersDaoImpl(this);
	
	private Spinner itemGroups;
	private ListView mBedLv;
	
	private Button mGroupMng;
	
	private List<Map<String,Bed>> bedList = new ArrayList<Map<String,Bed>>();
	private SimpleAdapter bedAdapter = null;
	
	//处理双击时的变量
    private long clickTime = 0; //当前点击时间
    private long lastClickTime = 0; //上一次点击时间
    private int lastClickId = 0; //上一次点击控件的ID
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bed_mng);
		initTitleBar(this,ACTIVITY_BED,R.string.ttl_back,R.string.ttr_bed,R.string.tt_bed);
		initNav(this,ACTIVITY_BED);
		
		itemGroups = (Spinner)this.findViewById(R.id.sel_group);
		mGroupMng = (Button)findViewById(R.id.groupMng);
		mBedLv = (ListView)findViewById(R.id.bedListView);
		
		initGroupsItem();
		initListView();
		initSetListener();
		setListView(null);
	}
	
	/**
	 * 初始化分组列表
	 */
	private void initGroupsItem(){
		List<Groups> list = groupsDao.queryGroupAndAssignedTotal();
		List<SpinnerItem > lst = new ArrayList<SpinnerItem>();
		if(list!=null){
			for(int i=0; i<list.size(); i++)
			{
				Groups groups = list.get(i);
				lst.add(new SpinnerItem(groups.getId().toString(),groups.getGroupAT().toString()));
			}
		}
		lst.add(new SpinnerItem("0","全部病床"));
		//将可选内容与ArrayAdapter连接起来
		ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<SpinnerItem>(this,
				R.layout.simple_spinner_item, lst);
		// 设置下拉列表的风格
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		// 将adapter 添加到spinner中
		itemGroups.setAdapter(adapter);
		// 添加事件Spinner事件监听
		itemGroups.setOnItemSelectedListener(new SpinnerSelectedListener());
		// 设置默认值
		itemGroups.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 初始化ListView
	 */
	private void initListView(){
		bedAdapter = new SimpleAdapter(getApplicationContext(), bedList, R.layout.list_item_bed_mng, 
				new String[] { BED_KEY },
				new int[] { R.id.bedItem});
		
		bedAdapter.setViewBinder(new ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, final Object data,
					String textRepresentation) {
				final LinearLayout bedItem = (LinearLayout)view;
				
				final TextView bedNoText = (TextView)bedItem.findViewById(R.id.bedNo);
				ImageView delGroupBedImg = (ImageView)bedItem.findViewById(R.id.delGroupBed);
				ImageView delBedImg = (ImageView)bedItem.findViewById(R.id.delBed);
				final TableRow normal = (TableRow)bedItem.findViewById(R.id.normal);
				final TableRow modification = (TableRow)bedItem.findViewById(R.id.modification);
				final EditText newBedNo = (EditText)bedItem.findViewById(R.id.newBedNo);
				ImageView updateBed = (ImageView)bedItem.findViewById(R.id.updateBed);
				
				normal.setVisibility(View.VISIBLE);
				modification.setVisibility(View.GONE);
				
				//设置病床号
				bedNoText.setText(((Bed)data).getBedNo());
				
			
				//设置从分组中删除该病床号
				if("0".equals(((SpinnerItem)itemGroups.getSelectedItem()).GetID())){
					delGroupBedImg.setVisibility(View.GONE);
				}else{
					delGroupBedImg.setVisibility(View.VISIBLE);
					delGroupBedImg.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							Builder builder = ActivityUtils.confirmDialog(BedMngActivity.this, new ConfirmDialogEvent("确定从" + ((SpinnerItem)itemGroups.getSelectedItem()).GetValue() + "中删除\"" + ((Bed)data).getBedNo() + "\"?",null){
								@Override
								public void onOK(Context ctx,DialogInterface dialog, int whichButton) {
									int bedId = Integer.valueOf( ((Bed)data).getId().toString() );
									int groupId = Integer.valueOf( ((SpinnerItem)itemGroups.getSelectedItem()).GetID() );
									groupBedsDao.delByBedAndGroupId(bedId, groupId);
									setListView(((SpinnerItem)itemGroups.getSelectedItem()).GetID());//刷新ListView
								}
					    		
					    	});
							ActivityUtils.show(builder);
						}
					});
				}
				
				//设置删除该病床号
				delBedImg.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Builder builder = ActivityUtils.confirmDialog(BedMngActivity.this, new ConfirmDialogEvent("确定要彻底删除\"" + ((Bed)data).getBedNo() +"\"?",null){
							@Override
							public void onOK(Context ctx,DialogInterface dialog, int whichButton) {
								int bedId = Integer.valueOf( ((Bed)data).getId().toString() );
								groupBedsDao.delByBedId(bedId);
								bedDao.delete(bedId);
								setListView(((SpinnerItem)itemGroups.getSelectedItem()).GetID());//刷新ListView
							}
				    		
				    	});
						ActivityUtils.show(builder);
					}
				});
				
				
				//设置修改病床号
				newBedNo.setText(((Bed)data).getBedNo());
				
				updateBed.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						if(((Bed)data).getBedNo().equals(newBedNo.getText().toString().trim())){ //修改后的病床号没有改变
							normal.setVisibility(View.VISIBLE);
							modification.setVisibility(View.GONE);
							return;
						}
						if("".equals(newBedNo.getText().toString().trim())){ //病床号为空
							displayToast(BED_NO_NULL_ALERT);
							return;
						}
						Bed bed = new Bed();
						bed.setId(((Bed)data).getId());
						bed.setBedNo(newBedNo.getText().toString().trim());
						bed = bedDao.modificationBed(bed);
						if(bed == null){ //病床号已存在,修改失败
							displayToast(MessageFormat.format(BED_NO_EXIST_ALERT, newBedNo.getText().toString().trim()));
							return;
						}
						setListView(((SpinnerItem)itemGroups.getSelectedItem()).GetID());//刷新ListView
						
					}
				});
			
				return true;
			}
		});
		
		mBedLv.setAdapter(bedAdapter);
	}
	
	/**
	 * 初始化事件
	 */
	private void initSetListener(){
		mGroupMng.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(BedMngActivity.this, GroupMngActivity.class));
				finish();
			}
		});
		
		mBedLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View v, int arg2,
					long arg3) { //处理双击事件
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
		
		mBedLv.setOnItemLongClickListener(new OnItemLongClickListener() {

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
		EditText newBedNo = (EditText)v.findViewById(R.id.newBedNo);
		normal.setVisibility(View.GONE);
		modification.setVisibility(View.VISIBLE);
		newBedNo.requestFocus();
		newBedNo.selectAll();
	}
	
	/**
	 * set ListView的值
	 */
	private void setListView(String groupID){
		bedList.clear();
		List<Bed> list = null;
		if(groupID == null){
			groupID = "1";
		}
		if("0".equals(groupID)){
			list = bedDao.query(null);//查询全部
		}else{
			list = bedDao.queryGroupBed(groupID);//查询分组内的
		}
		
		for(int i=0; i<list.size(); i++)
		{
			Bed tempBed = list.get(i);
			Map<String,Bed> tempMap = new HashMap<String,Bed>();
			tempMap.put(BED_KEY,tempBed);
			bedList.add(tempMap);
		}
		updateTitle(list.size()); //刷新标题
		bedAdapter.notifyDataSetChanged(); //刷新ListView
		
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
		tvTitle.setText("床位信息("+total+")");
	}
	
	class SpinnerSelectedListener implements OnItemSelectedListener{

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			setListView(((SpinnerItem)itemGroups.getSelectedItem()).GetID());
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	@Override
	public void onNextBtnClick(View v) {
		LayoutInflater factory = LayoutInflater.from(BedMngActivity.this);
		final View addBedView = factory.inflate(R.layout.dialog_add_bed, null);
		
		//初始化Spinner开始----
		final Spinner addItemGroups = (Spinner)addBedView.findViewById(R.id.selAddGroup);
		
		List<Groups> list = groupsDao.queryGroupAndAssignedTotal();
		List<SpinnerItem > addLst = new ArrayList<SpinnerItem>();
		
		if(list!=null){
			for(int i=0; i<list.size(); i++)
			{
				Groups groups = list.get(i);
				addLst.add(new SpinnerItem(groups.getId().toString(),groups.getGroupAT().toString()));
			}
		}
		addLst.add(new SpinnerItem("0","无"));
		//将可选内容与ArrayAdapter连接起来
		ArrayAdapter<SpinnerItem> addAdapter = new ArrayAdapter<SpinnerItem>(this,
				R.layout.simple_spinner_item, addLst);
		// 设置下拉列表的风格
		addAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		// 将adapter 添加到spinner中
		addItemGroups.setAdapter(addAdapter);
		//初始化Spinner结束----
		
		AlertDialog.Builder builder = ActivityUtils.confirmDialog(BedMngActivity.this, new ConfirmDialogEvent(null,null){
			@Override
			public void onOK(Context ctx,DialogInterface dialog, int whichButton) {
				EditText addBedNo = (EditText)addBedView.findViewById(R.id.edtAddBed);
				if("".equals(addBedNo.getText().toString().trim())){ //病床号为空
					displayToast(BED_NO_NULL_ALERT);
					ActivityUtils.setDialogShowing(dialog,false);
					return;
				}
				Bed bed = new Bed();
				bed.setBedNo(addBedNo.getText().toString());
				bed = bedDao.addBed(bed);
				if(bed == null){ //病床号已存在
					displayToast(MessageFormat.format(BED_NO_EXIST_ALERT, addBedNo.getText().toString()));
					ActivityUtils.setDialogShowing(dialog,false);
					return;
				}

				String groupId = ((SpinnerItem)addItemGroups.getSelectedItem()).GetID();
				if(!"0".equals(groupId)){
					GroupBeds groupBeds = new GroupBeds();
					groupBeds.setBedId(bed.getId());
					groupBeds.setGroupsId(Integer.valueOf(groupId));
					groupBedsDao.insert(groupBeds);
				}
				displayToast("病床号:" + bed.getBedNo() + "新增成功.");
				setListView(((SpinnerItem)itemGroups.getSelectedItem()).GetID());//刷新ListView
			}
    		
    	});
		
		
		builder.setView(addBedView);
		ActivityUtils.show(builder);
	}

}

