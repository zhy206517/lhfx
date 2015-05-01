package com.youa.mobile.utils;

import android.util.Log;

public class LogUtil {
	private static final boolean LOG_V = true;
	private static final boolean LOG_D = true;
	private static final boolean LOG_I = true;
	private static final boolean LOG_E = true;
	
	public static void v(String tag, String msg) {
		if (LOG_V) {
			Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (LOG_D) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (LOG_I) {
			Log.i(tag, msg);
		}
	}

	public static void i(String tag, String msg, Throwable tr) {
		if (LOG_I) {
			Log.i(tag, msg, tr);
		}
	}

	public static void ie(String tag, String msg) {
		if (LOG_E) {
			Log.e(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (LOG_E) {
			Log.e(tag, msg, tr);
		}
	}
}
