package com.youa.mobile.login.manager;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseRequestManager;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.http.HttpRes;
import com.youa.mobile.common.manager.NetworkStatus;
import com.youa.mobile.login.util.LoginConstant;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;

public class LoginHttpRequestManager extends BaseRequestManager {

	public JsonObject login(Context context, String username, String password) throws MessageException {
		Map<String, String > params = new HashMap<String, String>();
//		addPublicParams(params);
		params.put(LoginConstant.WEB_LOGIN_USERNAME, username);
		params.put(LoginConstant.WEB_LOGIN_PASSWORD, password);
//		params.put(LoginConstant.WEB__METHOD, LoginConstant.WEB_LOGIN_METHOD);
		HttpRes res = getHttpManager().post(LoginConstant.WEB_LOGIN_METHOD, params, context);
		return res.getJSONObject();
	}

	public JsonObject regist(Context context, String phoneNumble, String vcode, String password, String nickname, String sex, String province, String city, String resion) throws MessageException {
		Map<String, String > params = new HashMap<String, String>();
//		addPublicParams(params);
		params.put(LoginConstant.WEB_REGIST_MOBILEPHONE, phoneNumble);
		params.put(LoginConstant.WEB_REGIST_VCODE, vcode);
		params.put(LoginConstant.WEB_REGIST_PASSWORD, password);
		params.put(LoginConstant.WEB_REGIST_NICKNAME, nickname);
		params.put(LoginConstant.WEB_REGIST_SEX, sex);
		params.put(LoginConstant.WEB_REGIST_PROVINCE, province);
		params.put(LoginConstant.WEB_REGIST_CITY, city);
		params.put(LoginConstant.WEB_REGIST_RESION, resion);
		JsonObject result = getHttpManager().post(LoginConstant.WEB_MOBILE_REGIST_METHOD, params, context).getJSONObject();
		return result;
	}

//	private void addPublicParams(Map<String, String > params) {
//		params.put(LoginConstant.WEB_API_KEY, "1");
//		params.put(LoginConstant.WEB_FROM, "JT");
//		params.put(LoginConstant.WEB_V, "1.0");
//		params.put(LoginConstant.WEB_IE, "UTF-8");
//		params.put(LoginConstant.WEB_FORMAT, "JSON");
//	}
	/**
	 * 获取短信验证码
	 * @param context
	 * @param mobilNumber
	 * @return
	 * @throws MessageException
	 */
	public JsonObject getMmsCode(Context context,String mobilNumber) throws MessageException{
		Map<String, String > params = new HashMap<String, String>();
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(mobilNumber);
		String str = jsonArray.toJsonString();
		params.put(LoginConstant.WEB_REGIST_PHONENUMBLE, str);
		HttpRes res = getHttpManager().post("passport.genVcodeBySMS", params, context);
		return res.getJSONObject();
	}
	
	public JsonObject getUserInfoByThirdToken(Context context, String site, String accessToken) throws MessageException{
		Map<String, String > params = new HashMap<String, String>();
//		params.put(LoginConstant.THIRD_REGIST_SITE, site);
//		params.put(LoginConstant.THIRD_REGIST_ACCESSTOKEN, accessToken);
		JsonArray array = new JsonArray();
		array.add(site);
		array.add(accessToken);
		params.put("rpcinput", array.toJsonString());
		JsonObject userInfoObj = getHttpManager().post(LoginConstant.THIRD_REGIST_GETUSERID_METHOD, params, context).getJSONObject();
		String thirdUserId = userInfoObj.toJsonString();
		System.out.println(thirdUserId);
		return  userInfoObj;
	}
	
	public JsonObject checkcheckFirstLogin(Context context, String thirdType, String userid) throws MessageException{
		Map<String, String > params = new HashMap<String, String>();
		JsonArray array = new JsonArray();
		array.add(userid);
		array.add(thirdType);
		params.put("rpcinput", array.toJsonString());
		JsonObject resultObj = getHttpManager().post(LoginConstant.THIRD_REGIST_CHECKFIRST_LOGIN_METHOD, params, context).getJSONObject();
		return resultObj;
	}
	
