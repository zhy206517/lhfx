package com.youa.mobile.theme.action;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.theme.action.ThemeAction.ISearchResultListener;
import com.youa.mobile.theme.manager.HomeManager;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.friend.data.HomeData;

public class ThemeAction extends BaseAction<ISearchResultListener> {
	public static final String PARAM_KEYWORD = "keyword";

	public interface ISearchResultListener extends IResultListener,
			IFailListener {
		public void onStart();

		public void onEnd(List<HomeData> homeDataList);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISearchResultListener resultListener) throws Exception {
		String keyword = (String) params.get(PARAM_KEYWORD);
		List<HomeData> popDataList = new HomeManager().requestThemeDynamicList(
				context, keyword, 50, 0);
		resultListener.onEnd(popDataList);
	}

}
