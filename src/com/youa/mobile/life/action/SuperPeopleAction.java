package com.youa.mobile.life.action;

import java.util.List;
import java.util.Map;
import android.content.Context;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.life.action.SuperPeopleAction.SuperPeopleResultListener;
import com.youa.mobile.life.data.SuperPeopleClassify;
import com.youa.mobile.life.manager.LifeManager;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;

public class SuperPeopleAction extends BaseAction<SuperPeopleResultListener> {
	
	public static final String REQUEST_TYPE = "request_tyep";
	public static final String CLASSIFY_NAME_KEY = "class_name";
	
	public interface SuperPeopleResultListener extends IResultListener, IFailListener {
		void onStart();
		void onFinish(List<SuperPeopleClassify> list);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			SuperPeopleResultListener callback) throws Exception {
		callback.onStart();
		try {
			RequestType requestType = (RequestType)params.get(REQUEST_TYPE);
			switch (requestType) {
			case GET_CLASS:
				List<SuperPeopleClassify> list = new LifeManager().requestSuperPeopleClassify(context);
				callback.onFinish(list);
				break;
			case GET_CLASS_SUPPERPEOPLE:
				String className = (String)params.get(CLASSIFY_NAME_KEY);
				
				break;
			}
		} catch (MessageException e) {
			throw e;
		}
	}
	
	public enum RequestType{
		GET_CLASS, GET_CLASS_SUPPERPEOPLE
	}
}