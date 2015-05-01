package com.youa.mobile.life.action;

import java.util.List;
import java.util.Map;
import android.content.Context;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.life.FindLifeMainPage;
import com.youa.mobile.life.data.UserInfo;
import com.youa.mobile.life.manager.LifeManager;
import com.youa.mobile.life.action.RequestSearchAction.ISearchResultListener;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
public class RequestSearchAction extends BaseAction<ISearchResultListener> {

	public static final String KEY_SEARCH_KEY = "key";
	public static final String KEY_SEARCH_TYPE = "type";

	public interface ISearchResultListener extends IResultListener, IFailListener {
		void onStart();
		void onFinish(List<UserInfo> list);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISearchResultListener resultListener) throws Exception {

		resultListener.onStart();
		String searchKey = (String) params.get(KEY_SEARCH_KEY);
		int type = (Integer) params.get(KEY_SEARCH_TYPE);
		List list=null;
		try {
			if(FindLifeMainPage.RADIO_TYPE_PEOPLE==type){
				  list = new LifeManager().requestFindPeopleList(context, searchKey);
			}else if(FindLifeMainPage.RADIO_TYPE_TOPIC==type){
				  list = new LifeManager().requestFindTopicList(context, searchKey);
			}
			resultListener.onFinish(list);
		} catch (MessageException e) {
			throw e;
		}
	}

}
