package com.youa.mobile.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.login.util.LoginUtil;
import com.youa.mobile.theme.action.TopicAction;
import com.youa.mobile.theme.action.TopicAction.ITopicActionResultListener;
import com.youa.mobile.theme.data.TopicData;

public class SearchTopicPage extends BasePage {
	private final static String TAG = "SearchTopicPage";
	private List<TopicData> 	mTopicChahe = new ArrayList<TopicData>(0);
	private List<TopicData>	mResultList = new ArrayList<TopicData>(0);
	
	private EditText 	mSearchTopicKeyword;
	private ListView 	mTopicListView;
	private TextView 	mTitle;
	private Button 		mButton;
	private ImageButton mImageButton;
	private ProgressBar mProgressBar;
	
	private BaseTopicAdapter mTopicAdapter;
	
	private ITopicActionResultListener resultListener = new TopicAction.ITopicActionResultListener(){

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
		public void onGetAllFinish(List<TopicData> topicList) {
			mTopicChahe = topicList; //数据载入完成后放到缓存mTopicChahe中
			if(null != mTopicChahe && mTopicChahe.size() > 0){
				mResultList.addAll(mTopicChahe);
				mHandler.post(new Runnable(){
					@Override
					public void run() {
							mTopicAdapter.notifyDataSetChanged();
							mProgressBar.setVisibility(View.GONE);
					}
				});
			}
		}

		@Override
		public void onFinish(int resourceID, int position,
				boolean topicSubStatus) {
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_search_topic);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initViews();
		loadData();
	}

	private void initViews() {
		mSearchTopicKeyword = (EditText)findViewById(R.id.topic_keyword);
		mTopicListView 		= (ListView) findViewById(R.id.topic_list);
		mTitle 				= (TextView) findViewById(R.id.title);
		mButton 			= (Button) findViewById(R.id.send);
		mImageButton 		= (ImageButton) findViewById(R.id.back);
		mProgressBar 		= (ProgressBar)findViewById(R.id.progressBar);
		mTitle.setText(R.string.input_select_topic);
		mImageButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				SearchTopicPage.this.finish();
			}
		});
		mButton.setVisibility(View.GONE);
		mTopicAdapter = new BaseTopicAdapter(SearchTopicPage.this, mResultList);//该adapter的data为mResultList，下文updateView中根据输入关键字改变该mResultList
		mTopicListView.setAdapter(mTopicAdapter);
		mTopicListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				goBackWithData(mResultList.get(position));
			}
		});
		mSearchTopicKeyword.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable keyWord) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}
			
			@Override
			public void onTextChanged(CharSequence keyword, int arg1, int arg2,
					int arg3) {
				LoginUtil.LOGD(TAG, "the search keyword is < "+keyword+" >");
				updateView(keyword.toString());
			}
		});
	}
	
	private void loadData() {
		Map<String,Object>	params = new HashMap<String, Object>();
    	params.put(TopicAction.ACTION_TYPE, TopicAction.ACTION_TYPE_GETALL);
    	params.put(TopicAction.TOPIC_TYPE, TopicAction.TOPIC_UI_COMMEND);
    	ActionController.post(this, TopicAction.class, params, 
    			resultListener, true);
	}
	
	private void updateView(final String keyword){
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				if(null != mResultList){
					mResultList.clear();
				}
				//根据当前输入的关键字，让listView显示相关的item。下文中的mTopicChahe为加载的所有热门话题。
				if(!"".equals(keyword) && !"".equals(keyword.trim())){
					
					//首先将输入的关键字作为一个话题添加到adapter的data中。
					TopicData info = new TopicData();
					info.name = keyword.trim();
					info.sUid = "";
					mResultList.add(info);
					//根据关键字从话题缓存mTopicChahe中检索与输入关键字相关的话题，并添加到adapter的data中。
					String topicName;
					if(null != mTopicChahe && mTopicChahe.size() > 0){
						for(TopicData topic : mTopicChahe){
							topicName = topic.name;
							//话题名称不为空（及""）,话题名称包含输入关键字，话题名称不等于关键字，因为与关键字相等的话题在该该方法一开始就添加进来了。
							//因为此处仅用到话题名，而不是话题对象PopThemeInfo，故仅在此不添加同名话题，否则应该将方法一开始添加进来的同名话题对象进行替换，
							//在好友搜索页面用的是对象替换，如需修改，请参考@FriendListPage -> initView -> onTextChanged。
							if(null != topicName && !"".equals(topicName) && topicName.contains(keyword) && !topicName.equals(keyword)){
								mResultList.add(topic);
							}
						}
					}
				}else{
					mResultList.addAll(mTopicChahe);
				}
				mTopicAdapter.notifyDataSetChanged();
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mTopicListView.setSelection(0);
					}
				});
			}
		});
	}
	
	private void goBackWithData(TopicData topic) {
		Intent data = new Intent();
		data.putExtra(BaseInputPage.RESPONSE_TOPIC, topic.name);
		InputUtil.LOGD(TAG, "topic item onselected return topicName to PublicshPage: " + topic.name);
		setResult(RESULT_OK, data);
		finish();
	}
}
