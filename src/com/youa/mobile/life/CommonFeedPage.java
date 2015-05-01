package com.youa.mobile.life;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.params.PageSize;
import com.youa.mobile.content.ContentOriginActivity;
import com.youa.mobile.content.ContentTranspondActivity;
import com.youa.mobile.friend.HomeListView;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.life.action.LoadFeedAction;
import com.youa.mobile.life.action.LoadFeedAction.RequestType;

public class CommonFeedPage extends BasePage implements
		BaseListView.OnScrollEndListener, BaseListView.OnListItemListener {
	public static final String TITLE = "title";
	
	public static final String DISTRICT_STATUS = "district_status";
	public static final String DISTRICT_POSITION = "district_position";
	public static int districtPositon = -1;
	public static boolean districtStatus = false;
	
	public final static int REQUEST_COUNT = 50;
	private HomeListView homeList;
	private ListView homeListView;
	private LayoutInflater mInflater;
	private View mFooterView;
	//private LinearLayout mHeaderView;
	private ProgressBar homeProgressBar;
	private LinearLayout linearNull;
	private TextView listNull;
	
	private ImageButton mBackButton;
	private ImageButton mPublishTopicButton;
	private TextView mTitle;
	//private boolean isRefreshing;
	private FeedOnTouchListener feedOnTouchListener;
	public static List<HomeData> saveHomeDataList;
	
	private int mPageIndex = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.life_common_feed_list);
		initView();
		getExtrasAndLoadData();
	}
	
	private void getExtrasAndLoadData(){
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle == null){
			return ;
		}
		String title = (String)bundle.get(TITLE);
		mTitle.setText(title);
		RequestType requestType = (RequestType)bundle.get(LoadFeedAction.REQUEST_TYPE);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(LoadFeedAction.REQUEST_TYPE, requestType);
		switch (requestType) {
			case SHARE_CLASSIFY:
				String clsid = intent.getStringExtra(LoadFeedAction.SHARECLASSIFY_ID);
				if(clsid!=null){
					params.put(LoadFeedAction.SHARECLASSIFY_ID, clsid);
				}
				params.put(LoadFeedAction.PAGE, 0);
				break;
			case DISTRICT:
				String districtid = intent.getStringExtra(LoadFeedAction.DISCTRICT_ID);
				districtStatus = intent.getBooleanExtra(DISTRICT_STATUS, false);
				districtPositon = intent.getIntExtra(DISTRICT_POSITION, -1);
				params.put(LoadFeedAction.DISCTRICT_ID, districtid);
				params.put(DISTRICT_STATUS, districtStatus);
				params.put(DISTRICT_POSITION, districtPositon);
				break;
		}
		loadFeedData(params);
	}
	
	public void initView() {
		mInflater = LayoutInflater.from(this);
		homeListView = (ListView) findViewById(R.id.list);
		homeListView.setOnItemClickListener(new HomeItemClickListener());
    	mFooterView =mInflater.inflate(R.layout.feed_footer, null);
    	
		homeList = new HomeListView(homeListView, null, mFooterView);
		homeList.setOnScrollEndListener(this);
		mBackButton = (ImageButton) findViewById(R.id.back);
		mPublishTopicButton = (ImageButton) findViewById(R.id.right_btn);
		mTitle = (TextView)findViewById(R.id.title_name);
		feedOnTouchListener = new FeedOnTouchListener();
		mBackButton.setOnTouchListener(feedOnTouchListener);
		mPublishTopicButton.setOnTouchListener(feedOnTouchListener);
		linearNull = (LinearLayout) findViewById(R.id.linear_null);
		listNull = (TextView) findViewById(R.id.list_null);
		homeProgressBar = (ProgressBar) findViewById(R.id.progressBar);
	}

	

	private void updateListItem(final List<HomeData> homeDataList, int pageIndex) {
		mPageIndex = pageIndex;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				linearNull.setVisibility(View.GONE);
				listNull.setVisibility(View.GONE);
				homeListView.setVisibility(View.VISIBLE);
				homeList.closeHeaderFooter();
				//isRefreshing = false;
				if (homeDataList == null || homeDataList.size() < 1) {
					homeListView.setVisibility(View.GONE);
					linearNull.setVisibility(View.VISIBLE);
					listNull.setVisibility(View.VISIBLE);
					listNull.setText(getResources().getString(R.string.feed_null));
					return;
				}
				homeList.setLockEnd(true);
				homeList.setData(homeDataList, PageSize.HOME_FEED_MAXSIZE);
			}
		});
	}


	private void loadFeedData(Map<String, Object> params) {
		if(null == params){
			return;
		}
		ActionController.post(CommonFeedPage.this, LoadFeedAction.class, params, new LoadFeedAction.LoadFeedActionListener() {
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
					public void onFinish(List<HomeData> data, int pageIndex) {
						updateListItem(data, pageIndex);
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								homeProgressBar.setVisibility(View.GONE);
							}
						});
					}
					@Override
					public void onFail(int resourceID) {
						showToast(CommonFeedPage.this, resourceID);
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								homeProgressBar.setVisibility(View.GONE);
								homeList.closeHeaderFooter();
							}
						});
						
					}
				}, true);
	}


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
			
			if ("0".equals(data.PublicUser.feedType)) {
				c = ContentOriginActivity.class;
				bundle.putString(ContentOriginActivity.ORIGIN_FEED_ID,
						data.PublicUser.postId);// 源动态id
			} else {
				c = ContentTranspondActivity.class;
				bundle.putString(ContentTranspondActivity.TRANSPOND_FEED_ID,
						data.PublicUser.postId);// 转发动态id
			}
			
			Intent intent = new Intent();
			intent.putExtras(bundle);
			intent.setClass(CommonFeedPage.this, c);
			CommonFeedPage.this.startActivity(intent);
		}
	}

	@Override
	public void onScrollEnd() {
	}

	@Override
	public void onScrollHeader() {
	}

	@Override
	public void onUpEvent(final View v) {
		
	}

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
						break;
					case R.id.topic_status_text:
						break;
				}
				break;
			}
			return true;
		}
	}
	
//	private ITopicActionResultListener resultListener = new TopicAction.ITopicActionResultListener(){
//		@Override
//		public void onGetAllFinish(List<TopicData> data) {
//		}
//
//		@Override
//		public void onStart(Integer resourceID) {
//			if(null != resourceID)
//				mHandler.post(new Runnable() {
//					@Override
//					public void run() {
//						homeProgressBar.setVisibility(View.VISIBLE);
//					}
//				});
//		}
//
//		@Override
//		public void onFail(int resourceID) {
//			mHandler.post(new Runnable() {
//				@Override
//				public void run() {
//					homeProgressBar.setVisibility(View.GONE);
//				}
//			});
//			showToast(resourceID);
//		}
//
//		@Override
//		public void onFinish(final int resourceID, int position, final boolean topicStatus) {
//			mHandler.post(new Runnable() {
//				@Override
//				public void run() {
//					homeProgressBar.setVisibility(View.GONE);
//					showToast(resourceID);
//				}
//			});
//		}
//	};
}
