package com.youa.mobile.common.base;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.youa.mobile.R;
import com.youa.mobile.information.PersonnalInforPage;
import com.youa.mobile.ui.base.BaseHolder;
import com.youa.mobile.utils.Tools;

//feed和评论及所有的listview页面
public abstract class BaseListView<H extends BaseHolder, L extends List<?>> {
	public static final String TAG = "BaseListView";
	final private boolean isDebug = true;

	public interface OnScrollEndListener {
		void onScrollHeader();

		void onScrollEnd();
	}

	public interface OnListItemListener {
		void onUpEvent(View v);
	}

	// adapter
	protected L mDataList;
	protected ListView mListView;
	private IHeaderView mHeader;
	private IFooterView mFooter;
	private ListViewAdapter mListAdapter;
	private BaseOnScroolerListener mOnScroolerListener;
	private int mVersion;
	private static long id;
	private boolean mIsMoveing;
	private OnScrollEndListener mOnScrollEndListener;
	// private IHeaderView mHeaderView;
	protected OnListItemListener mOnListItemListener;
	private int screenHeight;
	final private int BOTTOM = 49;// DIP
	final private int TITLE = 44;// DIP
	private Handler mHandler = new Handler();

	public BaseListView(ListView listView, View header, View footer) {
		this.mListView = listView;
		if (header != null) {
			this.mHeader = new DefaultHeaderView(header);
		} else {
			this.mHeader = null;
		}
		if (footer != null) {
			footer.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					state = REFRESHING;
					mFooter.onRefreshHint();
					mOnScrollEndListener.onScrollEnd();
					mListView.scrollTo(0, 0);
				}});
			mFooter = new DefaultFooterView(footer);
		} else {
			mFooter = null;
		}

		if (header != null) {
			this.mListView.addHeaderView(header);
		}
		if (footer != null) {
			this.mListView.addFooterView(footer);
		}
		resetTopOrEnd();
		mListAdapter = new ListViewAdapter();
		mOnScroolerListener = new BaseOnScroolerListener();
		this.mListView.setAdapter(mListAdapter);
		if (footer != null) {
			footer.setVisibility(View.GONE);
		}
		this.mListView.setOnScrollListener(mOnScroolerListener);
		mListView.setOnTouchListener(new ListViewOnTouchEvent());
		screenHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, BOTTOM + TITLE, mListView
						.getContext().getResources().getDisplayMetrics());
		if (listView == null) {
			return;
		}
		Context context = listView.getContext();
		mToast = new Toast(context);
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToastText = new TextView(context);
		mToastText.setGravity(Gravity.CENTER);
		mToastText.setTextSize(16);
		mToastText.setTextColor(Color.WHITE);
		// mToastText.setWidth(ApplicationManager.getInstance().getWidth());
		// mToastText.setPadding(5, 5, 5, 5);
		mToastText.setBackgroundResource(R.drawable.refresh_bg);
		mToastText.setShadowLayer((float)0.1, 1, 1, Color.argb(38, 0, 0, 255));
		mToast.setView(mToastText);
		mToast.setGravity(Gravity.TOP, 0, Tools.dip2px(context, 44));
	}

	protected Context getContext() {
		return mListView.getContext();
	}
	
	public void addData(List data, int pagesize) {
		int size = 0;
		if(data != null) {
			mDataList.addAll(data);
			size = data.size();
		}
		mListAdapter.notifyDataSetChanged();
		
		if(data == null || data.size() == 0) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (mFooter != null) {
						mFooter.getView().setVisibility(View.GONE);
					}
				}
			});
		} 
	}
	public void setData(L data, final int pagesize, final boolean isNullLast) {
		// mHeader.setVisibility(View.GONE);
		// System.out.println("action-setData:" + System.currentTimeMillis());
		mDataList = data;
		if(data == null) {
			if(mDataList == null) {
				mDataList = (L)new ArrayList();
			} else {
				mDataList.clear();
			}
		} 
		
		final int size = mDataList.size();
//		if (isNewVersion) {
//			mVersion++;
//		}
		mListAdapter.notifyDataSetChanged();
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				// System.out.println("showok:" + System.currentTimeMillis());
				if (mFooter != null) {
					if((isNullLast && size == 0)
							||(!isNullLast && size < pagesize)
							|| isLock) {
						mFooter.getView().setVisibility(View.GONE);
					} else {
						mFooter.getView().setVisibility(View.VISIBLE);
					}
				}
			}
		});

	}
	
	public void setData(L data, final int pagesize) {
		setData(data, pagesize, false);
	}

	public IHeaderView getHeader() {
		return mHeader;
	}

	public int getVersion() {
		return mVersion;
	}

	public L getData() {
		return mDataList;
	}

	public void addOrRemoveHeaderOrFooter(View view, int height) {
		if (view == null) {
			return;
		}
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, height));
	}

