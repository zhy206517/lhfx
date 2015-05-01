package com.youa.mobile.theme.manager;

import java.util.List;

import android.content.Context;

import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.theme.data.TopicData;

public class TopicManager {
	
	private TopicHttpManager mTopHttpManager = null;
	
	private TopicHttpManager getTopRequestRequestManager() {
		if(mTopHttpManager == null) {
			mTopHttpManager = new TopicHttpManager();
		}
		return mTopHttpManager;
	}
	
	public List<TopicData> requestTopic( Context context, String type) throws MessageException {
		return getTopRequestRequestManager().requestTopicList(context, type);
	}
	
	public String requestSubTopic(Context context, String suid, String sname) throws MessageException {
		return getTopRequestRequestManager().requestSubTopic(context, suid, sname);
	}
	
	public String requestUnSubTopic(Context context, String suid) throws MessageException {
		return getTopRequestRequestManager().requestUnSubTopic(context, suid);
	}
	/*if(isAdd) {
			getTopRequestRequestManager().requestTopicList(
					context,
					uid,
					followedId);
		} else {
			getTopRequestRequestManager().requestCancelAttent(
					context,
					uid,
					followedId);
		}*/
}
