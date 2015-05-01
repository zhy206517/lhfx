package com.youa.mobile.theme.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.youa.mobile.common.base.BaseRequestManager;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.http.HttpRes;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.manager.ParserListView;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;
import com.youa.mobile.theme.data.PopThemeInfo;

public class HomeHttpManager extends BaseRequestManager {

	// 话题动态
	public List<HomeData> requestThemeDynamicList(Context context,
			String keyword, int req_num, int start_pos) throws MessageException {
		String uid = ApplicationManager.getInstance().getUserId();
		JsonObject object = new JsonObject();
		object.put("uid", uid);
		object.put("keyword", keyword);
		object.put("req_num", req_num);// 20
		object.put("start_pos", start_pos);// 0
		object.put("loose_check", 1);
		object.put("order_num", "500");
		JsonArray json = new JsonArray();
		json.add(object);
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
//		HttpRes httpRes = getHttpManager().post("snsui.searchSubject4Wireless",// snsui.searchSubject,jt:msg.getPagePosts
//				paramMap, context);
		HttpRes httpRes = getHttpManager().post("jt:mobile.searchSubject",// snsui.searchSubject,jt:msg.getPagePosts
		paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		List<HomeData> homeList = ParserListView.getInstance().parserFriend(
				context, resultObject);
		return homeList;
	}
	
	public List<HomeData> requestAlbumFeedList(Context context, String id,
			int req_num, int start_pos) throws MessageException {
		JsonArray json = new JsonArray();
		json.add(id);//
		json.add("0");// 0最新 1最热
		json.add(String.valueOf(start_pos));
		json.add(String.valueOf(req_num));
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		HttpRes httpRes = getHttpManager().post("jt:theme.getPostsForWL",
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		List<HomeData> homeList = ParserListView.getInstance().parserFriend(
				context, resultObject);
		return homeList;
	}

	private String getTopic(JsonObject object) {

		if (isResponseOk(object) == null) {
			return null;
		}
		JsonObject obj = object.getJsonObject("data").getJsonObject("rpcret");
		if (obj == null) {
			return null;
		} else {
			return obj.getString("name");
		}

	}

	public boolean requestDeleteThemeCollection(Context context, String themeId)
			throws MessageException {
		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray json = new JsonArray();
		json.add(uid);
		json.add(themeId);
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		HttpRes httpRes = getHttpManager().post("jt:msg.cancelFollowSubject",
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		boolean isDelete = parserDeleteThemeCollection(resultObject);
		return isDelete;
	}

	public List<PopThemeInfo> requestThemeCollection(Context context,
			int offset, int limit) throws MessageException {
		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray json = new JsonArray();
		json.add(uid);
		json.add("" + offset);// offset 从0开始
		json.add("" + limit);// limit
		String str = json.toJsonString();
		// "uid","cur_page", "last_page", "end_postid", "limit"
		// "396c723f65c45599a31fea33", "1", "1","1", "20"
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		HttpRes httpRes = getHttpManager().post("jt:msg.getSubjectListByUid",
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		List<PopThemeInfo> themeCollectionList = parserThemeCollection(resultObject);
		return themeCollectionList;
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

	// ---------------------------parser--------------------------------

	protected List<PopThemeInfo> parserThemeCollection(JsonObject object) {
		if (isResponseOk(object) == null) {
			return null;
		}
		JsonObject obj = object.getJsonObject("data");
		JsonObject dataObj = obj.getJsonObject("rpcret");
		// data.count = Integer.parseInt(dataObj.getString("tn"));
		JsonArray jsons = dataObj.getJsonArray("info");
		if (jsons == null || jsons.size() < 1) {
			return null;
		}
		// -----------------------------
		PopThemeInfo[] data = new PopThemeInfo[jsons.size()];
		// -----------------------------
		List<PopThemeInfo> list = new ArrayList<PopThemeInfo>();
		JsonObject JsonO = null;
		for (int i = 0; i < jsons.size(); i++) {
			data[i] = new PopThemeInfo();
			JsonO = (JsonObject) jsons.get(i);
			data[i].name = JsonO.getString("sname");
			data[i].sUid = JsonO.getString("suid");
			list.add(data[i]);
		}
		return list;
	}

	private boolean parserDeleteThemeCollection(JsonObject object) {
		if (isResponseOk(object) == null) {
			return false;
		}
		JsonObject obj = object.getJsonObject("data");
		String isDelete = obj.getString("rpcret");
		if ("1".equals(isDelete)) {
			return true;
		}
		return false;
	}

	// -------------------------------取数据库----------------------------

	public void saveThemeCollectionDB() {

	}

	public void getThemeCollectionDB() {

	}
}
