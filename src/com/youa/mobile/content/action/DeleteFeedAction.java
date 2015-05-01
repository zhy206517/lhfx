package com.youa.mobile.content.action;

import java.util.Map;

import android.content.Context;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.content.action.DeleteFeedAction.ISearchResultListener;
import com.youa.mobile.content.manager.HomeManager;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;


public class DeleteFeedAction extends BaseAction<ISearchResultListener> {
	public static final String PARAM_POST_ID = "postid";

	public interface ISearchResultListener extends IResultListener,
			IFailListener {
		public void onFail(int resourceID);

		public void onStart();

		public void onEnd(int likeType);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISearchResultListener resultListener) throws Exception {
		String postId = null;
		if (params != null) {
			postId = (String) params.get(PARAM_POST_ID);
		}
		try {
			int flag = new HomeManager().deleteFeed(context, postId);
			if (flag == 1) {
				resultListener.onEnd(flag);
			} else {
				resultListener
						.onFail(R.string.feed_theme_collection_delete_fail);
			}
		} catch (MessageException e) {
			resultListener.onFail(R.string.feed_theme_collection_delete_fail);
		}
	}

}
