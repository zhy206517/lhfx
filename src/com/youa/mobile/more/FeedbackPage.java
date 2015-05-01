package com.youa.mobile.more;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.input.BaseInputPage;
import com.youa.mobile.input.BitmapCanInsertEditText;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.more.action.FeedbackSendAction;

public class FeedbackPage extends BaseInputPage {

	private final static String TAG = "FeedbackPage";

	public final static String KEY_PARAMS_ID = "u_id";
	public final static String KEY_PARAMS_CONTENT = "content";
	public final static String KEY_PARAMS_SOURCE_ID = "source_id";
	public final static String KEY_PARAMS_IS_COMMENT = "is_comment";
	public final static String KEY_PARAMS_FORWARD_CONTENT = "forward_content";
	public final static String KEY_SOURCE_ID = "sourceId";
	public final static String KEY_SOURCE_USERNAME = "sourceUsername";
	private ImageButton mBackButton;
	private ImageView mInsertPeopleButton;
	private ImageView mInsertTopicButton;
	private TextView mTitleTextView;
	public final static String VALUE_PARAMS_SUGGEST = "11";
	public final static String VALUE_PARAMS_PROBLEM = "22";
	public final static String VALUE_PARAMS_OTHER = "33";
	public final static String KEY_PARAMS_TYPE = "feedback_type";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_back);
		initViews();
	}

	protected void initEditTextFocus() {
		mHandler.post(new Runnable() {
			public void run() {
				mContentEdit.setFocusable(true);
				mContentEdit.setFocusableInTouchMode(true);
				mContentEdit.requestFocus();
				mContentEdit.setSelection(0);
			}
		});
	}

	private void initViews() {
		mMainLayout = (RelativeLayout) findViewById(R.id.main);
		mBackButton = (ImageButton) findViewById(R.id.back);
		mSendButton = (Button) findViewById(R.id.send);
		mInsertPeopleButton = (ImageView) findViewById(R.id.insert_people);
		mInsertTopicButton = (ImageView) findViewById(R.id.insert_topic);
		mInsertFaceButton = (ImageView) findViewById(R.id.insert_face);
		mTitleTextView = (TextView) findViewById(R.id.title);
		mContentEdit = (BitmapCanInsertEditText) findViewById(R.id.edit);
		mFaceView = (GridView) findViewById(R.id.face_view);
		mLeftWordsButton = (Button) findViewById(R.id.left_words);
		mToolBar = (LinearLayout) findViewById(R.id.tool_bar);
		initBaseViews();
		setViewDisable(mToolBar);
		mTitleTextView.setText(R.string.feedback_lable);
		setEditMaxCount(PUBLISH_MAX_TEXT_COUNT);
		setEditTextWachter();
		setOnQuitCheckListener(new OnQuitCheckListener() {
			public void onQuitNoText() {
			}

			public void onQuitWithText() {
				startQuitDialogPage();
			}
		});
		mInsertTopicButton.setOnClickListener(onClickListener);
		mInsertPeopleButton.setOnClickListener(onClickListener);
		mInsertFaceButton.setOnClickListener(onClickListener);
		mBackButton.setOnClickListener(onClickListener);
		mSendButton.setOnClickListener(onClickListener);
		setOnLeftWordsButtonOnClick();
		setViewDisable(mSendButton);
	}

	OnClickListener onClickListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				goBack();
				break;
			case R.id.send:
				send();
				break;
			}
		}
	};

	private void send() {
		setViewDisable(mSendButton);
		String content = mContentEdit.getText().toString();
		if (content.length() < 10) {
			showToast(FeedbackPage.this, R.string.feedback_err_message);
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(KEY_PARAMS_ID, ApplicationManager.getInstance().getUserId());
		params.put(KEY_PARAMS_CONTENT, content);
		params.put(KEY_PARAMS_TYPE, VALUE_PARAMS_SUGGEST);
		ActionController.post(this, FeedbackSendAction.class, params,
				new FeedbackSendAction.IFeedbackSendResultListener() {
					@Override
					public void onFinish() {
						hideProgressDialog();
						showToastWithIcon(R.string.input_send_succ,
								mIconSuccResId);
						finish();
					}

					@Override
					public void onStart() {
						showProgressDialog(FeedbackPage.this,
								R.string.input_send, R.string.input_wait);
					}

					@Override
					public void onFail(int resourceID) {
						setViewVisiable(mSendButton);
						hideProgressDialog();
						showToast(resourceID);
					}
				}, true);
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

				}
				if (needSendButtonVisiable()) {
					setViewVisiable(mSendButton);
				} else {
					setViewDisable(mSendButton);
				}
			}

			if (onEditTextChangedListener != null) {
				onEditTextChangedListener.onChanged(mContentEdit.getText()
						.toString());
			}
		}
	};
	protected boolean needSendButtonVisiable() {
		return (mLeftWords >= 0) && (InputUtil.strip(mContentEdit.getText().toString()).length() > 0);
	}
}
