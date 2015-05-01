package com.youa.mobile.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.login.util.LoginUtil;

public class BindingLehoAccountPage extends BasePage {
	private Button mBack;
	private Button mSkip;
	private Button mBinding;
	private EditText mUsername;
	private EditText mPassword;
	private OnClickListener onClickListener;
	
	private String userid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_binding_leho_account);
		initUI();
	}
	private void initUI(){
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			userid = extras.getString("userid");
		}
		mBack = (Button)findViewById(R.id.back);
		mSkip = (Button)findViewById(R.id.skip);
		mBinding = (Button)findViewById(R.id.binding);
		mUsername = (EditText)findViewById(R.id.username);
		mPassword = (EditText)findViewById(R.id.password);
		onClickListener = new BtnOnClickListener();
		mBack.setOnClickListener(onClickListener);
		mSkip.setOnClickListener(onClickListener);
		mBinding.setOnClickListener(onClickListener);
	}
	
	private class BtnOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				BindingLehoAccountPage.this.finish();
				break;
			case R.id.skip:
				Intent i = new Intent(BindingLehoAccountPage.this, ThirdUserInfoConfirm.class);
				i.putExtra("userid", userid);
				startActivity(i);
				break;
			case R.id.binding:
				checkInfo();
				break;
			}
		}
	}
	
	private void checkInfo() {
		String username = mUsername.getText().toString();
		String password = mPassword.getText().toString();
		if (!LoginUtil.isUsernameAvailable(username)) {
			showToast(BindingLehoAccountPage.this, R.string.login_wrong_username);
			mUsername.requestFocus();
			return;
		}
		if (!LoginUtil.isPasswordAvailable(password)) {
			showToast(BindingLehoAccountPage.this, R.string.login_wrong_password);
			mPassword.requestFocus();
			return;
		}
//		regist();
	}
}
