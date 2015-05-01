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
import android.widget.Toast;

import com.youa.mobile.R;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.input.CommentPage;
import com.youa.mobile.news.action.RequestSayMeDataAction;
import com.youa.mobile.news.data.SayMeData;
import com.youa.mobile.news.util.NewsConstant;
import com.youa.mobile.news.util.NewsUtil;

public class SayMePage extends BaseNewsPage implements BaseListView.OnScrollEndListener {

	private static final String TAG = "SayMePage";

	private SayMeListView 	mSayMeListView;
	private TextView 		mNullText;
//	private List<SayMeData> mDataList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_say_me);
        init();
        loadData(NewsConstant.REQUEST_DEFAULT_TIME, true);
    }

    private void init() {
    	mNullText = (TextView) findViewById(R.id.text_null);
    	ListView listView = (ListView) findViewById(R.id.list);
    	listView.setOnItemClickListener(onItemClickListener);
    	mSayMeListView = new SayMeListView(listView, mHeaderView, mFooterView);
    	mSayMeListView.setOnScrollEndListener(this);
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
				mSayMeListView.refresh();
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
			List<SayMeData> mDataList = mSayMeListView.getData();
			if (mHeaderView != null
					&& position > 0
					&& position <= mDataList.size()) {
				SayMeData data = (SayMeData) mDataList.get(position - 1);
				if (data != null) {
					if(TextUtils.isEmpty(data.getSourceContent())&&TextUtils.isEmpty(data.sourceImage)){
						Toast.makeText(SayMePage.this, SayMePage.this.getResources().getString(R.string.feed_deleted), 3).show();
						return;
					}
					startCommentDialog(
							"1".equals(data.getReplyOrCommentFlag()),
							data.getSourceFeedId(),
							data.getOrgId(),
							data.getCmtId(),
							data.getPublishUserName());
				}
			}
		}
    };

    private void startCommentDialog(boolean isComment, String postId, String orgId, String cmtId, String replyName) {
    	Intent intent = new Intent(this, CommentDialogPage.class);
    	intent.putExtra(CommentPage.KEY_SOURCE, postId);
    	intent.putExtra(CommentPage.KEY_ORG_ID, orgId);
    	intent.putExtra(CommentPage.KEY_CMT_ID, cmtId);
    	intent.putExtra(CommentPage.KEY_TYPE, isComment);
    	intent.putExtra(CommentPage.KEY_REPLY_NAME, replyName);
    	startActivity(intent);
    }

    private void updateViews(final List<SayMeData> list, final boolean isRefresh){
    	
    		mHandler.post(new Runnable(){
    			public void run() {
    				mSayMeListView.closeHeaderFooter();
    				if (!NewsUtil.isEmpty(list)) {
	    				if(isRefresh) {
	    					mSayMeListView.setData(list, NewsConstant.REQUEST_DATA_LIMIT);
	    				} else {
	    					mSayMeListView.addData(list, NewsConstant.REQUEST_DATA_LIMIT);
	    				}
    				} else {
    					mSayMeListView.hiddenFooter();
    				}
					mNullText.setVisibility(NewsUtil.isEmpty(mSayMeListView.getData()) ? View.VISIBLE : View.GONE);
    			}
    		});
    }

    private void loadData(long time, boolean isRefresh) {

    	NewsUtil.LOGD(TAG, "enter loadData  <isRefresh> : " + isRefresh);
    	Map<String,Object>	params = new HashMap<String, Object>();
    	params.put(RequestSayMeDataAction.PARAM_USERNAME, ApplicationManager.getInstance().getUserId());
		params.put(RequestSayMeDataAction.PARAM_TIME, time);
		params.put(RequestSayMeDataAction.PARAM_LIMIT, NewsConstant.REQUEST_DATA_LIMIT);
		params.put(RequestSayMeDataAction.PARAM_REFRESH_OR_MORE, isRefresh);
    	ActionController.post(
    			this,
    			RequestSayMeDataAction.class,
    			params,
    			new RequestSayMeDataAction.ISayMeResultListener() {
					@Override
					public void onFinish(List<SayMeData> list, boolean isRefresh) {
						hideProgressBar();
						updateViews(list, isRefresh);
					}
					@Override
					public void onStart() {
					}
					public void onFail(int resourceID) {
						hideProgressBar();
						showToast(SayMePage.this, resourceID);
						updateViews(null, false);
					}
    			},
    			true);
    }

	@Override
	public void onScrollEnd() {
		List<SayMeData> mDataList = mSayMeListView.getData();
		String time = mDataList.get(mDataList.size() - 1).getPublishTime();
		loadData(Long.parseLong(time), false);
	}

	@Override
	public void onScrollHeader() {
		loadData(NewsConstant.REQUEST_DEFAULT_TIME, true);
	}
}
