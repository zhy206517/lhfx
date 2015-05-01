package com.youa.mobile.jingxuan.action;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.jingxuan.action.RequestTagAllAction.ITagAllResultListener;
import com.youa.mobile.jingxuan.data.ClassifyTagInfoData;
import com.youa.mobile.jingxuan.manager.JingXuanManager;

public class RequestTagAllAction extends BaseAction<ITagAllResultListener> {
	public interface ITagAllResultListener extends IResultListener,
			IFailListener {
		public void onStart();
		public void onEnd(List<ClassifyTagInfoData> list);
	}
	@Override
	protected void onExecute(Context context, Map params,
			ITagAllResultListener resultListener) throws Exception {
		JingXuanManager jingXuanManager = new JingXuanManager();
		List<ClassifyTagInfoData> list = jingXuanManager.requestAllTags(context);
		resultListener.onEnd(list);
	}
}