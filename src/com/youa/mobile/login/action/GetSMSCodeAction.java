package com.youa.mobile.login.action;

import java.util.Map;

import android.content.Context;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.params.CommonParam;
import com.youa.mobile.login.action.GetSMSCodeAction.SMSCodeListner;
import com.youa.mobile.login.manager.LoginHttpRequestManager;
import com.youa.mobile.login.util.LoginConstant;
import com.youa.mobile.login.util.LoginUtil;
import com.youa.mobile.parser.JsonObject;

public class GetSMSCodeAction extends BaseAction<SMSCodeListner>{
	public interface SMSCodeListner  extends IResultListener{
		void onFinish(int resourceID);
		void onError(int resourceID);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			SMSCodeListner resultListener) throws Exception {
		String mobilNumber = (String) params.get(LoginConstant.WEB_REGIST_PHONENUMBLE);
		try {
			JsonObject response = new LoginHttpRequestManager().getMmsCode(context, mobilNumber);
			if(response.getString("err").equals(CommonParam.VALUE_ERROR_OK)){
				String rpcretCode = response.getJsonObject("data").getString("rpcret");
				if("err.ok".equals(rpcretCode)){
					resultListener.onFinish(R.string.regist_success_getvcode);
				}else if(null != rpcretCode && LoginUtil.isNumeric(rpcretCode)){
					resultListener.onError(R.string.regist_registed);
				}else{
					
				}
			}else{
				resultListener.onError(R.string.regist_error_getvcode);
			}
		} catch (MessageException e) {
			resultListener.onError(R.string.regist_error_getvcode);
			e.printStackTrace();
		}
	}
}
