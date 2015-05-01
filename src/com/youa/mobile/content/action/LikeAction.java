package com.youa.mobile.content.action;

import java.util.Map;

import android.content.Context;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.content.action.LikeAction.ISearchResultListener;
import com.youa.mobile.content.manager.HomeManager;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;

public class LikeAction extends BaseAction<ISearchResultListener> {
	public static final String PARAM_POST_ID = "postid";

	final public static int LIKE_NO = 0, LIKE_OK = 1, LIKE_ED = 2,
			LIKE_ORIGIN_DELETED = 3;

	public interface ISearchResultListener extends IResultListener,
			IFailListener {

		public void onStart();

		public void onEnd(int likeType);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISearchResultListener resultListener) throws Exception {
		assertLogin();
		
		String postId = null;
		if (params != null) {
			postId = (String) params.get(PARAM_POST_ID);
		}
		try {
			int likeType = new HomeManager().feedLike(context, postId);
			resultListener.onEnd(likeType);
		} catch (MessageException e) {
			String err = e.getErrCode();
			if (err == null) {
				return;
			}
			if (err.startsWith("jt.u_liked")) {
				e.setResID(R.string.feed_like_ed);
			} else if (err.startsWith("jt.u_arg.post_del")) {
				e.setResID(R.string.feed_like_origin_deleted);
			}
			throw e;
		}
	}

}
