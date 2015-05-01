package com.youa.mobile.news.exception;

public class DatabaseOperateException extends Exception {

	private static final long serialVersionUID = 1272554405012052190L;

	public DatabaseOperateException() {
		super();
	}

	public DatabaseOperateException(String message) {
		super(message);
	}

	public DatabaseOperateException(Throwable cause) {
		super(cause);
	}

	public DatabaseOperateException(String message, Throwable cause) {
		super(message, cause);
	}

}
