package com.youa.mobile.news;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.youa.mobile.R;
import com.youa.mobile.R.color;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.UpdateService;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePageGroup;
import com.youa.mobile.information.widget.InformationRadioGroup;
import com.youa.mobile.more.MoreUtil;
import com.youa.mobile.news.action.RequestSetNewNewsCountAction;
import com.youa.mobile.news.util.NewsUtil;

public class NewsMainPage extends BasePageGroup implements
		NewsTitleRadioGroup.OnCheckedChangeListener {

	private final static String TAG = "NewsMainPage";
	private TabHost mHost;
	private Handler mHandler;
	private TextView mAddCount;
	private TextView mSayCount;
	private TextView mFavoriteCount;
	private RadioButton mAddCountLable;
	private RadioButton mSayCountLable;
	private RadioButton mFavoriteCountLable;
	private ImageButton mBack;
	private Intent mFavoriteIntent;
	private Intent mAddMeIntent;
	private Intent mSayMeIntent;
	private NewsTitleRadioGroup radioGroup;
	private int beLikeNum;
	private int commentNum;
	private int addMeNum;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.news_main);
		// 设置TabHost
		getNumFromIntent();
		initViews();
		mHandler = new Handler();
		mHandler.post(new Runnable() {
			public void run() {
				initTabs();
				mAddCount.setVisibility(View.INVISIBLE);
				updateNewNewsCountXML(0);
			}
		});
	}

	private void getNumFromIntent() {
		Intent i = this.getIntent();
		beLikeNum = i.getIntExtra("belikeNum", 0);
		commentNum = i.getIntExtra("commentNum", 0);
		addMeNum = i.getIntExtra("addMeNum", 0);
	}

	private void initViews() {
		mAddCountLable = (RadioButton) findViewById(R.id.add_me);
		AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(11, true);
		ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.argb(255, 73, 68, 66));
		String addMeNumStr = getString(R.string.news_add_me) + addMeNum;
		SpannableStringBuilder addMeNumStyle = new SpannableStringBuilder(
				addMeNumStr);
		addMeNumStyle.setSpan(colorSpan, 0, 2,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		addMeNumStyle.setSpan(sizeSpan, 2, addMeNumStr.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mAddCountLable.append(addMeNumStyle);
		mSayCountLable = (RadioButton) findViewById(R.id.say_me);
		String commentNumStr = getString(R.string.news_say_me) + commentNum;
		SpannableStringBuilder commentNumStyle = new SpannableStringBuilder(
				commentNumStr);
		commentNumStyle.setSpan(colorSpan, 0, 2,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		commentNumStyle.setSpan(sizeSpan, 2, commentNumStr.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mSayCountLable.append(commentNumStyle);
		mFavoriteCountLable = (RadioButton) findViewById(R.id.my_favorite);
		String beLikeNumStr = getString(R.string.news_my_favorite) + beLikeNum;
		SpannableStringBuilder beLikeNumStyle = new SpannableStringBuilder(
				beLikeNumStr);
		beLikeNumStyle.setSpan(colorSpan, 0, 3,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		beLikeNumStyle.setSpan(sizeSpan, 3, beLikeNumStr.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mFavoriteCountLable.append(beLikeNumStyle);
		mAddCount = (TextView) findViewById(R.id.add_me_count);
		mSayCount = (TextView) findViewById(R.id.say_me_count);
		mFavoriteCount = (TextView) findViewById(R.id.favorite_count);

		mBack = (ImageButton) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NewsMainPage.this.finish();
			}
		});
		mHost = (TabHost) findViewById(R.id.tabhost);
		radioGroup = (NewsTitleRadioGroup) findViewById(R.id.tab_title);
		radioGroup.check(R.id.add_me);
		radioGroup.setOnCheckedChangeListener(this);
	}

	private void initTabs() {
		mHost.setup(this.getLocalActivityManager());
		mAddMeIntent = new Intent(this, AddMePage.class);
		mSayMeIntent = new Intent(this, SayMePage.class);
		mFavoriteIntent = new Intent(this, FavoritePage.class);
		mHost.addTab(buildTabSpec("addme", "addme", mAddMeIntent));
		mHost.addTab(buildTabSpec("sayme", "sayme", mSayMeIntent));
		mHost.addTab(buildTabSpec("favorite", "favorite", mFavoriteIntent));
	}

	private TabHost.TabSpec buildTabSpec(String tag, String resLabel,
			final Intent content) {
		return mHost.newTabSpec(tag).setIndicator(resLabel).setContent(content);
	}

	private void updateNewNewsCountXML(int index) {
		int list[] = NewsUtil.readNewCountFromPref(this);
		if (list[index] != 0) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(RequestSetNewNewsCountAction.TYPE, index);
			params.put(RequestSetNewNewsCountAction.COUNT, 0);
			ActionController
					.post(this,
							RequestSetNewNewsCountAction.class,
							params,
							new RequestSetNewNewsCountAction.ISsetNewNewsCountResultListener() {
								public void onFinish() {
									NewsUtil.LOGD(TAG,
											"+++++++++ set new news count succ +++++++++");
								}

								public void onFail(int resourceID) {
									NewsUtil.LOGD(TAG,
											"+++++++++ set new news count fail +++++++++");
								}
							}, true);
			list[index] = 0;
			NewsUtil.writeNewCountToPref(this, list);
			sendNewNewsCountChangeBro();
		}
	}

	private void checkIfCountViewNeedShow() {
		boolean isSayMeShow = MoreUtil.readIsShowSayMeFromPref(this);
		boolean isAddMeShow = MoreUtil.readIsShowAddMeFromPref(this);
		boolean isFavShow = MoreUtil.readIsShowFavFromPref(this);
		int list[] = NewsUtil.readNewCountFromPref(this);
		if (list[0] != 0 && isAddMeShow) {
			mAddCount.setText(list[0] > 99 ? "99+" : list[0] + "");
			mAddCount.setVisibility(View.VISIBLE);
		}
		if (list[1] != 0 && isSayMeShow) {
			mSayCount.setText(list[1] > 99 ? "99+" : list[1] + "");
			mSayCount.setVisibility(View.VISIBLE);
		}
		if (list[2] != 0 && isFavShow) {
			mFavoriteCount.setText(list[2] > 99 ? "99+" : list[2] + "");
			mFavoriteCount.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkIfCountViewNeedShow();
	}

	private void sendNewNewsCountChangeBro() {
		Intent intent = new Intent(UpdateService.INTENT_NEW_NEWS_COUNT_CHANGE);
		sendBroadcast(intent);
	}

	@Override
	public void onCheckedChanged(NewsTitleRadioGroup informationRadioGroup,
			int checkedId) {
		switch (checkedId) {
		case R.id.add_me:
			mHost.setCurrentTabByTag("addme");
			break;
		case R.id.say_me:
			mSayCount.setVisibility(View.INVISIBLE);
			updateNewNewsCountXML(1);
			mHost.setCurrentTabByTag("sayme");
			break;
		case R.id.my_favorite:
			mFavoriteCount.setVisibility(View.INVISIBLE);
			updateNewNewsCountXML(2);
			mHost.setCurrentTabByTag("favorite");
			break;
		}
	}

}