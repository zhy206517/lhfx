package com.youa.mobile.input;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView.BufferType;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseSyncPage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.util.EmotionHelper;
import com.youa.mobile.content.ContentActivity;
import com.youa.mobile.information.FriendListPage;
import com.youa.mobile.login.auth.BaseToken;

public class BaseInputPage extends BaseSyncPage implements OnItemClickListener, OnClickListener {

	protected final static String TAG = BaseInputPage.class.getSimpleName();

	public final static int REQUEST_PEOPLE = 9;
	public final static int REQUEST_NEW_IMAGE = 2;
	public final static int REQUEST_TOPIC = 3;
	public final static int REQUEST_NEW_CONSUME = 4;
	public final static int REQUEST_SHOW_CONSUME = 5;
	public final static int REQUEST_SHOW_IMAGE = 6;
	public final static int REQUEST_QUIT_OR_NOT = 7;
	public final static int REQUEST_WORDS_CLEAN = 8;
	public final static String RESPONSE_TOPIC = "topic";
	public final static String RESPONSE_PEOPLE = "people";
	public final static int FACE_DEFAULT_SIZE = 22;
	

	protected int PUBLISH_MAX_TEXT_COUNT = 2000;
	protected int COMMENT_MAX_TEXT_COUNT = 140;
	protected int EDIT_MAX_COUNT;

	protected BitmapCanInsertEditText 	mContentEdit;
	protected Button 					mSendButton;
	protected ImageView 				mInsertTopicButton;
	protected ImageView 				mInsertPeopleButton;
	protected ImageView 				mInsertFaceButton;
	protected ViewGroup					mMainLayout;
	protected Button 					mLeftWordsButton;
	protected GridView 					mFaceView;
	protected LinearLayout 				mToolBar;

	protected OnEditTextChangedListener onEditTextChangedListener;
	protected OnQuitCheckListener 		onQuitCheckListener;

	private int faceViewHigh = -1;

