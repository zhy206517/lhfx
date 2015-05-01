package com.youa.mobile.content;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

import com.youa.mobile.R;
import com.youa.mobile.content.manager.HistoryFeedManager;
import com.youa.mobile.friend.data.HomeData;

public class HistoryPage extends WXSharePage {// OnItemClickListener,
	// /////////////////////////////////////////////////////
	// Fields
	// /////////////////////////////////////////////////////
	// private int mCurrPage = 0;

	protected void loadData(boolean isRefreshOrGetMore) {
		// LogUtil.d(TAG, "loadData. isRefreshOrGetMore = " + isRefreshOrGetMore);
		mEmptyView.setVisibility(View.GONE);
		if (isRefreshOrGetMore) {
			// mCurrPage = 0;
			// mDataList.clear();
		}
		List<HomeData> feedDataList = new HistoryFeedManager().getHistory();
		// mHandler.post(new Runnable() {
		// public void run() {
		// mListView.closeHeaderFooter();
		// if (feedDataList == null || feedDataList.isEmpty()) {
		// if (mCurrPage > 0) { // 加载到最后一页
		// Toast.makeText(HistoryPage.this, "已加载到最后一页。", Toast.LENGTH_SHORT).show();
		// } else {
		// runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// mEmptyView.setVisibility(View.VISIBLE);
		// ((TextView) mEmptyView.findViewById(R.id.wxcb_tishi)).setText(R.string.no_prefer);
		// mEmptyView.findViewById(R.id.wxcb_empty_btn).setBackgroundResource(R.drawable.wxcb_btn_search);
		// mEmptyView.findViewById(R.id.wxcb_empty_btn).setTag("1");
		// mEmptyView.setVisibility(View.VISIBLE);
		// }
		// });
		// }
		// } else {
		// mDataList.addAll(feedDataList);
		// }
		// mListView.setData(mDataList, PageSize.HISTORY_FEED_PAGESIZE);
		// mCurrPage++;
		// }
		// });
		// ArrayList<HomeData> list = new ArrayList<HomeData>();
		if (feedDataList == null) {
			feedDataList = new ArrayList<HomeData>();
		}
		// for (HomeData data : feedDataList) {
		// if (data.PublicUser != null) {
		// if (data.PublicUser.contentImg != null && data.PublicUser.contentImg.length > 0) {
		// list.add(data);
		// }
		// }
		// }
		updateViews(feedDataList, 1, R.string.no_history);
		hiddenProgressView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		showProgressView();
		loadData(true);
	}

	@Override
	protected void initHeaderAndFooter() {

	}
}
