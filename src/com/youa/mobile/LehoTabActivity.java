package com.youa.mobile;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.youa.mobile.circum.CircumFeedActivity;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.util.UpgradeUtil;
import com.youa.mobile.friend.FriendFeedActivity;
import com.youa.mobile.information.MySelfPersonnalInforPage;
import com.youa.mobile.input.PublishPage;
import com.youa.mobile.jingxuan.JingXuanPage;
import com.youa.mobile.login.LoginPage;
import com.youa.mobile.login.action.CheckLoginAction;
import com.youa.mobile.login.util.LoginUtil;
import com.youa.mobile.more.MoreUtil;
import com.youa.mobile.news.NewsTitleRadioGroup;
import com.youa.mobile.news.util.NewsUtil;

public class LehoTabActivity extends TabActivity implements
		OnTabChangeListener, NewsTitleRadioGroup.OnCheckedChangeListener {
	public static String TAG = "LehoTabActivity";
	public static final int REQUEST_CODE_LOGIN = 0;
	public static final int REQUEST_CODE_CLIENT_QUIT = 100;
	public static final String TAG_FIND = "piazza";
	public static final String TAG_FIND_THEME = "theme";
	public static final String INIT_TAG = "init_tag";
	public static final String INIT_FIND_TAG = "init_find_tag";
	private TabHost mHost;
	private Handler mHandler = new Handler();
	private NewNewsCountReceiver mNewNewsCountReceiver;
	private TextView mCountView;
	private NewsTitleRadioGroup radioGroup;

	private RadioButton mRadioButton0;
	private RadioButton mRadioButton1;
	private RadioButton mRadioButton2;
	private RadioButton mRadioButton3;
	private RadioButton mRadioButton4;

	private int mPreviousTabId;
	private int mCurrentTabId;
	private int mPreviousCheckId;
	private ScreenOnOrOffReceiver mScreenOnOrOffReceiver;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leho_tab_layout);
		radioGroup = (NewsTitleRadioGroup) findViewById(R.id.main_radio);
		mRadioButton0 = (RadioButton) findViewById(R.id.radio_button0);
		mRadioButton1 = (RadioButton) findViewById(R.id.radio_button1);
		mRadioButton2 = (RadioButton) findViewById(R.id.radio_button2);
		mRadioButton3 = (RadioButton) findViewById(R.id.radio_button3);
		mRadioButton4 = (RadioButton) findViewById(R.id.radio_button4);
		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() != R.id.radio_button2) {
					mPreviousTabId = mCurrentTabId;
				}
				switch (v.getId()) {
				case R.id.radio_button0:
					mCurrentTabId = R.id.radio_button0;
					if (mPreviousTabId == R.id.radio_button0) {
						Intent intent = new Intent(
								LehuoIntent.JINGXUAN_FEED_UPDATE);
						sendBroadcast(intent);
					}
					break;
				case R.id.radio_button1:
					mCurrentTabId = R.id.radio_button1;
					if (mPreviousTabId == R.id.radio_button1) {
						Intent intent = new Intent(
								LehuoIntent.CIRCUM_FEED_UPDATE);
						sendBroadcast(intent);
					}
					break;
				case R.id.radio_button2:
					// mCurrentTabId = R.id.radio_button2;
					// if (!ApplicationManager.getInstance().isLogin()) {
					// Intent i = new Intent(LehoTabActivity.this,
					// LoginPage.class);
					// startActivity(i);
					// radioGroup.check(R.id.radio_button0);
					// } else {
					// Log.d("zhy", "-----"+"PublishPage");
					// Intent i = new Intent(LehoTabActivity.this,
					// PublishPage.class);
					// startActivity(i);
					// radioGroup.check(R.id.radio_button2);
					// }
					break;
				case R.id.radio_button3:
					if (ApplicationManager.getInstance().isLogin()) {
						mCurrentTabId = R.id.radio_button3;
						if (mPreviousTabId == R.id.radio_button3) {
							Intent intent = new Intent(
									LehuoIntent.FRIEND_FEED_UPDATE);
							sendBroadcast(intent);
						}
					}
					break;
				case R.id.radio_button4:
					if (ApplicationManager.getInstance().isLogin()) {
						mCurrentTabId = R.id.radio_button4;
						if (mPreviousTabId == R.id.radio_button4) {
							Intent intent = new Intent(
									LehuoIntent.PERSON_FEED_UPDATE);
							sendBroadcast(intent);
						}
					}
					break;
				}
			}
		};
		mRadioButton0.setOnClickListener(onClickListener);
		mRadioButton1.setOnClickListener(onClickListener);
		mRadioButton2.setOnClickListener(onClickListener);
		mRadioButton3.setOnClickListener(onClickListener);
		mRadioButton4.setOnClickListener(onClickListener);

		mCountView = (TextView) findViewById(R.id.news_count_view);
		registNewNewsCountReceiver();
		registScreenOnOrOffReceiver();
		ApplicationManager.getInstance().init(LehoTabActivity.this);
		initView();
		UpgradeUtil.checkUpgrade(LehoTabActivity.this);
		LoginUtil.getSaveHomeData(LehoTabActivity.this);
