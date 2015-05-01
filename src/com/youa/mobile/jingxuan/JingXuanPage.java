package com.youa.mobile.jingxuan;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.base.DefaultHeaderView;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.jingxuan.action.JingXuanAction;
import com.youa.mobile.jingxuan.action.RequestTagAllAction;
import com.youa.mobile.jingxuan.data.AlbumItemData;
import com.youa.mobile.jingxuan.data.ClassifyTagInfoData;
import com.youa.mobile.login.LoginPage;
import com.youa.mobile.login.widget.CustomGallery;

public class JingXuanPage extends BasePage implements OnClickListener {

	private static final int HEADER_HEIGHT = -60;

	private CustomGallery mGuideGallery;
	private AlbumImgAdapter mAlbumImgAdapter;
	private ImageView mSendButton;
	private JingxuanGridLayout mJXLayout;
	private TextView mTitle;
	private ScrollView mScrollView;
	private View mScrollInnerView;
	private DefaultHeaderView mHeaderView;
	private boolean mOnTouch;
	private Thread mThread;
	private final int SCROLL = 21;
	private UpdateFeedReceiver mUpdateFeedReceiver = new UpdateFeedReceiver();
	private float mHeaderHeight = 0.f;
	private boolean mFinishLoadingData = false;
	private int mHeaderState = -1;

