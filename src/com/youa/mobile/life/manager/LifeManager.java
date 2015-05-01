package com.youa.mobile.life.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.youa.mobile.common.base.BaseManager;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.manager.ParserListView;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.jingxuan.data.CategoryData;
import com.youa.mobile.jingxuan.data.TagInfoData;
import com.youa.mobile.life.action.DistrictAction.RequestType;
import com.youa.mobile.life.data.DistrictData;
import com.youa.mobile.life.data.ShareClassifyData;
import com.youa.mobile.life.data.SuperPeopleClassify;
import com.youa.mobile.life.data.SuperPeopleData;
import com.youa.mobile.life.data.UserInfo;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;
import com.youa.mobile.parser.JsonValue;
import com.youa.mobile.theme.data.TopicData;

public class LifeManager extends BaseManager {

	private LifeHttpRequestManager mRequestManger;

	public LifeHttpRequestManager getHttpRequestManger() {
		if (mRequestManger == null) {
			mRequestManger = new LifeHttpRequestManager();
		}
		return mRequestManger;
	}

	public List<SuperPeopleData> requestSuperPeopleList(Context context)
			throws MessageException {
		return getHttpRequestManger().requestSuperPeopleList(context);
	}

	public List<SuperPeopleData> requestSuperPeopleList(Context context,
			String key) throws MessageException {
		return getHttpRequestManger().requestSuperPeopleList(context, key);
	}

	public List<SuperPeopleClassify> requestSuperPeopleClassify(Context context)
			throws MessageException {
		JsonObject httpJson = getHttpRequestManger()
				.requestSuperPeopleClassify(context);
		return parserSuperPeopleClassify(httpJson);
	}

	public List<SuperPeopleData> requestFindPeopleList(Context context, String findName)
			throws MessageException {
		return getHttpRequestManger().requestFindPeopleList(context, findName);
	}

	public List<TopicData> requestFindTopicList(Context context, String findName)
			throws MessageException {
		return getHttpRequestManger().requestFindTopicList(context, findName);
	}

	// --------------------
	public List<DistrictData> requestDistrictList(Context context, int start,
			int limit) throws MessageException {
		return getHttpRequestManger()
				.requestDistrictList(context, start, limit);
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
		return getHttpRequestManger()
				.requestFollowDistrict(context, dids, type);
	}

	public List<ShareClassifyData> requestShareClassify(Context context)
			throws MessageException {
		JsonObject json = getHttpRequestManger().requestShareClassify(context);// .requestClassifyFeed(context,
																				// "86c1450e74ab92ff22b1b1f8",
																				// 0,
																				// 100,
																				// true,
																				// true);
																				// //.requestShareClassify(context)
		System.out.println(json.toJsonString());
		List<ShareClassifyData> classifyList = parserShareClassify(json);
		return classifyList;
	}

	/**
	 * 加载feed内容
	 * 
	 * @param context
	 * @param feedId
	 * @return
	 * @throws MessageException
	 */
	public List<HomeData> requestShareClassifyFeedData(Context context,
			String clsid, int page, int limit, boolean hasoLcation,
			boolean hasPrice) throws MessageException {
		JsonObject jsonObj = getHttpRequestManger().requestClassifyFeed(
				context, clsid, page, limit, true, true);// "86c1450e74ab92ff22b1b1f8"
		List<HomeData> homeData = ParserListView.getInstance().parserFriend(
				context, jsonObj);
		return homeData;
	}

	/**
	 * 加载feed内容
	 * 
	 * @param context
	 * @param feedId
	 * @return
	 * @throws MessageException
	 */
	public List<HomeData> requestTagClassifyFeedData(Context context,
			String clsid, String tagid, int page, int limit,
			boolean hasoLcation, boolean hasPrice) throws MessageException {
		JsonObject jsonObj = getHttpRequestManger().requestTagClassifyFeed(
				context, clsid, tagid, page, limit, true, true);// "86c1450e74ab92ff22b1b1f8"
		List<HomeData> homeData = ParserListView.getInstance().parserFriend(
				context, jsonObj);
		return homeData;
	}

	/**
	 * 加载Tag内容
	 * 
	 * @param context
	 * @param feedId
	 * @return
	 * @throws MessageException
	 */
	public CategoryData requestTagAllInfo(Context context, String clsid)
			throws MessageException {
		return getHttpRequestManger().requestTagAllInfo(context, clsid);
	}

	/**
	 * 解析推荐分享分类
	 */

	private List<ShareClassifyData> parserShareClassify(JsonObject json) {
		List<ShareClassifyData> shareClassifyList = null;
		if (json != null) {
			JsonObject dataObj = json.getJsonObject("data");
			if (dataObj != null) {
				JsonArray array = dataObj.getJsonArray("rpcret");
				if (array != null) {
					JsonObject obj;
					ShareClassifyData shareClassify/* = new ShareClassifyData() */;
					shareClassifyList = new ArrayList<ShareClassifyData>(0);
					for (JsonValue jsonValue : array.getValue()) {
						if (jsonValue instanceof JsonObject) {
							obj = (JsonObject) jsonValue;
							shareClassify = new ShareClassifyData();
							shareClassify.id = obj.getString("clsid");
							shareClassify.name = obj.getString("name");
							shareClassifyList.add(shareClassify);
							// shareClassify.
						}
					}
				}
			}
		}
		return shareClassifyList;
	}

	private List<SuperPeopleClassify> parserSuperPeopleClassify(JsonObject josn) {
		if (isResponseOk(josn) == null) {
			return null;
		}
		List<SuperPeopleClassify> list = new ArrayList<SuperPeopleClassify>(0);
		JsonObject jsonobject = josn.getJsonObject("data").getJsonObject(
				"rpcret");
		if (jsonobject == null)
			return null;
		String[] str = jsonobject.getKeys();
		SuperPeopleClassify supperPeople = null;
		JsonObject jsonobjecttmp = null;
		for (int i = 0; i < str.length; i++) {
			jsonobjecttmp = jsonobject.getJsonObject(str[i]);
			if (jsonobjecttmp == null) {
				continue;
			}
			supperPeople = new SuperPeopleClassify(jsonobjecttmp);
			list.add(supperPeople);
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
