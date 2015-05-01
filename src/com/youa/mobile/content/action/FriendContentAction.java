package com.youa.mobile.content.action;

import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.content.action.FriendContentAction.ISearchResultListener;
import com.youa.mobile.content.manager.HomeManager;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.friend.data.HomeData;

public class FriendContentAction extends BaseAction<ISearchResultListener> {
	final public static String PARAM_UESER_ID = "user_id";
	final public static String PARAM_FEED_ID = "feed_id";

	public interface ISearchResultListener extends IResultListener,
			IFailListener {

		public void onStart();

		public void onEnd(HomeData contentData);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISearchResultListener resultListener) throws Exception {
		String postId = (String) params.get(PARAM_FEED_ID);
		HomeData contentData = new HomeManager().requestFriendContent(context,
				postId);
		// FeedContentData contentData = getData(false);
		resultListener.onEnd(contentData);
	}

	// content和publiccontent合为一个，如果content和publiccontent不为空，则是转发
	// private HomeData getData(boolean isOrigin) {
	// HomeData data = new HomeData();
	// data.public_name = "张秦宁号";
	// // data.publicContent
	// data.time = "5分钟前";
	// ContentData[] contents = new ContentData[30];
	// for (int i = 0; i < contents.length; i++) {
	// contents[i] = new ContentData();
	// if (i == 3 || i == 4 || i == 8) {
	// contents[i].type = ContentData.TYPE_AT;
	// contents[i].str = "@我是被at的";
	// } else if (i == 2 || i == 25) {
	// contents[i].type = ContentData.TYPE_TOPIC;
	// contents[i].str = "#我是话题";
	// } else if (i == 7) {
	// contents[i].type = ContentData.TYPE_EMOTION;
	// contents[i].str = "111";
	// } else if (i == 6) {
	// contents[i].str = "http://www.baidu.com/";
	// contents[i].href = "http://www.baidu.com/";
	// contents[i].type = ContentData.TYPE_LINK;
	// } else {
	// contents[i].type = ContentData.TYPE_TEXT;
	// contents[i].str = "积分卡点积分卡积分卡大黄蜂";
	// }
	// }
	// data.publicContents = contents;
	// // data.content = "啊啊啊啊啊啊111啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊"
	// // + "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊" + "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊"
	// // + "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊" + "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊"
	// // + "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊" + "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊"
	// // + "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊" + "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊"
	// // + "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊111啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊";
	// data.content =
	// "张秦宁和我坐车，李庆伟开车，吕磊坐前面，一块回家张秦宁坐车，李庆伟开车，吕磊坐前面，一块回家张秦宁坐车，李庆伟开车，吕磊坐前面，一块回家";
	// data.like_num = "3";
	// data.comment_num = "7";
	// data.transpond_num = "55";
	// return data;
	// }

}
