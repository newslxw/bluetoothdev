package com.alensic.nursing.mobile.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alensic.nursing.mobile.R;
import com.alensic.nursing.mobile.common.BaseMainActivity;
import com.alensic.nursing.mobile.common.Session;
import com.alensic.nursing.mobile.dao.SysParamDao;
import com.alensic.nursing.mobile.dao.SysParamDaoImpl;

/**
 * 设置界面
 * @author xwlian
 *
 */
public class SettingActivity  extends BaseMainActivity {
	
	private SysParamDao sysParamDao = new SysParamDaoImpl(this);

	private CheckBox mAutoSave;
	private CheckBox mSoundOnSaveSucceed;
	private CheckBox mSoundOnSaveFail;
	private CheckBox mSoundOnUploadSucceed;
	private CheckBox mSoundOnUploadFail;
	
	private CheckBox mServicerAddressCB;
	private EditText mServicerAddress;
	private CheckBox mBluetoothCB;
	private Button mSearchBluetooth;
	private TextView mBluetoothDevice;
	
	private TextView tv_autoSave;
	private TextView tv_soundOnSaveSucceed;
	private TextView tv_soundOnUploadSucceed;
	private TextView tv_soundOnUploadFail;
	private TextView tv_soundOnSaveFail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		initTitleBar(this,ACTIVITY_SETTING,R.string.ttl_back,R.string.ttr_setting,R.string.tt_setting);
		initNav(this,ACTIVITY_SETTING);
		
		mAutoSave = (CheckBox)findViewById(R.id.autoSave);
		mSoundOnSaveSucceed = (CheckBox)findViewById(R.id.soundOnSaveSucceed);
		mSoundOnSaveFail = (CheckBox)findViewById(R.id.soundOnSaveFail);
		mSoundOnUploadSucceed = (CheckBox)findViewById(R.id.soundOnUploadSucceed);
		mSoundOnUploadFail = (CheckBox)findViewById(R.id.soundOnUploadFail);
		
		mServicerAddressCB = (CheckBox)findViewById(R.id.servicerAddressCB);
		mServicerAddress = (EditText)findViewById(R.id.servicerAddress);
		mBluetoothCB = (CheckBox)findViewById(R.id.bluetoothCB);
		mSearchBluetooth = (Button)findViewById(R.id.searchBluetooth);
		mBluetoothDevice = (TextView)findViewById(R.id.bluetoothDevice);
		
		tv_autoSave =  (TextView)findViewById(R.id.tv_autosave);
		tv_soundOnSaveSucceed =  (TextView)findViewById(R.id.tv_soundOnSaveSucceed);
		tv_soundOnUploadSucceed =  (TextView)findViewById(R.id.tv_soundOnUploadSucceed);
		tv_soundOnUploadFail =  (TextView)findViewById(R.id.tv_soundOnUploadFail);
		tv_soundOnSaveFail =  (TextView)findViewById(R.id.tv_soundOnSaveFail);
		
		initSetValues();
		initSetListener();
	}

	@Override
	public void onNextBtnClick(View v) {
		updateValues();
		displayToast("保存成功");
	}
	
	private final OnClickListener tvClickHandle = new OnClickListener(){

		@Override
		public void onClick(View v) {
			if(v == tv_autoSave){
				mAutoSave.toggle();
			}else if(v == tv_soundOnSaveFail){
				mSoundOnSaveFail.toggle();
			}else if(v == tv_soundOnSaveSucceed){
				mSoundOnSaveSucceed.toggle();
			}else if(v == tv_soundOnUploadFail){
				mSoundOnUploadFail.toggle();
			}else if(v == tv_soundOnUploadSucceed){
				mSoundOnUploadSucceed.toggle();
			}
			
		}
		
	};
	//初始化数据
	private void initSetValues(){
		tv_autoSave.setOnClickListener(tvClickHandle);
		tv_soundOnSaveFail.setOnClickListener(tvClickHandle);
		tv_soundOnSaveSucceed.setOnClickListener(tvClickHandle);
		tv_soundOnUploadFail.setOnClickListener(tvClickHandle);
		tv_soundOnUploadSucceed.setOnClickListener(tvClickHandle);
		String value;
		value = sysParamDao.queryValue4Code("autoSave");
		mAutoSave.setChecked("1".equals(value));
		
		value = sysParamDao.queryValue4Code("soundOnSaveSucceed");
		mSoundOnSaveSucceed.setChecked("1".equals(value));
		
		value = sysParamDao.queryValue4Code("soundOnSaveFail");
		mSoundOnSaveFail.setChecked("1".equals(value));
		
		value = sysParamDao.queryValue4Code("soundOnUploadSucceed");
		mSoundOnUploadSucceed.setChecked("1".equals(value));
		
		value = sysParamDao.queryValue4Code("soundOnUploadFail");
		mSoundOnUploadFail.setChecked("1".equals(value));
		
		value = sysParamDao.queryValue4Code("uploadByWIFI");
		mServicerAddressCB.setChecked("1".equals(value));
		
		value = sysParamDao.queryValue4Code("WIFIName");
		mServicerAddress.setText(value);
		
		value = sysParamDao.queryValue4Code("uploadByBluetooth");
		mBluetoothCB.setChecked("1".equals(value));
		
		value = sysParamDao.queryValue4Code("bluetoothName");
		mBluetoothDevice.setText(value);
	}
	
	/**
	 * 初始化事件
	 */
	private void initSetListener(){
		mSearchBluetooth.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void updateValues(){
		//保存到数据库
		sysParamDao.updateValue4Code("autoSave", mAutoSave.isChecked()? "1" : "0");
		sysParamDao.updateValue4Code("soundOnSaveSucceed", mSoundOnSaveSucceed.isChecked()? "1" : "0");
		sysParamDao.updateValue4Code("soundOnSaveFail", mSoundOnSaveFail.isChecked()? "1" : "0");
		sysParamDao.updateValue4Code("soundOnUploadSucceed", mSoundOnUploadSucceed.isChecked()? "1" : "0");
		sysParamDao.updateValue4Code("soundOnUploadFail", mSoundOnUploadFail.isChecked()? "1" : "0");
		
		sysParamDao.updateValue4Code("uploadByWIFI", mServicerAddressCB.isChecked()? "1" : "0");
		sysParamDao.updateValue4Code("WIFIName", mServicerAddress.getText().toString());
		sysParamDao.updateValue4Code("uploadByBluetooth", mBluetoothCB.isChecked()? "1" : "0");
		sysParamDao.updateValue4Code("bluetoothName", mBluetoothDevice.getText().toString());
		
		//保存到session
		Session.setAutoSave(mAutoSave.isChecked());
		Session.setSoundOnSaveSucceed(mSoundOnSaveSucceed.isChecked());
		Session.setSoundOnSaveFail(mSoundOnSaveFail.isChecked());
		Session.setSoundOnUploadSucceed(mSoundOnUploadSucceed.isChecked());
		Session.setSoundOnUploadFail(mSoundOnUploadFail.isChecked());
		
		Session.setUploadByWIFI(mServicerAddressCB.isChecked());
		Session.setWIFIName(mServicerAddress.getText().toString());
		Session.setUploadByBluetooth(mBluetoothCB.isChecked());
		Session.setBluetoothName(mBluetoothDevice.getText().toString());
	}
	
	// 显示Toast函数
	private void displayToast(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}

}
