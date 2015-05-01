package com.youa.mobile.login.auth;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.youa.mobile.common.manager.ApplicationManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public abstract class BaseToken implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3598980773924935435L;
	//private String TAG = this.getClass().getSimpleName();
	public static final String SNSTYPE = "sns_type";
	public static final String URL_CALLBACK_SCHEME= "lehosns://";
	public String token;
	public String tokenSecret;
	public String userid;

	protected String useridKey;
	protected String tokenKey;
	protected String secretKey;
	protected String expTimeKey;
	protected String modifyTimeKey;
	protected String isSyncKey;
	
	public String flag;
	public String status;
	public long expTime;
	public SupportSite site;
	public String reFreshToken;
	public long modifyTime;
	public boolean isSync = true;
	
	private static final String OAUTH_TOKEN_SETTINGS = "oauth_token_settings";
	
	public BaseToken(Context c) {
		
	}
	
	public void saveToken(Context c){
		String uid = ApplicationManager.getInstance().getUserId();
		if(!TextUtils.isEmpty(uid)){
			SharedPreferences snsSettings = c.getSharedPreferences(OAUTH_TOKEN_SETTINGS, Context.MODE_PRIVATE); 
			Editor e = snsSettings.edit();
			e.putString(tokenKey+uid, token);
			if(tokenSecret!=null && !"".equals(tokenSecret)){
				e.putString(secretKey+uid, tokenSecret);
			}
			e.putString(useridKey+uid, userid);
			e.putLong(expTimeKey+uid, expTime);
			e.putLong(modifyTimeKey+uid, modifyTime);
			e.putBoolean(isSyncKey+uid, isSync);
			e.commit();
		}
	}
	
	public void clearToken(Context c){
		String uid = ApplicationManager.getInstance().getUserId();
		SharedPreferences snsSettings = c.getSharedPreferences(OAUTH_TOKEN_SETTINGS, Context.MODE_PRIVATE); 
		Editor e = snsSettings.edit();
		e.remove(tokenKey+uid);
		e.remove(secretKey+uid);
		e.remove(useridKey+uid);
		e.remove(expTimeKey+uid);
		e.remove(modifyTimeKey+uid);
		e.remove(isSyncKey+uid);
		e.commit();
	}
	
	public boolean isBinded(Context c){
		return !TextUtils.isEmpty(token);
	}
	
	public void initToken(Context c){
		String uid = ApplicationManager.getInstance().getUserId();
		SharedPreferences snsSettings = c.getSharedPreferences(OAUTH_TOKEN_SETTINGS, Context.MODE_PRIVATE); 
		token 		= snsSettings.getString(tokenKey+uid, "");
		tokenSecret = snsSettings.getString(secretKey+uid, "");
		userid 		= snsSettings.getString(useridKey+uid, "");
		expTime 	= snsSettings.getLong(expTimeKey+uid, 0);
		modifyTime	= snsSettings.getLong(modifyTimeKey+uid, 0);
		isSync 		= snsSettings.getBoolean(isSyncKey+uid, false);
	}
	
	abstract void getAuthToken(BaseAuthPage c);
	
	public boolean isSessionValid(Context c) {
		return (!TextUtils.isEmpty(token) && !isExpired(c));
    }
	
	public boolean isExpired(Context c){
		return (expTime == 0 || (System
                .currentTimeMillis()/1000 > expTime - 60*60));
	}
	
	public static BaseToken getTokenInstanceBySupportSite(SupportSite site, Context c){
		BaseToken token;
		switch (site) {
		case QQ:
			token = QQToken.getInstance(c);
			break;
		case SINA:
			token = SinaToken.getInstance(c);
			break;
		case RENREN:
			token = RenrenToken.getInstance(c);
			break;
		default:
			token = null;
			break;
		}
		if(token != null)
			token.site = site;
		return token;
	}
	
	public static final List<BaseToken> getAllToken(Context c){
		List<BaseToken> list = null;
		for(SupportSite site : SupportSite.values()){
			if(list == null){
				list = new ArrayList<BaseToken>();
			}
			BaseToken token = getTokenInstanceBySupportSite(site, c);
			//if(token != null){
			list.add(token);
			//}
		}
		return list;
	}
}
