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

public class NewsSettingsPage extends BasePage{
	private static final String TAG = "LocationSettingPage";
	private ImageButton mGoBackBtn;
	private TextView mTitle;
	private CheckBox mAddMeCheckBox;
	private CheckBox mSayMeCheckBox;
	private CheckBox mFavCheckBox;
	private View mAddMeTab;
	private View mSayMeTab;
	private View mFavTab;
	//private TextView mNewVersionNO;
	private ButtionOnClickListener mBtnOnClickListener = new ButtionOnClickListener();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_settings);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initView();
	}
	private void initView(){
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(R.string.msg_notify_title);
		mGoBackBtn = (ImageButton) findViewById(R.id.back);
		//mNewVersionNO = (TextView) findViewById(R.id.last_version_NO_text);
		mGoBackBtn.setVisibility(View.VISIBLE);
		mGoBackBtn.setOnClickListener(mBtnOnClickListener);
		mAddMeCheckBox = (CheckBox) findViewById(R.id.msg_addme_checkbox);
		mSayMeCheckBox = (CheckBox) findViewById(R.id.msg_sayme_checkbox);
		mFavCheckBox = (CheckBox) findViewById(R.id.msg_fav_checkbox);

		mAddMeTab = findViewById(R.id.msg_addme_tab);
		mSayMeTab = findViewById(R.id.msg_sayme_tab);
		mFavTab   = findViewById(R.id.msg_fav_tab);
		mAddMeTab.setOnClickListener(mBtnOnClickListener);
		mSayMeTab.setOnClickListener(mBtnOnClickListener);
		mFavTab.setOnClickListener(mBtnOnClickListener);
		
		
		mAddMeCheckBox.setChecked(MoreUtil.readIsShowAddMeFromPref(this));
		mSayMeCheckBox.setChecked(MoreUtil.readIsShowSayMeFromPref(this));
		mFavCheckBox.setChecked(MoreUtil.readIsShowFavFromPref(this));

		mAddMeCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				MoreUtil.writeIsShowAddMeNewsToPref(NewsSettingsPage.this, isChecked);
			}
		});
		mSayMeCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				MoreUtil.writeIsShowSayMeNewsToPref(NewsSettingsPage.this, isChecked);
			}
		});
		mFavCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				MoreUtil.writeIsShowFavNewsToPref(NewsSettingsPage.this, isChecked);
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
				case R.id.msg_addme_tab:
					mAddMeCheckBox.setChecked(!mAddMeCheckBox.isChecked());
					break;
				case R.id.msg_fav_tab:
					mFavCheckBox.setChecked(!mFavCheckBox.isChecked());
					break;
				case R.id.msg_sayme_tab:
					mSayMeCheckBox.setChecked(!mSayMeCheckBox.isChecked());
					break;
				default:
					break;
			}
		}
	}
	
}
