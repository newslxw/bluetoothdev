package com.alensic.nursing.mobile.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.alensic.nursing.mobile.LoginActivity;
import com.alensic.nursing.mobile.R;
import com.alensic.nursing.mobile.model.BedTemperature;
import com.alensic.nursing.mobile.ui.BedMngActivity;
import com.alensic.nursing.mobile.ui.BloodActivity;
import com.alensic.nursing.mobile.ui.HistoryDetailActivity;
import com.alensic.nursing.mobile.ui.SettingActivity;
import com.alensic.nursing.mobile.ui.TemperatureActivity;
import com.alensic.nursing.mobile.ui.UploadActivity;
import com.alensic.nursing.mobile.ui.UploadHistoryActivity;
import com.alensic.nursing.mobile.ui.titlebar.TitleBarView;

/**
 * 主界面的基类，有titlebar和navigatebar
 * @author xwlian
 *
 */
public abstract class BaseMainActivity extends Activity {

	protected View navBed;
	protected View navCt;
	protected View navUpload;
	protected View navSetting;
	protected View navBlood;

	protected Button btnNext;
	protected Button btnBack;
	protected TitleBarView titleBar;
	protected TextView tvTitle;
	
	public final static int ACTIVITY_LOGIN = -1;
	public final static int ACTIVITY_CT = 0;
	public final static int ACTIVITY_BED = 1;
	public final static int ACTIVITY_UPLOAD = 2;
	public final static int ACTIVITY_SETTING = 3;
	public final static int ACTIVITY_BLOOD = 4;
	protected Class upLevelActivity=LoginActivity.class; //按返回时默认的返回activity
	
	
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	/**
	 * 初始化导航栏
	 * @param ctx 
	 * @param curActivityNo -登陆=-1,测量体温=0, 病床管理=1,上传历史=2,设置=3
	 */
	protected void initNav(final Context ctx,int curActivityNo){
		navBed = (View)findViewById(R.id.navBed);
		navCt = (View)findViewById(R.id.navCt);
		navUpload = (View)findViewById(R.id.navUpload);
		navSetting = (View)findViewById(R.id.navHistory);
		navBlood = (View)findViewById(R.id.navBlood);
		View arr[]=new View[]{navCt,navBed,navUpload,navSetting,navBlood};
		if(curActivityNo>ACTIVITY_LOGIN)arr[curActivityNo].setBackgroundResource(R.drawable.ic_tab_bg_selected);
		if(curActivityNo !=ACTIVITY_CT){
			navCt.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							Intent intent = new Intent(ctx, TemperatureActivity.class);
							startActivity(intent);
							((Activity)ctx).finish();
						}
					});
		}
		if(curActivityNo !=ACTIVITY_BED){
			navBed.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							Intent intent = new Intent(ctx, BedMngActivity.class);
							startActivity(intent);
							((Activity)ctx).finish();
						}
					});
		}
		if(curActivityNo !=ACTIVITY_UPLOAD){
			navUpload.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(ctx, UploadActivity.class);
					startActivity(intent);
					((Activity)ctx).finish();
				}
			});
		}
		if(curActivityNo !=ACTIVITY_SETTING){
			navSetting.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(ctx, SettingActivity.class);
					startActivity(intent);
					((Activity)ctx).finish();
				}
			});
		}
		if(curActivityNo !=ACTIVITY_BLOOD){
			navBlood.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(ctx, BloodActivity.class);
					startActivity(intent);
					((Activity)ctx).finish();
				}
			});
		}
	}	
	

	/**
	 * 
	 * 初始化titlebar
	 * @param ctx 
	 * @param curActivityNo  -登陆=-1,测量体温=0, 病床管理=1,上传历史=2,设置=3
	 * @param leftTextId   左边按钮text的id
	 * @param rightTextId  右边text的id
	 * @param titleId	   title的id
	 */
	protected void initTitleBar(final Context ctx,int curActivityNo,Integer leftTextId,Integer rightTextId,Integer titleId) {
		//titleBar = (TitleBarView)this.findViewById(R.id.titleBar);
		btnNext = (Button)this.findViewById(R.id.btn_right);
		btnBack = (Button)this.findViewById(R.id.btn_left);
		tvTitle = (TextView)this.findViewById(R.id.tv_title);
		if(leftTextId!=null) btnBack.setText(leftTextId);
		if(rightTextId!=null) btnNext.setText(rightTextId);
		if(titleId!=null) tvTitle.setText(titleId);
		btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				onBackBtnClick(v);
			}
			
		});
		
		btnNext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				onNextBtnClick(v);
			}
			
		});
	}
	
	
	public abstract void onNextBtnClick(View v);
	

	public void onBackBtnClick(View v){
		Intent intent = new Intent(this,upLevelActivity);
		startActivity(intent);	
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent(this,upLevelActivity);
			startActivity(intent);	
			finish();
			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}

}
