package com.youa.mobile.circum.action;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.circum.action.CircumAction.ISearchResultListener;
import com.youa.mobile.circum.manager.HomeManager;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.friend.data.HomeData;

public class CircumAction extends BaseAction<ISearchResultListener> {
	public static final String PARAM_PLACE_X = "placeX";
	public static final String PARAM_PLACE_Y = "placeY";
	public static final String PARAM_PLACE_ID = "placeId";
	public static final String PARAM_PLACE_NUM = "placeNumber";
	public static final String PARAM_START_POS = "start_pos";
	public static final int page_num=30;

	public interface ISearchResultListener extends IResultListener,
			IFailListener {
		public void onStart();
//        public void onFail(int resourceID);
		public void onEnd(List<HomeData> homeList);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISearchResultListener resultListener) throws Exception {
		String place_x = (String) params.get(PARAM_PLACE_X);
		String place_y = (String) params.get(PARAM_PLACE_Y);
		String id = (String) params.get(PARAM_PLACE_ID);
		int start_pos = (Integer) params.get(PARAM_START_POS);
		List<HomeData> popDataList= new HomeManager()
			.requestCircumDynamicList(context, id,place_x, place_y, page_num, start_pos*page_num);
		resultListener.onEnd(popDataList);
	}

}
