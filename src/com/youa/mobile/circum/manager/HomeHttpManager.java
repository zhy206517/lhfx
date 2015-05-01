package com.youa.mobile.circum.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.youa.mobile.circum.data.PopCircumData;
import com.youa.mobile.common.base.BaseRequestManager;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.http.HttpRes;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.manager.ParserListView;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;

public class HomeHttpManager extends BaseRequestManager {
//    private final static String SEARCH_SUBJECT_METHOD = "snsui.searchSubject4Wireless";
	private final static String SEARCH_SUBJECT_METHOD = "jt:mobile.searchSubject";
	// 周边动态
	public List<HomeData> requestCircumDynamicList(Context context,
			String place_x, String place_y, int req_num, int start_pos)
			throws MessageException {
		String uid = ApplicationManager.getInstance().getUserId();
		// --------
		JsonObject object = new JsonObject();
//		object.put("uid", uid);
		object.put("place_x", place_x);
		object.put("place_y", place_y);
		object.put("req_num", req_num);
		object.put("start_pos", start_pos);
		object.put("loose_check", "1");
		object.put("order_num", "500");
		JsonArray json = new JsonArray();
		json.add(object);
		// ---------
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		// paramMap.put("username", "lehoqa360@baidu.com");
		HttpRes httpRes = getHttpManager().post(SEARCH_SUBJECT_METHOD,// snsui.searchSubject,jt:msg.getPagePosts
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		List<HomeData> homeList = ParserListView.getInstance().parserFriend(
				context, resultObject);
		return homeList;
	}
	public List<HomeData> requestCircumDynamicList(Context context,
			String id, int req_num, int start_pos)
			throws MessageException {
		String uid = ApplicationManager.getInstance().getUserId();
		// --------
		JsonObject object = new JsonObject();
		object.put("uid", uid);
		object.put("district_id", id);
		object.put("req_num", req_num);
		object.put("start_pos", start_pos);
		object.put("order_num", "500");
		JsonArray json = new JsonArray();
		json.add(object);
		// ---------
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		// paramMap.put("username", "lehoqa360@baidu.com");
		HttpRes httpRes = getHttpManager().post(SEARCH_SUBJECT_METHOD,// snsui.searchSubject,jt:msg.getPagePosts
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		List<HomeData> homeList = ParserListView.getInstance().parserFriend(
				context, resultObject);
		return homeList;
	}
	// 周边收藏列表		
	public List<PopCircumData> requestCircumCollection(Context context,
			int offset, int limit) throws MessageException {
		JsonArray json = new JsonArray();
		json.add(ApplicationManager.getInstance().getUserId());
		json.add("" + offset);// offset 从0开始
		json.add("" + limit);// limit
		String str = json.toJsonString();
		// "uid","cur_page", "last_page", "end_postid", "limit"
		// "396c723f65c45599a31fea33", "1", "1","1", "20"
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		HttpRes httpRes = getHttpManager().post("jt:msg.getUserFollowedDistrict",
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		List<PopCircumData> circumCollectionList = parserCircumCollection(resultObject);
		return circumCollectionList;
	}
	// 刪除周边
	public boolean requestDeleteCircumCollection(Context context,
			String circumId) throws MessageException {
		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray json = new JsonArray();
		json.add(uid);
		json.add(circumId);
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		HttpRes httpRes = getHttpManager().post("jt:msg.unfollowDistrict",
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		boolean isDelete = parserDeleteCircumCollection(resultObject);
		return isDelete;
	}
	
	private boolean parserDeleteCircumCollection(JsonObject object) {
		if (isResponseOk(object) == null) {
			return false;
		}
		JsonObject obj = object.getJsonObject("data");
		String isDelete = obj.getString("rpcret");
		if (isDelete.equals("1")) {
			return true;
		}
		return false;
	}
	
	private JsonObject isResponseOk(JsonObject object) {
		String responseCode = object.getString("err");
		if (!"mcphp.ok".equals(responseCode)) {
			// homeList.removeFooter(footer);
			// 错误信息处理
			return null;
		}
		return object;
	}
	
	private List<PopCircumData> parserCircumCollection(JsonObject object) {
		if (isResponseOk(object) == null) {
			return null;
		}
		JsonObject obj = object.getJsonObject("data");
		JsonObject jsonObj = obj.getJsonObject("rpcret");
		if (jsonObj == null || jsonObj.size() < 1) {
			return null;
		}
		JsonArray jsonArray = jsonObj.getJsonArray("data");		
		if (jsonArray == null || jsonArray.size() < 1) {
			return null;
		}
		List<PopCircumData> list = new ArrayList<PopCircumData>();
		PopCircumData[] data = new PopCircumData[jsonArray.size()];
		JsonObject jsonO = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			jsonO = (JsonObject) jsonArray.get(i);
			data[i] = new PopCircumData();
			data[i].pLid = jsonO.getString("district_id");
			data[i].place_name = jsonO.getString("district_name");
			list.add(data[i]);
		}
		return list;
	}

}
