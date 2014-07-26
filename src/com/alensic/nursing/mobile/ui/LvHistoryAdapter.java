package com.alensic.nursing.mobile.ui;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alensic.nursing.mobile.R;
import com.alensic.nursing.mobile.model.Bed;
import com.alensic.nursing.mobile.model.BedTemperature;
import com.alensic.nursing.mobile.model.UploadHistory;
import com.alensic.nursing.mobile.util.ActivityUtils;
import com.alensic.nursing.mobile.util.Constants;
import com.alensic.nursing.mobile.util.DateUtil;
import com.alensic.nursing.mobile.common.ConfirmDialogEvent;
import com.alensic.nursing.mobile.common.Session;
import com.alensic.nursing.mobile.common.SpinnerItem;
import com.alensic.nursing.mobile.dao.TemperatureDao;
import com.alensic.nursing.mobile.dao.TemperatureDaoImpl;
import com.alensic.nursing.mobile.exception.NursingIOException;
import com.alensic.nursing.mobile.util.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 上传历史
 * @author xwlian
 *
 */
public class LvHistoryAdapter extends BaseAdapter { 
	private TemperatureDao temperatureDao = null; 
    private class buttonViewHolder { 
        ImageView appIcon; 
        TextView appName; 
        Button btnReupload; 
        
    } 
    
    private List < UploadHistory > mAppList; 
    private LayoutInflater mInflater; 
    private Context mContext; 
    private LvHistoryReupload reuploadHanlder;

    
    private buttonViewHolder holder; 
    
    public LvHistoryAdapter( Context c, List < UploadHistory > appList, int resource,LvHistoryReupload reuploadHanlder) { 
    	temperatureDao = new TemperatureDaoImpl(c); 
        mAppList = appList; 
        mContext = c; 
        mInflater = ( LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE) ; 
        this.reuploadHanlder = reuploadHanlder;
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

    


	
    @Override 
    public View getView ( int position , View convertView, ViewGroup parent ) { 
        if ( convertView != null ) { 
            holder = ( buttonViewHolder) convertView.getTag ( ) ; 
        } else { 
            convertView = mInflater.inflate ( R.layout.lvitem_history, null ) ; 
            holder = new buttonViewHolder( ) ;
            holder.appIcon = ( ImageView ) convertView.findViewById( R.id.ItemImage ) ; 
            holder.appName = ( TextView) convertView.findViewById( R.id.ItemWinName ) ; 
            holder.btnReupload = ( Button) convertView.findViewById(R.id.btnReupload ) ; 
            convertView.setTag( holder) ; 
        } 
        
        UploadHistory appInfo = mAppList.get ( position ) ; 
        if ( appInfo != null ) { 
            String aname = appInfo.getUploadName();
            holder.appName.setText ( aname) ; 
            holder.btnReupload.setOnClickListener( new LvHistoryListener( position ) ) ; 
        }         
        return convertView; 
    } 

    class LvHistoryListener implements OnClickListener { 
        private int position ; 

        LvHistoryListener( int pos) { 
            position = pos; 
        } 
        
        @Override 
        public void onClick( View v) { 
            int vid= v.getId ( ) ; 
            if ( vid == holder.btnReupload.getId ( ) ) {
            	UploadHistory history =(UploadHistory) LvHistoryAdapter.this.getItem(position);
            	reuploadHanlder.reupload(history, position, LvHistoryAdapter.this); 
            }else{
            	//save
            }
        } 
    } 
}
