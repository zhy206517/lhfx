package com.youa.mobile.life.manager;

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
import com.youa.mobile.common.params.CommonParam;
import com.youa.mobile.content.data.FeedContentCommentData;
import com.youa.mobile.jingxuan.data.CategoryData;
import com.youa.mobile.jingxuan.data.TagInfoData;
import com.youa.mobile.life.action.DistrictAction.RequestType;
import com.youa.mobile.life.data.DistrictData;
import com.youa.mobile.life.data.SuperPeopleData;
import com.youa.mobile.life.data.UserInfo;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonNum;
import com.youa.mobile.parser.JsonObject;
import com.youa.mobile.parser.JsonValue;
import com.youa.mobile.theme.data.TopicData;

public class LifeHttpRequestManager extends BaseRequestManager {

	private final static String SUPER_PEOPLE_METHOD = "jt:opset.getWLCommendExpert";
	// private final static String RELATIONSHIP_METHOD =
	// "jt:msg.getFollowerStatusWl";
	private final static String FIND_PEOPLE_METHOD = "snsui.searchUser4Wireless";
	private final static String REQUEST_INPUT_KEY = "rpcinput";

	public final static String KEY_PEOPLE_UID = "uid";
	public final static String KEY_PEOPLE_UNAME = "nickname";// username
	public final static String KEY_PEOPLE_IMGID = "head_imid";
	public final static String KEY_PEOPLE_SEX = "sex";
	public final static String KEY_PEOPLE_SIGNATURE = "signature";
	public final static String KEY_PEOPLE_FOLLOW_STATUS = "follow_status";
	private final static int SUPER_PEOPLE_NUM = 100;
	private final static int FIND_PEOPLE_COUNT = 20;
	private static final String KEY_TOPIC_SUID = "suid";
	private static final String KEY_TOPIC_SUBSTATUS = "status";
	private static final String KEY_TOPIC_SNAME = "sname";
	// 发现生活达人获取达人分类
	private final static String GET_SUPPER_PEOPLE_CLASSIFY_METHOD = "jt:class.getClassTreeByType";
	private final static String GET_MAINCOMMEND_DAREN_METHOD = "jt:opset.getMainCommendDaren";
	private final static String GET_SMALLCOMMEND_DAREN_METHOD = "jt:opset.getSmallCommendDaren";

	public List<SuperPeopleData> requestSuperPeopleList(Context context)
			throws MessageException {
		JsonArray jsonArray = new JsonArray();
		String uid = ApplicationManager.getInstance().getUserId();
		jsonArray.add(uid);
		jsonArray.add(new JsonNum(SUPER_PEOPLE_NUM));
		JsonArray optionArray = new JsonArray();
		optionArray.add(KEY_PEOPLE_UID);
		optionArray.add(KEY_PEOPLE_UNAME);
		optionArray.add(KEY_PEOPLE_IMGID);
		optionArray.add(KEY_PEOPLE_SEX);
		optionArray.add(KEY_PEOPLE_SIGNATURE);
		jsonArray.add(optionArray);
		String str = jsonArray.toJsonString();
		Map<String, String> params = new HashMap<String, String>();
		params.put(REQUEST_INPUT_KEY, str);
		HttpRes res = getHttpManager().post(SUPER_PEOPLE_METHOD, params,
				context);
		JsonObject obj = res.getJSONObject();
		List<SuperPeopleData> list = parseFindPeople(obj);
		return list;
	}

	public List<SuperPeopleData> requestSuperPeopleList(Context context,
			String key) throws MessageException {
		JsonArray jsonArray = new JsonArray();
		String uid = ApplicationManager.getInstance().getUserId();
		jsonArray.add(key);
		jsonArray.add(uid);
		jsonArray.add(new JsonNum(SUPER_PEOPLE_NUM));
		JsonArray optionArray = new JsonArray();
		optionArray.add(KEY_PEOPLE_UID);
		optionArray.add(KEY_PEOPLE_UNAME);
		optionArray.add(KEY_PEOPLE_IMGID);
		optionArray.add(KEY_PEOPLE_SEX);
		optionArray.add(KEY_PEOPLE_SIGNATURE);
		optionArray.add(KEY_PEOPLE_FOLLOW_STATUS);
		jsonArray.add(optionArray);
		String str = jsonArray.toJsonString();
		Map<String, String> params = new HashMap<String, String>();
		params.put(REQUEST_INPUT_KEY, str);
		HttpRes resM = getHttpManager().post(GET_MAINCOMMEND_DAREN_METHOD,
				params, context);
		JsonObject objM = resM.getJSONObject();
		List<SuperPeopleData> list = parseSuperPeople(objM);
		HttpRes resS = getHttpManager().post(GET_SMALLCOMMEND_DAREN_METHOD,
				params, context);
		JsonObject objS = resS.getJSONObject();
		List<SuperPeopleData> listS = parseSuperPeople(objS);
		if (listS != null && list != null) {
			list.addAll(listS);
		}
		return list;
	}

