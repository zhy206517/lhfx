package com.youa.mobile.life;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePageGroup;
import com.youa.mobile.theme.TopicSubPage;

public class FindLifeMainPageTemp extends BasePageGroup {

	private final static String TAG = "NewsMainPage";
	public final static String KEY_IS_FROM_TOPIC = "topic"; 
	private TabHost 	mHost;
	private Handler 	mHandler = new Handler();;
	private Intent mFindTopicIntent;
	private Intent mSuperPeopleIntent;
	private RadioGroup radioGroup;
	private boolean isFromTopic = false;
	
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.life_main_temp);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			isFromTopic = bundle.getBoolean(KEY_IS_FROM_TOPIC);
		}
		initViews();
		if (isFromTopic) {
			mHandler.post(new Runnable() {
				public void run() {
					mHost.setCurrentTabByTag("find_topic");
				}
			});
		}
	}

	private void initViews() {
		radioGroup =(RadioGroup)findViewById(R.id.radio_group);
		radioGroup.check(isFromTopic ? R.id.life_find_topic : R.id.life_super_people);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.life_super_people:
					mHost.setCurrentTabByTag("super_people");
					break;
				case R.id.life_find_topic:
					mHost.setCurrentTabByTag("find_topic");
					break;
				}
				
			}
		});
		mHandler.post(new Runnable() {
			public void run() {
				initTabs();
			}
		});
	}
	private void initTabs() {
		mHost = (TabHost) findViewById(R.id.tabhost);
		mHost.setup(this.getLocalActivityManager());
		mSuperPeopleIntent 	= new Intent(this, SuperPeoplePage.class);
		mFindTopicIntent = new Intent(this, TopicSubPage.class);
		mHost.addTab(buildTabSpec("super_people", "super_people", mSuperPeopleIntent));
		mHost.addTab(buildTabSpec("find_topic", "find_topic", mFindTopicIntent));
	}


	private TabHost.TabSpec buildTabSpec(String tag, String resLabel, final Intent content) {
		return this.mHost.newTabSpec(tag)
				.setIndicator(resLabel)
				.setContent(content);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Activity tempa = getLocalActivityManager().getCurrentActivity();
		if(tempa.getComponentName().getClassName().endsWith(TopicSubPage.class.getSimpleName())){
			TopicSubPage page = ((TopicSubPage)tempa);
			page.onActivityResult(requestCode, resultCode, data);
		}else if(tempa.getComponentName().getClassName().endsWith(SuperPeoplePage.class.getSimpleName())){
			SuperPeoplePage page = ((SuperPeoplePage)tempa);
			page.onActivityResult(requestCode, resultCode, data);
		}
	}
	
}