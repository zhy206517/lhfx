package com.youa.mobile.life.action;

import java.util.List;
import java.util.Map;
import android.content.Context;
import android.text.TextUtils;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.life.action.RequestFindSuperPeopleAction.ISearchSuperPeopleResultListener;
import com.youa.mobile.life.data.SuperPeopleData;
import com.youa.mobile.life.manager.LifeManager;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;

public class RequestFindSuperPeopleAction extends
		BaseAction<ISearchSuperPeopleResultListener> {

	public interface ISearchSuperPeopleResultListener extends IResultListener,
			IFailListener {
		void onStart();

		void onFinish(List<SuperPeopleData> list);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISearchSuperPeopleResultListener callback) throws Exception {
		callback.onStart();
		try {
			List<SuperPeopleData> list = null;
			if(params ==  null){
				callback.onFail(R.string.life_search_no_result);
				return;
			}
			String key = (String) params.get("key");
			if (TextUtils.isEmpty(key)) {
				list = new LifeManager().requestSuperPeopleList(context);
			}else{
				list = new LifeManager().requestSuperPeopleList(context,key);
			}

			callback.onFinish(list);
		} catch (MessageException e) {
			throw e;
		}

	}
}
