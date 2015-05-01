package com.youa.mobile.information.action;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.data.UserData;
import com.youa.mobile.information.manager.FriendManager;
import com.youa.mobile.information.action.DownLoadFriendAction.DownLoadFriendListener;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;

public class DownLoadFriendAction extends BaseAction<DownLoadFriendListener> {
	public interface DownLoadFriendListener extends IResultListener,
			IFailListener {
		public void onStart();

		public void onFinish(List<UserData> dataList);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			DownLoadFriendListener resultListener) throws Exception {
		resultListener.onStart();
		List<UserData> dataList = new FriendManager()
				.downloadFriendList(context);
		resultListener.onFinish(dataList);
	}
}
