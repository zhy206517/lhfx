package com.youa.mobile.friend.friendsearch;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.youa.mobile.R;

public class MyEditText extends EditText {
	public interface Listener {
		void onClick();
	}

	private Listener listener;
	private BitmapDrawable bdDefault;// editText右边的image默认背景
	private BitmapDrawable bdHover;// editText右边的image按下时候的背景
	private BitmapDrawable searchBmp;
	// private Bitmap bmDefault;
	private Bitmap bmHover;

	// private int backgroudDeafult;
	// private int backgroudHover;
	// Context context;
	public MyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// this.context = context;
		TypedArray params = context.obtainStyledAttributes(attrs, R.styleable.MyEditText);
		int backgroudDeafult = params.getResourceId(R.styleable.MyEditText_img_background_default, 0);
		int backgroudHover = params.getResourceId(R.styleable.MyEditText_img_background_hover, 0);
		bdDefault = (BitmapDrawable) getResources().getDrawable(backgroudDeafult);
		// bmDefault=bdDefault.getBitmap();
		// ------------
		int width = (int) this.getResources().getDimension(R.dimen.search_width);
		int height = (int) this.getResources().getDimension(R.dimen.search_height);
		bdDefault.setBounds(0, 0, width, height);
		bdHover = (BitmapDrawable) getResources().getDrawable(backgroudHover);
		bmHover = bdHover.getBitmap();
		bdHover.setBounds(0, 0, bmHover.getWidth(), bmHover.getHeight());
		// ------------
		int search = params.getResourceId(R.styleable.MyEditText_img_search, 0);
		searchBmp = (BitmapDrawable) getResources().getDrawable(search);
		searchBmp.setBounds(0, 0, width, height);
		// ------------
		this.setHintTextColor(Color.GRAY);
		this.setCompoundDrawables(searchBmp, null, null, null);
		params.recycle();

		addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				if (getText().length() > 0) {
					setCompoundDrawables(searchBmp, null, bdDefault, null);
				} else {
					setCompoundDrawables(searchBmp, null, null, null);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setListener(Listener l) {
		listener = l;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		float x = event.getX();// 获得相对于父view的位置
		// float y = event.getY();

		float x2 = this.getWidth() - 40;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (x > x2 && x < this.getWidth()) {
				if (listener != null) {
					listener.onClick();
				}
				setText("");
				this.setCompoundDrawables(searchBmp, null, null, null);
				// this.setCompoundDrawables(searchBmp, null, bdHover, null);
				return true;
			} else {
				return super.onTouchEvent(event);
			}
		case MotionEvent.ACTION_UP:
			// this.setCompoundDrawables(searchBmp, null,bdDefault, null);
			if (x > x2 && x < this.getWidth()) {
//				setText("");
//				this.setCompoundDrawables(searchBmp, null, null, null);
				return true;
			} else {
				return super.onTouchEvent(event);
			}
		}
		return true;
	}
}
