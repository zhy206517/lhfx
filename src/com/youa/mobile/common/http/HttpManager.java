package com.youa.mobile.common.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.youa.mobile.DebugMode;
import com.youa.mobile.R;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.manager.LoginCommonDataStore;
import com.youa.mobile.common.manager.NetworkStatus;
import com.youa.mobile.common.params.CommonParam;
import com.youa.mobile.common.params.ServerAddressUtil;
import com.youa.mobile.common.util.LogFile;
import com.youa.mobile.login.manager.LoginManager;
import com.youa.mobile.login.util.LoginConstant;
import com.youa.mobile.utils.Md5;

public class HttpManager {
	private static final boolean IS_DEBUG = DebugMode.debug;
	private static final String TAG = "HttpManager";
	final public static String API_KEY = "1";
	// final public static String LOGIN_SECRETKEY = "YOUAJTMD52010";
	final public static String FROM = "JT";

	private DefaultHttpClient httpclient = null;// new DefaultHttpClient();
	// private HttpContext httpcontext = new BasicHttpContext();
	// private FHCookieStore cookieStore = null;// new FHCookieStore();
	private static final HttpManager httpManager = new HttpManager();

	public HttpManager() {
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params, 10);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));

		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
				params, schemeRegistry);
		httpclient = new DefaultHttpClient(cm, params);
		httpclient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				10000);
	}

	public static HttpManager getInstance() {
		return httpManager;
	}

	private HttpRes postPri(String method, Map<String, String> params,
			Context appCtx) throws MessageException, ClientProtocolException,
			IOException {
		if (IS_DEBUG) {
			Log.d(TAG, "post-method:" + method);
		}
		// this.updateCookieStore(appCtx);

		return execPost(method, params, appCtx);
	}

	//
	// public HttpRes post(
	// String url,
	// Map<String, String> params,
	// Context appCtx,
	// boolean isNeedCommonParam) throws MessageException {
	// return post(url, params, null, appCtx);
	// }

	public HttpRes post(String method, Map<String, String> params,
			Context appCtx) throws MessageException {
		if (!NetworkStatus.isNetworkAvailable(appCtx))
			throw new MessageException(CommonParam.VALUE_ERROR_NETWORK_ERROR,
					R.string.common_network_not_available);

		try {
			addCommonParam(method, params, appCtx);
			HttpRes res = postPri(method, params, appCtx);
			if (res.getCode().equals(CommonParam.VALUE_ERROR_NOT_LOGIN)
					|| res.getCode().equals(
							CommonParam.VALUE_ERROR_SESSION_ERROR)) {
//				if (autoLogin(appCtx)) {
//					return postNoLogin(method, params, appCtx);
//				}else{
					throw new MessageException(CommonParam.VALUE_ERROR_AUTOLOGIN_FAIL);
//				}
			}
			return res;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new MessageException(CommonParam.VALUE_ERROR_NETWORK_ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			throw new MessageException(CommonParam.VALUE_ERROR_NETWORK_ERROR);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			throw new MessageException(CommonParam.VALUE_ERROR_NETWORK_ERROR);
		}
	}

	// public HttpRes post(String url, Context appCtx, String jsonStr) throws
	// MessageException {
	// if (!NetworkStatus.isNetworkAvailable(appCtx))
	// throw new MessageException(HttpRes.NETWORK_ERROR,
	// R.string.common_network_not_available);
	//
	// try {
	//
	// HttpRes res = post(url, jsonStr, appCtx);
	// if (res.getCode() == HttpRes.NOT_LOGIN) {
	// if (onNeedReLogin(appCtx)) {
	// return postNoLogin(url, jsonStr, appCtx);
	// }
	// }
	// return res;
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// throw new MessageException(R.string.common_network_error);
	// } catch (IOException e) {
	// e.printStackTrace();
	// throw new MessageException(R.string.common_network_error);
	// }
	// }
	//
	// private HttpRes post(String url, String jsonStr, Context appCtx) throws
	// MessageException, ClientProtocolException, IOException {
	// return post(url, jsonStr, fileParams, appCtx, true);
	// }

	private HttpRes execPost(String method, Map<String, String> params,
			Context appCtx) throws MessageException, ClientProtocolException,
			IOException {
		// this.updateCookieStore(appCtx);
		HttpPost httppost = new HttpPost(ServerAddressUtil.HTTP_SERVER);
		if (IS_DEBUG) {
//			logDebugInfo(appCtx, method, params);
		}

		httppost.addHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=UTF-8");// 取数据

		// httpclient.
		// httppost.
		// byte[] enc_str = DESCoder.getInstance().encrypt(text);
		byte[] data = computeParams(params);
		httppost.setEntity(new ByteArrayEntity(data));
		// httppost.setEntity(new ByteArrayEntity(enc_str));
		HttpResponse response = httpclient.execute(httppost);
		// System.out.println("execPost-time:" + System.currentTimeMillis());
		InputStream stream = response.getEntity().getContent();

		int statusCode = response.getStatusLine().getStatusCode();
		Log.i(TAG, "statusCode:" + statusCode);
		if (statusCode == 200) {
			 stream = new GZIPInputStream(stream);
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(stream,
				"UTF-8"));
		StringBuffer buf = new StringBuffer();
		String line;
		while (null != (line = br.readLine())) {
			if (buf.length() > 0) {
				buf.append('\n').append(line);
			} else {
				buf.append(line);
			}
		}
		stream.close();
		// saveCookieStore(appCtx);
		String str = buf.toString();
		if (IS_DEBUG) {
//			LogFile logFile = LogFile.getInstance(appCtx);
//			logFile.writeOneLine(str);
			System.out.println("data:" + str);
		}

		HttpRes res = null;
		res = HttpRes.create(str);
		return res;
	}

	public static InputStream execPostStatic(String method, Map<String, String> params,
			Context appCtx) throws MessageException, ClientProtocolException,
			IOException {
		// this.updateCookieStore(appCtx);
		HttpPost httppost = new HttpPost(ServerAddressUtil.HTTP_SERVER);
		if (IS_DEBUG) {
//			logDebugInfo(appCtx, method, params);
		}

		httppost.addHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=UTF-8");// 取数据

		// httpclient.
		// httppost.
		// byte[] enc_str = DESCoder.getInstance().encrypt(text);
		byte[] data = computeParams(params);
		httppost.setEntity(new ByteArrayEntity(data));
		// httppost.setEntity(new ByteArrayEntity(enc_str));
		HttpParams httpparams = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(httpparams, 10);
		HttpProtocolParams.setVersion(httpparams, HttpVersion.HTTP_1_1);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));

		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
				httpparams, schemeRegistry);
		DefaultHttpClient httpclient = new DefaultHttpClient(cm, httpparams);
		httpclient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 
				10000);

		HttpResponse response = httpclient.execute(httppost);
		// System.out.println("execPost-time:" + System.currentTimeMillis());
		InputStream stream = response.getEntity().getContent();

		int statusCode = response.getStatusLine().getStatusCode();
		Log.i(TAG, "statusCode:" + statusCode);
