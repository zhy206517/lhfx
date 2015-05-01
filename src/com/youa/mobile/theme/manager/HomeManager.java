package com.youa.mobile.theme.manager;

import java.util.List;

import android.content.Context;

import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.data.HomePageListConfig;
import com.youa.mobile.theme.data.PopThemeInfo;

public class HomeManager {
	// 所有的获取数据管理
	private static HomeHttpManager mHomeHttpManager;

	public static HomeHttpManager getHomeHttpManager() {
		if (mHomeHttpManager == null) {
			mHomeHttpManager = new HomeHttpManager();
		}
		return mHomeHttpManager;
	}

	// 删除话题
	public boolean deleteThemeCollection(Context context, String themeId)
			throws MessageException {
		return getHomeHttpManager().requestDeleteThemeCollection(context,
				themeId);
	}

	// 话题动态
	public List<HomeData> requestThemeDynamicList(Context context,
			String keyword, int req_num, int start_pos) throws MessageException {
		List<HomeData> dataList = getHomeHttpManager().requestThemeDynamicList(
				context, keyword, req_num, start_pos);
		// 这里可以db 处理
		return dataList;
	}

	// 专辑动态
	public List<HomeData> requestAlbumFeedList(Context context, String id,
			int req_num, int start_pos) throws MessageException {
		List<HomeData> dataList = getHomeHttpManager().requestAlbumFeedList(
				context, id, req_num, start_pos);
		return dataList;
	}

	private static boolean isGetTheme;
	public static List<PopThemeInfo> mPopList;// 直接赋给话题列表List

	// 话题收藏
	public List<PopThemeInfo> requestThemeCollectionList(Context context,
			int offset, int limit) throws MessageException {
		List<PopThemeInfo> popList = null;
		if (isGetTheme) {
			isGetTheme = false;
			while (mPopList == null) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
				}
			}
			// getHomeHttpManager().getThemeCollectionDB();//
			// 如果登陆取了，就直接从数据库读。//取数据库
			popList = mPopList;// 没有存储时
		} else {
			getHomeHttpManager().getThemeCollectionDB();// 先从数据库取，有的话，就不用联网了//取数据库
			popList = getHomeHttpManager().requestThemeCollection(context, 0,
					limit);
			getHomeHttpManager().saveThemeCollectionDB(); // 存数据库
			return popList;
		}
		return popList;
	}

	// 登陆完成后调用,每次登陆都新取
	public static void requestThemeCollectionList(final Context context)
			throws MessageException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					isGetTheme = true;
					mPopList = getHomeHttpManager().requestThemeCollection(
							context, 0, HomePageListConfig.REQUEST_COUNT);
					if (mPopList == null) {
						isGetTheme = false;
					}
					getHomeHttpManager().saveThemeCollectionDB(); // 存数据库
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
