package com.youa.mobile.news.data;

import com.youa.mobile.news.util.NewsUtil;
import com.youa.mobile.parser.JsonObject;

public class FavoritePeopleData {

	private final static String TAG = "FavoritePeopleData";
	private String uId;
	private String uName;
	private String uImageId;
	private String mSex;

	public FavoritePeopleData() {
		
	}

	public FavoritePeopleData(JsonObject json) {
		NewsUtil.LOGD(TAG, "FavoritePeopleData data json:" + json.toJsonString());
		uId = json.getString("userid");
		uName = json.getString("nickname");//username
		uImageId = json.getString("head_imid");
		mSex= json.getString("sex");
	}

	public String getUId() {
		return uId;
	}
	public String getSex() {
		return mSex;
	}
	public void setUId(String id) {
		uId = id;
	}
	public String getUName() {
		return uName;
	}
	public void setUName(String name) {
		uName = name;
	}
	public String getUImageId() {
		return uImageId;
	}
	public void setUImageId(String imageId) {
		uImageId = imageId;
	}

}
