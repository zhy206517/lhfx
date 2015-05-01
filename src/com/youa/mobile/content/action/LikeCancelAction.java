package com.youa.mobile.content.action;

import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.content.action.LikeCancelAction.ISearchResultListener;
import com.youa.mobile.content.manager.HomeManager;
public class LikeCancelAction extends BaseAction<ISearchResultListener> {
	public static final String PARAM_POST_ID = "postid";
	final public static int DELIKE_NO = 0, DELIKE_OK = 1;
	public interface ISearchResultListener extends IResultListener,
			IFailListener {

		public void onStart();

		public void onEnd(int likeType);
	}

	@Override
	protected void onExecute(
			Context context,
			Map<String, Object> params,
			ISearchResultListener resultListener)
			throws Exception {
		String postId = null;
		if (params != null) {
			postId = (String) params.get(PARAM_POST_ID);
		}
		try {
			int likeType = new HomeManager().feedCancelLike(context, postId);
			resultListener.onEnd(likeType);
		} catch (MessageException e) {
		}
	}

}