package com.alensic.nursing.mobile.ui.blood;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

import android.util.Log;

import com.alensic.nursing.mobile.model.Blood;
import com.alensic.nursing.mobile.ui.bluebooth.BlockIOThread;
import com.alensic.nursing.mobile.ui.bluebooth.BluetoothService;
import com.alensic.nursing.mobile.ui.bluebooth.CodeFormat;
import com.alensic.nursing.mobile.ui.bluebooth.ConnectThread;
import com.alensic.nursing.mobile.util.StreamUtils;

/**
 * 蓝牙socket的IO操作基类，由ConnectThread调用
 * @author xwlian
 *
 */
public class BloodBlockIOThread extends BlockIOThread{

	private static byte[] bloodKeyBytes = new byte[]{0x17,0x01,0x0C,0x00,0x00,0x00,0x2F,0x01,0x1A,0x00,
		0x17,0x0B,0x00,0x00,0x00,0x00,0x1C,0x00,0x00,0x00,0x00,0x15,0x00,0x17,0x10,0x00,0x00,0x0F,0x00,0x64,0x40,0x00,
		0x08,0x00,0x00,0x00,0x01,0x00,0x00,0x00,0x00,-0x23,-0x03,0x47,0x76,0x3B,-0x33};
	private int blockSize=586;
	private byte startFlag = 0x17;
	
	/*private static byte[] bloodKeyBytes = new byte[]{0x17,0x01,0x0C,0x00,0x00,0x00,0x2F,0x01,0x1A,0x00,0x17,0x0B,0x00,0x00,0x00,0x00,0x1C,0x00,0x00,0x00,0x00,0x15,
		0x00,0x17,0x10,0x00,0x00,0x0F,0x00,0x64,0x40,0x00,0x08,0x00,0x00,0x00,0x01,0x00,0x00,0x00,0x01,-0x34,0x74,-0x78,0x2A,-0x52,-0x16};
	*/
    private String TAG="BloodBlockIOThread";
    private Blood oldBlood = null;
	
	
    public BloodBlockIOThread(ConnectThread ct, long sleepTime, InputStream in,
			OutputStream out, int bufferSize,CountDownLatch countDownLatch) {
		super(ct, sleepTime, in, out, bufferSize,countDownLatch);
		this.setBufferSize(blockSize);
	}

    /**
     * 查询血压计最新数据
     * @param buffer 存放血压计数据
     * @return 读取的字节
     * @throws IOException 
     */
	@Override
	protected int receiveDeviceData(byte[] buffer) throws IOException{
    	int bytes;
		StreamUtils.sleepWithNoException(500);//必须加上这个sleep，否则无法发送数据成功，也无法接收数据，比较诡异，是在测试时加断点无意中发现的
		this.getMmOutStream().write(bloodKeyBytes);
		this.getMmOutStream().flush();
    	Log.e(TAG,"finish write byte");
        bytes = this.getMmInStream().read(buffer);
        if(buffer[0]==startFlag){
	        while(bytes<blockSize){//虽然长度不够，但是是从0x17开始，表示这个一个数据的开始，就一定要读到586个字节才OK
	        	bytes += this.getMmInStream().read(buffer,bytes,blockSize - bytes);
	        }
        }
    	return bytes;
    }
    
    /**
     * 处理血压计返回的数据
     * @param buffer
     * @param bytes
     */
	@Override
	protected void handleData(byte[] buffer,int bytes){
    	if(bytes<201){
        	Log.e(TAG,"读取数据错误 ，读取的长度是byte = "+bytes +" ");
        }
		/*String v = CodeFormat.bytesToHexStringTwo(buffer, bytes);
		String temp = "";
		for(int i=0; i<v.length(); i+=2){
			temp = temp + v.substring(i,i+2)+" ";
		}
		Log.e(TAG,temp);*/
		Blood blood = createBlood(buffer);
		if(blood.getDia()==0 || blood.getSys()==0 || blood.getMap()==0 || blood.getSys()==0){
			Log.e(TAG,"测试结果为0，没有数据，忽略");
		}else if(oldBlood != null && oldBlood.equals(blood)){
			Log.e(TAG,"测量数据："+blood.getDia()+"/"+blood.getSys()+"#"+blood.getMap()+"#"+blood.getMb()+"和上次的测试结果一样，视为相同的结果，忽略");
		}else{
			oldBlood = blood;
			Log.e(TAG,"新测量数据："+blood.getDia()+"/"+blood.getSys()+"#"+blood.getMap()+"#"+blood.getMb()+"");
			this.getConnectThread().obtainMessage(BluetoothService.MESSAGE_READ, 2, -1, blood);
		}
		
    }

    /**
     * 生成血压数据
     * @param buffer
     * @return
     */
    public Blood createBlood(byte[] buffer){
    	Blood blood = new Blood();
    	int sPos = 193;

        String strDia = null,strSys=null,strMap = null,strMb = null;
        byte[] tByte=new byte[2];
        int nV;
        BigDecimal bd;
		//读取数据
		tByte[0] = buffer[sPos++];
		tByte[1] = buffer[sPos++];
		strDia = CodeFormat.bytesToHexStringTwo(tByte, 2);
		
		tByte[0] = buffer[sPos++];
		tByte[1] = buffer[sPos++];
		strSys = CodeFormat.bytesToHexStringTwo(tByte, 2);
		
		tByte[0] = buffer[sPos++];
		tByte[1] = buffer[sPos++];
		strMap = CodeFormat.bytesToHexStringTwo(tByte, 2);
		
		tByte[0] = buffer[sPos++];
		tByte[1] = buffer[sPos++];
		strMb = CodeFormat.bytesToHexStringTwo(tByte, 2);
		
		nV = Integer.parseInt(strDia, 16);
		bd = new BigDecimal(nV);
		bd = bd.divide(new BigDecimal(100),0,BigDecimal.ROUND_HALF_UP);			
		blood.setDia(bd.intValue());
		
		nV = Integer.parseInt(strSys, 16);
		bd = new BigDecimal(nV);
		bd = bd.divide(new BigDecimal(100),0,BigDecimal.ROUND_HALF_UP);
		blood.setSys(bd.intValue());

		nV = Integer.parseInt(strMap, 16);
		bd = new BigDecimal(nV);
		bd = bd.divide(new BigDecimal(100),0,BigDecimal.ROUND_HALF_UP);
		
		blood.setMap(bd.intValue());
		nV = Integer.parseInt(strMb, 16);
		blood.setMb(nV);
		return blood;
    }


    
	




}
