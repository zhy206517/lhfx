package com.youa.mobile.login.auth;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.renren.api.connect.android.Renren;
import com.renren.api.connect.android.Util;

public class RenrenToken extends BaseToken{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7162987028942314294L;
	public static final String API_KEY = "f723afe0ec964dff8208cfb5039ab9a7"; //leho : f723afe0ec964dff8208cfb5039ab9a7 youa :041920af54074107969d8967dcb9742f 
	public static final String SECRET_KEY = "2429b5e01f45449e954a067419d03299"; // leho : 2429b5e01f45449e954a067419d03299  youa: ead8af025ad742d484bcc65d09421a6f
	public static final String APP_ID = "180358";
	public static final String SNSTYPE_KEY = "renren";
	public static final String CALLBACK_HOST = "renren.com";
	private static final String[] DEFAULT_PERMISSIONS = { "publish_feed", "create_album", "photo_upload", "read_user_album", "status_update" };
	public static final String SESSION_KEY_URL = "http://graph.renren.com/renren_api/session_key";
	public static RenrenToken instance;
	
	public synchronized static RenrenToken getInstance(Context c){
		if(null == instance){
			instance = new RenrenToken(c);
		}
		instance.initToken(c);
		return instance;
	}
	
	public Renren getRenren(Context c){
		Renren renren = new Renren(API_KEY, SECRET_KEY, APP_ID, c);
		renren.init(c);
		return renren;
	}
	
	public RenrenToken(Context c) {
		super(c);
		this.tokenKey = "RENREN_TOKEN";
		this.secretKey = "RENREN_TOKEN_SECRET";
		this.useridKey = "RENREN_USER_ID";
		this.expTimeKey = "RENREN_EXPTIME";
		this.modifyTimeKey = "RENREN_MODIFY_TIME";
		this.isSyncKey = "RENREN_SYNC_KEY";
	}

	@Override
	public void getAuthToken(BaseAuthPage c) {
		Bundle params = new Bundle();
		params.putString("client_id", API_KEY);
		params.putString("redirect_uri", Renren.DEFAULT_REDIRECT_URI/*URL_CALLBACK_SCHEME + CALLBACK_HOST*/);
		params.putString("response_type", "token");
		params.putString("display", "touch");
		String scope = TextUtils.join(" ", DEFAULT_PERMISSIONS);
		params.putString("scope", scope);
		String url = Renren.AUTHORIZE_URL + "?" + Util.encodeUrl(params) + "#" + System.currentTimeMillis();
		
		c.showAuthPage(url);
	}
}
