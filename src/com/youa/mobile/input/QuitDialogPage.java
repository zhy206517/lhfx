package com.youa.mobile.input;

import android.os.Bundle;
import android.view.View;
import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseOptionPage;

public class QuitDialogPage extends BaseOptionPage {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		button1.setVisibility(View.GONE);
		button2.setText(R.string.input_confirm);
		title.setText(R.string.input_quit_or_not);
	}

	@Override
	protected void onButton1Click() {

	}

	@Override
	protected void onButton2Click() {
		setResult(RESULT_OK);
		finish();
	}

}
