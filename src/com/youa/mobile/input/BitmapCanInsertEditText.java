package com.youa.mobile.input;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class BitmapCanInsertEditText extends EditText {

	public BitmapCanInsertEditText(Context context) {
		super(context);
	}

	public BitmapCanInsertEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BitmapCanInsertEditText(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public void insertBitmap(int resId, CharSequence source) {

		SpannableString ss = new SpannableString(source);
		Drawable d = getResources().getDrawable(resId);
	    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
	    ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
	    ss.setSpan(span, 0, source.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    getText().append(ss);

	}
	
}
