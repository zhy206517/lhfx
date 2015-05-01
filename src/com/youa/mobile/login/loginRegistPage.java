package com.youa.mobile.login;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.youa.mobile.LehoTabActivity;
import com.youa.mobile.R;
import com.youa.mobile.YoumentEvent;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.information.RegionSelectPage;
import com.youa.mobile.life.SuperPeopleAllPage;
import com.youa.mobile.login.action.GetSMSCodeAction;
import com.youa.mobile.login.action.LoginAction;
import com.youa.mobile.login.action.RegistAction;
import com.youa.mobile.login.util.LoginConstant;
import com.youa.mobile.login.util.LoginUtil;

public class loginRegistPage extends LoginBasePage implements TextWatcher {

	private final static String TAG = "loginRegistPage";
//	private static final int REQUEST_CODE_PRE_USER = 0;
	public static final int REQUEST_SELECT_REGION = 2;
	private EditText mLoginUsername;
	private EditText mLoginPassword;
	private Button mLoginButton;
	private Button mRegistButton;
	private TextView mTitle;
	private ImageButton mback;

	// regist
	private EditText mRegistUsername;
	private EditText mValidationCode;
	private TextView mGetValidationCodeBtn;
	private EditText mRegistPassword;
	private EditText mRegistConfirmPass;
	private EditText mRegistNickname;
	private RadioGroup mRegistSex;
	private TextView mRegistAddress;
	private Handler mRegistHandler;
	private String mProvince = "";
	private String mCity = "";
	private String mCounties = "";
	private LoginResultListener mLoginResultListener = new LoginResultListener();
	private RegistResultListener mRegistResultListener = new RegistResultListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_regist_page);
		initViews();
	}

	private void initViews() {
		mRegistHandler = new Handler();
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(R.string.leho_accout);
		mback = (ImageButton) findViewById(R.id.back);
		mback.setVisibility(View.VISIBLE);
		mback.setOnClickListener(onClickListener);
		mLoginUsername = (EditText) findViewById(R.id.login_username);
		mLoginUsername.setText(ApplicationManager.getInstance()
				.getLoginUserName());
		mLoginPassword = (EditText) findViewById(R.id.login_password);
		mLoginUsername.addTextChangedListener(mTextWatcher);
		mLoginPassword.addTextChangedListener(mTextWatcher);
		mLoginPassword.setOnKeyListener(passwordEditListener);
		mLoginButton = (Button) findViewById(R.id.login);
		mLoginButton.setOnClickListener(onClickListener);
		setLoginBtnStatus();

		// 注册
		mRegistUsername = (EditText) findViewById(R.id.username);
		mRegistUsername.addTextChangedListener(this);
		mValidationCode = (EditText) findViewById(R.id.mobile_validation_code);
		mGetValidationCodeBtn = (TextView) findViewById(R.id.get_validation_code_btn);
		mGetValidationCodeBtn.setEnabled(false);
		mRegistPassword = (EditText) findViewById(R.id.password);
		mRegistConfirmPass = (EditText) findViewById(R.id.confirm_password);
		mRegistNickname = (EditText) findViewById(R.id.regist_nickname);
		mRegistSex = (RadioGroup) findViewById(R.id.user_sex);
		mRegistAddress = (TextView) findViewById(R.id.address);
		mRegistButton = (Button) findViewById(R.id.regist);
		mGetValidationCodeBtn.setOnClickListener(onClickListener);
		mRegistAddress.setOnClickListener(onClickListener);
		mRegistButton.setOnClickListener(onClickListener);

	}

	Button.OnClickListener onClickListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				loginRegistPage.this.finish();
				break;
			case R.id.login:
				checkInfo();
				break;
			case R.id.address:
				Intent i = new Intent();
				i.setClass(loginRegistPage.this, RegionSelectPage.class);
				startActivityForResult(i, REQUEST_SELECT_REGION);
				break;
			case R.id.regist:
				checkRegistInfo();
				break;
			case R.id.get_validation_code_btn:
				Map<String, Object> params = new HashMap<String, Object>();
				String phoneNo = mRegistUsername.getText().toString();
				if (LoginUtil.isUsernameAvailable(phoneNo)
						&& phoneNo.length() == 11) {
					mRegistHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							String phoneNo = mRegistUsername.getText()
									.toString();
							if (LoginUtil.isUsernameAvailable(phoneNo)
									&& phoneNo.length() == 11) {
								mGetValidationCodeBtn
										.setText(R.string.regist_reget_validation_code);
								mGetValidationCodeBtn.setEnabled(true);
							}
						}
					}, 3000);

					mGetValidationCodeBtn.setEnabled(false);

					params.put(LoginConstant.WEB_REGIST_PHONENUMBLE, phoneNo);
					ActionController.post(loginRegistPage.this,
							GetSMSCodeAction.class, params,
							new GetSMSCodeAction.SMSCodeListner() {
								@Override
								public void onFinish(int resourceID) {
									showToast(resourceID);
									// mGetValidationCodeBtn.setEnabled(true);
								}

								@Override
								public void onError(int resourceID) {
									showToast(resourceID);
									// mGetValidationCodeBtn.setEnabled(true);
								}
							}, true);
				} else {
					mRegistUsername.requestFocus();
					showToast(R.string.regist_error_phonenumble);
				}
				break;
			}
		}
	};

	TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			setLoginBtnStatus();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};
	OnKeyListener passwordEditListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			switch (v.getId()) {
			case R.id.password:
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& keyCode == KeyEvent.KEYCODE_ENTER)
					checkInfo();
				break;
			}
			return false;
		}
	};

	private void checkInfo() {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(loginRegistPage.this.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		LoginUtil.LOGD(TAG, "enter checkInfo  <checkInfo> : ");
		String username = mLoginUsername.getText().toString();
		String password = mLoginPassword.getText().toString();
		LoginUtil.LOGD(TAG, "enter checkInfo  <username> : " + username);
		LoginUtil.LOGD(TAG, "enter checkInfo  <password> : " + password);
		if (!LoginUtil.isUsernameAvailable(username)) {
			showToast(loginRegistPage.this, R.string.login_wrong_username);
			mLoginUsername.requestFocus();
			return;
		}
		if (!LoginUtil.isPasswordAvailable(password)) {
			showToast(loginRegistPage.this, R.string.login_wrong_password);
			mLoginPassword.requestFocus();
			return;
		}
		login();
		LoginUtil.LOGD(TAG, "quit checkInfo  <checkInfo> : ");
	}

	private void login() {
		LoginUtil.LOGD(TAG, "enter login  <login> : ");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(LoginConstant.WEB_LOGIN_USERNAME, mLoginUsername.getText()
				.toString());
		params.put(LoginConstant.WEB_LOGIN_PASSWORD, mLoginPassword.getText()
				.toString());
		ActionController.post(this, LoginAction.class, params,
				mLoginResultListener, true);
		LoginUtil.LOGD(TAG, "end login  <login> : ");
	}

	private void setLoginBtnStatus() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mLoginPassword.getText().toString().length() >= 6) {
					mLoginButton.setClickable(true);
					mLoginButton.setEnabled(true);
					LoginUtil.LOGD(TAG, "mLoginButton Clickable(true)");
				} else {
					mLoginButton.setClickable(false);
					mLoginButton.setEnabled(false);
					LoginUtil.LOGD(TAG, "mLoginButton Clickable(false)");
				}
			}
		});
	}

	private class LoginResultListener implements
			LoginAction.ILoginResultListener {

		@Override
		public void onFail(int resourceID) {
			hideProgressDialog();
			confirmDialog(getString(resourceID));
		}

		@Override
		public void onFinish() {
			LoginUtil.getSaveHomeData(loginRegistPage.this);
			LoginUtil.LOGD(TAG, "enter onFinish  <onFinish> : ");
			hideProgressDialog();
			startHomePage();
		}

		@Override
		public void onStart() {
			showProgressDialog(loginRegistPage.this, R.string.login_login,
					R.string.login_wait);
		}
	}

	private void startHomePage() {
		setResult(RESULT_OK);
		if (TextUtils.isEmpty(ACTION_NAME)) {
			Intent intent = new Intent();
			intent.setClass(this, LehoTabActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else {
			sendBroadcast(new Intent(ACTION_NAME));
		}
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK || data == null) {
			return;
		}
		switch (requestCode) {
//		case REQUEST_FROM_LOGIN:
//			if (resultCode != RESULT_CANCELED) {
//				finish();
//			}
		case REQUEST_SELECT_REGION:
			mProvince = data
					.getStringExtra(RegionSelectPage.RESULT_ADDRESS_PRVINCE);
			mCity = data.getStringExtra(RegionSelectPage.RESULT_ADDRESS_CITY);
			mCounties = data
					.getStringExtra(RegionSelectPage.RESULT_ADDRESS_COUNTIES);
			mRegistAddress.setText(mCity + " " + mCounties);
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	@Override
	public void afterTextChanged(Editable s) {
		String phoneNo = mRegistUsername.getText().toString();
		if (LoginUtil.isUsernameAvailable(phoneNo) && phoneNo.length() == 11) {
			mGetValidationCodeBtn.setEnabled(true);
		} else {
			mGetValidationCodeBtn.setEnabled(false);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// none
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// none
	}

	private void checkRegistInfo() {
		String username = mRegistUsername.getText().toString();
		String password = mRegistPassword.getText().toString();
		String conPass = mRegistConfirmPass.getText().toString();
		String nickName = mRegistNickname.getText().toString();
		String vCode = mValidationCode.getText().toString();
		if (LoginUtil.isEmpty(vCode)) {
			showToast(R.string.regist_error_vcode);
			mValidationCode.requestFocus();
			return;
		}
		if (!LoginUtil.isUsernameAvailable(username) || username.length() != 11) {
			showToast(R.string.regist_error_phonenumble);
			mRegistUsername.requestFocus();
			return;
		}
		if (!LoginUtil.isPasswordAvailable(password) || password.length() > 20
				|| password.length() < 6) {
			showToast(R.string.regist_error_password);
			mRegistPassword.requestFocus();
			return;
		}
		if (!LoginUtil.isConPasswordAvailable(password, conPass)) {
			showToast(R.string.regist_error_conpassword);
			mRegistConfirmPass.requestFocus();
			return;
		}
		if (!LoginUtil.isUsernameAvailable(nickName)
				|| nickName.getBytes().length < 4
				|| nickName.getBytes().length > 30) {
			showToast(R.string.regist_error_nickname);
			mRegistNickname.requestFocus();
			return;
		}
		if ("".equals(mProvince) || "".equals(mCity)) {
			showToast(R.string.regist_error_address);
			return;
		}
		regist();
	}

	private void regist() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(LoginConstant.WEB_REGIST_MOBILEPHONE, mRegistUsername
				.getText().toString());
		params.put(LoginConstant.WEB_REGIST_VCODE, mValidationCode.getText()
				.toString());
		params.put(LoginConstant.WEB_REGIST_PASSWORD, mRegistPassword.getText()
				.toString());
		params.put(LoginConstant.WEB_REGIST_NICKNAME, mRegistNickname.getText()
				.toString());
		// params.put(LoginConstant.WEB_LOGIN_CONFIRM_PASSWORD,
		// mRegistConfirmPass.getText().toString());
		int checkId = mRegistSex.getCheckedRadioButtonId();
		if (checkId == R.id.user_sex_male) {
			params.put(LoginConstant.WEB_REGIST_SEX, RegistAction.SEX_MAN);
		} else if (checkId == R.id.user_sex_female) {
			params.put(LoginConstant.WEB_REGIST_SEX, RegistAction.SEX_WOMAN);
		} else {
			params.put(LoginConstant.WEB_REGIST_SEX, "");
		}
		params.put(LoginConstant.WEB_REGIST_PROVINCE, mProvince);
		params.put(LoginConstant.WEB_REGIST_CITY, mCity);
		params.put(LoginConstant.WEB_REGIST_RESION, mCounties);
		ActionController.post(this, RegistAction.class, params,
				mRegistResultListener, true);
	}

	private class RegistResultListener implements
			RegistAction.IRegistResultListener {

		@Override
		public void onFail(int resourceID) {
			hideProgressDialog();
			showToast(loginRegistPage.this, resourceID);
		}

		@Override
		public void onFinish(int resourceID) {
			hideProgressDialog();
			MobclickAgent.onEvent(loginRegistPage.this,
					YoumentEvent.EVENT_REGIST);
			showToast(loginRegistPage.this, resourceID);
			mHandler.post(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					startGuide();
				}});
		}

		@Override
		public void onStart() {
			showProgressDialog(loginRegistPage.this, R.string.regist_regist,
					R.string.regist_wait);
		}
	}

	protected void startGuide() {
		setResult(RESULT_OK);
		if (TextUtils.isEmpty(ACTION_NAME)) {
			ApplicationManager.getInstance().init(loginRegistPage.this);
			Intent intent = new Intent(loginRegistPage.this,
					SuperPeopleAllPage.class);
			startActivity(intent);
		} else {
			sendBroadcast(new Intent(ACTION_NAME));
		}
		finish();
	}
}
