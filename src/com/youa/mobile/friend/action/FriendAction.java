package com.youa.mobile.friend.action;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.DebugMode;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.friend.action.FriendAction.ISearchResultListener;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.manager.HomeManager;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;

public class FriendAction extends BaseAction<ISearchResultListener> {
	// public static final String PARAM_PAGE_INDEX = "currentPageIndex";
	// 发出联网处理
	final public static int PARAM_REFRESH = 1, PARAM_PAGE = 2, PARAM_KEEP = 3;
	public static final String PARAM_TYPE = "type";
	// -------------------------------------------
	private int mLastPageIndex;
	public static final String PARAM_ENDPOSTID = "endpostid";

	public interface ISearchResultListener extends IResultListener,
			IFailListener {
		public void onStart();

		public void onEnd(List<HomeData> homeDataList, int type);
	}
	
	public static final String PARAM_MINPOST_ID = "minpostid";
	public static final String PARAM_MAXPOST_ID = "maxpostid";
	

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISearchResultListener resultListener) throws Exception {
		if (DebugMode.debug) {
			System.out.println("<FriendAction onExecute resultListener:>"
					+ resultListener);
			System.out.println("<FriendAction onExecute params:>" + params);
		}
		resultListener.onStart();
		int type = (Integer) params.get(PARAM_TYPE);
		String minPostId = (String) params.get(PARAM_MINPOST_ID);
		String maxPostId = (String) params.get(PARAM_MAXPOST_ID);
		int currentPageIndex = 0;
		switch (type) {
		case PARAM_REFRESH:
			minPostId = maxPostId;
			maxPostId = "-1";
			break;
		case PARAM_PAGE:
			maxPostId = minPostId;
			minPostId = "-1";
			// ---------db
			// maxPostId = minPostId;
			// minPostId=数据库中最新的一条数据
			break;
		case PARAM_KEEP:
//			resultListener.onEnd(FriendFeedActivity.saveHomeDataList, type);
			return;
		}
		List<HomeData> list = new HomeManager().searchFriendDynamicList(
				context, minPostId, maxPostId); // 发出联网处理
		resultListener.onEnd(list, type);
		mLastPageIndex = currentPageIndex;
	}


}
