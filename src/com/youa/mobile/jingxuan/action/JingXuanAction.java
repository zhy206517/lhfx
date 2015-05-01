package com.youa.mobile.jingxuan.action;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.jingxuan.action.JingXuanAction.IJingXuanResultListener;
import com.youa.mobile.jingxuan.data.AlbumItemData;
import com.youa.mobile.jingxuan.manager.JingXuanManager;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;

public class JingXuanAction extends BaseAction<IJingXuanResultListener> {
	public interface IJingXuanResultListener extends IResultListener,
			IFailListener {
		public void onStart();
		public void onEnd(List<AlbumItemData> list);
	}

	@Override
	protected void onExecute(Context context, Map params,
			IJingXuanResultListener resultListener) throws Exception {
		JingXuanManager jingXuanManager = new JingXuanManager();
		List<AlbumItemData> list = jingXuanManager.requestAlbumData();
		resultListener.onEnd(list);
	}

}
