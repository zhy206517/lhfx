package com.youa.mobile.life.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.life.action.DistrictAction.DistrictActionResultListener;
import com.youa.mobile.life.data.DistrictData;
import com.youa.mobile.life.manager.LifeManager;

public class DistrictAction extends BaseAction<DistrictActionResultListener> {
	public static final String KEY_DISTRICT_ID = "district_id";
	public static final String KEY_REQUEST_TYPE = "request_type";
	public static final String KEY_TOPIC_ITEM_POSITION = "positon";
	public static final int LOAD_LIMIT_SIZE = 50;
	
	public static final String KEY_START = "start";
	
	public interface DistrictActionResultListener extends IResultListener, IFailListener{
		@Override
		public void onFail(int resourceID);
		public void onStart(Integer resourceID);
		public void onLoadDataFinish(List<DistrictData> data);
		public void onFinish(int resourceID,int position, boolean status);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			DistrictActionResultListener resultListener) throws Exception {
		RequestType requestType = (RequestType) params.get(KEY_REQUEST_TYPE);
		resultListener.onStart(null);
		LifeManager lifeManager = new LifeManager();
		switch (requestType) {
			case LOADATA:
				int start = Integer.parseInt((String)params.get(KEY_START));
				try {
					List<DistrictData> data = lifeManager.requestDistrictList(context, start, LOAD_LIMIT_SIZE);
					resultListener.onLoadDataFinish(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case FOLLOW:
			case UNFOLLOW:
				boolean status = (requestType == RequestType.FOLLOW) ? true : false;
				int resourceStringId = (status ? R.string.life_sub_district_ok : R.string.life_unsub_district_ok);
				try {
					String did = (String)params.get(KEY_DISTRICT_ID);
					int position = (Integer)params.get(KEY_TOPIC_ITEM_POSITION);
					lifeManager.requestFollowDistrict(context, did, requestType);
					resultListener.onFinish(resourceStringId, position, status);
				} catch (MessageException e) {
					if("jt.follow.u_unfollowed".equals(e.getErrCode())){
						resourceStringId = R.string.life_unsub_district_fatal;
						resultListener.onFail(resourceStringId);
					}else if("jt.follow.u_followed".equals(e.getErrCode())){
						resourceStringId = R.string.life_sub_district_fatal;
						resultListener.onFail(resourceStringId);
					}else {
						throw e;
					}
				}
				break;
			default:
				throw new IllegalArgumentException("DistrictAction onExecute (Judge request_type) - type is not exist! please set type value");
		}
	}
	
	public enum RequestType{
		LOADATA, FOLLOW, UNFOLLOW
	}
}
