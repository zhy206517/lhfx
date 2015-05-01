package com.youa.mobile.common.base;

import android.view.View;

public interface IFooterView {
	View getView();
	void onPullHint();
	void onRefreshHint();
}
