package com.youa.mobile.login.action;

import java.util.Map;

import android.content.Context;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.params.CommonParam;
import com.youa.mobile.login.action.RegistAction.IRegistResultListener;
import com.youa.mobile.login.manager.LoginManager;
import com.youa.mobile.login.util.LoginConstant;
import com.youa.mobile.login.util.LoginUtil;
import com.youa.mobile.parser.JsonObject;

public class RegistAction extends BaseAction<IRegistResultListener>{

	public static final String KEY_SEX = "sex";
	public static final String SEX_MAN = "1";
	public static final String SEX_WOMAN = "2";
	public static final String KEY_PROVINCE = "province";
	public static final String KEY_CITY = "city";
	public static final String KEY_COUNTIES = "counties";
	public interface IRegistResultListener extends IResultListener, IFailListener {
		public void onStart();
		public void onFinish(int resourceID);
		public void onFail(int resourceID);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			IRegistResultListener callback) throws Exception {
		if (context == null || params == null || params.size() <= 0 || callback == null) {
			throw new IllegalArgumentException();
		}
		callback.onStart();
		String phoneNumble = (String) params.get(LoginConstant.WEB_REGIST_MOBILEPHONE);
		String vcode = (String) params.get(LoginConstant.WEB_REGIST_VCODE);
		String password = (String) params.get(LoginConstant.WEB_REGIST_PASSWORD);
		String nickname = (String) params.get(LoginConstant.WEB_REGIST_NICKNAME);
		String sex = (String) params.get(LoginConstant.WEB_REGIST_SEX);
		String province = (String) params.get(LoginConstant.WEB_REGIST_PROVINCE);
		String city = (String) params.get(LoginConstant.WEB_REGIST_CITY);
		String resion = (String) params.get(LoginConstant.WEB_REGIST_RESION);
		try {
			LoginManager lm = new LoginManager();
			JsonObject result = lm.regist(context, phoneNumble, vcode, password, nickname, sex, province, city, resion);
			JsonObject data = result.getJsonObject("data");
			String responseCode = result.getString("err");
			if (CommonParam.VALUE_ERROR_OK.equals(responseCode)
					&& null != data && null != data.getString("userid")
					&& !"".equals(data.getString("userid"))) {
				LoginUtil.writeUserToPrefForAutoLogin(context, phoneNumble, password);
				lm.login(context, phoneNumble, password);
				ApplicationManager.getInstance().init(context);
				callback.onFinish(R.string.regist_success);
			}else{
				callback.onFail(R.string.regist_fail);
			}
			/*
			 * if("err.vcode.expired".equals(responseCode) 
					|| "err.wrong.vcode".equals(responseCode)){
				callback.onFail(R.string.regist_response_vcodeerror);
			}else if("sns.api.err.username.long.or.short".equals(responseCode)){
				callback.onFail(R.string.regist_nickname_error);
			}else if("err.uname.used".equals(responseCode)){
				callback.onFail(R.string.regist_nickname_used);
			}else if("err.mobile.used".equals(responseCode)){
				callback.onFail(R.string.regist_err_mobile_used);
			}else*/
		} catch (MessageException e) {
			String errCode = e.getErrCode();
			if("err.vcode.expired".equals(errCode) 
					|| "err.wrong.vcode".equals(errCode)){
				callback.onFail(R.string.regist_response_vcodeerror);
			}else if("sns.api.err.username.long.or.short".equals(errCode)){
				callback.onFail(R.string.regist_nickname_error);
			}else if("err.uname.used".equals(errCode) || "sns.api.err.uname.used".equals(errCode)){
				callback.onFail(R.string.regist_nickname_used);
			}else if("err.mobile.used".equals(errCode)){
				callback.onFail(R.string.regist_err_mobile_used);
			}else{
				throw e;
			}
		}
	}
}
