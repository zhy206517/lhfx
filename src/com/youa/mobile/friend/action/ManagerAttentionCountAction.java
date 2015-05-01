package com.youa.mobile.friend.action;

import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.friend.action.ManagerAttentionCountAction.ISearchResultListener;
import com.youa.mobile.information.data.ShowCountData;
import com.youa.mobile.information.manager.PersonalInfoManager;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.manager.ApplicationManager;

public class ManagerAttentionCountAction extends BaseAction<ISearchResultListener>{
//已关注人数量 只能登陆用户翻页
	public interface ISearchResultListener extends IResultListener,
			IFailListener {
		void onStart();

		void onFinish(ShowCountData data);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISearchResultListener resultListener) throws Exception {
		resultListener.onStart();
		String uid = ApplicationManager.getInstance().getUserId();
		ShowCountData showCountData = new PersonalInfoManager().searchCount(
				context, uid);
		resultListener.onFinish(showCountData);
		
	}

}
