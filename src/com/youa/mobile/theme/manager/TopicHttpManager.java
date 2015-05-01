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
import com.youa.mobile.common.params.CommonParam;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;
import com.youa.mobile.parser.JsonValue;
import com.youa.mobile.theme.action.TopicAction;
import com.youa.mobile.theme.data.TopicData;

public class TopicHttpManager extends BaseRequestManager{
	private static final String TAG = "TopicHttpManager";
	private static final String KEY_SUID = "suid";
	private static final String KEY_SUBSTATUS = "status";
	private static final String KEY_SNAME = "sname";
	private static final String KEY_TOPIC_ARRAY = "rpcret";
	
	public void getCommendSubject(){
		
	}
	
	public List<TopicData> requestTopicList(Context context,
			String type) throws MessageException {
		JsonArray json = new JsonArray();
		json.add(TopicAction.TOPIC_UI_COMMEND);
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(CommonParam.KEY_RPCINPUT, str);
		HttpRes httpRes = getHttpManager().post("jt:msg.getCommendSubject",
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		List<TopicData> mTopicList = parserTopicList(resultObject);
		json = new JsonArray();
		String uid = ApplicationManager.getInstance().getUserId();
		json.add(uid);
		JsonArray topicIds = new JsonArray();
		for(int i = 0 ; i < mTopicList.size() ; i++){
			topicIds.add(mTopicList.get(i).sUid);
		}
		json.add(topicIds);
		paramMap.clear();
		paramMap.put(CommonParam.KEY_RPCINPUT, json.toJsonString());
		httpRes = getHttpManager().post("jt:msg.getUidSubjectStatusWl", paramMap, context);
		
		return parserTopicStatus(httpRes.getJSONObject(), mTopicList);
	}
	
	public String requestSubTopic(Context context, String suid, String sname) throws MessageException {
		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray json = new JsonArray();
		json.add(uid);
		json.add(suid);
		json.add(sname);
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(CommonParam.KEY_RPCINPUT, str);
		HttpRes httpRes = getHttpManager().post("jt:msg.followSubject",
				paramMap, context);
		System.out.println(httpRes.getJSONObject().getString("err"));
		return httpRes.getJSONObject().getString("err");
	}
	
	public String requestUnSubTopic(Context context, String suid) throws MessageException {
		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray json = new JsonArray();
		json.add(uid);
		json.add(suid);
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(CommonParam.KEY_RPCINPUT, str);
		HttpRes httpRes = getHttpManager().post("jt:msg.cancelFollowSubject",
				paramMap, context);
		return httpRes.getJSONObject().getString("err");
	}
	
	private List<TopicData> parserTopicList(JsonObject jsonObj){
		Log.d(TAG, jsonObj.toJsonString());
		JsonArray topicJsonArray = jsonObj.getJsonObject(CommonParam.RESULT_KEY_DATA).getJsonArray(KEY_TOPIC_ARRAY);
		List<TopicData> topicList = new ArrayList<TopicData>(0);
		JsonObject obj = null;
		TopicData topic = null;
		for(JsonValue value : topicJsonArray.getValue()){
			if(value instanceof JsonObject) {
				obj = (JsonObject)value;
				topic = new TopicData();
				topic.name = obj.getString(KEY_SNAME);
				topic.sUid = obj.getString(KEY_SUID);
				topicList.add(topic);
			}
		}
		return topicList;
	}
	
	private List<TopicData> parserTopicStatus(JsonObject jsonObj, List<TopicData> topicList){
		JsonArray topicJsonArray = jsonObj.getJsonObject(CommonParam.RESULT_KEY_DATA).getJsonArray(KEY_TOPIC_ARRAY);
		
		for(TopicData td : topicList){
			for(JsonValue value : topicJsonArray.getValue()){
				JsonObject obj = null;
				if(value instanceof JsonObject) {
					obj = (JsonObject)value;
					if(null != td && null != td.sUid && td.sUid.equals(obj.getString(KEY_SUID))){
						String status = obj.getString(KEY_SUBSTATUS);
						td.isSubscribe = null != status ? status.equals("1") : false;
					}
				}
			}
		}
		
		return topicList;
	}
}
