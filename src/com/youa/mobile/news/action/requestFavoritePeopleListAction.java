package com.youa.mobile.news.action;

import java.util.List;
import java.util.Map;
import android.content.Context;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.news.action.requestFavoritePeopleListAction.IFavoriteFavoritePeopleResultListener;
import com.youa.mobile.news.data.FavoritePeopleData;
import com.youa.mobile.news.manager.NewsManager;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;

public class requestFavoritePeopleListAction extends
		BaseAction<IFavoriteFavoritePeopleResultListener> {

	public static final String KEY_FEED_ID = "feed_id";

	public interface IFavoriteFavoritePeopleResultListener extends
			IResultListener, IFailListener {
		void onStart();
		void onFinish(List<FavoritePeopleData> list);
	}

	@Override
	protected void onExecute(
			Context context,
			Map<String, Object> params,
			IFavoriteFavoritePeopleResultListener callback) throws Exception {
		callback.onStart();
		String feedId = (String) params.get(KEY_FEED_ID);
		try {
			List<FavoritePeopleData> list = new NewsManager().requestFavoritePeopleList(context, feedId);
			callback.onFinish(list);
		} catch (MessageException e) {
			throw e;
		}
	}

}
