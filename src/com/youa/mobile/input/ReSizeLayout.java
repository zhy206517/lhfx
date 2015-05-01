package com.youa.mobile.input;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ReSizeLayout extends RelativeLayout {
	public static final byte KEYBOARD_STATE_SHOW = -3;
	public static final byte KEYBOARD_STATE_HIDE = -2;
	public static final byte KEYBOARD_STATE_INIT = -1;
	private boolean mHasInit;
	private boolean mHasKeybord;
	private int mHeight;
	private onKybdsChangeListener mListener;

	public ReSizeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ReSizeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ReSizeLayout(Context context) {
		super(context);
	}

	/**
	 * set keyboard state listener
	 */
	public void setOnkbdStateListener(onKybdsChangeListener listener) {
		mListener = listener;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (!mHasInit) {
			mHasInit = true;
			mHeight = b;
			if (mListener != null) {
				mListener.onKeyBoardStateChange(KEYBOARD_STATE_INIT);
			}
		} else {
			mHeight = mHeight < b ? b : mHeight;
		}
		if (mHasInit && mHeight > b) {
			mHasKeybord = true;
			if (mListener != null) {
				mListener.onKeyBoardStateChange(KEYBOARD_STATE_SHOW);
			}
		}
		if (mHasInit && mHasKeybord && mHeight == b) {
			mHasKeybord = false;
			if (mListener != null) {
				mListener.onKeyBoardStateChange(KEYBOARD_STATE_HIDE);
			}
		}
		super.onLayout(changed, l, t, r, b);
	}

	public interface onKybdsChangeListener {
		public void onKeyBoardStateChange(int state);
	}
}
