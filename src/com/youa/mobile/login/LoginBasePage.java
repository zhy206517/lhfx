package com.youa.mobile.login;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.youa.mobile.LehoTabActivity;
import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.login.action.ThirdAccountAction;
import com.youa.mobile.login.auth.BaseAuthPage;
import com.youa.mobile.login.auth.BaseToken;

public class LoginBasePage extends BaseAuthPage {

	public static final String KEY_FROM_WHERE = "from where";
//	public static final int REQUEST_FROM_LOGIN = 41;
	protected String fromWhere;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent callIntent = getIntent();
		fromWhere = callIntent.getStringExtra(KEY_FROM_WHERE);
	}

	protected void hideInputMethod(View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 确认对话框
	 */
	public void confirmDialog(final String message) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder ad = new AlertDialog.Builder(
						LoginBasePage.this);
				ad.setTitle(getString(R.string.common_hint));
				ad.setMessage(message);
				ad.setPositiveButton(getString(R.string.common_ok),
						new DialogInterface.OnClickListener() {// 退出按钮
							@Override
							public void onClick(DialogInterface dialog, int i) {
								// TODO Auto-generated method stub
							}
						});
				/*
				 * ad.setNegativeButton(getString(R.string.common_cancel), new
				 * DialogInterface.OnClickListener() {
				 * 
				 * @Override public void onClick(DialogInterface dialog, int i)
				 * { } });
				 */
				ad.show();// 显示对话框
			}
		});
	}

	@Override
	public void onAuthResult(BaseToken token) {
//		finish();
		checkThirdUserLogin(token);
	}
	
	private void checkThirdUserLogin(final BaseToken token) {
		if(token == null){
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(ThirdAccountAction.REQUEST_TYPE,
				ThirdAccountAction.RequestType.CHECK_THIRDUSER_AND_LOGIN);
		params.put(ThirdAccountAction.SITE_TYPE, token.site);
		params.put(ThirdAccountAction.USERID, token.userid);
		params.put(ThirdAccountAction.ACCESS_TOKEN, token.token);
		params.put(ThirdAccountAction.REFRESH_TOKEN, token.reFreshToken);
		params.put(ThirdAccountAction.EXP_TIME, token.expTime);
		ActionController.post(LoginBasePage.this, ThirdAccountAction.class,
				params, new ThirdAccountAction.ThirdResultListener() {
					@Override
					public void onStart() {
						showToast(R.string.login_third_loginng);
					}

					@Override
					public void onFinish(final int resourceID) {
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								onLoginSuccess(resourceID, token);
							}
						});
					}

					@Override
					public void onStartReg() {
						startThirdUserInfoConfirm(token);
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								showToast(R.string.login_third_first_login);
							}
						});
					}

					@Override
					public void onFail(final int resourceID) {
						onLoginFail(resourceID);
					}
				}, true);
	}

	private void startThirdUserInfoConfirm(BaseToken token) {
		Intent i = new Intent(LoginBasePage.this,
				ThirdUserInfoConfirm.class);
		i.putExtra(ThirdUserInfoConfirm.TOKEN, token);
		startActivity(i);
		finish();
	}

	private void onLoginFail(int resourceId) {
		showToast(resourceId);
	}

	private void onLoginSuccess(int resourceId,final BaseToken token) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (TextUtils.isEmpty(BaseAuthPage.ACTION_NAME)) {
					ApplicationManager.getInstance().init(
							LoginBasePage.this);
					Intent intent = new Intent(LoginBasePage.this,
							LehoTabActivity.class);
					startActivity(intent);
				} else {
					LoginBasePage.this.sendBroadcast(new Intent(
							BaseAuthPage.ACTION_NAME));
				}
				token.isSync = true;
				token.saveToken(LoginBasePage.this);
				finish();
			}
		});
	}
}
