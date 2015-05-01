package com.youa.mobile.webshop;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.http.LhWebViewClient;

public class WebShopPage extends BasePage {
	private WebView webView;
	private ProgressDialog progressDlg;
	private ImageButton back;
	private ImageButton exit;
	private TextView titleView;
	
	public static final String IS_ONRESULT = "is_onresult";
	public static final String SHOP_TITLE = "shop_title";
	public static final String SHOP_ID = "shop_id";
	public static final String URL = "web_view_url";// 仅当shop_id不存在的时候才会获取URL参数，来加载WebView页面
	private String shopId;
	private String webShopTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_shop);
		initView();
	}

	private void initView(){
		webView = (WebView) findViewById(R.id.web_shop_webview);
		progressDlg = new ProgressDialog(this);
		progressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		progressDlg.setMessage(getString(R.string.login_third_webview_loading));
		back = (ImageButton) findViewById(R.id.back);
		exit = (ImageButton)findViewById(R.id.exit);
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.back:
						goBack();
						break;
					case R.id.exit:
						finish();
						break;
					default:
						break;
				}
			}
		};
		back.setOnClickListener(clickListener);
		exit.setOnClickListener(clickListener);
		titleView = (TextView)findViewById(R.id.title);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebShopViewClient());
		webView.setWebChromeClient(new WebShopChromeClient());
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		Bundle extras = getIntent().getExtras();
		shopId = extras.getString(SHOP_ID);
		String url = null;
		if(!TextUtils.isEmpty(shopId)){
			StringBuilder urlBuf = new StringBuilder("http://wap.leho.com/shop/").append(shopId);
			url = urlBuf.toString();
		}else{
			url = extras.getString(URL);
		}
		if(TextUtils.isEmpty(url)){
			showToast(R.string.no_url);
			setTitle(extras.getString(SHOP_TITLE));
			return;
		}
		setTitle(extras.getString(SHOP_TITLE));
		webView.loadUrl(url+"?form=lhfx");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void goBack(){
		if (webView != null) {
			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				webView.stopLoading();
				finish();
			}
		}
		if (progressDlg != null && progressDlg.isShowing()) {
			progressDlg.dismiss();
		}
	}
	
	private class WebShopViewClient extends LhWebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith("tel:")) {
	            Intent intent = new Intent(Intent.ACTION_VIEW,  Uri.parse(url)); 
	            startActivity(intent); 
	            return true;
		    }
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			showProgressDlg();
			super.onPageStarted(view, url, favicon);
		}

		private void showProgressDlg() {
			ActivityManager am = (ActivityManager) getApplicationContext()
					.getSystemService(Context.ACTIVITY_SERVICE);
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			if (cn.getClassName().equals(
					WebShopPage.this.getComponentName().getClassName())) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						progressDlg.show();
					}
				});
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			progressDlg.hide();
		}

////		@TargetApi(8)
//		@Override
//		public void onReceivedSslError(WebView view, SslErrorHandler handler,
//				SslError error) {
//			handler.proceed();// 接受证书
//		}

		@Override
		public void onPageFinished(WebView view, String url) {
			setTitle(null);
			super.onPageFinished(view, url);
		}
	}

	private class WebShopChromeClient extends WebChromeClient {
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

	@Override
	public void setTitle(CharSequence title) {
		if(TextUtils.isEmpty(webShopTitle)){
			webShopTitle = webView.getTitle();
		}
		titleView.setText(webShopTitle);
//		setTitle(webShopTitle);
//		super.setTitle(title);
	}

	@Override
	protected void onStop() {
		if (progressDlg != null && progressDlg.isShowing()) {
			progressDlg.dismiss();
		}
		super.onStop();
	}
}
