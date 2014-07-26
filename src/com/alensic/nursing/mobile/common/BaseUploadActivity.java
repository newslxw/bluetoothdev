package com.alensic.nursing.mobile.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.alensic.nursing.mobile.LoginActivity;
import com.alensic.nursing.mobile.R;
import com.alensic.nursing.mobile.ui.BedMngActivity;
import com.alensic.nursing.mobile.ui.HistoryDetailActivity;
import com.alensic.nursing.mobile.ui.SettingActivity;
import com.alensic.nursing.mobile.ui.TemperatureActivity;
import com.alensic.nursing.mobile.ui.UploadActivity;
import com.alensic.nursing.mobile.ui.UploadHistoryActivity;
import com.alensic.nursing.mobile.ui.titlebar.TitleBarView;

/**
 * 上传管理界面基类，有titlebar和navigatebar
 * @author xwlian
 *
 */
public abstract class BaseUploadActivity extends Activity {

	protected View navHistory;
	protected View navUpload;

	protected Button btnNext;
	protected Button btnBack;
	protected TitleBarView titleBar;
	protected TextView tvTitle;
	protected Class upLevelActivity=TemperatureActivity.class; //按返回时默认的返回activity
	
	public final static int ACTIVITY_UPLOAD = 0;
	public final static int ACTIVITY_HISTORY = 1;
	
	

	
	/**
	 * 初始化导航栏
	 * @param ctx 
	 * @param curActivityNo 上传历史=1,待上传数据=0
	 */
	protected void initNav(final Context ctx,int curActivityNo){
		navUpload = (View)findViewById(R.id.navUpload);
		navHistory = (View)findViewById(R.id.navHistory);
		View arr[]=new View[]{navUpload,navHistory};
		arr[curActivityNo].setBackgroundResource(R.drawable.ic_tab_bg_selected);
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
		if(curActivityNo !=ACTIVITY_HISTORY){
			navHistory.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							startActivity(new Intent(ctx, UploadHistoryActivity.class));
							((Activity)ctx).finish();
						}
					});
		}
	}	
	

	/**
	 * 
	 * 初始化titlebar
	 * @param ctx 
	 * @param curActivityNo 上传历史=1,待上传数据=0
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
