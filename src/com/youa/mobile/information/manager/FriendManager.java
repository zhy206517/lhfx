package com.youa.mobile.information.manager;

import java.util.List;

import android.content.Context;

import com.youa.mobile.common.data.BaseUserData;
import com.youa.mobile.common.data.UserData;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.information.provider.PersonalInfoProvider;

public class FriendManager {
	private PersonalInfoProvider mPersonalInfoProvider;
	public PersonalInfoProvider getPersonalInfoProvider() {
		if(mPersonalInfoProvider == null) {
			mPersonalInfoProvider = new PersonalInfoProvider();
		}
		return mPersonalInfoProvider;
	}
	private PersonalInfoRequestManager mPersonalInfoRequestManager;
	public PersonalInfoRequestManager getPersonalInfoRequestManager() {
		if(mPersonalInfoRequestManager == null) {
			mPersonalInfoRequestManager = new PersonalInfoRequestManager();
		}
		return mPersonalInfoRequestManager;
	}
	public List<UserData> downloadFriendList(Context context) throws MessageException {
		List<UserData> userList = getPersonalInfoRequestManager().requestFriendList(context);
		getPersonalInfoProvider().replaceInformation(userList);
		return userList;
	}
	
	public List<UserData> searchFriendList() {
		List<UserData> dataList = getPersonalInfoProvider().searchFriendList();
		return dataList;
	}
	
	public void insertFriend(
			String userId,
			String userName,
			String headerImageId,
			String sex//1男 2女
			) {
		BaseUserData userData = new BaseUserData(
				userId,
				userName,
				headerImageId,
				sex);
		getPersonalInfoProvider().replaceInformation(userData);
	}
	
	public void deleteFriend(
			String userId
			) {
		getPersonalInfoProvider().deleteInformation(userId);
	}
	public void deleteFriend() {
		getPersonalInfoProvider().deleteInformation();
	}
}