//	public H findHolderById(int version, int position) {
//		if (this.mVersion != version) {
//			return null;
//		}
//		return mListAdapter.getHolderByPosition(position);
//	}

	private boolean isLock;

	public void setLockEnd(boolean isLock) {
		this.isLock = isLock;
		if (mFooter != null) {
			if (isLock) {
				mFooter.getView().setVisibility(View.GONE);
			} else {
				mFooter.getView().setVisibility(View.VISIBLE);
			}
		}
	}

	public void destroy(boolean isDestroy) {
		if (isDestroy) {
			mHeader = null;
			mFooter = null;
			mListAdapter = null;
			mOnScroolerListener = null;
			mListView = null;
		}
		mDataList.clear();
		mIsMoveing = false;
		// version = 0;
		// id = 0;
	}

	protected abstract View createTemplateView(int pos);
	protected abstract H getHolder(View convertView,int pos);

	protected abstract void setDataWithHolder(H holder, int position,
			boolean isMoving);

	// protected abstract void treateHeaderEvent();
	//
	// protected abstract void treateEndEvent();

	protected void treateStopEvent(H holder, int position) {
		//无用函数
	}

//	private void treateItemStopEvent(int form, int end) {
//		H holder = null;
//		for (int i = form; i <= end; i++) {
//			holder = mListAdapter.getHolderByPosition(i);
//			if (holder == null) {
//				continue;
//			}
//			if (holder.getNeedTreateScroolStopEvent()) {
//				treateStopEvent(holder, i);
//			}
//		}
//	}

	public class ListViewAdapter extends BaseAdapter {
//		private static final int HOLDER_KEY = 100000001;
		private long currentId;

		public ListViewAdapter() {
			synchronized (this) {
				id++;
				currentId = id;
			}
		}

		@Override
		public int getCount() {
			if (mDataList == null) {
				return 0;
			}
			return mDataList.size();
		}

		@Override
		public Object getItem(int position) {
			if (mDataList == null) {
				return null;
			}
			return mDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			H holder = null;
			if (convertView == null) {
				convertView = createTemplateView(position);
				holder = getHolder(convertView, position);
				convertView.setTag(holder);
			} else {
				holder = (H) convertView.getTag();
			}
			setDataWithHolder(holder, position, BaseListView.this.mIsMoveing);
			return convertView;
		}
	}

	// private boolean isToHeader;
	 private boolean isToEnd;

	private class BaseOnScroolerListener implements ListView.OnScrollListener {
		private int firstIndex;
		private int endIndex;
		private int oldEndIndex;

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			int size = mListAdapter.getCount();
			if (size == 0) {
				return;
			}
			if(isToEnd&&oldEndIndex!= endIndex&&mFooter!=null&&mFooter.getView().getVisibility()==View.VISIBLE){
				oldEndIndex= endIndex;
				isToEnd= false;
				state = REFRESHING;
				mFooter.onRefreshHint();
				mOnScrollEndListener.onScrollEnd();
				mListView.scrollTo(0, 0);
			}
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				firstIndex = mHeader != null ? firstIndex - 1 : firstIndex;
				endIndex = endIndex >= size - 1 ? size - 1 : endIndex;
				mIsMoveing = false;	
//				treateItemStopEvent(firstIndex, endIndex);
				break;
			case OnScrollListener.SCROLL_STATE_FLING:
				// if (isToHeader) {
				// // treateHeaderEvent();
				// // mHeader.setVisibility(View.VISIBLE);
				// // if (mOnScrollEndListener != null) {
				// // mOnScrollEndListener.onScrollHeader();
				// // }
				// } else if (isToEnd) {
				// // treateEndEvent();
				// if (mOnScrollEndListener != null) {
				// mOnScrollEndListener.onScrollEnd();
				// }
				// }
			
				mIsMoveing = true;
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				mIsMoveing = true;
				break;
			}
		}

		// 1.显示区内最后一个bottom大于屏幕高度，
		// 2.显示区内最后一个bottom等于屏幕高度，
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			firstIndex = firstVisibleItem;
			endIndex = firstVisibleItem + visibleItemCount - 1;
			int startIndex = mHeader != null ? 1 : 0;
			// System.out.println("child count:" + mListView.getChildCount());
			// View v = null;
			// v = mListView.getChildAt(mListView.getChildCount() - 1);
			// int bottom = 0;
			// if (v != null) {
			// bottom = v.getBottom();
			// System.out.println("last child:" + bottom);
			// }
			//
			// int height = ApplicationManager.getInstance().getHeight()
			// - screenHeight;
			// System.out.println(mListView.getBottom() + "////" + height);
