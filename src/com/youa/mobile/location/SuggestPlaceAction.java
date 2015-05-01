package com.youa.mobile.location;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;

import com.youa.mobile.location.data.LocationData;

import com.youa.mobile.location.manager.SuggestPlaceMananer;
import com.youa.mobile.location.SuggestPlaceAction.ISuggestLocationActionResultListener;

public class SuggestPlaceAction extends
		BaseAction<ISuggestLocationActionResultListener> {
	public interface ISuggestLocationActionResultListener extends
			IResultListener, IFailListener {
		public void onStart();

		public void onFinish(List<LocationData> locList);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISuggestLocationActionResultListener resultListener)
			throws Exception {
		int x = (Integer) params.get(MapPage.KEY_LAT);
		int y = (Integer) params.get(MapPage.KEY_LON);
		String placeName = (String) params.get(MapPage.KEY_PlACE_NAME);
		String pid = (String) params.get(MapPage.KEY_PID);
		String address = (String) params.get(MapPage.KEY_PlACE_ADDRESS);
		String type = (String) params.get(MapPage.KEY_PlACE_TYPE);
		new SuggestPlaceMananer()
				.insertLoc(x, y, placeName, pid, address, type);
	}
}
