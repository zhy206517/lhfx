package com.youa.mobile.life.data;

import com.youa.mobile.R;
import com.youa.mobile.life.manager.LifeHttpRequestManager;
import com.youa.mobile.parser.JsonObject;

public class UserInfo {
	public String userName;
	public String heardImgId;
	public String userId;
	public String sexInt;
	public static final String SEX_MAN = "1";
	public static final String SEX_WOMAN = "2";
	
	public UserInfo(JsonObject obj) {
		userId = obj.getString(LifeHttpRequestManager.KEY_PEOPLE_UID);
		userName = obj.getString("name");
		heardImgId = obj.getString(LifeHttpRequestManager.KEY_PEOPLE_IMGID);
		sexInt = obj.getString(LifeHttpRequestManager.KEY_PEOPLE_SEX);
	}
	
	public int getSexResId() {
		if(sexInt == null) {
			return R.drawable.information_header_man;
		} else if(sexInt.equals(SEX_MAN)) {
			return R.drawable.information_header_man;
		} else {
			return R.drawable.information_header_woman;
		}
	}
}
