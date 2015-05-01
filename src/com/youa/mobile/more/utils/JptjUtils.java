package com.youa.mobile.more.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.youa.mobile.SystemConfig;
import com.youa.mobile.common.manager.SavePathManager;
import com.youa.mobile.more.data.JptjData;
import com.youa.mobile.utils.LogUtil;

/**
 * 
 * 
 * @author Administrator
 * @since 2012-5-21
 */
public class JptjUtils {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String TAG = "JptjUtils";
	private static final int STREAM_BUFFER_SIZE = 8192;

	// ===========================================================
	// Fields
	// ===========================================================
	private static SharedPreferences SHAER_PRE = null;

	// ===========================================================
	// Methods
	// ===========================================================
	private static ArrayList<JptjData> parser(JSONArray json) {
//		LogUtil.d(TAG, "parser. json array = " + json);
		ArrayList<JptjData> list = new ArrayList<JptjData>();
		if (json != null && json.length() > 0) {
			for (int i = 0; i < json.length(); i++) {
//				LogUtil.d(TAG, "parser. json array. i = " + i);
				JSONObject object = null;
				try {
					object = json.getJSONObject(i);
					JptjData data = new JptjData(object.getString("name"), object.getString("info"), object.getString("pic"), object.getString("url"));
					list.add(data);
				} catch (JSONException e) {
//					LogUtil.e("ParserJptj", "parser.", e);
				}
			}
		}
//		LogUtil.d(TAG, "parser. list. size = " + list.size());
		return list;
	}

	public static ArrayList<JptjData> getJptj(Context context) {
		// return getSharedPreferences(context).getString(SystemConfig.KEY_JPTJ, "");
		String json = getSharedPreferences(context).getString(SystemConfig.KEY_JPTJ, "");
//		LogUtil.d(TAG, "getJptj. json = " + json);
		ArrayList<JptjData> datas = new ArrayList<JptjData>();
		if (TextUtils.isEmpty(json)) {
			return datas;
		}
		JSONArray array = null;
		try {
			array = new JSONArray(json);
		} catch (JSONException e) {
			LogUtil.e(TAG, "loadJptjData", e);
		}
		return JptjUtils.parser(array);
	}

	public static void setJptj(Context context, String json) {
		if (TextUtils.isEmpty(json)) {
			getSharedPreferences(context).edit().clear().commit();
			return;
		}
//		LogUtil.d(TAG, "setJptj. json = " + json);
		getSharedPreferences(context).edit().putString(SystemConfig.KEY_JPTJ, json).commit();
	}

	private static SharedPreferences getSharedPreferences(Context context) {
		if (SHAER_PRE == null) {
			SHAER_PRE = context.getSharedPreferences(SystemConfig.JPTJ_PREF_NAME, 0);
		}
		return SHAER_PRE;
	}

	public static void downloadImage(final int index, final String url, final DownloadListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String output = SavePathManager.changeURLToPath(url);
//				LogUtil.d(TAG, "downloadImage. output = " + output);
				File file = new File(output);
				if (!file.exists()) {
					downloadImage(output, url);
				}
				Drawable drawable = Drawable.createFromPath(file.getAbsolutePath());
				if (listener != null && drawable != null) {
					listener.onSuccess(index, drawable);
				} else if (listener != null) {
					listener.onFailure(index);
				}

			}
		}).start();
	}

	public static void downloadImage(String output, String url) {
//		LogUtil.d(TAG, "downloadImage. url = " + url + "output = " + output);
		File file = new File(output);
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 1000 * 60);
		HttpConnectionParams.setSoTimeout(httpParams, 1000 * 20);
		final DefaultHttpClient client = new DefaultHttpClient(httpParams);
		HttpEntity entity = null;
		InputStream is = null;
		FileOutputStream os = null;
		try {
			HttpResponse response = client.execute(new HttpGet(url));
			StatusLine status = response.getStatusLine();
			entity = response.getEntity();
//			LogUtil.d(TAG, "entity = " + entity + ", status. OK = " + (status.getStatusCode() == HttpStatus.SC_OK));
			if (HttpStatus.SC_OK == status.getStatusCode() && entity != null) {
				is = entity.getContent();
				os = new FileOutputStream(file);
				byte[] buffer = new byte[STREAM_BUFFER_SIZE];
				int rlen = 0;
				while ((rlen = is.read(buffer, 0, STREAM_BUFFER_SIZE)) != -1) {
					os.write(buffer, 0, rlen);
				}
				os.flush();
			} else {
				// callback.onFail();
			}
		} catch (ClientProtocolException e) {
			// callback.onFail();
			LogUtil.e(TAG, "onExecute", e);
		} catch (IOException e) {
			// callback.onFail();
			LogUtil.e(TAG, "onExecute", e);
		} catch (Exception e) {
			LogUtil.e(TAG, "onExecute", e);
			// callback.onFail();
		} finally {
			try {
				if (entity != null)
					entity.consumeContent();
			} catch (IOException e) {
				LogUtil.e(TAG, "", e);
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public interface DownloadListener {
		public void onSuccess(int index, Drawable drawable);

		public void onFailure(int index);
	}
}
