package com.youa.mobile;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKGeocoderAddressComponent;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.youa.mobile.common.manager.ApplicationManager;

public class LehoApp extends Application {
	private final static String TAG = "LehoApp";
	public BMapManager mBMapMan = null;
	public String mStrKey = "FC65B1563D9F4619FAA068370C66EC662D370FB2";
	public boolean isStartLoc = true;
	// LocSDk
	// public MyLocationListenner myListener = new MyLocationListenner();
	// search city
	public String locationName = null;
	MKSearch mSearch = null;
	LocationListener mLocationListener = null;
	private Timer LocationTimer = null;
	private TimerTask requestLocationTask;
	public static boolean isScreenOn = true;
	public boolean isMapActivityOpen = false;
	public boolean isRequestLocation = false;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				startLocation();
			}
			super.handleMessage(msg);
		}
	};

	public static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
			}
		}
	}

	private void initSearch() {
		if (mBMapMan == null) {
			initMap();
		}
		mBMapMan.start();
		mSearch = new MKSearch();
		mSearch.init(mBMapMan, new MKSearchListener() {
			public void onGetPoiResult(MKPoiResult res, int type, int error) {
			}

			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
				if (error == 0) {
					MKGeocoderAddressComponent component = res.addressComponents;
					if (!TextUtils.isEmpty(component.city)) {
						SharedPreferences sp = LehoApp.this
								.getSharedPreferences(
										SystemConfig.XML_FILE_LOCATION_GUIDE,
										Context.MODE_WORLD_READABLE);
						SharedPreferences.Editor edit = sp.edit();
						edit.putString(SystemConfig.KEY_CITY_NAME,
								component.city);
//						String strInfo = component.city == null ? ""
//								: component.city + component.district == null ? ""
//										: component.district + component.street == null ? ""
//												: component.street;
						locationName = !TextUtils.isEmpty(component.street) ? component.street
								: res.strAddr;
						edit.putString(SystemConfig.KEY_LOCATION_NAME, locationName);
						edit.commit();
						sendBroadcast(new Intent(LehuoIntent.LOCATION_SUCCESS));
					}
				}
			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onCreate() {
		SharedPreferences preference = this.getSharedPreferences(
				SystemConfig.XML_FILE_LOCATION_GUIDE,
				Context.MODE_WORLD_READABLE);
		isStartLoc = preference.getBoolean("is", true);
		super.onCreate();
	}

	public void initMap() {
		Log.d(TAG, "initMap");
		// if (!isStartLoc) {
		// return;
		// }
		if (mBMapMan == null) {
			mBMapMan = new BMapManager(this);
			mBMapMan.init(this.mStrKey, new MyGeneralListener());
			if (mLocationListener == null) {
				initLoatioonListener();
			}
			if (mSearch == null) {
				initSearch();
			}
		}
	}

	public void startLocation() {
		if (!isStartLoc) {
			return;
		}
		if (isMapActivityOpen) {
			return;
		}
		if (!isAppOnForeground()) {
			stopLocation();
			return;
		}
		Log.d(TAG, "startLocation");
		if (mBMapMan != null) {
			stopLocation();
			mBMapMan.getLocationManager().requestLocationUpdates(
					mLocationListener);
			mBMapMan.start();
		}

	}

	public void requestLocation() {
		if (!isStartLoc) {
			return;
		}
		Log.d(TAG, "requestLocation");
		if (mBMapMan != null) {
			SharedPreferences sp = getSharedPreferences(
					SystemConfig.XML_FILE_LOCATION_GUIDE,
					Context.MODE_WORLD_READABLE);
			if (sp.getLong(SystemConfig.KEY_TIME_TMP, 0) == 0) {
				return;
			}
			long timeTmp = System.currentTimeMillis()
					- sp.getLong(SystemConfig.KEY_TIME_TMP, 0);
			Log.d(TAG, "timeTmp:" + timeTmp);
			// 超出一分钟再进行定位
			if (timeTmp > 0 && timeTmp > 60000) {
				stopLocation();
				mBMapMan.getLocationManager().requestLocationUpdates(
						mLocationListener);
				mBMapMan.start();
				isRequestLocation = true;
			}
		}
	}

	public void requestStopLocation() {
		if (!isStartLoc) {
			return;
		}
		Log.d(TAG, "requestStopLocation");
		if (isRequestLocation) {
			stopLocation();
			isRequestLocation = false;
		}
	}

	public void stopLocation() {
		if (!isStartLoc) {
			return;
		}
		Log.d(TAG, "stopLocation");
		if (mBMapMan != null) {
			//mBMapMan.getLocationManager().removeUpdates(mLocationListener);
			//mBMapMan.stop();
		}
	}

	private void initLoatioonListener() {
		if (!isStartLoc) {
			return;
		}
		Log.d(TAG, "initLoatioonListener");
		mLocationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				if (location != null) {
					mSearch.reverseGeocode(new GeoPoint((int) (location
							.getLatitude() * 1e6), (int) (location
							.getLongitude() * 1e6)));
					SharedPreferences sp = getSharedPreferences(
							SystemConfig.XML_FILE_LOCATION_GUIDE,
							Context.MODE_WORLD_READABLE);
					SharedPreferences.Editor edit = sp.edit();
					edit.putLong(SystemConfig.KEY_TIME_TMP,
							System.currentTimeMillis());
					edit.putString(SystemConfig.KEY_PLACE_X, String
							.valueOf((int) (location.getLongitude() * 1e6)));
					edit.putString(SystemConfig.KEY_PLACE_Y, String
							.valueOf((int) (location.getLatitude() * 1e6)));
					if (DebugMode.debug) {
						Log.d(TAG, "getting location sucess");
						Log.d(TAG,
								String.valueOf((int) (location.getLatitude() * 1e6)));
						Log.d(TAG, String.valueOf((int) (location
								.getLongitude() * 1e6)));
					}
					edit.commit();
					stopLocation();
				}
			}
		};

	}

	public void startLocationTask(long delay) {
		if (!isStartLoc) {
			return;
		}
		Log.d(TAG, "startLocationTask");
		requestLocationTask = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		};
		LocationTimer = new Timer();
		LocationTimer.scheduleAtFixedRate(requestLocationTask, delay, 60000);
	}

	private boolean isAppOnForeground() {
		if (!isScreenOn) {
			return false;
		}
		ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = this.getPackageName();
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}

	public void destroyMap() {
		if (requestLocationTask != null) {
			requestLocationTask.cancel();
		}
		if (LocationTimer != null) {
			LocationTimer.purge();
			LocationTimer.cancel();
			LocationTimer = null;
		}
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
			mSearch = null;
		}
	}
}
