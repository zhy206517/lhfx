package com.youa.mobile.friend.action;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.friend.action.FriendSearchAction.ISearchResultListener;
import com.youa.mobile.friend.friendsearch.UserInfo;
import com.youa.mobile.friend.manager.HomeManager;
import com.youa.mobile.life.data.SuperPeopleData;

public class FriendSearchAction extends BaseAction<ISearchResultListener> {
	public static final String KEY_SEARCH_KEY = "key";
	public static final String KEY_START_POSTION = "pos";

	public interface ISearchResultListener extends IResultListener,
			IFailListener {
		void onStart();

		void onFinish(List<SuperPeopleData> list);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISearchResultListener resultListener) throws Exception {
		resultListener.onStart();
		String searchKey = (String) params.get(KEY_SEARCH_KEY);
		int pos = Integer
				.parseInt(String.valueOf(params.get(KEY_START_POSTION)));
		List<SuperPeopleData> list = new HomeManager().requestFindPeopleList(
				context, searchKey, pos);
		resultListener.onFinish(list);
	}

}
