package com.youa.mobile.login.data;

import com.youa.mobile.login.auth.SupportSite;

public class UserInfo {
	private String userId;
	private String username;
	private String password;
	private String email;
	private String youaSession;
	private String thirdUid;
	private String accessToken;
	private String refreshToken;
	private String expTime;
	private SupportSite site;
	
	public UserInfo() {
	}

//	public UserInfo(JsonObject obj) {
////		userId = obj.getString(LifeHttpRequestManager.KEY_PEOPLE_UID);
////		username = obj.getString("name");
////		heardImgId = obj.getString(LifeHttpRequestManager.KEY_PEOPLE_IMGID);
////		sexInt = obj.getString(LifeHttpRequestManager.KEY_PEOPLE_SEX);
//		//UserInfo user = new UserInfo();
//		String userid = obj.getString("userid");
//		userId = userid;
//	}
	
	public UserInfo(String username, String password) {
		this(username, password, null);
	}

	public UserInfo(String username, String password, String email) {
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public UserInfo(String thirdUid, SupportSite site){
		this.thirdUid = thirdUid;
		this.site = site;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getThirdUid() {
		return thirdUid;
	}

	public void setThirdUid(String thirdUid) {
		this.thirdUid = thirdUid;
	}

	public SupportSite getSite() {
		return site;
	}

	public void setSite(SupportSite site) {
		this.site = site;
	}

	public String getYouaSession() {
		return youaSession;
	}

	public void setYouaSession(String youaSession) {
		this.youaSession = youaSession;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getExpTime() {
		return expTime;
	}

	public void setExpTime(String expTime) {
		this.expTime = expTime;
	}
	
}
