package com.youa.mobile.input.action;

import java.util.ArrayList;
import java.util.Map;
import android.content.Context;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.input.action.SaveDraftAction.ISaveDraftResultListenter;
import com.youa.mobile.input.data.ImageData;
import com.youa.mobile.input.data.PublishData;
import com.youa.mobile.input.manager.DraftManager;
import com.youa.mobile.common.base.IAction.IResultListener;

public class SaveDraftAction extends BaseAction<ISaveDraftResultListenter> {

	public interface ISaveDraftResultListenter extends IResultListener {
		void onFinish();
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISaveDraftResultListenter callback) throws Exception {
		String content = (String) params.get(DraftManager.KEY_CONTENT);
		ArrayList<ImageData> contentImage = (ArrayList) params
				.get(DraftManager.KEY_CONTENT_IMAGE);
		String consumeAvPrice = (String) params
				.get(DraftManager.KEY_CONSUME_AV_PRICE);
		String consumePlace = (String) params
				.get(DraftManager.KEY_CONSUME_PLACE);
		int latitude = (Integer) params.get(DraftManager.KEY_POS_LATITUDE);
		int longitude = (Integer) params.get(DraftManager.KEY_POS_LONGITUDE);
		boolean isManyPeople = (Boolean) params
				.get(DraftManager.KEY_MANY_PEOPLE);
		String plid = (String) params.get(DraftManager.KEY_POS_ID);
		String consumePrice = (String) params.get(DraftManager.KEY_CONSUME_PRICE);
		String people = (String) params.get(DraftManager.KEY_CONSUME_PEOPLE_NUM);
		DraftManager.getInstance().saveDrafeData(
				context,
				new PublishData(content, contentImage, consumePlace,
						consumeAvPrice, isManyPeople, latitude, longitude, plid,consumePrice,people));
		callback.onFinish();
	}
}