	private Handler mSelfHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCROLL:
				mGuideGallery.setSelection(msg.arg1);// 进行图片切换
				break;
			}
		}
	};;

	private class UpdateFeedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (LehuoIntent.JINGXUAN_FEED_UPDATE.equals(intent.getAction())) {
				loadAlbumData();
				loadTagAllData();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jingxuan_layout);
		initView();
		loadAlbumData();
		loadTagAllData();
		IntentFilter filter = new IntentFilter();
		filter.addAction(LehuoIntent.JINGXUAN_FEED_UPDATE);
		this.registerReceiver(mUpdateFeedReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mUpdateFeedReceiver);
	}

	public void initView() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float dpDelta = metrics.densityDpi / 160f;
		mHeaderHeight = dpDelta * HEADER_HEIGHT;

		mScrollView = (ScrollView) findViewById(R.id.jingxuan_scroll);
		mScrollInnerView = findViewById(R.id.scroll_inner_layout);
		mHeaderView = new DefaultHeaderView(mScrollInnerView.findViewById(R.id.refresh_view));
		// LogUtil.d(TAG,
		// "initView. header h = " + mHeaderHeight + ",  header view h = " +
		// mScrollInnerView.getMeasuredHeight());
		mScrollInnerView.setPadding(0, (int) mHeaderHeight, 0, 0);

		mGuideGallery = (CustomGallery) this.findViewById(R.id.album_id);
		mGuideGallery.setHorizontalFadingEdgeEnabled(false);
		mGuideGallery.setVerticalFadingEdgeEnabled(false);
		mGuideGallery.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					mOnTouch = true;
				} else if (action == MotionEvent.ACTION_UP) {
					mOnTouch = false;
				}
				return false;
			}
		});
		mGuideGallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				AlbumItemData t = (AlbumItemData) mAlbumImgAdapter.getItem(position);
				Intent i = new Intent(JingXuanPage.this, TagClassFeedPage.class);
				i.putExtra(TagClassFeedPage.TYPE_KEY, TagClassFeedPage.TYPE_TOPIC);
				i.putExtra(TagClassFeedPage.ID_KEY, t.link);
				i.putExtra(TagClassFeedPage.TITLE_NAME, t.text);
				startActivity(i);
			}
		});
		mJXLayout = (JingxuanGridLayout) findViewById(R.id.jingxuan_grid);
		mTitle = (TextView) this.findViewById(R.id.title);
		mTitle.setText(R.string.home_title_lable);
		mProcessView = findViewById(R.id.progressBar);
		showProgressView();
		findViewById(R.id.back).setVisibility(View.GONE);
		mSendButton = (ImageView) findViewById(R.id.send);
		if (ApplicationManager.getInstance().isLogin()) {
			mSendButton.setVisibility(View.GONE);
		} else {
			mSendButton.setVisibility(View.VISIBLE);
		}
		mSendButton.setOnClickListener(this);

		mScrollView.setOnTouchListener(new DropdownListener());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send:
			Intent intent = new Intent(this, LoginPage.class);
			startActivity(intent);
			break;
		}
	}

	private void startAutoScroll() {
		if (mThread == null) {
			mThread = new Thread() {
				@Override
				public void run() {
					int count = 0;
					while (true) {
						count = 0;
						while (count < 30) {
							count++;
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if (mOnTouch) {
								count = 0;
							}
						}
						int postion = mGuideGallery.getSelectedItemPosition();
						postion++;
						Message msg = mSelfHandler.obtainMessage(SCROLL, postion, 0);
						mSelfHandler.sendMessage(msg);
					}
				}
			};
		}
		if (!mThread.isAlive()) {
			mThread.start();
		}
	}

	private void loadTagAllData() {
		ActionController.post(JingXuanPage.this, RequestTagAllAction.class, null,
				new RequestTagAllAction.ITagAllResultListener() {
					@Override
					public void onEnd(final List<ClassifyTagInfoData> list) {
						if (mFinishLoadingData) {
							resetHeader();
						} else {
							mFinishLoadingData = true;
						}
						hiddenProgressView();
						if (list == null || list.isEmpty()) {
							return;
						}
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mJXLayout.initViews(list);
							}
						});
					}

					@Override
					public void onStart() {
					}

					@Override
					public void onFail(int resourceID) {
						hiddenProgressView();
						if (mFinishLoadingData) {
							resetHeader();
						} else {
							mFinishLoadingData = true;
						}
					}
				}, true);
	}

	private void loadAlbumData() {
		ActionController.post(JingXuanPage.this, JingXuanAction.class, null,
				new JingXuanAction.IJingXuanResultListener() {
					@Override
					public void onEnd(final List<AlbumItemData> list) {
						if (mFinishLoadingData) {
							resetHeader();
						} else {
							mFinishLoadingData = true;
						}
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								if (list != null && list.size() > 0) {
									mAlbumImgAdapter = new AlbumImgAdapter(JingXuanPage.this, list);
									mGuideGallery.setAdapter(mAlbumImgAdapter);
									startAutoScroll();
								}
							}
						});
					}

					@Override
					public void onStart() {

					}

					@Override
					public void onFail(int resourceID) {
						if (mFinishLoadingData) {
							resetHeader();
						} else {
							mFinishLoadingData = true;
						}
					}
				}, true);
	}

	private void resetHeader() {
		mHeaderState = -1;
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mScrollInnerView.setPadding(0, (int) mHeaderHeight, 0, 0);
				mHeaderView.onPullHint(true);
				mScrollView.scrollTo(0, 0);
			}
		});
	}

	private final class DropdownListener implements OnTouchListener {
		private static final int PULLING = 0;
		private static final int NEED_REFRESH = 1;
		private static final int REFRESHING = 2;
		private float lastY = -1;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mHeaderState == REFRESHING) {
				return false;
			}
			int action = event.getAction();
			// LogUtil.d(TAG, "onTouch. action = " + action);
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				lastY = event.getRawY();
				// LogUtil.d(TAG, "onTouch. lastY = " + lastY);
				mHeaderState = -1;
				break;
			case MotionEvent.ACTION_MOVE:
				float y = event.getRawY();
				int delta = lastY < 0 ? 1 : (int) (y - lastY);
				// LogUtil.d(TAG,
				// "onTouch.  y = " + y + ", delta y = " + delta +
				// ", curr scroll y = " + mScrollView.getScrollY());
				int lastPadding = mScrollInnerView.getPaddingTop();
				int currPadding = lastPadding + delta;
				if (currPadding <= mHeaderHeight || mScrollView.getScrollY() > 0) {
					mHeaderState = -1;
					return false;
				}
				mScrollInnerView.setPadding(0, currPadding, 0, 0);

				if (mHeaderState != PULLING && mHeaderState != NEED_REFRESH) {
					mHeaderView.onRefreshLastTime();
				}
				if (mHeaderState != NEED_REFRESH) {
					mHeaderState = PULLING;
				}
				// LogUtil.d(TAG, "onTouch. curr padding top = " +
				// mScrollInnerView.getPaddingTop());
				if (mHeaderState != NEED_REFRESH && currPadding >= -mHeaderHeight / 6) {
					// LogUtil.d(TAG, "onTouch. move. to refresh.");
					mHeaderView.onRelaseHint();
					mHeaderState = NEED_REFRESH;
				} else if (currPadding < -mHeaderHeight / 6 && mHeaderState == NEED_REFRESH) {
					// LogUtil.d(TAG, "onTouch. move. pulling.");
					mHeaderView.onPullHint(true);
					mHeaderState = PULLING;
				}
				lastY = y;
				break;
			case MotionEvent.ACTION_UP:
				// flag = false;
				lastY = -1;
				if (mHeaderState == NEED_REFRESH) {
					// LogUtil.d(TAG, "onTouch. to refresh.");
					mHeaderState = REFRESHING;
					mFinishLoadingData = false;
					loadAlbumData();
					loadTagAllData();
					mHeaderView.onRefreshHint();
					mScrollInnerView.setPadding(0, 0, 0, 0);
					((ScrollView) findViewById(R.id.jingxuan_scroll)).scrollTo(0, 0);
				} else if (mHeaderState == PULLING) {
					// LogUtil.d(TAG, "onTouch. to reset.");
					resetHeader();
				}
				break;
			default:
				break;
			}
			return true;
		}
	}
}
