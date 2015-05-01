package com.youa.mobile.common.base;

import android.view.View;
import android.widget.TextView;

import com.youa.mobile.R;

public class DefaultFooterView implements IFooterView {
	
	private View mFooterView;

	private TextView footerTextview;
	private View progressBar;


	public DefaultFooterView(View footerView) {
		mFooterView = footerView;
		footerTextview = (TextView) footerView
		.findViewById(R.id.footerText);
		progressBar = footerView.findViewById(R.id.footerProgressBar);
	}
	
	@Override
	public View getView() {
		return mFooterView;
	}

	@Override
	public void onPullHint() {
		// 是由RELEASE_To_REFRESH状态转变来的
		progressBar.setVisibility(View.GONE);
		footerTextview.setText(R.string.common_listview_viewmore);
	}

	@Override
	public void onRefreshHint() {
		progressBar.setVisibility(View.VISIBLE);
		footerTextview.setText("加载中....");
	}

}
