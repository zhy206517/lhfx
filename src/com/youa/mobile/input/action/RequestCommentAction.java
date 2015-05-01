package com.youa.mobile.input.action;

import java.util.Map;
import android.content.Context;
import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.input.CommentPage;
import com.youa.mobile.input.action.RequestCommentAction.ICommentResultListener;
import com.youa.mobile.input.manager.PublishManager;

public class RequestCommentAction extends BaseAction<ICommentResultListener>{

	public interface ICommentResultListener extends IResultListener, IFailListener {
		void onStart();
		void onFinish();
	}

	@Override
	protected void onExecute(
			Context context,
			Map<String, Object> params,
			ICommentResultListener callback) throws Exception {
		callback.onStart();
		String uid = (String) params.get(CommentPage.KEY_PARAMS_ID);
		String content = (String) params.get(CommentPage.KEY_PARAMS_CONTENT);
		String sourceId = (String) params.get(CommentPage.KEY_PARAMS_SOURCE_ID);
		String commentId = (String) params.get(CommentPage.KEY_PARAMS_COMMENT_ID);
		boolean isComment = (Boolean) params.get(CommentPage.KEY_PARAMS_COMMENT_OR_REPLY);
		boolean isForward = (Boolean) params.get(CommentPage.KEY_PARAMS_IS_FORWARD);
		try {
			new PublishManager().requestPublishComment(
					context,
					uid,
					content,
					sourceId,
					commentId,
					isComment,
					isForward);
			callback.onFinish();
		} catch (MessageException e) {
			throw e;
		}
	}

}
