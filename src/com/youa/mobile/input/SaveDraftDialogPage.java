package com.youa.mobile.input;

import android.graphics.Color;
import android.os.Bundle;
import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseOptionPage;

public class SaveDraftDialogPage extends BaseOptionPage {

	public static final int RESULT_NOT_OK = 2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		button1.setBackgroundResource(R.drawable.input_not_save_bg_sec);
		button2.setBackgroundResource(R.drawable.input_save_bg_sec);
		//buttonCancel.setBackgroundResource(R.drawable.input_cancel_bg);
		button1.setText(R.string.publish_not_save);
		button1.setTextColor(Color.WHITE);
		button2.setText(R.string.publish_save);
		buttonCancel.setText(R.string.publish_cancel);
		title.setText(R.string.publish_save_draft);
	}

	@Override
	protected void onButton1Click() {
		setResult(RESULT_NOT_OK);
		finish();
	}

	@Override
	protected void onButton2Click() {
		setResult(RESULT_OK);
		finish();
	}

}
