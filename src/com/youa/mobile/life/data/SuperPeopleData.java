package com.youa.mobile.life.data;

import android.text.TextUtils;

import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.life.manager.LifeHttpRequestManager;
import com.youa.mobile.parser.JsonObject;

public class SuperPeopleData {

	private final static String TAG = "SuperPeopleData";
	private String userId;
	private String userName;
	private String userImage;
	private boolean isPayAttentionTo;
	private boolean isRelation;
	public boolean isRecommendSuperPeople;
	private String sex;
	private String signature;

	public SuperPeopleData() {

	}

	public SuperPeopleData(JsonObject obj) {
		userId = obj.getString(LifeHttpRequestManager.KEY_PEOPLE_UID);
		userName = obj.getString(LifeHttpRequestManager.KEY_PEOPLE_UNAME);
		if (TextUtils.isEmpty(userName)) {
			userName = obj.getString("name");
		}
		userImage = obj.getString(LifeHttpRequestManager.KEY_PEOPLE_IMGID);
		sex = obj.getString(LifeHttpRequestManager.KEY_PEOPLE_SEX);
		signature = obj.getString(LifeHttpRequestManager.KEY_PEOPLE_SIGNATURE);
		String falg = obj
				.getString(LifeHttpRequestManager.KEY_PEOPLE_FOLLOW_STATUS);
		if ("1".equals(falg) || "2".equals(falg)) {
			isPayAttentionTo = true;
			isRelation = true;
		} else if ("0".equals(falg)) {
			isPayAttentionTo = false;
			isRelation = true;
		} else {
			isRelation = false;
		}
		InputUtil.LOGD(TAG, "enter SuperPeopleData data <userId	  > : "
				+ userId);
		InputUtil.LOGD(TAG, "enter SuperPeopleData data <userName > : "
				+ userName);
		InputUtil.LOGD(TAG, "enter SuperPeopleData data <userImage> : "
				+ userImage);
		InputUtil.LOGD(TAG, "enter SuperPeopleData data <sex      > : " + sex);
		InputUtil.LOGD(TAG, "enter SuperPeopleData data <signature      > : "
				+ signature);
		InputUtil.LOGD(TAG,
				"enter SuperPeopleData data <isPayAttentionTo      > : "
						+ isPayAttentionTo);
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

	public String getSignature() {
		return signature;
	}

	public boolean getRelation() {
		return isRelation;
	}
}
