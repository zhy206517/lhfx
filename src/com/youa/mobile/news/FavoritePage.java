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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.youa.mobile.R;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.news.action.RequestAddMeDataAction;
import com.youa.mobile.news.action.RequestFavoriteDataAction;
import com.youa.mobile.news.data.FavoriteData;
import com.youa.mobile.news.util.NewsConstant;
import com.youa.mobile.news.util.NewsUtil;

public class FavoritePage extends BaseNewsPage implements BaseListView.OnScrollEndListener {

	private final static String TAG = "FavoritePage";

	private FavoriteListView 	mFavoriteListView;
	private TextView 			mNullText;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_favorite);
        init();
        loadData("-1", true);
    }

    private void init() {
    	ListView listView = (ListView) findViewById(R.id.list);
    	mNullText = (TextView) findViewById(R.id.text_null);
//    	listView.setOnItemClickListener(onItemClickListener);
    	mFavoriteListView = new FavoriteListView(listView, mHeaderView, mFooterView);
    	mFavoriteListView.setOnScrollEndListener(this);
    	mFirstInProgressBar = (ProgressBar) findViewById(R.id.progressBar);
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
				mFavoriteListView.refresh();
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

    OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			List<FavoriteData> 	mDataList = mFavoriteListView.getData();
			if (mHeaderView != null
					&& position > 0
					&& position <= mDataList.size()) {
				FavoriteData data = mDataList.get(position - 1);
				startFavoriteDialog(data);
			}
		}
    };

    private void startFavoriteDialog(FavoriteData data) {

    	Intent intent = new Intent(this, FavoriteDialogPage.class);
    	intent.putExtra(FavoriteDialogPage.FEED_ID, data.sourceFeedId);
    	startActivity(intent);
    }

    private void updateViews(final List<FavoriteData> list, final boolean isRefresh){
    	
		mHandler.post(new Runnable(){
			public void run() {
				mFavoriteListView.closeHeaderFooter();
				if (!NewsUtil.isEmpty(list)) {
    				if(isRefresh) {
    					mFavoriteListView.setData(list, NewsConstant.REQUEST_DATA_LIMIT);
    				} else {
    					mFavoriteListView.addData(list, NewsConstant.REQUEST_DATA_LIMIT);
    				}
				} else {
					mFavoriteListView.hiddenFooter();
				}
				mNullText.setVisibility(NewsUtil.isEmpty(mFavoriteListView.getData()) ? View.VISIBLE : View.GONE);
			}
		});
    }

    private void loadData(String postId, boolean isRefresh) {

    	NewsUtil.LOGD(TAG, "enter loadData  <isRefresh> : " + isRefresh);
    	Map<String,Object>	params = new HashMap<String, Object>();
    	params.put(RequestFavoriteDataAction.PARAM_USERNAME, ApplicationManager.getInstance().getUserId());
		params.put(RequestFavoriteDataAction.PARAM_POSTID, postId);
		params.put(RequestFavoriteDataAction.PARAM_LIMIT, NewsConstant.REQUEST_DATA_LIMIT);
		params.put(RequestAddMeDataAction.PARAM_REFRESH_OR_MORE, isRefresh);
    	ActionController.post(
    			this,
    			RequestFavoriteDataAction.class,
    			params,
    			new RequestFavoriteDataAction.IFavoritResultListener() {
					@Override
					public void onFinish(List<FavoriteData> list, boolean isRefresh) {
						hideProgressBar();
						NewsUtil.LOGD(TAG, "enter onFinish  <list> : " + list.size());
						NewsUtil.LOGD(TAG, "enter onFinish  <isRefresh> : " + isRefresh);
						updateViews(list, isRefresh);
					}
					@Override
					public void onStart() {
					}
					public void onFail(int resourceID) {
						hideProgressBar();
						showToast(FavoritePage.this, resourceID);
						updateViews(null, false);
					}
    			},
    			true);
    }

	@Override
	public void onScrollEnd() {
		List<FavoriteData> 	mDataList = mFavoriteListView.getData();
		String postId = mDataList.get(mDataList.size() - 1).postId;
		loadData(postId, false);
	}

	@Override
	public void onScrollHeader() {
		loadData("-1", true);
	}

}
