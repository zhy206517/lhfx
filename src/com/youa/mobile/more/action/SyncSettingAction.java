/*package com.youa.mobile.more.action;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.login.auth.SupportSite;
import com.youa.mobile.login.auth.SyncThirdData;
import com.youa.mobile.more.action.SyncSettingAction.SyncSettingResultListener;
import com.youa.mobile.more.manager.SyncSettingManager;

public class SyncSettingAction extends BaseAction<SyncSettingResultListener> {
	
	public static final String REQUEST_TYPE = "request_type";
	public static final String THIRD_UID = "third_uid";
	public static final String SUPPORT_SITE = "support_site";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String EXP_TIME = "exp_time";
	
	public interface SyncSettingResultListener extends IResultListener,
			IFailListener {
		void onStart();
		void onFinish(int resourceID,SupportSite site, RequestType requestType, String thirdUid);
		void onGetSyncListFinish(List<SyncThirdData> datas);
		void onFail(int resourceID,SupportSite site);
	}
	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			SyncSettingResultListener callback) throws Exception {
		callback.onStart();
		RequestType requestType = (RequestType)params.get(REQUEST_TYPE);
		SyncSettingManager ssm 	= new SyncSettingManager();
		String thirdUid 		= (String)params.get(THIRD_UID);
		SupportSite site 		= (SupportSite)params.get(SUPPORT_SITE);
		try {
			switch (requestType) {
				case THIRD_SYNC_LIST:
					List<SyncThirdData> datas = ssm.getUserThirdSyncList(context);
					callback.onGetSyncListFinish(datas);
					break;
				case BINDING_THIRD_ACCOUNT:
					String accessToken 	= (String)params.get(ACCESS_TOKEN);
					String refreshToken = (String)params.get(REFRESH_TOKEN);
					String expTime 		= String.valueOf((Long)params.get(EXP_TIME));
					ssm.bindThird(context, thirdUid, site, accessToken, refreshToken, expTime);
					callback.onFinish(R.string.bind_success, site, requestType, thirdUid);
					break;
				case UNBINDING_THIRD_ACCOUNT:
					ssm.unBindThird(context, thirdUid, site);
					callback.onFinish(R.string.unbind_success, site, requestType, thirdUid);
					break;
				case BINDING_LEHO:
					break;
				case UNBINDING_LEHO:
					break;
			}
		} catch (MessageException e) {
			String errCode = e.getErrCode();
			int resID = -1;
			if(errCode.contains("third.conflict")){//passport.u_relateToLocal.third.conflict.userid.210063206.third_key.1645447780  第三方帐号绑定冲突
//				e.setResID(R.string.third_bind_conflict);
				resID = R.string.third_bind_conflict;
			}else if(errCode.contains("user.has.binded.one")){
//				e.setResID(R.string.user_has_binded_one);
				resID = R.string.user_has_binded_one;
			}else if(errCode.contains("account.not.existed")){
//				e.setResID(R.string.account_not_existed);
				resID = R.string.account_not_existed;
			}else if(errCode.contains("user.donot.has.the.third")){
//				e.setResID(R.string.user_donot_has_the_third);
				resID = R.string.user_donot_has_the_third;
			}else if(errCode.contains("user has no local account")){
//				e.setResID(R.string.user_has_no_local_account);
				resID = R.string.user_has_no_local_account;
			}
			if(resID > 0)
				callback.onFail(resID, site);
			else
				throw e;
		}
		
	}
	
	public enum RequestType {
		THIRD_SYNC_LIST, BINDING_THIRD_ACCOUNT, UNBINDING_THIRD_ACCOUNT, BINDING_LEHO, UNBINDING_LEHO
	}
}
*/