	public JsonObject regThirdUser(Context context,String thirdType, String userid, String username, String sex, String province, String city, String district) throws MessageException{
		//$inputs: username, third_type, regip, thirdid  必须提供 third_type: 
		Map<String, String > params = new HashMap<String, String>();
		JsonArray array = new JsonArray();
		JsonObject inputs = new JsonObject();
		inputs.put("username", username);
		inputs.put("sex", sex);
		inputs.put("province", province);
		inputs.put("city", city);
		inputs.put("district", district);
		inputs.put("third_type", thirdType);
		String localIp = NetworkStatus.getLocalIpAddress();
		inputs.put("regip", localIp != null ? localIp : "");
		
		inputs.put("thirdid", userid);
		array.add(inputs);
		array.add("false");
		params.put("rpcinput", array.toJsonString());
		JsonObject resultObj = getHttpManager().post(LoginConstant.THIRD_REGIST_REGTHIRD_METHOD, params, context).getJSONObject();
		System.out.println(resultObj.toJsonString());
		return resultObj;
	}
	/**
	 * 第三方用户登录
	 * @param context
	 * @param siteType
	 * @param thirduserid
	 * @return
	 * @throws MessageException
	 */
	public JsonObject loginThirdUser(Context context, String siteType, String thirduserid, String accessToken, String refreshToken, String expTime) throws MessageException {
		Map<String, String > params = new HashMap<String, String>();
		JsonArray array = new JsonArray();
		array.add(thirduserid);
		array.add(siteType);
		String localIp = NetworkStatus.getLocalIpAddress();
		array.add(localIp);
		array.add(accessToken);
		array.add(refreshToken);
		array.add(expTime);
		params.put("rpcinput", array.toJsonString());
		JsonObject resultObj = getHttpManager().post(LoginConstant.THIRD_LOGIN_REGTHIRD_METHOD, params, context).getJSONObject();
		//getLehoUserSession(context, lehoUid);
		return resultObj;
	}
	/**
	 * 根据leho的uid 获取Session等信息
	 * @param uid
	 * @return
	 */
//	private JsonObject getLehoUserSession(Context context, String uid) throws MessageException{
//		Map<String, String > params = new HashMap<String, String>();
//		params.put("uid", uid);
//		JsonObject resultObj = getHttpManager().post(LoginConstant.LOGIN_GET_USERSESSION_METHOD, params, context).getJSONObject();
//		return resultObj;
//	}
	//
	
	
//	
//	public JsonObject checkThirdAccountBindStatus(Context context, String lehoUid) throws MessageException {
//		Map<String, String > params = new HashMap<String, String>();
//		JsonArray array = new JsonArray();
//		array.add(lehoUid);
//		params.put("rpcinput", array.toJsonString());
//		if(!LoginConstant.THIRD_BIND_CHECKSTATUS_METHOD.equals("")){
//			JsonObject resultObj = getHttpManager().post(LoginConstant.THIRD_BIND_CHECKSTATUS_METHOD, params, context).getJSONObject();
//			return resultObj;
//		}
//		return null;
//	}
//	
	
//	public JsonObject bindThird(Context context, String userid, String thirduserid, String siteType) throws MessageException {
//		Map<String, String > params = new HashMap<String, String>();
//		JsonArray array = new JsonArray();
//		array.add(userid);
//		array.add(thirduserid);
//		array.add(siteType);
//		params.put("rpcinput", array.toJsonString());
//		JsonObject resultObj = getHttpManager().post(LoginConstant.THIRD_BIND_BIND_METHOD, params, context).getJSONObject();
//		return resultObj;
//	}
//	
//
//	
//	public JsonObject unbindThird(Context context, String userid, String thirduserid, String siteType) throws MessageException {
//		Map<String, String > params = new HashMap<String, String>();
//		JsonArray array = new JsonArray();
//		array.add(userid);
//		array.add(thirduserid);
//		array.add(siteType);
//		params.put("rpcinput", array.toJsonString());
//		JsonObject resultObj = getHttpManager().post(LoginConstant.THIRD_BIND_UNBIND_METHOD, params, context).getJSONObject();
//		return resultObj;
//	}
}
