package com.alensic.nursing.mobile.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alensic.nursing.mobile.R;
import com.alensic.nursing.mobile.common.BaseMainActivity;
import com.alensic.nursing.mobile.common.Session;
import com.alensic.nursing.mobile.common.SpinnerItem;
import com.alensic.nursing.mobile.dao.BedDao;
import com.alensic.nursing.mobile.dao.BedDaoImpl;
import com.alensic.nursing.mobile.dao.GroupsDao;
import com.alensic.nursing.mobile.dao.GroupsDaoImpl;
import com.alensic.nursing.mobile.dao.TemperatureDao;
import com.alensic.nursing.mobile.dao.TemperatureDaoImpl;
import com.alensic.nursing.mobile.exception.DBException;
import com.alensic.nursing.mobile.model.Bed;
import com.alensic.nursing.mobile.model.BedTemperature;
import com.alensic.nursing.mobile.model.Groups;
import com.alensic.nursing.mobile.ui.bluebooth.BluetoothService;
import com.alensic.nursing.mobile.ui.eardevice.EarBluetoothService;
import com.alensic.nursing.mobile.util.Constants;
import com.alensic.nursing.mobile.util.DateUtil;
import com.alensic.nursing.mobile.util.PreferenceHelper;
import com.alensic.nursing.mobile.util.StringUtils;

public class TemperatureActivity  extends BaseMainActivity {

	protected BedDao bedDao = new BedDaoImpl(this);
	protected GroupsDao groupsDao = new GroupsDaoImpl(this);
	protected TemperatureDao temperatureDao = new TemperatureDaoImpl(this);
	protected LvCTAdapter listItemAdapter;
	protected ListView mBedLv;
	protected Spinner itemGroups;
	protected CheckBox itemChk;
	protected TextView earDeviceValue;
	
	protected static BedTemperature temperature;  //当前没保存的测量值
	protected static Bed seledBed=null; //选中的病床
	
	protected String TAG = "TemperatureActivity";
	// 类型的消息发送从bluetoothservice处理程序
	public static final String TOAST = "toast";
	// 独特的是这个应用程序
	// Intent需要 编码
	public static final int REQUEST_CONNECT_DEVICE = 1;
	protected static final int REQUEST_ENABLE_BT = 2;
	protected static final int REQUEST_FILE_DEVICE = 3;
	// 名字的连接装置
	
	// 当地的蓝牙适配器
	protected BluetoothAdapter mBluetoothAdapter = null;
	protected String mDeviceName = null;
	protected String mDeviceMac = null;
	// 成员对象的聊天服务
	protected  BluetoothService mService = null;
	protected TextView btnLinkDevice;
		
	protected SoundPool soundPool;
	protected int soundIdOnSaveSucceed; //保存成功声音文件的id
	protected int soundIdOnSaveFail; //保存失败声音文件的id
	protected String dataType=BedTemperature.TYPE_EW; //测量的数据类型
	protected int activityNo=ACTIVITY_CT; //
	protected int titleId=R.string.tt_ct; //
	
	protected void onCreate(Bundle savedInstanceState) {
		if(temperature!=null&&!this.getDataType().equals(temperature.getDataType())){
			temperature = null;
			seledBed = null;
		}
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.temperature);
		itemGroups = (Spinner)this.findViewById(R.id.sel_group);
		mBedLv = (ListView)findViewById(R.id.item_beds);
		
