package com.youa.mobile.login.action;

import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.login.action.CheckLoginAction.CheckResultListner;
import com.youa.mobile.login.manager.LoginManager;

public class CheckLoginAction extends BaseAction<CheckResultListner>{
	public interface CheckResultListner extends IResultListener {
		void onHasUser();
		void onNoUser();		
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			CheckResultListner resultListener) throws Exception {
		boolean isHasUser = new LoginManager().isHasUser(context);
		if(isHasUser) {
			resultListener.onHasUser();
		} else {
			resultListener.onNoUser();
		}
	}
}
