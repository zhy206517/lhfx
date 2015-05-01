package com.youa.mobile.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePage;

public class BaseNewsPage extends BasePage {

	private TextView 		mTipsTextview;
	private TextView 		mLastUpdatedTextView;
	private ImageView 		mArrowImageView;
	private ProgressBar 	mProgressBar;
	private RotateAnimation mAnimation;
	private RotateAnimation mReverseAnimation;
	protected View 			mFooterView;
	protected LinearLayout 	mHeaderView;
	protected ProgressBar 	mFirstInProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater mInflater = LayoutInflater.from(this);
    	mHeaderView = (LinearLayout) mInflater.inflate(R.layout.feed_header, null);
    	mFooterView = mInflater.inflate(R.layout.feed_footer, null);
    	toTopInit();
	}

	// heed传进来，进行事件处理，
	protected void toTopInit() {
		mArrowImageView 		= (ImageView) mHeaderView.findViewById(R.id.head_arrowImageView);
		mProgressBar 			= (ProgressBar) mHeaderView.findViewById(R.id.head_progressBar);
		mTipsTextview 			= (TextView) mHeaderView.findViewById(R.id.head_tipsTextView);
		mLastUpdatedTextView 	= (TextView) mHeaderView.findViewById(R.id.head_lastUpdatedTextView);
		mAnimation = new RotateAnimation(
				0,
				-180,
				RotateAnimation.RELATIVE_TO_SELF,
				0.5f,
				RotateAnimation.RELATIVE_TO_SELF,
				0.5f);
		mAnimation.setInterpolator(new LinearInterpolator());
		mAnimation.setDuration(250);
		mAnimation.setFillAfter(true);
		mReverseAnimation = new RotateAnimation(
				-180,
				0,
				RotateAnimation.RELATIVE_TO_SELF,
				0.5f,
				RotateAnimation.RELATIVE_TO_SELF,
				0.5f);
		mReverseAnimation.setInterpolator(new LinearInterpolator());
		mReverseAnimation.setDuration(200);
		mReverseAnimation.setFillAfter(true);
	}

	protected void hideProgressBar() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mFirstInProgressBar.setVisibility(View.GONE);
			}
		});
	}

}
