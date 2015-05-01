package com.youa.mobile.information.action;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.data.UserData;
import com.youa.mobile.information.manager.FriendManager;
import com.youa.mobile.information.action.SearchFriendListAction.SearchFriendListListener;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;

public class SearchFriendListAction extends
		BaseAction<SearchFriendListListener> {
	public interface SearchFriendListListener extends IResultListener,
			IFailListener {
		public void onStart();

		public void onFinish(List<UserData> dataList);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			SearchFriendListListener resultListener) throws Exception {
		resultListener.onStart();
		 List<UserData> userData = new FriendManager().searchFriendList();
		resultListener.onFinish(userData);
	}
}