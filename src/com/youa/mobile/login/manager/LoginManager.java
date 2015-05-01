package com.youa.mobile.login.manager;

import android.content.Context;
import android.text.TextUtils;

import com.youa.mobile.common.base.BaseManager;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.manager.LoginCommonDataStore;
import com.youa.mobile.login.auth.SupportSite;
import com.youa.mobile.login.data.UserInfo;
import com.youa.mobile.login.util.LoginUtil;
import com.youa.mobile.parser.JsonObject;

public class LoginManager extends BaseManager {

	private final static String TAG = "LoginManager";

	private LoginHttpRequestManager mLoginHttpRequestManager;

	public LoginHttpRequestManager getLoginHttpRequestManager() {
		if (mLoginHttpRequestManager == null) {
			mLoginHttpRequestManager = new LoginHttpRequestManager();
		}
		return mLoginHttpRequestManager;
	}

	public void login(Context context, String username, String password) throws MessageException {
//		LoginCommonDataStore.clearLoginData(context, username);
		JsonObject object = getLoginHttpRequestManager().login(context, username, password);
		LoginUtil.LOGD(TAG, "enter login   <JsonObject> : " + object.toString() );
		LoginUtil.LOGD(TAG, "enter login   <writeUserToPrefForAutoLogin> username=" + username + " password=" + "password");
		JsonObject dataObject = object.getJsonObject("data");
		String sessionID = dataObject.getString("youasession");
		String youaindentity = dataObject.getString("youaidentity");
		String uid = dataObject.getString("userid");
		
		LoginUtil.writeUserToPrefForAutoLogin(context, username, password);
		
		LoginCommonDataStore.saveDataIfNeed(
				context, 
				LoginCommonDataStore.KEY_SESSIONID, 
				"", 
				sessionID);
		LoginCommonDataStore.saveDataIfNeed(
				context, 
				LoginCommonDataStore.KEY_USERID, 
				"", 
				uid);
		LoginCommonDataStore.saveDataIfNeed(
				context, 
				LoginCommonDataStore.KEY_YOUAINDENTITY, 
				"", 
				youaindentity);
		LoginUtil.writeLoginFromType(context, null);
	}

//	public boolean autoLogin(Context context) throws MessageException {
//		UserInfo userInfo = LoginUtil.readUserFromPrefForAutoLogin(context);
//		if (userInfo != null) {
//			SupportSite site = LoginUtil.readLoginFromType(context);
//			if(site == null){
//				String username = userInfo.getUsername();
//				String password = userInfo.getPassword();
//				login(context, username, password);
//			}else{
//				String thirdUid = userInfo.getThirdUid();
//				site = userInfo.getSite();
//				loginThirdUser(context, site, thirdUid);
//			}
//			
//			return true;
//		} else {
//			return false;
//		}
//	}
	
	public boolean isHasUser(Context context) {
		UserInfo userInfo = LoginUtil.readUserFromPrefForAutoLogin(context);
		if (userInfo != null) {
			System.out.println(" userInfo : " + userInfo.getUsername());
		}
		return userInfo != null && userInfo.getYouaSession() != null && !"".equals(userInfo.getYouaSession());
	}
	
	/**
	 * 
	 * 以下为注册功能调用。 
	 *
	 */
	
	public JsonObject regist(Context context, String phoneNumble, String vcode, String password, String nickname, String sex, String province, String city, String resion) throws MessageException {
		return getLoginHttpRequestManager().regist(context, phoneNumble, vcode, password, nickname, sex, province, city, resion);
	}
	
//	public UserInfo getUserInfoByThirdToken(Context context, String site, String accessToken) throws MessageException{
//		JsonObject userJsonObj = getLoginHttpRequestManager().getUserInfoByThirdToken(context, site, accessToken);
//		return parseUserInfo(userJsonObj);
//	}
	
//	private UserInfo parseUserInfo(JsonObject userJsonObj){
//		return new UserInfo(userJsonObj);
//	}
	
	public JsonObject checkcheckFirstLogin(Context context, SupportSite site, String userid) throws MessageException{
		String thirdType = LoginUtil.parseSiteType(site.getSiteTag());
		return getLoginHttpRequestManager().checkcheckFirstLogin(context, thirdType, userid);
	}
	
	public void regThirdUser(Context context, SupportSite site, String userid, String username, String sex, String province, String city, String district) throws MessageException{
		String thirdType = LoginUtil.parseSiteType(site.getSiteTag());
		JsonObject resultObject = getLoginHttpRequestManager().regThirdUser(context, thirdType, userid, username, sex, province, city, district);
		
		thridLoginStoreData(context, site, userid, resultObject, true);
	}
	
	public void loginThirdUser(Context context, SupportSite site, String thirduserid, String accessToken, String refreshToken, String expTime) throws MessageException{
		String thirdType = LoginUtil.parseSiteType(site.getSiteTag());
		JsonObject resultObject = getLoginHttpRequestManager().loginThirdUser(context, thirdType, thirduserid, accessToken, refreshToken, expTime);
		thridLoginStoreData(context, site, thirduserid, resultObject, true);
	}
	
	public void reLoginThirdUser(Context context, SupportSite site, String thirduserid, String accessToken, String refreshToken, String expTime) throws MessageException{
		String thirdType = LoginUtil.parseSiteType(site.getSiteTag());
		JsonObject resultObject = getLoginHttpRequestManager().loginThirdUser(context, thirdType, thirduserid, accessToken, refreshToken, expTime);
		thridLoginStoreData(context, site, thirduserid, resultObject, false);
	}
	
	/**
	 * 三方帐号登录后的数据保存
	 * @param context
	 * @param site
	 * @param thirduserid
	 * @param resultObject
	 */
	private void thridLoginStoreData(Context context, SupportSite site, String thirduserid, JsonObject resultObject, boolean override){
		LoginUtil.writeLoginFromType(context, site);
		LoginUtil.writeThirdUserToPrefForAutoLogin(context, thirduserid, site);
		JsonObject dataObject = resultObject.getJsonObject("data");
		String sessionID = dataObject.getString("youasession");
		String youaindentity = dataObject.getString("youaidentity");
		String uid = dataObject.getString("userid");
		
		String nickName = dataObject.getString("username");
		if(!TextUtils.isEmpty(sessionID) && !TextUtils.isEmpty(uid)){
			String loginedUid = ApplicationManager.getInstance().getUserId();
			boolean hasUser = !TextUtils.isEmpty(loginedUid);
			if(override && (hasUser ? uid.equals(loginedUid) : true)){
				LoginCommonDataStore.saveDataIfNeed(context, LoginCommonDataStore.KEY_SESSIONID, "", sessionID);
				LoginCommonDataStore.saveDataIfNeed(context, LoginCommonDataStore.KEY_USERID, "", uid);
				LoginCommonDataStore.saveDataIfNeed(context, LoginCommonDataStore.KEY_YOUAINDENTITY, "", youaindentity);
				LoginCommonDataStore.saveDataIfNeed(context, LoginCommonDataStore.KEY_NICKNAME, "", nickName);
			}
		}
	}
	
//	public void checkThirdAccountBindStatus(Context context) throws MessageException{
//		String uid = ApplicationManager.getInstance().getUserId();
//		JsonObject resultObject = getLoginHttpRequestManager().checkThirdAccountBindStatus(context, uid);
//	}
}


