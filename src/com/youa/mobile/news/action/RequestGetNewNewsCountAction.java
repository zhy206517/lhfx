package com.youa.mobile.news.action;

import java.util.Map;
import android.content.Context;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.news.action.RequestGetNewNewsCountAction.INewNewsCountResultListener;
import com.youa.mobile.news.manager.NewsManager;
import com.youa.mobile.news.util.NewsUtil;

public class RequestGetNewNewsCountAction extends BaseAction<INewNewsCountResultListener> {

	public interface INewNewsCountResultListener extends IResultListener, IFailListener {
		void onFinish(boolean isChanged);
	}

	@Override
	protected void onExecute(
			Context context,
			Map<String, Object> params,
			INewNewsCountResultListener callback) throws Exception {
		try {
			int[] list = new NewsManager().requestGetNewNewsCount(
					context,
					ApplicationManager.getInstance().getUserId());
			boolean isChanged = false;
			if (!NewsUtil.isZero(list)) {
				NewsUtil.writeNewCountToPref(context, list);
				isChanged = true;
			}
			callback.onFinish(isChanged);
		} catch (MessageException e) {
			throw e;
		}
	}

}