		itemChk = (CheckBox)findViewById(R.id.chkTemperature);
		btnLinkDevice =  (Button)findViewById(R.id.btnLinkEarDevice);
		/*if(dataType.equals(BedTemperature.TYPE_BLOOD)){
			btnLinkDevice.setText("连接血压计");
		}*/
		/*if(dataType.equals(BedTemperature.TYPE_BLOOD)){
			activityNo = ACTIVITY_BLOOD;
			ttId = R.string.tt_blood;
		}*/
		initTitleBar(this,activityNo,R.string.ttl_back,R.string.ttr_ct,titleId);	
		btnNext.setVisibility(View.GONE);
		initNav(this,activityNo);
		initGroupsItem();
		initChkTemperature();
		Integer groupId = null;
		SpinnerItem item = (SpinnerItem)itemGroups.getSelectedItem();
		String str = item.GetID();
		if(!"".equals(str)) groupId = Integer.parseInt(str);
		initBedItem(groupId,Session.isMeterOrderByTemperature());
		initSound();
		

	}
	
	
	

	/**
	 * 初始化是否按测量排序
	 */
	protected void initChkTemperature(){
		Map<String,String> map = PreferenceHelper.getPreferenceValues(this, new String[]{Constants.PreferencesKey.MeterOrder});
		String meterOrder = null;
		if(map != null ) meterOrder = map.get(Constants.PreferencesKey.MeterOrder);
		itemChk.setChecked(Constants.enable.equals(meterOrder));
		Session.setMeterOrderByTemperature(Constants.enable.equals(meterOrder));
		itemChk.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				String meterOrder = isChecked?Constants.enable.toString():Constants.disable.toString();
				Session.setMeterOrderByTemperature(isChecked);
				PreferenceHelper.updatePreferenceValues(TemperatureActivity.this, new String[]{Constants.PreferencesKey.MeterOrder},new String[]{meterOrder});
				Integer groupId = null;
				SpinnerItem item = (SpinnerItem)itemGroups.getSelectedItem();
				String str = item.GetID();
				if(!"".equals(str)) groupId = Integer.parseInt(str);
				initBedItem(groupId,isChecked);
			}
			
		});
		
	}
	
	/**
	 * 初始化分组列表
	 */
	protected void initGroupsItem(){
		List<Groups> list = null;
		List<SpinnerItem > lst = new ArrayList<SpinnerItem>();
		list = groupsDao.query(null);
		for(int li=0; li<list.size();li++)
		{
			Groups groups = list.get(li);
			lst.add(new SpinnerItem(groups.getId().toString(),groups.getGroupName()));
		}
		lst.add(new SpinnerItem("","全部病床"));
		//将可选内容与ArrayAdapter连接起来
		ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<SpinnerItem>(this,
				R.layout.simple_spinner_item, lst);
		// 设置下拉列表的风格
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		// 将adapter 添加到spinner中
		itemGroups.setAdapter(adapter);
		// 添加事件Spinner事件监听
		itemGroups.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> parent, View view, int position,
					long id) {
				SpinnerItem item = (SpinnerItem)parent.getItemAtPosition(position);
				Integer groupId = null;
				if(!"".equals(item.GetID())) groupId = Integer.parseInt(item.GetID());
				initBedItem(groupId,Session.isMeterOrderByTemperature());
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		// 设置默认值
		itemGroups.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 初始化病床列表
	 */
	protected void initBedItem(Integer groupId,boolean orderByTemperature){
		List<Bed> list=null;
		list = bedDao.queryByGroupNo(groupId,orderByTemperature,this.dataType);
		boolean bc = true; //判断结果集中有没有选中的病床，没有就清空选中病床
		int tCount = 0;
		for(int i=0; list!=null&&i<list.size(); i++){
			Bed b = list.get(i);
			if(b.getTemperature()!=null&&!StringUtils.isEmpty(b.getTemperature().getTemperature())) tCount++;
			if(seledBed!=null&&seledBed.getId() == list.get(i).getId()){
				bc = false;
			}
		}
		updateTitle(tCount,list.size());
		if(bc) seledBed = null;
		listItemAdapter = new LvCTAdapter(this,list,R.layout.lvitem,seledBed);
		mBedLv.setAdapter( listItemAdapter) ; 
		mBedLv.setOnItemClickListener(new OnItemClickListener() {  
            @Override  
            public void onItemClick(AdapterView<?> parent, View view, int position,  
                            long id) {  
            	Bed temp = (Bed) listItemAdapter.getItem(position);
            	//抛弃没保存的数据
            	if(seledBed != null && temp!=null&&temp.getId()!=seledBed.getId()&&seledBed.getTemperature()!=null
            			&&(seledBed.getTemperature().getId()==null||seledBed.getTemperature().getId()==0)){
            		seledBed.setTemperature(seledBed.getTemperatureBak());
            		seledBed.setTemperatureBak(null);
            	}
            	seledBed = temp;
            	listItemAdapter.changeBg(position);  
                      
            }  
    });  
		
		
		
	}


	/**
	 * 更新标题栏的数目
	 * @param tc 已测量数目
	 * @param total
	 */
	public void updateTitle(int tc, int total){
		/*if(this.dataType.equals(BedTemperature.TYPE_BLOOD)){
			tvTitle.setText("测量血压和心率("+tc+"/"+total+")");
		}else{*/
			tvTitle.setText("测量体温("+tc+"/"+total+")");
		//}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
	}
	
	/**
	 * 保存测量的问题，并且清空测量值
	 */
	public void saveTemperature(){
		temperature.setDataType(dataType);
		if(seledBed != null && temperature.getTemperature()!=null){
			try {
				if(seledBed.getTemperatureBak()!=null&&seledBed.getTemperatureBak().getInDate()!=null&&
						DateUtil.isRepeatTemperature(seledBed.getTemperature().getInDate(), seledBed.getTemperatureBak().getInDate())){
					//是重复的数据
					temperature.setId(seledBed.getTemperatureBak().getId());
					temperatureDao.update(temperature);
				}else{
					temperatureDao.insert(temperature);
				}
				if(Session.isSoundOnSaveSucceed()){ //播放保存成功声音
					soundPool.play(soundIdOnSaveSucceed, 1, 1, 0, 0, 1);
				}
				temperature = null;
				seledBed.setTemperatureBak(null);
			} catch (ParseException e) {
				Log.e(this.getClass().getName(), "", e);
				if(Session.isSoundOnSaveFail()){ //播放保存失败声音
					soundPool.play(soundIdOnSaveFail, 1, 1, 0, 0, 1);
				}
				throw new DBException(
						"saveTemperature error! Please contact the support or developer.",
						e);
			}
		}else{
			temperatureDao.insert(temperature);
			seledBed.setTemperatureBak(null);
			temperature = null;
		}
		int tCount = 0;
		for(int i=0; i<listItemAdapter.getCount(); i++){
			Bed temp = (Bed)listItemAdapter.getItem(i);
			if(temp.getTemperature()!=null&&temp.getTemperature().getId()!=null&&temp.getTemperature().getId()!=0) tCount++;
		}
		updateTitle(tCount,listItemAdapter.getCount());
		this.listItemAdapter.notifyDataSetChanged();
	}

	@Override
	public void onNextBtnClick(View v) {
		
	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//初始化蓝牙，获取耳温计
		initDeviceBlueTooth();
				
	}
	
	
	
	public boolean initDeviceBlueTooth(){
		btnLinkDevice.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent serverIntent = new Intent(TemperatureActivity.this, DeviceListActivity.class); // 跳转程序设置
				serverIntent.putExtra(BedTemperature.DATA_TYPE, dataType);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE); // 设置返回宏定义
			}
			
			
		});
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(getApplicationContext(),"设备没有蓝牙功能，不能接收数据!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			//打开蓝牙功能
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}else{
			if (mService == null)
				setupDevice();
		}
		return true;
		
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK) {
				mDeviceMac = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				mDeviceName = data.getExtras().getString(BluetoothService.DEVICE_NAME);
				Session.updateLastDevice(this, mDeviceMac, mDeviceName, dataType);
				mService.connect(mDeviceMac,mDeviceName);
			}
			break;
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				setupDevice();
			} else {
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * 创建服务
	 * @return
	 */
	protected BluetoothService createBluetoothService(){
		return new EarBluetoothService(this, mEarHandler);
	}


	/**
	 * 启动耳温计监听服务
	 */
	protected void setupDevice() {
		Log.d(TAG, "setupEarDevice()");
		// 初始化bluetoothchatservice执行蓝牙连接
		mService = createBluetoothService();
		if (mDeviceMac == null) {//没有耳温计的MAC，需要选择其他耳温计
			Map<String,String> tMap = Session.getLastDevice(this,dataType);;
			if(tMap == null){
				Intent serverIntent = new Intent(this, DeviceListActivity.class); // 跳转程序设置
				serverIntent.putExtra(BedTemperature.DATA_TYPE, dataType);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE); // 设置返回宏定义
			}else{
				mDeviceMac = tMap.get(getMacKey());
				mDeviceName = tMap.get(getDeviceNameKey());// 把蓝牙设备对象
				// 试图连接到装置
				mService.connect(mDeviceMac,mDeviceName);
			}
		}else {
			// 把蓝牙设备对象
			mService.connect(mDeviceMac,mDeviceName);
		}
		
	}
	
	
	protected String getMacKey(){
		return Constants.PreferencesKey.LastEarDeviceMac;
	}
	
	protected String getDeviceNameKey(){
		return Constants.PreferencesKey.LastEarDeviceName;
	}
	
	// 处理程序，获取信息的bluetoothservice回来
	protected Handler mEarHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			String text = null;
			switch (msg.what) {
			case BluetoothService.MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BluetoothService.STATE_CONNECTED:
					btnLinkDevice.setText("已连接"+mDeviceName);
					break;
				case BluetoothService.STATE_CONNECTING:
					text = "正在连接";
					if(msg.obj != null) text=msg.obj.toString();
					btnLinkDevice.setText(text);
					break;
				case BluetoothService.STATE_LISTEN:
				case BluetoothService.STATE_NONE:
					text = "未连接";
					if(msg.obj != null) text=msg.obj.toString();
					btnLinkDevice.setText(text);
					break;
				case BluetoothService.STATE_CANNT_CONNECTED:
					btnLinkDevice.setText("无法连接"+mDeviceName);
					break;
				}
				break;
			case BluetoothService.MESSAGE_WRITE:

				break;
			case BluetoothService.MESSAGE_READ:
				temperature = new BedTemperature();
				temperature.setInDate(DateUtil.getDateTimeStr());
				String str = parseTemperatureMsg(msg);
				temperature.setTemperature(str);
				temperature.setDataType(dataType);
				if(seledBed!= null&& listItemAdapter.getWhichClick()!=-1){
					temperature.setBedId(seledBed.getId());
					listItemAdapter.updateItem(listItemAdapter.getWhichClick(), temperature);
					if(Session.isAutoSave()){
						saveTemperature();
					}else{
						//显示保存按钮
						
					}
				}
				break;
			case BluetoothService.MESSAGE_DEVICE_NAME:
				// 保存该连接装置的名字
				mDeviceName = msg.getData().getString(BluetoothService.DEVICE_NAME);
				Toast.makeText(getApplicationContext(),"已连接 " + mDeviceName, Toast.LENGTH_SHORT).show();
				break;
			case BluetoothService.MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),msg.obj.toString(), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	protected String parseTemperatureMsg(Message msg){
		return (String)msg.obj;
	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(mService != null){
			mService.stop();
			mService = null;
		}
	}	
	
	//初始化声音
	protected void initSound(){
		soundPool = new SoundPool(4, AudioManager.STREAM_SYSTEM, 5);
		soundIdOnSaveSucceed = soundPool.load(this, R.raw.succeed, 1);
		soundIdOnSaveFail = soundPool.load(this, R.raw.fail, 1);
	}




	public String getDataType() {
		return dataType;
	}




	public void setDataType(String dataType) {
		this.dataType = dataType;
	}




	public int getActivityNo() {
		return activityNo;
	}




	public void setActivityNo(int activityNo) {
		this.activityNo = activityNo;
	}




	public int getTitleId() {
		return titleId;
	}




	public void setTitleId(int titleId) {
		this.titleId = titleId;
	}
	
}

