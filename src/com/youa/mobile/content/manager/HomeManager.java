package com.youa.mobile.content.manager;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.youa.mobile.circum.data.PopCircumData;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.content.data.FeedContentCommentData;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.data.HomePageListConfig;

public class HomeManager {
	// 所有的获取数据管理
	private static HomeHttpManager mHomeHttpManager;

	public static HomeHttpManager getHomeHttpManager() {
		if (mHomeHttpManager == null) {
			mHomeHttpManager = new HomeHttpManager();
		}
		return mHomeHttpManager;
	}

	public int feedLike(Context context, String postId) throws MessageException {
		return getHomeHttpManager().requestLike(context, postId);
	}
	public int deleteFeed(Context context, String postId) throws MessageException {
		return getHomeHttpManager().requestDelete(context, postId);
	}
	public int feedCancelLike(Context context, String postId)
			throws MessageException {
		return getHomeHttpManager().requestCancleLike(context, postId);
	}

	public List<FeedContentCommentData> requestFriendComment(Context context,
			String postId, String offset, int limit) throws MessageException {
		List<FeedContentCommentData> data = getHomeHttpManager()
				.requestFriendComment(context, postId, offset, limit);
		return data;
	}

	// 好友正文页
	public HomeData requestFriendContent(Context context, String postId)
			throws MessageException {
		HomeData data = getHomeHttpManager().requestFriendContent(context,
				postId);
		// 等待
		if(data==null||data.PublicUser==null){
			return data;
		}
		data.PublicUser.isLiked = getHomeHttpManager().requestIsLiked(context,
				data.PublicUser.postId);
		return data;
	}

}
