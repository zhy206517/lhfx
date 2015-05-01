package com.youa.mobile.input.action;

import java.util.Map;
import android.content.Context;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.input.action.SearchDraftAction.IGetDraftResultListener;
import com.youa.mobile.input.data.PublishData;
import com.youa.mobile.input.manager.DraftManager;

public class SearchDraftAction extends BaseAction<IGetDraftResultListener> {

	public interface IGetDraftResultListener extends IResultListener {
		void onFinish(PublishData data);
	}

	@Override
	protected void onExecute(
			Context context,
			Map<String, Object> params,
			IGetDraftResultListener callback) throws Exception {
		callback.onFinish(DraftManager.getInstance().SearchDrafeData(context));
	}

}
