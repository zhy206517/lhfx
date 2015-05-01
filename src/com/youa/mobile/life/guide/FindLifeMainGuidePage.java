package com.youa.mobile.life.guide;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.youa.mobile.ExitPage;
import com.youa.mobile.LehoTabActivity;
import com.youa.mobile.MainActivity;
import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePageGroup;

public class FindLifeMainGuidePage extends BasePageGroup implements OnClickListener{

	private final static String TAG = "FindLifeMainGuidePage";
	public final static String KEY_IS_FROM_TOPIC = "topic";
	public final static String KEY_IS_FROM_REGIST = "regist"; 
	public final static String KEY_IS_FROM = "key_from";
	private TabHost 	mHost;
	private Handler 	mHandler = new Handler();;
	private Intent mFindTopicIntent;
	private Intent mSuperPeopleIntent;
	private RadioGroup radioGroup;
	private String isFrom;
	private TextView title;
	private View titleView;
	private Button prevStep;
	private Button nextStep;
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.life_main_temp);
		
		initViews();
		if (isFrom != null && isFrom.equals(KEY_IS_FROM_TOPIC)) {
			mHandler.post(new Runnable() {
				public void run() {
					mHost.setCurrentTabByTag("find_topic");
				}
			});
		}else if(isFrom != null && isFrom.equals(KEY_IS_FROM_REGIST)){
			mHandler.post(new Runnable() {
				public void run() {
					Toast.makeText(FindLifeMainGuidePage.this, "from regist!", Toast.LENGTH_LONG).show();
				}
			});
		}else{
			isFrom = null;
		}
	}

	private void initViews() {
		nextStep = (Button)findViewById(R.id.next_step);
		prevStep = (Button)findViewById(R.id.back);
		prevStep.setVisibility(View.GONE);
		title = (TextView)findViewById(R.id.title);
		titleView = findViewById(R.id.life_title_layout);
		title.setText(R.string.life_title_people);
		nextStep.setOnClickListener(this);
		prevStep.setOnClickListener(this);
		radioGroup =(RadioGroup)findViewById(R.id.radio_group);
		radioGroup.check((isFrom != null && isFrom.equals(KEY_IS_FROM_TOPIC)) ? R.id.life_find_topic : R.id.life_super_people);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.life_super_people:
					title.setText(R.string.life_title_people);
					mHost.setCurrentTabByTag("super_people");
					break;
				case R.id.life_find_topic:
					title.setText(R.string.life_title_topic);
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
		mSuperPeopleIntent 	= new Intent(this, SuperPeopleGuidePage.class);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			isFrom = bundle.getString(KEY_IS_FROM);//KEY_IS_FROM_REGIST
		}
		
		mFindTopicIntent = new Intent(this, TopicSubGuidePage.class);
		if(isFrom != null && isFrom.equals(KEY_IS_FROM_REGIST)){
			radioGroup.setVisibility(View.GONE);
			titleView.setVisibility(View.VISIBLE);
			
			mFindTopicIntent.putExtra(TopicSubGuidePage.KEY_ITEM_CAN_CLICK, false);
			mSuperPeopleIntent.putExtra(TopicSubGuidePage.KEY_ITEM_CAN_CLICK, false);
		}
		
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			radioGroup.check(R.id.life_super_people);
			break;
		case R.id.next_step:
			if(getLocalActivityManager().getCurrentActivity().getComponentName().getClassName().endsWith(TopicSubGuidePage.class.getSimpleName())){
				nextStep.setVisibility(View.GONE);
				prevStep.setVisibility(View.GONE);
				startActivity(new Intent(FindLifeMainGuidePage.this, LehoTabActivity.class));
				finish();
			}else{
				radioGroup.check(R.id.life_find_topic);
				nextStep.setText(R.string.common_complate);
				prevStep.setVisibility(View.VISIBLE);
			}
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, 0,
				getResources().getString(R.string.feed_back_home));
		menu.add(Menu.NONE, 1, 0,
				getResources().getString(R.string.feed_exit));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(this, LehoTabActivity.class);
			startActivity(intent);
			break;
		case 1:
			intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(this, ExitPage.class);
			startActivity(intent);
			break;
		}
		return true;
	}
}