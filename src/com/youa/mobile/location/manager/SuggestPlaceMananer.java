package com.youa.mobile.location.manager;

import java.util.List;

import com.youa.mobile.location.data.LocationData;
import com.youa.mobile.location.provider.SuggestPlaceProvider;

public class SuggestPlaceMananer {
	private SuggestPlaceProvider mSuggestPlaceProvider;
	public SuggestPlaceProvider getSuggestPlaceProvider() {
		if (mSuggestPlaceProvider == null) {
			mSuggestPlaceProvider = new SuggestPlaceProvider();
		}
		return mSuggestPlaceProvider;
	}
	public List<LocationData> searchLocList() {
		List<LocationData> dataList = getSuggestPlaceProvider().searchLocList();
		return dataList;
	}
	public void insertLoc(int x, int y, String placeName, String pid,String address,String type) {
		LocationData locationData = new LocationData(x, y, placeName, pid,address,type);
		getSuggestPlaceProvider().replaceInformation(locationData);
	}
}