//		if (statusCode == 200) {
//			 stream = new GZIPInputStream(stream);
//		}
		return stream;
	}
	// private HttpRes postNoLogin(String url, String jsonStr, Context appCtx)
	// throws MessageException {
	// if (!NetworkStatus.isNetworkAvailable(appCtx))
	// throw new MessageException(HttpRes.NETWORK_ERROR,
	// R.string.common_network_not_available);
	//
	// try {
	// // addCommonParam(params, appCtx);
	// HttpRes res = post(url, jsonStr, appCtx);
	// if (res.getCode() == HttpRes.NOT_LOGIN) {
	// throw new MessageException(R.string.common_network_error);
	// }
	// return res;
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// throw new MessageException(R.string.common_network_error);
	// } catch (IOException e) {
	// e.printStackTrace();
	// throw new MessageException(R.string.common_network_error);
	// } catch (MessageException e) {
	// if(e.getErrCode() == HttpRes.PARAM_ERROR) {
	// e.setResID(R.string.common_error_password_error_gotologin);
	// }
	// throw e;
	// }
	// }

	/***
	 * 调用Login，失败DeprecatedAccount重新激活
	 * 
	 * @param appCtx
	 * @return 成功返回true,失败返回false
	 * @throws MessageException
	 */
//	public static boolean autoLogin(Context context) throws MessageException {
//		// TO
//        boolean isAuto=false;
//		try {
//			LoginManager loginManager = new LoginManager();
//			isAuto=loginManager.autoLogin(context);
//		} catch (MessageException e) {// TODO 这块是提示异常，还是返回登录界面呢？ 想用异常来区分
//			e.printStackTrace();
//			throw new MessageException(CommonParam.VALUE_ERROR_AUTOLOGIN_FAIL);
//		}
//		return isAuto;
//	}

	private HttpRes postNoLogin(String method, Map<String, String> params,
			Context appCtx) throws MessageException {
		if (!NetworkStatus.isNetworkAvailable(appCtx))
			throw new MessageException(CommonParam.VALUE_ERROR_NETWORK_ERROR,
					R.string.common_network_not_available);

		try {
			addCommonParam(method, params, appCtx);
			HttpRes res = postPri(method, params, appCtx);
			if (res.getCode().equals(CommonParam.VALUE_ERROR_NOT_LOGIN)
					|| res.getCode().equals(
							CommonParam.VALUE_ERROR_SESSION_ERROR)) {
				throw new MessageException(
						CommonParam.VALUE_ERROR_NETWORK_ERROR);
			}
			return res;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new MessageException(CommonParam.VALUE_ERROR_NETWORK_ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			throw new MessageException(CommonParam.VALUE_ERROR_NETWORK_ERROR);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			throw new MessageException(CommonParam.VALUE_ERROR_NETWORK_ERROR);
		}
	}

	public static void addCommonParam(String method, Map<String, String> params,
			Context appCtx) throws NameNotFoundException {
		params.put(CommonParam.KEY_METHOD, method);
		params.put(CommonParam.KEY_API_KEY, API_KEY);
		if (!params.containsKey(CommonParam.KEY_FROM)) {
			params.put(CommonParam.KEY_FROM, FROM);
		}
		params.put(CommonParam.KEY_FORMAT, "JSON");
		//String uid = ApplicationManager.getInstance().getUserId();
		String sessionId = LoginCommonDataStore.getData(appCtx,
				LoginCommonDataStore.KEY_SESSIONID, "");
		String youaindentity = LoginCommonDataStore.getData(appCtx,
				LoginCommonDataStore.KEY_YOUAINDENTITY, "");
		if (!LoginConstant.WEB_LOGIN_METHOD.equals(method)) {
			params.put(CommonParam.KEY_SESSIOID, sessionId);
			params.put(CommonParam.KEY_YOUAINDENTIRY, youaindentity);
		} else {
			params.remove(CommonParam.KEY_SESSIOID);
			params.remove(CommonParam.KEY_YOUAINDENTIRY);
		}
		params.put(CommonParam.KEY__IE, "utf-8");
		int versionCode = ApplicationManager.getInstance().getVersionCode();
		params.put(CommonParam.KEY_V, String.valueOf(versionCode));
		params.put("needCompress", "1");
		// T
	}

	public HttpRes postFile(String method, Map<String, String> params,
			Map<String, File> files, Context appCtx) throws MessageException {
		if (!NetworkStatus.isNetworkAvailable(appCtx))
			throw new MessageException(CommonParam.VALUE_ERROR_NETWORK_ERROR,
					R.string.common_network_not_available);

		try {
			addCommonParam(method, params, appCtx);
			String sign = httpManager.getSign(params);
			params.put("sign", sign);
			System.out.println("params begin:" + params.toString());
			System.out.println("files begin:" + params.toString());
			HttpRes res = postFile(method, params, files);

			if (res.getCode().equals(CommonParam.VALUE_ERROR_NOT_LOGIN)
					|| res.getCode().equals(
							CommonParam.VALUE_ERROR_SESSION_ERROR)) {
//				if (autoLogin(appCtx)) {
//					return postNoLogin(method, params, appCtx);
//				}else{
					throw new MessageException(CommonParam.VALUE_ERROR_AUTOLOGIN_FAIL);
//				}
			}
			return res;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new MessageException(CommonParam.VALUE_ERROR_NETWORK_ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			throw new MessageException(CommonParam.VALUE_ERROR_NETWORK_ERROR);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			throw new MessageException(CommonParam.VALUE_ERROR_NETWORK_ERROR);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			throw new MessageException(CommonParam.VALUE_ERROR_NETWORK_ERROR);

		}
	}

	private static HttpRes postFile(String actionUrl,
			Map<String, String> params, Map<String, File> files)
			throws IOException, MessageException {
		String BOUNDARY = "---------------------------7da2137580612"; // 数据分隔线
		// String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		URL uri = new URL(ServerAddressUtil.HTTP_SERVER);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(5 * 1000); // 缓存的最长时间
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false); // 不允许使用缓存
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
				+ ";boundary=" + BOUNDARY);

		// 首先组拼文本类型的参数
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}

		DataOutputStream outStream = new DataOutputStream(
				conn.getOutputStream());
		outStream.write(sb.toString().getBytes());
		// 发送文件数据
		if (files != null) {
			int i = 0;
			for (Map.Entry<String, File> fileEntry : files.entrySet()) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1.append("Content-Disposition: form-data; name=\""
						+ fileEntry.getKey() + "\"; filename=\""
						+ fileEntry.getValue().getAbsolutePath() + "\""
						+ LINEND);
				sb1.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());

				InputStream is = new FileInputStream(fileEntry.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}

				is.close();
				outStream.write(LINEND.getBytes());
			}
		}

		// 请求结束标志
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();

		// 得到响应码
		int res = conn.getResponseCode();
		InputStream in = null;
		StringBuilder sb2 = new StringBuilder();
		Log.d(TAG, "res:" + res);
		if (res == 200) {
			in = conn.getInputStream();
			in = new GZIPInputStream(in);
			int ch;

			while ((ch = in.read()) != -1) {
				sb2.append((char) ch);
			}
		}
		String result = sb2.toString();
		System.out.println("data:" + result);
		return in == null ? null : HttpRes.create(result);
	}

	private void logDebugInfo(Context appCtx, String method,
			Map<String, String> params) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuilder sb = new StringBuilder();
		for (String key : params.keySet()) {
			String value = params.get(key);
			if (value == null || value.length() == 0) {
				continue;
			}
			if (sb.length() > 0)
				sb.append('&');
			// sb.append(key).append('=')
			// .append(URLEncoder.encode(value, "UTF-8"));
			sb.append(key).append('=').append(value);
		}
		String text = sb.toString();
		LogFile logFile = LogFile.getInstance(appCtx);
		logFile.writeOneLine(sdf.format(new Date()) + "  " + text);
		Log.i("BackDoor", "insert one msg");
		// Log.d(TAG, "method:" + method);

	}

	public static byte[] computeParams(Map<String, String> paramMap) {
		// System.out.println("paramMap:" + paramMap.toString());
//		if(IS_DEBUG) {
//			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>:");
//			System.out.println("paramMap:" + paramMap.toString());
//			System.out.println("############################################:");
//		}
		StringBuilder sb = new StringBuilder();
		// int n = paramMap.size();
		// ------------------------------------------
		List<String> paramsList = new ArrayList<String>();
		for (Map.Entry<String, String> param : paramMap.entrySet()) {//
			String key = param.getKey();
			String value = param.getValue();
			if (key == null || value == null) {
				continue;
			}
			paramsList.add(key + "=" + value);
			sb.append(key).append("=").append(URLEncoder.encode(value)).append("&");// URLEncoder.encode(
		}
		String[] params = new String[paramsList.size()];
		paramsList.toArray(params);
		String sig = getSign(params);
		sb.append("sign=").append(URLEncoder.encode(sig));
		String str = sb.toString();
		// String[] str1 = str.split("&");
		// for(int i = 0; i <str1.length; i++) {
		// if(str1[i].length() < 150) {
		// System.out.println("str1:" + str1[i]);
		// }
		// }
		if(IS_DEBUG) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>:");
			System.out.println("param:" + str);
			System.out.println("############################################:");
		}
		byte[] data = null;
		try {
			data = str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			data = str.getBytes();
		}
		return data;
	}

	public String getSign(Map<String, String> paramMap) {
		List<String> paramsList = new ArrayList<String>();
		for (Map.Entry<String, String> param : paramMap.entrySet()) {//
			String key = param.getKey();
			String value = param.getValue();
			if (key == null || value == null) {
				continue;
			}
			paramsList.add(key + "=" + value);
		}

		String[] params = new String[paramsList.size()];
		paramsList.toArray(params);
		String sign = getSign(params);
		return sign;
	}

	/**
	 * 
	 * @param ss
	 * 
	 * @param secretKey
	 * @return
	 */
	private static String getSign(String[] ss) {
		String secretKey = CommonParam.KEY_LOGIN_SECRETKEY;
		for (int i = 0; i < ss.length; i++) {
			for (int j = ss.length - 1; j > i; j--) {
				if (ss[j].compareTo(ss[j - 1]) < 0) {
					String temp = ss[j];
					ss[j] = ss[j - 1];
					ss[j - 1] = temp;
				}
			}
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ss.length; i++) {
			sb.append(ss[i]);
			sb.append("&");
		}
		sb.append("key=");
		sb.append(secretKey);
		// System.out.println("getSign:" + sb.toString());
		return URLEncoder.encode(Md5.toMD5(sb.toString()));
	}
}
