package com.youa.mobile.login;

import java.util.HashMap;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.renren.api.connect.android.Renren;
import com.renren.api.connect.android.Util;
import com.tencent.tauth.TencentOpenAPI;
import com.tencent.tauth.bean.OpenId;
import com.tencent.tauth.http.Callback;
import com.weibo.net.Utility;
import com.weibo.net.WeiboException;
import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.http.LhWebViewClient;
import com.youa.mobile.common.util.BaiduOpenAPIUtils;
import com.youa.mobile.login.auth.BaiduToken;
import com.youa.mobile.login.auth.BaseAuthPage;
import com.youa.mobile.login.auth.BaseToken;
import com.youa.mobile.login.auth.QQToken;
import com.youa.mobile.login.auth.RenrenToken;
import com.youa.mobile.login.auth.SinaToken;
import com.youa.mobile.login.auth.SupportSite;

public class ThirdLoginWebPage extends BasePage {
	private WebView webView;
	private ProgressDialog progressDlg;
//	private boolean isOnResult;
	public static final String URL = "startUrl";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_third_login_webpage);
		webView = (WebView) findViewById(R.id.sns_callback_webview);
		progressDlg = new ProgressDialog(this);
		progressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		progressDlg.setMessage(getString(R.string.login_third_webview_loading));
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new ThirdLoginWebViewClient());
		webView.setWebChromeClient(new ThirdLoginWebChromeClient());
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		String url = getIntent().getStringExtra(URL);
//		isOnResult = getIntent().getBooleanExtra(IS_ONRESULT, false);
		webView.loadUrl(url);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webView != null) {
				if (webView.canGoBack()) {
					webView.goBack();
				} else {
					webView.stopLoading();
				}

			}
			if (progressDlg != null) {
				progressDlg.dismiss();
			}
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private class ThirdLoginWebViewClient extends LhWebViewClient {
		private static final String LOG_TAG = "ThirdLoginWebPage.WebViewClient";

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(LOG_TAG, "Redirect URL: " + url);
			int ver = Build.VERSION.SDK_INT;
			if (ver <= 10) {
				return super.shouldOverrideUrlLoading(view, url);
			}else{
				if(progressUrl(view, url)){
					return super.shouldOverrideUrlLoading(view, url);
				}
				return false;
			}
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			int ver = android.os.Build.VERSION.SDK_INT;
			if (ver <= 10){
				progressUrl(view, url);
				return ;
			}
			ActivityManager am = (ActivityManager) getApplicationContext()
					.getSystemService(Context.ACTIVITY_SERVICE);
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			if (cn.getClassName().equals(
					ThirdLoginWebPage.this.getComponentName().getClassName())) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						progressDlg.show();
					}
				});
			}
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			progressDlg.hide();
		}

