package com.youa.mobile.common.base;

import android.view.View;

public interface IHeaderView {
	View getView();
	void onPullHint(boolean isHasReverseAnimation);
	void onRelaseHint();
	void onRefreshHint();
	void onRefreshLastTime();
}
