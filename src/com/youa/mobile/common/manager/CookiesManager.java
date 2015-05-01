package com.youa.mobile.common.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.youa.mobile.SystemConfig;

public class CookiesManager {
	private static final String FEH_PREFERENCES = SystemConfig.XML_FILE_SYSTEM_CONFIG;
	private static final String PRE_FEH_COOKIESTORE = "cookiestore";
	public static String getCookieStore(Context appCtx)
	{
		SharedPreferences sp = appCtx.getSharedPreferences(FEH_PREFERENCES, Context.MODE_PRIVATE);
		String cookie = sp.getString(PRE_FEH_COOKIESTORE, "");
		return cookie;
	}
	public static void setCookieStore(Context appCtx,
			String cookies) {
		SharedPreferences sp = appCtx.getSharedPreferences(FEH_PREFERENCES, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString(PRE_FEH_COOKIESTORE, cookies);
		edit.commit();
		return;
	}
}
