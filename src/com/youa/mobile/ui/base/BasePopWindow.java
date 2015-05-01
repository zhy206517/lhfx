package com.youa.mobile.ui.base;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

public class BasePopWindow {

	private OnClosePopListener mOnClosePop;

	public interface OnClosePopListener {
		void OnClose(View v);
	}

	public void setOnClosePopListener(OnClosePopListener onClosePop) {
		mOnClosePop = onClosePop;
	}

	private Context context;
	private View mAnchor;

	public BasePopWindow(Context context) {
		popUpWindow = new PopupWindow(context);
		this.context = context;
		popUpWindow.setFocusable(true);
		popUpWindow.setOutsideTouchable(true);
		onTuchListener = new PopUpOnTuchListener();
		popUpWindow.setTouchInterceptor(onTuchListener);
	}

	public void setBackgroundDrawable(int imgResource) {
		popUpWindow.setBackgroundDrawable(context.getResources().getDrawable(
				imgResource));
	}

	public void destroy(boolean destroyed) {
		if (destroyed) {
			popUpWindow = null;
			onTuchListener = null;
			return;
		}
		if (popUpWindow == null) {
			return;
		}
		popUpWindow.dismiss();
	}

	public void buildPopWindow(View root, int width, int height) {
		popUpWindow.setContentView(root);
		popUpWindow.setWidth(width);
		popUpWindow.setHeight(height);
	}

	public void buildPopWindowWithMode(View root, int width, int height) {
		popUpWindow.setContentView(root);
		popUpWindow.setWindowLayoutMode(width, height);
	}

	public void setHeight(int height) {
		popUpWindow.setHeight(height);
	}

	public void showDropDown(View anchor) {
		if (popUpWindow == null) {
			return;
		}
		mAnchor = anchor;
		popUpWindow.showAsDropDown(anchor);
	}

	public void showDropDown(View anchor, int offx, int offy) {
		if (popUpWindow == null) {
			return;
		} else {
			popUpWindow.dismiss();
		}
		mAnchor = anchor;
		popUpWindow.showAsDropDown(anchor, offx, offy);
	}

	public void show(View location, int gravity, int x, int y) {
		if (popUpWindow == null) {
			return;
		}
		mAnchor = location;
		popUpWindow.showAtLocation(location, gravity, x, y);
	}

	public void dismiss() {
		if (popUpWindow == null || !popUpWindow.isShowing()) {
			return;
		}
		popUpWindow.dismiss();
	}

	private class PopUpOnTuchListener implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mOnClosePop != null) {
				mOnClosePop.OnClose(mAnchor);
			}

			return false;
		}
	}

	private PopupWindow popUpWindow;
	private PopUpOnTuchListener onTuchListener;

}
