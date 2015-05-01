package com.youa.mobile.friend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.LehoApp;
import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.manager.ParserListView;
import com.youa.mobile.common.params.PageSize;
import com.youa.mobile.content.ContentOriginActivity;
import com.youa.mobile.content.ContentTranspondActivity;
import com.youa.mobile.friend.HomeListView.TapGestureRecognize;
import com.youa.mobile.friend.action.FriendAction;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.data.User;
import com.youa.mobile.friend.friendmanager.FriendManagerActivity;
import com.youa.mobile.friend.store.FriendFileStore;
import com.youa.mobile.input.PublishPage;

public class FriendFeedActivity extends BasePage implements
		BaseListView.OnScrollEndListener, TapGestureRecognize {

	public final static int REQUEST_COUNT = 50;
	// -----------friend---------------
	private HomeListView homeList;
	private ListView homeListView;
	private View footer;
	private LinearLayout header;
	private LayoutInflater mInflater;
	private Handler mHandler = new Handler();
	private ProgressBar homeProgressBar;
	// private LinearLayout linearNull;
	// private TextView listNull;
	private FeedOnClickListener feedOnClickListener;
	String mHomeDeleteId = null;
	public static boolean isGetFriendOnce;
	// -------------------title--------------------
	private ImageButton title_turnpage;
	private ImageButton title_write;
	private boolean isRefreshing;
	public boolean isFriendNull;
	final public static int request_delete_code = 20, result_delete_code = 50;
	private boolean isTurnPage;
	LinearLayout mEmptyView;

	// ---------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_home);
		isFriendNull = true;
		initView();
		// HintPageUtil.checkHint(HintPageUtil.HINT_HOME, this, mHandler);
		loadFriendData(FriendAction.PARAM_REFRESH);
		homeProgressBar.setVisibility(View.VISIBLE);
		registerReceiver();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isTurnPage = false;
		if (ApplicationManager.getInstance().isLogin()) {
			showFriend();
			((LehoApp) FriendFeedActivity.this.getApplication())
					.requestLocation();
		}
	}

	@Override
	protected void onPause() {
		if (ApplicationManager.getInstance().isLogin()) {
			((LehoApp) FriendFeedActivity.this.getApplication())
					.requestStopLocation();
			// new Thread() {
			// public void run() {
			// FriendFileStore.getInstance().saveFriend(
			// (ArrayList<HomeData>) homeList.getData(),
			// FriendFeedActivity.this);
			// }
			// }.start();
		}
		super.onPause();
	}

	private void showFriend() {
		if (!isGetFriendOnce) {
			return;
		}
		isFriendNull = false;
		isGetFriendOnce = false;
		loadFriendData(FriendAction.PARAM_REFRESH);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (TextUtils.equals(LehuoIntent.FRIEND_FEED_UPDATE,
					intent.getAction())
					|| TextUtils.equals(LehuoIntent.ACTION_FEED_PUBLISH_OK,
							intent.getAction())) {
				homeList.refresh();
			}
		}
	};

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(LehuoIntent.FRIEND_FEED_UPDATE);
		filter.addAction(LehuoIntent.ACTION_FEED_PUBLISH_OK);
		registerReceiver(mReceiver, filter);
	}

	private void unregisterReceiver() {
		unregisterReceiver(mReceiver);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver();
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	public void initView() {
		feedOnClickListener = new FeedOnClickListener();
		mInflater = LayoutInflater.from(this);
		// ------------------------------------------------------------
		homeListView = (ListView) findViewById(R.id.list);
		// homeListView.setOnItemClickListener(new HomeItemClickListener());
		header = (LinearLayout) getInflaterLayout(R.layout.feed_header);
		footer = getInflaterLayout(R.layout.feed_footer);
		toTopInit();
		toEndInit();
		homeList = new HomeListView(homeListView, header, footer);
		homeList.setOnScrollEndListener(this);
		homeList.setTapGestureRecognizeListener(this);
		// ----------------------------title--------------------------------
		TextView title = (TextView) findViewById(R.id.title_text);
		title.setText(getResources().getString(R.string.feed_friend_title));
		title_turnpage = (ImageButton) findViewById(R.id.turnpage);
		title_write = (ImageButton) findViewById(R.id.write);
		title_write.setVisibility(View.GONE);
		title_turnpage.setVisibility(View.GONE);
		// title_write.setOnClickListener(feedOnClickListener);
//		 title_turnpage.setOnClickListener(feedOnClickListener);
		// -----------------------------null-list------------------------------
		// linearNull = (LinearLayout) findViewById(R.id.linear_null);
		// listNull = (TextView) findViewById(R.id.list_null);
		// listNull.setOnClickListener(feedOnClickListener);
		homeProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mEmptyView = (LinearLayout) findViewById(R.id.empty_view);
		mEmptyView.findViewById(R.id.wxcb_empty_btn).setVisibility(View.GONE);
//		mEmptyView.setOnClickListener(feedOnClickListener);
	}

	private String getMinId() {
		List<HomeData> datas = homeList.getData();
		if (datas == null) {
			return "-1";
		}
		return datas.get(datas.size() - 1).PublicUser.postId;
	}

	private String getMaxId() {
		List<HomeData> datas = homeList.getData();
		if (datas == null) {
			return "-1";
		}
		return datas.get(0).PublicUser.postId;
	}

	// -----------------------------------updata-----------------------------

	private void updateView(final List<HomeData> homeDataList, final int type) {
		mHandler.post(new Runnable() {
			public void run() {
				homeList.closeHeaderFooter();
				homeProgressBar.setVisibility(View.GONE);
				// linearNull.setVisibility(View.GONE);
				// listNull.setVisibility(View.GONE);
				mEmptyView.setVisibility(View.GONE);
				// themeTakeButton.setVisibility(View.GONE);
				homeListView.setVisibility(View.VISIBLE);
				isRefreshing = false;
				List<HomeData> data = homeList.getData();
				if (homeDataList == null || homeDataList.size() < 1) {
					if (!TextUtils.isEmpty(mHomeDeleteId)) {
						deleteHomeList(data);
						return;
					}
					if (!isFriendNull && type == FriendAction.PARAM_REFRESH) {// 刷新
						homeList.refreshFinish(getResources().getString(
								R.string.feed_newed));
					} else if (isFriendNull) {// 页面没有数据
						homeListView.setVisibility(View.GONE);
						// themeTakeButton.setVisibility(View.GONE);
						// linearNull.setVisibility(View.VISIBLE);
						// listNull.setVisibility(View.VISIBLE);
						// listNull.setText(getResources().getString(
						// R.string.add_friend_lable));
						((TextView) mEmptyView.findViewById(R.id.wxcb_tishi)).setText(FriendFeedActivity.this
								.getString(R.string.no_has_friend_warn));
						mEmptyView.setVisibility(View.VISIBLE);
					} else if (!isFriendNull && type == FriendAction.PARAM_PAGE) {// 取下一页，数据为空隐藏footer
						homeList.addData(null, PageSize.HOME_FEED_PAGESIZE);
					}
					// showToast(FeedActivity.this, R.string.feed_refresh_null);
					return;
				}
				int count = 0;
				if (type == FriendAction.PARAM_PAGE) {
					homeList.addData(homeDataList, PageSize.HOME_FEED_PAGESIZE);
				} else if (type == FriendAction.PARAM_KEEP) {
					homeList.setData(homeDataList, PageSize.HOME_FEED_PAGESIZE);
				} else {// 刷新：小于20，则新老数据都显示；否则，丢掉老数据，显示新数据
					count = homeDataList.size();
					if (homeDataList != null && data != null && data.size() > 0
							&& count < PageSize.HOME_FEED_STATTARD_PAGESIZE) {
						homeDataList.addAll(data);
					}
					homeList.setData(homeDataList, PageSize.HOME_FEED_PAGESIZE);
				}
				if (!isFriendNull && type == FriendAction.PARAM_REFRESH
						&& count != 0) {
					homeList.refreshFinish(getResources().getString(
							R.string.feed_get_string)
							+ count
							+ getResources().getString(
									R.string.feed_share_string));
				}
				homeList.setLockEnd(false);
				isFriendNull = false;
			}
		});
	}

	private List<HomeData> deleteHomeList(List<HomeData> homeDataList) {
		List<HomeData> list = homeDataList;
		if (list != null && list.size() > 0) {
			for (HomeData homeData : list) {
				if (homeData.PublicUser.postId.equals(mHomeDeleteId)) {
					list.remove(homeData);
					homeList.setData(list, PageSize.HOME_FEED_PAGESIZE);
					mHomeDeleteId = null;
					if (list.size() > 1) {
						isFriendNull = false;
					}
					break;
				}
			}
		}
		return list;
	}

	// -----------------------------------load-----------------------------

	private boolean treateFileCache() {
		if (!isFriendNull) {
			return false;
		}
		List<HomeData> saveList = FriendFileStore.getInstance()
				.loadFriend(this);
		if (saveList == null) {
			return false;
		}
		// ---------------------------------取得charsequence------------------------------
		ParserListView parser = ParserListView.getInstance();
		HomeData homeData = null;
		for (int i = 0; i < saveList.size(); i++) {
			homeData = saveList.get(i);
			if (homeData == null) {
				continue;
			}
			tranceCharSqence(homeData.PublicUser, parser);
			if (("1".equals(homeData.PublicUser.feedType) || "2"
					.equals(homeData.PublicUser.feedType))
					&& homeData.originUser != null) {// 0原创1转发2喜欢
				homeData.originUser.charSequence = parser.getCharSqence(this,
						null, homeData.originUser.contents);
				// int end = 0;
				// if (homeData.originUser.name != null) {
				// end = homeData.originUser.name.length();
				// }
				// homeData.originUser.charSequence = parser
				// .createStyle(this, end);
				homeData.originUser.nameCharSequence = parser
						.createStyle(homeData.originUser.name);
			}
		}
		// ---------------------------------取得charsequence------------------------------
		updateView(saveList, FriendAction.PARAM_REFRESH);
		return true;
	}

	private void tranceCharSqence(User user, ParserListView parser) {
		if (user == null) {
			return;
		}
		user.charSequence = parser.getCharSqence(this, null, user.contents);
	}

	private void loadFriendData(int type) {
		if (isRefreshing) {
			return;
		}
		if (!isFriendNull && type == FriendAction.PARAM_REFRESH) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					homeListView.setSelection(0);
				}
			});
		}
		isRefreshing = true;
		if (isFriendNull) {
			homeProgressBar.setVisibility(View.VISIBLE);
		}
		// if (treateFileCache()) {
		// return;
		// }
		// --------------------------------------------------------------
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FriendAction.PARAM_MAXPOST_ID, getMaxId());
		params.put(FriendAction.PARAM_MINPOST_ID, getMinId());
		params.put(FriendAction.PARAM_TYPE, type);
		ActionController.post(FriendFeedActivity.this, FriendAction.class, // action的class
				params, //
				new FriendAction.ISearchResultListener() {// 回调
					@Override
					public void onEnd(final List<HomeData> homeDataList,
							final int type) {
						Log.d("testTime",
								"loadFriendData onEnd:"
										+ System.currentTimeMillis());
						updateView(homeDataList, type);
						mHandler.post(new Runnable() {
							public void run() {
								Log.d("testTime",
										"show onEnd:"
												+ System.currentTimeMillis());
							}
						});
						isRefreshing = false;
					}

					@Override
					public void onStart() {
						Log.d("testTime",
								"loadFriendData begin:"
										+ System.currentTimeMillis());
					}

					@Override
					public void onFail(final int resourceID) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								homeProgressBar.setVisibility(View.GONE);
								homeList.closeHeaderFooter();
								if (isFriendNull) {
									// title_refresh
									// .setBackgroundResource(R.drawable.feed_title_button_bg);
									// title_refresh
									// .setImageResource(R.drawable.common_button_refresh_normal);
									homeListView.setVisibility(View.GONE);
									// themeTakeButton.setVisibility(View.GONE);
									// linearNull.setVisibility(View.VISIBLE);
									// listNull.setVisibility(View.VISIBLE);
									// listNull.setText(getResources().getString(
									// R.string.feed_null));
								} else {
									showToast(FriendFeedActivity.this,
											resourceID);
								}
								isRefreshing = false;
							}
						});
					}
				}, true);// 是否起线程
	}

	// -----------------------------------------------------------------------------------

	public View getInflaterLayout(int resource) {
		return mInflater.inflate(resource, null);
	}

	// --------------------------------------home-item-进内容页-----------------------------------
	private class HomeItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// ----------
			// if (homeList.getHeader() != null) {
			// position -= 1;
			// }
			// List<HomeData> list = homeList.getData();
			// if (position < 0 || position >= list.size()) {
			// return;
			// }
			// HomeData data = list.get(position);
			// Bundle bundle = new Bundle();
			// Class c = null;
			// // --------------------
			// if ("0".equals(data.PublicUser.feedType)) {
			// c = ContentOriginActivity.class;
			// bundle.putString(ContentOriginActivity.ORIGIN_FEED_ID,
			// data.PublicUser.postId);// 源动态id
			// } else {
			// c = ContentTranspondActivity.class;
			// bundle.putString(ContentTranspondActivity.TRANSPOND_FEED_ID,
			// data.PublicUser.postId);// 转发动态id
			// }
			// // --------------------
			// Intent intent = new Intent();
			// intent.putExtras(bundle);
			// intent.setClass(FriendFeedActivity.this, c);
			// startActivityForResult(intent,
			// request_delete_code);
		}
	}

	@Override
	public void onTapGestureRecognizeListener(String[] str) {
		if (str == null) {
			return;
		}
		String postId = str[0];
		String feedType = str[1];
		Bundle bundle = new Bundle();
		Class c = null;
		// --------------------
		if ("0".equals(feedType)) {
			c = ContentOriginActivity.class;
			bundle.putString(ContentOriginActivity.ORIGIN_FEED_ID, postId);// 源动态id
		} else {
			c = ContentTranspondActivity.class;
			bundle.putString(ContentTranspondActivity.TRANSPOND_FEED_ID, postId);// 转发动态id
		}
		// --------------------
		Intent intent = new Intent();
		intent.putExtras(bundle);
		intent.setClass(FriendFeedActivity.this, c);
		startActivityForResult(intent, request_delete_code);
	}

	// -----------------------------------上拉下拉刷新处理---------------------------------------

	@Override
	public void onScrollEnd() {
		loadFriendData(FriendAction.PARAM_PAGE);
	}

	@Override
	public void onScrollHeader() {
		loadFriendData(FriendAction.PARAM_REFRESH);
	}

	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private TextView footer_tipsTextview;
	private TextView footer_lastUpdatedTextView;
	private ImageView footer_arrowImageView;
	private ProgressBar footer_progressBar;

	// heed传进来，进行事件处理，

	private void toEndInit() {
		footer_arrowImageView = (ImageView) footer
				.findViewById(R.id.head_arrowImageView);
		// arrowImageView.setMinimumWidth(70);
		// arrowImageView.setMinimumHeight(50);
		footer_progressBar = (ProgressBar) footer
				.findViewById(R.id.head_progressBar);
		footer_tipsTextview = (TextView) footer
				.findViewById(R.id.head_tipsTextView);
		footer_lastUpdatedTextView = (TextView) footer
				.findViewById(R.id.head_lastUpdatedTextView);

		// animation = new RotateAnimation(0, -180,
		// RotateAnimation.RELATIVE_TO_SELF, 0.5f,
		// RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		// animation.setInterpolator(new LinearInterpolator());
		// animation.setDuration(250);
		// animation.setFillAfter(true);
		//
		// reverseAnimation = new RotateAnimation(-180, 0,
		// RotateAnimation.RELATIVE_TO_SELF, 0.5f,
		// RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		// reverseAnimation.setInterpolator(new LinearInterpolator());
		// reverseAnimation.setDuration(200);
		// reverseAnimation.setFillAfter(true);
	}

	private void toTopInit() {
		arrowImageView = (ImageView) header
				.findViewById(R.id.head_arrowImageView);
		// arrowImageView.setMinimumWidth(70);
		// arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) header.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) header.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) header
				.findViewById(R.id.head_lastUpdatedTextView);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);
	}

	// -----------------------------------title-event-----------------------------------------
	final public static int pulbicRequest = 10, publicOk = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case pulbicRequest:
			homeList.refresh();
			break;
		case result_delete_code:
			mHomeDeleteId = data.getStringExtra("postId");
			updateView(null, FriendAction.PARAM_REFRESH);
			break;
		}
	}

	private class FeedOnClickListener implements
			android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.write:
				Intent intent = new Intent(FriendFeedActivity.this,
						PublishPage.class);
				startActivityForResult(intent, publicOk);
				break;
			case R.id.turnpage:
				if (!isTurnPage) {
					isTurnPage = true;
					intent = new Intent(FriendFeedActivity.this,
							FriendManagerActivity.class);
					startActivity(intent);
				}

				break;
			case R.id.empty_view:
				intent = new Intent();
				intent.setClass(FriendFeedActivity.this,
						FriendManagerActivity.class);
				// intent.putExtra(TopicSubPage.INTENT_FROM_KEY,
				// TopicSubPage.INTENT_FROM_FEED);
				startActivity(intent);
				break;
			}
		}

	}

}
