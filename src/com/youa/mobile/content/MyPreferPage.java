package com.youa.mobile.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.View;
import android.widget.LinearLayout;

import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.information.action.SearchEnjoyFeedAction;
import com.youa.mobile.information.action.SearchOwnFeedAction;

public class MyPreferPage extends WXSharePage {// OnItemClickListener,

	@Override
	protected void initHeaderAndFooter() {
		mHeaderView = (LinearLayout) mInflater.inflate(R.layout.feed_header, null);
		mFooterView = mInflater.inflate(R.layout.feed_footer, null);
	}

	@Override
	protected void loadData(boolean isRefreshOrGetMore) {
		// LogUtil.d(TAG, "loadData. isRefreshOrGetMore = " + isRefreshOrGetMore);
		mEmptyView.setVisibility(View.GONE);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(SearchOwnFeedAction.PARAM_UID, ApplicationManager.getInstance().getUserId());
		params.put(SearchOwnFeedAction.PARAM_PAGEINDEX, mPageIndex);
		params.put(SearchOwnFeedAction.PARAM_REFRESH_OR_MORE, isRefreshOrGetMore);
		ActionController.post(this, SearchEnjoyFeedAction.class, params, new SearchEnjoyFeedAction.SearchEnjoyFeedResult() {

			@Override
			public void onStart() {
			}

			public void onFail(int resourceID) {
				showToast(MyPreferPage.this, resourceID);
				hiddenProgressView();
			}

			@Override
			public void onEnd(List<HomeData> feedData, int pageIndex) {
				if (feedData == null) {
					feedData = new ArrayList<HomeData>();
				}
				// ArrayList<HomeData> list = new ArrayList<HomeData>();
				// for (HomeData data : feedData) {
				// if (data != null) {
				// if (data.originUser != null) {
				// if (data.originUser.contentImg != null && data.originUser.contentImg.length > 0) {
				// list.add(data);
				// }
				// } else if (data.PublicUser != null) {
				// if (data.PublicUser.contentImg != null && data.PublicUser.contentImg.length > 0) {
				// list.add(data);
				// }
				// }
				// }
				// }
				// LogUtil.d(TAG, "onEnd. feedData size = " + feedData.size());
				updateViews(feedData, pageIndex, R.string.no_prefer);
				hiddenProgressView();
			}
		}, true);
	}
}
