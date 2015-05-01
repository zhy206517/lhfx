package com.youa.mobile.information;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.common.widget.TakePicturePage;
import com.youa.mobile.information.action.InitPersonalInfoAction;
import com.youa.mobile.information.action.SaveAction;
import com.youa.mobile.information.data.PersonalInformationData;

public class PersonalEditPage extends BasePage implements OnClickListener {
	public static final String TAG = "PersonalEditPage";
	public static final int REQUEST_TACKIMAGE = 1;
	public static final int REQUEST_SELECT_REGION = 2;
	private final int DIALOG_DATEPICKER = 100;
	public static final String KEY_UID = "uid";
	private ImageView mHeaderView;
	private TextView mTitle;
	private TextView mNameView;
	// private TextView mNickView;
	private View mBack;
	private View mSend;
	private RadioGroup mSexView;
	private TextView mAddressView;
	private TextView mBirthView;
	private TextView mIntroduceView;
	// private Button mEditAddrButton;

	private String mUid;
	private PersonalInformationData mPersonalInformationData;
	private String mBirthdayYear;
	private String mBirthdayMonth;
	private String mBirthdayDay;
	private View moidfyButton;
	private String mHeaderImagePath;
	private String mProvince = "";
	private String mCity = "";
	private String mCounties = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information_personal_editpage);
		mUid = ApplicationManager.getInstance().getUserId();
		initView();
		loadData();
	}

	private void initView() {
		mBack = (View) findViewById(R.id.back);
		mSend = (View) findViewById(R.id.send);
		mSend.setEnabled(false);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(R.string.edit_lable);
		// mNickView = (TextView)findViewById(R.id.user_nickname);
		mSexView = (RadioGroup) findViewById(R.id.user_sex);
		mAddressView = (TextView) findViewById(R.id.user_address);
		mBirthView = (TextView) findViewById(R.id.user_birthday);
		mIntroduceView = (TextView) findViewById(R.id.user_introduce);
		mHeaderView = (ImageView) findViewById(R.id.header_image);
		// mHeaderLayoutView = findViewById(R.id.header_layout);
		mNameView = (TextView) findViewById(R.id.user_name);
		// moidfyButton = findViewById(R.id.modify_password_button);
		// mEditAddrButton = (Button) findViewById(R.id.edit_address_btn);
		mHeaderView.setOnClickListener(this);
		// mHeaderLayoutView.setOnClickListener(this);
		mBack.setOnClickListener(this);
		mSend.setOnClickListener(this);
		mBirthView.setOnClickListener(this);
		mSend.setOnClickListener(this);
		mIntroduceView.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				final String str = mIntroduceView.getText().toString();
				int length = str.length();
				if (length >= 70) {
					showToast(R.string.user_introduce_error);
				}

			}
		});
		// moidfyButton.setOnClickListener(this);
		// mEditAddrButton.setOnClickListener(this);
		mAddressView.setOnClickListener(this);
	}

	private void updateView(
			final PersonalInformationData personalInformationData) {
		mPersonalInformationData = personalInformationData;
		mUid = personalInformationData.getUserId();
		mBirthdayYear = personalInformationData.getBirthdayYear();
		mBirthdayMonth = personalInformationData.getBirthdayMonth();
		mBirthdayDay = personalInformationData.getBirthdayDay();
		mProvince = personalInformationData.getProvince();
		mCity = personalInformationData.getCity();
		mCounties = personalInformationData.getDistrict();
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mNameView.setText(personalInformationData.getUserName());
				String sex = personalInformationData.getSexInt();
				int defaultHeaderRes = R.drawable.head_men;
				if (sex != null && !"".equals(sex)) {
					if (PersonalInformationData.SEX_MAN.equals(sex)) {
						mSexView.check(R.id.user_sex_man);
					} else {
						mSexView.check(R.id.user_sex_womain);
						defaultHeaderRes = R.drawable.head_women;
					}
				} else {
					mSexView.clearCheck();
				}
				mAddressView.setText(getAddress());
				mBirthView.setText(personalInformationData.getBirthday());
				mIntroduceView.setText(personalInformationData.getIntroduce());
				ImageUtil.setImageView(PersonalEditPage.this, mHeaderView,
						personalInformationData.getHeaderImageId(),
						ImageUtil.HEADER_SIZE_BIG, ImageUtil.HEADER_SIZE_BIG,
						defaultHeaderRes, 8);
				mSend.setEnabled(true);
			}

		});
	}

	public void loadData() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(InitPersonalInfoAction.KEY_UID, mUid);
		ActionController.post(this, InitPersonalInfoAction.class, paramMap,
				new InitPersonalInfoAction.IInitResult() {
					@Override
					public void onEnd(
							PersonalInformationData personalInformationData) {
						updateView(personalInformationData);
					}

					@Override
					public void onShowMessage(int resourceID) {
						showToast(resourceID);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_image:
			Intent intent = new Intent(this, TakePicturePage.class);
			intent.putExtra(TakePicturePage.KEY_OPERATE_TYPE,
					TakePicturePage.OPERATE_TYPE_SELECT);
			intent.putExtra(TakePicturePage.KEY_OPERATE_ISCROP, true);
			startActivityForResult(intent, REQUEST_TACKIMAGE);
			break;
		case R.id.back:
			finish();
			break;
		case R.id.send:
			if (checkInput(mBirthView, R.string.information_birthday)
					&& checkInput(mSexView, R.string.information_sex)) {
				save();
			}
			break;
		case R.id.user_birthday:
			showDialog(DIALOG_DATEPICKER);
			break;
		// case R.id.modify_password_button:
		// intent = new Intent(this, ModifyPassPage.class);
		// startActivity(intent);
		// break;
		case R.id.user_address:
			Intent i = new Intent();
			i.setClass(PersonalEditPage.this, RegionSelectPage.class);
			startActivityForResult(i, REQUEST_SELECT_REGION);
			break;
		}

	}

	private void save() {
		int checkId = mSexView.getCheckedRadioButtonId();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(SaveAction.KEY_UID, mUid);
		paramMap.put(SaveAction.KEY_USERNAME, mTitle.getText().toString());
		// paramMap.put(SaveAction.KEY_NICKNAME,
		// mNickView.getText().toString());
		if (checkId == R.id.user_sex_man) {
			paramMap.put(SaveAction.KEY_SEX, PersonalInformationData.SEX_MAN);
		} else if (checkId == R.id.user_sex_womain) {
			paramMap.put(SaveAction.KEY_SEX, PersonalInformationData.SEX_WOMAN);
		} else {
			paramMap.put(SaveAction.KEY_SEX, "");
		}
		paramMap.put(SaveAction.KEY_CITY, mCity);
		paramMap.put(SaveAction.KEY_PROVINCE, mProvince);
		paramMap.put(SaveAction.KEY_COUNTIES, mCounties);
		paramMap.put(SaveAction.KEY_BIRTHDAYYEAR, mBirthdayYear);
		paramMap.put(SaveAction.KEY_BIRTHDAYMONTH, mBirthdayMonth);
		paramMap.put(SaveAction.KEY_BIRTHDAYDAY, mBirthdayDay);
		paramMap.put(SaveAction.KEY_INTRUDUCE, mIntroduceView.getText()
				.toString());
		paramMap.put(SaveAction.KEY_HEADERIMAGEPATH, mHeaderImagePath);

		// paramMap.put("abc", "efg");
		// System.out.println("#########################");
		// System.out.println("######paramMap:" +paramMap.toString());
		ActionController.post(this, SaveAction.class, paramMap,
				new SaveAction.ISaveResultListener() {

					@Override
					public void onEnd() {
						hideProgressDialog();
						Intent intent = new Intent(
								SystemConfig.ACTION_REFRESH_USER_INFO_UPDATE);
						sendBroadcast(intent);

						mHandler.post(new Runnable() {
							@Override
							public void run() {
								finish();
							}
						});
					}

					@Override
					public void onStart() {
						showProgressDialog(PersonalEditPage.this,
								R.string.common_hint,
								R.string.common_requesting_server);
					}

					@Override
					public void onFail(int resourceID) {
						showToast(resourceID);
						hideProgressDialog();
					}

				});
	}

	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {

		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case REQUEST_TACKIMAGE:
			String filePath = data.getStringExtra(TakePicturePage.RESULT_PATH);
			mHeaderImagePath = filePath;
			Bitmap bitmap = BitmapFactory.decodeFile(filePath);
			mHeaderView.setImageBitmap(bitmap);
			break;
		case REQUEST_SELECT_REGION:
			mProvince = data
					.getStringExtra(RegionSelectPage.RESULT_ADDRESS_PRVINCE);
			mCity = data.getStringExtra(RegionSelectPage.RESULT_ADDRESS_CITY);
			mCounties = data
					.getStringExtra(RegionSelectPage.RESULT_ADDRESS_COUNTIES);
			mAddressView.setText(getAddress());
			break;
		}

	}

	private String getAddress() {
		return mCity + " " + mCounties;
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch (id) {
		case DIALOG_DATEPICKER:
			DatePickerDialog dataPicker = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							mBirthdayYear = String.valueOf(year);
							mBirthdayMonth = String.valueOf(monthOfYear + 1);
							mBirthdayDay = String.valueOf(dayOfMonth);
							mBirthView.setText(year + "-" + (monthOfYear + 1)
									+ "-" + dayOfMonth);
						}
					}, getShowYear(), getShowMonth() - 1, getShowDay());
			dataPicker.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
								.hideSoftInputFromWindow(PersonalEditPage.this
										.getCurrentFocus().getWindowToken(),
										InputMethodManager.HIDE_NOT_ALWAYS);
					}
					return false;
				}
			});
			return dataPicker;
		default:
			return null;
		}
	}

	/*
	 * public static boolean isSoftKeyBoardShow(View view,boolean isShowHide){
	 * InputMethodManager imm =
	 * (InputMethodManager)view.getContext().getSystemService
	 * (Context.INPUT_METHOD_SERVICE); boolean isActive = imm.isActive();
	 * 
	 * if(isActive && isShowHide){
	 * imm.hideSoftInputFromWindow(view.getWindowToken(), 0); }
	 * Log.e(TYPE_TAG,"isSoftKeyBoardShow = "+isActive); return isActive; }
	 */
	private int getShowYear() {
		if (mBirthdayYear != null && !"".equals(mBirthdayYear)) {
			int year = Integer.parseInt(mBirthdayYear);
			return year >= 1900 ? year : 1900;
		} else {
			Calendar calendar = Calendar.getInstance();
			return calendar.get(Calendar.YEAR);
		}
	}

	private int getShowMonth() {
		if (mBirthdayMonth != null && !"".equals(mBirthdayMonth)) {
			int month = Integer.parseInt(mBirthdayMonth);
			if (month < 1)
				month = 1;
			if (month > 12)
				month = 12;
			return month;
		} else {
			Calendar calendar = Calendar.getInstance();
			return calendar.get(Calendar.MONTH) + 1;
		}
	}

	private int getShowDay() {
		if (mBirthdayDay != null && !"".equals(mBirthdayDay)) {
			int day = Integer.parseInt(mBirthdayDay);
			if (day < 1)
				day = 1;
			return day;
		} else {
			Calendar calendar = Calendar.getInstance();
			return calendar.get(Calendar.DAY_OF_MONTH);
		}
	}
}
