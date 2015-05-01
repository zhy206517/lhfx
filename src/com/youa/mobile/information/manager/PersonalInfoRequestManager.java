package com.youa.mobile.information.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseRequestManager;
import com.youa.mobile.common.data.BaseUserData;
import com.youa.mobile.common.data.UserData;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.http.HttpRes;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.manager.ParserListView;
import com.youa.mobile.common.params.CommonParam;
import com.youa.mobile.common.params.PageSize;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.information.data.PersonalInformationData;
import com.youa.mobile.information.data.ShowCountData;
import com.youa.mobile.life.data.SuperPeopleData;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;

public class PersonalInfoRequestManager extends BaseRequestManager {
	public PersonalInformationData requestPersonalInformation(Context context,
			String uid) throws MessageException {

		// JsonObject object = new JsonObject();
		// object.put(InformationConstant.KEY_UID, uid);
		// object.put("keyword", keyword);
		// object.put("req_num", req_num);// 20
		// object.put("start_pos", start_pos);// 0
		// object.put("order_num", "500");
		JsonArray json = new JsonArray();
		json.add(uid);
		String str = json.toJsonString();

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);

		// Map<String, String> paramMap = new HashMap<String, String>();
		// paramMap.put(InformationConstant.KEY_UID, uid);
		paramMap.put(CommonParam.KEY_FROM, "jt/profile");
		HttpRes httpRes = getHttpManager().post("jt:profile.getProfile",
				paramMap, context);
		JsonObject ojbect = httpRes.getJSONObject();
		JsonObject dataObject = ojbect
				.getJsonObject(CommonParam.RESULT_KEY_DATA);
		//
		// test data begin
		// JsonObject dataObject = new JsonObject();
		// dataObject.put(InformationConstant.result_key_uid, uid);
		// dataObject.put(InformationConstant.result_key_username, "username");
		// dataObject.put(InformationConstant.result_key_sex, 1);
		// dataObject.put(InformationConstant.result_key_city, "北京");
		// dataObject.put(InformationConstant.result_key_birth_year, 1991);
		// // dataObject.put(InformationConstant.result_key_birth_month, 2);
		// dataObject.put(InformationConstant.result_key_birth_day, 2);
		// dataObject.put(InformationConstant.result_key_signature, "ab年后");
		// test data end