	protected void setOnLeftWordsButtonOnClick() {
		mLeftWordsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mContentEdit.getText().length() > 0) {
					Intent intent = new Intent(BaseInputPage.this,
							InputWordsLimitDialogPage.class);
					startActivityForResult(intent, REQUEST_WORDS_CLEAN);
				}
			}
		});
	}
	protected void initBaseViews() {
		mFaceView.setAdapter(new EmoAdapter(this));
		mFaceView.setOnItemClickListener(this);
		mContentEdit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		hideFaceView();
	}

	protected void hideFaceView() {
		mFaceView.setVisibility(View.GONE);
		mInsertFaceButton.setImageResource(R.drawable.input_face_sec);
		showSoft();
	}

	protected void showFaceView() {
		hideSoft();
		mInsertFaceButton.setImageResource(R.drawable.input_soft_sec);
		mHandler.postDelayed(new Runnable() {
			public void run() {
				mFaceView.setVisibility(View.VISIBLE);	
			}
		}, 100);
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String emoChar = (String) arg0.getItemAtPosition(arg2);
		if(emoChar != null && emoChar.length()>0) {
			EditText input = mContentEdit;
			int cursorStart = input.getSelectionStart();
			int cursorEnd = input.getSelectionEnd();
			Drawable drawable = getResources().getDrawable(EmotionHelper.getEmoImg(arg2));  
			//float txtSize = input.getTextSize();
			float density = ApplicationManager.getInstance().getDensity();
	        drawable.setBounds(0, 0, (int) (FACE_DEFAULT_SIZE*density), 0+(int) (FACE_DEFAULT_SIZE*density));  
	        String emotion = EmotionHelper.getEmoChar(arg2);
	        //需要处理的文本，emotion是需要被替代的文本  
	        SpannableString spannable = new SpannableString(emotion);  
	        //要让图片替代指定的文字就要用EmotionImageSpan  
	        EmotionImageSpan span = new EmotionImageSpan(drawable, ImageSpan.ALIGN_BASELINE);  
	        //开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）  
	        //最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12  
	        spannable.setSpan(span, 0,emotion.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        span.setSource(emotion);
	        CharSequence oriText = (CharSequence) input.getText();
	        input.setText(oriText.subSequence(0, cursorStart),BufferType.SPANNABLE);
	        input.append(spannable);
	        input.append(oriText.subSequence(cursorEnd, oriText.length()));
	        input.setSelection(cursorStart+emotion.length(), cursorStart+emotion.length());	
		}
	}

	protected void setEditMaxCount(int count) {
		EDIT_MAX_COUNT = count;
	}
	protected void startQuitDialogPage() {
		Intent intent = new Intent(this, QuitDialogPage.class);
		startActivityForResult(intent, REQUEST_QUIT_OR_NOT);
	}
	protected void setViewVisiable(View view) {
		view.setVisibility(View.VISIBLE);
		view.setClickable(true);
		view.setEnabled(true);
	}

	protected void hideView(View view) {
		view.setVisibility(View.GONE);
	}

	protected void setViewDisable(View view) {
		view.setClickable(false);
		view.setEnabled(false);
	}

	public interface OnEditTextChangedListener {
		void onChanged(String str);
	}

	public interface OnQuitCheckListener {
		void onQuitWithText();
		void onQuitNoText();
	}

	protected void setOnEditTextChangedListener(
			OnEditTextChangedListener onEditTextChangedListener) {
		this.onEditTextChangedListener = onEditTextChangedListener;
	}

	protected void setOnQuitCheckListener(
			OnQuitCheckListener onQuitCheckListener) {
		this.onQuitCheckListener = onQuitCheckListener;
	}

	protected void goBack() {
		hideSoft();
		if (mContentEdit.getText().length() > 0) {
			if (onQuitCheckListener != null) {
				onQuitCheckListener.onQuitWithText();
			}
		} else {
			if (onQuitCheckListener != null) {
				onQuitCheckListener.onQuitNoText();
			}
			setResult(ContentActivity.commentBack);
			finish();
		}
	}

	protected void insertTextOnFocus(String str) {
		int cursor = mContentEdit.getSelectionStart();
		mContentEdit.getText().insert(cursor, str);
	}

	protected void initEditTextFocus() {
		mHandler.post(new Runnable() {
			public void run() {
				mContentEdit.setFocusable(true);
				mContentEdit.setFocusableInTouchMode(true);
				mContentEdit.requestFocus();
				mContentEdit.setSelection(mContentEdit.getText().length());
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
//		initEditTextFocus();
		hideFaceView();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			goBack();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_PEOPLE:
			if (data != null) {
				String str = data.getExtras().getString(
						FriendListPage.RESULT_USERNAME);
				insertTextOnFocus("@" + str + " ");
			}
			break;
		case REQUEST_TOPIC:
			if (data != null) {
				String str = data.getExtras().getString(RESPONSE_TOPIC);
				insertTextOnFocus("#" + str + "# ");
			}
			break;
		case REQUEST_QUIT_OR_NOT:
			if (resultCode == RESULT_OK) {
				finish();
			}
			break;
		case REQUEST_WORDS_CLEAN:
			if (resultCode == RESULT_OK) {
				mContentEdit.setText("");
			}
			break;
		}
	}

	protected void startPeopleSearchPage() {
		Intent intent = new Intent(this, FriendListPage.class);
		startActivityForResult(intent, REQUEST_PEOPLE);
	}

	protected void startTopicSearchPage() {
		Intent intent = new Intent(this, SearchTopicPage.class);
		startActivityForResult(intent, REQUEST_TOPIC);
	}

	protected void checkToshowFaceViewOrNot() {
		if (mFaceView.getVisibility() == View.VISIBLE) {
			hideFaceView();
		} else {
			showFaceView();
		}
		
	}

	protected void hideSoft() {
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
        imm.hideSoftInputFromWindow(mContentEdit.getWindowToken(),0);
    }

	protected void showSoft() {
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.showSoftInput(mContentEdit, 0);
    	if(faceViewHigh == -1) {
    		mHandler.postDelayed(new Runnable(){
				public void run() {
					ininFaceViewHigh();
				}
    		}, 500);
    	}
    }

	private void ininFaceViewHigh() {
		faceViewHigh = ApplicationManager.getInstance().getHeight() - mMainLayout.getBottom();
		if (faceViewHigh > 100) { 
			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(      
		               LinearLayout.LayoutParams.FILL_PARENT,      
		               faceViewHigh);
			mFaceView.setLayoutParams(p);
		} else {
			faceViewHigh = -1;
		}
		
	}

	@Override
	public void onAuthResult(BaseToken tokenData) {
		// TODO Auto-generated method stub
		
	}

}
