package com.youa.mobile.news.manager;

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
import com.youa.mobile.news.data.AddMeData;
import com.youa.mobile.news.data.FavoriteData;
import com.youa.mobile.news.data.FavoritePeopleData;
import com.youa.mobile.news.data.SayMeData;
import com.youa.mobile.news.util.NewsUtil;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;
import com.youa.mobile.parser.JsonString;

public class NewsHttpRequestManager extends BaseRequestManager {

	private final static String TAG = "NewsHttpRequestManager";

//	private final static String ADDME_METHOD = "jt:msg.getAtPostByUidWl";old
	private final static String ADDME_METHOD = "jt:mobile.getAtPostByUid";
	private final static String SAYME_METHOD = "jt:msg.getRcvCmtByUidWl";
	private final static String FAVORITE_METHOD = "jt:msg.getUserBeLikedPostInfoSimple";
	private final static String GET_NEWNEWS_METHOD = "jt:user.getNotifyInfo";
	private final static String SET_NEWNEWS_METHOD = "jt:user.resetNotify";
	private final static String GET_FAV_PEOPLE_METHOD = "jt:msg.getPostBeLikeUserInfo";
	private final static String REQUEST_INPUT_KEY = "rpcinput";
	private final static String NEW_NEWS_AT_ME = "new_at_post_num";
	private final static String NEW_NEWS_AT_CMT = "new_cmt_num";
	private final static String NEW_NEWS_LIKE = "new_like_num";
	
