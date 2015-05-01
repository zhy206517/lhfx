package com.youa.mobile.input;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.util.NumberUtil;
import com.youa.mobile.input.util.InputUtil;

public class ConsumerExperiencePage extends BasePage {

	private final static String TAG = "ConsumerExperiencePage";

	private EditText 	mPeopleEdit;
	private EditText 	mPriceEdit;
	private Button 		mOkButton;
	private ImageButton mBackButton;
	private TextView 	mTitleTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_consume);
		initViews();
	}

	private void initViews() {
		mPeopleEdit 	= (EditText) findViewById(R.id.people);
		mPriceEdit 		= (EditText) findViewById(R.id.price);
		mTitleTextView 	= (TextView) findViewById(R.id.title);
		mOkButton 		= (Button) findViewById(R.id.send);
		mBackButton 	= (ImageButton) findViewById(R.id.back);
		mTitleTextView.setText(R.string.consume_title);
		mOkButton.setOnClickListener(onClickListener);
		mBackButton.setOnClickListener(onClickListener);
		mPeopleEdit.addTextChangedListener(mTextWatcher);
		mPriceEdit.addTextChangedListener(mTextWatcher);
		String propleNum =this.getIntent().getStringExtra(PublishPage.CONSUME_PEOPLE_NUM);
		String price =this.getIntent().getStringExtra(PublishPage.CONSUME_PRICE);
		if(!TextUtils.isEmpty(propleNum)){
			mPeopleEdit.setText(propleNum);
		}
		if(!TextUtils.isEmpty(price)){
			mPriceEdit.setText(price);
		}
//		hideSendButton();
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
			Float price = NumberUtil.toFloat((mPriceEdit.getText().toString()));
			if (price >= 0) {
				showSendButton();
			} else {
				hideSendButton();
			}
		}
	};

	private void showSendButton() {
		mOkButton.setClickable(true);
		mOkButton.setEnabled(true);
	}

	private void hideSendButton() {
		mOkButton.setClickable(false);
		mOkButton.setEnabled(false);
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.send:
				goBackWithData();
				break;
			}
		}
	};

	private void goBackWithData() {
		Intent data = new Intent();
		float price = NumberUtil.toFloat(mPriceEdit.getText().toString());
		int people = NumberUtil.toInt(mPeopleEdit.getText().toString(), -1);
		if(people<=0){
			Toast.makeText(ConsumerExperiencePage.this, R.string.consume_invalid, Toast.LENGTH_LONG).show();
			return;
		}else if(price<=0){
			Toast.makeText(ConsumerExperiencePage.this, R.string.consume_price_invalid, Toast.LENGTH_LONG).show();
			return;
		}
		DecimalFormat df = new DecimalFormat("0.00");
		if (people > 0) {
			float d = (float)(price / people); 
			float db = Float.parseFloat(df.format(d));
			data.putExtra(PublishPage.CONSUME_AV_PRICE, String.valueOf(db));
			data.putExtra(PublishPage.CONSUME_PRICE, String.valueOf(price));
			data.putExtra(PublishPage.CONSUME_PEOPLE_NUM, String.valueOf(people));
			data.putExtra(PublishPage.CONSUME_MANY_PEOPLE, true);
		}
		InputUtil.LOGD(TAG, "enter onclick send button <price> : " + price);
		setResult(RESULT_OK, data);
		finish();
	}
	@Override
	public void finish(){
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	if(mPeopleEdit.isFocused()){
    		imm.hideSoftInputFromWindow(mPeopleEdit.getWindowToken(),0);
    	}else if(mPriceEdit.isFocused()){
    		imm.hideSoftInputFromWindow(mPriceEdit.getWindowToken(),0);
    	}
		super.finish();
	}

}
