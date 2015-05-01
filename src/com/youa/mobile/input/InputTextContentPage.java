package com.youa.mobile.input;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.util.EmotionHelper;
import com.youa.mobile.input.BaseInputPage.OnQuitCheckListener;
import com.youa.mobile.input.data.PublishData;
import com.youa.mobile.input.util.InputUtil;

public class InputTextContentPage extends BaseInputPage {

	private final static String TAG = "InputTextContentPage";

	public final static String CONSUME_PRICE = "price";
	public final static String CONSUME_MANY_PEOPLE = "many_people";
	public final static String IS_DELETE = "delete";
	public final static String KEY_PARAMS_ID = "uid";
	public final static String KEY_PARAMS_CONTENT = "content";
	public final static String KEY_PARAMS_PRICE = "price";
	public final static String KEY_PARAMS_MANY_PEOPLE = "many_people";
	public final static String KEY_PARAMS_PLID = "pid";
	public final static String KEY_PARAMS_PLACE = "place";
	public final static String KEY_PARAMS_IMAGE = "image";

	public final static String KEY_PARAMS_SYNC_SITE = "sync_data";
	public final static String KEY_PARAMS_SHARED = "share";

	public final static String KEY_PARAMS_LATITUDE = "latitude";
	public final static String KEY_PARAMS_LONGITUDE = "longitude";
	public final static String KEY_FROM_TOPIC = "from_topic";
	public final static String KEY_FROM_OUTSIDE_SHARE = "from_outside_share";

	private ImageButton mBackButton;
	private TextView mTitleTextView;
	private PublishData mPublicData;

	private int leftWords;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_comment);
		initViews();
	}

	private void initViews() {
		// comment_layout
		this.findViewById(R.id.comment_layout).setVisibility(View.INVISIBLE);
		mMainLayout = (ViewGroup) findViewById(R.id.main);

		mBackButton = (ImageButton) findViewById(R.id.back);
		mSendButton = (Button) findViewById(R.id.send);

		mInsertTopicButton = (ImageView) findViewById(R.id.insert_topic);
		mInsertPeopleButton = (ImageView) findViewById(R.id.insert_people);
		mInsertFaceButton = (ImageView) findViewById(R.id.insert_face);
		mFaceView = (GridView) findViewById(R.id.face_view);

		mTitleTextView = (TextView) findViewById(R.id.title);
		mTitleTextView.setText(R.string.add_text_info);

		mContentEdit = (BitmapCanInsertEditText) findViewById(R.id.edit);
		Intent i = getIntent();
		if (i != null) {
			String content = i.getStringExtra(PublishPage.KEY_PARAMS_CONTENT);
			if (!TextUtils.isEmpty(content)) {
				if (!InputUtil.isEmpty(content)) {
					SpannableString ss = new SpannableString(content);
					mContentEdit.setText("");
					Editable editable = mContentEdit.getText();
					// 解析表情，循环解析每一个表情
					for (String emoChar : EmotionHelper.emoChars) {
						int start = 0;
						int index;
						// 查找是否含有该表情字符串
						while ((index = content.indexOf(emoChar, start)) != -1) {
							// int index = content.indexOf(emoChar, start);
							// 如果有则换成对应表情
							Drawable drawable = getResources().getDrawable(
									EmotionHelper.getEmoImgByChar(emoChar));
							float density = ApplicationManager.getInstance()
									.getDensity();
							drawable.setBounds(0, 0,
									(int) (FACE_DEFAULT_SIZE * density),
									0 + (int) (FACE_DEFAULT_SIZE * density));
							// 得到imageSpan，插入SpannableString
							ImageSpan span = new ImageSpan(drawable,
									ImageSpan.ALIGN_BASELINE);
							ss.setSpan(span, index, index + emoChar.length(),
									Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							start = index + emoChar.length();
						}
					}
					editable.append(ss);
				}
			}
		}

		initBaseViews();

		mLeftWordsButton = (Button) findViewById(R.id.left_words);
		mBackButton.setOnClickListener(onClickListener);
		mSendButton.setOnClickListener(onClickListener);
		mInsertTopicButton.setOnClickListener(onClickListener);
		mInsertPeopleButton.setOnClickListener(onClickListener);
		mInsertFaceButton.setOnClickListener(onClickListener);

		setOnQuitCheckListener(new OnQuitCheckListener() {
			public void onQuitNoText() {
			}

			public void onQuitWithText() {
				startQuitDialogPage();
			}
		});

		setEditMaxCount(PUBLISH_MAX_TEXT_COUNT);
		mLeftWordsButton.setText(String.valueOf(PUBLISH_MAX_TEXT_COUNT));
		setOnEditTextChangedListener(new OnEditTextChangedListener() {
			public void onChanged(String str) {
				if (mPublicData != null) {
					mPublicData.setContent(str);
				}
			}
		});
		setEditTextWachter();
		setOnLeftWordsButtonOnClick();
		// setViewDisable(mSendButton);
	}

	protected boolean needSendButtonVisiable() {
		return (leftWords >= 0)
				&& (InputUtil.strip(mContentEdit.getText().toString()).length() > 0);
	}

	OnClickListener onClickListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				hideSoft();
				finish();
				break;
			case R.id.send:
				Intent i = new Intent();
				i.putExtra(PublishPage.KEY_PARAMS_CONTENT, mContentEdit
						.getText().toString());
				InputTextContentPage.this.setResult(RESULT_OK, i);
				hideSoft();
				finish();
				break;
			case R.id.insert_topic:
				startTopicSearchPage();
				break;
			case R.id.insert_people:
				startPeopleSearchPage();
				break;
			case R.id.insert_face:
				mContentEdit.requestFocus();
				checkToshowFaceViewOrNot();
				break;
			}
		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			hideSoft();
			finish();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private int mLeftWords;

	protected void setEditTextWachter() {
		mContentEdit.addTextChangedListener(mTextWatcher);
	}

	TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			int length = mContentEdit.getText().length();
			InputUtil.LOGD(TAG, "================>super length : " + length);
			if (mLeftWordsButton != null) {
				mLeftWords = EDIT_MAX_COUNT - length;
				mLeftWordsButton.setText(String.valueOf(mLeftWords));
				if (mLeftWords < 0) {
					mMainLayout.setBackgroundColor(Color.rgb(249, 228, 207));
					mLeftWordsButton
							.setBackgroundResource(R.drawable.input_left_words_bg_disable);
					mLeftWordsButton.setTextColor(Color.WHITE);
					setViewDisable(mSendButton);
				} else {
					mMainLayout.setBackgroundColor(Color.WHITE);
					mLeftWordsButton
							.setBackgroundResource(R.drawable.input_left_words_bg);
					mLeftWordsButton.setTextColor(Color.GRAY);
					setViewVisiable(mSendButton);
				}
				// if (needSendButtonVisiable()) {
				// setViewVisiable(mSendButton);
				// } else {
				// setViewDisable(mSendButton);
				// }
			}

			if (onEditTextChangedListener != null) {
				onEditTextChangedListener.onChanged(mContentEdit.getText()
						.toString());
			}
		}
	};

}
