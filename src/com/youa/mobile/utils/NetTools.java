package com.youa.mobile.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpHost;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetTools {
	// secretKey = YOUAJTMD52010
	public static byte[] computeParams(String[] props, String[] values,
			String secretKey) {
		StringBuilder sb = new StringBuilder();
		int n = props.length;
		// ------------------------------------------
		String[] params = null;
		params = new String[n];
		for (int i = 0; i < n; i++) {//
			params[i] = props[i] + "=" + values[i];
			sb.append(props[i]).append("=").append(values[i]).append("&");// URLEncoder.encode(
		}
		String sig = getSign(params, secretKey);
		sb.append("sign=").append(URLEncoder.encode(sig));
		String str = sb.toString();
		System.out.println("param:" + str);
		byte[] data = null;
		try {
			data = str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			data = str.getBytes();
		}
		return data;
	}

	/**
	 * 
	 * @param ss
	 * 
	 * @param secretKey
	 * @return
	 */
	public static String getSign(String[] ss, String secretKey) {
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
		return Md5.toMD5(sb.toString());
	}

	public static HttpHost getProxyHttpHost(Context context) {
		ConnectivityManager ConnMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = ConnMgr.getActiveNetworkInfo();
		String net = info.getExtraInfo();
		HttpHost proxy = null;
		if (info != null && info.getType() != ConnectivityManager.TYPE_WIFI) {
			if (net.equals("cmwap") || net.equals("uniwap")) {// 移动和联通
				proxy = new HttpHost("10.0.0.172", 80);
			} else if (net.equals("ctwap") || net.equals("ctnet")) {// 电信
				proxy = new HttpHost("10.0.0.200", 80);
			} else {

				// GPRS: APN http proxy
				String proxyHost = android.net.Proxy.getDefaultHost();
				// int proxyPort = android.net.Proxy.getDefaultPort();
				// System.out.println(proxyHost + "//" + proxyPort);
				if (proxyHost != null) {
					proxy = new HttpHost(android.net.Proxy.getDefaultHost(),
							android.net.Proxy.getDefaultPort());
				}
			}
		}
		return proxy;
	}

	public static byte[] toByteArray(InputStream input) {
		if (input == null) {
			return null;
		}
		ByteArrayOutputStream output = null;
		byte[] result = null;
		try {
			output = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024 * 100];
			int n = 0;
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
			}
			result = output.toByteArray();
		} catch (IOException e) {
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {

			}
		}
		return result;
	}

	final public static int NET_NO = 0, NET_WIFI = 1, NET_MOBILE = 2,
			NET_OTHER = 3;

	public static int checkNet(Context context) {
		ConnectivityManager mConnectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();
		if (info == null) {
			return NET_NO;
		}
		if (!info.isAvailable() || !mConnectivity.getBackgroundDataSetting()) {
			return NET_NO;
		}
		if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			return NET_WIFI; // WIFI网络
		} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
			return NET_MOBILE; //移动互联网
		} else {
			return NET_OTHER; //未知网络
		}
	}

}