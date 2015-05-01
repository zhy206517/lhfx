package com.youa.mobile.login;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.youa.mobile.R;
import com.youa.mobile.YoumentEvent;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.information.RegionSelectPage;
import com.youa.mobile.life.SuperPeopleAllPage;
import com.youa.mobile.login.action.GetSMSCodeAction;
import com.youa.mobile.login.action.RegistAction;
import com.youa.mobile.login.util.LoginConstant;
import com.youa.mobile.login.util.LoginUtil;

public class RegistPage extends BasePage implements TextWatcher {
	
	public static final int REQUEST_SELECT_REGION = 2;
	
	public static final String FROM_REGIST = "from_reg";
	
	private EditText mRegistUsername;
	private EditText mValidationCode;
	private TextView mGetValidationCodeBtn;
	private EditText mRegistPassword;
	private EditText mRegistConfirmPass;
	private EditText mRegistNickname;
	private RadioGroup mRegistSex;
	private TextView mRegistAddress;
	private Button mRegistButton;
	private ImageButton mBackButton;
	private TextView mTitle;
	
	private Handler mRegistHandler;
	
	private String mProvince = "";
	private String mCity = "";
	private String mCounties ="";
	
	private RegistResultListener mRegistResultListener = new RegistResultListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_regist);
		ApplicationManager.getInstance().init(RegistPage.this);
		mRegistHandler = new Handler();
		
		initViews();
	}

	private void initViews() {
		mRegistUsername = (EditText) findViewById(R.id.username);
		mRegistUsername.addTextChangedListener(this);
		mValidationCode = (EditText) findViewById(R.id.mobile_validation_code);
		mGetValidationCodeBtn = (TextView) findViewById(R.id.get_validation_code_btn);
		mGetValidationCodeBtn.setEnabled(false);
		mRegistPassword = (EditText) findViewById(R.id.password);
		mRegistConfirmPass = (EditText) findViewById(R.id.confirm_password);
		mRegistNickname = (EditText) findViewById(R.id.regist_nickname);
		mRegistSex = (RadioGroup)findViewById(R.id.user_sex);
		mRegistAddress = (TextView) findViewById(R.id.address);
		mRegistButton = (Button) findViewById(R.id.regist);
		mGetValidationCodeBtn.setOnClickListener(onClickListener);
		mRegistAddress.setOnClickListener(onClickListener);
		mRegistButton.setOnClickListener(onClickListener);
		mBackButton.setOnClickListener(onClickListener);
	}

	Button.OnClickListener onClickListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.regist:
				checkInfo();
				break;
			case R.id.back:
				setResult(RESULT_CANCELED);
				finish();
				break;
			case R.id.address:
				Intent i = new Intent(); 
				i.setClass(RegistPage.this, RegionSelectPage.class);
				startActivityForResult(i, REQUEST_SELECT_REGION);
				break;
			case R.id.get_validation_code_btn:
				Map<String, Object> params = new HashMap<String, Object>();
				String phoneNo = mRegistUsername.getText().toString();
				if(LoginUtil.isUsernameAvailable(phoneNo) && phoneNo.length() == 11){
					mRegistHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							String phoneNo = mRegistUsername.getText().toString();
							if(LoginUtil.isUsernameAvailable(phoneNo) && phoneNo.length() == 11){
								mGetValidationCodeBtn.setText(R.string.regist_reget_validation_code);
								mGetValidationCodeBtn.setEnabled(true);
							}
						}
					}, 3000);
					
					mGetValidationCodeBtn.setEnabled(false);
					
					params.put(LoginConstant.WEB_REGIST_PHONENUMBLE, phoneNo);
					ActionController.post(RegistPage.this, GetSMSCodeAction.class, params, new GetSMSCodeAction.SMSCodeListner(){
						@Override
						public void onFinish(int resourceID) {
							showToast(resourceID);
							//mGetValidationCodeBtn.setEnabled(true);
						}
						@Override
						public void onError(int resourceID) {
							showToast(resourceID);
							//mGetValidationCodeBtn.setEnabled(true);
						}
					}, true);			
				}else{
					mRegistUsername.requestFocus();
					showToast(R.string.regist_error_phonenumble);
				}
				break;
			}
		}
	};

	private void checkInfo() {
		String username = mRegistUsername.getText().toString();
		String password = mRegistPassword.getText().toString();
		String conPass = mRegistConfirmPass.getText().toString();
		String nickName = mRegistNickname.getText().toString();
		String vCode = mValidationCode.getText().toString();
		if(LoginUtil.isEmpty(vCode)){
			showToast(R.string.regist_error_vcode);
			mValidationCode.requestFocus();
			return;
		}
		if (!LoginUtil.isUsernameAvailable(username) || username.length() != 11) {
			showToast(R.string.regist_error_phonenumble);
			mRegistUsername.requestFocus();
			return;
		}
		if (!LoginUtil.isPasswordAvailable(password) || password.length()>20 || password.length() < 6 ) {
			showToast(R.string.regist_error_password);
			mRegistPassword.requestFocus();
			return;
		}
		if (!LoginUtil.isConPasswordAvailable(password, conPass)) {
			showToast(R.string.regist_error_conpassword);
			mRegistConfirmPass.requestFocus();
			return;
		}
		if(!LoginUtil.isUsernameAvailable(nickName) || nickName.getBytes().length < 4 || nickName.getBytes().length >30){
			showToast(R.string.regist_error_nickname);
			mRegistNickname.requestFocus();
			return;
		}
		if("".equals(mProvince) || "".equals(mCity)){
			showToast(R.string.regist_error_address);
			return;
		}
		regist();
	}

	private void regist() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(LoginConstant.WEB_REGIST_MOBILEPHONE, mRegistUsername.getText().toString());
		params.put(LoginConstant.WEB_REGIST_VCODE, mValidationCode.getText().toString());
		params.put(LoginConstant.WEB_REGIST_PASSWORD, mRegistPassword.getText().toString());
		params.put(LoginConstant.WEB_REGIST_NICKNAME, mRegistNickname.getText().toString());
		//params.put(LoginConstant.WEB_LOGIN_CONFIRM_PASSWORD, mRegistConfirmPass.getText().toString());
		int checkId = mRegistSex.getCheckedRadioButtonId();
		if(checkId == R.id.user_sex_male) {
			params.put(LoginConstant.WEB_REGIST_SEX, RegistAction.SEX_MAN);
		} else if(checkId == R.id.user_sex_female){
			params.put(LoginConstant.WEB_REGIST_SEX, RegistAction.SEX_WOMAN);
		} else {
			params.put(LoginConstant.WEB_REGIST_SEX, "");
		}
		params.put(LoginConstant.WEB_REGIST_PROVINCE, mProvince);
		params.put(LoginConstant.WEB_REGIST_CITY, mCity);
		params.put(LoginConstant.WEB_REGIST_RESION, mCounties);
		ActionController.post(
				this,
				RegistAction.class,
				params,
				mRegistResultListener,
				true);
	}

	private class RegistResultListener implements RegistAction.IRegistResultListener {

		@Override
		public void onFail(int resourceID) {
			hideProgressDialog();
			showToast(RegistPage.this, resourceID);
		}

		@Override
		public void onFinish(int resourceID) {
			hideProgressDialog();
			MobclickAgent.onEvent(RegistPage.this,YoumentEvent.EVENT_REGIST);
			showToast(RegistPage.this, resourceID);
			startGuide();
		}

		@Override
		public void onStart() {
			showProgressDialog(RegistPage.this, R.string.regist_regist, R.string.regist_wait);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
			case REQUEST_SELECT_REGION:
				mProvince = data.getStringExtra(RegionSelectPage.RESULT_ADDRESS_PRVINCE);
				mCity = data.getStringExtra(RegionSelectPage.RESULT_ADDRESS_CITY);
				mCounties = data.getStringExtra(RegionSelectPage.RESULT_ADDRESS_COUNTIES);
				mRegistAddress.setText(mCity + " " + mCounties);
				break;
		}
	}
	
	protected void startGuide() {
		
		/*ApplicationManager.getInstance().init(PreparePage.this);
		Intent intent = new Intent(PreparePage.this, FindLifeMainGuidePage.class);
		intent.putExtra(FindLifeMainGuidePage.KEY_IS_FROM, FindLifeMainGuidePage.KEY_IS_FROM_REGIST);
		//intent.putExtra(MainActivity.INIT_TAG, MainActivity.TAG_FIND);
		//intent.putExtra(MainActivity.INIT_FIND_TAG, RegistPage.FROM_REGIST);
		PreparePage.this.startActivity(intent);*/
		
		/*Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(MainActivity.INIT_FIND_TAG, FROM_REGIST);*/
//		ApplicationManager.getInstance().init(RegistPage.this);
		Intent intent = new Intent(RegistPage.this, SuperPeopleAllPage.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void afterTextChanged(Editable s) {
		String phoneNo = mRegistUsername.getText().toString();
		if(LoginUtil.isUsernameAvailable(phoneNo) && phoneNo.length() == 11){
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
}
