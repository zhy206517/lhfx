package com.youa.mobile.input;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
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
import com.youa.mobile.input.action.RequestForwardAction;
import com.youa.mobile.input.util.InputUtil;

public class ForwardPage extends BaseInputPage {

	private final static String TAG = "ForwardPage";

	public final static String KEY_PARAMS_ID = "u_id";
	public final static String KEY_PARAMS_CONTENT = "content";
	public final static String KEY_PARAMS_SOURCE_ID = "source_id";
	public final static String KEY_PARAMS_IS_COMMENT = "is_comment";
	public final static String KEY_PARAMS_FORWARD_CONTENT = "forward_content";
	public final static String KEY_SOURCE_ID = "sourceId";
	public final static String KEY_SOURCE_USERNAME = "sourceUsername";
	private ImageButton 	mBackButton;
	private ImageView 		mInsertPeopleButton;
	private ImageView 		mInsertTopicButton;
	private TextView 		mTitleTextView;
	private TextView 		mCheckBoxText;
	private TextView 		mCommentToText;
	private CheckBox 		mCheckBox;
	private String mSourceId;
	private String mSurceUserName;
//	private String mForwardContent;

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
			mSourceId = bundle.getString(KEY_SOURCE_ID);
//			mSurceUserName = bundle.getString(KEY_SOURCE_USERNAME);
//			mForwardContent = bundle.getString(KEY_PARAMS_FORWARD_CONTENT);
			InputUtil.LOGD(TAG, "enter onCreate  data <sourceId		  > : " + mSourceId);
			InputUtil.LOGD(TAG, "enter onCreate  data <sourceUserName > : " + mSurceUserName);
//			InputUtil.LOGD(TAG, "enter onCreate  data <mForwardContent> : " + mForwardContent);
		} else {
			finish();
		}
		setContentView(R.layout.input_comment);
		initViews();
//		initEditTextFocus();
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

		mMainLayout 		= (RelativeLayout) findViewById(R.id.main);
		mBackButton 		= (ImageButton) findViewById(R.id.back);
		mSendButton 		= (Button) findViewById(R.id.send);
		mInsertPeopleButton = (ImageView) findViewById(R.id.insert_people);
		mInsertTopicButton 	= (ImageView) findViewById(R.id.insert_topic);
		mInsertFaceButton 	= (ImageView) findViewById(R.id.insert_face);
		mCheckBox 			= (CheckBox) findViewById(R.id.checkbox);
		mTitleTextView 		= (TextView) findViewById(R.id.title);
		mCheckBoxText 		= (TextView) findViewById(R.id.checkbox_text);
//		mCommentToText 		= (TextView) findViewById(R.id.comment_to);
		mContentEdit 		= (BitmapCanInsertEditText) findViewById(R.id.edit);
		mFaceView			= (GridView) findViewById(R.id.face_view);
		mLeftWordsButton 	= (Button) findViewById(R.id.left_words);
		mToolBar			= (LinearLayout) findViewById(R.id.tool_bar);
		initBaseViews();
		mTitleTextView.setText(R.string.forward_title);
		mCheckBoxText.setText(R.string.forward_meantime_comment);
//		mCommentToText.setText(mSurceUserName);
		setEditMaxCount(COMMENT_MAX_TEXT_COUNT);
		setEditTextWachter();
		//setEditTextWachter();
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
//		if (!InputUtil.isEmpty(mForwardContent)) {
//			mContentEdit.setText(mForwardContent);
//			mContentEdit.setSelection(0);
//		}
	}


	OnClickListener onClickListener = new OnClickListener(){

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
		if( mContentEdit.getText()!=null&& mContentEdit.getText().length()>COMMENT_MAX_TEXT_COUNT){
			showToast(R.string.over_text_length);
			return;
		}
		setViewDisable(mSendButton);
		boolean isComment = false;
		if (mCheckBox.isChecked()) {
			isComment = true;
		}
		Map<String , Object> params = new HashMap<String, Object>();
		params.put(KEY_PARAMS_ID, ApplicationManager.getInstance().getUserId());
		params.put(KEY_PARAMS_CONTENT, mContentEdit.getText().toString());
		params.put(KEY_PARAMS_SOURCE_ID, mSourceId);
		params.put(KEY_PARAMS_IS_COMMENT, isComment);
		final boolean isCommentTemp = isComment;
		ActionController.post(
				this,
				RequestForwardAction.class,
				params,
				new RequestForwardAction.IForwardResultListener() {

					@Override
					public void onFinish() {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								hideProgressDialog();
								showToastWithIcon(R.string.forward_succ, mIconSuccResId);
								if (isCommentTemp) {
									setResult(ContentActivity.commentOk);
								}
								finish();
							}
						});

					}
					@Override
					public void onStart() {
						//添加友盟统计(发布)>>> begin
						MobclickAgent.onEvent(
								ForwardPage.this, 
								YoumentEvent.EVENT_SEND, 
								YoumentEvent.EVENT_SENDLABEL_FORWARD);
						//<<< end
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								showProgressDialog(ForwardPage.this, R.string.input_send, R.string.input_wait);
							}
						});
					}

					@Override
					public void onFail(final int resourceID) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								setViewVisiable(mSendButton);
								MobclickAgent.onEvent(ForwardPage.this,
										YoumentEvent.EVENT_SEND_FAIL);
								hideProgressDialog();
								showToast(resourceID);
							}
						});
					}
				},
				true);
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
					mLeftWordsButton.setBackgroundResource(R.drawable.input_left_words_bg_disable);
					mLeftWordsButton.setTextColor(Color.WHITE);
//					setViewDisable(mSendButton);
				} else {
					mMainLayout.setBackgroundColor(Color.WHITE);
					mLeftWordsButton.setBackgroundResource(R.drawable.input_left_words_bg);
					mLeftWordsButton.setTextColor(Color.GRAY);
					
				}
//				if (needSendButtonVisiable()) {
//					setViewVisiable(mSendButton);
//				} else {
//					setViewDisable(mSendButton);
//				}
			}
		
			if (onEditTextChangedListener != null) {
				onEditTextChangedListener.onChanged(mContentEdit.getText()
						.toString());
			}
		}
	};
}
