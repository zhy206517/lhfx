package com.youa.mobile.information;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.SystemConfig;
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
import com.youa.mobile.information.action.SearchEnjoyFeedAction;
import com.youa.mobile.information.action.SearchOwnFeedAction;
import com.youa.mobile.news.AddMePage;

public class EnjoyFeedPage extends BasePage implements BaseListView.OnScrollEndListener,TapGestureRecognize {// OnItemClickListener,
public static final String KEY_UID = "uid";
	
	private HomeListView mListView;
	private LayoutInflater mInflater;
	private List<HomeData> mDataList;
	private View mFooterView;
	private LinearLayout mHeaderView;
	private String mUid;
	private int mPageIndex = 1;
	private boolean mRegisterRefresh;
	private ImageButton mBackButton;
	private Button mSendButton;
	private TextView mTitle;
	private String mUserName;
	LinearLayout mEmptyView;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_ownfeed);
        
        if (savedInstanceState == null) {
			mRegisterRefresh = getIntent().getBooleanExtra(
					PersonnalInforPage.EXTRA_REGISTER_REFRESH, false);
		} else {
			mRegisterRefresh = savedInstanceState
					.getBoolean(PersonnalInforPage.EXTRA_REGISTER_REFRESH);
		}
        
        initDataFromIntent(getIntent());
        initTitle();
        init();
        toTopInit();
        loadData(true);
    }
	public void initTitle() {
		mBackButton = (ImageButton)this.findViewById(R.id.back);
        mBackButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EnjoyFeedPage.this.finish();
			}});
        mTitle = (TextView)this.findViewById(R.id.title);
        if(!TextUtils.isEmpty(ApplicationManager.getInstance().getUserId())&&ApplicationManager.getInstance().getUserId().equals(mUid)){
        	mTitle.setText(getString(R.string.enjoy_feed_title, "我"));
        }else{
        	mTitle.setText(getString(R.string.enjoy_feed_title, mUserName));
        }
        mSendButton = (Button)this.findViewById(R.id.send);
        mSendButton.setVisibility(View.GONE);
	}
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putBoolean(PersonnalInforPage.EXTRA_REGISTER_REFRESH, mRegisterRefresh);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
		if (mRegisterRefresh) {
			registerReceiver();
		}
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	if (mRegisterRefresh) {
    		unregisterReceiver();
    	}
    };
    
    private BroadcastReceiver mRefreshReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (TextUtils.equals(SystemConfig.ACTION_REFRESH_MY,
					intent.getAction())) {
				mListView.refresh();
			}
		}
	};
	
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(SystemConfig.ACTION_REFRESH_MY);
		registerReceiver(mRefreshReceiver, filter);
	}

	private void unregisterReceiver() {
		unregisterReceiver(mRefreshReceiver);
	}
	
    private void initDataFromIntent(Intent intent) {
		mUid = intent.getStringExtra(KEY_UID);	
    	mUserName=intent.getStringExtra(PersonnalInforPage.KEY_USER_NAME);
	}
    private void init() {
    	mInflater = LayoutInflater.from(this);
    	mHeaderView = (LinearLayout) mInflater.inflate(R.layout.feed_header, null);
//    	mHeaderView = (LinearLayout) findViewById(R.id.header);
    	mFooterView =mInflater.inflate(R.layout.feed_footer, null);
    	ListView listView = (ListView) findViewById(R.id.list);
    	mListView = new HomeListView(listView, mHeaderView, mFooterView);
    	//mListView.getAdapter().hideShowView(R.id.user_head, R.id.timer_line_layout);
    	mListView.setOnScrollEndListener(this);
    	mListView.setTapGestureRecognizeListener(this);
//    	listView.setOnItemClickListener(this);
		mProcessView = findViewById(R.id.progressBar);
		mLoadView = listView;
		mEmptyView = (LinearLayout) findViewById(R.id.empty_view);
		mEmptyView.findViewById(R.id.wxcb_empty_btn).setVisibility(View.GONE);
		showProgressView();
    }

    private void updateViews(final List<HomeData> feedDataList, final int pageIndex){
    	
		mPageIndex = pageIndex;
		mHandler.post(new Runnable(){
			public void run() {
				mEmptyView.setVisibility(View.GONE);
				mListView.closeHeaderFooter();
				if(pageIndex == 1) {//刷新
					mDataList = feedDataList;
					if(mDataList == null||mDataList.size()==0) {
						((TextView) mEmptyView.findViewById(R.id.wxcb_tishi)).setText(EnjoyFeedPage.this
								.getString(R.string.my_like_num_zero));
						mEmptyView.setVisibility(View.VISIBLE);
					}else{
						mListView.setData(mDataList, PageSize.INFO_FEED_PAGESIZE);	
					}
				} else {
					if(feedDataList != null) {
						mListView.addData(feedDataList, PageSize.INFO_FEED_PAGESIZE);
//						mDataList.addAll(feedDataList);
					} else {
						mListView.addData(new ArrayList<HomeData>(), PageSize.INFO_FEED_PAGESIZE);
					}
				}								
				
			}
		});
		sendBroadcast(new Intent(LehuoIntent.ACTION_USERCOUNT_NEEDUPDATE));

    }

    private void loadData(boolean isRefreshOrGetMore) {

    	Map<String,Object>	params = new HashMap<String, Object>();
    	params.put(SearchOwnFeedAction.PARAM_UID, mUid);
    	params.put(SearchOwnFeedAction.PARAM_PAGEINDEX, mPageIndex);
		params.put(SearchOwnFeedAction.PARAM_REFRESH_OR_MORE, isRefreshOrGetMore);
    	ActionController.post(
    			this,
    			SearchEnjoyFeedAction.class,
    			params,
    			new SearchEnjoyFeedAction.SearchEnjoyFeedResult() {
		
					@Override
					public void onStart() {
					}
					
					public void onFail(int resourceID) {
						showToast(EnjoyFeedPage.this, resourceID);
						hiddenProgressView();
						
					}
					@Override
					public void onEnd(List<HomeData> feedData, int pageIndex) {
						updateViews((List<HomeData>)feedData, pageIndex);
						hiddenProgressView();
					}
    			},
    			true
    			);
    }

	@Override
	public void onScrollEnd() {
		loadData(false);
	}
	private long lastUpdataTime;
	@Override
	public void onScrollHeader() {
//		String updateTime = null;
//		if (lastUpdataTime == 0) {
//			// updateTime =
//			// getResources().getString(R.string.feed_refresh_null);
//		} else {
//			updateTime = Tools.translateToString(lastUpdataTime);
//		}
//		switch (mListView.getState()) {
//		case HomeListView.RELEASE_To_REFRESH:
//			arrowImageView.setVisibility(View.VISIBLE);
//			progressBar.setVisibility(View.GONE);
//			tipsTextview.setVisibility(View.VISIBLE);
//			lastUpdatedTextView.setVisibility(View.VISIBLE);
//			lastUpdatedTextView.setText(updateTime);
//			arrowImageView.clearAnimation();
//			arrowImageView.startAnimation(animation);
//			tipsTextview.setText("松开刷新");
//			break;
//		case HomeListView.PULL_To_REFRESH:
//			progressBar.setVisibility(View.GONE);
//			tipsTextview.setVisibility(View.VISIBLE);
//			lastUpdatedTextView.setVisibility(View.VISIBLE);
//			lastUpdatedTextView.setText(updateTime);
//			arrowImageView.clearAnimation();
//			arrowImageView.setVisibility(View.VISIBLE);
//			// 是由RELEASE_To_REFRESH状态转变来的
//			if (mListView.isBack) {
//				mListView.isBack = false;
//				arrowImageView.clearAnimation();
//				arrowImageView.startAnimation(reverseAnimation);
//
//				tipsTextview.setText("下拉刷新");
//			} else {
//				tipsTextview.setText("下拉刷新");
//			}
//			break;
//
//		case HomeListView.REFRESHING:
//			// -------------------------------------
//			// header.setVisibility(View.VISIBLE);
//			// Message msg = Message.obtain();
//			// msg.what = REQUEST_HOME_FRIEND;
//			// msg.arg1 = FEED_NEW;
//			// netFriend(1, 1, "1", msg);
			loadData(true);
//			lastUpdataTime = System.currentTimeMillis();
//			// -------------------------------------
//			mHeaderView.setPadding(0, 0, 0, 0);
//			progressBar.setVisibility(View.VISIBLE);
//			arrowImageView.clearAnimation();
//			arrowImageView.setVisibility(View.GONE);
//			tipsTextview.setText("正在刷新...");
//			lastUpdatedTextView.setText(updateTime);
//			lastUpdatedTextView.setVisibility(View.VISIBLE);
//			break;
//		case HomeListView.DONE:
//			mHeaderView.setPadding(0, -1 * mListView.getHeaderContent(), 0, 0);
//
//			progressBar.setVisibility(View.GONE);
//			arrowImageView.clearAnimation();
//			arrowImageView.setImageResource(R.drawable.feed_head_refresh);
//			tipsTextview.setText("下拉刷新");
//			lastUpdatedTextView.setVisibility(View.VISIBLE);
//			lastUpdatedTextView.setText(updateTime);
//			break;
//		}

	}
	
	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	private void toTopInit() {
		arrowImageView = (ImageView) mHeaderView
				.findViewById(R.id.head_arrowImageView);
		// arrowImageView.setMinimumWidth(70);
		// arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) mHeaderView.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) mHeaderView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) mHeaderView
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
	
	 @Override
		public void onTapGestureRecognizeListener(String[] str) {
			if(str==null){
				return;
			}
			String postId=str[0];
			String feedType=str[1];
			Bundle bundle = new Bundle();
			Class c = null;
			// --------------------
			if ("0".equals(feedType)) {
				c = ContentOriginActivity.class;
				bundle.putString(ContentOriginActivity.ORIGIN_FEED_ID,
						postId);// 源动态id
			} else {
				c = ContentTranspondActivity.class;
				bundle.putString(ContentTranspondActivity.TRANSPOND_FEED_ID,
						postId);// 转发动态id
			}
			// --------------------
			Intent intent = new Intent();
			intent.putExtras(bundle);
			intent.setClass(EnjoyFeedPage.this, c);
			startActivity(intent);
		}
	
//	@Override
//	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//		if (mListView.getHeader() != null) {
//			position -= 1;
//		} 
//		int size = mDataList.size();
//		if(position >= size) {
//			return;
//		}
//		
//		HomeData data = mDataList.get(position);
//		Bundle bundle = new Bundle();
//		Class c = null;
//		// --------------------
//		if ("0".equals(data.PublicUser.feedType)) {
//			c = ContentOriginActivity.class;
//			bundle.putString(ContentOriginActivity.ORIGIN_FEED_ID,
//					data.PublicUser.postId);// 源动态id
//		} else {
//			c = ContentTranspondActivity.class;
//			bundle.putString(ContentTranspondActivity.TRANSPOND_FEED_ID,
//					data.PublicUser.postId);// 转发动态id
//		}
//		// --------------------
//		Intent intent = new Intent();
//		intent.putExtras(bundle);
//		intent.setClass(this, c);
//		startActivity(intent);
//	}
}
