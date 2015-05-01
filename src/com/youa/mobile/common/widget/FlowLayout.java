package com.youa.mobile.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.youa.mobile.R;

public final class FlowLayout extends ViewGroup {

	private int mLineHeight;
	private int mHorizontalSpacing;
	private int mVerticalSpacing;

	public FlowLayout(Context context) {
		super(context);
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
		mHorizontalSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_horizontalSpacing, 0);
		mVerticalSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_verticalSpacing, 0);
		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);

		final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
		int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
		final int count = getChildCount();
		int lineHeight = 0;

		int xpos = getPaddingLeft();
		int ypos = getPaddingTop();

		int childHeightMeasureSpec;
		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
			childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
		} else {
			childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}

		final int cw = (width - mHorizontalSpacing * 3) / 3;
		// if (Constants.LOGD) {
		// Log.d(Constants.TAG, "onMeasure. width = " + width + ", h s = " + mHorizontalSpacing + ", c w = " + cw);
		// }
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				child.measure(MeasureSpec.makeMeasureSpec(cw, MeasureSpec.EXACTLY), childHeightMeasureSpec);
				lineHeight = Math.max(lineHeight, child.getMeasuredHeight() + mVerticalSpacing);
				if (i == count - 1) {
					continue;
				}
				xpos += cw + mHorizontalSpacing;
				if (xpos + cw > width) {
					xpos = getPaddingLeft();
					ypos += lineHeight;
				}
			}
		}
		this.mLineHeight = lineHeight;

		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
			height = ypos + lineHeight;
		} else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
			if (ypos + lineHeight < height) {
				height = ypos + lineHeight;
			}
		}
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int count = getChildCount();
		final int width = r - l;
		int xpos = getPaddingLeft();
		int ypos = getPaddingTop();

		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				final int childw = child.getMeasuredWidth();
				final int childh = child.getMeasuredHeight();
				child.layout(xpos, ypos, xpos + childw, ypos + childh);
				if (i == count - 1) {
					continue;
				}
				xpos += childw + mHorizontalSpacing;
				if (xpos + childw >= width) {
					xpos = getPaddingLeft();
					ypos += mLineHeight;
				}
			}
		}
	}

	public int getHorizontalSpacing() {
		return mHorizontalSpacing;
	}

	public void setHorizontalSpacing(int horizontalSpacing) {
		this.mHorizontalSpacing = horizontalSpacing;
	}

	public int getVerticalSpacing() {
		return mVerticalSpacing;
	}

	public void setVerticalSpacing(int verticalSpacing) {
		this.mVerticalSpacing = verticalSpacing;
	}
}
