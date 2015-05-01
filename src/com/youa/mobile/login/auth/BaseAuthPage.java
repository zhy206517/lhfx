package com.youa.mobile.login.auth;

import android.content.Intent;

import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.login.ThirdLoginWebPage;

public abstract class BaseAuthPage extends BasePage {
	public static final int AUTH_REQUEST_CODE = 10000;
	public static final String TOKEN_DATA = "token_data";
	public static String ACTION_NAME;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		switch () {
//		case LoginBasePage.REQUEST_FROM_LOGIN:
//			if (resultCode == RESULT_OK) {
//				finish();
//			}
//			break;
		if(AUTH_REQUEST_CODE == requestCode){
			if (data != null) {
				BaseToken tokenData = (BaseToken) data
						.getSerializableExtra(TOKEN_DATA);
					onAuthResult(tokenData);
			} else {
				onAuthResult(null);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void setActionName(String name) {
		ACTION_NAME = name;
	}

	protected void showAuthPage(String url) {
		Intent i = new Intent(BaseAuthPage.this, ThirdLoginWebPage.class);
		i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		i.putExtra(ThirdLoginWebPage.URL, url);
		startActivityForResult(i, AUTH_REQUEST_CODE);
	}

	public abstract void onAuthResult(BaseToken tokenData);
}
