package com.youa.mobile.friend.manager;

import java.util.List;

import android.content.Context;

import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.friendmanager.ManagerSuperMenCalssifyData;
import com.youa.mobile.friend.friendsearch.UserInfo;
import com.youa.mobile.life.data.SuperPeopleData;


public class HomeManager {
	// 所有的获取数据管理
	private static HomeHttpManager mHomeHttpManager;

	public static HomeHttpManager getHomeHttpManager() {
		if (mHomeHttpManager == null) {
			mHomeHttpManager = new HomeHttpManager();
		}
		return mHomeHttpManager;
	}

	// 好友动态
	public List<HomeData> searchFriendDynamicList(Context context,
			String minPostId, String maxPostId) throws MessageException {
		List<HomeData> dataList = getHomeHttpManager()
				.requestFriendDynamicList(// 联网取数据
						context, minPostId, maxPostId);
		// 这里可以db 处理
		return dataList;
	}
	
	//获取达人分类列表
	public  List<ManagerSuperMenCalssifyData> requestSuperPeopleClassify(Context context) throws MessageException {
		List<ManagerSuperMenCalssifyData> data = getHomeHttpManager().requestSuperPeopleClassify(context);
		return data;
	}
	
	//全站搜人
	public List<SuperPeopleData> requestFindPeopleList(Context context ,String findName ,int pos) throws MessageException {
		return getHomeHttpManager().requestFindPeopleList(context, findName, pos);
	}

}
