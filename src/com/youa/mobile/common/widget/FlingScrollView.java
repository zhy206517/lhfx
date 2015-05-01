package com.youa.mobile.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.utils.Tools;

public class FlingScrollView extends LinearLayout{
	private static final int TAP_TO_REFRESH = 1;
	private static final int PULL_TO_REFRESH = 2;
	private static final int RELEASE_TO_REFRESH = 3;
	private static final int REFRESHING = 4;
	public int mRefreshState;
	public int mfooterRefreshState;
	public Scroller scroller;
	public ScrollView sv;
	private View refreshView;
	public View mfooterView;
//	public TextView mfooterViewText;
	private ImageView refreshIndicatorView;
	private int refreshTargetTop = -(int)(55 * ApplicationManager.getInstance().getDensity());
	public int refreshFooter;
	private ProgressBar bar;
	private TextView downTextView;
	private TextView timeTextView;
	private String updateTime;
	private RefreshListener refreshListener;
	long lastTime=0;
	private int lastY;
	private RotateAnimation mFlipAnimation;
	private RotateAnimation mReverseFlipAnimation;
	public int nowpull = -1;
	private boolean isRecord;
	private Context mContext;

	public FlingScrollView(Context context) {
		super(context);
		mContext = context;

	}

	public FlingScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();

	}

	private void init() {
		mFlipAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		mFlipAnimation.setDuration(250);
		mFlipAnimation.setFillAfter(true);
		mReverseFlipAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(250);
		mReverseFlipAnimation.setFillAfter(true);
		scroller = new Scroller(mContext);
		refreshView = LayoutInflater.from(mContext).inflate(
				R.layout.feed_header, null);
		refreshIndicatorView = (ImageView) refreshView
				.findViewById(R.id.head_arrowImageView);
		bar = (ProgressBar) refreshView
				.findViewById(R.id.head_progressBar);
		downTextView = (TextView) refreshView
				.findViewById(R.id.head_tipsTextView);
		timeTextView = (TextView) refreshView
				.findViewById(R.id.head_lastUpdatedTextView);
//		refreshView.setMinimumHeight(50);
		LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, -refreshTargetTop);
		lp.topMargin = refreshTargetTop;
		lp.gravity = Gravity.CENTER;
		addView(refreshView, lp);

		isRecord = false;

		mRefreshState = TAP_TO_REFRESH;
		mfooterRefreshState = TAP_TO_REFRESH;

	}

	public boolean onTouchEvent(MotionEvent event) {

		int y = (int) event.getRawY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (isRecord == false) {
				lastY = y;
				isRecord = true;
			}
			break;

		case MotionEvent.ACTION_MOVE:
			int m = y - lastY;
			doMovement(m);
			lastY = y;
			break;
		case MotionEvent.ACTION_UP:
			Log.i("TAG", "ACTION_UP");
			fling();
			isRecord = false;
			break;
		}
		return true;
	}

	private void fling() {
		// TODO Auto-generated method stub
		if (nowpull == 0 && mRefreshState != REFRESHING) {
			LinearLayout.LayoutParams lp = (LayoutParams) refreshView
					.getLayoutParams();
			Log.i("TAG", "fling()" + lp.topMargin);
			if (lp.topMargin > 0) {
				refresh();
			} else {
				returnInitState();
			}
		} else if (nowpull == 1 && mfooterRefreshState != REFRESHING) {

			if (refreshFooter >= 20
					&& mfooterRefreshState == RELEASE_TO_REFRESH) {
				mfooterRefreshState = REFRESHING;
				FooterPrepareForRefresh(); // ׼��ˢ��
				onRefresh(); // ˢ��
			} else {
				if (refreshFooter >= 0)
					resetFooterPadding();
				else {
					resetFooterPadding();
					mfooterRefreshState = TAP_TO_REFRESH;
					Log.i("other", "i::" + refreshFooter);
					WaterFallScrollView.ScrollToPoint(sv,
							sv.getChildAt(0), -refreshFooter);
				}
			}
		}
	}

	// ˢ��
	public void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	private void returnInitState() {
		// TODO Auto-generated method stub
		mRefreshState = TAP_TO_REFRESH;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
				.getLayoutParams();
		int i = lp.topMargin;
		scroller.startScroll(0, i, 0, refreshTargetTop);
		invalidate();
	}

	private void refresh() {
		// TODO Auto-generated method stub
		mRefreshState = REFRESHING;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
				.getLayoutParams();
		int i = lp.topMargin;
		refreshIndicatorView.setVisibility(View.GONE);
		refreshIndicatorView.setImageDrawable(null);
		bar.setVisibility(View.VISIBLE);
		//timeTextView.
		timeTextView.setVisibility(View.VISIBLE);
		downTextView.setText(R.string.common_listview_loading);
		downTextView.setVisibility(View.VISIBLE);
		scroller.startScroll(0, i, 0, 0 - i);
		invalidate();
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}
	public void refreshTab() {
		// TODO Auto-generated method stub
		mRefreshState = REFRESHING;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
				.getLayoutParams();
		int i = lp.topMargin;
		refreshIndicatorView.setVisibility(View.GONE);
		refreshIndicatorView.setImageDrawable(null);
		bar.setVisibility(View.VISIBLE);
		//timeTextView.
		timeTextView.setVisibility(View.VISIBLE);
		setTime(lastTime);
		downTextView.setText(R.string.common_listview_loading);
		downTextView.setVisibility(View.VISIBLE);
		scroller.startScroll(0, i, 0, 0 - i);
		invalidate();
	}
	private void resetFooterPadding() {
		LayoutParams svlp = (LayoutParams) sv.getLayoutParams();
		svlp.bottomMargin = 0;
		sv.setLayoutParams(svlp);
		WaterFallScrollView.ScrollToPoint(sv, sv.getChildAt(0), 0);

	}

	public void FooterPrepareForRefresh() {
		resetFooterPadding();
//		mfooterViewText
//				.setText(R.string.pull_to_refresh_footer_refreshing_label);
		mfooterRefreshState = REFRESHING;
	}

	/**
	 * 
	 */
	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			int i = this.scroller.getCurrY();
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
					.getLayoutParams();
			int k = Math.max(i, refreshTargetTop);
			lp.topMargin = k;
			this.refreshView.setLayoutParams(lp);
			this.refreshView.invalidate();
			invalidate();
		}
	}
	public void doMovement(int moveY) {
		LinearLayout.LayoutParams lp = (LayoutParams) refreshView
				.getLayoutParams();
		if (sv.getScrollY() == 0 && moveY > 0 && refreshFooter <= 0) {
			nowpull = 0;
		}
		if (sv.getChildAt(0).getMeasuredHeight() <= sv.getScrollY()
				+ getHeight()
				&& moveY < 0 && lp.topMargin <= refreshTargetTop) {
			nowpull = 1;
		}
		if (nowpull == 0 && mRefreshState != REFRESHING) {
			float f1 = lp.topMargin;
			float f2 = f1 + moveY * 0.3F;
			int i = (int) f2;
			lp.topMargin = i;
			refreshView.setLayoutParams(lp);
			refreshView.invalidate();
			invalidate();
			setTime(lastTime);
			downTextView.setVisibility(View.VISIBLE);
			refreshIndicatorView.setVisibility(View.VISIBLE);
			bar.setVisibility(View.GONE);
			if (lp.topMargin > 0 && mRefreshState != RELEASE_TO_REFRESH) {
				downTextView.setText(R.string.refresh_release_text);
				refreshIndicatorView.clearAnimation();
				refreshIndicatorView.startAnimation(mFlipAnimation);
				mRefreshState = RELEASE_TO_REFRESH;
			} else if (lp.topMargin <= 0 && mRefreshState != PULL_TO_REFRESH) {
				downTextView.setText(R.string.refresh_down_text);
				if (mRefreshState != TAP_TO_REFRESH) {
					refreshIndicatorView.clearAnimation();
					refreshIndicatorView.startAnimation(mReverseFlipAnimation);
				}
				mRefreshState = PULL_TO_REFRESH;
			}
		} else if (nowpull == 1 && mfooterRefreshState != REFRESHING) {
			LayoutParams svlp = (LayoutParams) sv.getLayoutParams();
			svlp.bottomMargin = svlp.bottomMargin - moveY;
			refreshFooter = svlp.bottomMargin;
			sv.setLayoutParams(svlp);
			WaterFallScrollView.ScrollToPoint(sv, sv.getChildAt(0), 0);
			if (svlp.bottomMargin >= 20
					&& mfooterRefreshState != RELEASE_TO_REFRESH) {
//				mfooterViewText.setText(R.string.pull_to_refresh_footer_label);
				mfooterRefreshState = RELEASE_TO_REFRESH;
			} else if (svlp.bottomMargin < 20
					&& mfooterRefreshState != PULL_TO_REFRESH) {
//				mfooterViewText
//						.setText(R.string.pull_to_refresh_footer_pull_label);
				mfooterRefreshState = PULL_TO_REFRESH;
			}

		}

	}

	public void setRefreshListener(RefreshListener listener) {
		this.refreshListener = listener;
	}
	public void finishRefresh() {
		if (mRefreshState != TAP_TO_REFRESH) {
			lastTime = System.currentTimeMillis();
			mRefreshState = TAP_TO_REFRESH;
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
					.getLayoutParams();
			int i = lp.topMargin;
			refreshIndicatorView.setImageResource(R.drawable.feed_head_refresh);
			refreshIndicatorView.clearAnimation();
			bar.setVisibility(View.GONE);
			refreshIndicatorView.setVisibility(View.GONE);
			downTextView.setVisibility(View.GONE);
			timeTextView.setVisibility(View.GONE);
			scroller.startScroll(0, i, 0, refreshTargetTop);
			invalidate();
		}
		if (mfooterRefreshState != TAP_TO_REFRESH) {
			resetFooter();
		}
	}

	public void resetFooter() {
		mfooterRefreshState = TAP_TO_REFRESH;
		resetFooterPadding();
//		mfooterViewText.setText(R.string.pull_to_refresh_footer_pull_label);

	}

	public void HideFooter() {
		LayoutParams mfvlp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mfvlp.bottomMargin = refreshFooter;
		mfooterView.setLayoutParams(mfvlp);
		mfooterRefreshState = TAP_TO_REFRESH;
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		int action = e.getAction();
		int y = (int) e.getRawY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (isRecord == false) {
				Log.i("moveY", "lastY:" + y);
				lastY = y;
				isRecord = true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int m = y - lastY;

			if (canScroll(m)) {
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			isRecord = false;
			break;

		case MotionEvent.ACTION_CANCEL:

			break;
		}
		return false;
	}

	private boolean canScroll(int diff) {
		// TODO Auto-generated method stub
		View childView;
		if (mRefreshState == REFRESHING || mfooterRefreshState == REFRESHING) {
			return true;
		}
		if (getChildCount() > 1) {
			childView = this.getChildAt(1);
			if (childView instanceof ListView) {
				int top = ((ListView) childView).getChildAt(0).getTop();
				int pad = ((ListView) childView).getListPaddingTop();
				if ((Math.abs(top - pad)) < 3
						&& ((ListView) childView).getFirstVisiblePosition() == 0) {
					return true;
				} else {
					return false;
				}
			} else if (childView instanceof ScrollView) {
				if (((ScrollView) childView).getScrollY() == 0 && diff > 0) {
					nowpull = 0;
					return true;
				} else if ((((ScrollView) childView).getChildAt(0)
						.getMeasuredHeight() <= ((ScrollView) childView)
						.getScrollY() + getHeight() && diff < 0)) {
					nowpull = 1;

					return true;
				} else {
					return false;
				}
			}

		}
		return false;
	}
	public interface RefreshListener {
		public void onRefresh();
	}
	private void setTime(long time) {
		if(time==0){
			time=System.currentTimeMillis();
		}
		updateTime = Tools.translateToString(time);
		timeTextView.setText(updateTime);
	}
}
