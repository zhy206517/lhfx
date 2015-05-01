package com.youa.mobile.ui.base;

import java.util.List;

import com.youa.mobile.ui.first_content.FirstContentActivity;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

//feed和评论及所有的listview页面
public abstract class BaseListView<H extends BaseHolder, L extends List<?>> {
	// adapter
	protected L list;
	private ListView listView;
	private View header;
	private View footer;
	private ListViewAdapter listAdapter;
	private BaseOnScroolerListener onScroolerListener;
	private int version;
	private static long id;
	private boolean isMoveing;

	public BaseListView(ListView listView, View header, View footer) {
		this.listView = listView;
		this.header = header;
		this.footer = footer;
		if (header != null) {
			this.listView.addHeaderView(header);
		}
		if (footer != null) {
			this.listView.addFooterView(footer);
		}
		listAdapter = new ListViewAdapter();
		onScroolerListener = new BaseOnScroolerListener();
		this.listView.setAdapter(listAdapter);
		this.listView.setOnScrollListener(onScroolerListener);
	}

	public void setData(L data, boolean isNewVersion) {
		list = data;
		if (isNewVersion) {
			version++;
		}
		listAdapter.notifyDataSetChanged();
	}

	public int getVersion() {
		return version;
	}

	public L getData() {
		return list;
	}

	public void addOrRemoveHeaderOrFooter(View view, int height) {
		if (view == null) {
			return;
		}
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, height));
	}

	public H findHolderById(int version, int position) {
		if (this.version != version) {
			return null;
		}
		return listAdapter.getHolderByPosition(position);
	}

	public void addFooter(View footer) {
		if (footer == null || listView == null) {
			return;
		}
		if (listView.getFooterViewsCount() > 0) {
			try {
				listView.removeFooterView(footer);
			} catch (Exception e) {
			}
		}
		footer.setVisibility(View.VISIBLE);
		try {
			listView.addFooterView(footer);
		} catch (Exception e) {
		}
	}

	public void removeFooter(View footer) {
		if (footer == null || listView == null) {
			return;
		}
		footer.setVisibility(View.GONE);
		if (listView.getFooterViewsCount() > 0) {
			try {
				listView.removeFooterView(footer);
			} catch (Exception e) {
			}
		}
	}

	public void destroy(boolean isDestroy) {
		if (isDestroy) {
			header = null;
			footer = null;
			listAdapter = null;
			onScroolerListener = null;
			listView = null;
		}
		setData(null, true);
		isMoveing = false;
		// version = 0;
		// id = 0;
	}

	protected abstract View getTemplateView();

	protected abstract H getHolder(View convertView);

	protected abstract void setDataWithHolder(H holder, int position,
			boolean isMoving);

	protected abstract void treateHeaderEvent();

	protected abstract void treateEndEvent();

	protected abstract void treateStopEvent(H holder, int position);

	private void treateItemStopEvent(int form, int end) {
		H holder = null;
		for (int i = form; i <= end; i++) {
			holder = listAdapter.getHolderByPosition(i);
			if (holder == null) {
				continue;
			}
			if (holder.getNeedTreateScroolStopEvent()) {
				treateStopEvent(holder, i);
			}
		}
	}

	class ListViewAdapter extends BaseAdapter {
		private static final int HOLDER_KEY = 100000001;
		private long currentId;

		public ListViewAdapter() {
			synchronized (this) {
				id++;
				currentId = id;
			}
		}

		@Override
		public int getCount() {
			if (list == null) {
				return 0;
			}
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			if (list == null) {
				return null;
			}
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			H holder = null;
			if (convertView == null) {
				convertView = getTemplateView();
				holder = getHolder(convertView);
				convertView.setTag(HOLDER_KEY, holder);
			}
			holder = (H) convertView.getTag(HOLDER_KEY);

			convertView.setTag(currentId + ":" + position);
			setDataWithHolder(holder, position, BaseListView.this.isMoveing);
			return convertView;
		}

		public final H getHolderByPosition(int position) {
			H holder = null;
			View currentView = listView.findViewWithTag(currentId + ":"
					+ position);
			if (currentView == null) {
				return null;
			}
			holder = (H) currentView.getTag(HOLDER_KEY);
			return holder;
		}

	}

	public int getFirstIndex() {
		return firstIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	private int firstIndex;
	private int endIndex;

	private class BaseOnScroolerListener implements ListView.OnScrollListener {
		private boolean isToHeader;
		private boolean isToEnd;

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			int size = listAdapter.getCount();
			if (size == 0) {
				return;
			}
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				firstIndex = header != null ? firstIndex - 1 : firstIndex;
				endIndex = endIndex >= size - 1 ? size - 1 : endIndex;
				isMoveing = false;
				treateItemStopEvent(firstIndex, endIndex);
				break;
			case OnScrollListener.SCROLL_STATE_FLING:
				if (isToHeader) {
					treateHeaderEvent();
				} else if (isToEnd) {
					treateEndEvent();
				}
				isMoveing = true;
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				isMoveing = true;
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			listView.requestLayout();
			listView.invalidate();
			firstIndex = firstVisibleItem;
			endIndex = firstVisibleItem + visibleItemCount - 1;
			int startIndex = header != null ? 1 : 0;
			if (firstIndex <= startIndex) {
				isToHeader = true;
			} else if (endIndex + 1 >= totalItemCount) {
				isToEnd = true;
			} else {
				isToHeader = false;
				isToEnd = false;
			}
		}
	}

}
