package com.youa.mobile.information.action;

import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.information.action.SearchRelationAction.ISearchResult;
import com.youa.mobile.information.manager.PersonalInfoRequestManager;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
public class SearchRelationAction extends BaseAction<ISearchResult> {
//	public static final String KEY_LOGINID = "loginid";
	public static final String KEY_UID = "uid";
	
	public interface ISearchResult extends IResultListener, IFailListener {
		void onStart();
		void onEnd(String status);
	}

	@Override
	protected void onExecute(
			Context context, 
			Map<String, Object> params,
			ISearchResult resultListener) throws Exception {
		String loginID = ApplicationManager.getInstance().getUserId();
		String uid = (String)params.get(KEY_UID);
		
		String status = new PersonalInfoRequestManager().getFollowerStatus(
				context,
				loginID,
				uid);
		resultListener.onEnd(status);
	}
}
