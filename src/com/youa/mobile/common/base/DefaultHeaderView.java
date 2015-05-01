package com.youa.mobile.common.base;

import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.utils.Tools;

public class DefaultHeaderView implements IHeaderView {
	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	long lastTime = 0;
	private View mHeaderView;
	private String timeText = "";

	public DefaultHeaderView(View headerView) {
		mHeaderView = headerView;
		arrowImageView = (ImageView) headerView.findViewById(R.id.head_arrowImageView);
		// arrowImageView.setMinimumWidth(70);
		// arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) headerView.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headerView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headerView.findViewById(R.id.head_lastUpdatedTextView);

		animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);
	}

	@Override
	public View getView() {
		return mHeaderView;
	}

	@Override
	public void onPullHint(boolean isHasReverseAnimation) {
		progressBar.setVisibility(View.GONE);
		tipsTextview.setVisibility(View.VISIBLE);
		lastUpdatedTextView.setVisibility(View.VISIBLE);
		lastUpdatedTextView.setText(String.format(
				mHeaderView.getContext().getString(R.string.common_listview_last_refresh), timeText));
		arrowImageView.clearAnimation();
		arrowImageView.setVisibility(View.VISIBLE);
		// 是由RELEASE_To_REFRESH状态转变来的
		if (isHasReverseAnimation) {
			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(reverseAnimation);
			tipsTextview.setText("下拉刷新");
		} else {
			tipsTextview.setText("下拉刷新");
		}
	}

	@Override
	public void onRefreshHint() {
		// -------------------------------------
		// // -------------------------------------
		lastTime = System.currentTimeMillis();
		progressBar.setVisibility(View.VISIBLE);
		arrowImageView.clearAnimation();
		arrowImageView.setVisibility(View.GONE);
		tipsTextview.setText("正在刷新...");
		if (TextUtils.isEmpty(timeText)) {
			setTime(lastTime);
		}
		lastUpdatedTextView.setText(String.format(
				mHeaderView.getContext().getString(R.string.common_listview_last_refresh), timeText));
		lastUpdatedTextView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onRelaseHint() {
		// if(lastTime==0){
		// lastTime = System.currentTimeMillis();
		// }
		// setTime(lastTime);
		// lastTime = System.currentTimeMillis();
		arrowImageView.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		tipsTextview.setVisibility(View.VISIBLE);
		lastUpdatedTextView.setVisibility(View.VISIBLE);
		lastUpdatedTextView.setText(String.format(
				mHeaderView.getContext().getString(R.string.common_listview_last_refresh), timeText));
		arrowImageView.clearAnimation();
		arrowImageView.startAnimation(animation);

		tipsTextview.setText("松开刷新");
	}

	@Override
	public void onRefreshLastTime() {
		if (lastTime == 0) {
			lastTime = System.currentTimeMillis();
		}
		setTime(lastTime);
		lastUpdatedTextView.setVisibility(View.VISIBLE);
		lastUpdatedTextView.setText(String.format(
				mHeaderView.getContext().getString(R.string.common_listview_last_refresh), timeText));
	}

	private void setTime(long time) {
		String updateTime = null;
		if (time == 0) {
			updateTime = mHeaderView.getContext().getResources().getString(R.string.feed_refresh_null);
		} else {
			updateTime = Tools.translateToString(time);
		}
		timeText = updateTime;
	}

}
