package com.alensic.nursing.mobile.exception;

/**
 * 数据库异常处理
 * @author xwlian
 *
 */
public class DBException extends BaseException {
	
	protected String errorCode =DB;
	protected String uiMsg ="数据库异常";//显示在界面上的信息
	protected String logMsg ="数据库异常" ;//日志中打印的信息
	
	public DBException() {
		super();
	}

	public DBException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public DBException(String detailMessage) {
		super(detailMessage);
	}

	public DBException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param logMsg  日志消息
	 * @param uiMsg 显示在界面上的消息
	 * @param throwable
	 */
	public DBException(String logMsg,String uiMsg,Throwable throwable) {
		super(logMsg, throwable);
		this.logMsg = logMsg;
		this.uiMsg = uiMsg;
	}
	
	
}
