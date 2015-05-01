package com.youa.mobile.life;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.params.PageSize;
import com.youa.mobile.common.util.HintPageUtil;
import com.youa.mobile.content.ContentOriginActivity;
import com.youa.mobile.content.ContentTranspondActivity;
import com.youa.mobile.friend.HomeListView;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.input.PublishPage;
import com.youa.mobile.life.action.DistrictAction;
import com.youa.mobile.life.action.DistrictAction.RequestType;
import com.youa.mobile.theme.action.ThemeAction;
import com.youa.mobile.theme.action.TopicAction;
import com.youa.mobile.theme.action.TopicAction.ITopicActionResultListener;
import com.youa.mobile.theme.data.TopicData;

public class DistrictFeedPage extends BasePage implements
		BaseListView.OnScrollEndListener, BaseListView.OnListItemListener {

	public final static int REQUEST_COUNT = 50;
	public final static String KEYWORD = "keyword", FEED_TYPE = "type", DISTRICT_STATUS = "topic_status" , DISTRICT_ID = "topic_id";
	private HomeListView homeList;
	private ListView homeListView;
	private Handler mHandler = new Handler();
	private ProgressBar homeProgressBar;
	private LinearLayout linearNull;
	private TextView listNull;
	
	private ImageButton mBackButton;
	private ImageButton mPublishDistrictButton;
	private boolean isRefreshing;
	private TextView mDistrictName;
	private String mKeyWord;
	private String mDistrictId;
	private CheckBox mDistrictStatus;
	private TextView mDistrictStatusText;
	private boolean isFollowed;
	private int mDistrictPosition;
	private FeedOnTouchListener feedOnTouchListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("testTime", "onCreate begin:" + System.currentTimeMillis());
		setContentView(R.layout.topic_feed_list);
		HintPageUtil.checkHint(HintPageUtil.HINT_HOME, this, mHandler);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mKeyWord = bundle.getString(KEYWORD);
		isFollowed = bundle.getBoolean(DISTRICT_STATUS);
		mDistrictId = bundle.getString(DISTRICT_ID);
		//mDistrictPosition = bundle.getInt(DistrictPage.UPDATE_DISTRICT_POSITION);
		initView();
		loadThemeData(mKeyWord);
		//homeProgressBar.setVisibility(View.VISIBLE);
	}

	public void initView() {
		//mInflater = LayoutInflater.from(this);
		mDistrictStatus = (CheckBox)findViewById(R.id.topic_status);
		mDistrictStatus.setFocusableInTouchMode(false);
		mDistrictStatus.setChecked(isFollowed);
		mDistrictStatus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				subTopic();
			}
		});
		mDistrictStatusText = (TextView) findViewById(R.id.topic_status_text);		
		homeListView = (ListView) findViewById(R.id.list);
		homeListView.setOnItemClickListener(new HomeItemClickListener());
		/*header = (LinearLayout) getInflaterLayout(R.layout.feed_header);
		footer = getInflaterLayout(R.layout.feed_footer);
		toTopInit();
		toEndInit();*/
		homeList = new HomeListView(homeListView, null, null);
		homeList.setOnScrollEndListener(this);
		// ----------------------------title--------------------------------
		RelativeLayout title = (RelativeLayout) findViewById(R.id.title);
		mBackButton = (ImageButton) title.findViewById(R.id.back);
		mPublishDistrictButton = (ImageButton) title.findViewById(R.id.subtopic_btn);
		mDistrictName = (TextView)title.findViewById(R.id.topic_name);
		mDistrictName.setText(mKeyWord);
		feedOnTouchListener = new FeedOnTouchListener();
		mBackButton.setOnTouchListener(feedOnTouchListener);
		mPublishDistrictButton.setOnTouchListener(feedOnTouchListener);
		mDistrictStatusText.setOnTouchListener(feedOnTouchListener);
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
		ActionController.post(DistrictFeedPage.this, ThemeAction.class, params,
				new ThemeAction.ISearchResultListener() {
					@Override
					public void onFail(int resourceID) {
						showToast(DistrictFeedPage.this, resourceID);
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

	/*public View getInflaterLayout(int resource) {
		return mInflater.inflate(resource, null);
	}*/

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
			intent.setClass(DistrictFeedPage.this, c);
			DistrictFeedPage.this.startActivity(intent);
		}
	}

	// -----------------------------------上拉下拉刷新处理---------------------------------------

	@Override
	public void onScrollEnd() {
		//loadFriendData(FriendAction.PARAM_PAGE);
	}

	@Override
	public void onScrollHeader() {
		//loadThemeData(mKeyWord);
	}

	@Override
	public void onUpEvent(final View v) {
		
	}

	// -----------------------------------title-event-----------------------------------------
	private class FeedOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
//			case MotionEvent.ACTION_DOWN:
//				switch (v.getId()) {
//				case R.id.back:
//				case R.id.subtopic_btn:
//					v.setBackgroundResource(R.drawable.feed_title_button_bg_sel);
//					break;
//				}
//				break;
//			case MotionEvent.ACTION_MOVE:
//				break;
			case MotionEvent.ACTION_UP:
				switch (v.getId()) {
					case R.id.back:
						finish();
						break;
					case R.id.subtopic_btn:
						//subTopic();
						Intent i = new Intent(DistrictFeedPage.this, PublishPage.class);
						i.putExtra(PublishPage.KEY_FROM_TOPIC, mKeyWord);
						startActivity(i);
						break;
					case R.id.topic_status_text:
						subTopic();
						break;
				}
				break;
			}
			return true;
		}
	}
	
	private ITopicActionResultListener resultListener = new TopicAction.ITopicActionResultListener(){
		@Override
		public void onGetAllFinish(List<TopicData> data) {
		}

		@Override
		public void onStart(Integer resourceID) {
			if(null != resourceID)
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						homeProgressBar.setVisibility(View.VISIBLE);
					}
				});
		}

		@Override
		public void onFail(int resourceID) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					homeProgressBar.setVisibility(View.GONE);
					mDistrictStatus.setChecked(isFollowed);
				}
			});
			showToast(resourceID);
		}

		@Override
		public void onFinish(final int resourceID, int position, final boolean status) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					homeProgressBar.setVisibility(View.GONE);
					isFollowed = status;
					mDistrictStatus.setChecked(isFollowed);
					showToast(resourceID);
				}
			});
		}
	};
	
	private synchronized void subTopic(){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				RequestType requestType = isFollowed ? RequestType.UNFOLLOW : RequestType.FOLLOW;
				Map<String,Object>	params = new HashMap<String, Object>();
				params.put(DistrictAction.KEY_DISTRICT_ID, mDistrictId);
				params.put(DistrictAction.KEY_REQUEST_TYPE, requestType);
				params.put(DistrictAction.KEY_TOPIC_ITEM_POSITION, mDistrictPosition);
				ActionController.post(DistrictFeedPage.this, DistrictAction.class, params, resultListener, true);
				sendBroadcast(new Intent(LehuoIntent.ACTION_USER_TOPIC_CHANGE));
			}
		});
	}
	
	@Override
	public void finish() {
		Intent i = new Intent();
		//i.putExtra(DistrictPage.UPDATE_DISTRICT_POSITION, mDistrictPosition);
		i.putExtra(DistrictPage.UPDATE_DISTRICT_STATUS, isFollowed);
		setResult(RESULT_OK,i);
		super.finish();
	}
}
