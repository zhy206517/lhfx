package com.youa.mobile.friend.action;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.friend.friendmanager.ManagerSuperMenCalssifyData;
import com.youa.mobile.friend.manager.HomeManager;
import com.youa.mobile.friend.action.ManagerSuperClassifyAction.ISearchResultListener;

public class ManagerSuperClassifyAction extends BaseAction<ISearchResultListener> {
	//达人分类
	public interface ISearchResultListener extends IResultListener,
			IFailListener {
		void onStart();

		void onFinish(List<ManagerSuperMenCalssifyData> list);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISearchResultListener callback) throws Exception {
		callback.onStart();

		List<ManagerSuperMenCalssifyData> list = new HomeManager()
				.requestSuperPeopleClassify(context);
		callback.onFinish(list);

	}

}
