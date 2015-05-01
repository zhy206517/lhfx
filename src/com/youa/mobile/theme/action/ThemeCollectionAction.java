package com.youa.mobile.theme.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.friend.FriendFeedActivity;
import com.youa.mobile.theme.data.PopThemeInfo;
import com.youa.mobile.theme.manager.HomeManager;
import com.youa.mobile.theme.action.ThemeCollectionAction.ISearchResultListener;

public class ThemeCollectionAction extends BaseAction<ISearchResultListener> {
	public static final String PARAM_DELETE = "isDelete";
	public static final String PARAM_THEME_ID = "themeId";

	public interface ISearchResultListener extends IResultListener,
			IFailListener {
		public void onStart();

		public void onEnd(List<PopThemeInfo> homeDataList);

		public void onEnd(String themeId,boolean isDelete);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISearchResultListener resultListener) throws Exception {
		boolean isDelete = (Boolean) params.get(PARAM_DELETE);
		if (isDelete) {
			String themeId = (String) params.get(PARAM_THEME_ID);
			boolean isD = new HomeManager().deleteThemeCollection(context,
					themeId);
			resultListener.onEnd(themeId,isD);
		} else {
			List<PopThemeInfo> popDataList = new HomeManager()
					.requestThemeCollectionList(context, 0,
							FriendFeedActivity.REQUEST_COUNT);
//			popDataList = getData();
			resultListener.onEnd(popDataList);
		}
	}

	private List<PopThemeInfo> getData() {
		List<PopThemeInfo> list = new ArrayList<PopThemeInfo>();
		PopThemeInfo info = null;
		for (int i = 0; i < 20; i++) {
			info = new PopThemeInfo();
			if (i % 2 == 0) {
				info.name = "宁浩亮";
				info.sUid = "1";
			} else {
				info.name = "张宇";
				info.sUid = "2";
			}
			list.add(info);
		}
		return list;
	}

}
