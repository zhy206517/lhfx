package com.youa.mobile.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.youa.mobile.R;
import com.youa.mobile.common.manager.NetworkStatus;
import com.youa.mobile.login.auth.SupportSite;
import com.youa.mobile.login.util.LoginUtil;

public class LoginPage extends LoginBasePage {
	//private final static String TAG = "LoginPage";
	private TextView mTitle;
	private ImageButton mback;
	private View mLoginSina;
	private View mLoginQQ;
	private View mLoginRenRen;
	private View mLoginLeho;
	private View mLoginBaidu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_login);
		setActionName();
		initViews();
	}

	public void setActionName() {
		Intent i = getIntent();
		if (i != null) {
			String str = i.getStringExtra("action_name");
			setActionName(str);
		}
	}

	private void initViews() {
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(R.string.login_login);
		mback = (ImageButton) findViewById(R.id.back);
		mback.setVisibility(View.VISIBLE);
		mback.setOnClickListener(onClickListener);
		mLoginSina = findViewById(R.id.login_sina);
		mLoginQQ = findViewById(R.id.login_qq);
		mLoginRenRen = findViewById(R.id.login_renren);
		mLoginLeho = findViewById(R.id.login_leho);
		mLoginBaidu = findViewById(R.id.login_baidu);
		mLoginLeho.setOnClickListener(onClickListener);
		mLoginSina.setOnClickListener(onClickListener);
		mLoginQQ.setOnClickListener(onClickListener);
		mLoginRenRen.setOnClickListener(onClickListener);
		mLoginBaidu.setOnClickListener(onClickListener);
	}
	
	// onClick方法中之所以只有当 view id 为login_leho时才进行网络检查，是因为其他登录方式都调用LoginUtil.openThirdAuthPage方法，该方法中有网络检查。
	Button.OnClickListener onClickListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.login_baidu:
				LoginUtil.openThirdAuthPage(LoginPage.this, SupportSite.BAIDU);
				break;
			case R.id.login_sina:
				LoginUtil.openThirdAuthPage(LoginPage.this, SupportSite.SINA);
				break;
			case R.id.login_qq:
				LoginUtil.openThirdAuthPage(LoginPage.this, SupportSite.QQ);
				break;
			case R.id.login_renren:
				LoginUtil.openThirdAuthPage(LoginPage.this, SupportSite.RENREN);
				break;
			case R.id.login_leho:
				if (!NetworkStatus.isNetworkAvailable(LoginPage.this)) {
					Toast.makeText(LoginPage.this, R.string.common_network_not_available, Toast.LENGTH_SHORT).show();
					return;
				}
				Intent i = new Intent(LoginPage.this, loginRegistPage.class);
				startActivityForResult(i, AUTH_REQUEST_CODE);
				break;
			case R.id.back:
				LoginPage.this.finish();
				break;
			}
		}
	};

}
