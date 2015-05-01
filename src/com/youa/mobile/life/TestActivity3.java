package com.youa.mobile.life;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class TestActivity3 extends ActivityGroup implements OnCheckedChangeListener {
	private TabHost mHost;
	private Intent intent0;
	private Intent intent1;
	private Intent intent2;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	
	}

	private void initTabs() {
	}

	private void initRadios() {
	}

	private void setupIntent() {

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	}

	private TabHost.TabSpec buildTabSpec(String tag, String resLabel, int resIcon, final Intent content) {
		return this.mHost.newTabSpec(tag).setIndicator(resLabel,
				getResources().getDrawable(resIcon)).setContent(content);
	}
}
