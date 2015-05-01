package com.youa.mobile.jingxuan.data;

import android.util.Log;

import com.youa.mobile.parser.JsonObject;

public class ClassifyTagInfoData {
	public String TAG_ARR_KEY = "tagArr";
	public String TAG_CLSID_KEY = "clsid";
	public String TAG_NAME_KEY = "name";
	public String FEED_POSTID_KEY = "postid";
	public String FEED_TAGNAME_KEY = "tname";
	public String FEED_DESC_KEY = "desc";
	public String FEED_TID_KEY = "tid";
	public String FEED_IMAGEID_KEY = "imageid";

	public ClassifyTagInfoData(JsonObject obj) {
		JsonObject jsonObject = obj.getJsonObject(TAG_ARR_KEY);
		String[] strKey = jsonObject.getKeys();
		JsonObject json = null;
		int index = strKey.length;
		mFeedData = new FeedData[index];
		for (int i = 0; i < index; i++) {
			json = jsonObject.getJsonObject(strKey[i]);
			mFeedData[i] = new FeedData(json);
		}
		mClsid = obj.getString(TAG_CLSID_KEY);
		mName = obj.getString(TAG_NAME_KEY);
	}

	public String mClsid;
	public String mName;
	public FeedData mFeedData[];

	public class FeedData {
		public String mPostid;
		public String mTagName;
		public String mDescription;
		public String mTagId;
		public String mImageid;

		public FeedData(JsonObject obj) {
			mPostid = obj.getString(FEED_POSTID_KEY);
			mTagName = obj.getString(FEED_TAGNAME_KEY);
			mDescription = obj.getString(FEED_DESC_KEY);
			mTagId = obj.getString(FEED_TID_KEY);
			mImageid = obj.getString(FEED_IMAGEID_KEY);
		}
	}
}