//		ApplicationManager.getInstance().init(LehoTabActivity.this);
		((LehoApp) LehoTabActivity.this.getApplication()).initMap();
		((LehoApp) LehoTabActivity.this.getApplication())
				.startLocationTask(100);
		radioGroup.setOnCheckedChangeListener(LehoTabActivity.this);
		radioGroup.check(R.id.radio_button0);
		checkLogin();
	}

	private void checkLogin() {
		ActionController.post(this, CheckLoginAction.class, null,
				new CheckLoginAction.CheckResultListner() {
					@Override
					public void onHasUser() {
						Log.d(TAG, "onHasUser");
						Intent intent = new Intent(LehoTabActivity.this,
								UpdateService.class);
						intent.putExtra(UpdateService.PARAM_TYPE,
								UpdateService.TYPE_STARTCLIENT);
						startService(intent);
						initView();
					}

					@Override
					public void onNoUser() {
						initView();
					}
				});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		UpgradeUtil.checkUpgrade(LehoTabActivity.this);
		LoginUtil.getSaveHomeData(LehoTabActivity.this);
		checkLogin();
		super.onNewIntent(intent);
	}

	private void initView() {
		initTabs();
	}

	private void initTabs() {
		Intent intent0 = new Intent(this, JingXuanPage.class);
		Intent intent1 = new Intent(this, CircumFeedActivity.class);
		Intent intent2 = new Intent(this, FriendFeedActivity.class);
		Intent intent3 = new Intent(this, MySelfPersonnalInforPage.class);
		intent3.putExtra(MySelfPersonnalInforPage.KEY_FROM_HOME, true);
		mHost = getTabHost();
		mHost.setOnTabChangedListener(this);
		mHost.addTab(mHost.newTabSpec(TAG_FIND).setIndicator(TAG_FIND)
				.setContent(intent0));
		mHost.addTab(mHost.newTabSpec("circumFeed").setIndicator("circumFeed")
				.setContent(intent1));
		// mHost.addTab(mHost.newTabSpec("publish").setIndicator("publish").set);
		mHost.addTab(mHost.newTabSpec("friendFeed").setIndicator("friendFeed")
				.setContent(intent2));
		mHost.addTab(mHost.newTabSpec("information")
				.setIndicator("information").setContent(intent3));
		mHost.setup(this.getLocalActivityManager());
	}

	@Override
	public void onTabChanged(String tabId) {
	}

	protected void showToast(final int resourceID) {
		showToast(getString(resourceID));
	}

	protected void showToast(final String str) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(LehoTabActivity.this, str, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("resultCode:" + resultCode);
		System.out.println("requestCode:" + requestCode);
		switch (requestCode) {
		case REQUEST_CODE_LOGIN:
			finish();
			break;
		case REQUEST_CODE_CLIENT_QUIT:
			if (resultCode == RESULT_OK) {
				finish();
			}
			break;
		}
	}

	private class NewNewsCountReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!ApplicationManager.getInstance().isLogin()) {
				return;
			}
			boolean isSayMeShow = MoreUtil.readIsShowSayMeFromPref(context);
			boolean isAddMeShow = MoreUtil.readIsShowAddMeFromPref(context);
			boolean isFavShow = MoreUtil.readIsShowFavFromPref(context);
			int count = 0;
			if (UpdateService.INTENT_NEW_NEWS_COUNT_CHANGE.equals(intent
					.getAction())) {
				final int[] list = NewsUtil.readNewCountFromPref(context);
				if (isAddMeShow) {
					count += list[0];
				}
				if (isSayMeShow) {
					count += list[1];
				}
				if (isFavShow) {
					count += list[2];
				}
				final int total = count;
				if (total != 0) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if (mCountView != null) {
								mCountView.setText(total > 99 ? "99+" : total
										+ "");
								mCountView.setVisibility(View.VISIBLE);
							}
						}
					});

				} else {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if (mCountView != null) {
								mCountView.setVisibility(View.INVISIBLE);
							}
						}
					});
				}
			}
		}
	}

	private void registNewNewsCountReceiver() {
		mNewNewsCountReceiver = new NewNewsCountReceiver();
		IntentFilter filter = new IntentFilter(
				UpdateService.INTENT_NEW_NEWS_COUNT_CHANGE);
		registerReceiver(mNewNewsCountReceiver, filter);
	}

	private void registScreenOnOrOffReceiver() {
		mScreenOnOrOffReceiver = new ScreenOnOrOffReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mScreenOnOrOffReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mNewNewsCountReceiver);
		unregisterReceiver(mScreenOnOrOffReceiver);
	}

	@Override
	protected void onPause() {
		// super.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
		super.onPause();

	}

	@Override
	protected void onResume() {
		// super.overridePendingTransition(R.anim.push_left_out,R.anim.push_left_in);
		super.onResume();

	}

	protected void showQuitPage() {
		Intent intent = new Intent(this, ExitPage.class);
		startActivityForResult(intent, REQUEST_CODE_CLIENT_QUIT);
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				showQuitPage();
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onCheckedChanged(NewsTitleRadioGroup informationRadioGroup,
			int checkedId) {
		if (!ApplicationManager.getInstance().isLogin()
				&& checkedId != R.id.radio_button0
				&& checkedId != R.id.radio_button1) {
			Intent i = new Intent(LehoTabActivity.this, LoginPage.class);
			startActivity(i);
			radioGroup.check(R.id.radio_button0);
			return;
		}
		if (ApplicationManager.getInstance().isLogin()
				&& checkedId == R.id.radio_button2) {
			this.mHost.setCurrentTabByTag("publish");
			// mRadioButton2.setCompoundDrawablesWithIntrinsicBounds(0,
			// R.drawable.tab_publish_bg_sel, 0, 0);
			Intent i = new Intent(LehoTabActivity.this, PublishPage.class);
			startActivity(i);
			radioGroup.check(mPreviousCheckId);
			return;
		}
		mRadioButton0.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.tab_jingxuan, 0, 0);
		mRadioButton1.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.tab_circum, 0, 0);
		mRadioButton2.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.tab_publish_bg, 0, 0);
		mRadioButton3.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.tab_haoyou, 0, 0);
		mRadioButton4.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.tab_wo, 0, 0);
		switch (checkedId) {
		case R.id.radio_button0:
			mPreviousCheckId=R.id.radio_button0;
			this.mHost.setCurrentTabByTag(TAG_FIND);
			mRadioButton0.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.tab_jingxuan_sel, 0, 0);
			break;
		case R.id.radio_button1:
			mPreviousCheckId=R.id.radio_button1;
			this.mHost.setCurrentTabByTag("circumFeed");
			mRadioButton1.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.tab_circum_sel, 0, 0);
			break;
		// case R.id.radio_button2:
		// this.mHost.setCurrentTabByTag("publish");
		// // mRadioButton2.setCompoundDrawablesWithIntrinsicBounds(0,
		// // R.drawable.tab_publish_bg_sel, 0, 0);
		// Intent i = new Intent(LehoTabActivity.this, PublishPage.class);
		// startActivity(i);
		// break;
		case R.id.radio_button3:
			mPreviousCheckId=R.id.radio_button3;
			this.mHost.setCurrentTabByTag("friendFeed");
			mRadioButton3.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.tab_haoyou_sel, 0, 0);

			break;
		case R.id.radio_button4:
			mPreviousCheckId=R.id.radio_button4;
			this.mHost.setCurrentTabByTag("information");
			mRadioButton4.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.tab_wo_sel, 0, 0);
			break;
		}
	}
}