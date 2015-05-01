package com.youa.mobile.location.manager;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.location.data.LocationData;

public class LocationManager {
	private LocationHttpManager mLocationManager = null;

	private LocationHttpManager getLocRequestManager() {
		if (mLocationManager == null) {
			mLocationManager = new LocationHttpManager();
		}
		return mLocationManager;
	}

	public List<LocationData> requestLocationList(Context context, Map<String, Object> para)
			throws MessageException {
		return getLocRequestManager().requestLocationList(context, para);
	}
	public List<LocationData> requestSearchList(Context context, Map<String, Object> para)
			throws MessageException {
		return getLocRequestManager().requestSearchList(context, para);
	}
}
