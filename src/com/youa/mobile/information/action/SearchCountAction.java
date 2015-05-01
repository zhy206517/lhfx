package com.youa.mobile.information.action;

import java.util.Map;

import android.content.Context;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.information.data.ShowCountData;
import com.youa.mobile.information.manager.PersonalInfoManager;
import com.youa.mobile.information.action.SearchCountAction.ISearchCountResult;

public class SearchCountAction extends BaseAction<ISearchCountResult> {
	public static final String KEY_UID = "uid";

	public interface ISearchCountResult extends IResultListener, IFailListener {
		public void onStart();

		public void onEnd(ShowCountData showCountData);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISearchCountResult resultListener) throws Exception {
		resultListener.onStart();
		String uid = (String) params.get(KEY_UID);
		ShowCountData showCountData = new PersonalInfoManager().searchCount(
				context, uid);
		resultListener.onEnd(showCountData);
	}

}
