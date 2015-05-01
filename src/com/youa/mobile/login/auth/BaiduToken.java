package com.youa.mobile.login.auth;

import android.content.Context;

public class BaiduToken extends BaseToken{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6624835717132363017L;
	public final static String CANCEL_URL="http://openapi.baidu.com/oauth/2.0/oob?error=access_denied";
	public final static String ACCESS_URL = "https://openapi.baidu.com/oauth/2.0/authorize?response_type=token&redirect_uri=oob&display=mobile&client_id=";
	public final static String BAIDIRECT_URI = "http://openapi.baidu.com/oauth/2.0/login_success";
	public final static String BAIUSERINFO_URI ="https://openapi.baidu.com/rest/2.0/passport/users/getLoggedInUser";
	public static final String CALLBACK_HOST = "baidu.com";
	public static final String CONSUMER_KEY = "zCR6l9c77a8yFo5B5cPOo1KI";
	public static final String CONSUMER_SECRET = "GRxNnlr4Uacmx8npQsRbajvu9SFIdidC";
	public static BaiduToken instance;
	
	public synchronized static BaiduToken getInstance(Context c){
		if(null == instance){
			instance = new BaiduToken(c);
		}
		instance.initToken(c);
		return instance;
	}
	
	public BaiduToken(Context c) {
		super(c);
		this.tokenKey = "BAIDU_TOKEN";
		this.secretKey = "BAIDU_TOKEN_SECRET";
		this.useridKey = "BAIDU_USER_ID";
		this.modifyTimeKey = "BAIDU_MODIFY_TIME";
		this.isSyncKey = "BAIDU_SYNC_KEY";
	}

	@Override
	public void getAuthToken(BaseAuthPage c) {
		StringBuffer urlStr = new StringBuffer(ACCESS_URL);
		urlStr.append(CONSUMER_KEY);
		c.showAuthPage(urlStr.toString());
		}
	}
