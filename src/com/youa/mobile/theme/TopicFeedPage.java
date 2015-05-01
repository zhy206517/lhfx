package com.youa.mobile.theme;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.params.PageSize;
import com.youa.mobile.content.ContentOriginActivity;
import com.youa.mobile.content.ContentTranspondActivity;
import com.youa.mobile.friend.FriendFeedActivity;
import com.youa.mobile.friend.HomeListView;
import com.youa.mobile.friend.HomeListView.TapGestureRecognize;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.input.PublishPage;
import com.youa.mobile.login.LoginPage;
import com.youa.mobile.theme.action.ThemeAction;

public class TopicFeedPage extends BasePage implements
		BaseListView.OnScrollEndListener, BaseListView.OnListItemListener,
		TapGestureRecognize {

	public final static int REQUEST_COUNT = 50;
	public final static String KEYWORD = "keyword";
	// FEED_TYPE = "type", TOPIC_STATUS = "topic_status" , TOPIC_ID =
	// "topic_id";
	// -----------friend-theme-circum---------------
	private HomeListView homeList;
	private ListView homeListView;
	// private RelativeLayout footer;
	/*
	 * private View footer; private LinearLayout header; private LayoutInflater
	 * mInflater;
	 */
	private Handler mHandler = new Handler();
	private ProgressBar homeProgressBar;
	private LinearLayout linearNull;
	private TextView listNull;

	private ImageButton mBackButton;
	private ImageButton mPublishTopicButton;
	private boolean isRefreshing;
	private TextView mTopicName;
	private String mKeyWord;
	private String mTopicId;
	// private CheckBox mTopicStatus;
	// private TextView mTopicStatusText;
	// private boolean isSubscribe;
	private int mTopicPosition;
	// ------------------theme collection----------
	private FeedOnTouchListener feedOnTouchListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("testTime", "onCreate begin:" + System.currentTimeMillis());
		setContentView(R.layout.topic_feed_list);
		// HintPageUtil.checkHint(HintPageUtil.HINT_HOME, this, mHandler);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mKeyWord = bundle.getString(KEYWORD);
		// isSubscribe = bundle.getBoolean(TOPIC_STATUS);
		// mTopicId = bundle.getString(TOPIC_ID);
		// mTopicPosition = bundle.getInt(TopicSubPage.UPDATE_TOPIC_POSITION);
		initView();
		loadThemeData(mKeyWord);
		// homeProgressBar.setVisibility(View.VISIBLE);
	}

	public void initView() {
		// mInflater = LayoutInflater.from(this);
		// mTopicStatus = (CheckBox)findViewById(R.id.topic_status);
		// mTopicStatus.setFocusableInTouchMode(false);
		// mTopicStatus.setChecked(isSubscribe);
		// mTopicStatus.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// subTopic();
		// }
		// });
		// mTopicStatusText = (TextView) findViewById(R.id.topic_status_text);
		homeListView = (ListView) findViewById(R.id.list);
		// homeListView.setOnItemClickListener(new HomeItemClickListener());
		/*
		 * header = (LinearLayout) getInflaterLayout(R.layout.feed_header);
		 * footer = getInflaterLayout(R.layout.feed_footer); toTopInit();
		 * toEndInit();
		 */
		homeList = new HomeListView(homeListView, null, null);
		homeList.setOnScrollEndListener(this);
		homeList.setTapGestureRecognizeListener(this);
		// ----------------------------title--------------------------------
		RelativeLayout title = (RelativeLayout) findViewById(R.id.title);
		mBackButton = (ImageButton) title.findViewById(R.id.back);
		mPublishTopicButton = (ImageButton) title
				.findViewById(R.id.subtopic_btn);
		mTopicName = (TextView) title.findViewById(R.id.topic_name);
		mTopicName.setText(mKeyWord);
		feedOnTouchListener = new FeedOnTouchListener();
		mBackButton.setOnTouchListener(feedOnTouchListener);
		mPublishTopicButton.setOnTouchListener(feedOnTouchListener);
		LinearLayout bottom = (LinearLayout) this.findViewById(R.id.bottom);
		bottom.setOnTouchListener(feedOnTouchListener);
		mPublishTopicButton.setVisibility(View.GONE);
		// mTopicStatusText.setOnTouchListener(feedOnTouchListener);
		// -----------------------------null-list------------------------------
		linearNull = (LinearLayout) findViewById(R.id.linear_null);
		listNull = (TextView) findViewById(R.id.list_null);
		homeProgressBar = (ProgressBar) findViewById(R.id.progressBar);
	}

	// -----------------------------------updata-----------------------------
	public static List<HomeData> saveHomeDataList;

	private void updateTheme(final List<HomeData> homeDataList) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				linearNull.setVisibility(View.GONE);
				listNull.setVisibility(View.GONE);
				homeListView.setVisibility(View.VISIBLE);
				homeList.closeHeaderFooter();
				isRefreshing = false;
				if (homeDataList == null || homeDataList.size() < 1) {
					homeListView.setVisibility(View.GONE);
					linearNull.setVisibility(View.VISIBLE);
					listNull.setVisibility(View.VISIBLE);
					listNull.setText(getResources().getString(
							R.string.feed_null));
					return;
				}
				homeList.setLockEnd(true);
				homeList.setData(homeDataList, PageSize.HOME_FEED_MAXSIZE);
			}
		});
	}

	private void loadThemeData(String keyword) {
		if (keyword == null || isRefreshing) {
			return;
		}
		isRefreshing = true;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(ThemeAction.PARAM_KEYWORD, keyword);
		ActionController.post(TopicFeedPage.this, ThemeAction.class, params,
				new ThemeAction.ISearchResultListener() {
					@Override
					public void onFail(int resourceID) {
						showToast(TopicFeedPage.this, resourceID);
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								homeProgressBar.setVisibility(View.GONE);
								homeList.closeHeaderFooter();
							}
						});
					}

					@Override
					public void onStart() {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								homeProgressBar.setVisibility(View.VISIBLE);
							}
						});
					}

					@Override
					public void onEnd(List<HomeData> homeDataList) {
						updateTheme(homeDataList);
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								homeProgressBar.setVisibility(View.GONE);
							}
						});
					}
				}, true);
	}

	/*
	 * public View getInflaterLayout(int resource) { return
	 * mInflater.inflate(resource, null); }
	 */

	// --------------------------------------home-item-进内容页-----------------------------------
	private class HomeItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (homeList.getHeader() != null) {
				position -= 1;
			}
			List<HomeData> list = homeList.getData();
			if (position >= list.size()) {
				return;
			}

			HomeData data = list.get(position);
			Bundle bundle = new Bundle();
			Class c = null;
			// --------------------
			if ("0".equals(data.PublicUser.feedType)) {
				c = ContentOriginActivity.class;
				bundle.putString(ContentOriginActivity.ORIGIN_FEED_ID,
						data.PublicUser.postId);// 源动态id
			} else {
				c = ContentTranspondActivity.class;
				bundle.putString(ContentTranspondActivity.TRANSPOND_FEED_ID,
						data.PublicUser.postId);// 转发动态id
			}
			// --------------------
			Intent intent = new Intent();
			intent.putExtras(bundle);
			intent.setClass(TopicFeedPage.this, c);
			TopicFeedPage.this.startActivity(intent);
		}
	}

	// -----------------------------------上拉下拉刷新处理---------------------------------------

	@Override
	public void onScrollEnd() {
		// loadFriendData(FriendAction.PARAM_PAGE);
	}

	@Override
	public void onScrollHeader() {
		// loadThemeData(mKeyWord);
	}

	@Override
	public void onUpEvent(final View v) {

	}

	// -----------------------------------title-event-----------------------------------------
	private class FeedOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				switch (v.getId()) {
				case R.id.back:
					finish();
					break;
				case R.id.bottom:
					if (!ApplicationManager.getInstance().isLogin()) {
						Intent i = new Intent(TopicFeedPage.this,
								LoginPage.class);
						startActivity(i);
						break;
					}
					// subTopic();
					Intent i = new Intent(TopicFeedPage.this, PublishPage.class);
					i.putExtra(PublishPage.KEY_FROM_TOPIC, mKeyWord);
					startActivity(i);
					break;
				}
				break;
			}
			return true;
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
		intent.setClass(TopicFeedPage.this, c);
		startActivityForResult(intent, FriendFeedActivity.request_delete_code);

	}
}
