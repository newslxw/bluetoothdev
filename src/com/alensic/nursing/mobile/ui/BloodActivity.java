package com.alensic.nursing.mobile.ui;


import com.alensic.nursing.mobile.R;
import com.alensic.nursing.mobile.model.BedTemperature;
import com.alensic.nursing.mobile.model.Blood;
import com.alensic.nursing.mobile.ui.blood.BloodBluetoothService;
import com.alensic.nursing.mobile.ui.bluebooth.BluetoothService;
import com.alensic.nursing.mobile.util.Constants;

import android.os.Bundle;
import android.os.Message;
import android.view.View;

public class BloodActivity extends TemperatureActivity {


	protected String TAG = "BloodActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setDataType(BedTemperature.TYPE_BLOOD);
		this.setActivityNo(ACTIVITY_BLOOD);
		this.setTitleId(R.string.tt_blood);
		super.onCreate(savedInstanceState);
		btnLinkDevice.setText("连接血压计");
		//initTitleBar(this,ACTIVITY_BLOOD,R.string.ttl_back,R.string.ttr_ct,R.string.tt_blood);	
		//btnNext.setVisibility(View.GONE);
		
	}
	
	

	/**
	 * 更新标题栏的数目
	 * @param tc 已测量数目
	 * @param total
	 */
	public void updateTitle(int tc, int total){
		tvTitle.setText("测量血压和心率("+tc+"/"+total+")");
	}
	
	protected BluetoothService createBluetoothService(){
		return new BloodBluetoothService(this, mEarHandler);
	}

	
	protected String getMacKey(){
		return Constants.PreferencesKey.LastBloodDeviceMac;
	}
	
	protected String getDeviceNameKey(){
		return Constants.PreferencesKey.LastBloodDeviceName;
	}
	
	protected String parseTemperatureMsg(Message msg){
		Blood blood = (Blood)msg.obj;
		String str = blood.getDia()+"/"+blood.getSys()+"#"+ blood.getMap()+"#" + blood.getMb();
		return str;
	}
	
}
