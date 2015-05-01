package com.youa.mobile.input;

import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

public class EmotionImageSpan extends ImageSpan {

	private String oriSource = "";
	public EmotionImageSpan(Drawable d, int verticalAlignment) {
		super(d, verticalAlignment);
	}
	
	public String getOriSource()
	{
		return oriSource;
	}
	public void setSource(String source)
	{
		oriSource = new String(source);
	}

	public boolean changed(String source) {
		return !oriSource.equals(source);
	}

}
