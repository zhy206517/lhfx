package com.youa.mobile.theme.action;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseAction;
/*import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.base.IAction.IFailListener;*/
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.theme.action.TopicAction.ITopicActionResultListener;
import com.youa.mobile.theme.data.TopicData;
import com.youa.mobile.theme.manager.TopicManager;

public class TopicAction extends BaseAction<ITopicActionResultListener> {
	public static final String ACTION_TYPE = "type";
	public static final String ACTION_TYPE_SUB = "sub";
	public static final String ACTION_TYPE_UNSUB = "unsub";
	public static final String ACTION_TYPE_GETALL = "getall";
	
	public static final String TOPIC_TYPE = "topic_type";
	public static final String TOPIC_POP_COMMEND = "1";
	public static final String TOPIC_UI_COMMEND = "2";
	
	public static final String TOPIC_ID = "suid";
	public static final String TOPIC_NAME = "sname";
	
	public static final String TOPIC_ITEM_POSITION_KEY = "topic_positon";
	
	public interface ITopicActionResultListener extends IResultListener, IFailListener{
		@Override
		public void onFail(int resourceID);
		public void onStart(Integer resourceID);
		public void onGetAllFinish(List<TopicData> topicList);
		public void onFinish(int resourceID,int position, boolean topicSubStatus);
		//public void onEnd(int position);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ITopicActionResultListener resultListener) throws Exception {
		String type = (String) params.get(ACTION_TYPE);
		if(null == type || "".equals(type)){
			throw new IllegalArgumentException("TopicAction onExecute (Judge action type) - type is error! please set type");
		}
		if(ACTION_TYPE_GETALL.equals(type)){//热门话题
			resultListener.onStart(R.string.topic_loading);
			List<TopicData> topicList = null;
			try {
				String topicType = (String)params.get(TOPIC_TYPE);
				topicList = new TopicManager().requestTopic(context, topicType);
			} catch (MessageException e) {
				e.setResID(R.string.topic_load_error);
				resultListener.onFail(R.string.topic_load_error);
			}
			resultListener.onGetAllFinish(topicList);
		}else if(ACTION_TYPE_SUB.equals(type)){//订阅话题
			resultListener.onStart(null);
			String suid = (String)params.get(TOPIC_ID);
			String sname = (String)params.get(TOPIC_NAME);
			int positon = (Integer)params.get(TOPIC_ITEM_POSITION_KEY);
			try {
				new TopicManager().requestSubTopic(context, suid, sname);
				resultListener.onFinish(R.string.topic_sub_ok, positon, true);
			} catch (MessageException e) {
				if("jt.follow.u_followed".equals(e.getErrCode())) {//jt.follow.u_followed ... jt.follow.followed
					e.setResID(R.string.topic_error_followed);
					resultListener.onFail(R.string.topic_error_followed);
				}else{
					resultListener.onFail(R.string.topic_sub_fail);
				}
				e.printStackTrace();
			}
		}else if(ACTION_TYPE_UNSUB.equals(type)){//取消订阅
			resultListener.onStart(null);
			String suid = (String)params.get(TOPIC_ID);
			int positon = (Integer)params.get(TOPIC_ITEM_POSITION_KEY);
			try {
				new TopicManager().requestUnSubTopic(context, suid);
				resultListener.onFinish(R.string.topic_unsub_ok, positon, false);
			} catch (MessageException e) {
				if("jt.follow.u_followed".equals(e.getErrCode())) {
					e.setResID(R.string.topic_error_unsubed);
					resultListener.onFail(R.string.topic_error_unsubed);
				}else{
					resultListener.onFail(R.string.topic_unsub_fail);
				}
				e.printStackTrace();
			}
		}
	};
}