	/**
	 * 获取达人分类
	 * 
	 * @param context
	 * @return
	 * @throws MessageException
	 */
	public JsonObject requestSuperPeopleClassify(Context context)
			throws MessageException {
		JsonArray jsonArray = new JsonArray();
		jsonArray.add("6");// 6为默认参数
		Map<String, String> params = new HashMap<String, String>();
		params.put(REQUEST_INPUT_KEY, jsonArray.toJsonString());
		HttpRes res = getHttpManager().post(GET_SUPPER_PEOPLE_CLASSIFY_METHOD,
				params, context);
		return res.getJSONObject();
	}

	public List<SuperPeopleData> requestFindPeopleList(Context context, String findName)
			throws MessageException {
		JsonArray jsonArray = new JsonArray();
		JsonObject object = new JsonObject();
		object.put("uid", ApplicationManager.getInstance().getUserId());
		object.put("keyword", findName);
		object.put("req_num", FIND_PEOPLE_COUNT);
		jsonArray.add(object);
		String str = jsonArray.toJsonString();
		Map<String, String> params = new HashMap<String, String>();
		params.put(REQUEST_INPUT_KEY, str);
		HttpRes res = getHttpManager()
				.post(FIND_PEOPLE_METHOD, params, context);
		List<SuperPeopleData> list = parseFindPeople(res.getJSONObject());
		return list;
	}

	public List<TopicData> requestFindTopicList(Context context, String findName)
			throws MessageException {
		JsonArray jsonArray = new JsonArray();
		JsonObject object = new JsonObject();
		object.put("uid", ApplicationManager.getInstance().getUserId());
		object.put("keyword", findName);
		object.put("start_pos", 0);
		object.put("loose_check", 1);
		object.put("order_num", 500);
		object.put("req_num", FIND_PEOPLE_COUNT);
		jsonArray.add(object);
		String str = jsonArray.toJsonString();
		Map<String, String> params = new HashMap<String, String>();
		params.put(REQUEST_INPUT_KEY, str);
		HttpRes res = getHttpManager().post("snsui.suggestSubject4Wireless",
				params, context);
		List<TopicData> list = parseFindTopic(res.getJSONObject());
		return list;
	}

	// public void requestRelationship(Context context, String uid, String[]
	// list) throws MessageException {
	// JsonArray jsonArray = new JsonArray();
	// jsonArray.add(uid);
	// JsonArray peopleIdArray = new JsonArray();
	// for (String id : list) {
	// peopleIdArray.add(id);
	// }
	// jsonArray.add(peopleIdArray);
	// String str = jsonArray.toJsonString();
	// Map<String, String> params = new HashMap<String, String>();
	// params.put(REQUEST_INPUT_KEY, str);
	// HttpRes res = getHttpManager().post(RELATIONSHIP_METHOD, params,
	// context);
	// }

	private List<SuperPeopleData> parseSuperPeople(JsonObject object) {
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
		List<SuperPeopleData> list = new ArrayList<SuperPeopleData>();
		JsonObject json = null;
		for (int i = 0; i < strKey.length; i++) {
			json = dataObj.getJsonObject(strKey[i]);
			if (json == null || json.size() < 1) {
				continue;
			}
			list.add(new SuperPeopleData(json));
		}
		return list;
	}

