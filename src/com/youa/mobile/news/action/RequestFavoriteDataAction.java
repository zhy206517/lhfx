package com.youa.mobile.news.action;

import java.util.List;
import java.util.Map;
import android.content.Context;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.news.data.FavoriteData;
import com.youa.mobile.news.manager.NewsManager;
import com.youa.mobile.news.util.NewsUtil;
import com.youa.mobile.news.action.RequestFavoriteDataAction.IFavoritResultListener;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;

public class RequestFavoriteDataAction extends
		BaseAction<IFavoritResultListener> {

	private static final String TAG = "RequestAddMeDataAction";

	public static final String PARAM_USERNAME = "at_uid";
	public static final String PARAM_POSTID = "time";
	public static final String PARAM_LIMIT = "limit";
	public static final String PARAM_REFRESH_OR_MORE = "isrefresh";

	public interface IFavoritResultListener extends IFailListener,
			IResultListener {
		void onStart();
		void onFinish(List<FavoriteData> list, boolean isRefresh);
	}

	@Override
	protected void onExecute(
			Context context,
			Map<String, Object> params,
			IFavoritResultListener callback) throws Exception {
		callback.onStart();
		boolean isRefresh = (Boolean) params.get(PARAM_REFRESH_OR_MORE);
		String username = (String) params.get(PARAM_USERNAME);
		String postId = (String) params.get(PARAM_POSTID);
		int limit = (Integer) params.get(PARAM_LIMIT);
		NewsUtil.LOGD(TAG, "enter onExecute  <date username> : " + username);
		NewsUtil.LOGD(TAG, "enter onExecute  <date time> : " + postId);
		NewsUtil.LOGD(TAG, "enter onExecute  <date limit> : " + limit);
		NewsUtil.LOGD(TAG, "enter onExecute  <date isRefresh> : " + isRefresh);
		List<FavoriteData> list;
		try {
			list = new NewsManager().requestFavoriteData(
					context,
					username,
					limit,
					postId);
			callback.onFinish(list, isRefresh);
		} catch (MessageException e) {
			throw e;
		}
	}

}
