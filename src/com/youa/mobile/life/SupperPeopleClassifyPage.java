package com.youa.mobile.life;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.life.action.SuperPeopleAction;
import com.youa.mobile.life.action.SuperPeopleAction.RequestType;
import com.youa.mobile.life.data.SuperPeopleClassify;

public class SupperPeopleClassifyPage extends BasePage {
	private List<SuperPeopleClassify> mSupperPeopleClassify = new ArrayList<SuperPeopleClassify>(0);
	
	private ListView 	mListView;
	private TextView 	mTitle;
//	private ImageButton mImageButton;
	private ProgressBar mProgressBar;
	private Button mSend;
	private LifeSearchResultAdapter<SuperPeopleClassify> mClassifyAdapter;
	
	private OnClickListener onClickListenter = new OnClickListener(){
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.life_share_classify);
		initViews();
		loadData();
	}

	private void initViews() {
		mListView 			= (ListView) findViewById(R.id.shareify_list);
		mTitle 				= (TextView) findViewById(R.id.title);
//		mImageButton 		= (ImageButton) findViewById(R.id.back);
		mSend               = (Button) findViewById(R.id.send);
		mSend.setVisibility(View.GONE);
		mProgressBar 		= (ProgressBar)findViewById(R.id.progressBar);
		mTitle.setText(R.string.daren_hall);
//		mImageButton.setOnClickListener(onClickListenter);
		mClassifyAdapter = new LifeSearchResultAdapter<SuperPeopleClassify>(this, R.layout.life_search_user, R.id.name, mSupperPeopleClassify, onClickListenter);
		mListView.setAdapter(mClassifyAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object item = view.getTag();
				if(item instanceof SuperPeopleClassify){
					//String classid = ((SuperPeopleClassify)item).id;
					Intent i = new Intent(SupperPeopleClassifyPage.this, ClassifySuperPeoplePage.class);
					i.putExtra(ClassifySuperPeoplePage.TITLE, ((SuperPeopleClassify)item).name);
					//i.putExtra(LoadFeedAction.SHARECLASSIFY_ID, classid);
					startActivity(i);
				}
			}
		});
	}

	private void loadData() {
		Map<String,Object>	params = new HashMap<String, Object>();
    	params.put(SuperPeopleAction.REQUEST_TYPE, RequestType.GET_CLASS);
    	ActionController.post(this, SuperPeopleAction.class, params, 
    			new SuperPeopleAction.SuperPeopleResultListener() {
					
					@Override
					public void onStart() {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mProgressBar.setVisibility(View.VISIBLE);
							}
						});
					}
					
					@Override
					public void onFinish(List<SuperPeopleClassify> data) {
						mSupperPeopleClassify.clear();
						if(data!=null){
							mSupperPeopleClassify.addAll(data);
						}
						updateView();
					}
					
					@Override
					public void onFail(final int resourceID) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								showToast(SupperPeopleClassifyPage.this, resourceID);
								mProgressBar.setVisibility(View.GONE);
							}
						});
					}
				}, true);
	}
	
	private void updateView(){
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mClassifyAdapter.notifyDataSetChanged();
						mProgressBar.setVisibility(View.GONE);
					}
				});
			}
		});
	}
}
