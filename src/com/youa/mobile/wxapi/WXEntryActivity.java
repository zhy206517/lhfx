package com.youa.mobile.wxapi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.youa.mobile.LehoTabActivity;
import com.youa.mobile.LehuoIntent;
import com.youa.mobile.PreparePage;
import com.youa.mobile.R;
import com.youa.mobile.utils.LogUtil;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final String TAG = WXEntryActivity.class.getSimpleName();
	private static final String ACTION_START_CLIENT = "com.youa.mobile.enter_leho_client";
	public static final String EXTRA_WHERE_FROM = "EXTRA_WHERE_FROM";
	public static final String EXTRA_FROM_WEIXIN = "weixin";
	// 通过WXAPIFactory工厂，获取IWXAPI的实例
	private IWXAPI api;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			LogUtil.d("WXEntryActivity", "onReceive. action = " + action);
			if (ACTION_START_CLIENT.equals(action)) {
				startActivity(new Intent(WXEntryActivity.this,
						LehoTabActivity.class));
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		getWindow().setBackgroundDrawableResource(R.drawable.about_img);
		api = WXAPIFactory.createWXAPI(this, PreparePage.APP_ID, false);
		api.handleIntent(getIntent(), this);
		registerReceiver(mReceiver, new IntentFilter(ACTION_START_CLIENT));
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		Bundle bundle = new Bundle();
		req.toBundle(bundle);
		LogUtil.d(TAG, "onReq. req = " + bundle.toString());
		// startActivity(new Intent(this, MainActivity.class));
		Intent intent = new Intent(this, PreparePage.class);
		intent.putExtra(EXTRA_WHERE_FROM, EXTRA_FROM_WEIXIN);
		startActivity(intent);
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		Bundle bundle = new Bundle();
		resp.toBundle(bundle);
		LogUtil.d(TAG, "onReq. req = " + bundle.toString());
		int result = 0;
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success;
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}
		Intent i = new Intent(LehuoIntent.WEIXIN_SEND_SUCCESS);
		i.putExtra("msginfo", result);
		i.putExtra("msgcode", resp.errCode);
		sendBroadcast(i);
		finish();
	}
}