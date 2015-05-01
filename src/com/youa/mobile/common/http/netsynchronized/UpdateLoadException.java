package com.youa.mobile.common.http.netsynchronized;

public class UpdateLoadException extends RuntimeException {

	private int mReason;
	public UpdateLoadException(int reason) {
		super(String.valueOf(reason));
		mReason = reason;
	}
	
	public int getReason() {
		return mReason;
	}
}
