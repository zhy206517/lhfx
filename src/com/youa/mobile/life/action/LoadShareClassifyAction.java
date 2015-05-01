package com.youa.mobile.life.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.life.action.LoadShareClassifyAction.ShareClassifyActionListener;
import com.youa.mobile.life.data.ShareClassifyData;
import com.youa.mobile.life.manager.LifeManager;

public class LoadShareClassifyAction extends BaseAction<ShareClassifyActionListener>{
	
	public interface ShareClassifyActionListener extends IResultListener, IFailListener{
		@Override
		public void onFail(int resourceID);
		public void onStart();
		public void onFinish(List<ShareClassifyData> data);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ShareClassifyActionListener resultListener) throws Exception {
		resultListener.onStart();
		LifeManager lifeManager = new LifeManager();
		List<ShareClassifyData> classifyList = lifeManager.requestShareClassify(context);
		List<ShareClassifyData> classifyListResult = new ArrayList<ShareClassifyData>(0);
		int i=0;
		if(classifyList != null){
			for(ShareClassifyData shareClassify : classifyList){
				if(i>6){
					break;
				}
				i++;
				if(shareClassify.name != null && (shareClassify.name.contains("全部") || shareClassify.name.contains("所有"))){
					//classifyList.add(0, shareClassify);
				}else{
					classifyListResult.add(shareClassify);
				}
			}
		}
		resultListener.onFinish(classifyListResult);
	}
}
