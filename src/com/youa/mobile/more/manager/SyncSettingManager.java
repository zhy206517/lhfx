
/*
package com.youa.mobile.more.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.youa.mobile.common.base.BaseManager;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.login.auth.SupportSite;
import com.youa.mobile.login.auth.SyncThirdData;
import com.youa.mobile.login.util.LoginUtil;
import com.youa.mobile.parser.JsonObject;

public class SyncSettingManager extends BaseManager {

	private final static String TAG = "SyncSettingManager";

	private SyncSettingRequestManager syncSettingRequestManager;

	private SyncSettingRequestManager getSyncSettingRequestManager() {
		if (syncSettingRequestManager == null) {
			syncSettingRequestManager = new SyncSettingRequestManager();
		}
		return syncSettingRequestManager;
	}
	
	public List<SyncThirdData> getUserThirdSyncList(Context context) throws MessageException{
		JsonObject resultObject = getSyncSettingRequestManager().getUserThirdSyncList(context);
		@SuppressWarnings("unchecked")
		List<SyncThirdData> sites = (List<SyncThirdData>)parseJsonObj(resultObject,ParseType.PARSE_SYNCLIST);
		return sites;
	}
	
	public boolean bindThird(Context context, String thirdUid, SupportSite site, String accessToken, String refreshToken, String expTime) throws MessageException{
		String siteCode = LoginUtil.parseSiteType(site.getSiteTag());
		JsonObject resultObject = getSyncSettingRequestManager().bindThird(context, thirdUid, siteCode, accessToken, refreshToken, expTime);
		String result = (String)parseJsonObj(resultObject,ParseType.PARSE_BIND);
		if(result != null)
			return true;
		else
			return false;
	}

	public boolean unBindThird(Context context, String thirdUid, SupportSite site) throws MessageException{
		String siteCode = LoginUtil.parseSiteType(site.getSiteTag());
		JsonObject resultObject = getSyncSettingRequestManager().unBindThird(context, thirdUid, siteCode);
		String result = (String)parseJsonObj(resultObject,ParseType.PARSE_UNBIND);
		if(result != null)
			return true;
		else
			return false;
	}
	
	private Object parseJsonObj(JsonObject resultObject,ParseType type) {
		Object o = null;
		switch (type) {
		case PARSE_BIND:
			
			break;
		case PARSE_UNBIND:
			break;
		case PARSE_SYNCLIST:
			JsonObject syncObjs = resultObject.getJsonObject("data").getJsonObject("rpcret");
			if(syncObjs == null)
				return null;
			List<SyncThirdData> list = new ArrayList<SyncThirdData>(0);
			for(String key : syncObjs.getKeys()){
				//JsonObject obj = syncObjs.getJsonObject(key);
				SyncThirdData data = new SyncThirdData(syncObjs.getJsonObject(key),key);
				if(data != null)
					list.add(data);
//				if(thirdObj instanceof JsonObject){
//					JsonObject third = (JsonObject)thirdObj;
//					for(String key : third.getKeys()){
//						SyncThirdData data = new SyncThirdData(third.getJsonObject(key),key);
//						if(data != null)
//							list.add(data);
//					}
//				}
			}
			o = list;
		default:
			break;
		}
		System.out.println(resultObject.toJsonString());
		return o;
	}
	
	private enum ParseType {
		PARSE_BIND, PARSE_UNBIND, PARSE_SYNCLIST
	}
}
*/

