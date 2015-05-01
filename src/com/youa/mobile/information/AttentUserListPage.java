package com.youa.mobile.information;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.params.PageSize;
import com.youa.mobile.friend.UserListView;
import com.youa.mobile.friend.friendmanager.FriendManagerActivity;
import com.youa.mobile.friend.friendsearch.FriendSearchActivity;
import com.youa.mobile.information.action.SearchAttentUserAction;
import com.youa.mobile.life.data.SuperPeopleData;

public class AttentUserListPage extends BasePage implements
		BaseListView.OnScrollEndListener, OnItemClickListener {
	public static final String KEY_UID = "uid";
	public static final String KEY_ATTENT_TYPE = "attent_type";
	// 关注的人
	public static final String ATTENT_TYPE_ATTENT = "1";
	// 观众
	public static final String ATTENT_TYPE_FANS = "2";
	private UserListView userList;
	private ListView mListView;
	private View footer;
	private LinearLayout header;
	private LayoutInflater mInflater;
	private String mUid;
	private String mUserName;
	private String mType;
	private int mPageIndex = -1;
	private boolean mRegisterRefresh;
	// private int pageSize = 45;
	private ImageButton mBackButton;
	private Button mSendButton;
	private TextView mTitle;
	private TextView mTextNull;
	private UpdataBroadCastReceiver mUpdataBroadCastReceiver = new UpdataBroadCastReceiver();

	class UpdataBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (LehuoIntent.ACTION_USERCOUNT_NEEDUPDATE.equals(action)) {
				loadData(true);
			}
		}

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information_attent);
		if (savedInstanceState == null) {
			mRegisterRefresh = getIntent().getBooleanExtra(
					PersonnalInforPage.EXTRA_REGISTER_REFRESH, false);
		} else {
			mRegisterRefresh = savedInstanceState
					.getBoolean(PersonnalInforPage.EXTRA_REGISTER_REFRESH);
		}
		IntentFilter fileter = new IntentFilter();
		fileter.addAction(LehuoIntent.ACTION_USERCOUNT_NEEDUPDATE);
		this.registerReceiver(mUpdataBroadCastReceiver, fileter);
		initDataFromIntent(getIntent());
		initTitle();
		init();
		loadData(true);
	}

	public void initTitle() {
		mBackButton = (ImageButton) this.findViewById(R.id.back);
		mBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AttentUserListPage.this.finish();
			}
		});
		// concern_user_title
		mTitle = (TextView) this.findViewById(R.id.title);
		if ("1".equals(mType)) {
			if (!TextUtils
					.isEmpty(ApplicationManager.getInstance().getUserId())
					&& ApplicationManager.getInstance().getUserId()
							.equals(mUid)) {
				mTitle.setText(getString(R.string.concern_user_title, "我"));
			} else {
				mTitle.setText(getString(R.string.concern_user_title, mUserName));
			}
		} else {
			if (!TextUtils
					.isEmpty(ApplicationManager.getInstance().getUserId())
					&& ApplicationManager.getInstance().getUserId()
							.equals(mUid)) {
				mTitle.setText(getString(R.string.attent_user_title, "我"));
			} else {
				mTitle.setText(getString(R.string.attent_user_title, mUserName));
			}
		}
		mSendButton = (Button) this.findViewById(R.id.send);
		mSendButton.setVisibility(View.GONE);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(PersonnalInforPage.EXTRA_REGISTER_REFRESH,
				mRegisterRefresh);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mRegisterRefresh) {
			registerReceiver();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mRegisterRefresh) {
			unregisterReceiver();
		}
	};

	private BroadcastReceiver mRefreshReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (TextUtils.equals(SystemConfig.ACTION_REFRESH_MY,
					intent.getAction())) {
				// mListView.refresh();
			}
		}
	};

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(SystemConfig.ACTION_REFRESH_MY);
		registerReceiver(mRefreshReceiver, filter);
	}

	private void unregisterReceiver() {
		unregisterReceiver(mRefreshReceiver);
	}

	private void initDataFromIntent(Intent intent) {
		mUid = intent.getStringExtra(KEY_UID);
		mType = intent.getStringExtra(KEY_ATTENT_TYPE);
		mUserName = intent.getStringExtra(PersonnalInforPage.KEY_USER_NAME);
	}

	private void init() {
		mInflater = LayoutInflater.from(this);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setOnItemClickListener(this);
		header = (LinearLayout) getInflaterLayout(R.layout.feed_header);
		footer = getInflaterLayout(R.layout.feed_footer);
		mProcessView = (ProgressBar) findViewById(R.id.progressBar);
		mTextNull = (TextView) findViewById(R.id.text_null);
		toTopInit();
		toEndInit();
		userList = new UserListView(mListView, header, footer, mHandler);
		userList.setOnScrollEndListener(this);
	}

	private void updateViews(final List<SuperPeopleData> list,
			final boolean isRefreshOrGetMore) {
		mHandler.post(new Runnable() {
			public void run() {
				userList.closeHeaderFooter();
				mListView.setVisibility(View.VISIBLE);
				if (isRefreshOrGetMore) {// 刷新
					if (list != null && list.size() > 0) {
						mTextNull.setVisibility(View.GONE);
						mListView.setVisibility(View.VISIBLE);
						if (ATTENT_TYPE_ATTENT.equals(mType)) {
							if (!TextUtils.isEmpty(ApplicationManager
									.getInstance().getUserId())
									&& ApplicationManager.getInstance()
											.getUserId().equals(mUid)) {
								List<SuperPeopleData> peopleDatalist = new ArrayList<SuperPeopleData>();
								peopleDatalist.add(new SuperPeopleData());
								peopleDatalist.addAll(list);
								userList.setData(peopleDatalist,
										PageSize.HOME_FEED_PAGESIZE);
							} else {
								userList.setData(list,
										PageSize.HOME_FEED_PAGESIZE);
							}
						} else {
							userList.setData(list, PageSize.HOME_FEED_PAGESIZE);
						}
						if (list.size() < 20) {
							userList.setLockEnd(true);
						}
						mPageIndex = 0;
					} else {
						mTextNull.setVisibility(View.VISIBLE);
						if (ATTENT_TYPE_ATTENT.equals(mType)) {
							mTextNull.setText(AttentUserListPage.this.getResources().getString(R.string.attent_num_zero));
							if (!TextUtils.isEmpty(ApplicationManager
									.getInstance().getUserId())
									&& ApplicationManager.getInstance()
											.getUserId().equals(mUid)) {
								List<SuperPeopleData> peopleDatalist = new ArrayList<SuperPeopleData>();
								peopleDatalist.add(new SuperPeopleData());
								userList.setData(peopleDatalist,
										PageSize.HOME_FEED_PAGESIZE);
								userList.setLockEnd(true);
							} else {
								
								mListView.setVisibility(View.GONE);
							}
						} else {
							mTextNull.setText(AttentUserListPage.this.getResources().getString(R.string.fans_num_zero));
							mListView.setVisibility(View.GONE);
						}
					}
				} else {
					userList.addData(list, PageSize.HOME_FEED_PAGESIZE);
					mPageIndex++;
					userList.setLockEnd(false);
				}

			}
		});
	}

	private void handlerCloserHeaderFooter() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				userList.closeHeaderFooter();
			}
		});
	}

	private void loadData(final boolean isRefreshOrGetMore) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(SearchAttentUserAction.KEY_UID, mUid);
		params.put(SearchAttentUserAction.KEY_ATTENT_TYPE, mType);
		if (isRefreshOrGetMore) {
			params.put(SearchAttentUserAction.KEY_OFFSET, 0);
		} else {
			int pageIndex = mPageIndex + 1;
			params.put(SearchAttentUserAction.KEY_OFFSET, pageIndex);
		}
		ActionController.post(this, SearchAttentUserAction.class, params,
				new SearchAttentUserAction.ISearchAttentResult() {
					@Override
					public void onStart() {
					}

					public void onFail(int resourceID) {
						handlerCloserHeaderFooter();
						showToast(AttentUserListPage.this, resourceID);
						hiddenProgressView();
					}

					@Override
					public void onEnd(List<SuperPeopleData> userData) {
						updateViews(userData, isRefreshOrGetMore);
						hiddenProgressView();
					}
				}, true);
	}

	@Override
	public void onScrollEnd() {
		loadData(false);
	}

	@Override
	public void onScrollHeader() {
		loadData(true);
	}

	@Override
	public void onItemClick(AdapterView<?> listview, View view, int position,
			long id) {
		if (userList.getHeader() != null) {
			position -= 1;
		}
		List<SuperPeopleData> list = userList.getData();
		if (position < 0 || position >= list.size()) {
			return;
		}
		if ("1".equals(mType)) {
			if (!TextUtils
					.isEmpty(ApplicationManager.getInstance().getUserId())
					&& ApplicationManager.getInstance().getUserId()
							.equals(mUid) && position == 0) {
				Intent intent = new Intent(AttentUserListPage.this,
						FriendSearchActivity.class);
				startActivity(intent);
			} else {
				SuperPeopleData mUserData = userList.getData().get(position);
				Intent intent = new Intent(this, PersonnalInforPage.class);
				intent.putExtra(PersonnalInforPage.KEY_USER_ID,
						mUserData.getUserId());
				intent.putExtra(PersonnalInforPage.KEY_USER_NAME,
						mUserData.getUserName());
				startActivity(intent);
			}
		} else {
			SuperPeopleData mUserData = userList.getData().get(position);
			Intent intent = new Intent(this, PersonnalInforPage.class);
			intent.putExtra(PersonnalInforPage.KEY_USER_ID,
					mUserData.getUserId());
			intent.putExtra(PersonnalInforPage.KEY_USER_NAME,
					mUserData.getUserName());
			startActivity(intent);
		}

	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mUpdataBroadCastReceiver);
		super.onDestroy();
	}

	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private TextView footer_tipsTextview;
	private TextView footer_lastUpdatedTextView;
	private ImageView footer_arrowImageView;
	private ProgressBar footer_progressBar;

	private void toEndInit() {
		footer_arrowImageView = (ImageView) footer
				.findViewById(R.id.head_arrowImageView);
		footer_progressBar = (ProgressBar) footer
				.findViewById(R.id.head_progressBar);
		footer_tipsTextview = (TextView) footer
				.findViewById(R.id.head_tipsTextView);
		footer_lastUpdatedTextView = (TextView) footer
				.findViewById(R.id.head_lastUpdatedTextView);
	}

	private void toTopInit() {
		arrowImageView = (ImageView) header
				.findViewById(R.id.head_arrowImageView);
		progressBar = (ProgressBar) header.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) header.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) header
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

	public View getInflaterLayout(int resource) {
		return mInflater.inflate(resource, null);
	}
}
