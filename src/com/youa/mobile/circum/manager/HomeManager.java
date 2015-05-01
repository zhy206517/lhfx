package com.youa.mobile.circum.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.GeoPoint;
import com.tencent.mm.sdk.platformtools.LocaleUtil;
import com.youa.mobile.circum.data.PopCircumData;
import com.youa.mobile.common.data.UserData;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.data.HomePageListConfig;
import com.youa.mobile.information.manager.FriendManager;
import com.youa.mobile.location.util.LocationUtil;
import com.youa.mobile.location.util.LocationUtil.EarthPoint;

public class HomeManager {
	// 所有的获取数据管理
	private static HomeHttpManager mHomeHttpManager;

	public static HomeHttpManager getHomeHttpManager() {
		if (mHomeHttpManager == null) {
			mHomeHttpManager = new HomeHttpManager();
		}
		return mHomeHttpManager;
	}

	// 删除周边
	public boolean deleteCircumCollection(Context context, String CircumId)
			throws MessageException {
		return getHomeHttpManager().requestDeleteCircumCollection(context,
				CircumId);
	}
	// 周边动态
	public List<HomeData> requestCircumDynamicList(Context context,
			String id,String place_x, String place_y, int req_num, int start_pos)
			throws MessageException {
		List<HomeData> dataList=null;
		if(TextUtils.isEmpty(id)){
			dataList = getHomeHttpManager()
					.requestCircumDynamicList(context, place_x, place_y, req_num,
							start_pos);	
		}else{
			dataList = getHomeHttpManager()
					.requestCircumDynamicList(context, id, req_num,
							start_pos);		
		}
		
		return sortFeedsByUserAndPlace(context, dataList);
	}

	private static boolean isGetCircum;
	public static List<PopCircumData> mPopCircumList;

	// 周边收藏列表
	public List<PopCircumData> requestCircumCollectionList(Context context,
			int offset, int limit) throws MessageException {
		List<PopCircumData> popList = null;
		if (isGetCircum) {
			isGetCircum = false;
			while (mPopCircumList == null) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
				}
			}
			// getHomeHttpManager().getThemeCollectionDB();//
			// 如果登陆取了，就直接从数据库读。//取数据库
			popList = mPopCircumList;// 没有存储时
		} else {
//			getHomeHttpManager().getThemeCollectionDB();// 先从数据库取，有的话，就不用联网了//取数据库
			popList = getHomeHttpManager().requestCircumCollection(context,
					offset, limit);
//			getHomeHttpManager().saveThemeCollectionDB(); // 存数据库
			return popList;
		}
		// ----------------------------------------------------------------
		// List<PopCircumData> dataList = getHomeHttpManager()
		// .requestCircumCollection(context, offset, limit);
		// 这里可以db 处理
		return popList;
	}

	// 登陆完成后调用,每次登陆都重新取
	public static void requestCircumCollectionList(final Context context)
			throws MessageException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					isGetCircum = true;
					mPopCircumList = getHomeHttpManager()
							.requestCircumCollection(context, 0,
									HomePageListConfig.REQUEST_COUNT);
					if (mPopCircumList == null) {
						isGetCircum = false;
					}
//					getHomeHttpManager().saveCircumCollectionDB();
					// 存数据库
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * 计算并排序周边动态的feeds
	 */
	private List<HomeData> sortFeedsByUserAndPlace(final Context c, List<HomeData> feeds){
		if(feeds==null||feeds.size()==0){
			return null;
		}
		List<UserData> userData = new FriendManager().searchFriendList();
		HashMap<String, Integer> friendsMap = new HashMap<String, Integer>();
		for (UserData user : userData) {
			friendsMap.put(user.getUserId(), 0);
		}
		List<HomeData> locationFeeds = new ArrayList<HomeData>(10);
		List<HomeData> friendsFeeds = new ArrayList<HomeData>(5);
		for (HomeData feed : feeds) {
			if (friendsMap.containsKey(feed.PublicUser.uId)) {
				friendsFeeds.add(feed);
			}else{
				locationFeeds.add(feed);
			}
		}
		final LocationUtil lu = new LocationUtil();
		final EarthPoint curPoint = lu.getCurLocation(c);
		Comparator<HomeData> comp = new Comparator<HomeData>(){
			
			public int compare(HomeData data1, HomeData data2) {
				int result = Integer.MAX_VALUE;
				if(!TextUtils.isEmpty(data1.PublicUser.lat) && !TextUtils.isEmpty(data1.PublicUser.lon)
						&& !TextUtils.isEmpty(data2.PublicUser.lat) && !TextUtils.isEmpty(data2.PublicUser.lon)){
					EarthPoint ep1 = lu.new EarthPoint(Double.parseDouble(data1.PublicUser.lat), Double.parseDouble(data1.PublicUser.lon));
					EarthPoint ep2 = lu.new EarthPoint(Double.parseDouble(data2.PublicUser.lat), Double.parseDouble(data2.PublicUser.lon));
					return ((Double) lu.getCurFarByEarthPoint(c, ep1)).compareTo(lu.getCurFarByEarthPoint(c, ep2));
				}
				return Integer.MAX_VALUE;
			}  
	    };
		Collections.sort(friendsFeeds, comp);
		Collections.sort(locationFeeds, comp);
		feeds.clear();
		feeds.addAll(friendsFeeds);
		feeds.addAll(locationFeeds);
		//LocationUtil lu = new LocationUtil();
		for (HomeData homeData : feeds) {
			EarthPoint ep1 = lu.new EarthPoint(Double.parseDouble(TextUtils.isEmpty(homeData.PublicUser.lat)?"0":homeData.PublicUser.lat), Double.parseDouble(TextUtils.isEmpty(homeData.PublicUser.lon)?"0":homeData.PublicUser.lon));
		}
		
		return feeds;
	}
}
