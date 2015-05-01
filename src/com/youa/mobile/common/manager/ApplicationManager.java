package com.youa.mobile.common.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.YoumentEvent;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BaseManager;
import com.youa.mobile.common.db.ConnectManager;
import com.youa.mobile.common.util.pinyin.Pinyin;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.input.PublishFeedStore;
import com.youa.mobile.input.PublishPage;
import com.youa.mobile.input.action.RequestPublishAciton;
import com.youa.mobile.input.data.PublishData;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.login.auth.BaseToken;
import com.youa.mobile.login.auth.ShareUtil;
import com.youa.mobile.login.auth.ShareUtil.ShareToCallBack;
import com.youa.mobile.login.auth.SupportSite;

public class ApplicationManager extends BaseManager {
	private final static String TAG = "ApplicationManager";
	protected int mIconSuccResId = R.drawable.toast_succ;
	protected int mIconFailResId = R.drawable.toast_fail;

	private ApplicationManager() {

	}

	private String mLoginName;
	private String mPassword;
	private String mVersionName;
	private int mVersionCode;
	private float mDensity;
	private int mWidth;
	private int mHeight;
	private Context appContext;
	private int mDensityDpi;
	private ConnectivityManager connectivityManager;
	public List<PublishData> publishDataList = null;
	public static final ApplicationManager mApplicationManager = new ApplicationManager();
	private Handler mHandler;

	public static ApplicationManager getInstance() {
		return mApplicationManager;
	}

	// private static boolean isInit = false;

	/**
	 * 登录时初期话.
	 * 
	 * @param context
	 */
	public void init(Activity context) {
		if (publishDataList == null) {
			publishDataList = Collections
					.synchronizedList(new ArrayList<PublishData>());
			if (PublishFeedStore.getInstance().loadPublish(context) != null) {
				publishDataList = PublishFeedStore.getInstance().loadPublish(
						context);
			}

		}
		if (mWidth == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			context.getWindowManager().getDefaultDisplay().getMetrics(dm);
			mDensity = dm.density;
			mWidth = dm.widthPixels;
			mHeight = dm.heightPixels;
			mDensityDpi = dm.densityDpi;
			if (mWidth > mHeight) {
				int temp = mHeight;
				mHeight = mWidth;
				mWidth = temp;
			}
		}
		initPri(context);
		connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		mHandler = new Handler();
	}

