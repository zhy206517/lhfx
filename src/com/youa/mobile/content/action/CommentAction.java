package com.youa.mobile.content.action;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.content.action.CommentAction.ISearchResultListener;
import com.youa.mobile.content.data.FeedContentCommentData;
import com.youa.mobile.content.manager.HomeManager;
import com.youa.mobile.friend.data.HomePageListConfig;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;

public class CommentAction extends BaseAction<ISearchResultListener> {
	public static final String PARAM_POST_ID = "postid";
	public static final String PARAM_OFFSET = "commId";

	public interface ISearchResultListener extends IResultListener,
			IFailListener {

		public void onStart();

		public void onEnd(List<FeedContentCommentData> commentList,
				String commId);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISearchResultListener resultListener) throws Exception {
		String postId = null;
		String commId = null;
		if (params != null) {
			postId = (String) params.get(PARAM_POST_ID);
			commId = (String) params.get(PARAM_OFFSET);
		}
		List<FeedContentCommentData> commentList = new HomeManager()
				.requestFriendComment(context, postId, commId,
						HomePageListConfig.LIMIT_NUM);// HomePageListConfig.LIMIT_NUM
		resultListener.onEnd(commentList, commId);
	}

	// private List<FeedContentCommentData> getData() {
	// List<FeedContentCommentData> list = new
	// ArrayList<FeedContentCommentData>();
	// FeedContentCommentData data = null;
	// for (int i = 0; i < 10; i++) {
	// data = new FeedContentCommentData();
	// data.public_name = "张秦宁" + i + "号";
	// data.public_time = i + "分钟前";
	// data.public_content =
	// "张秦宁和我坐车，李庆伟开车，吕磊坐前面，一块回家张秦宁坐车，李庆伟开车，吕磊坐前面，一块回家张秦宁坐车，李庆伟开车，吕磊坐前面，一块回家";
	// data.replyName="张亚唯";
	// list.add(data);
	// }
	// return list;
	// }

}
