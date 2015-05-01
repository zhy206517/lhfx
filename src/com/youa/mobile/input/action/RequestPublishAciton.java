package com.youa.mobile.input.action;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.input.PublishPage;
import com.youa.mobile.input.action.RequestPublishAciton.IPublishResultListener;
import com.youa.mobile.input.manager.PublishManager;
import com.youa.mobile.input.util.InputUtil;

public class RequestPublishAciton extends BaseAction<IPublishResultListener> {

	public interface IPublishResultListener extends IResultListener, IFailListener {
		void onStart();
		void onFinish(final HomeData feed);
	}

	@Override
	protected void onExecute(
			Context context,
			Map<String, Object> params,
			IPublishResultListener callback) throws Exception {
		callback.onStart();
		String userId = (String) params.get(PublishPage.KEY_PARAMS_ID);
		String content = (String) params.get(PublishPage.KEY_PARAMS_CONTENT);
		ArrayList imageList = (ArrayList) params.get(PublishPage.KEY_PARAMS_IMAGE);
		String place = (String) params.get(PublishPage.KEY_PARAMS_PLACE);
		String price = (String) params.get(PublishPage.KEY_PARAMS_PRICE);
		String plid = (String) params.get(PublishPage.KEY_PARAMS_PLID);
		
		int latitude = (Integer) params.get(PublishPage.KEY_PARAMS_LATITUDE);
		int longitude = (Integer) params.get(PublishPage.KEY_PARAMS_LONGITUDE);
		int columnNo = 0;
		if (InputUtil.isEmpty(place) && InputUtil.isEmpty(price)) {
			columnNo = 2;
		}
		
//		@SuppressWarnings("unchecked")
//		List<BaseToken> tokens = (List<BaseToken>)params.get(PublishPage.KEY_PARAMS_SYNC_SITE);
		boolean shared = (Boolean)params.get(PublishPage.KEY_PARAMS_SHARED);
		try {
			HomeData feed = new PublishManager().requestPublishOriginal(
					context,
					imageList,
					userId,
					content,
					columnNo,//随便说说2，分享0
					place,
					price,
					latitude,
					longitude,
					plid, shared);//,datas
			callback.onFinish(feed);
		} catch (MessageException e) {
			throw e;
		}
	}

}
