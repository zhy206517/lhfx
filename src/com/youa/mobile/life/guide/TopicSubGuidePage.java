package com.youa.mobile.life.guide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.LehuoIntent;
import com.youa.mobile.PreparePage;
import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.friend.FriendFeedActivity;
import com.youa.mobile.theme.TopicSubItemAdapter;
import com.youa.mobile.theme.action.TopicAction;
import com.youa.mobile.theme.action.TopicAction.ITopicActionResultListener;
import com.youa.mobile.theme.data.TopicData;

public class TopicSubGuidePage extends BasePage implements OnClickListener,  OnItemClickListener{
	private static final String TAG = "TopicSubGuidePage";
	public static final String INTENT_FROM_KEY = "intent_from";
	public static final String INTENT_FROM_FEED = "intent_from_feed";
	public static final String UPDATE_TOPIC_POSITION = "topic_position";
	public static final String UPDATE_TOPIC_STATUS = "topic_status";
	public static final String KEY_ITEM_CAN_CLICK = "item_can_click";
	private List<TopicData> mTopicList = new ArrayList<TopicData>(0);
	private ImageButton mGoBackBtn;
	private Button mSendBtn;
	private TextView mTitle;
	private ListView mTopicListView;
	private TopicSubItemAdapter mListViewAdapter;
	private ProgressBar mProgressBar;
	private static final int REQUEST_SHOW_TOPIC = 100;
	private BroadcastReceiver deleteTopicReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
//			if(LehuoIntent.ACTION_USER_TOPIC_DELETE.equals(intent.getAction())){
//				String topicId = intent.getStringExtra(FriendFeedActivity.THEME_DELETE_ID);
//				if(topicId!=null && !"".equals(topicId)){
//					for(TopicData topic : mTopicList){
//						if(topic.sUid.equals(topicId)){
//							topic.isSubscribe = false;
//						}
//					}
//					mHandler.post(new Runnable() {
//						@Override
//						public void run() {
//							mListViewAdapter.notifyDataSetChanged();
//							//updataView(mTopicList);
//						}
//					});
//				}
//			}
		}
	};
	
	private ITopicActionResultListener resultListener = new TopicAction.ITopicActionResultListener(){
		@Override
		public void onGetAllFinish(List<TopicData> data) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mProgressBar.setVisibility(View.GONE);
				}
			});
			if(null == data){
				showToast(R.string.topic_load_error);
			}else{
				updataView(data);
			}
		}

		@Override
		public void onStart(Integer resourceID) {
			if(null != resourceID)
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mProgressBar.setVisibility(View.VISIBLE);
					}
				});
		}

		@Override
		public void onFail(final int resourceID) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mProgressBar.setVisibility(View.GONE);
					showToast(resourceID);
				}
			});
		}

		@Override
		public void onFinish(final int resourceID, final int position, final boolean topicStatus) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mProgressBar.setVisibility(View.GONE);
					showToast(resourceID);
					updataBtn(position, topicStatus);
				}
			});
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topic_add);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    	initView();
    	Map<String,Object>	params = new HashMap<String, Object>();
    	params.put(TopicAction.ACTION_TYPE, TopicAction.ACTION_TYPE_GETALL);
    	params.put(TopicAction.TOPIC_TYPE, TopicAction.TOPIC_UI_COMMEND);
		loadData(params);
		registerReceiver(deleteTopicReceiver, new IntentFilter(LehuoIntent.ACTION_USER_TOPIC_DELETE));
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(deleteTopicReceiver);
		super.onDestroy();
	}
	
	private void initView(){
		Intent i = getIntent();
		if(null != i && null != i.getStringExtra(INTENT_FROM_KEY) && i.getStringExtra(INTENT_FROM_KEY).equals(INTENT_FROM_FEED)){
			mSendBtn = (Button) findViewById(R.id.send);
			mTitle = (TextView)findViewById(R.id.title);
			mGoBackBtn = (ImageButton) findViewById(R.id.back);
			mSendBtn.setVisibility(View.GONE);
			mTitle.setText(R.string.hot_topic);
			mGoBackBtn.setOnClickListener(this);
		}else{
			findViewById(R.id.title_layout).setVisibility(View.GONE);
		}

		
		mTopicListView = (ListView) findViewById(R.id.topic_list);
		mTopicListView.setOnItemClickListener(this);
		mListViewAdapter = new TopicSubItemAdapter(this, mTopicList);
		mTopicListView.setAdapter(mListViewAdapter);
		mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
	}
	private void loadData(Map<String,Object> params) {
    	ActionController.post(this, TopicAction.class, params,resultListener, true);
	}
	private void updataView(final List<TopicData> data){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mTopicList.clear();
				mTopicList.addAll(data);
				System.out.println(data.size());
				mListViewAdapter.notifyDataSetChanged();
			}
		});
	}

	private void updataBtn(int position, boolean topicStatus){
		TopicData  topic = mTopicList.get(position);
		topic.isSubscribe = topicStatus;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mListViewAdapter.notifyDataSetChanged();
				sendBroadcast(new Intent(LehuoIntent.ACTION_USER_TOPIC_CHANGE));
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.sub_unsub_topic_btn:
				int position = (Integer)v.getTag();
				TopicData  topic = mTopicList.get(position);
				Log.d(TAG, "click button of topic.SUID = " + topic.sUid);
				String actionType;
				if(topic.isSubscribe){
					actionType = TopicAction.ACTION_TYPE_UNSUB;
				}else{
					actionType = TopicAction.ACTION_TYPE_SUB;
				}
				Map<String,Object>	params = new HashMap<String, Object>();
				params.put(TopicAction.TOPIC_ID, topic.sUid);
				params.put(TopicAction.ACTION_TYPE, actionType);
				params.put(TopicAction.TOPIC_ITEM_POSITION_KEY, position);
		    	loadData(params);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
//		if(getIntent().getBooleanExtra(KEY_ITEM_CAN_CLICK, true)){
//			Intent i = new Intent();
//			i.setClass(TopicSubGuidePage.this, TopicFeedPage.class);
//			TopicData  topic = mTopicList.get(position);
//			i.putExtra(TopicFeedPage.KEYWORD, topic.name);
//			i.putExtra(TopicFeedPage.TOPIC_ID, topic.sUid);
//			i.putExtra(TopicFeedPage.TOPIC_STATUS, topic.isSubscribe);
//			i.putExtra(UPDATE_TOPIC_POSITION, position);
//			//startActivity(i);
//			if(getParent() == null){
//				startActivityForResult(i, REQUEST_SHOW_TOPIC);
//			} else {
//				getParent().startActivityForResult(i, REQUEST_SHOW_TOPIC);
//			}
//		}
//		/*getParent().*///startActivityForResult(i, REQUEST_SHOW_TOPIC);
	}
	
	//@Override
//	public void onActivityResult(int requestCode, int resultCode, final Intent data) {
//		switch(requestCode) {
//			case REQUEST_SHOW_TOPIC:
//				if(resultCode == RESULT_OK) {
//					mHandler.post(new Runnable() {
//						@Override
//						public void run() {
//							int position = data.getIntExtra(UPDATE_TOPIC_POSITION, -1);
//							boolean mTopicStatus = data.getBooleanExtra(UPDATE_TOPIC_STATUS, mTopicList.get(position).isSubscribe);
//							updataBtn(position, mTopicStatus);
//						}
//					});
//				}
//				break;
//		}
//	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			goHome();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	public void goHome() {
		Intent intent = new Intent();
		intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(this, PreparePage.class);
		startActivity(intent);
	}
}
