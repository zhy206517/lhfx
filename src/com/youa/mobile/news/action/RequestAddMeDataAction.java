package com.youa.mobile.news.action;

import java.util.List;
import java.util.Map;
import android.content.Context;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.news.action.RequestAddMeDataAction.IAddMeResultInitDataListener;
import com.youa.mobile.news.data.AddMeData;
import com.youa.mobile.news.manager.NewsManager;
import com.youa.mobile.news.util.NewsUtil;

public class RequestAddMeDataAction extends BaseAction<IAddMeResultInitDataListener> {

	private static final String TAG = "RequestAddMeDataAction";

	public static final String PARAM_U_ID = "at_uid";
	public static final String PARAM_TIME = "time";
	public static final String PARAM_LIMIT = "limit";
	public static final String PARAM_REFRESH_OR_MORE = "isrefresh";

	public interface IAddMeResultInitDataListener extends IFailListener, IResultListener {
		void onStart();
		void onFinish(List<HomeData> list, boolean isRefresh);
	}

	@Override
	protected void onExecute(
			Context context,
			Map<String, Object> params,
			IAddMeResultInitDataListener callback) throws Exception {

		boolean isRefresh = (Boolean)params.get(PARAM_REFRESH_OR_MORE);
		String uId = (String)params.get(PARAM_U_ID);
		long time = (Long)params.get(PARAM_TIME);
		int limit = (Integer)params.get(PARAM_LIMIT);
		NewsUtil.LOGD(TAG, "enter onExecute  <date uId> : " + uId);
		NewsUtil.LOGD(TAG, "enter onExecute  <date time> : " + time);
		NewsUtil.LOGD(TAG, "enter onExecute  <date limit> : " + limit);
		NewsUtil.LOGD(TAG, "enter onExecute  <date isRefresh> : " + isRefresh);
		List<HomeData> list;
		try {
			list = new NewsManager().requestAddMeData(context, uId, limit, time);
			callback.onFinish(list, isRefresh);
		} catch (MessageException e) {
			throw e;
		}
	}

}