//		@Override
//		public void onReceivedSslError(WebView view, SslErrorHandler handler,
//				SslError error) {
//			handler.proceed();// 接受证书
//		}

		@Override
		public void onPageFinished(WebView view, String url) {
			if (!url.startsWith(Renren.DEFAULT_REDIRECT_URI)
					&& url.startsWith(BaseToken.URL_CALLBACK_SCHEME
							+ QQToken.CALLBACK_HOST)
					&& url.startsWith(SinaToken.CALLBACK_URI)) {
				super.onPageFinished(view, url);
				setContextTitle();
				progressDlg.hide();
			} else if (url.startsWith(BaiduToken.BAIDIRECT_URI)) {
				progressDlg.hide();
				progressUrl(view,url);
				view.clearView();
				super.onPageFinished(view, url);
			} else if (url.startsWith(BaiduToken.CANCEL_URL)) {
				super.onPageFinished(view, url);
				ThirdLoginWebPage.this.finish();
			}
		}
	}

	private class ThirdLoginWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {
			return super.onJsAlert(view, url, message, result);
		}

		public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
			if (progress == 100) {
				progressDlg.hide();
			}
			super.onProgressChanged(view, progress);
		}
	}

	private void setContextTitle() {
		String webViewTitle = webView.getTitle();
		if (webViewTitle != null && webViewTitle.length() > 0) {
			setTitle(webViewTitle);
		}
	}

	public boolean progressUrl(WebView view, String url){
		if (url.startsWith(Renren.DEFAULT_REDIRECT_URI)
				|| url.startsWith(BaiduToken.BAIDIRECT_URI)
				|| url.startsWith(BaseToken.URL_CALLBACK_SCHEME
						+ QQToken.CALLBACK_HOST)
				|| url.startsWith(SinaToken.CALLBACK_URI)) {
			Util.clearCookies(ThirdLoginWebPage.this
					.getApplicationContext());
			view.stopLoading();
			Uri uri = Uri.parse(url);
			if (url.startsWith(Renren.DEFAULT_REDIRECT_URI)) {
				uri = Uri.parse(BaseToken.URL_CALLBACK_SCHEME
						+ RenrenToken.CALLBACK_HOST
						+ "/?"
						+ url.substring((Renren.DEFAULT_REDIRECT_URI + "#")
								.length()));
			} else if (url.startsWith(BaiduToken.BAIDIRECT_URI)) {
				uri = Uri.parse(BaseToken.URL_CALLBACK_SCHEME
						+ BaiduToken.CALLBACK_HOST
						+ "/?"
						+ url.substring((BaiduToken.BAIDIRECT_URI + "#")
								.length()));
			}
			dispatchLoginFrom(uri);
			view.clearView();
//			String html ="<!DOCTYPE html><html><head><meta charset='utf-8'/><meta name='viewport' content='width=device-width, initial-scale=1'/><style type='text/css'>body{text-align:center;}</style></head><body>登陆成功</body></html>";
//			view.loadData(html, "text/html", "UTF-8");
			
			//view.canGoBack();
//			view.pauseTimers();
			return false;
		} else {
			return true;
		}
	}
	
	public void dispatchLoginFrom(Uri uri) {
		if (uri == null) {
			showToast(R.string.login_third_login_fail);
		}
		String snsHost = uri.getHost();
		if (uri.toString().startsWith(SinaToken.CALLBACK_URI)) {
			loginFormSina(uri);
		} else if (QQToken.CALLBACK_HOST.equals(snsHost)) {
			loginFormQQ(uri);
		} else if (RenrenToken.CALLBACK_HOST.equals(snsHost)) {
			loginFormRenren(uri);
		} else if (BaiduToken.CALLBACK_HOST.equals(snsHost)) {
			loginFormBaidu(uri);
		}
	}

	private void loginFormBaidu(Uri uri) {
		// lehosns://baidu.com/?expires_in=2592000&access_token=3.4b12d8bef02c2ee98f8ccb30f06eb7e4.2592000.1342342574.1202495-283794&session_secret=8b418673cbfb0de4c867173c6b619429&session_key=94rnCm6w7m62QrbwDxTYo3vQU6mGbTumMqrYkvAtnLiKhqJYwEjCxozcN0PXav0aRD%2Bm0JR7W2nkFCW2J5D2UiZvXQQ%3D&scope=basic
		if (uri.toString().contains("login_denied")) {
			finish();
			return;
		}
		String error = uri.getQueryParameter("error");
		if (error != null) {
			showToast(R.string.login_third_login_fail);
			finish();
		} else {
			try {
				String accessToken = uri.getQueryParameter("access_token");
				StringBuffer str = new StringBuffer(BaiduToken.BAIUSERINFO_URI);
				str.append("?");
				str.append("access_token=");
				str.append(accessToken);
				str.append("&format=json");
				String thirduid = BaiduOpenAPIUtils.getUid(
						ThirdLoginWebPage.this, str.toString(), "GET");
				BaiduToken token = BaiduToken
						.getInstance(ThirdLoginWebPage.this);
				token.flag = null;
				token.modifyTime = System.currentTimeMillis()/1000;
				String expTimeIn = uri.getQueryParameter("expires_in");
				long expTomeLong = 0;
				try {
					expTomeLong = token.modifyTime + Long.parseLong(expTimeIn);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				token.expTime = expTomeLong;
				token.reFreshToken = "";
				token.site = SupportSite.BAIDU;
				token.status = null;
				token.token = accessToken;
				token.userid = thirduid;
				token.isSync = true;
				token.saveToken(ThirdLoginWebPage.this);
				onAuthResult(token);
			} catch (WeiboException e) {
				e.printStackTrace();
				showToast(R.string.login_third_login_fail);
				finish();
			}
		}
	}

	private void loginFormQQ(Uri uri) {
		String tmp = uri.toString();
		if (tmp.startsWith(BaseToken.URL_CALLBACK_SCHEME
				+ QQToken.CALLBACK_HOST + "?#")) {
			tmp = tmp.substring(tmp.indexOf('#') + 1);
		} else {
			tmp = tmp.substring(tmp.indexOf('?') + 1);
		}
		String[] arr = tmp.split("&");
		HashMap<String, String> res = new HashMap<String, String>();
		for (String item : arr) {
			String[] data = item.split("=");
			res.put(data[0], data[1]);
		}
		final String mAccessToken = res.get("access_token");
		final String expiresIn = res.get("expires_in");
		TencentOpenAPI.openid(mAccessToken, new Callback() {
			@Override
			public void onSuccess(final Object obj) {
				String thirdUid = ((OpenId) obj).getOpenId();
				QQToken token = QQToken.getInstance(ThirdLoginWebPage.this);
				token.tokenSecret = "";
				token.token = mAccessToken;
				token.userid = thirdUid;
				token.modifyTime = System.currentTimeMillis()/1000;
				long expTimeLong = 0;
				try {
					expTimeLong = token.modifyTime + Long.parseLong(expiresIn);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				token.expTime = expTimeLong;
				token.flag = null;
				token.reFreshToken = "";
				token.site = SupportSite.QQ;
				token.status = null;
				token.isSync = true;
				token.saveToken(ThirdLoginWebPage.this);
				onAuthResult(token);
			}

			@Override
			public void onFail(int ret, final String msg) {
				showToast(R.string.login_third_login_fail);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						finish();
					}
				});
			}
		});
	}

	private void loginFormRenren(Uri uri) {
		if (uri.toString().contains("login_denied")) {
			finish();
			return;
		}
		String error = uri.getQueryParameter("error");// .getString("error");//
														// OAuth返回的错误代码
		if (error != null) {
			showToast(R.string.login_third_login_fail);
			finish();
		} else {
			// lehosns://renren.com/?
			// access_token=180358%7C6.fddf7fe761651ad2e4d160ae720321ff.2592000.1339142400-200219524
			// =2592766
			// &scope=publish_feed+create_album+photo_upload+read_user_album+status_update
			String accessToken = uri.getQueryParameter("access_token");
			String expiresIn = uri.getQueryParameter("expires_in");
			Renren renren = RenrenToken.getInstance(ThirdLoginWebPage.this)
					.getRenren(ThirdLoginWebPage.this);
			renren.updateAccessToken(accessToken);
			String thirduid = String.valueOf(renren.getCurrentUid());
			RenrenToken token = RenrenToken.getInstance(ThirdLoginWebPage.this);
			token.tokenSecret = renren.getSecret();
			long expTime = -1;
			try {
				expTime = System.currentTimeMillis()/1000+Long.parseLong(expiresIn);
			} catch (Exception e) { }
			token.expTime = expTime;
			token.flag = null;
			token.modifyTime = System.currentTimeMillis()/1000;
			token.reFreshToken = "";
			token.site = SupportSite.RENREN;
			token.status = null;
			token.token = accessToken;
			token.userid = thirduid;
			token.isSync = true;
			token.saveToken(ThirdLoginWebPage.this);
			onAuthResult(token);
		}
	}

	private void loginFormSina(Uri uri) {
		//Redirect URL: http://i.leho.com/psp/third/login/sina/callback#access_token=2.001II3nB0yzWud5818b35763hZqzdC&remind_in=604799&expires_in=604799&uid=1645447780
		if (uri != null) {
			Bundle values = Utility.parseUrl(uri.toString());
	        String error = values.getString("error");
	        String error_code = values.getString("error_code");
	        if (error == null && error_code == null) {
	        	SinaToken token = SinaToken.getInstance(ThirdLoginWebPage.this);
				try {
					String expiresin = values.getString("expires_in");
					token.expTime = System.currentTimeMillis()/1000 + Long.parseLong(expiresin);
				} catch (Exception e) {
					e.printStackTrace();
					token.expTime = -1;
				}
				token.flag = null;
				token.modifyTime =  System.currentTimeMillis()/1000;
				token.reFreshToken = "";
				token.site = SupportSite.SINA;
				token.status = null;
				token.token = values.getString("access_token");
				token.userid = values.getString("uid");
				if(TextUtils.isEmpty(token.token) || TextUtils.isEmpty(token.userid)){
					showToast(R.string.third_oauth_fail_access_denied);
		            finish();
				}
				token.isSync = true;
				token.saveToken(ThirdLoginWebPage.this);
				onAuthResult(token);
	        } else if (error.equals("access_denied")) {
	            // 用户或授权服务器拒绝授予数据访问权限
	        	//showToast(R.string.third_oauth_fail_access_denied);
	            finish();
	        } else {//mListener.onWeiboException(new WeiboException(error, Integer.parseInt(error_code)));
	        	showToast(R.string.login_third_login_fail);
	        }
		}
	}

	

	@Override
	protected void onStop() {
		if (progressDlg != null && progressDlg.isShowing()) {
			progressDlg.dismiss();
		}
		super.onStop();
	}
