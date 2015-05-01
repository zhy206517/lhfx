package com.youa.mobile.location;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.location.LocationAction.ILocationActionResultListener;
import com.youa.mobile.location.data.LocationData;
import com.youa.mobile.location.manager.LocationManager;
import com.youa.mobile.location.manager.SuggestPlaceMananer;

public class LocationAction extends BaseAction<ILocationActionResultListener> {

	public interface ILocationActionResultListener extends IResultListener,
			IFailListener {
		@Override
		public void onFail(int resourceID);

		public void onStart(Integer resourceID);

		public void onGetAllFinish(List<LocationData> topicList);

		public void onFinish(int resourceID, int position,
				boolean topicSubStatus);
		// public void onEnd(int position);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ILocationActionResultListener resultListener) throws Exception {
		resultListener.onStart(R.string.topic_loading);
		List<LocationData> locList = null;
		String searchType = (String) params.get(MapPage.SEARCH_TYPE);
		try {
			if (MapPage.SEARCH_ALL.equals(searchType)) {
				locList = new LocationManager().requestLocationList(context,
						params);
			} else if (MapPage.SEARCH_BY_KEYWORD.equals(searchType)) {
				locList = new LocationManager().requestSearchList(context,
						params);
			} else if (MapPage.SEARCH_FROM_DB.equals(searchType)) {
				locList = new SuggestPlaceMananer().searchLocList();
			}
		} catch (MessageException e) {
			e.setResID(R.string.location_load_error);
			resultListener.onFail(R.string.location_load_error);
		}
		resultListener.onGetAllFinish(locList);

	}
}
