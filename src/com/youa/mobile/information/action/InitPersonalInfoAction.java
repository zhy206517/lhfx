package com.youa.mobile.information.action;

import java.util.Map;

import android.content.Context;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.information.data.PersonalInformationData;
import com.youa.mobile.information.manager.PersonalInfoManager;

import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.information.action.InitPersonalInfoAction.IInitResult;

public class InitPersonalInfoAction extends BaseAction <IInitResult>{

	public interface IInitResult extends IResultListener, IFailListener {
		void onStart();
		void onShowMessage(int res);
		void onEnd(PersonalInformationData personalInformationData);
	}
	public static final String KEY_UID = "uid";

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			IInitResult resultListener) throws Exception {
		resultListener.onStart();
		String uid = (String)params.get(KEY_UID);
		PersonalInfoManager mPersonalInfoManager = new  PersonalInfoManager();
		PersonalInformationData personalInformationData = 
			       mPersonalInfoManager.searchPersonalInfo(context, uid);
		String errorCode = mPersonalInfoManager.getErrorCode();
		if(errorCode != null) {
			resultListener.onShowMessage(R.string.common_network_error);
		}
		resultListener.onEnd(personalInformationData);
	}
}


