package com.youa.mobile.content;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.youa.mobile.LehuoIntent;
import com.youa.mobile.PreparePage;
import com.youa.mobile.R;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.data.User;
import com.youa.mobile.login.LoginPage;
import com.youa.mobile.login.auth.BaseAuthPage;
import com.youa.mobile.login.auth.BaseToken;
import com.youa.mobile.login.auth.ShareUtil;
import com.youa.mobile.login.auth.ShareUtil.HomeDataImageType;
import com.youa.mobile.login.auth.ShareUtil.ShareToCallBack;
import com.youa.mobile.login.auth.SupportSite;
import com.youa.mobile.parser.ContentData;

public class ShareFeedPage extends BaseAuthPage implements OnClickListener {

	public static final String EXTRA_DATA_FROM_WHERE = "extra_data_from_where";
	public static final String EXTRA_DATA_FROM_WX = "extra_data_from_wx";

	protected Button mShareWeixin;
	protected Button mShareWeiBo;
	protected Button mShareQQ;
	protected Button mShareRenRen;
	protected Button mCancel;
	protected TextView title;
	private HomeData mData;
	public static String CONTEXT_OBJ = "context_obj";
	// IWXAPI 是第三方app和微信通信的openapi接口
	private IWXAPI api;
	// public static final String APP_ID = "wx00a3ed16a2dd178e";
	private static final int THUMB_SIZE = 120;
	private boolean isWXAppSupportAPI = false;
	private UpdateCountReceiver mUpdateCountReceiver = new UpdateCountReceiver();

	private ShareToCallBack shareCallBack;
	private class UpdateCountReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (LehuoIntent.WEIXIN_SEND_SUCCESS.equals(action)) {
				// String msginfo= intent.getStringExtra("msginfo");
				int msgcode = intent.getIntExtra("msgcode", 0);
				if (msgcode == 0) {
					showToast(R.string.share_success);
					ShareFeedPage.this.finish();
				} else {
					Toast.makeText(
							getApplicationContext(),
							getString(R.string.share_fail,
									getString(R.string.sns_weixin)),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.alpha(0)));
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(this, PreparePage.APP_ID, false);
		isWXAppSupportAPI = api.isWXAppSupportAPI();
		// if (isWXAppSupportAPI) {
		// api.registerApp(PreparePage.APP_ID);
		// }
		setContentView(R.layout.share_feed_page);
		LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.dimAmount = 0.8f;
		layoutParams.x = 0;
		layoutParams.y = 0;
		layoutParams.width = -1;
		layoutParams.height = -1;
		getWindow().setAttributes(layoutParams);
		initView();
		IntentFilter fileter = new IntentFilter();
		fileter.addAction(LehuoIntent.WEIXIN_SEND_SUCCESS);
		registerReceiver(mUpdateCountReceiver, fileter);
		mData = (HomeData) getIntent().getSerializableExtra(CONTEXT_OBJ);
		// loadThirdAccountList();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mUpdateCountReceiver);
		// api.unregisterApp();
		super.onDestroy();
	}

	private void initView() {
		title = (TextView) findViewById(R.id.title);
		mShareWeixin = (Button) findViewById(R.id.share_weixin);
		// mShareWeixin.setVisibility(View.GONE);
		mShareWeiBo = (Button) findViewById(R.id.share_weibo);
		mShareQQ = (Button) findViewById(R.id.share_qq);
		mShareRenRen = (Button) findViewById(R.id.share_renren);
		mCancel = (Button) findViewById(R.id.cancel);
		mShareWeiBo.setOnClickListener(this);
		mShareQQ.setOnClickListener(this);
		mShareRenRen.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		mShareWeixin.setOnClickListener(this);

		Intent intent = getIntent();
		if (intent != null) {
			String where = intent.getStringExtra(EXTRA_DATA_FROM_WHERE);
			if (!TextUtils.isEmpty(where) && EXTRA_DATA_FROM_WX.equals(where)) {
				mShareQQ.setVisibility(View.GONE);
				mShareRenRen.setVisibility(View.GONE);
				mShareWeiBo.setVisibility(View.GONE);
			}
		}
		shareCallBack = new ShareToListener();
	}

	@Override
	public void onClick(View v) {
		onButtonClick(v.getId());
	}

	// private void loadThirdAccountList(){
	// mHandler.post(new Runnable() {
	// @Override
	// public void run() {
	// Map<String, Object> params = new HashMap<String, Object>();
	// params.put(SyncSettingAction.REQUEST_TYPE, RequestType.THIRD_SYNC_LIST);
	// ActionController.post(ShareFeedPage.this, SyncSettingAction.class,
	// params, syncSettingListener, true);
	// }
	// });
	// }

