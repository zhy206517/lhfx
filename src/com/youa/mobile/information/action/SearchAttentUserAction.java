package com.youa.mobile.information.action;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.information.action.SearchAttentUserAction.ISearchAttentResult;
import com.youa.mobile.information.manager.PersonalInfoManager;
import com.youa.mobile.life.data.SuperPeopleData;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;

public class SearchAttentUserAction extends BaseAction<ISearchAttentResult> {
	public static final String KEY_UID="uid";
	public static final String KEY_ATTENT_TYPE="attent_type";
	public static final String KEY_OFFSET="attent_offset";
	public interface ISearchAttentResult extends IResultListener,IFailListener {
		public void onStart();
		public void onEnd(List<SuperPeopleData> userData);
	}

	@Override
	protected void onExecute(
			Context context,
			Map<String, Object> params,
			ISearchAttentResult resultListener)
			throws Exception {
		resultListener.onStart();
		String uid = (String)params.get(SearchAttentUserAction.KEY_UID);
		String type =  (String)params.get(SearchAttentUserAction.KEY_ATTENT_TYPE);
		int offset = (Integer)params.get(SearchAttentUserAction.KEY_OFFSET);
		List<SuperPeopleData> userData = 
			new PersonalInfoManager().searchAttentSuperUserList(
				context,
				type,
				uid,
				offset);
		resultListener.onEnd(userData);
		
	}
}
