package com.youa.mobile.login.auth;


import android.content.Context;
import android.os.Build;

import com.youa.mobile.common.manager.NetworkStatus;

public final class QQToken extends BaseToken{
/**
	 * 
	 */
	private static final long serialVersionUID = -9038418643903859594L;
	//	private static final String app_Key = "801099957";          
//	private static final String app_Secret = "f24fbb85f74b2130d0a7b8f05e73eb9d";
	public static final String app_Key = "100248015";          
	//private static final String app_Secret = "2ff4576df5e9ffa6795f7e63793d88d6";
	public static final String SNSTYPE_KEY = "tencent";
	private static String scope = "get_user_info,get_user_profile,add_share,add_topic,list_album,upload_pic,add_album";//授权范围
	public static final String CALLBACK_HOST = "auth.qq.com";
	public static QQToken instance;
	//public static final String URL_CALLBACK = "lehosns://auth.qq.com";
	
	public synchronized static QQToken getInstance(Context c){
		if(null == instance){
			instance = new QQToken(c);
		}
		instance.initToken(c);
		return instance;
	}
	
	
	public QQToken(Context c) {
		super(c);
		this.tokenKey = "QQ_TOKEN";
		this.secretKey = "QQ_TOKEN_SECRET";
		this.useridKey = "QQ_USER_ID";
		this.expTimeKey = "QQ_EXPTIME";
		this.modifyTimeKey = "QQ_MODIFY_TIME";
		this.isSyncKey = "QQ_SYNC_KEY";
	}

	@Override
	public void getAuthToken(BaseAuthPage c) {
		String mGraphURL = ("https://graph.qq.com/oauth2.0/authorize?response_type=token&display=mobile&client_id=%s&scope=%s&redirect_uri=%s&status_userip=%s&status_os=%s&status_machine=%s&status_version=%s#" + System.currentTimeMillis());
		String url = String.format(mGraphURL, new Object[] { app_Key, scope, URL_CALLBACK_SCHEME + CALLBACK_HOST, NetworkStatus.getLocalIpAddress(), Build.VERSION.RELEASE, Build.MODEL, Build.VERSION.SDK });
		c.showAuthPage(url);
	}
}
