package com.youa.mobile.login;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.information.RegionSelectPage;
import com.youa.mobile.life.SuperPeopleAllPage;
import com.youa.mobile.login.action.RegistAction;
import com.youa.mobile.login.action.ThirdAccountAction;
import com.youa.mobile.login.auth.BaseAuthPage;
import com.youa.mobile.login.auth.BaseToken;
import com.youa.mobile.login.util.LoginConstant;
import com.youa.mobile.login.util.LoginUtil;

public class ThirdUserInfoConfirm extends BasePage {
	private EditText mNicknameEdit;
	private RadioGroup mSexView;
	private TextView mAddress;
	private Button mRegistButton;
	private Button mBackButton;

	private String mProvince = "";
	private String mCity = "";
	private String mCounties = "";
	public static final int REQUEST_SELECT_REGION = 100;
//	private String userid;
//	private SupportSite site;
	private BaseToken thirdToken;
	private BtnOnClickListener onClickListener;
	
	public static final String TOKEN = "token";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_userinfo_confirm);
		Intent i = getIntent();
		thirdToken = (BaseToken) i.getSerializableExtra(TOKEN);
		initUI();
	}

	private void initUI() {
		mNicknameEdit = (EditText) findViewById(R.id.user_nickname);
		mSexView = (RadioGroup) findViewById(R.id.user_sex);
		mAddress = (TextView) findViewById(R.id.user_address);
		mBackButton = (Button) findViewById(R.id.back);
		mRegistButton = (Button) findViewById(R.id.reg_third_user);
		onClickListener = new BtnOnClickListener();
		mBackButton.setOnClickListener(onClickListener);
		mAddress.setOnClickListener(onClickListener);
		mRegistButton.setOnClickListener(onClickListener);
	}

	private class BtnOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				ThirdUserInfoConfirm.this.finish();
				break;
			case R.id.reg_third_user:
				checkInfo();
				break;
			case R.id.user_address:
				Intent i = new Intent();
				i.setClass(ThirdUserInfoConfirm.this, RegionSelectPage.class);
				startActivityForResult(i, REQUEST_SELECT_REGION);
				break;
			}
		}
	}

	private void regist() {
		Map<String, Object> params = new HashMap<String, Object>();
		if (thirdToken == null || TextUtils.isEmpty(thirdToken.userid)) {
			showToast(R.string.third_get_oauth_fail);
			return;
		}
		params.put(LoginConstant.WEB_REGIST_NICKNAME, mNicknameEdit.getText()
				.toString());
		params.put(ThirdAccountAction.REQUEST_TYPE,
				ThirdAccountAction.RequestType.REG_USER_AND_LOGIN);
		params.put(ThirdAccountAction.SITE_TYPE, thirdToken.site);
		String nickName = mNicknameEdit.getText().toString();
		params.put(ThirdAccountAction.USERNIKENAME, nickName);
		params.put(ThirdAccountAction.USERID, thirdToken.userid);
		params.put(ThirdAccountAction.ACCESS_TOKEN, thirdToken.token);
		params.put(ThirdAccountAction.REFRESH_TOKEN, thirdToken.reFreshToken);
		params.put(ThirdAccountAction.EXP_TIME, thirdToken.expTime);

		params.put(LoginConstant.WEB_REGIST_PROVINCE, mProvince);
		params.put(LoginConstant.WEB_REGIST_CITY, mCity);
		params.put(LoginConstant.WEB_REGIST_RESION, mCounties);
		int checkId = mSexView.getCheckedRadioButtonId();
		if (checkId == R.id.user_sex_man) {
			params.put(LoginConstant.WEB_REGIST_SEX, RegistAction.SEX_MAN);
		} else if (checkId == R.id.user_sex_womain) {
			params.put(LoginConstant.WEB_REGIST_SEX, RegistAction.SEX_WOMAN);
		} else {
			params.put(LoginConstant.WEB_REGIST_SEX, "");
		}
		ActionController.post(this, ThirdAccountAction.class, params,
				new ThirdAccountAction.ThirdResultListener() {

					@Override
					public void onStart() {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(ThirdUserInfoConfirm.this,
										R.string.login_third_loginng,
										Toast.LENGTH_LONG).show();
							}
						});
					}

					@Override
					public void onFinish(final int resourceID) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								showToast(resourceID);
								if (TextUtils.isEmpty(BaseAuthPage.ACTION_NAME)) {
									ApplicationManager.getInstance().init(
											ThirdUserInfoConfirm.this);
									Intent intent = new Intent(
											ThirdUserInfoConfirm.this,
											SuperPeopleAllPage.class);
									intent.putExtra("thirdUid", thirdToken.userid);
									intent.putExtra("thirdType", thirdToken.site);
									startActivity(intent);
								} else {
									ThirdUserInfoConfirm.this
											.sendBroadcast(new Intent(
													BaseAuthPage.ACTION_NAME));
								}
								ThirdUserInfoConfirm.this.finish();
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
								showToast(resourceID);
							}
						});
					}
				}, true);

	}

	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {

		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case REQUEST_SELECT_REGION:
			mProvince = data
					.getStringExtra(RegionSelectPage.RESULT_ADDRESS_PRVINCE);
			mCity = data.getStringExtra(RegionSelectPage.RESULT_ADDRESS_CITY);
			mCounties = data
					.getStringExtra(RegionSelectPage.RESULT_ADDRESS_COUNTIES);
			mAddress.setText(getAddress());
			break;
		}
	}

	private String getAddress() {
		return mCity + " " + mCounties;
	}

	private void checkInfo() {
		// 注册时只需要用户名（昵称）信息
		String nickName = mNicknameEdit.getText().toString();
		if (!LoginUtil.isUsernameAvailable(nickName)
				|| nickName.getBytes().length < 4
				|| nickName.getBytes().length > 30) {
			showToast(R.string.regist_error_nickname);
			mNicknameEdit.requestFocus();
			return;
		}
		if (mProvince == null || mCity == null || "".equals(mProvince)
				|| "".equals(mCity)) {
			showToast(R.string.regist_error_address);
			return;
		}
		regist();
	}
}
