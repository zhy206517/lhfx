package com.youa.mobile.login.action;

import java.util.Map;

import android.content.Context;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.login.action.ThirdAccountAction.ThirdResultListener;
import com.youa.mobile.login.auth.SupportSite;
import com.youa.mobile.login.manager.LoginManager;
import com.youa.mobile.login.util.LoginConstant;
import com.youa.mobile.parser.JsonObject;

public class ThirdAccountAction extends BaseAction<ThirdResultListener>{
	
	public static final String REQUEST_TYPE = "request_type";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String SITE_TYPE = "site_type";
	
	public static final String USERID = "userid";
	public static final String USERNIKENAME = "nickname";
	
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String EXP_TIME = "exp_time";
	
	public interface ThirdResultListener extends IResultListener, IFailListener {
		public void onStart();
		public void onFinish(int resourceID);
		public void onStartReg();
		public void onFail(int resourceID);
	}
	@Override
	protected void onExecute(Context context, Map<String, Object> params, ThirdResultListener resultListener) throws Exception {
		boolean hasListener = resultListener != null;
		if(hasListener)
			resultListener.onStart();
		RequestType requestType = (RequestType)params.get(REQUEST_TYPE);
		SupportSite site = (SupportSite)params.get(SITE_TYPE);
		String thirdUserid = (String)params.get(USERID);
		String accessToken = (String)params.get(ACCESS_TOKEN);
		String refreshToken = (String)params.get(REFRESH_TOKEN);
		String expTime = String.valueOf((Long)params.get(EXP_TIME));
		
		LoginManager lm = new LoginManager();
			try {
				switch (requestType) {
					case REG_USER_AND_LOGIN:
						String nickname = (String)params.get(USERNIKENAME);
						String sex = (String)params.get(LoginConstant.WEB_REGIST_SEX);
						String province = (String)params.get(LoginConstant.WEB_REGIST_PROVINCE);
						String city = (String)params.get(LoginConstant.WEB_REGIST_CITY);
						String district = (String)params.get(LoginConstant.WEB_REGIST_RESION);
						lm.regThirdUser(context, site, thirdUserid, nickname, sex, province, city, district);
						if(hasListener)
							resultListener.onFinish(R.string.regist_success);
						break;
					case CHECK_THIRDUSER_AND_LOGIN:
						JsonObject checkFirstLoginJson = lm.checkcheckFirstLogin(context, site, thirdUserid);
						if(checkFirstLoginJson != null){
							JsonObject checkUserObjdata = checkFirstLoginJson.getJsonObject("data");
							if(checkUserObjdata != null){
								String checkUserResult = checkUserObjdata.getString("rpcret");
								if((checkUserResult.equals("-1")) && hasListener){//-1为该thirduid 未在乐活注册过。
									resultListener.onStartReg();
								}else{
									lm.loginThirdUser(context, site, thirdUserid, accessToken, refreshToken, expTime);
									if(hasListener)
										resultListener.onFinish(R.string.login_wait);
								}
							}
						}
						break;
					/*
					case UPDATE_ACCESS_TOKEN:
							SyncSettingManager ssm 	= new SyncSettingManager();
							List<SyncThirdData> datas = ssm.getUserThirdSyncList(context);
							boolean hasBind = false;
							if(datas != null){
								for(SyncThirdData data : datas){
									if(data.getSite() == site && data.getThirdUid().equals(thirdUserid)){
										hasBind = true;
										break;
									}
								}
							}
							
							if(hasBind){
								lm.reLoginThirdUser(context.getApplicationContext(), site, thirdUserid, accessToken, refreshToken, expTime);
							}else{
								ssm.bindThird(context.getApplicationContext(), thirdUserid, site, accessToken, refreshToken, expTime);
							}
							if(hasListener)
								resultListener.onFinish(R.string.login_wait);
						
						break;
						*/
				}
			} catch (MessageException e) {
				String errCode = e.getErrCode();
				if(errCode == null){
				}else if(errCode.startsWith("passport.u_login.unauthed with ")){
					e.setResID(R.string.login_third_fail_unauthed);
				}else if(errCode.equals("passport.u_reglocal.uname.used")){
					e.setResID(R.string.regist_nickname_used);
				}else if(errCode.equals("passport.u_reglocal.third.used")){
					e.setResID(R.string.login_third_reglocal_third_used);
				}else if(errCode.equals("snsapi.passport.regthird_failed") || errCode.contains("u_input") ){
					e.setResID(R.string.login_third_reg_uname_error);
				}else if(errCode.contains("third.conflict")){//passport.u_relateToLocal.third.conflict.userid.210063206.third_key.1645447780  第三方帐号绑定冲突
					e.setResID(R.string.third_bind_conflict);
				}else if(errCode.contains("user.has.binded.one")){
					e.setResID(R.string.user_has_binded_one);
				}else if(errCode.contains("account.not.existed")){
					e.setResID(R.string.account_not_existed);
				}else if(errCode.contains("user.donot.has.the.third")){
					e.setResID(R.string.user_donot_has_the_third);
				}else if(errCode.contains("user has no local account")){
					e.setResID(R.string.user_has_no_local_account);
				}
				throw e;
			}
	}
	
	public enum RequestType {
		REG_USER_AND_LOGIN, CHECK_THIRDUSER_AND_LOGIN
	}
}