	private void onButtonClick(int btnId) {
		switch (btnId) {
		case R.id.share_weixin:
			if (isWXAppSupportAPI) {
				sendWeixin();
			} else {
				new AlertDialog.Builder(ShareFeedPage.this)
						.setMessage("请下载最新的微信")
						.setPositiveButton(R.string.common_ok,
								new Dialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent i = new Intent(
												Intent.ACTION_VIEW);
										i.setData(Uri
												.parse("http://weixin.qq.com/m"));
										startActivity(i);
										finish();
									}
								})
						.setNeutralButton(R.string.common_cancel,
								new Dialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			}
			break;
		case R.id.share_qq:
			checkToken(SupportSite.QQ);
			break;
		case R.id.share_renren:
			checkToken(SupportSite.RENREN);
			break;
		case R.id.share_weibo:
			checkToken(SupportSite.SINA);
			break;
		case R.id.cancel:
			setResult(RESULT_CANCELED);
			finish();
			break;
		}
	}

	private static final String PREFIXURL = "http://i.leho.com/post/";
	private static final String SUBFIXURL = "?type=wap&hmsr=乐活分享&hmmd=&hmpl=&hmkw=&hmci=&from=lhfx&ioslink=http://itunes.apple.com/us/app/ai-le-huo-fa-xian-ni-dui-sheng/id522048671?ls=1&androidlink=http://st.youa.com/resource/wl/leho.apk&startapp=startlh://com.youa.mobile.enter_leho_client";

	private void sendWeixin() {
		User user = null;
		if (!"0".equals(mData.PublicUser.feedType)) {
			user = mData.originUser;
		} else {
			user = mData.PublicUser;
		}
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = PREFIXURL + user.postId + SUBFIXURL;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = this.getString(R.string.sns_share_title, user.name);
		msg.description = getContent(user);
		msg.thumbData = ImageUtil.getThumbDataByteArray(this, user, THUMB_SIZE,
				THUMB_SIZE, true);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		boolean flg = api.sendReq(req);
		if (flg) {
			finish();
		} else {
			Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private static String getContent(User user) {
		String str;
		ContentData[] contents = user.contents;
		StringBuffer content = new StringBuffer();
		if (contents != null) {
			for (ContentData data : contents) {
				content.append(data.str);
				if (data.type == ContentData.TYPE_AT)
					content.append(" ");
			}
		}
		if (content != null && content.length() > 80) {
			str = content.toString().substring(0, 80);
			str += ".....";
		} else {
			str = content.toString();
		}
		return str;
	}

	private void checkToken(SupportSite site){
		if (!ApplicationManager.getInstance().isLogin()) {
			startLoginActivity();
			finish();
			return;
		}
		checkToken(site, true);
	}
	
	private void checkToken(final SupportSite site, final boolean atuoOpenAuthPage) {
		new Thread(){
			public void run() {
				ShareUtil.shareTo(mData, ShareFeedPage.this, site, HomeDataImageType.CACHED_IMG, atuoOpenAuthPage, shareCallBack);
			};
		}.start();
	}

	@Override
	public void onAuthResult(BaseToken token) {
		if (token == null || token.site == null)
			return;
		checkToken(token.site, false);
	}
	
	
	public class ShareToListener implements ShareToCallBack {

		@Override
		public void onStart() {
			showTost(ShareFeedPage.this.getString(R.string.sns_shareding));
		}

		@Override
		public void onSuccess(SupportSite site) {
			String msg =ShareFeedPage.this.getString(R.string.share_sns_success,
					site.getSiteName(ShareFeedPage.this));
			showTost(msg);
			ShareFeedPage.this.finish();
		}

		@Override
		public void onFail(SupportSite site, final String msg) {
			String msgStr = ShareFeedPage.this.getString(R.string.share_sns_fail_msg,
					site.getSiteName(ShareFeedPage.this), msg);
			showTost(msgStr);
		}

		private void showTost(final String msg) {
			ShareFeedPage.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Handler mHander = new Handler();
					mHander.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(ShareFeedPage.this.getApplicationContext(), msg,
									Toast.LENGTH_SHORT).show();
						}
					});
				}
			});
		}
	}

	
//	private ShareToCallBack shareCallBack = new ShareToCallBack() {
//		@Override
//		public void onSuccess() {
//			mHandler.post(new Runnable() {
//				@Override
//				public void run() {
//					showToast(R.string.share_success);
//					finish();
//				}
//			});
//
//		}
//
//		@Override
//		public void onStart() {
//			// TODO Auto-generated method stub
//			showToast(R.string.wait);
//		}
//
//		@Override
//		public void onFail(final int arg0, final int arg1) {
//			mHandler.post(new Runnable() {
//				@Override
//				public void run() {
//					Toast.makeText(getApplicationContext(),
//							getString(arg0, getString(arg1)),
//							Toast.LENGTH_SHORT).show();
//				}
//			});
//		}
//	};

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}
}
