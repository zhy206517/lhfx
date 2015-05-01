package com.youa.mobile.life;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.youa.mobile.R;
import com.youa.mobile.circum.action.CircumAction;
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
import com.youa.mobile.life.action.DistrictAction.DistrictActionResultListener;
import com.youa.mobile.life.action.DistrictAction.RequestType;
import com.youa.mobile.life.data.DistrictData;

public class DistricFeedPage extends BasePage implements
		BaseListView.OnScrollEndListener, BaseListView.OnListItemListener {

	public final static int REQUEST_COUNT = 50;
	public final static String KEYWORD = "keyword", FEED_TYPE = "type", DISTRIC_STATUS = "distric_status" , DISTRIC_ID = "distric_id";
	// -----------friend-theme-circum---------------
	private HomeListView homeList;
	private ListView homeListView;
	// private RelativeLayout footer;
	/*private View footer;
	private LinearLayout header;
	private LayoutInflater mInflater;*/
	private Handler mHandler = new Handler();
	private ProgressBar homeProgressBar;
	private LinearLayout linearNull;
	private TextView listNull;
	
	private ImageButton mBackButton;
	private ImageButton mPublishTopicButton;
	private boolean isRefreshing;
	private TextView mDistricName;
	private String mKeyWord;
	private String mDistricId;
	private CheckBox mDistricStatus;
	private TextView mDistricStatusText;
	private boolean isSubscribe;
	private int mDistricPosition;
	// ------------------theme collection----------
	private FeedOnTouchListener feedOnTouchListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topic_feed_list);
		HintPageUtil.checkHint(HintPageUtil.HINT_HOME, this, mHandler);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mKeyWord = bundle.getString(KEYWORD);
		isSubscribe = bundle.getBoolean(DISTRIC_STATUS);
		mDistricId = bundle.getString(DISTRIC_ID);
		mDistricPosition = bundle.getInt(DistrictPage.UPDATE_DISTRICT_STATUS);
		initView();
		loadDistricData(mDistricId);
	}

	public void initView() {
		//mInflater = LayoutInflater.from(this);
		mDistricStatus = (CheckBox)findViewById(R.id.topic_status);
		mDistricStatus.setFocusableInTouchMode(false);
		mDistricStatus.setChecked(isSubscribe);
		mDistricStatus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				subTopic();
			}
		});
		mDistricStatusText = (TextView) findViewById(R.id.topic_status_text);	
		mDistricStatusText.setText(R.string.distric_unsub_checkbox_lable);
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
		mPublishTopicButton = (ImageButton) title.findViewById(R.id.subtopic_btn);
		mDistricName = (TextView)title.findViewById(R.id.topic_name);
		mDistricName.setText(mKeyWord);
		feedOnTouchListener = new FeedOnTouchListener();
		mBackButton.setOnTouchListener(feedOnTouchListener);
		mPublishTopicButton.setOnTouchListener(feedOnTouchListener);
		mDistricStatusText.setOnTouchListener(feedOnTouchListener);
		// -----------------------------null-list------------------------------
		linearNull = (LinearLayout) findViewById(R.id.linear_null);
		listNull = (TextView) findViewById(R.id.list_null);
		homeProgressBar = (ProgressBar) findViewById(R.id.progressBar);
	}

	// -----------------------------------updata-----------------------------
	public static List<HomeData> saveHomeDataList;

	private void updateDistric(final List<HomeData> homeDataList) {
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


	private void loadDistricData(String districId) {
		if (districId == null || isRefreshing) {
			return;
		}
		isRefreshing = true;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(CircumAction.PARAM_PLACE_ID, districId);
		ActionController.post(DistricFeedPage.this, CircumAction.class, params,
				new CircumAction.ISearchResultListener() {
					@Override
					public void onFail(int resourceID) {
						showToast(DistricFeedPage.this, resourceID);
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
						updateDistric(homeDataList);
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
			intent.setClass(DistricFeedPage.this, c);
			DistricFeedPage.this.startActivity(intent);
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
						Intent i = new Intent(DistricFeedPage.this, PublishPage.class);
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
	
	private DistrictActionResultListener resultListener = new DistrictAction.DistrictActionResultListener(){
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
					mDistricStatus.setChecked(isSubscribe);
				}
			});
			showToast(resourceID);
		}
		@Override
		public void onFinish(final int resourceID, int position, final boolean topicStatus) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					homeProgressBar.setVisibility(View.GONE);
					isSubscribe = topicStatus;
					mDistricStatus.setChecked(isSubscribe);
					showToast(resourceID);
				}
			});
		}

		@Override
		public void onLoadDataFinish(List<DistrictData> data) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private synchronized void subTopic(){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				RequestType requestType;
				if(isSubscribe){
					requestType = RequestType.UNFOLLOW;
				}else{
					requestType = RequestType.FOLLOW;
				}
				Map<String,Object>	params = new HashMap<String, Object>();
				params.put(DistrictAction.KEY_DISTRICT_ID, mDistricId);
				params.put(DistrictAction.KEY_REQUEST_TYPE, requestType);
				params.put(DistrictAction.KEY_TOPIC_ITEM_POSITION, mDistricPosition);
				ActionController.post(DistricFeedPage.this, DistrictAction.class, params, resultListener, true);
			}
		});
	}
	@Override
	public void finish() {
		Intent i = new Intent();
		i.putExtra(DistrictPage.UPDATE_DISTRICT_POSITION, mDistricPosition);
		i.putExtra(DistrictPage.UPDATE_DISTRICT_STATUS, isSubscribe);
		setResult(RESULT_OK,i);
		super.finish();
	}
}
