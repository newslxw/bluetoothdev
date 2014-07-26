package com.alensic.nursing.mobile.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alensic.nursing.mobile.R;
import com.alensic.nursing.mobile.model.Bed;
import com.alensic.nursing.mobile.model.BedTemperature;
import com.alensic.nursing.mobile.util.ActivityUtils;
import com.alensic.nursing.mobile.util.DateUtil;
import com.alensic.nursing.mobile.common.ConfirmDialogEvent;
import com.alensic.nursing.mobile.common.Session;
import com.alensic.nursing.mobile.dao.TemperatureDao;
import com.alensic.nursing.mobile.dao.TemperatureDaoImpl;
import com.alensic.nursing.mobile.util.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class LvCTAdapter extends BaseAdapter { 
	private TemperatureDao temperatureDao = null; 
    private class buttonViewHolder { 
        ImageView appIcon; 
        TextView appName; 
        TextView temperature; 
        TextView inDate; 
        ImageButton buttonDel; 
        ImageButton buttonSave;
        
    } 
    
    private List < Bed > mAppList; 
    private LayoutInflater mInflater; 
    private Context mContext; 
    private boolean isHistoryDetail=false;

  //alertdialog 被选中初始化false  
    private static boolean[] isFocused ;  
    private static int whichClick = -1; 
    
  /*  private String [ ] keyString; 
    private int [ ] valueViewID; */
    private buttonViewHolder holder; 
    
    public LvCTAdapter( Context c, List < Bed > appList, int resource, Bed seledBed) { 
    	temperatureDao = new TemperatureDaoImpl(c); 
        mAppList = appList; 
        mContext = c; 
        whichClick = -1;
        isHistoryDetail = false;
        mInflater = ( LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE) ; 
        isFocused = new boolean[appList.size()];  
        for(int i=0;i<appList.size();i++){  
        	isFocused[i] = false;  
            if(seledBed!=null&&seledBed.getId()==appList.get(i).getId()){
            	whichClick = i;
            	isFocused[i] = true;
            }
        } 
    } 
    
    /**
     * 
     * @param c
     * @param appList
     * @param resource
     * @param activityType  用于上传历史明细
     */
    public LvCTAdapter( Context c, List < Bed > appList, int resource,String activityType) { 
    	temperatureDao = new TemperatureDaoImpl(c); 
        mAppList = appList; 
        mContext = c; 
        whichClick = -1;
        isHistoryDetail = true;
        mInflater = ( LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE) ; 
    } 
    
    @Override 
    public int getCount ( ) { 
        return mAppList.size ( ) ; 
    } 

    @Override 
    public Object getItem ( int position ) { 
        return mAppList.get ( position ) ; 
    } 

    @Override 
    public long getItemId( int position ) { 
        return position ; 
    } 

    public void removeItem ( final int position ) { 
    	Builder builder = ActivityUtils.confirmDialog(this.mContext, new ConfirmDialogEvent("","确定要删除吗?"){
			@Override
			public void onOK(Context ctx,DialogInterface dialog, int whichButton) {
				Bed appInfo = mAppList.get ( position ) ; 
		        if ( appInfo != null ) {
		        	BedTemperature bt = appInfo.getTemperature();
		        	if(bt != null && bt.getId()!=null){
		        		temperatureDao.delete(bt.getId());
		        	}
		        	if(appInfo.getTemperatureBak()!=null&&appInfo.getTemperatureBak().getId()!=null){
		        		//删除还没保存的温度值，恢复数据库值
		        		appInfo.setTemperature(appInfo.getTemperatureBak());
		        		appInfo.setTemperatureBak(null);
		        	}else{//删除数据库值
		        		appInfo.setTemperature(null);
		        	}
		        }
				//mAppList.remove ( position ) ; 
		        int tCount = 0;
				for(int i=0; i<LvCTAdapter.this.getCount(); i++){
					Bed temp = (Bed)LvCTAdapter.this.getItem(i);
					if(temp.getTemperature()!=null&&temp.getTemperature().getId()!=null&&temp.getTemperature().getId()!=0) tCount++;
				}
				((TemperatureActivity)mContext).updateTitle(tCount,LvCTAdapter.this.getCount());
				
				notifyDataSetChanged( ) ;
			}
    		
    	});
    	ActivityUtils.show(builder);
    } 
    
    /**
     * 更新温度
     * @param position
     * @param temperature
     */
    public void updateItem(int position, BedTemperature temperature){
    	Bed appInfo = mAppList.get ( position ) ; 
    	if(appInfo.getTemperatureBak() == null) appInfo.setTemperatureBak(appInfo.getTemperature());//备份原来的温度
    	appInfo.setTemperature(temperature);
    	notifyDataSetChanged( ) ;
    }
    
    @Override 
    public View getView ( int position , View convertView, ViewGroup parent ) { 
        if ( convertView != null ) { 
            holder = ( buttonViewHolder) convertView.getTag ( ) ; 
        } else { 
            convertView = mInflater.inflate ( R.layout.lvitem, null ) ; 
            holder = new buttonViewHolder( ) ;
            holder.appIcon = ( ImageView ) convertView.findViewById( R.id.ItemImage ) ; 
            holder.appName = ( TextView) convertView.findViewById( R.id.ItemWinName ) ; 
            holder.inDate = ( TextView) convertView.findViewById( R.id.inDate ) ; 
            holder.temperature = ( TextView) convertView.findViewById( R.id.itemT ) ; 
            holder.buttonDel = ( ImageButton) convertView.findViewById( R.id.btnDel ) ; 
            holder.buttonSave = ( ImageButton) convertView.findViewById(R.id.btnSave ) ; 
            convertView.setTag( holder) ; 
        } 
        
        Bed appInfo = mAppList.get ( position ) ; 
        BedTemperature bt = appInfo.getTemperature();
        if ( appInfo != null ) { 
            String aname = appInfo.getBedNo() ; 
            String ind = null ; 
            String temperature = null ; 
            if(bt != null){
    			ind = bt.getInDate();
    			if(ind != null) {ind = DateUtil.formatDBdate(ind); ind = ind.substring(5, ind.length()-3);}
    			temperature = bt.showTemperature();
    			
            }
            holder.appName.setText ( aname) ; 
            holder.inDate.setText ( ind) ; 
            holder.temperature.setText ( temperature) ; 
            holder.buttonDel.setOnClickListener( new LvButtonListener( position ) ) ; 
            holder.buttonSave.setOnClickListener( new LvButtonListener( position ) ) ; 
            
            if(isHistoryDetail){
            	//上传历史明细，不用按钮
            	holder.buttonDel.setVisibility(View.GONE);
	            holder.buttonSave.setVisibility(View.GONE);
            }else{
            	int visibility = StringUtils.isEmpty(temperature)? View.INVISIBLE:View.VISIBLE;
	            holder.buttonDel.setVisibility(visibility);
            	visibility = View.VISIBLE;
	            if(Session.isAutoSave()) {
	            	visibility = View.GONE;
	            }else if(appInfo.getTemperatureBak()==null){
	            	visibility = View.INVISIBLE;
	            }
	            holder.buttonSave.setVisibility(visibility);
            }
        }  
        if(!isHistoryDetail)
        	convertView.setBackgroundResource(isFocused[position]?R.color.bg_bed_list_item_selected:R.color.transparent);  
        return convertView; 
    } 

    class LvButtonListener implements OnClickListener { 
        private int position ; 

        LvButtonListener( int pos) { 
            position = pos; 
        } 
        
        @Override 
        public void onClick( View v) { 
            int vid= v.getId ( ) ; 
            if ( vid == holder.buttonDel.getId ( ) ) 
                removeItem ( position ) ; 
            else{
            	((TemperatureActivity)mContext).saveTemperature();
            }
        } 
    } 
    
    public void changeBg(int position){  
        isFocused[whichClick==-1?0:whichClick] = false;  
        whichClick = position;  
        isFocused[position] = true;  
        notifyDataSetChanged();  
}

	public static int getWhichClick() {
		return whichClick;
	}

    
    
}
