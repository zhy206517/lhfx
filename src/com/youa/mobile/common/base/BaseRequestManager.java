package com.youa.mobile.common.base;

import com.youa.mobile.common.http.HttpManager;

public class BaseRequestManager {


	private HttpManager mHttpManager = HttpManager.getInstance();
	
	public HttpManager getHttpManager() {
		return mHttpManager;
	}
	
}
