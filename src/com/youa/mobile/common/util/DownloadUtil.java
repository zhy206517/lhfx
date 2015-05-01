package com.youa.mobile.common.util;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

public class DownloadUtil {
	public static void download(Context context, String url, String title) {
		ContentValues values = new ContentValues();
        values.put("uri", url);//指定下载地址
        values.put("useragent", "Mozilla/5.0 (Linux; U; Android 1.5; en-us; sdk Build/CUPCAKE) AppleWebKit/528.5+ (KHTML, like Gecko) Version/3.1.2 Mobile Safari/525.20.1");
        values.put("visibility", 1);//设置下载提示是否在屏幕顶部显示
        values.put("notificationpackage", context.getPackageName());//设置下载完成之后回调的包名
        values.put("notificationclass", "UpgradeBroadcastReceiver");//设置下载完成之后负责接收的Receiver，这个类要继承 BroadcastReceiver
        values.put("hint", title);
        values.put("mimetype", "application/vnd.android.package-archive");
        values.put("is_public_api", true);
        values.put("destination", 0);
        context.getContentResolver().insert(Uri.parse("content://downloads/download"), values);
	}
}
