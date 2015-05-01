package com.youa.mobile.common.manager;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.youa.mobile.R;


public class PickPosition {
	private Timer closeGps;
	private TimerTask closeGpsTask;
	private TimerTask closeNetworkTask;
	private boolean canceled = false;
	public static final PickPosition mPickPosition = new PickPosition();
	public static PickPosition getInstance() {
		return mPickPosition;
	}
	public interface PickPositionListener
	{   
		public static final int fail_error = R.string.common_getgps_fail;
		public static final int fail_cancel = R.string.common_getgps_cancel;
		public void onStart(); 
		public void onPositionPicked(double longi, double lati, long time);
		public void onFailed(int season);
	}
	public void cancel()
	{
		locationM.removeUpdates(locationListener);
		try
		{
			closeGps.cancel();
		}catch(Exception e){}
		canceled = true;
	}
	LocationManager locationM;
	LocationListener locationListener;
	public void doPickPosition(Context appCtx, final PickPositionListener callback) {
		canceled = false;
		locationM =  (LocationManager) appCtx.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {

			public void onProviderDisabled(String provider) {
				// Provider被disable时触发此函数，比如GPS被关闭
				//Log.d("record", "gps disabled");
				locationM.removeUpdates(this);
				if(closeGps!=null)
				{
					closeGps.cancel();
					closeGps = null;
				}
			}

			public void onProviderEnabled(String provider) {
				// Provider被enable时触发此函数，比如GPS被打开
			}

			public void onStatusChanged(String provider,
					int status, Bundle extras) {
				//Log.d("record", "gps state "+status);
				// Provider的转态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
			}

			@Override
			public void onLocationChanged(Location location) {
				//Log.d("record", "gps data arrived");
				callback.onPositionPicked(location.getLatitude(),location.getLongitude(),location.getTime());
				if(closeGps!=null)
				{
					closeGps.cancel();
					closeGps = null;
				}
				locationM.removeUpdates(this);
			}
		};
		final Handler startNetwork = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				locationM.removeUpdates(locationListener);
				closeGps = new Timer();
				closeNetworkTask = new TimerTask() {
					
					@Override
					public void run() {
						Location m_local = null;
						m_local = locationM.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if(m_local == null)
						{
							//Log.d("record", "local info not available");
							callback.onFailed(PickPositionListener.fail_error);
						}else
						{
							//Log.d("record", new Date(m_local.getTime()).toLocaleString()+" network data");
							callback.onPositionPicked(m_local.getLatitude(),m_local.getLongitude(),m_local.getTime());				
						}
						locationM.removeUpdates(locationListener);

					}
				}; 
				closeGps.schedule(closeNetworkTask, 10000);
				locationM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
						 0, 0, locationListener);							
				super.handleMessage(msg);
			}
		};
		closeGps = new Timer();
		
		if(locationM.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			locationM.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					 0, 0, locationListener);	
			closeGpsTask = new TimerTask(){
				@Override
				public void run() {
					Location m_local = null;
					m_local = locationM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//					if(m_local!=null)
//						Log.d("record", new Date(m_local.getTime()).toLocaleString()+" gps data");
					if(m_local == null && locationM.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
					{
//						callback.onToast(R.string.switch_to_network);
						startNetwork.sendEmptyMessage(0);
						return;
					}
					if(m_local == null)
					{
						//Log.d("record", "local info not available");
						callback.onFailed(PickPositionListener.fail_error);
					}else
					{
						callback.onPositionPicked(m_local.getLatitude(),m_local.getLongitude(),m_local.getTime());		
					}
					locationM.removeUpdates(locationListener);
				}
				
			};
			closeGps.schedule(closeGpsTask, 15000);
		}				
		else if(locationM.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{
//			callback.onToast(R.string.switch_to_network);
			locationM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					 0, 0, locationListener);					
			closeNetworkTask = new TimerTask() {
				
				@Override
				public void run() {
					Location m_local = null;
					m_local = locationM.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					if(m_local == null)
					{
						//Log.d("record", "local info not available");
						callback.onFailed(PickPositionListener.fail_error);
					}else
					{
						//Log.d("record", new Date(m_local.getTime()).toLocaleString()+" network data");
						callback.onPositionPicked(m_local.getLatitude(),m_local.getLongitude(),m_local.getTime());		
					}
					locationM.removeUpdates(locationListener);

				}
			}; 
			closeGps.schedule(closeNetworkTask, 10000);
		}else
		{
			callback.onFailed(PickPositionListener.fail_error);
		}
	}

}
