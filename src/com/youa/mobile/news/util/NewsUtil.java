package com.youa.mobile.news.util;

import java.util.List;

import com.youa.mobile.DebugMode;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.common.util.EmotionHelper;
import com.youa.mobile.parser.ContentData;
import com.youa.mobile.parser.ParserContent;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

public class NewsUtil {

	private final static String TAG = "NewsUtil";
	public final static String ADD_ME_COUNT = "add";
	public final static String SAY_ME_COUNT = "say";
	public final static String FAVORITE_COUNT = "fav";
	public final static String ALL_COUNT = "all";
	public  static boolean isComplete;

	public static void LOGD(String tag, String msg) {
		if (DebugMode.debug)
			Log.d("#news#" + tag, "----------------->" + msg);
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean isEmpty(CharSequence str) {
		return str == null || str.length() == 0;
	}

	public static boolean isEmpty(String[] list) {
		return list == null || list.length == 0;
	}

	public static boolean isEmpty(List list) {
		return list == null || list.size() == 0;
	}

	public static boolean isEmpty(int[] list) {
		return list == null || list.length == 0;
	}

	public static void writeNewCountToPref(Context context, int[] list) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_NEW_NEWS_COUNT, Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(ADD_ME_COUNT, list[0]);
		editor.putInt(SAY_ME_COUNT, list[1]);
		editor.putInt(FAVORITE_COUNT, list[2]);
		editor.putInt(ALL_COUNT, list[0] + list[1] + list[2]);
		LOGD(TAG, "write XML <new addme> : " + list[0]);
		LOGD(TAG, "write XML <new sayme> : " + list[1]);
		LOGD(TAG, "write XML <new favor> : " + list[2]);
		LOGD(TAG, "write XML <new   all> : " + list[0] + list[1] + list[2]);
		editor.commit();
	}

	public static int[] readNewCountFromPref(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SystemConfig.XML_NEW_NEWS_COUNT, Context.MODE_WORLD_READABLE);
		int addme = preference.getInt(ADD_ME_COUNT, 0);
		int sayme = preference.getInt(SAY_ME_COUNT, 0);
		int favorite = preference.getInt(FAVORITE_COUNT, 0);
		int all = preference.getInt(ALL_COUNT, 0);
		LOGD(TAG, "read XML <new addme> : " + addme);
		LOGD(TAG, "read XML <new sayme> : " + sayme);
		LOGD(TAG, "read XML <new favor> : " + favorite);
		LOGD(TAG, "read XML <new   all> : " + all);
		return new int[] { addme, sayme, favorite, all };
	}

	public static boolean isZero(int[] list) {
		if (!isEmpty(list)) {
			for (int i : list) {
				if (i != 0) {
					return false;
				}
			}
		}
		return true;
	}

	public static String parseContent(String content) {
		if(TextUtils.isEmpty(content)){
			return "";
		}
		ContentData[] datas = ParserContent.getParser().parser(content.toCharArray());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < datas.length; i++) {
			sb.append(datas[i].str);
		}
		return sb.toString();
	}

	private static TextView temp = null;
	public static CharSequence parseStr2charSequence(String tempContent, String prefix, Context context) {
		while (isComplete) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
			}
		}
		isComplete=true;
		ContentData[] datas = ParserContent.getParser().parser(
				tempContent.toCharArray());
		if (datas != null) {
			temp = new TextView(context);
			if (prefix != null) {
				temp.append(prefix);
			}
			for (ContentData d : datas) {
				if (d.type == ContentData.TYPE_EMOTION) {
					Spanned span = EmotionHelper.parseToImageText(context,
							d.str, 20);
					temp.append(span);
				} else {
					temp.append(d.str == null ? "" : d.str);
				}
			}
			isComplete=false;
			return temp.getText();
		}
		isComplete=false;
		return null;
	}
}
