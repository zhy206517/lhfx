package com.youa.mobile.more.action;

import java.util.Map;

import android.content.Context;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.params.CommonParam;
import com.youa.mobile.more.FeedbackPage;
import com.youa.mobile.more.action.FeedbackSendAction.IFeedbackSendResultListener;
import com.youa.mobile.input.manager.PublishManager;

public class FeedbackSendAction extends BaseAction<IFeedbackSendResultListener> {

	public interface IFeedbackSendResultListener extends IResultListener,
			IFailListener {
		void onStart();
		void onFinish();
	}
	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			IFeedbackSendResultListener callback) throws Exception {
		callback.onStart();
		String uid = (String) params.get(FeedbackPage.KEY_PARAMS_ID);
		String content = (String) params.get(FeedbackPage.KEY_PARAMS_CONTENT);
		String type = (String) params.get(FeedbackPage.KEY_PARAMS_TYPE);
		try {
			new PublishManager().requestPublishFeedback(
					 context,
					 uid,
					 content,
					 type);
			callback.onFinish();
		} catch (MessageException e) {
			if((CommonParam.VALUE_ERROR_PARAM_ERROR).equals(e.getErrCode())){
				callback.onFail(R.string.feedback_err_message);
			}else{
				throw e;
			}
		}
	}

}
