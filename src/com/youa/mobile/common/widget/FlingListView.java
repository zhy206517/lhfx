package com.youa.mobile.common.widget;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.youa.mobile.R;
import com.youa.mobile.common.base.DefaultFooterView;
import com.youa.mobile.common.base.DefaultHeaderView;
import com.youa.mobile.common.base.IFooterView;
import com.youa.mobile.common.base.IHeaderView;
import com.youa.mobile.common.manager.ApplicationManager;

public class FlingListView<T> extends RelativeLayout implements OnTouchListener {

	public static final int SEARCHSTATUS_DEFAULT = 0;
	public static final int SEARCHSTATUS_PREPARED_REFRESH = 1;
	public static final int SEARCHSTATUS_PREPARED_UPDATEMORE = 2;
	
	boolean fastScrollValid = true;
	private OnScrollEndListener mOnScrollEndListener;
	private AbsListView mListView;
	private IHeaderView mHeader;
	private IFooterView mFooter;
	private RelativeLayout.LayoutParams headerparams;
	private RelativeLayout.LayoutParams footerparams;
	private RelativeLayout.LayoutParams listparams;
	private int headerHeight = (int)(55 * ApplicationManager.getInstance().getDensity());
	private int footerHeight = (int)(55 * ApplicationManager.getInstance().getDensity());
	private int dragHeight = 100;
	private View arrowImageView;
	private ProgressBar progressBar;
	private TextView tipsTextview ;
	private TextView lastUpdatedTextView;
	private RotateAnimation rotateAnimation;
	private RotateAnimation reverseAnimation;
//	private List<T> mDataList;
	private FlingAdapter<T> mFlingAdapter;
	private int searchstatus = SEARCHSTATUS_DEFAULT;

	
	boolean isBouncing = false;
	int bounced = 0;
	boolean bouncedHigh = false;
	float yPosStart = 0;
	float lastYPos = 0;
	boolean readyBouncing = false;
	int threshold = 50;
	int listenThreshold = 60;
	boolean isClickingSlider = false;
	boolean bouncingTop = true;
	private Handler mHandler = new Handler();

