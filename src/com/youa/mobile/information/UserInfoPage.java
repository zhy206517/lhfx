package com.youa.mobile.information;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.information.action.AddCancelAttentAction;
import com.youa.mobile.information.action.InitPersonalInfoAction;
import com.youa.mobile.information.data.PersonalInformationData;

public class UserInfoPage extends BasePage implements OnClickListener {
	public static final String KEY_UID = "uid";

	// private TextView mTitleView;
	private TextView mNameView;
	private TextView mSexView;
	private TextView mAddressView;
	private TextView mBirthView;
	private TextView mIntroduceView;
	private String mUid;
	private ImageView mImageView;
	private boolean mReload;
	private ImageButton mback;
	private TextView mTitle;
	private ImageButton mSend;
	//-----
	private String mUserName;
	private String mSexInt;
	private String mImageId;
	private boolean isAttention = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information_user);
		initDataFromIntent(getIntent());
		initTitle();
		initView();
		loadData();
		registerReceiver();
	}

	public void initTitle() {
		mback = (ImageButton) findViewById(R.id.back);
		mback.setOnClickListener(this);
		mSend = (ImageButton) findViewById(R.id.send);
		if(TextUtils.isEmpty(ApplicationManager.getInstance().getUserId())){
			mSend.setVisibility(View.GONE);
		}
		mSend.setOnClickListener(this);
//		mSend.setBackgroundResource(R.drawable.bt_on_switch_bg_seletor);
//		mSend.setVisibility(View.GONE);
		mTitle = (TextView) findViewById(R.id.title);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mReload) {
			loadData();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver();
	}

	private BroadcastReceiver mRefreshReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (TextUtils.equals(SystemConfig.ACTION_REFRESH_USER_INFO_UPDATE,
					intent.getAction())) {
				mReload = true;
			}
		}
	};

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(SystemConfig.ACTION_REFRESH_USER_INFO_UPDATE);
		registerReceiver(mRefreshReceiver, filter);
	}

	private void unregisterReceiver() {
		unregisterReceiver(mRefreshReceiver);
	}

	private void initDataFromIntent(Intent intent) {
		mUid = intent.getStringExtra(KEY_UID);

	}

	private void initView() {
		mNameView = (TextView) findViewById(R.id.user_name);
		mSexView = (TextView) findViewById(R.id.user_sex);
		mAddressView = (TextView) findViewById(R.id.user_address);
		mBirthView = (TextView) findViewById(R.id.user_birthday);
		mIntroduceView = (TextView) findViewById(R.id.user_introduce);
		mImageView = (ImageView) findViewById(R.id.header_image);
	}

	private void updateView(
			final PersonalInformationData personalInformationData) {
		mUid = personalInformationData.getUserId();
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mUserName=personalInformationData.getUserName();
				mTitle.setText(personalInformationData.getUserName());
				mNameView.setText(mUserName);
				mSexView.setText(personalInformationData.getSexResId());
				mAddressView.setText(personalInformationData.getCity());
				mBirthView.setText(personalInformationData.getBirthday());
				mIntroduceView.setText(personalInformationData.getIntroduce());
				mSexInt=personalInformationData.getSexInt();
				mImageId=personalInformationData.getHeaderImageId();
				if(PersonalInformationData.RELATION_STATUS_FOLLOW.equals(personalInformationData.getRelationStatus())||PersonalInformationData.RELATION_STATUS_FANS_FOLLOW.equals(personalInformationData.getRelationStatus())){
					isAttention=true;
				}else{
					isAttention=false;
				}
				setAttention(isAttention);
				// System.out.println("personalInformationData.getHeaderImageId():"
				// + personalInformationData.getHeaderImageId());
				String sex = personalInformationData.getSexInt();
				int defaultHeaderRes = R.drawable.head_men;
				if (PersonalInformationData.SEX_WOMAN.equals(sex)) {
					defaultHeaderRes = R.drawable.head_women;
				}
				ImageUtil.setHeaderImageView(UserInfoPage.this, mImageView,
						personalInformationData.getHeaderImageId(),
						defaultHeaderRes);
			}

		});
	}
	
	private void setAttention(boolean isAttention){
		if(isAttention){
			mSend.setBackgroundResource(R.drawable.bt_on_switch_bg_seletor);
			mSend.setImageResource(R.drawable.image_on_switch);
		}else{
			mSend.setBackgroundResource(R.drawable.bt_off_switch_bg_seletor);
			mSend.setImageResource(R.drawable.image_off_switch);
		}
	}

	public void loadData() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(InitPersonalInfoAction.KEY_UID, mUid);
		ActionController.post(this, InitPersonalInfoAction.class, paramMap,
				new InitPersonalInfoAction.IInitResult() {
					@Override
					public void onEnd(
							PersonalInformationData personalInformationData) {
						Intent intent = new Intent(
								LehuoIntent.ACTION_USERINFORMATION_UPDATE);
						intent.putExtra(LehuoIntent.USERINFOR_UID, mUid);
						intent.putExtra(LehuoIntent.USERINFOR_RELATION,
								personalInformationData.getRelationStatus());// TODO
																				// 关系·
						intent.putExtra(LehuoIntent.USERINFOR_NAME,
								personalInformationData.getUserName());
						intent.putExtra(LehuoIntent.USERINFOR_SEXINT,
								personalInformationData.getSexInt());
						intent.putExtra(LehuoIntent.USERINFOR_IMAGEID,
								personalInformationData.getHeaderImageId());
						UserInfoPage.this.sendBroadcast(intent);
						updateView(personalInformationData);
					}

					@Override
					public void onShowMessage(int resourceID) {
						showToast(UserInfoPage.this, resourceID);
					}

					@Override
					public void onStart() {

					}

					@Override
					public void onFail(int resourceID) {
						showToast(UserInfoPage.this, resourceID);
					}
				}, true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			UserInfoPage.this.finish();
			break;
		case R.id.send:
			operateAttent(!isAttention);     
			break;
		}

	}
	
	private void operateAttent(final boolean isAddAttent) {
		// AddCancelAttentAction
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(AddCancelAttentAction.KEY_FOLLOW_UID, mUid);
		if (isAddAttent) {
			paramMap.put(AddCancelAttentAction.KEY_OPERATE_TYPE,
					AddCancelAttentAction.TYPE_ADD);
			paramMap.put(AddCancelAttentAction.KEY_ADD_IMAGEID, mImageId);
			paramMap.put(AddCancelAttentAction.KEY_ADD_SEXINT, mSexInt);
			paramMap.put(AddCancelAttentAction.KEY_ADD_UNAME, mUserName);
		} else {
			paramMap.put(AddCancelAttentAction.KEY_OPERATE_TYPE,
					AddCancelAttentAction.TYPE_CANCEL);
		}
		ActionController.post(this, AddCancelAttentAction.class, paramMap,
				new AddCancelAttentAction.IOperateResult() {
					@Override
					public void onEnd(boolean flag) {
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								setAttention(isAddAttent);
							}
						});
						isAttention = isAddAttent;
					}

					@Override
					public void onStart() {

					}

					@Override
					public void onFail(int resourceID) {
						showToast(resourceID);
					}
				}, true);
	}
}
