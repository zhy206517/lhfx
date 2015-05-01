package com.youa.mobile.information;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youa.mobile.input.PublishFeedStore;
import com.youa.mobile.input.data.PublishData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.youa.mobile.ExitPage;
import com.youa.mobile.LehoTabActivity;
import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.UpdateService;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.params.PageSize;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.content.ContentOriginActivity;
import com.youa.mobile.content.ContentTranspondActivity;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.information.TimeLimeListView.TapGestureRecognize;
import com.youa.mobile.information.action.AddCancelAttentAction;
import com.youa.mobile.information.action.InitPersonalInfoAction;
import com.youa.mobile.information.action.SearchCountAction;
import com.youa.mobile.information.action.SearchOwnFeedAction;
import com.youa.mobile.information.data.PersonalInformationData;
import com.youa.mobile.information.data.ShowCountData;
import com.youa.mobile.more.MoreMainPage;
import com.youa.mobile.more.MoreUtil;
import com.youa.mobile.news.NewsMainPage;
import com.youa.mobile.news.util.NewsUtil;

public class MySelfPersonnalInforPage extends BasePage implements
		BaseListView.OnScrollEndListener, OnClickListener, TapGestureRecognize {// OnItemClickListener,

	private class UpdateCountReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			loadCountData();
		}
	}

	public static final String EXTRA_REGISTER_REFRESH = "extra_register_refresh";
	public static final String KEY_USER_NAME = "username";
	public static final String KEY_USER_ID = "userid";
	public static final String KEY_FROM_HOME = "fromHome";

	private final int MENU_BACK = 1;
	private final int MENU_EXIT = 2;
	ListView listView;
	private NewsCountReceiver mNewNewsCountReceiver;
	private LayoutInflater mInflater;
	private TimeLimeListView mListView;
	private TextView mTitleView;
	private ImageButton editView;
	private ImageButton mBack;
	private ImageView mHeadView;
	private ImageView mTouXiangView;
	// private TextView mUserLable;
	private TextView mFavorNum;
	private TextView mFansNum;
	private TextView mNewsNum;
	private TextView mAttentionNum;
	private TextView mNewsCount;
	private String mUid;
	private String mImageId;
	private String mSexInt;
	private String mType;
	private boolean isFromHome = true;
	private int mPageIndex = 1;
	private List<HomeData> mDataList;
	private View mFooterView;
	private LinearLayout mHeaderView;
	UpdateCountReceiver mUpdateCountReceiver = new UpdateCountReceiver();
	ExitClentReceiver mExitClentReceiver = new ExitClentReceiver();
	private Handler mHandler = new Handler();
	private UpdataBroadCastReceiver mUpdateFeedReceiver = new UpdataBroadCastReceiver();
	LinearLayout mEmptyView;
	int commentNum;
	int addMeNum;
	int beLikeNum;

	class UpdataBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, final Intent intent) {
			String action = intent.getAction();
			if (LehuoIntent.ACTION_OWN_FEED_DELETE.equals(action)) {
				final String postId = intent.getStringExtra("postId");
				mHandler.post(new Runnable() {
					public void run() {
						List<HomeData> list = mListView.getData();
						if (list != null && list.size() > 0) {
							for (HomeData homeData : list) {
								if (homeData.draftList==null&&homeData.PublicUser.postId.equals(postId)) {
									list.remove(homeData);
									mListView.setData(list,
											PageSize.INFO_FEED_PAGESIZE);
									break;
								}
							}
						}
					}
				});
			} else if (LehuoIntent.ACTION_FEED_PUBLISH_REFRESH.equals(action)) {
				mHandler.post(new Runnable() {
					public void run() {
						List<HomeData> list = mListView.getData();
						List<PublishData> dataList = ApplicationManager
								.getInstance().publishDataList;
							if (list != null && list.size() > 0) {
								HomeData homeData = list.get(0);
								if (homeData.draftList != null) {
									homeData.draftList.clear();
									if (dataList != null && dataList.size() > 0) {
										homeData.draftList.addAll(dataList);	
									}else{
										list.remove(0);
									}
									mListView.setData(list,
											PageSize.INFO_FEED_PAGESIZE);
								} else {
									HomeData draftHomeData = new HomeData();
									List<HomeData> feedlist = new ArrayList<HomeData>();
									if(dataList != null && dataList.size() > 0){
										draftHomeData.draftList = new ArrayList<PublishData>();
										draftHomeData.draftList.addAll(ApplicationManager
												.getInstance().publishDataList);
										feedlist.add(draftHomeData);
										feedlist.addAll(list);
										mListView.setData(feedlist,
												PageSize.INFO_FEED_PAGESIZE);
									}
								}
							}

					}
				});
			}
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (TextUtils.equals(LehuoIntent.PERSON_FEED_UPDATE,
					intent.getAction())
					|| TextUtils.equals(LehuoIntent.ACTION_FEED_PUBLISH_OK,
							intent.getAction())) {
				loadUserData();
				loadCountData();
				mListView.refresh();
			} else if (SystemConfig.ACTION_REFRESH_USER_INFO_UPDATE
					.equals(intent.getAction())) {
				loadUserData();
			}
		}
	};

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(LehuoIntent.PERSON_FEED_UPDATE);
		filter.addAction(LehuoIntent.ACTION_FEED_PUBLISH_OK);
		filter.addAction(SystemConfig.ACTION_REFRESH_USER_INFO_UPDATE);
		registerReceiver(mReceiver, filter);
	}

	private void unregisterReceiver() {
		unregisterReceiver(mReceiver);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(LehuoIntent.ACTION_USERCOUNT_NEEDUPDATE);
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(LehuoIntent.ACTION_EXIT_CLIENT);
		registerReceiver(mExitClentReceiver, mFilter);
		registerReceiver(mUpdateCountReceiver, mIntentFilter);
		registNewNewsCountReceiver();
		IntentFilter mUpdateFeed = new IntentFilter();
		mUpdateFeed.addAction(LehuoIntent.ACTION_OWN_FEED_DELETE);
		mUpdateFeed.addAction(LehuoIntent.ACTION_FEED_PUBLISH_REFRESH);
		registerReceiver(mUpdateFeedReceiver, mUpdateFeed);
		registerReceiver();
		setContentView(R.layout.information_myselfpersonal_main);
		initFromIntent(getIntent());
		mInflater = LayoutInflater.from(this);
		if (isFromHome) {
			mUid = ApplicationManager.getInstance().getUserId();
		}
		initView();
		checkIfCountViewNeedShow();
		loadUserData();
		loadCountData();
		loadFeedData(true);
		toTopInit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		editView.setImageResource(0);
		editView.setBackgroundResource(R.drawable.common_setting_selector);
	}

	private void checkIfCountViewNeedShow() {
		boolean isSayMeShow = MoreUtil.readIsShowSayMeFromPref(this);
		boolean isAddMeShow = MoreUtil.readIsShowAddMeFromPref(this);
		boolean isFavShow = MoreUtil.readIsShowFavFromPref(this);
		int list[] = NewsUtil.readNewCountFromPref(this);
		int count = 0;
		if (isAddMeShow) {
			count += list[0];
		}
		if (isSayMeShow) {
			count += list[1];
		}
		if (isFavShow) {
			count += list[2];
		}
		if (mNewsCount != null && count != 0 && isFromHome) {
			mNewsCount.setText(count > 99 ? "99+" : count + "");
			mNewsCount.setVisibility(View.VISIBLE);
		}

	}

	private void registNewNewsCountReceiver() {
		mNewNewsCountReceiver = new NewsCountReceiver();
		IntentFilter filter = new IntentFilter(
				UpdateService.INTENT_NEW_NEWS_COUNT_CHANGE);
		registerReceiver(mNewNewsCountReceiver, filter);
	}

	private class NewsCountReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean isSayMeShow = MoreUtil.readIsShowSayMeFromPref(context);
			boolean isAddMeShow = MoreUtil.readIsShowAddMeFromPref(context);
			boolean isFavShow = MoreUtil.readIsShowFavFromPref(context);
			int count = 0;
			if (UpdateService.INTENT_NEW_NEWS_COUNT_CHANGE.equals(intent
					.getAction())) {
				final int[] list = NewsUtil.readNewCountFromPref(context);
				if (isAddMeShow) {
					count += list[0];
				}
				if (isSayMeShow) {
					count += list[1];
				}
				if (isFavShow) {
					count += list[2];
				}
				final int total = count;
				if (total != 0) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if (mNewsCount != null && isFromHome) {
								mNewsCount.setText(total > 99 ? "99+" : total
										+ "");
								mNewsCount.setVisibility(View.VISIBLE);
							}
						}
					});

				} else {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if (mNewsCount != null && isFromHome) {
								mNewsCount.setVisibility(View.INVISIBLE);
							}
						}
					});
				}
			}
		}
	}

	private class ExitClentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (LehuoIntent.ACTION_EXIT_CLIENT.equals(intent.getAction())) {
				MySelfPersonnalInforPage.this.finish();

			}
		}
	}

	private void initFeedListView() {
		mInflater = LayoutInflater.from(this);
		mHeaderView = (LinearLayout) mInflater.inflate(R.layout.feed_header,
				null);
		mFooterView = mInflater.inflate(R.layout.feed_footer, null);
		listView = (ListView) findViewById(R.id.list);
		mListView = new TimeLimeListView(listView, mHeaderView, mFooterView);
		mListView.setOnScrollEndListener(this);
		// listView.setOnItemClickListener(this);
		mListView.setTapGestureRecognizeListener(this);
		mProcessView = findViewById(R.id.progressBar);
		mLoadView = listView;
		showProgressView();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		initFromIntent(getIntent());
		if (isFromHome) {
			mUid = ApplicationManager.getInstance().getUserId();
		}
		loadUserData();
		loadCountData();
		loadFeedData(true);
	}

	private void loadFeedData(boolean isRefreshOrGetMore) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(SearchOwnFeedAction.PARAM_UID, mUid);
		params.put(SearchOwnFeedAction.PARAM_PAGEINDEX, mPageIndex);
		params.put(SearchOwnFeedAction.PARAM_REFRESH_OR_MORE,
				isRefreshOrGetMore);
		if (isRefreshOrGetMore && mListView.getData() != null
				&& mListView.getData().size() > 0) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					listView.setSelection(0);
				}
			});
		}
		ActionController.post(this, SearchOwnFeedAction.class, params,
				new SearchOwnFeedAction.SearchOwnFeedResult() {

					@Override
					public void onStart() {

					}

					public void onFail(int resourceID) {
						handlerCloserHeaderFooter();
						showToast(MySelfPersonnalInforPage.this, resourceID);
						hiddenProgressView();

					}

					@Override
					public void onEnd(List<HomeData> feedData, int pageIndex) {
						updateFeedViews((List<HomeData>) feedData, pageIndex);
						hiddenProgressView();
					}
				}, true);
	}

	@Override
	public void onScrollEnd() {
		loadFeedData(false);
	}

	private long lastUpdataTime;

	@Override
	public void onScrollHeader() {
		loadFeedData(true);
	}

	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private void toTopInit() {
		arrowImageView = (ImageView) mHeaderView
				.findViewById(R.id.head_arrowImageView);
		progressBar = (ProgressBar) mHeaderView
				.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) mHeaderView
				.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) mHeaderView
				.findViewById(R.id.head_lastUpdatedTextView);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);
	}

	@Override
	public void onTapGestureRecognizeListener(String[] str) {
		if (str == null) {
			return;
		}
		String postId = str[0];
		String feedType = str[1];
		Bundle bundle = new Bundle();
		Class c = null;
		// --------------------
		if ("0".equals(feedType)) {
			c = ContentOriginActivity.class;
			bundle.putString(ContentOriginActivity.ORIGIN_FEED_ID, postId);// 源动态id
		} else {
			c = ContentTranspondActivity.class;
			bundle.putString(ContentTranspondActivity.TRANSPOND_FEED_ID, postId);// 转发动态id
		}
		// --------------------
		Intent intent = new Intent();
		intent.putExtras(bundle);
		intent.setClass(MySelfPersonnalInforPage.this, c);
		startActivity(intent);
	}

	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1, int position,
	// long arg3) {
	// if (mListView.getHeader() != null) {
	// position -= 1;
	// }
	// int size = mDataList.size();
	// if (position >= size) {
	// return;
	// }
	// HomeData data = mDataList.get(position);
	// Bundle bundle = new Bundle();
	// Class c = null;
	// // --------------------
	// if ("0".equals(data.PublicUser.feedType)) {
	// c = ContentOriginActivity.class;
	// bundle.putString(ContentOriginActivity.ORIGIN_FEED_ID,
	// data.PublicUser.postId);// 源动态id
	// } else {
	// c = ContentTranspondActivity.class;
	// bundle.putString(ContentTranspondActivity.TRANSPOND_FEED_ID,
	// data.PublicUser.postId);// 转发动态id
	// }
	// // --------------------
	// Intent intent = new Intent();
	// intent.putExtras(bundle);
	// intent.setClass(this, c);
	// startActivity(intent);
	// }

	private void loadCountData() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(SearchCountAction.KEY_UID, mUid);
		ActionController.post(this, SearchCountAction.class, paramMap,
				new SearchCountAction.ISearchCountResult() {
					@Override
					public void onEnd(ShowCountData showCountData) {
						updateView(showCountData);
					}

					@Override
					public void onStart() {

					}

					@Override
					public void onFail(int resourceID) {
						showToast(resourceID);
					}
				}, true);
	}

	private void loadUserData() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(SearchCountAction.KEY_UID, mUid);
		ActionController.post(this, InitPersonalInfoAction.class, paramMap,
				new InitPersonalInfoAction.IInitResult() {
					@Override
					public void onEnd(
							final PersonalInformationData personalInformationData) {
						mHandler.post(new Runnable() {
							public void run() {
								mTitleView.setText(personalInformationData
										.getUserName());
								mImageId = personalInformationData
										.getHeaderImageId();
								mSexInt = personalInformationData.getSexInt();
								mType = personalInformationData.getUserType();
								if (!mUid.equals(ApplicationManager
										.getInstance().getUserId())) {
									if ("2".equals(mType)) {
										mTouXiangView
												.setImageResource(R.drawable.person_t);
									} else if ("3".equals(mType)) {
										mTouXiangView
												.setImageResource(R.drawable.person_v);
									} else {
										mTouXiangView
												.setImageResource(R.drawable.touxiang_info);
									}

								} else {
									mTouXiangView
											.setImageResource(R.drawable.touxiang_edit);
								}
								// --------------------------------
								int defaultHeaderRes = R.drawable.head_men;
								String sex = personalInformationData
										.getSexInt();
								if (PersonalInformationData.SEX_WOMAN
										.equals(sex)) {
									defaultHeaderRes = R.drawable.head_women;
								}
								ImageUtil.setHeaderImageView(
										MySelfPersonnalInforPage.this,
										mHeadView, personalInformationData
												.getHeaderImageId(),
										defaultHeaderRes);
							}
						});
					}

					@Override
					public void onStart() {

					}

					@Override
					public void onFail(int resourceID) {
						showToast(resourceID);
					}

					@Override
					public void onShowMessage(int res) {
						showToast(res);
					}
				}, true);
	}

	private void updateView(final ShowCountData showCountData) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mFavorNum.setText(String.valueOf(showCountData.getFavorCount()));
				mFansNum.setText(String.valueOf(showCountData.getFansCount()));
				mNewsNum.setText(String.valueOf(showCountData.getNewsCount()));
				mAttentionNum.setText(String.valueOf(showCountData
						.getAttentCount()));
				commentNum = showCountData.getCommentCount();
				addMeNum = showCountData.getAddmeCount();
				beLikeNum = showCountData.getBelikeCount();
			}
		});
	}

	private void initFromIntent(Intent intent) {
		mUid = intent.getStringExtra(KEY_USER_ID);
		isFromHome = intent.getBooleanExtra(KEY_FROM_HOME, false);
	}

	private void initView() {
		mTitleView = (TextView) findViewById(R.id.title);
		mTouXiangView = (ImageView) findViewById(R.id.touxiang_info);
		if (mUid.equals(ApplicationManager.getInstance().getUserId())) {
			mTouXiangView.setImageResource(R.drawable.touxiang_edit);
		}
		mEmptyView = (LinearLayout) findViewById(R.id.empty_view);
		mEmptyView.findViewById(R.id.wxcb_empty_btn).setVisibility(View.GONE);
		editView = (ImageButton) findViewById(R.id.send);
		mBack = (ImageButton) findViewById(R.id.back);
		mBack.setVisibility(View.GONE);
		mBack.setOnClickListener(this);
		editView.setOnClickListener(this);
		mHeadView = (ImageView) findViewById(R.id.header_image);
		mHeadView.setOnClickListener(this);
		mFavorNum = (TextView) findViewById(R.id.favor_num);
		findViewById(R.id.favor_id).setOnClickListener(this);
		mAttentionNum = (TextView) findViewById(R.id.attention_num);
		findViewById(R.id.attention_id).setOnClickListener(this);
		mFansNum = (TextView) findViewById(R.id.fans_num);
		findViewById(R.id.fans_id).setOnClickListener(this);
		mNewsNum = (TextView) findViewById(R.id.news_num);
		mNewsCount = (TextView) findViewById(R.id.news_count_view);
		findViewById(R.id.news_id).setOnClickListener(this);
		initFeedListView();
	}

	private void startFavorActivity() {
		Intent intent = new Intent(this, EnjoyFeedPage.class);
		intent.putExtra(UserInfoPage.KEY_UID, mUid);
		intent.putExtra(KEY_USER_NAME, mTitleView.getText());
		intent.putExtra(EXTRA_REGISTER_REFRESH, true);
		startActivity(intent);
	}

	private void startFansActivity() {
		Intent intent = new Intent(this, AttentUserListPage.class);
		intent.putExtra(AttentUserListPage.KEY_UID, mUid);
		intent.putExtra(AttentUserListPage.KEY_ATTENT_TYPE,
				AttentUserListPage.ATTENT_TYPE_FANS);
		intent.putExtra(UserInfoPage.KEY_UID, mUid);
		intent.putExtra(KEY_USER_NAME, mTitleView.getText());
		intent.putExtra(EXTRA_REGISTER_REFRESH, true);
		startActivity(intent);
	}

	private void startUserInfoActivity() {
		Intent intent = new Intent();
		if (isFromHome
				|| (!TextUtils.isEmpty(ApplicationManager.getInstance()
						.getUserId()) && ApplicationManager.getInstance()
						.getUserId().equals(mUid))) {
			intent.setClass(this, PersonalEditPage.class);
		} else {
			intent.setClass(this, UserInfoPage.class);
			intent.putExtra(KEY_USER_NAME, mTitleView.getText());
			intent.putExtra(UserInfoPage.KEY_UID, mUid);
		}
		intent.putExtra(KEY_USER_ID, mUid);
		startActivity(intent);
	}

	private void startNewsActivity() {
		Intent intent = new Intent();
		intent.setClass(this, NewsMainPage.class);
		intent.putExtra("belikeNum", beLikeNum);
		intent.putExtra("commentNum", commentNum);
		intent.putExtra("addMeNum", addMeNum);
		startActivity(intent);
	}

	private void startAttentionActivity() {
		Intent intent = new Intent();
		intent.setClass(this, AttentUserListPage.class);
		intent.putExtra(AttentUserListPage.KEY_UID, mUid);
		intent.putExtra(AttentUserListPage.KEY_ATTENT_TYPE,
				AttentUserListPage.ATTENT_TYPE_ATTENT);
		startActivity(intent);
	}

	protected void showToast(final int resourceID) {
		showToast(getString(resourceID));
	}

	protected void showToast(final String str) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MySelfPersonnalInforPage.this, str,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.send:
			startSettingActivity();
			break;
		case R.id.header_image:
			startUserInfoActivity();
			break;
		case R.id.favor_id:
			startFavorActivity();
			break;
		case R.id.fans_id:
			startFansActivity();
			break;
		case R.id.news_id:
			startNewsActivity();
			break;
		case R.id.attention_id:
			startAttentionActivity();
			break;
		}
	}

	private void startSettingActivity() {
		Intent intent = new Intent(this, MoreMainPage.class);
		intent.putExtra(PersonalEditPage.KEY_UID, mUid);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mExitClentReceiver);
		unregisterReceiver(mUpdateCountReceiver);
		unregisterReceiver(mNewNewsCountReceiver);
		unregisterReceiver(mUpdateFeedReceiver);
		unregisterReceiver();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!isFromHome) {
			MenuItem m = null;
			m = menu.add(Menu.NONE, MENU_BACK, 0,
					getResources().getString(R.string.feed_back_home));
			m.setIcon(R.drawable.menum_home);
			m = menu.add(Menu.NONE, MENU_EXIT, 0,
					getResources().getString(R.string.feed_exit));
			m.setIcon(R.drawable.menum_exit);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_BACK:
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(this, LehoTabActivity.class);
			startActivity(intent);
			break;
		case MENU_EXIT:
			intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(this, ExitPage.class);
			startActivity(intent);
			break;
		}
		return true;
	}

	private void handlerCloserHeaderFooter() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mListView.closeHeaderFooter();
			}
		});
	}

	private void updateFeedViews(final List<HomeData> feedDataList,
			final int pageIndex) {
		mPageIndex = pageIndex;
		mHandler.post(new Runnable() {
			public void run() {
				mListView.closeHeaderFooter();
				if (pageIndex == 1) {// 刷新
					mDataList = feedDataList;
					List<PublishData> list = ApplicationManager.getInstance().publishDataList;
					if (mDataList == null || mDataList.size() == 0) {
						if (list != null && list.size() > 0) {
							List<HomeData> feedList = new ArrayList<HomeData>();
							HomeData data = new HomeData();
							data.draftList = list;
							feedList.add(data);
							mListView.setData(feedList,
									PageSize.INFO_FEED_PAGESIZE);
						} else {
							((TextView) mEmptyView
									.findViewById(R.id.wxcb_tishi)).setText(MySelfPersonnalInforPage.this
									.getString(R.string.no_self_publish));
							mEmptyView.setVisibility(View.VISIBLE);
							listView.setVisibility(View.GONE);
						}

					} else {
						mEmptyView.setVisibility(View.GONE);
						listView.setVisibility(View.VISIBLE);
						if (list != null && list.size() > 0) {
							HomeData data = new HomeData();
							data.draftList = list;
							mDataList.add(0, data);
							mListView.setData(mDataList,
									PageSize.INFO_FEED_PAGESIZE);
						} else {
							mListView.setData(mDataList,
									PageSize.INFO_FEED_PAGESIZE);
						}

					}

				} else {
					if (feedDataList != null) {
						mListView.addData(feedDataList,
								PageSize.INFO_FEED_PAGESIZE);
					} else {
						mListView.addData(new ArrayList<HomeData>(),
								PageSize.INFO_FEED_PAGESIZE);
					}
				}
			}
		});
	}
}
