package com.youa.mobile.information.action;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.information.manager.PersonalInfoManager;
import com.youa.mobile.information.action.SearchEnjoyFeedAction.SearchEnjoyFeedResult;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.friend.data.HomeData;

public class SearchEnjoyFeedAction extends BaseAction <SearchEnjoyFeedResult>{
	
	public static final String PARAM_UID = "uid";
	public static final String PARAM_PAGEINDEX = "pageindex";
	public static final String PARAM_REFRESH_OR_MORE = "isrefresh";
	
	public interface SearchEnjoyFeedResult extends IResultListener, IFailListener {
		public void onStart();
		public void onEnd(List<HomeData> feedData, int pageIndex);
	}

	@Override
	protected void onExecute(
			Context context, 
			Map<String, Object> params,
			SearchEnjoyFeedResult resultListener) throws Exception {
		resultListener.onStart();
		boolean isrefresh = (Boolean) params.get(PARAM_REFRESH_OR_MORE);
		int lastPageIndex = (Integer) params.get(PARAM_PAGEINDEX);
		String uid = (String) params.get(PARAM_UID);
			
		int currentPageIndex = 0;
		if (isrefresh) {
			lastPageIndex = 1;
			currentPageIndex = 1;
		} else {
			currentPageIndex = lastPageIndex + 1;
		}
		List<HomeData> feedList = new PersonalInfoManager()
				.searchEnjoyFeedList(
						context,
						uid,
						currentPageIndex);	//发出联网处理
		resultListener.onEnd(feedList, currentPageIndex);
		
	}
}