	public boolean checkWifi() {
		boolean isWifiConnect = true;
		NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
		for (int i = 0; i < networkInfos.length; i++) {
			if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
				if (networkInfos[i].getType() == connectivityManager.TYPE_MOBILE) {
					isWifiConnect = false;
				}
				if (networkInfos[i].getType() == connectivityManager.TYPE_WIFI) {
					isWifiConnect = true;
				}
			}
		}
		return isWifiConnect;
	}

	private void initPri(Context context) {
		if (mVersionName == null) {
			PackageInfo info;
			try {
				info = context.getPackageManager().getPackageInfo(
						context.getPackageName(), 0);
			} catch (NameNotFoundException e) {
				throw new RuntimeException(e);
			}
			mVersionName = info.versionName;
			mVersionCode = info.versionCode;
			appContext = context.getApplicationContext();
			try {
				Log.i("ApplicationManager", "Pinyin.init(context);");
				Pinyin.init(context);
			} catch (IOException e) {
				e.printStackTrace();
			}
			ConnectManager.initDBConnector(context);
			ConnectManager.setDatabaseName("database");
			SavePathManager.initFolder();
		}
		// if (mPassword == null || "".equals(mPassword)) {
		// SharedPreferences sharedPreferences = context.getSharedPreferences(
		// SystemConfig.XML_FILE_SYSTEM_CONFIG, 0);
		// // mLoginName =
		// // sharedPreferences.getString(SystemConfig.KEY_LOGIN_NAME,
		// // "");
		// mLoginName = "";
		// mPassword = sharedPreferences.getString(
		// SystemConfig.KEY_LOGIN_PASS, "");
		// }
	}

	public void init(Context context) {
		initPri(context);
	}

	public String getVersionName() {
		return mVersionName;
	}

	public int getVersionCode() {
		return mVersionCode;
	}

	public int getHttpProtocolVersion() {
		return 3;
	}

	public String getLoginUserName() {
		return mLoginName;
	}

	public String getLoginUserPassword() {
		return mPassword;
	}

	public float getDensity() {
		return mDensity;
	}

	public int getWidth() {
		return mWidth;
	}

	public int getHeight() {
		return mHeight;
	}

	public int getDensityDpi() {
		return mDensityDpi;
	}

	public String getUserId() {
		return LoginCommonDataStore.getData(appContext,
				LoginCommonDataStore.KEY_USERID, mLoginName);
	}
	
	public String getNickName(){
		return LoginCommonDataStore.getData(appContext,
				LoginCommonDataStore.KEY_NICKNAME, mLoginName);
	}

	public boolean isLogin() {
		return !TextUtils.isEmpty(getUserId());
	}

	public void logout(Context context) {
		LoginCommonDataStore.logout(context, LoginCommonDataStore.KEY_USERID,
				mLoginName);
		PublishFeedStore.getInstance().clearData(context);
		// mLoginName = null;
		mPassword = null;
	}

	public void send(final PublishData data) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PublishPage.KEY_PARAMS_ID, data.getUserId());
		String strContent = data.getContent();
		if (TextUtils.isEmpty(strContent)) {
			// params.put(KEY_PARAMS_CONTENT, "@爱乐活随行");
		} else {
			params.put(PublishPage.KEY_PARAMS_CONTENT, strContent);// +"@爱乐活随行"
		}
		params.put(PublishPage.KEY_PARAMS_IMAGE, data.getContentImage());
		params.put(PublishPage.KEY_PARAMS_PLACE, data.getConsumePlace());
		params.put(PublishPage.KEY_PARAMS_PRICE, data.getConsumePrice());
		params.put(PublishPage.KEY_PARAMS_LATITUDE, data.getLatitude());
		params.put(PublishPage.KEY_PARAMS_LONGITUDE, data.getLongitude());
		params.put(PublishPage.KEY_PARAMS_MANY_PEOPLE, data.isManyPeople());
		params.put(PublishPage.KEY_PARAMS_PLID, data.getPlid());
		List<BaseToken> tokens = data.getTokens();
		boolean hasSync = false;
		if(tokens != null){
			for(BaseToken token : tokens){
				if(token != null && token.isBinded(appContext) && !token.isExpired(appContext) && token.isSync){
					hasSync = true;
					break;
				}
			}
		}
		params.put(PublishPage.KEY_PARAMS_SHARED, hasSync);
		final boolean isSync = hasSync;
		ActionController.post(appContext, RequestPublishAciton.class, params,	
				new RequestPublishAciton.IPublishResultListener() {
					@Override
					public void onFinish(final HomeData feed) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast toast = Toast.makeText(
										appContext,
										appContext
												.getString(R.string.input_send_succ),
										Toast.LENGTH_SHORT);
								LinearLayout toastView = (LinearLayout) toast
										.getView();
								toastView.setGravity(Gravity.CENTER);
								toastView
										.setOrientation(LinearLayout.HORIZONTAL);
								ImageView iconView = new ImageView(appContext);
								iconView.setImageResource(mIconSuccResId);
								toastView.addView(iconView, 0);
								toast.show();
								if(isSync && feed != null){
									ShareUtil.shareToThird(appContext, feed, data, new ShareToCallBack() {
										@Override
										public void onStart() {
											showTost(appContext.getString(R.string.sns_shareding));
										}

										@Override
										public void onSuccess(SupportSite site) {
											String msg = appContext.getString(R.string.share_sns_success, site.getSiteName(appContext));
											showTost(msg);
										}

										@Override
										public void onFail(SupportSite site, final String msg) {
											String msgStr = appContext.getString(R.string.share_sns_fail_msg, site.getSiteName(appContext), msg);
											showTost(msgStr);
										}
										private void showTost(final String msg){
											mHandler.post(new Runnable() {
												@Override
												public void run() {
													Handler mHander = new Handler();
													mHander.post(new Runnable() {
														@Override
														public void run() {
															Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show();
														}
													});
												}
											});
										}
									});
								}
									
							}
						});
						if (publishDataList != null
								&& publishDataList.size() > 0) {
							publishDataList.remove(data);
						}
						appContext.sendBroadcast(new Intent(
								LehuoIntent.ACTION_FEED_PUBLISH_OK));
					}

					@Override
					public void onStart() {
						// 添加友盟统计(发布)>>> begin
						String label = YoumentEvent.EVENT_SENDLABEL_NORMAL;
						boolean isHasPrice = false;
						boolean isHasImage = false;
						if (!InputUtil.isEmpty(data.getConsumePrice())) {
							isHasPrice = true;
							label = YoumentEvent.EVENT_SENDLABEL_PRICE;
						}
						if (data.getContentImage().size() > 0) {
							isHasImage = true;
							if (isHasPrice) {
								label = YoumentEvent.EVENT_SENDLABEL_PIC_AND_PRICE;
							} else {
								label = YoumentEvent.EVENT_SENDLABEL_PIC;
							}
						}
						if (!isHasImage && !isHasPrice) {
							label = YoumentEvent.EVENT_SENDLABEL_NORMAL;
						}
						MobclickAgent.onEvent(appContext,
								YoumentEvent.EVENT_SEND, label);
						Intent i = new Intent(
								LehuoIntent.ACTION_FEED_PUBLISH_REFRESH);
						if (publishDataList != null
								&& publishDataList.size() >= 0
								&& !publishDataList.contains(data)) {
							if (publishDataList.size() > 10) {
								publishDataList.clear();
							}
							publishDataList.add(0, data);
						}
						appContext.sendBroadcast(i);
					}

					@Override
					public void onFail(final int resourceID) {
						MobclickAgent.onEvent(appContext,
								YoumentEvent.EVENT_SEND_FAIL);
						data.setPublishState(false);
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast toast = Toast.makeText(
										appContext,
										appContext
												.getString(R.string.input_send_fail),
										Toast.LENGTH_SHORT);
								LinearLayout toastView = (LinearLayout) toast
										.getView();
								toastView.setGravity(Gravity.CENTER);
								toastView
										.setOrientation(LinearLayout.HORIZONTAL);
								ImageView iconView = new ImageView(appContext);
								iconView.setImageResource(mIconFailResId);
								toastView.addView(iconView, 0);
								toast.show();
							}
						});
						appContext.sendBroadcast(new Intent(
								LehuoIntent.ACTION_FEED_PUBLISH_REFRESH));
					}
				}, true);
	}
}
