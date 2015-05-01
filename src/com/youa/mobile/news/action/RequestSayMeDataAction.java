package com.youa.mobile.news.action;

import java.util.List;
import java.util.Map;
import android.content.Context;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.news.action.RequestSayMeDataAction.ISayMeResultListener;
import com.youa.mobile.news.data.SayMeData;
import com.youa.mobile.news.manager.NewsManager;
import com.youa.mobile.news.util.NewsUtil;

public class RequestSayMeDataAction extends BaseAction<ISayMeResultListener>{

	public static final String PARAM_USERNAME = "at_uid";
	public static final String PARAM_TIME = "time";
	public static final String PARAM_LIMIT = "limit";
	public static final String PARAM_REFRESH_OR_MORE = "isrefresh";

	public interface ISayMeResultListener extends IFailListener, IResultListener {
		void onStart();
		void onFinish(List<SayMeData> list, boolean	isRefresh);
	}

	@Override
	protected void onExecute(
			Context context,
			Map<String, Object> params,
			ISayMeResultListener callback) throws Exception {
		boolean isRefresh = (Boolean)params.get(PARAM_REFRESH_OR_MORE);
		String username = (String)params.get(PARAM_USERNAME);
		long time = (Long)params.get(PARAM_TIME);
		int limit = (Integer)params.get(PARAM_LIMIT);
		NewsUtil.LOGD(TAG, "enter onExecute  <date username> : " + username);
		NewsUtil.LOGD(TAG, "enter onExecute  <date time> : " + time);
		NewsUtil.LOGD(TAG, "enter onExecute  <date limit> : " + limit);
		NewsUtil.LOGD(TAG, "enter onExecute  <date isRefresh> : " + isRefresh);
		List<SayMeData> list;
		try {
			list = new NewsManager().requestSayMeData(context, username, limit, time);
			callback.onFinish(list, isRefresh);
		} catch (MessageException e) {
			throw e;
		}
	}

}
