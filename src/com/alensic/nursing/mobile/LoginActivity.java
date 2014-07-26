package com.alensic.nursing.mobile;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alensic.nursing.mobile.common.BaseMainActivity;
import com.alensic.nursing.mobile.common.ConfirmDialogEvent;
import com.alensic.nursing.mobile.common.Session;
import com.alensic.nursing.mobile.dao.BedDao;
import com.alensic.nursing.mobile.dao.BedDaoImpl;
import com.alensic.nursing.mobile.dao.GroupsDao;
import com.alensic.nursing.mobile.dao.GroupsDaoImpl;
import com.alensic.nursing.mobile.dao.SysParamDao;
import com.alensic.nursing.mobile.dao.SysParamDaoImpl;
import com.alensic.nursing.mobile.dao.UsersDao;
import com.alensic.nursing.mobile.dao.UsersDaoImpl;
import com.alensic.nursing.mobile.model.Users;
import com.alensic.nursing.mobile.service.MyService;
import com.alensic.nursing.mobile.service.MyServiceConnection;
import com.alensic.nursing.mobile.ui.TemperatureActivity;
import com.alensic.nursing.mobile.ui.titlebar.TitleBarView;
import com.alensic.nursing.mobile.util.ActivityUtils;
import com.alensic.nursing.mobile.util.DBHelper;

public class LoginActivity extends BaseMainActivity {

	private Button mButton = null;
	private BedDao bedDao = new BedDaoImpl(this);
	private GroupsDao groupsDao = new GroupsDaoImpl(this);
	private UsersDao userDao = new UsersDaoImpl(this);
	private EditText nameItem ;
	private EditText wardItem ;
	private Button loginBtn ;
	private Button exitBtn ;
	private TitleBarView titleBar;
	private SysParamDao paramDao = new SysParamDaoImpl(this);
	MyServiceConnection conn = new MyServiceConnection();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		nameItem = (EditText)LoginActivity.this.findViewById(R.id.login_name);
		wardItem = (EditText)LoginActivity.this.findViewById(R.id.login_ward);
		/*titleBar = (TitleBarView)this.findViewById(R.id.titleBar);
		loginBtn = titleBar.getBtn_right();
		exitBtn = titleBar.getBtn_left();*/
		//运行测试代码
		DBHelper.getInstance(this).runTestData();
		
		initTitleBar(this,ACTIVITY_LOGIN,R.string.ttl_login,R.string.ttr_login,R.string.tt_login);

		initInputItem();
		initServiceBtn();
	}
	

	/**
	 * 初始化输入框
	 */
	private void initInputItem(){
		Users user = userDao.getLastUser();
		if(user != null){
			nameItem = (EditText)this.findViewById(R.id.login_name);
			wardItem = (EditText)this.findViewById(R.id.login_ward);
			nameItem.setText(user.getUserName());
			wardItem.setText(user.getGroupName());
		}
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}



	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	
	protected void destroy(){
		Builder builder = ActivityUtils.confirmDialog(this, new ConfirmDialogEvent("确认退出体温管理", null){
			@Override
			public void onOK(Context ctx, DialogInterface dialog,
					int whichButton) {
				finish();
			}
			
		});
		ActivityUtils.show(builder);
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			destroy();
			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}



	@Override
	public void onNextBtnClick(View v) {
		String name = nameItem.getText().toString().trim();
		String ward = wardItem.getText().toString().trim();
		if(name.equals("") || ward.equals("")){
			Toast toast = Toast.makeText(getApplicationContext(),
				     "请输入[姓名/工号]和[病区]", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		Users user = new Users();
		user.setGroupName(ward);
		user.setUserName(name);
		userDao.login(user);
		initSession();
		Intent intent = new Intent(LoginActivity.this,TemperatureActivity.class);
		startActivity(intent);	
		finish();
	}



	/**
	 * 初始化session
	 * 将所有系统参数都保存到session中
	 */
	private void initSession() {
		String value;
		value = paramDao.queryValue4Code("autoSave");
		Session.setAutoSave("1".equals(value));
		
		value = paramDao.queryValue4Code("soundOnSaveSucceed");
		Session.setSoundOnSaveSucceed("1".equals(value));
		
		value = paramDao.queryValue4Code("soundOnSaveFail");
		Session.setSoundOnSaveFail("1".equals(value));
		
		value = paramDao.queryValue4Code("soundOnUploadSucceed");
		Session.setSoundOnUploadSucceed("1".equals(value));
		
		value = paramDao.queryValue4Code("soundOnUploadFail");
		Session.setSoundOnUploadFail("1".equals(value));
		
		value = paramDao.queryValue4Code("uploadByWIFI");
		Session.setUploadByWIFI("1".equals(value));
		
		value = paramDao.queryValue4Code("WIFIName");
		Session.setWIFIName(value);
		
		value = paramDao.queryValue4Code("uploadByBluetooth");
		Session.setUploadByBluetooth("1".equals(value));
		
		value = paramDao.queryValue4Code("bluetoothName");
		Session.setBluetoothName(value);
		
		value = ActivityUtils.getLocalMacAddress(this);
		Session.setMac(value);
	}


	@Override
	public void onBackBtnClick(View v) {
		destroy();
	}
	
	
	public void initServiceBtn() {
		/*Button btnStartService = (Button)this.findViewById(R.id.btn_service_start);
		Button btnStopService = (Button)this.findViewById(R.id.btn_service_stop);
		Button btnBindService = (Button)this.findViewById(R.id.btn_service_bind);
		Button btnUnBindService = (Button)this.findViewById(R.id.btn_service_unbind);
		btnStartService.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,MyService.class);
				//LoginActivity.this.bindService(intent, conn, Context.BIND_AUTO_CREATE); 
				LoginActivity.this.startService(intent);
				
				
			}
			
		});
		
		btnStopService.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,MyService.class);  
				LoginActivity.this.stopService(intent);
			}
			
		});

		btnBindService.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,MyService.class);  
				LoginActivity.this.bindService(intent, conn, Context.BIND_AUTO_CREATE);
			}
			
		});
		

		btnUnBindService.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(LoginActivity.this,MyService.class);  
				LoginActivity.this.unbindService(conn);
			}
			
		});*/
		
	}
	

}
