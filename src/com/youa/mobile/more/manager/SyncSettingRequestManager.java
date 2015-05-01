package com.youa.mobile.more.manager;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseRequestManager;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.login.util.LoginConstant;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;

public class SyncSettingRequestManager extends BaseRequestManager {
	
	/**
	 * 检查三方帐号绑定状态（功能实现，接口未调）
	 * @param context
	 * #@param lehoUid
	 * @return
	 * @throws MessageException
	 */
	public JsonObject getUserThirdSyncList(Context context) throws MessageException {
		Map<String, String > params = new HashMap<String, String>();
		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray array = new JsonArray();
		array.add(uid);
		params.put("rpcinput", array.toJsonString());
		JsonObject resultObj = getHttpManager().post(LoginConstant.THIRD_SYNC_GETBINGLIST, params, context).getJSONObject();
		return resultObj;
	}
	
	/**
	 * 第三方帐号绑定
	 * @param context
	 * @param userid
	 * @param thirdUid
	 * @param siteType
	 * @param accessToken
	 * @param refreshToken
	 * @param expTime
	 * @return
	 * @throws MessageException
	 * 服务器接口参数为unbindThird($userid, $third_key, $third_type) $userid -> 当前登录者的userid $third_key -> 为第三方用户ID $third_type -> 1--百度，2--新浪， 4--人人， 8--腾讯
	 */
	public JsonObject bindThird(Context context, String thirdUid, String siteType, String accessToken, String refreshToken, String expTime) throws MessageException{
		Map<String, String > params = new HashMap<String, String>();
		JsonArray array = new JsonArray();
		String uid = ApplicationManager.getInstance().getUserId();
		array.add(uid);
		array.add(thirdUid);
		array.add(siteType);
		array.add(accessToken);
		array.add(refreshToken);
		array.add(expTime);
		params.put("rpcinput", array.toJsonString());
		JsonObject resultObj = getHttpManager().post(LoginConstant.THIRD_BIND_BIND_METHOD, params, context).getJSONObject();
		return resultObj;
	}

	/**
	 * 解除第三方帐号绑定
	 * @param context
	 * @param userid
	 * @param thirdUid
	 * @param siteType
	 * @return
	 * @throws MessageException
	 * 服务器接口参数为unbindThird($userid, $third_key, $third_type) $userid -> 当前登录者的userid $third_key -> 为第三方用户ID $third_type -> 1--百度，2--新浪， 4--人人， 8--腾讯
	 */
	public JsonObject unBindThird(Context context,
			String thirdUid, String siteType) throws MessageException{
		Map<String, String > params = new HashMap<String, String>();
		JsonArray array = new JsonArray();
		String uid = ApplicationManager.getInstance().getUserId();
		array.add(uid);
		array.add(thirdUid);
		array.add(siteType);
		params.put("rpcinput", array.toJsonString());
		JsonObject resultObj = getHttpManager().post(LoginConstant.THIRD_BIND_UNBIND_METHOD, params, context).getJSONObject();
		return resultObj;
	}
}
