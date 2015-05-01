package com.youa.mobile.more;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import com.youa.mobile.LehoApp;
import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePage;

public class LocationSettingPage extends BasePage{
	private static final String TAG = "LocationSettingPage";
	private ImageButton mGoBackBtn;
	private TextView mTitle;
	private CheckBox mSetLbsCheckBox;
	//private TextView mNewVersionNO;
	private ButtionOnClickListener mBtnOnClickListener = new ButtionOnClickListener();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_setting);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initView();
	}
	private void initView(){
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(R.string.lbs_function_lable);
		mGoBackBtn = (ImageButton) findViewById(R.id.back);
		//mNewVersionNO = (TextView) findViewById(R.id.last_version_NO_text);
		mGoBackBtn.setVisibility(View.VISIBLE);
		mGoBackBtn.setOnClickListener(mBtnOnClickListener);
		mSetLbsCheckBox= (CheckBox) findViewById(R.id.set_lbs_checkbox);
		mSetLbsCheckBox.setChecked(MoreUtil.readIsStartLocationFromPref(this));
		mSetLbsCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				((LehoApp)LocationSettingPage.this.getApplication()).isStartLoc=isChecked;
				if(isChecked){
					((LehoApp)LocationSettingPage.this.getApplication()).initMap();
					((LehoApp)LocationSettingPage.this.getApplication()).startLocationTask(100);
				}else{
					((LehoApp)LocationSettingPage.this.getApplication()).destroyMap();
				}
				MoreUtil.writeIsStartLocationFromPref(LocationSettingPage.this, isChecked);
				
			}
		});
		findViewById(R.id.set_lbs_lab).setOnClickListener(mBtnOnClickListener);
	}
	private class ButtionOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.back:
					finish();
					break;
				case R.id.set_lbs_lab:
					mSetLbsCheckBox.setChecked(!mSetLbsCheckBox.isChecked());
					break;
			}
		}
	}
}
