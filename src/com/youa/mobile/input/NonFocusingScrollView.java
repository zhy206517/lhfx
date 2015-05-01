package com.youa.mobile.input;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.youa.mobile.common.manager.ApplicationManager;

public class NonFocusingScrollView extends ScrollView {
	
	public NonFocusingScrollView(Context context) {
		super(context);
	}

	public NonFocusingScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NonFocusingScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected boolean onRequestFocusInDescendants(int direction,
			Rect previouslyFocusedRect) {
		return true;
	}
	
	@Override
	protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
		rect.bottom = rect.bottom
				+ (int) (ApplicationManager.getInstance().getDensity() * 20);
		
		return super.computeScrollDeltaToGetChildRectOnScreen(rect);
	}
	
}
