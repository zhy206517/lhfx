package com.youa.mobile;

import android.content.Intent;

public class LehuoIntent extends Intent {
	public static final String ACTION_VIEWPICTURE = "com.youa.mobile.viewpicture";
	// 用户信息下载成功
	public static final String ACTION_USERINFORMATION_UPDATE = "com.youa.mobile.user_update";
	public static final String USERINFOR_RELATION = "com.youa.mobile.user_relation";
	public static final String USERINFOR_UID = "com.youa.mobile.user_uid";
	public static final String USERINFOR_NAME = "com.youa.mobile.user_name";
	public static final String USERINFOR_IMAGEID = "com.youa.mobile.imageid";
	public static final String USERINFOR_SEXINT = "com.youa.mobile.sexint";
	// 用户信息数量下载
	public static final String ACTION_USERCOUNT_NEEDUPDATE = "com.youa.mobile.user_count_needupdate";
	// 用户订阅的热门话题改变
	public static final String ACTION_USER_TOPIC_CHANGE = "com.youa.mobile.user_topic_change";
	// 用户关注的热门商圈改变
	public static final String ACTION_USER_DISTRICT_CHANGE = "com.youa.mobile.user_district_change";

	// 话题删除成功
	public static final String ACTION_USER_TOPIC_DELETE = "com.youa.mobile.user_topic_delete";
	public static final String ACTION_OWN_FEED_DELETE = "com.youa.mobile.own_feed_delete";
	public static final String ACTION_FEED_PUBLISH_OK = "com.youa.mobile.feed_publish_ok";
	public static final String ACTION_FEED_PUBLISH_REFRESH = "com.youa.mobile.feed_publish_refresh";
	// 程序退出
	public static final String ACTION_EXIT_CLIENT = "com.youa.mobile.exit_client";
	// feed刷新
	public static final String JINGXUAN_FEED_UPDATE = "com.youa.mobile.update_jingxuan";
	public static final String FRIEND_FEED_UPDATE = "com.youa.mobile.update_friend";
	public static final String CIRCUM_FEED_UPDATE = "com.youa.mobile.update_circum";
	public static final String PERSON_FEED_UPDATE = "com.youa.mobile.update_personInfo";
	//微信发送成功
	public static final String WEIXIN_SEND_SUCCESS = "com.youa.mobile.weixin_send";
	public static final String WEIXIN_ENTER_LEHO_CLIENT="com.youa.mobile.enter_leho_client";
	public static final String LOGIN_SUCESS_WXSharePage="com.youa.mobile.content.WXSharePage";
	
	public static final String LOCATION_SUCCESS = "com.youa.mobile.location.location_success";
}
