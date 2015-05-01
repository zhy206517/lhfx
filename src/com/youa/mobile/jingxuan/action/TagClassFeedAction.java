package com.youa.mobile.jingxuan.action;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;

import com.youa.mobile.jingxuan.TagClassFeedPage;
import com.youa.mobile.jingxuan.action.TagClassFeedAction.ISearchResultListener;
import com.youa.mobile.life.manager.LifeManager;
import com.youa.mobile.theme.manager.HomeManager;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.friend.data.HomeData;

public class TagClassFeedAction extends BaseAction<ISearchResultListener> {
	static int limit = 21;

	public interface ISearchResultListener extends IResultListener,
			IFailListener {
		public void onStart();

		public void onEnd(List<HomeData> homeList);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISearchResultListener resultListener) throws Exception {
		String id = (String) params.get(TagClassFeedPage.ID_KEY);
		String subid = (String) params.get(TagClassFeedPage.SUB_ID_KEY);
		String type = (String) params.get(TagClassFeedPage.TYPE_KEY);
		int page = (Integer) params.get(TagClassFeedPage.CURRENT_PAGE);
		List<HomeData> feedList = null;
		if (TagClassFeedPage.TYPE_TOPIC.equals(type)) {
			feedList = new HomeManager().requestAlbumFeedList(context, id,
					limit, limit * page);
		} else if (TagClassFeedPage.TYPE_TAG.equals(type)) {
			if (TextUtils.isEmpty(subid)) {
				feedList = new LifeManager().requestShareClassifyFeedData(context,
						id, page, limit, true, true);
			} else {
				feedList = new LifeManager().requestTagClassifyFeedData(
						context, id, subid, page, limit, true, true);
			}
		} else if (TagClassFeedPage.TYPE_CLASSIFY.equals(type)) {
			feedList = new LifeManager().requestShareClassifyFeedData(context,
					id, page, limit, true, true);
		}

		resultListener.onEnd(feedList);
	}
}
