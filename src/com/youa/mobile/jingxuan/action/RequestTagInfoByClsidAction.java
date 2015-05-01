package com.youa.mobile.jingxuan.action;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.jingxuan.TagClassFeedPage;
import com.youa.mobile.jingxuan.data.CategoryData;
import com.youa.mobile.jingxuan.action.RequestTagInfoByClsidAction.ITagInfoResultListener;
import com.youa.mobile.life.manager.LifeManager;

public class RequestTagInfoByClsidAction extends
		BaseAction<ITagInfoResultListener> {
	public interface ITagInfoResultListener extends IResultListener,
			IFailListener {
		public void onStart();

		public void onEnd(CategoryData data);
	}

	@Override
	protected void onExecute(Context context, Map params,
			ITagInfoResultListener resultListener) throws Exception {
		String id = (String) params.get(TagClassFeedPage.ID_KEY);
		CategoryData data = new LifeManager().requestTagAllInfo(context, id);
		resultListener.onEnd(data);
	}
}