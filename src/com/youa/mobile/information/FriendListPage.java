package com.youa.mobile.information;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.data.BaseUserData;
import com.youa.mobile.common.data.UserData;
import com.youa.mobile.common.widget.BaseUserListAdapter;
import com.youa.mobile.information.action.SearchFriendListAction;

public class FriendListPage extends BasePage implements OnClickListener, OnItemClickListener{
	public static final String RESULT_USERNAME = "username";
	public static final String RESULT_USERID = "userid";
	
	private ListView mListView;
	private List<UserData> userDataList = new ArrayList<UserData>();
	private BaseUserListAdapter<List<UserData>> mAdapter;
	private EditText mSearchFriendKeyword;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information_friendlist);
		initView();
		loadData();
	}
	
	private void initView() {
		View leftButton =  findViewById(R.id.title_left);
		mListView =  (ListView)findViewById(R.id.list);
		mProcessView = findViewById(R.id.progressBar);
		mLoadView = mListView;
		mSearchFriendKeyword = (EditText)findViewById(R.id.friendname_keyword);
		mListView.setOnItemClickListener(this);
		leftButton.setOnClickListener(this);
		mAdapter = new BaseUserListAdapter<List<UserData>>(this, R.layout.information_friendlistitem);
		mListView.setAdapter(mAdapter);
		mSearchFriendKeyword.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable keyWord) {
				mAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}
			
			@Override
			public void onTextChanged(CharSequence keyword, int arg1, int arg2,
					int arg3) {
				List<UserData> keywordData = new ArrayList<UserData>(0);
				UserData keywordUserData = null;
				String keywordTrim = keyword.toString().trim();
				if(!"".equals(keyword) && !"".equals(keywordTrim)){
					keywordUserData = new BaseUserData(null,keywordTrim,null,null);
					keywordData.add(keywordUserData);
				}
				for(UserData ud : userDataList){
					if(null == ud){
						break;
					}
					String userName = ud.getUserName();
					if(null != userName && userName.contains(keyword)){
						if(userName.equals(keywordTrim)){
							Collections.replaceAll(keywordData, keywordUserData, ud);//因为要保持索引，在嘴上放显示与keyword相同名称的好友，在此应用该ud替换掉方法开始添加到那个UserData
						}else{
							keywordData.add(ud);
						}
					}
				}
				mAdapter.setData(keywordData);
			}
		});
		showProgressView();
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.title_left:
				finish();
				break;
		}
		
	}
	
	private void updateView(final List<UserData> resultDataList) {
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				userDataList = resultDataList;
				mAdapter.setData(userDataList);
				mAdapter.notifyDataSetChanged();
			}
		});
	}
	
	private void loadData() {
    	ActionController.post(this, SearchFriendListAction.class, null, 
    			new SearchFriendListAction.SearchFriendListListener(){

					@Override
					public void onFinish(List<UserData> dataList) {
						updateView(dataList);
						hiddenProgressView();
					}

					@Override
					public void onStart() {
						
					}

					@Override
					public void onFail(int resourceID) {
						showToast(resourceID);
						hiddenProgressView();
					}
					
    	}, true);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		UserData userData = mAdapter.getDataList().get(position);
		Intent intent = new Intent();
		intent.putExtra(RESULT_USERID, userData.getUserId());
		intent.putExtra(RESULT_USERNAME, userData.getUserName());
		setResult(RESULT_OK, intent);
		finish();
	}
	
}
