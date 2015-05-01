package com.youa.mobile.input;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.youa.mobile.R;
import com.youa.mobile.YoumentEvent;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.content.ContentActivity;
import com.youa.mobile.input.action.RequestCommentAction;
import com.youa.mobile.input.util.InputUtil;

public class CommentPage extends BaseInputPage {

	private final static String TAG = "CommentPage";
	public final static String KEY_TYPE = "type";
	public final static String KEY_SOURCE = "sourceId";
	public final static String KEY_ORG_ID = "orgId";
	public final static String KEY_CMT_ID = "cmtId";
	public final static String KEY_PARAMS_ID = "u_id";
	public final static String KEY_REPLY_NAME = "reply_name";
	public final static String KEY_PARAMS_CONTENT = "content";
	public final static String KEY_PARAMS_SOURCE_ID = "source_id";
	public final static String KEY_PARAMS_COMMENT_ID = "comment_id";
	public final static String KEY_PARAMS_IS_FORWARD = "is_forward";
	public final static String KEY_PARAMS_COMMENT_OR_REPLY = "comment_or_reply";
	public final static String KEY_DEFAULT_CONTENT = "default_content";
	private ImageButton mBackButton;
	private ImageView mInsertPeopleButton;
	private ImageView mInsertTopicButton;
	private TextView mTitleTextView;
	private TextView mCheckBoxText;
	private CheckBox mCheckBox;
	private boolean mIsComment;
	private String mSourceId;
	private String mCommentId;
	private String mContent = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!ApplicationManager.getInstance().isLogin()) {
			startLoginActivity();
			finish();
			return;
		}

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mIsComment = bundle.getBoolean(KEY_TYPE, false);
			mSourceId = bundle.getString(KEY_SOURCE);
			mCommentId = bundle.getString(KEY_ORG_ID);
			String str = bundle.getString(KEY_DEFAULT_CONTENT);
			if (!TextUtils.isEmpty(str)) {
				mContent = str;
			}
			InputUtil.LOGD(TAG, "enter onCreate  data <isComment> : "
					+ mIsComment);
			InputUtil.LOGD(TAG, "enter onCreate  data <sourceId > : "
					+ mSourceId);
			InputUtil.LOGD(TAG, "enter onCreate  data <commentId> : "
					+ mCommentId);
		} else {
			finish();
		}
		setContentView(R.layout.input_comment);
		initViews();
		// initEditTextFocus();
	}

	private void initViews() {

		mMainLayout = (RelativeLayout) findViewById(R.id.main);
		mBackButton = (ImageButton) findViewById(R.id.back);
		mSendButton = (Button) findViewById(R.id.send);
		mInsertPeopleButton = (ImageView) findViewById(R.id.insert_people);
		mInsertTopicButton = (ImageView) findViewById(R.id.insert_topic);
		mInsertFaceButton = (ImageView) findViewById(R.id.insert_face);
		mCheckBox = (CheckBox) findViewById(R.id.checkbox);
		mTitleTextView = (TextView) findViewById(R.id.title);
		mCheckBoxText = (TextView) findViewById(R.id.checkbox_text);
		mContentEdit = (BitmapCanInsertEditText) findViewById(R.id.edit);
		mFaceView = (GridView) findViewById(R.id.face_view);
		mLeftWordsButton = (Button) findViewById(R.id.left_words);
		mToolBar = (LinearLayout) findViewById(R.id.tool_bar);
		initBaseViews();
		int resId = mIsComment ? R.string.comment_title : R.string.replay_title;
		mTitleTextView.setText(resId);
		mCheckBoxText.setText(R.string.comment_meantime_forward);
		setEditMaxCount(COMMENT_MAX_TEXT_COUNT);
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
		// if (!InputUtil.isEmpty(mContent)) {
		// mContentEdit.setText(mContent);
		// mContentEdit.setSelection(mContent.length());
		// }
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
			case R.id.insert_topic:
				startTopicSearchPage();
				break;
			case R.id.insert_people:
				startPeopleSearchPage();
				break;
			case R.id.insert_face:
				checkToshowFaceViewOrNot();
				break;
			}
		}
	};

	private void send() {
		if (mContentEdit.getText() != null
				&& mContentEdit.getText().length() > COMMENT_MAX_TEXT_COUNT) {
			showToast(R.string.over_text_length);
			return;
		}
		setViewDisable(mSendButton);
		boolean isForward = false;
		if (mCheckBox.isChecked()) {
			isForward = true;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(KEY_PARAMS_ID, ApplicationManager.getInstance().getUserId());
		params.put(KEY_PARAMS_CONTENT, mContent
				+ mContentEdit.getText().toString());
		params.put(KEY_PARAMS_SOURCE_ID, mSourceId);
		params.put(KEY_PARAMS_COMMENT_ID, mCommentId);
		params.put(KEY_PARAMS_IS_FORWARD, isForward);
		params.put(KEY_PARAMS_COMMENT_OR_REPLY, mIsComment);
		InputUtil.LOGD(TAG, " enter send() data <uid          > : "
				+ ApplicationManager.getInstance().getUserId());
		InputUtil.LOGD(TAG, " enter send() data <content      > : " + mContent
				+ mContentEdit.getText().toString());
		InputUtil.LOGD(TAG, " enter send() data <sourceId	  > : " + mSourceId);
		InputUtil.LOGD(TAG, " enter send() data <commentId    > : "
				+ mCommentId);
		InputUtil
				.LOGD(TAG, " enter send() data <isForward    > : " + isForward);
		InputUtil.LOGD(TAG, " enter send() data <isComment    > : "
				+ mIsComment);
		ActionController.post(this, RequestCommentAction.class, params,
				new RequestCommentAction.ICommentResultListener() {

					@Override
					public void onFinish() {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								hideProgressDialog();
								int resId = mIsComment ? R.string.comment_succ
										: R.string.reply_succ;
								showToastWithIcon(resId, mIconSuccResId);
								setResult(ContentActivity.commentOk);
								finish();
							}
						});

					}

					@Override
					public void onStart() {
						MobclickAgent.onEvent(CommentPage.this,
								YoumentEvent.EVENT_SEND,
								YoumentEvent.EVENT_SENDLABEL_COMMENT);
						showProgressDialog(CommentPage.this,
								R.string.input_send, R.string.input_wait);
					}

					@Override
					public void onFail(final int resourceID) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								setViewVisiable(mSendButton);
								MobclickAgent.onEvent(CommentPage.this,
										YoumentEvent.EVENT_SEND_FAIL);
								hideProgressDialog();
								showToast(resourceID);
							}
						});
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
