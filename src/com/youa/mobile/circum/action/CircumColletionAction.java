package com.youa.mobile.circum.action;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.circum.action.CircumColletionAction.ISearchResultListener;
import com.youa.mobile.circum.data.PopCircumData;
import com.youa.mobile.circum.manager.HomeManager;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.friend.FriendFeedActivity;

public class CircumColletionAction extends BaseAction<ISearchResultListener> {
	public static final String PARAM_DELETE = "isDelete";
	public static final String PARAM_CIRCUM_ID = "circumId";
	public interface ISearchResultListener extends IResultListener,
			IFailListener {
		public void onStart();
		public void onEnd(String ccircumId,boolean isDelete);
		public void onEnd(List<PopCircumData> circumList);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISearchResultListener resultListener) throws Exception {
		boolean isDelete = (Boolean) params.get(PARAM_DELETE);
		if (isDelete) {
			String circumId = (String) params.get(PARAM_CIRCUM_ID);
			boolean isD = new HomeManager().deleteCircumCollection(context,
					circumId);
			resultListener.onEnd(circumId,isD);
		} else {
			List<PopCircumData> data = new HomeManager()
			.requestCircumCollectionList(context, 0,
					FriendFeedActivity.REQUEST_COUNT);
	        resultListener.onEnd(data);
		}
		
	}

}