	private List<AddMeData> parserAddMeData(JsonObject object) {
		if (isResponseOk(object) == null) {
			return null;
		}
		JsonObject dataObj = object.getJsonObject("data");
		JsonObject data = dataObj.getJsonObject("rpcret");
		JsonArray jsonArray = data.getJsonArray("info");
		if (jsonArray == null || jsonArray.size() < 1) {
			return null;
		}
		List<AddMeData> list = new ArrayList<AddMeData>();
		JsonObject json = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			json = (JsonObject) jsonArray.get(i);
			list.add(new AddMeData(json));
		}
		json = null;
		return list;
	}

	private List<SayMeData> parserSayMeData(Context context, JsonObject object) {
		if (isResponseOk(object) == null) {
			return null;
		}
		JsonObject dataObj = object.getJsonObject("data");
		if (dataObj == null) {
			return null;
		}
		JsonArray jsonArray = dataObj.getJsonArray("rpcret");
		if (jsonArray == null || jsonArray.size() < 1) {
			return null;
		}
		List<SayMeData> list = new ArrayList<SayMeData>();
		JsonObject json = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			json = (JsonObject) jsonArray.get(i);
			list.add(new SayMeData(context, json));
		}
		json = null;
		return list;
	}

	private List<FavoriteData> parserFavoriteData(Context context, JsonObject object) {
		JsonObject dataObj = object.getJsonObject("data");
		JsonArray jsonArray = dataObj.getJsonArray("rpcret");
		List<FavoriteData> list = new ArrayList<FavoriteData>();
		if (jsonArray == null || jsonArray.size() < 1) {
			return list;
		}
		JsonObject json = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			json = (JsonObject) jsonArray.get(i);
			list.add(new FavoriteData(context, json));
		}
		return list;
	}

	public List<HomeData> requestAddMeData (Context context, String uId, int limit, long time) throws MessageException {

		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray json = new JsonArray();
		json.add(uid);
		json.add("" + time);
		json.add("" + limit);
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(REQUEST_INPUT_KEY, str);
		HttpRes httpRes = getHttpManager().post(ADDME_METHOD, paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		List<HomeData> list = ParserListView.getInstance().parserAddMe(context, resultObject);
		if (list != null) {
			NewsUtil.LOGD(TAG, "enter requestAddMeData  <date list> : " + list.size());
		}
		return list;
	}

	public List<SayMeData> requestSayMeData (Context context, String uId, int limit, long time) throws MessageException {

		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray json = new JsonArray();
		json.add(uid);
		json.add("" + time);
		json.add("" + limit);
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(REQUEST_INPUT_KEY, str);
		HttpRes httpRes = getHttpManager().post(SAYME_METHOD, paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		List<SayMeData> list = parserSayMeData(context, resultObject);
		if (list != null) {
			NewsUtil.LOGD(TAG, "enter requestSayMeData  <date list> : " + list.size());
		}
		return list;
	}

	public List<FavoriteData> requestFavoriteData(Context context, String uId, int limit, String postId) throws MessageException {

		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray json = new JsonArray();
		json.add(uid);
		json.add(postId);
		json.add("" + limit);
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(REQUEST_INPUT_KEY, str);
		HttpRes httpRes = getHttpManager().post(FAVORITE_METHOD, paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		List<FavoriteData> list = parserFavoriteData(context, resultObject);
		if (list != null) {
			NewsUtil.LOGD(TAG, "enter requestFavoriteData  <date list> : " + list.size());
		}
		return list;
	}

	public int[] requestGetNewNewsCount(Context context, String uId) throws MessageException {
		
		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray json = new JsonArray();
		json.add(uid);
		json.add(new JsonString());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(REQUEST_INPUT_KEY, json.toJsonString());
		HttpRes httpRes = getHttpManager().post(GET_NEWNEWS_METHOD, paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		int[] list = parserNewNewsData(resultObject);
		if (list != null) {
			NewsUtil.LOGD(TAG, "enter requestGetNewNewsCount  <date list> : " + list.length);
		}
		return list;
	}

	public List<FavoritePeopleData> requestFavoritePeopleList(Context context, String feedId) throws MessageException {

		JsonArray json = new JsonArray();
		json.add(feedId);
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(REQUEST_INPUT_KEY, str);
		HttpRes httpRes = getHttpManager().post(GET_FAV_PEOPLE_METHOD, paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		List<FavoritePeopleData> list = parserPeopleList(resultObject);
		if (list != null) {
			NewsUtil.LOGD(TAG, "enter requestFavoritePeopleList  <date list> : " + list.size());
		}
		return list;
	}

	private List<FavoritePeopleData> parserPeopleList(JsonObject object) {
		if (isResponseOk(object) == null) {
			return null;
		}
		JsonObject dataObj = object.getJsonObject("data");
		JsonArray jsonArray = dataObj.getJsonArray("rpcret");
		if (jsonArray == null || jsonArray.size() < 1) {
			return null;
		}
		List<FavoritePeopleData> list = new ArrayList<FavoritePeopleData>();
		JsonObject json = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			json = (JsonObject) jsonArray.get(i);
			list.add(new FavoritePeopleData(json));
		}
		return list;
	}

	public void requestSetNewNewsCount(Context context, String uId, int type, int count) throws MessageException {
		String[] types = new String[]{NEW_NEWS_AT_ME,NEW_NEWS_AT_CMT,NEW_NEWS_LIKE};
		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray json = new JsonArray();
		json.add(uid);
		json.add(types[type]);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(REQUEST_INPUT_KEY, json.toJsonString());
		getHttpManager().post(SET_NEWNEWS_METHOD, paramMap, context);
	}

	private int[] parserNewNewsData(JsonObject object) {
		if (isResponseOk(object) == null) {
			return null;
		}
		JsonObject dataObj = object.getJsonObject("data");
		if (dataObj == null) {
			return null;
		}
		JsonObject json = dataObj.getJsonObject("rpcret");
		if (json == null) {
			return null;
		}
		String add = json.getString(NEW_NEWS_AT_ME);
		String say = json.getString(NEW_NEWS_AT_CMT);
		String fav = json.getString(NEW_NEWS_LIKE);
		int[] list  = new int[4];
		try {
			list[0] = Integer.parseInt(add);
			list[1] = Integer.parseInt(say);
			list[2] = Integer.parseInt(fav);
			list[3] = list[0] + list[1] + list[2];
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}

	private JsonObject isResponseOk(JsonObject object) {
		String responseCode = object.getString("err");
		if (!"mcphp.ok".equals(responseCode)) {
			//TODO throw new MessageException();
			// 错误信息处理
			return null;
		}
		return object;
	}
}
