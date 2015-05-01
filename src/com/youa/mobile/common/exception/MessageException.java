package com.youa.mobile.common.exception;

public class MessageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 671885919652189971L;
	private String mErrCode;
	private int mResID = -1;
	//errcode直接是标识错误的String号,这样可以直接toast很方便
	public String getErrCode() {
		return mErrCode;
	}
	
	public int getResID() {
		return mResID;
	}
	
	public MessageException(String errCode) {
		super();
		mErrCode = errCode;
		mResID = -1;
	}
	
	public MessageException(String errCode, int resID) {
		super();
		mErrCode = errCode;
		mResID = resID;
	}
	
	public void setResID(int resID) {
		this.mResID = resID;
	}
}
