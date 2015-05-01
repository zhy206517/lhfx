package com.youa.mobile.more;

import android.content.Context;
import android.content.SharedPreferences;

import com.youa.mobile.LehoApp;
import com.youa.mobile.MainActivity;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.news.util.NewsUtil;

public class MoreUtil {

	private final static String TAG = "MoreUtil";
	private final static String IS_SHOW_NEW_NEWS = "is";
	private final static String IS_SHOW_ADD_ME_NEWS = "ADD";
	private final static String IS_SHOW_SAY_ME_NEWS = "SAY";
	private final static String IS_SHOW_FAV_NEWS = "FAV";
	private final static String IS_HIGH_DEFINITION = "is_high_definition";
	private final static String IS_SYNC_SINA = "is_sync_sina";
	private final static String IS_SYNC_RENREN = "is_sync_renren";
	private final static String IS_SYNC_QQ = "is_sync_qq";

	public static void writeIsShowNewNewsToPref(Context context, boolean isShow) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_NEW_NEWS_COUNT, Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(IS_SHOW_NEW_NEWS, isShow);
		NewsUtil.LOGD(TAG, "<write xml data IS_SHOW_NEW_NEWS> :" + isShow);
		editor.commit();
	}
    //read new news count
	public static boolean readIsShowNewNewsFromPref(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_NEW_NEWS_COUNT, Context.MODE_WORLD_READABLE);
		boolean isShow = preference.getBoolean(IS_SHOW_NEW_NEWS, true);
		NewsUtil.LOGD(TAG, "<read XML IS_SHOW_NEW_NEWS> : " + isShow);
		return isShow;
	}
    //write add me news count
	public static void writeIsShowAddMeNewsToPref(Context context, boolean isShow) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_NEW_NEWS_COUNT, Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(IS_SHOW_ADD_ME_NEWS, isShow);
		NewsUtil.LOGD(TAG, "<write xml data IS_SHOW_ADD_ME_NEWS> :" + isShow);
		editor.commit();
	}
    //read add me news count
	public static boolean readIsShowAddMeFromPref(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_NEW_NEWS_COUNT, Context.MODE_WORLD_READABLE);
		boolean isShow = preference.getBoolean(IS_SHOW_ADD_ME_NEWS, true);
		NewsUtil.LOGD(TAG, "<read XML IS_SHOW_ADD_ME_NEWS> : " + isShow);
		return isShow;
	}
	//write say me news count
	public static void writeIsShowSayMeNewsToPref(Context context, boolean isShow) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_NEW_NEWS_COUNT, Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(IS_SHOW_SAY_ME_NEWS, isShow);
		NewsUtil.LOGD(TAG, "<write xml data IS_SHOW_SAY_ME_NEWS> :" + isShow);
		editor.commit();
	}
	//read say me news count
	public static boolean readIsShowSayMeFromPref(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_NEW_NEWS_COUNT, Context.MODE_WORLD_READABLE);
		boolean isShow = preference.getBoolean(IS_SHOW_SAY_ME_NEWS, true);
		NewsUtil.LOGD(TAG, "<read XML IS_SHOW_SAY_ME_NEWS> : " + isShow);
		return isShow;
	}
	//write fav news count
	public static void writeIsShowFavNewsToPref(Context context, boolean isShow) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_NEW_NEWS_COUNT, Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(IS_SHOW_FAV_NEWS, isShow);
		NewsUtil.LOGD(TAG, "<write xml data IS_SHOW_FAV_NEWS> :" + isShow);
		editor.commit();
	}
	//read fav news count
	public static boolean readIsShowFavFromPref(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_NEW_NEWS_COUNT, Context.MODE_WORLD_READABLE);
		boolean isShow = preference.getBoolean(IS_SHOW_FAV_NEWS, true);
		NewsUtil.LOGD(TAG, "<read XML IS_SHOW_FAV_NEWS> : " + isShow);
		return isShow;
	}
	//read lbs
	public static boolean readIsStartLocationFromPref(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_FILE_LOCATION_GUIDE, Context.MODE_WORLD_READABLE);
		boolean isShow = preference.getBoolean(IS_SHOW_NEW_NEWS, true);
		NewsUtil.LOGD(TAG, "<read XML IS_SHOW_NEW_NEWS> : " + isShow);
		return isShow;
	}
	//write lbs
	public static void writeIsStartLocationFromPref(Context context, boolean isShow) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_FILE_LOCATION_GUIDE, Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(IS_SHOW_NEW_NEWS, isShow);
		NewsUtil.LOGD(TAG, "<write xml data IS_SHOW_NEW_NEWS> :" + isShow);
		editor.commit();
	}
	//read browser modle
	public static boolean readHighDefinitionFromPref(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_FILE_SYSTEM_CONFIG, Context.MODE_WORLD_READABLE);
		boolean isShow = preference.getBoolean(IS_HIGH_DEFINITION, true);
		NewsUtil.LOGD(TAG, "<read XML IS_HIGH_DEFINITION> : " + isShow);
		return isShow;
	}
	//write browser modle
	public static void writeHighDefinitionFromPref(Context context, boolean isShow) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_FILE_SYSTEM_CONFIG, Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(IS_HIGH_DEFINITION, isShow);
		NewsUtil.LOGD(TAG, "<write xml data IS_HIGH_DEFINITION> :" + isShow);
		editor.commit();
	}
	//read sync sina
	public static boolean readSyncSinaFromPref(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_FILE_SYSTEM_CONFIG, Context.MODE_WORLD_READABLE);
		boolean isShow = preference.getBoolean(IS_SYNC_SINA, true);
		NewsUtil.LOGD(TAG, "<read XML IS_SYNC_SINA> : " + isShow);
		return isShow;
	}
	//write sync sina
	public static void writeSyncSinaFromPref(Context context, boolean isShow) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_FILE_SYSTEM_CONFIG, Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(IS_SYNC_SINA, isShow);
		NewsUtil.LOGD(TAG, "<write xml data IS_SYNC_SINA> :" + isShow);
		editor.commit();
	}
	//read sync renren
	public static boolean readSyncRenrenFromPref(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_FILE_SYSTEM_CONFIG, Context.MODE_WORLD_READABLE);
		boolean isShow = preference.getBoolean(IS_SYNC_RENREN, true);
		NewsUtil.LOGD(TAG, "<read XML IS_SYNC_RENREN> : " + isShow);
		return isShow;
	}
	//write sync renren
	public static void writeSyncRenrenFromPref(Context context, boolean isShow) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_FILE_SYSTEM_CONFIG, Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(IS_SYNC_RENREN, isShow);
		NewsUtil.LOGD(TAG, "<write xml data IS_SYNC_RENREN> :" + isShow);
		editor.commit();
	}
	//read sync qq
	public static boolean readSyncQQFromPref(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_FILE_SYSTEM_CONFIG, Context.MODE_WORLD_READABLE);
		boolean isShow = preference.getBoolean(IS_SYNC_QQ, true);
		NewsUtil.LOGD(TAG, "<read XML IS_SYNC_QQ> : " + isShow);
		return isShow;
	}
	//write sync qq
	public static void writeSyncQQFromPref(Context context, boolean isShow) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_FILE_SYSTEM_CONFIG, Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(IS_SYNC_QQ, isShow);
		NewsUtil.LOGD(TAG, "<write xml data IS_SYNC_QQ> :" + isShow);
		editor.commit();
	}
}
