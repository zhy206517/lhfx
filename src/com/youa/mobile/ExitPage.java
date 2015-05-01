package com.youa.mobile;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.youa.mobile.common.base.BaseOptionPage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.input.PublishFeedStore;
import com.youa.mobile.common.util.UpgradeUtil;

public class ExitPage extends BaseOptionPage{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		button1.setBackgroundResource(R.drawable.input_not_save_bg_sec);
		button2.setVisibility(View.GONE);
		button1.setText(R.string.quit_confirm);
		button1.setTextColor(Color.WHITE);
		buttonCancel.setText(R.string.quit_cancel);
		title.setText(R.string.quit_or_not);
	}
	@Override
	protected void onButton1Click() {
		setResult(RESULT_OK);
//		finish();
		UpgradeUtil.isUpgrade=true;
		exitClient(this);
		((LehoApp)ExitPage.this.getApplication()).stopLocation();
		((LehoApp)ExitPage.this.getApplication()).destroyMap();
		PublishFeedStore.getInstance().savePublish(ApplicationManager.getInstance().publishDataList, this);
		//((LehoApp)ExitPage.this.getApplication()).stopLocLocationClient();
	}

	@Override
	protected void onButton2Click() {
		// TODO ...
		//This page is button2 is hidden;
	}
	@Override
	protected void onCancelClick() {
		// TODO Auto-generated method stub
		//super.onCancelClick();
		setResult(RESULT_CANCELED);
		finish();
	}
}
