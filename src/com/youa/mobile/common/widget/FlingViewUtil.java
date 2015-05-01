//package com.youa.mobile.common.widget;
//
//import android.graphics.Canvas;
//import android.graphics.drawable.Drawable;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.AbsListView;
//import android.widget.AdapterView;
//import android.widget.ListAdapter;
//import android.widget.SectionIndexer;
//
//import com.youa.mobile.R;
//
//public class FlingViewUtil {
//	public static int RESULT_TYPE_TRUE = 1;
//	public static int RESULT_TYPE_FALSE = 1;
//	boolean fastScrollValid = true;
//	private AbsListView contentView;
//	public interface OnMeasureCallback
//	{
//		void onMeasured();
//	}
//	private OnMeasureCallback onMeasured = null;
//	public void setOnMeasureListener(OnMeasureCallback callback)
//	{
//		this.onMeasured = callback;
//	}
//
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		contentView.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		if(onMeasured!=null)
//		{
//			onMeasured.onMeasured();
//		}
//	}
//	
//
//	public void setAdapter(ListAdapter adapter) {
//		contentView.setAdapter(adapter);
//		if(adapter instanceof SectionIndexer && contentView.isFastScrollEnabled())
//		{
//			fastScrollValid = true;
//		}
//		else
//		{
//			fastScrollValid = false;
//		}
//	}
//	
//	public FlingViewUtil(AbsListView view) {
//		contentView = view;
//	}
//	
////	public FlingViewUtil(View view) {
////		super(context);
////
////		this.setCacheColorHint(Color.argb(0, 0, 0, 0));
////		this.setVerticalScrollBarEnabled(false);
////		threshold = (int) (50*getResources().getDisplayMetrics().density);
////		listenThreshold = (int) (60*getResources().getDisplayMetrics().density);
////	}
////
////	public FlingListView(Context context, AttributeSet attrs) {
////		super(context, attrs);
////		this.setCacheColorHint(Color.argb(0, 0, 0, 0));
////		this.setVerticalScrollBarEnabled(false);
////		threshold = (int) (50*getResources().getDisplayMetrics().density);
////		listenThreshold = (int) (60*getResources().getDisplayMetrics().density);
////	}
////
////	public FlingListView(Context context, AttributeSet attrs, int defStyle) {
////		super(context, attrs, defStyle);
////		this.setCacheColorHint(Color.argb(0, 0, 0, 0));
////		this.setVerticalScrollBarEnabled(false);
////		threshold = (int) (50*getResources().getDisplayMetrics().density);
////		listenThreshold = (int) (60*getResources().getDisplayMetrics().density);
////	}
//	boolean isBouncing = false;
//	int bounced = 0;
//	boolean bouncedHigh = false;
//	float yPosStart = 0;
//	float lastYPos = 0;
//	boolean readyBouncing = false;
//	int threshold = 50;
//	int listenThreshold = 60;
//	boolean isClickingSlider = false;
//	boolean bouncingTop = true;
//
//	public boolean onTouchEvent(MotionEvent ev) {
//		if(ev.getAction()==MotionEvent.ACTION_DOWN)
//		{
//			if(fastScrollValid && ev.getX()>contentView.getWidth()*5/6)
//				isClickingSlider = true;
//			else
//				isClickingSlider = false;
//			isBouncing = false;
//			readyBouncing = false;
//			return contentView.onTouchEvent(ev);
//		}else if(ev.getAction()==MotionEvent.ACTION_MOVE)
//		{
//			if(isClickingSlider)
//			{
//				return contentView.onTouchEvent(ev);
//			}
//			if(!isBouncing && !readyBouncing)
//			{
//				if(			contentView.getCount()==0 || 
//						    contentView.getChildAt(0)==null || 
//						(	contentView.getFirstVisiblePosition()==0 && 
//								contentView.getChildAt(0).getTop()==0)
//						)
//				{
//					bouncingTop = true;
//					readyBouncing = true;
//					isBouncing = false;
//					yPosStart = ev.getY();
//					lastYPos = ev.getY();
//					bounced = 0;
//					contentView.onTouchEvent(ev);
//					return true;
//				}else if(		contentView.getCount()==0 || 
//						        contentView.getChildAt(contentView.getChildCount()-1)==null || 
//							(	contentView.getLastVisiblePosition()==contentView.getCount()-1 && 
//							    contentView.getChildAt(contentView.getChildCount()-1).getBottom()==contentView.getHeight())
//							)
//				{
//					bouncingTop = false;
//					readyBouncing = true;
//					isBouncing = false;
//					yPosStart = ev.getY();
//					lastYPos = ev.getY();
//					bounced = 0;
//					contentView.onTouchEvent(ev);
//					return true;
//				}else
//				{
//					contentView.onTouchEvent(ev);
//					return true;
//				}
//			}else if(readyBouncing)
//			{
//				float ypos = ev.getY();
//				if(bouncingTop)
//				{
//					if(ypos<lastYPos)
//					{
//						readyBouncing = false;
//						contentView.onTouchEvent(ev);
//						return true;
//					}else if(ypos-lastYPos>threshold)
//					{
//						readyBouncing = false;
//						isBouncing = true;
//						bouncedHigh = false;
//						yPosStart = ypos;
//						contentView.onTouchEvent(ev);
//						return true;
//					}else
//					{
//						contentView.onTouchEvent(ev);
//						return true;
//					}
//				}else
//				{
//					if(ypos>lastYPos)
//					{
//						readyBouncing = false;
//						contentView.onTouchEvent(ev);
//						return true;
//					}else if(lastYPos-ypos>threshold)
//					{
//						readyBouncing = false;
//						isBouncing = true;
//						bouncedHigh = false;
//						yPosStart = ypos;
//						contentView.onTouchEvent(ev);
//						return true;
//					}else
//					{
//						contentView.onTouchEvent(ev);
//						return true;
//					}
//				}
//			}else if(isBouncing)
//			{
//				float ypos = ev.getY();
//				bounced = (int) (ypos-yPosStart);
//				if(bouncingTop)
//				{
//					if(bounced > listenThreshold)
//					{
//							bouncedHigh = true;
//					}else
//					{
//							bouncedHigh = false;
//					}
//					if(ypos<=yPosStart)
//					{
//						contentView.scrollTo(0, 0);
//						isBouncing = false;
//						contentView.onTouchEvent(ev);
//						return true;
//					}else
//					{
//						contentView.scrollTo(0, -bounced*2/5);
//						return true;
//					}				
//				}else
//				{
//					if(bounced < -listenThreshold)
//					{
//	//					if(!bouncedHigh)
//	//					{
//							bouncedHigh = true;
//	//						if(bounceHighListener!=null)
//	//							bounceHighListener.onBounceHigh(this, 1);
//	//					}
//					}else
//					{
//	//					if(bouncedHigh)
//	//					{
//							bouncedHigh = false;
//	//					}
//					}
//					if(ypos>=yPosStart)
//					{
//						contentView.scrollTo(0, 0);
//						isBouncing = false;
//						contentView.onTouchEvent(ev);
//						return true;
//					}else
//					{
//						contentView.scrollTo(0, -bounced*2/5);
//						return true;
//					}				
//				}
//			}
//		}
//		else if(ev.getAction()==MotionEvent.ACTION_UP)
//		{
//			if(isBouncing)
//			{
//				contentView.scrollTo(0, 0);
//				if(bounceListener!=null && bouncedHigh)
//					bounceListener.onBounce(contentView, 0);
//			}
//			isBouncing = false;
//			bounced = 0;
//			bouncedHigh = false;
//			yPosStart = 0;
//			lastYPos = 0;
//			readyBouncing = false;
//		}
//		else if (ev.getAction() == MotionEvent.ACTION_CANCEL) {
//			if (contentView.getScrollY() < 0) {
//				if(isBouncing)
//				{
//					contentView.scrollTo(0, 0);
//					if(bounceListener!=null && bouncedHigh)
//						bounceListener.onBounce(contentView, 0);
//				}
//				isBouncing = false;
//				bounced = 0;
//				bouncedHigh = false;
//				yPosStart = 0;
//				lastYPos = 0;
//				readyBouncing = false;
//			}
//		}
//		return false;
//	}
//
//
//	protected void onDraw(Canvas canvas) {
//		if(contentView.getChildCount()>0 && bounced>0 && bouncingTop)
//		{
//			int rid = R.drawable.feed_head_bg;
//			Drawable d = contentView.getContext().getResources().getDrawable(rid);			
//			if(d!=null)
//			{
//				int width = d.getIntrinsicWidth();
//				int height = d.getIntrinsicHeight();
//				int listWidth = contentView.getWidth();
////				DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
//				d.setBounds(listWidth/2-width/2, 0-height, listWidth/2+width/2, 0);
//				d.draw(canvas);
//			}
//			//canvas.drawLine(0, childTop-textHeight/2, getWidth(), childTop-textHeight/2, linePaint);
//		}
//	}
//	
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
//	/**
//	 * @author wsl
//	 *	deal with when bounce is high enough to trigger an event
//	 *	usually to notify user
//	 */
//	public interface OnBounceHighListener {
//		/**
//		 * @param view 
//		 * 			the view bounce happened in
//		 * @param top_btm
//		 * 			indicate bounce happend at top or bottom , 0 for top ,1 for bottom
//		 */
//		public void onBounceHigh(AdapterView<?> view, int top_btm);
//	}
//	}
