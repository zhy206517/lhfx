package com.youa.mobile.location.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.youa.mobile.common.base.BaseRequestManager;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.http.HttpRes;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.params.CommonParam;
import com.youa.mobile.life.action.RequestSearchAction;
import com.youa.mobile.location.MapPage;
import com.youa.mobile.location.data.LocationData;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;
import com.youa.mobile.parser.JsonValue;

public class LocationHttpManager extends BaseRequestManager {
	private static final String TAG = "TopicHttpManager";
	private static final String KEY_PUID = "plid";
	private static final String KEY_PLACE_X = "place_x";
	private static final String KEY_PLACE_Y = "place_y";
	private static final String KEY_SNAME = "place_name";
	private static final String KEY_ADDRESS_NAME = "place_addr";
	private static final String KEY_PLACE_TYPE = "type";
	private static final String KEY_LOC_ARRAY = "rpcret";
	private static final int REQ_NUM = 50;

	public void getCommendSubject() {

	}

	public List<LocationData> requestLocationList(Context context,
			Map<String, Object> para) throws MessageException {
		JsonArray json = new JsonArray();
		JsonObject object = new JsonObject();
		object.put("uid", ApplicationManager.getInstance().getUserId());
		object.put("start_pos", 0);
		object.put("loose_check", 1);
		object.put("req_num", REQ_NUM);
		object.put("order_num", 1000);
		object.put("order", "distance");
		object.put("place_x", para.get(MapPage.KEY_LAT).toString());
		object.put("place_y", para.get(MapPage.KEY_LON).toString());
		object.put("distance", para.get("distance").toString());
		String city = para.get(MapPage.KEY_CITY).toString();
		if (!TextUtils.isEmpty(city)) {
			if (city.contains("市")) {
				city = city.substring(0, city.indexOf("市"));
			}
		}
		object.put("city", city);
		json.add(object);
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		HttpRes httpRes = getHttpManager().post("snsui.suggestPlace4Wireless",
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		List<LocationData> mLocList = parserLocationList(resultObject);
		return mLocList;
	}

	public List<LocationData> requestSearchList(Context context,
			Map<String, Object> para) throws MessageException {
		JsonArray json = new JsonArray();
		JsonObject object = new JsonObject();
		object.put("uid", ApplicationManager.getInstance().getUserId());
		object.put("keyword",
				String.valueOf(para.get(RequestSearchAction.KEY_SEARCH_KEY)));
		object.put("start_pos", 0);
		object.put("loose_check", 1);
		object.put("req_num", REQ_NUM);
		json.add(object);
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		HttpRes httpRes = getHttpManager().post("snsui.suggestPlace4Wireless",
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		List<LocationData> mLocList = parserLocationList(resultObject);
		return mLocList;
	}

	private List<LocationData> parserLocationList(JsonObject jsonObj) {
		if (isResponseOk(jsonObj) == null) {
			return null;
		}
		JsonArray LocJsonArray = jsonObj.getJsonObject(
				CommonParam.RESULT_KEY_DATA).getJsonArray(KEY_LOC_ARRAY);
		if (LocJsonArray == null || LocJsonArray.size() < 1) {
			return null;
		}
		List<LocationData> locList = new ArrayList<LocationData>(0);
		JsonObject obj = null;
		LocationData loc = null;
		for (JsonValue value : LocJsonArray.getValue()) {
			if (value instanceof JsonObject) {
				obj = (JsonObject) value;
				loc = new LocationData();
				loc.locName = obj.getString(KEY_SNAME);
				loc.sPid = obj.getString(KEY_PUID);
				loc.latitude = Integer.parseInt(obj.getString(KEY_PLACE_X));
				loc.longitude = Integer.parseInt(obj.getString(KEY_PLACE_Y));
				loc.addName = obj.getString(KEY_ADDRESS_NAME);
				loc.type = obj.getString(KEY_PLACE_TYPE);
				locList.add(loc);
			}
		}
		return locList;
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
