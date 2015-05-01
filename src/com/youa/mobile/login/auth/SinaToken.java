package com.youa.mobile.login.auth;

import android.content.Context;

import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboParameters;

public class SinaToken extends BaseToken{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6624835717132363007L;
	//	public static final String CONSUMER_KEY = "2869955500";
//	public static final String CONSUMER_SECRET = "8445809261786b93e5d4c66944a61a38";
//	public static final String CONSUMER_KEY = "589750322";//leho:589750322 //youa :783182709
//	public static final String CONSUMER_SECRET = "625010f32da1d30d2646d7072143f27d";// leho : 625010f32da1d30d2646d7072143f27d youa : fe8143a971860828757ceef7be790cf1
	public static final String SNSTYPE_KEY = "sina";
	public static final String CALLBACK_URI = "http://i.leho.com/psp/third/login/sina/callback";
	private static SinaToken instance;
	
	public synchronized static SinaToken getInstance(Context c){
		if(null == instance){
			instance = new SinaToken(c);
		}
		instance.initToken(c);
		return instance;
		
	}
	
	public SinaToken(Context c) {
		super(c);
		this.tokenKey = "SINA_TOKEN";
		this.secretKey = "SINA_TOKEN_SECRET";
		this.useridKey = "SINA_USER_ID";
		this.expTimeKey = "SINA_EXPTIME";
		this.modifyTimeKey = "SINA_MODIFY_TIME";
		this.isSyncKey = "SINA_SYNC_KEY";
	}

	@Override
	public void getAuthToken(BaseAuthPage c) {
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("client_id", Weibo.getAppKey());
        parameters.add("response_type", "token");
        parameters.add("redirect_uri", CALLBACK_URI);
        parameters.add("display", "mobile");
        parameters.add("with_offical_account", "1");///加参数 with_offical_account ＝ 1 关注微博
        
        String url = Weibo.URL_OAUTH2_ACCESS_AUTHORIZE + "?" + Utility.encodeUrl(parameters);
        c.showAuthPage(url);
	}
}
