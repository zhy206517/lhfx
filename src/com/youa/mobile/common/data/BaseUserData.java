package com.youa.mobile.common.data;

import com.youa.mobile.parser.JsonObject;

public class BaseUserData implements UserData {
	private String userId;
	private String userName;
	private String headerImageId;
	private String sex;
	public BaseUserData(JsonObject json) {
		userId = json.getString("uid");
		userName = json.getString("nickname");//username
		sex = json.getString("sex");
		headerImageId = json.getString("head_imid");
	}
	
	public BaseUserData(
			String userId,
			String userName,
			String headerImageId,
			String sex) {
		this.userId = userId;
		this.userName = userName;
		this.sex = sex;
		this.headerImageId = headerImageId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getHeaderImgid() {
		return headerImageId;
	}
	
	public String getSexInt() {
		return sex;
	}

	@Override
	public long getOrderTime() {
		// TODO Auto-generated method stub
		return 0;
	}
}
