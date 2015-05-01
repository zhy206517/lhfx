package com.youa.mobile.information.manager;

import java.io.FileNotFoundException;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseManager;
import com.youa.mobile.common.data.UserData;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.http.netsynchronized.FileUploader;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.information.data.PersonalInformationData;
import com.youa.mobile.information.data.ShowCountData;
import com.youa.mobile.information.provider.PersonalInfoProvider;
import com.youa.mobile.life.data.SuperPeopleData;

public class PersonalInfoManager extends BaseManager {
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
	
	private FriendManager mFriendManager;
	public FriendManager getFriendManager() {
		if(mFriendManager == null) {
			mFriendManager = new FriendManager();
		}
		return mFriendManager;
	}
	
	private String errorCode = "";
	
	public PersonalInformationData searchPersonalInfo(
			Context context, 
			String uid) throws MessageException {
		PersonalInformationData mPersonalInformationData = null;
		try {
			errorCode = null;
			mPersonalInformationData = 
				getPersonalInfoRequestManager().requestPersonalInformation(context, uid);
			String loginID = ApplicationManager.getInstance().getUserId();
//			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>loginID:" + loginID);
			if(!TextUtils.isEmpty(loginID)&&!loginID.equals(uid)) {//读取关系
				String status = getPersonalInfoRequestManager().getFollowerStatus(
						context,
						loginID,
						uid);
				mPersonalInformationData.setRelationStatus(status);
			}			
//			getPersonalInfoProvider().replaceInformation(mPersonalInformationData);
		} catch(MessageException e) {
//			errorCode = e.getErrCode();
//			mPersonalInformationData = getPersonalInfoProvider().getInformation(uid);
//			if(mPersonalInformationData == null) {
				throw e;
//			}
		}
		return mPersonalInformationData;
	}
	
	public List<HomeData> searchOwnFeedList(
			Context context, 
			String uid,
			int currentPageIndex) throws MessageException {

		List<HomeData> dataList = 
			getPersonalInfoRequestManager().requestOwnFeedList(
					context,
					uid,
					currentPageIndex);

		return dataList;
	}
	
	public List<HomeData> searchEnjoyFeedList(
			Context context, 
			String uid,
			int currentPageIndex) throws MessageException {

		List<HomeData> dataList = 
			getPersonalInfoRequestManager().requestEnjoyFeedList(
					context,
					uid,
					currentPageIndex);

		return dataList;
	}
	
	public List<UserData> searchAttentUserList(
			Context context, 
			String attentType,
			String attentuid,
			long offset) throws MessageException {

		List<UserData> dataList = 
			getPersonalInfoRequestManager().requestAttentUserList(
					context,
					attentType,
					attentuid,
					offset);

		return dataList;
	}
	public List<SuperPeopleData> searchAttentSuperUserList(
			Context context, 
			String attentType,
			String attentuid,
			long offset) throws MessageException {

		List<SuperPeopleData> dataList = 
			getPersonalInfoRequestManager().requestAttentSuperUserList(
					context,
					attentType,
					attentuid,
					offset);

		return dataList;
	}
	public ShowCountData searchCount(
			Context context, 
			String uid) throws MessageException {
		ShowCountData showCountData = 
		        getPersonalInfoRequestManager().requestInforCount(context, uid);
		return showCountData;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public void saveManager(
			Context context,
			String uid,
			String username,
			String nickname,
			String sex,
			String province,
			String city,
			String counties,
			String birthdayYear,
			String birthdayMonth,
			String birthdayDay,
			String intruduce,
			String imagePath) throws MessageException {
		String imageID = null;
//		System.out.println("imagePath:" + imagePath);
		if(imagePath != null && imagePath.length() > 0) {
			FileUploader mFileUploader;
			try {
				mFileUploader = new FileUploader(imagePath, true);
			} catch (FileNotFoundException e) {
				throw new MessageException("", R.string.common_error_filenotfound);
			}
			imageID = mFileUploader.startUpLoad(context);
		}
		
		getPersonalInfoRequestManager().requestSaveManager(
				context,
				uid,
				username,
				nickname,
				sex,
				province,
				city,
				counties,
				birthdayYear,
				birthdayMonth,
				birthdayDay,
				intruduce,
				imageID);
	}
	
	public void addAttent(
			Context context,
            String uid,
            String followedId,
            String followUname,
            String headerImageId,
            String sexInt) throws MessageException {

			getPersonalInfoRequestManager().requestAddAttent(
					context,
					uid,
					followedId);
			getFriendManager().insertFriend(followedId, followUname, headerImageId, sexInt);
	}
	public void cancelAttent(
			Context context,
            String uid,
            String followedId) throws MessageException {
			getPersonalInfoRequestManager().requestCancelAttent(
					context,
					uid,
					followedId);
			getFriendManager().deleteFriend(followedId);
	}
	public void deleteFriend(
			Context context,
            String uid) throws MessageException {
			getFriendManager().deleteFriend();
	}
}
