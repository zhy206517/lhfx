package com.youa.mobile.input.manager;

import java.util.ArrayList;
import java.util.List;

import com.youa.mobile.input.data.ImageData;
import com.youa.mobile.input.data.PublishData;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.jingxuan.data.ClassifyTagInfoData;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DraftManager {

	private final static String TAG = "DraftManager";
	private final static String SHARE_PREF_NAME = "draft";
	public final static String KEY_CONTENT = "data";
	public final static String KEY_CONTENT_IMAGE = "image";
	public final static String KEY_CONSUME_AV_PRICE = "av_price";
	public final static String KEY_CONSUME_PRICE = "price";
	public final static String KEY_CONSUME_PEOPLE_NUM = "people_num";
	public final static String KEY_MANY_PEOPLE = "many_people";
	public final static String KEY_CONSUME_PLACE = "place";
	public final static String KEY_POS_ID = "pid";
	public final static String KEY_POS_LATITUDE = "latitude";
	public final static String KEY_POS_LONGITUDE = "longitude";
	private static DraftManager draftManager;

	private DraftManager() {

	}

	public static DraftManager getInstance() {
		if (draftManager == null) {
			draftManager = new DraftManager();
		}
		return draftManager;
	}

	public PublishData SearchDrafeData(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SHARE_PREF_NAME, 0);
		String content = sharedPreferences.getString(KEY_CONTENT, "");
		String contentImage = sharedPreferences
				.getString(KEY_CONTENT_IMAGE, "");
		String consumeAvPrice = sharedPreferences
				.getString(KEY_CONSUME_AV_PRICE, "");
		String consumePlace = sharedPreferences
				.getString(KEY_CONSUME_PLACE, "");
		String consumePrice = sharedPreferences
				.getString(KEY_CONSUME_PRICE, "");
		String consumePeopleNum = sharedPreferences
				.getString(KEY_CONSUME_PEOPLE_NUM, "");
		int posLatitude = sharedPreferences.getInt(KEY_POS_LATITUDE, 0);
		int posLongitude = sharedPreferences.getInt(KEY_POS_LONGITUDE, 0);
		boolean isManyPeople = sharedPreferences.getBoolean(KEY_MANY_PEOPLE,
				false);
		String plid = sharedPreferences.getString(KEY_POS_ID, "");
		JsonObject jsonObject = JsonObject.parseObject(contentImage);
		return new PublishData(content, parseImageData(jsonObject),
				consumePlace, consumeAvPrice, isManyPeople, posLatitude,
				posLongitude, plid,consumePrice,consumePeopleNum);
	}

	private ArrayList<ImageData> parseImageData(JsonObject object) {
		JsonArray dataObj = object.getJsonArray("imageInfo");
		if (dataObj == null) {
			return null;
		}
		int index = dataObj.size();
		if (dataObj == null || index < 1) {
			return null;
		}
		ArrayList<ImageData> arr = new ArrayList();
		JsonObject json = null;
		for (int i = 0; i < index; i++) {
			json = (JsonObject) dataObj.get(i);
			arr.add(new ImageData(json));
		}
		return arr;
	}

	private JsonObject parseJsonObject(ArrayList<ImageData> imageData) {
		JsonArray objArr = new JsonArray();
		JsonObject obj = null;
		for (int i = 0; i < imageData.size(); i++) {
			obj = new JsonObject();
			obj = imageData.get(i).getJsonObject();
			objArr.add(obj);
		}
		obj = new JsonObject();
		obj.put("imageInfo", objArr);
		return obj;
	}

	public void saveDrafeData(Context context, PublishData data) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SHARE_PREF_NAME, 0);
		Editor edit = sharedPreferences.edit();
		edit.putString(KEY_CONTENT, data.getContent());
		edit.putString(KEY_CONSUME_AV_PRICE, data.getConsumePrice());
		edit.putString(KEY_CONTENT_IMAGE,
				parseJsonObject(data.getContentImage()).toJsonString());
		edit.putString(KEY_CONSUME_PLACE, data.getConsumePlace());
		edit.putInt(KEY_POS_LATITUDE, data.getLatitude());
		edit.putInt(KEY_POS_LONGITUDE, data.getLongitude());
		edit.putBoolean(KEY_MANY_PEOPLE, data.isManyPeople());
		edit.putString(KEY_POS_ID, data.getPlid());
		edit.putString(KEY_CONSUME_PRICE, data.mConsumePrice);
		edit.putString(KEY_CONSUME_PEOPLE_NUM, data.mPeopleNum);
		edit.commit();
	}
}
