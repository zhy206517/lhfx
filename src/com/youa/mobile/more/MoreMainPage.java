package com.youa.mobile.more;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;
import com.umeng.fb.util.FeedBackListener;
import com.youa.mobile.LehoTabActivity;
import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.util.UpgradeUtil;
import com.youa.mobile.friend.store.FriendFileStore;
import com.youa.mobile.information.manager.PersonalInfoManager;
import com.youa.mobile.more.action.MoreDeleteCacheAction;
import com.youa.mobile.more.action.MoreDeleteCacheAction.IMoreDeleteCacheResultListener;
import com.youa.mobile.more.data.JptjData;
import com.youa.mobile.news.util.NewsUtil;

public class MoreMainPage extends BasePage {

	private static final int SET_APP_ICON = 0;

	private Button mMsgNotifyCheckBox;
	private Button mSetLbsCheckBox;
	private Button mBrowserCheckBox;
	private Button mSyncCheckBox;
	// private CheckBox mAddMeCheckBox;
	// private CheckBox mSayMeCheckBox;
	// private CheckBox mFavCheckBox;
	private TextView mTitle;
	private ImageButton mBack;
	private Button mFeedbackBtn;
	private Button mAboutBtn;
	private Button mCleanCacheBtn;
	private Button mExitClientBtn;
	private Button mCheckUpdateBtn;
	private Button mBindingThirdAccountBtn;
	private ButtonOnClickListener mOnClickListener = new ButtonOnClickListener();
	private LayoutInflater mInflater;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (SET_APP_ICON == msg.what) {

			}
			super.handleMessage(msg);
		}
	};

	public static final int REQUEST_CODE_USER_QUIT = 0;

	// private Handler mHandler = new Handler();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_main);
		mInflater = LayoutInflater.from(this);
		UMFeedbackService.enableNewReplyNotification(this, NotificationType.NotificationBar);
		initView();
		loadJptjData();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initView() {
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(R.string.setting_title);
		mBack = (ImageButton) findViewById(R.id.back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(mOnClickListener);
		mMsgNotifyCheckBox = (Button) findViewById(R.id.msg_notify_checkbox);
		mSetLbsCheckBox = (Button) findViewById(R.id.set_lbs_checkbox);
		mBrowserCheckBox = (Button) findViewById(R.id.browser_modle_id);
		mSyncCheckBox = (Button) findViewById(R.id.sync_settings_id);
		mMsgNotifyCheckBox.setOnClickListener(mOnClickListener);
		mSetLbsCheckBox.setOnClickListener(mOnClickListener);
		mBrowserCheckBox.setOnClickListener(mOnClickListener);
		mSyncCheckBox.setOnClickListener(mOnClickListener);
		// mAddMeCheckBox = (CheckBox) findViewById(R.id.msg_addme_checkbox);
		// mSayMeCheckBox = (CheckBox) findViewById(R.id.msg_sayme_checkbox);
		// mFavCheckBox = (CheckBox) findViewById(R.id.msg_fav_checkbox);
		//
		// mAddMeCheckBox.setChecked(MoreUtil.readIsShowAddMeFromPref(this));
		// mSayMeCheckBox.setChecked(MoreUtil.readIsShowSayMeFromPref(this));
		// mFavCheckBox.setChecked(MoreUtil.readIsShowFavFromPref(this));

		// mAddMeCheckBox.setOnCheckedChangeListener(new
		// OnCheckedChangeListener() {
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean
		// isChecked) {
		// MoreUtil.writeIsShowAddMeNewsToPref(MoreMainPage.this, isChecked);
		// }
		// });
		// mSayMeCheckBox.setOnCheckedChangeListener(new
		// OnCheckedChangeListener() {
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean
		// isChecked) {
		// MoreUtil.writeIsShowSayMeNewsToPref(MoreMainPage.this, isChecked);
		// }
		// });
		// mFavCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		// {
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean
		// isChecked) {
		// MoreUtil.writeIsShowFavNewsToPref(MoreMainPage.this, isChecked);
		// }
		// });

		mFeedbackBtn = (Button) findViewById(R.id.feedback);
		mAboutBtn = (Button) findViewById(R.id.about);
		mCleanCacheBtn = (Button) findViewById(R.id.clean_cache_btn);
		mExitClientBtn = (Button) findViewById(R.id.exit_user_btn);
		mCheckUpdateBtn = (Button) findViewById(R.id.check_update_btn);
		mBindingThirdAccountBtn = (Button) findViewById(R.id.binding_third_account);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// mMsgNotifyCheckBox.setChecked(MoreUtil.readIsShowNewNewsFromPref(this));
		// mMsgNotifyCheckBox.setOnCheckedChangeListener(new
		// OnCheckedChangeListener() {
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean
		// isChecked) {
		// MoreUtil.writeIsShowNewNewsToPref(MoreMainPage.this, isChecked);
		// }
		// });
		// mSetLbsCheckBox.setChecked(MoreUtil.readIsStartLocationFromPref(this));
		// mSetLbsCheckBox.setOnCheckedChangeListener(new
		// OnCheckedChangeListener() {
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean
		// isChecked) {
		// ((LehoApp)MoreMainPage.this.getApplication()).isStartLoc=isChecked;
		// if(isChecked){
		// ((LehoApp)MoreMainPage.this.getApplication()).initMap();
		// ((LehoApp)MoreMainPage.this.getApplication()).startLocationTask(100);
		// }else{
		// ((LehoApp)MoreMainPage.this.getApplication()).destroyMap();
		// }
		// MoreUtil.writeIsStartLocationFromPref(MoreMainPage.this, isChecked);
		//
		// }
		// });
		mFeedbackBtn.setOnClickListener(mOnClickListener);
		mAboutBtn.setOnClickListener(mOnClickListener);
		mCleanCacheBtn.setOnClickListener(mOnClickListener);
		mExitClientBtn.setOnClickListener(mOnClickListener);
		mCheckUpdateBtn.setOnClickListener(mOnClickListener);
		mBindingThirdAccountBtn.setOnClickListener(mOnClickListener);
	}

	private ImageView[] mIcons;

	private void loadJptjData() {
		LinearLayout applayout = (LinearLayout) findViewById(R.id.jptj_app_layout);
		// ArrayList<JptjData> datas = JptjUtils.getJptj(this);
		ArrayList<JptjData> datas = new ArrayList<JptjData>();
		JptjData data = new JptjData("经期助手",
				"女性经期记录必备应用。不仅能够科学预测排卵期，还可建议适合的受孕时间。关爱自己，从佳期有约开始。",
				R.drawable.jiaqi_icon,
				"http://st.youa.com/resource/wl/jiaqi.apk");
		datas.add(data);
		if (datas == null || datas.isEmpty()) {
			findViewById(R.id.jptj_layout).setVisibility(View.GONE);
			return;
		}
		int index = 0;
		mIcons = new ImageView[datas.size()];
		for (JptjData d : datas) {
			// add divider
			if (index > 0) {
				LayoutParams params = applayout.findViewById(R.id.app_line)
						.getLayoutParams();
				View line = new View(this);
				line.setLayoutParams(params);
				line.setBackgroundResource(R.drawable.jptj_item_line_middle);
				applayout.addView(line);
			}
			// add item
			View view = mInflater.inflate(R.layout.jptj_item, null);
			mIcons[index] = (ImageView) view.findViewById(R.id.app_icon);
			((TextView) view.findViewById(R.id.app_name)).setText(d.name);
			((TextView) view.findViewById(R.id.app_desc)).setText(d.info);
			view.findViewById(R.id.to_download).setTag(d.url);
			view.findViewById(R.id.to_download).setVisibility(View.VISIBLE);
			applayout.addView(view);
			mIcons[index].setImageResource(d.picId);
			// if (!TextUtils.isEmpty(d.pic)) {
			// JptjUtils.downloadImage(index, d.pic, new DownloadListener() {
			//
			// @Override
			// public void onSuccess(final int index,
			// final Drawable drawable) {
			// runOnUiThread(new Runnable() {
			// public void run() {
			// mIcons[index].setImageDrawable(drawable);
			// }
			// });
			// }
			//
			// @Override
			// public void onFailure(int index) {
			//
			// }
			// });
			// }
			index++;
		}
	}

	private class ButtonOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent i;
			switch (v.getId()) {
			case R.id.back:
				MoreMainPage.this.finish();
				break;
			case R.id.feedback:
//				i = new Intent();
//				i.setClass(MoreMainPage.this, FeedbackPage.class);
//				startActivity(i);
				startFeedbackPage();
				break;
			case R.id.msg_notify_checkbox:
				i = new Intent();
				i.setClass(MoreMainPage.this, NewsSettingsPage.class);
				startActivity(i);
				break;
			case R.id.set_lbs_checkbox:
				i = new Intent();
				i.setClass(MoreMainPage.this, LocationSettingPage.class);
				startActivity(i);
				break;
			case R.id.browser_modle_id:
				i = new Intent();
				i.setClass(MoreMainPage.this, BrowserModlePage.class);
				startActivity(i);
				break;
			case R.id.sync_settings_id:
				i = new Intent();
				i.setClass(MoreMainPage.this, SyncSettingPage.class);
				startActivity(i);
				break;
			case R.id.about:
				i = new Intent();
				i.setClass(MoreMainPage.this, AboutPage.class);
				startActivity(i);
				break;
			case R.id.clean_cache_btn:
				ActionController.post(MoreMainPage.this,
						MoreDeleteCacheAction.class, null,
						new IMoreDeleteCacheResultListener() {

							@Override
							public void onStart() {
								showProgressDialog(MoreMainPage.this,
										R.string.clean_cache_lable,
										R.string.cleaning_cache);
							}

							@Override
							public void onFinish() {
								hideProgressDialog();
								showToast(R.string.clean_cache_finish);
								// computeCacheSize();
							}

							@Override
							public void onFail(int resourceID) {
								showToast(resourceID);
								hideProgressDialog();
							}
						}, true);
				break;
			case R.id.exit_user_btn:
				showExitUserPage();
				break;
			case R.id.check_update_btn:
				UpgradeUtil.checkUpgrade(MoreMainPage.this, true,true);
				break;
			case R.id.binding_third_account:
				showBindingThirdAccountPage();
				break;
			}
		}

		public void startFeedbackPage() {
			FeedBackListener listener = new FeedBackListener() {
			    @Override
			    public void onSubmitFB(Activity activity) {
//			        EditText phoneText = (EditText) activity
//			                .findViewById(R.id.feedback_phone);
//			        EditText qqText = (EditText) activity
//			                .findViewById(R.id.feedback_qq);
//			        EditText nameText = (EditText) activity
//			                .findViewById(R.id.feedback_name);
//			        EditText emailText = (EditText) activity
//			                .findViewById(R.id.feedback_email);
//			        Map<String, String> contactMap = new HashMap<String, String>();
//			        contactMap.put("phone", phoneText.getText()
//			                .toString());
//			        contactMap.put("qq", qqText.getText()
//			                .toString());
//			        UMFeedbackService.setContactMap(contactMap);
//			        Map<String, String> remarkMap = new HashMap<String, String>();
//			        remarkMap.put("name", nameText.getText()
//			                .toString());
//			        remarkMap.put("email", emailText
//			                .getText().toString());
//			        UMFeedbackService.setRemarkMap(remarkMap);
			    }
			    @Override
			    public void onResetFB(Activity activity,
			            Map<String, String> contactMap,
			            Map<String, String> remarkMap) {
			        // TODO Auto-generated method stub`
			        // FB initialize itself,load other attribute
			        // from local storage and set them
//			        EditText phoneText = (EditText) activity
//			                .findViewById(R.id.feedback_phone);
//			        EditText qqText = (EditText) activity
//			                .findViewById(R.id.feedback_qq);
//			        EditText nameText = (EditText) activity
//			                .findViewById(R.id.feedback_name);
//			        EditText emailText = (EditText) activity
//			                .findViewById(R.id.feedback_email);
//			        if (remarkMap != null) {
//			            nameText.setText(remarkMap.get("name"));
//			            emailText.setText(remarkMap
//			                    .get("email"));
//			        }
//			        if (contactMap != null) {
//			            phoneText.setText(contactMap
//			                    .get("phone"));
//			            qqText.setText(contactMap.get("qq"));
//			        }
			    }
			};
			UMFeedbackService.setGoBackButtonVisible();
			UMFeedbackService.setFeedBackListener(listener); 
			UMFeedbackService.openUmengFeedbackSDK(MoreMainPage.this);
		}
	}

	// private void computeCacheSize() {
	// new Thread() {
	// public void run() {
	// String path = SavePathManager.getImagePath();
	// File pathFile = new File(path);
	// long size = FileUtil.getSize(pathFile);
	// final String showText = FileUtil.getSizeText(size);
	// mHandler.post(new Runnable(){
	// @Override
	// public void run() {
	// String text = getString(R.string.clean_cache_lable);
	// text += " (" + showText + ")";
	// mCleanCacheBtn.setText(text);
	// }
	// });
	// }
	// }.start();
	// }

	private void showExitUserPage() {
		Intent intent = new Intent(this, ExitUserPage.class);
		startActivityForResult(intent, REQUEST_CODE_USER_QUIT);
	}

	private void showBindingThirdAccountPage() {
		Intent intent = new Intent(this, SyncSettingPage.class);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_USER_QUIT:
			if (resultCode == RESULT_OK) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						SharedPreferences preference = MoreMainPage.this
								.getSharedPreferences(
										SystemConfig.XML_FILE_SYSTEM_CONFIG,
										Context.MODE_WORLD_WRITEABLE);
						SharedPreferences.Editor editor = preference.edit();
						// editor.remove(SystemConfig.KEY_LOGIN_PASS);
						editor.remove(SystemConfig.KEY_SESSION_PREFIX);
						editor.remove(SystemConfig.KEY_YOUAINDENTITY_PREFIX);
						editor.remove(SystemConfig.KEY_USERID_PREFIX);
						editor.remove(SystemConfig.KEY_THIRD_USERID);
						editor.remove(SystemConfig.KEY_THIRD_SITE);
						editor.commit();
						SharedPreferences newsPreference = MoreMainPage.this
								.getSharedPreferences(
										SystemConfig.XML_NEW_NEWS_COUNT,
										Context.MODE_WORLD_WRITEABLE);
						SharedPreferences.Editor newsEditor = newsPreference
								.edit();
						newsEditor.remove(NewsUtil.ADD_ME_COUNT);
						newsEditor.remove(NewsUtil.SAY_ME_COUNT);
						newsEditor.remove(NewsUtil.FAVORITE_COUNT);
						newsEditor.remove(NewsUtil.ALL_COUNT);
						newsEditor.commit();
						ApplicationManager.getInstance().logout(
								MoreMainPage.this);
						FriendFileStore.getInstance().clearData(
								MoreMainPage.this);
						try {
							new PersonalInfoManager().deleteFriend(
									MoreMainPage.this, ApplicationManager
											.getInstance().getUserId());
						} catch (MessageException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// FriendFeedActivity.saveHomeDataList = null;
						sendBroadcast(new Intent(LehuoIntent.ACTION_EXIT_CLIENT));
						Intent i = new Intent();
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						i.setClass(MoreMainPage.this, LehoTabActivity.class);
						startActivity(i);
						finish();
					}
				});
			}
			break;
		}
	}

	public void toDownloadApp(View view) {
		String tag = (String) view.getTag();
		if (!TextUtils.isEmpty(TAG)) {
			Uri uri = Uri.parse(tag);
			startActivity(new Intent(Intent.ACTION_VIEW, uri));
		}
	}
}
