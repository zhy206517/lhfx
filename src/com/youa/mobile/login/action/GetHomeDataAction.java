package com.youa.mobile.login.action;

import java.util.Map;

import android.content.Context;

import com.youa.mobile.circum.manager.HomeManager;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.login.action.GetHomeDataAction.DataResultListner;

public class GetHomeDataAction extends BaseAction<DataResultListner>{
	public interface DataResultListner extends IResultListener {
		void onFinish();
		void onFail();		
	}

	@Override
	protected void onExecute(
			Context context,
			Map<String, Object> params,
			DataResultListner listener)
			throws Exception {
		try {
//			HomeManager.requestThemeCollectionList(context);
			HomeManager.requestCircumCollectionList(context);
			listener.onFinish();
		} catch (Exception e) {
			listener.onFail();
			e.printStackTrace();
		}
	}

}
