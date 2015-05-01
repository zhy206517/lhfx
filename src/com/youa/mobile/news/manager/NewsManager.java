package com.youa.mobile.news.manager;

import java.util.List;

import android.content.Context;

import com.youa.mobile.common.base.BaseManager;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.news.data.FavoriteData;
import com.youa.mobile.news.data.FavoritePeopleData;
import com.youa.mobile.news.data.SayMeData;

public class NewsManager extends BaseManager {

	private NewsHttpRequestManager mNewsRequestManger;

	public NewsHttpRequestManager getNewsHttpRequestManger() {
		if (mNewsRequestManger == null) {
			mNewsRequestManger = new NewsHttpRequestManager();
		}
		return mNewsRequestManger;
	}

	public List<HomeData> requestAddMeData (Context context, String uId, int limit, long time) throws MessageException {
		return getNewsHttpRequestManger().requestAddMeData(context, uId, limit, time);
	}

	public List<SayMeData> requestSayMeData (Context context, String uId, int limit, long time) throws MessageException {
		return getNewsHttpRequestManger().requestSayMeData(context, uId, limit, time);
	}

	public List<FavoriteData> requestFavoriteData(Context context, String uId, int limit, String postId) throws MessageException {
		return getNewsHttpRequestManger().requestFavoriteData(context, uId, limit, postId);
	}

	public int[] requestGetNewNewsCount(Context context, String uId) throws MessageException {
		return getNewsHttpRequestManger().requestGetNewNewsCount(context, uId);
	}

	public void requestSetNewNewsCount(Context context, String uId, int type, int count) throws MessageException {
		getNewsHttpRequestManger().requestSetNewNewsCount(context, uId, type, count);
	}

	public List<FavoritePeopleData> requestFavoritePeopleList(Context context, String feedId) throws MessageException {
		return getNewsHttpRequestManger().requestFavoritePeopleList(context, feedId);
	}

	

}