//			System.out.println("scroll:"+endIndex+"//"+totalItemCount);
			if (firstIndex <= startIndex) {
				// isToHeader = true;
			} else if (endIndex + 1 >= totalItemCount) {// && bottom >=
				isToEnd=true;										// screenHeight
														// isToEnd = true;
			} else {
				// isToHeader = false;
				 isToEnd = false;
			}
			// System.out.println("onScroll-isToEnd:" + isToEnd);
			// System.out.println("onScroll-isToHeader:" + isToHeader);
		}
	}

	public void setOnScrollEndListener(OnScrollEndListener onScrollEndListener) {
		mOnScrollEndListener = onScrollEndListener;
	}

	public void setOnListItemListener(OnListItemListener listItemListener) {
		mOnListItemListener = listItemListener;
	}

	// -------------------------------------------------------------------
	final static int DONE = 0;
	final static int PULL_To_REFRESH = 1;
	final static int RELEASE_To_REFRESH = 2;
	final static int REFRESHING = 3;
	private int state;
	private boolean isRecored;
	protected int headContentHeight;
	protected int footerContentHeight;
	private int startY;
	public boolean isBack;
	private final static int RATIO = 3;

	public int getState() {
		return state;
	}

	public int getHeaderContent() {
		return headContentHeight;
	}

	public void closeHeaderFooter() {
		state = DONE;
		if (mHeader != null) {
			mHeader.getView().setPadding(0, -headContentHeight, 0, 0);
			mHeader.onPullHint(true);
		}
		if (mFooter != null) {
			mFooter.onPullHint();
		}
	}

	public void hiddenFooter() {
		if (mFooter != null) {
			mFooter.getView().setVisibility(View.GONE);
		}
	}

	private void resetTopOrEnd() {
		if (mHeader != null) {
			View headerView = mHeader.getView();
			measureView(headerView);
			headContentHeight = headerView.getMeasuredHeight();

			headerView.setPadding(0, -1 * headContentHeight, 0, 0);
			headerView.invalidate();
		}
		// ---------------
		if (mFooter != null) {
			View footerView = mFooter.getView();
			measureView(footerView);
		}
		state = DONE;
	}

	public void refresh() {
		state = REFRESHING;
		mHeader.onRefreshHint();
		mHeader.getView().setPadding(0, 0, 0, 0);
		mOnScrollEndListener.onScrollHeader();
	}

	private Toast mToast;
	private TextView mToastText;

	public void refreshFinish(String refresh) {
		mToastText.setText(refresh);
		mToast.show();
	}

	public void measureView(View child) {
		if (child == null) {
			return;
		}
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	private class ListViewOnTouchEvent implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			boolean result;
//			 System.out.println(">>>>>>>>>>>>>onTouch:" + state);
//			 System.out.println(">>>>>>>>>>>>>action:" + event.getAction());

			result = headTreat(event);
//			System.out.println("state:" + state);
//			System.out.println("result:" + result);
			// System.out.println(">>>>>>>>>>>>>state:" + state);
			// System.out.println(">>>>>>>>>>>>>result:" + result);
			
//			if (!result) { 
//				result = footerTreat(event);
//			}
			
//			System.out.println("state:" + state);
//			System.out.println("result:" + result);
			// System.out.println(">>>>>>>>>>>>>state:" + state);
			// System.out.println(">>>>>>>>>>>>>result:" + result);
			if (!result) {
				try {
					result = mListView.onTouchEvent(event);
				} catch(Exception e) {
					e.printStackTrace();
					//TODO 个别手机会有问题
				}
			}
//			System.out.println("state:" + state);
//			System.out.println("result:" + result);
			// System.out.println(">>>>>>>>>>>>>state:" + state);
			// System.out.println(">>>>>>>>>>>>>result:" + result);
			// MotionEvent.ACTION_MOVE
			return result;
			// return false;
		}
	}

	// private boolean isLoading;

	private boolean footerTreat(MotionEvent event) {
		boolean isToEnd = isEnd();
		if (isLock || !isToEnd || mFooter == null 
				|| mFooter.getView().getVisibility() != View.VISIBLE) {
			return false;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isRecored = false;
			startY = (int) event.getY();
			break;
		case MotionEvent.ACTION_UP:
			boolean isreturntrue = false;
			if (isRecored) {
				if(state != REFRESHING) {
					state = DONE;
				}
				mListView.scrollTo(0, 0);
				isreturntrue = true;
			}
			isRecored = false;
			isBack = false;
			isToEnd = false;
			return isreturntrue;
		case MotionEvent.ACTION_MOVE:
			int tempY = (int) event.getY();
			
			if (!isRecored && isToEnd && tempY < startY - 10
					) {
//				event.setAction(MotionEvent.ACTION_CANCEL);
				if(state == REFRESHING) {
					return false;
				}
				isRecored = true;
				startY = tempY;
				
				return false;
			}
			if (!isRecored) {
				return false;
			}
			if (state == PULL_To_REFRESH) {
				// 上拉到可以进入RELEASE_TO_REFRESH的状态
				if ((startY - tempY) / RATIO >= headContentHeight) {
					state = REFRESHING;
					mFooter.onRefreshHint();
					mOnScrollEndListener.onScrollEnd();
					mListView.scrollTo(0, 0);
					startY = tempY;
					return true;
				}
			}
			if (state == DONE) {
				if (startY - tempY > 0) {
					state = PULL_To_REFRESH;
				}
			}
			if (state == PULL_To_REFRESH || state == REFRESHING) {// 到下拉刷新状态
				mListView.scrollTo(0, (startY - tempY) * 2 / 5);
				return true;
			}
			// break;
		}
		return false;
	}

	private boolean headTreat(MotionEvent event) {
		boolean isToHeader = isHeader();
		// System.out.println("headTreat-isLock:" + isLock);
		// System.out.println("headTreat-isToHeader:" + isToHeader);
		// System.out.println("headTreat-mHeader:" + mHeader);
		if (isLock || !isToHeader || mHeader == null) {
			return false;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isRecored = false;
			if (isToHeader) {
				startY = (int) event.getY();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (state == RELEASE_To_REFRESH) {// 刷新
				state = REFRESHING;
				mHeader.onRefreshHint();
				mHeader.getView().setPadding(0, 0, 0, 0);
				mOnScrollEndListener.onScrollHeader();
				return true;
			} else if (state == PULL_To_REFRESH) {
				state = DONE;
				mHeader.getView().setPadding(0, -headContentHeight, 0, 0);
				isRecored = false;
				isBack = false;
				// isLoading = false;
				isToHeader = false;
				return true;
			}

		case MotionEvent.ACTION_MOVE:			
			int tempY = (int) event.getY();

			if (!isRecored && isToHeader && (tempY > startY + 10)) {// 在move时候记录下位置
				// System.out.println("headTreat-isRecored:" + isRecored);
				// System.out.println("headTreat-startY:" + startY);
				// System.out.println("headTreat-tempY:" + tempY);
				// System.out.println("headTreat-state:" + state);
				if(state == REFRESHING) {
					return true;
				}
				isRecored = true;
				startY = tempY;
//				event.setAction(MotionEvent.ACTION_CANCEL);
				return false;
			}

			if (!isRecored) {
				return false;
			}
			// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
			// 可以松手去刷新了
			if (state == RELEASE_To_REFRESH) {
				// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
				if (((tempY - startY) / RATIO < headContentHeight)) {// 由松开刷新状态转变到下拉刷新状态
					state = PULL_To_REFRESH;
					mHeader.onPullHint(true);
					return true;
				}
			}
			// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
			if (state == PULL_To_REFRESH) {
				mHeader.onRefreshLastTime();
				if ((tempY - startY) / RATIO >= headContentHeight) {// 由done或者下拉刷新状态转变到松开刷新
					state = RELEASE_To_REFRESH;
					mHeader.onRelaseHint();
					return true;
				}
			}
			if (state == DONE) {
				if (tempY - startY > 0) {
					state = PULL_To_REFRESH;
				}
			}
			// 更新headView的size
			if (state == PULL_To_REFRESH) {// 到下拉刷新状态
				mHeader.getView()
						.setPadding(
								0,
								-1 * headContentHeight + (tempY - startY)
										/ RATIO, 0, 0);
				return true;

			}
			// 更新headView的paddingTop
			if (state == RELEASE_To_REFRESH) {// 到松开刷新状态
				mHeader.getView().setPadding(0,
						(tempY - startY) / RATIO - headContentHeight, 0, 0);
				return true;
			}

			break;
		}
		return false;
	}

	private boolean isEnd() {
		if (mListView.getCount() == 0
				|| mListView.getChildAt(mListView.getChildCount() - 1) == null
				|| mListView.getLastVisiblePosition() == mListView.getCount() - 1
				&& mListView.getChildAt(mListView.getChildCount() - 1)
						.getBottom() == mListView.getHeight()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isHeader() {
		if (mListView.getCount() == 0
				|| mListView.getChildAt(0) == null
				|| (mListView.getFirstVisiblePosition() == 0 && mListView
						.getChildAt(0).getTop() == 0)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isHasNextPage() {
		int dataCount = mListView.getCount();
		if (dataCount == 0) {
			Log.d(TAG, "isHasNextPage:" + false);
			return false;
		}
		if (mListView.getChildCount() < mListView.getCount() - 1
				|| (mListView.getChildCount() == mListView.getCount() - 1 && mListView
						.getChildAt(mListView.getChildCount() - 1).getBottom() > mListView
						.getHeight())) {
			Log.d(TAG, "isHasNextPage:" + true);
			return true;
		} else {
			Log.d(TAG, "isHasNextPage:" + false);
			return false;
		}
	}

	public ListViewAdapter getAdapter() {
		return this.mListAdapter;
	}
	
	public void startUserInfoActivity(String uid, String uname){
		Intent intent = new Intent(getContext(), PersonnalInforPage.class);
		intent.putExtra(PersonnalInforPage.KEY_USER_ID, uid);
		intent.putExtra(PersonnalInforPage.KEY_USER_NAME, uname);
		getContext().startActivity(intent);
	}
}
