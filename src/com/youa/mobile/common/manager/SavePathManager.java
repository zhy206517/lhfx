package com.youa.mobile.common.manager;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class SavePathManager {

	private static final SavePathManager mSavePathManager = new SavePathManager();

	// public static SavePathManager getIntance() {
	// return mSavePathManager;
	// }

	public static void initFolder() {
		File file = new File(getImagePath());
		if (!file.exists()) {
			file.mkdirs();
		}

	}

	// mBaseDir = new File(context.getCacheDir(), "webviewCache");

	public static String getImagePath() {
		return getSDPath() + "/LeHuoStore/";
	}

	public static File getLogFile(Context context) {
		File dirFile = context.getCacheDir();
		return new File(dirFile, "feihong_log.txt");
	}

	public static String changeURLToPath(String url) {
		initFolder();
		String filename = processUrl(url);
		return getImageFilePath(filename);
	}

	public static String getImageFilePath(String fileName) {
		return getImagePath() + fileName;
	}

	private static String processUrl(String url) {
		int index = url.lastIndexOf("/");
		String path;
		if (index > -1) {
			path = url.substring(index + 1);
		} else {
			url = url.toLowerCase();
			path = url.replace("http://", "");
			path = path.replace('/', '_');
		}
		// System.out.println("page>>>>>>>>>:" + path);
		return path;
	}

	public static String getSDPath() {
		String sdDir = "/sdcard";
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		// 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();// 获取跟目录
		}
		return sdDir;
	}
}
