package com.youa.mobile.more;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseSyncPage;

public class SyncSettingPage extends BaseSyncPage {
//	private static final String TAG = "SyncSettingPage";
	private ImageButton mBack;
	private TextView mTitle;
	private OnClickListener onClickListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_binding_third_account);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initUI();
	}


	private void initUI() {
		mBindSinaParentView = findViewById(R.id.bind_sina);
		mBindQQParentView = findViewById(R.id.bind_qq);
		mBindRenrenParentView = findViewById(R.id.bind_renren);

		mBindSinaBox = (CheckBox) findViewById(R.id.bind_sina_status);
		mBindQQBox = (CheckBox) findViewById(R.id.bind_qq_status);
		mBindRenrenBox = (CheckBox) findViewById(R.id.bind_renren_status);
		isSyncPage = false;//放在updateAllSyncBoxStatus前
		updateAllSyncBoxStatus();
		
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(R.string.sync_settings_lable);
		mBack = (ImageButton) findViewById(R.id.back);
		mBack.setVisibility(View.VISIBLE);
		onClickListener = new BtnOnClickListener();
		mBack.setOnClickListener(onClickListener);
		
		mBindSinaParentView.setOnClickListener(onClickListener);
		mBindRenrenParentView.setOnClickListener(onClickListener);
		mBindQQParentView.setOnClickListener(onClickListener);
		
		initSyncBoxCheckListener(onClickListener);
		
		loadProgressBar = (ProgressBar) findViewById(R.id.progressBar);
	}

	private class BtnOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			int btnId = v.getId();
			switch (btnId) {
			case R.id.back:
				finish();
				break;
			case R.id.bind_sina_status:
			case R.id.bind_qq_status:
			case R.id.bind_renren_status:
			case R.id.bind_renren:
			case R.id.bind_qq:
			case R.id.bind_sina:
				openThirdAuthPageOrUnbind(btnId, true);
				break;
			}
		}
	}
}
