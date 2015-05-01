package com.youa.mobile.information;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePage;

public class ModifyPassPage extends BasePage implements OnClickListener {

	private View titleLeft;
	private View titleRight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information_modifypass);
		titleLeft = findViewById(R.id.title_left);
		titleRight = findViewById(R.id.title_right);
		titleLeft.setOnClickListener(this);
		titleRight.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.title_left:
				finish();
				break;
			case R.id.title_right:
				finish();
				break;
			default:
				break;
		}
		
	}
	
}
