package com.alensic.nursing.mobile.exception;

/**
 * 只允许通过  BaseException(String errorCode,String logMsg,String uIMsg,Throwable throwable)创建对象
 * @author xwlian
 *
 */
public class BaseException extends RuntimeException implements ExpCode {

	protected String errorCode =UNDEFINED;
	protected String uIMsg ="";//显示在界面上的信息
	protected String logMsg ="" ;//日志中打印的信息
	
	
	protected BaseException() {
		super();
	}

	protected BaseException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		uIMsg = detailMessage;
	}

	protected BaseException(String detailMessage) {
		super(detailMessage);
		uIMsg = detailMessage;
	}
	
	/**
	 * 
	 * @param errorCode 异常编码
	 * @param logMsg  日志消息
	 * @param uIMsg 显示在界面上的消息
	 * @param throwable
	 */
	public BaseException(String errorCode,String logMsg,String uIMsg,Throwable throwable) {
		super(logMsg, throwable);
		this.logMsg = logMsg;
		this.uIMsg = uIMsg;
		this.errorCode = errorCode;
	}
	
	protected BaseException(Throwable throwable) {
		super(throwable);
		logMsg = uIMsg = throwable.getMessage();
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getUIMsg() {
		return uIMsg;
	}

	public void setUIMsg(String uIMsg) {
		this.uIMsg = uIMsg;
	}

	public String getLogMsg() {
		return logMsg;
	}

	public void setLogMsg(String logMsg) {
		this.logMsg = logMsg;
	}

	@Override
	public String toString() {
		return this.getClass().getName()+" [errorCode=" + errorCode + ", uIMsg=" + uIMsg
				+ ", logMsg=" + logMsg + "]";
	}

	@Override
	public void printStackTrace() {
		// TODO Auto-generated method stub
		super.printStackTrace();
	}
	
	

	
	
}
