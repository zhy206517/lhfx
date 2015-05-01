package com.youa.mobile.more.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

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
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.text.TextUtils;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.manager.SavePathManager;
import com.youa.mobile.more.data.JptjData;
import com.youa.mobile.more.utils.JptjUtils;
import com.youa.mobile.utils.LogUtil;

public class JptjAction extends BaseAction<IResultListener> {

	public static final String KEY_JPTJ_URL = "key_jptj_url";
	public static final String URL_SUBFIX = "/static/third.json";

	// public interface IJptjListener extends IResultListener {
	// void onFail();
	//
	// void onFinish(JSONArray array);
	// }

	@Override
	protected void onExecute(Context context, Map<String, Object> params, IResultListener callback) throws Exception {
		String url = (String) params.get(KEY_JPTJ_URL);
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 1000 * 60);
		HttpConnectionParams.setSoTimeout(httpParams, 1000 * 20);
		final DefaultHttpClient client = new DefaultHttpClient(httpParams);
		HttpEntity entity = null;
		try {
			HttpResponse response = client.execute(new HttpGet(url));
			StatusLine status = response.getStatusLine();
			entity = response.getEntity();
			// LogUtil.d(TAG, "entity = " + entity + ", status. OK = " + (status.getStatusCode() == HttpStatus.SC_OK));
			if (HttpStatus.SC_OK == status.getStatusCode() && entity != null) {
				String json = EntityUtils.toString(entity, "utf-8");
				// LogUtil.d(TAG, "request. json=\n" + json);
				JptjUtils.setJptj(context, json);
				// download icons
				ArrayList<JptjData> list = JptjUtils.getJptj(context);
				if (list != null) {
					for (JptjData data : list) {
						if (!TextUtils.isEmpty(data.pic)) {
							// LogUtil.d(TAG, "onExecute. to download pic. url = " + data.pic);
							String output = SavePathManager.changeURLToPath(data.pic);
							if (!new File(output).exists()) {
								JptjUtils.downloadImage(output, data.pic);
							}
						}
					}
				}
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
		}
	}
}
