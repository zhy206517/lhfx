package com.youa.mobile.friend.manager;

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
import com.youa.mobile.friend.data.HomePageListConfig;
import com.youa.mobile.friend.friendmanager.ManagerSuperMenCalssifyData;
import com.youa.mobile.life.data.SuperPeopleData;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;

public class HomeHttpManager extends BaseRequestManager {

	// 好友动态
	public List<HomeData> requestFriendDynamicList(Context context,
			String minPostId, String maxPostId) throws MessageException {
		// "uid","cur_page", "last_page", "end_postid", "limit"
		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray json = new JsonArray();
		json.add(uid);// uid
		json.add(minPostId);// 最小postId
		json.add(maxPostId);// 最大postId
		json.add("" + HomePageListConfig.LIMIT_NUM);// limit

		String str = json.toJsonString();

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		HttpRes httpRes = getHttpManager().post("jt:mobile.getFriendsFeeds",
				paramMap, context);// 新接口jt:mobile.getFriendsFeeds 原接口名
									// jt:msg.getPageInboxForWireless
		JsonObject resultObject = httpRes.getJSONObject();
		// List<HomeData> homeList = parserFriend(context, resultObject);
		List<HomeData> homeList = ParserListView.getInstance().parserFriend(
				context, resultObject);

		resultObject = null;
		return homeList;
	}

	/**
	 * 获取达人分类
	 */
	public List<ManagerSuperMenCalssifyData> requestSuperPeopleClassify(
			Context context) throws MessageException {
		JsonArray jsonArray = new JsonArray();
		jsonArray.add("6");// 6为默认参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("rpcinput", jsonArray.toJsonString());
		HttpRes res = getHttpManager().post("jt:class.getClassTreeByType",
				params, context);
		JsonObject resultObject = res.getJSONObject();
		return parserSuperPeopleClassify(resultObject);
	}

	/*
	 * 全站搜素人
	 */

	public List<SuperPeopleData> requestFindPeopleList(Context context,
			String findName, int postion) throws MessageException {
		JsonArray jsonArray = new JsonArray();
		JsonObject object = new JsonObject();
		object.put("uid", ApplicationManager.getInstance().getUserId());
		object.put("keyword", findName);
		object.put("start_pos", postion * HomePageListConfig.LIMIT_NUM);
		object.put("req_num", HomePageListConfig.LIMIT_NUM);
		object.put("loose_check", "1");
		jsonArray.add(object);
		String str = jsonArray.toJsonString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("rpcinput", str);
		HttpRes res = getHttpManager().post("snsui.searchUser4Wireless",
				params, context);
		List<SuperPeopleData> list = parseFindPeople(res.getJSONObject());
		return list;
	}

	// -----------------------------------parser-------------------------------------------

	private List<SuperPeopleData> parseFindPeople(JsonObject object) {
		if (isResponseOk(object) == null) {
			return null;
		}
		Log.d("LifeHttpRequestManager", object.toJsonString());
		JsonObject dataObj = object.getJsonObject("data");
		JsonArray jsonArray = dataObj.getJsonArray("rpcret");
		if (jsonArray == null || jsonArray.size() == 0) {
			return null;
		}
		List<SuperPeopleData> list = new ArrayList<SuperPeopleData>();
		JsonObject json = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			json = (JsonObject) jsonArray.get(i);
			list.add(new SuperPeopleData(json));
		}
		return list;
	}

	private JsonObject isResponseOk(JsonObject object) {
		if (object == null || object.size() < 1) {
			return null;
		}
		String responseCode = object.getString("err");
		if (!"mcphp.ok".equals(responseCode)) {
			// homeList.removeFooter(footer);
			// 错误信息处理
			return null;
		}
		return object;
	}

	private List<ManagerSuperMenCalssifyData> parserSuperPeopleClassify(
			JsonObject object) {
		if (isResponseOk(object) == null) {
			return null;
		}
		JsonObject dataObj = object.getJsonObject("data");
		JsonObject jsonArray = dataObj.getJsonObject("rpcret");
		if (jsonArray == null || jsonArray.size() < 1) {
			return null;
		}
		List<ManagerSuperMenCalssifyData> list = new ArrayList<ManagerSuperMenCalssifyData>();
		// ------------------------
		ManagerSuperMenCalssifyData data = null;
		// ------------------------
		String[] keys = jsonArray.getKeys();
		for (int i = 0; i < keys.length; i++) {
			dataObj = (JsonObject) jsonArray.getJsonObject(keys[i]);
			data = parserItem(dataObj);
			if (getSuperName(data.name)) {
				list.add(data);
			}
		}
		return list;
	}

	private ManagerSuperMenCalssifyData parserItem(JsonObject object) {
		ManagerSuperMenCalssifyData data = new ManagerSuperMenCalssifyData();
		data.id = object.getString("id");
		data.name = object.getString("name");
		return data;
	}

	public boolean getSuperName(String Value) {
		// if ("hot_recommend".equals(Value)) {
		// key = "热门推荐";
		// } else
		if ("share_cate".equals(Value)) {
			return true;
		} else if ("play_group".equals(Value)) {
			return true;
		} else if ("city_beauty".equals(Value)) {
			return true;
		} else if ("mother_baby".equals(Value)) {
			return true;
		} else if ("roman_mary".equals(Value)) {
			return true;
		} else if ("happy_house".equals(Value)) {
			return true;
		}
		return false;
	}
	// ---------------

}
