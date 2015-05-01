package com.youa.mobile.more;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.youa.mobile.LehoApp;
import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePage;

public class BrowserModlePage extends BasePage {
	private static final String TAG = "LocationSettingPage";
	private ImageButton mGoBackBtn;
	private TextView mTitle;
	private CheckBox mBrowserCheckBox;
	// private TextView mNewVersionNO;
	private ButtionOnClickListener mBtnOnClickListener = new ButtionOnClickListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browser_settings);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initView();
	}

	private void initView() {
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(R.string.browser_modle_lable);
		mGoBackBtn = (ImageButton) findViewById(R.id.back);
		// mNewVersionNO = (TextView) findViewById(R.id.last_version_NO_text);
		mGoBackBtn.setVisibility(View.VISIBLE);
		mGoBackBtn.setOnClickListener(mBtnOnClickListener);
		mBrowserCheckBox = (CheckBox) findViewById(R.id.browser_settings_id);
		mBrowserCheckBox.setChecked(MoreUtil.readHighDefinitionFromPref(this));
		mBrowserCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						MoreUtil.writeHighDefinitionFromPref(
								BrowserModlePage.this, isChecked);

					}
				});
	}

	private class ButtionOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			}
		}
	}
}
