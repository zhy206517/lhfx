package com.youa.mobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.data.UserData;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.params.ServerAddressUtil;
import com.youa.mobile.information.action.DownLoadFriendAction;
import com.youa.mobile.more.action.JptjAction;
import com.youa.mobile.news.action.RequestGetNewNewsCountAction;

public class UpdateService extends Service {
	public static final String TAG = "UpdateService";
	public static final long TIME_MINUTE = 60 * 1000;
	public static final long TIME_HOUR = 60 * TIME_MINUTE;
	public static final long TIME_DAY = 24 * TIME_HOUR;
	public static final String INTENT_NEW_NEWS_COUNT_CHANGE = "intent.news.new_news_count_change";
	public static final String PARAM_TYPE = "param_type";
	public static final String TYPE_REQUESTNEWSCOUNT = "requestnewscount";
	public static final String TYPE_REQUESTFRIEND = "requestfriend";
	public static final String TYPE_STARTCLIENT = "startClient";
	private boolean isHasDownload = false;
	// private String mUid = "";
	private Timer timer = new Timer();
	private TimerTask requestFriendTask;
	ExitClentReceiver mExitClentReceiver = new ExitClentReceiver();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class ExitClentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (LehuoIntent.ACTION_EXIT_CLIENT.equals(intent.getAction())) {
				ApplicationManager.getInstance().logout(UpdateService.this);
				isHasDownload = false;
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		ApplicationManager.getInstance().init(this);// service 和activity不是一个进程
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(LehuoIntent.ACTION_EXIT_CLIENT);
		registerReceiver(mExitClentReceiver, mFilter);
		startNewNewsUpdateService();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mExitClentReceiver);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(TAG, "onStart");
		if (intent == null) {
			return;
		}
		String type = intent.getStringExtra(PARAM_TYPE);
		if (type == null) {
			return;
		}
		requestNewNewsCount();
		if (type.equals(TYPE_STARTCLIENT)) {
			if (TextUtils.isEmpty(ApplicationManager.getInstance().getUserId())) {
				ApplicationManager.getInstance().init(this);
			}
			if (!isHasDownload) {
				isHasDownload = true;
				Log.d(TAG, "downloadFriend");
				downloadFriend();
			}
		} else if (type.equals(TYPE_REQUESTFRIEND)) {
			downloadFriend();
		}
		rquestJptj();
	}

	private void rquestJptj() {
		// if (TextUtils.isEmpty(ApplicationManager.getInstance().getUserId()))
		// {
		// return;
		// }
		// Map<String, Object> params = new HashMap<String, Object>();
		// params.put(JptjAction.KEY_JPTJ_URL, ServerAddressUtil.JPTJ_SERVER +
		// JptjAction.URL_SUBFIX);
		// ActionController.post(getApplicationContext(), JptjAction.class,
		// params, null);
	}

	private void downloadFriend() {
		if (TextUtils.isEmpty(ApplicationManager.getInstance().getUserId())) {
			return;
		}
		ActionController.post(getApplicationContext(), DownLoadFriendAction.class, null, new DownLoadFriendAction.DownLoadFriendListener() {
			public void onFail(int resourceID) {
				startNextDownloadTask(TIME_MINUTE * 10);
			}

			@Override
			public void onFinish(List<UserData> dataList) {
				startNextDownloadTask(TIME_DAY);
			}

			@Override
			public void onStart() {
			}
		}, true);
	}

	private void startNextDownloadTask(long delay) {
		if (requestFriendTask != null) {
			requestFriendTask.cancel();
			timer.purge();
		}
		requestFriendTask = new TimerTask() {
			public void run() {
				downloadFriend();
			}
		};
		timer.schedule(requestFriendTask, delay);
	}

	private void startNewNewsUpdateService() {
		TimerTask requestNewsCountTask = new TimerTask() {
			public void run() {
				requestNewNewsCount();
			}
		};
		timer.scheduleAtFixedRate(requestNewsCountTask, TIME_DAY / 3, TIME_DAY / 3);
	}

	private void sendNewNewsCountChangeBro() {
		Intent intent = new Intent(INTENT_NEW_NEWS_COUNT_CHANGE);
		sendBroadcast(intent);
	}

	private void requestNewNewsCount() {
		if (ApplicationManager.getInstance().getUserId() != null) {
			ActionController.post(getApplicationContext(), RequestGetNewNewsCountAction.class, null,
					new RequestGetNewNewsCountAction.INewNewsCountResultListener() {
						public void onFinish(boolean isChanged) {
							if (isChanged) {
								sendNewNewsCountChangeBro();
							}
						}

						public void onFail(int resourceID) {
						}
					}, true);
		}
	}

}
