package com.youa.mobile.jingxuan.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.youa.mobile.SystemConfig;
import com.youa.mobile.common.base.BaseRequestManager;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.http.HttpRes;
import com.youa.mobile.jingxuan.data.ClassifyTagInfoData;
import com.youa.mobile.parser.JsonObject;

public class JingXuanHttpRequestManager extends BaseRequestManager {
	String GETALL_TAGS_METHOD = "jt:tag.getFirstPostInAllTags2";
	// 3 hours
	private static long THIRD_HOURS = 60 * 60 * 1000 * 3;

	// private static long THIRD_HOURS = 20*1000*3;
	public List<ClassifyTagInfoData> getFirstPostInAllTags(Context context)
			throws MessageException {
		JsonObject resultObject = null;
		List<ClassifyTagInfoData> mTagInfoDataList = new ArrayList<ClassifyTagInfoData>();
		long now = System.currentTimeMillis();
		SharedPreferences sp = context.getSharedPreferences(
				SystemConfig.XML_FILE_TAG_CACHE, Context.MODE_WORLD_READABLE);
		String str = sp.getString(SystemConfig.KEY_TAG_INFO, "");
		long tmp = sp.getLong(SystemConfig.KEY_TAG_TMP, 0);
		if (tmp == 0 || (now - tmp) > THIRD_HOURS) {
			resultObject = getResultObject(context);
		} else {
			if (TextUtils.isEmpty(str)) {
				resultObject = getResultObject(context);
			} else {
				resultObject = JsonObject.parseObject(str);
			}

		}
		mTagInfoDataList = parseTagInfoData(resultObject);
		return mTagInfoDataList;
	}

	public JsonObject getResultObject(Context context) throws MessageException {
		JsonObject resultObject;
		SharedPreferences sp;
		Map<String, String> paramMap = new HashMap<String, String>();
		HttpRes httpRes = getHttpManager().post(GETALL_TAGS_METHOD, paramMap,
				context);
		resultObject = httpRes.getJSONObject();
		sp = context.getSharedPreferences(SystemConfig.XML_FILE_TAG_CACHE,
				Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor edit = sp.edit();
		edit.putString(SystemConfig.KEY_TAG_INFO, resultObject.toJsonString());
		edit.putLong(SystemConfig.KEY_TAG_TMP, System.currentTimeMillis());
		edit.commit();
		return resultObject;
	}

	private List<ClassifyTagInfoData> parseTagInfoData(JsonObject object) {
		if (isResponseOk(object) == null) {
			return null;
		}
		JsonObject dataObj = object.getJsonObject("data").getJsonObject(
				"rpcret");
		if (dataObj == null) {
			return null;
		}
		String[] strKey = dataObj.getKeys();
		if (strKey == null || strKey.length < 1) {
			return null;
		}
		List<ClassifyTagInfoData> list = new ArrayList<ClassifyTagInfoData>();
		JsonObject json = null;
		for (int i = 0; i < strKey.length; i++) {
			json = dataObj.getJsonObject(strKey[i]);
			if (json != null && json.size() > 0) {
				list.add(new ClassifyTagInfoData(json));
			}
		}
		return list;
	}

	private JsonObject isResponseOk(JsonObject object) {
		String responseCode = object.getString("err");
		if (!"mcphp.ok".equals(responseCode)) {
			// TODO throw new MessageException();
			// 错误信息处理
			return null;
		}
		return object;
	}
}