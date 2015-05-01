package com.youa.mobile.common.base;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.youa.mobile.R;

public abstract class BaseOptionPage extends BasePage implements OnClickListener {

	protected Button button1;
	protected Button button2;
	protected Button buttonCancel;
	protected TextView title;
	protected abstract void onButton1Click();
	protected abstract void onButton2Click();
	protected void onCancelClick() {
		setResult(RESULT_CANCELED);
		finish();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.alpha(0)));
		setContentView(R.layout.common_takepage);
		LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.dimAmount = 0.8f;
		layoutParams.x = 0;
		layoutParams.y = 0;
		layoutParams.width = -1;
		layoutParams.height = -1;
		getWindow().setAttributes(layoutParams);
		initView();
	}
	
	private void initView() {
		title = (TextView)findViewById(R.id.title);
		button1 = (Button)findViewById(R.id.button1);
		button2 = (Button)findViewById(R.id.button2);;
		buttonCancel = (Button)findViewById(R.id.cancel);
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		  case R.id.button1:
			  onButton1Click();
			  break;
		  case R.id.button2:
			  onButton2Click();
			  break;
		  case R.id.cancel:
			  onCancelClick();
			  break;
		}
	}
}
