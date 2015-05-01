package com.youa.mobile.life.data;

import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.life.manager.LifeHttpRequestManager;
import com.youa.mobile.parser.JsonObject;

public class PeopleData {

	private final static String TAG = "SuperPeopleData";
	private String userId;
	private String userName;
	private String userImage;
	private boolean isPayAttentionTo;
	private String sex;

	public PeopleData() {
		
	}

	public PeopleData(JsonObject obj) {
		userId = obj.getString(LifeHttpRequestManager.KEY_PEOPLE_UID);
		userName = obj.getString(LifeHttpRequestManager.KEY_PEOPLE_UNAME);
		userImage = obj.getString(LifeHttpRequestManager.KEY_PEOPLE_IMGID);
		sex = obj.getString(LifeHttpRequestManager.KEY_PEOPLE_SEX);
		InputUtil.LOGD(TAG, "enter SuperPeopleData data <userId	  > : " + userId);
		InputUtil.LOGD(TAG, "enter SuperPeopleData data <userName > : " + userName);
		InputUtil.LOGD(TAG, "enter SuperPeopleData data <userImage> : " + userImage);
		InputUtil.LOGD(TAG, "enter SuperPeopleData data <sex      > : " + sex);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public boolean isPayAttentionTo() {
		return isPayAttentionTo;
	}

	public void setPayAttentionTo(boolean isPayAttentionTo) {
		this.isPayAttentionTo = isPayAttentionTo;
	}

	public String getSex() {
		return sex;
	}

	
}
