package com.youa.mobile.common.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.params.ServerAddressUtil;

public class UpgradeHttpManager {
	private static final String TAG = "UpgradeHttpManager";
	private static final String url_min_version_code = "resource/wl/minVersion";
	private static final String url_version_code = "resource/wl/version";
	private static final String url_version_description = "resource/wl/description";
	private static final String APK_PREFIX = "upgrade:";
	private static final String APK_VERSIONNAME = "versionname:";
	private static final String APK_DESCRIPTION = "description:";
	private int mVersionCode;
	private int mMinVersionCode;
	private String mDescription;
	private String mApkURL;
	private String mVersionName;
	private static final UpgradeHttpManager mUpgradeManager = new UpgradeHttpManager();
	public static UpgradeHttpManager getInstance() {
		return mUpgradeManager;
	}
	
	public void request() {
		requestVersionCode();
		requestMinVersionCode();
		int curVersion = ApplicationManager.getInstance().getVersionCode();
		Log.d(TAG, "curVersion:" + curVersion);
		if(mVersionCode > curVersion) {			
			requestDescription();
		} else {
			mDescription = "";
			mApkURL = "";
			mVersionName = "";
		}
	}
	
	private void requestVersionCode() {
		try {
			String url = ServerAddressUtil.HTTP_UPDATE_SERVER + url_version_code;
			Log.d(TAG, "url:" + url);
			String versionStr = getURLContent(url);
			Log.d(TAG, "versionStr:" + versionStr);
			if(versionStr != null && !"".equals(versionStr)) {
				mVersionCode = Integer.parseInt(versionStr);
			} else {
				mVersionCode = -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			mVersionCode = -1;
		} 
	}
	private void requestMinVersionCode() {
		try {
			String url = ServerAddressUtil.HTTP_UPDATE_SERVER + url_min_version_code;
			Log.d(TAG, "url:" + url);
			String versionStr = getURLContent(url);
			Log.d(TAG, "minVersionStr:" + versionStr);
			if(versionStr != null && !"".equals(versionStr)) {
				mMinVersionCode = Integer.parseInt(versionStr);
			} else {
				mMinVersionCode = -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			mMinVersionCode = -1;
		} 
	}
	private void requestDescription() {
		try {			
			String url = ServerAddressUtil.HTTP_UPDATE_SERVER + url_version_description;
			Log.d(TAG, "url:" + url);
			String text = getURLContent(url);
			Log.d(TAG, "text:" + text);
			mApkURL = parseLine(APK_PREFIX, text);
			mVersionName = parseLine(APK_VERSIONNAME, text);
			int descriptionIndex = text.indexOf(APK_DESCRIPTION);
			if(descriptionIndex != -1) {
				mDescription = text.substring(APK_DESCRIPTION.length() + descriptionIndex);
			}
		} catch (Exception e) {
			mDescription = "";
			mApkURL = "";
			mVersionName = "";
		}
	}
	
	private String parseLine(String prefix, String alltext) {
		int start = alltext.indexOf(prefix);
		if(start == -1) {
			return "";
		}
		String splitStr = "\r\n";
		int end = alltext.indexOf(splitStr, start);
		if(end == -1) {
			splitStr = "\n";
			end = alltext.indexOf(splitStr, start);
		}
		String result;
		if(start == -1 || end == -1) {
			result = "";
		} else {
			result = alltext.substring(start + prefix.length(), end);
		}
		return result;
	}
	
	private static String getURLContent(String actionUrl) throws IOException
			 {
		String result = null;
		HttpURLConnection conn = null;
		try
		{
			URL contentUrl = new URL(actionUrl);
			conn = (HttpURLConnection) contentUrl
					.openConnection();
			int res = conn.getResponseCode();
			InputStream in = null;
			StringBuffer buf = new StringBuffer();
			if (res == 200) {
				in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in,
				"utf-8"));
				String line;
				while (null != (line = br.readLine())) {
					if (buf.length() > 0) {
						buf.append('\n').append(line);
					} else {
						buf.append(line);
					}
				}
				result = buf.toString();
			}
		} finally {
			
		}
		return result;
	}

	public int getVersionCode() {
		return mVersionCode;
	}
	public int getMinVersionCode() {
		return mMinVersionCode;
	}
	public String getDescription() {
		return mDescription;
	}
	
	public String getApkURL() {
		return mApkURL;
	}
	
	public String getVersionName() {
		return mVersionName;
	}
	
}
