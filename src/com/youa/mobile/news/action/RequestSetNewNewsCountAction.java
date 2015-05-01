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
import com.youa.mobile.news.action.RequestSetNewNewsCountAction.ISsetNewNewsCountResultListener;

public class RequestSetNewNewsCountAction extends BaseAction<ISsetNewNewsCountResultListener>{

	public static final int ADD = 0;
	public static final int SAY = 1;
	public static final int FAV = 2;
	public static final String TYPE = "type";
	public static final String COUNT = "count";
	public interface ISsetNewNewsCountResultListener extends IResultListener, IFailListener {
		void onFinish();
	}

	@Override
	protected void onExecute(
			Context context,
			Map<String, Object> params,
			ISsetNewNewsCountResultListener callback) throws Exception {
		String uId = ApplicationManager.getInstance().getUserId();
		int type = (Integer) params.get(TYPE);
//		int count = (Integer) params.get(COUNT);
		try {
			new NewsManager().requestSetNewNewsCount(context, uId, type, 0);
			callback.onFinish();
		} catch (MessageException e) {
			throw e;
		}
	}
}
