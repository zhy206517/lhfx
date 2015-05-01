package com.youa.mobile.common.base;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
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
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.information.PersonnalInforPage;
import com.youa.mobile.ui.base.BaseHolder;
import com.youa.mobile.utils.Tools;

//feed和评论及所有的listview页面
public abstract class AbstractListView<H extends BaseHolder, L extends List<HomeData>> {
	protected static final String TAG = AbstractListView.class.getSimpleName();

	public interface OnScrollEndListener {
		void onScrollHeader();

		void onScrollEnd();
	}

	public interface OnListItemListener {
		void onUpEvent(View v);
	}

	// adapter
	protected List<HomeData> mDataList;
	protected ListView mListView;
	private IHeaderView mHeader;
	protected IFooterView mFooter;
	protected ListViewAdapter mListAdapter;
	private BaseOnScroolerListener mOnScroolerListener;
	private int mVersion;
	private static long id;
	private boolean mIsMoveing;
	private OnScrollEndListener mOnScrollEndListener;
	// private IHeaderView mHeaderView;
	protected OnListItemListener mOnListItemListener;
	protected Handler mHandler = new Handler();

	public AbstractListView(ListView listView, View header, View footer) {
		mDataList = new ArrayList<HomeData>();
		this.mListView = listView;
		if (header != null) {
			this.mHeader = new DefaultHeaderView(header);
		} else {
			this.mHeader = null;
		}
		if (footer != null) {
			footer.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					state = REFRESHING;
					mFooter.onRefreshHint();
					mOnScrollEndListener.onScrollEnd();
					mListView.scrollTo(0, 0);
				}
			});
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
		mToastText.setBackgroundResource(R.drawable.refresh_bg);
		mToastText.setShadowLayer((float) 0.1, 1, 1, Color.argb(38, 0, 0, 255));
		mToast.setView(mToastText);
		mToast.setGravity(Gravity.TOP, 0, Tools.dip2px(context, 44));
	}

	protected Context getContext() {
		return mListView.getContext();
	}

	public void addData(List<HomeData> datas, final int pagesize) {
		// LogUtil.d(TAG, "addData. pagesize = " + pagesize);
		int size = 0;
		ArrayList<HomeData> list = new ArrayList<HomeData>();
		if (datas != null && !datas.isEmpty()) {
			size = datas.size();
			for (HomeData data : datas) {
				if (data != null) {
					if (data.PublicUser != null && !"0".equals(data.PublicUser.feedType)) {// data.originUser != null
						if (data.originUser != null && data.originUser.contentImg != null && data.originUser.contentImg.length > 0) {
							list.add(data);
						}
					} else if (data.PublicUser != null && "0".equals(data.PublicUser.feedType)) {
						if (data.PublicUser.contentImg != null && data.PublicUser.contentImg.length > 0) {
							list.add(data);
						}
					}
				}
			}
		}
		mDataList.addAll(list);
		mListAdapter.notifyDataSetChanged();

		// if (datas == null || datas.size() == 0) {
		// mHandler.post(new Runnable() {
		// @Override
		// public void run() {
		// if (mFooter != null) {
		// mFooter.getView().setVisibility(View.GONE);
		// }
		// }
		// });
		// }
		// final int originSize = size;
		// // LogUtil.d(TAG, "addData. originSize = " + originSize);
		// mHandler.post(new Runnable() {
		// @Override
		// public void run() {
		// if (mFooter != null) {
		// if (originSize < pagesize || isLock) {
		// mFooter.getView().setVisibility(View.GONE);
		// } else {
		// mFooter.getView().setVisibility(View.VISIBLE);
		// }
		// }
		// }
		// });
	}

	private ShowEmptyViewListener mHideListener;

	public void setShowListener(ShowEmptyViewListener listener) {
		mHideListener = listener;
	}

	public void setData(List<HomeData> datas, final int pagesize, final boolean isNullLast) {
		mDataList.clear();
		ArrayList<HomeData> list = new ArrayList<HomeData>();
		int size = 0;
		if (datas != null && !datas.isEmpty()) {
			size = datas.size();
			for (HomeData data : datas) {
				if (data != null) {
					if (data.PublicUser != null && !"0".equals(data.PublicUser.feedType)) {// data.originUser != null
						if (data.originUser != null && data.originUser.contentImg != null && data.originUser.contentImg.length > 0) {
							list.add(data);
						}
					} else if (data.PublicUser != null && "0".equals(data.PublicUser.feedType)) {
						if (data.PublicUser.contentImg != null && data.PublicUser.contentImg.length > 0) {
							list.add(data);
						}
					}
				}
			}
		}
		mDataList.addAll(list);

		final int originSize = size;
		// LogUtil.d(TAG, "setData. originSize = " + originSize);
		mListAdapter.notifyDataSetChanged();
		// mHandler.post(new Runnable() {
		// @Override
		// public void run() {
		// if (mFooter != null) {
		// if (originSize < pagesize || isLock) {
		// mFooter.getView().setVisibility(View.GONE);
		// } else {
		// mFooter.getView().setVisibility(View.VISIBLE);
		// }
		// }
		// }
		// });
		if (mHideListener != null && mDataList.isEmpty()) {
			mHideListener.onShowEmptyView();
		}
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

	public List<HomeData> getData() {
		return mDataList;
	}

	public void addOrRemoveHeaderOrFooter(View view, int height) {
		if (view == null) {
			return;
		}
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, height));
	}

	protected boolean isLock;

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
	}

	protected abstract View createTemplateView();

	protected abstract H getHolder(View convertView);

	protected abstract void setDataWithHolder(H holder, int position, boolean isMoving);

	public class ListViewAdapter extends BaseAdapter {
		// private static final int HOLDER_KEY = 100000001;
		private long currentId;

		public ListViewAdapter() {
			synchronized (this) {
				id++;
				currentId = id;
			}
		}

		@Override
		public int getCount() {
			if (mDataList == null || mDataList.isEmpty()) {
				return 0;
			} else {
				int size = mDataList.size() / 7;
				int count = mDataList.size() % 7 == 0 ? size : size + 1;
				return count;
			}
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
			// LogUtil.d(TAG, "getView. position = " + position);
			H holder = null;
			if (convertView == null) {
				convertView = createTemplateView();
				holder = getHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder=(H)convertView.getTag();
			}
			
			setDataWithHolder(holder, position, AbstractListView.this.mIsMoveing);
			return convertView;
		}
	}

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
			if (isToEnd && oldEndIndex != endIndex && mFooter != null && mFooter.getView().getVisibility() == View.VISIBLE) {
				oldEndIndex = endIndex;
				isToEnd = false;
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
				// treateItemStopEvent(firstIndex, endIndex);
				break;
			case OnScrollListener.SCROLL_STATE_FLING:
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
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			firstIndex = firstVisibleItem;
			endIndex = firstVisibleItem + visibleItemCount - 1;
			int startIndex = mHeader != null ? 1 : 0;
			if (firstIndex <= startIndex) {
			} else if (endIndex + 1 >= totalItemCount) {// && bottom >=
				isToEnd = true; // screenHeight
			} else {
				isToEnd = false;
			}
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
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	private class ListViewOnTouchEvent implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			boolean result;
			result = headTreat(event);
			if (!result) {
				try {
					result = mListView.onTouchEvent(event);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return result;
		}
	}

	private boolean headTreat(MotionEvent event) {
		boolean isToHeader = isHeader();
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
				isToHeader = false;
				return true;
			}

		case MotionEvent.ACTION_MOVE:
			int tempY = (int) event.getY();

			if (!isRecored && isToHeader && (tempY > startY + 10)) {// 在move时候记录下位置
				if (state == REFRESHING) {
					return true;
				}
				isRecored = true;
				startY = tempY;
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
				mHeader.getView().setPadding(0, -1 * headContentHeight + (tempY - startY) / RATIO, 0, 0);
				return true;

			}
			// 更新headView的paddingTop
			if (state == RELEASE_To_REFRESH) {// 到松开刷新状态
				mHeader.getView().setPadding(0, (tempY - startY) / RATIO - headContentHeight, 0, 0);
				return true;
			}

			break;
		}
		return false;
	}

	private boolean isHeader() {
		if (mListView.getCount() == 0 || mListView.getChildAt(0) == null
				|| (mListView.getFirstVisiblePosition() == 0 && mListView.getChildAt(0).getTop() == 0)) {
			return true;
		} else {
			return false;
		}
	}

	public ListViewAdapter getAdapter() {
		return this.mListAdapter;
	}

	public void startUserInfoActivity(String uid, String uname) {
		Intent intent = new Intent(getContext(), PersonnalInforPage.class);
		intent.putExtra(PersonnalInforPage.KEY_USER_ID, uid);
		intent.putExtra(PersonnalInforPage.KEY_USER_NAME, uname);
		getContext().startActivity(intent);
	}

	/**
	 * 说明：当原始数据通过setData方法传进之后进行无图记录的过滤，有可能全部记录都没有图片，此时应该显示“无记录提示”，这就是该接口的任务。
	 * @author kfperfect
	 *
	 */
	public interface ShowEmptyViewListener {
		public void onShowEmptyView();
	}
}