/*
	private void loginOrBind(Context c, final BaseToken token){
		if(token == null || !isOnResult){
			onAuthResult(token);
			return;
		}
		UserInfo user = LoginUtil.readUserFromPrefForAutoLogin(c);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(ThirdAccountAction.REQUEST_TYPE,
				ThirdAccountAction.RequestType.UPDATE_ACCESS_TOKEN);
		params.put(ThirdAccountAction.SITE_TYPE, token.site);
		params.put(ThirdAccountAction.USERID, token.userid);
		params.put(ThirdAccountAction.ACCESS_TOKEN, token.token);
		params.put(ThirdAccountAction.REFRESH_TOKEN, token.reFreshToken);
		params.put(ThirdAccountAction.EXP_TIME, token.expTime);
		ThirdResultListener listener = new ThirdResultListener() {

			@Override
			public void onStart() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						progressDlg.show();
					}
				});
			}

			@Override
			public void onFinish(final int resourceID) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						//showToast(resourceID);
						progressDlg.dismiss();
						onAuthResult(token);
					}
				});
			}

			@Override
			public void onStartReg() {
			}

			@Override
			public void onFail(final int resourceID) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						progressDlg.dismiss();
						showToast(resourceID);
						onAuthResult(null);
					}
				});
			}
		};
		
		ActionController.post(c, ThirdAccountAction.class, params, listener, true);
	}
*/	
	private void onAuthResult(BaseToken token) {
		Intent i = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BaseAuthPage.TOKEN_DATA, token);
		i.putExtras(bundle);
		setResult(RESULT_OK, i);
		this.finish();
	}
}
