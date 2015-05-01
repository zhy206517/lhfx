package com.youa.mobile;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

import com.youa.mobile.circum.CircumFeedActivity;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.util.UpgradeUtil;
import com.youa.mobile.friend.FriendFeedActivity;
import com.youa.mobile.information.PersonnalInforPage;
import com.youa.mobile.jingxuan.JingXuanPage;
import com.youa.mobile.login.LoginPage;
import com.youa.mobile.login.action.CheckLoginAction;
import com.youa.mobile.news.NewsTitleRadioGroup;

public class MainActivity extends TabActivity implements OnTabChangeListener,
		NewsTitleRadioGroup.OnCheckedChangeListener {
	public static String TAG = "MainActivity";
	public static final int REQUEST_CODE_LOGIN = 0;
	public static final int REQUEST_CODE_CLIENT_QUIT = 100;
	public static final String TAG_FIND = "piazza";
	// public static final String TAG_FIND_PEOPLE = "people";
	public static final String TAG_FIND_THEME = "theme";
	public static final String INIT_TAG = "init_tag";
	public static final String INIT_FIND_TAG = "init_find_tag";
	private TabHost mHost;
	private Handler mHandler = new Handler();
	private NewsTitleRadioGroup radioGroup;
	private Intent intent0;

	private RadioButton mRadioButton0;
	private RadioButton mRadioButton1;
	private RadioButton mRadioButton2;
	private RadioButton mRadioButton3;

	private int mPreviousTabId;
	private int mCurrentTabId;

	// private AlertDialog mLoginDialog;
	// private int mCurrentTabId;
	// private ScreenOnOrOffReceiver mScreenOnOrOffReceiver;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		setContentView(R.layout.maintabs);
		radioGroup = (NewsTitleRadioGroup) findViewById(R.id.main_radio);
		radioGroup.check(R.id.radio_button0);
		radioGroup.setOnCheckedChangeListener(MainActivity.this);
		mRadioButton0 = (RadioButton) findViewById(R.id.radio_button0);
		mRadioButton1 = (RadioButton) findViewById(R.id.radio_button1);
		mRadioButton2 = (RadioButton) findViewById(R.id.radio_button2);
		mRadioButton3 = (RadioButton) findViewById(R.id.radio_button3);
		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPreviousTabId = mCurrentTabId;
				switch (v.getId()) {
				case R.id.radio_button0:
					mCurrentTabId = 0;
					if (mPreviousTabId == 0) {
						sendBroadcast(new Intent(
								LehuoIntent.JINGXUAN_FEED_UPDATE));
					}
					break;
				case R.id.radio_button1:
					mCurrentTabId = 1;
					if (mPreviousTabId == 1) {
						sendBroadcast(new Intent(LehuoIntent.CIRCUM_FEED_UPDATE));
					}
					break;
				}
			}
		};
		mRadioButton0.setOnClickListener(onClickListener);
		mRadioButton1.setOnClickListener(onClickListener);
		// registScreenOnOrOffReceiver();
		ApplicationManager.getInstance().init(MainActivity.this);
		((LehoApp) MainActivity.this.getApplication()).initMap();
		((LehoApp) MainActivity.this.getApplication()).startLocationTask(100);
		checkLogin();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		checkLogin();
	}

	private void checkLogin() {
		ActionController.post(this, CheckLoginAction.class, null,
				new CheckLoginAction.CheckResultListner() {
					@Override
					public void onHasUser() {
						Log.d(TAG, "onHasUser");
						ApplicationManager.getInstance()
								.init(MainActivity.this);
						Intent intent = new Intent(MainActivity.this,
								UpdateService.class);
						intent.putExtra(UpdateService.PARAM_TYPE,
								UpdateService.TYPE_STARTCLIENT);
						startService(intent);
						startActivity(new Intent(MainActivity.this,
								LehoTabActivity.class));
						MainActivity.this.finish();
					}

					@Override
					public void onNoUser() {
						ApplicationManager.getInstance()
								.init(MainActivity.this);
						initView();
					}
				});
	}

	private void initView() {
		initTabs();
	}

	private void initTabs() {
		intent0 = new Intent(this, JingXuanPage.class);
		Intent intent1 = new Intent(this, CircumFeedActivity.class);
		Intent intent2 = new Intent(this, FriendFeedActivity.class);
		Intent intent3 = new Intent(this, PersonnalInforPage.class);
		intent3.putExtra(PersonnalInforPage.KEY_FROM_HOME, true);

		mHost = getTabHost();
		mHost.setOnTabChangedListener(this);
		mHost.addTab(mHost.newTabSpec(TAG_FIND).setIndicator(TAG_FIND)
				.setContent(intent0));
		mHost.addTab(mHost.newTabSpec("circumFeed").setIndicator("circumFeed")
				.setContent(intent1));
		mHost.addTab(mHost.newTabSpec("friendFeed").setIndicator("friendFeed")
				.setContent(intent2));
		mHost.addTab(mHost.newTabSpec("information")
				.setIndicator("information").setContent(intent3));
		mHost.setup(this.getLocalActivityManager());
		Intent intent = getIntent();
		if (intent != null) {
			String initTag = intent.getStringExtra(INIT_TAG);
			if (initTag != null && initTag.equals(TAG_FIND)) {
				radioGroup.check(R.id.radio_button3);
			}
		}
		UpgradeUtil.checkUpgrade(MainActivity.this);
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
				Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT)
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

	// private void registScreenOnOrOffReceiver() {
	// mScreenOnOrOffReceiver = new ScreenOnOrOffReceiver();
	// IntentFilter filter = new IntentFilter();
	// filter.addAction(Intent.ACTION_SCREEN_ON);
	// filter.addAction(Intent.ACTION_SCREEN_OFF);
	// registerReceiver(mScreenOnOrOffReceiver, filter);
	// }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// unregisterReceiver(mScreenOnOrOffReceiver);
	}

	@Override
	protected void onPause() {
		// super.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
		super.onPause();

	}

	@Override
	protected void onStop() {
		// if( mLoginDialog!=null){
		// mLoginDialog.dismiss();
		// }
		super.onStop();

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
			// if(mLoginDialog==null){
			// AlertDialog.Builder builder= new AlertDialog.Builder(
			// MainActivity.this);
			// builder.setMessage(getResources().getString(
			// R.string.login_confirm));
			// builder.setPositiveButton(getResources().getString(R.string.yes),
			// new OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// Intent i = new Intent(MainActivity.this,
			// LoginPage.class);
			// startActivity(i);
			// radioGroup.check(R.id.radio_button0);
			// }
			// });
			// builder.setNegativeButton(getResources().getString(R.string.no),
			// new OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// radioGroup.check(R.id.radio_button0);
			// }
			// });
			// mLoginDialog = builder.create();
			// }
			// mLoginDialog.show();
			Intent i = new Intent(MainActivity.this, LoginPage.class);
			startActivity(i);
			radioGroup.check(R.id.radio_button0);
			return;
		}
		mRadioButton0.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.tab_jingxuan, 0, 0);
		mRadioButton1.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.tab_circum, 0, 0);
		mRadioButton2.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.tab_haoyou, 0, 0);
		mRadioButton3.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.tab_wo, 0, 0);
		switch (checkedId) {
		case R.id.radio_button0:
			intent0.putExtra(INIT_FIND_TAG, (String) null);
			this.mHost.setCurrentTabByTag(TAG_FIND);
			mRadioButton0.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.tab_jingxuan_sel, 0, 0);
			break;
		case R.id.radio_button1:
			this.mHost.setCurrentTabByTag("circumFeed");
			mRadioButton1.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.tab_circum_sel, 0, 0);
			break;
		case R.id.radio_button2:
			this.mHost.setCurrentTabByTag("friendFeed");
			mRadioButton2.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.tab_haoyou_sel, 0, 0);
			break;
		case R.id.radio_button3:
			this.mHost.setCurrentTabByTag("information");
			mRadioButton3.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.tab_wo_sel, 0, 0);
			break;
		}
	}

}