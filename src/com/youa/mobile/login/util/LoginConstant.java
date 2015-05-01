package com.youa.mobile.login.util;

public class LoginConstant {

	//web login params key
	public static final String WEB_LOGIN_USERNAME = "uname";
	public static final String WEB_LOGIN_PASSWORD = "password";
	public static final String WEB_LOGIN_CONFIRM_PASSWORD = "password_again";
	public static final String WEB_LOGIN_NICKNAME = "nickname";
	public static final String WEB_LOGIN_URL = "";
	public static final String WEB_REGIST_URL = "";
	public static final String WEB_LOGIN_METHOD = "_apilogin";
	public static final String WEB_REGIST_METHOD = "_apireg";
	public static final String WEB_MOBILE_REGIST_METHOD = "_mobilereg";
	//web public key
	public static final String WEB_API_KEY = "api_key";
	public static final String WEB_FROM = "from";
	public static final String WEB__METHOD = "method";
	public static final String WEB_FORMAT = "fromat";
	public static final String WEB_IE = "_ie";
	public static final String WEB_V = "v";
	//exception code
	public static final String LOGIN_SESSION_NOT_EXIST = "snsapi.sys_inter.session_not_exist";
	public static final String LOGIN_DB_QUERY_ERROR = "db.QueryError";
	public static final String LOGIN_NOT_MATCH = "mcphp.u_input";
	
	public static final String WEB_REGIST_PHONENUMBLE = "rpcinput";		//获取验证码时手机号参数名。
	public static final String WEB_REGIST_MOBILEPHONE = "mobilephone";	//注册时手机号参数名
	public static final String WEB_REGIST_PASSWORD = "loginpass";		//注册时密码参数名
	public static final String WEB_REGIST_NICKNAME = "username";		//注册时昵称参数名 
	public static final String WEB_REGIST_VCODE = "vcode";				//注册时验证码参数名
	public static final String WEB_REGIST_SEX = "sex";					//注册时性别参数名
	public static final String WEB_REGIST_PROVINCE = "province";		//注册时省参数名
	public static final String WEB_REGIST_CITY = "city";				//注册时城市参数名
	public static final String WEB_REGIST_RESION = "district";			//注册区县域参数名
	
	
	
	public static final String THIRD_REGIST_SITE = "site";				//$site 为baidu，qq，renren，sina等等
	public static final String THIRD_REGIST_ACCESSTOKEN = "token";		//$token oauth2协议中获取的access_token
	public static final String THIRD_REGIST_GETUSERID_METHOD = "oauth.getUserId";
	public static final String THIRD_REGIST_CHECKFIRST_LOGIN_METHOD = "_leho.checkFirstLoginNew";
	public static final String THIRD_REGIST_REGTHIRD_METHOD = "_leho.regThird";
	public static final String THIRD_LOGIN_REGTHIRD_METHOD = "_leho.loginThird";//passport.loginThird
	//public static final String LOGIN_GET_USERSESSION_METHOD = "_apithirdlogin";
	
	/**
	 * 解除三方绑定方法名
	 */
	public static final String THIRD_BIND_UNBIND_METHOD = "passport.unbindThird";
	/**
	 * 绑定三方帐号方法名
	 */
	public static final String THIRD_BIND_BIND_METHOD = "passport.bindThird";
	/**
	 * 查询绑定帐号列表
	 */
	public static final String THIRD_SYNC_GETBINGLIST = "jt:msg.getUserThirdSyncList";
	
}
