package com.youa.mobile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.manager.SavePathManager;
import com.youa.mobile.common.util.FileUtil;
import com.youa.mobile.common.util.UpgradeUtil;
import com.youa.mobile.content.WXShareMainPage;
import com.youa.mobile.friend.store.FriendFileStore;
import com.youa.mobile.information.data.PersonalInformationData;
import com.youa.mobile.location.MapPage;
import com.youa.mobile.login.LoginGuideImgAdapter;
import com.youa.mobile.login.util.LoginUtil;
import com.youa.mobile.login.widget.CustomGallery;
import com.youa.mobile.more.action.MoreDeleteCacheAction;
import com.youa.mobile.more.action.MoreDeleteCacheAction.IMoreDeleteCacheResultListener;
import com.youa.mobile.wxapi.WXEntryActivity;

public class PreparePage extends BasePage {
	private final static String TAG = "PreparePage";
	private SharedPreferences preference;
	public static String _IS_GUIDE = "isGuide";
	private LinearLayout mLoginGuide;
	private RelativeLayout mPrePareLayout;
	private CustomGallery mGuideGallery;
	private boolean isLast = false;
	public static boolean isFrist = false;
	public static int GUIDE_POS = -1;
	// IWXAPI 是第三方app和微信通信的openapi接口
	private IWXAPI api;
	public static final String APP_ID = "wx00a3ed16a2dd178e";
	private boolean isWXAppSupportAPI = false;
	private UserInforUpdateReciever mUserInforUpdateReciever = new UserInforUpdateReciever();
	public class UserInforUpdateReciever extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if("com.youa.mobile.exit".equals(intent.getAction())){
				PreparePage.this.onStop();
			}
			
		}
	}
	@Override
	protected void onDestroy() {
		this.unregisterReceiver(mUserInforUpdateReciever);
		super.onDestroy();
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.youa.mobile.exit");
		this.registerReceiver(mUserInforUpdateReciever, filter);
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		setContentView(R.layout.prepare_page);
		if (UpgradeUtil.checkForceUpgrade(PreparePage.this)) {
			UpgradeUtil.popForceUpgradeDialog(PreparePage.this);
		} else {
			api = WXAPIFactory.createWXAPI(this, APP_ID, false);
			isWXAppSupportAPI = api.isWXAppSupportAPI();
			if (isWXAppSupportAPI) {
				api.registerApp(APP_ID);
			}
			clearFile();
			MobclickAgent.onError(this);
			new Thread() {
				public void run() {
					clearCache();
				}
			}.start();
			mLoginGuide = (LinearLayout) findViewById(R.id.login_guide_layout);
			mPrePareLayout = (RelativeLayout) findViewById(R.id.prepare_layout);
			mGuideGallery = (CustomGallery) findViewById(R.id.login_guide);
			preference = PreparePage.this.getSharedPreferences(
					SystemConfig.XML_FILE_LOGIN_GUIDE,
					Context.MODE_WORLD_WRITEABLE);
			mHandler.postDelayed(new Runnable() {
				public void run() {
					if (WXEntryActivity.EXTRA_FROM_WEIXIN.equals(getIntent()
							.getStringExtra(WXEntryActivity.EXTRA_WHERE_FROM))) {
						startActivity(new Intent(PreparePage.this,
								WXShareMainPage.class));
						finish();
					} else {
						showGuideImage();
					}
				}
			}, 200);
			createShortCut();
			if (SystemConfig.SCREEN_WIDTH == -1) {
				int width = getWindowManager().getDefaultDisplay().getWidth();
				if (width == 320 || width == 480) {
					SystemConfig.SCREEN_WIDTH = 0;
				} else if (width > 0) {
					SystemConfig.SCREEN_WIDTH = width;
				}
			}
		}
		;

	}
	@Override
	protected void onPause() {
		super.onPause();
		if (GUIDE_POS != -1) {
			String str = null;
			if (GUIDE_POS == 0) {
				str = YoumentEvent.EVENT_EXIT_GUIDE1;
			} else if (GUIDE_POS == 1) {
				str = YoumentEvent.EVENT_EXIT_GUIDE2;
			} else if (GUIDE_POS == 2) {
				str = YoumentEvent.EVENT_EXIT_GUIDE3;
			} else if (GUIDE_POS == 3) {
				str = YoumentEvent.EVENT_ENTER_APP;
			}
			MobclickAgent.onEvent(PreparePage.this, YoumentEvent.EVENT_EXIT,
					str);
		}
	}

	// --------------------------------------------------------------------------
	private SharedPreferences preferenceVersion;
	public final String XML_FILE_UPGRADE_FIRST_LOGIN = "upgrade_frist_login";
	public final String UPGRADE_LAST_VERSIONCODE = "versioncode";

	// 15, 1.1.1由username改成nickname，清除原好友动态文件数据
	private void clearFile() {
		preferenceVersion = PreparePage.this.getSharedPreferences(
				XML_FILE_UPGRADE_FIRST_LOGIN, Context.MODE_WORLD_WRITEABLE);
		int versionCode = preferenceVersion.getInt(UPGRADE_LAST_VERSIONCODE, 0);
		// --------------------
		PackageManager pm = PreparePage.this.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(PreparePage.this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
		}
		// --------------------
		if (versionCode < 15 && pi != null && pi.versionCode >= 15
				&& versionCode != pi.versionCode) {
			FriendFileStore.getInstance().clearData(PreparePage.this);
		}
		// --------------------
		SharedPreferences.Editor editor = preferenceVersion.edit();
		editor.putInt(UPGRADE_LAST_VERSIONCODE, pi.versionCode);
		editor.commit();
	}

	// --------------------------------------------------------------------------

	public void showGuideImage() {
		if (!preference.getBoolean(_IS_GUIDE, false)) {
			mGuideGallery.setHorizontalFadingEdgeEnabled(false);
			mGuideGallery.setVerticalFadingEdgeEnabled(false);
			mLoginGuide.setVisibility(View.VISIBLE);
			mPrePareLayout.setVisibility(View.GONE);
			List<Integer> list = new ArrayList<Integer>();
			list.add(R.drawable.login_guide1);
			list.add(R.drawable.login_guide2);
			list.add(R.drawable.login_guide3);
			mGuideGallery.setAdapter(new LoginGuideImgAdapter(PreparePage.this,
					list));
			mGuideGallery
					.setOnItemSelectedListener(new OnGuideGalleryListener());
			mGuideGallery.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if (arg2 == 2) {
						SharedPreferences preference = PreparePage.this
								.getSharedPreferences(
										SystemConfig.XML_FILE_LOGIN_GUIDE,
										Context.MODE_WORLD_WRITEABLE);
						SharedPreferences.Editor editor = preference.edit();
						editor.putBoolean(PreparePage._IS_GUIDE, true);
						editor.commit();
						GUIDE_POS = 3;
						startMainActivity();
					} else if (arg2 < 2) {
						mGuideGallery.setSelection(++arg2);
					}

				}
			});
			mGuideGallery.setOnTouchListener(new OnTouchListener() {
				private float mXmotify = 0;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (isLast) {
						int action = event.getAction();
						if (action == MotionEvent.ACTION_DOWN) {
							mXmotify = event.getRawX();
							LoginUtil.LOGD(TAG, "ACTION_DOWN:mXmotify = "
									+ mXmotify);
						}
						if (action == MotionEvent.ACTION_MOVE) {
							if (event.getRawX() - mXmotify < -50) {
								SharedPreferences.Editor editor = preference
										.edit();
								editor.putBoolean(_IS_GUIDE, true);
								editor.commit();
								if (!isFrist) {
									GUIDE_POS = 3;
									startMainActivity();
								}
							}
						}
						if (action == MotionEvent.ACTION_UP) {
							mXmotify = 0;
						}
					}
					return false;
				}
			});
		} else {
			GUIDE_POS = -1;
			Intent intent = new Intent(PreparePage.this, LehoTabActivity.class);
			PreparePage.this.startActivity(intent);
			finish();
		}
	}

	public void startMainActivity() {
		isFrist = true;
		Intent intent = new Intent(PreparePage.this, LehoTabActivity.class);
		PreparePage.this.startActivity(intent);
		finish();
	}

	private void clearCache() {
		String path = SavePathManager.getImagePath();
		File pathFile = new File(path);
		long size = FileUtil.getSize(pathFile);
		if (size <= 1024 * 1024 * 50) {
			return;
		}
		ActionController.post(this, MoreDeleteCacheAction.class, null,
				new IMoreDeleteCacheResultListener() {
					@Override
					public void onStart() {
					}

					@Override
					public void onFinish() {
					}

					@Override
					public void onFail(int resourceID) {
					}
				}, false);
	}

	// private boolean shortCutInstalled() {
	// boolean isInstallShortcut = false;
	// final ContentResolver cr = this.getContentResolver();
	// final String AUTHORITY = "com.android.launcher.settings";
	// final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
	// + "/favorites?notify=true");
	//
	// Cursor c = cr.query(CONTENT_URI,
	// new String[] { "title", "iconResource" }, "title=?",
	// new String[] { getString(R.string.app_name) }, null);
	// if (c != null && c.getCount() > 0) {
	// isInstallShortcut = true;
	// }
	// return isInstallShortcut;
	// }

	private class OnGuideGalleryListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parentView, View arg1,
				int position, long arg3) {
			GUIDE_POS = position;
			if (position == parentView.getChildCount()) {
				isLast = true;
			} else {
				isLast = false;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	public void createShortCut() {
		SharedPreferences preference = this
				.getSharedPreferences(SystemConfig.XML_FILE_LOGIN_GUIDE,
						Context.MODE_WORLD_WRITEABLE);
		// 删除以前爱乐活的快捷方式
		delShortcut();
		if (!preference.getBoolean(_IS_GUIDE, false)) {
			// if(!shortCutInstalled()){
			// 创建快捷方式的Intent
			if (!hasShortcut()) {
				Intent shortcutintent = new Intent(
						"com.android.launcher.action.INSTALL_SHORTCUT");
				// 不允许重复创建
				shortcutintent.putExtra("duplicate", false);
				// 需要现实的名称
				shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
						getString(R.string.app_name));
				// 快捷图片
				Parcelable icon = Intent.ShortcutIconResource.fromContext(
						getApplicationContext(), R.drawable.icon);
				shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
						icon);
				// 点击快捷图片，运行的程序主入口
				shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
						new Intent(getApplicationContext(), PreparePage.class));
				// 发送广播。OK
				sendBroadcast(shortcutintent);
			}

			/*
			 * if(shortCutInstalled()){ Toast.makeText(this,
			 * "chuangjianchenggong", Toast.LENGTH_LONG).show(); }
			 */
		}
	}

	private void delShortcut() {
		Intent shortcut = new Intent(
				"com.android.launcher.action.UNINSTALL_SHORTCUT");
		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, "爱乐活");
		// 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
		// 注意: ComponentName的第二个参数必须是完整的类名（包名+类名），否则无法删除快捷方式
		String appClass = this.getPackageName() + "."
				+ this.getLocalClassName();
		ComponentName comp = new ComponentName(this.getPackageName(), appClass);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
				Intent.ACTION_MAIN).setComponent(comp));
		sendBroadcast(shortcut);
	}

	private boolean hasShortcut() {
		boolean isInstallShortcut = false;
		final ContentResolver cr = PreparePage.this.getContentResolver();
		final String AUTHORITY = "com.android.launcher.settings";
		final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/favorites?notify=true");
		Cursor c = cr.query(CONTENT_URI,
				new String[] { "title", "iconResource" }, "title=?",
				new String[] { PreparePage.this.getString(R.string.app_name)
						.trim() }, null);
		if (c != null && c.getCount() > 0) {
			isInstallShortcut = true;
		}
		return isInstallShortcut;
	}
}
