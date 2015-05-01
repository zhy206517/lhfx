package com.youa.mobile.input;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BaseEnterPage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.login.LoginBasePage;
import com.youa.mobile.login.LoginPage;
import com.youa.mobile.login.action.CheckLoginAction;
import com.youa.mobile.login.util.LoginUtil;

public class ReceiveFromOutsidePage extends BaseEnterPage{

	public static final int REQUEST_CODE_LOGIN = 0;
	private Uri uri;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationManager.getInstance().init(this);
		saveData();
		checkLogin();
	}

	private void saveData() {
		Intent callerIntent = getIntent();
		Bundle bundle = callerIntent.getExtras();
		if (bundle != null) {
			uri = (Uri) bundle.get(Intent.EXTRA_STREAM);
		}
	}

	private void goToInputPage() {
		String imgPath = InputUtil.getPathByImageURI(this, uri);
		if (imgPath != null) {
			Intent intent = new Intent(this, PublishPage.class);
			intent.putExtra(PublishPage.KEY_FROM_OUTSIDE_SHARE, imgPath);
			startActivity(intent);
		}
		finish();
	}

	private void checkLogin() {
		ActionController.post(
				 this, 
				 CheckLoginAction.class, 
				 null, 
				 new CheckLoginAction.CheckResultListner(){

					@Override
					public void onHasUser() {
						ApplicationManager.getInstance().init(ReceiveFromOutsidePage.this);
						LoginUtil.getSaveHomeData(ReceiveFromOutsidePage.this);
						goToInputPage();
					}
					@Override
					public void onNoUser() {
						Intent intent = new Intent(ReceiveFromOutsidePage.this, LoginPage.class);
						intent.putExtra(LoginBasePage.KEY_FROM_WHERE, BaseEnterPage.FROM_OUTSIDE_SHARE);
						startActivityForResult(intent, REQUEST_CODE_LOGIN);
					}
				 });
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case REQUEST_CODE_LOGIN:
				if (resultCode == RESULT_OK) {
					goToInputPage();
					break;
				} else {
					finish();
				}
				
		}
	}
	

}