		return new PersonalInformationData(dataObject);
	}

	// public int getStatus(
	// Context context,
	// String objUid) {
	// String uid = ApplicationManager.getInstance().getUserId();
	// JsonArray json = new JsonArray();
	// json.add(uid);// uid
	// json.add(objUid);// cur_page 即将要请求的页面
	//
	// String str = json.toJsonString();
	//
	// Map<String, String> paramMap = new HashMap<String, String>();
	// paramMap.put("rpcinput", str);
	// HttpRes httpRes = getHttpManager().post("jt:msg.getFollowerStatus",
	// paramMap, context);
	// JsonObject resultObject = httpRes.getJSONObject();
	// List<HomeData> feedList =
	// ParserListView.getInstance().parserFriend(context, resultObject);
	// return feedList;
	// }

	public List<HomeData> requestOwnFeedList(Context context, String uid,
			int currentPageIndex) throws MessageException {
		JsonArray json = new JsonArray();
		json.add(uid);// uid
		json.add("" + currentPageIndex);// cur_page 即将要请求的页面
		json.add(String.valueOf(PageSize.INFO_FEED_PAGESIZE));// limit
		JsonObject object = new JsonObject();
		object.put("is_visiable_in_profile", true);
		object.put("type", "0");
		json.add(object);
		String str = json.toJsonString();
		// is_visiable_in_profile = true |false 表示是否在时光轴展示
		// type = 0 |1 |2 表示消息的类型，其中0表示原创，1表示转发，2表示喜欢。如果该字段没有将默认返回转发+原创的内容

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		HttpRes httpRes = getHttpManager().post("jt:msg.getPageOutBox",
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		List<HomeData> feedList = ParserListView.getInstance().parserFriend(
				context, resultObject);
		return feedList;
	}

	public List<HomeData> requestEnjoyFeedList(Context context, String uid,
			int lastPageIndex) throws MessageException {
		JsonArray json = new JsonArray();
		json.add(uid);// uid
		json.add("" + lastPageIndex);// last_page 现在所在页面
		json.add(String.valueOf(PageSize.INFO_FEED_PAGESIZE));// limit

		String str = json.toJsonString();

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
//		HttpRes httpRes = getHttpManager().post(
//				"jt:msg.getUserLikesForWireless", paramMap, context);
		HttpRes httpRes = getHttpManager().post(
				"jt:mobile.getUserLikes", paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		// List<FeedData> feedList = parserFeedData(resultObject);
		List<HomeData> homeList = ParserListView.getInstance().parserFriend(
				context, resultObject);
		return homeList;
	}

	public List<UserData> requestAttentUserList(Context context,
			String attentType, String attentUid, long offset)
			throws MessageException {

		String uid = ApplicationManager.getInstance().getUserId();
		// Map<String, String> paramMap = new HashMap<String, String>();
		// paramMap.put(InformationConstant.KEY_ATTENT_UID, uid);
		// paramMap.put(InformationConstant.KEY_ATTENT_OBJ_UID, attentUid);
		// paramMap.put(InformationConstant.KEY_ATTENT_TYPE, attentType);
		// paramMap.put(InformationConstant.KEY_ATTENT_OFFSET, "0");//TODO
		// paramMap.put(InformationConstant.KEY_ATTENT_LIMIT, "50");//TODO
		JsonArray json = new JsonArray();
		json.add(uid);// uid
		json.add(attentUid);// cur_page 即将要请求的页面
		json.add(attentType);// last_page 现在所在页面
		json.add(String.valueOf(offset));// end_postid 现在所在页面的 postid最小的
		json.add(String.valueOf(PageSize.INFO_USER_PAGESIZE));// limit

		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		paramMap.put("from", "jt/msg");
		HttpRes httpRes = getHttpManager().post("jt:msg.getFollowListWl",
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		// testData start
		// JsonObject resultObject = new JsonObject();
		// JsonObject arrayObject = new JsonObject();
		// JsonArray jsonArray = new JsonArray();
		// for(int i = 0; i < 10; i++) {
		// JsonObject json = new JsonObject();
		// json.put("uid", "111");
		// json.put("username", "username1");
		// json.put("photo_imid", "");
		// jsonArray.add(json);
		// }
		// arrayObject.put("rpcret", jsonArray);
		// resultObject.put("data", arrayObject);

		// testData end
		List<UserData> userList = parserUserData(resultObject);
		return userList;
	}

	public List<SuperPeopleData> requestAttentSuperUserList(Context context,
			String attentType, String attentUid, long offset)
			throws MessageException {

		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray json = new JsonArray();
		json.add(uid);// uid
		json.add(attentUid);// cur_page 即将要请求的页面
		json.add(attentType);// last_page 现在所在页面
		json.add(String.valueOf(offset));// end_postid 现在所在页面的 postid最小的
		json.add(String.valueOf(PageSize.INFO_FEED_PAGESIZE));// limit
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		paramMap.put("from", "jt/msg");
		HttpRes httpRes = getHttpManager().post("jt:msg.getFollowListWl",
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		List<SuperPeopleData> userList = parseSuperPeople(resultObject);
		return userList;
	}

	private List<SuperPeopleData> parseSuperPeople(JsonObject object) {
		if (isResponseOk(object) == null) {
			return null;
		}
		JsonObject dataObj = object.getJsonObject("data");
		JsonObject contentObject = dataObj.getJsonObject("rpcret");
		JsonArray jsonArray = contentObject.getJsonArray("info");
		List<SuperPeopleData> list = new ArrayList<SuperPeopleData>();
		if (jsonArray == null || jsonArray.size() < 1) {
			return list;
		}
		JsonObject json = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			json = (JsonObject) jsonArray.get(i);
			list.add(new SuperPeopleData(json));
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

	public List<UserData> requestFriendList(Context context)
			throws MessageException {
		String uid = ApplicationManager.getInstance().getUserId();
		// Map<String, String> paramMap = new HashMap<String, String>();
		// paramMap.put(InformationConstant.KEY_ATTENT_UID, uid);
		// paramMap.put(InformationConstant.KEY_ATTENT_OBJ_UID, attentUid);
		// paramMap.put(InformationConstant.KEY_ATTENT_TYPE, attentType);
		// paramMap.put(InformationConstant.KEY_ATTENT_OFFSET, "0");//TODO
		// paramMap.put(InformationConstant.KEY_ATTENT_LIMIT, "50");//TODO
		JsonArray json = new JsonArray();
		json.add(uid);

		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		paramMap.put("from", "jt/msg");
		HttpRes httpRes = getHttpManager().post("jt:msg.getUserFollowerInfoWl",
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		List<UserData> userList = parserUserData(resultObject);
		return userList;
	}

	// private List<FeedData> parserFeedData(JsonObject object) {
	// JsonObject dataObj = object.getJsonObject("data");
	// JsonArray jsonArray = dataObj.getJsonArray("rpcret");
	// List<FeedData> list = new ArrayList<FeedData>();
	// if (jsonArray == null || jsonArray.size() < 1) {
	// return list;
	// }
	// JsonObject json = null;
	// for (int i = 0; i < jsonArray.size(); i++) {
	// json = (JsonObject) jsonArray.get(i);
	// list.add(new BaseFeedData(json));
	// }
	// return list;
	// }

	private List<UserData> parserUserData(JsonObject object) {
		JsonObject dataObj = object.getJsonObject("data");
		JsonObject contentObject = dataObj.getJsonObject("rpcret");
		JsonArray jsonArray = contentObject.getJsonArray("info");
		List<UserData> list = new ArrayList<UserData>();
		if (jsonArray == null || jsonArray.size() < 1) {
			return list;
		}
		JsonObject json = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			json = (JsonObject) jsonArray.get(i);
			list.add(new BaseUserData(json));
		}
		return list;
	}

	private List<UserData> parserUserArrayData(JsonObject object) {
		JsonObject dataObj = object.getJsonObject("data");
		JsonObject contentObject = dataObj.getJsonObject("rpcret");
		JsonArray jsonArray = contentObject.getJsonArray("info");
		List<UserData> list = new ArrayList<UserData>();
		if (jsonArray == null || jsonArray.size() < 1) {
			return list;
		}
		JsonObject json = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			json = (JsonObject) jsonArray.get(i);
			list.add(new BaseUserData(json));
		}
		return list;
	}

	public ShowCountData requestInforCount(Context context, String uid)
			throws MessageException {
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(uid);

		// JsonArray jsonBase = new JsonArray();
		// jsonBase.add(uid);
		// jsonBase.add(jsonArray);

		// String str = jsonArray.toJsonString();

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("from", "jt/opset");
		paramMap.put("rpcinput", jsonArray.toJsonString());
		HttpRes httpRes = getHttpManager().post("jt:opset.getWLCounters",
				paramMap, context);

		JsonObject resultObject = httpRes.getJSONObject();
		JsonObject rpcjson = resultObject.getJsonObject("data");
		JsonObject subjson = rpcjson.getJsonObject("rpcret");
		String feedNum = subjson.getString("share_num");
		String favorNum = subjson.getString("like_num");
		String attentNum = subjson.getString("follow_num");
		String fansNum = subjson.getString("fans_num");
		String totalNum = subjson.getString("total_num");
		String belikeNum = subjson.getString("liked_num");
		String commentNum = subjson.getString("comment_num");
		String addMeNum = subjson.getString("at_num");
		return new ShowCountData(feedNum == null ? 0
				: Integer.parseInt(feedNum), favorNum == null ? 0
				: Integer.parseInt(favorNum), attentNum == null ? 0
				: Integer.parseInt(attentNum), fansNum == null ? 0
				: Integer.parseInt(fansNum), totalNum == null ? 0
				: Integer.parseInt(totalNum), belikeNum == null ? 0
				: Integer.parseInt(belikeNum), commentNum == null ? 0
				: Integer.parseInt(commentNum), addMeNum == null ? 0
				: Integer.parseInt(addMeNum));
		// return new ShowCountData(112, 20, 25, 10);
	}

	public void requestSaveManager(Context context, String uid,
			String username, String nickname, String sex, String province,
			String city, String counties, String birthdayYear,
			String birthdayMonth, String birthdayDay, String intruduce,
			String imageID) throws MessageException {

		JsonObject obj = new JsonObject();
		obj.put("head_imid", imageID);
		obj.put("sex", sex);
		obj.put("birth_year", birthdayYear);
		obj.put("birth_month", birthdayMonth);
		obj.put("birth_day", birthdayDay);
		obj.put("province", province);
		obj.put("city", city);
		obj.put("district", counties);
		obj.put("signature", intruduce);
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(uid);
		jsonArray.add(obj);

		// JsonArray jsonBase = new JsonArray();
		// jsonBase.add(uid);
		// jsonBase.add(jsonArray);

		String str = jsonArray.toJsonString();

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		paramMap.put("from", "jt/profile");
		HttpRes httpRes = getHttpManager().post("jt:profile.setWLProfile",
				paramMap, context);
		// JsonObject resultObject = httpRes.getJSONObject();
		// List<FeedData> feedList = parserFeedData(resultObject);
		// return feedList;
		// String divide = "";
		// String infoStr =
		// uid + divide +
		// sex + divide +
		// birthdayYear + divide +
		// birthdayMonth + divide +
		// birthdayDay + divide +
		// address + divide;
	}

	public void requestAddAttent(Context context, String uid, String followedId)
			throws MessageException {

		JsonArray json = new JsonArray();
		json.add(uid);
		json.add(followedId);

		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		paramMap.put("from", "jt/msg");
		HttpRes httpRes = getHttpManager().post("jt:msg.followUser", paramMap,
				context);
		httpRes.getJSONObject();
	}

	public void requestCancelAttent(Context context, String uid,
			String followedId) throws MessageException {

		JsonArray json = new JsonArray();
		json.add(uid);
		json.add(followedId);

		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		paramMap.put("from", "jt/msg");
		HttpRes httpRes = getHttpManager().post("jt:msg.cancelFollow",
				paramMap, context);
		httpRes.getJSONObject();
	}

	public String getFollowerStatus(Context context, String uid,
			String followedId) throws MessageException {
		JsonArray json = new JsonArray();
		json.add(uid);
		json.add(followedId);

		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		paramMap.put("from", "jt/msg");
		HttpRes httpRes = getHttpManager().post("jt:msg.getFollowerStatusWl",
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		JsonObject datajson = resultObject.getJsonObject("data");
		JsonArray rpcjson = datajson.getJsonArray("rpcret");
		if (rpcjson.size() > 0) {
			JsonObject jsonObject = (JsonObject) rpcjson.get(0);
			String status = jsonObject.getString("status");
			return status;
		} else {
			return "0";
		}
	}
}
