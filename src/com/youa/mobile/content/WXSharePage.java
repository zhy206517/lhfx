package com.youa.mobile.content;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.common.base.AbstractListView.ShowEmptyViewListener;
import com.youa.mobile.common.base.AbstractListView.OnScrollEndListener;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.params.PageSize;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.login.LoginPage;

public abstract class WXSharePage extends BasePage implements OnScrollEndListener {
	// /////////////////////////////////////////////////////
	// Fields
	// /////////////////////////////////////////////////////
	protected WaterfallListView mListView;
	protected LayoutInflater mInflater;
	// protected ArrayList<HomeData> mDataList;
	protected View mFooterView;
	protected LinearLayout mHeaderView;
	protected LinearLayout mEmptyView;
	protected int mPageIndex = 1;
	protected UpdateLoginSuccessReceiver mReceiver = new UpdateLoginSuccessReceiver();

	private class UpdateLoginSuccessReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && LehuoIntent.LOGIN_SUCESS_WXSharePage.equals(intent.getAction())) {
				loadData(true);
			}

		}
	}

	// protected boolean mRegisterRefresh;
	@Override
	public void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_waterfall_2_lines);

		// mDataList = new ArrayList<HomeData>();
		IntentFilter filter = new IntentFilter();
		filter.addAction(LehuoIntent.LOGIN_SUCESS_WXSharePage);
		registerReceiver(mReceiver, filter);
		toTopInit();
		initViews();

		if (ApplicationManager.getInstance().isLogin() || this instanceof HistoryPage) {
			showProgressView();
			loadData(true);
		} else {
			((TextView) mEmptyView.findViewById(R.id.wxcb_tishi)).setText(R.string.not_login);
			mEmptyView.findViewById(R.id.wxcb_empty_btn).setBackgroundResource(R.drawable.wxcb_btn_login);
			mEmptyView.findViewById(R.id.wxcb_empty_btn).setTag("0");
			mEmptyView.setVisibility(View.VISIBLE);
		}

		// Display display = getWindowManager().getDefaultDisplay();
		// LogUtil.d(TAG, "onCreate. w = " + display.getWidth() + ", h = " +
		// display.getHeight());
	}

	private void initViews() {
		mInflater = LayoutInflater.from(this);
		// mHeaderView = (LinearLayout) mInflater.inflate(R.layout.feed_header,
		// null);
		// mFooterView = mInflater.inflate(R.layout.feed_footer, null);
		initHeaderAndFooter();
		ListView listView = (ListView) findViewById(R.id.waterfall_list);
		mListView = new WaterfallListView(listView, mHeaderView, mFooterView);
		mListView.setOnScrollEndListener(this);
		mListView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable(ShareFeedPage.CONTEXT_OBJ, (HomeData) v.getTag());
				bundle.putString(ShareFeedPage.EXTRA_DATA_FROM_WHERE, ShareFeedPage.EXTRA_DATA_FROM_WX);
				intent.putExtras(bundle);
				intent.setClass(WXSharePage.this, ShareFeedPage.class);
				startActivity(intent);
			}
		});
		// mListView.setTapGestureRecognizeListener(this);
		mLoadView = listView;
		mProcessView = findViewById(R.id.progress_view);
		mEmptyView = (LinearLayout) findViewById(R.id.empty_view);
	}

	protected abstract void initHeaderAndFooter();

	protected void updateViews(final List<HomeData> feedDataList, final int pageIndex, final int btn_text) {
		// LogUtil.d(TAG, "updateViews. pageIndex = " + pageIndex);
		mPageIndex = pageIndex;
		mHandler.post(new Runnable() {
			public void run() {
				mListView.closeHeaderFooter();
				if (pageIndex == 1) {// 刷新
					// mDataList.clear();
					// if (feedDataList != null && !feedDataList.isEmpty()) {
					// // mDataList.addAll(feedDataList);
					// } else {
					// ((TextView)
					// mEmptyView.findViewById(R.id.wxcb_tishi)).setText(btn_text);
					// mEmptyView.findViewById(R.id.wxcb_empty_btn).setBackgroundResource(R.drawable.wxcb_btn_search);
					// mEmptyView.findViewById(R.id.wxcb_empty_btn).setTag("1");
					// mEmptyView.setVisibility(View.VISIBLE);
					// }
					mListView.setShowListener(new ShowEmptyViewListener() {

						@Override
						public void onShowEmptyView() {
							((TextView) mEmptyView.findViewById(R.id.wxcb_tishi)).setText(btn_text);
							mEmptyView.findViewById(R.id.wxcb_empty_btn).setBackgroundResource(R.drawable.wxcb_btn_search);
							mEmptyView.findViewById(R.id.wxcb_empty_btn).setTag("1");
							mEmptyView.setVisibility(View.VISIBLE);
						}
					});
					mListView.setData(feedDataList, PageSize.INFO_FEED_PAGESIZE);
				} else {
					// if (feedDataList != null) {
					mListView.addData(feedDataList, PageSize.INFO_FEED_PAGESIZE);
					// } else {
					// mListView.addData(new ArrayList<HomeData>(),
					// PageSize.INFO_FEED_PAGESIZE);
					// }
				}
			}
		});
	}

	protected abstract void loadData(boolean isRefreshOrGetMore);

	@Override
	public void onScrollEnd() {
		loadData(false);
	}

	@Override
	public void onScrollHeader() {
		loadData(true);
	}

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private void toTopInit() {
		animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);
	}

	public void toSearchOrLogin(View view) {
		if ("0".equals(view.getTag())) {
			Intent i = new Intent(this, LoginPage.class);
			i.putExtra("action_name", LehuoIntent.LOGIN_SUCESS_WXSharePage);
			startActivity(i);
		} else {
			startActivity(new Intent(this, FeedSearchActivity.class));
		}
	}
}

// private void loadData(boolean isRefreshOrGetMore) {
// LogUtil.d(TAG, "loadData. isRefreshOrGetMore = " + isRefreshOrGetMore);
// mEmptyView.setVisibility(View.GONE);
// Map<String, Object> params = new HashMap<String, Object>();
// params.put(SearchOwnFeedAction.PARAM_UID, mUid);
// params.put(SearchOwnFeedAction.PARAM_PAGEINDEX, mPageIndex);
// params.put(SearchOwnFeedAction.PARAM_REFRESH_OR_MORE, isRefreshOrGetMore);
// ActionController.post(this, SearchEnjoyFeedAction.class, params, new
// SearchEnjoyFeedAction.SearchEnjoyFeedResult() {
//
// @Override
// public void onStart() {
// }
//
// public void onFail(int resourceID) {
// showToast(MyPreferPage.this, resourceID);
// hiddenProgressView();
// }
//
// @Override
// public void onEnd(List<HomeData> feedData, int pageIndex) {
// mPageIndex = pageIndex;
// if (feedData != null) {
// LogUtil.d(TAG, "onEnd. list size = " + feedData.size() + ", pageIndex = " +
// pageIndex);
// } else {
// LogUtil.d(TAG, "onEnd. list is null" + ", pageIndex = " + pageIndex);
// }
// updateViews((List<HomeData>) (ArrayList<HomeData>) feedData, pageIndex);
// hiddenProgressView();
// }
// }, true);
// }