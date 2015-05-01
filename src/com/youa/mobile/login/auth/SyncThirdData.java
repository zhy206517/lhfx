/*package com.youa.mobile.login.auth;

import java.io.Serializable;

import com.youa.mobile.parser.JsonObject;

public class SyncThirdData implements Serializable{
	
	private static final long serialVersionUID = -1809929828410300982L;
	private String flag;
	private String status;
	private String thirdUid;
	private String expTime;
	private SupportSite site;
	private String uid;
	private String reFreshToken;
	private String modifyTime;
	private String accessToken;
	
	public SyncThirdData() {
		super();
	}
	public SyncThirdData(JsonObject json,String key) {
		super();
		setFlag(json.getString("flag"));
		setStatus(json.getString("status"));
		setThirdUid(json.getString("third_key"));
		setExpTime(json.getString("exp_time"));
		setSite(parseSupportSite(key, json.getString("third_type")));
		setUid(json.getString("userid"));
		setReFreshToken(json.getString("refresh_token"));
		setModifyTime(json.getString("modify_time"));
		setAccessToken(json.getString("access_token"));
	}
	
	public SupportSite parseSupportSite(String siteName, String siteKey){
		if(siteName == null || "".equals(siteName) || siteKey == null || "".equals(siteKey)){
			return null;
		}
		for(SupportSite tag : SupportSite.values()){
			if(tag.getSiteTag().equals(siteName)){
				return tag;
			}
		}
		return null;
	}
	
	
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getThirdUid() {
		return thirdUid;
	}
	public void setThirdUid(String thirdUid) {
		this.thirdUid = thirdUid;
	}
	public String getExpTime() {
		return expTime;
	}
	public void setExpTime(String expTime) {
		this.expTime = expTime;
	}
	public SupportSite getSite() {
		return site;
	}
	public void setSite(SupportSite site) {
		this.site = site;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getReFreshToken() {
		return reFreshToken;
	}
	public void setReFreshToken(String reFreshToken) {
		this.reFreshToken = reFreshToken;
	}
	public String getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public Boolean isExpired(){
		long expTimeLong = 0;
		try {
			expTimeLong = Long.parseLong(expTime);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
		if((expTimeLong-60*60) * 1000 < System.currentTimeMillis()){
			return true;
		}
		return false;
	}
	
//	"sina":{"flag":"1","status":0,"third_key":"1645447780","exp_time":"",
//		"third_type":"2",
//		"create_time":"1336443795",
//    "userid":"c85683ead0c6c9ecfb5987a0","refresh_token":"",
//    "modify_time":"1336443795",
//    "access_token":"8227a75a7009582670bbe8df4f439359"}
}*/
