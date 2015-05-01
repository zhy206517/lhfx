package com.youa.mobile.jingxuan.manager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.youa.mobile.common.base.BaseManager;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.params.ServerAddressUtil;
import com.youa.mobile.jingxuan.data.AlbumData;
import com.youa.mobile.jingxuan.data.AlbumItemData;
import com.youa.mobile.jingxuan.data.ClassifyTagInfoData;

public class JingXuanManager extends BaseManager {
	private static final String TAG = "JingXuanManager";
	private JingXuanHttpRequestManager mRequestManger;
	private static final String url_album_properties = "/content/tpl_inc/sns/discover/index_scroll.properties";

	public JingXuanHttpRequestManager getHttpRequestManger() {
		if (mRequestManger == null) {
			mRequestManger = new JingXuanHttpRequestManager();
		}
		return mRequestManger;
	}

	public List<ClassifyTagInfoData> requestAllTags(Context context)
			throws MessageException {
		return getHttpRequestManger().getFirstPostInAllTags(context);
	}

	public List<AlbumItemData> requestAlbumData() throws MessageException {
		String url = ServerAddressUtil.HTTP_UPDATE_SERVER
				+ url_album_properties;
		Log.d(TAG, "url:" + url);
		AlbumData data = (AlbumData) getURLContent(url);
		if (data != null) {
			return data.AlbumItemDataList;
		}
		return null;
	}

	private AlbumData getURLContent(String actionUrl) {
		HttpURLConnection conn = null;
		InputStream in = null;
		AlbumData data = null;
		try {
			URL contentUrl = new URL(actionUrl);
			conn = (HttpURLConnection) contentUrl.openConnection();
			int res = conn.getResponseCode();
			if (res == 200) {
				in = conn.getInputStream();
				Properties p = new Properties();
				p.load(in);
				data = new AlbumData(p);
			}
			in.close();
		} catch (Exception e) {
			data = null;
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException Ioe) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return data;
	}
}
