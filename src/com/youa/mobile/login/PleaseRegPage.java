package com.youa.mobile.login;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseOptionPage;

public class PleaseRegPage extends BaseOptionPage{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		button1.setBackgroundResource(R.drawable.input_not_save_bg_sec);
		button2.setVisibility(View.GONE);
		buttonCancel.setBackgroundResource(R.drawable.input_cancel_bg);
		button1.setText(R.string.login_regist);
		button1.setTextColor(Color.WHITE);
		button1.setClickable(false);
		buttonCancel.setText(R.string.quit_cancel);
		title.setText(R.string.reg_or_not);
	}
	@Override
	protected void onButton1Click() {
		setResult(RESULT_OK);
		finish();
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
		//setResult(RESULT_CANCELED);
		finish();
	}
}