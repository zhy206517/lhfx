package com.youa.mobile.more;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePage;

public class AboutPage extends BasePage {
	private static final String TAG = "AboutPage";
	private ImageButton mGoBackBtn;
	private TextView mTitle;
	private TextView mVersionNO;
	//private TextView mNewVersionNO;
	private ButtionOnClickListener mBtnOnClickListener = new ButtionOnClickListener();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_about);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initView();
	}
	private void initView(){
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(R.string.about_client_lable);
		mGoBackBtn = (ImageButton) findViewById(R.id.back);
		mVersionNO = (TextView) findViewById(R.id.version_NO_text);
		//mNewVersionNO = (TextView) findViewById(R.id.last_version_NO_text);
		mGoBackBtn.setVisibility(View.VISIBLE);
		mGoBackBtn.setOnClickListener(mBtnOnClickListener);
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				String versionName = getAppVersionName(AboutPage.this);
				mVersionNO.setText(getString(R.string.about_version_lable, versionName));
			}
		});
	}
	private class ButtionOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.back:
					finish();
					break;
			}
		}
	}
	/**
	 * 返回当前程序版本名
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			Log.d(TAG, "Exception", e);
		}
		return versionName;
	}
}