	private List<SuperPeopleData> parseFindPeople(JsonObject object) {
		if (isResponseOk(object) == null) {
			return null;
		}
		Log.d("LifeHttpRequestManager", object.toJsonString());
		JsonObject dataObj = object.getJsonObject("data");
		JsonArray jsonArray = dataObj.getJsonArray("rpcret");
		// if (jsonArray == null || jsonArray.size() < 1) { //????为神马是小于1？？？？
		if (jsonArray == null || jsonArray.size() == 0) {
			return null;
		}
		List<SuperPeopleData> list = new ArrayList<SuperPeopleData>();
		JsonObject json = null;
		SuperPeopleData	data ;
		for (int i = 0; i < jsonArray.size(); i++) {
			json = (JsonObject) jsonArray.get(i);
			data=new SuperPeopleData(json);
			data.isRecommendSuperPeople=true;
			list.add(data);
		}
		return list;
	}

	private List<TopicData> parseFindTopic(JsonObject object) {
		if (isResponseOk(object) == null) {
			return null;
		}
		Log.d("LifeHttpRequestManager", object.toJsonString());
		JsonObject dataObj = object.getJsonObject("data");
		JsonArray jsonArray = dataObj.getJsonArray("rpcret");
		if (jsonArray == null || jsonArray.size() == 0) {
			return null;
		}
		List<TopicData> list = new ArrayList<TopicData>();
		JsonObject json = null;
		TopicData data = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			data = new TopicData();
			json = (JsonObject) jsonArray.get(i);
			data.name = json.getString(KEY_TOPIC_SNAME);
			data.sUid = json.getString(KEY_TOPIC_SUID);
			data.isSubscribe = json.getBool(KEY_SUBSTATUS);
			list.add(data);
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

	private static final String KEY_SID = "district_id";
	private static final String KEY_SUBSTATUS = "district_status";
	private static final String KEY_SNAME = "district_name";
	private static final String KEY_TOPIC_ARRAY = "rpcret";

	public List<DistrictData> requestDistrictList(Context context, int start,
			int limit) throws MessageException {

		Map<String, String> params = new HashMap<String, String>();
		// params.put("uid", uid);
		// if(start != null && limit != null){
		// params.put(CommonParam.KEY_DISTRICT_START, start);
		// params.put(CommonParam.KEY_DISTRICT_LIMIT, limit);
		// }
		JsonArray array = new JsonArray();
		array.add(String.valueOf(start));
		array.add(String.valueOf(limit));
		params.put(REQUEST_INPUT_KEY, array.toJsonString());
		HttpRes httpRes = getHttpManager().post("jt:msg.getAllDistrict",
				params, context);
		System.out.println(httpRes.getJSONObject().toJsonString());
		// String[] userDistrictIds =
		Map<String, String> params2 = new HashMap<String, String>();
		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray array2 = new JsonArray();
		array2.add(uid);
		array2.add(String.valueOf(start));
		array2.add(String.valueOf(limit));
		params2.put(REQUEST_INPUT_KEY, array2.toJsonString());
		HttpRes userDistrictHttpRes = getHttpManager().post(
				"jt:msg.getUserFollowedDistrict", params2, context);

		return parserDistrictData(httpRes.getJSONObject(),
				userDistrictHttpRes.getJSONObject());
	}

	/**
	 * 
	 * @param context
	 * @param dids
	 * @param type
	 *            {@link RequestType} <br>
	 *            请求类型，订阅、取消订阅
	 * @return
	 * @throws MessageException
	 */
	public String requestFollowDistrict(Context context, String dids,
			RequestType type) throws MessageException {
		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray paramsArray = new JsonArray();
		paramsArray.add(uid);
		// JsonArray jsonIds = new JsonArray();
		// jsonIds.add(dids);
		paramsArray.add(dids);
		String str = paramsArray.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(REQUEST_INPUT_KEY, str);
		String method = "jt:msg.followDistrict";
		if (type == RequestType.UNFOLLOW) {
			method = "jt:msg.unfollowDistrict";
		}
		HttpRes httpRes = getHttpManager().post(method, paramMap, context);
		System.out.println(httpRes.getJSONObject().toJsonString());
		return httpRes.getJSONObject().getString("err");
	}

	private List<DistrictData> parserDistrictData(JsonObject jsonObj,
			JsonObject userDistrictJsonObj) {
		List<DistrictData> districtList = new ArrayList<DistrictData>(0);
		JsonArray allDistrict = null;
		JsonArray userDistrictIds = null;
		try {
			allDistrict = jsonObj.getJsonObject(CommonParam.RESULT_KEY_DATA)
					.getJsonObject(KEY_TOPIC_ARRAY)
					.getJsonArray(CommonParam.RESULT_KEY_DATA);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("LifeHttpRequestManager", "all District is null");
			return districtList;
		}
		try {
			userDistrictIds = userDistrictJsonObj
					.getJsonObject(CommonParam.RESULT_KEY_DATA)
					.getJsonObject(KEY_TOPIC_ARRAY)
					.getJsonArray(CommonParam.RESULT_KEY_DATA);
		} catch (Exception e) {
			Log.d("LifeHttpRequestManager", "user followed District is null");
			e.printStackTrace();
		}
		DistrictData data = null;
		JsonObject districtJsonObj = null;
		for (JsonValue value : allDistrict.getValue()) {
			if (value instanceof JsonObject) {
				data = new DistrictData();
				districtJsonObj = (JsonObject) value;
				data.id = districtJsonObj.getString(KEY_SID);
				boolean isFollowed = false;
				if (userDistrictIds != null) {
					for (JsonValue idValue : userDistrictIds.getValue()) {
						JsonObject idObj = (JsonObject) idValue;
						if (idObj != null) {
							String id = idObj.getString("district_id");
							if (id != null && id.equals(data.id)) {
								isFollowed = true;// ((JsonObject)value).getBool(KEY_SUBSTATUS);
								break;
							}
						}
					}
				}
				data.name = ((JsonObject) value).getString(KEY_SNAME);
				data.isFollowed = isFollowed;// ((JsonObject)value).getBool(KEY_SUBSTATUS);
				districtList.add(data);
			}
		}
		return districtList;
	}

	public JsonObject requestShareClassify(Context context)
			throws MessageException {
		Map<String, String> paramMap = new HashMap<String, String>();
		HttpRes httpRes = getHttpManager().post("jt:tag.getClasses", paramMap,
				context);
		return httpRes.getJSONObject();
	}

	public JsonObject requestClassifyFeed(Context context, String clsid,
			int page, int limit, boolean hasoLcation, boolean hasPrice)
			throws MessageException {
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(clsid);
		jsonArray.add(String.valueOf(page));
		jsonArray.add(String.valueOf(limit));
		// JsonObject opration = new JsonObject();
		// opration.put("has_location", "0");//String.valueOf(false)
		// opration.put("has_price", "0")20;//String.valueOf(false)
		// jsonArray.add(opration);
		Map<String, String> params = new HashMap<String, String>();
		params.put(REQUEST_INPUT_KEY, jsonArray.toJsonString());
		HttpRes httpRes = getHttpManager().post("jt:mobile.getWaterfallPosts",
				params, context);// 原接口名 jt:tag.getPostsArrForWireless
		return httpRes.getJSONObject();
	}

	public JsonObject requestTagClassifyFeed(Context context, String clsid,
			String tagid, int page, int limit, boolean hasoLcation,
			boolean hasPrice) throws MessageException {
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(clsid);
		jsonArray.add(tagid);
		jsonArray.add(String.valueOf(page));
		jsonArray.add(String.valueOf(limit));
		Map<String, String> params = new HashMap<String, String>();
		params.put(REQUEST_INPUT_KEY, jsonArray.toJsonString());
//		HttpRes httpRes = getHttpManager().post(
//				"jt:tag.getPostsArrForFrontWireless", params, context);老接口
		HttpRes httpRes = getHttpManager().post(
		"jt:mobile.getFeedByClass", params, context);
		return httpRes.getJSONObject();
	}

	public CategoryData requestTagAllInfo(Context context, String clsid)
			throws MessageException {
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(clsid);
		Map<String, String> params = new HashMap<String, String>();
		params.put(REQUEST_INPUT_KEY, jsonArray.toJsonString());
		//jt:tag.getTagesForClass
		HttpRes httpRes = getHttpManager().post("jt:tag.getClassesAndTagsForFirstClassWireless",
				params, context);
		JsonObject resultObject = httpRes.getJSONObject();
		JsonObject obj = resultObject.getJsonObject("data").getJsonObject(
				"rpcret");
		if (obj == null || obj.size() < 1) {
			return null;
		}
		CategoryData data = new CategoryData(obj);
		return data;
	}
}
