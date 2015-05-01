package com.youa.mobile.life.action;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.life.action.LoadFeedAction.LoadFeedActionListener;
import com.youa.mobile.life.data.ShareClassifyData;
import com.youa.mobile.life.manager.LifeManager;

public class LoadFeedAction extends BaseAction<LoadFeedActionListener>{
	public static final String REQUEST_TYPE = "requestType"; 
	public static final String SHARECLASSIFY_ID = "shareclassifyId";
	public static final String DISCTRICT_ID = "disctrictId";
	public static final String PAGE = "page";
	public static final int limit = 25;
	public interface LoadFeedActionListener extends IResultListener, IFailListener{
		@Override
		public void onFail(int resourceID);
		public void onStart();
		public void onFinish(List<HomeData> data, int pageIndex);
	}
	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			LoadFeedActionListener resultListener) throws Exception {
		resultListener.onStart();
		LifeManager lifeManager = new LifeManager();
		RequestType requestType = (RequestType)params.get(REQUEST_TYPE);
		List<HomeData> homeData = null;
		int page = (Integer)params.get(PAGE);
		int currentPageIndex = 0;
		currentPageIndex = page;
		switch (requestType) {
			//根据推荐分享的类别id获取feed列表
			case SHARE_CLASSIFY:
				String clsid = (String)params.get(SHARECLASSIFY_ID);
				if(clsid == null || "".equals(clsid)){
					List<ShareClassifyData> shareClassifys = lifeManager.requestShareClassify(context);
					if(shareClassifys != null){
						for(ShareClassifyData shareClassify : shareClassifys){
							if(shareClassify.name != null && (shareClassify.name.contains("全部") || shareClassify.name.contains("所有"))){
								clsid = shareClassify.id;
								break;
							}
						}
					}else{
						resultListener.onFail(-1);
					}
					//clsid = lifeManager.requestShareClassify(context);
				}
				homeData = lifeManager.requestShareClassifyFeedData(context, clsid, currentPageIndex, limit, true, true);
				break;
			//根据商圈信息获取feed列表
			case DISTRICT:
				//TODO	商圈
//				params.put(LoadFeedAction.DISCTRICT_ID, districtid);
//				params.put(DISTRICT_STATUS, districtStatus);
//				params.put(DISTRICT_POSITION, districtPositon);
				/*homeData = */lifeManager.requestDistrictList(context, currentPageIndex, limit);//ShareClassifyFeedData(context, clsid, currentPageIndex, limit, true, true);
				break;
		}
		resultListener.onFinish(homeData, currentPageIndex);
	}
	
	public enum RequestType{
		/**
		 * 推荐分享分类
		 */
		SHARE_CLASSIFY, 
		/**
		 * 商圈
		 */
		DISTRICT
	}
}
