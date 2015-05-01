package com.youa.mobile.content.manager;

import java.util.ArrayList;

import com.youa.mobile.content.provider.HistoryFeedProvider;
import com.youa.mobile.friend.data.HomeData;

public class HistoryFeedManager {

	private HistoryFeedProvider mProvider;

	public HistoryFeedManager() {
		mProvider = new HistoryFeedProvider();
	}

	public void addToHistory(HomeData data) {
		mProvider.insertHistory(data);
	}

	/**
	 * @param currPage
	 *            current page index, pass a int values <=0 to get all records.
	 * @return
	 */
	public ArrayList<HomeData> getHistory(int currPage) {
		return mProvider.getHistory(currPage);
	}

	public ArrayList<HomeData> getHistory() {
		return mProvider.getHistory();
	}

	// public ArrayList<HomeData> getHistoryByPage(int currPage) {
	// return mProvider.getHistoryByPage(currPage);
	// }
}
