package com.youa.mobile.news;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.content.ContentOriginActivity;
import com.youa.mobile.content.ContentTranspondActivity;
import com.youa.mobile.friend.FriendFeedActivity;
import com.youa.mobile.friend.HomeListView;
import com.youa.mobile.friend.HomeListView.TapGestureRecognize;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.news.action.RequestAddMeDataAction;
import com.youa.mobile.news.data.AddMeData;
import com.youa.mobile.news.util.NewsConstant;
import com.youa.mobile.news.util.NewsUtil;

public class AddMePage extends BaseNewsPage implements BaseListView.OnScrollEndListener ,TapGestureRecognize{

	private final static String TAG = "AddMePage";
	private HomeListView 	mListView;
	private TextView 				mNullText;
//	private List<HomeData> 	mDataList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_add_me);
        init();
        loadData(NewsConstant.REQUEST_DEFAULT_TIME, true);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	registerReceiver();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	unregisterReceiver();
    };
    
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (TextUtils.equals(SystemConfig.ACTION_REFRESH_NEWS,
					intent.getAction())) {
				mListView.refresh();
			}
		}
	};
	
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(SystemConfig.ACTION_REFRESH_NEWS);
		registerReceiver(mReceiver, filter);
	}

	private void unregisterReceiver() {
		unregisterReceiver(mReceiver);
	}

    private void init() {
    	ListView listView = (ListView) findViewById(R.id.list);
//    	listView.setOnItemClickListener(onItemClickListener);
    	mNullText = (TextView) findViewById(R.id.text_null);
    	mListView = new HomeListView(listView, mHeaderView, mFooterView);
    	mListView.setTapGestureRecognizeListener(this);
    	mListView.setOnScrollEndListener(this);
    	mFirstInProgressBar = (ProgressBar) findViewById(R.id.progressBar);
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
		intent.setClass(AddMePage.this, c);
		startActivity(intent);
	}

//    OnItemClickListener onItemClickListener = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			List<HomeData> 	mDataList = mListView.getData();
//			if (mHeaderView != null 
//					&& position > 0
//					&& position <= mDataList.size()) {
//				HomeData data = mDataList.get(position - 1);
//				startContentPage(data);
//			}
//		}
//    };
//
//    private void startContentPage(HomeData data) {
//    	String type = data.PublicUser.feedType;
//    	Bundle bundle = new Bundle();
//    	if (AddMeData.TYPE_FORWARD.equals(type)) {
//			bundle.putString(ContentTranspondActivity.TRANSPOND_FEED_ID,
//					data.PublicUser.postId);
//			Intent intent = new Intent(this, ContentTranspondActivity.class);
//	    	intent.putExtras(bundle);
//	    	startActivity(intent);
//    	} else if (AddMeData.TYPE_ORIGINAL.equals(type)) {
//    		NewsUtil.LOGD(TAG, "........type :" + type);
//			bundle.putString(ContentOriginActivity.ORIGIN_FEED_ID,
//					data.PublicUser.postId);// 源动态id
//			Intent intent = new Intent(this, ContentOriginActivity.class);
//	    	intent.putExtras(bundle);
//	    	startActivity(intent);
//    	}
//    }

    private void updateViews(final List<HomeData> list, final boolean isRefresh){

    		mHandler.post(new Runnable(){
    			public void run() {
    				mListView.closeHeaderFooter();
    				if (!NewsUtil.isEmpty(list)) {
	    				if(isRefresh) {
	    					mListView.setData(list, NewsConstant.REQUEST_DATA_LIMIT);
	    				} else {
	    					mListView.addData(list, NewsConstant.REQUEST_DATA_LIMIT);
	    				}
    				} else {
    					mListView.hiddenFooter();
    				}
					mNullText.setVisibility(NewsUtil.isEmpty(mListView.getData()) ? View.VISIBLE : View.GONE);
    			}
    		});
    }

    private void loadData(long time, boolean isRefresh) {

    	NewsUtil.LOGD(TAG, "enter loadData  <isRefresh> : " + isRefresh);
    	Map<String,Object>	params = new HashMap<String, Object>();
    	params.put(RequestAddMeDataAction.PARAM_U_ID, ApplicationManager.getInstance().getUserId());
		params.put(RequestAddMeDataAction.PARAM_TIME, time);
		params.put(RequestAddMeDataAction.PARAM_LIMIT, NewsConstant.REQUEST_DATA_LIMIT);
		params.put(RequestAddMeDataAction.PARAM_REFRESH_OR_MORE, isRefresh);
    	ActionController.post(
    			this,
    			RequestAddMeDataAction.class,
    			params,
    			new RequestAddMeDataAction.IAddMeResultInitDataListener() {
					@Override
					public void onFinish(List<HomeData> list, boolean isRefresh) {
						hideProgressBar();
						updateViews(list, isRefresh);
					}
					@Override
					public void onStart() {
					}
					public void onFail(int resourceID) {
						hideProgressBar();
						showToast(AddMePage.this, resourceID);
						updateViews(null, false);
					}
    			},
    			true);
    }
	@Override
	public void onScrollEnd() {
		List<HomeData> 	mDataList = mListView.getData();
		String time = mDataList.get(mDataList.size() - 1).PublicUser.time;
		loadData(Long.parseLong(time), false);
	}

	@Override
	public void onScrollHeader() {
		loadData(NewsConstant.REQUEST_DEFAULT_TIME, true);
	}

}
