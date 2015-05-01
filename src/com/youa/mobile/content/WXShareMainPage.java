package com.youa.mobile.content;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;

import com.youa.mobile.PreparePage;
import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePageGroup;
import com.youa.mobile.news.NewsTitleRadioGroup;
import com.youa.mobile.utils.LogUtil;

public class WXShareMainPage extends BasePageGroup implements NewsTitleRadioGroup.OnCheckedChangeListener {

	private static final String HISTORY = "history";
	private static final String MYSHARE = "myshare";
	private static final String MYPREFER = "myperfer";
	private TabHost mHost;
	private Intent mPreferIntent;
	private Intent mShareIntent;
	private Intent mHistoryIntent;
	private NewsTitleRadioGroup radioGroup;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// if start from weixin web page, to start prepare page.
		Intent i = getIntent();
		if (i != null && i.getData() != null) {
			LogUtil.d(TAG, "onReceive. action = " + i.getAction() + ", intent = " + i.toString());
			if ("startlh".equals(i.getData().getScheme()) && Intent.ACTION_VIEW.equals(i.getAction())) {
				startActivity(new Intent(this, PreparePage.class));
				finish();
			}
		}

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wenxin_share_main);
		initViews();
		initTabs();
	}

	private void initViews() {
		mHost = (TabHost) findViewById(R.id.tabhost);
		radioGroup = (NewsTitleRadioGroup) findViewById(R.id.tab_title);
		radioGroup.check(R.id.my_enjoy);
		radioGroup.setOnCheckedChangeListener(this);
	}

	private void initTabs() {
		mHost.setup(this.getLocalActivityManager());
		mPreferIntent = new Intent(this, MyPreferPage.class);
		mShareIntent = new Intent(this, MySharePage.class);
		mHistoryIntent = new Intent(this, HistoryPage.class);
		mHost.addTab(buildTabSpec(MYPREFER, MYPREFER, mPreferIntent));
		mHost.addTab(buildTabSpec(MYSHARE, MYSHARE, mShareIntent));
		mHost.addTab(buildTabSpec(HISTORY, HISTORY, mHistoryIntent));
	}

	private TabHost.TabSpec buildTabSpec(String tag, String resLabel, final Intent content) {
		return mHost.newTabSpec(tag).setIndicator(resLabel).setContent(content);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onCheckedChanged(NewsTitleRadioGroup informationRadioGroup, int checkedId) {
		switch (checkedId) {
		case R.id.my_enjoy:
			mHost.setCurrentTabByTag(MYPREFER);
			break;
		case R.id.my_share:
			mHost.setCurrentTabByTag(MYSHARE);
			break;
		case R.id.rec_viewed:
			mHost.setCurrentTabByTag(HISTORY);
			break;
		}
	}

	public void toBack(View v) {
		finish();
	}

	public void toSearch(View v) {
		startActivity(new Intent(this, FeedSearchActivity.class));
	}
}