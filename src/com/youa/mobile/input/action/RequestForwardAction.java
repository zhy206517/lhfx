package com.youa.mobile.input.action;

import java.util.Map;
import android.content.Context;
import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.input.CommentPage;
import com.youa.mobile.input.ForwardPage;
import com.youa.mobile.input.action.RequestForwardAction.IForwardResultListener;
import com.youa.mobile.input.manager.PublishManager;

public class RequestForwardAction extends BaseAction<IForwardResultListener> {

	public interface IForwardResultListener extends IResultListener, IFailListener {
		void onStart();
		void onFinish();
	}

	@Override
	protected void onExecute(
			Context context,
			Map<String, Object> params,
			IForwardResultListener callback) throws Exception {
		callback.onStart();
		String uid = (String) params.get(ForwardPage.KEY_PARAMS_ID);
		String content = (String) params.get(ForwardPage.KEY_PARAMS_CONTENT);
		String sourceId = (String) params.get(ForwardPage.KEY_PARAMS_SOURCE_ID);
		boolean isComment = (Boolean) params.get(ForwardPage.KEY_PARAMS_IS_COMMENT);
		try {
			new PublishManager().requestPublishForward(
					context,
					uid,
					content,
					sourceId,
					isComment);
			callback.onFinish();
		} catch (MessageException e) {
			throw e;
		}
		
	}
	
}