	public FlingListView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		setOrientation(LinearLayout.VERTICAL);
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				initView();
			}
		});
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(this.getChildCount()>0 && bounced>0 && bouncingTop)
		{
			Drawable d = new BitmapDrawable(mHeader.getView().getDrawingCache());
			if(d!=null)
			{
				int width = d.getIntrinsicWidth();
				int height = d.getIntrinsicHeight();
				int listWidth = getWidth();
				d.setBounds(listWidth/2-width/2, 0-height, listWidth/2+width/2, 0);
				d.draw(canvas);
			}
		}
	}
	
	private void initView() {
		if(mListView != null) {
			return;
		} 
		int count = getChildCount();
		if(count < 0) {
			throw new RuntimeException("please add view");
//		} else if(count > 1) {
//			throw new RuntimeException("the child view must be 1");
		} 
		else {
//			header = getChildAt(0);
			View view = getChildAt(0);
//			System.out.println("view:" + view.toString()) ;
			if(view instanceof AbsListView) {
				mListView =(AbsListView)getChildAt(0);
				listparams = (RelativeLayout.LayoutParams)mListView.getLayoutParams();
			} else {
				throw new RuntimeException("the child view must be AbsListView");
			}				
		}
//		header.setVisibility(View.GONE);
		LayoutInflater inflater = LayoutInflater.from(mListView.getContext());
		View headerView = inflater.inflate(R.layout.feed_header, null);
		mHeader = new DefaultHeaderView(headerView);
//		TextView textView = (TextView)headerView.findViewById(R.id.head_tipsTextView);
//		textView.setText(R.string.common_listview_searchhint_pull);
//		header.setVisibility(View.GONE);
		View footerView = inflater.inflate(R.layout.feed_footer, null);	
		mFooter = new DefaultFooterView(footerView);
		addView(headerView, 0);
		addView(footerView);
		headerparams = (RelativeLayout.LayoutParams)headerView.getLayoutParams();
		footerparams = (RelativeLayout.LayoutParams)footerView.getLayoutParams();
		
		topViewInit();
		updateViewWhenNewData(true);
		this.requestLayout();
		footerparams.bottomMargin = -footerHeight;
		init();
	}
	private void init() {
		mListView.setOnTouchListener(this);
		mListView.setCacheColorHint(Color.argb(0, 0, 0, 0));
		mListView.setVerticalScrollBarEnabled(false);
		threshold = (int) (50* getContext().getResources().getDisplayMetrics().density);
		listenThreshold = (int) (60*getContext().getResources().getDisplayMetrics().density);

		if(mListView.isFastScrollEnabled())
		{
			fastScrollValid = true;
		}
		else
		{
			fastScrollValid = false;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent ev) {
		if(ev.getAction()==MotionEvent.ACTION_DOWN)
		{
			isBouncing = false;
			readyBouncing = false;
			return false;
		}else if(ev.getAction()==MotionEvent.ACTION_MOVE)
		{
			if(!isBouncing && !readyBouncing)
			{
				if(			mListView.getCount()==0 || 
						    mListView.getChildAt(0)==null || 
						(	mListView.getFirstVisiblePosition()==0 && 
								mListView.getChildAt(0).getTop()==0)
						)
				{//到头
					bouncingTop = true;
					readyBouncing = true;
					isBouncing = false;
					yPosStart = ev.getY();
					lastYPos = ev.getY();
					bounced = 0;
					return false;
				}else if(		mListView.getCount()==0 || 
						        mListView.getChildAt(mListView.getChildCount()-1)==null || 
							(	mListView.getLastVisiblePosition()==mListView.getCount()-1 && 
									mListView.getChildAt(mListView.getChildCount()-1).getBottom()==mListView.getHeight())
							)
				{//到尾
					bouncingTop = false;
					readyBouncing = true;
					isBouncing = false;
					yPosStart = ev.getY();
					lastYPos = ev.getY();
					bounced = 0;
					return false;
				}else
				{
					return false;
				}
			}else if(readyBouncing)
			{//开始拉动
//				System.out.println("readyBouncing:" + readyBouncing);
				float ypos = ev.getY();
				if(bouncingTop)
				{//头
					if(ypos<lastYPos)
					{
						readyBouncing = false;
						return false;
					}else if(ypos-lastYPos>threshold)
					{
						readyBouncing = false;
						isBouncing = true;
						bouncedHigh = false;
						yPosStart = ypos;
						return false;
					}else
					{
						return false;
					}
				}else
				{//尾部
					if(ypos>lastYPos)
					{
						readyBouncing = false;
						return false;
					}else if(lastYPos-ypos>threshold)
					{
						readyBouncing = false;
						isBouncing = true;
						bouncedHigh = false;
						yPosStart = ypos;
						return false;
					}else
					{
						return false;
					}
				}
			}else if(isBouncing)
			{//拉动中
				float ypos = ev.getY();
				bounced = (int) (ypos-yPosStart);
//				System.out.println("bouncingTop:" + bouncingTop);
				if(bouncingTop)
				{//头
					
					if(bounced > listenThreshold)
					{
							bouncedHigh = true;
					}else
					{
							bouncedHigh = false;
					}
					if(ypos<=yPosStart)
					{
						this.scrollTo(0, 0);
						isBouncing = false;
						return false;
					}else
					{
//						header.setVisibility(View.VISIBLE);
						this.scrollTo(0, -bounced*2/5);
//						System.out.println("yPosStart - ypos:" + (yPosStart - ypos));
//						System.out.println("headerHeight:" + headerHeight);
						if(ypos - yPosStart> dragHeight) {
//							System.out.println("################3layout");
							if(searchstatus != SEARCHSTATUS_PREPARED_REFRESH) {
								preparedRefresh();
							}
						}
						return true;
					}				
				}else
				{//尾
					if(bounced < -listenThreshold)
					{
						bouncedHigh = true;
					}else
					{
						bouncedHigh = false;
					}
					if(ypos>=yPosStart)
					{
						this.scrollTo(0, 0);
						isBouncing = false;
						return false;
					}else
					{
//						footer.setVisibility(View.VISIBLE);
						this.scrollTo(0, -bounced*2/5);
						if(yPosStart - ypos  > dragHeight) {
							if(searchstatus != SEARCHSTATUS_PREPARED_UPDATEMORE) {
								preparedSearchMore();
							}
					    }
						
						return true;
					}				
				}
			}
		}
		else if(ev.getAction()==MotionEvent.ACTION_UP)
		{
			if(isBouncing)
			{
				if(searchstatus == SEARCHSTATUS_PREPARED_REFRESH) {
					checkSearch();
				}
				this.scrollTo(0, 0);
//				headerparams.topMargin = headerHeight;
//				footer.setVisibility(View.GONE);
				
			}
			isBouncing = false;
			bounced = 0;
			bouncedHigh = false;
			yPosStart = 0;
			lastYPos = 0;
			readyBouncing = false;
		}
		else if (ev.getAction() == MotionEvent.ACTION_CANCEL) {
			if (getScrollY() < 0) {
				resetPosition();
			}
		}
		
		return false;
	}
	
	private void resetPosition() {
		if(isBouncing)
		{
			this.scrollTo(0, 0);
		}
		isBouncing = false;
		bounced = 0;
		bouncedHigh = false;
		yPosStart = 0;
		lastYPos = 0;
		readyBouncing = false;
	}
	
//	public void setOnBounceListener(OnBounceListener onBounceListener){
//		this.bounceListener = onBounceListener;
//	}
//	public void setOnBounceHighListener(OnBounceHighListener onBounceHighListener){
//		this.bounceHighListener = onBounceHighListener;
//	}
//	private OnBounceListener bounceListener = null;
//	@SuppressWarnings("unused")
//	private OnBounceHighListener bounceHighListener = null;
//	public interface OnBounceListener {
//		/**
//		 * @param view 
//		 * 			the view bounce happened in
//		 * @param top_btm
//		 * 			indicate bounce happend at top or bottom , 0 for top ,1 for bottom
//		 */
//		public void onBounce(AdapterView<?> view, int top_btm);
//	}
	
	public interface OnScrollEndListener {
		void onScrollHeader();

		void onScrollEnd();
	}

	private void topViewInit() {
		View headerView = mHeader.getView();
		arrowImageView = (ImageView) headerView
				.findViewById(R.id.head_arrowImageView);
		// arrowImageView.setMinimumWidth(70);
		// arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) headerView.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headerView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headerView
				.findViewById(R.id.head_lastUpdatedTextView);

		rotateAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setInterpolator(new LinearInterpolator());
		rotateAnimation.setDuration(250);
		rotateAnimation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(1);
		reverseAnimation.setFillBefore(true);
	}

//	private void refresh() {
//		arrowImageView.setVisibility(View.VISIBLE);
//		progressBar.setVisibility(View.GONE);
//		tipsTextview.setVisibility(View.VISIBLE);
//		lastUpdatedTextView.setVisibility(View.VISIBLE);
////		lastUpdatedTextView.setText(updateTime);
//		arrowImageView.clearAnimation();
//		arrowImageView.startAnimation(rotateAnimation);
//		if(mOnScrollEndListener != null) {
//			mOnScrollEndListener.onScrollHeader();
//		}
//	}
	
	public void refresh(){
		preparedRefresh();
		checkSearch();
	}

	private void checkSearch() {		
		if(searchstatus == SEARCHSTATUS_PREPARED_REFRESH) {
			if(mOnScrollEndListener != null) {
				headerparams.topMargin = 0;
				showLoading();
				requestLayout();
				mOnScrollEndListener.onScrollHeader();
			}
		} else if(searchstatus == SEARCHSTATUS_PREPARED_UPDATEMORE) {
			if(mOnScrollEndListener != null) {
				footerparams.bottomMargin = 0;
				listparams.addRule(RelativeLayout.ABOVE, mFooter.getView().getId());
				footerparams.getRules()[RelativeLayout.BELOW] = 0;
				footerparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				requestLayout();
				mFooter.onRefreshHint();
				mOnScrollEndListener.onScrollEnd();
			}
		}
		
	}
	private void preparedRefresh() {
		searchstatus = SEARCHSTATUS_PREPARED_REFRESH;
//		TextView textView = (TextView)mHeader.getView().findViewById(R.id.head_tipsTextView);
//		textView.setText(R.string.common_listview_searchhint_push);
//		arrowImageView.setVisibility(View.VISIBLE);
//		progressBar.setVisibility(View.GONE);
//		tipsTextview.setVisibility(View.VISIBLE);
//		lastUpdatedTextView.setVisibility(View.VISIBLE);
////		lastUpdatedTextView.setText(updateTime);
//		arrowImageView.clearAnimation();
//		arrowImageView.startAnimation(rotateAnimation);
		mHeader.onRelaseHint();
	}
	
	private void showLoading() {
//		TextView textView = (TextView)mHeader.getView().findViewById(R.id.head_tipsTextView);
//		textView.setText(R.string.common_listview_loading);
//		progressBar.setVisibility(View.VISIBLE);
//		arrowImageView.setVisibility(View.GONE);
		mHeader.onRefreshHint();
	}
	private void preparedSearchMore() {
		searchstatus = SEARCHSTATUS_PREPARED_UPDATEMORE;
		checkSearch();
	}
	
	public void updateViewWhenNewData(boolean isInit) {
		if(searchstatus == SEARCHSTATUS_PREPARED_REFRESH || isInit) {
			searchstatus = SEARCHSTATUS_DEFAULT;
			if(mHeader != null) {
				mHeader.onPullHint(true);
			}
//			TextView textView = (TextView)mHeader.getView().findViewById(R.id.head_tipsTextView);
//			textView.setText(R.string.common_listview_searchhint_pull);
//			mHeader.getView().clearAnimation();
//			mHeader.getView().startAnimation(reverseAnimation);	
			headerparams.topMargin = -headerHeight;
			listparams.addRule(RelativeLayout.BELOW, mHeader.getView().getId());
			headerparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//			progressBar.setVisibility(View.GONE);
//			arrowImageView.setVisibility(View.VISIBLE);
		} 
		
		if(searchstatus == SEARCHSTATUS_PREPARED_UPDATEMORE ||isInit) {
			if(mFooter != null) {
				mFooter.onPullHint();
			}
			searchstatus = SEARCHSTATUS_DEFAULT;
			listparams.getRules()[RelativeLayout.ABOVE] = 0;
			footerparams.getRules()[RelativeLayout.ALIGN_PARENT_BOTTOM] = 0;
			footerparams.addRule(RelativeLayout.BELOW, mListView.getId());
			footerparams.bottomMargin = -footerHeight;
			footerparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		}
		
		resetPosition();
		if(mFlingAdapter != null) {
			mFlingAdapter.notifyDataSetChanged();
		}
	}
	
	public void setAdapter(FlingAdapter<T> mBaseAdapter) {
		mFlingAdapter = mBaseAdapter;
		initView();
		mListView.setAdapter(mFlingAdapter);
	}
	
	public void setData(List<T> data) {
		mFlingAdapter.setData(data);
		updateViewWhenNewData(true);
		mFlingAdapter.notifyDataSetChanged();		
//		mHandler.post(new Runnable(){
//			@Override
//			public void run() {
////				System.out.println(" isHasNextPage():" + isHasNextPage());
//				if(mFooter != null) {
//					footerparams.bottomMargin = -footerHeight;
//					requestLayout();
//				}
//			}
//		});
	}
	
	public void closeHeaderFooter() {
		updateViewWhenNewData(true);
	}
	
	public List<T> getData() {
		return mFlingAdapter.getDataList();
	}
	
	public void setOnScrollEndListener(OnScrollEndListener onScrollEndListener) {
		mOnScrollEndListener = onScrollEndListener;
	}
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener ) {
		mListView.setOnItemClickListener(onItemClickListener);
	}
	
	private boolean isHasNextPage() {
		int dataCount = mListView.getCount();
		if(dataCount==0) {
			return false;
		}
//		System.out.println("@@@@@@@@######################################3");
//		System.out.println(" mListView.getLastVisiblePosition():" + mListView.getLastVisiblePosition());
//		System.out.println(" mListView.getCount():" + mListView.getCount());
//		System.out.println(" mListView.getChildAt(mListView.getChildCount()-1).getBottom():" + mListView.getChildAt(mListView.getChildCount()-1).getBottom());
//		System.out.println(" mListView.getHeight():" + mListView.getHeight());
		if(
			 mListView.getLastVisiblePosition()<mListView.getCount()-1 ||
		    (mListView.getLastVisiblePosition()==mListView.getCount()-1 && 
		    mListView.getChildAt(mListView.getChildCount()-1).getBottom()>=mListView.getHeight())) {
			return true;
		} else {
			return false;
		}
	}
}
