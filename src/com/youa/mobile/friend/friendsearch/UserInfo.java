package com.youa.mobile.friend.friendsearch;

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
		userId = obj.getString("uid");
		userName = obj.getString("name");
		heardImgId = obj.getString("head_imid");
		sexInt = obj.getString("sex");
	}
	
	public int getSexResId() {
		if(sexInt == null) {
			return R.drawable.head_men;//R.drawable.information_header_man
		} else if(sexInt.equals(SEX_MAN)) {
			return R.drawable.head_men;//R.drawable.information_header_man
		} else {
			return R.drawable.head_women;//R.drawable.information_header_woman
		}
	}
}
