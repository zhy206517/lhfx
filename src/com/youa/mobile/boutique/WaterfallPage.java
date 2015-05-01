package com.youa.mobile.boutique;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.YoumentEvent;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.widget.FlingScrollView;
import com.youa.mobile.common.widget.FlingScrollView.RefreshListener;
import com.youa.mobile.common.widget.FlowTag;
import com.youa.mobile.common.widget.FlowView;
import com.youa.mobile.common.widget.WaterFallScrollView;
import com.youa.mobile.common.widget.WaterFallScrollView.OnScrollListener;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.data.User;
import com.youa.mobile.input.PublishPage;
import com.youa.mobile.life.CommonFeedPage;
import com.youa.mobile.life.ShareClassifyPage;
import com.youa.mobile.life.action.LoadFeedAction;
import com.youa.mobile.login.LoginPage;

public class WaterfallPage extends BasePage implements OnClickListener {
	public static final int HOTSHARE_REQUEST_CODE = 20;
	public static final int LOGIN_REQUEST_CODE = 21;
	Animation anim_appear;
	private ImageButton mBackButton;
	private Button mSendButton;
	private TextView mTitle;
	private ProgressBar homeProgressBar;
	public List<HomeData> mHomeDataList = new ArrayList();
	public List<HomeData> mCurPageList = new ArrayList();
	private WaterFallScrollView waterfall_scroll;
	private LinearLayout waterfall_container;
	private ArrayList<LinearLayout> waterfall_items;
	// private Handler handler;
	private int item_width;
	private int column_count = 3;// 显示列数
	private int current_page = 0;// 当前页数
	private int[] topIndex;
	private int[] bottomIndex;
	private int[] lineIndex;
	private int[] column_height;// 每列的高度
	private int loaded_count = 0;// 已加载数量
	private HashMap<Integer, Integer>[] pin_mark = null;
	private Context context;
	// private HashMap<Integer, FlowView> iviews;
	int scroll_height;
	private LinearLayout mfooterView;
	private String mShareclassifyId = "";
	boolean isLoced = true;
	boolean initFlag = true;
	private boolean isTurnPage;
	private FlingScrollView rv;
	private boolean mHeadRefresh;
	UpdateFeedReceiver mUpdateFeedReceiver = new UpdateFeedReceiver();

