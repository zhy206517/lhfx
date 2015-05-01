package com.youa.mobile.login.action;

import java.util.Map;

import android.content.Context;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.login.action.LoginAction.ILoginResultListener;
import com.youa.mobile.login.manager.LoginManager;
import com.youa.mobile.login.util.LoginConstant;
import com.youa.mobile.login.util.LoginUtil;


public class LoginAction extends BaseAction<ILoginResultListener> {

	private final static String TAG = "LoginAction";

	public interface ILoginResultListener extends IResultListener, IFailListener {
		public void onStart();
		public void onFinish();
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ILoginResultListener callback) throws Exception {
		LoginUtil.LOGD(TAG, "enter onExecute   : ");
		if (context == null || params == null || params.size() <= 0 || callback == null) {
			throw new IllegalArgumentException();
		}
		callback.onStart();
		String username = (String) params.get(LoginConstant.WEB_LOGIN_USERNAME);
		String password = (String) params.get(LoginConstant.WEB_LOGIN_PASSWORD);
		try {
			new LoginManager().login(context, username, password);
			ApplicationManager.getInstance().init(context);
			callback.onFinish();
		} catch (MessageException e) {
			if (e.getErrCode().startsWith(LoginConstant.LOGIN_NOT_MATCH)
					|| LoginConstant.LOGIN_DB_QUERY_ERROR.equals(e.getErrCode())
					|| LoginConstant.LOGIN_SESSION_NOT_EXIST.equals(e.getErrCode())) {
				e.setResID(R.string.login_not_match);
			}
			throw e;
			
		}
	}
}
