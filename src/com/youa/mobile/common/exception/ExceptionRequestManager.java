package com.youa.mobile.common.exception;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;
import com.youa.mobile.common.base.BaseRequestManager;

public class ExceptionRequestManager extends BaseRequestManager {
	public static final ExceptionRequestManager mExceptionRequestManager = new ExceptionRequestManager();
	public static final ExceptionRequestManager getInstance() {
		return mExceptionRequestManager;
	}
	public void uploadError(
			Context context,
			String errorText) throws MessageException {
		
//		public void requestMessage(
//				Context context) throws FeihongException {
//			String url = getBaseURL() + "/report/android";
		    
//			Map<String, String> params = new HashMap<String, String>(0);
//			params.put("msg", errorText);
//			params.put("level", "1");
//			JsonObject object = new JsonObject();
//			object.put("msg", errorText);
//			object.put("level", "fatal");
		MobclickAgent.reportError(context,errorText);
//			JsonArray json = new JsonArray();
//			json.add(errorText);
//			json.add("fatal");
//			String str = json.toJsonString();
//
//			Map<String, String> paramMap = new HashMap<String, String>();
//			paramMap.put("rpcinput", str);
//			getHttpManager().post(
//					"jt.moLogger", 
//					paramMap,
//					context);
//		}
	}
}