	private class UpdateFeedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (LehuoIntent.JINGXUAN_FEED_UPDATE.equals(intent.getAction())) {
				initFlag = true;
				mHeadRefresh = true;
				if (rv != null) {
					rv.refreshTab();
					loadData(current_page, mShareclassifyId);
				}
			}
		}
	}

	public void registUpdateReceiver() {
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(LehuoIntent.JINGXUAN_FEED_UPDATE);
		registerReceiver(mUpdateFeedReceiver, mFilter);
	}

	public void unRegistUpdateReceiver() {
		this.unregisterReceiver(mUpdateFeedReceiver);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registUpdateReceiver();
//		setContentView(R.layout.waterfall_layout);
		mBackButton = (ImageButton) findViewById(R.id.back);
		mBackButton.setBackgroundResource(R.drawable.common_fenlan_selector);
		mBackButton.setOnClickListener(this);
		mSendButton = (Button) findViewById(R.id.send);
		if (!ApplicationManager.getInstance().isLogin()) {
			mSendButton.setBackgroundResource(R.drawable.login_reg_selector);
		} else {
			mSendButton
					.setBackgroundResource(R.drawable.feed_button_edit_selector);
		}
		mSendButton.setOnClickListener(this);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(R.string.home_title_lable);
		homeProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		item_width = ApplicationManager.getInstance().getWidth() / column_count;// 根据屏幕大小计算每列大小
		context = this;
		mfooterView = (LinearLayout) findViewById(R.id.footer);
		mfooterView.setVisibility(View.GONE);
		waterfall_scroll = (WaterFallScrollView) findViewById(R.id.waterfall_scroll);
		rv = (FlingScrollView) findViewById(R.id.fling_id);
		rv.mfooterView = mfooterView;
		rv.sv = waterfall_scroll;
		waterfall_container = (LinearLayout) findViewById(R.id.waterfall_container);
		initData();
		InitLayout();
		waterfall_scroll.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onTop() {
				// 滚动到最顶端
				Log.d("LazyScroll", "Scroll to top");
			}

			@Override
			public void onScroll() {

			}

			@Override
			public void onBottom() {
				// 滚动到最低端
				if (!isLoced) {
					return;
				}
				int pageIndex = ++current_page;
				loadData(pageIndex, mShareclassifyId);
			}

			@Override
			public void onAutoScroll(int l, int t, int oldl, int oldt) {
				if (initFlag || mHomeDataList == null
						|| mHomeDataList.size() == 0) {
					return;
				}
				scroll_height = waterfall_scroll.getMeasuredHeight();
				if (t > oldt) {// 向下滚动
					if (t > 2 * scroll_height) {// 超过两屏幕后
						for (int k = 0; k < column_count; k++) {
							LinearLayout localLinearLayout = waterfall_items
									.get(k);
							if (pin_mark[k].get(Math.min(bottomIndex[k] + 1,
									lineIndex[k])) <= t + 3 * scroll_height) {// 最底部的图片位置小于当前t+3*屏幕高度
								((FlowView) waterfall_items.get(k).getChildAt(
										Math.min(1 + bottomIndex[k],
												lineIndex[k]))).Reload();
								bottomIndex[k] = Math.min(1 + bottomIndex[k],
										lineIndex[k]);
							}
							if (pin_mark[k].get(topIndex[k]) < t - 2
									* scroll_height) {// 未回收图片的最高位置<t-两倍屏幕高度
								int i1 = topIndex[k];
								topIndex[k]++;
								((FlowView) localLinearLayout.getChildAt(i1))
										.recycle();
							}
						}
					}
				} else {// 向上滚动
					for (int k = 0; k < column_count; k++) {
						if (waterfall_items == null
								|| waterfall_items.size() == 0
								|| waterfall_items.size() < k) {
							break;
						}
						LinearLayout localLinearLayout = waterfall_items.get(k);
						if (pin_mark == null || pin_mark.length == 0
								|| pin_mark.length < k) {
							break;
						}
						if (pin_mark[k].get(bottomIndex[k]) > t + 3
								* scroll_height) {
							((FlowView) localLinearLayout
									.getChildAt(bottomIndex[k])).recycle();
							bottomIndex[k]--;
						}
						if (pin_mark[k].get(Math.max(topIndex[k] - 1, 0)) >= t
								- 2 * scroll_height) {
							((FlowView) localLinearLayout.getChildAt(Math.max(
									-1 + topIndex[k], 0))).Reload();
							topIndex[k] = Math.max(topIndex[k] - 1, 0);
						}
					}

				}

			}
		});
		rv.setRefreshListener(new RefreshListener() {
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				if (rv.mRefreshState == 4) {
					initFlag = true;
					mHeadRefresh = true;
					loadData(current_page, mShareclassifyId);
				} else if (rv.mfooterRefreshState == 4) {
					// new GetFooterDataTask().execute();
				}
			}
		});
		loadData(current_page, mShareclassifyId);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(ApplicationManager.getInstance().isLogin()){
			MobclickAgent.onEvent(WaterfallPage.this, YoumentEvent.EVENT_ENTER_JINGXUAN,
					YoumentEvent.EVENT_ENTER_STATE_LOGIN);
		}else{
			MobclickAgent.onEvent(WaterfallPage.this, YoumentEvent.EVENT_ENTER_JINGXUAN,
					YoumentEvent.EVENT_ENTER_STATE_NOLOGIN);
		}
		isTurnPage = false;
		reloadBitmap();
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onStop() {
		super.onStop();
		recycleBitmap();
	}

	public void reloadBitmap() {
		if (waterfall_items != null && waterfall_items.size() == 3) {
			// for (int i = 0; i < column_count; i++) {
			// LinearLayout itemLayout = waterfall_items.get(i);
			// int count = itemLayout.getChildCount();
			// for (int j = 0; j < count; j++) {
			// ((FlowView) itemLayout.getChildAt(j)).Reload();
			// }
			// }
			for (int k = 0; k < column_count; k++) {
				LinearLayout localLinearLayout = waterfall_items.get(k);
				int bottomItemIndex = bottomIndex[k];
				int topItemIndex = topIndex[k];
				// Log.d("WaterfallPage", "topItemIndex:" + topItemIndex);
				// Log.d("WaterfallPage", "bottomItemIndex:" + bottomItemIndex);

				for (int j = topItemIndex; j <= bottomItemIndex; j++) {
					FlowView flowView = (FlowView) localLinearLayout
							.getChildAt(j);
					flowView.Reload();
				}
			}
		}
	}

	public void recycleBitmap() {
		// Log.d("seememory", "recycleBitmap:" +
		// Runtime.getRuntime().freeMemory());
		if (waterfall_items != null && waterfall_items.size() == 3) {
			for (int i = 0; i < column_count; i++) {
				LinearLayout itemLayout = waterfall_items.get(i);
				int count = itemLayout.getChildCount();
				for (int j = 0; j < count; j++) {
					((FlowView) itemLayout.getChildAt(j)).recycle();
				}
			}
		}
		// Log.d("seememory", "recycleBitmap:" +
		// Runtime.getRuntime().freeMemory());

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegistUpdateReceiver();
	}

	public void initData() {
		current_page = 0;
		loaded_count=0;
		// if (iviews != null) {
		// Set keySet = iviews.keySet();
		// for (Iterator iter = keySet.iterator(); iter.hasNext();) {
		// Integer key = (Integer) iter.next();
		// Log.d("zhy", "recycle");
		// ((FlowView) iviews.get(key)).recycle();
		// iviews.remove(key);
		// }
		// } else {
		// iviews = new HashMap<Integer, FlowView>();
		// }
		if (waterfall_items == null) {
			waterfall_items = new ArrayList<LinearLayout>();
		}
		column_height = new int[column_count];
		pin_mark = new HashMap[column_count];
		this.lineIndex = new int[column_count];
		this.bottomIndex = new int[column_count];
		this.topIndex = new int[column_count];
		for (int i = 0; i < column_count; i++) {
			lineIndex[i] = -1;
			bottomIndex[i] = -1;
			pin_mark[i] = new HashMap();
		}
	}

	public List<HomeData> clearNoImageFeed(List<HomeData> data) {
		List<HomeData> list = new ArrayList();
		for (HomeData d : data) {
			User user = null;
			if (!"0".equals(d.PublicUser.feedType)) {
				user = d.originUser;
			} else {
				user = d.PublicUser;
			}
			if (user.contentImg != null
					&& !TextUtils.isEmpty(user.contentImg[0].img_content_id)) {
				list.add(d);
			}
		}
		return list;
	}

	private void loadData(final int currentPage, String clsid) {
		// Log.d("seememory","loadData-start:" +
		// (Runtime.getRuntime().totalMemory() -
		// Runtime.getRuntime().freeMemory()));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(LoadFeedAction.REQUEST_TYPE,
				LoadFeedAction.RequestType.SHARE_CLASSIFY);
		params.put(LoadFeedAction.PAGE, currentPage);
		if (!TextUtils.isEmpty(clsid)) {
			params.put(LoadFeedAction.SHARECLASSIFY_ID, clsid);
		}
		if(mHeadRefresh&&mHomeDataList!=null&&mHomeDataList.size()>0){
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					waterfall_scroll.scrollTo(0, 0);
				}
			});
		}
		ActionController.post(WaterfallPage.this, LoadFeedAction.class, params,
				new LoadFeedAction.LoadFeedActionListener() {
					@Override
					public void onStart() {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								// Log.d("seememory","loadData-end:" +
								// (Runtime.getRuntime().totalMemory() -
								// Runtime.getRuntime().freeMemory()));
								isLoced = false;
								if (mHeadRefresh) {
									return;
								}
								if (currentPage == 0) {
									homeProgressBar.setVisibility(View.VISIBLE);
								} else {
									mfooterView.setVisibility(View.VISIBLE);
								}
							}
						});
					}

					@Override
					public void onFinish(List<HomeData> data, int pageIndex) {
						if(initFlag){
							mHomeDataList.clear();
						}
						initFlag = false;
						if (data != null && data.size() > 0) {
							// mHomeDataList.clear();
							mCurPageList.clear();
							mCurPageList.addAll(clearNoImageFeed(data));
							mHomeDataList.addAll(mCurPageList);
						}
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								if (mHeadRefresh) {
									initData();
									InitLayout();
								}
								if (currentPage == 0) {
									homeProgressBar.setVisibility(View.GONE);
								} else {
									mfooterView.setVisibility(View.GONE);
								}
								if (mHeadRefresh) {
									rv.finishRefresh();
								}
								mHeadRefresh = false;
								AddItemToContainer(currentPage,
										mCurPageList.size());
								isLoced = true;
							}
						});
					}

					@Override
					public void onFail(int resourceID) {
						isLoced = true;
						showToast(WaterfallPage.this, resourceID);
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								if (mHeadRefresh) {
									rv.finishRefresh();
								}
								if (current_page > 0) {
									current_page--;
								}
								mHeadRefresh = false;
								mfooterView.setVisibility(View.GONE);
								homeProgressBar.setVisibility(View.GONE);
							}
						});
					}
				}, true);
	}

	private void InitLayout() {
		waterfall_scroll.getView();
		if (waterfall_items.size() == 3) {
			for (int i = 0; i < column_count; i++) {
				LinearLayout itemLayout = waterfall_items.get(i);
				int count = itemLayout.getChildCount();
				for (int j = 0; j < count; j++) {
					((FlowView) itemLayout.getChildAt(j)).recycle();
				}
				itemLayout.removeAllViews();
			}
			waterfall_items.clear();
			waterfall_container.removeAllViews();
		}
		for (int i = 0; i < column_count; i++) {
			LinearLayout itemLayout = new LinearLayout(this);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
					item_width, LayoutParams.WRAP_CONTENT);
			// itemLayout.set
			itemLayout.setOrientation(LinearLayout.VERTICAL);
			itemLayout.setLayoutParams(itemParam);
			waterfall_items.add(itemLayout);
			waterfall_container.addView(itemLayout);
		}
	}

	private void AddItemToContainer(int pageindex, int pagecount) {
		int currentIndex = loaded_count;
		for (int i = currentIndex; i < mHomeDataList.size(); i++) {
			loaded_count++;
			AddImage(mHomeDataList.get(i),
					(int) Math.ceil(loaded_count / (double) column_count));
		}
	}

	private void clearNoUseData(User user) {
		// data.originUser.contentImg = null;
		user.postId = user.postId + ""; // friend ,theme
		user.feedType = user.feedType + "";

		user.contents = null;

		user.charSequence = null;
		user.img_head_id = null;
		user.name = null;
		user.fromWhere = null;
		user.place = null;

		user.uId = null;

		user.time = null;
		user.price = null;
		user.like_num = null;
		user.comment_num = null;
		user.transpond_num = null;
	}

	private void clearNoUseData(HomeData data) {
		if (data.originUser != null) {
			clearNoUseData(data.originUser);
		}
		if (data.PublicUser != null) {
			clearNoUseData(data.PublicUser);
		}
		if (data.transPondUser != null) {
			clearNoUseData(data.transPondUser);
		}
	}

	private void AddImage(HomeData data, int rowIndex) {
		clearNoUseData(data);
		// if(data.originUser != null) {
		// data.originUser.contents = null;
		// }
		// if(data.PublicUser != null) {
		// data.PublicUser.contents = null;
		// }
		// if(data.transPondUser != null) {
		// data.transPondUser.contents = null;
		// }

		// data.PublicUser.contents = null;
		// data.transPondUser.contents = null;
		FlowView item = new FlowView(context);
		// 多线程参数
		FlowTag param = new FlowTag();
		param.setData(data);
		param.setItemWidth(item_width);
		LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
				item_width, (item_width * param.getImageHeight())
						/ param.imageWidth);
		item.setLayoutParams(itemParam);
		item.setBackgroundResource(R.drawable.photo_border);
		item.setScaleType(ScaleType.FIT_XY);
		item.setRowIndex(rowIndex);
		item.setFlowTag(param);
		// anim_appear = AnimationUtils.loadAnimation(WaterfallPage.this,
		// R.anim.alpha_anim_appear);
		// anim_appear.setFillEnabled(true);
		// anim_appear.setFillAfter(true);
		// item.startAnimation(anim_appear);
		// 此处计算列值
		LayoutParams lp = item.getLayoutParams();
		int columnIndex = GetMinValue(column_height);
		item.setColumnIndex(columnIndex);
		column_height[columnIndex] += lp.height;
		// iviews.put(item.getId(), item);
		waterfall_items.get(columnIndex).addView(item);
		lineIndex[columnIndex]++;
		pin_mark[columnIndex].put(lineIndex[columnIndex],
				column_height[columnIndex]);
		bottomIndex[columnIndex] = lineIndex[columnIndex];
		item.LoadImage();
		
		
		
		
	}

	private int GetMinValue(int[] array) {
		int m = 0;
		int length = array.length;
		for (int i = 0; i < length; ++i) {
			if (array[i] < array[m]) {
				m = i;
			}
		}
		return m;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			if (!isTurnPage) {
				isTurnPage = true;
				Intent i = new Intent(this, ShareClassifyPage.class);
				i.putExtra(LoadFeedAction.SHARECLASSIFY_ID, mShareclassifyId);
				startActivityForResult(i, HOTSHARE_REQUEST_CODE);
			}

			break;
		case R.id.send:
			if (ApplicationManager.getInstance().isLogin()) {
				Intent intent = new Intent(this, PublishPage.class);
				startActivity(intent);
			} else {
				Intent intent = new Intent(this, LoginPage.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("from where", "share");
				startActivityForResult(intent, LOGIN_REQUEST_CODE);
			}
			break;
		}
		// TODO Auto-generated method stub
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case HOTSHARE_REQUEST_CODE:
			if (data == null) {
				return;
			}
			String id = data.getStringExtra(LoadFeedAction.SHARECLASSIFY_ID);
			if(mShareclassifyId.equals(id)){
				return;
			}
			mShareclassifyId=id;
			String title = data.getStringExtra(CommonFeedPage.TITLE);
			initFlag = true;
			initData();
			InitLayout();
			mTitle.setText(title);
			loadData(current_page, mShareclassifyId);
			break;
		case LOGIN_REQUEST_CODE:
			mSendButton.setBackgroundResource(R.drawable.common_done_selector);
		}
	}
}
